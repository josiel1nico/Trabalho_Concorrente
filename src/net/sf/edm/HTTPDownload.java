/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.edm;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Arrays;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.sf.edm.io.FileChannel;
import net.sf.edm.net.HTTPConnection;
import net.sf.edm.net.HTTPConstants;
import net.sf.edm.net.HTTPException;
import net.sf.edm.ui.PreferencesManager;

/**
 *
 * @author usien
 */
public class HTTPDownload extends Observable {

    private boolean pause = false;
    private boolean paused = false;
    private boolean autoCrtLclFile = false;
    private boolean resumable = true;
    private int[] byts;					//a variável dos bytes do download não está de 
    private int len = -1;				//forma adequada para ser compartilhada entre threads, 
    private int bfCap = 5120;			//deveria haver uma instância para cada thread..
    private String url;
    private String rtURL;
    private String fileName;
    private String dspn = null;
    private HTTPConstants rtVer;
    private String lclDir;
    private SocketChannel[] chans;
    private DownloadState st = DownloadState.IDLE;
    private int spd = 0;
    private int lstRd = 0;
    private static boolean dbg = true;
    private int[] stgs;
    private Timer tmr;
    private FileChannel channel;
    private int retries = 0;

    public HTTPDownload(String url, int sects) {
        this.url = url;
        rtURL = url;
        fileName = rtURL;
        byts = new int[sects];
        chans = new SocketChannel[sects];
        stgs = new int[sects];
        Arrays.fill(byts, 0);
        Arrays.fill(stgs, 0);
    }

    private class Starter implements Runnable {

        private int sect;

        public Starter(int sect) {
            this.sect = sect;
        }


        public void run() {
            try {
                HTTPConnection conn = new HTTPConnection(rtURL);
                int lmt = len / byts.length;
                int strt = lmt * sect;

                if (sect == byts.length - 1) {
                    lmt = len - strt;
                }

                if (byts[sect] == lmt && len != -1) {
                    dbg("equal " + sect);
                    if (sect == 0) {
                        fireInid();
                    }
                    return;
                }

                if (sect > 0 && rtVer != null) {
                    conn.setHttpVersion(rtVer);
                }

                conn.setByteRangeStart(strt + byts[sect]);
                conn.connect();

                if (len == -1) {
                    len = conn.getContentLength();
                    lmt = len / byts.length;
                }

                if (sect == 0) {
                    rtURL = conn.getURL().toString();
                    rtVer = conn.getHttpVersion();
                    dspn = conn.getHeaderValue("Content-Disposition", "null");

                    if (conn.getHeaderValue("Accept-Ranges", "null").equals(null)) {
                        JOptionPane.showMessageDialog(null, "The download is not resumable.");
                    }

                    fireInid();
                }

                chans[sect] = conn.getChannel();
                strtIOLp(sect, strt + byts[sect], lmt);
            } catch (Exception ex) {
                ntfyExcp(ex);
            }
        }
    }
    
    //cria uma thread e starta ela
   
    public void start() {
        dbg("Starting download");
        pause = false;
        ntfySt(DownloadState.CONNECTING);
        new Thread(new Starter(0)).start();


        class SpdTsk extends TimerTask {

            int cntr = 1;
            int totRd = 0;

            @Override
            public void run() {
                int byts = getBytes();
                if (byts - lstRd > 0) {
                    if (pause) {
                        return;
                    }

                    totRd += (byts - lstRd);
                    spd = totRd / cntr;
                    ntfySt(DownloadState.RUNNING);
                    lstRd = byts;
                    //dbg(String.valueOf(cntr));
                    if (cntr == 6) {
                        cntr = 1;
                        totRd = (byts - lstRd);
                    } else {
                        cntr++;
                    }
                }
            }
        }

        lstRd = getBytes();
        tmr = new Timer();
        tmr.scheduleAtFixedRate(new SpdTsk(), 0, 1000);
    }

    public void pause() {
        pause = true;

        if (tmr != null) {
            tmr.cancel();
            spd = 0;
        }

        ntfySt(DownloadState.PAUSING);

        for (int x = 0; x < chans.length; x++) {

            if (chans[x] != null && chans[x].isOpen()) {
                try {
                    dbg("before pausing section " + x + " is at stage " + stgs[x]);
                    chans[x].close();
                } catch (IOException ex) {
                    ntfyExcp(ex);
                }
            }
            dbg("section " + x + " is at stage " + stgs[x]);
        }

        try {
            if (channel != null) {
                channel.flush();
            }
        } catch (IOException ex) {
            ntfyExcp(ex);
        }

        paused = true;
        ntfySt(DownloadState.PAUSED);
    }

    private void fireInid() {
        dbg("fireInid");

        if (fileName.equals(url)) {
            fileName = rtURL;

            if (!dspn.equals("null")) {
                fileName = dspn.substring(dspn.lastIndexOf("=") + 1, dspn.length() - 2);
            } else {
                dbg("RTURL:" + rtURL);

                if (fileName.contains("?")) {
                    dbg("?:" + fileName);
                    fileName = fileName.substring(rtURL.lastIndexOf("/") + 1, rtURL.indexOf("?"));
                } else {
                    fileName = rtURL.substring(rtURL.lastIndexOf("/") + 1);
                }

                fileName = fileName.replaceAll("%20", " ");
                dbg("local file: " + getLocalFile());
            }
        }
        dbg("filename:" + fileName);


        for (int sect = 1; sect < byts.length; sect++) {
            new Thread(new Starter(sect)).start();
        }
    }

    private boolean strtIOLp(int sect, int strt, int lmt) {
        ntfySt(DownloadState.RUNNING);
        retries = 0;
        ByteBuffer bfr = ByteBuffer.allocate(bfCap);
        int pos = strt;
        int lpRd;

        dbg(sect + " loop start : " + pos);

        try {
            while (chans[sect].isOpen() && byts[sect] < lmt && !pause) {
                stgs[sect] = 0;
                bfr.clear();
                lpRd = chans[sect].read(bfr);
                stgs[sect] = 1;
                if (pause) {
                    stgs[sect] = -1;
                    break;
                }

                if (lpRd + byts[sect] > lmt) {
                    lpRd = lmt - byts[sect];
                }

                byts[sect] += lpRd;

                if (autoCrtLclFile && lpRd != -1) {
                    if (channel == null) {
                        channel = new FileChannel(lclDir + System.getProperty("file.separator") + fileName, 512000);
                        channel.setBuffersCount(chans.length);
                    }

                    stgs[sect] = 2;
                    synchronized (channel) {
//                        dbg(bfr.array().length + ":" + lpRd);
                        if (!channel.isIndiceSet(sect)) {
                            channel.setIndice(sect, pos);
                            dbg("indice set: " + String.valueOf(pos));
                        }

                        channel.write(sect, bfr);
                    }

                }
                stgs[sect] = 3;

                pos += lpRd;
                ntfySt(DownloadState.RUNNING);
            }

            dbg("read: " + byts[sect]);

            if (byts[sect] == lmt) {
                dbg("finished");


                if (tmr != null) {
                    spd = 0;
                    tmr.cancel();
                    tmr = null;
                }

                if (isComplete() && channel != null) {
                    channel.close();
                    dbg("closed");

                    ntfySt(DownloadState.COMPLETE);
                }

                return true;
            }
        } catch (AsynchronousCloseException ex) {
            return false;
        } catch (IOException ex) {
            ntfyExcp(ex);
        }

        return false;
    }

    public boolean isComplete() {
        return getBytes() == len ? true : false;
    }

    public int getLength() {
        return len;
    }

    public String getURL() {
        return url;
    }

    public String getLocalFile() {
        return lclDir + System.getProperty("file.separator") + fileName;
    }

    public void autoCreateLocalFile(String dir) {
        lclDir = dir;
        autoCrtLclFile = true;
    }

    public String getFileName() {
        return fileName;
    }

    public int getSections() {
        return byts.length;
    }

    public int getBytes() {
        int sum = 0;

        for (int lclByts : byts) {
            sum += lclByts;
        }

        return sum;
    }

    public DownloadState getState() {
        return st;
    }

    public boolean isPaused() {
        return paused;
    }

    public void writeState(FileWriter writer) throws IOException {
        String lf = System.getProperty("line.separator");
        writer.write(url);
        writer.write(lf + (st == DownloadState.EXCEPTION ? "true" : "false"));

        if (lclDir != null) {
            writer.write(lf + lclDir + System.getProperty("file.separator") + fileName);
        } else {
            writer.write(lf + " ");
        }

        writer.write(lf + len);
        writer.write(lf + byts.length);

        for (int x = 0; x < byts.length - 1; x++) {
            writer.write(lf + byts[x]);
        }

        writer.write(lf + byts[byts.length - 1] + lf);
    }

    public void readState(BufferedReader reader) throws IOException {
        url = reader.readLine();
        rtURL = url;

        dbg("url line: " + url);

        boolean exSt = Boolean.parseBoolean(reader.readLine());

        if (exSt) {
            st = DownloadState.EXCEPTION;
        }

        String fileLine = reader.readLine();

        dbg("file line: " + fileLine);

        if (fileLine.equals(" ")) {
            lclDir = null;
            fileName = url;
        } else {
            String fs = System.getProperty("file.separator");

            dbg("fs:" + fs);
            dbg("find:" + fileLine.lastIndexOf(fs));
            lclDir = fileLine.substring(0, fileLine.lastIndexOf(fs));
            fileName = fileLine.substring(fileLine.lastIndexOf(fs) + 1);
        }

        len = Integer.parseInt(reader.readLine());
        int sects = Integer.parseInt(reader.readLine());
        byts = new int[sects];
        chans = new SocketChannel[sects];
        stgs = new int[sects];
        Arrays.fill(stgs, 0);

        for (int index = 0; index < sects; index++) {
            byts[index] = Integer.parseInt(reader.readLine());
        }

        if (getBytes() == len && st != DownloadState.EXCEPTION) {
            ntfySt(DownloadState.COMPLETE);
        }
    }

    private void dbg(String s) {
        if (dbg) {
            System.out.println(s);
        }
    }

    private void ntfySt(DownloadState st) {
        this.st = st;
        setChanged();
        DownloadEvent evnt = new DownloadEvent();
        evnt.setStateEvent(true);
        evnt.setState(st);
        notifyObservers(evnt);
    }

    private void ntfyExcp(Exception ex) {
        ex.printStackTrace();
        dbg("ex:" + ex.getClass());
        if (ex instanceof UnresolvedAddressException || ex instanceof IOException) {
            dbg("io exc");
            ntfySt(DownloadState.CONNECTION_EXCEPTION);
        } else if (ex instanceof HTTPException) {
            HTTPException httpEx = (HTTPException) ex;
            if (httpEx.getResponseCode() >= 400) {
                ntfySt(DownloadState.FAILED);
            }
        }

        DownloadEvent evnt = new DownloadEvent();
        evnt.setExceptionEvent(true);
        evnt.setException(ex);
        notifyObservers(evnt);

        if (retries < Integer.parseInt(PreferencesManager.getRetries())) {
            ntfySt(DownloadState.RETRYING);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex1) {
                Logger.getLogger(HTTPDownload.class.getName()).log(Level.SEVERE, null, ex1);
            }
            retries++;
            start();
        }
    }

    public int getSpeed() {
        return spd;
    }
}

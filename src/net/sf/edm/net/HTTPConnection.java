/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.edm.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 *
 * @author usien
 */
public class HTTPConnection {

    private URL url;
    private SocketChannel chan;
    private ByteChannelCodec cdc;
    private ArrayList<String> rspLns = new ArrayList();
    private HTTPConstants mthd = HTTPConstants.GET_METHOD;
    private HTTPConstants ver = HTTPConstants.V1_1;
    private int bytStrt = 0;
    private boolean cntd = false;
    private boolean rdts = false;
    private boolean autoRdt = true;
    private static boolean dbg = true;

    public HTTPConnection(String url) throws MalformedURLException {
        this.url = new URL(url);
        dbg("ver:" + ver);
        dbg("mthd:" + mthd);
    }

    public void connect() throws IOException, HTTPException {
        chan = SocketChannel.open(new InetSocketAddress(url.getHost(), 80));
        cdc = new ByteChannelCodec(chan);

        dbg("sending request");

        sndRqst();

        dbg("request sent");

        rdRsp();
        boolean reCnct = false;
        int rspCd = getResponseCode();

        dbg("response message: " + getResponseMessage());
        dbg("response code: " + String.valueOf(rspCd));


        if (rspCd >= 400) {
            throw new HTTPException(rspCd, getResponseMessage());
        }

        if (rspCd >= 300 && rspCd < 400) {
            rdts = true;

            if (autoRdt) {
                dbg("redirect caught");

                url = new URL(getHeaderValue("Location", null).replaceAll(" ", "%20"));

                dbg("redirecting url:" + url);

                reCnct = true;
            }

            dbg("redirect");
        }

        dbg("rsponse http version: " + getRspVer());

        if (ver == HTTPConstants.V1_1 && getRspVer() == HTTPConstants.V1_0) {
            ver = HTTPConstants.V1_0;
            reCnct = true;
            dbg("changing http version to 1.o");
        }

        if (reCnct) {
            connect();
        }

        cntd = true;
    }

    public HTTPConstants getRequestMethod() {
        return mthd;
    }

    public void setRequestMethod(HTTPConstants mthd) {
        this.mthd = mthd;
    }

    public HTTPConstants getHttpVersion() {
        return ver;
    }

    public void setHttpVersion(HTTPConstants ver) {
        this.ver = ver;
    }

    public int getByteRangeStart() {
        return bytStrt;
    }

    public void setByteRangeStart(int bytStrt) {
        this.bytStrt = bytStrt;
    }

    private void sndRqst() throws IOException {
        String rqstVer = null;

        switch (ver) {
            case V1_0:
                rqstVer = "HTTP/1.0";
                break;
            case V1_1:
                rqstVer = "HTTP/1.1";
                break;
        }

        String rqstMthd = null;

        switch (mthd) {
            case GET_METHOD:
                rqstMthd = "GET";
                break;
            case POST_METHOD:
                rqstMthd = "POST";
                break;
            case HEAD_METHOD:
                rqstMthd = "HEAD";
                break;
        }

        String crlf = "\r\n";
        String rqstUrl;

        if (ver == HTTPConstants.V1_0) {
            rqstUrl = url.toString();
        } else {
            rqstUrl = url.getFile();
        }

        dbg("file: " + url.getFile());

        String rqst = rqstMthd + " " + rqstUrl + " " + rqstVer;

        dbg(rqst);

        cdc.write(rqst + crlf);

        if (ver == HTTPConstants.V1_1) {
            String host = "Host: " + url.getHost();
            cdc.write(host + crlf);
        }

        if (bytStrt > 0) {
            String rnge = "Range: bytes=" + String.valueOf(bytStrt) + "-";
            cdc.write(rnge + crlf);
        }

        cdc.write(crlf);
    }

    private void rdRsp() throws IOException {
        rspLns.clear();
        String line;

        while (!(line = cdc.readLine()).trim().equals("")) {
            dbg(line);

            rspLns.add(line);
        }
    }

    public String getHeaderValue(String key, String defaultValue) {
        for (String line : rspLns) {
            if (line.startsWith(key)) {
                if (line.contains(":")) {
                    return line.substring((key + ": ").length());
                } else {
                    return line;
                }
            }
        }

        return defaultValue;
    }

    public ArrayList<String> getHeaderValues(String header, ArrayList<String> defaultValues) {
        return null;
    }

    public URL getURL() {
        return url;
    }

    public int getContentLength() {
        return Integer.parseInt(getHeaderValue("Content-Length", "-1").trim());
    }

    public int getResponseCode() {
        String[] sts = getHeaderValue("", "").trim().split(" ");
        return Integer.parseInt(sts[1]);
    }

    public String getResponseMessage() {
        String[] sts = getHeaderValue("", "").trim().split(" ");
        String msg = "";

        for (int x = 2; x < sts.length; x++) {
            msg += sts[x] + " ";
        }
        return msg;
    }

    private HTTPConstants getRspVer() {
        String line = getHeaderValue("", "").trim();
        String val = line.substring(0, line.indexOf(" ")).trim();

        if (val.equals("HTTP/1.1")) {
            return HTTPConstants.V1_1;
        } else {
            return HTTPConstants.V1_0;
        }
    }

    public boolean isConnected() {
        return cntd;
    }

    public boolean getRedirects() {
        return rdts;
    }

    public SocketChannel getChannel() {
        return chan;
    }

    private void dbg(String val) {
        if (dbg) {
            System.out.println(val);
        }
    }
}

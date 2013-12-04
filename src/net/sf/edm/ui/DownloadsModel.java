package net.sf.edm.ui;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import net.sf.edm.HTTPDownload;

/**
 *
 * @author usien
 */
public class DownloadsModel extends DefaultTableModel implements Observer {

    private static String[] headers = new String[]{"File", "Progress", "Time"};
    private static Class[] columnClasses = new Class[]{NameRenderer.class, ProgressRenderer.class, TimeRenderer.class};
    private ArrayList<HTTPDownload> dnlds;

    public DownloadsModel() {
        dnlds = new ArrayList();
    }

    public void addDownloadObserver(int idx, Observer obs) {
        dnlds.get(idx).addObserver(obs);
    }

    public HTTPDownload getDownload(int index) {
        return dnlds.get(index);
    }

    public int indexOf(HTTPDownload dnld) {
        return dnlds.indexOf(dnld);
    }

    public ArrayList<HTTPDownload> getDownloads() {
        return dnlds;
    }

    @Override
    public int getColumnCount() {
        return headers.length;
    }

    @Override
    public String getColumnName(int index) {
        return headers[index];
    }

    @Override
    public int getRowCount() {
        try {
            return dnlds.size();
        } catch (NullPointerException ex) {
            return 0;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        return dnlds.get(row);
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return columnClasses[columnIndex];
    }

    public void update(Observable o, Object arg) {
        int row = dnlds.indexOf(o);
        fireTableRowsUpdated(row, row);
    }

    public HTTPDownload addAction(String url, String dir, int sects, Observer... obss) {
        HTTPDownload dnld = new HTTPDownload(url, sects);
        dnld.autoCreateLocalFile(dir);

        for (Observer obs : obss) {
            dnld.addObserver(obs);
        }

        dnld.addObserver(this);
        dnlds.add(dnld);
        fireTableStructureChanged();

        return dnld;
    }

    public void addAction(HTTPDownload dnld, String dir, Observer... obss) {
        dnld.autoCreateLocalFile(dir);

        for (Observer obs : obss) {
            dnld.addObserver(obs);
        }

        dnld.addObserver(this);
        dnlds.add(dnld);
        fireTableStructureChanged();
    }

    public void pauseAction(int row) {
        dnlds.get(row).pause();
    }

    public void startAction(int row) {
        dnlds.get(row).start();
    }

    public void cancelAction(int row, boolean remove) {
        HTTPDownload dnld = dnlds.get(row);

        if (!dnld.isPaused()) {
            dnld.pause();
        }

        if (remove) {
            File file = new File(dnld.getLocalFile());

            if (file != null && file.exists()) {
                file.delete();
            }
        }

        dnlds.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void pauseAllAction() {
        for (HTTPDownload dnld : dnlds) {
            dnld.pause();
        }
    }

    public void writeList(String file) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(String.valueOf(dnlds.size()) + "\n");

            for (HTTPDownload download : dnlds) {
                download.writeState(writer);
            }

            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(EDM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void readList(String file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int count = Integer.parseInt(reader.readLine());
            HTTPDownload dnld;

            for (int x = 0; x < count; x++) {
                dnld = new HTTPDownload("", 0);
                dnld.readState(reader);
                String filePath = dnld.getLocalFile();
                addAction(dnld, filePath.substring(0, filePath.lastIndexOf(System.getProperty("file.separator"))));
            }

            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(EDM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public HTTPDownload urlExists(String url) {
        for (HTTPDownload dnld : dnlds) {
            if (dnld.getURL().equals(url)) {
                return dnld;
            }
        }

        return null;
    }
}

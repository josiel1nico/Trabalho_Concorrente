/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.edm.ui;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author usien
 */
public class PreferencesManager {

    private static Properties props = new Properties();
    private static File userDir = new File(System.getProperty("user.home") + System.getProperty("file.separator"));
    private static File prefsDir = new File(userDir, "EDM");
    private static File prefsFile = new File(prefsDir, "prefs");
    private boolean mapFild = false;
    public static String dnldLstFile = prefsDir.getPath() + System.getProperty("file.separator") + "dnlds";
    public static HashMap<String, String> lafMap = new HashMap();
    public static FileFilter dirFltr = new FileFilter() {

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public String getDescription() {
            return "Directories";
        }
    };


    static {
        try {
            if (!prefsDir.exists()) {
                prefsDir.mkdir();
                File dnldsFile = new File(prefsDir, "dnlds");
                File dnldsDir = new File(userDir, "downloads");


                dnldsDir.mkdir();
                props.put("dnldDir", dnldsDir.getPath());
                props.put("failAct", "null");
                LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();

                for (LookAndFeelInfo info : infos) {
                    if (info.getClassName().equals(UIManager.getSystemLookAndFeelClassName())) {
                        props.put("laf", info.getName());
                    }
                }

                props.put("rtrs", "5");
                props.store(new FileWriter(prefsFile), null);
                FileWriter writer = new FileWriter(dnldsFile);
                writer.write("0");
                writer.close();

            } else {
                props.load(new FileReader(prefsFile));
            }
        } catch (IOException ex) {
            Logger.getLogger(EDM.class.getName()).log(Level.SEVERE, null, ex);
        }

        LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();

        for (LookAndFeelInfo info : infos) {
            lafMap.put(info.getName(), info.getClassName());
        }
    }

    public static void setDownloadDirectory(String dir) {
        props.put("dnldDir", dir);
    }

    public static String getDownloadDirectory() {
        return props.getProperty("dnldDir");
    }

    public static String getFailAction() {
        return props.getProperty("failAct");
    }

    public static void setFailAction(String failAct) {
        props.put("failAct", failAct);
    }

    public static String getLaf() {
        return props.getProperty("laf");
    }

    public static void setLaf(String laf) {
        props.put("laf", laf);
    }

    public static String getRetries() {
        return props.getProperty("rtrs");
    }

    public static void setRetries(String rtrs) {
        props.put("rtrs", rtrs);
    }

    public static String getPreferencesDirectory() {
        return prefsDir.getPath();
    }

    public static void savePreferences() {
        File file = new File(props.getProperty("dnldDir"));

        try {

            props.store(new FileWriter(prefsFile), null);

            if (!file.exists()) {
                file.mkdir();
            }
        } catch (IOException ex) {
            Logger.getLogger(EDM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

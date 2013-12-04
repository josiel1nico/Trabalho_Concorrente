/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.edm.ui;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author usien
 */
public class TrayManager {

    private static SystemTray tray;
    private JFrame frm;


    static {
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
        } else {
            JOptionPane.showMessageDialog(null, "Tray icon will not be shown because it is not supported on your system");
        }
    }

    public TrayManager(JFrame frm) {
        this.frm = frm;
    }

    public void addEDMTray() throws AWTException {
        TrayIcon icon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/net/sf/edm/ui/icons/download.png")));
        PopupMenu menu = new PopupMenu();
        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                EDM.mdl.pauseAllAction();
                EDM.mdl.writeList(PreferencesManager.dnldLstFile);
                System.exit(0);
            }
        });
        menu.add(exit);
        icon.setPopupMenu(menu);
        icon.setToolTip("Express Download Manager");
        tray.add(icon);
        icon.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                frm.setVisible(!frm.isVisible());
            }
        });
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.edm.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.net.URL;

/**
 *
 * @author usien
 */
public class ClipboardManager {

    private static Clipboard board = Toolkit.getDefaultToolkit().getSystemClipboard();

    public static String getURL() {
        if (board.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            String text = null;
            URL url;

            try {
                text = (String) board.getData(DataFlavor.stringFlavor);
                url = new URL(text);
            } catch (Exception ex) {
                return null;
            }

            return text;
        }

        return null;
    }

    public static Clipboard getClipboard() {
        return board;
    }
}

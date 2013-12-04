/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.edm.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import net.sf.edm.HTTPDownload;

/**
 *
 * @author usien
 */
public class ProgressRenderer extends JPanel implements TableCellRenderer {

    private JProgressBar bar;
    private JLabel percent;
    private JLabel size;

    public ProgressRenderer() {
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(new GridBagLayout());
        bar = new JProgressBar(0, 100);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(3, 1, 1, 1);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        add(bar, constraints);
        percent = new JLabel();
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.insets = new Insets(1, 1, 1, 1);
        constraints.weightx = 0;
        add(percent, constraints);
        size = new JLabel();
        constraints.gridx = 0;
        constraints.gridy = 1;
        add(size, constraints);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        HTTPDownload dnld = (HTTPDownload) value;
        int rd = dnld.getBytes();
        int mbLen = 1024 * 1024;
        String rdMbs = String.valueOf((float) rd / mbLen);
        String rdStr = (rd > 1024) ? (rd > mbLen) ? rdMbs.substring(0, rdMbs.indexOf(".") + 3) + " MBs" : rd / 1024 + " KBs" : rd + " bytes";
        int len = dnld.getLength();
        String lenStr;

        if (len == -1) {
            lenStr = "unknown bytes";

        } else {
            String lenMbs = String.valueOf((float) len / mbLen);
            lenStr = (len > 1024) ? (len > mbLen) ? lenMbs.substring(0, lenMbs.indexOf(".") + 3) + " MBs" : len / 1024 + " KBs" : len + " bytes";
        }

        size.setText(rdStr + " of " + lenStr);

        if (len == -1 || len == 0) {
            bar.setValue(0);
            percent.setText("0 %");
        } else {
            float perc = ((float) rd / len) * 100;
            bar.setValue((int) perc);
            String val = String.valueOf(perc);
            val = (val.contains(".")) ? val.substring(0, val.indexOf(".")) : val;
            percent.setText((val.length() < 3) ? (val.length() == 2) ? " " + val + " %" : "  " + val + " %" : val + " %");
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground((row % 2) == 0 ? Color.WHITE : new Color(220, 220, 220));
            setForeground(table.getForeground());
        }

        return this;
    }
}

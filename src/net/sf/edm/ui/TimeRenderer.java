/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.edm.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import net.sf.edm.HTTPDownload;

/**
 *
 * @author usien
 */
public class TimeRenderer extends JPanel implements TableCellRenderer {

    private JLabel spd;
    private JLabel time;

    public TimeRenderer() {
        spd = new JLabel();
        time = new JLabel();
        setLayout(new GridLayout(2, 1));
        add(spd);
        add(time);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        HTTPDownload dnld = (HTTPDownload) value;
//        System.out.println("spd:" + dnld.getSpeed());
        int rt = dnld.getSpeed() / 1024;
        spd.setText("    " + String.valueOf(rt) + " Kbps");
//        time.setText(dnld.getState().title());



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

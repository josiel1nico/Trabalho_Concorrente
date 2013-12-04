/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.edm.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
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
public class NameRenderer extends JPanel implements TableCellRenderer {

    private JLabel name;
    private JLabel status;

    public NameRenderer() {
        name = new JLabel();
        name.setFont(name.getFont().deriveFont(Font.BOLD));
        status = new JLabel();
        setLayout(new GridLayout(2, 1));
        add(name);
        add(status);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        HTTPDownload download = (HTTPDownload) value;
        name.setText(download.getFileName());
        status.setText(download.getState().title());

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

/*
 * DownloadDialog.java
 *
 * Created on September 12, 2008, 5:28 PM
 */
package net.sf.edm.ui.diags;

import net.sf.edm.ui.*;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import net.sf.edm.HTTPDownload;

/**
 *
 * @author  usien
 */
public class DownloadDialog extends javax.swing.JDialog {

    /** Creates new form DownloadDialog */
    public DownloadDialog(Frame parent) {
        super(parent, true);
        initComponents();
        saveFld.setText(PreferencesManager.getDownloadDirectory());
        String url;

        if ((url = ClipboardManager.getURL()) != null) {
            urlFld.setText(url);
        }
    }

    public static void addClipboardListener(Frame frm) {
        class CBM implements FlavorListener {

            DownloadDialog diag;

            public CBM(DownloadDialog diag) {
                this.diag = diag;
            }

            public void flavorsChanged(FlavorEvent e) {
                String url = ClipboardManager.getURL();
                if (url != null) {
                    diag.setURL(url);
                    diag.setVisible(true);
                }
            }
        }

        DownloadDialog diag = new DownloadDialog(frm);
        ClipboardManager.getClipboard().addFlavorListener(new CBM(diag));
    }

    public void setURL(String text) {
        urlFld.setText(text);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        urlFld = new javax.swing.JTextField();
        saveFld = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        sectsFld = new javax.swing.JSpinner();
        strtFld = new javax.swing.JCheckBox();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("New Download"); // NOI18N
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/net/sf/edm/ui/icons/new window.png")));

        jButton1.setText("Add"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        jButton2.setText("Cancel"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("URL:"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        jPanel2.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Save to:"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        jPanel2.add(jLabel2, gridBagConstraints);

        urlFld.setColumns(40);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanel2.add(urlFld, gridBagConstraints);

        saveFld.setColumns(40);
        saveFld.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanel2.add(saveFld, gridBagConstraints);

        jLabel3.setText("Sections:"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        jPanel2.add(jLabel3, gridBagConstraints);

        sectsFld.setModel(new javax.swing.SpinnerNumberModel(1, 1, 10, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanel2.add(sectsFld, gridBagConstraints);

        strtFld.setSelected(true);
        strtFld.setText("Start downloading now"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        jPanel2.add(strtFld, gridBagConstraints);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/edm/ui/icons/editpaste.png"))); // NOI18N
        jButton3.setToolTipText("Paste URL from Clipboard"); // NOI18N
        jButton3.setMargin(new java.awt.Insets(2, 4, 2, 4));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        jPanel2.add(jButton3, gridBagConstraints);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/edm/ui/icons/fileopen.png"))); // NOI18N
        jButton4.setToolTipText("Browse"); // NOI18N
        jButton4.setMargin(new java.awt.Insets(2, 4, 2, 4));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        jPanel2.add(jButton4, gridBagConstraints);

        getContentPane().add(jPanel2, java.awt.BorderLayout.NORTH);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-531)/2, (screenSize.height-275)/2, 531, 275);
    }// </editor-fold>//GEN-END:initComponents

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    dispose();
}//GEN-LAST:event_jButton2ActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    String urlTxt = urlFld.getText();
    boolean error = false;
    HTTPDownload dnld;

    if ((dnld = EDM.mdl.urlExists(urlTxt)) != null) {
        JOptionPane.showMessageDialog(this, "This URL already exists in the Download list.");
        error = true;
    } else {
        URL url;
        File file;

        try {
            url = new URL(urlTxt);

            if (!url.getProtocol().equals("http")) {
                JOptionPane.showMessageDialog(this, "This protocol is not supported yet.", "Error", JOptionPane.ERROR_MESSAGE);
                error = true;
            }
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(this, "The URL is not valid.", "Error", JOptionPane.ERROR_MESSAGE);
            error = true;
        }

        file = new File(saveFld.getText());

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "The file location is not valid.", "Error", JOptionPane.ERROR_MESSAGE);
            error = true;
        }

        if (!error) {
            HTTPDownload dndl = EDM.mdl.addAction(urlTxt, saveFld.getText(), (Integer) sectsFld.getValue(), (EDM) getParent());

            if (strtFld.isSelected()) {
                EDM.mdl.startAction(EDM.mdl.indexOf(dndl));
            }

            dispose();
        }
    }

/*
    dispose();//GEN-LAST:event_jButton1ActionPerformed
     */
    }

private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    String url;

    if ((url = ClipboardManager.getURL()) != null) {
        urlFld.setText(url);
    }
}//GEN-LAST:event_jButton3ActionPerformed

private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    JFileChooser chsr = new JFileChooser(saveFld.getText());
    chsr.setAcceptAllFileFilterUsed(false);
    chsr.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    chsr.setFileFilter(PreferencesManager.dirFltr);
    int rc = chsr.showOpenDialog(this);

    if (rc == JFileChooser.APPROVE_OPTION) {
        saveFld.setText(chsr.getSelectedFile().getPath());
    }
}//GEN-LAST:event_jButton4ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField saveFld;
    private javax.swing.JSpinner sectsFld;
    private javax.swing.JCheckBox strtFld;
    private javax.swing.JTextField urlFld;
    // End of variables declaration//GEN-END:variables
}
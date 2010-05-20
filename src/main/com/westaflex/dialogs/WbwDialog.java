/*
 * WbwDialog.java
 *
 * Created on 25. Juni 2007, 16:09
 */
package com.westaflex.dialogs;

import com.bensmann.superswing.component.util.ConversionUtil;
import com.bensmann.superswing.component.util.JTableUtil;
import com.seebass.tools.Tools;
import com.westaflex.database.WestaDB;
import com.westaflex.resource.Strings.Strings;
import com.westaflex.util.WestaWacApplHelper;
import java.awt.Component;
import java.util.EventObject;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author  Oliver
 */
public class WbwDialog extends javax.swing.JDialog {

    private Object buffer;

    private String rc = null;

    class MyCellEditor extends DefaultCellEditor {

        private JTextField textField;

        MyCellEditor(JTextField textField) {
            super(textField);
            this.textField = textField;
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (table.getValueAt(row, column) != null) {
                textField.setText("" + table.getValueAt(row, column));
            } else {
                textField.setText("");
            }
            return textField;
        }

        @Override
        public String getCellEditorValue() {
            return textField.getText();
        }

    }

    class MyCellEditorListener implements CellEditorListener {

        @Override
        public void editingStopped(ChangeEvent e) {

            // Werte berechnen
            updateSumme();
        }

        @Override
        public void editingCanceled(ChangeEvent e) {
        }

    }

    class MyListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent listSelectionEvent) {

            int row = widerstandsbeiwerteTabelleTable.getSelectedRow();
            String image = null;

            // Ist -1 wenn keine Zeile ausgewählt wurde
            if (row < 0) {
                row = 0;
            }
            image = (String) widerstandsbeiwerteTabelleTable.getModel().getValueAt(row, 3);
            if (image != null) {
                wbwImage.setText("");
                wbwImage.setIcon(
                        new ImageIcon(this.getClass().getResource(image)));
            } else {
                wbwImage.setText(Strings.NO_PICTURE);
                wbwImage.setIcon(null);
            }
        }

    }

    /** Creates new form WbwDialog */
    public WbwDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        widerstandsbeiwerteTabelleTable.getColumn("Anzahl").setPreferredWidth(40);
        widerstandsbeiwerteTabelleTable.getColumn("Bezeichnung").setPreferredWidth(200);
        widerstandsbeiwerteTabelleTable.getColumn("Bildablage").setMinWidth(0);
        Tools.centerComponent(this);
        init2();
    }

    public WbwDialog(java.awt.Frame parent, Object buffer) {
        this(parent, true);
        this.buffer = buffer;

        if (buffer != null) {
            String[] st = ((String) buffer).split(";");
            for (int i = 0; i < st.length; i++) {
                widerstandsbeiwerteTabelleTable.setValueAt(st[i], i, 0);
            }
        }
        updateSumme();
    }

    private void init2() {
        JTextField textField;

        final String mySQLStatement = "select ~ID~, ~bezeichnung~, ~wert~, ~bild~ from ~widerstandsbeiwerte~";

        textField = new JTextField("0");
        textField.setBorder(BorderFactory.createEmptyBorder());
        MyCellEditor w = new MyCellEditor(textField);
        w.addCellEditorListener(new MyCellEditorListener());
        TableColumn column = widerstandsbeiwerteTabelleTable.getColumnModel().getColumn(0);
        column.setCellEditor(w);

        // Druckverlustberechnung / Teilstrecken / Widerstandsbeiwerte bearbeiten
        JTable table = new JTable(WestaDB.getInstance().queryDBResultArray(mySQLStatement), new String[]{"Anzahl", "Bezeichnung", "Wert", "Bildressource"});
        JTableUtil.copyTableData(table, widerstandsbeiwerteTabelleTable, 1);

        widerstandsbeiwerteTabelleTable.getColumnModel().getColumn(3).setMaxWidth(0);

        // Selection Mode und Listener
        widerstandsbeiwerteTabelleTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        widerstandsbeiwerteTabelleTable.getSelectionModel().
                addListSelectionListener(new MyListSelectionListener());
    }

    public String execute() {
        setVisible(true);
        return rc;
    }

    private void updateSumme() {
        ConversionUtil.setFormattedFloatInComponent(
                summeAllerWiderstandeWertLabel,
                JTableUtil.summarizeIntegerInTableColumnWithCount(widerstandsbeiwerteTabelleTable, 0, 2),
                Locale.GERMAN);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        pbOK = new javax.swing.JButton();
        pbCancel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        pbAdd = new javax.swing.JButton();
        pbRemove = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        widerstandsbeiwerteBezeichnungTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        widerstandsbeiwerteWiderstandsbeiwertTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        widerstandsbeiwerteAnzahlTextField = new javax.swing.JTextField();
        wbwImage = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        summeAllerWiderstandeWertLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        widerstandsbeiwerteTabelleTable = new javax.swing.JTable();

        setTitle("Widerstandsbeiwerte bearbeiten");
        setResizable(false);
        pbOK.setText("OK");
        pbOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbOKActionPerformed(evt);
            }
        });

        pbCancel.setText("Abbrechen");
        pbCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbCancelActionPerformed(evt);
            }
        });

        pbAdd.setText("<<");
        pbAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbAddAction(evt);
            }
        });

        pbRemove.setText(">>");
        pbRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pbRemoveAction(evt);
            }
        });

        jLabel1.setText("Bezeichnung");

        jLabel2.setText("Widerstandsbeiwert");

        jLabel3.setText("Anzahl");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pbAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pbRemove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1)
                    .addComponent(widerstandsbeiwerteAnzahlTextField)
                    .addComponent(widerstandsbeiwerteWiderstandsbeiwertTextField)
                    .addComponent(widerstandsbeiwerteBezeichnungTextField)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(8, 8, 8)
                .addComponent(widerstandsbeiwerteBezeichnungTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(pbAdd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(widerstandsbeiwerteWiderstandsbeiwertTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(pbRemove))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(widerstandsbeiwerteAnzahlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        wbwImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        wbwImage.setText("- kein Bild -");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("Summe aller Einzelwiderst\u00e4nde");

        summeAllerWiderstandeWertLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        summeAllerWiderstandeWertLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        summeAllerWiderstandeWertLabel.setText("0,00");
        summeAllerWiderstandeWertLabel.setName("widerstandsbeiweriteSummeAllerWiderstandeWertLabel");
        summeAllerWiderstandeWertLabel.setPreferredSize(new java.awt.Dimension(45, 14));

        widerstandsbeiwerteTabelleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Anzahl", "Bezeichnung", "Widerstandswert", "Bildablage"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        widerstandsbeiwerteTabelleTable.setColumnSelectionAllowed(true);
        widerstandsbeiwerteTabelleTable.setName("widerstandsbeiwerteTabelleTable");
        jScrollPane1.setViewportView(widerstandsbeiwerteTabelleTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                        .addComponent(summeAllerWiderstandeWertLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(wbwImage)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(pbOK)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(pbCancel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {pbCancel, pbOK});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wbwImage)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(summeAllerWiderstandeWertLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pbOK)
                    .addComponent(pbCancel)
                    .addComponent(jLabel5))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pbRemoveAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pbRemoveAction

        int row = widerstandsbeiwerteTabelleTable.getSelectedRow();

        if (row > WestaWacApplHelper.getInstance().getAnzahlWiderstandsbeiwerte()) {

            DefaultTableModel d = (DefaultTableModel) widerstandsbeiwerteTabelleTable.getModel();
            // Werte aus Tabelle übertragen
            widerstandsbeiwerteAnzahlTextField.setText("" + d.getValueAt(row, 0));
            widerstandsbeiwerteBezeichnungTextField.setText("" + d.getValueAt(row, 1));
            widerstandsbeiwerteWiderstandsbeiwertTextField.setText("" + d.getValueAt(row, 2));
            // Zeile löschen
            d.removeRow(row);
            // Neue Zeile selektieren
            if (row > 0) {
                row--;
            }
            widerstandsbeiwerteTabelleTable.changeSelection(row, 0, false, false);

        } else {
            // Fehlermeldung
            Tools.errbox(Strings.CANNOT_DELETE_GIVEN_FALUE);
        }
    }//GEN-LAST:event_pbRemoveAction

    private void pbAddAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pbAddAction

        DefaultTableModel d = (DefaultTableModel) widerstandsbeiwerteTabelleTable.getModel();
        d.addRow(new Object[]{
                    widerstandsbeiwerteAnzahlTextField.getText(),
                    widerstandsbeiwerteBezeichnungTextField.getText(),
                    widerstandsbeiwerteWiderstandsbeiwertTextField.getText()
                });
    }//GEN-LAST:event_pbAddAction

    private void pbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pbCancelActionPerformed
        rc = null;
        setVisible(false);
    }//GEN-LAST:event_pbCancelActionPerformed

    private void pbOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pbOKActionPerformed
        rc = JTableUtil.joinTableColum(widerstandsbeiwerteTabelleTable, 0);
        dispose();
    }//GEN-LAST:event_pbOKActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new WbwDialog(new javax.swing.JFrame(), true).setVisible(true);
            }

        });
    }

    public Object getSumme() {
        return summeAllerWiderstandeWertLabel.getText();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton pbAdd;
    private javax.swing.JButton pbCancel;
    private javax.swing.JButton pbOK;
    private javax.swing.JButton pbRemove;
    private javax.swing.JLabel summeAllerWiderstandeWertLabel;
    private javax.swing.JLabel wbwImage;
    private javax.swing.JTextField widerstandsbeiwerteAnzahlTextField;
    private javax.swing.JTextField widerstandsbeiwerteBezeichnungTextField;
    private javax.swing.JTable widerstandsbeiwerteTabelleTable;
    private javax.swing.JTextField widerstandsbeiwerteWiderstandsbeiwertTextField;
    // End of variables declaration//GEN-END:variables
}

/*
 * SeeTextField.java
 *
 * Created on 11. August 2006, 18:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.westaflex.component.classes;

import com.sun.star.comp.beans.OfficeDocument;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameAccess;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.table.XCell;
import com.sun.star.table.XCellRange;
import com.sun.star.text.XText;
import com.sun.star.text.XTextTable;
import com.sun.star.text.XTextTablesSupplier;
import com.sun.star.uno.UnoRuntime;
import com.westaflex.resource.Strings.Strings;
import java.awt.Container;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author Oliver
 */
public class SeeTable extends JTable
        implements DocumentAware {
    private RaumItem.PROP prop = null;

    private String myRef = null;
    private int firstRow = 0;
    private int firstCol = 0;
    private int lastCol = 0;
    private int[] colIndex = null;

    /** Creates a new instance of SeeTextField */
    public SeeTable() {
        super();
        getModel().addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent evt) {
                Container c = getTopLevelAncestor();
                if (c != null) {
                    c.firePropertyChange("Changed", 0, 1);
                }
            }
        });
    }

    public void setFirstRowCol(int firstRow, int firstCol) {
        this.firstRow = firstRow;
        this.firstCol = firstCol;
    }

    public void setLastCol(int lastCol) {
        this.lastCol = lastCol;
    }

    public void setColIndex(int[] colIndex) {
        this.colIndex = colIndex;
    }

    @Override
    public boolean toDoc(OfficeDocument oDoc) {
        int lastRow = getRowCount();
        int col = 0;

        myRef = getName();
        XTextTablesSupplier xTablesSupplier = (XTextTablesSupplier) UnoRuntime.queryInterface(XTextTablesSupplier.class, oDoc);
        XNameAccess xNamedTables = xTablesSupplier.getTextTables();
        if (xNamedTables.hasByName(myRef) == true) {
            try {
                System.out.println("Aktuelle Tabelle: " + myRef + " firstRow=" + firstRow + " firstCol=" + firstCol + " lastCol=" + lastCol);
                Object table = xNamedTables.getByName(myRef);
                XTextTable xTextTable = (XTextTable) UnoRuntime.queryInterface(XTextTable.class, table);
                for (int i = 0; i < lastRow; i++) {
                    System.out.println("writeCell(i=" + i + "   firstRow=" + firstRow + "   Col=" + col);
                    for (int j = 0; j < lastCol; j++) {
                        if (colIndex == null) {
                            col = j + firstCol;
                        } else {
                            col = colIndex[j];
                        }
                        if (col >= firstCol) {
                            writeCell(xTextTable, i + firstRow, col, this.getValueAt(i, j));
                        }
                    }

                }
            } catch (NoSuchElementException ex) {
                ex.printStackTrace();
            } catch (WrappedTargetException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    private void writeCell(XTextTable tab, int row, int col, Object cont) {

        XCell cell = null;
        if (row > tab.getRows().getCount()) {
            tab.getRows().insertByIndex(row - 1, 1);
        }
        try {
            XCellRange xCellRange = (XCellRange) UnoRuntime.queryInterface(XCellRange.class, tab);
            cell = xCellRange.getCellByPosition(col - 1, row - 1);
            XText xCellText = (XText) UnoRuntime.queryInterface(XText.class, cell);
            xCellText.setString((cont == null ? "" : cont.toString()));
//            System.out.format("Geschrieben in Zeile: %d, Spalte %d, Inhalt %s%n", row-1 , col-1, cont);
        } catch (com.sun.star.lang.IndexOutOfBoundsException ex) {
            System.err.format(Strings.TABLE_ERROR, tab.getClass().getName(), row, col, cont);
            ex.printStackTrace();
        }
    }

    @Override
    public Object getValue() {
        return toString();
    }

    @Override
    public String getString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setValue(Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setRaumItemProp( RaumItem.PROP prop ) {
        this.prop = prop;
    }

    @Override
    public RaumItem.PROP getRaumItemProp() {
        return this.prop;
    }
}

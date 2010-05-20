/*
 * JTableUtil.java
 *
 * Created on 5. August 2006, 12:46
 *
 */

package com.bensmann.superswing.component.util;

import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author rb
 */
public class JTableUtil {
    
    private static NumberFormat nfGerman;
    
    private static NumberFormat nfUs;
    
    /**
     * Save previous index of JSpinner for a certain JTable
     */
    private static Map<JTable, Integer> spinnerMovesTableMap;
    
    // Static initializer
    static {
        
        nfGerman = NumberFormat.getInstance(Locale.GERMAN);
        nfGerman.setGroupingUsed(true);
        nfGerman.setMinimumFractionDigits(2);
        nfGerman.setMaximumFractionDigits(2);
        
        nfUs = NumberFormat.getInstance(Locale.US);
        nfUs.setGroupingUsed(true);
        nfUs.setMinimumFractionDigits(2);
        nfUs.setMaximumFractionDigits(2);
        
        spinnerMovesTableMap = new Hashtable<JTable, Integer>();
        
    }
    
    /**
     * Creates a new instance of JTableUtil
     */
    public JTableUtil() {
    }
    
    /**
     * Hide a column in a table: just set column and header width to zero ;-)
     *
     * @param table
     * @param column
     */
    public static void hideColumn(JTable table, int column) {
        
        TableColumnModel columnModel = table.getColumnModel();
        TableColumnModel headerColumnModel =
                table.getTableHeader().getColumnModel();
        
        TableColumn tableColumn = columnModel.getColumn(column);
        TableColumn headerColumn = headerColumnModel.getColumn(column);
        
        // Set column width to zero
        tableColumn.setMaxWidth(0);
        tableColumn.setMinWidth(0);
        
        // Set column header width (to avoid the ... sign)
        headerColumn.setMaxWidth(0);
        headerColumn.setMinWidth(0);
    }
    
    /**
     * Set width of column in a table to 'width' pixels.
     *
     * @param table
     * @param column
     * @param width
     */
    public static void setColumnSize(JTable table, int column, int width) {
        table.getColumnModel().getColumn(column).setMinWidth(width);
        table.getColumnModel().getColumn(column).setMaxWidth(width);
    }
    
    /**
     * Set horizontal alignment of a certain cell in a certain row.
     *
     * @param table
     * @param row
     * @param column
     * @param alignment
     */
    public static void setHorizontalColumnAlignment(JTable table, int row, int column, int alignment) {
        ((DefaultTableCellRenderer) table.getCellRenderer(row, column)).setHorizontalAlignment(alignment);
    }
    
    /**
     * Set alignment of a cell in all rows of a table.
     *
     * @param table
     * @param column
     * @param alignment
     */
    public static void setHorizontalColumnAlignment(JTable table, int column, int alignment) {
        
        for (int row = 0; row < table.getRowHeight() - 1; row++) {
            setHorizontalColumnAlignment(table, row, column, alignment);
        }
        
    }
    
    /**
     * Parse a formatted float-string (set using NumberFormat) from a table cell
     *
     * @param table
     * @param row
     * @param col
     * @return
     */
    public static float parseFloatFromTableCell(JTable table, int row, int col) {
        
        float f = 0f;
        Object o = null;
        String s = null;
        Locale locale = null;
        
        o = table.getModel().getValueAt(row, col);
        
        if (o instanceof Float) {
            f = (Float) o;
        } else if (o instanceof String) {
            
            s = "" + o;
            
            locale = ConversionUtil.getLocaleFromFloat(s);
            if (locale == Locale.US) {
                
                try {
                    f = nfUs.parse(s).floatValue();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                
            } else if (locale == Locale.GERMAN) {
                
                try {
                    f = nfGerman.parse(s).floatValue();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                
            } else {
                // TODO No locale?!?!
            }
            
        }
        
        return f;
        
    }
    
    /**
     *
     * @param table
     * @param row
     * @param col
     * @return
     */
    public static int parseIntegerFromTableCell(JTable table, int row, int col) {
        
        int i = 0;
        
        try {
            i = new Integer("" + table.getValueAt(row, col));
        } catch (NumberFormatException e) {
            // Ignore
        }
        
        return i;
        
    }
    
    /**
     * Takes a float and sets it as a formatted value in a certain table cell
     *
     * @param table
     * @param row
     * @param col
     * @param f
     */
    public static void setFormattedFloatInTableCell(JTable table, int row, int col, float f) {
        // TODO: german format is default!
        table.getModel().setValueAt(nfGerman.format(f), row, col);
    }
    
    /**
     *
     * @param table
     * @param row
     * @param col
     * @param f
     * @param maxFractionDigits
     */
    public static void setFormattedFloatInTableCell(JTable table, int row, int col, float f, int maxFractionDigits) {
        // TODO: german format is default!
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
        nf.setMinimumFractionDigits(maxFractionDigits);
        nf.setMaximumFractionDigits(maxFractionDigits);
        table.getModel().setValueAt(nf.format(f), row, col);
    }
    
    /**
     * Set a value in a certain row/col of a table
     *
     * @param table
     * @param row
     * @param col
     * @param value
     */
    public static void setTableCell(JTable table, int row, int col, Object value) {
        table.getModel().setValueAt(value, row, col);
    }
    
    /**
     * Move rows in a JTable using a JSpinner
     *
     * @param selectedRow
     * @param table
     * @param spinner
     */
    public static void spinnerMovesTableRow(JTable table, JSpinner spinner,
            Integer selectedRow) {
        
        DefaultTableModel tableModel = null;
        SpinnerNumberModel spinnerModel = null;
        int value = -1;
        Integer previousValue = null;
        int row = -1;
        int newRow = -1;
        
        spinnerModel = (SpinnerNumberModel) spinner.getModel();
        value = Integer.valueOf("" + spinnerModel.getValue());
        previousValue = spinnerMovesTableMap.get(table);
        if (previousValue == null) {
            previousValue = 0;
        }
        
        tableModel = (DefaultTableModel) table.getModel();
        if (selectedRow == null) {
            row = table.getSelectedRow();
        } else {
            row = selectedRow;
        }
        
        newRow = row;
        
        // Move up
        if (previousValue < value) {
            
            // Calculate new row
            newRow = row - 1;
            if (newRow == -1) {
                newRow = table.getRowCount() - 1;
            }
            
        }
        // Move down
        else if (previousValue > value) {
            
            // Calculate new row
            newRow = row + 1;
            if (newRow == table.getRowCount()) {
                newRow = 0;
            }
            
        }
        
        // Move row in table
        tableModel.moveRow(row, row, newRow);
        // Select/highlight new row
        table.changeSelection(newRow, 0, false, false);
        
        // Save actual value as previous value
        spinnerMovesTableMap.put(table, new Integer(value));
        
    }
    
    /**
     *
     * @param table
     * @param spinner
     */
    public static void spinnerMovesTableRow(JTable table, JSpinner spinner) {
        spinnerMovesTableRow(table, spinner, null);
    }
    
    /**
     *
     * @param table
     * @param column
     * @return
     */
    public static float summarizeIntegerInTableColumn(JTable table, int column) {
        
        float f = 0f;
        
        for (int row = 0; row < table.getRowCount(); row++) {
            f += parseIntegerFromTableCell(table, row, column);
        }
        
        return f;
    }
    
    public static String joinTableColum(JTable table, int column){
        StringBuffer sb = new StringBuffer();
        String s = null;
        
        for (int i = 0; i < table.getRowCount(); i++) {
            s = (String) table.getValueAt(i, column);
            if (s == null){
                s = "";
            }
            sb.append(s + ";");
        }
        if (sb.length() > 0){
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }
    
    /**
     * Compute sum of integer values in a column that is multiplied with another
     * column.
     *
     * @param table
     * @param column
     * @return
     */
    public static float summarizeIntegerInTableColumnWithCount(JTable table,
            int countColumn, int column) {
        
        float f = 0f;
        
        for (int row = 0; row < table.getRowCount(); row++) {
            f +=
                    parseIntegerFromTableCell(table, row, countColumn) *
                    parseFloatFromTableCell(table, row, column);
        }
        
        return f;
        
    }
    
    /**
     *
     * @param table
     * @param column
     * @return
     */
    public static float summarizeFloatInTableColumn(JTable table, int column) {
        
        float f = 0f;
        
        for (int row = 0; row < table.getRowCount(); row++) {
            f += parseFloatFromTableCell(table, row, column);
        }
        
        return f;
        
    }
    
    /**
     *
     * @param table
     * @param column
     * @return
     */
    public static float summarizeFloatInTableColumn(JTable table, int column, int fromRow, int toRow) {
        
        float f = 0f;
        
        for (int row = fromRow; row < toRow; row++) {
            f += parseFloatFromTableCell(table, row, column);
        }
        
        return f;
        
    }
    
    /**
     * Compute sum of float values in a column that is multiplied with another
     * column.
     *
     * @param table
     * @param column
     * @return
     */
    public static float summarizeFloatInTableColumnWithCount(JTable table,
            int countColumn, int column) {
        
        float f = 0f;
        
        for (int row = 0; row < table.getRowCount(); row++) {
            f +=
                    parseFloatFromTableCell(table, row, countColumn) *
                    parseFloatFromTableCell(table, row, column);
        }
        
        return f;
        
    }
    
    /**
     * Copute sum of all float values (columns) per row
     *
     * @param row
     * @param table
     */
    public float summarizeFloatInTableRow(JTable table, int row) {
        
        float sum = 0f;
        
        for (int column = 0; column < table.getColumnCount(); column++) {
            sum += parseFloatFromTableCell(table, row, column);
        }
        
        return sum;
        
    }
    
    /**
     * Copute sum of all float values (columns) in certain rows:
     * 'fromRow' to 'toRow'
     *
     * @param row
     * @param table
     */
    public float summarizeFloatInTableRow(JTable table, int fromRow, int toRow) {
        
        float sum = 0f;
        
        for (int row = fromRow; row < toRow; row++) {
            
            for (int column = 0; column < table.getColumnCount(); column++) {
                sum += parseFloatFromTableCell(table, row, column);
            }
            
        }
        
        return sum;
        
    }
    
    /**
     *
     * @param source
     * @return
     */
    public static JTable copyTable(JTable source) {
        
        TableModel sourceModel = source.getModel();
        int cols = sourceModel.getColumnCount();
        int rows = sourceModel.getRowCount();
        Object[][] o = new Object[rows][cols];
        TableModel destinationModel = null;
        
        for (int i = 0; i < rows; i++) {
            
            for (int j = 0; j < cols; j++) {
                o[i][j] = sourceModel.getValueAt(i, j);
            }
            
        }
        
        try {
            
            destinationModel = sourceModel.getClass().
                    getConstructor(new Class[] { Object[][].class, Object[].class }).
                    newInstance(new Object[] { o, new String[] { "A","B","C" }});
            
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        
        return new JTable(destinationModel);
        
    }
    
    /**
     * Copy data of one table to another. If no destination table is given
     * as argument, a new table with a DefaultTableModel is created.
     *
     * The destination table should have a table model that is at least
     * a DefaultTableModel (or derived).
     *
     * @param source
     * @param destination
     * @return Destination table
     */
    public static JTable copyTableData(JTable source, JTable destination, int startCol) {
        
        int cols = source.getColumnCount();
        int rows = source.getRowCount();
        Object[][] o = new Object[rows][cols];
        
        if (destination == null) {
            destination = new JTable(new DefaultTableModel(
                    source.getRowCount(), source.getColumnCount()));
        }
        
        for (int row = 0; row < rows; row++) {
            
            for (int column = 0+startCol; column < cols; column++) {
                o[row][column] = source.getValueAt(row, column);
            }
            
            setTableRow(destination, row, o[row]);
            
        }
        
        return destination;
        
    }
    
    /**
     * Add a row with data in a table. When the row does not exist, a new
     * row is created using DefaultTableModel.addRow.
     *
     * @param table
     * @param row
     * @param data
     */
    public static void setTableRow(JTable table, int row, Object[] data) {
        
        if (table.getRowCount() - 1 >= row) {
            
            for(int column = 0; column < table.getColumnCount(); column++) {
                table.setValueAt(data[column], row, column);
            }
            
        } else {
            ((DefaultTableModel) table.getModel()).addRow(data);
        }
        
    }
    
    /**
     *
     * @param table
     */
    public static void removeAllRows(JTable table) {
        
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        for (int row = 0; row < table.getRowCount(); row++) {
            model.removeRow(row);
        }
        
    }
    
    /**
     *
     * @param table
     */
    public static void dumpTable(JTable table) {
        
        TableModel model = table.getModel();
        int cols = model.getColumnCount();
        int rows = model.getRowCount();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.println(i + "/" + j + "=" + model.getValueAt(i, j));
            }
        }
        
    }
    
}

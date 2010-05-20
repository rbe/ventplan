/*
 * Created on 07.04.2005
 *
 */
package com.bensmann.superframe.persistence.jdbc;

import com.bensmann.superframe.java.Debug;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * This DML meta data object holds all information that are needed to perform
 * DML queries on a table:
 * <p>- Prepared statements for INSERT and UPDATE
 * <p>- Sequence of columns to apply values from request
 *
 * We need one object per query/combination of table and colunms
 *
 * @author rb
 * @version $Id: DmlMetaData.java,v 1.1 2005/07/19 15:51:40 rb Exp $
 */
public class DmlMetaData {
    
    /**
     * Map that holds instances of DmlMetaData per ident (String) that
     * consists of name of SQL connection and name of SQL table
     */
    private static Map<String, DmlMetaData> instances =
            new HashMap<String, DmlMetaData>();
    
    /**
     * Name of SQL table
     */
    private String sqlTable;
    
    /**
     * JDBC connection object (to get a prepared statement)
     */
    private SingleJdbcConnection sjc;
    
    /**
     * Prepared INSERT statement
     */
    private PreparedStatement insertStmt;
    
    /**
     * Prepared UPDATE statement
     */
    private PreparedStatement updateStmt;
    
    /**
     * ColumnDescription for every column
     */
    private Map<String, ColumnDescription> columnDescriptions =
            Collections.synchronizedMap(new HashMap<String, ColumnDescription>());
    
    /**
     * Map of columns; key=Integer/position in prepared statement, value =
     * column name
     */
    private Map<Integer, String> columnOrderByNumber =
            Collections.synchronizedMap(new HashMap<Integer, String>());
    
    /**
     * Reverse: map of columns; key=column name,
     * value=Integer/position in prepared statement
     */
    private Map<String, Integer> columnOrderByColumn =
            Collections.synchronizedMap(new HashMap<String, Integer>());
    
    /**
     * Columns (e.g. got from request)
     */
    private Vector columns;
    
    /**
     * Constructor
     *
     * @param sqlTable
     * @throws SQLException
     */
    private DmlMetaData(SingleJdbcConnection sjc, String sqlTable,
            Vector columns) throws SQLException {
        
        this.sjc = sjc;
        this.sqlTable = sqlTable;
        this.columns = columns;
        
        readColumns();
        
    }
    
    /**
     * Get certain DmlMetaData object for a certain SQL connection and table
     * @param sjc
     * @param sqlTable
     * @param columns
     * @throws java.sql.SQLException
     * @return
     */
    public static DmlMetaData getInstance(SingleJdbcConnection sjc,
            String sqlTable, Vector columns) throws SQLException {
        
        // Debug
        Debug.log("sjc=" + sjc + " sqlTable=" + sqlTable + " columns=" + columns);
        
        // Generate ident for DmlMetaData object
        String ident = sjc.getJdbcDbname() + "_" + sqlTable + "_" +
                columns.hashCode();
        
        // Try to retrieve it from Map instances. If no object exists,
        // create a new one and store it in Map
        DmlMetaData dmd = instances.get(ident);
        if (dmd == null) {
            dmd = new DmlMetaData(sjc, sqlTable, columns);
            instances.put(ident, dmd);
        }
        
        return dmd;
        
    }
    
    /**
     * Read columns from table using metadata and store ColumnDescription
     * objects
     *
     * @throws SQLException
     *
     */
    private void readColumns() throws SQLException {
        
        StringBuffer sqlQuery = new StringBuffer("SELECT ");
        Iterator sqlTableParamsIterator = null;
        int parameterIndex = 0;
        
        // List all columns
        sqlTableParamsIterator = columns.iterator();
        while (sqlTableParamsIterator.hasNext()) {
            
            // Retrieve column name with prefix used in HTML form 'table_'
            String c = ((String) sqlTableParamsIterator.next()).replaceAll(
                    sqlTable + "_", "");
            
            // If column is not "ID" increase parameter index and put column
            // into Map for lookup when setting values for prepared statement
            if (!c.equalsIgnoreCase("ID")) {
                
                parameterIndex++;
                columnOrderByNumber.put(new Integer(parameterIndex), c);
                columnOrderByColumn.put(c, new Integer(parameterIndex));
                
                // Debug
                Debug.log("Column '" + c + "' is at parameter index " +
                        parameterIndex);
                
                // Add column to query
                sqlQuery.append(c);
                if (sqlTableParamsIterator.hasNext()) {
                    sqlQuery.append(", ");
                }
                
            } else {
                // Debug
                Debug.log("Column '" + c + "' ignored");
            }
            
        }
        
        // Add FROM ... WHERE clause to query
        sqlQuery.append(" FROM " + sqlTable + " WHERE 1 = 0");
        
        // Query table for fields and create ColumnDescriptions
        MyResult myResult = sjc.executeQuery(sqlQuery.toString());
        ResultSetMetaData rsmd = myResult.getResultSet().getMetaData();
        for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
            
            String n = rsmd.getColumnName(i);
            String t = rsmd.getColumnTypeName(i);
            int p = rsmd.getPrecision(i);
            int s = rsmd.getScale(i);
            
            ColumnDescription cd = new ColumnDescription(n, t, null, p, s);
            
            // Debug
            Debug.log("Adding column description: " + cd.toString());
            
            // Save column description in map
            columnDescriptions.put(n, cd);
            
        }
        
        // Close myResult
        myResult.close();
        myResult = null;
        
    }
    
    /**
     *
     * @param column
     * @return
     */
    public ColumnDescription getColumnDescription(String column) {
        return (ColumnDescription) columnDescriptions.get(column);
    }
    
    /**
     * Return parameter index in prepared statement for a certain column
     *
     * @param column
     * @return
     */
    public int getParameterIndex(String column) {
        
        column = column.replaceAll(sqlTable + "_", "");
        Integer i = (Integer) columnOrderByColumn.get(column);
        
        // Debug
        Debug.log("Parameter index for column '" + column + "' is " + i);
        
        return i.intValue();
        
    }
    
    /**
     * Generate an UPDATE statement for a given SQL table using request
     * parameters that have the sql table name as a prefix 'sqlTable_'. The
     * statement should be an prepared statement that is saved in the session
     * and re-used next time this insert should take place
     *
     * @param sqlTable
     * @return
     * @throws SQLException
     */
    public PreparedStatement getUpdateStatement() throws SQLException {
        
        StringBuffer sqlQuery = new StringBuffer();
        
        if (updateStmt == null) {
            
            // Debug
            Debug.log("Creating new prepared UPDATE statement");
            
            // Create SQL INSERT statement for table
            sqlQuery.append("UPDATE " + sqlTable + " SET ");
            
            // Append column names
            int j = columnOrderByNumber.size();
            for (int i = 1; i < j + 1; i++) {
                
                String c = (String) columnOrderByNumber.get(new Integer(i));
                
                sqlQuery.append(c + " = ?");
                
                if (i < j) {
                    sqlQuery.append(", ");
                }
                
                // Debug
                Debug.log("Added column '" + c + "' to statement");
                
            }
            
            // Append WHERE condition
            sqlQuery.append(" WHERE id = ?");
            
            // Debug
            Debug.log("Generated prepared UPDATE statement: " +
                    sqlQuery.toString());
            
            // Generate prepared statement
            updateStmt = sjc.getPreparedStatement(sqlQuery.toString());
            
        }
        
        return updateStmt;
        
    }
    
    /**
     * Generate an INSERT statement for a given SQL table using request
     * parameters that have the sql table name as a prefix 'sqlTable_'. The
     * statement should be an prepared statement that is saved in the session
     * and re-used next time this insert should take place
     *
     * @param sqlTable
     * @return
     * @throws SQLException
     */
    public PreparedStatement getInsertStatement() throws SQLException {
        
        // Create SQL INSERT statement for table
        StringBuffer sqlQuery = new StringBuffer("INSERT INTO " + sqlTable);
        StringBuffer sqlQueryColumns = new StringBuffer(" (");
        StringBuffer sqlQueryValues = new StringBuffer(" VALUES (");
        
        insertStmt = null;
        
        if (insertStmt == null) {
            
            // Debug
            Debug.log("Creating new prepared INSERT statement");
            
            // Append column names
            String placeholder = "?";
            int j = columnOrderByNumber.size();
            for (int i = 1; i < j + 1; i++) {
                
                String c = (String) columnOrderByNumber.get(new Integer(i));
                
                // Append column to SQL query
                sqlQueryColumns.append(c);
                
                // Append placeholder
                sqlQueryValues.append(placeholder);
                
                // If there are more columns to add, append comma to statement
                if (i < j) {
                    sqlQueryColumns.append(", ");
                    sqlQueryValues.append(", ");
                }
                
                // Debug
                Debug.log("Added column '" + c + "' to statement");
                
            }
            
            // Add ID column
            sqlQueryColumns.append(", id");
            sqlQueryValues.append(", " + sqlTable + "_seq.nextval");
            
            // Build complete SQL query
            sqlQuery.append(sqlQueryColumns).
                    append(")").
                    append(sqlQueryValues).
                    append(")");
            
            // Debug
            Debug.log("Generated prepared INSERT statement: " +
                    sqlQuery.toString());
            
            // Generate prepared statement
            insertStmt = sjc.getPreparedStatement(sqlQuery.toString());
            
        }
        
        try {
            // Check connection for statement
            sjc.checkConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        return insertStmt;
        
    }
    
}
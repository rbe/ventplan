/*
 * Created on 06.04.2005
 *
 */
package com.bensmann.superframe.persistence.jdbc;

import java.util.Calendar;

/**
 * @author rb
 * @version $Id: ColumnDescription.java,v 1.1 2005/07/19 15:51:40 rb Exp $
 *  
 */
public class ColumnDescription {

    private String columnName;

    private String columnType;

    private Object columnValue;

    private int columnPrecision;

    private int columnScale;

    /**
     * Calendar is used to store millisecond information if column is of type
     * DATE
     */
    private Calendar calendar;

    /**
     * Create description for a colunm with name, type and its value
     * 
     * @param columnName
     * @param columnType
     * @param columnValue
     * @param columnPrecision
     * @param columnScale
     */
    public ColumnDescription(String columnName, String columnType,
            Object columnValue, int columnPrecision, int columnScale) {

        this.columnName = columnName;
        this.columnType = columnType;
        this.columnValue = columnValue;
        this.columnPrecision = columnPrecision;
        this.columnScale = columnScale;

    }

    /**
     * Returns name of column that is described here
     * 
     * @return
     */
    public String getColunmName() {
        return columnName;
    }

    /**
     * Returns type of column that is described here
     * 
     * @return
     */
    public String getColumnType() {
        return columnType;
    }

    /**
     * Returns value of column that is described here
     * 
     * @return
     */
    public Object getColumnValue() {
        return columnValue;
    }

    /**
     * Return precision of column that is described here
     * 
     * @return
     */
    public int getColumnPrecision() {
        return columnPrecision;
    }

    /**
     * Return scale of column that is described here
     * 
     * @return
     */
    public int getColumnScale() {
        return columnScale;
    }

    /**
     * Returns a calendar object. If the object is null we create a new instance
     * of Calendar. Used with SQL DATE-columns
     * 
     * @return
     */
    public Calendar getCalendar() {

        if (calendar == null) {
            calendar = Calendar.getInstance();
        }

        return calendar;

    }

    /**
     * 
     * @param columnValue 
     */
    public void setColumnValue(Object columnValue) {
        this.columnValue = columnValue;
    }

    /**
     * 
     * @return 
     */
    public String toString() {
        return "Column[" + columnName + "]Type[" + columnType + "]Precision["
                + columnPrecision + "]Scale[" + columnScale + "]";
    }

}
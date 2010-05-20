/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.westaflex.component.classes;

/**
 *
 * @author gz
 */
public class RaumItem {

    public static enum PROP{    RAUMNUMMER,RAUMNAME, RAUMTYP, GESCHOSS, LUFTART,
                            FAKTOR_ZULUFTVERTEILUNG,VORGABELUFTMENGE,
                            ZULUFTVOLUMENSTROM,ABLUFTVOLUMENSTROM,
                            DURCHLASSPOSITION_ZULUFT, DURCHLASSPOSITION_ABLUFT,
                            KANALANSCHLUSS_ZULUFT, KANALANSCHLUSS_ABLUFT,
                            TUEREN,MAX_TUERSPALTHOEHE,RAUMLAENGE,RAUMBREITE,
                            RAUMFLAECHE, RAUMVOLUMEN, RAUMHOEHE
    }

    private String columnName;
    private int columnIndex;
    private PROP prop;
    private Class propClass;
    private String toString;

    public RaumItem( PROP prop, String columnName, int columnIndex, Class propClass ){
        this.prop = prop;
        this.columnName = columnName;
        this.columnIndex = columnIndex;
        this.propClass = propClass;

        StringBuffer sb = new StringBuffer( prop.toString() );
        sb.append( "[");
        sb.append( columnIndex );
        sb.append("]-");
        sb.append( propClass.getName() );
        this.toString = sb.toString();
    }

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @return the columnIndex
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * @return the prop
     */
    public PROP getProp() {
        return prop;
    }

    /**
     * @return the propClass
     */
    public Class getPropClass() {
        return propClass;
    }

    @Override
    public String toString() {
        return this.toString;
    }




}

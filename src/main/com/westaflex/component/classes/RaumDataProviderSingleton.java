/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.westaflex.component.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author gz
 */
public final class RaumDataProviderSingleton {

    private static RaumDataProviderSingleton INSTANCE = null;

    private HashMap<RaumItem.PROP, RaumItem> itemsByProp = new HashMap<RaumItem.PROP, RaumItem>();

    private HashMap<String, RaumItem> itemsByColName = new HashMap<String, RaumItem>();

    private HashMap<Integer, RaumItem> itemsByColIndex = new HashMap<Integer, RaumItem>();

    private ArrayList<String> columnNames = new ArrayList<String>();

    private RaumDataProviderSingleton() {
        initializeItems();
    }

    public static final RaumDataProviderSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RaumDataProviderSingleton();
        }

        return INSTANCE;
    }

    public RaumItem getRaumItemByColumnIndex(int index) {
        return this.itemsByColIndex.get(index);
    }

    public RaumItem getRaumItemByProp(RaumItem.PROP prop) {
        return this.itemsByProp.get(prop);
    }

    private void initializeItems() {
        RaumItem ri = new RaumItem(RaumItem.PROP.RAUMNUMMER, "Raumnummer", 0, String.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.RAUMNAME, "Raumname", 1, String.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.RAUMTYP, "Raumtyp", 2, String.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.GESCHOSS, "Geschoss", 3, String.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.LUFTART, "Luftart", 4, String.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.FAKTOR_ZULUFTVERTEILUNG, "Faktor Zuluftverteilung", 5, Float.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.VORGABELUFTMENGE, "Vorgabeluftmenge", 6, Float.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.ZULUFTVOLUMENSTROM, "Zuluftvolumenstrom", 7, Float.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.ABLUFTVOLUMENSTROM, "Abluftvolumenstrom", 8, Float.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.DURCHLASSPOSITION_ZULUFT, "Durchlassposition Zuluft", 9, String.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.DURCHLASSPOSITION_ABLUFT, "Durchlassposition Abluft", 10, String.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.KANALANSCHLUSS_ZULUFT, "Kanalanschluss Zuluft", 11, String.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.KANALANSCHLUSS_ABLUFT, "Kanalanschluss Abluft", 12, String.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.TUEREN, "Tueren", 13, Vector.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.MAX_TUERSPALTHOEHE, "Max. Türspalthöhe", 14, Float.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.RAUMLAENGE, "Raumlänge", 15, Float.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.RAUMBREITE, "Raumbreite", 16, Float.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.RAUMFLAECHE, "Raumfläche", 17, Float.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.RAUMVOLUMEN, "Raumvolumen", 18, Float.class);
        addRaumItem(ri);
        ri = new RaumItem(RaumItem.PROP.RAUMHOEHE, "Raumhöhe", 19, Float.class);
        addRaumItem(ri);
    }

    private void addRaumItem(RaumItem ri) {
        this.itemsByProp.put(ri.getProp(), ri);
        this.itemsByColName.put(ri.getColumnName(), ri);
        this.itemsByColIndex.put(ri.getColumnIndex(), ri);
        columnNames.add(ri.getColumnName());
    }

    public String[] getColumnNames() {
        return columnNames.toArray(new String[columnNames.size()]);
    }

}

/*
 * WAC
 *
 * Copyright (C) 2005      Informationssysteme Ralf Bensmann.
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

import groovy.sql.*
import org.javanicus.gsql.*

/**
 * Erstellen der Stückliste aus den vorhandenen Werten.
 */
class VerlegeplanService {

    /**
	 *
	 */
	public static boolean DEBUG = false

    // Geschosse:
    //      {KG; EG; OG; DG; SB}
    // Pro Geschoss:
    //      * Geschoss a[i,0]
    //      * Anzahl Zuluftventile pro Geschoss a[i,1]
    //      * Zuluftvolumenstrom pro Geschoss a[i,2]
    //      * Gesamt Zuluftvolumenstrom = Volumenstrom des Zentralgeräts a[i,3]
    //      * Anzahl Abluftventile pro Geschoss a[i,4]
    //      * Abluftvolumenstrom pro Geschoss a[i,5]
    //      * Gesamt Abluftvolumenstrom = Volumenstrom des Zentralgeräts a[i,6]
	def stucklistArray = [ [ "KG", "EG", "OG", "DG", "SB" ],
                           [ ] ]
	
	/**
	 * 
	 */
	def VerlegeplanService() {
		
	}

    /**
     *
     public Object[] collectTransferData() {Object[] ret = new Object[4];
     Object[][] controls = new Object[][]{{"Bauvorhaben", adBauvorhabenTextField},{"Firma1", afFirma1TextField},{"Firma2", afFirma2TextField},{"Strasse", afStrasseTextField},{"PlzOrt", afPlzOrtTextField},{"Telefon", afTelefonTextField},{"Telefax", afFaxTextField},{"Ansprechpartner", afAnsprechpartnerTextField},{"Volumenstrom", lmeVolumenstromCombobox},{"Zentralgeraet", lmeZentralgeraetCombobox},{"Aussenluft", aussenluftButtonGroup},{"Fortluft", fortluftButtonGroup},{"Geraetestandort", geraetestandortButtonGroup}};//{"Ueberstroemelement", lmeTabelleTable.getValueAt(row, column)},
     com.sun.star.beans.NamedValue[] first = new com.sun.star.beans.NamedValue[controls.length + 1];
     for (int i = 0; i < controls.length; i++) {first[i] = new com.sun.star.beans.NamedValue((String) controls[i][0], ((DocumentAware) controls[i][1]).getString());}// Hier kommt die Grafik hin!
     Description desc = new Description();
     int lmeRowCount = lmeTabelleTable.getRowCount();
     for (int i = 0; i < lmeRowCount; i++) {String luftart = (String) lmeTabelleTable.getValueAt(i, 1); // Luftart
     String verteilebene = (String) lmeTabelleTable.getValueAt(i, 10); // Verteilebene
     String ventil = null;
     String raumname = null;
     if (luftart.equals("AB")) {ventil = (String) lmeTabelleTable.getValueAt(i, 9);
     raumname = (String) lmeTabelleTable.getValueAt(i, 0) + " / " + ventil;
     desc.addConnector(luftart, verteilebene, raumname);} else if (luftart.equals("ZU")){ventil = (String) lmeTabelleTable.getValueAt(i, 8);
     raumname = (String) lmeTabelleTable.getValueAt(i, 0) + " / " + ventil;
     desc.addConnector(luftart, verteilebene, raumname);} else if (luftart.equals("ZU/AB")) {// AB
     ventil = (String) lmeTabelleTable.getValueAt(i, 9);
     raumname = (String) lmeTabelleTable.getValueAt(i, 0) + " / " + ventil;
     desc.addConnector("AB", verteilebene, raumname);
     // ZU
     ventil = (String) lmeTabelleTable.getValueAt(i, 8);
     raumname = (String) lmeTabelleTable.getValueAt(i, 0) + " / " + ventil;
     desc.addConnector("ZU", verteilebene, raumname);}}//
     WestaDB westaDb = WestaDB.getInstance();
     ResultSet rs;
     //
     // 4. Außenluft
     //
     rs = westaDb.queryDB("select ~stueckliste~.~Artikel~, ~stueckliste~.~Anzahl~ from ~stueckliste~, ~pakete~ where ~stueckliste~.~Paket~ = ~pakete~.~ID~ and ~pakete~.~Kategorie~= 70 and ~pakete~.~Geraet~= '" + lmeZentralgeraetCombobox.getString() + "' and ~pakete~.~Bedingung~ = '" + aussenluftButtonGroup.getValue().toString() + "' and ~stueckliste~.~Reihenfolge~ = 10");
     try {rs.next();
     desc.putName("aussenluft", rs.getString(1));} catch (SQLException ex) {Logger.getLogger(ProjectInternalFrame.class.getName()).log(Level.SEVERE, null, ex);}//
     // 5. Fortluft
     //
     rs = westaDb.queryDB("select ~stueckliste~.~Artikel~, ~stueckliste~.~Anzahl~ from ~stueckliste~, ~pakete~ where ~stueckliste~.~Paket~ = ~pakete~.~ID~ and ~pakete~.~Kategorie~= 71 and ~pakete~.~Geraet~= '" + lmeZentralgeraetCombobox.getString() + "' and ~pakete~.~Bedingung~ = '" + fortluftButtonGroup.getValue().toString() + "' and ~stueckliste~.~Reihenfolge~ = 10");
     try {rs.next();
     desc.putName("fortluft", rs.getString(1));} catch (SQLException ex) {Logger.getLogger(ProjectInternalFrame.class.getName()).log(Level.SEVERE, null, ex);}desc.putName("zentralgeraet", lmeZentralgeraetCombobox.getString());
     try {first[first.length - 1] = new com.sun.star.beans.NamedValue("grafik", desc.drawText().getAbsolutePath());} catch (Exception e) {}// Beim Volumenstrom die Kommata entfernen
     first[8].Value = ((String) first[8].Value).replaceAll(",", ".");
     //
     final int UEBERSTROEM = 5;
     final int VENTILBEZEICHNUNG = 9;
     final int VERTEILEBENE = 10;
     ret[0] = first;
     ret[1] = makeNamedValuesFromTable(lmeTabelleTable, VERTEILEBENE); // 10
     ret[2] = makeNamedValuesFromTable(lmeTabelleTable, VENTILBEZEICHNUNG); // 9
     ret[3] = makeNamedValuesFromTable(lmeTabelleUeberstroemTable, UEBERSTROEM); // 5
     return ret;}com.sun.star.beans.NamedValue[] makeNamedValuesFromTable(SeeTable table, int index) {final int LUFTART = 1, ANZAHLABLUFT = 4, ANZAHLZULUFT = 7, LUFTMENGE = 6;
     HashMap hm = new HashMap();
     String itemName = "";
     String itemName2 = "";
     String keyString[] = null;
     float itemCount = 0;
     float itemCountZU = 0;
     float luftMenge = 0;
     for (int i = 0; i < table.getRowCount(); i++) {if (((String) lmeTabelleTable.getValueAt(i, LUFTART)).contains("ZU") && index == 9) {itemName2 = (String) table.getValueAt(i, index - 1);} else {if (index == 5) {itemName2 = "";} else {itemName2 = (String) table.getValueAt(i, index);}}//System.out.println(String.format("i = %d;index =%d", i, index));
     // Nur für Abluft
     itemName = (String) table.getValueAt(i, index);
     if ((itemName != null) && (!itemName.isEmpty()) && index != 5
     && ((String) lmeTabelleTable.getValueAt(i, LUFTART)).contains("AB")) {itemCount = JTableUtil.parseFloatFromTableCell(table, i, ANZAHLABLUFT);
     luftMenge = JTableUtil.parseFloatFromTableCell(table, i, LUFTMENGE);
     itemName += "AB";
     // itemName ist die Zusammensetzung aus Luftart und Verteilebene
     if (hm.containsKey(itemName)) {keyString = ((String) hm.get(itemName)).split(";");
     itemCount = itemCount + Float.parseFloat(keyString[0]);
     luftMenge = luftMenge + Float.parseFloat(keyString[1]);}hm.put(itemName, itemCount + ";" + luftMenge);}// Nur für Zuluft
     if ((itemName2 != null) && (!itemName2.isEmpty())
     && ((String) lmeTabelleTable.getValueAt(i, LUFTART)).contains("ZU")) {itemCountZU = JTableUtil.parseFloatFromTableCell(table, i, ANZAHLZULUFT);
     luftMenge = JTableUtil.parseFloatFromTableCell(table, i, LUFTMENGE);
     itemName2 += "ZU";
     if (hm.containsKey(itemName2)) {keyString = ((String) hm.get(itemName2)).split(";");
     itemCountZU = itemCountZU + Float.parseFloat(keyString[0]);
     luftMenge = luftMenge + Float.parseFloat(keyString[1]);}hm.put(itemName2, itemCountZU + ";" + luftMenge);}// Nur für Überströmelemente
     if ((itemName != null) && (!itemName.isEmpty()) && index == 5) {itemCount = JTableUtil.parseFloatFromTableCell(table, i, 3);
     if (hm.containsKey(itemName)) {keyString = ((String) hm.get(itemName)).split(";");
     itemCount = itemCount + Float.parseFloat(keyString[0]);
     luftMenge = luftMenge + Float.parseFloat(keyString[1]);}hm.put(itemName, itemCount + ";" + luftMenge);}}com.sun.star.beans.NamedValue[] nv = new com.sun.star.beans.NamedValue[hm.size()];
     Object[] keys = hm.keySet().toArray();
     for (int i = 0; i < hm.size(); i++) {nv[i] = new com.sun.star.beans.NamedValue((String) keys[i], hm.get(keys[i]));}return nv;}
     */

    // SQL für Gerätepaket: (Zuluft + Abluft)
    // MaxVolumenstrom >= 'Volumenstrom des Zentralgeräts'
    Integer selectMaxVolumenstrom(String zentralgerat, Integer volumenstrom)
    {
        def statement = "SELECT MaxVolumenstrom FROM pakete " +
                        " WHERE Geraet = ? " +
                        " AND MaxVolumenstrom >= ? " +
                        " AND Kategorie = 72 " +
                        " ORDER BY MaxVolumenstrom asc;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, volumenstrom])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectMaxVolumenstrom: ${r?.dump()}"
		r
    }

    def selectZentralgeratZuluft(String zentralgerat, Integer maxvolumenstrom) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 72 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.MaxVolumenstrom >= ? " +
                        " AND pakete.Bedingung = 'ZU' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, maxvolumenstrom])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectZentralgeratZuluft: ${r?.dump()}"
		r
    }

    def selectZentralgeratAbluft(String zentralgerat, Integer maxvolumenstrom) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 72 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.MaxVolumenstrom >= ? " +
                        " AND pakete.Bedingung = 'AB' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, maxvolumenstrom])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectZentralgeratAbluft: ${r?.dump()}"
		r
    }



    //SQL für AU-FO-Paket: (Außenluft und Fortluft)
    def selectAussenluftPaket(String zentralgerat, String paketeBedingung) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 70 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.Bedingung = ? " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, paketeBedingung])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectAussenluftPaket: ${r?.dump()}"
		r
    }

    def selectFortluftPaket(String zentralgerat, String paketeBedingung) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 71 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.Bedingung = ? " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, paketeBedingung])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectFortluftPaket: ${r?.dump()}"
		r
    }


    //SQL für Grundpaket: (Zuluft + Abluft)
    def selectGrundpaketZuluft(String zentralgerat, Integer maxvolumenstrom) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 73 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.MaxVolumenstrom = ? " +
                        " AND pakete.Bedingung = 'ZU' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, maxvolumenstrom])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectGrundpaketZuluft: ${r?.dump()}"
		r
    }

    def selectGrundpaketAbluft(String zentralgerat, Integer maxvolumenstrom) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 73 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.MaxVolumenstrom = ? " +
                        " AND pakete.Bedingung = 'AB' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, maxvolumenstrom])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectGrundpaketAbluft: ${r?.dump()}"
		r
    }


    //SQL und Logik für Erweiterungspaket
    List selectErweiterungspaketZuluft() {
        def statement = "SELECT MaxVolumenstrom " +
                        " FROM pakete " +
                        " WHERE Kategorie = 75 " +
                        " AND MaxVolumenstrom >= 'Gesamt Zuluftvolumenstrom' " +
                        " ORDER BY MaxVolumenstrom asc;"
    }
    
    List selectErweiterungspaketAbluft() {
        def statement = "SELECT MaxVolumenstrom " +
                        " FROM pakete " +
                        " WHERE Kategorie = 75 " +
                        " AND MaxVolumenstrom >= 'Gesamt Abluftvolumenstrom' " +
                        " ORDER BY MaxVolumenstrom asc;"
    }


    //Für Zuluft:
    // maxvolumentstrom = 'Gesamt Zuluftvolumenstrom'
    List selectErweiterungspaketZuluftProGeschoss(String zentralgerat, Integer maxvolumenstrom) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 74 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.MaxVolumenstrom >= ? " +
                        " AND pakete.Bedingung = 'ZU' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, maxvolumenstrom])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectErweiterungspaketZuluftProGeschoss: ${r?.dump()}"
		r
    }

    //Für Abluft:
    // maxvolumentstrom = 'Gesamt Abluftvolumenstrom'
    List selectErweiterungspaketAbluftProGeschoss(String zentralgerat, Integer maxvolumenstrom) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 74 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.MaxVolumenstrom >= ? " +
                        " AND pakete.Bedingung = 'AB' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, maxvolumenstrom])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectErweiterungspaketAbluftProGeschoss: ${r?.dump()}"
		r
    }

    //SQL für Verteilpaket
    //1. Der Array für das Erweiterungspaket wird auch hier benötigt.
    //2. Pro Geschoss, in dem die Anzahl Zuluftventile (bzw. Abluftventile) größer Null ist, wird folgende SQL-Abfrage gestartet:
    //Für Zuluft:
    List selectVerteilpaketZuluft(Integer anzahlZuluftventile) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 75 " +
                        " AND pakete.Geraet = '' " +
                        " AND pakete.MaxVolumenstrom >= 0 " +
                        " AND pakete.Bedingung >= 'Anzahl Zuluftventile' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[anzahlZuluftventile])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectVerteilpaketZuluft: ${r?.dump()}"
		r
    }

    //Für Abluft:
    List selectVerteilpaketAbluft(Integer anzahlAbluftventile) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 75 " +
                        " AND pakete.Geraet = '' " +
                        " AND pakete.MaxVolumenstrom >= 0 " +
                        " AND pakete.Bedingung >= 'Anzahl Abluftventile' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[anzahlAbluftventile])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectVerteilpaketAbluft: ${r?.dump()}"
		r
    }

    //SQL für Luftdurchlass
    //1. In einer Map müssen alle Luftventile aus den „Raumvolumenströme / Zu- Abluftventile“ erfasst und die „Gesamtanzahl“ ermittelt werden.
    //   Die Luftventile sind die „Bezeichnung Luftventil“.
    def selectLuftdurchlass(String zentralgerat) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 76 " +
                        " AND pakete.Geraet = ? " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectVerteilpaketAbluft: ${r?.dump()}"
		r
    }

}

/**
 * /Users/rbe/project/wac2/griffon-app/services/com/westaflex/wac/OOoService.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

/**
 * 
 */
class OooService {
	
	/**
	 * OpenOffice connection manager.
	 */
	private def ocm =
		new com.bensmann.odisee.server.OOoConnectionManager(
			oooOptions: ["-nologo", "-nofirststartwizard", "-nodefault", "-nocrashreport", "-norestart", "-norestore", "-nolockcheck"],
			group1: ["pipe1"]
		)
	
	/**
	 * OpenOffice template.
	 */
	def westaAuslegungTemplate
	
	/**
	 * Constructor.
	 */
	def OooService() {
		// Get template and temporary file
		def stream = Wac2Resource.getOOoAsStream("WestaAuslegung")
		westaAuslegungTemplate = java.io.File.createTempFile("wacauslegung", ".ott")
		westaAuslegungTemplate.deleteOnExit()
		// Copy stream from jar into temporary file
		westaAuslegungTemplate.newOutputStream().write(stream.bytes)
		// Close stream
		stream.close()
	}
	
	/**
	 * Shutdown OOo connection manager.
	 */
	def shutdown = {
		ocm.shutdown(true)
	}
	
	/**
	 * 
	 */
	def performAngebot = { blanko = false, title = null ->
		// Which macro to call?
		def macro = blanko ? "Westa.Main.silentMain" : "Westa.Main.silentMode"
		// Get connection to OpenOffice
		def oooConnection = ocm.acquire("group1")
		if (!oooConnection) throw new IllegalStateException("No connection to OpenOffice")
		// Process template
		def file
		use (com.bensmann.odisee.category.OOoDocumentCategory) {
			// Create new document from template
			def doc = westaAuslegungTemplate.open(oooConnection, [Hidden: Boolean.FALSE])
			// Execute macro
			doc.executeMacro("${macro}?language=Basic&location=application", [])
			// Save and close
			file = java.io.File.createTempFile((title ?: "WAC_Auslegung") as String, ".odt")
			doc.saveAs(file)
			doc.close()
		}
		// Return file reference to generated document
		file
	}
	
	/**
	 * 
	 */
	def performAuslegung = { blanko = false, title = null, map ->
		// Which macro to call?
		def macro = blanko ? "Westa.Main.silentMain" : "Westa.Main.silentMode"
		// Get connection to OpenOffice
		def oooConnection = ocm.acquire("group1")
		if (!oooConnection) throw new IllegalStateException("No connection to OpenOffice")
		// Process template
		def file
		use (com.bensmann.odisee.category.OOoDocumentCategory) {
			// Create new document from template
			def doc = westaAuslegungTemplate.open(oooConnection, [Hidden: Boolean.FALSE])
			// Daten übergeben
			addProjektdaten(doc, map.kundendaten)
			addKundendaten(doc, map.kundendaten)
			addInformationen(doc, map)
			// Save and close
			file = java.io.File.createTempFile((title ?: "WAC_Auslegung") as String, ".odt")
			doc.saveAs(file)
			doc.close()
		}
		// Return file reference to generated document
		file
	}
	
	/**
	 * Return key of map.key as String else an empty one:
	 * key=Decke -> map[decke] set, then "Decke" else ""
	 * key= -> map[decke] set, then "Decke" else ""
	 */
	private def gt = { map, key, value ->
		def val = map[key]
		val ? value as String : ""
	}
	
	/**
	 * @param map model.map.kundendaten
	 */
	private def addProjektdaten = { doc, map ->
		use (com.bensmann.odisee.category.OOoFieldCategory) {
			// Projektdaten
			doc["adBauvorhabenTextField"] = map.bauvorhaben
		}
	}
	
	/**
	 * @param map model.map.kundendaten
	 */
	private def addKundendaten = { doc, map ->
		use (com.bensmann.odisee.category.OOoFieldCategory) {
			// Grosshandel
			doc["ghFirma1TextField"]          = map.grosshandel.firma1
			doc["ghFirma2TextField"]          = map.grosshandel.firma2
			doc["ghStrasseTextField"]         = map.grosshandel.strasse
			doc["ghPlzOrtTextField"]          = "${map.grosshandel.plz} ${map.grosshandel.ort}"
			doc["ghTelefonTextField"]         = map.grosshandel.telefon
			doc["ghFaxTextField"]             = map.grosshandel.telefax
			doc["ghAnsprechpartnerTextField"] = map.grosshandel.ansprechpartner
			// Ausführende Firma
			doc["afFirma1TextField"]          = map.ausfuhrendeFirma.firma1
			doc["afFirma2TextField"]          = map.ausfuhrendeFirma.firma2
			doc["afStrasseTextField"]         = map.ausfuhrendeFirma.strasse
			doc["afPlzOrtTextField"]          = "${map.ausfuhrendeFirma.plz} ${map.ausfuhrendeFirma.ort}"
			doc["afTelefonTextField"]         = map.ausfuhrendeFirma.telefon
			doc["afFaxTextField"]             = map.ausfuhrendeFirma.telefax
			doc["afAnsprechpartnerTextField"] = map.ausfuhrendeFirma.ansprechpartner
		}
	}
	
	/**
	 * @param map model.map.kundendaten
	 */
	private def addInformationen = { doc, map ->
		use (com.bensmann.odisee.category.OOoFieldCategory) {
			// Gerätestandort
			doc["gsKellergeschossRadioButton"]     = gt(map.anlage.standort, "KG", "Kellergeschoss")
			doc["gsErdgeschossRadioButton"]        = gt(map.anlage.standort, "EG", "Erdgeschoss")
			doc["gsObergeschossRadioButton"]       = gt(map.anlage.standort, "OG", "Obergeschoss")
			doc["gsDachgeschossRadioButton"]       = gt(map.anlage.standort, "DG", "Dachgeschoss")
			doc["gsSpitzbodenRadioButton"]         = gt(map.anlage.standort, "SB", "Spitzboden")
			// Luftkanalverlegung
			doc["lkAufputzCheckbox"]               = gt(map.anlage.luftkanalverlegung, "aufputz", "Aufputz (Abkastung)")
			doc["lkDaemmschichtCheckbox"]          = gt(map.anlage.luftkanalverlegung, "dammschicht", "Dämmschicht unter Estrich")
			doc["lkDeckeCheckbox"]                 = gt(map.anlage.luftkanalverlegung, "decke", "Decke (abgehängt)")
			doc["lkSpitzbodenCheckbox"]            = gt(map.anlage.luftkanalverlegung, "spitzboden", "Spitzboden")
			// Geplante Belegung
			doc["personenAnzahlSpinner"]           = map.gebaude.geplanteBelegung.personenAnzahl as Integer
			// Außenluft
			doc["rbAlDachdurchfuehrung"]           = gt(map.anlage.aussenluft, "dach", "Dachdurchführung")
			doc["rbAlWand"]                        = gt(map.anlage.aussenluft, "wand", "Wand (Luftgitter)")
			doc["rbAlErdwaermetauscher"]           = gt(map.anlage.aussenluft, "erdwarme", "Erdwärmetauscher")
			// Fortluft
			doc["flDachdurchfuehrungRadioButton"]  = gt(map.anlage.fortluft, "dach", "Dachdurchführung")
			doc["flWandRadioButton"]               = gt(map.anlage.fortluft, "wand", "Wand (Luftgitter)")
			doc["flLichtschachtRadioButton"]       = gt(map.anlage.fortluft, "lichtschacht", "Lichtschacht (Kellergeschoss)")
			// Luftauslässe
			doc["laTellerventileCheckbox"]         = gt(map.anlage.abluft, "tellerventile", "Tellerventile")
			// Lufteinlässe
			doc["lzTellerventileCheckbox"]         = gt(map.anlage.zuluft, "tellerventile", "Tellerventile")
			doc["lzSchlitzauslassCheckbox"]        = gt(map.anlage.zuluft, "schlitzauslass", "Schlitzauslass (Weitwurfdüse)")
			doc["lzFussbodenauslassCheckbox"]      = gt(map.anlage.zuluft, "fussboden", "Fußbodenauslass")
			doc["lzSockelquellauslassCheckbox"]    = gt(map.anlage.zuluft, "sockel", "Sockelquellauslass")
			// Gebäudetyp
			doc["gtMFHRadioButton"]                = gt(map.gebaude.typ, "mfh", "Mehrfamilienhaus")
			doc["gtEFHRadioButton"]                = gt(map.gebaude.typ, "efh", "Einfamilienhaus")
			doc["gtMaisonetteRadioButton"]         = gt(map.gebaude.typ, "maisonette", "Maisonette")
			// Gebäudelage
			doc["glWschwachRadioButton"]           = gt(map.gebaude.lage, "windschwach", "windschwach")
			doc["glWstarkRadioButton"]             = gt(map.gebaude.lage, "windstark", "windstark")
			// Wärmeschutz
			doc["wsHochRadioButton"]               = gt(map.gebaude.warmeschutz, "hoch", "hoch (Neubau / Sanierung mind. WSchV 1995)")
			doc["wsNiedrigRadioButton"]            = gt(map.gebaude.warmeschutz, "niedrig", "niedrig (Gebäude bestand vor 1995)")
			// Luftdichtheit
			doc["ldKatARadioButton"]               = gt(map.gebaude.luftdichtheit, "kategorieA", "Kategorie A")
			doc["ldKatBRadioButton"]               = gt(map.gebaude.luftdichtheit, "kategorieB", "Kategorie B")
			doc["ldKatCRadioButton"]               = gt(map.gebaude.luftdichtheit, "kategorieC", "Kategorie C")
		}
	}
	
	/**
	 * 
	public Object[] collectTransferData() {
		Object[] ret = new Object[4];
		Object[][] controls = new Object[][]{
			{"Bauvorhaben", adBauvorhabenTextField},
			{"Firma1", afFirma1TextField},
			{"Firma2", afFirma2TextField},
			{"Strasse", afStrasseTextField},
			{"PlzOrt", afPlzOrtTextField},
			{"Telefon", afTelefonTextField},
			{"Telefax", afFaxTextField},
			{"Ansprechpartner", afAnsprechpartnerTextField},
			{"Volumenstrom", lmeVolumenstromCombobox},
			{"Zentralgeraet", lmeZentralgeraetCombobox},
			{"Aussenluft", aussenluftButtonGroup},
			{"Fortluft", fortluftButtonGroup},
			{"Geraetestandort", geraetestandortButtonGroup}
		};//{"Ueberstroemelement", lmeTabelleTable.getValueAt(row, column)},
		final int UEBERSTROEM = 5;
		final int VENTILBEZEICHNUNG = 9;
		final int VERTEILEBENE = 10;
		com.sun.star.beans.NamedValue[] first = new com.sun.star.beans.NamedValue[controls.length + 1];
		for (int i = 0; i < controls.length; i++) {
			first[i] = new com.sun.star.beans.NamedValue((String) controls[i][0], ((DocumentAware) controls[i][1]).getString());
		}
		// Hier kommt die Grafik hin!
		Description desc = new Description();
		int lmeRowCount = lmeTabelleTable.getRowCount();
		for (int i = 0; i < lmeRowCount; i++) {
			String luftart = (String) lmeTabelleTable.getValueAt(i, 1); // Luftart
			String verteilebene = (String) lmeTabelleTable.getValueAt(i, 10); // Verteilebene
			String ventil = null;
			String raumname = null;
			if (luftart.equals("AB")) {
				ventil = (String) lmeTabelleTable.getValueAt(i, 9);
				raumname = (String) lmeTabelleTable.getValueAt(i, 0) + " / " + ventil;
				desc.addConnector(luftart, verteilebene, raumname);
			} else if (luftart.equals("ZU")){
				ventil = (String) lmeTabelleTable.getValueAt(i, 8);
				raumname = (String) lmeTabelleTable.getValueAt(i, 0) + " / " + ventil;
				desc.addConnector(luftart, verteilebene, raumname);
			} else if (luftart.equals("ZU/AB")) {
				// AB
				ventil = (String) lmeTabelleTable.getValueAt(i, 9);
				raumname = (String) lmeTabelleTable.getValueAt(i, 0) + " / " + ventil;
				desc.addConnector("AB", verteilebene, raumname);
				// ZU
				ventil = (String) lmeTabelleTable.getValueAt(i, 8);
				raumname = (String) lmeTabelleTable.getValueAt(i, 0) + " / " + ventil;
				desc.addConnector("ZU", verteilebene, raumname);
			}
		}
		//
		WestaDB westaDb = WestaDB.getInstance();
		ResultSet rs;
		//
		// 4. Außenluft
		//
		rs = westaDb.queryDB("select ~stueckliste~.~Artikel~, ~stueckliste~.~Anzahl~ from ~stueckliste~, ~pakete~ where ~stueckliste~.~Paket~ = ~pakete~.~ID~ and ~pakete~.~Kategorie~= 70 and ~pakete~.~Geraet~= '" + lmeZentralgeraetCombobox.getString() + "' and ~pakete~.~Bedingung~ = '" + aussenluftButtonGroup.getValue().toString() + "' and ~stueckliste~.~Reihenfolge~ = 10");
		try {
			rs.next();
			desc.putName("aussenluft", rs.getString(1));
		} catch (SQLException ex) {
			Logger.getLogger(ProjectInternalFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
		//
		// 5. Fortluft
		//
		rs = westaDb.queryDB("select ~stueckliste~.~Artikel~, ~stueckliste~.~Anzahl~ from ~stueckliste~, ~pakete~ where ~stueckliste~.~Paket~ = ~pakete~.~ID~ and ~pakete~.~Kategorie~= 71 and ~pakete~.~Geraet~= '" + lmeZentralgeraetCombobox.getString() + "' and ~pakete~.~Bedingung~ = '" + fortluftButtonGroup.getValue().toString() + "' and ~stueckliste~.~Reihenfolge~ = 10");
		try {
			rs.next();
			desc.putName("fortluft", rs.getString(1));
		} catch (SQLException ex) {
			Logger.getLogger(ProjectInternalFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
		desc.putName("zentralgeraet", lmeZentralgeraetCombobox.getString());
		try {
			first[first.length - 1] = new com.sun.star.beans.NamedValue("grafik", desc.drawText().getAbsolutePath());
		} catch (Exception e) {
		}
		// Beim Volumenstrom die Kommata entfernen
		first[8].Value = ((String) first[8].Value).replaceAll(",", ".");
		//
		ret[0] = first;
		ret[1] = makeNamedValuesFromTable(lmeTabelleTable, VERTEILEBENE);
		ret[2] = makeNamedValuesFromTable(lmeTabelleTable, VENTILBEZEICHNUNG);
		ret[3] = makeNamedValuesFromTable(lmeTabelleUeberstroemTable, UEBERSTROEM);
		return ret;
	}
	
	com.sun.star.beans.NamedValue[] makeNamedValuesFromTable(SeeTable table, int index) {
		final int LUFTART = 1, ANZAHLABLUFT = 4, ANZAHLZULUFT = 7, LUFTMENGE = 6;
		HashMap hm = new HashMap();
		String itemName = "";
		String itemName2 = "";
		String keyString[] = null;
		float itemCount = 0;
		float itemCountZU = 0;
		float luftMenge = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			if (((String) lmeTabelleTable.getValueAt(i, LUFTART)).contains("ZU") && index == 9) {
				itemName2 = (String) table.getValueAt(i, index - 1);
			} else {
				if (index == 5) {
					itemName2 = "";
				} else {
					itemName2 = (String) table.getValueAt(i, index);
				}
			}
			//System.out.println(String.format("i = %d;index =%d", i, index));
			// Nur für Abluft
			itemName = (String) table.getValueAt(i, index);
			if ((itemName != null) && (!itemName.isEmpty()) && index != 5
					&& ((String) lmeTabelleTable.getValueAt(i, LUFTART)).contains("AB")) {
				itemCount = JTableUtil.parseFloatFromTableCell(table, i, ANZAHLABLUFT);
				luftMenge = JTableUtil.parseFloatFromTableCell(table, i, LUFTMENGE);
				itemName += "AB";
				// itemName ist die Zusammensetzung aus Luftart und Verteilebene
				if (hm.containsKey(itemName)) {
					keyString = ((String) hm.get(itemName)).split(";");
					itemCount = itemCount + Float.parseFloat(keyString[0]);
					luftMenge = luftMenge + Float.parseFloat(keyString[1]);
				}
				hm.put(itemName, itemCount + ";" + luftMenge);
			}
			// Nur für Zuluft
			if ((itemName2 != null) && (!itemName2.isEmpty())
					&& ((String) lmeTabelleTable.getValueAt(i, LUFTART)).contains("ZU")) {
				itemCountZU = JTableUtil.parseFloatFromTableCell(table, i, ANZAHLZULUFT);
				luftMenge = JTableUtil.parseFloatFromTableCell(table, i, LUFTMENGE);
				itemName2 += "ZU";
				if (hm.containsKey(itemName2)) {
					keyString = ((String) hm.get(itemName2)).split(";");
					itemCountZU = itemCountZU + Float.parseFloat(keyString[0]);
					luftMenge = luftMenge + Float.parseFloat(keyString[1]);
				}
				hm.put(itemName2, itemCountZU + ";" + luftMenge);
			}
			// Nur für Überströmelemente
			if ((itemName != null) && (!itemName.isEmpty()) && index == 5) {
				itemCount = JTableUtil.parseFloatFromTableCell(table, i, 3);
				if (hm.containsKey(itemName)) {
					keyString = ((String) hm.get(itemName)).split(";");
					itemCount = itemCount + Float.parseFloat(keyString[0]);
					luftMenge = luftMenge + Float.parseFloat(keyString[1]);
				}
				hm.put(itemName, itemCount + ";" + luftMenge);
			}
		}
		com.sun.star.beans.NamedValue[] nv = new com.sun.star.beans.NamedValue[hm.size()];
		Object[] keys = hm.keySet().toArray();
		for (int i = 0; i < hm.size(); i++) {
			nv[i] = new com.sun.star.beans.NamedValue((String) keys[i], hm.get(keys[i]));
		}
		return nv;
	}
	 */
	
}
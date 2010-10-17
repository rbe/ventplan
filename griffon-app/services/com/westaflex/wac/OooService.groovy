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

import com.bensmann.griffon.GriffonHelper as GH

/**
 * 
 */
class OooService {
	
	/**
	 * OpenOffice connection manager.
	 */
	private def ocm
	
	/**
	 * OpenOffice template.
	 */
	private def westaAuslegungTemplate
	
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
	 * Setup OOo connection manager.
	 */
	def setupOCM = {
		if (!ocm) ocm = new com.bensmann.odisee.server.OOoConnectionManager(
				oooOptions: [
						"-headless", "-nologo",
						"-nofirststartwizard", "-nodefault",
						"-nocrashreport", "-norestart", "-norestore",
						"-nolockcheck"
					],
				group1: ["wac2-${new java.util.Date().format("yyyyMMddHHmmss")}"]
			)
	}
	
	/**
	 * Shutdown OOo connection manager.
	 */
	def shutdownOCM = {
		ocm.shutdown(true)
	}
	
	/**
	 * 
	 */
	def performAngebot = { blanko = false, title = null ->
		try {
			// Setup OCM
			setupOCM()
			// Get connection to OpenOffice
			def oooConnection = ocm.acquire("group1")
			// Which macro to call?
			def macro = blanko ? "Westa.Main.silentMain" : "Westa.Main.silentMode"
			// Get connection to OpenOffice
			oooConnection = ocm.acquire("group1")
			if (!oooConnection) throw new IllegalStateException("No connection to OpenOffice")
			// Process template
			def file
			use (com.bensmann.odisee.category.OOoDocumentCategory) {
				// Create new document from template
				def doc = westaAuslegungTemplate.open(oooConnection, [Hidden: Boolean.FALSE])
				// Execute macro
				/*
				def aVerteilung
				map.raum.raume.collect { it ->
					def k = "${it.raumVerteilebene}${it.raumLuftart}"
					def v = "${map.raum.raume.inject(0, { o, n -> o + n.raumAnzahlAbluftventile })};${map.raum.raume.inject(0, { o, n -> o + n.raumAbluftmengeJeVentil })}"
					new NamedValue(k, v)
				}
				*/
				// Basic Parameter: aRumpf (Basisdaten, Kunde etc.), aVerteilung, aVentile, aUberstroemElemente
				doc.executeMacro("${macro}?language=Basic&location=application", [])
				// Save generated document
				file = java.io.File.createTempFile((title ?: "WAC_Auslegung") as String, ".odt")
				doc.saveAs(file)
				// Close it
				doc.close()
			}
			// Return file reference to generated document
			file
		} finally {
			// Shutdown
			shutdownOCM()
		}
	}
	
	/**
	 * 
	 */
	def performAuslegung = { blanko = false, title = null, map ->
		try {
			// Setup OCM
			setupOCM()
			// Get connection to OpenOffice
			def oooConnection = ocm.acquire("group1")
			if (!oooConnection) throw new IllegalStateException("No connection to OpenOffice")
			// Process template
			def file
			use (com.bensmann.odisee.category.OOoDocumentCategory) {
				// Create new document from template
				def doc = westaAuslegungTemplate.open(oooConnection, [Hidden: Boolean.TRUE])
				// Daten übergeben
				println "projektdaten"
				addProjektdaten(doc, map.kundendaten)
				println "kundendaten"
				addKundendaten(doc, map.kundendaten)
				println "informationen"
				addInformationen(doc, map)
				println "raumdaten"
				addRaumdaten(doc, map)
				println "rauvolumenströme"
				addRaumvolumenstrome(doc, map)
				println "überströmelement"
				addUberstromelemente(doc, map)
				println "akustik"
				addAkustikBerechnung(doc, map)
				println "dvbkanalnetz"
				addDvbKanalnetz(doc, map)
				println "dvbventileinstellung"
				addDvbVentileinstellung(doc, map)
				// Save generated document
				file = java.io.File.createTempFile((title ?: "WAC_Auslegung") as String, ".odt")
				doc.saveAs(file)
				// Close it
				doc.close()
			}
			// Return file reference to generated document
			file
		} finally {
			// Shutdown
			shutdownOCM()
		}
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
		// Felder
		use (com.bensmann.odisee.category.OOoFieldCategory) {
			// Projektdaten
			doc["adBauvorhabenTextField"] = map.bauvorhaben
		}
	}
	
	/**
	 * @param map model.map.kundendaten
	 */
	private def addKundendaten = { doc, map ->
		// Felder
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
		// Felder
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
			doc["personenAnzahlSpinner"]           = map.gebaude.geplanteBelegung.personenanzahl as String
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
			doc["ldMessRadioButton"]               = gt(map.gebaude.luftdichtheit, "messwerte", "Messwerte")
		}
	}
	
	/**
	 * @param map model.map
	 */
	def addRaumdaten = { doc, map ->
		// Tabelle
		use (com.bensmann.odisee.category.OOoTextTableCategory) {
		}
		// Felder
		use (com.bensmann.odisee.category.OOoFieldCategory) {
			// Zusammenfassung
			doc["lmeZuSummeWertLabel"]             = GH.toString2Converter(map.raum.raume.findAll { it.raumLuftart == "ZU" }?.inject(0.0d, { o, n -> o + n.raumVolumen }) ?: 0.0d)
			doc["lmeAbSummeWertLabel"]             = GH.toString2Converter(map.raum.raume.findAll { it.raumLuftart == "AB" }?.inject(0.0d, { o, n -> o + n.raumVolumen }) ?: 0.0d)
			doc["lmeUebSummeWertLabel"]            = GH.toString2Converter(map.raum.raume.findAll { it.raumLuftart == "ÜB" }?.inject(0.0d, { o, n -> o + n.raumVolumen }) ?: 0.0d)
			doc["lmeGesamtvolumenWertLabel"]       = GH.toString2Converter(map.raum.raumVs.gesamtVolumenNE)
			doc["lmeGebaeudeluftwechselWertLabel"] = GH.toString2Converter(map.raum.raumVs.luftwechselNE)
			doc["kzKennzeichenLabel"]              = map.anlage.kennzeichnungLuftungsanlage
			// Bemerkungen
			doc["adNotizenTextArea"]               = map.kundendaten.notizen
		}
	}
	
	/**
	 * @param map model.map
	 */
	def addRaumvolumenstrome = { doc, map ->
		// Tabelle
		use (com.bensmann.odisee.category.OOoTextTableCategory) {
		}
		// Felder
		use (com.bensmann.odisee.category.OOoFieldCategory) {
			// Ergebnis der Berechnungen
			doc["lmeSumLTMZuluftmengeWertLabel"]   = GH.toString2Converter(map.raum.raume.findAll { it.raumLuftart == "ZU" }?.inject(0.0d, { o, n -> o + n.raumZuluftVolumenstrom }) ?: 0.0d)
			doc["lmeSumLTMAbluftmengeWertLabel"]   = GH.toString2Converter(map.raum.raume.findAll { it.raumLuftart == "AB" }?.inject(0.0d, { o, n -> o + n.raumAbluftVolumenstrom }) ?: 0.0d)
			doc["lmeGesAussenluftmengeWertLabel"]  = GH.toString2Converter(map.raum.raumVs.gesamtaussenluftVsMitInfiltration)
		}
	}
	
	/**
	 * @param map model.map
	 */
	def addUberstromelemente = { doc, map ->
		// Tabelle
		use (com.bensmann.odisee.category.OOoTextTableCategory) {
		}
		// Felder
		use (com.bensmann.odisee.category.OOoFieldCategory) {
			// Einstellungen am Lüftungsgerät und an der Fernbedienung
			doc["lmeZentralgeraetCombobox"]     = map.anlage.zentralgerat
			doc["lmeFeuchteschutzWertLabel"]    = GH.toString2Round5Converter(map.aussenluftVs.raumVsAussenluftVsDerLtmFs)
			doc["lmeMindestluftungWertLabel"]   = GH.toString2Round5Converter(map.aussenluftVs.raumVsAussenluftVsDerLtmRl)
			doc["lmeGrundlueftungWertLabel"]    = GH.toString2Round5Converter(map.aussenluftVs.raumVsAussenluftVsDerLtmNl)
			doc["lmeIntensivlueftungWertLabel"] = GH.toString2Round5Converter(map.aussenluftVs.raumVsAussenluftVsDerLtmIl)
		}
	}
	
	/**
	 * @param map model.map
	 */
	def addAkustikBerechnung = { doc, map ->
		// Tabelle
		use (com.bensmann.odisee.category.OOoTextTableCategory) {
		}
		// Felder
		use (com.bensmann.odisee.category.OOoFieldCategory) {
			// Zuluft
			//abZuTabelleUberschrift2Label = "Zuluft"
			doc["abZuRaumbezeichnungComboBox"]                    = map.akustik.zuluft.raumBezeichnung
			doc["abZuSchallleistungspegelZuluftstutzenComboBox"]  = map.akustik.zuluft.zentralgerat
			doc["abZuKanalnetzComboBox"]                          = map.akustik.zuluft.slpErhohungKanalnetz as String
			doc["abZuFilterverschmutzungComboBox"]                = map.akustik.zuluft.slpErhohungFilter as String
			doc["abZuHauptschalldaempfer1ComboBox"]               = map.akustik.zuluft.hauptschalldampfer1
			doc["abZuHauptschalldaempfer2ComboBox"]               = map.akustik.zuluft.hauptschalldampfer2
			doc["abZuAnzahlUmlenkungenTextField"]                 = GH.toString2Converter(map.akustik.zuluft.anzahlUmlenkungen)
			doc["abZuLuftverteilerkastenTextField"]               = map.akustik.zuluft.luftverteilerkasten
			doc["abZuLaengsdaempfungKanalComboBox"]               = map.akustik.zuluft.langsdampfungKanal
			doc["abZuLaengsdaempfungKanalTextField"]              = GH.toString2Converter(map.akustik.zuluft.langsdampfungKanalLfdmMeter)
			doc["abZuSchalldaempferVentilComboBox"]               = map.akustik.zuluft.schalldampferVentil
			doc["abZuEinfuegungswertLuftdurchlassComboBox"]       = map.akustik.zuluft.einfugungsdammwert
			doc["abZuTabelleDezibelWertLabel"]                    = GH.toString2Converter(map.akustik.zuluft.dbA)
			doc["abZuTabelleMittlererSchalddruckpegelWertLabel"]  = GH.toString2Converter(map.akustik.zuluft.mittlererSchalldruckpegel)
			// Abluft
			//abAbTabelleUberschrift2Label = "Abluft"
			doc["abAbRaumbezeichnungComboBox"]                    = map.akustik.abluft.raumBezeichnung
			doc["abAbSchallleistungspegelAbluftstutzenComboBox"]  = map.akustik.abluft.zentralgerat
			doc["abAbKanalnetzComboBox"]                          = map.akustik.abluft.slpErhohungKanalnetz as String
			doc["abAbFilterverschmutzungComboBox"]                = map.akustik.abluft.slpErhohungFilter as String
			doc["abAbHauptschalldaempfer1ComboBox"]               = map.akustik.abluft.hauptschalldampfer1
			doc["abAbHauptschalldaempfer2ComboBox"]               = map.akustik.abluft.hauptschalldampfer2
			doc["abAbAnzahlUmlenkungenTextField"]                 = map.akustik.abluft.anzahlUmlenkungen as String
			doc["abAbLuftverteilerkastenTextField"]               = map.akustik.abluft.luftverteilerkasten
			doc["abAbLaengsdaempfungKanalComboBox"]               = map.akustik.abluft.langsdampfungKanal
			doc["abAbLaengsdaempfungKanalTextField"]              = GH.toString2Converter(map.akustik.abluft.langsdampfungKanalLfdmMeter)
			doc["abAbSchalldaempferVentilComboBox"]               = map.akustik.abluft.schalldampferVentil
			doc["abAbEinfuegungswertLuftdurchlassComboBox"]       = GH.toString2Converter(map.akustik.abluft.einfugungsdammwert)
			doc["abAbTabelleDezibelWertLabel"]                    = map.akustik.abluft.dbA
			doc["abAbTabelleMittlererSchalddruckpegelWertLabel"]  = GH.toString2Converter(map.akustik.abluft.mittlererSchalldruckpegel)
		}
	}
	
	/**
	 * @param map model.map
	 */
	def addDvbKanalnetz = { doc, map ->
		// Tabelle
		use (com.bensmann.odisee.category.OOoTextTableCategory) {
		}
	}
	
	/**
	 * @param map model.map
	 */
	def addDvbVentileinstellung = { doc, map ->
		// Tabelle
		use (com.bensmann.odisee.category.OOoTextTableCategory) {
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
		final int UEBERSTROEM = 5;
		final int VENTILBEZEICHNUNG = 9;
		final int VERTEILEBENE = 10;
		ret[0] = first;
		ret[1] = makeNamedValuesFromTable(lmeTabelleTable, VERTEILEBENE); // 10
		ret[2] = makeNamedValuesFromTable(lmeTabelleTable, VENTILBEZEICHNUNG); // 9
		ret[3] = makeNamedValuesFromTable(lmeTabelleUeberstroemTable, UEBERSTROEM); // 5
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

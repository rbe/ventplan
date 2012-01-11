/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/services/com/westaflex/wac/OooService.groovy
 * Last modified at 28.03.2011 14:43:30 by rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

/**
 * 
 */
class OooService {
    
    public static boolean DEBUG = false
    
    /**
     * Constructor.
     */
    def OooService() {
    }
    
    /**
     * 
    def performAngebot(blanko = false, title = null) {
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
                //def aVerteilung
                //map.raum.raume.collect { it ->
                //    def k = "${it.raumVerteilebene}${it.raumLuftart}"
                //    def v = "${map.raum.raume.inject(0, { o, n -> o + n.raumAnzahlAbluftventile })};${map.raum.raume.inject(0, { o, n -> o + n.raumAbluftmengeJeVentil })}"
                //    new NamedValue(k, v)
                //}
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
     */
    
    /**
     * 
     */
    def noZero = { val ->
        if (val == '0,00' || val == '0') val = ''
        val
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
     * @param wpxAuslegungFilename Filename of WPX w/o extension.
     */
    String performAuslegung(File wpxFile, Map map, boolean saveOdiseeXml = false) {
        // Filename w/o extension
        def wpxFilenameWoExt = wpxFile.name - '.wpx'
        // Generate Odisee XML
        def domBuilder = groovy.xml.DOMBuilder.newInstance()
        def odisee = domBuilder.odisee() {
            request(name: wpxFilenameWoExt, id: 1) {
                ooo(group: 'group0') {}
                template(name: 'WestaAuslegung', revision: 'LATEST', outputFormat: 'pdf') {}
                archive(database: false, files: true) {}
                instructions() {
                    addErsteller(domBuilder)
                    addProjektdaten(domBuilder, map.kundendaten)
                    addKundendaten(domBuilder, map.kundendaten)
                    addInformationen(domBuilder, map)
                    addRaumdaten(domBuilder, map)
                    addRaumvolumenstrome(domBuilder, map)
                    addUberstromelemente(domBuilder, map)
                    addAkustikBerechnung(domBuilder, map)
                    addDvbKanalnetz(domBuilder, map)
                    addDvbVentileinstellung(domBuilder, map)
                }
            }
        }
        // Convert XML to string (StreamingMarkupBuilder will generate XML with correct german umlauts)
        String xml = new groovy.xml.StreamingMarkupBuilder().bind {
                mkp.yieldUnescaped odisee
            }.toString()
        // Save Odisee request XML
        if (saveOdiseeXml) {
            def odiseeXmlFile = new File(wpxFile.parentFile, "${wpxFilenameWoExt}_odisee.xml")
            odiseeXmlFile.withWriter("UTF-8") { writer ->
                writer.write(xml)
            }
        }
        // Return Odisee XML
        xml
    }
    
    /**
     * 
     */
    private def addErsteller(domBuilder) {
        def prefHelper = AuslegungPrefHelper.getInstance()
        domBuilder.userfield(name: 'erstellerFirma', prefHelper.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_FIRMA))
        domBuilder.userfield(name: 'erstellerName', prefHelper.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_NAME))
        domBuilder.userfield(name: 'erstellerStrasse', prefHelper.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_STRASSE))
        domBuilder.userfield(name: 'erstellerPLZ', prefHelper.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_PLZ))
        domBuilder.userfield(name: 'erstellerOrt', prefHelper.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_ORT))
        domBuilder.userfield(name: 'erstellerTelefon', prefHelper.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_TEL))
        domBuilder.userfield(name: 'erstellerFax', prefHelper.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_FAX))
        domBuilder.userfield(name: 'erstellerEmail', prefHelper.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_EMAIL))
    }
    
    /**
     * @param map model.map.kundendaten
     */
    private def addProjektdaten(domBuilder, map) {
        domBuilder.userfield(name: 'adBauvorhabenTextField', map.bauvorhaben)
    }
    
    /**
     * @param map model.map.kundendaten
     */
    private def addKundendaten(domBuilder, map) {
        // Grosshandel
        domBuilder.userfield(name: 'ghFirma1TextField', map.grosshandel.firma1)
        domBuilder.userfield(name: 'ghFirma2TextField', map.grosshandel.firma2)
        domBuilder.userfield(name: 'ghStrasseTextField', map.grosshandel.strasse)
        domBuilder.userfield(name: 'ghPlzOrtTextField', "${map.grosshandel.plz} ${map.grosshandel.ort}")
        domBuilder.userfield(name: 'ghTelefonTextField', map.grosshandel.telefon)
        domBuilder.userfield(name: 'ghFaxTextField', map.grosshandel.telefax)
        domBuilder.userfield(name: 'ghAnsprechpartnerTextField', map.grosshandel.ansprechpartner)
        // Ausführende Firma
        domBuilder.userfield(name: 'afFirma1TextField', map.ausfuhrendeFirma.firma1)
        domBuilder.userfield(name: 'afFirma2TextField', map.ausfuhrendeFirma.firma2)
        domBuilder.userfield(name: 'afStrasseTextField', map.ausfuhrendeFirma.strasse)
        domBuilder.userfield(name: 'afPlzOrtTextField', "${map.ausfuhrendeFirma.plz} ${map.ausfuhrendeFirma.ort}")
        domBuilder.userfield(name: 'afTelefonTextField', map.ausfuhrendeFirma.telefon)
        domBuilder.userfield(name: 'afFaxTextField', map.ausfuhrendeFirma.telefax)
        domBuilder.userfield(name: 'afAnsprechpartnerTextField', map.ausfuhrendeFirma.ansprechpartner)
    }
    
    /**
     * @param map model.map.kundendaten
     */
    private def addInformationen(domBuilder, map) {
        // Gerätestandort
        domBuilder.userfield(name: 'gsKellergeschossRadioButton', gt(map.anlage.standort, "KG", "Kellergeschoss"))
        domBuilder.userfield(name: 'gsErdgeschossRadioButton', gt(map.anlage.standort, "EG", "Erdgeschoss"))
        domBuilder.userfield(name: 'gsObergeschossRadioButton', gt(map.anlage.standort, "OG", "Obergeschoss"))
        domBuilder.userfield(name: 'gsDachgeschossRadioButton', gt(map.anlage.standort, "DG", "Dachgeschoss"))
        domBuilder.userfield(name: 'gsSpitzbodenRadioButton', gt(map.anlage.standort, "SB", "Spitzboden"))
        // Luftkanalverlegung
        domBuilder.userfield(name: 'lkAufputzCheckbox', gt(map.anlage.luftkanalverlegung, "aufputz", "Aufputz (Abkastung)"))
        domBuilder.userfield(name: 'lkDaemmschichtCheckbox', gt(map.anlage.luftkanalverlegung, "dammschicht", "Dämmschicht unter Estrich"))
        domBuilder.userfield(name: 'lkDeckeCheckbox', gt(map.anlage.luftkanalverlegung, "decke", "Decke (abgehängt)"))
        domBuilder.userfield(name: 'lkSpitzbodenCheckbox', gt(map.anlage.luftkanalverlegung, "spitzboden", "Spitzboden"))
        // Geplante Belegung
        println "map.gebaude -> ${map.gebaude?.dump()}"
        domBuilder.userfield(name: 'personenAnzahlSpinner', GH.toString0Converter(map.gebaude.geplanteBelegung.personenanzahl))
        // Außenluft
        domBuilder.userfield(name: 'rbAlDachdurchfuehrung', gt(map.anlage.aussenluft, "dach", "Dachdurchführung"))
        domBuilder.userfield(name: 'rbAlWand', gt(map.anlage.aussenluft, "wand", "Wand (Luftgitter)"))
        domBuilder.userfield(name: 'rbAlErdwaermetauscher', gt(map.anlage.aussenluft, "erdwarme", "Erdwärmetauscher"))
        // Fortluft
        domBuilder.userfield(name: 'flDachdurchfuehrungRadioButton', gt(map.anlage.fortluft, "dach", "Dachdurchführung"))
        domBuilder.userfield(name: 'flWandRadioButton', gt(map.anlage.fortluft, "wand", "Wand (Luftgitter)"))
        domBuilder.userfield(name: 'flLichtschachtRadioButton', gt(map.anlage.fortluft, "bogen135", "Bogen 135°"))
        // Luftauslässe
        domBuilder.userfield(name: 'laTellerventileCheckbox', gt(map.anlage.abluft, "tellerventile", "Tellerventile (Standard)"))
        // Lufteinlässe
        domBuilder.userfield(name: 'lzTellerventileCheckbox', gt(map.anlage.zuluft, "tellerventile", "Tellerventile"))
        domBuilder.userfield(name: 'lzSchlitzauslassCheckbox', gt(map.anlage.zuluft, "schlitzauslass", "Schlitzauslass (Weitwurfdüse)"))
        domBuilder.userfield(name: 'lzFussbodenauslassCheckbox', gt(map.anlage.zuluft, "fussboden", "Fußbodenauslass"))
        domBuilder.userfield(name: 'lzSockelquellauslassCheckbox', gt(map.anlage.zuluft, "sockel", "Sockelquellauslass"))
        // Gebäudetyp
        domBuilder.userfield(name: 'gtMFHRadioButton', gt(map.gebaude.typ, "mfh", "Mehrfamilienhaus MFH"))
        domBuilder.userfield(name: 'gtEFHRadioButton', gt(map.gebaude.typ, "efh", "Einfamilienhaus EFH"))
        domBuilder.userfield(name: 'gtMaisonetteRadioButton', gt(map.gebaude.typ, "maisonette", "Maisonette"))
        // Gebäudelage
        domBuilder.userfield(name: 'glWschwachRadioButton', gt(map.gebaude.lage, "windschwach", "windschwach"))
        domBuilder.userfield(name: 'glWstarkRadioButton', gt(map.gebaude.lage, "windstark", "windstark"))
        // Wärmeschutz
        domBuilder.userfield(name: 'wsHochRadioButton', gt(map.gebaude.warmeschutz, "hoch", "hoch (Neubau / Sanierung mind. WSchV 1995)"))
        domBuilder.userfield(name: 'wsNiedrigRadioButton', gt(map.gebaude.warmeschutz, "niedrig", "niedrig (Gebäude bestand vor 1995)"))
        // Luftdichtheit
        domBuilder.userfield(name: 'ldKatARadioButton', gt(map.gebaude.luftdichtheit, "kategorieA", "Kategorie A (ventilatorgestützt)"))
        domBuilder.userfield(name: 'ldKatBRadioButton', gt(map.gebaude.luftdichtheit, "kategorieB", "Kategorie B (frei, Neubau)"))
        domBuilder.userfield(name: 'ldKatCRadioButton', gt(map.gebaude.luftdichtheit, "kategorieC", "Kategorie C (frei, Bestand)"))
        domBuilder.userfield(name: 'ldMessRadioButton', gt(map.gebaude.luftdichtheit, "messwerte", "Messwerte"))
    }
    
    /**
     * @param map model.map
     */
    def addRaumdaten(domBuilder, map) {
        // Tabelle
        def m = [:]
        map.raum.raume.eachWithIndex { r, i ->
            domBuilder.userfield(name: "wfTabelleTable!B${i + 3}", r.raumBezeichnung)
            domBuilder.userfield(name: "wfTabelleTable!C${i + 3}", r.raumGeschoss)
            domBuilder.userfield(name: "wfTabelleTable!D${i + 3}", r.raumLuftart)
            domBuilder.userfield(name: "wfTabelleTable!E${i + 3}", GH.toString2Converter(r.raumFlache))
            domBuilder.userfield(name: "wfTabelleTable!F${i + 3}", GH.toString2Converter(r.raumHohe))
        }
        // Zusammenfassung
        domBuilder.userfield(name: 'lmeZuSummeWertLabel',
            GH.toString2Converter(map.raum.raume.findAll { it.raumLuftart == "ZU" }?.inject(0.0d, { o, n -> o + n.raumVolumen }) ?: 0.0d))
        domBuilder.userfield(name: 'lmeAbSummeWertLabel',
            GH.toString2Converter(map.raum.raume.findAll { it.raumLuftart == "AB" }?.inject(0.0d, { o, n -> o + n.raumVolumen }) ?: 0.0d))
        domBuilder.userfield(name: 'lmeUebSummeWertLabel',
            GH.toString2Converter(map.raum.raume.findAll { it.raumLuftart == "ÜB" }?.inject(0.0d, { o, n -> o + n.raumVolumen }) ?: 0.0d))
        domBuilder.userfield(name: 'lmeGesamtvolumenWertLabel',
            GH.toString2Converter(map.raum.raumVs.gesamtVolumenNE))
        domBuilder.userfield(name: 'lmeGebaeudeluftwechselWertLabel',
            GH.toString2Converter(map.raum.raumVs.luftwechselNE))
        domBuilder.userfield(name: 'kzKennzeichenLabel', map.anlage.kennzeichnungLuftungsanlage)
        // Bemerkungen
        domBuilder.userfield(name: 'adNotizenTextArea', map.kundendaten.notizen)
    }
    
    /**
     * @param map model.map
     */
    def addRaumvolumenstrome(domBuilder, map) {
        // Tabelle
        def m = [:]
        map.raum.raume.eachWithIndex { r, i ->
            // raumZuluft- oder raumAbluftvolumenstrom
            // ZU/AB: größeren Wert nehmen
            def vs
            switch (r.raumLuftart) {
                case "ZU":
                    vs = r.raumZuluftVolumenstromInfiltration //r.raumVolumenstrom
                    break
                case "AB":
                    vs = r.raumAbluftVolumenstromInfiltration
                    break
                case "ZU/AB":
                    vs = java.lang.Math.max(r.raumZuluftVolumenstromInfiltration, r.raumAbluftVolumenstromInfiltration)
                    break
            }
            domBuilder.userfield(name: "lmeTabelleTable!B${i + 3}", r.raumBezeichnung)
            domBuilder.userfield(name: "lmeTabelleTable!C${i + 3}", noZero(GH.toString2Converter(r.raumVolumen)))
            domBuilder.userfield(name: "lmeTabelleTable!D${i + 3}", r.raumLuftart)
            domBuilder.userfield(name: "lmeTabelleTable!E${i + 3}", noZero(GH.toString2Converter(vs)))
            domBuilder.userfield(name: "lmeTabelleTable!F${i + 3}", noZero(GH.toString2Converter(r.raumLuftwechsel)))
            domBuilder.userfield(name: "lmeTabelleTable!G${i + 3}", noZero(GH.toString0Converter(r.raumAnzahlZuluftventile)))
            domBuilder.userfield(name: "lmeTabelleTable!H${i + 3}", noZero(GH.toString2Converter(r.raumZuluftmengeJeVentil)))
            domBuilder.userfield(name: "lmeTabelleTable!I${i + 3}", r.raumBezeichnungZuluftventile)
            domBuilder.userfield(name: "lmeTabelleTable!J${i + 3}", noZero(GH.toString0Converter(r.raumAnzahlAbluftventile)))
            domBuilder.userfield(name: "lmeTabelleTable!K${i + 3}", noZero(GH.toString2Converter(r.raumAbluftmengeJeVentil)))
            domBuilder.userfield(name: "lmeTabelleTable!L${i + 3}", r.raumBezeichnungAbluftventile)
            domBuilder.userfield(name: "lmeTabelleTable!M${i + 3}", r.raumVerteilebene)
        }
        // Ergebnis der Berechnungen
        domBuilder.userfield(name: 'lmeSumLTMZuluftmengeWertLabel',
            noZero(GH.toString2Converter(map.raum.raume.findAll { it.raumLuftart == "ZU" }?.inject(0.0d, { o, n -> o + n.raumZuluftVolumenstromInfiltration }) ?: 0.0d)))
        domBuilder.userfield(name: 'lmeSumLTMAbluftmengeWertLabel',
            noZero(GH.toString2Converter(map.raum.raume.findAll { it.raumLuftart == "AB" }?.inject(0.0d, { o, n -> o + n.raumAbluftVolumenstromInfiltration }) ?: 0.0d)))
        domBuilder.userfield(name: 'lmeGesAussenluftmengeWertLabel',
            GH.toString2Converter(map.raum.raumVs.gesamtaussenluftVsMitInfiltration))
    }
    
    /**
     * @param map model.map
     */
    def addUberstromelemente(domBuilder, map) {
        // Tabelle
        def m = [:]
        map.raum.raume.eachWithIndex { r, i ->
            // raumZuluft- oder raumAbluftvolumenstrom
            // ZU/AB: größeren Wert nehmen
            def vs
            // TODO Wert abzgl. Infiltration?
            switch (r.raumLuftart) {
                case "ZU":
                    vs = r.raumZuluftVolumenstromInfiltration
                    break
                case "AB":
                    vs = r.lmeSumLTMZuluftmengeWertLabel
                    break
                case "ZU/AB":
                    vs = java.lang.Math.max(r.raumZuluftVolumenstromInfiltration, r.lmeSumLTMZuluftmengeWertLabel)
                    break
            }
            domBuilder.userfield(name: "lmeTabelleUeberstroemTable!B${i + 3}", r.raumBezeichnung)
            domBuilder.userfield(name: "lmeTabelleUeberstroemTable!C${i + 3}", noZero(GH.toString2Converter(r.raumVolumen)))
            domBuilder.userfield(name: "lmeTabelleUeberstroemTable!D${i + 3}", r.raumLuftart)
            domBuilder.userfield(name: "lmeTabelleUeberstroemTable!E${i + 3}", noZero(GH.toString2Converter(r.raumUberstromVolumenstrom)))
            domBuilder.userfield(name: "lmeTabelleUeberstroemTable!F${i + 3}", noZero(GH.toString0Converter(r.raumAnzahlUberstromVentile)))
            domBuilder.userfield(name: "lmeTabelleUeberstroemTable!G${i + 3}", r.raumUberstromElement)
        }
        // Einstellungen am Lüftungsgerät und an der Fernbedienung
        domBuilder.userfield(name: 'lmeZentralgeraetCombobox', map.anlage.zentralgerat)
        domBuilder.userfield(name: 'lmeFeuchteschutzWertLabel', GH.toString2Round5Converter(map.aussenluftVs.gesamtLvsLtmLvsFs))
        domBuilder.userfield(name: 'lmeMindestlueftungWertLabel', GH.toString2Round5Converter(map.aussenluftVs.gesamtLvsLtmLvsRl))
        domBuilder.userfield(name: 'lmeGrundlueftungWertLabel', GH.toString2Round5Converter(map.aussenluftVs.gesamtLvsLtmLvsNl))
        domBuilder.userfield(name: 'lmeIntensivlueftungWertLabel', GH.toString2Round5Converter(map.aussenluftVs.gesamtLvsLtmLvsIl))
    }
    
    /**
     * @param map model.map
     */
    def addAkustikBerechnung(domBuilder, map) {
        // Zuluft
        //abZuTabelleUberschrift2Label = "Zuluft"
        map.akustik.zuluft.tabelle.eachWithIndex { ak, i ->
            domBuilder.userfield(name: "abZuTabelleTable!B${i + 2}", GH.toString2Converter(ak.slp125))
            domBuilder.userfield(name: "abZuTabelleTable!C${i + 2}", GH.toString2Converter(ak.slp250))
            domBuilder.userfield(name: "abZuTabelleTable!D${i + 2}", GH.toString2Converter(ak.slp500))
            domBuilder.userfield(name: "abZuTabelleTable!E${i + 2}", GH.toString2Converter(ak.slp1000))
            domBuilder.userfield(name: "abZuTabelleTable!F${i + 2}", GH.toString2Converter(ak.slp2000))
            domBuilder.userfield(name: "abZuTabelleTable!G${i + 2}", GH.toString2Converter(ak.slp4000))
        }
        domBuilder.userfield(name: 'abZuRaumbezeichnungComboBox', map.akustik.zuluft.raumBezeichnung)
        domBuilder.userfield(name: 'abZuSchallleistungspegelZuluftstutzenComboBox', map.akustik.zuluft.volumenstromZentralgerat)
        domBuilder.userfield(name: 'abZuKanalnetzComboBox', map.akustik.zuluft.slpErhohungKanalnetz as String)
        domBuilder.userfield(name: 'abZuFilterverschmutzungComboBox', map.akustik.zuluft.slpErhohungFilter as String)
        domBuilder.userfield(name: 'abZuHauptschalldaempfer1ComboBox', map.akustik.zuluft.hauptschalldampfer1)
        domBuilder.userfield(name: 'abZuHauptschalldaempfer2ComboBox', map.akustik.zuluft.hauptschalldampfer2)
        domBuilder.userfield(name: 'abZuAnzahlUmlenkungenTextField', map.akustik.zuluft.anzahlUmlenkungen as String)
        // throws NullPointerException
        domBuilder.userfield(name: 'abZuLuftverteilerkastenTextField', map.akustik.zuluft.luftverteilerkasten ?: '')
        domBuilder.userfield(name: 'abZuLaengsdaempfungKanalComboBox', map.akustik.zuluft.langsdampfungKanal)
        domBuilder.userfield(name: 'abZuLaengsdaempfungKanalTextField', map.akustik.zuluft.langsdampfungKanalLfdmMeter)
        domBuilder.userfield(name: 'abZuSchalldaempferVentilComboBox', map.akustik.zuluft.schalldampferVentil)
        domBuilder.userfield(name: 'abZuEinfuegungswertLuftdurchlassComboBox', map.akustik.zuluft.einfugungsdammwert)
        domBuilder.userfield(name: 'abZuRaumabsorptionTextField', map.akustik.zuluft.raumabsorption)
        domBuilder.userfield(name: 'abZuTabelleDezibelWertLabel', /*GH.toString2Converter(*/map.akustik.zuluft.dbA/*)*/)
        domBuilder.userfield(name: 'abZuTabelleMittlererSchalldruckpegelWertLabel', GH.toString2Converter(map.akustik.zuluft.mittlererSchalldruckpegel))
        // Abluft
        //abAbTabelleUberschrift2Label = "Abluft"
        map.akustik.zuluft.tabelle.eachWithIndex { ak, i ->
            domBuilder.userfield(name: "abAbTabelleTable!B${i + 2}", GH.toString2Converter(ak.slp125))
            domBuilder.userfield(name: "abAbTabelleTable!C${i + 2}", GH.toString2Converter(ak.slp250))
            domBuilder.userfield(name: "abAbTabelleTable!D${i + 2}", GH.toString2Converter(ak.slp500))
            domBuilder.userfield(name: "abAbTabelleTable!E${i + 2}", GH.toString2Converter(ak.slp1000))
            domBuilder.userfield(name: "abAbTabelleTable!F${i + 2}", GH.toString2Converter(ak.slp2000))
            domBuilder.userfield(name: "abAbTabelleTable!G${i + 2}", GH.toString2Converter(ak.slp4000))
        }
        domBuilder.userfield(name: 'abAbRaumbezeichnungComboBox', map.akustik.abluft.raumBezeichnung)
        domBuilder.userfield(name: 'abAbSchallleistungspegelAbluftstutzenComboBox', map.akustik.abluft.volumenstromZentralgerat)
        domBuilder.userfield(name: 'abAbKanalnetzComboBox', map.akustik.abluft.slpErhohungKanalnetz as String)
        domBuilder.userfield(name: 'abAbFilterverschmutzungComboBox', map.akustik.abluft.slpErhohungFilter as String)
        domBuilder.userfield(name: 'abAbHauptschalldaempfer1ComboBox', map.akustik.abluft.hauptschalldampfer1)
        domBuilder.userfield(name: 'abAbHauptschalldaempfer2ComboBox', map.akustik.abluft.hauptschalldampfer2)
        domBuilder.userfield(name: 'abAbAnzahlUmlenkungenTextField', map.akustik.abluft.anzahlUmlenkungen as String)
        // throws NullPointerException
        domBuilder.userfield(name: 'abAbLuftverteilerkastenTextField', map.akustik.abluft.luftverteilerkasten ?: '')
        domBuilder.userfield(name: 'abAbLaengsdaempfungKanalComboBox', map.akustik.abluft.langsdampfungKanal)
        domBuilder.userfield(name: 'abAbLaengsdaempfungKanalTextField', map.akustik.abluft.langsdampfungKanalLfdmMeter)
        domBuilder.userfield(name: 'abAbSchalldaempferVentilComboBox', map.akustik.abluft.schalldampferVentil)
        domBuilder.userfield(name: 'abAbEinfuegungswertLuftdurchlassComboBox', map.akustik.abluft.einfugungsdammwert)
        domBuilder.userfield(name: 'abAbRaumabsorptionTextField', map.akustik.abluft.raumabsorption)
        domBuilder.userfield(name: 'abAbTabelleDezibelWertLabel', /*GH.toString2Converter(*/map.akustik.zuluft.dbA/*)*/)
        domBuilder.userfield(name: 'abAbTabelleMittlererSchalldruckpegelWertLabel', GH.toString2Converter(map.akustik.abluft.mittlererSchalldruckpegel))
    }
    
    /**
     * @param map model.map
     */
    def addDvbKanalnetz(domBuilder, map) {
        map.dvb.kanalnetz.eachWithIndex { kn, i ->
            domBuilder.userfield(name: "dvbTeilstreckenTabelleTable!B${i + 3}", kn.luftart)
            domBuilder.userfield(name: "dvbTeilstreckenTabelleTable!C${i + 3}", kn.teilstrecke)
            domBuilder.userfield(name: "dvbTeilstreckenTabelleTable!D${i + 3}", GH.toString2Converter(kn.luftVs))
            domBuilder.userfield(name: "dvbTeilstreckenTabelleTable!E${i + 3}", kn.kanalbezeichnung)
            domBuilder.userfield(name: "dvbTeilstreckenTabelleTable!F${i + 3}", GH.toString2Converter(kn.lange))
            domBuilder.userfield(name: "dvbTeilstreckenTabelleTable!G${i + 3}", GH.toString2Converter(kn.geschwindigkeit))
            domBuilder.userfield(name: "dvbTeilstreckenTabelleTable!H${i + 3}", GH.toString2Converter(kn.reibungswiderstand))
            domBuilder.userfield(name: "dvbTeilstreckenTabelleTable!I${i + 3}", GH.toString2Converter(kn.gesamtwiderstandszahl))
            domBuilder.userfield(name: "dvbTeilstreckenTabelleTable!J${i + 3}", GH.toString2Converter(kn.einzelwiderstand))
            domBuilder.userfield(name: "dvbTeilstreckenTabelleTable!K${i + 3}", GH.toString2Converter(kn.widerstandTeilstrecke))
        }
    }
    
    /**
     * @param map model.map
     */
    def addDvbVentileinstellung(domBuilder, map) {
        map.dvb.ventileinstellung.eachWithIndex { ve, i ->
            domBuilder.userfield(name: "dvbVentileinstellungTabelleTable!B${i + 3}", ve.luftart)
            domBuilder.userfield(name: "dvbVentileinstellungTabelleTable!C${i + 3}", ve.raum)
            domBuilder.userfield(name: "dvbVentileinstellungTabelleTable!D${i + 3}", ve.teilstrecken)
            domBuilder.userfield(name: "dvbVentileinstellungTabelleTable!E${i + 3}", ve.ventilbezeichnung)
            domBuilder.userfield(name: "dvbVentileinstellungTabelleTable!F${i + 3}", GH.toString2Converter(ve.dpOffen))
            domBuilder.userfield(name: "dvbVentileinstellungTabelleTable!G${i + 3}", GH.toString2Converter(ve.gesamtWiderstand))
            domBuilder.userfield(name: "dvbVentileinstellungTabelleTable!H${i + 3}", GH.toString2Converter(ve.differenz))
            domBuilder.userfield(name: "dvbVentileinstellungTabelleTable!I${i + 3}", GH.toString2Converter(ve.abgleich))
            domBuilder.userfield(name: "dvbVentileinstellungTabelleTable!J${i + 3}", GH.toString2Converter(ve.einstellung))
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

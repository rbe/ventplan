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

import groovyx.net.http.ContentType
import com.bensmann.griffon.GriffonHelper as GH
import com.bensmann.griffon.PdfCreator

/**
 *
 */
class ProjektController {

    public static boolean DEBUG = false
    Boolean loadMode = false

    def builder
    def model
    def view

    def wacCalculationService
    def wacModelService
    def projektModelService
    def oooService

    javax.swing.JDialog raumBearbeitenDialog
    def wbwDialog
    def teilstreckenDialog

    def static auslegungPrefs = AuslegungPrefHelper.getInstance()
    def nutzerdatenDialog

    def waitDialog
    boolean nutzerdatenGeandert

    def angebotsverfolgungDialog

    /**
     * Initialize MVC group.
     */
    void mvcGroupInit(Map args) {
        // Save MVC id
        model.mvcId = args.mvcId
        // Set defaults
        setDefaultValues()
        // Add PropertyChangeListener to our model.meta
        GH.addMapPropertyChangeListener("meta", model.meta)
        // Add PropertyChangeListener to our model.map
        GH.addMapPropertyChangeListener("map", model.map, { evt ->
            // Nur ausführen, wenn Projekt nicht gerade geladen wird
            if (!loadMode) {
                // Show dialog only when property changes
                if (evt.propertyName == "ltm") {
                    ltmErforderlichDialog()
                }
                // Only set dirty flag, when modified property is not the dirty flag
                // Used for loading and saving
                else if (evt.propertyName != "dirty" && !model.map.dirty) {
                    // Dirty-flag im eigenen und Wac2Model setzen
                    model.map.dirty = true
                    // Wac2Model über Änderung informieren
                    app.models["wac2"].aktivesProjektGeandert =
                    app.models["wac2"].alleProjekteGeandert =
                        true
                    // Change tab title (show a star)
                    ////println "popertyChangeListener: calling setTabTitle: ${evt.propertyName}"
                    setTabTitle(view.projektTabGroup.tabCount - 1)
                }
            }
        })
        //}
    }

    /**
     * Setze Standardwerte (meist in Comboboxen).
     */
    def setDefaultValues() {
        // Lookup values from database and put them into our model
//        doOutside {
        // Raumdaten - Türen
        model.meta.raumTurTyp = ["Tür", "Durchgang"]
        model.meta.raumTurbreiten = [610, 735, 860, 985, 1110, 1235, 1485, 1735, 1985]
        // Raumvolumenströme - Bezeichnungen der Zu-/Abluftventile
        model.meta.raum.raumVsBezeichnungZuluftventile = wacModelService.getZuluftventile()
        model.meta.raum.raumVsBezeichnungAbluftventile = wacModelService.getAbluftventile()
        // Raumvolumenströme - Überströmelemente
        model.meta.raum.raumVsUberstromelemente = wacModelService.getUberstromelemente()
        // Fix: raum typ setzen, sonst wird bei den AkustikBindings eine Exception geworfen.
        //model.meta.raum.typ = ["Wohnzimmer", "Kinderzimmer", "Schlafzimmer", "Esszimmer", "Arbeitszimmer", "Gästezimmer",
        //          "Hausarbeitsraum", "Kellerraum", "WC", "Küche", "Kochnische", "Bad mit/ohne WC", "Duschraum",
        //          "Sauna", "Flur", "Diele"]
        // Raumvolumenströme - Zentralgerät + Volumenstrom
        model.meta.zentralgerat = wacModelService.getZentralgerat()
        // Liste aller möglichen Volumenströme des 1. Zentralgeräts
        def volumenstromZentralgerat =
            wacModelService.getVolumenstromFurZentralgerat(model.meta.zentralgerat[0])
        // 5er-Schritte
        model.meta.volumenstromZentralgerat = []
        def minVsZentralgerat = volumenstromZentralgerat[0] as Integer
        def maxVsZentralgerat = volumenstromZentralgerat.toList().last() as Integer
        (minVsZentralgerat..maxVsZentralgerat).step 5, { model.meta.volumenstromZentralgerat << it }
        // Druckverlustberechnung - Kanalnetz - Kanalbezeichnung
        model.meta.druckverlust.kanalnetz.kanalbezeichnung = wacModelService.getDvbKanalbezeichnung()
        // Druckverlustberechnung - Kanalnetz - Widerstandsbeiwerte
        model.meta.wbw = wacModelService.getWbw()
        // Druckverlustberechnung - Ventileinstellung - Ventilbezeichnung
        model.meta.druckverlust.ventileinstellung.ventilbezeichnung = wacModelService.getDvbVentileinstellung()
        // Akustikberechnung - 1. Hauptschalldämpfer
        model.meta.akustik.schalldampfer = wacModelService.getSchalldampfer()
        //
        doLater {
            // Raumvolumenströme, Zentralgerät
            model.map.anlage.zentralgerat = model.meta.zentralgerat[0]
            // Raumvolumenströme, Volumenstrom des Zentralgeräts; default ist erster Wert der Liste
            model.map.anlage.volumenstromZentralgerat = model.meta.volumenstromZentralgerat[0]
        }
//        }
    }

    /**
     * Titel für dieses Projekt erstellen: Bauvorhaben, ansonsten MVC ID.
     */
    StringBuilder getProjektTitel() {
        def title = new StringBuilder()
        // Bauvorhaben
        def bauvorhaben = model.map.kundendaten.bauvorhaben
        if (bauvorhaben) {
            title << "Projekt - ${bauvorhaben}"
        } else {
            title << model.mvcId
        }
        if (DEBUG)
            println "ProjektController.getProjektTitel stop -> ${title}"
        title
    }

    /**
     * Titel der Tab für dieses Projekt erstellen, und Sternchen für ungesicherte Änderungen anhängen.
     */
    StringBuilder makeTabTitle() {
        def tabTitle = getProjektTitel()
        if (DEBUG)
            println "ProjektController.makeTabTitle start -> ${tabTitle}"
        // Dateiname des Projekts oder MVC ID
        tabTitle << " (${model.wpxFilename ?: view.mvcId})"
        // Ungespeicherte Daten?
        if (model.map.dirty) {
            tabTitle << "*"
        }
        //
        tabTitle
    }

    /**
     * Titel des Projekts für Tab setzen.
     */
    def setTabTitle = { tabIndex ->
        if (DEBUG)
            println "ProjektController.setTabTitle tabIndex=${tabIndex}"
        if (!tabIndex) {
            tabIndex = view.projektTabGroup.selectedIndex
            if (DEBUG)
                println "ProjektController.setTabTitle new tabIndex=${tabIndex}"
        }
        def tabTitle = makeTabTitle()?.toString()
        try {
            view.projektTabGroup.setTitleAt(tabIndex, tabTitle)
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            // ignore
            println "${this}.setTabTitle(${tabIndex}): EXCEPTION: ${e}"
            /*
            java.lang.ArrayIndexOutOfBoundsException: -1
            at java.util.Vector.elementAt(Vector.java:430)
            at javax.swing.JTabbedPane.getTitleAt(JTabbedPane.java:1091)
            at com.jidesoft.swing.JideTabbedPane.setTitleAt(JideTabbedPane.java:1057)
            at com.jidesoft.swing.JideTabbedPane$setTitleAt.call(Unknown Source)
            at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCall(CallSiteArray.java:42)
            at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:108)
            at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:120)
            at com.westaflex.wac.ProjektController$_closure1.doCall(ProjektController.groovy:167)
            at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
            at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
            at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
            at java.lang.reflect.Method.invoke(Method.java:597)
            at org.codehaus.groovy.reflection.CachedMethod.invoke(CachedMethod.java:90)
            at groovy.lang.MetaMethod.doMethodInvoke(MetaMethod.java:233)
            at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:1053)
            at groovy.lang.ExpandoMetaClass.invokeMethod(ExpandoMetaClass.java:1071)
            at groovy.lang.MetaClassImpl.invokePropertyOrMissing(MetaClassImpl.java:1092)
            at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:1055)
            at groovy.lang.ExpandoMetaClass.invokeMethod(ExpandoMetaClass.java:1071)
            at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:883)
            at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:703)
            at groovy.lang.GroovyObjectSupport.invokeMethod(GroovyObjectSupport.java:44)
            at groovy.lang.MetaClassImpl.invokeMethodOnGroovyObject(MetaClassImpl.java:1118)
            at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:1012)
            at groovy.lang.ExpandoMetaClass.invokeMethod(ExpandoMetaClass.java:1071)
            at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:883)
            at org.codehaus.groovy.runtime.callsite.PogoMetaClassSite.callCurrent(PogoMetaClassSite.java:66)
            at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCallCurrent(CallSiteArray.java:46)
            at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callCurrent(AbstractCallSite.java:133)
            at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callCurrent(AbstractCallSite.java:141)
             */
        }
    }

    /**
     * Can we close? Is there unsaved data -- is our model dirty?
     */
    boolean canClose() {
        model.map.dirty == false
    }

    /**
     * WAC-151: Perform automatic calculations of 'everything'.
     */
    def automatischeBerechnung = {
        // Flags setzen
        model.map.anlage.zentralgeratManuell = false
        // Neu berechnen
        berechneAlles()
    }

    /**
     * Save this project.
     * @return Boolean Was project successfully saved to a file?
     */
    boolean save() {
        if (DEBUG)
            println "save: saving project '${getProjektTitel()}' in file ${model.wpxFilename?.dump()}"
        try {
            if (model.wpxFilename) {
                // Save data
                projektModelService.save(model.map, model.wpxFilename)
                // Set dirty-flag in project's model to false
                model.map.dirty = false
                // Update tab title to ensure that no "unsaved-data-star" is displayed
                setTabTitle(view.projektTabGroup.selectedIndex)
                // Project was saved
                true
            } else {
                // Project was not saved
                false
            }
        } catch (e) {
            def errorMsg = e.printStackTrace()
            app.controllers["Dialog"].showErrorDialog(errorMsg as String)
            // Project was not saved
            false
        }
    }

    /**
     * WAC-108 Auslegung und Angebot mit Stückliste erstellen.
     */
    def showNutzerdatenDialog(String title = null, String okButtonText = null, Closure closure = null) {
        // Anzeigen, dass Daten nicht geändert wurden (da dieser Dialog erneut angezeigt wird)
        nutzerdatenGeandert = false
        // Dialog erzeugen
        String _title = title ?: 'Informationen über den Ersteller'
        String _okButtonText = okButtonText ?: 'Eingaben speichern'
        nutzerdatenDialog = GH.createDialog(builder, NutzerdatenView, [title: _title, resizable: false, pack: true])
        view.auslegungErstellerSpeichern.text = _okButtonText
        // Gespeicherte Daten holen und in den Dialog setzen
        view.auslegungErstellerFirma.text = auslegungPrefs.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_FIRMA)
        view.auslegungErstellerName.text = auslegungPrefs.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_NAME)
        view.auslegungErstellerAnschrift.text = auslegungPrefs.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_STRASSE)
        view.auslegungErstellerPlz.text = auslegungPrefs.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_PLZ)
        view.auslegungErstellerOrt.text = auslegungPrefs.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_ORT)
        view.auslegungErstellerTelefon.text = auslegungPrefs.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_TEL)
        view.auslegungErstellerFax.text = auslegungPrefs.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_FAX)
        view.auslegungErstellerEmail.text = auslegungPrefs.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_EMAIL)
        view.auslegungErstellerEmpfanger.selectedItem = auslegungPrefs.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_EMPFANGER)
        view.auslegungErstellerDokumenttyp.selectedItem = auslegungPrefs.getPrefValue(AuslegungPrefHelper.PREFS_USER_KEY_DOKUMENTTYP)
        // Closure ausführen
        closure(nutzerdatenDialog)
        // Dialog ausrichten und anzeigen
        nutzerdatenDialog = GH.centerDialog(app.views['wac2'], nutzerdatenDialog)
        nutzerdatenDialog.show()
    }

    /**
     * WAC-108 Auslegung und Angebot mit Stückliste erstellen.
     * Dialog schliessen und nichts tun.
     */
    def nutzerdatenAbbrechen = {
        nutzerdatenDialog.dispose()
        // Anzeigen, dass Daten nicht geändert wurden
        nutzerdatenGeandert = false
    }

    /**
     * WAC-108 Auslegung und Angebot mit Stückliste erstellen.
     * Action: Saves Auslegung Ersteller information to preferences.
     */
    def nutzerdatenSpeichern = {
        try {
            // Daten aus dem Dialog holen
            def firma = view.auslegungErstellerFirma.text
            def name = view.auslegungErstellerName.text
            def anschrift = view.auslegungErstellerAnschrift.text
            def plz = view.auslegungErstellerPlz.text
            def ort = view.auslegungErstellerOrt.text
            def tel = view.auslegungErstellerTelefon.text
            def fax = view.auslegungErstellerFax.text
            def email = view.auslegungErstellerEmail.text
            def angebotsnummer = view.auslegungErstellerAngebotsnummer.text
            def empfanger = view.auslegungErstellerEmpfanger.selectedItem
            def dokumenttyp = view.auslegungErstellerDokumenttyp.selectedItem
            if (name?.trim() && anschrift?.trim() && plz?.trim() && ort?.trim()) {
                // Daten speichern
                def map = [:]
                /*
                        AuslegungPrefHelper.PREFS_USER_KEY_FIRMA: firma,
                        AuslegungPrefHelper.PREFS_USER_KEY_NAME: name,
                        AuslegungPrefHelper.PREFS_USER_KEY_STRASSE: anschrift,
                        AuslegungPrefHelper.PREFS_USER_KEY_PLZ: plz,
                        AuslegungPrefHelper.PREFS_USER_KEY_ORT: ort,
                        AuslegungPrefHelper.PREFS_USER_KEY_TEL: tel,
                        AuslegungPrefHelper.PREFS_USER_KEY_FAX: fax,
                        AuslegungPrefHelper.PREFS_USER_KEY_EMAIL: email,
                        AuslegungPrefHelper.PREFS_USER_KEY_ANGEBOTSNUMMER: angebotsnummer,
                        AuslegungPrefHelper.PREFS_USER_KEY_EMPFANGER: empfanger,
                        AuslegungPrefHelper.PREFS_USER_KEY_DOKUMENTTYP: dokumenttyp
                */
                map.put(AuslegungPrefHelper.PREFS_USER_KEY_FIRMA, firma)
                map.put(AuslegungPrefHelper.PREFS_USER_KEY_NAME, name)
                map.put(AuslegungPrefHelper.PREFS_USER_KEY_STRASSE, anschrift)
                map.put(AuslegungPrefHelper.PREFS_USER_KEY_PLZ, plz)
                map.put(AuslegungPrefHelper.PREFS_USER_KEY_ORT, ort)
                map.put(AuslegungPrefHelper.PREFS_USER_KEY_TEL, tel)
                map.put(AuslegungPrefHelper.PREFS_USER_KEY_FAX, fax)
                map.put(AuslegungPrefHelper.PREFS_USER_KEY_EMAIL, email)
                map.put(AuslegungPrefHelper.PREFS_USER_KEY_ANGEBOTSNUMMER, angebotsnummer)
                map.put(AuslegungPrefHelper.PREFS_USER_KEY_EMPFANGER, empfanger)
                map.put(AuslegungPrefHelper.PREFS_USER_KEY_DOKUMENTTYP, dokumenttyp)
                auslegungPrefs.save(map)
            } else {
                def errorMsg = "Auslegung konnte nicht erstellt werden. " +
                        "Es muss mindestens Name, Anschrift, PLZ, Ort angegeben werden." as String
                app.controllers["Dialog"].showErrorDialog(errorMsg as String)
            }
            // Anzeigen, dass Daten geändert wurden
            nutzerdatenGeandert = true
        } catch (e) {
            println "Error saving ersteller values -> ${e.dump()}"
        } finally {
            nutzerdatenDialog.dispose()
        }
    }

    /**
     * WAC-108 Auslegung und Angebot mit Stückliste erstellen.
     */
    def auslegungErstellen() {
        // Dialog immer anzeigen, damit die Nutzer die Daten ändern können.
        showNutzerdatenDialog(
                'Auslegung erstellen - Daten eingeben',
                'Eingaben speichern und Auslegung erstellen',
                { dialog ->
                    view.auslegungErstellerAngebotsnummer.enabled = false
                    view.auslegungErstellerEmpfanger.enabled = false
                }
        )
        if (nutzerdatenGeandert) {
            // Auslegung/Dokument erstellen
            try {
                File wpxFile = new File(model.wpxFilename)
                String xmlDoc = oooService.performAuslegung(wpxFile, model.map, DEBUG)
                java.io.File pdfFile
                doLater {
                    doOutside {
                        pdfFile = postToOdisee(wpxFile, 'Auslegung', xmlDoc)
                        if (pdfFile?.exists()) {
                            if (DEBUG)
                                println "auslegungErstellen: doc=${pdfFile?.dump()}"
                            // Open document
                            java.awt.Desktop.desktop.open(pdfFile)
                        } else {
                            if (DEBUG)
                                println "auslegungErstellen: PDF file does not exist: ${pdfFile?.dump()}"
                        }
                        waitDialog?.dispose()
                    }
                    // Dialog: Bitte warten...
                    waitDialog = GH.createDialog(builder, WaitingView, [title: "Die Auslegung wird erstellt", resizable: false, pack: true])
                    waitDialog = GH.centerDialog(app.views['wac2'], waitDialog)
                    waitDialog.show()
                }
            } catch (e) {
                println "auslegungErstellen: Error while calling 'Auslegung' -> ${e.dump()}"
                def errorMsg = "Auslegung konnte leider nicht erstellt werden.\n${e}" as String
                app.controllers["Dialog"].showErrorDialog(errorMsg as String)
            }
        }
    }

    /**
     * WAC-108 Auslegung und Angebot mit Stückliste erstellen.
     */
    def angebotErstellen() {
        // Dialog immer anzeigen, damit die Nutzer die Daten ändern können.
        showNutzerdatenDialog(
                'Angebot erstellen - Daten eingeben',
                'Eingaben speichern und Angebot erstellen',
                { dialog ->
                    view.auslegungErstellerAngebotsnummer.enabled = true
                    view.auslegungErstellerEmpfanger.enabled = true
                }
        )
        if (nutzerdatenGeandert) {
            // Auslegung/Dokument erstellen
            try {
                File wpxFile = new File(model.wpxFilename)
                String xmlDoc = oooService.performAngebot(wpxFile, model.map, DEBUG)
                java.io.File pdfFile
                doLater {
                    doOutside {
                        pdfFile = postToOdisee(wpxFile, 'Angebot', xmlDoc)
                        if (pdfFile?.exists()) {
                            if (DEBUG)
                                println "angebotErstellen: doc=${pdfFile?.dump()}"
                            // Open document
                            java.awt.Desktop.desktop.open(pdfFile)
                        } else {
                            if (DEBUG)
                                println "angebotErstellen: PDF fFile does not exist: ${pdfFile?.dump()}"
                        }
                        waitDialog?.dispose()
                    }
                    // Dialog: Bitte warten...
                    waitDialog = GH.createDialog(builder, WaitingView, [title: "Das Angebot wird erstellt", resizable: false, pack: true])
                    waitDialog = GH.centerDialog(app.views['wac2'], waitDialog)
                    waitDialog.show()
                }
            } catch (e) {
                println "angebotErstellen: Error while calling 'Angebot' -> ${e.dump()}"
                def errorMsg = "Angebot konnte leider nicht erstellt werden.\n${e}" as String
                app.controllers["Dialog"].showErrorDialog(errorMsg as String)
            }
        }
    }

    /**
     * WAC-108 Auslegung und Angebot mit Stückliste erstellen.
     */
    def stuecklisteErstellen() {
        // Dialog immer anzeigen, damit die Nutzer die Daten ändern können.
        showNutzerdatenDialog(
                'Stückliste erstellen - Daten eingeben',
                'Eingaben speichern und Stückliste erstellen',
                { dialog ->
                    view.auslegungErstellerAngebotsnummer.enabled = false
                    view.auslegungErstellerEmpfanger.enabled = false
                }
        )
        if (nutzerdatenGeandert) {
            // Auslegung/Dokument erstellen
            try {
                File wpxFile = new File(model.wpxFilename)
                String xmlDoc = oooService.performStueckliste(wpxFile, model.map, DEBUG)
                java.io.File pdfFile
                doLater {
                    doOutside {
                        pdfFile = postToOdisee(wpxFile, 'Stueckliste', xmlDoc)
                        if (pdfFile?.exists()) {
                            if (DEBUG)
                                println "stuecklisteErstellen: doc=${pdfFile?.dump()}"
                            // Open document
                            java.awt.Desktop.desktop.open(pdfFile)
                        } else {
                            if (DEBUG)
                                println "stuecklisteErstellen: PDF file does not exist: ${pdfFile?.dump()}"
                        }
                        waitDialog?.dispose()
                    }
                    // Dialog: Bitte warten...
                    waitDialog = GH.createDialog(builder, WaitingView, [title: "Die Stückliste wird erstellt", resizable: false, pack: true])
                    waitDialog = GH.centerDialog(app.views['wac2'], waitDialog)
                    waitDialog.show()
                }
            } catch (e) {
                println "stuecklisteErstellen: Error while calling 'Stückliste' -> ${e.dump()}"
                def errorMsg = "Stückliste konnte leider nicht erstellt werden.\n${e}" as String
                app.controllers["Dialog"].showErrorDialog(errorMsg as String)
            }
        }
    }

    /**
     * WAC-108 Auslegung und Angebot mit Stückliste erstellen.
     * Post XML document via REST and receive a PDF file.
     * @param wpxFile WPX-Datei mit Daten.
     * @param fileSuffix
     * @param xmlDoc
     * @return
     */
    java.io.File postToOdisee(File wpxFile, String fileSuffix, String xmlDoc) {
        java.io.File responseFile = null
        def restUrl = GH.getOdiseeRestUrl() as String
        def restPath = GH.getOdiseeRestPath() as String
        try {
            withRest(id: "odisee", uri: restUrl) {
                auth.basic 'wac', 're:Xai3u'
                def resp = post(path: restPath, body: xmlDoc, requestContentType: ContentType.XML, responseContentType: ContentType.BINARY, charset: 'utf-8')
                if (DEBUG)
                    println "${new Date()}: model.wpxFilename.absolutePath -> ${model.wpxFilename}"
                def byteArrayInputStream = new ByteArrayInputStream(resp.data.bytes)
                responseFile = new java.io.File(model.wpxFilename - '.wpx' + "_${fileSuffix}.pdf")
                responseFile << byteArrayInputStream
                if (DEBUG)
                    println "${new Date()}: response end..."
            }
        } catch (e) {
            println "post withRest exception -> ${e.dump()}"
            e.printStackTrace()
        }
        if (DEBUG)
            println "returning responsefile ${responseFile.absolutePath}"
        return responseFile
    }

    /**
     * WAC-177 Angebotsverfolgung
     */
    def angebotsverfolgung = {
        // Dialog erstellen
        angebotsverfolgungDialog = GH.createDialog(builder, AngebotsverfolgungView, [title: "Angebotsverfolgung durchführen", resizable: false, pack: true])
        // Dialog mit Daten füllen
        if (DEBUG)
            println "model.map.kundendaten=${model.map.kundendaten}"
        view.angebotsverfolgungDialogBauvorhaben.text = model.map.kundendaten.bauvorhaben
        view.angebotsverfolgungDialogAnschrift.text = model.map.kundendaten.bauvorhabenAnschrift
        view.angebotsverfolgungDialogPlz.text = model.map.kundendaten.bauvorhabenPlz
        view.angebotsverfolgungDialogOrt.text = model.map.kundendaten.bauvorhabenOrt
        // Dialog anzeigen
        angebotsverfolgungDialog = GH.centerDialog(app.views['wac2'], angebotsverfolgungDialog)
        angebotsverfolgungDialog.show()
    }

    /**
     * WAC-177 Angebotsverfolgung
     * Wert auslesen und auswerten...
     */
    def angebotsverfolgungAbbrechen = { evt ->
        angebotsverfolgungDialog.dispose()
    }

    /**
     * WAC-177 Angebotsverfolgung
     * Wert auslesen und auswerten...
     */
    def angebotsverfolgungErstellen = { evt ->
        if (view.angebotsverfolgungDialogAGB.selected) {
            def bauvorhabenValue = view.angebotsverfolgungDialogBauvorhaben.text
            def anschriftValue = view.angebotsverfolgungDialogAnschrift.text
            def plzValue = view.angebotsverfolgungPlz.text
            def ortValue = view.angebotsverfolgungOrt.text
            // TODO mmu http://wac.service.odisee.de/wac/177/bauvorhaben/<bauvorhaben>/anschrift/<anschrift>/plz/<plz>/ort/<ort>
            def restUrl = GH.getOdiseeWac177RestUrl() as String
            def restPath = GH.getOdiseeWac177RestPath() as String
            try {
                def postBody = [bauvorhaben: bauvorhabenValue,
                        anschrift: anschriftValue,
                        plz: plzValue,
                        ort: ortValue]
                withRest(id: "wac177", uri: restUrl) {
                    //auth.basic 'wac', 're:Xai3u'
                    def resp = post(path: restPath, body: postBody, requestContentType: ContentType.URLENC, responseContentType: ContentType.ANY, charset: 'utf-8')
                    println "ProjektController.angebotsverfolgungErstellen resp=${resp?.dump()}"
                    if (DEBUG)
                        println "${new Date()}: response end..."
                }
            } catch (e) {
                println "ProjektController.angebotsverfolgungErstellen exception -> ${e.dump()}"
                e.printStackTrace()
            }
        } else {
            def infoMsg = "Bitte akzeptieren Sie dazu die AGB!"
            app.controllers['Dialog'].showInformDialog(infoMsg as String)
        }
    }

    /**
     *
     */
    def angebotsverfolgungAGB = {
        view.angebotsverfolgungDialogAbsenden.enabled = !view.angebotsverfolgungDialogAbsenden.enabled
    }

    /**
     * WAC-177 Angebotsverfolgung
     * Öffnet den Link zu den AGBs in dem Default-Browser
     */
    def agbOeffnen = {
        def agbLink = "http://www.westaflex.com/support/agbs"
        java.awt.Desktop.getDesktop().browse(java.net.URI.create(agbLink));
    }

    /**
     * Alles neu berechnen.
     */
    def berechneAlles = { loadMode = false ->
        this.loadMode = loadMode
        if (DEBUG)
            println "berechneAlles: loadMode=${loadMode}"
        // Anlagendaten - Kennzeichen
        berechneEnergieKennzeichen()
        berechneHygieneKennzeichen()
        berechneKennzeichenLuftungsanlage()
        // Projekt wird geladen, Räume: Türen-TableModels hinzufügen
        if (loadMode) {
            // Räume; ConcurrentModificationException!
            def raume = model.map.raum.raume.clone()
            // Jedem Raum ein TableModel für Türen hinzufügen
            raume.each { raum ->
                model.addRaumTurenModel()
            }
        }
        // Räume: set cell editors
        if (loadMode)
            model.setRaumEditors(view)
        try {
            model.resyncRaumTableModels()
        } catch (e) {
            println "berechneAlles: resyncRaumTableModels: ${e}"
        }
        //
        model.map.raum.raume.each { raum ->
            if (DEBUG)
                println "berechneAlles: BERECHNE RAUM ${raum.position}"
            try {
                raumGeandert(raum.position, -1)
            } catch (e) {
                println "berechneAlles: ${raum.raumBezeichnung} ${e}"
            }
        }
        model.resyncRaumTableModels()
        // Druckverlustberechnung
        // Kanalnetze berechnen
        model.map.dvb.kanalnetz.each {
            if (DEBUG)
                println "berechneAlles: BERECHNE KANALNETZ ${it}"
            dvbKanalnetzGeandert(it.position)
        }
        // Ventile berechnen
        wacCalculationService.berechneVentileinstellung(model.map)
        // CellEditors, TableModels aktualisieren
        model.resyncDvbKanalnetzTableModels()
        model.resyncDvbVentileinstellungTableModels()
        if (loadMode)
            model.setDvbKanalnetzEditors(view)
        if (loadMode)
            model.setDvbVentileinstellungEditors(view)
        // Akustik
        berechneAkustik("Zuluft")
        berechneAkustik("Abluft")
        // Dirty flag
        model.map.dirty = false
        setTabTitle(view.projektTabGroup.selectedIndex)
        //
        this.loadMode = false
    }

    /**
     * Gebäudedaten wurden geändert - Aussenluftvolumenströme berechnen.
     */
    def gebaudedatenGeandert = { evt = null ->
        if (model.map.gebaude.lage.windstark) {
            // WAC-169: Änderung der Druckdifferenz durch Gebäudelage windstark
            doLater {
                model.map.gebaude.luftdichtheit.druckdifferenz = 4.0d
            }
        }
        berechneAussenluftVs()
    }

    /**
     * Gebäudedaten - Geometrie wurde manuell eingegeben.
     */
    def berechneGeometrie = {
        // Read values from view and transfer them into our model
        def g = [
                wohnflache: view.gebaudeGeometrieWohnflache.text.toDouble2(),
                raumhohe: view.gebaudeGeometrieMittlereRaumhohe.text.toDouble2(),
                //gelufteteFlache: view.gebaudeGeometrieGelufteteFlache.text.toDouble2()
                gelufteteVolumen: view.gebaudeGeometrieGeluftetesVolumen.text.toDouble2()
        ]
        doLater {
            // Write values into model
            model.map.gebaude.geometrie.wohnflache = g.wohnflache
            model.map.gebaude.geometrie.raumhohe = g.raumhohe
            //model.map.gebaude.geometrie.gelufteteFlache = g.gelufteteFlache
            model.map.gebaude.geometrie.gelufteteaVolumen = g.geluftetesVolumen
            //
            wacCalculationService.geometrie(model.map)
            wacCalculationService.aussenluftVs(model.map)
            // Zentralgerät bestimmen
            onZentralgeratAktualisieren()
        }
    }

    /**
     * Gebäudedaten - Luftdichtheit der Gebäudehülle
     */
    def luftdichtheitKategorieA = {
        doLater {
            model.map.gebaude.luftdichtheit.with {
                druckdifferenz = 2.0d
                luftwechsel = 1.0d
                druckexponent = 0.666f
            }
            berechneAussenluftVs()
        }
    }

    /**
     * Gebäudedaten - Luftdichtheit der Gebäudehülle
     */
    def luftdichtheitKategorieB = {
        doLater {
            model.map.gebaude.luftdichtheit.with {
                druckdifferenz = 2.0d
                luftwechsel = 1.5f
                druckexponent = 0.666f
            }
            berechneAussenluftVs()
        }
    }

    /**
     * Gebäudedaten - Luftdichtheit der Gebäudehülle
     */
    def luftdichtheitKategorieC = {
        doLater {
            model.map.gebaude.luftdichtheit.with {
                druckdifferenz = 2.0d
                luftwechsel = 2.0d
                druckexponent = 0.666f
            }
            berechneAussenluftVs()
        }
    }

    /**
     * Gebäudedaten - Luftdichtheit der Gebäudehülle
     */
    def speichereLuftdichtheit = {
        doLater {
            model.map.gebaude.luftdichtheit.druckdifferenz = view.gebaudeLuftdichtheitDruckdifferenz.text.toDouble2()
            model.map.gebaude.luftdichtheit.luftwechsel = view.gebaudeLuftdichtheitLuftwechsel.text.toDouble2()
            model.map.gebaude.luftdichtheit.druckexponent = view.gebaudeLuftdichtheitDruckexponent.text.toDouble2()
        }
        berechneAussenluftVs()
    }

    /**
     * Gebäudedaten - Faktor für besondere Anforderungen
     */
    def speichereFaktorBesondereAnforderungen = {
        doLater {
            model.map.gebaude.faktorBesondereAnforderungen = view.faktorBesondereAnforderungen.text.toDouble2()
        }
    }

    /**
     * Gebäudedaten - Geplante Belegung
     */
    def berechneMindestaussenluftrate = {
        doLater {
            // Save actual caret position
            def personenanzahlCaretPos = view.gebaudeGeplantePersonenanzahl.editor.textField.caretPosition
            def aussenluftVsProPersonCaretPos = view.gebaudeGeplanteAussenluftVsProPerson.editor.textField.caretPosition
            // Hack: model is not updated when KeyListener is used on spinner.editor.textfield
            model.map.gebaude.geplanteBelegung.personenanzahl = view.gebaudeGeplantePersonenanzahl.editor.textField.text?.toDouble2(0) ?: 0
            model.map.gebaude.geplanteBelegung.aussenluftVsProPerson = view.gebaudeGeplanteAussenluftVsProPerson.editor.textField.text?.toDouble2() ?: 0.0d
            model.map.gebaude.geplanteBelegung.with {
                try {
                    mindestaussenluftrate = personenanzahl * aussenluftVsProPerson
                } catch (e) {
                    def errorMsg = e.printStackTrace()
                    app.controllers["Dialog"].showErrorDialog(errorMsg as String)
                    mindestaussenluftrate = 0.0d
                }
            }
            try {
                // Set caret to old position; is moved through model update?
                view.gebaudeGeplantePersonenanzahl.editor.textField.caretPosition = personenanzahlCaretPos
                view.gebaudeGeplanteAussenluftVsProPerson.editor.textField.caretPosition = aussenluftVsProPersonCaretPos
            } catch (e) {
                e.printStackTrace()
            }
            // Berechnen
            berechneAussenluftVs()
        }
    }

    /**
     * Anlagendaten - Energie-Kennzeichen
     */
    def berechneEnergieKennzeichen = {
        doLater {
            model.map.anlage.energie.with {
                if (zuAbluftWarme && bemessung && ruckgewinnung && regelung) {
                    nachricht = "Energiekennzeichen gesetzt!"
                } else {
                    nachricht = " "
                }
            }
            berechneKennzeichenLuftungsanlage()
        }
    }

    /**
     * Anlagendaten - Hygiene-Kennzeichen
     */
    def berechneHygieneKennzeichen = {
        doLater {
            model.map.anlage.hygiene.with {
                if (ausfuhrung && filterung && keineVerschmutzung && dichtheitsklasseB) {
                    nachricht = "Hygienekennzeichen gesetzt!"
                } else {
                    nachricht = " "
                }
            }
            berechneKennzeichenLuftungsanlage()
        }
    }

    /**
     * Anlagendaten - Kennzeichen
     */
    def berechneKennzeichenLuftungsanlage = {
        doLater {
            def gebaudeTyp = model.map.gebaude.typ.efh ? "EFH" : "WE"
            def energieKz = model.map.anlage.energie.nachricht != " " ? "E" : "0"
            def hygieneKz = model.map.anlage.hygiene.nachricht != " " ? "H" : "0"
            def ruckschlag = model.map.anlage.ruckschlagklappe ? "RK" : "0"
            def schallschutz = model.map.anlage.schallschutz ? "S" : "0"
            def feuerstatte = model.map.anlage.feuerstatte ? "F" : "0"
            model.map.anlage.kennzeichnungLuftungsanlage =
                "ZuAbLS-Z-${gebaudeTyp}-WÜT-${energieKz}-${hygieneKz}-${ruckschlag}-${schallschutz}-${feuerstatte}"
        }
    }

    /**
     * Aussenluftvolumenströme berechnen.
     */
    def berechneAussenluftVs = {
        // Mit/ohne Infiltrationsanteil berechnen
        wacCalculationService.aussenluftVs(model.map)
        // Zentralgerät bestimmen
        onZentralgeratAktualisieren()
    }

    /**
     * Raumdaten - Eingabe - Raumtyp in Combobox ausgewählt.
     */
    def raumTypGeandert = {
        doLater {
            switch (view.raumTyp.selectedIndex) {
            // Zulufträume
                case 0..5:
                    view.raumLuftart.selectedItem = "ZU"
                    switch (view.raumTyp.selectedIndex) {
                        case 0:
                            view.raumAbluftVolumenstrom.text = ""
                            view.raumZuluftfaktor.text = "3,00"
                            break
                        case 1..2:
                            view.raumAbluftVolumenstrom.text = ""
                            view.raumZuluftfaktor.text = "2,00"
                            break
                        case 3..5:
                            view.raumAbluftVolumenstrom.text = ""
                            view.raumZuluftfaktor.text = "1,50"
                            break
                    }
                    break
            // Ablufträume
                case 6..13:
                    view.raumLuftart.selectedItem = "AB"
                    switch (view.raumTyp.selectedIndex) {
                        case 6..8:
                            view.raumZuluftfaktor.text = ""
                            view.raumAbluftVolumenstrom.text = "25"
                            break
                        case 9..12:
                            view.raumZuluftfaktor.text = ""
                            view.raumAbluftVolumenstrom.text = "45"
                            break
                        case 13:
                            view.raumZuluftfaktor.text = ""
                            view.raumAbluftVolumenstrom.text = "100"
                            break
                    }
                    break
            // Überströmräume
                case { it > 13 }:
                    view.raumLuftart.selectedItem = "ÜB"
                    view.raumZuluftfaktor.text = ""
                    view.raumAbluftVolumenstrom.text = ""
            }
        }
    }

    /**
     * Zeige Dialog "lüftungstechnische Maßnahmen erforderlich."
     */
    def ltmErforderlichDialog = {
        def infoMsg = model.map.messages.ltm
        app.controllers["Dialog"].showInformDialog(infoMsg as String)
        if (DEBUG)
            println infoMsg
    }

    /**
     * Raumdaten - Raum anlegen.
     */
    def raumHinzufugen = {
        _raumHinzufugen()
    }

    def _raumHinzufugen() {
        // Erstelle Model für Raum: Standardwerte überschreiben mit eingegebenen Werten
        // Berechne Position: Raum wird unten angefügt
        def raum = model.raumMapTemplate.clone() +
                GH.getValuesFromView(view, "raum") +
                [position: model.map.raum.raume.size()]
        // TODO rbe Improve cloning of list with maps: use deep, not shallow copy.
        // raum.turen = model.raumTurenTemplate.clone() as ObservableList
        raum.turen = [
                [turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
                [turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
                [turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
                [turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
                [turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true]
        ] as ObservableList
        // Hole Werte für neuen Raum aus der View und füge Raum hinzu
        raum.with {
            // Übernehme Wert für Bezeichnung vom Typ?
            raumBezeichnung = raumBezeichnung ?: raumTyp
            // Länge + Breite
            raumLange = 0.0d
            raumBreite = 0.0d
            // Fläche, Höhe, Volumen
            raumFlache = raumFlache.toDouble2()
            raumHohe = raumHohe.toDouble2()
            raumVolumen = raumFlache * raumHohe
            // Zuluftfaktor
            raumZuluftfaktor = raumZuluftfaktor?.toDouble2() ?: 0.0d
            // Abluftvolumenstrom
            raumAbluftVolumenstrom = raumAbluftVolumenstrom?.toDouble2() ?: 0.0d
            // Standard Türspalthöhe ist 10 mm
            raumTurspaltHohe = 10.0d
        }
        doLater {
            // Raum im Model hinzufügen
            if (DEBUG)
                println "onRaumHinzufugen: raum -> ${raum}"
            model.addRaum(raum, view)
            raumGeandert(raum.position, -1)
            // WAC-210: _raumGeandert(raum.position), wird nun über Benutzer/Button gemacht
            // WAC-170: abw. Raumbezeichnung leeren
            view.raumBezeichnung.text = ""
            // WAC-179: Abluftmenge je Ventil / Anzahl AB-Ventile ändert sich nicht, wenn ein Abluftraum gelöscht wird
            berechneAlles()
        }
    }

    /**
     * Raumdarten - ein Raum wurde geändert.
     * @param raumPosition Could be position or real index position.
     * @param setSelectedIndex Real index position in table view. Real selected index item.
     * @param isIndex Boolean value if this is the real index position in map rather than position-key in map.
     */
    def raumGeandert = { Integer raumPosition, Integer setSelectedIndex, boolean isIndex = false ->
        _raumGeandert(raumPosition, setSelectedIndex, isIndex)
    }

    def _raumGeandert(Integer raumPosition, Integer setSelectedIndex, boolean isIndex) {
        doLater {
            // WAC-174 (raumIndex kann == 0 sein!)
            def isSelectedRow = false
            if (raumPosition < 0) {
                raumPosition = view.raumTabelle.selectedRow
                isSelectedRow = true
            }
            if (DEBUG)
                println "raumGeandert: raum[${raumPosition}]"
            if (raumPosition > -1) {
                if (DEBUG)
                    println "raumGeandert: raum[${raumPosition}] ${model.map.raum.raume[raumPosition]}"
                // Raum holen
                ////def raum = model.map.raum.raume[raumIndex]
                // Raumdaten prüfen
                def raum
                def raumIndex
                if (!isSelectedRow && !isIndex) {
                    model.map.raum.raume.eachWithIndex { item, pos ->
                        if (item.position == raumPosition) {
                            raum = item
                            raumIndex = pos
                        }
                    }
                } else {
                    raumIndex = raumPosition
                }
                // Diesen Raum in allen Tabellen anwählen
                if (setSelectedIndex > -1) {
                    onRaumInTabelleWahlen(setSelectedIndex)
                } else {
                    onRaumInTabelleWahlen(raumIndex)
                }

                if (DEBUG)
                    println "raumGeandert: raum[${raumPosition}] currentRaum=${raum.dump()}"

                model.prufeRaumdaten(model.map.raum.raume[raumIndex])
                // WAC-65: Errechnete Werte zurücksetzen
                model.map.raum.raume[raumIndex].with {
                    if (raumBreite && raumLange)
                        raumFlache = raumBreite * raumLange
                    raumVolumen = raumFlache * raumHohe
                    raumLuftwechsel = 0.0d
                    // Abluft
                    raumAbluftVolumenstromInfiltration = 0.0d // Abluftvs abzgl. Infiltration
                    raumAnzahlAbluftventile = 0
                    raumAbluftmengeJeVentil = 0.0d
                    // Zuluft
                    raumZuluftVolumenstromInfiltration = 0.0d // Zuluftvs abzgl. Infiltration
                    raumAnzahlZuluftventile = 0
                    raumZuluftmengeJeVentil = 0.0d
                    raumAnzahlUberstromVentile = 0
                }
                // Überströmvolumenstrom
                // WAC-151
                if (!model.map.anlage.zentralgeratManuell) {
                    model.map.raum.raume[raumIndex].raumUberstromVolumenstrom = 0.0d
                }

                // Raumvolumenströme, (Werte abzgl. Infiltration werden zur Berechnung der Ventile benötigt) berechnen
                wacCalculationService.autoLuftmenge(model.map)
                // Zu-/Abluftventile
                model.map.raum.raume[raumIndex] =
                    wacCalculationService.berechneZuAbluftventile(model.map.raum.raume[raumIndex])
                // Türspalt und Türen
                model.map.raum.raume[raumIndex] =
                    wacCalculationService.berechneTurspalt(model.map.raum.raume[raumIndex])
                berechneTuren(null, raumIndex, isSelectedRow)
                // Überströmelement berechnen
                model.map.raum.raume[raumIndex] =
                    wacCalculationService.berechneUberstromelemente(model.map.raum.raume[raumIndex])
            }
            // Nummern der Räume berechnen
            wacCalculationService.berechneRaumnummer(model.map)
            // Gebäude-Geometrie berechnen
            wacCalculationService.geometrieAusRaumdaten(model.map)
            // Aussenluftvolumenströme berechnen
            wacCalculationService.aussenluftVs(model.map)
            // Zentralgerät bestimmen
            onZentralgeratAktualisieren()
            // WAC-171: Finde Räume ohne Türen oder ÜB-Elemente
            def raumeOhneTuren = model.map.raum.raume.findAll { raum ->
                raum.raumUberstromVolumenstrom > 0 && !raum.turen.any { it.turBreite > 0 }
            }
            def raumeOhneUbElemente = []
            raumeOhneUbElemente = model.map.raum.raume.findAll { raum ->
                def turSpalthoheUberschritten = raum.turen.findAll {
                    it.turSpalthohe > raum.raumMaxTurspaltHohe.toDouble2()
                }?.size() ?: 0
                if (DEBUG)
                    println "WAC-171: Raum=${raum.raumBezeichnung}, turspalthoheUberschritten=${turSpalthoheUberschritten}"
                if (DEBUG)
                    println "WAC-171: ${raum.raumUberstromVolumenstrom > 0} && ${turSpalthoheUberschritten > 0} && ${!raum.raumUberstromElement}"
                // WAC-187
                raum.raumUberstromVolumenstrom > 0 && turSpalthoheUberschritten > 0 && !raum.raumUberstromElement
            }
            model.map.raum.raumVs.turenHinweis = raumeOhneTuren.size() > 0 ?
                "Hinweis: bitte Türen prüfen: ${raumeOhneTuren.collect { it.raumBezeichnung }.join(", ")}" : ""
            model.map.raum.raumVs.ubElementeHinweis = raumeOhneUbElemente.size() > 0 ?
                "Hinweis: bitte ÜB-Elemente prüfen: ${raumeOhneUbElemente.collect { it.raumBezeichnung }.join(", ")}" : ""
        }
    }

    /**
     * Raumdaten - einen Raum entfernen.
     */
    def raumEntfernen = {
        _raumEntfernen()
    }

    def _raumEntfernen() {
        // Raum aus Model entfernen
        model.removeRaum(view.raumTabelle.selectedRow, view)
        // Es hat sich was geändert...
        def raum = model.map.raum.raume[view.raumTabelle.selectedRow]
        // WAC-210: raumGeandert(raum.position)
        raumGeandert(view.raumTabelle.selectedRow, -1, true)
        // WAC-179: Abluftmenge je Ventil / Anzahl AB-Ventile ändert sich nicht, wenn ein Abluftraum gelöscht wird
        berechneAlles()
    }

    /**
     * Raumdaten - einen Raum kopieren.
     */
    def raumKopieren = {
        _raumKopieren()
    }

    def _raumKopieren() {
        doLater {
            // Get selected row
            def row = view.raumTabelle.selectedRow
            // Raum anhand seiner Position finden
            //def x = model.map.raum.raume.find { it.position == row }
            def x = model.map.raum.raume[row]
            // Kopie erzeugen
            def newMap = new ObservableMap()
            x.collect {
                //[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true]
                // turen separat kopieren, da sonst Abhaengigkeiten zum Originalraum bestehen
                if (it.key == 'turen') {
                    if (DEBUG)
                        println "raumKopieren turen -> key=${it.key} ::: value=${it.value}"
                    def y = it.value
                    if (DEBUG)
                        println "turen dump ---> ${y.dump()}"
                    def turenList = [] as ObservableList
                    y.each() { i ->
                        if (DEBUG)
                            println "list=${i}"
                        def tur = i
                        def turenMap = new ObservableMap()
                        tur.collect {
                            turenMap.put(it.key, it.value)
                        }
                        turenList.add(turenMap)
                    }
                    if (DEBUG)
                        println "turenList -> ${turenList}"
                    newMap.put(it.key, turenList)
                }
                else {
                    newMap.put(it.key, it.value)
                }
                if (DEBUG)
                    println "raumKopieren -> key=${it.key} ::: value=${it.value}"

            }
            if (DEBUG)
                println "newMap -> ${newMap}"
            // Neuen Namen und neue Position (Ende) setzen
            // raumBezeichnung als String speichern (vorher GString).
            newMap.raumBezeichnung = "Kopie von ${x.raumBezeichnung}".toString()
            newMap.position = model.map.raum.raume.size()
            // Raum zum Model hinzufügen
            model.addRaum(newMap, view, true)
            // Raum hinzugefügt
            raumGeandert(newMap.position, -1)
            // WAC-179: Abluftmenge je Ventil / Anzahl AB-Ventile ändert sich nicht, wenn ein Abluftraum gelöscht wird
            berechneAlles()
        }
    }

    /**
     * Raumdaten - einen Raum in der Tabelle nach oben verschieben.
     */
    def raumNachObenVerschieben = {
        doLater {
            // Get selected row
            def row = view.raumTabelle.selectedRow
            if (row > 0) {
                def currentRaum
                // Recalculate positions
                model.map.raum.raume.each {
                    if (it.position == row - 1)
                        it.position += 1
                    else if (it.position == row) {
                        it.position -= 1
                        currentRaum = it
                    }
                }
                model.resyncRaumTableModels()
                // Raum geändert
                raumGeandert(currentRaum.position, row - 1)
            }
        }
    }

    /**
     * Raumdaten - einen Raum in der Tabelle nach oben verschieben.
     */
    def raumNachUntenVerschieben = {
        // Get selected row
        def row = view.raumTabelle.selectedRow
        if (row < view.raumTabelle.rowCount - 1) {
            // Recalculate positions
            def currentRaum
            model.map.raum.raume.each {
                if (it.position == row + 1)
                    it.position -= 1
                else if (it.position == row) {
                    it.position += 1
                    currentRaum = it
                }
            }
            model.resyncRaumTableModels()
            // Raum geändert
            raumGeandert(currentRaum.position, row + 1, false)
        }
    }

    /**
     * Raumdaten - einen Raum bearbeiten.
     */
    def raumBearbeiten = {
        doLater {
            // Get selected row
            if (DEBUG)
                println "raumBearbeiten: view.raumTabelle -> ${view.raumTabelle.dump()}"
            if (DEBUG)
                println "raumBearbeiten: model.meta.gewahlterRaum -> ${model.meta.gewahlterRaum.dump()}"
            def row = view.raumTabelle.selectedRow
            if (row > -1) {
                // Show dialog
                raumBearbeitenDialog = GH.createDialog(builder, RaumBearbeitenView, [title: "Raum bearbeiten", pack: true])
                // Modify TableModel for Turen
                def columnModel = view.raumBearbeitenTurenTabelle.columnModel
                GH.makeComboboxCellEditor(columnModel.getColumn(0), model.meta.raumTurTyp)
                GH.makeComboboxCellEditor(columnModel.getColumn(1), model.meta.raumTurbreiten)
                berechneTuren(null, model.meta.gewahlterRaum.position, false)

                raumBearbeitenDialog = GH.centerDialog(app.views['wac2'], raumBearbeitenDialog)

                raumBearbeitenDialog.show()
            } else {
                println "${this}.raumBearbeiten: no row selected"
            }
        }
    }

    /**
     * RaumBearbeiten - RaumBearbeitenView schliessen.
     */
    def raumBearbeitenSchliessen = {
        _raumBearbeitenSchliessen()
    }

    def _raumBearbeitenSchliessen() {
        if (DEBUG)
            println "raumBearbeitenSchliessen: closing dialog '${raumBearbeitenDialog.title}'"
        raumBearbeitenGeandert()
        raumBearbeitenDialog.dispose()
    }

    /**
     * Raum bearbeiten - Daten eingegeben. Mit raumBearbeitenSchliessen zusammenlegen?
     */
    def raumBearbeitenGeandert = { evt = null ->
        _raumBearbeitenGeandert()
    }

    def _raumBearbeitenGeandert() {
        // WAC-174: Immer Raum Index/Position aus Metadaten nehmen
        //def raumIndex = view.raumTabelle.selectedRow
        def raumPosition = model.meta.gewahlterRaum.position
        // Daten aus Dialog übertragen und neu berechnen
        def m = model.map.raum.raume.find { it.position == raumPosition }
        // Raumnummer
        model.meta.gewahlterRaum.raumNummer =
        m.raumNummer =
            view.raumBearbeitenRaumnummer.text
        // Raumbezeichnung
        model.meta.gewahlterRaum.raumBezeichnung =
        m.raumBezeichnung =
            view.raumBearbeitenBezeichnung.text
        // Raumtyp
        // Geschoss
        model.meta.gewahlterRaum.raumGeschoss =
        m.raumGeschoss =
            view.raumBearbeitenRaumGeschoss.selectedItem
        // Luftart
        try {
            //println "!!!: ${view.raumBearbeitenLuftart.selectedItem?.dump()}"
            model.meta.gewahlterRaum.raumLuftart =
            m.raumLuftart =
                view.raumBearbeitenLuftart.selectedItem
        } catch (e) {
            // TODO Warum, funktioniert trotzdem? groovy.lang.MissingPropertyException: No such property: text for class: javax.swing.JComboBox
            //println "raumBearbeitenGeandert: EXCEPTION: ${e}"
        }
        // Zuluftfaktor
        model.meta.gewahlterRaum.raumZuluftfaktor =
        m.raumZuluftfaktor =
            view.raumBearbeitenLuftartFaktorZuluftverteilung.text?.toDouble2()
        // Abluftvolumenstrom
        model.meta.gewahlterRaum.raumAbluftVolumenstrom =
        m.raumAbluftVolumenstrom =
            view.raumBearbeitenLuftartAbluftVs.text?.toDouble2()
        // Max. Türspalthöhe
        model.meta.gewahlterRaum.raumMaxTurspaltHohe =
        m.raumMaxTurspaltHohe =
            view.raumBearbeitenDetailsTurspalthohe.text?.toDouble2()
        // Geometrie
        model.meta.gewahlterRaum.raumLange =
        m.raumLange =
            view.raumBearbeitenOptionalRaumlange.text?.toDouble2()
        model.meta.gewahlterRaum.raumBreite =
        m.raumBreite =
            view.raumBearbeitenOptionalRaumbreite.text?.toDouble2()
        model.meta.gewahlterRaum.raumHohe =
        m.raumHohe =
            view.raumBearbeitenOptionalRaumhohe.text?.toDouble2()
        // Raum neu berechnen
        raumGeandert(raumPosition, -1)
        // Daten aus Model in den Dialog übertragen
        // Zuluft/Abluft
        model.meta.gewahlterRaum.raumZuluftfaktor = m.raumZuluftfaktor
        model.meta.gewahlterRaum.raumAbluftvolumenstrom = m.raumAbluftvolumenstrom
        ////if (model.meta.gewahlterRaum.)
        // Geometrie
        model.meta.gewahlterRaum.raumFlache = m.raumFlache
        model.meta.gewahlterRaum.raumVolumen = m.raumVolumen
        model.meta.gewahlterRaum.raumNummer = m.raumNummer
    }

    /**
     * Berechne Türen eines bestimmten Raumes.
     */
    def berechneTuren = { evt = null, raumIndex = null, isTableRow ->
        if (DEBUG)
            println "WAC-174: berechneTuren: evt=${evt?.dump()} raumIndex=${raumIndex?.dump()}"
        if (DEBUG)
            println "WAC-174: berechneTuren: raume=${model.map.raum?.dump()}"
        // ist der raumIndex aus der Raumtabelle?
        def isSelectedRow = false
        // Hole gewählten Raum
        if (!raumIndex) {
            raumIndex = view.raumTabelle.selectedRow
            isSelectedRow = true
        }
        if (DEBUG)
            println "WAC-174: berechneTuren: raumIndex=${raumIndex?.dump()}"
        // Suche Raum mittels übergebenen raumIndex
        def raum
        if (isSelectedRow || isTableRow) {
            model.map.raum.raume.eachWithIndex { item, pos ->
                if (raumIndex == pos) {
                    raum = item
                }
            }
        }
        else {
            raum = model.map.raum.raume.find { it.position == raumIndex }
        }

        if (DEBUG)
            println "WAC-174: berechneTuren: raum=${raum?.dump()}"
        // Türen berechnen?
        if (DEBUG)
            println "raum.turen -> ${raum?.turen.dump()}"
        if (raum?.turen.findAll { it.turBreite > 0 }?.size() > 0 && raum.raumUberstromVolumenstrom) {
            wacCalculationService.berechneTurspalt(raum)
            // WAC-165: Hinweis: Türspalt > max. Türspalthöhe?
            def turSpalthoheUberschritten = raum.turen.findAll {
                it.turSpalthohe > raum.raumMaxTurspaltHohe.toDouble2()
            }?.size() ?: 0
            if (turSpalthoheUberschritten > 0) {
                model.meta.gewahlterRaum.raumTurspaltHinweis = "Hinweis: Maximale Türspalthöhe überschritten!"
            } else {
                model.meta.gewahlterRaum.raumTurspaltHinweis = ""
            }
        }
        // WAC-165: Bugfix: Werte in der Türen-Tabelle werden erst dann aktualisiert, wenn die Maus über einzelne Zeilen bewegt wird
        try {
            view.raumBearbeitenTurenTabelle.repaint()
        } catch (e) { }
    }

    /**
     * Tur Werte entfernen in Raum bearbeiten Dialog
     */
    def raumBearbeitenTurEntfernen = { evt = null, reload = true ->
        if (DEBUG)
            println "raumBearbeitenTurEntfernen: view.raumBearbeitenTurenTabelle.selectedRow -> ${view.raumBearbeitenTurenTabelle.selectedRow}"
        def turenIndex = view.raumBearbeitenTurenTabelle.selectedRow
        def raumIndex = model.meta.gewahlterRaum.position
        //println "turenIndex -> ${turenIndex}"
        def raumPosition
        try {
            //def raum = model.map.raum.raume[raumIndex]
            //def raum = model.map.raum.raume.find { it.position == raumIndex }
            def raum
            model.map.raum.raume.eachWithIndex { item, pos ->
                if (item.position == raumIndex) {
                    raum = item
                    raumPosition = pos
                }
            }
            raum.turen[turenIndex] = [turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true]
            if (DEBUG)
                println "raumBearbeitenTurEntfernen: ${raum}"
            model.map.raum.raume[raumPosition] = raum
        } catch (e) { }
        if (DEBUG)
            println "raumBearbeitenTurEntfernen: raumPosition=${raumPosition}"
        // WAC-174: resyncTableModels ist notwendig, selectedRow wird auf 0 gesetzt, daher selectedRow setzen
        model.resyncRaumTableModels()
        //view.raumTabelle.changeSelection(model.meta.gewahlterRaum.position, 0, false, false) 
        view.raumTabelle.changeSelection(raumPosition, 0, false, false)
        // WAC-174: Parameter fehlten!
        berechneTuren(null, raumIndex, false)
        if (reload) {
            try {
                raumBearbeitenTurEntfernen(null, false)
            } catch (e) { }
        }
    }

    /**
     * Raumvolumenströme - Zu/Abluftventile geändert.
     */
    def raumZuAbluftventileGeandert = {
        def raumIndex = view.raumVsZuAbluftventileTabelle.selectedRow
        if (DEBUG)
            println "raumZuAbluftventileGeandert: raumIndex=${raumIndex}"
        if (raumIndex > -1) {
            wacCalculationService.berechneZuAbluftventile(model.map.raum.raume.find { it.position == raumIndex })
        } else {
            if (DEBUG)
                println "raumZuAbluftventileGeandert: Kein Raum ausgewählt, es wird nichts berechnet"
        }
    }

    /**
     * Raumvolumenströme - Überströmelemente geändert.
     */
    def raumUberstromelementeGeandert = {
        def raumIndex = view.raumVsUberstromelementeTabelle.selectedRow
        if (raumIndex > -1) {
            wacCalculationService.berechneUberstromelemente(model.map.raum.raume.find { it.position == raumIndex })
        } else {
            if (DEBUG)
                println "raumUberstromelementeLuftmengeBerechnen: Kein Raum ausgewählt, es wird nichts berechnet"
        }
    }

    /**
     * In einer der Raum-Tabellen wurde die Auswahl durch den Benutzer geändert:
     * die Auswahl aller anderen Tabellen entsprechend anpassen.
     * @param evt javax.swing.event.ListSelectionEvent
     */
    def raumInTabelleGewahlt = { evt, table ->
        if (!evt.isAdjusting && evt.firstIndex > -1 && evt.lastIndex > -1) {
            // source = javax.swing.ListSelectionModel
            def selectedRow = evt.source.leadSelectionIndex
            //if (DEBUG) println "raumInTabelleGewahlt: ${evt.dump()}, selectedRow=${selectedRow}"
            onRaumInTabelleWahlen(selectedRow, table)
        }
    }

    /**
     * Execute code with all "Raum"-tables...
     */
    def withAllRaumTables = { closure ->
        view.with {
            [raumTabelle, raumVsZuAbluftventileTabelle, raumVsUberstromelementeTabelle].each { t ->
                closure(t)
            }
        }
    }

    /**
     * Einen bestimmten Raum in allen Raum-Tabellen markieren.
     */
    def onRaumInTabelleWahlen = { row, table = null ->
        doLater {
            //if (DEBUG) println "onRaumInTabelleWahlen: row=${row}"
            row = GH.checkRow(row, view.raumTabelle)
            if (row > -1) {
                // Raum in Raumdaten-Tabelle, Raumvolumenströme-Zu/Abluftventile-Tabelle,
                // Raumvolumenströme-Überströmelemente-Tabelle markieren
                withAllRaumTables { t ->
                    try {
                        GH.withDisabledListSelectionListeners t, {-> changeSelection(row, 0, false, false) }
                    } catch (e) {
                        // java.lang.IndexOutOfBoundsException: bitIndex < 0: -1
                    }
                }
                // Aktuellen Raum in Metadaten setzen
                model.meta.gewahlterRaum.putAll(model.map.raum.raume[row])
                // TODO Warum wird das hier gemacht??? WacCalculationService.berechneRaumnummer ist zuständig!
                ////model.meta.gewahlterRaum.raumNummer = row + 1
            } else {
                // Remove selection in all tables
                withAllRaumTables { t ->
                    t.clearSelection()
                }
                // Aktuell gewählten Raum in Metadaten leeren
                model.meta.gewahlterRaum.clear()
            }
        }
    }

    /**
     * Raumvolumenströme - Zentralgerät: manuelle Auswahl des Zentralgeräts.
     */
    def zentralgeratManuellGewahlt = {
        ////publishEvent "ZentralgeratGewahlt", [view.raumVsZentralgerat.selectedItem]
        doLater {
            // Merken, dass das Zentralgerät manuell ausgewählt wurde
            // -> keine automatische Auswahl des Zentralgeräts mehr durchführen
            if (DEBUG)
                println "zentralgeratManuellGewahlt: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
            model.map.anlage.zentralgeratManuell = true
            if (DEBUG)
                println "zentralgeratManuellGewahlt: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
            // Zentralgerät aus View in Model übernehmen
            model.map.anlage.zentralgerat = view.raumVsZentralgerat.selectedItem
            // Hole Volumenströme des Zentralgeräts
            model.meta.volumenstromZentralgerat =
                wacModelService.getVolumenstromFurZentralgerat(model.map.anlage.zentralgerat)
            // Aussenluftvolumenströme neu berechnen
            wacCalculationService.aussenluftVs(model.map)
            // Neue Auswahl setzen
            zentralgeratAktualisieren()
        }
    }

    /**
     * Aktualisiere Zentralgerät und Volumenstrom in allen Comboboxen
     */
    void zentralgeratAktualisieren() {
        if (DEBUG)
            println "zentralgeratAktualisieren: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
        doLater {
            /*
               // Füge Volumenströme des aktuellen Zentralgeräts in Combobox hinzu
               // Raumvolumenströme
               view.raumVsVolumenstrom.removeAllItems()
               // Akustikberechnung Zu-/Abluft
               view.akustikZuluftPegel.removeAllItems()
               view.akustikAbluftPegel.removeAllItems()
               model.meta.volumenstromZentralgerat.each {
                   // Raumvolumenströme
                   view.raumVsVolumenstrom.addItem(it)
                   // Akustikberechnung Zu-/Abluft
                   view.akustikZuluftPegel.addItem(it)
                   view.akustikAbluftPegel.addItem(it)
               }
               // Akustikberechnung Zu-/Abluft
               view.akustikZuluftZuluftstutzenZentralgerat.selectedItem =
                   view.akustikAbluftAbluftstutzenZentralgerat.selectedItem =
                   model.meta.volumenstromZentralgerat
               */
            // Aktualisiere Zentralgerät
            GH.withDisabledActionListeners view.raumVsZentralgerat, {
                // Raumvolumenströme
                model.map.anlage.zentralgerat =
                view.raumVsZentralgerat.selectedItem =
                    model.map.anlage.zentralgerat
                // Akustik Zu-/Abluft
                view.akustikZuluftZuluftstutzenZentralgerat.selectedItem =
                view.akustikAbluftAbluftstutzenZentralgerat.selectedItem =
                    model.map.anlage.zentralgerat
            }
            // Aktualisiere Volumenstrom
            GH.withDisabledActionListeners view.raumVsVolumenstrom, {
                // Hole Volumenströme des Zentralgeräts
                def volumenstromZentralgerat =
                    wacModelService.getVolumenstromFurZentralgerat(view.raumVsZentralgerat.selectedItem)
                // 5er-Schritte
                model.meta.volumenstromZentralgerat = []
                def minVsZentralgerat = volumenstromZentralgerat[0] as Integer
                def maxVsZentralgerat = volumenstromZentralgerat.toList().last() as Integer
                (minVsZentralgerat..maxVsZentralgerat).step 5, { model.meta.volumenstromZentralgerat << it }
                // Füge Volumenströme in Comboboxen hinzu

                view.raumVsVolumenstrom.removeAllItems()
                // Akustik
                view.akustikZuluftPegel.removeAllItems()
                view.akustikAbluftPegel.removeAllItems()

                model.meta.volumenstromZentralgerat.each {
                    // Raumvolumenströme
                    view.raumVsVolumenstrom.addItem(it)
                    // Akustikberechnung
                    view.akustikZuluftPegel.addItem(it)
                    view.akustikAbluftPegel.addItem(it)
                }

                // Selektiere errechneten Volumenstrom
                def roundedVs = wacCalculationService.round5(model.map.anlage.volumenstromZentralgerat)
                if (DEBUG)
                    println "zentralgeratAktualisieren: model.map.anlage.volumenstromZentralgerat=${model.map.anlage.volumenstromZentralgerat} roundedVs=${roundedVs}"
                def foundVs = model.meta.volumenstromZentralgerat.find { it.toInteger() == roundedVs }
                // Wenn gerundeter Volumenstrom nicht gefunden wurde, setze Minimum des Zentralgeräts
                if (!foundVs) {
                    foundVs = model.meta.volumenstromZentralgerat[0]
                }
                //println "view.akustikAbluftPegel.selectedItem -> ${view.akustikAbluftPegel.selectedItem}"
                model.map.anlage.volumenstromZentralgerat = foundVs
                view.raumVsVolumenstrom.selectedItem = foundVs
                view.akustikZuluftPegel.selectedItem = foundVs
                view.akustikAbluftPegel.selectedItem = foundVs
            }
        }
    }

    /**
     * Raumvolumenströme - Zentralgerät: automatische Aktualisierung das Zentralgeräts.
     * Darf nur dann durchgeführt werden, wenn der Benutzer das Zentralgerät noch nicht selbst
     * verändert hat!
     */
    def onZentralgeratAktualisieren = {
        doLater {
            if (DEBUG)
                println "onZentralgeratAktualisieren: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
            if (!model.map.anlage.zentralgeratManuell) {
                // Berechne Zentralgerät und Volumenstrom
                def (zentralgerat, nl) = wacCalculationService.berechneZentralgerat(model.map)
                model.map.anlage.zentralgerat = zentralgerat
                model.map.anlage.volumenstromZentralgerat = wacCalculationService.round5(nl)
                if (DEBUG)
                    println "onZentralgeratAktualisieren: zentralgerat=${model.map.anlage.zentralgerat}, nl=${nl}/${model.map.anlage.volumenstromZentralgerat}"
                zentralgeratAktualisieren()
            }
        }
    }

    /**
     * Raumvolumenströme - Volumenstrom des Zentralgeräts.
     */
    def volumenstromZentralgeratManuellGewahlt = {
        // Merken, dass das Zentralgerät manuell ausgewählt wurde
        // -> keine automatische Auswahl des Zentralgeräts mehr durchführen
        if (DEBUG)
            println "zentralgeratManuellGewahlt: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
        model.map.anlage.zentralgeratManuell = true
        if (DEBUG)
            println "zentralgeratManuellGewahlt: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
        // Aus der View im Projekt-Model speichern
        model.map.anlage.volumenstromZentralgerat = view.raumVsVolumenstrom.selectedItem?.toInteger()
        zentralgeratAktualisieren()
    }

    /**
     * Druckverlustberechnung - Kanalnetz - Hinzufügen.
     */
    def dvbKanalnetzHinzufugen = {
        def kanalnetz = GH.getValuesFromView(view, "dvbKanalnetz")
        ////publishEvent "DvbKanalnetzHinzufugen", [kanalnetz, view]
        doLater {
            // Map values from GUI
            def k = [
                    luftart: kanalnetz.dvbKanalnetzLuftart,
                    teilstrecke: kanalnetz.dvbKanalnetzNrTeilstrecke?.toInteger(),
                    luftVs: kanalnetz.dvbKanalnetzLuftmenge?.toDouble2(),
                    kanalbezeichnung: kanalnetz.dvbKanalnetzKanalbezeichnung,
                    lange: kanalnetz.dvbKanalnetzLange?.toDouble2(),
                    position: model.map.dvb.kanalnetz.size(),
                    gesamtwiderstandszahl: 0.0d
            ] as ObservableMap
            // Berechne die Teilstrecke
            k = wacCalculationService.berechneTeilstrecke(k)
            // Add values to model
            model.addDvbKanalnetz(k, view)
            // Add PropertyChangeListener to our model.map
            GH.addMapPropertyChangeListener("map.dvb.kanalnetz", k)
            //
            dvbKanalnetzGeandert(k.position)
        }
        // Reset values in view
        view.dvbKanalnetzNrTeilstrecke.text = ""
        view.dvbKanalnetzLuftmenge.text = ""
        view.dvbKanalnetzLange.text = ""
    }

    /**
     * Druckverlustberechnung - Kanalnetz wurde hinzugefügt: Eintrag in der Tabelle anwählen.
     */
    def onDvbKanalnetzInTabelleWahlen = { kanalnetzIndex ->
        doLater {
            view.dvbKanalnetzTabelle.changeSelection(kanalnetzIndex, 0, false, false)
        }
    }

    /**
     * Druckverlustberechnung - Kanalnetz - Geändert.
     */
    def dvbKanalnetzGeandert = { kanalnetzIndex ->
        doLater {
            // Berechne die Teilstrecke
            if (DEBUG)
                println "kanalnetzIndex -> ${kanalnetzIndex}, kanalnetz -> ${model.map.dvb.kanalnetz.dump()}"
            model.map.dvb.kanalnetz[kanalnetzIndex] =
                wacCalculationService.berechneTeilstrecke(model.map.dvb.kanalnetz[kanalnetzIndex])
            //
            !loadMode && onDvbKanalnetzInTabelleWahlen(kanalnetzIndex)
        }
    }

    /**
     * Druckverlustberechnung - Kanalnetz - Entfernen.
     */
    def dvbKanalnetzEntfernen = {
        ///publishEvent "DvbKanalnetzEntfernen", [view.dvbKanalnetzTabelle.selectedRow]
        def kanalnetzIndex = view.dvbKanalnetzTabelle.selectedRow
        doLater {
            //println "onDvbKanalnetzEntfernen: kanalnetzIndex=${kanalnetzIndex}"
            // Zeile aus Model entfernen
            model.removeDvbKanalnetz(kanalnetzIndex)
        }
    }

    /**
     * Druckverlustberechnung - Kanalnetz - Widerstandsbeiwerte.
     */
    def widerstandsbeiwerteBearbeiten = {
        // Welche Teilstrecke ist ausgewählt? Index bestimmen
        def index = view.dvbKanalnetzTabelle.selectedRow
        if (DEBUG)
            println "widerstandsbeiwerteBearbeiten: index=${index}"
        // Setze Index des gewählten Kanalnetzes in Metadaten
        model.meta.dvbKanalnetzGewahlt = index
        // TableModel für WBW hinzufügen, wenn noch nicht vorhanden
        doLater {
            model.addWbwTableModel(index)
        }
        // WBW summieren, damit das Label im Dialog (bind model.meta.summeAktuelleWBW) den richtigen Wert anzeigt
        wbwSummieren()
        // Show dialog
        // Ist zentriert!
        wbwDialog = GH.createDialog(builder, WbwView, [title: "Widerstandsbeiwerte", size: [750, 650], locationRelativeTo: app.windowManager.findWindow('wac2Frame')])
        wbwDialog.show()
        if (DEBUG)
            println "widerstandsbeiwerteBearbeiten: dialog '${dialog.title}'"
    }

    /**
     * Ein Widerstandsbeiwert wurde in der Tabelle gewählt:
     * Bild anzeigen und Daten in die Eingabemaske kopieren.
     */
    def wbwInTabelleGewahlt = { evt = null ->
        // Welche Teilstrecke ist ausgewählt? Index bestimmen
        def index = model.meta.dvbKanalnetzGewahlt //view.dvbKanalnetzTabelle.selectedRow
        // Welche Zeile ist gewählt --> welcher Widerstand?
        def wbwIndex = view.wbwTabelle.selectedRow
        if (DEBUG)
            println "wbwInTabelleGewahlt: index=${index} wbwIndex=${wbwIndex}"

        def wbw = model.tableModels.wbw[index][wbwIndex]
        javax.swing.ImageIcon image = null
        // Neu generierte WBWs haben kein Image. Exception abfangen.
        try {
            def url = Wac2Resource.getWiderstandURL(wbw.id)
            image = new javax.swing.ImageIcon(url)
        } catch (NullPointerException e) { }
        // Image und Text setzen
        if (image) {
            view.wbwBild.text = ""
            view.wbwBild.icon = image
        } else {
            view.wbwBild.text = "-- kein Bild --"
            view.wbwBild.icon = null
        }
        // Daten in Eingabemaske setzen
        view.wbwBezeichnung.text = wbw.name
        view.wbwWert.text = wbw.widerstandsbeiwert.toString2()
        view.wbwAnzahl.text = wbw.anzahl
    }

    /**
     * Widerstandsbeiwerte, Übernehmen-Button.
     */
    def wbwSaveButton = {
        // Welche Teilstrecke ist ausgewählt? Index bestimmen
        def index = model.meta.dvbKanalnetzGewahlt //view.dvbKanalnetzTabelle.selectedRow
        if (DEBUG)
            println "wbwSaveButton: index=${index}"
        def wbw = model.tableModels.wbw[index]
        // Daten aus der Eingabemaske holen
        def dialogWbw = [
                name: view.wbwBezeichnung.text,
                widerstandsbeiwert: view.wbwWert.text?.toDouble2() ?: 0.0d,
                anzahl: view.wbwAnzahl.text?.toString() ?: "0"
        ]
        // Wenn WBW noch nicht vorhanden, dann hinzufügen
        def editedWbw = wbw.find { it.name == dialogWbw.name }
        if (!editedWbw) {
            if (DEBUG)
                println "wbwSaveButton: adding ${dialogWbw.dump()} to model"
            wbw << dialogWbw
        } else {
            editedWbw = wbw[view.wbwTabelle.selectedRow]
            editedWbw.widerstandsbeiwert = dialogWbw.widerstandsbeiwert
            editedWbw.anzahl = dialogWbw.anzahl
        }
        wbwSummieren()
    }

    /**
     * Widerstandsbeiwerte: eingegebene Werte aufsummieren.
     */
    def wbwSummieren = {
        doLater {
            // Welche Teilstrecke ist ausgewählt? Index bestimmen
            def index = model.meta.dvbKanalnetzGewahlt //view.dvbKanalnetzTabelle.selectedRow
            if (DEBUG)
                println "wbwSummieren: index=${index}"
            def wbw = model.tableModels.wbw[index]
            // Summiere WBW
            def map = model.map.dvb.kanalnetz[index]
            model.meta.summeAktuelleWBW =
            map.gesamtwiderstandszahl =
                wbw?.sum {
                    it.anzahl.toDouble2() * it.widerstandsbeiwert.toDouble2()
                }
            if (DEBUG)
                println "wbwSummieren: map.gesamtwiderstandszahl=${map.gesamtwiderstandszahl}"
        }
    }

    /**
     * Widerstandsbeiwerte, Dialog mit OK geschlossen.
     */
    def wbwOkButton = {
        // Welche Teilstrecke ist ausgewählt? Index bestimmen
        def index = model.meta.dvbKanalnetzGewahlt //view.dvbKanalnetzTabelle.selectedRow
        if (DEBUG)
            println "wbwOkButton: index=${index}"
        def wbw = model.tableModels.wbw[index]
        def map = model.map.dvb.kanalnetz[index]
        // Berechne Teilstrecke
        wbwSummieren()
        wacCalculationService.berechneTeilstrecke(map)
        // Resync model
        //model.resyncDvbKanalnetzTableModels()
        // Close dialog
        wbwDialog.dispose()
    }

    /**
     *
     */
    def wbwCancelButton = {
        // Close dialog
        wbwDialog.dispose()
    }

    /**
     * Druckverlustberechnung - Ventileinstellung - Hinzufügen.
     */
    def dvbVentileinstellungHinzufugen = {
        def ventileinstellung = GH.getValuesFromView(view, "dvbVentileinstellung")
        doLater {
            // Map values from GUI
            def v = [
                    luftart: ventileinstellung.dvbVentileinstellungLuftart,
                    raum: ventileinstellung.dvbVentileinstellungRaum,
                    teilstrecken: ventileinstellung.dvbVentileinstellungTeilstrecken,
                    ventilbezeichnung: ventileinstellung.dvbVentileinstellungVentilbezeichnung,
                    position: model.map.dvb.ventileinstellung.size() ?: 0
            ] as ObservableMap
            model.addDvbVentileinstellung(v, view)
            def index = v.position
            // Add PropertyChangeListener to our model.map
            GH.addMapPropertyChangeListener("map.dvb.ventileinstellung",
                    model.map.dvb.ventileinstellung[index])
            //
            dvbVentileinstellungGeandert(index)
        }
    }

    /**
     * Druckverlustberechnung - Ventileinstellung wurde hinzugefügt: Eintrag in der Tabelle anwählen.
     */
    def onDvbVentileinstellungInTabelleWahlen = { ventileinstellungIndex ->
        doLater {
            view.dvbVentileinstellungTabelle.changeSelection(ventileinstellungIndex, 0, false, false)
            // Wurde keine Einstellung gefunden, Benutzer informieren
            model.map.dvb.ventileinstellung.each { ve ->
                if (ve.einstellung == 0) {
                    def infoMsg = "Keine Einstellung für Ventil ${ve.ventilbezeichnung} gefunden! Bitte prüfen Sie die Zeile#${ve.position}."
                    app.controllers["Dialog"].showInformDialog(infoMsg as String)
                    println infoMsg
                }
            }
        }
    }

    /**
     * Druckverlustberechnung - Ventileinstellung - Geändert.
     */
    def dvbVentileinstellungGeandert = { ventileinstellungIndex ->
        doLater {
            wacCalculationService.berechneVentileinstellung(model.map)
            onDvbVentileinstellungInTabelleWahlen(ventileinstellungIndex)
        }
    }

    /**
     * Druckverlustberechnung - Ventileinstellung - Entfernen.
     */
    def dvbVentileinstellungEntfernen = {
        doLater {
            // Zeile aus Model entfernen
            def ventileinstellungIndex = view.dvbVentileinstellungTabelle.selectedRow
            model.removeDvbVentileinstellung(ventileinstellungIndex)
        }
    }

    /**
     * Druckverlustberechnung - Ventileinstellung - Teilstrecke wählen.
     */
    def dvbVentileinstellungTeilstreckeDialog = {
        teilstreckenDialog = GH.createDialog(builder, TeilstreckenView, [title: "Teilstrecken", pack: true])
        teilstreckenDialog = GH.centerDialog(app.views['wac2'], teilstreckenDialog)

        def listModel = view.teilstreckenVerfugbareListe.model
        model.map.dvb.kanalnetz.each { listModel.addElement(it.teilstrecke) }

        if (DEBUG)
            println "dvbVentileinstellungTeilstreckeDialog: listModel -> ${listModel}"

        view.teilstreckenVerfugbareListe.setModel(listModel)

        teilstreckenDialog.show()

        if (DEBUG)
            println "TeilstreckenAuswahlen: dialog '${dialog.title}' closed: dialog=${dialog.dump()}"
    }

    /**
     * Teilstrecke von ausgewählte Teilstrecke nach verfügbare Teilstrecke verschieben
     */
    def teilstreckenNachVerfugbarVerschieben = {
        doLater {
            // get selected items
            def selectedValues = view.teilstreckenAusgewahlteListe.selectedValues as String[]
            if (DEBUG)
                println "teilstreckenNachVerfugbarVerschieben: selectedIndices -> ${selectedValues}"
            // add to verfugbare list and remove from ausgewahlte list
            def vListModel = view.teilstreckenVerfugbareListe.model
            selectedValues.each { vListModel.addElement(it) }
            view.teilstreckenVerfugbareListe.setModel(vListModel)

            // remove from ausgewahlte list
            def aListModel = view.teilstreckenAusgewahlteListe.model
            selectedValues.each { aListModel.removeElement(it) }
            view.teilstreckenAusgewahlteListe.setModel(aListModel)

            def listArray = aListModel.toArray()
            def newText = listArray.collect { it }.join(';')
            view.teilstreckenAuswahl.setText(newText)
        }
    }

    /**
     * Teilstrecke von verfügbare Teilstrecke nach ausgewählte Teilstrecke verschieben
     */
    def teilstreckenNachAusgewahlteVerschieben = {
        doLater {
            // get selected items
            def selectedValues = view.teilstreckenVerfugbareListe.selectedValues as String[]
            if (DEBUG)
                println "teilstreckenNachAusgewahlteVerschieben: selectedValues -> ${selectedValues}"
            // add to ausgewahlte list and remove from verfugbare list
            def aListModel = view.teilstreckenAusgewahlteListe.model
            selectedValues.each { aListModel.addElement(it) }
            view.teilstreckenAusgewahlteListe.setModel(aListModel)

            // remove from verfugbare list
            def vListModel = view.teilstreckenVerfugbareListe.model
            selectedValues.each { vListModel.removeElement(it) }
            view.teilstreckenVerfugbareListe.setModel(vListModel)

            // set text
            def listArray = aListModel.toArray()
            def newText = listArray.collect { it }.join(';')
            view.teilstreckenAuswahl.setText(newText)
        }
    }

    /**
     * Teilstrecken, Dialog mit OK geschlossen.
     */
    def teilstreckenOkButton = {
        // save values...
        view.dvbVentileinstellungTeilstrecken.setText(view.teilstreckenAuswahl.text)
        teilstreckenDialog.dispose()
    }

    /**
     * Teilstrecken Dialog Abbrechen - nichts speichern!
     */
    def teilstreckenCancelButton = {
        // Close dialog
        teilstreckenDialog.dispose()
    }

    /**
     * Akustikberechnung - Zentralgerät geändert.
     */
    def aktualisiereAkustikVolumenstrom = { tabname ->
        def zg = view."akustik${tabname}${tabname}stutzenZentralgerat"
        def p = view."akustik${tabname}Pegel"
        // Aktualisiere Volumenstrom
        GH.withDisabledActionListeners p, {
            p.removeAllItems()
            // Hole Volumenströme des Zentralgeräts und füge diese in Combobox hinzu
            // TODO 5er-Schritte
            wacModelService.getVolumenstromFurZentralgerat(zg.selectedItem).each {
                p.addItem(it)
            }
        }
    }

    /**
     * Akustikberechnung.
     */
    void berechneAkustik(tabname) {
        def m = model.map.akustik."${tabname.toLowerCase()}"
        // Konvertiere Wert TextField, ComboBox in Integer, default ist 0
        // Eingabe einer 0 im TextField gibt ""???
        def getInt = { comp ->
            def x
            if (comp instanceof javax.swing.JTextField) {
                x = comp.text
            } else if (comp instanceof javax.swing.JComboBox) {
                x = comp.selectedItem
            }
            if (x == "")
                x = null
            x?.toInteger() ?: 0
        }
        // Input parameter map
        def input = [
                zentralgerat: view."akustik${tabname}${tabname}stutzenZentralgerat".selectedItem,
                volumenstrom: getInt(view."akustik${tabname}Pegel"),
                slpErhohungKanalnetz: getInt(view."akustik${tabname}Kanalnetz"),
                slpErhohungFilter: getInt(view."akustik${tabname}Filter"),
                hauptschalldampfer1: view."akustik${tabname}1Hauptschalldampfer".selectedItem,
                hauptschalldampfer2: view."akustik${tabname}2Hauptschalldampfer".selectedItem,
                umlenkungen: getInt(view."akustik${tabname}AnzahlUmlenkungen90GradStck"),
                luftverteilerkasten: getInt(view."akustik${tabname}LuftverteilerkastenStck"),
                langsdampfungKanal: view."akustik${tabname}LangsdampfungKanal".selectedItem,
                langsdampfungKanalLfdmMeter: getInt(view."akustik${tabname}LangsdampfungKanalLfdmMeter"),
                schalldampferVentil: view."akustik${tabname}SchalldampferVentil".selectedItem,
                einfugungsdammwert: view."akustik${tabname}EinfugungsdammwertLuftdurchlass".selectedItem,
                raumabsorption: view."akustik${tabname}Raumabsorption".selectedItem
        ]
        // Nur berechnen, wenn Zentralgerät gesetzt
        if (input.zentralgerat) {
            // Volumenstrom gesetzt?
            if (input.volumenstrom == 0) {
                if (DEBUG)
                    println "berechneAkustik: Kein Volumenstrom (${input.volumenstrom}), setze auf 50"
                input.volumenstrom = 50
                view."akustik${tabname}Pegel".selectedItem = model.meta.volumenstromZentralgerat[0]
            }
            // Berechne Akustik
            wacCalculationService.berechneAkustik(tabname, input, model.map)
            // Zentralgerät, Überschrift
            view."akustik${tabname}${tabname}Zentralgerat".text = input.zentralgerat
            // db(A)
            m.dbA = view."akustik${tabname}dbA".text =
                wacModelService.getDezibelZentralgerat(input.zentralgerat, input.volumenstrom, tabname).toString2()
            // Mittlerer Schalldruckpegel
            view."akustik${tabname}MittlererSchalldruckpegel".text =
                m.mittlererSchalldruckpegel?.toString2() ?: 0d.toString2()
            // Resync table
            model.resyncAkustikTableModels(view)
        }
    }

    /**
     *
     */
    void generiereVerlegeplan() {

        try {

            def title = getProjektTitel() as String

            // Save document in user home dir
            //def userDir = System.getProperty("user.home")
            //userDir = userDir + "/" + title + "_" + System.currentTimeMillis() + ".pdf"
            def verlegeplanFilename = model.wpxFilename - '.wpx' + ' Verlegeplan.pdf'

            PdfCreator pdfCreator = new PdfCreator()
            // Create a new pdf document
            pdfCreator.createDocument(verlegeplanFilename)

            def logourl = Wac2Resource.getPdfLogo()
            if (DEBUG)
                println "logourl -> ${logourl.dump()}"
            pdfCreator.addLogo(logourl)
            // Add title to document
            pdfCreator.addTitle(title)
            // create table with relative column width
            pdfCreator.createTable([2f, 1f, 3f, 1f] as float[])

            if (DEBUG)
                println "adding zentralgerät... "
            // Zentralgerät
            def zentralgerat = "Zentralgerät: ${model.map.anlage.zentralgerat}" as String
            pdfCreator.addArtikelToDocument(zentralgerat)
            if (DEBUG)
                println "added zentralgerät... "
            // Add empty line.
            pdfCreator.addArtikelToDocument("  ")
            if (DEBUG)
                println "adding empty artikel"

            model.map.raum.raume.each { r ->
                erzeugeRaumVerlegeplanAbluft(r, pdfCreator)
            }
            model.map.raum.raume.each { r ->
                erzeugeRaumVerlegeplanZuluft(r, pdfCreator)
            }
            model.map.raum.raume.each { r ->
                erzeugeRaumVerlegeplanUberstrom(r, pdfCreator)
            }
            erzeugeDruckverlustVerlegeplan(model.map.dvb, pdfCreator)
            erzeugeSchalldampferVerlegeplan(model.map.akustik.zuluft, "zuluft", pdfCreator)
            erzeugeSchalldampferVerlegeplan(model.map.akustik.abluft, "abluft", pdfCreator)

            // Add table to the document
            pdfCreator.addTable()
            // Close the pdf document
            pdfCreator.closeDocument()
            /* Dialog nicht anzeigen, einfach PDF öffnen
            def successMsg = "Verlegeplan '${verlegeplanFilename}' erfolgreich generiert"
            app.controllers["Dialog"].showInformDialog(successMsg as String)
            */
            java.io.File sf = new java.io.File(verlegeplanFilename)
            if (sf.exists()) {
                java.awt.Desktop.desktop.open(sf)
            }
        } catch (e) {
            println "Error generating document: ${e.dump()}"

            def errorMsg = "Beim generieren des Verlegeplans ist ein Fehler aufgetreten"
            app.controllers["Dialog"].showErrorDialog(errorMsg as String)
        }

    }

    /**
     *
     * @param map
     * @param pdfCreator
     */
    void erzeugeRaumVerlegeplanAbluft(map, pdfCreator) {

        if (map.raumBezeichnungAbluftventile) {
            if (DEBUG)
                println "adding Abluft... ${map.dump()}"
            pdfCreator.addArtikel(map.raumBezeichnung, "Abluft", map.raumBezeichnungAbluftventile, map.raumAnzahlAbluftventile)
        }
    }

    /**
     *
     * @param map
     * @param pdfCreator
     */
    void erzeugeRaumVerlegeplanZuluft(map, pdfCreator) {
        if (map.raumBezeichnungZuluftventile) {
            if (DEBUG)
                println "adding Zuluft... ${map.dump()}"
            pdfCreator.addArtikel(map.raumBezeichnung, "Zuluft", map.raumBezeichnungZuluftventile, map.raumAnzahlZuluftventile)
        }
    }

    /**
     *
     * @param map
     * @param pdfCreator
     */
    void erzeugeRaumVerlegeplanUberstrom(map, pdfCreator) {
        if (map.raumAnzahlUberstromVentile && map.raumAnzahlUberstromVentile > 0) {
            if (DEBUG)
                println "adding Überström... ${map.dump()}"
            pdfCreator.addArtikel("", "Überström", map.raumUberstromElement, map.raumAnzahlUberstromVentile)
        }
    }

    /**
     *
     * @param dvb
     * @param pdfCreator
     */
    void erzeugeDruckverlustVerlegeplan(dvb, pdfCreator) {
        dvb.kanalnetz.each {
            if (it.kanalbezeichnung) {
                if (DEBUG)
                    println "adding kanalnetz..."
                pdfCreator.addArtikel("", "", it.kanalbezeichnung, it.lange)
            }
        }
    }

    /**
     *
     * @param akusik
     * @param luftart
     * @param pdfCreator
     */
    void erzeugeSchalldampferVerlegeplan(akustik, luftart, pdfCreator) {
        if (akustik.hauptschalldampfer1) {
            if (DEBUG)
                println "adding schalldampfer 1..."
            pdfCreator.addArtikel("", "", akustik.hauptschalldampfer1, 1)
        }
        if (akustik.hauptschalldampfer2) {
            if (DEBUG)
                println "adding schalldampfer 2..."
            pdfCreator.addArtikel("", "", akustik.hauptschalldampfer2, 1)
        }
    }
}

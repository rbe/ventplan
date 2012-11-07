/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/16/12 10:35 AM
 */

package com.ventplan.desktop

import com.bensmann.griffon.GriffonHelper as GH

import groovy.io.FileType

import javax.swing.JDialog
import javax.swing.JFileChooser

/**
 *
 */
class VentplanController {

    //<editor-fold desc="Instance fields">
    VpxModelService vpxModelService

    def model
    def view
    def builder

    def aboutDialog
    def checkUpdateDialog
    def projektSuchenDialog

    JDialog neuesProjektWizardDialog

    /**
     * Flag zum Abbrechen des Schliessen Vorgangs
     */
    private boolean abortClosing = false

    /**
     * Zähler für erstellte/geladene Projekte. Wird als "unique id" verwendet.
     * Siehe generateMVCId().
     */
    def static projektCounter = 1

    /**
     * User's settings.
     private def prefs = Preferences.userNodeForPackage(VentplanController)
     */

    def static mruFileManager = MRUFileManager.instance

    /**
     *
     private def wacwsUrl = GH.getWacwsUrl()
     */

    /**
     * WAC-192 Saving file path of search folder
     */
    def static projektSuchenPrefs = ProjektSuchenPrefHelper.instance
    //</editor-fold>

    //<editor-fold desc="MVC">
    /**
     * Get access to all components of a MVC group by its ID.
     */
    def getMVCGroup(mvcId) {
        [
                mvcId: mvcId,
                model: app.groups[mvcId]?.model,
                view: app.groups[mvcId]?.view,
                controller: app.groups[mvcId]?.controller
        ]
    }

    /**
     * Hole MVC Group des aktiven Projekts.
     */
    def getMVCGroupAktivesProjekt() {
        def projekt = model.aktivesProjekt
        if (!projekt) {
            throw new IllegalStateException('Missing MVC ID')
        }
        getMVCGroup(projekt)
    }

    String generateMVCId() {
        def c = VentplanController.projektCounter++
        def t = "Projekt ${c.toString()}".toString()
        t
    }

    /**
     * Ein Projekt aktivieren -- MVC ID an VentplanModel übergeben.
     */
    def projektAktivieren = { mvcId ->
        // Anderes Projekt wurde aktiviert?
        if (mvcId && mvcId != model?.aktivesProjekt) {
            // MVC ID merken
            model.aktivesProjekt = mvcId
            // Dirty-flag aus Projekt-Model übernehmen
            try {
                def mvcGroup = getMVCGroup(mvcId)
                model.aktivesProjektGeandert = mvcGroup.model?.map?.dirty
            } catch (e) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Ein Projekt aktivieren -- MVC ID an VentplanModel übergeben.
     */
    def projektIndexAktivieren = { index ->
        if (index > -1) {
            projektAktivieren(model.projekte[index])
        }
    }
    //</editor-fold>

    //<editor-fold desc="Exiting the application">
    /**
     * Schliessen? Alle Projekte fragen, ob ungesicherte Daten existieren.
     */
    boolean canClose() {
        model.projekte.inject(true) { o, n ->
            def c = getMVCGroup(n).controller
            o &= c.canClose()
            o // Added as error before was "Assignment is unused"
        }
    }

    def exitApplication = { evt = null ->
        canExitApplication(evt)
    }

    /**
     * WAC-8
     */
    boolean canExitApplication(evt) {
        boolean proceed = false
        // Ask if we can close
        def canClose = canClose()
        if (canClose) {
            def choice = app.controllers['Dialog'].showApplicationOnlyCloseDialog()
            switch (choice) {
                case 0:
                    alleProjekteSpeichern(evt)
                    proceed = !abortClosing
                    break
                case 1: // Cancel: do nothing...
                    proceed = false
                    break
            }
        } else {
            // Show dialog: ask user for save all, cancel, quit
            def choice = app.controllers['Dialog'].showApplicationSaveAndCloseDialog()
            switch (choice) {
                case 0:
                    alleProjekteSpeichern(evt)
                    proceed = !abortClosing
                    break
                case 1: // Cancel: do nothing...
                    proceed = false
                    break
                case 2:
                    proceed = true
                    break
            }
        }
        mruFileManager.save()
        return proceed
    }
    //</editor-fold>

    /**
     * Disable last tab of JTabbedPane 'projektTabGroup'.
     def disableLastProjektTab = {view.projektTabGroup.setEnabledAt(view.projektTabGroup.tabCount - 1, false)}*/

    /**
     * Enable last tab of JTabbedPane 'projektTabGroup'.
     def enableLastProjektTab = {view.projektTabGroup.setEnabledAt(view.projektTabGroup.tabCount - 1, true)}*/

    //<editor-fold desc="Sonstige Dialoge">
    /**
     * WAC-167 Info-Menü mit Über-Dialog
     * Dialog mit Logo und Versionsnummer
     */
    def aboutDialogOeffnen = { evt = null ->
        aboutDialog = GH.createDialog(builder, AboutView, [title: 'Über', resizable: false, pack: true])
        aboutDialog = GH.centerDialog(app.views['MainFrame'], aboutDialog)
        aboutDialog.show()
    }

    /**
     * Dialog "Aktualisierungen prüfen" öffnen.
     */
    def checkUpdateDialogOeffnen = { evt = null ->
        checkUpdateDialog = GH.createDialog(builder, CheckUpdateView, [title: 'Aktualisierung von Ventplan', resizable: false, pack: true])
        checkUpdateDialog = GH.centerDialog(app.views['MainFrame'], checkUpdateDialog)
        checkUpdateDialog.show()
    }
    //</editor-fold>

    //<editor-fold desc="Projekt öffnen">
    /**
     * Projekt öffnen: zeige FileChooser, lese XML, erstelle eine MVC Group und übertrage die Werte
     * aus dem XML in das ProjektModel.
     */
    def projektOffnen = { evt = null ->
        // WAC-246 Choose Ventplan directory
        view.vpxFileChooserWindow.currentDirectory = getVentplanDir()
        def openResult = view.vpxFileChooserWindow.showOpenDialog(view.ventplanFrame)
        if (JFileChooser.APPROVE_OPTION == openResult) {
            def file = view.vpxFileChooserWindow.selectedFile
            projektOffnenClosure(file)
        }
    }

    /**
     * Öffnet das zuletzt geladene Projekt aus MRUFileManager.
     */
    def zuletztGeoffnetesProjekt = { evt = null ->
        try {
            def file = evt.getActionCommand()
            projektOffnenClosure(file)
        }
        catch (Exception e) {
            println "VentplanController#zuletztGeoffnetesProjekt: ERROR: Could not load file ${file} caused by: ${e.dump()}"
        }
    }

    /**
     * Öffnet das Projekt aus der angegebenen Datei.
     * Die zu ladende Datei wird in den MRUFileManager als zuletzt geöffnetes Projekt gespeichert.
     * Alle Werte werden neu berechnet.
     */
    def projektOffnenClosure = { file, resetFilename = false ->
        jxwithWorker(start: true) {
            // initialize the worker
            onInit {
                model.statusProgressBarIndeterminate = true
                model.statusBarText = 'Phase 1/3: Projektdatei wählen ...'
            }
            // do the task
            work {
                // Add file to MRU list
                addRecentlyOpenedFile(file)
                // ... and reset it in FileChooser
                view.vpxFileChooserWindow.selectedFile = null
                // Load data; start thread
                model.statusBarText = 'Phase 2/3: Lade Daten ...'
                def projektModel //, projektView, projektController
                // May return null due to org.xml.sax.SAXParseException while validating against XSD
                def document = vpxModelService.load(file)
                if (document) {
                    // Create new Projekt MVC group
                    String mvcId = generateMVCId()
                    //(projektModel, projektView, projektController) = createMVCGroup('Projekt', mvcId, [projektTabGroup: view.projektTabGroup, tabName: mvcId, mvcId: mvcId])
                    (projektModel, _, _) = createMVCGroup('Projekt', mvcId, [projektTabGroup: view.projektTabGroup, tabName: mvcId, mvcId: mvcId])
                    // Set filename in model
                    projektModel.vpxFilename = file
                    // Convert loaded XML into map
                    def map = vpxModelService.toMap(document)
                    // WAC-226: Stuckliste laden
                    projektModel.stucklisteMap = vpxModelService.stucklisteToMap(document)
                    // Recursively copy map to model
                    // ATTENTION: DOES NOT fire bindings and events asynchronously/in background!
                    // They are fired after leaving this method.
                    GH.deepCopyMap projektModel.map, map
                    //println "projektOffnen: projektModel.map=${projektModel.map}"
                    // MVC ID zur Liste der Projekte hinzufügen
                    model.projekte << mvcId
                    // Projekt aktivieren
                    projektAktivieren(mvcId)
                    projektModel.enableDisableRaumButtons(true)
                    // Fixes WAC-216
                    projektModel.enableDvbButtons()
                } else {
                    def errorMsg = 'Konnte Projekt nicht öffnen!'
                    app.controllers['Dialog'].showErrorDialog(errorMsg)
                }
            }
            // do sth. when the task is done.
            onDone {
                model.statusBarText = 'Phase 3/3: Berechne Projekt ...'
                def mvc = getMVCGroupAktivesProjekt()
                try {
                    mvc.controller.berechneAlles(true)
                    model.statusBarText = 'Bereit.'
                } catch (e) {
                    model.statusBarText = 'Fehler!'
                    e.printStackTrace()
                } finally {
                    if (resetFilename) {
                        mvc.model.vpxFilename = null
                    }
                    model.statusProgressBarIndeterminate = false
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Projekt speichern">
    /**
     * Wird über action aufgerufen. Weiterleiten an projektSpeichern.
     * @return Boolean Was project saved sucessfully?
     */
    def aktivesProjektSpeichern = { evt = null ->
        def mvc = getMVCGroupAktivesProjekt()
        projektSpeichern(mvc)
    }

    /**
     * Projekt speichern. Es wird der Dateiname aus dem ProjektModel 'vpxFilename' verwendet.
     * Ist er nicht gesetzt, wird "Projekt speichern als" aufgerufen.
     */
    def projektSpeichern = { mvc ->
        def saved = mvc.controller.save()
        if (!saved) {
            aktivesProjektSpeichernAls()
        }
        addRecentlyOpenedFile(mvc.model.vpxFilename)
    }

    /**
     * Wird über action aufgerufen. Weiterleiten an projektSpeichernAls.
     * @return Boolean Was project saved sucessfully?
     */
    def aktivesProjektSpeichernAls = { evt = null ->
        def mvc = getMVCGroupAktivesProjekt()
        projektSpeichernAls(mvc)
    }

    /**
     * WAC-246
     * @return
     */
    File getVentplanDir() {
        File vpxDir = new File("${System.getProperty('user.home')}/Ventplan")
        if (!vpxDir.exists()) {
            vpxDir.mkdirs()
        }
        vpxDir
    }

    /**
     * Zeige FileChooser, setze gewählten Dateinamen im ProjektModel und rufe "Projekt speichern".
     */
    def projektSpeichernAls = { mvc ->
        // WAC-246 Set selected filename and choose Ventplan directory
        view.vpxFileChooserWindow.selectedFile = new File("${mvc.model.map.kundendaten.bauvorhaben}.vpx")
        view.vpxFileChooserWindow.currentDirectory = getVentplanDir()
        // Open filechooser
        def openResult = view.vpxFileChooserWindow.showSaveDialog(app.windowManager.windows.find {it.focused})
        if (JFileChooser.APPROVE_OPTION == openResult) {
            def fname = view.vpxFileChooserWindow.selectedFile.toString()
            // Take care of file extension
            if (!fname.endsWith('.vpx')) {
                fname -= '.wpx'
                fname += '.vpx'
            }
            mvc.model.vpxFilename = fname
            // Save data
            projektSpeichern(mvc)
        }
        else {
            abortClosing = true
        }
    }

    /**
     * Projekt speichern. Es wird der Dateiname aus dem ProjektModel 'vpxFilename' verwendet.
     * Ist er nicht gesetzt, wird "Projekt speichern als" aufgerufen.
     * @return Boolean Was project saved sucessfully?
     */
    def alleProjekteSpeichernAction = { evt = null ->
        alleProjekteSpeichern(evt)
    }

    /**
     * Alle Projekte speichern, die nicht bereits gesichert wurden.
     */
    def alleProjekteSpeichern = { evt ->
        model.projekte.each {
            def mvc = getMVCGroup(it)
            def saved = mvc.controller.save()
            if (!saved) {
                projektSpeichernAls(mvc)
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="WAC-230">

    /**
     * WAC-230, WAC-234
     */
    File makeTemporaryProject(String wizardProjektName = '') {
        Date date = new Date()
        String projektName = wizardProjektName == '' ? "VentplanExpress_${date.dateTimeString}.vpx" : "${wizardProjektName}.vpx"
        File file = new File(getVentplanDir(), projektName)
        file.deleteOnExit()
        file
    }

    /**
     * WAC-230
     */
    void openVpxResource(String name) {
        // Temporäre Datei erzeugen
        def saveFile = makeTemporaryProject()
        // Write stream from classpath into temporary file
        InputStream stream = this.getClass().getResourceAsStream("/vpx/${name}.vpx")
        if (null != stream) {
            // Save VPX and open file
            saveFile.write(stream.getText('UTF-8'), 'UTF-8')
            projektOffnenClosure(saveFile, true)
        }
    }

    /**
     * WAC-230
     */
    def neuesProjekt_EFH4ZKBWC = {
        openVpxResource('EFH-4ZKB-WC')
    }

    /**
     * WAC-230
     */
    def neuesProjekt_EFH5ZKBHWWC = {
        openVpxResource('EFH-5ZKB-HW-WC')
    }

    /**
     * WAC-230
     */
    def neuesProjekt_EFH5ZKBWC2KRHW = {
        openVpxResource('EFH-5ZKB-WC-2KR-HW')
    }

    /**
     * WAC-230
     */
    def neuesProjekt_EFH5ZKBWCDG = {
        openVpxResource('EFH-5ZKB-WC-DG')
    }

    /**
     * WAC-234 Wizard Dialog
     */
    def neuesProjektWizard = { evt = null ->
        // Show dialog
        neuesProjektWizardDialog = GH.createDialog(builder, WizardView, [title: "Neues Projekt mit dem Wizard erstellen", size: [850, 580], resizable: true, pack: false])
        // Modify TableModel for Turen
        neuesProjektWizardDialog = GH.centerDialog(app.views['MainFrame'], neuesProjektWizardDialog)
        neuesProjektWizardDialog.show()
    }

    /**
     * WAC-234 Wizard Dialog
     */
    def wizardAbbrechen = { evt = null ->
        neuesProjektWizardDialog.dispose()
    }

    /**
     * WAC-234 Wizard Dialog
     * Neues Projekt erstellen
     */
    def wizardProjektErstellen = { evt = null ->
        model.wizardmap = model.makeWizardMap()
        //
        def typ = [
                mfh: view.wizardGebaudeTypMFH.selected,
                efh: view.wizardGebaudeTypEFH.selected,
                maisonette: view.wizardGebaudeTypMaisonette.selected
        ]
        model.wizardmap.gebaude.typ << typ
        //
        def lage = [
                windschwach: view.wizardGebaudeLageWindschwach.selected,
                windstark: view.wizardGebaudeLageWindstark.selected
        ]
        model.wizardmap.gebaude.lage << lage
        //
        def warmeschutz = [
                hoch: view.wizardGebaudeWarmeschutzHoch.selected,
                niedrig: view.wizardGebaudeWarmeschutzNiedrig.selected
        ]
        model.wizardmap.gebaude.warmeschutz << warmeschutz
        //
        def personenanzahlValue = Integer.valueOf(view.wizardHausPersonenanzahl.text)
        def aussenluftVsProPersonValue = Double.valueOf(view.wizardHausAussenluftVsProPerson.text)
        def minAussenluftRate = personenanzahlValue * aussenluftVsProPersonValue
        def geplanteBelegung = [
                personenanzahl: personenanzahlValue,
                aussenluftVsProPerson: aussenluftVsProPersonValue,
                mindestaussenluftrate: minAussenluftRate
        ]
        model.wizardmap.gebaude.geplanteBelegung << geplanteBelegung
        // Räume validieren
        def wzAnzahl = view.wizardRaumTypWohnzimmer.text == '' ? 0 : view.wizardRaumTypWohnzimmer.text.toInteger()
        addRaume('Wohnzimmer', wzAnzahl)
        def wcAnzahl = view.wizardRaumTypWC.text == '' ? 0 : view.wizardRaumTypWC.text.toInteger()
        addRaume('WC', wcAnzahl)
        def kzAnzahl = view.wizardRaumTypKinderzimmer.text == '' ? 0 : view.wizardRaumTypKinderzimmer.text.toInteger()
        addRaume('Kinderzimmer', kzAnzahl)
        def kAnzahl = view.wizardRaumTypKuche.text == '' ? 0 : view.wizardRaumTypKuche.text.toInteger()
        addRaume('Küche', kAnzahl)
        def szAnzahl = view.wizardRaumTypSchlafzimmer.text == '' ? 0 : view.wizardRaumTypSchlafzimmer.text.toInteger()
        addRaume('Schlafzimmer', szAnzahl)
        def knAnzahl = view.wizardRaumTypKochnische.text == '' ? 0 : view.wizardRaumTypKochnische.text.toInteger()
        addRaume('Kochnische', knAnzahl)
        def ezAnzahl = view.wizardRaumTypEsszimmer.text == '' ? 0 : view.wizardRaumTypEsszimmer.text.toInteger()
        addRaume('Esszimmer', ezAnzahl)
        def bAnzahl = view.wizardRaumTypBad.text == '' ? 0 : view.wizardRaumTypBad.text.toInteger()
        addRaume('Bad mit/ohne WC', bAnzahl)
        def azAnzahl = view.wizardRaumTypArbeitszimmer.text == '' ? 0 : view.wizardRaumTypArbeitszimmer.text.toInteger()
        addRaume('Arbeitszimmer', azAnzahl)
        def drAnzahl = view.wizardRaumTypDuschraum.text == '' ? 0 : view.wizardRaumTypDuschraum.text.toInteger()
        addRaume('Duschraum', drAnzahl)
        def gzAnzahl = view.wizardRaumTypGastezimmer.text == '' ? 0 : view.wizardRaumTypGastezimmer.text.toInteger()
        addRaume('Gästezimmer', gzAnzahl)
        def sAnzahl = view.wizardRaumTypSauna.text == '' ? 0 : view.wizardRaumTypSauna.text.toInteger()
        addRaume('Sauna', sAnzahl)
        def hrAnzahl = view.wizardRaumTypHausarbeitsraum.text == '' ? 0 : view.wizardRaumTypHausarbeitsraum.text.toInteger()
        addRaume('Hausarbeitsraum', hrAnzahl)
        def fAnzahl = view.wizardRaumTypFlur.text == '' ? 0 : view.wizardRaumTypFlur.text.toInteger()
        addRaume('Flur', fAnzahl)
        def krAnzahl = view.wizardRaumTypKellerraum.text == '' ? 0 : view.wizardRaumTypKellerraum.text.toInteger()
        addRaume('Kellerraum', krAnzahl)
        def dAnzahl = view.wizardRaumTypDiele.text == '' ? 0 : view.wizardRaumTypDiele.text.toInteger()
        addRaume('Diele', dAnzahl)
        // Dialog schließen
        neuesProjektWizardDialog.dispose()
        // Temporäre Datei erzeugen
        def saveFile = makeTemporaryProject(view.wizardProjektName.text)
        // Model speichern und ...
        vpxModelService.save(model.wizardmap, saveFile)
        // ... anschließend wieder laden
        projektOffnenClosure(saveFile, true)
    }

    def addRaume(raumTyp, anzahl) {
        String raumName
        for (int i = 1; i <= anzahl; i++) {
            if (i == 1) {
                raumName = raumTyp
            } else if (i > 1) {
                raumName = raumTyp + ' ' + i.toString()
            }
            def raum = model.raumMapTemplate.clone() as ObservableMap
            def pos = model.wizardmap.raum?.raume?.size()
            //String raumSize = (model.wizardmap.raum?.raume?.size() + 1).toString()
            // Türen
            raum.turen = [
                    [turBezeichnung: '', turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
                    [turBezeichnung: '', turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
                    [turBezeichnung: '', turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
                    [turBezeichnung: '', turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
                    [turBezeichnung: '', turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true]
            ] as ObservableList
            // Hole Werte für neuen Raum aus der View und füge Raum hinzu
            raum.with {
                // Übernehme Wert für Bezeichnung vom Typ?
                raumBezeichnung = raumName
                // Länge + Breite
                raumLange = 5.0d
                raumBreite = 4.0d
                // Fläche, Höhe, Volumen
                raumFlache = raumLange * raumBreite
                raumHohe = 2.5d
                raumVolumen = raumFlache * raumHohe
                // Zuluftfaktor
                raumZuluftfaktor = raumZuluftfaktor?.toDouble2() ?: 0.0d
                // Abluftvolumenstrom
                raumAbluftVolumenstrom = raumAbluftVolumenstrom?.toDouble2() ?: 0.0d
                // Standard Türspalthöhe ist 10 mm
                raumTurspaltHohe = 10.0d
                raumNummer = '' + raumSize
                position = pos
            }
            raum = raumTypAendern(raum)
            //prufeRaumDaten(raum, expressModus)
            model.wizardmap.raum.raume << raum
        }
    }

    static List typ = [
            'Wohnzimmer', 'Kinderzimmer', 'Schlafzimmer', 'Esszimmer', 'Arbeitszimmer', 'Gästezimmer',
            'Hausarbeitsraum', 'Kellerraum', 'WC', 'Küche', 'Kochnische', 'Bad mit/ohne WC', 'Duschraum',
            'Sauna', 'Flur', 'Diele'
    ]

    /**
     * Aus ProjektModel
     */
    def raumTypAendern = { raum ->
        int pos = -1
        typ.eachWithIndex { n, i ->
            if (raum.raumBezeichnung.startsWith(n))
                pos = i
        }
        if (pos == -1) {
            return
        }
        switch (pos) {
        // Zulufträume
            case 0..5:
                raum.raumLuftart = 'ZU'
                switch (pos) {
                    case 0:
                        raum.raumZuluftfaktor = 3.0d
                        break
                    case 1..2:
                        raum.raumZuluftfaktor = 2.0d
                        break
                    case 3..5:
                        raum.raumZuluftfaktor = 1.5d
                        break
                }
                break
        // Ablufträume
            case 6..13:
                raum.raumLuftart = 'AB'
                switch (pos) {
                    case 6..8:
                        raum.raumAbluftVolumenstrom = 25.0d
                        break
                    case 9..12:
                        raum.raumAbluftVolumenstrom = 45.0d
                        break
                    case 13:
                        raum.raumAbluftVolumenstrom = 100.0d
                        break
                }
                break
        // Überströmräume
            case { it > 13 }:
                raum.raumLuftart = 'ÜB'
        }
        return raum
    }

    //</editor-fold>

    /**
     * Ein neues Projekt erstellen.
     */
    def neuesProjekt = { evt = null ->
        // Progress bar in VentplanView.
        jxwithWorker(start: true) {
            //
            String mvcId = ''
            // initialize the worker
            onInit {
                model.statusProgressBarIndeterminate = true
                view.mainStatusBarText.text = 'Phase 1/3: Erstelle ein neues Projekt...'
            }
            // do the task
            work {
                // Die hier vergebene MVC ID wird immer genutzt, selbst wenn das Projekt anders benannt wird!
                // (durch Bauvorhaben, Speichern)
                // Es wird also immer 'Projekt 1', 'Projekt 2' etc. genutzt, nach Reihenfolge der Erstellung
                view.mainStatusBarText.text = ''
                view.mainStatusBarText.text = 'Phase 2/3: Initialisiere das Projekt...'
                try {
                    mvcId = generateMVCId()
                    def (m, v, c) = createMVCGroup('Projekt', mvcId, [projektTabGroup: view.projektTabGroup, tabName: mvcId, mvcId: mvcId])
                    view.mainStatusBarText.text = ''
                    view.mainStatusBarText.text = 'Phase 3/3: Erstelle Benutzeroberfläche für das Projekt...'
                    doLater {
                        // MVC ID zur Liste der Projekte hinzufügen
                        model.projekte << mvcId
                        // Projekt aktivieren
                        projektAktivieren(mvcId)
                        // resize the frame to validate the components.
                        try {
                            def dim = ventplanFrame.getSize()
                            ventplanFrame.setSize((int) dim.width + 1, (int) dim.height)
                            ventplanFrame.invalidate()
                            ventplanFrame.validate()
                            ventplanFrame.setSize(dim)
                            ventplanFrame.invalidate()
                            ventplanFrame.validate()
                        } catch (e) {
                            e.printStackTrace()
                        }
                    }
                    model.statusBarText = ''
                    view.mainStatusBarText.text = ''
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }
            // do sth. when the task is done.
            onDone {
                view.mainStatusBarText.text = ''
                //model.statusBarText = ''
                model.statusProgressBarIndeterminate = false
                //model.statusBarText = 'Bereit.'
                view.mainStatusBarText.text = 'Bereit.'
                model.statusBarText = 'Bereit.'
            }
        }
    }

    /**
     * Das aktive Projekt schliessen.
     */
    def projektSchliessen = { evt = null ->
        // Closure for closing the active project
        def clacpr = { mvc ->
            // Tab entfernen
            view.projektTabGroup.remove(view.projektTabGroup.selectedComponent)
            // MVC Gruppe zerstören
            destroyMVCGroup(mvc.mvcId)
            // Aus Liste der Projekte entfernen
            model.projekte.remove(mvc.mvcId)
            // aktives Projekt auf null setzen.
            // Wichtig für die Bindings in den Menus
            model.aktivesProjekt = null
            // aktives Projekt nur setzen, wenn noch weitere Projekte offen sind.
            if (view.projektTabGroup.selectedIndex > -1) {
                model.aktivesProjekt = model.projekte[view.projektTabGroup.selectedIndex]
            }
            // NOT NEEDED projektIndexAktivieren(view.projektTabGroup.selectedIndex)
            // Wird durch die Tab und den ChangeListener erledigt.
            // Das aktive Projekt wurde geandert... nein, geschlossen.
            model.aktivesProjektGeandert = false
        }
        // Projekt zur aktiven Tab finden
        def mvc = getMVCGroupAktivesProjekt()
        if (!mvc) {
            //println 'projektSchliessen: kein aktives Projekt!'
            return
        }
        def canClose = mvc.controller.canClose()
        if (!canClose) {
            def choice = app.controllers['Dialog'].showCloseProjectDialog()
            switch (choice) {
                case 0: // Save: save and close project
                    aktivesProjektSpeichern(evt)
                    clacpr(mvc)
                    break
                case 1: // Cancel: do nothing...
                    break
                case 2: // Close: just close the tab...
                    clacpr(mvc)
                    break
            }
        } else {
            clacpr(mvc)
        }
    }

    //<editor-fold desc="WAC-108 Odisee">
    /**
     * WAC-108 Auslegung
     */
    def projektAuslegungErstellen = { evt = null ->
        getMVCGroupAktivesProjekt().controller.auslegungErstellen()
    }

    /**
     * WAC-108 Angebot
     */
    def projektAngebotErstellen = { evt = null ->
        getMVCGroupAktivesProjekt().controller.angebotErstellen()
    }

    /**
     * WAC-108 Stückliste
     */
    def projektStuecklisteErstellen = { evt = null ->
        getMVCGroupAktivesProjekt().controller.stuecklisteErstellen()
    }
    //</editor-fold>

    //<editor-fold desc="WAC-151 Automatische und manuelle Berechnung">
    /**
     * WAC-151 Automatische und manuelle Berechnung
     */
    def automatischeBerechnung = { evt = null ->
        def mvc = getMVCGroupAktivesProjekt()
        jxwithWorker(start: true) {
            // initialize the worker
            onInit {
                model.statusProgressBarIndeterminate = true
                model.statusBarText = 'Berechne ...'
            }
            work {
                // Neu berechnen
                mvc.controller.automatischeBerechnung()
            }
            onDone {
                model.statusProgressBarIndeterminate = false
                model.statusBarText = 'Bereit.'
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="WAC-161 Zuletzt geöffnete Projekte">
    /**
     * WAC-161 Zuletzt geöffnete Projekte
     */
    def addRecentlyOpenedFile = { filename ->
        /* TODO mmu
        // Add file to MRU list
        if (mruFileManager.size() == MRUFileManager.DEFAULT_MAX_SIZE) {
            //def position = 6 + MRUFileManager.DEFAULT_MAX_SIZE
            //view.mainMenu.remove(position)
        }
        */
        mruFileManager.setMRU(filename)
        mruFileManager.save()
        buildRecentlyOpenedMenuItems()
    }

    /**
     * Removes all recently opened file menuItem objects and adds them again.
     */
    def buildRecentlyOpenedMenuItems = {
        if (mruFileManager.size() > 0) {
            view.recentlyOpenedMenu.removeAll()
            def mruList = mruFileManager.getMRUFileList()
            edt {
                mruList.each() { f ->
                    def newMenuItem = builder.menuItem(f)
                    newMenuItem.setAction(builder.action(
                            id: 'zuletztGeoffnetesProjektAction',
                            name: "${f}".toString(),
                            enabled: true,
                            closure: zuletztGeoffnetesProjekt
                    ))
                    view.recentlyOpenedMenu.add(newMenuItem)
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="WAC-192 Suchfunktion für WPX-Dateien"> 
    /**
     * WAC-192 Suchfunktion für WPX-Dateien
     * Start-Verzeichnis für die Suche wählen.
     * Dieses Verzeichnis wird in den prefs gespeichert.
     */
    def projektSuchenOrdnerOeffnen = { evt = null ->
        def openResult = view.projektSuchenFolderChooserWindow.showOpenDialog(view.projektSuchenPanel)
        if (JFileChooser.APPROVE_OPTION == openResult) {
            def file = view.projektSuchenFolderChooserWindow.selectedFile
            view.projektSuchenOrdnerPfad.text = file.absolutePath
            // Save file path for later use...
            projektSuchenPrefs.save(file.absolutePath)
        }
    }

    /**
     * WAC-192 Suchfunktion für WPX-Dateien
     * Suche starten:
     * Iteriert über das ausgewählte Verzeichnis + Unterverzeichnis und sucht in allen Dateien (*.wpx, *.vpx)
     * nach den Wörtern.
     * Es ist eine "Oder"-Suche
     */
    def projektSuchenStarteSuche = { evt = null ->
        String searchInPath = view.projektSuchenOrdnerPfad.text
        if (searchInPath) {
            File startFileDir = new File(searchInPath)
            if (startFileDir.exists()) {

                def list = []
                // liste leeren
                model.projektSuchenEventList.clear()

                // Sucht alle Dateien, die auf wpx enden
                startFileDir.traverse(
                        type: FileType.FILES,
                        nameFilter: ~/.*\.wpx|.*\.vpx/
                ) { file ->
                    def rootWpx = new XmlSlurper().parseText(file.getText())
                    def projekt = rootWpx.projekt
                    if (view.projektSuchenBauvorhaben.text) {
                        if (projekt.bauvorhaben.text() && projekt.bauvorhaben.text().contains(view.projektSuchenBauvorhaben.text)) {
                            if (!list.contains(file.absolutePath)) {
                                list << file.absolutePath
                            }
                        }
                    }

                    def allFirma = projekt.firma
                    def ausfuhrendeFirma = allFirma[0].name().equals('firma') && allFirma[0].rolle.text().equals('Ausfuhrende') ? allFirma[0] : allFirma[1]
                    def grosshandelFirma = allFirma[0].name().equals('firma') && allFirma[0].rolle.text().equals('Grosshandel') ? allFirma[0] : allFirma[1]

                    if (view.projektSuchenInstallateur.text) {
                        // installateur = handwerker = Auführende Firma

                        if ((ausfuhrendeFirma.firma1.text() && ausfuhrendeFirma.firma1.text().contains(view.projektSuchenInstallateur.text)) ||
                                (ausfuhrendeFirma.firma2.text() && ausfuhrendeFirma.firma2.text().contains(view.projektSuchenInstallateur.text)) ||
                                (ausfuhrendeFirma.ort.text() && ausfuhrendeFirma.ort.text().contains(view.projektSuchenInstallateur.text))) {
                            if (!list.contains(file.absolutePath)) {
                                list << file.absolutePath
                            }
                        }
                    }
                    if (view.projektSuchenHandel.text) {
                        if ((grosshandelFirma.firma1.text() && grosshandelFirma.firma1.text().contains(view.projektSuchenHandel.text)) ||
                                (grosshandelFirma.firma2.text() && grosshandelFirma.firma2.text().contains(view.projektSuchenHandel.text)) ||
                                (grosshandelFirma.ort.text() && grosshandelFirma.ort.text().contains(view.projektSuchenHandel.text))) {
                            if (!list.contains(file.absolutePath)) {
                                list << file.absolutePath
                            }
                        }
                    }
                }
                if (list.size() == 0) {
                    def infoMsg = 'Es wurden keine Dateien mit Ihren Suchbegriffen gefunden!'
                    app.controllers['Dialog'].showInformDialog(infoMsg as String)
                } else {
                    // Gefundene Dateien in der Liste anzeigen
                    model.projektSuchenEventList.addAll(list)
                }
            }
        } else {
            def infoMsg = 'Bitte wählen Sie erst einen Pfad zum Suchen aus!'
            app.controllers['Dialog'].showInformDialog(infoMsg as String)
        }
    }

    /**
     * WAC-192 Suchfunktion für WPX-Dateien
     * Dialog schließen.
     */
    def projektSuchenAbbrechen = { evt = null ->
        projektSuchenDialog.dispose()
    }

    /**
     * WAC-192 Suchfunktion für WPX-Dateien
     * Dialog für die Suche öffnen
     */
    def nachProjektSuchenDialogOeffnen = { evt = null ->
        projektSuchenDialog = GH.createDialog(builder, ProjektSuchenView, [title: 'Projekt suchen', resizable: true, pack: true])
        projektSuchenDialog = GH.centerDialog(app.views['MainFrame'], projektSuchenDialog)
        if (projektSuchenPrefs.getSearchFolder()) {
            view.projektSuchenOrdnerPfad.text = projektSuchenPrefs.getSearchFolder()
        }
        projektSuchenDialog.show()
    }

    /**
     * WAC-192 Suchfunktion für WPX-Dateien
     * Gewählte Datei
     */
    def projektSuchenDateiOeffnen = { evt = null ->
        def file = view.projektSuchenList.selectedValue
        if (file) {
            projektOffnenClosure(file)
            projektSuchenDialog.dispose()
        } else {
            def infoMsg = 'Sie haben keine Datei zum Öffnen ausgewählt!'
            app.controllers['Dialog'].showInformDialog(infoMsg as String)
        }
    }
    //</editor-fold>

    //<editor-fold desc="WAC-202 Prinzipskizze">
    /**
     * WAC-202 Prinzipskizze
     */
    def projektPrinzipskizzeErstellen = {
        // Erzeuge Stückliste für aktives Projekt.
        getMVCGroupAktivesProjekt()?.controller?.generierePrinzipskizze()
    }
    //</editor-fold>

    /**
     * WAC-177 Angebotsverfolgung
     def angebotsverfolgung = {getMVCGroupAktivesProjekt().controller.angebotsverfolgung()}*/

}

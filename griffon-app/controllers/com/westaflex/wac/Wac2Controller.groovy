/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/controllers/Wac2Controller.groovy
 * Last modified at 13.03.2011 18:34:15 by rbe
 */
package com.westaflex.wac

import com.westaflex.wac.*
import com.bensmann.griffon.GriffonHelper as GH

/**
 * 
 */
class Wac2Controller {
	
    public static boolean DEBUG = true

    def model
    def wacModelService
    def projektModelService
    def view
    def wacCalculationService
    def oooService
    def builder
	
    /**
     * Zähler für erstellte/geladene Projekte. Wird als "unique id" verwendet.
     * Siehe generateMVCId().
     */
    def static projektCounter = 1

    /**
     * User's settings.
     */
    private def prefs = java.util.prefs.Preferences.userNodeForPackage(Wac2Controller)

    def static mruFileManager = MRUFileManager.getInstance()
    
    /**
     *
     */
    private def wacwsUrl = GH.getWacwsUrl()

    /**
     * Initialize wac2 MVC group.
     */
    void mvcGroupInit(Map args) {
    }
	
    /**
     * Get access to all components of a MVC group by its ID.
     */
    def getMVCGroup(mvcId) {
        [
            mvcId: mvcId,
            model: app.groups[mvcId].model,
            view: app.groups[mvcId].view,
            controller: app.groups[mvcId].controller
        ]
    }

    /**
     * Hole MVC Group des aktiven Projekts.
     */
    def getMVCGroupAktivesProjekt() {
        if (DEBUG) println "getMVCGroupAktivesProjekt: model.aktivesProjekt=${model.aktivesProjekt}"
        getMVCGroup(model.aktivesProjekt)
    }
	
    /**
     * Shutdown application and all resources.
     */
    def _shutdown() {
        // Shutdown OpenOffice
        oooService.shutdownOCM()
        // Shutdown application
        app.shutdown()
    }
	
    /**
     * Schliessen? Alle Projekte fragen, ob ungesicherte Daten existieren.
     */
    boolean canClose() {
        model.projekte.inject(true) { o, n ->
            def c = getMVCGroup(n).controller
            if (DEBUG) println "o=${o} c.canClose=${c.canClose()}"
            o &= c.canClose()
        }
    }
    
    def exitApplication = { evt = null ->
        exitApplication()
    }
	
    /**
     * 
     */
    boolean exitApplication(evt) {
        boolean proceed = false
        // Ask if we can close
        def canClose = canClose()
        //if (DEBUG) println "exitApplication: ${canClose}"
        if (canClose) {
            def choice = app.controllers["Dialog"].showApplicationOnlyCloseDialog()
            if (DEBUG) println "exitApplication: choice=${choice}"
            switch (choice) {
                case 0:
                	if (DEBUG) println "Ja"
                	alleProjekteSpeichern(evt)
                	proceed = true
                	break
                case 1: // Cancel: do nothing...
                	if (DEBUG) println "Nein"
                	proceed = false
                	break
            }
        } else {
            if (DEBUG) println "exitApplication: there are unsaved changes"
            // Show dialog: ask user for save all, cancel, quit
            def choice = app.controllers["Dialog"].showApplicationSaveAndCloseDialog()
            if (DEBUG) println "exitApplication: choice=${choice}"
            switch (choice) {
                case 0:
                	if (DEBUG) println "Alles speichern"
                	alleProjekteSpeichern(evt)
                	proceed = true
                	break
                case 1: // Cancel: do nothing...
                	if (DEBUG) println "Abbrechen..."
                	proceed = false
                	break
                case 2:
                	if (DEBUG) println "Schliessen"
                	proceed = true
                	break
            }
        }
        mruFileManager.save()
        return proceed
    }
	
    /**
     * Disable last tab of JTabbedPane 'projektTabGroup'.
     */
    def disableLastProjektTab = {
        view.projektTabGroup.setEnabledAt(view.projektTabGroup.tabCount - 1, false)
    }

    /**
     * Enable last tab of JTabbedPane 'projektTabGroup'.
     */
    def enableLastProjektTab = {
        view.projektTabGroup.setEnabledAt(view.projektTabGroup.tabCount - 1, true)
    }

    /**
     * 
     */
    String generateMVCId() {
        def c = Wac2Controller.projektCounter++
        def t = "Projekt ${c.toString()}".toString()
        //println "Wac2Controller.generateMVCId -> t=${t.dump()}"
        t
    }
	
    /**
     * Ein neues Projekt erstellen.
     */
    def neuesProjekt = { evt = null ->
        // Progress bar in Wac2View.
        jxwithWorker(start: true) {
            // initialize the worker
            onInit {
                model.statusProgressBarIndeterminate = true
                model.statusBarText = "Phase 1/3: Erstelle ein neues Projekt..."
            }
            // do the task
            work {
                // Die hier vergebene MVC ID wird immer genutzt, selbst wenn das Projekt anders benannt wird!
                // (durch Bauvorhaben, Speichern)
                // Es wird also immer "Projekt 1", "Projekt 2" etc. genutzt, nach Reihenfolge der Erstellung
                model.statusBarText = "Phase 2/3: Initialisiere das Projekt..."
                String mvcId = generateMVCId()
                def (m, v, c) =
                    createMVCGroup("Projekt", "${mvcId}", [projektTabGroup: view.projektTabGroup, tabName: mvcId, mvcId: mvcId])
                model.statusBarText = "Phase 3/3: Erstelle Benutzeroberfläche für das Projekt..."
                //doLater {
                    // MVC ID zur Liste der Projekte hinzufügen
                    model.projekte << mvcId
                    // Projekt aktivieren
                    projektAktivieren(mvcId)
                    // resize the frame to validate the components.
                    try{
                        def dim = wac2Frame.getSize()
                        wac2Frame.setSize((int) dim.width + 1, (int) dim.height)
                        wac2Frame.invalidate()
                        wac2Frame.validate()
                        wac2Frame.setSize(dim)
                        wac2Frame.invalidate()
                        wac2Frame.validate()
                    } catch (e) {
                        e.printStackTrace()
                    }
                //}
            }
            // do sth. when the task is done.
            onDone {
                model.statusBarText = ""
                model.statusProgressBarIndeterminate = false
                model.statusBarText = "Bereit."
            }
        }
    }
	
    /**
     * Ein Projekt aktivieren -- MVC ID an Wac2Model übergeben.
     */
    def projektAktivieren = { mvcId ->
        if (DEBUG) println "Wac2Controller.projektAktivieren: mvcId=${mvcId} model.aktivesProjekt=${model?.aktivesProjekt}"
        if (DEBUG) println "Wac2Controller.projektAktivieren: model=${model?.dump()}"
        // Anderes Projekt wurde aktiviert?
        if (mvcId && mvcId != model?.aktivesProjekt) {
            // MVC ID merken
            model.aktivesProjekt = mvcId
            // Dirty-flag aus Projekt-Model übernehmen
            try {
                def mvcGroup = getMVCGroup(mvcId)
                if (DEBUG) println "Wac2Controller.projektAktivieren: getMVCGroup(mvcGroup)=${mvcGroup}, wpx=${mvcGroup.model?.wpxFilename}"
                if (DEBUG) println "Wac2Controller.projektAktivieren: getMVCGroup(mvcGroup)=${mvcGroup.dump()}"
                model.aktivesProjektGeandert = mvcGroup.model?.map.dirty
            } catch (e) {
                e.printStackTrace()
            }
            if (DEBUG) println "Wac2Controller.projektAktivieren: mvcId=${model.aktivesProjekt}"
        }
        /*
        else {
        	if (DEBUG) println "projektAktivieren: no change"
        }
         */
    }

    /**
     * Ein Projekt aktivieren -- MVC ID an Wac2Model übergeben.
     */
    def projektIndexAktivieren = { index ->
        if (index > -1) {
            //if (DEBUG) println "projektIndexAktivieren: model.projekte=${model.projekte.dump()}"
            projektAktivieren(model.projekte[index])
            //if (DEBUG) println "projektIndexAktivieren: index=${index} -> ${model.aktivesProjekt}"
        }
        /*
        else {
        if (DEBUG) println "projektIndexAktivieren: index=${index}: Kein Projekt vorhanden!"
        }
         */
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
            if (DEBUG) println "projektSchliessen: removing ${model.aktivesProjekt} from model.projekte=${model.projekte.dump()}"
            model.projekte.remove(mvc.mvcId)
            // aktives Projekt auf null setzen.
            // Wichtig für die Bindings in den Menus
            model.aktivesProjekt = null
            // aktives Projekt nur setzen, wenn noch weitere Projekte offen sind.
            if (view.projektTabGroup.selectedIndex > -1) {
                model.aktivesProjekt = model.projekte[view.projektTabGroup.selectedIndex]
            }
            if (DEBUG) println "projektSchliessen: model.projekte=${model.projekte.dump()}"
            // NOT NEEDED projektIndexAktivieren(view.projektTabGroup.selectedIndex)
            // Wird durch die Tab und den ChangeListener erledigt.
        }
        // Projekt zur aktiven Tab finden
        println "projektSchliessen: "
        def mvc = getMVCGroupAktivesProjekt()
        if (!mvc) {
            println "projektSchliessen: kein aktives Projekt!"
            return
        }
        if (DEBUG) println "projektSchliessen: model.aktivesProjekt=${model.aktivesProjekt} mvc=${mvc}"
        def canClose = mvc.controller.canClose()
        if (!canClose) {
            if (DEBUG) println "projektSchliessen: canClose=${canClose}, there's unsaved data"
            def choice = app.controllers["Dialog"].showCloseProjectDialog()
            if (DEBUG) println "projektSchliessen: choice=${choice}"
            switch (choice) {
                case 0: // Save: save and close project
                	if (DEBUG) println "projektSchliessen: save and close"
                	aktivesProjektSpeichern(evt)
                	clacpr(mvc)
                	break
                case 1: // Cancel: do nothing...
                	if (DEBUG) println "projektSchliessen: cancel"
                	break
                case 2: // Close: just close the tab...
                	if (DEBUG) println "projektSchliessen: close without save"
               		clacpr(mvc)
                	break
            }
        } else {
            if (DEBUG) println "projektSchliessen: else... close!!"
            clacpr(mvc)
        }
    }
	
    /**
     * Projekt öffnen: zeige FileChooser, lese XML, erstelle eine MVC Group und übertrage die Werte
     * aus dem XML in das ProjektModel.
     */
    def projektOffnen = { evt = null ->
        def openResult = view.wpxFileChooserWindow.showOpenDialog(view.wac2Frame)
        if (javax.swing.JFileChooser.APPROVE_OPTION == openResult) {
            def file = view.wpxFileChooserWindow.selectedFile
            projektOffnenClosure(file)
        }
    }
    
    def zuletztGeoffnetesProjekt = { evt = null ->
        try {
            def file = evt.getActionCommand()
            projektOffnenClosure(file)
        }
        catch (Exception e) {
            println "ERROR in zuletztGeoffnetesProjekt: Could not load file ${file} caused by: ${e.dump()}"
        }
    }
    
    def projektOffnenClosure = { file ->
        jxwithWorker(start: true) {
            // initialize the worker
            onInit {
                model.statusProgressBarIndeterminate = true
                model.statusBarText = "Phase 1/3: Projektdatei wählen..."
            }
            // do the task
            work {
                // Add file to MRU list
                addRecentlyOpenedFile(file)
                // ... and reset it in FileChooser
                view.wpxFileChooserWindow.selectedFile = null
                if (DEBUG) println "projektOffnen: file=${file?.dump()}"
                // Load data; start thread
                model.statusBarText = "Phase 2/3: Lade Daten..."
                def projektModel, projektView, projektController
                // May return null due to org.xml.sax.SAXParseException while validating against XSD
                def document = projektModelService.load(file)
                if (document) {
                    //if (DEBUG) println "projektOffnen: document=${document?.dump()}"
                    // Create new Projekt MVC group
                    String mvcId = generateMVCId()
                    (projektModel, projektView, projektController) =
                    createMVCGroup("Projekt", mvcId,
                        [projektTabGroup: view.projektTabGroup, tabName: mvcId, mvcId: mvcId])
                    // Set filename in model
                    projektModel.wpxFilename = file
                    // Convert loaded XML into map
                    def map = projektModelService.toMap(document)
                    // Recursively copy map to model
                    // ATTENTION: DOES NOT fire bindings and events asynchronously/in background!
                    // They are fired after leaving this method.
                    GH.deepCopyMap projektModel.map, map
                    //println "projektOffnen: projektModel.map=${projektModel.map}"
                    // MVC ID zur Liste der Projekte hinzufügen
                    model.projekte << mvcId
                    // Projekt aktivieren
                    projektAktivieren(mvcId)
                } else {
                    def errorMsg = "projektOffnen: Konnte Projekt nicht öffnen!"
                    app.controllers["Dialog"].showErrorDialog(errorMsg as String)
                    if (DEBUG) println errorMsg
                }
            }
            // do sth. when the task is done.
            onDone {
                def mvc = getMVCGroupAktivesProjekt()
                model.statusBarText = "Phase 3/3: Berechne Projekt..."
                //
                try {
                    ///println "calling berechneAlles()"
                    mvc.controller.berechneAlles(true)
                } catch (e) {}
                //
                model.statusProgressBarIndeterminate = false
                model.statusBarText = "Bereit."
            }
        }
    }

    /**
     * Wird über action aufgerufen. Weiterleiten an projektSpeichern.
     * @return Boolean Was project saved sucessfully?
     */
    def aktivesProjektSpeichern = { evt = null ->
        def mvc = getMVCGroupAktivesProjekt()
        projektSpeichern(mvc)
    }

    /**
     * Projekt speichern. Es wird der Dateiname aus dem ProjektModel 'wpxFilename' verwendet.
	 * Ist er nicht gesetzt, wird "Projekt speichern als" aufgerufen.
     */
    def projektSpeichern = { mvc ->
        def saved = mvc.controller.save()
        if (saved) {
            if (DEBUG) println "aktivesProjektSpeichern: Projekt gespeichert in ${mvc.model.wpxFilename}"
            //saved
        } else {
            if (DEBUG) println "aktivesProjektSpeichern: Projekt nicht gespeichert, kein Dateiname (mvc.model.wpxFilename=${mvc.model.wpxFilename?.dump()})?"
            aktivesProjektSpeichernAls()
        }
        addRecentlyOpenedFile(mvc.model.wpxFilename)
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
     * Zeige FileChooser, setze gewählten Dateinamen im ProjektModel und rufe "Projekt speichern".
     */
    def projektSpeichernAls = { mvc ->
        // Reset selected filename
        view.wpxFileChooserWindow.selectedFile = null
        // Open filechooser
        def openResult = view.wpxFileChooserWindow.showSaveDialog(view.wac2Frame)
        if (javax.swing.JFileChooser.APPROVE_OPTION == openResult) {
            def fname = view.wpxFileChooserWindow.selectedFile.toString()
            if (!fname.endsWith(".wpx")) fname += ".wpx"
            mvc.model.wpxFilename = fname
            if (DEBUG) println "projektSpeichernAls: wpxFilename=${mvc.model.wpxFilename?.dump()}"
            // Save data
            projektSpeichern(mvc)
        }
    }

    /**
     * Projekt speichern. Es wird der Dateiname aus dem ProjektModel 'wpxFilename' verwendet.
     * Ist er nicht gesetzt, wird "Projekt speichern als" aufgerufen.
     * @return Boolean Was project saved sucessfully?
     */
    def alleProjekteSpeichern = { evt = null ->
        model.projekte.each {
            def mvc = getMVCGroup(it)
            def saved = mvc.controller.save()
            if (saved) {
                if (DEBUG) println "alleProjekteSpeichern: Projekt gespeichert in ${mvc.model.wpxFilename}"
                //saved
            } else {
                if (DEBUG) println "alleProjekteSpeichern: Projekt nicht gespeichert, kein Dateiname (mvc.model.wpxFilename=${mvc.model.wpxFilename?.dump()})?"
                projektSpeichernAls(mvc)
            }
        }
    }

    /**
     * Seitenansicht öffnen.
     */
    def projektSeitenansicht = { evt = null ->
        getMVCGroupAktivesProjekt().controller.seitenansicht()
    }

    /**
     * Projekt drucken.
     */
    def projektDrucken = { evt = null ->
        getMVCGroupAktivesProjekt().controller.drucken()
    }

    /**
     * WAC-151: Automatische und manuelle Berechnung
     */
    def automatischeBerechnung = { evt = null ->
        def mvc = getMVCGroupAktivesProjekt()
        
        jxwithWorker(start: true) {
            // initialize the worker
            onInit {
                model.statusProgressBarIndeterminate = true
                model.statusBarText = "Berechne..."
            }
            work {
                // Neu berechnen
                mvc.controller.automatischeBerechnung()
            }
            onDone {
                model.statusProgressBarIndeterminate = false
                model.statusBarText = "Bereit."
            }
        }
    }

    /**
     * WAC-167: Info-Menü mit Über-Dialog
     * Dialog mit Logo und Versionsnummer
     */
    def aboutDialogOeffnen = { evt = null ->
        def aboutDialog = GH.createDialog(builder, AboutView, [title: "Über", resizable: false, pack: true])
        aboutDialog.show()
    }
	
    /**
     * WAC-161: Zuletzt geöffnete Projekte
     */
    void addRecentlyOpenedFile(filename) {
        if (DEBUG) println "addRecentlyOpenedFile -> filename: ${filename}"
        // Add file to MRU list
        if (mruFileManager.size() == MRUFileManager.DEFAULT_MAX_SIZE) {
            //def position = 6 + MRUFileManager.DEFAULT_MAX_SIZE
            //view.mainMenu.remove(position)
        }
        mruFileManager.setMRU(filename)
        mruFileManager.save()
        buildRecentlyOpenedMenuItems()
    }
    
    /**
     * Removes all recently opened file menuItem objects and adds them again.
     */
    def buildRecentlyOpenedMenuItems = {
        if (mruFileManager.size() > 0) {
            if (DEBUG) println "Wac2Controller.buildRecentlyOpenedMenuItems view dump -> ${view.dump()}"
            view.recentlyOpenedMenu.removeAll()
            
            def mruList = mruFileManager.getMRUFileList()
            
            edt{
                mruList.each() { f -> 
                    if (DEBUG) println "Wac2Controller.buildRecentlyOpenedMenuItems -> f = ${f}".toString()
                    def newMenuItem = builder.menuItem(f)
                    newMenuItem.setAction(builder.action(
                            id: "zuletztGeoffnetesProjektAction" as String,
                            name: "${f}".toString(),
                            enabled: true,
                            closure: zuletztGeoffnetesProjekt
                        ))
                    view.recentlyOpenedMenu.add(newMenuItem)
                }
            }
        }
    }
    
    /**
     * WAC-177: Angebotsverfolgung
     */
    def angebotsverfolgung = {
        // show input dialog
        def inputName = javax.swing.JOptionPane.showInputDialog("Bitte geben Sie Ihren Namen ein.")
        // if input is not empty show file dialog
        if (inputName) {
            def openResult = view.angebotsverfolgungChooserWindow.showOpenDialog(view.wac2Frame)
            if (javax.swing.JFileChooser.APPROVE_OPTION == openResult) {
                def files = view.angebotsverfolgungChooserWindow.selectedFiles as java.io.File[]
                angebotsverfolgungFilesClosure(files, inputName)
            }
        } else {
            def errorMsg = "Geben Sie einen Namen an, um die Angebotsverfolgung durchzuführen." as String
            app.controllers["Dialog"].showErrorDialog(errorMsg as String)
        }
    }
    
    /**
     * Iterate through the files and submit each file to the web service.
     * Informs the user with dialogs what happens: error, success...
     */
    def angebotsverfolgungFilesClosure = { files, inputName ->
        
        def wacwsUrl
        
        boolean isError = false
        def fileMsg = "" as String
        jxwithWorker(start: true) {
            // initialize the worker
            onInit {
                model.statusProgressBarIndeterminate = true
                model.statusBarText = "WPX-Dateien werden hochgeladen..."
            }
            // do the task
            work {
                // ... and reset it in FileChooser
                view.angebotsverfolgungChooserWindow.selectedFile = null
                // check if files array is directory
                if (files?.class.isArray()) {
                    files.each { file -> 
                        try {
                            if (file.isDirectory()) {
                                model.statusBarText = "Lade WPX-Dateien aus Verzeichnis ${file.path} hoch..." as String

                                def listFiles = file.listFiles()

                                listFiles.each { f -> 
                                    if (f.name.toLowerCase().endsWith(".wpx")) {
                                        model.statusBarText = "Lade WPX-Datei hoch..." as String
                                        if (postWpxFile(f, inputName)) {
                                            fileMsg = fileMsg + f.name + "\n"
                                            isError = isError ?: true
                                        }
                                    }
                                }
                            } else {
                                model.statusBarText = "Lade WPX-Datei hoch..." as String
                                if (postWpxFile(file, inputName)) {
                                    fileMsg = fileMsg + file.name + "\n"
                                    isError = isError ?: true
                                }
                            }
                        } catch (e) {
                            if (DEBUG) println "catching inner each... ${e}"
                            def errMsg = "Fehler beim Übermitteln der WPX-Datei."
                            app.controllers["Dialog"].showErrorDialog("Fehler bei Angebotsverfolgung" as String, errMsg as String)
                            isError = isError ?: true
                        }
                    }
                } else {
                    model.statusBarText = "Lade WPX-Datei hoch..." as String
                    if (postWpxFile(it, inputName)) {
                        fileMsg = fileMsg + it.name + "\n"
                        isError = isError ?: true
                    }
                }
            }
            // do sth. when the task is done.
            onDone {
                def mvc = getMVCGroupAktivesProjekt()
                model.statusBarText = "Alle WPX-Dateien hochgeladen..."
                //
                model.statusProgressBarIndeterminate = false
                model.statusBarText = "Bereit."
                
                def infoMsg = "Übermittlung der WPX-Dateien erfolgreich abgeschlossen."
                if (isError || fileMsg.length() > 0) {
                    infoMsg = "Übermittlung der WPX-Dateien mit Fehler abgeschlossen.\n ${fileMsg}"
                }
                println "Wac2Controller.angebotsverfolgungFilesClosure -> ${app.controllers["Dialog"]?.dump()}"
                app.controllers["Dialog"].showCustomInformDialog("Angebotsverfolgung" as String, infoMsg as String)
            }
        }
    }
    
    // Post text of file object.
    def postWpxFile = { f, inputName -> 
        doOutside {
            try {
                if (DEBUG) println "wacwsUrl -> ${wacwsUrl}"
                // call webservice with paramter
                def result = withWs(wsdl: wacwsUrl) {
                    uploadWpx(f?.text, inputName)
                }
                doLater {
                    if (DEBUG) println "postWpxFile: SOAP Webservice response is: ${result}" as String
                }
                return true
            } catch (e) {
                if (DEBUG) println "postWpxFile: SOAP call failed. Error=${e}"
                return false
            } finally {
                if (DEBUG) println "postWpxFile: End of postWpxFile file ${f.name}..." as String
            }
        }
    }
    
    /**
     * WAC-108: Stückliste generieren
     */
    def stuckliste = {
        // Projekt zur aktiven Tab finden
        def mvc = getMVCGroupAktivesProjekt()
        
        // Erzeuge Stückliste für aktives Projekt.
        mvc.controller.generiereStuckliste()
    }
    
    
	
}

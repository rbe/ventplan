/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/controllers/Wac2Controller.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
import com.westaflex.wac.*
import com.bensmann.griffon.GriffonHelper as GH

/**
 * 
 */
class Wac2Controller {
	
	public static boolean DEBUG = false
	
	def model
	def wacModelService
	def projektModelService
	def view
	def wacCalculationService
	def oooService
	
	/**
	 * Zähler für erstellte/geladene Projekte. Wird als "unique id" verwendet.
	 * Siehe generateMVCId().
	 */
	def static projektCounter = 1
	
	/**
	 * User's settings.
	 */
	private def prefs = java.util.prefs.Preferences.userNodeForPackage(Wac2Controller)
	
	/**
	 * Initialize wac2 MVC group.
	 */
	void mvcGroupInit(Map args) {
		// Lookup values from database and put them into our model
		doOutside {
			// Raumvolumenströme - Bezeichnungen der Zu-/Abluftventile
			model.meta.raumVsBezeichnungZuluftventile =
				model.meta.raumVsBezeichnungAbluftventile =
				wacModelService.getZuAbluftventile()
			// Raumvolumenströme - Überströmelemente
			model.meta.raumVsUberstromelemente = wacModelService.getUberstromelemente()
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
			model.meta.dvbKanalbezeichnung = wacModelService.getDvbKanalbezeichnung()
			// Druckverlustberechnung - Kanalnetz - Widerstandsbeiwerte
			model.meta.wbw = wacModelService.getWbw()
			// Druckverlustberechnung - Ventileinstellung - Ventilbezeichnung
			model.meta.dvbVentileinstellung = wacModelService.getDvbVentileinstellung()
			// Akustikberechnung - 1. Hauptschalldämpfer
			model.meta.akustikSchalldampfer = wacModelService.getSchalldampfer()
		}
	}
	
	/**
	 * Get access to all components of a MVC group by its ID.
	 */
	def getMVCGroup(mvcId) {
		[
			mvcId: mvcId,
			model: app.models[mvcId],
			view: app.views[mvcId],
			controller: app.controllers[mvcId]
		]
	}
	
	/**
	 * Hole MVC Group des aktiven Projekts.
	 */
	def getMVCGroupAktivesProjekt = {
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
	
	/**
	 * 
	 */
	def exitApplication = { evt = null ->
		// Ask if we can close
		def canClose = canClose()
		if (DEBUG) println "exitApplication: ${canClose}"
		if (canClose) {
			_shutdown()
		} else {
			if (DEBUG) println "exitApplication: there are unsaved changes"
			// Show dialog: ask user for save all, cancel, quit
			def choice = app.controllers["Dialog"].showApplicationCloseDialog()
			if (DEBUG) println "exitApplication: choice=${choice}"
			switch (choice) {
				case 0:
					if (DEBUG) println "Alles speichern"
					// TODO rbe Projekte speichern aufrufen
					_shutdown()
					break
				case 1: // Cancel: do nothing...
					if (DEBUG) println "Abbrechen..."
					app.shutdown() // TODO mmu REMOVE THIS LATER
					break
				case 2:
					if (DEBUG) println "Schliessen"
					_shutdown()
					break
			}
		}
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
	def generateMVCId = {
		"Projekt " + (Wac2Controller.projektCounter++)
	}
	
	/**
	 * Ein neues Projekt erstellen.
	 */
	def neuesProjekt = { evt = null ->
		// Splash screen
		doLater {
			Wac2Splash.instance.setup()
			Wac2Splash.instance.creatingProject()
			doOutside {
				// Die hier vergebene MVC ID wird immer genutzt, selbst wenn das Projekt anders benannt wird!
				// (durch Bauvorhaben, Speichern)
				// Es wird also immer "Projekt 1", "Projekt 2" etc. genutzt, nach Reihenfolge der Erstellung
				String mvcId = generateMVCId()
				def (m, v, c) =
					createMVCGroup("Projekt", mvcId,
									[projektTabGroup: view.projektTabGroup, tabName: mvcId, mvcId: mvcId])
				doLater {
					// Splash screen
					Wac2Splash.instance.creatingUiForProject()
					// MVC ID zur Liste der Projekte hinzufügen
					model.projekte << mvcId
					// Projekt aktivieren
					projektAktivieren(mvcId)
					// Splash screen
					Wac2Splash.instance.dispose()
				}
			}
		}
	}
	
	/**
	 * Ein Projekt aktivieren -- MVC ID an Wac2Model übergeben.
	 */
	def projektAktivieren = { mvcId ->
		//if (DEBUG) println "projektAktivieren: mvcId=${mvcId} model.aktivesProjekt=${model.aktivesProjekt}"
		// Anderes Projekt wurde aktiviert?
		if (mvcId && mvcId != model.aktivesProjekt) {
			// MVC ID merken
			model.aktivesProjekt = mvcId
			// Dirty-flag aus Projekt-Model übernehmen
			try {
				def mvcGroup = getMVCGroup(mvcId)
				//if (DEBUG) println "projektAktivieren: getMVCGroup(mvcId)=${mvcGroup}, wpx=${mvcGroup.model?.wpxFilename}"
				model.aktivesProjektGeandert = mvcGroup.model?.map.dirty
			} catch (e) {
				e.printStackTrace()
			}
			//if (DEBUG) println "projektAktivieren: mvcId=${model.aktivesProjekt}"
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
			if (DEBUG) println "projektSchliessen: model.projekte=${model.projekte.dump()}"
			// Anderes Projekt aktivieren?
			// NOT NEEDED projektIndexAktivieren(view.projektTabGroup.selectedIndex)
			// Wird durch die Tab und den ChangeListener erledigt.
		}
		// Projekt zur aktiven Tab finden
		def mvc = getMVCGroupAktivesProjekt()
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
		// Splash screen
		doLater {
			// Choose file
			def openResult = view.wpxFileChooserWindow.showOpenDialog(view.wac2Frame)
			if (javax.swing.JFileChooser.APPROVE_OPTION == openResult) {
				// Save selected file
				def file = view.wpxFileChooserWindow.selectedFile.toString()
				// ... and reset it in FileChooser
				view.wpxFileChooserWindow.selectedFile = null
				if (DEBUG) println "projektOffnen: file=${file?.dump()}"
				// Load data
				Wac2Splash.instance.setup()
				Wac2Splash.instance.loadingProject()
				// Start thread
				def projektModel, projektView, projektController
				doOutside {
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
						// Splash screen
						doLater {
							Wac2Splash.instance.creatingUiForProject()
						}
						// MVC ID zur Liste der Projekte hinzufügen
						model.projekte << mvcId
						// Projekt aktivieren
						projektAktivieren(mvcId)
					} else {
						def errorMsg = "projektOffnen: Konnte Projekt nicht öffnen!"
						app.controllers["Dialog"].showErrorDialog(errorMsg as String)
						if (DEBUG) println errorMsg
					}
					// Bindings and events of ProjektModel are fired now!?
					if (DEBUG) println "-" * 80
					if (DEBUG) println "projektOffnen: ProjektModel bidings/events fire now!?"
					if (DEBUG) println "-" * 80
					// HACK
					if (projektController) {
						doOutside {
							try { Thread.sleep(1 * 1000) } catch (e) {}
							projektController.afterLoading()
						}
					}
				}
			}
		}
	}
	
	/**
	 * Projekt speichern. Es wird der Dateiname aus dem ProjektModel 'wpxFilename' verwendet.
	 * Ist er nicht gesetzt, wird "Projekt speichern als" aufgerufen.
	 * @return Boolean Was project saved sucessfully?
	 */
	def aktivesProjektSpeichern = { evt = null ->
		def mvc = getMVCGroupAktivesProjekt()
		def saved = mvc.controller.save()
		if (saved) {
			if (DEBUG) println "aktivesProjektSpeichern: Projekt gespeichert in ${mvc.model.wpxFilename}"
			saved
		} else {
			if (DEBUG) println "aktivesProjektSpeichern: Projekt nicht gespeichert, kein Dateiname (mvc.model.wpxFilename=${mvc.model.wpxFilename?.dump()})?"
			aktivesProjektSpeichernAls(evt)
		}
	}
	
	/**
	 * Zeige FileChooser, setze gewählten Dateinamen im ProjektModel und rufe "Projekt speichern".
	 * @return Boolean Was project saved sucessfully?
	 */
	def aktivesProjektSpeichernAls = { evt = null ->
		def mvc = getMVCGroupAktivesProjekt()
		// Reset selected filename
		view.wpxFileChooserWindow.selectedFile = null
		// Open filechooser
		def openResult = view.wpxFileChooserWindow.showSaveDialog(view.wac2Frame)
		if (javax.swing.JFileChooser.APPROVE_OPTION == openResult) {
			mvc.model.wpxFilename = view.wpxFileChooserWindow.selectedFile.toString()
			if (DEBUG) println "projektSpeichernAls: wpxFilename=${mvc.model.wpxFilename?.dump()}"
			// Save data
			aktivesProjektSpeichern(evt)
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
	
}

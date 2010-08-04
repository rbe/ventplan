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
	
	def model
	def wacModelService
	def projektModelService
	def view
	def wacCalculationService
	
	/**
	 * User's settings.
	 */
	private def prefs = java.util.prefs.Preferences.userNodeForPackage(Wac2Controller)
	
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
			model.meta.volumenstromZentralgerat =
				wacModelService.getVolumenstromFurZentralgerat(model.meta.zentralgerat[0])
			// Druckverlustberechnung - Kanalnetz - Kanalbezeichnung
			model.meta.dvbKanalbezeichnung = wacModelService.getDvbKanalbezeichnung()
			// Druckverlustberechnung - Kanalnetz - Widerstandsbeiwerte
			model.meta.wbw = wacModelService.getWbw()
			// Druckverlustberechnung - Ventileinstellung - Ventilbezeichnung
			model.meta.dvbVentileinstellung = wacModelService.getDvbVentileinstellung()
		}
	}
	
	/**
	 * Get access to all components of a MVC group by its ID.
	 */
	def getMVCGroup(id) {
		[model: app.models[id], view: app.views[id], controller: app.controllers[id]]
	}
	
	/**
	 * Hole MVC Group des aktiven Projekts.
	 */
	def getMVCGroupAktivesProjekt = {
		println "getMVCGroupAktivesProjekt: model.aktivesProjekt=${model.aktivesProjekt}"
		getMVCGroup(model.aktivesProjekt)
	}
	
	/**
	 * Schliessen? Alle Projekte fragen, ob ungesicherte Daten existieren.
	 */
	boolean canClose() {
		model.projekte.inject(true) { o, n ->
			def c = getMVCGroup(n).controller
			println "o=${o} c.canClose=${c.canClose()}"
			o &= c.canClose()
		}
	}
	
	/**
	 * 
	 */
	def exitApplication = { evt = null ->
		// Ask if we can close
		def canClose = canClose()
		println "exitApplication: ${canClose}"
		if (canClose) {
			app.shutdown()
		} else {
			println "windowClosing(${evt.dump()}): there are unsaved changes"
			// TODO mmu Show dialog: ask user for save all, cancel, quit
			println "projektSchliessen: there's unsaved data"
			def choice = app.controllers["Dialog"].showApplicationCloseDialog()
			println "exitApplication: choice=${choice}"
			switch (choice) {
				case 0:
					println "Alles speichern"
					// TODO rbe Projekte speichern aufrufen
					app.shutdown()
					break
				case 1:
					// Cancel: do nothing...
					println "Abbrechen..."
					app.shutdown() // REMOVE THIS LATER
					break
				case 2:
					println "Schliessen"
					app.shutdown()
					break
			}
		}
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
				String mvcId = "Projekt " + (view.projektTabGroup.tabCount + 1)
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
		// Anderes Projekt wurde aktiviert
		if (mvcId && mvcId != model.aktivesProjekt) {
			// MVC ID merken
			model.aktivesProjekt = mvcId
			// Dirty-flag aus Projekt-Model übernehmen
			try {
				println "projektAktivieren: getMVCGroup(mvcId)=" + getMVCGroup(mvcId)
				model.aktivesProjektGeandert = getMVCGroup(mvcId).model?.map.dirty
			} catch (e) {
				e.printStackTrace()
			}
			println "projektAktivieren: mvcId=${model.aktivesProjekt}"
		}
		/*
		else {
			println "projektAktivieren: no change"
		}
		*/
	}
	
	/**
	 * Ein Projekt aktivieren -- MVC ID an Wac2Model übergeben.
	 */
	def projektIndexAktivieren = { index ->
		if (index > -1) {
			projektAktivieren(model.projekte[index])
			println "projektIndexAktivieren: index=${index} -> ${model.aktivesProjekt}"
		}
		/*
		else {
			println "projektIndexAktivieren: index=${index}: Kein Projekt vorhanden!"
		}
		*/
	}
	
	/**
	 * Das aktive Projekt schliessen.
	 */
	def projektSchliessen = { evt = null ->
		// Projekt zur aktiven Tab finden
		def mvc = getMVCGroupAktivesProjekt()
		println "projektSchliessen: model.aktivesProjekt=${model.aktivesProjekt} mvc=${mvc}"
		def canClose = mvc.controller.canClose()
		if (!canClose) {
			println "projektSchliessen: canClose=${canClose}, there's unsaved data"
			//def options = ['Speichern', 'Abbrechen', 'Schliessen']
			def choice = app.controllers["Dialog"].showCloseProjectDialog()
			println "projektSchliessen: choice=${choice}"
			switch (choice) {
				case 0:
					// Save: save the closing project
					println "Speichern und Beenden"
					projektSpeichern(evt)
					// MVC Gruppe zerstören
					destroyMVCGroup(model.aktivesProjekt)
					// Aus Liste der Projekte entfernen
					model.projekte.remove(model.aktivesProjekt)
					// Tab entfernen
					view.projektTabGroup.remove(view.projektTabGroup.selectedComponent)
					// Anderes Projekt aktivieren?
					projektIndexAktivieren(view.projektTabGroup.selectedIndex)
					break
				case 1:
					// Cancel: do nothing...
					println "projektSchliessen: Abbrechen"
					break
				case 2:
					// Close: just close the tab...
					println "projektSchliessen: Schliessen ohne Speichern"
					// MVC Gruppe zerstören
					destroyMVCGroup(model.aktivesProjekt)
					// Aus Liste der Projekte entfernen
					model.projekte.remove(model.aktivesProjekt)
					// Tab entfernen
					view.projektTabGroup.remove(view.projektTabGroup.selectedComponent)
					// Anderes Projekt aktivieren?
					projektIndexAktivieren(view.projektTabGroup.selectedIndex)
					break
			}
		} else {
			println "projektSchliessen: else... close!!"
			// MVC Gruppe zerstören
			destroyMVCGroup(model.aktivesProjekt)
			// Aus Liste der Projekte entfernen
			model.projekte.remove(model.aktivesProjekt)
			// Tab entfernen
			view.projektTabGroup.remove(view.projektTabGroup.selectedComponent)
			// Anderes Projekt aktivieren?
			projektIndexAktivieren(view.projektTabGroup.selectedIndex)
		}
	}
	
	/**
	 * Projekt öffnen: zeige FileChooser, lese XML, erstelle eine MVC Group und übertrage die Werte
	 * aus dem XML in das ProjektModel.
	 */
	def projektOffnen = { evt = null ->
		// Splash screen
		doLater {
			doOutside {
				// Choose file
				def file
				def openResult = view.fileChooserWindow.showOpenDialog(view.wac2Frame)
				if (javax.swing.JFileChooser.APPROVE_OPTION == openResult) {
					file = view.fileChooserWindow.selectedFile.toString()
					println "projektOffnen: file=${file?.dump()}"
					// Load data
					Wac2Splash.instance.setup()
					Wac2Splash.instance.loadingProject()
					// May return null due to org.xml.sax.SAXParseException while validating against XSD
					def document = projektModelService.load(file)
					println "projektOffnen: document=${document?.dump()}"
					if (document) {
						// Create new Projekt MVC group
						String mvcId = "Projekt " + (view.projektTabGroup.tabCount + 1)
						def (m, v, c) =
							createMVCGroup("Projekt", mvcId,
											[projektTabGroup: view.projektTabGroup, tabName: mvcId, mvcId: mvcId])
						// Convert loaded XML into map
						def map = projektModelService.toMap(document)
						// Recursively copy map to model
						GH.deepCopyMap m.map, map
						// MVC ID zur Liste der Projekte hinzufügen
						model.projekte << mvcId
						// Projekt aktivieren
						projektAktivieren(mvcId)
						// Update splash screen, UI
						doLater {
							// Splash screen
							Wac2Splash.instance.creatingUiForProject()
							// Set dirty-flag in project's model to false
							m.map.dirty = false
							// Update tab title to ensure that no "unsaved-data-star" is displayed
							c.setTabTitle()
						}
					} else {
						// TODO mmu Show error dialog
						println "projektOffnen: Konnte Projekt nicht öffnen!"
					}
					// Splash screen
					Wac2Splash.instance.dispose()
				}
			}
		}
	}
	
	/**
	 * Projekt speichern. Es wird der Dateiname aus dem ProjektModel 'wpxFilename' verwendet.
	 * Ist er nicht gesetzt, wird "Projekt speichern als" aufgerufen.
	 */
	def projektSpeichern = { evt = null ->
		def p = getMVCGroupAktivesProjekt()
		def m = p.model
		// Do we have a filename? If not, redirect to save-as
		if (m.wpxFilename) {
			// Save data
			projektModelService.save(m.map, m.wpxFilename)
			// Set dirty-flag in project's model to false
			m.map.dirty = false
			// Update tab title to ensure that no "unsaved-data-star" is displayed
			p.controller.setTabTitle()
		} else {
			projektSpeichernAls(evt)
		}
	}
	
	/**
	 * Zeige FileChooser, setze gewählten Dateinamen im ProjektModel und rufe "Projekt speichern".
	 */
	def projektSpeichernAls = { evt = null ->
		def m = getMVCGroupAktivesProjekt().model
		// Open filechooser
		def openResult = view.fileChooserWindow.showSaveDialog(view.wac2Frame)
		println openResult
		if (javax.swing.JFileChooser.APPROVE_OPTION == openResult) {
			m.wpxFilename = view.fileChooserWindow.selectedFile.toString()
			println "projektSpeichernAls: wpxFilename=${m.wpxFilename?.dump()}"
			// Save data
			projektSpeichern(evt)
		}
	}
	
	/**
	 * TODO rbe
	 */
	def projektSeitenansicht = { evt = null ->
	}
	
	/**
	 * TODO rbe
	 */
	def projektDrucken = { evt = null ->
	}
	
}

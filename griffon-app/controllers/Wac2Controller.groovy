/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/controllers/Wac2Controller.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
import com.westaflex.wac.*

/**
 * 
 */
class Wac2Controller {
	
	def model
	def view
	def wacModelService
	def projektModelService
	
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
	def getMvcGroupAktivesProjekt = {
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
		def canClose = canClose()
		println "exitApplication: ${canClose}"
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
					createMVCGroup("Projekt", mvcId, [projektTabGroup: view.projektTabGroup, tabName: mvcId, mvcId: mvcId])
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
		def mvc = getMvcGroupAktivesProjekt() //getMVCGroup(model.aktivesProjekt)
		println "projektSchliessen: model.aktivesProjekt=${model.aktivesProjekt} mvc=${mvc}"
		def canClose = mvc.controller.canClose()
		if (canClose) {
			// MVC Gruppe zerstören
			destroyMVCGroup(model.aktivesProjekt)
			// Aus Liste der Projekte entfernen
			model.projekte.remove(model.aktivesProjekt)
			// Tab entfernen
			view.projektTabGroup.remove(view.projektTabGroup.selectedComponent)
			// Anderes Projekt aktivieren?
			projektIndexAktivieren(view.projektTabGroup.selectedIndex)
		} else {
			// TODO mmu Dialog anzeigen: Speichern, Abbrechen, Schliessen
			println "projektSchliessen: there's unsaved data"
		}
	}
	
	/**
	 * TODO rbe
	 */
	def projektOffnen = { evt = null ->
		// Splash screen
		doLater {
			Wac2Splash.instance.setup()
			Wac2Splash.instance.loadingProject()
			doOutside {
				// Load data
				def document = projektModelService.load("/Users/rbe/wac2.xml")
				def map = projektModelService.toMap(document)
				// Set dirty-flag in project's model to false
				map.dirty = false
				//
				String mvcId = "Projekt " + (view.projektTabGroup.tabCount + 1)
				def (m, v, c) =
					createMVCGroup("Projekt", mvcId, [projektTabGroup: view.projektTabGroup, tabName: mvcId, mvcId: mvcId])
				// Recursively copy map
				m.map.kundendaten.bauvorhaben = map.kundendaten.bauvorhaben
				m.map.dirty = map.dirty
				doLater {
					// Splash screen
					Wac2Splash.instance.creatingUiForProject()
					// Update tab title to ensure that no "unsave-data-star" is displayed
					c.setTabTitle()
					// Splash screen
					Wac2Splash.instance.dispose()
				}
			}
		}
	}
	
	/**
	 * TODO rbe
	 */
	def projektSpeichern = { evt = null ->
		// Save data
		println projektModelService.save(getMvcGroupAktivesProjekt().model.map, null)
		// Set dirty-flag in project's model to false
	}
	
	/**
	 * TODO rbe
	 */
	def projektSpeichernAls = { evt = null ->
		// Open filechooser
		// Save data
		// Set dirty-flag in project's model to false
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

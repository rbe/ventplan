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
	
	void mvcGroupInit(Map args) {
		// Lookup values from database and put them into our model
		doOutside {
			model.meta.raumVsBezeichnungZuluftventile =
				model.meta.raumVsBezeichnungAbluftventile =
				wacModelService.getZuAbluftventile()
			model.meta.raumVsUberstromelement = wacModelService.getUberstromelemente()
			model.meta.zentralgerat = wacModelService.getZentralgerat()
			model.meta.volumenstromZentralgerat = wacModelService.getVolumenstromFurZentralgerat(model.meta.zentralgerat[0])
		}
	}
	
	/**
	 * Schliessen? Alle Projekte fragen, ob ungesicherte Daten existieren.
	 */
	boolean canClose() {
		model.projekte.inject(true) { o, n ->
			println "o=${o} n.controller.canClose=${n.controller.canClose()}"
			o &= n.controller.canClose()
		}
	}
	
	/**
	 * Ein neues Projekt erstellen.
	 */
	def neuesProjekt = { evt = null ->
		doOutside {
			String mvcId = "Projekt " + (view.projektTabGroup.tabCount + 1)
			def (m, v, c) = createMVCGroup("Projekt", mvcId, [projektTabGroup: view.projektTabGroup, tabName: mvcId, mvcId: mvcId])
			doLater {
				// Add created MVC group 'Projekt' to list of active projects
				model.aktivesProjekt.model = m
				model.aktivesProjekt.view = v
				model.aktivesProjekt.controller = c
				model.projekte << model.aktivesProjekt //[model: m, view: v, controller: c]
			}
		}
	}
	
	/**
	 * TODO rbe
	 */
	def projektSchliessen = { evt = null ->
	}
	
	/**
	 * TODO rbe
	 */
	def projektLaden = { evt = null ->
		// Load data
		// Set dirty-flag in project's model to false
	}
	
	/**
	 * TODO rbe
	 */
	def projektSpeichern = { evt = null ->
		// Load data
		// Set dirty-flag in project's model to false
	}
	
}

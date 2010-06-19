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
	
	void mvcGroupInit(Map args) {
	}
	
	/**
	 * 
	 */
	def neuesProjekt = { evt = null ->
		doOutside {
			String mvcId = "Projekt " + (view.projektTabGroup.tabCount + 1)
			def (m, v, c) = createMVCGroup("Projekt", mvcId, [projektTabGroup: view.projektTabGroup, tabName: mvcId, mvcId: mvcId])
			doLater {
				// Add new 'Projekt'-model to list of active models
				model.projekte += m
			}
		}
	}
	
}

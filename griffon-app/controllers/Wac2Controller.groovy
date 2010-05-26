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

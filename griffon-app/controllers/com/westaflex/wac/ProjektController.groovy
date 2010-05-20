package com.westaflex.wac

import java.beans.PropertyChangeListener

/**
 * 
 */
class ProjektController {
	
	def model
	def view
	
	void mvcGroupInit(Map args) {
		model.mvcId = args.mvcId
		// Compute a hash of the map when data is entered. Compare this hash to a previously
		// stored hash value to see if the data has changed
		def computeHash = { evt ->
		} as PropertyChangeListener
		// Add PropertyChangeListener to model
		model.map.addPropertyChangeListener({ evt ->
			model.map.each { k, v ->
				println "${k}: size=${model.map[k].size()} class=${model.map[k].class}"
			}
			println ""
		} as PropertyChangeListener)
		model.map.each { k, v ->
			println "ProjektController.mvcGroupInit: adding PropertyChangeListener for ${k}"
			v.addPropertyChangeListener({ evt ->
				println "${k}: ${model.map[k]}"
				println ""
			} as PropertyChangeListener)
		}
	}
	
}

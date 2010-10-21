/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/events/com/westaflex/wac/AussenlufVsEvents.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

/**
 * 
 */
@griffon.util.EventPublisher
class AussenluftVsEvents {
	
	def model
	def wacCalculationService
	def wacModelService
	
	/**
	 * Execute code 'later'.
	 */
	def doLater = { closure ->
		javax.swing.SwingUtilities.invokeLater closure
	}
	
	/**
	 * 
	 */
	def onAussenluftVsBerechnen = {
		doLater {
			//println "processing event 'AussenluftVsBerechnen'"
			wacCalculationService.aussenluftVs(model.map)
			// Zentralgerät bestimmen
			publishEvent "ZentralgeratAktualisieren"
		}
	}
	
}

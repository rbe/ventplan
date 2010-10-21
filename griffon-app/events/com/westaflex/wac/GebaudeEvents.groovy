/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/events/com/westaflex/wac/GebaudeEvents.groovy
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
class GebaudeEvents {
	
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
	def onGeometrieEingegeben = {
		doLater {
			//println "processing event 'GeometrieEingegeben'"
			wacCalculationService.geometrie(model.map)
			wacCalculationService.aussenluftVs(model.map)
			// Zentralgerät bestimmen
			publishEvent "ZentralgeratAktualisieren"
		}
	}
	
}

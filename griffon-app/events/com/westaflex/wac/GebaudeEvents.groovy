/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/events/com/westaflex/wac/GebaudeEvents.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
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
	 * 
	 */
	def onGeometrieEingegeben = {
		javax.swing.SwingUtilities.invokeLater {
			println "processing event 'GeometrieEingegeben'"
			wacCalculationService.geometrie(model.map)
			wacCalculationService.aussenluftVs(model.map)
		}
	}
	
}

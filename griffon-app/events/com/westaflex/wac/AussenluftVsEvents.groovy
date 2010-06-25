/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/events/com/westaflex/wac/AussenlufVsEvents.groovy
 * 
 * Copyright (C) 1996-2010 Informationssysteme Ralf Bensmann.
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
class AussenluftVsEvents {
	
	def model
	def wacCalculationService
	def wacModelService
	
	/**
	 * 
	 */
	def onAussenluftVsBerechnen = {
		javax.swing.SwingUtilities.invokeLater {
			println "processing event 'AussenluftVsBerechnen'"
			wacCalculationService.aussenluftVs(model.map)
		}
	}
	
}
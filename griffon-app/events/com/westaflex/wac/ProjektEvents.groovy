/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/events/ProjektEvents.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.westaflex.wac

/**
 * 
 */
@griffon.util.EventPublisher
class ProjektEvents {
	
	def model
	def wacCalculationService
	
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
	
	/**
	 * 
	 */
	def onAussenluftVsBerechnen = {
		javax.swing.SwingUtilities.invokeLater {
			println "processing event 'AussenluftVsBerechnen'"
			wacCalculationService.aussenluftVs(model.map)
		}
	}
	
	/**
	 * 
	 */
	def onRaumHinzugefugt = {
		javax.swing.SwingUtilities.invokeLater {
			println "processing event 'RaumHinzugefugt'"
			wacCalculationService.geometrieAusRaumdaten(model.map)
			wacCalculationService.aussenluftVs(model.map)
			model.syncRaumTableModels()
			publishEvent "RaumInTabelleWahlen", [model.map.raum.raume.size() - 1]
		}
	}
	
	/**
	 * 
	 */
	def onRaumEntfernt = {
		println "processing event 'RaumEntfernt', delegating to 'RaumHinzugefugt'"
		onRaumHinzugefugt()
	}
	
	/**
	 * 
	 */
	def onRaumZuAbluftventileLuftmengeBerechnen = { raumIndex ->
		println "onRaumZuAbluftventileLuftmengeBerechnen: Berechne Luftmenge für Raum Nr. ${raumIndex}"
		javax.swing.SwingUtilities.invokeLater {
			wacCalculationService.raumLuftmengeBerechnen(map, raumIndex)
			model.syncRaumTableModels()
		}
	}
	
	/**
	 * 
	 */
	def onRaumUberstromelementeLuftmengeBerechnen = { raumIndex ->
		println "onRaumUberstromelementeLuftmengeBerechnen: Berechne Luftmenge für Raum Nr. ${raumIndex}"
		javax.swing.SwingUtilities.invokeLater {
			wacCalculationService.raumLuftmengeBerechnen(map, raumIndex)
			model.syncRaumTableModels()
		}
	}
	
}

//package com.westaflex.wac

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
	
}

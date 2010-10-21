/**
 * /Users/rbe/project/wac2/griffon-app/events/com/westaflex/wac/RaumVsEvents.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

/**
 * 
 */
@griffon.util.EventPublisher
class RaumVsEvents {
	
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
	 * Das Zentralgerät wurde geändert.
	 */
	def onZentralgeratGewahlt = { zentralgerat ->
		// TODO rbe Compare old and new value; only change values when old != new?
		doLater {
			// Merken, dass das Zentralgerät manuell ausgewählt wurde
			// -> keine automatische Auswahl des Zentralgeräts mehr durchführen
			model.map.anlage.zentralgeratManuell = true
			// Hole Volumenströme des Zentralgeräts
			model.meta.volumenstromZentralgerat =
				wacModelService.getVolumenstromFurZentralgerat(zentralgerat)
			// Im Projekt-Model speichern
			model.map.anlage.zentralgerat = zentralgerat
			// Aussenluftvolumenströme neu berechnen
			wacCalculationService.aussenluftVs(model.map)
		}
	}
	
}

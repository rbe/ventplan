/**
 * /Users/rbe/project/wac2/griffon-app/events/com/westaflex/wac/DvbKanalnetzEvents.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

/**
 * 
 */
@griffon.util.EventPublisher
class DvbKanalnetzEvents {
	
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
	def onDvbKanalnetzHinzufugen = { kanalnetz ->
		doLater {
			// Map values from GUI
			def k = [
					druckverlustLuftartCombo: kanalnetz.dvbKanalnetzLuftart,
					teilstrecke: kanalnetz.dvbKanalnetzNrTeilstrecke?.toInteger(),
					luftVs: kanalnetz.dvbKanalnetzLuftmenge?.toDouble2(),
					druckverlustKanalbezeichnungCombo: kanalnetz.dvbKanalnetzKanalbezeichnung,
					lange: kanalnetz.dvbKanalnetzLange?.toDouble2(),
					position: model.map.dvb.kanalnetz.size()
				] as ObservableMap
			model.addDvbKanalnetz(k)
			//
			onDvbKanalnetzGeandert(k.position)
			publishEvent "AddDvbKanalnetzToTableModel", [k]
		}
	}
	
	/**
	 * 
	 */
	def onDvbKanalnetzGeandert = { kanalnetzIndex ->
		doLater {
			// Add PropertyChangeListener to our model.map
			GH.addMapPropertyChangeListener("map.dvb.kanalnetz", model.map.dvb.kanalnetz[kanalnetzIndex])
			// Berechne die Teilstrecke
			model.map.dvb.kanalnetz[kanalnetzIndex] =
				wacCalculationService.berechneTeilstrecke(model.map.dvb.kanalnetz[kanalnetzIndex])
			//
			publishEvent "DvbKanalnetzInTabelleWahlen", [kanalnetzIndex]
		}
	}
	
}

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
class DvbVentileinstellungEvents {
	
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
	def onDvbVentileinstellungHinzufugen = { ventileinstellung ->
		doLater {
			// Map values from GUI
			def v = [
					luftart: ventileinstellung.dvbVentileinstellungLuftart,
					raum: ventileinstellung.dvbVentileinstellungRaum,
					teilstrecken: ventileinstellung.dvbVentileinstellungTeilstrecken,
					ventilbezeichnung: ventileinstellung.dvbVentileinstellungVentilbezeichnung,
					position: model.map.dvb.ventileinstellung.size() ?: 0
				] as ObservableMap
			model.addDvbVentileinstellung(v)
			//
			onDvbVentileinstellungGeandert(v.position)

            publishEvent "AddDvbVentileinstellungToTableModel", [v]
		}
	}
	
	/**
	 * 
	 */
	def onDvbVentileinstellungGeandert = { ventileinstellungIndex ->
		doLater {
			// Add PropertyChangeListener to our model.map
			GH.addMapPropertyChangeListener("map.dvb.ventileinstellung",
				model.map.dvb.ventileinstellung[ventileinstellungIndex])
			//
			model.map.dvb.ventileinstellung[ventileinstellungIndex] =
				wacCalculationService.berechneVentileinstellung(model.map)
			//
			publishEvent "DvbVentileinstellungInTabelleWahlen", [ventileinstellungIndex]
		}
	}
	
}

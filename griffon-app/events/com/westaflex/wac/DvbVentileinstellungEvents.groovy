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
	def onDvbVentileinstellungHinzufugen = { ventileinstellung, view ->
		doLater {
			// Map values from GUI
			def v = [
					luftart: ventileinstellung.dvbVentileinstellungLuftart,
					raum: ventileinstellung.dvbVentileinstellungRaum,
					teilstrecken: ventileinstellung.dvbVentileinstellungTeilstrecken,
					ventilbezeichnung: ventileinstellung.dvbVentileinstellungVentilbezeichnung,
					position: model.map.dvb.ventileinstellung.size() ?: 0
				] as ObservableMap
			model.addDvbVentileinstellung(v, view)
			def index = v.position
			// Add PropertyChangeListener to our model.map
			GH.addMapPropertyChangeListener("map.dvb.ventileinstellung",
				model.map.dvb.ventileinstellung[index])
			//
			onDvbVentileinstellungGeandert(index)
		}
	}
	
	/**
	 * 
	 */
	def onDvbVentileinstellungGeandert = { ventileinstellungIndex ->
		doLater {
			wacCalculationService.berechneVentileinstellung(model.map)
			//
			publishEvent "DvbVentileinstellungInTabelleWahlen", [ventileinstellungIndex]
		}
	}
	
	/**
	 * Zeile aus Druckverlustberechnung Ventileinstellung entfernen.
	 */
	def onDvbVentileinstellungEntfernen = { ventileinstellungIndex ->
		doLater {
			//println "onDvbVentileinstellungEntfernen: ventileinstellungIndex=${ventileinstellungIndex}"
			// Zeile aus Model entfernen
			model.removeDvbVentileinstellung(ventileinstellungIndex)
		}
	}
	
}

package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

/**
 * 
 */
//@griffon.util.EventPublisher
class ProjektController {
	
	def model
	def view
	def wacCalculationService
	
	/**
	 * Initialize MVC group.
	 */
	void mvcGroupInit(Map args) {
		model.mvcId = args.mvcId
		// Add PropertyChangeListener to model
		model.map.addPropertyChangeListener({ evt ->
			model.map.each { k, v ->
				println "${k}: size=${model.map[k].size()} class=${model.map[k].class}"
				println ""
			}
		} as java.beans.PropertyChangeListener)
		model.map.each { k, v ->
			println "ProjektController.mvcGroupInit: adding PropertyChangeListener for ${k}"
			v.addPropertyChangeListener({ evt ->
				println "${k}: ${model.map[k]}"
				println ""
			} as java.beans.PropertyChangeListener)
		}
	}
	
	/**
	 * Gebäudedaten - Geometrie
	 */
	def berechneGeometrie = {
		def g = model.map.gebaude.geometrie
		try {
			// Luftvolumen der Nutzungseinheit = Wohnfläche * mittlere Raumhöhe
			if (g.wohnflache && g.raumhohe) {
				g.luftvolumen = g.geluftetesVolumen = GH.sf(wacCalculationService.volumen(GH.pf(g.wohnflache), GH.pf(g.raumhohe)))
			}
			// Gelüftetes Volumen = gelüftete Fläche * mittlere Raumhöhe
			if (g.gelufteteFlache && g.raumhohe) {
				g.geluftetesVolumen = GH.sf(wacCalculationService.volumen(GH.pf(g.gelufteteFlache), GH.pf(g.raumhohe)))
			}
			// Gelüftetes Volumen = Luftvolumen, wenn kein gelüftetes Volumen berechnet
			if (g.luftvolumen && !g.geluftetesVolumen) {
				g.geluftetesVolumen = g.luftvolumen
			}
		} catch (e) {
			//e.printStackTrace()
			g.luftvolumen = g.geluftetesVolumen = "E"
		}
		model.map.gebaude.geometrie = g
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def luftdichtheitKategorieA = {
		model.map.gebaude.luftdichtheit.with {
			druckdifferenz = "2,00"
			luftwechsel = "1,00"
			druckexponent = "0,666"
		}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def luftdichtheitKategorieB = {
		model.map.gebaude.luftdichtheit.with {
			druckdifferenz = "2,00"
			luftwechsel = "1,50"
			druckexponent = "0,666"
		}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def luftdichtheitKategorieC = {
		model.map.gebaude.luftdichtheit.with {
			druckdifferenz = "2,00"
			luftwechsel = "2,00"
			druckexponent = "0,666"
		}
	}
	
	/**
	 * Gebäudedaten - Geplante Belegung
	 */
	def berechneMindestaussenluftrate = {
		model.map.gebaude.geplanteBelegung.with {
			try {
				mindestaussenluftrate = personenanzahl * aussenluftVsProPerson
			} catch (e) {
				mindestaussenluftrate = 0
			}
		}
	}
	
	/**
	 * Anlagendaten - Energie-Kennzeichen
	 */
	def berechneEnergieKennzeichen = {
		def all = model.map.anlage.energie.with {
			zuAbluftWarme && bemessung && ruckgewinnung && regelung
		}
		if (all) {
			model.map.anlage.energie.nachricht = "Energiekennzeichen gesetzt!"
		} else {
			model.map.anlage.energie.nachricht = " "
		}
	}
	
}

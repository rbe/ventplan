package com.westaflex.wac

/**
 * 
 */
@griffon.util.EventPublisher
class ProjektController {
	
	def model
	def view
	def wacCalculationService
	
	void mvcGroupInit(Map args) {
		model.mvcId = args.mvcId
		// Compute a hash of the map when data is entered. Compare this hash to a previously
		// stored hash value to see if the data has changed
		def computeHash = { evt ->
		} as java.beans.PropertyChangeListener
		// Add PropertyChangeListener to model
		model.map.addPropertyChangeListener({ evt ->
			model.map.each { k, v ->
				println "${k}: size=${model.map[k].size()} class=${model.map[k].class}"
			}
			println ""
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
	 * Kategorie A
	def onAnotherEvent = {
		println "onAnotherEvent"
		model.map.gebaude.luftdichtheit.with {
			druckdifferenz = 2.0
			luftwechsel = 1.0
			druckexponent = 0.666
		}
	}
	 */
	
	/**
	 * Gebäudedaten - Geometrie
	 */
	def berechneGeometrie() {
		if (model.map.gebaeude.geometrie.wohnflaeche)
		if (!geoWohnflaecheTextField.getText().isEmpty() && !geoHoeheTextField.getText().isEmpty()) {
			fFlaeche = ConversionUtil.parseFloatFromComponent(geoWohnflaecheTextField);
			fHoehe = ConversionUtil.parseFloatFromComponent(geoHoeheTextField);
			fVol = fFlaeche * fHoehe;
			setGesamtVolumen(fVol);
			if (geoGeluefteteflaecheTextField.getText().isEmpty()) {
				setGelueftetVolumen(fVol);
			}
		}
		if (!geoGeluefteteflaecheTextField.getText().isEmpty() && !geoHoeheTextField.getText().isEmpty()) {
			fFlaeche = ConversionUtil.parseFloatFromComponent(geoGeluefteteflaecheTextField);
			fHoehe = ConversionUtil.parseFloatFromComponent(geoHoeheTextField);
			fVol = fFlaeche * fHoehe;
			setGelueftetVolumen(fVol);
		}
		if (!geoVolumenTextField.getText().isEmpty() && geoGelueftetesVolumenTextField.getText().isEmpty()) {
			setGelueftetVolumen(ConversionUtil.parseFloatFromComponent(geoVolumenTextField));
		}
	}
	
}

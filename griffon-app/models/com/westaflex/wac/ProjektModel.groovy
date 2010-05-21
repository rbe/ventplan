package com.westaflex.wac

import groovy.beans.Bindable

/**
 * 
 */
class ProjektModel {
	
	/**
	 * The MVC id.
	 */
	String mvcId
	
	/**
	 * Is the model changed (since last save?)
	 */
	boolean dirty
	
	/**
	 * 
	 */
	@Bindable map = [
		kundendaten: [
			grosshandel: [:] as ObservableMap,
			ausfuhrendeFirma: [:] as ObservableMap,
		] as ObservableMap,
		gebaude: [
				typ: [EFH: true] as ObservableMap,
				lage: [windschwach: true] as ObservableMap,
				warmeschutz: [hoch: true] as ObservableMap,
				geometrie: [:] as ObservableMap,
				luftdichtheit: [
						kategorieA: true,
						druckdifferenz: "2,00",
						luftwechsel: "1,00",
						druckexponent: "0,666"
					] as ObservableMap,
				faktorBesondereAnforderungen: 1,
				geplanteBelegung: [
						personenanzahl: 0,
						aussenluftVsProPerson: 30
					] as ObservableMap,
			] as ObservableMap,
		anlage: [
				standort: [EG: true] as ObservableMap,
				luftkanalverlegung: [:] as ObservableMap,
				aussenluft: [:] as ObservableMap,
				zuluft: [:] as ObservableMap,
				abluft: [:] as ObservableMap,
				fortluft: [dach: true] as ObservableMap,
				energie: [zuAbluftWarme: true] as ObservableMap,
				hygiene: [:] as ObservableMap,
				ruckschlag: [:] as ObservableMap,
				schallschutz: [:] as ObservableMap,
				feuerstatte: [:] as ObservableMap,
				kzLuftung: "ZuAbLS-Z-WE-WÜT-0-0-0-0-0",
			] as ObservableMap,
		raum: [:] as ObservableMap,
		aussenluftVs: [:] as ObservableMap,
		raumVs: [:] as ObservableMap,
		druckverlust: [:] as ObservableMap,
		akkustik: [:] as ObservableMap
	] as ObservableMap
	
	/**
	 * Compute a hash of the map when data is entered. Compare this hash to a previously
	 * stored hash value to see if the data has changed.
	 */
	def computeHash = { evt ->
	}
	
}
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
				luftdichtheit: [kategorieA: true] as ObservableMap,
				faktorBesondereAnforderungen: 1,
				geplanteBelegung: [personenanzahl: 0, aussenluftVolumenstromProPerson: 30] as ObservableMap,
			] as ObservableMap,
		anlage: [:] as ObservableMap,
		raum: [:] as ObservableMap,
		aussenluftVs: [:] as ObservableMap,
		raumVs: [:] as ObservableMap,
		druckverlust: [:] as ObservableMap,
		akkustik: [:] as ObservableMap
	] as ObservableMap
	
}
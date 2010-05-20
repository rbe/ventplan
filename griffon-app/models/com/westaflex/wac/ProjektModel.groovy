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
		grosshandel: [:] as ObservableMap,
		ausfuehrendeFirma: [:] as ObservableMap,
		gebaeude: [gebaeudeTypEFH: true, gebaeudeLageWindschwach: true, gebaeudeWaermeschutzHoch: true] as ObservableMap,
		anlage: [:] as ObservableMap,
		raum: [:] as ObservableMap,
		aussenluftVs: [:] as ObservableMap,
		raumVs: [:] as ObservableMap,
		druckverlust: [:] as ObservableMap,
		akkustik: [:] as ObservableMap
	] as ObservableMap
	
}
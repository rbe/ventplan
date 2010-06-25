import groovy.beans.Bindable

import com.westaflex.wac.*

/**
 * 
 */
class Wac2Model {
	
	/**
	 * Meta-data.
	 */
	@Bindable meta = [
		raum: [
				typ: ["Wohnzimmer", "Kinderzimmer", "Schlafzimmer", "Esszimmer", "Arbeitszimmer", "Gästezimmer", "Hausarbeitsraum", "Kellerraum", "WC", "Küche", "Kochnische", "Bad mit/ohne WC", "Duschraum", "Sauna", "Flur", "Diele"],
				geschoss: ["KG", "EG", "OG", "DG", "SB"],
				luftart: ["ZU", "AB", "ZU/AB", "ÜB"],
				raumVsBezeichnungZuluftventile: [/* initialized in ProjektController.mvcGroupInit */],
				raumVsBezeichnungAbluftventile: [/* initialized in ProjektController.mvcGroupInit */],
				raumVsUberstromelemente: [/* initialized in ProjektController.mvcGroupInit */],
				raumVsVentilebene: ["KG", "EG", "OG", "DG", "SB"],
			],
		gewahlterRaum: [:] as ObservableMap
	] as ObservableMap
	
	/**
	 * Aktive Projekte.
	 */
	@Bindable projekte = []
	
}

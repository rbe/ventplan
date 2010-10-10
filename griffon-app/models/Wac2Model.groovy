/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/models/Wac2Model.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
import groovy.beans.Bindable

import com.westaflex.wac.*

/**
 * 
 */
class Wac2Model {
	
	/**
	 * Meta-data.
	 */
	def meta = [
		raum: [
				typ: ["Wohnzimmer", "Kinderzimmer", "Schlafzimmer", "Esszimmer", "Arbeitszimmer", "Gästezimmer", "Hausarbeitsraum", "Kellerraum", "WC", "Küche", "Kochnische", "Bad mit/ohne WC", "Duschraum", "Sauna", "Flur", "Diele"],
				geschoss: ["KG", "EG", "OG", "DG", "SB"],
				luftart: ["ZU", "AB", "ZU/AB", "ÜB"],
				raumVsBezeichnungZuluftventile: [/* initialized in Wac2Controller.mvcGroupInit */],
				raumVsBezeichnungAbluftventile: [/* initialized in Wac2Controller.mvcGroupInit */],
				raumVsUberstromelemente: [/* initialized in Wac2Controller.mvcGroupInit */],
				raumVsVerteilebene: ["KG", "EG", "OG", "DG", "SB"],
			],
		gewahlterRaum: [:] as ObservableMap,
		summeAktuelleWBW: 0.0d,
		wbw: [] as ObservableList, /* initialized in Wac2Controller.mvcGroupInit */
	] as ObservableMap
	
	/**
	 * Status bar.
	 */
	def statusBarText = "Bereit."
	
	/**
	 * Liste aller offenen Projekte - MVC IDs.
	 */
	def projekte = []
	
	/**
	 * Die MVC ID des derzeit aktiven Projekts/der aktive Tab.
	 */
	@Bindable def aktivesProjekt
	
	/**
	 * Wurde das Model des aktuellen Projekts geändert?
	 */
	@Bindable Boolean aktivesProjektGeandert = false
	
}

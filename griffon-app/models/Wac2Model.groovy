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
				raumVsBezeichnungZuluftventile: [/* initialized in ProjektController.mvcGroupInit */],
				raumVsBezeichnungAbluftventile: [/* initialized in ProjektController.mvcGroupInit */],
				raumVsUberstromelemente: [/* initialized in ProjektController.mvcGroupInit */],
				raumVsVentilebene: ["KG", "EG", "OG", "DG", "SB"],
			],
		gewahlterRaum: [:] as ObservableMap
	] as ObservableMap
	
	/**
	 * Status bar.
	 */
	def statusBarText = "Bereit."
	
	/**
	 * Das derzeit aktive Projekt.
	 */
	def aktivesProjekt = [:] as ObservableMap
	
	/**
	 * Alle aktiven Projekte.
	 */
	def projekte = []
	
}

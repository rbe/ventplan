/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/models/Wac2Model.groovy
 * Last modified at 14.03.2011 20:27:07 by rbe
 */
package com.westaflex.wac

import groovy.beans.Bindable

import com.westaflex.wac.*

/**
 * 
 */
class Wac2Model {
	
	/**
	 * Meta-data.
	 */
//	@Bindable meta = [
//		raum: [
//				typ: ["Wohnzimmer", "Kinderzimmer", "Schlafzimmer", "Esszimmer", "Arbeitszimmer", "Gästezimmer", "Hausarbeitsraum", "Kellerraum", "WC", "Küche", "Kochnische", "Bad mit/ohne WC", "Duschraum", "Sauna", "Flur", "Diele"],
//				geschoss: ["KG", "EG", "OG", "DG", "SB"],
//				luftart: ["ZU", "AB", "ZU/AB", "ÜB"],
//				raumVsBezeichnungZuluftventile: [/* initialized in Wac2Controller.mvcGroupInit */],
//				raumVsBezeichnungAbluftventile: [/* initialized in Wac2Controller.mvcGroupInit */],
//				raumVsUberstromelemente: [/* initialized in Wac2Controller.mvcGroupInit */],
//				raumVsVerteilebene: ["KG", "EG", "OG", "DG", "SB"],
//			],
//		gewahlterRaum: [:] as ObservableMap,
//		summeAktuelleWBW: 0.0d,
//		wbw: [] as ObservableList, /* initialized in Wac2Controller.mvcGroupInit */
//	] as ObservableMap

	/**
	 * Status bar.
	 */
	@Bindable def statusBarText = "Bereit."

    /**
     * Progress bar in status bar.
     * Wert auf true setzen bewirkt, dass die Progress bar "unendlich" durchläuft.
     * Wert auf false setzen beendet das Ganze wieder.
     */
	@Bindable def statusProgressBarIndeterminate = false

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

    /**
     * Wurde irgendein Model eines Projekts geändert?
     */
    @Bindable Boolean alleProjekteGeandert = false
	
}

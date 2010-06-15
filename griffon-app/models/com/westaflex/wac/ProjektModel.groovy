/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/models/com/westaflex/wac/ProjektModel.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
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
	 * Was the model changed (since last save)? This is set true by
	 * a PropertyChangeListener installed in ProjectController.addMapPropertyChange().
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
				typ: [MFH: true] as ObservableMap,
				lage: [windschwach: true] as ObservableMap,
				warmeschutz: [hoch: true] as ObservableMap,
				geometrie: [:
						//raumhohe: "0,00",
						//geluftetesVolumen: "0,00"
					] as ObservableMap,
				luftdichtheit: [
						kategorieA: true,
						druckdifferenz: 2.0f,
						luftwechsel: 1.0f,
						druckexponent: 0.666f
					] as ObservableMap,
				faktorBesondereAnforderungen: 1.0f,
				geplanteBelegung: [
						personenanzahl: 0.0f,
						aussenluftVsProPerson: 30,
						mindestaussenluftrate: 0.0f
					] as ObservableMap,
			] as ObservableMap,
		anlage: [
				standort: [EG: true] as ObservableMap,
				luftkanalverlegung: [:] as ObservableMap,
				aussenluft: [:] as ObservableMap,
				zuluft: [:] as ObservableMap,
				abluft: [:] as ObservableMap,
				fortluft: [dach: true] as ObservableMap,
				energie: [zuAbluftWarme: true, nachricht: " "] as ObservableMap,
				hygiene: [nachricht: " "] as ObservableMap,
				kennzeichnungLuftungsanlage: "ZuAbLS-Z-WE-WÜT-0-0-0-0-0",
			] as ObservableMap,
		raum: [
				typ: ["Wohnzimmer", "Kinderzimmer", "Schlafzimmer", "Esszimmer", "Arbeitszimmer", "Gästezimmer", "Hausarbeitsraum", "Kellerraum", "WC", "Küche", "Kochnische", "Bad mit/ohne WC", "Duschraum", "Sauna", "Flur", "Diele"],
				geschoss: ["KG", "EG", "OG", "DG", "SB"],
				luftart: ["ZU", "AB", "ZU/AB", "ÜB"],
				raume: [] as ObservableList,
				raumVsBezeichnungZuluftventile: ["1", "2", "3"],
				raumVsBezeichnungAbluftventile: ["4", "5", "6"],
				raumVsVerteilebene: ["7", "8", "9"],
				ltmZuluftSumme: 0.0f,
				ltmAbluftSumme: 0.0f,
			] as ObservableMap,
		aussenluftVs: [
				infiltrationBerechnen: true,
				massnahme: " "
			] as ObservableMap,
		druckverlust: [:] as ObservableMap,
		akkustik: [:] as ObservableMap
	] as ObservableMap
	
	// TableModels
	def tableModels = [
			raume: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), { a, b -> a.position <=> b.position } as Comparator) as ca.odell.glazedlists.EventList,
			raumeVsZuAbluftventile: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), { a, b -> a.position <=> b.position } as Comparator) as ca.odell.glazedlists.EventList,
			raumeVsUberstromventile: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), { a, b -> a.position <=> b.position } as Comparator) as ca.odell.glazedlists.EventList
		]
	
	/**
	 * Synchronize all Swing table models depending on map.raum.raume.
	 */
	def syncRaumTableModels() {
		// Raumdaten
		tableModels.raume.clear()
		tableModels.raume.addAll(map.raum.raume)
		// Raumvolumentströme - Zu-/Abluftventile
		tableModels.raumeVsZuAbluftventile.clear()
		tableModels.raumeVsZuAbluftventile.addAll(map.raum.raume)
		// Raumvolumentströme - Überströmventile
		tableModels.raumeVsUberstromventile.clear()
		tableModels.raumeVsUberstromventile.addAll(map.raum.raume)
	}
	
	/**
	 * Raumdaten - TableModel
	 */
	def createRaumTableModel() {
		def columnNames =   ["Raum",            "Geschoss",     "Luftart",     "Raumfläche (m²)", "Raumhöhe (m)", "Zuluftfaktor",     "Abluftvolumenstrom"]
		def propertyNames = ["raumBezeichnung", "raumGeschoss", "raumLuftart", "raumFlache",      "raumHohe",     "raumZuluftfaktor", "raumAbluftVs"]
		new ca.odell.glazedlists.swing.EventTableModel(tableModels.raume, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}" }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
	/**
	 * Raumvolumenströme, Zu-/Abluftventile - TableModel
	 */
	def createRaumVsZuAbluftventileTableModel() {
		def columnNames =   ["Raum",            "Luftart",     "Raumvolumen (m³)", "Luftwechsel (1/h)", "Anzahl Abluftventile",    "Abluftmenge je Ventil",   "Volumenstrom (m³/h)", "Anzahl Zuluftventile",    "Bezeichnung Zuluftventile"]
		def propertyNames = ["raumBezeichnung", "raumLuftart", "raumVolumen",      "raumLuftwechsel",   "raumAnzahlAbluftventile", "raumAbluftmengeJeVentil", "raumVolumenstrom",    "raumAnzahlZuluftventile", "raumBezeichnungZuluftventile"]
		new ca.odell.glazedlists.swing.EventTableModel(tableModels.raumeVsZuAbluftventile, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}" }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
	/**
	 * Raumvolumenströme - Überströmventile TableModel
	 */
	def createRaumVsUberstromventileTableModel() {
		def columnNames =   ["Raum",            "Luftart",     "Anzahl Ventile",                "Volumenstrom (m³/h)", "Überström-Elemente"]
		def propertyNames = ["raumBezeichnung", "raumLuftart", "raumAnzahlUberstromVentile",    "raumVolumenstrom",    "raumUberstromElemente"]
		new ca.odell.glazedlists.swing.EventTableModel(tableModels.raumeVsUberstromventile, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}" }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
}

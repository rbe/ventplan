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
				energie: [zuAbluftWarme: true, nachricht: " "] as ObservableMap,
				hygiene: [nachricht: " "] as ObservableMap,
				kennzeichnungLuftungsanlage: "ZuAbLS-Z-WE-WÜT-0-0-0-0-0",
			] as ObservableMap,
		raum: [
				typ: ["Wohnzimmer", "Kinderzimmer", "Schlafzimmer", "Esszimmer", "Arbeitszimmer", "Gästezimmer", "Hausarbeitsraum", "Kellerraum", "WC", "Küche", "Kochnische", "Bad mit/ohne WC", "Duschraum", "Sauna", "Flur", "Diele"],
				geschoss: ["KG", "EG", "OG", "DG", "SB"],
				luftart: ["ZU", "AB", "ZU/AB", "ÜB"],
				hohe: "2,50",
				zuluftfaktor: "3,00",
				raume: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), { a, b -> a.position <=> b.position } as Comparator) as ca.odell.glazedlists.EventList
			] as ObservableMap,
		aussenluftVs: [:] as ObservableMap,
		raumVs: [
				raumVsBezeichnungZuluftventile: ["1", "2", "3"],
				raumVsBezeichnungAbluftventile: ["4", "5", "6"],
				raumVsVerteilebene: ["7", "8", "9"],
				raume: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), { a, b -> a.position <=> b.position } as Comparator) as ca.odell.glazedlists.EventList
			] as ObservableMap,
		druckverlust: [:] as ObservableMap,
		akkustik: [:] as ObservableMap
	] as ObservableMap
	
	/**
	 * Raum - TableModel
	 */
	def createRaumTableModel() {
		def columnNames =   ["Raum",            "Geschoss",     "Luftart",     "Raumfläche (m²)", "Raumhöhe (m)", "Zuluftfaktor",     "Abluftvolumenstrom"]
		def propertyNames = ["raumBezeichnung", "raumGeschoss", "raumLuftart", "raumFlache",      "raumHohe",     "raumZuluftfaktor", "raumAbluftVs"]
		new ca.odell.glazedlists.swing.EventTableModel(map.raum.raume, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}" }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
	/**
	 * Compute a hash of the map when data is entered. Compare this hash to a previously
	 * stored hash value to see if the data has changed.
	 */
	def computeHash = { evt ->
	}
	
	
	/**
	 * RaumVs - Zu-/Abluftventile TableModel
	 */
	def createRaumVsZuAbluftventileModel() {
		def columnNames =   ["Raum",              "Luftart",       "Raumvolumen (m³)", "Luftwechsel (l/h)", "Anzahl Abluftventile",      "Abluftmenge je Ventil",     "Volumenstrom (m³/h)", "Anzahl Zuluftventile",       "Bezeichnung Zuluftventile",      "Bezeichnung Abluftventile",      "Verteilebene",       "Zuluftmenge je Ventil"]
		def propertyNames = ["raumVsBezeichnung", "raumVsLuftart", "raumVsVolumen",    "raumVsLuftwechsel", "raumVsAnzahlAbluftventile", "raumVsAbluftmengeJeVentil", "raumVsVolumenstrom",   "raumVsAnzahlZuluftventile", "raumVsBezeichnungZuluftventile", "raumVsBezeichnungAbluftventile", "raumVsVerteilebene", "raumVsZuluftmengeJeVentil"]
		new ca.odell.glazedlists.swing.EventTableModel(map.raumVs.raume, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}" }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
	
	/**
	 * RaumVs - Überströmventile TableModel
	 */
	def createRaumVsUberstromventileModel() {
		def columnNames =   ["Raum",               "Luftart",        "Anzahl Ventile", "Volumenstrom (m³/h)", "Überström-Elemente"]
		def propertyNames = ["raumVs2Bezeichnung", "raumVs2Luftart", "raumVs2Ventile", "raumVs2Volumenstrom", "raumVs2UberstromElemente"]
		new ca.odell.glazedlists.swing.EventTableModel(map.raumVs2.raume, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}" }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
}
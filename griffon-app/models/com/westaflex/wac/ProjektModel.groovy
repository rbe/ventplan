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
				raume: [] as ObservableList,
				raumVsBezeichnungZuluftventile: ["1", "2", "3"],
				raumVsBezeichnungAbluftventile: ["4", "5", "6"],
				raumVsVerteilebene: ["7", "8", "9"],
				// TableModels
				raumTabelleTableModel: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), { a, b -> a.position <=> b.position } as Comparator) as ca.odell.glazedlists.EventList,
				raumVsZuAbluftventileTabelleTableModel: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), { a, b -> a.position <=> b.position } as Comparator) as ca.odell.glazedlists.EventList,
				raumVsUberstromventileTabelleTableModel: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), { a, b -> a.position <=> b.position } as Comparator) as ca.odell.glazedlists.EventList
			] as ObservableMap,
		aussenluftVs: [massnahme: " "] as ObservableMap,
		druckverlust: [:] as ObservableMap,
		akkustik: [:] as ObservableMap
	] as ObservableMap
	
	/**
	 * Synchronize all Swing table models depending on map.raum.raume.
	 */
	def syncRaumTableModels() {
		map.raum.with {
			// Raumdaten
			raumTabelleTableModel.clear()
			raumTabelleTableModel.addAll(raume)
			// Raumvolumentströme - Zu-/Abluftventile
			raumVsZuAbluftventileTabelleTableModel.clear()
			raumVsZuAbluftventileTabelleTableModel.addAll(raume)
			// Raumvolumentströme - Überströmventile
			raumVsUberstromventileTabelleTableModel.clear()
			raumVsUberstromventileTabelleTableModel.addAll(raume)
		}
	}
	
	/**
	 * Raumdaten - TableModel
	 */
	def createRaumTableModel() {
		def columnNames =   ["Raum",            "Geschoss",     "Luftart",     "Raumfläche (m²)", "Raumhöhe (m)", "Zuluftfaktor",     "Abluftvolumenstrom"]
		def propertyNames = ["raumBezeichnung", "raumGeschoss", "raumLuftart", "raumFlache",      "raumHohe",     "raumZuluftfaktor", "raumAbluftVs"]
		new ca.odell.glazedlists.swing.EventTableModel(map.raum.raumTabelleTableModel, [
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
		def propertyNames = ["raumBezeichnung", "raumLuftart", "raumVolumen",      "raumLuftwechsel",   "raumAnzahlAbluftventile", "raumAbluftmengeJeVentil", "raumVsProStunde",     "raumAnzahlZuluftventile", "raumBezeichnungZuluftventile"]
		new ca.odell.glazedlists.swing.EventTableModel(map.raum.raumVsZuAbluftventileTabelleTableModel, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}" }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
	/**
	 * RaumVs - Überströmventile TableModel
	 */
	def createRaumVsUberstromventileTableModel() {
		def columnNames =   ["Raum",            "Luftart",     "Anzahl Ventile",                "Volumenstrom (m³/h)", "Überström-Elemente"]
		def propertyNames = ["raumBezeichnung", "raumLuftart", "raumAnzahlUberstromVentile",    "raumVolumenstrom",    "raumUberstromElemente"]
		new ca.odell.glazedlists.swing.EventTableModel(map.raum.raumVsUberstromventileTabelleTableModel, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}" }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
}

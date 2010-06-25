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
	 * Meta-data.
	 */
	@Bindable meta
	
	/**
	 * Our central model.
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
						druckdifferenz: 2.0d,
						luftwechsel: 1.0d,
						druckexponent: 0.666f
					] as ObservableMap,
				faktorBesondereAnforderungen: 1.0d,
				geplanteBelegung: [
						personenanzahl: 0.0d,
						aussenluftVsProPerson: 30,
						mindestaussenluftrate: 0.0d
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
				raume: [
						/*
						[
							raumBezeichnung: "",
							raumLuftart: "",
							raumGeschoss: "",
							raumFlache: "",
							raumHohe: "",
							raumZuluftfaktor: "",
							raumAbluftVs: "",
							raumVolumen: "",
							raumLuftwechsel: "",
							raumBezeichnungAbluftventile: "",
							raumAnzahlAbluftventile: "",
							raumAbluftmengeJeVentil: "",
							raumVolumenstrom: "",
							raumBezeichnungZuluftventile: "",
							raumAnzahlZuluftventile: "",
							raumZuluftmengeJeVentil: "",
							raumVentilebene: ""
							raumAnzahlUberstromVentile: "",
							raumVolumenstrom: "",
							raumUberstromElemente: "",
							turen: [] as ObservableList
						]
						*/
					] as ObservableList,
				ltmZuluftSumme: 0.0d,
				ltmAbluftSumme: 0.0d,
				raumVs: [
					gesamtVolumenNE: 0.0d
				] as ObservableMap
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
	 * Wrap text in HTML and substitute every space character with HTML-breaks.
	 */
	def ws = { t, threshold = 0 ->
		def n = t
		if (threshold) {
			def i = 0
			n = t.collect { c ->
				if (i++ > threshold && c == " ") "<br/>"
				else c
			}.join()
		}
		"<html><div align=\"center\">${n}</div></html>" as String
	}
	
	/**
	 * Raumdaten - TableModel
	 */
	def createRaumTableModel() {
		def columnNames =   ['Raum',            "Geschoss",     "Luftart",     "Raumfläche (m²)", "Raumhöhe (m)", "Zuluftfaktor",     "Abluftvolumenstrom"]
		def propertyNames = ["raumBezeichnung", "raumGeschoss", "raumLuftart", "raumFlache",      "raumHohe",     "raumZuluftfaktor", "raumAbluftVs"]
		new ca.odell.glazedlists.swing.EventTableModel(tableModels.raume, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}"?.toString2() }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
	/**
	 * Raumvolumenströme, Zu-/Abluftventile - TableModel
	 */
	def createRaumVsZuAbluftventileTableModel() {
		def columnNames =   ["Raum",            "Luftart",     ws("Raumvolumen<br/>(m³)"), ws("Luftwechsel<br/>(1/h)"), ws("Bezeichnung<br/>Abluftventile"),    ws("Anzahl<br/>Abluftventile"),    ws("Abluftmenge<br/>je Ventil"),   ws("Volumenstrom<br/>(m³/h)"), ws("Bezeichnung<br/>Zuluftventile"),    ws("Anzahl<br/>Zuluftventile"),    ws("Zuluftmenge<br/>je Ventil"),   "Ventilebene"]
		def propertyNames = ["raumBezeichnung", "raumLuftart", "raumVolumen",              "raumLuftwechsel",           "raumBezeichnungAbluftventile",         "raumAnzahlAbluftventile",         "raumAbluftmengeJeVentil",         "raumVolumenstrom",            "raumBezeichnungZuluftventile",         "raumAnzahlZuluftventile",         "raumZuluftmengeJeVentil",         "raumVentilebene"]
		new ca.odell.glazedlists.swing.EventTableModel(tableModels.raumeVsZuAbluftventile, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}"?.toString2() }
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
				getColumnValue: { object, index -> object."${propertyNames[index]}"?.toString2() }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
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
	
}

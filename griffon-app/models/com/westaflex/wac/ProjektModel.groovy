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
	 * Was the model changed (since last save)? This is set true by a PropertyChangeListener installed in ProjectController.addMapPropertyChangeListener().
	 */
	boolean dirty
	
	/**
	 * Meta-data: will be initialized by ProjektController.
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
			raumeVsUberstromventile: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), { a, b -> a.position <=> b.position } as Comparator) as ca.odell.glazedlists.EventList,
                        raumeBearbeitenEinstellungen: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), { a, b -> a.position <=> b.position } as Comparator) as ca.odell.glazedlists.EventList
		]
	
	/**
	 * Wrap text in HTML and substitute every space character with HTML-breaks.
	 * TODO rbe Move into GriffonHelper
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

        def createRaumEinstellungenTableModel() {
            def columnNames =   ["Raum", "Raumnummer", "Raumtyp", "Geschoss", "Luftart", "Faktor", "Vorgang", "Zuluft", "Abluft", "Duch??", "Duch2???", "Kanalnetz", "Kanalnetz2", "Türhöhe", "Max...?", "Rau..???", "Rau...???", "Rau...???", "Rau...???", "Rau...???"]
            def propertyNames = ["raumBezeichnung", "raumNummer", "raumTyp", "raumGeschoss", "luftart", "faktor", "vorgang", "zuluft", "abluft", "duch1", "duch2", "kanalnetz", "kanalnetz2", "turhohe", "max1", "raum1", "raum2", "raum3", "raum4", "raum5"]
            new ca.odell.glazedlists.swing.EventTableModel(tableModels.raumeBearbeitenEinstellungen, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}"?.toString2() }
			] as ca.odell.glazedlists.gui.TableFormat)
        }
	
	/**
	 * Einen Raum im Model hinzufügen, alle TableModels synchronisieren.
	 */
	def addRaum = { raum ->
		//println "addRaum: adding raum=${raum.dump()}"
		map.raum.raume << raum
		// Sync table models
		[tableModels.raume, tableModels.raumeVsZuAbluftventile, tableModels.raumeVsUberstromventile].each {
			it.add(map.raum.raume[raum.position])
		}
	}
	
	/**
	 * Einen Raum aus dem Model entfernen, alle TableModels synchronisieren.
	 */
	def removeRaum = { raumIndex ->
		//println "removeRaum: removing raumIndex=${raumIndex}"
		/*
		map.raum.raume.eachWithIndex { r, i ->
			println "BEFORE raumIndex=${raumIndex} i=${i}: ${r.raumBezeichnung} ${r.position}"
		}
		*/
		map.raum.raume.remove(raumIndex)
		/*
		map.raum.raume.eachWithIndex { r, i ->
			println "AFTER raumIndex=${raumIndex} i=${i}: ${r.raumBezeichnung} ${r.position}"
		}
		*/
		// Sync table models
		[tableModels.raume, tableModels.raumeVsZuAbluftventile, tableModels.raumeVsUberstromventile].each {
			it.remove(raumIndex)
		}
	}
	
	/**
	 * Synchronize all Swing table models depending on map.raum.raume.
	 */
	def resyncRaumTableModels() {
		// Räume sortieren, damit die Liste immer der Position des Raums und der Reihenfolge im TableModel entspricht
		// Nicht zwingend notwendig, da die TableModels bereits nach Position sortieren:
		// SortedList(new BasicEventList(), { a, b -> a.position <=> b.position } as Comparator)
		// TODO rbe Following code throws java.lang.IndexOutOfBoundsException: Index: 2, Size: 2
		//println map.raum.raume.sort { a, b -> a.position <=> b.position }
		/*
		map.raum.raume.eachWithIndex { r, i ->
			println "resyncRaumTableModels: ${r.raumBezeichnung}: i=${i} == position=${r.position}?"
		}
		*/
		// Raumdaten
		tableModels.raume.clear()
		tableModels.raume.addAll(map.raum.raume)
		// Raumvolumentströme - Zu-/Abluftventile
		tableModels.raumeVsZuAbluftventile.clear()
		tableModels.raumeVsZuAbluftventile.addAll(map.raum.raume)
		// Raumvolumentströme - Überströmventile
		tableModels.raumeVsUberstromventile.clear()
		tableModels.raumeVsUberstromventile.addAll(map.raum.raume)

                tableModels.raumeBearbeiten.addAll(map.raum.raume)
	}
	
}

/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/models/com/westaflex/wac/ProjektModel.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
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
	 * Meta-data: will be initialized by ProjektController.
	 */
	@Bindable meta
	
	/**
	 * Template für alle Werte eines Raumes.
	 */
	def raumMapTemplate = [
			raumBezeichnung: "",
			raumLuftart: "",
			raumGeschoss: "",
			raumFlache: 0.0d,
			raumHohe: 0.0d,
			raumZuluftfaktor: 0.0d,
			raumAbluftVs: 0.0d,
			raumVolumen: 0.0d,
			raumLuftwechsel: 0.0d,
			raumVolumenstrom: 0.0d,
			raumBezeichnungAbluftventile: "",
			raumAnzahlAbluftventile: "",
			raumAbluftmengeJeVentil: 0.0d,
			raumBezeichnungZuluftventile: "",
			raumAnzahlZuluftventile: 0,
			raumZuluftmengeJeVentil: 0.0d,
			raumVentilebene: "",
			raumAnzahlUberstromVentile: 0d,
			raumUberstromElement: "",
			raumNummer: "",
			turen: [] as ObservableList
		]
	
	/**
	 * Template für alle Werte bei Druckverlust - Kanalnetz.
	 */
	def dvbKanalnetzMapTemplate = [
			luftart: "",
			teilstrecke: 0,
			luftVs: 0.0d,
			kanalbezeichnung: "",
			lange: 0.0d,
			geschwindigkeit: 0.0d,
			reibungswiderstand: 0.0d,
			gesamtwiderstandszahl: 0,
			einzelwiderstand: 0.0d,
			widerstandTeilstrecke: 0.0d
		]
	
	/**
	 * Template for alle Werte bei Druckverlust - Ventileinstellung.
	 */
	def dvbVentileinstellungMapTemplate = [
			luftart: "",
			raum: "",
			teilstrecken: "",
			ventilbezeichnung: "",
			dpOffen: 0.0d,
			gesamtWiderstand: 0.0d,
			differenz: 0.0d,
			abgleich: 0.0d,
			einstellung: 0
		]
	
	/**
	 * Our central model.
	 * dirty: Was the model changed (since last save)? This is set true by a PropertyChangeListener installed in ProjectController.addMapPropertyChangeListener().
	 */
	@Bindable map = [
		dirty: false,
		kundendaten: [
			grosshandel: [:] as ObservableMap,
			ausfuhrendeFirma: [:] as ObservableMap,
		] as ObservableMap,
		gebaude: [
				typ: [mfh: true] as ObservableMap,
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
				zentralgerat: "",
				zentralgeratManuell: false,
				volumenstromZentralgerat: 0,
			] as ObservableMap,
		raum: [
				raume: [
						/* ProjektModel.raumMapTemplate wird durch Event RaumHinzufugen pro Raum erstellt */
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
		dvb: [
				kanalnetz: [] as ObservableList,
				ventileinstellung: [] as ObservableList
			] as ObservableMap,
		akkustik: [:] as ObservableMap
	] as ObservableMap
	
	// TableModels
	def tmPositionComparator = { a, b -> a.position <=> b.position } as Comparator
	def tmNameComparator = { a, b -> a.name <=> b.name } as Comparator
	def tableModels = [
			raume: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			raumeVsZuAbluftventile: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			raumeVsUberstromventile: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			raumeBearbeitenDetails: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			raumeBearbeitenEinstellungen: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			dvbKanalnetz: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			dvbVentileinstellung: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			wbw: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmNameComparator) as ca.odell.glazedlists.EventList
		]
	
	/**
	 * Wrap text in HTML and substitute every space character with HTML-breaks.
	 */
	def ws = GH.ws
	
	/**
	 * Raumdaten - TableModel
	 */
	def createRaumTableModel() {
		def columnNames =   ["Raum",            "Geschoss",     "Luftart",     "Raumfläche (m²)", "Raumhöhe (m)", "Zuluftfaktor",     "Abluftvolumenstrom"]
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
		def columnNames =	["Raum",			"Luftart",	   ws("Raumvolumen<br/>(m³)"), ws("Luftwechsel<br/>(1/h)"), ws("Bezeichnung<br/>Abluftventile"),	ws("Anzahl<br/>Abluftventile"),	   ws("Abluftmenge<br/>je Ventil"),	  ws("Volumenstrom<br/>(m³/h)"), ws("Bezeichnung<br/>Zuluftventile"),	 ws("Anzahl<br/>Zuluftventile"),	ws("Zuluftmenge<br/>je Ventil"),   "Ventilebene"]
		def propertyNames = ["raumBezeichnung", "raumLuftart", "raumVolumen",			   "raumLuftwechsel",			"raumBezeichnungAbluftventile",			"raumAnzahlAbluftventile",		   "raumAbluftmengeJeVentil",		  "raumVolumenstrom",			 "raumBezeichnungZuluftventile",		 "raumAnzahlZuluftventile",			"raumZuluftmengeJeVentil",		   "raumVentilebene"]
		new ca.odell.glazedlists.swing.EventTableModel(tableModels.raumeVsZuAbluftventile, [
				getColumnCount: { columnNames.size() },
				getColumnName:	{ index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}"?.toString2() }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
	/**
	 * Raumvolumenströme - Überströmventile TableModel
	 */
	def createRaumVsUberstromventileTableModel() {
		def columnNames =   ["Raum",            "Luftart",     "Anzahl Ventile",                "Volumenstrom (m³/h)", "Überström-Elemente"]
		def propertyNames = ["raumBezeichnung", "raumLuftart", "raumAnzahlUberstromVentile",    "raumVolumenstrom",    "raumUberstromElement"]
		new ca.odell.glazedlists.swing.EventTableModel(tableModels.raumeVsUberstromventile, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}"?.toString2() }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
	/**
	 * RaumBearbeitenView - Details Tab TableModel
	 */
	def createRaumDetailsTableModel() {
		def columnNames =   ["Bezeichnung", "Breite in mm", "Querschnittsfläche in mm²", "Spaltenhöhe in mm", "mit Dichtung"]
		def propertyNames = ["turBezeichnung", "turBreite", "turQuerschnitt", "turSpaltenhohe", "turDichtung"]
		new ca.odell.glazedlists.swing.EventTableModel(tableModels.raumeBearbeitenDetails, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}"?.toString2() }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
	/**
	 * RaumBearbeitenView - Zusammenfassung Tab TableModel
	 */
	def createRaumEinstellungenTableModel() {
		def columnNames =   ["Raum",            "Raumnummer", "Raumtyp", "Geschoss",     "Luftart", "Faktor", "Vorgang", "Zuluft", "Abluft", "Duch??", "Duch2???", "Kanalnetz", "Kanalnetz2", "Türhöhe", "Max...?", "Rau..???", "Rau...???", "Rau...???", "Rau...???", "Rau...???"]
		def propertyNames = ["raumBezeichnung", "raumNummer", "raumTyp", "raumGeschoss", "luftart", "faktor", "vorgang", "zuluft", "abluft", "duch1", "duch2", "kanalnetz", "kanalnetz2", "turhohe", "max1", "raum1", "raum2", "raum3", "raum4", "raum5"]
		new ca.odell.glazedlists.swing.EventTableModel(tableModels.raumeBearbeitenEinstellungen, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}"?.toString2() }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz.
	 */
	def createDvbKanalnetzTableModel() {
		def columnNames =   ["Luftart", "Teilstrecke", ws("Luftvolumen-<br/>strom<br/>(m³/h)"), "Kanalbezeichnung", ws("Kanallänge<br/>(m)"), ws("Geschwindigkeit<br/>(m/s)"), ws("Reibungswiderstand<br/>gerader Kanal<br/>(Pa)"), ws("Gesamtwider-<br/>standszahl"), ws("Einzelwider-<br/>stand<br/>(Pa)"), ws("Widerstand<br/>Teilstrecke<br/><(Pa)")]
		def propertyNames = ["luftart", "teilstrecke", "luftVs",                                "kanalbezeichnung", "lange",                  "geschwindigkeit",               "reibungswiderstand",                                "gesamtwiderstandszahl",           "einzelwiderstand",                    "widerstandTeilstrecke"]
		new ca.odell.glazedlists.swing.EventTableModel(tableModels.dvbKanalnetz, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}"?.toString2() }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
	/**
	 * Druckverlustberechnung - Ventileinstellung.
	 */
	def createDvbVentileinstellungTableModel() {
		def columnNames =   ["Luftart", "Raum", "Teilstrecken", "Ventiltyp",         "dP offen (Pa)", "Gesamt (Pa)",      "Differenz", "Abgleich (Pa)", "Einstellung"]
		def propertyNames = ["luftart", "raum", "teilstrecken", "ventilbezeichnung", "dpOffen",       "gesamtWiderstand", "differenz", "abgleich",      "einstellung"]
		new ca.odell.glazedlists.swing.EventTableModel(tableModels.dvbVentileinstellung, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}"?.toString2() }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
	/**
	 * Druckverlustberechnung - Kanal - Widerstandsbeiwerte.
	 */
	def createWbwTableModel() {
		def columnNames =   ["Anzahl", "Widerstand", "Widerstandsbeiwert"]
		def propertyNames = ["anzahl", "name",       "widerstandsbeiwert"]
		new ca.odell.glazedlists.swing.EventTableModel(tableModels.wbw, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { index -> columnNames[index] },
				getColumnValue: { object, index -> object."${propertyNames[index]}"?.toString2() }
			] as ca.odell.glazedlists.gui.TableFormat)
	}
	
	/**
	 * Einen Raum im Model hinzufügen: auch alle TableModels, Comboboxen synchronisieren.
	 */
	def addRaum = { raum ->
		def r = (raumMapTemplate + raum) as ObservableMap
		println "addRaum: adding raum=${r.dump()}"
		map.raum.raume << r
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
		// Räume sortieren, damit die Liste immer der Position des Raums und der Reihenfolge im TableModel entspricht:
		// Nicht zwingend notwendig, da die TableModels bereits über den Comparator nach Position sortieren:
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
		// TODO mmu Documentation?
		// TODO mmu Possible java.lang.NullPointerException: Cannot invoke method addAll() on null object when RaumBearbeitenDialog was not opened before
		// TODO mmu rbe: Quickfix: added null-safe-operator
		tableModels.raumeBearbeiten?.addAll(map.raum.raume)
	}
	
	/**
	 * 
	 */
	def addDvbKanalnetz = { kanalnetz ->
		def k = (dvbKanalnetzMapTemplate + kanalnetz) as ObservableMap
		println "addDvbKanalnetz: adding kanalnetz=${k.dump()}"
		map.dvb.kanalnetz << k
		// Sync table model
		[tableModels.dvbKanalnetz].each {
			it.add(map.dvb.kanalnetz[kanalnetz.position])
		}
	}
	
	/**
	 * 
	 */
	def removeDvbKanalnetz = { kanalnetzIndex ->
		
	}
	
	/**
	 * 
	 */
	def addDvbVentileinstellung = { ventileinstellung ->
		def v = (dvbVentileinstellungMapTemplate + ventileinstellung) as ObservableMap
		println "addDvbVentileinstellung: adding ventileinstellung=${v.dump()}"
		map.dvb.ventileinstellung << v
		// Sync table model
		[tableModels.dvbVentileinstellung].each {
			it.add(map.dvb.ventileinstellung[ventileinstellung.position])
		}
	}
	
	/**
	 * 
	 */
	def removeDvbVentileinstellung = { ventileinstellungIndex ->
		
	}
	
	/**
	 * Synchronize all Swing table models depending on map.dvb.kanalnetz.
	 */
	def resyncDvbKanalnetzTableModels() {
		// Druckverlust - Kanalnetz
		tableModels.dvbKanalnetz.clear()
		tableModels.dvbKanalnetz.addAll(map.dvb.kanalnetz)
	}
	
	/**
	 * Synchronize all Swing table models depending on map.dvb.ventileinstellung.
	 */
	def resyncDvbVentileinstellungTableModels() {
		// Druckverlust - Ventileinstellung
		tableModel.dvbVentileinstellung.clear()
		tableModel.dvbVentileinstellung.addAll(map.dvb.ventileinstellung)
	}
	
}

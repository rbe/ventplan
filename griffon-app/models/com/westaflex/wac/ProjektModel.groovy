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
import ca.odell.glazedlists.GlazedLists
import ca.odell.glazedlists.swing.AutoCompleteSupport
import javax.swing.table.TableColumn
import javax.swing.DefaultCellEditor
import javax.swing.DefaultComboBoxModel

/**
 * 
 */
class ProjektModel {
	
	public static boolean DEBUG = false
	
	/**
	 * The MVC id.
	 */
	String mvcId
	
	/**
	 * Filename of .wpx file.
	 */
	String wpxFilename
	
	/**
	 * Meta-data: will be initialized by ProjektController.
	 */
	@Bindable meta = [
			gewahlterRaum: [:] as ObservableMap
		]
	
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
			raumAnzahlAbluftventile: 0,
			raumAbluftmengeJeVentil: 0.0d,
			raumBezeichnungZuluftventile: "",
			raumAnzahlZuluftventile: 0,
			raumZuluftmengeJeVentil: 0.0d,
			raumVerteilebene: "",
			raumAnzahlUberstromVentile: 0,
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
						personenanzahl: 0,
						aussenluftVsProPerson: 30,
						mindestaussenluftrate: 0
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
					gesamtVolumenNE: 0.0d,
					luftwechselNE: 0.0d,
					gesamtaussenluftVsMitInfiltration: 0.0d
				] as ObservableMap
			] as ObservableMap,
		aussenluftVs: [
				infiltrationBerechnen: true,
				massnahme: " ",
				gesamtLvsLtmLvsFs: 0.0d,
				gesamtLvsLtmLvsRl: 0.0d,
				gesamtLvsLtmLvsNl: 0.0d,
				gesamtLvsLtmLvsIl: 0.0d,
			] as ObservableMap,
		dvb: [
				kanalnetz: [] as ObservableList,
				ventileinstellung: [] as ObservableList
			] as ObservableMap,
		akustik: [
				zuluft: [
						anzahlUmlenkungen: 5,
						luftverteilerkastenStck: 1,
						langsdampfung: 12,
						raumabsorption: 1,
						tabelle: [
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
							]
					] as ObservableMap,
				abluft: [
						anzahlUmlenkungen: 4,
						luftverteilerkastenStck: 1,
						langsdampfung: 7,
						raumabsorption: 0,
						tabelle: [
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
								[slp125: 0, slp250: 0, slp500: 0, slp1000: 0, slp2000: 0, slp4000: 0],
							]
					] as ObservableMap,
			] as ObservableMap,
			raumBezeichnung: [] as ObservableList
	] as ObservableMap
	
	// TableModels
	def tmPositionComparator = { a, b -> a.position <=> b.position } as Comparator
	def tmNameComparator = { a, b -> a.name <=> b.name } as Comparator
	def tmNothingComparator = { a, b -> 0 } as Comparator
	def tableModels = [
			raume:                        new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			raumeVsZuAbluftventile:       new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			raumeVsUberstromventile:      new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			raumeBearbeitenDetails:       new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			raumeBearbeitenEinstellungen: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			dvbKanalnetz:                 new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			dvbVentileinstellung:         new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			wbw:                          new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmNameComparator) as ca.odell.glazedlists.EventList,
			akustikZuluft:                new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmNothingComparator) as ca.odell.glazedlists.EventList,
			akustikAbluft:                new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmNothingComparator) as ca.odell.glazedlists.EventList
		]
	
	/**
	 * Wrap text in HTML and substitute every space character with HTML-breaks.
	 */
	def ws = GH.ws
	
	/**
	 * Closure to generate glazedlists EventTableModel.
	 * @param columnNames String[]
	 * @param propertyNames String[]
	 * @param writable boolean[]
	 * @param tableModel ca.odell.glazedlists.EventList
	 */
	def gltmClosure = { columnNames, propertyNames, writable, tableModel, mapToUpdate ->
		new ca.odell.glazedlists.swing.EventTableModel(tableModel, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { columnIndex -> columnNames[columnIndex] },
				getColumnValue: { object, columnIndex ->
					try {
						object."${propertyNames[columnIndex]}"?.toString2()
					} catch (e) {
						// combobox...
						println "gltmClosure, getColumnValue: ${e}: ${object?.dump()}"
						object?.toString2()
					}
				},
				isEditable:     { object, columnIndex -> writable[columnIndex] },
				setColumnValue: { object, value, columnIndex ->
					println "setColumnValue: value@${columnIndex}=${value}"
					object."${propertyNames[columnIndex]}" = value
					println "... ${object}"
					def myTempMap
					switch (mapToUpdate) {
						case "raume":
							// TODO Call ProjektController.raumGeandert(raumIndex, view)
							println "raume 1 ===> ${map.raum.raume}"
							myTempMap = map.raum.raume.find { it.position == object.position }
							myTempMap[columnIndex] = value
							println "raume 2 ===> ${map.raum.raume}"
							meta.gewahlterRaum[columnIndex] = value
							println "Edited: map.raum.raume -> ${map.raum.raume}"
							resyncRaumTableModels()
							break
						case "dvb.kanalnetz":
							myTempMap = map.dvb.kanalnetz.find { it.position == object.position }
							myTempMap[columnIndex] = value
							println "Edited: map.dvb.kanalnetz -> ${map.dvb.kanalnetz}"
							resyncDvbKanalnetzTableModels()
							break
						case "dvb.ventileinstellung":
							myTempMap = map.dvb.ventileinstellung.find { it.position == object.position }
							myTempMap[columnIndex] = value
							println "Edited: map.dvb.ventileinstellung -> ${map.dvb.ventileinstellung}"
							resyncDvbVentileinstellungTableModels()
							break
					}
				},
				getValueAt: { rowIndex, columnIndex ->
					meta.gewahlterRaum[columnIndex]
				}
			] as ca.odell.glazedlists.gui.WritableTableFormat)
	}
	
	/**
	 * Raumdaten - TableModel
	 */
	def createRaumTableModel() {
		def columnNames =   ["Raum",            "Geschoss",     "Luftart",     ws("Raumfläche<br/>(m²)"), ws("Raumhöhe<br/>(m)"), "Zuluftfaktor",     "Abluftvolumenstrom"] as String[]
		def propertyNames = ["raumBezeichnung", "raumGeschoss", "raumLuftart", "raumFlache",      "raumHohe",     "raumZuluftfaktor", "raumAbluftVs"] as String[]
		def writable      = [true, true, true, true, true, true, true] as boolean[]
		gltmClosure(columnNames, propertyNames, writable, tableModels.raume, "raume")
	}
	
	/**
	 * Raumvolumenströme, Zu-/Abluftventile - TableModel
	 */
	def createRaumVsZuAbluftventileTableModel() {
		def columnNames =   ["Raum",            "Luftart",     ws("Raumvolumen<br/>(m³)"), ws("Luftwechsel<br/>(1/h)"), ws("Bezeichnung<br/>Abluftventile"),    ws("Anzahl<br/>Abluftventile"),    ws("Abluftmenge<br/>je Ventil"),   ws("Volumenstrom<br/>(m³/h)"), ws("Bezeichnung<br/>Zuluftventile"),    ws("Anzahl<br/>Zuluftventile"),    ws("Zuluftmenge<br/>je Ventil"),   "Verteilebene"] as String[]
		def propertyNames = ["raumBezeichnung", "raumLuftart", "raumVolumen",              "raumLuftwechsel",           "raumBezeichnungAbluftventile",         "raumAnzahlAbluftventile",         "raumAbluftmengeJeVentil",         "raumVolumenstrom",            "raumBezeichnungZuluftventile",         "raumAnzahlZuluftventile",         "raumZuluftmengeJeVentil",         "raumVerteilebene"] as String[]
		def writable      = [true, true, true, true, true, true, true, true, true, true, true, true] as boolean[]
		gltmClosure(columnNames, propertyNames, writable, tableModels.raumeVsZuAbluftventile, "raume")
	}
	
	/**
	 * Raumvolumenströme - Überströmventile TableModel
	 */
	def createRaumVsUberstromventileTableModel() {
		def columnNames =   ["Raum",            "Luftart",     "Anzahl Ventile",                "Volumenstrom (m³/h)", "Überström-Elemente"] as String[]
		def propertyNames = ["raumBezeichnung", "raumLuftart", "raumAnzahlUberstromVentile",    "raumVolumenstrom",    "raumUberstromElement"] as String[]
		def writable      = [true, true, true, true, true] as boolean[]
		gltmClosure(columnNames, propertyNames, writable, tableModels.raumeVsUberstromventile, "raume")
	}
	
	/**
	 * RaumBearbeitenView - Details Tab TableModel
	 */
	def createRaumDetailsTableModel() {
		def columnNames =   ["Bezeichnung", "Breite in mm", "Querschnittsfläche in mm²", "Spaltenhöhe in mm", "mit Dichtung"] as String[]
		def propertyNames = ["turBezeichnung", "turBreite", "turQuerschnitt", "turSpaltenhohe", "turDichtung"] as String[]
		def writable      = [true, true, true, true, true] as boolean[]
		gltmClosure(columnNames, propertyNames, writable, tableModels.raumeBearbeitenDetails, "raume")
	}
	
	/**
	 * RaumBearbeitenView - Zusammenfassung Tab TableModel
	 */
	def createRaumEinstellungenTableModel() {
		def columnNames =   ["Raum",            "Raumnummer", "Raumtyp", "Geschoss",     "Luftart", "Faktor", "Vorgang", "Zuluft", "Abluft", "Duch??", "Duch2???", "Kanalnetz", "Kanalnetz2", "Türhöhe", "Max...?", "Rau..???", "Rau...???", "Rau...???", "Rau...???", "Rau...???"] as String[]
		def propertyNames = ["raumBezeichnung", "raumNummer", "raumTyp", "raumGeschoss", "luftart", "faktor", "vorgang", "zuluft", "abluft", "duch1", "duch2", "kanalnetz", "kanalnetz2", "turhohe", "max1", "raum1", "raum2", "raum3", "raum4", "raum5"] as String[]
		def writable      = [true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true] as boolean[]
		gltmClosure(columnNames, propertyNames, writable, tableModels.raumeBearbeitenEinstellungen, "raume")
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz.
	 */
	def createDvbKanalnetzTableModel() {
		def columnNames =   ["Luftart", "Teilstrecke", ws("Luftvolumen-<br/>strom<br/>(m³/h)"), "Kanalbezeichnung", ws("Kanallänge<br/>(m)"), ws("Geschwindigkeit<br/>(m/s)"), ws("Reibungswiderstand<br/>gerader Kanal<br/>(Pa)"), ws("Gesamtwider-<br/>standszahl"), ws("Einzelwider-<br/>stand<br/>(Pa)"), ws("Widerstand<br/>Teilstrecke<br/><(Pa)")] as String[]
		def propertyNames = ["dvbkLuftart", "teilstrecke", "luftVs",                                "kanalbezeichnung", "lange",                  "geschwindigkeit",               "reibungswiderstand",                                "gesamtwiderstandszahl",           "einzelwiderstand",                    "widerstandTeilstrecke"] as String[]
		def writable      = [true, true, true, true, true, true, true, true, true, true] as boolean[]
		gltmClosure(columnNames, propertyNames, writable, tableModels.dvbKanalnetz, "dvb.kanalnetz")
	}
	
	/**
	 * Druckverlustberechnung - Ventileinstellung.
	 */
	def createDvbVentileinstellungTableModel() {
		def columnNames =   ["Raum", "Luftart", "Teilstrecken", "Ventiltyp",         "dP offen (Pa)", "Gesamt (Pa)",      "Differenz", "Abgleich (Pa)", "Einstellung"] as String[]
		def propertyNames = ["raum", "dvbvLuftart", "teilstrecken", "ventilbezeichnung", "dpOffen",       "gesamtWiderstand", "differenz", "abgleich",      "einstellung"] as String[]
		def writable      = [true, true, true, true, true, true, true, true, true] as boolean[]
		gltmClosure(columnNames, propertyNames, writable, tableModels.dvbVentileinstellung, "dvb.ventileinstellung")
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz - Widerstandsbeiwerte.
	 */
	def createWbwTableModel() {
		def columnNames =   ["Anzahl", "Bezeichnung", "Widerstandsbeiwert"] as String[]
		def propertyNames = ["anzahl", "name",        "widerstandsbeiwert"] as String[]
		def writable      = [true, true, true] as boolean[]
		gltmClosure(columnNames, propertyNames, writable, tableModels.wbw, "wbw")
	}
	
	/**
	 * Akustikberechnung - Zuluft.
	 */
	def createAkustikZuluftTableModel() {
		def columnNames =   ["125",    "250",    "500",    "1000",     "2000",    "4000"] as String[]
		def propertyNames = ["slp125", "slp250", "slp500", "slp1000",  "slp2000", "slp4000"] as String[]
		def writable      = [false] * columnNames.length as boolean[]
		gltmClosure(columnNames, propertyNames, writable, tableModels.akustikZuluft, "akustik.zuluft")
	}
	
	/**
	 * Akustikberechnung - Abluft.
	 */
	def createAkustikAbluftTableModel() {
		def columnNames =   ["125",    "250",    "500",    "1000",     "2000",    "4000"] as String[]
		def propertyNames = ["slp125", "slp250", "slp500", "slp1000",  "slp2000", "slp4000"] as String[]
		def writable      = [false] * columnNames.length as boolean[]
		gltmClosure(columnNames, propertyNames, writable, tableModels.akustikAbluft, "akustik.abluft")
	}
	
	/**
	 * Einen Raum im Model und allen TableModels hinzufügen, Comboboxen synchronisieren.
	 */
	def addRaum = { raum, view ->
		synchronized (map.raum.raume) {
			def r = (raumMapTemplate + raum) as ObservableMap
			println "addRaum: adding raum=${r?.dump()}"
			map.raum.raume << r
			// Sync table models
			[tableModels.raume, tableModels.raumeVsZuAbluftventile, tableModels.raumeVsUberstromventile].each {
				//println "addRaum: ${map.raum.raume.raumBezeichnung} r.bezeichnung=${r.raumBezeichnung}"
				//println "addRaum: map.raum.raume.size=${map.raum.raume.size()} r.position=${r.position}"
				//println "map.raum.raume[r.position] -> ${map.raum.raume[r.position]}"
				it.add(map.raum.raume[r.position])
			}
			/* TODO Bitte nur Vorlagen wie in Raumdaten nehmen, nicht die tatsächlichen Räume
			// Neues DefaultComboBoxModel für AkustikView setzen
			try {
				map.akustik.raumBezeichnung.add(r.raumBezeichnung)
			} catch (e) {
				// combobox...
				map.akustik.raumBezeichnung = []
				map.akustik.raumBezeichnung.add(r.raumBezeichnung)
			}
			view.akustikAbluftRaumbezeichnung.setModel(new DefaultComboBoxModel(map.akustik.raumBezeichnung as String[]))
			view.akustikZuluftRaumbezeichnung.setModel(new DefaultComboBoxModel(map.akustik.raumBezeichnung as String[]))
			*/
			//view.raumTabelle.setModel(createRaumTableModel())
			//
			def geschossEventList = GlazedLists.eventList(meta.raum.geschoss) as ca.odell.glazedlists.EventList
			DefaultCellEditor raumGeschossCellEditor = AutoCompleteSupport.createTableCellEditor(geschossEventList)
			TableColumn raumGeschossColumn = view.raumTabelle.getColumnModel().getColumn(1)
			raumGeschossColumn.setCellEditor(raumGeschossCellEditor)
			//
			def luftartEventList = GlazedLists.eventList(meta.raum.luftart) as ca.odell.glazedlists.EventList
			DefaultCellEditor raumLuftartCellEditor = AutoCompleteSupport.createTableCellEditor(luftartEventList)
			TableColumn raumLuftartColumn = view.raumTabelle.getColumnModel().getColumn(2)
			raumLuftartColumn.setCellEditor(raumLuftartCellEditor)
			//
			def raumVsluftartEventList = GlazedLists.eventList(meta.raum.luftart) as ca.odell.glazedlists.EventList
			DefaultCellEditor raumVsLuftartCellEditor = AutoCompleteSupport.createTableCellEditor(raumVsluftartEventList)
			TableColumn raumVsLuftartColumn = view.raumVsZuAbluftventileTabelle.getColumnModel().getColumn(1)
			raumVsLuftartColumn.setCellEditor(raumVsLuftartCellEditor)
			//
			def raumVsUsluftartEventList = GlazedLists.eventList(meta.raum.luftart) as ca.odell.glazedlists.EventList
			DefaultCellEditor raumVsUsLuftartCellEditor = AutoCompleteSupport.createTableCellEditor(raumVsUsluftartEventList)
			TableColumn raumVsUsLuftartColumn = view.raumVsUberstromventileTabelle.getColumnModel().getColumn(1)
			raumVsUsLuftartColumn.setCellEditor(raumVsUsLuftartCellEditor)
			/*
			println "-" * 80
			println "akustikAbluftRaumbezeichnung -> ${view.akustikAbluftRaumbezeichnung}"
			println "akustikZuluftRaumbezeichnung -> ${view.akustikZuluftRaumbezeichnung}"
			println "-" * 80
			*/
		}
	}
	
	/**
	 * Einen Raum aus dem Model entfernen, alle TableModels synchronisieren.
	 */
	def removeRaum = { raumIndex ->
		synchronized (map.raum.raume) {
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
			//
            map.akustik.raumBezeichnung.remove(raumIndex)
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
		synchronized (tableModels) {
			println "-" * 80
			println "resyncRaumTableModels"
			// Raumdaten
			tableModels.raume.clear()
			tableModels.raume.addAll(map.raum.raume)
			// Raumvolumentströme - Zu-/Abluftventile
			tableModels.raumeVsZuAbluftventile.clear()
			tableModels.raumeVsZuAbluftventile.addAll(map.raum.raume)
			// Raumvolumentströme - Überströmventile
			tableModels.raumeVsUberstromventile.clear()
			tableModels.raumeVsUberstromventile.addAll(map.raum.raume)
			// java.lang.NullPointerException: Cannot invoke method addAll() on null object when RaumBearbeitenDialog was not opened before
			// Quickfix: added null-safe-operator
			tableModels.raumeBearbeiten?.addAll(map.raum.raume)
			println "-" * 80
		}
	}
	
	/**
	 * 
	 */
	def addDvbKanalnetz = { kanalnetz, view ->
		synchronized (map.dvb.kanalnetz) {
			def k = (dvbKanalnetzMapTemplate + kanalnetz) as ObservableMap
			println "addDvbKanalnetz: adding kanalnetz=${k.dump()}"
			map.dvb.kanalnetz << k
			// Sync table model
			[tableModels.dvbKanalnetz].each {
				it.add(map.dvb.kanalnetz[kanalnetz.position])
			}
			// Comboboxen in den Tabellen hinzufügen
			def dvbKnLuftartEventList = GlazedLists.eventList(meta.raum.luftart) as ca.odell.glazedlists.EventList
			DefaultCellEditor dvbKnLuftartCellEditor = AutoCompleteSupport.createTableCellEditor(dvbKnLuftartEventList)
			TableColumn dvbKnLuftartColumn = view.dvbKanalnetzTabelle.getColumnModel().getColumn(0)
			dvbKnLuftartColumn.setCellEditor(dvbKnLuftartCellEditor)
			//
			def dvbKnKanalbezeichnungEventList = GlazedLists.eventList(meta.dvbKanalbezeichnung) as ca.odell.glazedlists.EventList
			DefaultCellEditor dvbKnKanalbezeichnungCellEditor = AutoCompleteSupport.createTableCellEditor(dvbKnKanalbezeichnungEventList)
			TableColumn dvbKnKanalbezeichnungColumn = view.dvbKanalnetzTabelle.getColumnModel().getColumn(3)
			dvbKnKanalbezeichnungColumn.setCellEditor(dvbKnKanalbezeichnungCellEditor)
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
	def addDvbVentileinstellung = { ventileinstellung, view ->
		def v = (dvbVentileinstellungMapTemplate + ventileinstellung) as ObservableMap
		println "addDvbVentileinstellung: adding ventileinstellung=${v.dump()}"
		map.dvb.ventileinstellung << v
		// Sync table model
		[tableModels.dvbVentileinstellung].each {
			it.add(map.dvb.ventileinstellung[ventileinstellung.position])
		}
		// Comboboxen in den Tabellen hinzufügen
		def dvbVeLuftartEventList = GlazedLists.eventList(meta.raum.luftart) as ca.odell.glazedlists.EventList
		DefaultCellEditor dvbVeLuftartCellEditor = AutoCompleteSupport.createTableCellEditor(dvbVeLuftartEventList)
		TableColumn dvbVeLuftartColumn = view.dvbVentileinstellungTabelle.getColumnModel().getColumn(1)
		dvbVeLuftartColumn.setCellEditor(dvbVeLuftartCellEditor)
		//
		def dvbVeVentiltypEventList = GlazedLists.eventList(meta.dvbVentileinstellung) as ca.odell.glazedlists.EventList
		DefaultCellEditor dvbVeVentiltypCellEditor = AutoCompleteSupport.createTableCellEditor(dvbVeVentiltypEventList)
		TableColumn dvbVeVentiltypColumn = view.dvbVentileinstellungTabelle.getColumnModel().getColumn(3)
		dvbVeVentiltypColumn.setCellEditor(dvbVeVentiltypCellEditor)
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
		synchronized (tableModels) {
			tableModels.dvbKanalnetz.clear()
			tableModels.dvbKanalnetz.addAll(map.dvb.kanalnetz)
		}
	}
	
	/**
	 * Synchronize all Swing table models depending on map.dvb.ventileinstellung.
	 */
	def resyncDvbVentileinstellungTableModels() {
		// Druckverlust - Ventileinstellung
		synchronized (tableModels) {
			tableModels.dvbVentileinstellung.clear()
			tableModels.dvbVentileinstellung.addAll(map.dvb.ventileinstellung)
		}
	}
	
	/**
	 * Synchronize all Swing table models depending on map.akustik.*.tabelle.
	 */
	def resyncAkustikTableModels() {
		if (DEBUG) println "resyncAkustikTableModels()"
		synchronized (tableModels) {
			// Akustikberechnung Zuluft
			tableModels.akustikZuluft.clear()
			map.akustik.zuluft.tabelle.each { tableModels.akustikZuluft.addAll(it) }
			// Akustikberechnung Abluft
			tableModels.akustikAbluft.clear()
			map.akustik.abluft.tabelle.each { tableModels.akustikAbluft.addAll(it) }
		}
	}
	
}

/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/models/com/westaflex/wac/ProjektModel.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

import groovy.beans.Bindable
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
	@Bindable meta
	
	/**
	 * WAC calculation service.
	 */
	def wacCalculationService
	
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
			raumVolumen: 0.0d,
			raumLuftwechsel: 0.0d,
			raumZuluftVolumenstrom: 0.0d,
			raumAbluftVolumenstrom: 0.0d,
			raumBezeichnungAbluftventile: "",
			raumAnzahlAbluftventile: 0,
			raumAbluftmengeJeVentil: 0.0d,
			raumBezeichnungZuluftventile: "",
			raumAnzahlZuluftventile: 0,
			raumZuluftmengeJeVentil: 0.0d,
			raumVerteilebene: "",
			raumAnzahlUberstromVentile: 0,
			raumUberstromElement: "",
			raumUberstromVolumenstrom: "",
			raumNummer: "",
			raumMaxTurspaltHohe: 10.0d,
			turen: []
		]
	
	/**
	 * Template für Türen eines Raumes.
	 */
	def raumTurenTemplate = [
				[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
				[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
				[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
				[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
				[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true]
			] /*as ObservableList*/
	
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
		messages: [ltm: ""] as ObservableMap,
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
			raumBezeichnung: [] as ObservableList // TODO Wofür wird er genutzt?
	] as ObservableMap
	
	// TableModels
	def tmPositionComparator = { a, b -> a.position <=> b.position } as Comparator
	def tmNameComparator = { a, b -> a.name <=> b.name } as Comparator
	def tmNothingComparator = { a, b -> 0 } as Comparator
	def tableModels = [
			raume:                   new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			raumeTuren:              [/* TableModels will be added in addRaum() */],
			raumeVsZuAbluftventile:  new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			raumeVsUberstromventile: new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			dvbKanalnetz:            new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			dvbVentileinstellung:    new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList,
			wbw:                     [/* TableModels will be added in addWbwTableModel() */],
			akustikZuluft:           new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmNothingComparator) as ca.odell.glazedlists.EventList,
			akustikAbluft:           new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmNothingComparator) as ca.odell.glazedlists.EventList
		]
	
	/**
	 * Wrap text in HTML and substitute every space character with HTML-breaks.
	 */
	def ws = GH.ws
	
	/**
	 * Prüfe Raumdaten auf Richtigkeit.
	 * @return raum
	 */
	def prufeRaumdaten = { raum ->
        // Anhand des Raumtyps nicht benötigte Werte löschen
		switch (raum.raumLuftart) {
			case ~/ZU.*/:
				raum.with {
					raumAnzahlAbluftventile = 0
					raumAbluftmengeJeVentil = 0.0d 
					raumBezeichnungAbluftventile = ""
					raumAbluftVolumenstrom = 0.0d
				}
				// Prüfe Toleranzwerte für Zuluftfaktor
				def eingegebenerZuluftfaktor = raum.raumZuluftfaktor.toDouble2()
				def (zuluftfaktor, neuerZuluftfaktor) =
					wacCalculationService.prufeZuluftfaktor(raum.raumTyp, eingegebenerZuluftfaktor)
				if (zuluftfaktor != neuerZuluftfaktor) {
					def infoMsg = "Der Zuluftfaktor wird von ${zuluftfaktor} auf ${neuerZuluftfaktor} (laut Norm-Tolerenz) geändert!"
					app.controllers["Dialog"].showInformDialog(infoMsg as String)
					if (DEBUG) println infoMsg
				}
				raum.raumZuluftfaktor = neuerZuluftfaktor
				break
			case "AB":
				raum.with {
					raumAnzahlZuluftventile = 0
					raumZuluftmengeJeVentil = 0.0d
					raumBezeichnungZuluftventile = ""
					raumZuluftVolumenstrom = 0.0d
					raumZuluftfaktor = 0.0d
				}
				break
		}
        // Wenn Raum = ÜB dann Zu/Abluftventile leeren
        if (raum.raumLuftart == "ÜB") {
            raum.with {
                // Zuluft
                raumAnzahlZuluftventile = 0
                raumZuluftmengeJeVentil = 0.0d
                raumBezeichnungZuluftventile = ""
                raumZuluftVolumenstrom = 0.0d
                raumZuluftfaktor = 0.0d
                // Abluft
                raumAnzahlAbluftventile = 0
                raumAbluftmengeJeVentil = 0.0d
                raumBezeichnungAbluftventile = ""
                raumAbluftVolumenstrom = 0.0d
            }
        }
        //
		if (DEBUG) println "checkRaum: ${raum}"
		raum
	}
	
	/**
	 * Prüfe Daten in einem Raum auf Plausibilität.
	 * @return raum
	 * Siehe Ticket #60, #66.
	 */
	def checkRaum = { object, property, value, columnIndex ->
		println "checkRaum: $object, $property, $value, $columnIndex"
		// Try to save double value; see ticket 60
		object[property] = value.toDouble2()
		prufeRaumdaten(map.raum.raume.find { it.position == object.position })
	}
	
	/**
	 * Closure to generate glazedlists EventTableModel.
	 * @param columnNames String[]
	 * @param propertyNames String[]
	 * @param writable boolean[]
	 * @param tableModel ca.odell.glazedlists.EventList
	 * @param postValueSet Closure to execute after value was set
	 * @param preValueSet Closure to execute before value was set
	 */
	def gltmClosure = { columnNames, propertyNames, writable, tableModel, postValueSet = null, preValueSet = null ->
		new ca.odell.glazedlists.swing.EventTableModel(tableModel, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { columnIndex -> columnNames[columnIndex] },
				getColumnValue: { object, columnIndex ->
					try {
						object."${propertyNames[columnIndex]}"?.toString2()
					} catch (e) {
						println "gltmClosure, getColumnValue: ${e}: ${object?.dump()}"
						object?.toString()
					}
				},
				isEditable:     { object, columnIndex -> writable[columnIndex] },
				setColumnValue: { object, value, columnIndex ->
					def property = propertyNames[columnIndex]
					if (DEBUG) println "gltmClosure, setColumnValue: ${property}=${value}"
					// Call pre-value-set closure
					if (preValueSet) object = preValueSet(object, property, value, columnIndex)
					else {
						// Try to save double value; see ticket #60
						object[property] = value.toDouble2()
					}
					// Call post-value-set closure
					if (postValueSet) postValueSet(object, columnIndex, value)
					// VERY IMPORTANT: return null value to prevent e.g. returning
					// a boolean value. Table would display the wrong value in all
					// cells !!!
					null
				},
				getValueAt: { rowIndex, columnIndex ->
					if (DEBUG) println "gltmClosure, getValueAt: rowIndex=${rowIndex}, columnIndex=${columnIndex}"
					//no value to get...
				}
			] as ca.odell.glazedlists.gui.WritableTableFormat)
	}

    /**
	 * Closure to generate glazedlists EventTableModel.
	 * @param columnNames String[]
	 * @param propertyNames String[]
     * @param propertyTypes String[] String array of the class types e.g. Integer.class.getName() etc.
	 * @param writable boolean[]
	 * @param tableModel ca.odell.glazedlists.EventList
	 * @param postValueSet Closure to execute after value was set
	 * @param preValueSet Closure to execute before value was set
	 */
	def gltmClosureWithTypes = { columnNames, propertyNames, propertyTypes, writable, tableModel, postValueSet = null, preValueSet = null ->
		new ca.odell.glazedlists.swing.EventTableModel(tableModel, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { columnIndex -> columnNames[columnIndex] },
				getColumnValue: { object, columnIndex ->
                    def isInt
                    try {
                        if (propertyTypes[columnIndex].equals(Integer.class.getName())) {
                            isInt = true
                        }
                    } catch (e) {

                    }
					try {
                        if (isInt || isInt == true) {
                            object."${propertyNames[columnIndex]}"?.toString()
                        }
                        else {
						    object."${propertyNames[columnIndex]}"?.toString2()
                        }
					} catch (e) {
						println "gltmClosure, getColumnValue: ${e}: ${object?.dump()}"
						object?.toString()
					}
				},
				isEditable:     { object, columnIndex -> writable[columnIndex] },
				setColumnValue: { object, value, columnIndex ->
					def property = propertyNames[columnIndex]
					if (DEBUG) println "gltmClosure, setColumnValue: ${property}=${value}"
					// Call pre-value-set closure
					if (preValueSet) object = preValueSet(object, property, value, columnIndex)
					else {
						// Try to save double value; see ticket #60
						object[property] = value.toDouble2()
					}
					// Call post-value-set closure
					if (postValueSet) postValueSet(object, columnIndex, value)
					// VERY IMPORTANT: return null value to prevent e.g. returning
					// a boolean value. Table would display the wrong value in all
					// cells !!!
					null
				},
				getValueAt: { rowIndex, columnIndex ->
					if (DEBUG) println "gltmClosure, getValueAt: rowIndex=${rowIndex}, columnIndex=${columnIndex}"
					//no value to get...
				}
			] as ca.odell.glazedlists.gui.WritableTableFormat)
	}
	
	/**
	 * ATTENTION: Used only within TableModel for Raume/Turen!
	 * @param columnNames String[]
	 * @param propertyNames String[]
	 * @param writable boolean[]
	 * @param tableModel ca.odell.glazedlists.EventList
	 * @param postValueSet Closure to execute after value was set
	 * @param preValueSet Closure to execute before value was set
	 */
	def gltmClosureCheckbox = { columnNames, propertyNames, writable, tableModel, postValueSet = null, preValueSet = null ->
		println "gltmClosureCheckbox: tablelModel=${tableModel?.dump()}"
		new ca.odell.glazedlists.swing.EventTableModel(tableModel, [
				getColumnCount: { columnNames.size() },
				getColumnName:  { columnIndex -> columnNames[columnIndex] },
				getColumnValue: { object, columnIndex ->
                    try {
                        if (DEBUG) println "###### gltmClosureCheckbox: object -> ${object?.dump()}"
                    } catch (e) {}
					if (columnIndex == 4) {
						def tempValue = object."${propertyNames[columnIndex]}"
						//println "tempValue ${tempValue}"
						if (tempValue == "0,00" || tempValue == "0.00") {
							true
						} else {
							tempValue
						}
					} else {
						try {
							object."${propertyNames[columnIndex]}"?.toString2()
						} catch (e) {
							println "gltmClosureCheckbox, getColumnValue: ${e}: ${object?.dump()}"
							object?.toString()
						}
					}
				},
				isEditable:     { object, columnIndex -> writable[columnIndex] },
				setColumnValue: { object, value, columnIndex ->
					def property = propertyNames[columnIndex]
					if (DEBUG) println "gltmClosureCheckbox, setColumnValue: ${property}=${value}"
					if (columnIndex == 4) {
						object[property] = value
					} else {
						// Call pre-value-set closure
						if (preValueSet) object = preValueSet(object, property, value, columnIndex)
						else {
							// Try to save double value; see ticket 60
							object[property] = value.toDouble2()
						}
					}
					// Call post-value-set closure
					if (postValueSet) postValueSet(object, columnIndex, value)
					// VERY IMPORTANT: return null value to prevent e.g. returning
					// a boolean value. Table would display the wrong value in all
					// cells !!!
					null
				},
				getValueAt: { rowIndex, columnIndex ->
					if (DEBUG) println "gltmClosureCheckbox, getValueAt: rowIndex=${rowIndex}, columnIndex=${columnIndex}"
					// No value to get...
				},
				getColumnClass: { columnIndex ->
					if (columnIndex == 4) {
						java.lang.Boolean.class
					}
				},
				getColumnComparator: { columnIndex ->
					null
				}
			] as com.bensmann.griffon.AdvancedWritableTableFormat)
	}
	
	/**
	 * Raumdaten - TableModel
	 */
	def createRaumTableModel() {
		def columnNames =   ["Raum",            "Geschoss",     "Luftart",     ws("Raumfläche<br/>[m²]"), ws("Raumhöhe<br/>[m]"), "Zuluftfaktor",     "Abluftvolumenstrom"] as String[]
		def propertyNames = ["raumBezeichnung", "raumGeschoss", "raumLuftart", "raumFlache",              "raumHohe",             "raumZuluftfaktor", "raumAbluftVolumenstrom"] as String[]
		def writable      = [true,              true,           false,         true,                      true,                   true,               true] as boolean[]
		def postValueSet  = { object, columnIndex, value ->
			def myTempMap = map.raum.raume.find { it.position == object.position }
			myTempMap[columnIndex] = value
			meta.gewahlterRaum[columnIndex] = value
			//println "Edited: map.raum.raume -> ${map.raum.raume}"
			// Call ProjektController
			app.controllers[mvcId].raumGeandert()
			resyncRaumTableModels()
		}
		gltmClosure(columnNames, propertyNames, writable, tableModels.raume, postValueSet, checkRaum)
	}
	
	/**
	 * Raumvolumenströme, Zu-/Abluftventile - TableModel
	 */
	def createRaumVsZuAbluftventileTableModel() {
		def columnNames =   ["Raum",            "Luftart",     ws("Raum [m³]"), ws("Luftwechsel<br/>[1/h]"), ws("Zuluft<br/>[m³/h]"), ws("Bezeichnung<br/>Zuluftventile"),    ws("Anzahl<br/>Zuluftventile"),    ws("Zuluftmenge<br/>je Ventil"), ws("Abluft<br/>[m³/h]"), ws("Bezeichnung<br/>Abluftventile"),    ws("Anzahl<br/>Abluftventile"), ws("Abluftmenge<br/>je Ventil"),   "Verteilebene"] as String[]
		def propertyNames = ["raumBezeichnung", "raumLuftart", "raumVolumen",              "raumLuftwechsel",           "raumZuluftVolumenstrom",             "raumBezeichnungZuluftventile",         "raumAnzahlZuluftventile",         "raumZuluftmengeJeVentil",       "raumAbluftVolumenstrom",             "raumBezeichnungAbluftventile",         "raumAnzahlAbluftventile",      "raumAbluftmengeJeVentil",         "raumVerteilebene"] as String[]
		def writable      = [true,              false,         false,                      false,                       false,                                true,                                   false,                             false,                           false,                                true,                                   false,                          false,                             true] as boolean[]
		def postValueSet  = { object, columnIndex, value ->
			// Call ProjektController
			app.controllers[mvcId].raumGeandert()
			app.controllers[mvcId].raumZuAbluftventileGeandert()
			resyncRaumTableModels()
		}
		gltmClosure(columnNames, propertyNames, writable, tableModels.raumeVsZuAbluftventile, postValueSet, checkRaum)
	}
	
	/**
	 * Raumvolumenströme - Überströmventile TableModel
	 */
	def createRaumVsUberstromelementeTableModel() {
		def columnNames =   ["Raum",            "Luftart",     "Anzahl Ventile",             "Überström [m³/h]", "Überström-Elemente"] as String[]
		def propertyNames = ["raumBezeichnung", "raumLuftart", "raumAnzahlUberstromVentile", "raumUberstromVolumenstrom",     "raumUberstromElement"] as String[]
		def writable      = [true,              false,         false,                        true,                            true] as boolean[]
		def postValueSet  = { object, columnIndex, value ->
			// Call ProjektController
			app.controllers[mvcId].raumGeandert()
			app.controllers[mvcId].raumUberstromelementeGeandert()
			resyncRaumTableModels()
		}
		gltmClosure(columnNames, propertyNames, writable, tableModels.raumeVsUberstromventile, postValueSet, checkRaum)
	}
	
	/**
	 * RaumBearbeitenView - Türen.
	 */
	def createRaumTurenTableModel() {
		def index = meta.gewahlterRaum.position
		if (DEBUG) {
			println "createRaumTurenTableModel: index=${index}"
			println "createRaumTurenTableModel: raumeTuren: ${tableModels.raumeTuren}"
			println "#########"
			println "createRaumTurenTableModel: raumeTuren[index]: ${tableModels.raumeTuren[index]}"
		}
		def columnNames =   ["Bezeichnung",    "Breite [mm]", "Querschnittsfläche [mm²]", "Spaltenhöhe [mm]", "mit Dichtung"] as String[]
		def propertyNames = ["turBezeichnung", "turBreite",   "turQuerschnitt",           "turSpalthohe",     "turDichtung"] as String[]
		def writable      = [true,             true,          false,                      false,              true] as boolean[]
		def postValueSet  = { object, columnIndex, value -> 
			// Call ProjektController
			app.controllers[mvcId].berechneTuren()
		}
		gltmClosureCheckbox(columnNames, propertyNames, writable, tableModels.raumeTuren[index], postValueSet)
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz.
	 */
	def createDvbKanalnetzTableModel() {
		def columnNames =   ["Luftart",     "Teilstrecke",  ws("Luft [m³/h]"), "Kanalbezeichnung", ws("Kanallänge<br/>[m]"), ws("Geschwindigkeit<br/>[m/s]"), ws("Reibungswiderstand<br/>gerader Kanal<br/>[Pa]"), ws("Gesamtwider-<br/>standszahl"), ws("Einzelwider-<br/>stand<br/>[Pa]"), ws("Widerstand<br/>Teilstrecke<br/><[Pa]")] as String[]
		def propertyNames = ["luftart",     "teilstrecke",  "luftVs",                                "kanalbezeichnung", "lange",                  "geschwindigkeit",               "reibungswiderstand",                                "gesamtwiderstandszahl",           "einzelwiderstand",                    "widerstandTeilstrecke"] as String[]
		def writable      = [false,         true,           true,                                    true,               true,                     false,                           false,                                               true/* TODO false*/,               false,                                 false] as boolean[]
		def postValueSet  = { object, columnIndex, value ->
			def myTempMap = map.dvb.kanalnetz.find { it.position == object.position }
			myTempMap[columnIndex] = value
			if (DEBUG) println "Edited: map.dvb.kanalnetz -> ${map.dvb.kanalnetz}"
			// Call ProjektController
			app.controllers[mvcId].dvbKanalnetzGeandert(object.position)
			resyncDvbKanalnetzTableModels()
		}
		gltmClosure(columnNames, propertyNames, writable, tableModels.dvbKanalnetz, postValueSet)
	}
	
	/**
	 * Druckverlustberechnung - Ventileinstellung.
	 */
	def createDvbVentileinstellungTableModel() {
		def columnNames =   ["Raum", "Luftart",     "Teilstrecken", "Ventiltyp",         "dP offen [Pa]", "Gesamt [Pa]",      "Differenz", "Abgleich [Pa]", "Einstellung"] as String[]
		def propertyNames = ["raum", "luftart",     "teilstrecken", "ventilbezeichnung", "dpOffen",       "gesamtWiderstand", "differenz", "abgleich",      "einstellung"] as String[]
		def writable      = [true,   false,         true,           true,                false,           false,              false,       false,            false] as boolean[]
		def postValueSet  = { object, columnIndex, value ->
			def myTempMap = map.dvb.ventileinstellung.find { it.position == object.position }
			myTempMap[columnIndex] = value
			if (DEBUG) println "Edited: map.dvb.ventileinstellung -> ${map.dvb.ventileinstellung}"
			// Call ProjektController
			app.controllers[mvcId].dvbVentileinstellungGeandert(object.position)
			resyncDvbVentileinstellungTableModels()
		}
		gltmClosure(columnNames, propertyNames, writable, tableModels.dvbVentileinstellung, postValueSet)
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz - Widerstandsbeiwerte.
	 */
	def addWbwTableModel(index) {
		if (DEBUG) println "addWbwTableModel(${index}): ${tableModels.wbw[index]}"
		// TableModel schon vorhanden?
		if (tableModels.wbw[index]) return
		// Neues TableModel erstellen und füllen
		tableModels.wbw << new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmNameComparator) as ca.odell.glazedlists.EventList
		meta.wbw.each {
			tableModels.wbw[index].add([id: it.id, anzahl: 0 as Integer, name: it.bezeichnung, widerstandsbeiwert: it.wert])
		}
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz - Widerstandsbeiwerte.
	 * TableModel für WBW des aktuell gewählten Kanalnetz liefern.
	 */
	def getSelectedWbwTableModel() {
		tableModels.wbw[meta.dvbKanalnetzGewahlt]
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz - Widerstandsbeiwerte.
	 */
	def createWbwTableModel() {
		def index = meta.dvbKanalnetzGewahlt
		if (DEBUG) println "createWbwTableModel: index=${index}"
		def columnNames =   ["Anzahl", "Bezeichnung", "Widerstandsbeiwert"] as String[]
		def propertyNames = ["anzahl", "name",        "widerstandsbeiwert"] as String[]
		def propertyTypes = [Integer.class.getName(), String.class.getName(), Double.class.getName()] as String[]
		def writable      = [true, true, true] as boolean[]
		def postValueSet  = { object, columnIndex, value ->
			app.controllers[mvcId].wbwSummieren()
			app.controllers[mvcId].wbwInTabelleGewahlt()
		}
		// Widerstandsbeiwerte für die gewählte Kanalnetz in tableModels.wbw übertragen
		gltmClosureWithTypes(columnNames, propertyNames, propertyTypes, writable, tableModels.wbw[index], postValueSet)
	}
	
	/**
	 * Akustikberechnung - Zuluft.
	 */
	def createAkustikZuluftTableModel() {
		def columnNames =   ["125",    "250",    "500",    "1000",     "2000",    "4000"] as String[]
		def propertyNames = ["slp125", "slp250", "slp500", "slp1000",  "slp2000", "slp4000"] as String[]
		def writable      = [false] * columnNames.length as boolean[]
		gltmClosure(columnNames, propertyNames, writable, tableModels.akustikZuluft)
	}
	
	/**
	 * Akustikberechnung - Abluft.
	 */
	def createAkustikAbluftTableModel() {
		def columnNames =   ["125",    "250",    "500",    "1000",     "2000",    "4000"] as String[]
		def propertyNames = ["slp125", "slp250", "slp500", "slp1000",  "slp2000", "slp4000"] as String[]
		def writable      = [false] * columnNames.length as boolean[]
		gltmClosure(columnNames, propertyNames, writable, tableModels.akustikAbluft)
	}
	
	/**
	 * Raum, Turen: add model.
	 */
	def addRaumTurenModel() {
		tableModels.raumeTuren <<
			new ca.odell.glazedlists.SortedList(new ca.odell.glazedlists.BasicEventList(), tmPositionComparator) as ca.odell.glazedlists.EventList
	}
	
	/**
	 * 
	 */
	def setRaumEditors(view) {
		javax.swing.SwingUtilities.invokeLater {
			// Raumdaten - Geschoss
			GH.makeComboboxCellEditor view.raumTabelle.columnModel.getColumn(1), meta.raum.geschoss
			// Raumdaten - Luftart
			GH.makeComboboxCellEditor view.raumTabelle.columnModel.getColumn(2), meta.raum.luftart
			// RaumVs Zu- und Abluftventile
			// Combobox RaumVs - Luftart
			GH.makeComboboxCellEditor view.raumVsZuAbluftventileTabelle.columnModel.getColumn(1), meta.raum.luftart
			// Combobox RaumVs - Bezeichnung Abluftmenge
			GH.makeComboboxCellEditor view.raumVsZuAbluftventileTabelle.columnModel.getColumn(5), meta.raumVsBezeichnungAbluftventile
			// Combobox RaumVs - Bezeichnung Zuluftmenge
			GH.makeComboboxCellEditor view.raumVsZuAbluftventileTabelle.columnModel.getColumn(9), meta.raumVsBezeichnungZuluftventile
			// Combobox RaumVs - Verteilebene
			GH.makeComboboxCellEditor view.raumVsZuAbluftventileTabelle.columnModel.getColumn(12), meta.raum.geschoss
			// RaumVs Überströmventile
			// Combobox RaumVs - Luftart
			GH.makeComboboxCellEditor view.raumVsUberstromelementeTabelle.columnModel.getColumn(1), meta.raum.luftart
			// Combobox RaumVs - Überströmelemente
			GH.makeComboboxCellEditor view.raumVsUberstromelementeTabelle.columnModel.getColumn(4), meta.raumVsUberstromelemente
			// TODO: Verbesserung! Später freischalten.
			// Raum Typ für Druckverlustberechnung - Ventileinstellung Combobox.
			//updateDvbVentileinstellungComboBoxModel(view)
		}
	}
	
	/**
	 * Einen Raum im Model und allen TableModels hinzufügen, Comboboxen synchronisieren.
	 */
	def addRaum = { raum, view, isCopy = false ->
		synchronized (map.raum.raume) {
			raum = prufeRaumdaten(raum)
			// Raumdaten mit Template zusammführen
			if (DEBUG) println "addRaum: isCopy -> ${isCopy}"
			if (isCopy || isCopy == "true") {
				map.raum.raume.add(raum)
				if (DEBUG) println "addRaum: copy -> map: ${map.raum.raume}"
			} else {
				if (DEBUG) println "${raum}"
				// Türen erstellen und mit bereits vorhandenen überschreiben!
				def r = ([turen:
							[
								[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
								[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
								[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
								[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
								[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true]
							]
						] + raum) as ObservableMap
				if (DEBUG) println "addRaum: adding raum=${r?.dump()}"
				if (DEBUG) println "addRaum: raumMapTemplate=${raumMapTemplate.turen}"
				if (DEBUG) println "addRaum: adding raum after editing r.turen=${r?.dump()}"
				// Raum in der Map hinzufügen
				map.raum.raume << raum
				if (DEBUG) println "addRaum: adding raum.raume=${map.raum.raume}"
			}

			// disables sorting in raumTabelle
            try {
                view.raumTabelle.setSortable(false);
                view.raumTabelle.getTableHeader().setDefaultRenderer(new javax.swing.table.JTableHeader().getDefaultRenderer());
            } catch (e) {
                println "ProjektModel: addRaum: Error while modifying raumTabelle: ${e}"
            }

            // disables sorting in raumVsUberstromelementeTabelle
            try {
                view.raumVsUberstromelementeTabelle.setSortable(false);
                view.raumVsUberstromelementeTabelle.getTableHeader().setDefaultRenderer(new javax.swing.table.JTableHeader().getDefaultRenderer());
            } catch (e) {
                println "ProjektModel: addRaum: Error while modifying raumVsUberstromelementeTabelle: ${e}"
            }

            // disables sorting in raumVsZuAbluftventileTabelle
            try {
                view.raumVsZuAbluftventileTabelle.setSortable(false);
                view.raumVsZuAbluftventileTabelle.getTableHeader().setDefaultRenderer(new javax.swing.table.JTableHeader().getDefaultRenderer());
            } catch (e) {
                println "ProjektModel: addRaum: Error while modifying raumVsZuAbluftventileTabelle: ${e}"
            }

			addRaumTurenModel()
			// Sync table models
			resyncRaumTableModels()
			//
			setRaumEditors(view)
		}
	}
	
	/**
	 * Einen Raum aus dem Model entfernen, alle TableModels synchronisieren.
	 */
	def removeRaum = { raumIndex, view ->
		synchronized (map.raum.raume) {
			if (DEBUG) println "removeRaum: removing raumIndex=${raumIndex}"
			map.raum.raume.remove(raumIndex)
			// Sync table models
			[tableModels.raume, tableModels.raumeVsZuAbluftventile, tableModels.raumeVsUberstromventile].each {
				it.remove(raumIndex)
			}
			// TODO: Verbesserung! Später freischalten.
			//updateDvbVentileinstellungComboBoxModel(view)
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
			//println "-" * 80
			//println "resyncRaumTableModels"
			// Raumdaten
			tableModels.raume.clear()
			tableModels.raume.addAll(map.raum.raume)
			// Türen
            if (DEBUG) println "tableModels.raume -> ${tableModels.raume}"
            if (DEBUG) println "tableModels.raumeTuren -> BEFORE -> ${tableModels.raumeTuren}"
			tableModels.raume.each {
                def m = tableModels.raumeTuren[it.position]
				// TODO NullPointer when loading data
				try {
                    m.clear()
					m.addAll(it.turen)
                    if (DEBUG) println "tableModels.raume.each -> raumeTuren: ${m}"
				} catch (e) {}
			}
            if (DEBUG) println "tableModels.raumeTuren -> AFTER -> ${tableModels.raumeTuren}"
			// Raumvolumentströme - Zu-/Abluftventile
			tableModels.raumeVsZuAbluftventile.clear()
			tableModels.raumeVsZuAbluftventile.addAll(map.raum.raume)
			// Raumvolumentströme - Überströmventile
			tableModels.raumeVsUberstromventile.clear()
			tableModels.raumeVsUberstromventile.addAll(map.raum.raume)
			// java.lang.NullPointerException: Cannot invoke method addAll() on null object
			// when RaumBearbeitenDialog was not opened before
			// Quickfix: added null-safe-operator
			tableModels.raumeBearbeiten?.addAll(map.raum.raume)
			//println "-" * 80
		}
	}
	
	/**
	 * 
	 */
	def addDvbKanalnetz = { kanalnetz, view ->
		synchronized (map.dvb.kanalnetz) {
			// Kanalnetz mit Template zusammenführen
			def k = (dvbKanalnetzMapTemplate + kanalnetz) as ObservableMap
			if (DEBUG) println "addDvbKanalnetz: adding kanalnetz=${k.dump()}"
			// In der Map hinzufügen
			map.dvb.kanalnetz << k
			// Sync table model
			[tableModels.dvbKanalnetz].each {
				it.add(map.dvb.kanalnetz[kanalnetz.position])
			}
			// Comboboxen in den Tabellen hinzufügen
			GH.makeComboboxCellEditor view.dvbKanalnetzTabelle.getColumnModel().getColumn(0), meta.raum.luftart
			GH.makeComboboxCellEditor view.dvbKanalnetzTabelle.getColumnModel().getColumn(3), meta.dvbKanalbezeichnung
		}
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz: eine Zeile aus dem Model entfernen.
	 */
	def removeDvbKanalnetz = { kanalnetzIndex ->
		synchronized (map.dvb.kanalnetz) {
			map.dvb.kanalnetz.remove(kanalnetzIndex)
			try { tableModels.wbw.remove(kanalnetzIndex) } catch (e) {}
			// Sync table models
			[tableModels.dvbKanalnetz].each {
				it.remove(kanalnetzIndex)
			}
		}
	}
	
	/**
	 * Druckverlustberechnung - Ventileinstellung.
	 */
	def addDvbVentileinstellung = { ventileinstellung, view ->
		def v = (dvbVentileinstellungMapTemplate + ventileinstellung) as ObservableMap
		if (DEBUG) println "addDvbVentileinstellung: adding ventileinstellung=${v.dump()}"
		map.dvb.ventileinstellung << v
		// Sync table model
		[tableModels.dvbVentileinstellung].each {
			it.add(map.dvb.ventileinstellung[ventileinstellung.position])
		}
		// Comboboxen in den Tabellen hinzufügen
		GH.makeComboboxCellEditor view.dvbVentileinstellungTabelle.getColumnModel().getColumn(1), meta.raum.luftart
		GH.makeComboboxCellEditor view.dvbVentileinstellungTabelle.getColumnModel().getColumn(3), meta.dvbVentileinstellung
	}
	
	/**
	 * ComboBox Model für die Räume in der Druckverlustberechnung Ventileinstellung setzen.
	 */
	def updateDvbVentileinstellungComboBoxModel = { view ->
		def newComboBoxModel = meta.raum.typ + map.raum.raume.raumBezeichnung as Set
		view.dvbVentileinstellungRaum.setModel(new DefaultComboBoxModel(newComboBoxModel.toArray()))
	}
	
	/**
	 * 
	 */
	def removeDvbVentileinstellung = { ventileinstellungIndex ->
		synchronized (map.dvb.ventileinstellung) {
			map.dvb.ventileinstellung.remove(ventileinstellungIndex)
			// Sync table models
			[tableModels.dvbVentileinstellung].each {
				it.remove(ventileinstellungIndex)
			}
		}
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
	 * Synchronize all Swing table models depending on map.dvb.kanalnetz.wbw.
	 */
	def resyncWbwTableModels() {
		// NOT NEEDED
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

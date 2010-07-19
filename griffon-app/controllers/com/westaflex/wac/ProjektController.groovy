/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/controllers/com/westaflex/wac/ProjektController.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

import javax.swing.DefaultCellEditor
import javax.swing.JComboBox
import javax.swing.JTable
import javax.swing.table.AbstractTableModel
import javax.swing.event.TableModelListener
import java.awt.Component

/**
 * 
 */
@griffon.util.EventPublisher
class ProjektController {
	
	def builder
	def model
	def view
	def wacCalculationService
	def wacModelService
	
	/**
	 * User's settings.
	 */
	private def prefs = java.util.prefs.Preferences.userNodeForPackage(ProjektController)
	
	/**
	 * Initialize MVC group.
	 */
	void mvcGroupInit(Map args) {
		// Splash screen
		Wac2Splash.instance.initializingProject()
		// Save MVC id
		model.mvcId = args.mvcId
		// Reference meta values
		model.meta = app.models["wac2"].meta
		setDefaultValues()
		// Add PropertyChangeListener to our model.meta
		GH.addMapPropertyChangeListener("meta", model.meta)
		// Add PropertyChangeListener to our model.map
		GH.addMapPropertyChangeListener("map", model.map, { evt ->
				// Only set dirty flag, when modified property is not the dirty flag
				// Used for loading and saving
				if (evt.propertyName != "dirty" && !model.map.dirty) {
					// Dirty-flag im eigenen und Wac2Model setzen
					model.map.dirty = true
					app.models["wac2"].aktivesProjektGeandert = true
					// Change tab title (show a star)
					setTabTitle()
				}
			})
		// Setup private event listener
		// This properties are used with constructor of the event listener
		def props = [
				model: model,
				wacCalculationService: wacCalculationService,
				wacModelService: wacModelService
			]
		GH.tieEventListener(this, ProjektEvents, props)
		GH.tieEventListener(this, GebaudeEvents, props)
		GH.tieEventListener(this, RaumEvents, props)
		GH.tieEventListener(this, AussenluftVsEvents, props)
	}
	
	/**
	 * Method call interception.
	Object invokeMethod(String methodName, Object params) {
		println "ProjektController.invokeMethod: ${methodName}"
		metaClass.invokeMethod(this, methodName, params)
	}
	 */
	
	/**
	 * Setze Standardwerte (meist in Comboboxen).
	 */
	def setDefaultValues() {
		// Raumvolumenströme
		model.map.anlage.zentralgerat = model.meta.zentralgerat[0]
		model.map.anlage.volumenstromZentralgerat = model.meta.volumenstromZentralgerat //model.meta.volumenstromZentralgerat[0]
		// Druckverlustberechnung - Kanalnetz - Kanalbezeichnung
		model.map.dvb.kanalbezeichnung = model.meta.dvbKanalbezeichnung
		// Druckverlustberechnung - Ventileinstellung - Ventilbezeichnung
		model.map.dvb.ventileinstellung = model.meta.dvbVentileinstellung[]
	}
	
	/**
	 * Titel der Tab für dieses Projekt erstellen: Bauvorhaben und Sternchen für ungesicherte Änderungen.
	 */
	def makeTabTitle = {
		def title = new StringBuilder()
		// Bauvorhaben
		def bauvorhaben = model.map.kundendaten.bauvorhaben
		if (bauvorhaben) {
			title << "Projekt - ${bauvorhaben}"
		} else {
			title << model.mvcId
		}
		// MVC ID
		title << " (${view.mvcId})"
		// Ungespeicherte Daten?
		if (model.map.dirty) title << "*"
		title.toString()
	}
	
	/**
	 * Titel der Tab für dieses Projekt setzen.
	 */
	def setTabTitle() {
		view.projektTabGroup.setTitleAt(view.projektTabGroup.selectedIndex, makeTabTitle())
	}
	
	/**
	 * Can we close? Is there unsaved data -- is our model dirty?
	 */
	boolean canClose() {
		model.map.dirty == false
	}
	
	/**
	 * Gebäudedaten - Geometrie wurde manuell eingegeben.
	 */
	def berechneGeometrie = {
		// Read values from view and transfer them into our model
		def g = [
			wohnflache: view.gebaudeGeometrieWohnflache.text.toDouble2(),
			raumhohe: view.gebaudeGeometrieMittlereRaumhohe.text.toDouble2(),
			gelufteteFlache: view.gebaudeGeometrieGelufteteFlache.text.toDouble2()
		]
		// Write values into model
		model.map.gebaude.geometrie.wohnflache = g.wohnflache
		model.map.gebaude.geometrie.raumhohe = g.raumhohe
		model.map.gebaude.geometrie.gelufteteFlache = g.gelufteteFlache
		//
		publishEvent "GeometrieEingegeben"
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def luftdichtheitKategorieA = {
		doLater {
			model.map.gebaude.luftdichtheit.with {
				druckdifferenz = 2.0d
				luftwechsel = 1.0d
				druckexponent = 0.666f
			}
		}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def luftdichtheitKategorieB = {
		doLater {
			model.map.gebaude.luftdichtheit.with {
				druckdifferenz = 2.0d
				luftwechsel = 1.5f
				druckexponent = 0.666f
			}
		}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def luftdichtheitKategorieC = {
		doLater {
			model.map.gebaude.luftdichtheit.with {
				druckdifferenz = 2.0d
				luftwechsel = 2.0d
				druckexponent = 0.666f
			}
		}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def speichereLuftdichtheit = {
		doLater {
			model.map.gebaude.luftdichtheit.druckdifferenz = view.gebaudeLuftdichtheitDruckdifferenz.text.toDouble2()
			model.map.gebaude.luftdichtheit.luftwechsel = view.gebaudeLuftdichtheitLuftwechsel.text.toDouble2()
			model.map.gebaude.luftdichtheit.druckexponent = view.gebaudeLuftdichtheitDruckexponent.text.toDouble2()
		}
	}
	
	/**
	 * Gebäudedaten - Faktor für besondere Anforderungen
	 */
	def speichereFaktorBesondereAnforderungen = {
		doLater {
			model.map.gebaude.faktorBesondereAnforderungen = view.faktorBesondereAnforderungen.text.toDouble2()
		}
	}
	
	/**
	 * Gebäudedaten - Geplante Belegung
	 */
	def berechneMindestaussenluftrate = {
		doLater {
			model.map.gebaude.geplanteBelegung.with {
				try {
					mindestaussenluftrate = personenanzahl * aussenluftVsProPerson
				} catch (e) {
					// TODO mmu Dialog via Oxbow
					e.printStackTrace()
					mindestaussenluftrate = 0.0d
				}
			}
		}
	}
	
	/**
	 * Anlagendaten - Energie-Kennzeichen
	 */
	def berechneEnergieKennzeichen = {
		doLater {
			model.map.anlage.energie.with {
				if (zuAbluftWarme && bemessung && ruckgewinnung && regelung) {
					nachricht = "Energiekennzeichen gesetzt!"
				} else {
					nachricht = " "
				}
			}
			berechneKennzeichenLuftungsanlage()
		}
	}
	
	/**
	 * Anlagendaten - Hygiene-Kennzeichen
	 */
	def berechneHygieneKennzeichen = {
		doLater {
			model.map.anlage.hygiene.with {
				if (ausfuhrung && filterung && keineVerschmutzung && dichtheitsklasseB) {
					nachricht = "Hygienekennzeichen gesetzt!"
				} else {
					nachricht = " "
				}
			}
			berechneKennzeichenLuftungsanlage()
		}
	}
	
	/**
	 * Anlagendaten - Kennzeichen
	 */
	def berechneKennzeichenLuftungsanlage = {
		doLater {
			def gebaudeTyp = model.map.gebaude.efh ? "EFH" : "WE"
			def energieKz = model.map.anlage.energie.nachricht != " " ? "E" : "0"
			def hygieneKz = model.map.anlage.hygiene.nachricht != " " ? "H" : "0"
			def ruckschlag = model.map.anlage.ruckschlagKappe ? "RK" : "0"
			def schallschutz = model.map.anlage.schallschutz ? "S" : "0"
			def feuerstatte = model.map.anlage.feuerstatte ? "F" : "0"
			model.map.anlage.kennzeichnungLuftungsanlage =
				"ZuAbLS-Z-${gebaudeTyp}-WÜT-${energieKz}-${hygieneKz}-${ruckschlag}-${schallschutz}-${feuerstatte}"
		}
	}
	
	/**
	 * 
	 */
	def setRoundingMode = {
		def rm = java.math.RoundingMode.valueOf(view.aussenluftVsRoundingMode.selectedItem)
		println "setRoundingMode: setting rounding mode to ${rm.dump()}"
		GH.ROUNDING_MODE = rm
	}
	
	/**
	 * Aussenluftvolumenströme - Mit/ohne Infiltrationsanteil berechnen.
	 */
	def berechneAussenluftVs = {
		publishEvent "AussenluftVsBerechnen"
	}
	
	/**
	 * Raumdaten - Raumtyp in Combobox ausgewählt.
	 */
	def raumTypGeandert = {
		doLater {
			switch (view.raumTyp.selectedIndex) {
				// Zulufträume
				case 0..5:
					view.raumLuftart.selectedItem = "ZU"
					switch (view.raumTyp.selectedIndex) {
						case 0:
							view.raumAbluftVs.text = ""
							view.raumZuluftfaktor.text = "3,00"
							break
						case 1..2:
							view.raumAbluftVs.text = ""
							view.raumZuluftfaktor.text = "2,00"
							break
						case 3..5:
							view.raumAbluftVs.text = ""
							view.raumZuluftfaktor.text = "1,50"
							break
					}
					break
				// Ablufträume
				case 6..13:
					view.raumLuftart.selectedItem = "AB"
					switch (view.raumTyp.selectedIndex) {
						case 6..8:
							view.raumZuluftfaktor.text = ""
							view.raumAbluftVs.text = "25"
							break
						case 9..12:
							view.raumZuluftfaktor.text = ""
							view.raumAbluftVs.text = "45"
							break
						case 13:
							view.raumZuluftfaktor.text = ""
							view.raumAbluftVs.text = "100"
							break
					}
					break
				// Überströmräume
				case { it > 13 }:
					view.raumLuftart.selectedItem = "ÜB"
					view.raumZuluftfaktor.text = ""
					view.raumAbluftVs.text = ""
			}
		}
	}
	
	/**
	 * Raumdaten - Raum anlegen.
	 */
	def raumHinzufugen = {
		// Hole Werte für neuen Raum aus der View und füge Raum hinzu
		publishEvent "RaumHinzufugen", [GH.getValuesFromView(view, "raum")]

                // Neues TableModel setzen !
                // TODO mmu/rbe: Wann ist das model.map.raum.raume aktualisiert??? Ich brauche es hier!
                doLater {
                    def i = model.map.raum.raume.size() - 1 ?: 0
                    GH.updateTableModel(model.map.raum.raume[i], builder, view.raumVsZuAbluftventileTabelle)
                    //def raumeVsZuAbluftventile = GH.tweakTableModelBuilder(dataList)
                    //raumVsZuAbluftventileTabelle.setModel(raumeVsZuAbluftventile)
                }
	}
	
	/**
	 * Raumdaten - einen Raum entfernen.
	 */
	def raumEntfernen = {
		publishEvent "RaumEntfernen", [view.raumTabelle.selectedRow/*model.meta.gewahlterRaum.position*/]
	}
	
	/**
	 * Raumdaten - einen Raum kopieren.
	 */
	def raumKopieren = {
		doLater {
			// Get selected row
			def row = view.raumTabelle.selectedRow
			// Raum anhand seiner Position finden und eine Kopie erzeugen
			def x = model.map.raum.raume.find { it.position == row }.clone()
			// Neuen Namen und neue Position (Ende) setzen
			x.raumBezeichnung = "Kopie von ${x.raumBezeichnung}"
			x.position = model.map.raum.raume.size()
			// Raum zum Model hinzufügen
			model.map.raum.raume.add(x)
			// Raum hinzugefügt
			publishEvent "RaumHinzugefugt", [rowCount - 1]
		}
	}
	
	/**
	 * Raumdaten - einen Raum in der Tabelle nach oben verschieben.
	 */
	def raumNachObenVerschieben = {
		doLater {
			// Get selected row
			def row = view.raumTabelle.selectedRow
			if (row > 0) {
				// Recalculate positions
				model.map.raum.raume.each {
					if (it.position == row - 1) it.position += 1
					else if (it.position == row) it.position -= 1
				}
				model.resyncRaumTableModels()
				// Raum geändert
				publishEvent "RaumGeandert", [row - 1]
			}
		}
	}
	
	/**
	 * Raumdaten - einen Raum in der Tabelle nach oben verschieben.
	 */
	def raumNachUntenVerschieben = {
		doLater {
			// Get selected row
			def row = view.raumTabelle.selectedRow
			if (row < view.raumTabelle.rowCount - 1) {
				// Recalculate positions
				model.map.raum.raume.each {
					if (it.position == row + 1) it.position -= 1
					else if (it.position == row) it.position += 1
				}
				model.resyncRaumTableModels()
				// Raum geändert
				publishEvent "RaumGeandert", [row - 1]
			}
		}
	}
	
	/**
	 * Raumdaten - einen Raum bearbeiten.
	 */
	def raumBearbeiten = {
		// Get selected row
		def row = view.raumTabelle.selectedRow
		if (row > -1) {
			/* Aktuellen Raum in Metadaten setzen -- dies wurde durch das Event RaumInTabelleWahlen bereits erledigt
			model.meta.gewahlterRaum.putAll(model.map.raum.raume[row])
			*/
			// Show dialog
			def dialog = GH.showDialog(builder, RaumBearbeitenView)
			println "raumBearbeiten: dialog '${dialog.title}' closed: dialog=${dialog.dump()}"
			// Berechne alles, was von Räumen abhängt
			publishEvent "RaumGeandert", [row]
		}
	}
	
	/**
	 * Raumvolumenströme - Zu/Abluftventile, Luftmenge berechnen.
	 */
	def raumZuAbluftventileLuftmengeBerechnen = {
		def raumIndex = view.raumVsZuAbluftventileTabelle.selectedRow
		if (raumIndex > -1) {
			publishEvent "RaumZuAbluftventileLuftmengeBerechnen", [raumIndex]
		} else {
			println "raumZuAbluftventileLuftmengeBerechnen: Kein Raum ausgewählt, es wird nichts berechnet"
		}
	}
	
	/**
	 * Raumvolumenströme - Überströmelemente, Luftmenge berechnen.
	 */
	def raumUberstromelementeLuftmengeBerechnen = {
		def raumIndex = view.raumVsUberstromventileTabelle.selectedRow
		if (raumIndex > -1) {
			publishEvent "RaumUberstromelementeLuftmengeBerechnen", [raumIndex]
		} else {
			println "raumUberstromelementeLuftmengeBerechnen: Kein Raum ausgewählt, es wird nichts berechnet"
		}
	}
	
	/**
	 * Raum bearbeiten - Luftart ändern.
	 */
	def raumBearbeitenLuftartGeandert = {
		println "TODO raumBearbeitenLuftartGeandert"
	}
	
	/**
	 * Raum bearbeiten - Geometrie eingegeben.
	 */
	def raumBearbeitenGeometrieGeandert = {
		println "TODO raumBearbeitenGeometrieGeandert"
	}
	
	/**
	 * In einer der Raum-Tabellen wurde die Auswahl durch den Benutzer geändert:
	 * alle anderen Tabellen anpassen.
	 * evt = javax.swing.event.ListSelectionEvent
	 */
	def raumInTabelleGewahlt = { evt, table ->
		if (!evt.isAdjusting && evt.firstIndex > -1 && evt.lastIndex > -1) {
			// source = javax.swing.ListSelectionModel
			def selectedRow = evt.source.leadSelectionIndex
			//println "raumInTabelleGewahlt: ${evt.dump()}, selectedRow=${selectedRow}"
			onRaumInTabelleWahlen(selectedRow, table)
		}
	}
	
	/**
	 * Execute code with all Raum-tables...
	 */
	def withAllRaumTables = { closure ->
		view.with {
			[raumTabelle, raumVsZuAbluftventileTabelle, raumVsUberstromventileTabelle].each { t ->
				closure(t)
			}
		}
	}
	
	/**
	 * Einen bestimmten Raum in allen Raum-Tabellen markieren.
	 */
	def onRaumInTabelleWahlen = { row, table = null ->
		doLater {
			//println "onRaumInTabelleWahlen: row=${row}"
			row = GH.checkRow(row, view.raumTabelle)
			if (row > -1) {
				// Raum in Raumdaten-Tabelle, Raumvolumenströme-Zu/Abluftventile-Tabelle, Raumvolumenströme-Überströmelemente-Tabelle markieren
				withAllRaumTables { t ->
					GH.withDisabledListSelectionListeners t, { -> changeSelection(row, 0, false, false) }
				}
				// Aktuellen Raum in Metadaten setzen
				model.meta.gewahlterRaum.putAll(model.map.raum.raume[row])
				model.meta.gewahlterRaum.raumNummer = row + 1
			} else {
				// Remove selection in all tables
				withAllRaumTables { t ->
					t.clearSelection()
				}
				// Aktuell gewählten Raum in Metadaten leeren
				model.meta.gewahlterRaum.clear()
			}
		}
	}
	
	/**
	 * Raumvolumenströme - Zentralgerät: manuelle Auswahl des Zentralgeräts.
	 */
	def zentralgeratGewahlt = {
		// TODO rbe Compare old and new value; only change values when old != new?
		doLater {
			view.raumVsVolumenstrom.removeAllItems()
			// Hole Volumenströme des Zentralgeräts
			model.meta.volumenstromZentralgerat =
				wacModelService.getVolumenstromFurZentralgerat(view.raumVsZentralgerat.selectedItem)
			// Füge Volumenströme in Combobox hinzu
			model.meta.volumenstromZentralgerat.each { view.raumVsVolumenstrom.addItem(it) }
			// Im Projekt-Model speichern
			model.map.anlage.zentralgerat = view.raumVsZentralgerat.selectedItem
			// Merken, dass das Zentralgerät manuell ausgewählt wurde
			// -> keine automatische Auswahl des Zentralgeräts mehr durchführen
			model.map.anlage.zentralgeratManuell = true
		}
	}
	
	/**
	 * Raumvolumenströme - Zentralgerät: automatische Aktualisierung das Zentralgeräts.
	 */
	def onZentralgeratAktualisieren = {
		doLater {
			println "onZentralgeratAktualisieren: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
			if (!model.map.anlage.zentralgeratManuell) {
				def (zentralgerat, nl) = wacCalculationService.berechneZentralgerat(model.map)
				println "onZentralgeratAktualisieren: zentralgerat=${zentralgerat}, nl=${nl}/${wacCalculationService.round5(nl)}"
				// Aktualisiere Zentralgerät
				GH.withDisabledActionListeners view.raumVsZentralgerat, {
					model.map.anlage.zentralgerat =
						view.raumVsZentralgerat.selectedItem =
						zentralgerat
				}
				// Aktualisiere Volumenstrom
				GH.withDisabledActionListeners view.raumVsVolumenstrom, {
					view.raumVsVolumenstrom.removeAllItems()
					// Hole Volumenströme des Zentralgeräts
					model.meta.volumenstromZentralgerat =
						wacModelService.getVolumenstromFurZentralgerat(view.raumVsZentralgerat.selectedItem)
					// Füge Volumenströme in Combobox hinzu
					model.meta.volumenstromZentralgerat.each { view.raumVsVolumenstrom.addItem(it) }
					//
					model.map.anlage.volumenstromZentralgerat =
						view.raumVsVolumenstrom.selectedItem =
						wacCalculationService.round5(nl)
				}
			}
		}
	}
	

        def initTableModelBuilder() {
            def myTableModel = GH.initTableModelBuilder(builder)
            myTableModel
        }
	
	/**
	 * Raumvolumenströme - Volumenstrom des Zentralgeräts.
	 */
	def volumenstromZentralgeratGewahlt = {
		// Im Projekt-Model speichern
		model.map.anlage.volumenstromZentralgerat = view.raumVsVolumenstrom.selectedItem?.toInteger()
	}
	
}

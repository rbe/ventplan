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
	 * Initialize MVC group.
	 */
	void mvcGroupInit(Map args) {
		// Save MVC id
		model.mvcId = args.mvcId
		// Add PropertyChangeListener to our model.meta
		GH.addMapPropertyChange("meta", model.meta)
		// Add PropertyChangeListener to our model.map
		GH.addMapPropertyChange("map", model.map, { model.dirty = true })
		// Lookup values from database and put them into our model
		// TODO rbe Move into Wac2 MVC group as this data is needed for every single project
		model.meta.raumVsBezeichnungZuluftventile =
			model.meta.raumVsBezeichnungAbluftventile =
			wacModelService.getZuAbluftventile()
		model.meta.raumVsUberstromelement = wacModelService.getUberstromelemente()
		model.meta.zentralgerat = wacModelService.getZentralgerat()
		model.meta.volumenstromZentralgerat = wacModelService.getVolumenstromFurZentralgerat(model.meta.zentralgerat[0])
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
			def gebaudeTyp = model.map.gebaude.EFH ? "EFH" : "WE"
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
						case 0..1:
							view.raumAbluftVs.text = ""
							view.raumZuluftfaktor.text = "3,00"
							break
						case 2..3:
							view.raumAbluftVs.text = ""
							view.raumZuluftfaktor.text = "2,00"
							break
						case 4..6:
							view.raumAbluftVs.text = ""
							view.raumZuluftfaktor.text = "1,50"
							break
					}
					break
				// Ablufträume
				case 6..13:
					view.raumLuftart.selectedItem = "AB"
					switch (view.raumTyp.selectedIndex) {
						case 6..9:
							view.raumZuluftfaktor.text = ""
							view.raumAbluftVs.text = "25"
							break
						case 10..12:
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
	}
	
	/**
	 * 
	 */
	def raumInTabelleGewahlt = { /*javax.swing.event.ListSelectionEvent*/evt ->
		if (!evt.isAdjusting && evt.firstIndex > -1 && evt.lastIndex > -1) {
			println "raumInTabelleGewahlt: ${evt}"
		}
	}
	
	/**
	 * Einen bestimmten Raum in allen Tabellen markieren.
	 */
	def onRaumInTabelleWahlen = { row ->
		doLater {
			if (row > -1) {
				// Raum in Raumdaten-Tabelle markieren
				view.raumTabelle.changeSelection(row, 0, false, false)
				// Raum in Raumvolumenströme-Zu/Abluftventile-Tabelle markieren
				view.raumVsZuAbluftventileTabelle.changeSelection(row, 0, false, false)
				// Raum in Raumvolumenströme-Überströmelemente-Tabelle markieren
				view.raumVsUberstromventileTabelle.changeSelection(row, 0, false, false)
				// Aktuellen Raum in Metadaten setzen
				model.meta.gewahlterRaum.putAll(model.map.raum.raume[row])
				/*
				model.map.raum.raume[row].each { k, v ->
					try {
						model.meta.gewahlterRaum[k] = v
					} catch(e) {
						println "!!!!!!!!!!!!!!!!!!!! ${k}=${v}: ${e}"
					}
				}
				*/
			} else {
				// TODO view.raumTabelle.removeSelection?
				model.meta.gewahlterRaum.removeAll()
			}
		}
	}
	
	/**
	 * Raumdaten - einen Raum entfernen.
	 */
	def raumEntfernen = {
		// Get selected row
		def row = view.raumTabelle.selectedRow
		model.map.raum.raume.remove(row)
		// Select new row
		view.raumTabelle.with {
			if (row == rowCount) row = rowCount - 1
			else if (row < 0) row = 0
			changeSelection(row, 0, false, false)
		}
		// Berechne alles, was von Räumen abhängt
		publishEvent "RaumEntfernt"
	}
	
	/**
	 * Raumdaten - einen Raum kopieren.
	 */
	def raumKopieren = {
		// Get selected row
		def row = view.raumTabelle.selectedRow
		// Find room and make a copy
		def x = model.map.raum.raume.find { it.position == row }.clone()
		// Set name and position
		x.raumBezeichnung = "Kopie von ${x.raumBezeichnung}"
		x.position = model.map.raum.raume.size()
		// Add to model
		model.map.raum.raume.add(x)
		// Select new row
		view.raumTabelle.with {
			changeSelection(rowCount - 1, 0, false, false)
		}
		// Berechne alles, was von Räumen abhängt
		publishEvent "RaumHinzugefugt"
	}
	
	/**
	 * Raumdaten - einen Raum in der Tabelle nach oben verschieben.
	 */
	def raumNachObenVerschieben = {
		// Get selected row
		def row = view.raumTabelle.selectedRow
		if (row > 0) {
			// Recalculate positions
			model.map.raum.raume.each {
				if (it.position == row - 1) it.position += 1
				else if (it.position == row) it.position -= 1
			}
			model.syncRaumTableModels()
			// Select new row
			view.raumTabelle.with {
				changeSelection(row - 1, 0, false, false)
			}
		}
	}
	
	/**
	 * Raumdaten - einen Raum in der Tabelle nach oben verschieben.
	 */
	def raumNachUntenVerschieben = {
		// Get selected row
		def row = view.raumTabelle.selectedRow
		if (row < view.raumTabelle.rowCount - 1) {
			// Recalculate positions
			model.map.raum.raume.each {
				if (it.position == row + 1) it.position -= 1
				else if (it.position == row) it.position += 1
			}
			model.syncRaumTableModels()
			// Select new row
			view.raumTabelle.with {
				changeSelection(row + 1, 0, false, false)
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
			// Aktuellen Raum in Metadaten setzen
			model.meta.gewahlterRaum.putAll(model.map.raum.raume[row])
			// Show dialog
			def dialog = GH.showDialog(builder, RaumBearbeitenView)
			println "raumBearbeiten: dialog '${dialog.title}' closed"
			// Berechne alles, was von Räumen abhängt
			publishEvent "RaumHinzugefugt"
		}
	}
	
	/**
	 * Raumvolumenströme - Zu/Abluftventile, Luftmenge berechnen.
	 */
	def raumZuAbluftventileLuftmengeBerechnen = {
		def raumIndex = raumVsZuAbluftventileTabelle.selectedRow
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
		def raumIndex = raumVsUberstromventileTabelle.selectedRow
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
		println "raumBearbeitenLuftartGeandert"
	}
	
	/**
	 * Raum bearbeiten - Geometrie eingegeben.
	 */
	def raumBearbeitenGeometrieGeandert = {
		println "raumBearbeitenGeometrieGeandert"
	}
	
	/**
	 * 
	 */
	def zentralgeratGewahlt = {
		// TODO Check old and new value
		doLater {
			view.raumVsVolumenstrom.removeAllItems()
			// Hole Volumenströme des Zentralgeräts
			model.meta.volumenstromZentralgerat = wacModelService.getVolumenstromFurZentralgerat(view.raumVsZentralgerat.selectedItem)
			// Füge Volumenströme in Combobox hinzu
			model.meta.volumenstromZentralgerat.each { view.raumVsVolumenstrom.addItem(it) }
		}
	}
	
}

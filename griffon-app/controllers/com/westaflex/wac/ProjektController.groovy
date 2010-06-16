package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

/**
 * 
 */
@griffon.util.EventPublisher
class ProjektController {
	
	def model
	def view
	def wacCalculationService
	
	/**
	 * Initialize MVC group.
	 */
	void mvcGroupInit(Map args) {
		// Save MVC id
		model.mvcId = args.mvcId
		// Add PropertyChangeListener to our model.map
		addMapPropertyChange(model.map)
		/*
		// Raumvolumenströme
		model.tableModels.raumeVsZuAbluftventile.addListEventListener({ evt ->
			println "raumVsZuAbluftventileTabelleTableModel,${evt}"
		} as ca.odell.glazedlists.event.ListEventListener)
		*/
		// Setup private event listener
		def pe = new ProjektEvents(model: model, wacCalculationService: wacCalculationService)
		addEventListener(pe)
		pe.addEventListener(this)
	}
	
	/**
	 * Dump a change
	 */
	def dumpPropertyChange = { evt, k ->
		println "${k}.${evt.propertyName}: value changed: ${evt.oldValue?.dump()} -> ${evt.newValue?.dump()}"
	}
	
	/**
	 * Recursively add PropertyChangeListener to the map itself and all nested maps.
	 */
	def addMapPropertyChange = { map ->
		map.each { k, v ->
			if (v instanceof ObservableMap) {
				//println "addMapPropertyChange: adding PropertyChangeListener for ${k}"
				v.addPropertyChangeListener({ evt ->
					dumpPropertyChange.delegate = v
					dumpPropertyChange(evt, k)
					model.dirty = true
				} as java.beans.PropertyChangeListener)
				addMapPropertyChange(v)
			}
		}
	}
	
	/**
	 * Gebäudedaten - Geometrie
	 */
	def berechneGeometrie = {
		// Read values from view and transfer them into our model
		def g = [
			wohnflache: view.gebaudeGeometrieWohnflache.text.toDouble2(),
			raumhohe: view.gebaudeGeometrieMittlereRaumhohe.text.toDouble2(),
			gelufteteFlache: view.gebaudeGeometrieGelufteteFlache.text.toDouble2()
		]
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
				druckdifferenz = 2.0f
				luftwechsel = 1.0f
				druckexponent = 0.666f
			}
			println "luftdichtheitKategorieA: ${model.map.gebaude.luftdichtheit?.dump()}"
		}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def luftdichtheitKategorieB = {
		doLater {
			model.map.gebaude.luftdichtheit.with {
				druckdifferenz = 2.0f
				luftwechsel = 1.5f
				druckexponent = 0.666f
			}
			println "luftdichtheitKategorieB: ${model.map.gebaude.luftdichtheit?.dump()}"
		}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def luftdichtheitKategorieC = {
		doLater {
			model.map.gebaude.luftdichtheit.with {
				druckdifferenz = 2.0f
				luftwechsel = 2.0f
				druckexponent = 0.666f
			}
			println "luftdichtheitKategorieC: ${model.map.gebaude.luftdichtheit?.dump()}"
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
			println "speichereLuftdichtheit: ${model.map.gebaude.luftdichtheit?.dump()}"
		}
	}
	
	/**
	 * Gebäudedaten - Faktor für besondere Anforderungen
	 */
	def speichereFaktorBesondereAnforderungen = {
		doLater {
			model.map.gebaude.faktorBesondereAnforderungen = view.faktorBesondereAnforderungen.text.toDouble2()
			println "speichereFaktorBesondereAnforderungen: ${model.map.gebaude.faktorBesondereAnforderungen?.dump()}"
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
					// TODO Dialog
					e.printStackTrace()
					mindestaussenluftrate = 0.0f
				}
			}
			println "berechneMindestaussenluftrate: ${model.map.gebaude.geplanteBelegung?.dump()}"
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
			def kennzeichen = new StringBuilder("ZuAbLS-Z-")
			def gebaudeTyp = model.map.gebaude.EFH ? "EFH" : "WE"
			def energieKz = model.map.anlage.energie.nachricht != " " ? "E" : "0"
			def hygieneKz = model.map.anlage.hygiene.nachricht != " " ? "H" : "0"
			def ruckschlag = model.map.anlage.ruckschlagKappe ? "RK" : "0"
			def schallschutz = model.map.anlage.schallschutz ? "S" : "0"
			def feuerstatte = model.map.anlage.feuerstatte ? "F" : "0"
			model.map.anlage.kennzeichnungLuftungsanlage = "ZuAbLS-Z-${gebaudeTyp}-WÜT-${energieKz}-${hygieneKz}-${ruckschlag}-${schallschutz}-${feuerstatte}"
		}
	}
	
	/**
	 * Raumdaten - Raumtyp ausgewählt
	 */
	def raumTypSelected = {
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
	 * Raumdaten - Raum anlegen
	 */
	def raumHinzufugen = {
		def raumWerte = GH.getValuesFromView(view, "raum")
		// Standard-Werte setzen
		raumWerte.with {
			// Übernehme Wert für Bezeichnung vom Typ?
			if (!raumBezeichnung) raumBezeichnung = raumTyp
			// Standard Türspalthöhe ist 10 mm
			raumTurspaltHohe = 10.0f
			// Raumvolumen
			raumFlache = raumFlache.toDouble2()
			raumHohe = raumHohe.toDouble2()
			raumVolumen = raumFlache * raumHohe
			// Raumvolumenströme, Ventile
			zuabluftVentile = []
			uberstromVentile = []
			// Zuluftfaktor
			raumZuluftfaktor = raumZuluftfaktor?.toDouble2() ?: 0.0f
			/*if (!raumZuluftfaktor) raumZuluftfaktor = 0.0f
			else raumZuluftfaktor = raumZuluftfaktor.toDouble2()*/
			// Abluftvolumenstrom
			raumAbluftVs = raumAbluftVs?.toDouble2() ?: 0.0f
			/*if (!raumAbluftVs) raumAbluftVs = 0.0f
			else raumAbluftVs = raumAbluftVs.toDouble2()*/
		}
		// Überstrom-Raum
		if (raumWerte.raumLuftart == "ÜB") {
			def (zuluftfaktor, neuerZuluftfaktor) = wacCalculationService.prufeZuluftfaktor(raumWerte.raumZuluftfaktor)
			if (zuluftfaktor != neuerZuluftfaktor) {
				// TODO Dialog
				println "Der Zuluftfaktor wird von ${zuluftfaktor} auf ${neuerZuluftfaktor} (laut Norm-Tolerenz) geändert!"
			}
			raumWerte.raumZuluftfaktor = neuerZuluftfaktor
		}
		// Update model and set table selection
		doLater {
			// Raum im Model hinzufügen
			//println "raumHinzufugen: raumWerte=${raumWerte}"
			model.map.raum.raume << raumWerte + [position: model.map.raum.raume.size() ?: 0]
			// Berechne alles, was von Räumen abhängt
			publishEvent "RaumHinzugefugt"
		}
	}
	
	/**
	 * Raum in Tabelle markieren
	 */
	def onRaumInTabelleWahlen = { msg ->
		println "onRaumInTabelleWahlen: msg=${msg}"
		// Raum in Tabelle markieren
		view.raumTabelle.with {
			changeSelection(msg, 0, false, false)
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
		// Open dialog
		new RaumdatenDialogView(model.map.raum.raume.get(row))
	}
	
	/**
	 * Aussenluftvolumenströme - Mit/ohne Infiltrationsanteil berechnen
	 */
	def berechneAussenluftVs = {
		publishEvent "AussenluftVsBerechnen"
	}
	
}

package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

/**
 * 
 */
//@griffon.util.EventPublisher
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
		// Räume
		model.map.raum.raume.addPropertyChangeListener({ evt ->
			doLater {
				println "raume,${evt.typeAsString}: ${evt.newValue}"
				model.syncRaumTableModels()
				raumEingegeben()
			}
		} as java.beans.PropertyChangeListener)
	}
	
	/**
	 * Dump a change
	 */
	def dumpPropertyChange = { evt, k ->
		println "${k}: value changed: ${evt.propertyName}: ${evt.oldValue} -> ${evt.newValue}"
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
		def g = model.map.gebaude.geometrie
		try {
			// Luftvolumen der Nutzungseinheit = Wohnfläche * mittlere Raumhöhe
			if (g.wohnflache && g.raumhohe) {
				g.luftvolumen = g.geluftetesVolumen = wacCalculationService.volumen(g.wohnflache.toFloat2(), g.raumhohe.toFloat2()).toString2()
			}
			// Gelüftetes Volumen = gelüftete Fläche * mittlere Raumhöhe
			if (g.gelufteteFlache && g.raumhohe) {
				g.geluftetesVolumen = wacCalculationService.volumen(g.gelufteteFlache.toFloat2(), g.raumhohe.toFloat2()).toString2()
			}
			// Gelüftetes Volumen = Luftvolumen, wenn kein gelüftetes Volumen berechnet
			if (g.luftvolumen && !g.geluftetesVolumen) {
				g.geluftetesVolumen = g.luftvolumen
			}
		} catch (e) {
			e.printStackTrace()
			g.luftvolumen = g.geluftetesVolumen = "E"
		}
		model.map.gebaude.geometrie = g
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def luftdichtheitKategorieA = {
		model.map.gebaude.luftdichtheit.with {
			druckdifferenz = "2,00"
			luftwechsel = "1,00"
			druckexponent = "0,666"
		}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def luftdichtheitKategorieB = {
		model.map.gebaude.luftdichtheit.with {
			druckdifferenz = "2,00"
			luftwechsel = "1,50"
			druckexponent = "0,666"
		}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def luftdichtheitKategorieC = {
		model.map.gebaude.luftdichtheit.with {
			druckdifferenz = "2,00"
			luftwechsel = "2,00"
			druckexponent = "0,666"
		}
	}
	
	/**
	 * Gebäudedaten - Geplante Belegung
	 */
	def berechneMindestaussenluftrate = {
		model.map.gebaude.geplanteBelegung.with {
			try {
				mindestaussenluftrate = personenanzahl * aussenluftVsProPerson
			} catch (e) {
				mindestaussenluftrate = 0
			}
		}
	}
	
	/**
	 * Anlagendaten - Energie-Kennzeichen
	 */
	def berechneEnergieKennzeichen = {
		model.map.anlage.energie.with {
			if (zuAbluftWarme && bemessung && ruckgewinnung && regelung) {
				nachricht = "Energiekennzeichen gesetzt!"
			} else {
				nachricht = " "
			}
		}
		berechneKennzeichenLuftungsanlage()
	}
	
	/**
	 * Anlagendaten - Hygiene-Kennzeichen
	 */
	def berechneHygieneKennzeichen = {
		model.map.anlage.hygiene.with {
			if (ausfuhrung && filterung && keineVerschmutzung && dichtheitsklasseB) {
				nachricht = "Hygienekennzeichen gesetzt!"
			} else {
				nachricht = " "
			}
		}
		berechneKennzeichenLuftungsanlage()
	}
	
	/**
	 * Anlagendaten - Kennzeichen
	 */
	def berechneKennzeichenLuftungsanlage = {
		def kennzeichen = new StringBuilder("ZuAbLS-Z-")
		def gebaudeTyp = model.map.gebaude.EFH ? "EFH" : "WE"
		def energieKz = model.map.anlage.energie.nachricht != " " ? "E" : "0"
		def hygieneKz = model.map.anlage.hygiene.nachricht != " " ? "H" : "0"
		def ruckschlag = model.map.anlage.ruckschlagKappe ? "RK" : "0"
		def schallschutz = model.map.anlage.schallschutz ? "S" : "0"
		def feuerstatte = model.map.anlage.feuerstatte ? "F" : "0"
		model.map.anlage.kennzeichnungLuftungsanlage = "ZuAbLS-Z-${gebaudeTyp}-WÜT-${energieKz}-${hygieneKz}-${ruckschlag}-${schallschutz}-${feuerstatte}"
	}
	
	/**
	 * Raumdaten - Raumtyp ausgewählt
	 */
	def raumTypSelected = {
		switch (view.raumTyp.selectedIndex) {
			// Zulufträume
			case 0..6:
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
			case 7..13:
				view.raumLuftart.selectedItem = "AB"
				switch (view.raumTyp.selectedIndex) {
					case 7..9:
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
			default:
				view.raumLuftart.selectedItem = "ÜB"
				view.raumZuluftfaktor.text = ""
				view.raumAbluftVs.text = ""
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
			raumTurspaltHohe = "10,00"
			// Raumvolumen
			raumVolumen = raumFlache * raumHohe
			// Raumvolumenströme, Ventile
			zuabluftVentile = []
			uberstromVentile = []
		}
		// Überstrom-Raum
		if (raumWerte.raumLuftart == "ÜB") {
			// Prüfe den Zuluftfaktor, Rückgabe: [übergebener wert, neuer wert]
			def prufeZuluftfaktor = { float zf ->
				def minMax = { v, min, max -> if (v < min) { min } else if (v > max) { max } else { v } }
				def nzf
				switch (raumWerte.raumBezeichnung) {
					case "Wohnzimmer":                                  nzf = minMax(zf, 2.5f, 3.5f); break
					case ["Kinderzimmer", "Schlafzimmer"]:              nzf = minMax(zf, 1.0f, 3.0f); break
					case ["Esszimmer", "Arbeitszimmer", "Gästezimmer"]: nzf = minMax(zf, 1.0f, 2.0f); break
					default: nzf = zf
				}
				[zf, nzf]
			}
			def (zuluftfaktor, neuerZuluftfaktor) = prufeZuluftfaktor(raumWerte.raumZuluftfaktor.toFloat2())
			if (zuluftfaktor != neuerZuluftfaktor) {
				println "Der Zuluftfaktor wird von ${zuluftfaktor} auf ${neuerZuluftfaktor} (laut Norm-Tolerenz) geändert!"
			}
			raumWerte.raumZuluftfaktor = neuerZuluftfaktor.toString2()
		}
		// Update model and set table selection
		doLater {
			// Raum im Model hinzufügen
			model.map.raum.raume << raumWerte + [position: model.map.raum.raume.size() ?: 0]
			// Geometrie berechnen
			// Raum in Tabelle markieren
			view.raumTabelle.with {
				changeSelection(rowCount - 1, 0, false, false)
			}
		}
		//println "raumHinzufugen: raumWerte=${raumWerte}"
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
	 * Raumdaten - ein Raum wurde angelegt, nun Gebäudedaten - Geometrie ändern
	 */
	def raumEingegeben = {
		model.map.with {
			// Gebäudedaten - Geometrie: Gesamtfläche berechnen
			gebaude.geometrie.wohnflache = (raum.raume.sum { it.raumFlache.toFloat2() }).toString2()
			// Gebäudedaten - Geometrie: Mittlere Raumhöhe berechnen
			gebaude.geometrie.raumhohe = (raum.raume.sum { it.raumHohe.toFloat2() } / raum.raume.size()).toString2()
		}
	}
	
	/**
	 * Außenluftvolumenströme
	 */
	def berechneAussenluftVs = {
		wacCalculationService.aussenluftVs(model.map)
	}
	
}

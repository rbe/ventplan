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
		model.mvcId = args.mvcId
		addPropertyChange(model.map)
	}
	
	/**
	 * Dump a change
	 */
	def dumpPropertyChange = { evt, k ->
		println "${k}: value changed: ${evt.propertyName}: ${evt.oldValue} -> ${evt.newValue}"
	}
	
	/**
	 * Recursively add PropertyChangeListener to the/all nested maps.
	 */
	def addPropertyChange = { map ->
		map.each { k, v ->
			if (v instanceof Map) {
				println "ProjektController.mvcGroupInit: adding PropertyChangeListener for ${k}"
				v.addPropertyChangeListener({ evt ->
					dumpPropertyChange.delegate = v
					dumpPropertyChange(evt, k)
				} as java.beans.PropertyChangeListener)
				addPropertyChange(v)
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
				g.geluftetesVolumen = wacCalculationService.volumen(g.gelufteteFlache.toFloat2(), g.raumhohe.toFloat2()).toFloat2()
			}
			// Gelüftetes Volumen = Luftvolumen, wenn kein gelüftetes Volumen berechnet
			if (g.luftvolumen && !g.geluftetesVolumen) {
				g.geluftetesVolumen = g.luftvolumen
			}
		} catch (e) {
			//e.printStackTrace()
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
	 * Raumdaten - Raum anlegen
	 */
	def raumHinzufugen = {
		def raumWerte = GH.getValuesFromView(view, "raum")
		// Standard-Werte setzen
		raumWerte.with {
			// Übernehme Wert für Bezeichnung vom Typ
			if (!raumBezeichnung) raumBezeichnung = raumTyp
			// Standard Türspalthöhe ist 10 mm
			raumTurspaltHohe = "10,00"
			// Raumvolumen
			raumVolumen = raumFlache * raumHohe
		}
		// Überstrom-Raum
		if (raumWerte.raumLuftart == "ÜB") {
			// Prüfe den Zuluftfaktor, Rückgabe: [übergebener wert, neuer wert]
			def prufeZuluftfaktor = { float zf ->
				def minMax = { v, min, max -> if (v < min) { min } else if (v > max) { max } else { v } }
				def nzf = 0.0f
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
		edt {
			model.map.raum.raume << raumWerte
			view.raumTable.changeSelection(view.raumTable.rowCount - 1, 0, false, false)
		}
		println "raumHinzufugen: raumWerte=${raumWerte}"
	}
	
}

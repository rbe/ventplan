package com.westaflex.wac

/**
 * 
 */
//@Singleton
class WacCalculationService {
	
	static scope = "singleton"
	
	/**
	 * Ermittelt das Volumen aus den vorhandenen Werten:
	 * Gesamtvolumen = Fläche * Höhe
	 */
	Float volumen(Float flaeche, Float hoehe) {
		flaeche * hoehe
	}
	
	/**
	 * Projekt, Gebäudedaten
	 */
	Float mindestaussenluftRate(Integer personenAnzahl, Float aussenluftVolumenstrom) {
		personenAnzahl * aussenluftVolumenstrom
	}
	
	/**
	 * Summe der Fläche aller Räume.
	 */
	Float summeRaumFlache(map) {
		def flache = map.raum.raume.sum { it.raumFlache.toFloat2() } ?: 0.0f
		println "summeRaumFlache: ${flache}"
		flache
	}
	
	/**
	 * Summe der Volumen aller Räume.
	 */
	Float summeRaumVolumen(map) {
		def volumen = map.raum.raume.sum { it.raumVolumen.toFloat2() }
		// Siehe WestaWacBerechnungen.sumVolumen:499, warum project.getHohe?
		if (volumen < 30f * 2.5f) volumen = 30.0f * 2.5f
		println "summeRaumVolumen: ${volumen}"
		volumen
	}
	
	/**
	 * Gesamt-Außenluft-Volumenstrom berechnen.
	 */
	Float gesamtAussenluftVs(map) {
		// Fläche = Geometrie, gelüftetes Volumen / mittlere Raumhöhe
		def flache = 0.0f
		def g = map.gebaude.geometrie
		if (g.geluftetesVolumen && g.raumhohe) {
			flache = g.geluftetesVolumen.toFloat2() / g.raumhohe.toFloat2()
		}
		// Fläche = addiere alle Flächen aus der Tabelle Raumdaten
		else {
			flache = summeRaumFlache(map)
			// Wenn Summe < 30, dann 30
			if (flache < 30.0f) flache = 30.0f
		}
		def r = 0.0f
		if (flache) {
			r = -0.001 * flache * flache + 1.15 * flache + 20
		} else {
			println "gesamtAussenluftVs: Konnte keine Fläche ermitteln"
		}
		println "gesamtAussenluftVs: ${r}"
		r
	}
	
	/**
	 * Wärmeschutz: hoch, niedrig
	 */
	Float warmeschutzFaktor(map) {
		def r = map.gebaude.warmeschutz.with {
			if (hoch) 0.3f
			else if (niedrig) 0.4f
			else {
				println "warmeschutzFaktor: unbekannt, 0.0"
				0.0f
			}
		}
		println "warmeschutzFaktor: ${r}"
		r
	}
	
	/**
	 * DiffDruck ist abhängig von der Gebäudelage.
	 */
	Float diffDruck(map) {
		def r = map.gebaude.lage.with {
			if (windschwach) 2.0f
			else if (windstark) 4.0f
			else {
				println "diffDruck: Gebäudelage unbekannt, 0.0"
				0.0f
			}
		}
		println "diffDruck: ${r}"
		r
	}
	
	/**
	 * Wirksamen Infiltrationsanteil berechnen.
	 */
	Float infiltration(map, Boolean ventilator) {
		def m = [
			sys: 0.6f,
			inf: 1.0f,
			n50: 1.0f,
			druckExpo: 2.0f / 3,
			diffDruck: diffDruck(map)
		]
		//
		if (ventilator) {
			m.inf = 0.9f
			m.n50 = map.gebaude.luftdichtheit.luftwechsel
			if (map.gebaude.typ.MFH) { m.sys = 0.45f }
		} else {
			if (map.gebaude.typ.MFH) { m.sys = 0.5f }
			if (map.gebaude.luftdichtheit != 0) { m.n50 = 1.5f }
		}
		//
		if (map.gebaude.luftdichtheit.messwerte) {
			//fDiffDruck = fDDruck (= diffDruck(map))
			m.n50 = luftwechsel
			m.druckExpo = map.gebaude.luftdichtheit.druckexponent
		} else {
			if (map.gebaude.lage.windschwach) m.diffDruck = 2.0f
			else m.diffDruck = 4.0f
			if (!ventilator && !map.gebaude.typ.MFH) {
				if (map.gebaude.lage.windschwach) m.diffDruck = 5.0f
				else m.diffDruck = 7.0f
			}
		}
		//
		if (!map.gebaude.geometrie.geluftetesVolumen) {
			m.flache = summeRaumFlache(map)
			m.volumen = summeRaumVolumen(map)
		} else {
			m.flache = map.gebaude.geometrie.geluftetesVolumen.toFloat2() / map.gebaude.geometrie.raumhohe.toFloat2()
			m.volumen = map.gebaude.geometrie.geluftetesVolumen.toFloat2()
		}
		// wirk wird nach Tabelle 11 Seite 29 ermittelt, Norm 1946-6 endgültige Fassung
		//m.wirk = sys * inf * (-flache / 1600 + 1.025)
		if (ventilator) m.wirk = 0.45f
		else m.wirk = 0.5f
		//
		def r = m.with {
			if (wirk && volumen && n50 && diffDruck && druckExpo) {
				wirk * volumen * n50 * Math.pow(diffDruck / 50, druckExpo)
			} else {
				0.0f
			}
		}
		println "infiltration: ${m} -> ${r}"
		r
	}
	
	/**
	 * Sind lüftungstechnische Maßnahmen erforderlich?
	 */
	Boolean ltmErforderlich(map) {
		def r = map.aussenluftVs.with {
			gesamt.toFloat2() > infiltration.toFloat2()
		}
		println "ltmErforderlich: ${r}"
		r
	}
	
	/**
	 * Sind lüftungstechnische Maßnahmen erforderlich?
	 */
	Boolean ltmErforderlich2(map) {
		Float infiltration = infiltration(map, false)
		Float volFL = gesamtAussenluftVs(map) * warmeschutzFaktor(map)
		def r = false
		if (volFL > infiltration) {
			r = true
		}
		println "ltmErforderlich2: ${r}"
		r
	}
	
	/**
	 * 
	 */
	void luftmenge(map, Boolean b) {
		// LTM erforderlich?
		if (!ltmErforderlich2(map)) {
			// TODO Fehlermeldung, dialog
			println "luftmenge: Es sind keine Lüftungstechnischen Maßnahmen notwendig!"
		}
		Float gesamtZu = map.raum.raume.findAll { it.raumLuftart == "ZU" }.sum { it.raumAbluftVs }
		Float gesamtAb = map.raum.raume.findAll { it.raumLuftart == "AB" }.sum { it.raumZuluftfaktor }
	}
	
	/**
	 * 
	 */
	void aussenluftVs(map) {
		// Gesamt-Außenluftvolumentstrom
		map.aussenluftVs.gesamt = round5(
				gesamtAussenluftVs(map)
				* warmeschutzFaktor(map)
				* map.gebaude.faktorBesondereAnforderungen.toFloat2()
			).toString2()
		// Infiltration
		map.aussenluftVs.infiltration = round5(infiltration(map, false)).toString2()
		// Lüftungstechnische Maßnahmen erforderlich?
		if (ltmErforderlich(map)) map.aussenluftVs.massnahme = "Lüftungstechnische Maßnahmen erforderlich!"
		//
		Float zuluftmenge = 0.0f
		Float abluftmenge = 0.0f
		
	}
	
	/**
	 * Dezimalzahl auf 5 runden.
	 */
	private Integer round5(Double factor) {
		5 * (Math.round(factor / 5))
	}
	
}

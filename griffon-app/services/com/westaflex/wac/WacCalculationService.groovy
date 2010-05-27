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
		def flache = map.raum.raume.inject(0.0f) { o, n -> o + n.raumFlache.toFloat2() }
		println "summeRaumFlache: ${flache}"
		flache
	}
	
	/**
	 * Summe der Volumen aller Räume.
	 */
	Float summeRaumVolumen(map) {
		def volumen = map.raum.raume.inject(0.0f) { o, n -> o + n.raumVolumen.toFloat2() }
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
			m.n50 = map.gebaude.luftdichtheit.luftwechsel.toFloat2()
			if (map.gebaude.typ.MFH) { m.sys = 0.45f }
		} else {
			if (map.gebaude.typ.MFH) { m.sys = 0.5f }
			if (map.gebaude.luftdichtheit != 0) { m.n50 = 1.5f }
		}
		//
		if (map.gebaude.luftdichtheit.messwerte) {
			//fDiffDruck = fDDruck (= diffDruck(map))
			m.n50 = map.gebaude.luftdichtheit.luftwechsel.toFloat2()
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
		def r = volFL > infiltration ? true : false
		println "ltmErforderlich2: ${r}"
		r
	}
	
	/**
	 * Automatische Berechnung der Luftmenge und Luftwechsel pro Stunde
	 * getrennt nach Abluft und Zuluft im Verhältnis der einzelnen Raumluftvolumenströme
	 * zum Gesamt-Raumluftvolumenstrom.
	 * @param b true=Raumvolumenströme, false=Gesamtraumvolumenstrom
	 */
	void autoLuftmenge(map, Boolean b) {
		// LTM erforderlich?
		if (!ltmErforderlich2(map)) {
			// TODO Fehlermeldung, dialog
			println "luftmenge: Es sind keine lüftungstechnischen Maßnahmen notwendig!"
		}
		// LTM: erste Berechnung für Raumvolumenströme
		// Summiere Daten aus Raumdaten
		Float gesamtZu = map.raum.raume.findAll { it.raumLuftart.contains("ZU") }.inject(0.0f) { o, n ->
			o + n.raumZuluftfaktor.toFloat2()
		}
		Float gesamtAb = map.raum.raume.findAll { it.raumLuftart.contains("AB") }.inject(0.0f) { o, n ->
			o + n.raumAbluftVs.toFloat2()
		}
		// Gesamt-Außenluftvolumenstrom bestimmen
		Float gesamtAussenluft =
			Math.max(
				Math.max(gesamtAb, gesamtAussenluftVs(map)),
				map.gebaude.geplanteBelegung.mindestaussenluftrate.toFloat2()
			) * (map.gebaude.faktorBesondereAnforderungen.toFloat2() ?: 1.0f)
		// Gesamt-Außenluftvolumenstrom für lüftungstechnische Maßnahmen
		Float gesamtAvsLTM = 0.0f
		if (map.aussenluftVs.infiltrationBerechnen && b) {
			gesamtAvsLTM = gesamtAussenluft - infiltration(true)
		} else {
			gesamtAvsLTM = gesamtAussenluft
		}
		//
		Float ltmAbluftSumme = 0.0f
		Float ltmZuluftSumme = 0.0f
		// Alle Räume, die einen Abluftvolumenstrom > 0 haben...
		map.raum.raume.grep { it.raumAbluftVs.toFloat2() > 0.0f }.each {
			Float ltmAbluftRaum = Math.round(gesamtAvsLTM / gesamtAb * it.raumAbluftVs.toFloat2())
			// Raumvolumenströme berechnen?
			if (b) {
				it.raumAbluftVs = it.raumVolumenstrom = ltmAbluftRaum.toString2()
				if (it.raumVolumen > 0) {
					it.raumLuftwechsel = (ltmAbluftRaum / it.raumVolumen.toFloat2()).toString2()
				} else {
					it.raumLuftwechsel = 0.toString2()
				}
				// ZU/AB
				if (it.raumLuftart == "ZU/AB") {
					Float ltmZuluftRaum = Math.round(gesamtAvsLTM * it.raumZuluftfaktor.toFloat2() / gesamtZu)
					it.raumZuluftVs = ltmZuluftRaum.toString2()
					it.raumAbluftVs = ltmAbluftRaum.toString2()
					if (ltmZuluftRaum > ltmAbluftRaum) {
						it.raumVolumenstrom = ltmZuluftRaum.toString2()
						it.raumLuftwechsel = (ltmZuluftRaum / it.raumVolumen.toFloat2()).toString2()
					} else {
						it.raumVolumenstrom = ltmAbluftRaum.toString2()
						it.raumLuftwechsel = (ltmAbluftRaum / it.raumVolumen.toFloat2()).toString2()
					}
					ltmZuluftSumme += ltmAbluftRaum
				}
			} else {
				ltmAbluftSumme += ltmAbluftRaum
			}
			map.raum.ltmAbluftSumme = ltmAbluftSumme.toString2()
		}
		// LTM: zweite Berechnung für Raumvolumenströme
		map.raum.raume.grep { it.raumZuluftfaktor.toFloat2() > 0.0f && it.raumLuftart != "ZU/AB" }.each {
			Float ltmZuluftRaum = Math.round(gesamtAvsLTM * it.raumZuluftfaktor.toFloat2() / gesamtZu)
			if (b) {
				it.raumVolumenstrom = ltmZuluftRaum.toString2()
				it.raumZuluftVs = ltmZuluftRaum.toString2()
				it.raumLuftwechsel = ltmZuluftRaum / it.raumVolumen.toFloat2()
			} else {
				ltmZuluftSumme += ltmZuluftRaum
			}
			map.raum.ltmZuluftSumme = ltmZuluftSumme.toString2()
		}
	}
	
	/**
	 * 
	 */
	void aussenluftVs(map) {
		// Gesamt-Außenluftvolumentstrom
		def gesamtAvs = gesamtAussenluftVs(map)
		def wsFaktor = warmeschutzFaktor(map)
		map.aussenluftVs.gesamt = round5(
				gesamtAvs * wsFaktor * map.gebaude.faktorBesondereAnforderungen.toFloat2()
			).toString2()
		// Infiltration
		map.aussenluftVs.infiltration = round5(infiltration(map, false)).toString2()
		// Lüftungstechnische Maßnahmen erforderlich?
		if (ltmErforderlich(map)) {
			map.aussenluftVs.massnahme = "Lüftungstechnische Maßnahmen erforderlich!"
		}
		//
		autoLuftmenge(map, false)
		Float grundluftung = gesamtAvs //gesamtAussenluftVs(map)
		Float geluftetesVolumen = map.gebaude.geometrie.geluftetesVolumen.toFloat2()
		Float mindestluftung
		Float intensivluftung
		Float feuchteluftung
		// Ausgabe der Gesamt-Außenluftvolumenströme
		map.aussenluftVs.gesamtAvsNeLvsNl = round5(grundluftung).toString2()
		map.aussenluftVs.gesamtAvsNeLwNl = (grundluftung / geluftetesVolumen).toString2()
		mindestluftung = 0.7f * grundluftung
		map.aussenluftVs.gesamtAvsNeLvsRl = round5(mindestluftung).toString2()
		map.aussenluftVs.gesamtAvsNeLwRl = (mindestluftung / geluftetesVolumen).toString2()
		intensivluftung = 1.3f * grundluftung
		map.aussenluftVs.gesamtAvsNeLvsIl = round5(intensivluftung).toString2()
		map.aussenluftVs.gesamtAvsNeLwIl = (intensivluftung / geluftetesVolumen).toString2()
		feuchteluftung = wsFaktor * grundluftung
		map.aussenluftVs.gesamtAvsNeLvsFs = round5(feuchteluftung).toString2()
		map.aussenluftVs.gesamtAvsNeLwFs = (feuchteluftung / geluftetesVolumen).toString2()
		// Ausgabe der Gesamt-Raumabluft-Volumenströme
		grundluftung = map.raum.raume.grep { it.raumLuftart.contains("AB") }.inject(0.0f) { o, n ->
			o + n.raumAbluftVs.toFloat2()
		}
		map.aussenluftVs.gesamtAvsRaumLvsNl = round5(grundluftung).toString2()
		map.aussenluftVs.gesamtAvsRaumLwNl = (grundluftung / geluftetesVolumen).toString2()
		mindestluftung = 0.7f * grundluftung
		map.aussenluftVs.gesamtAvsRaumLvsRl = round5(mindestluftung).toString2()
		map.aussenluftVs.gesamtAvsRaumLwRl = (mindestluftung / geluftetesVolumen).toString2()
		intensivluftung = 1.3f * grundluftung
		map.aussenluftVs.gesamtAvsRaumLvsIl = round5(intensivluftung).toString2()
		map.aussenluftVs.gesamtAvsRaumLwIl = (intensivluftung / geluftetesVolumen).toString2()
		feuchteluftung = wsFaktor * grundluftung
		map.aussenluftVs.gesamtAvsRaumLvsFs = round5(feuchteluftung).toString2()
		map.aussenluftVs.gesamtAvsRaumLwFs = (feuchteluftung / geluftetesVolumen).toString2()
		// Ausgabe der personenbezogenen Gesamtaußenluftvolumenströme
		grundluftung = map.gebaude.geplanteBelegung.mindestaussenluftrate.toFloat2()
		map.aussenluftVs.gesamtAvsPersonLvsNl = round5(grundluftung).toString2()
		map.aussenluftVs.gesamtAvsPersonLwNl = (grundluftung / geluftetesVolumen).toString2()
		mindestluftung = 0.7f * grundluftung
		map.aussenluftVs.gesamtAvsPersonLvsRl = round5(mindestluftung).toString2()
		map.aussenluftVs.gesamtAvsPersonLwRl = (mindestluftung / geluftetesVolumen).toString2()
		intensivluftung = 1.3f * grundluftung
		map.aussenluftVs.gesamtAvsPersonLvsIl = round5(intensivluftung).toString2()
		map.aussenluftVs.gesamtAvsPersonLwIl = (intensivluftung / geluftetesVolumen).toString2()
		feuchteluftung = wsFaktor * grundluftung
		map.aussenluftVs.gesamtAvsPersonLvsFs = round5(feuchteluftung).toString2()
		map.aussenluftVs.gesamtAvsPersonLwFs = (feuchteluftung / geluftetesVolumen).toString2()
		// Ausgabe der Volumenströme für LTM
		grundluftung = Math.max(map.raum.ltmAbluftSumme.toFloat2(), map.raum.ltmZuluftSumme.toFloat2())
		grundluftung = Math.max(gesamtAvs/*gesamtAussenluftVs(map)*/, grundluftung)
		grundluftung = Math.max(map.gebaude.geplanteBelegung.mindestaussenluftrate.toFloat2(), grundluftung)
		if (map.aussenluftVs.infiltrationBerechnen) {
			grundluftung -= infiltration(map, true)
		}
		map.aussenluftVs.gesamtLvsLtmLvsNl = round5(grundluftung).toString2()
		map.aussenluftVs.gesamtLvsLtmLwNl = (grundluftung / geluftetesVolumen).toString2()
		mindestluftung = 0.7f * grundluftung
		map.aussenluftVs.gesamtLvsLtmLvsRl = round5(mindestluftung).toString2()
		map.aussenluftVs.gesamtLvsLtmLwRl = (mindestluftung / geluftetesVolumen).toString2()
		intensivluftung = 1.3f * grundluftung
		map.aussenluftVs.gesamtLvsLtmLvsIl = round5(intensivluftung).toString2()
		map.aussenluftVs.gesamtLvsLtmLwIl = (intensivluftung / geluftetesVolumen).toString2()
	}
	
	/**
	 * Dezimalzahl auf 5 runden.
	 */
	private Integer round5(Double factor) {
		5 * (Math.round(factor / 5))
	}
	
}

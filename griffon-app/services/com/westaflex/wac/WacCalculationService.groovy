/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/services/com/westaflex/wac/WacCalculationService.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.westaflex.wac

/**
 * 
 */
class WacCalculationService {
	
	/**
	 * The WAC model service.
	 */
	def wacModelService
	
	/**
	 * Dezimalzahl auf 5 runden.
	 */
	Integer round5(Double factor) {
		5.0d * (Math.round(factor / 5.0d))
	}
	
	/**
	 * Hole alle Zuluft-Räume.
	 */
	private def zuluftRaume(map) {
		map.raum.raume.findAll { it.raumLuftart.contains("ZU") }
	}
	
	/**
	 * Hole alle Abluft-Räume.
	 */
	private def abluftRaume(map) {
		map.raum.raume.findAll { it.raumLuftart.contains("AB") }
	}
	
	/**
	 * Ermittelt das Volumen aus den vorhandenen Werten:
	 * Gesamtvolumen = Fläche * Höhe
	 */
	Double volumen(Double flaeche, Double hoehe) {
		flaeche * hoehe
	}
	
	/**
	 * Projekt, Gebäudedaten, Geplante Belegung.
	 */
	void mindestaussenluftrate(map) {
		map.gebaude.geplanteBelegung.with {
			mindestaussenluftrate = personenanzahl * aussenluftVsProPerson
		}
	}
	
	/**
	 * Gebäudedaten - Geometrie anhand eingegebener Räume berechnen.
	 */
	void geometrieAusRaumdaten(map) {
		// May produce NaN values (when all entered rooms are removed)
		if (map.raum.raume?.size() > 0) {
			map.with {
				// Gesamtfläche berechnen
				gebaude.geometrie.wohnflache =
					(raum.raume.inject(0.0d) { o, n ->
							o + n.raumFlache
						})
				// Mittlere Raumhöhe berechnen
				gebaude.geometrie.raumhohe =
					(raum.raume.inject(0.0d) { o, n ->
							o + n.raumHohe
						} / raum.raume.size())
			}
			// Geometrie berechnen...
			geometrie(map)
		} else {
			println "wacCalculation/geometrieAusRaumdaten: Keine Räume vorhanden!"
		}
	}
	
	/**
	 * Gebäudedate - Geometrie berechnen.
	 */
	void geometrie(map) {
		def g = map.gebaude.geometrie
		try {
			// Luftvolumen der Nutzungseinheit = Wohnfläche * mittlere Raumhöhe
			g.luftvolumen = g.geluftetesVolumen = g.wohnflache && g.raumhohe ? volumen(g.wohnflache, g.raumhohe) : 0.0d
			// Gelüftetes Volumen = gelüftete Fläche * mittlere Raumhöhe
			g.geluftetesVolumen = g.gelufteteFlache && g.raumhohe ? volumen(g.gelufteteFlache, g.raumhohe) : 0.0d
			// Gelüftetes Volumen = Luftvolumen, wenn kein gelüftetes Volumen berechnet
			g.geluftetesVolumen = g.luftvolumen && !g.geluftetesVolumen ? g.luftvolumen : 0.0d
		} catch (e) {
			e.printStackTrace()
			g.luftvolumen = g.geluftetesVolumen = "E"
		}
		// Set calculated values in model
		map.gebaude.geometrie.geluftetesVolumen = g.geluftetesVolumen ?: 0.0d
		map.gebaude.geometrie.luftvolumen = g.luftvolumen ?: 0.0d
		println "wacCalculation/geometrie: ${map.gebaude.geometrie?.dump()}"
		// Raumvolumenströme - Gesamtvolumen der Nutzungseinheit
		map.raum.raumVs.gesamtVolumenNE = g.luftvolumen ?: 0.0d
		println "wacCalculation/geometrie: map.raum.raumVs.gesamtVolumenNE=${map.raum.raumVs.gesamtVolumenNE?.dump()}"
	}
	
	/**
	 * Summe der Fläche aller Räume.
	 */
	Double summeRaumFlache(map) {
		def flache = map.raum.raume.inject(0.0d) { o, n -> o + n.raumFlache }
		println "wacCalculation/summeRaumFlache: ${flache?.dump()}"
		flache
	}
	
	/**
	 * Summe der Volumen aller Räume.
	 */
	Double summeRaumVolumen(map) {
		def volumen = map.raum.raume.inject(0.0d) { o, n -> o + n.raumVolumen }
		// Minimum
		def mittlereRaumhohe = map.gebaude.geometrie.raumhohe
		if (mittlereRaumhohe) {
			if (volumen < 30.0d * mittlereRaumhohe) volumen = 30.0d * mittlereRaumhohe
		}
		println "wacCalculation/summeRaumVolumen: ${volumen?.dump()}"
		volumen
	}
	
	/**
	 * Addiere alle Volumen (m³) Spalten aus der Tabelle für Raumvolumenströme (aka Luftmengenermittlung).
	 */
	Double summeLuftmengeVolumen(map, luftart = null) {
		// Hole alle Räume mit der entsprechenden Luftart; oder alle
		def raume = luftart ? map.raum.raume.grep { it.raumLuftart.contains(luftart) } : map.raum.raume
		def mittlereRaumhohe = map.gebaude.geometrie.raumhohe
		def volumen = raume.inject(0.0d) { o, n ->
				o + n.raumVolumen
			}
		// Minimum
		if (vol < 30.0d * mittlereRaumhohe) volumen = 30.0d * mittlereRaumhohe
		println "wacCalculation/summeLuftmengeVolumen: ${volumen?.dump()}"
		volumen
	}
	
	/**
	 * Eine Raumnummer anhand der Raumdaten erzeugen:
	 * Sie besteht immer aus drei Ziffern:
	 * 1. Die erste Ziffer steht für das Geschoß (Keller = 0; Erdgeschoß = 1; usw.).
	 * 2. Die beiden folgenden Ziffern ergeben sich als fortlaufende Nummer bei 1 angefangen.
	 *    Beispiel: erster Raum im Erdgeschoß = 101; dritter Raum im Kellergeschoß = 003
	 */
	String berechneRaumnummer(map) {
		// Hole alle Räume pro Geschoss, sortiere nach ihrer Position in der Tabelle und vergebe eine Raumnummer
		["KG", "EG", "OG", "DG", "SB"].eachWithIndex { geschoss, geschossIndex ->
			map.raum.raume.grep { raum ->
					raum.raumGeschoss == geschoss
				}?.sort { raum ->
					raum.position
				}?.eachWithIndex { raum, raumIndex ->
					raum.raumNummer = String.format("%s%02d", geschossIndex, raumIndex + 1)
				}
		}
	}
	
	/**
	 * Prüfe den Zuluftfaktor, Rückgabe: [übergebener wert, neuer wert]
	 */
	def prufeZuluftfaktor(String raumTyp, Double zf) {
		def minMax = { v, min, max ->
			if (v < min) { min }
			else if (v > max) { max } else { v }
		}
		Double nzf = 0.0d
		switch (raumTyp) {
			case "Wohnzimmer":                                  nzf = minMax(zf, 2.5d, 3.5d); break
			case ["Kinderzimmer", "Schlafzimmer"]:              nzf = minMax(zf, 1.0d, 3.0d); break
			case ["Esszimmer", "Arbeitszimmer", "Gästezimmer"]: nzf = minMax(zf, 1.0d, 2.0d); break
			default: nzf = zf
		}
		[zf, nzf]
	}
	
	/**
	 * Gesamt-Außenluft-Volumenstrom berechnen.
	 */
	Double gesamtAussenluftVs(map) {
		// Fläche = Geometrie, gelüftetes Volumen / mittlere Raumhöhe
		def flache = 0.0d
		def g = map.gebaude.geometrie
		if (g.geluftetesVolumen && g.raumhohe) {
			flache = g.geluftetesVolumen / g.raumhohe
		}
		// Fläche = addiere alle Flächen aus der Tabelle Raumdaten
		else {
			flache = summeRaumFlache(map)
			// Wenn Summe < 30, dann 30
			if (flache < 30.0d) flache = 30.0d
		}
		//
		if (flache == Double.NaN) {
			println "wacCalculation/gesamtAussenluftVs: flache=${flache?.dump()}"
			flache = 30.0d
		}
		//
		def r = 0.0d
		if (flache) {
			r = -0.001 * flache * flache + 1.15 * flache + 20
		} else {
			println "gesamtAussenluftVs: Konnte keine Fläche ermitteln"
		}
		r = r ?: 0.0d
		println "wacCalculation/gesamtAussenluftVs: ${r?.dump()}"
		r
	}
	
	/**
	 * Wärmeschutz: hoch, niedrig
	 */
	Double warmeschutzFaktor(map) {
		def r = map.gebaude.warmeschutz.with {
			if (hoch) 0.3f
			else if (niedrig) 0.4f
			else {
				println "wacCalculation/warmeschutzFaktor: unbekannt, 0.0"
				0.0d
			}
		}
		println "wacCalculation/warmeschutzFaktor: ${r?.dump()}"
		r
	}
	
	/**
	 * DiffDruck ist abhängig von der Gebäudelage.
	 */
	Double diffDruck(map) {
		def r = map.gebaude.lage.with {
			if (windschwach) 2.0d
			else if (windstark) 4.0d
			else {
				println "wacCalculation/diffDruck: Gebäudelage unbekannt, 0.0"
				0.0d
			}
		}
		println "wacCalculation/diffDruck: ${r?.dump()}"
		r
	}
	
	/**
	 * Wirksamen Infiltrationsanteil berechnen.
	 */
	Double infiltration(map, Boolean ventilator) {
		def m = [
			sys: 0.6f,
			inf: 1.0d,
			n50: 1.0d,
			druckExpo: 2.0d / 3,
			diffDruck: diffDruck(map)
		]
		//
		if (ventilator) {
			m.inf = 0.9f
			m.n50 = map.gebaude.luftdichtheit.luftwechsel
			if (map.gebaude.typ.mfh) { m.sys = 0.45f }
		} else {
			if (map.gebaude.typ.mfh) { m.sys = 0.5f }
			if (map.gebaude.luftdichtheit != 0) { m.n50 = 1.5f }
		}
		//
		if (map.gebaude.luftdichtheit.messwerte) {
			m.n50 = map.gebaude.luftdichtheit.luftwechsel
			m.druckExpo = map.gebaude.luftdichtheit.druckexponent
		} else {
			if (map.gebaude.lage.windschwach) m.diffDruck = 2.0d
			else m.diffDruck = 4.0d
			if (!ventilator && !map.gebaude.typ.mfh) {
				if (map.gebaude.lage.windschwach) m.diffDruck = 5.0d
				else m.diffDruck = 7.0d
			}
		}
		//
		if (!map.gebaude.geometrie.geluftetesVolumen) {
			m.flache = summeRaumFlache(map)
			m.volumen = summeRaumVolumen(map)
		} else {
			m.flache = map.gebaude.geometrie.geluftetesVolumen / map.gebaude.geometrie.raumhohe
			m.volumen = map.gebaude.geometrie.geluftetesVolumen
		}
		// wirk wird nach Tabelle 11 Seite 29 ermittelt, Norm 1946-6 endgültige Fassung
		m.wirk = ventilator ? 0.45f : 0.5f
		//
		def r = m.with {
			if (wirk && volumen && n50 && diffDruck && druckExpo) {
				wirk * volumen * n50 * Math.pow(diffDruck / 50, druckExpo)
			} else {
				0.0d
			}
		}
		println "wacCalculation/infiltration: ${m?.dump()} -> ${r?.dump()}"
		r
	}
	
	/**
	 * Sind lüftungstechnische Maßnahmen erforderlich?
	 */
	Boolean ltmErforderlich(map) {
		Double infiltration = infiltration(map, false)
		Double volFL =
			gesamtAussenluftVs(map) * warmeschutzFaktor(map) * map.gebaude.faktorBesondereAnforderungen
		def r = volFL > infiltration ? true : false
		println "wacCalculation/ltmErforderlich: ${r?.dump()}"
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
		if (!ltmErforderlich(map)) {
            def infoMsg = "autoLuftmenge: Es sind keine lüftungstechnischen Maßnahmen notwendig!"
            app.controllers["Dialog"].showInformDialog(infoMsg as String)
			println infoMsg
		}
		// LTM: erste Berechnung für Raumvolumenströme
		// Summiere Daten aus Raumdaten
		Double gesamtZuluftfaktor = zuluftRaume(map).inject(0.0d) { o, n ->
				o + n.raumZuluftfaktor
			}
		Double gesamtAbluftVs = abluftRaume(map).inject(0.0d) { o, n ->
				o + n.raumAbluftVs
			}
		// Gesamt-Außenluftvolumenstrom bestimmen
		Double gesamtAussenluft =
				Math.max(
						Math.max(gesamtAbluftVs, gesamtAussenluftVs(map)),
						map.gebaude.geplanteBelegung.mindestaussenluftrate ?: 0.0d
					) * (map.gebaude.faktorBesondereAnforderungen ?: 1.0d)
		// Gesamt-Außenluftvolumenstrom für lüftungstechnische Maßnahmen
		Double gesamtAvsLTM = 0.0d
		if (map.aussenluftVs.infiltrationBerechnen && b) {
			gesamtAvsLTM = gesamtAussenluft - infiltration(true)
		} else {
			gesamtAvsLTM = gesamtAussenluft
		}
		//
		Double ltmAbluftSumme = 0.0d
		map.raum.ltmAbluftSumme = 0.0d
		Double ltmZuluftSumme = 0.0d
		map.raum.ltmZuluftSumme = 0.0d
		// Alle Räume, die einen Abluftvolumenstrom > 0 haben...
		map.raum.raume.grep { it.raumAbluftVs > 0.0d }.each {
			Double ltmAbluftRaum = Math.round(gesamtAvsLTM / gesamtAbluftVs * it.raumAbluftVs)
			// Raumvolumenströme berechnen?
			if (b) {
				it.raumAbluftVs = it.raumVolumenstrom = ltmAbluftRaum
				if (it.raumVolumen > 0) {
					it.raumLuftwechsel = (ltmAbluftRaum / it.raumVolumen)
				} else {
					it.raumLuftwechsel = 0
				}
				// ZU/AB
				if (it.raumLuftart.contains("ZU/AB")) {
					Double ltmZuluftRaum = Math.round(gesamtAvsLTM * it.raumZuluftfaktor / gesamtZuluftfaktor)
					it.raumZuluftVs = ltmZuluftRaum
					it.raumAbluftVs = ltmAbluftRaum
					if (ltmZuluftRaum > ltmAbluftRaum) {
						it.raumVolumenstrom = ltmZuluftRaum
						it.raumLuftwechsel = (ltmZuluftRaum / it.raumVolumen)
					} else {
						it.raumVolumenstrom = ltmAbluftRaum
						it.raumLuftwechsel = (ltmAbluftRaum / it.raumVolumen)
					}
					ltmZuluftSumme += ltmAbluftRaum
				}
			} else {
				ltmAbluftSumme += ltmAbluftRaum
			}
			map.raum.ltmAbluftSumme = ltmAbluftSumme
		}
		// LTM: zweite Berechnung für Raumvolumenströme
		map.raum.raume.grep {
			it.raumZuluftfaktor > 0.0d && it.raumLuftart != "ZU/AB"
		}.each {
			Double ltmZuluftRaum = Math.round(gesamtAvsLTM * it.raumZuluftfaktor / gesamtZuluftfaktor)
			if (b) {
				it.raumVolumenstrom = ltmZuluftRaum
				it.raumZuluftVs = ltmZuluftRaum
				it.raumLuftwechsel = ltmZuluftRaum / it.raumVolumen
			} else {
				ltmZuluftSumme += ltmZuluftRaum
			}
			map.raum.ltmZuluftSumme = ltmZuluftSumme
		}
	}
	
	/**
	 * Berechne Aussenluftvolumenströme.
	 */
	void aussenluftVs(map) {
		// Gesamt-Außenluftvolumentstrom
		Double gesamtAvs = gesamtAussenluftVs(map)
		Double wsFaktor = warmeschutzFaktor(map)
		map.aussenluftVs.gesamt = (gesamtAvs * wsFaktor * map.gebaude.faktorBesondereAnforderungen)
		// Infiltration
		map.aussenluftVs.infiltration = infiltration(map, false)
		// Lüftungstechnische Maßnahmen erforderlich?
		if (ltmErforderlich(map)) {
			map.aussenluftVs.massnahme = "Lüftungstechnische Maßnahmen erforderlich!"
		}
		//
		autoLuftmenge(map, false)
		Double grundluftung = gesamtAvs //gesamtAussenluftVs(map)
		Double geluftetesVolumen = map.gebaude.geometrie.geluftetesVolumen ?: 0.0d
		Double mindestluftung
		Double intensivluftung
		Double feuchteluftung
		// Ausgabe der Gesamt-Außenluftvolumenströme
		map.aussenluftVs.gesamtAvsNeLvsNl = grundluftung
		map.aussenluftVs.gesamtAvsNeLwNl = grundluftung / geluftetesVolumen
		mindestluftung = 0.7f * grundluftung
		map.aussenluftVs.gesamtAvsNeLvsRl = mindestluftung
		map.aussenluftVs.gesamtAvsNeLwRl = mindestluftung / geluftetesVolumen
		intensivluftung = 1.3f * grundluftung
		map.aussenluftVs.gesamtAvsNeLvsIl = intensivluftung
		map.aussenluftVs.gesamtAvsNeLwIl = intensivluftung / geluftetesVolumen
		feuchteluftung = wsFaktor * grundluftung
		map.aussenluftVs.gesamtAvsNeLvsFs = feuchteluftung
		map.aussenluftVs.gesamtAvsNeLwFs = feuchteluftung / geluftetesVolumen
		// Ausgabe der Gesamt-Raumabluft-Volumenströme
		grundluftung = abluftRaume(map).inject(0.0d) { o, n ->
			o + n.raumAbluftVs
		}
		map.aussenluftVs.gesamtAvsRaumLvsNl = grundluftung
		map.aussenluftVs.gesamtAvsRaumLwNl = grundluftung / geluftetesVolumen
		mindestluftung = 0.7f * grundluftung
		map.aussenluftVs.gesamtAvsRaumLvsRl = mindestluftung
		map.aussenluftVs.gesamtAvsRaumLwRl = mindestluftung / geluftetesVolumen
		intensivluftung = 1.3f * grundluftung
		map.aussenluftVs.gesamtAvsRaumLvsIl = intensivluftung
		map.aussenluftVs.gesamtAvsRaumLwIl = intensivluftung / geluftetesVolumen
		feuchteluftung = wsFaktor * grundluftung
		map.aussenluftVs.gesamtAvsRaumLvsFs = feuchteluftung
		map.aussenluftVs.gesamtAvsRaumLwFs = feuchteluftung / geluftetesVolumen
		// Ausgabe der personenbezogenen Gesamtaußenluftvolumenströme
		grundluftung = map.gebaude.geplanteBelegung.mindestaussenluftrate
		map.aussenluftVs.gesamtAvsPersonLvsNl = grundluftung
		map.aussenluftVs.gesamtAvsPersonLwNl = grundluftung / geluftetesVolumen
		mindestluftung = 0.7f * grundluftung
		map.aussenluftVs.gesamtAvsPersonLvsRl = mindestluftung
		map.aussenluftVs.gesamtAvsPersonLwRl = mindestluftung / geluftetesVolumen
		intensivluftung = 1.3f * grundluftung
		map.aussenluftVs.gesamtAvsPersonLvsIl = intensivluftung
		map.aussenluftVs.gesamtAvsPersonLwIl = intensivluftung / geluftetesVolumen
		feuchteluftung = wsFaktor * grundluftung
		map.aussenluftVs.gesamtAvsPersonLvsFs = feuchteluftung
		map.aussenluftVs.gesamtAvsPersonLwFs = feuchteluftung / geluftetesVolumen
		// Ausgabe der Volumenströme für LTM
		grundluftung = Math.max(map.raum.ltmAbluftSumme, map.raum.ltmZuluftSumme)
		grundluftung = Math.max(gesamtAvs/*gesamtAussenluftVs(map)*/, grundluftung)
		grundluftung = Math.max(map.gebaude.geplanteBelegung.mindestaussenluftrate, grundluftung)
		def infiltration = map.aussenluftVs.infiltrationBerechnen ? infiltration(map, true) : 0.0d
		map.aussenluftVs.gesamtLvsLtmLvsNl = grundluftung - infiltration
		map.aussenluftVs.gesamtLvsLtmLwNl = (grundluftung - infiltration) / geluftetesVolumen
		mindestluftung = 0.7f * grundluftung - infiltration
		map.aussenluftVs.gesamtLvsLtmLvsRl = mindestluftung
		map.aussenluftVs.gesamtLvsLtmLwRl = mindestluftung / geluftetesVolumen
		intensivluftung = 1.3f * grundluftung - infiltration
		map.aussenluftVs.gesamtLvsLtmLvsIl = intensivluftung
		map.aussenluftVs.gesamtLvsLtmLwIl = intensivluftung / geluftetesVolumen
		map.aussenluftVs.gesamtLvsLtmLvsFs = wsFaktor * grundluftung - infiltration
		// Raumvolumenströme - Gesamtaussenluftvolumentrom mit Infiltration
		map.raum.raumVs.gesamtaussenluftVsMitInfiltration = grundluftung
		// Raumvolumenströme - Luftwechsel der Nutzungseinheit
		map.raum.raumVs.luftwechselNE = grundluftung / map.raum.raumVs.gesamtVolumenNE
		//
		setzeAussenluftVsMindestwerte(map)
	}
	
	/**
	 * Aussenluftvolumenstrom, Raumvolumenströme: je nach Zentralgerät bestehen unterschiedliche
	 * Mindestwerte.
	 */
	def setzeAussenluftVsMindestwerte(map) {
		def c = { v, min -> v < min ? min : v }
		// 140WACCF
		map.aussenluftVs.gesamtLvsLtmLvsRl = c(map.aussenluftVs.gesamtLvsLtmLvsRl, 50.0d)
		map.aussenluftVs.gesamtLvsLtmLvsFs = c(map.aussenluftVs.gesamtLvsLtmLvsFs, 50.0d)
		// 400WAC
		if (map.anlage.zentralgerat == "400WAC") {
			map.aussenluftVs.gesamtLvsLtmLvsRl = c(map.aussenluftVs.gesamtLvsLtmLvsRl, 75.0d)
			map.aussenluftVs.gesamtLvsLtmLvsFs = c(map.aussenluftVs.gesamtLvsLtmLvsFs, 75.0d)
		}
	}
	
	/**
	 * Raumvolumenströme - wähle das zu verwendende Zentralgerät anhand der
	 * Nennleistung Luftvolumenstrom der lüftungstechnischen Maßnahme (Aussenluftvolumenströme).
	 */
	def berechneZentralgerat(map) {
		def nl = map.aussenluftVs.gesamtLvsLtmLvsNl as Integer
		String zentralgerat = wacModelService.getZentralgeratFurVolumenstrom(nl)
		[zentralgerat, nl]
	}
	
	/**
	 * Raumvolumenströme - Zu/Abluftventile.
	 * @param map One of map.raum.raumVs
	 */
	def berechneZuAbluftventile(map) {
		map.putAll([
			raumBezeichnungAbluftventile: "100ULC",
			raumAnzahlAbluftventile: 1,
			raumAbluftmengeJeVentil: 50.0d,
			raumBezeichnungZuluftventile: "100ULE",
			raumAnzahlZuluftventile: 1,
			raumZuluftmengeJeVentil: 50.0d,
			raumVerteilebene: "EG"
		])
		println "berechneZuAbluftventile: ${map}"
		map
	}
	
	/**
	 * Druckverlust - Kanalnetz: berechne eine (gerade eingegebene) Teilstrecke.
	 * @param map One of map.dvb.kanalnetz
	 */
	def berechneTeilstrecke(map) {
		println "berechneTeilstrecke: map=${map.dump()}"
		def kanal = wacModelService.getKanal(map.kanalbezeichnung)
		map.geschwindigkeit = map.luftVs * 1000000 / (kanal.flaeche * 3600)
		def lambda
		switch (kanal.klasse?.toInteger()) {
			case 4:
				lambda = calcDruckverlustKlasse4(kanal.durchmesser, kanal.seiteA, kanal.seiteB)
				break
			case [5, 6, 7, 8]:
				lambda = "calcDruckverlustKlasse${kanal.klasse}"(map.geschwindigkeit, kanal.durchmesser)
				break
			default:
				println "switch clause runs into default. Klasse=${kanal.klasse} not defined ==> lambda not set"
		}
		//
		def geschwPow2 = Math.pow(map.geschwindigkeit, 2)
		// Gesamtwiderstandszahl wurde via ProjektController.wbwOkButton gesetzt
		// Reibungswiderstand
		map.reibungswiderstand =
			lambda * map.lange * 1.2 * geschwPow2 / (2 * (kanal.durchmesser + 0.0d) / 1000)
		// Einzelwiderstand berechnen
		map.einzelwiderstand = 0.6d * map.gesamtwiderstandszahl * geschwPow2
		// Widerstand der Teilstrecke = Reibungswiderstand + Einzelwiderstand
		map.widerstandTeilstrecke = map.reibungswiderstand + map.einzelwiderstand
		println "berechneTeilstrecke: map=${map.dump()}"
	}
	
	def calcDruckverlustKlasse4 = { BigDecimal durchmesser, BigDecimal seiteA, BigDecimal seiteB ->
		final Double k1 = 0.0255 * Math.pow((seiteA / seiteB), 2) - 0.1393 * (seiteA / seiteB) + 1.1485
		final Double lambda1 = Math.log10(50 * Math.sqrt(0.674 * durchmesser))
		final Double lambda2 = lambda1 * lambda1
		k1 * 0.25 / lambda2
	}
	
	def calcDruckverlustKlasse5 = { Double geschwindigkeit, BigDecimal durchmesser ->
		final Double re = geschwindigkeit * (durchmesser + 0f) / (0.015) //1000 * 15 hoch -6
		def lambda = 0.22 / Math.pow(re, 0.2)
        lambda
	}
	
	def calcDruckverlustKlasse6 = { Double geschwindigkeit, BigDecimal durchmesser ->
		Double lambda
		final Double re = geschwindigkeit * (durchmesser + 0f) / 0.015
		if (re < 2300) {
			lambda = 64 / re
		} else {
			final Float f1 = 1.2f
			final Float f2 = 2.4f
			if (re >= 2300 && re < 20000) {
				lambda = 1.14 - 2 * Math.log10(f1 / durchmesser)
				// Iteration
				0.upto 3, { i ->
					lambda = -2 * Math.log10(f1 / (durchmesser * 3.71) + 2.51 / re * lambda)
				}
				lambda = Math.pow(1 / lambda, 2)
			} else {
				lambda = 1.14 - 2 * Math.log10(f2 / durchmesser)
				lambda = Math.pow(1 / lambda, 2)
			}
		}
		lambda
	}
	
	def calcDruckverlustKlasse7 = { Double geschwindigkeit, BigDecimal durchmesser ->
		Double lambda = 0d
		final Double re = geschwindigkeit * durchmesser / 0.015d
		if (re < 2300) {
			lambda = 64 / re
		} else {
			final Float f1 = 0.09f
			if (re >= 2300 && re < 20000) {
				// Iteration
				lambda = 1.14 - 2 * Math.log10(f1 / durchmesser)
				0.upto 3, { i ->
					lambda = -2 * Math.log10(f1 / (durchmesser * 3.71) + 2.51 / re * lambda)
				}
				lambda = Math.pow(1 / lambda, 2)
			} else {
				lambda = 1.14 - 2 * Math.log10(0.8 / durchmesser)
				lambda = Math.pow(1 / lambda, 2)
			}
		}
		lambda
	}
	
	def calcDruckverlustKlasse8 = calcDruckverlustKlasse7
	
	/**
	 * Druckverlust - Ventileinstellung: berechne die (gerade eingegebene) Ventileinstellung.
	 * @param map
	 */
	def berechneVentileinstellung(map) {
		// Hole den Luftvolumenstrom der letzten Teilstrecke
		def luftVsLetzteTeilstrecke = { ve ->
			def teilstrecken = ve.teilstrecken.split(";").toList()
			// Hole Luftvolumenstrom der letzten Teilstrecke
			def letzteTeilstrecke = teilstrecken.last().toInteger()
			def teilstrecke = map.dvb.kanalnetz.find { it.teilstrecke == letzteTeilstrecke }
			if (teilstrecke.luftart == ve.dvbvLuftart) {
				teilstrecke.luftVs
			} else {
				0.0d
			}
			
		}

		// Alle Einträge in der Tabelle Ventileinstellung durchlaufen
		map.dvb.ventileinstellung.each { ve ->
			// Prüfe, ob die letzte Teilstrecke existiert und ob die Luftart übereinstimmt
			def luftVsLts = luftVsLetzteTeilstrecke(ve)
			if (luftVsLts > 0.0d) {
				// Berechne dP offen
				ventileinstellung.dpOffen =
					wacModelService.getMinimalerDruckverlustFurVentil(ve.ventilbezeichnung, ve.dvbvLuftart, luftVsLts)
				// Berechne Gesamtwiderstand aller Teilstrecken
				ventileinstellung.gesamtWiderstand =
					ventileinstellung.dpOffen +
					teilstrecken.collect { t ->
							map.dvb.kanalnetz.find { t.teilstrecke }?.widerstandTeilstrecke
						}.inject(0.0d) { o, n ->
							o + n
						}
			} else {
				println "berechneVentileinstellung: Letzte Teilstrecke ${letzteTeilstrecke} existiert nicht oder" +
					" Luftart stimmt nicht überein: ${map.luftart} == ${teilstrecke?.luftart}?"
			}
		}
		// Ermittle maximale Widerstandswerte
		def maxZu = Collections.max(map.dvb.ventileinstellung.findAll { it.dvbvLuftart == "ZU" }.collect { it.gesamtWiderstand })
		def maxAb = Collections.max(map.dvb.ventileinstellung.findAll { it.dvbvLuftart == "AB" }.collect { it.gesamtWiderstand })
		// Differenzen
		// Alle Einträge in der Tabelle Ventileinstellung durchlaufen
		map.dvb.ventileinstellung.each { ve ->
			if (ve.dvbvLuftart == "ZU") {
				ve.differenz = maxZu - ve.gesamtWiderstand
			} else if (ve.dvbvLuftart == "AB") {
				ve.differenz = maxAb - ve.gesamtWiderstand
			}
			ve.abgleich = ve.differenz + ve.dpOffen
			ve.einstellung =
				wacModelService.getEinstellung(ve.ventilbezeichnung, ve.dvbvLuftart, luftVsLetzteTeilstrecke(ve), ve.abgleich)
			// Wurde keine Einstellung gefunden, Benutzer informieren
			if (ve.einstellung != 0.0d) {
				def infoMsg = "Keine Einstellung für Ventil ${ve.ventilbezeichnung} gefunden! Bitte prüfen Sie die Zeile#${ve.position}."
                app.controllers["Dialog"].showInformDialog(infoMsg as String)
				println infoMsg
			}
		}
	}
	
	/**
	 * Raum bearbeiten - Türen berechnen.
	 * @param map One of model.map.raum.raume
	 */
	def berechneTuerspalt(map) {
		// Gilt nicht für Überström-Räume
		if (map.luftart.contains("ÜB")) {
			return
		} else {
			map.turen.each {
			}
		}
	}
	
}

/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/services/com/westaflex/wac/WacCalculationService.groovy
 * Last modified at 28.03.2011 14:40:58 by rbe
 */
package com.westaflex.wac

/**
 * 
 */
class WacCalculationService {
	
	public static boolean DEBUG = false
	
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
			if (DEBUG) println "wacCalculation/geometrieAusRaumdaten: Keine Räume vorhanden!"
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
		if (DEBUG) println "wacCalculation/geometrie: ${map.gebaude.geometrie?.dump()}"
		// Raumvolumenströme - Gesamtvolumen der Nutzungseinheit
		map.raum.raumVs.gesamtVolumenNE = g.luftvolumen ?: 0.0d
		if (DEBUG) println "wacCalculation/geometrie: map.raum.raumVs.gesamtVolumenNE=${map.raum.raumVs.gesamtVolumenNE?.dump()}"
	}
	
	/**
	 * Summe der Fläche aller Räume.
	 */
	Double summeRaumFlache(map) {
		def flache = map.raum.raume.inject(0.0d) { o, n -> o + n.raumFlache }
		if (DEBUG) println "wacCalculation/summeRaumFlache: ${flache?.dump()}"
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
		if (DEBUG) println "wacCalculation/summeRaumVolumen: ${volumen?.dump()}"
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
		if (DEBUG) println "wacCalculation/summeLuftmengeVolumen: ${volumen?.dump()}"
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
                    // WAC-151: Nur berechnen, wenn keine Daten eingegeben wurden
					if (!raum.raumNummer) raum.raumNummer = String.format("%s%02d", geschossIndex, raumIndex + 1)
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
			if (DEBUG) println "wacCalculation/gesamtAussenluftVs: flache=${flache?.dump()}"
			flache = 30.0d
		}
		//
		def r = 0.0d
		if (flache) {
			r = -0.001 * flache * flache + 1.15 * flache + 20
		} else {
			if (DEBUG) println "gesamtAussenluftVs: Konnte keine Fläche ermitteln"
		}
		r = r ?: 0.0d
		if (DEBUG) println "wacCalculation/gesamtAussenluftVs: ${r?.dump()}"
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
				if (DEBUG) println "wacCalculation/warmeschutzFaktor: unbekannt, 0.0"
				0.0d
			}
		}
		if (DEBUG) println "wacCalculation/warmeschutzFaktor: ${r?.dump()}"
		r
	}
	
	/**
	 * DiffDruck ist abhängig von der Gebäudelage.
     * TODO rbe DIN1946, Seite 37, Tabelle 10, Freie Lüftung
	 */
	Double diffDruck(map) {
		def r = map.gebaude.lage.with {
			if (windschwach) 2.0d
			else if (windstark) 4.0d
			/*else {
				if (DEBUG) println "wacCalculation/diffDruck: Gebäudelage unbekannt, 0.0"
				0.0d
			}*/
		}
		if (DEBUG) println "wacCalculation/diffDruck: ${r?.dump()}"
		r
	}
	
	/**
	 * Wirksamen Infiltrationsanteil berechnen.
     * TODO rbe DIN1946, Seite 34
	 */
	Double infiltration(map, Boolean ventilator) {
		def m = [
			sys: 0.6f,
			inf: 1.0d,
			n50: 1.0d, // Je nach Kategorie (A,B,C) setzen (Tabelle 9)
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
		if (DEBUG) println "wacCalculation/infiltration: ${m?.dump()} -> ${r?.dump()}"
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
		if (DEBUG) println "wacCalculation/ltmErforderlich: ${r?.dump()}"
		r
	}
	
	/**
	 * Automatische Berechnung der Luftmenge und Luftwechsel pro Stunde
	 * getrennt nach Abluft und Zuluft im Verhältnis der einzelnen Raumluftvolumenströme
	 * zum Gesamt-Raumluftvolumenstrom.
	 * @param map model.map
	 * @param b true=Raumvolumenströme, false=Gesamtraumvolumenstrom
	 */
	void autoLuftmenge(map/*, Boolean b*/) {
		// LTM nicht erforderlich?
		if (!ltmErforderlich(map)) {
			map.messages.ltm = "Es sind keine lüftungstechnischen Maßnahmen notwendig!"
		}
		// LTM: erste Berechnung für Raumvolumenströme
		// Summiere Daten aus Raumdaten
		Double gesamtZuluftfaktor = 0.0d
        gesamtZuluftfaktor = zuluftRaume(map)?.inject(0.0d) { o, n ->
				o + n.raumZuluftfaktor
			}
        // Eingegebener oder berechneter Wert? Eingegebener, wird erst weiter unten errechnet!
		Double gesamtAbluftVs = 0.0d
        gesamtAbluftVs = abluftRaume(map)?.inject(0.0d) { o, n ->
				o + n.raumAbluftVolumenstrom
			}
		// Gesamt-Außenluftvolumenstrom bestimmen
		Double gesamtAussenluft =
				Math.max(
						Math.max(gesamtAbluftVs, gesamtAussenluftVs(map)),
						map.gebaude.geplanteBelegung.mindestaussenluftrate ?: 0.0d
					) * (map.gebaude.faktorBesondereAnforderungen ?: 1.0d)
		// Gesamt-Außenluftvolumenstrom für lüftungstechnische Maßnahmen
		Double gesamtAvsLTM = 0.0d
        def infilt = infiltration(map, true)
        if (DEBUG) println "autoLuftmenge: infiltration(map, true)=${infilt}"
        if (DEBUG) println "autoLuftmenge: infiltration(map, false)=${infiltration(map, false)}"
		if (map.aussenluftVs.infiltrationBerechnen/* && b*/) {
			gesamtAvsLTM = gesamtAussenluft - infilt
		} else {
			gesamtAvsLTM = gesamtAussenluft
		}
		//
		Double ltmAbluftSumme = 0.0d
		map.raum.ltmAbluftSumme = 0.0d
		Double ltmZuluftSumme = 0.0d
		map.raum.ltmZuluftSumme = 0.0d
		// Alle Räume, die einen Abluftvolumenstrom > 0 haben...
		map.raum.raume.grep { it.raumAbluftVolumenstrom > 0.0d }.each {
            if (DEBUG) println "autoLuftmenge: raum mit Abluftvolumenstrom > 0: ${it.raumBezeichnung}"
            // Abluftvolumenstrom abzgl. Infiltration errechnen
			Double ltmAbluftRaum = Math.round(gesamtAvsLTM / gesamtAbluftVs * it.raumAbluftVolumenstrom)
            Double ltmZuluftRaum = 0.0d
//			// Raumvolumenströme berechnen?
//			if (b) {
				it.raumAbluftVolumenstromInfiltration = ltmAbluftRaum
				if (it.raumVolumen > 0) {
					it.raumLuftwechsel = (ltmAbluftRaum / it.raumVolumen)
				} else {
					it.raumLuftwechsel = 0
				}
				// ZU/AB
				if (it.raumLuftart.contains("ZU/AB")) {
					ltmZuluftRaum = Math.round(gesamtAvsLTM * it.raumZuluftfaktor / gesamtZuluftfaktor)
                    // Abzgl. Infiltration
					it.raumZuluftVolumenstromInfiltration = ltmZuluftRaum
					it.raumAbluftVolumenstromInfiltration = ltmAbluftRaum
                    if (ltmZuluftRaum > ltmAbluftRaum) {
						//it.raumVolumenstrom = ltmZuluftRaum
						it.raumLuftwechsel = (ltmZuluftRaum / it.raumVolumen)
					} else {
						//it.raumVolumenstrom = ltmAbluftRaum
						it.raumLuftwechsel = (ltmAbluftRaum / it.raumVolumen)
					}
					ltmZuluftSumme += ltmAbluftRaum
				}
//			} else {
//				ltmAbluftSumme += ltmAbluftRaum
//			}
			map.raum.ltmAbluftSumme = ltmAbluftSumme
            if (DEBUG) println "autoLuftmenge: ${it.raumBezeichnung}: raumAbluftVolumenstromInfiltration: Math.round(${gesamtAvsLTM} / ${gesamtAbluftVs} * ${it.raumAbluftVolumenstrom})=${ltmAbluftRaum}"
            if (DEBUG) println "autoLuftmenge: ${it.raumBezeichnung}: raumZuluftVolumenstromInfiltration: Math.round(${gesamtAvsLTM} * ${it.raumZuluftfaktor} / ${gesamtZuluftfaktor})  =${ltmZuluftRaum}"
		}
		// LTM: zweite Berechnung für Raumvolumenströme
		map.raum.raume.grep {
			it.raumZuluftfaktor > 0.0d && it.raumLuftart != "ZU/AB"
		}.each {
			Double ltmZuluftRaum = Math.round(gesamtAvsLTM * it.raumZuluftfaktor / gesamtZuluftfaktor)
//			if (b) {
                // Abzgl. Infiltration berechnen
				it.raumZuluftVolumenstromInfiltration = ltmZuluftRaum
				it.raumLuftwechsel = ltmZuluftRaum / it.raumVolumen
//			} else {
//				ltmZuluftSumme += ltmZuluftRaum
//			}
			map.raum.ltmZuluftSumme = ltmZuluftSumme
            if (DEBUG) println "autoLuftmenge: ${it.raumBezeichnung}: raumZuluftVolumenstromInfiltration: Math.round(${gesamtAvsLTM} * ${it.raumZuluftfaktor} / ${gesamtZuluftfaktor})  =${ltmZuluftRaum}"
		}
		// Überströmvolumenstrom = Vorschlag: Raumvolumenstrom
        // WAC-151
        if (!map.anlage.zentralgeratManuell) {
            println "WAC-151: Errechne Überströmvolumenstrom"
            map.raum.raume.each {
                // WAC-151: Wegen manueller Änderung nur vorschlagen, wenn kein Wert vorhanden ist
                if (!it.raumUberstromVolumenstrom || it.raumUberstromVolumenstrom == 0) {
                    switch (it.raumLuftart) {
                        case "ZU":
                            // Abzgl. Infiltration
                            it.raumUberstromVolumenstrom = it.raumZuluftVolumenstromInfiltration
                            break
                        case "AB":
                            // Abzgl. Infiltration
                            it.raumUberstromVolumenstrom = it.raumAbluftVolumenstromInfiltration
                            break
                        case "ZU/AB":
                            // Abzgl. Infiltration
                            it.raumUberstromVolumenstrom =
                                java.lang.Math.abs(it.raumZuluftVolumenstromInfiltration - it.raumAbluftVolumenstromInfiltration)
                            break
                    }
                    if (DEBUG) println "${it.raumBezeichnung}: raumUberstromVolumenstrom=${it.raumUberstromVolumenstrom}"
                }
            }
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
        if (DEBUG) println "aussenluftVs: map.aussenluftVs.infiltration=${map.aussenluftVs.infiltration}"
		// Lüftungstechnische Maßnahmen erforderlich?
		if (ltmErforderlich(map)) {
			map.aussenluftVs.massnahme = "Lüftungstechnische Maßnahmen erforderlich!"
		}
        else {
            // WAC-115: Hinweis LTM erforderlich -> Meldung ausblenden!
            map.aussenluftVs.massnahme = ""
        }
		//
		autoLuftmenge(map/*, true*/)
		//autoLuftmenge(map, false)
		//
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
		grundluftung = 0.0d
        grundluftung = abluftRaume(map)?.inject(0.0d) { o, n ->
            // Ohne Infiltration abzuziehen!
			o + n.raumAbluftVolumenstrom
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
		// Ausgabe der Gesamt-Luftvolumenströme für LTM
		grundluftung = Math.max(map.raum.ltmAbluftSumme, map.raum.ltmZuluftSumme)
		grundluftung = Math.max(gesamtAvs/*gesamtAussenluftVs(map)*/, grundluftung)
		grundluftung = Math.max(map.gebaude.geplanteBelegung.mindestaussenluftrate, grundluftung)
		def infiltration = map.aussenluftVs.infiltrationBerechnen ? infiltration(map, true) : 0.0d
        map.aussenluftVs.gesamtLvsLtmLvsNl = map.aussenluftVs.with {
                Math.max(Math.max(gesamtAvsNeLvsNl, gesamtAvsRaumLvsNl), gesamtAvsPersonLvsNl)
            } - infiltration
		map.aussenluftVs.gesamtLvsLtmLwNl = (grundluftung - infiltration) / geluftetesVolumen
		mindestluftung = 0.7f * grundluftung - infiltration
		map.aussenluftVs.gesamtLvsLtmLvsRl = mindestluftung
		map.aussenluftVs.gesamtLvsLtmLwRl = mindestluftung / geluftetesVolumen
		intensivluftung = 1.3f * grundluftung - infiltration
		map.aussenluftVs.gesamtLvsLtmLvsIl = intensivluftung
		map.aussenluftVs.gesamtLvsLtmLwIl = intensivluftung / geluftetesVolumen
		map.aussenluftVs.gesamtLvsLtmLvsFs = wsFaktor * grundluftung - infiltration
        // Lüftung zum Feuchteschutz = Nennlüftung / 1.3
        map.aussenluftVs.gesamtLvsLtmLvsFs = map.aussenluftVs.gesamtLvsLtmLvsNl / 1.3f
        map.aussenluftVs.gesamtLvsLtmLwFs = map.aussenluftVs.gesamtLvsLtmLwNl / 1.3f
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
	 * @param map One of map.raum.raume
	 */
	def berechneZuAbluftventile(map) {
		if (map.raumLuftart in ["ZU", "ZU/AB"]) {
			def ventil = map.raumBezeichnungZuluftventile
            if (DEBUG) println "berechneZuAbluftventile: ${map.raumBezeichnung}: raumBezeichnungZuluftventile=${ventil}"
			if (ventil) {
				def maxVolumenstrom = wacModelService.getMaxVolumenstrom(ventil)
				// Anzahl Ventile; abzgl. Infiltration
				map.raumAnzahlZuluftventile = java.lang.Math.ceil(map.raumZuluftVolumenstromInfiltration / maxVolumenstrom)
                if (DEBUG) println "berechneZuAbluftventile: ${map.raumBezeichnung}: raumAnzahlZuluftventile=java.lang.Math.ceil(${map.raumZuluftVolumenstromInfiltration} / ${maxVolumenstrom})=${map.raumAnzahlZuluftventile}"
				// Luftmenge je Ventil; abzgl. Infiltration
				map.raumZuluftmengeJeVentil = map.raumZuluftVolumenstromInfiltration / map.raumAnzahlZuluftventile
                if (DEBUG) println "berechneZuAbluftventile: ${map.raumBezeichnung}: ${map.raumZuluftVolumenstromInfiltration} / ${map.raumAnzahlZuluftventile}=${map.raumZuluftmengeJeVentil}"
			}
		}
		if (map.raumLuftart in ["AB", "ZU/AB"]) {
			def ventil = map.raumBezeichnungAbluftventile
            if (DEBUG) println "berechneZuAbluftventile: ${map.raumBezeichnung}: raumBezeichnungAbluftventile=${ventil}"
			if (ventil) {
				def maxVolumenstrom = wacModelService.getMaxVolumenstrom(ventil)
				// Anzahl Ventile; abzgl. Infiltration
				map.raumAnzahlAbluftventile = java.lang.Math.ceil(map.raumAbluftVolumenstromInfiltration / maxVolumenstrom)
                if (DEBUG) println "berechneZuAbluftventile: ${map.raumBezeichnung}: raumAnzahlAbluftventile=java.lang.Math.ceil(${map.raumAbluftVolumenstromInfiltration} / ${maxVolumenstrom})=${map.raumAnzahlAbluftventile}"
				// Luftmenge je Ventil; abzgl. Infiltration
				map.raumAbluftmengeJeVentil = map.raumAbluftVolumenstromInfiltration / map.raumAnzahlAbluftventile
                if (DEBUG) println "berechneZuAbluftventile: ${map.raumBezeichnung}: ${map.raumAbluftVolumenstromInfiltration} / ${map.raumAnzahlAbluftventile}=${map.raumAbluftmengeJeVentil}"
			}
		}
		if (DEBUG) println "berechneZuAbluftventile: ${map}"
		map
	}
	
	/**
	 * Raumvolumenströme - Überströmelemente.
	 * @param map One of map.raum.raume
	 */
	def berechneUberstromelemente(map) {
		// 1-3 wurden woanders erledigt
		// 4
		if (DEBUG) println "berechneUberstromelemente: map.raumUberstromElement=${map?.raumUberstromElement?.dump()} map=${map?.dump()}"
        if (map.raumUberstromElement) {
            def maxVolumenstrom = wacModelService.getMaxVolumenstrom(map.raumUberstromElement)
            // 5b
            if (DEBUG) println "berechneUberstromelemente: map.turen=${map?.turen?.dump()}"
            if (DEBUG) println "berechneUberstromelemente: map.raumMaxTurspaltHohe=${map.raumMaxTurspaltHohe}"
            if (!map.raumMaxTurspaltHohe) {
                if (DEBUG) println "WARNING: value for maxTurspaltHohe is missing, using default: 10.0 -> ${map?.dump()}"
                map.raumMaxTurspaltHohe = 10.0d
            }
            def querschnitt = map.turen.inject(0.0d, { o, n -> o + n.turBreite * map.raumMaxTurspaltHohe })
            // 5b
            def anzTurenOhneDichtung = map.turen.findAll { !it.turDichtung }?.size() ?: 0
            // 5a
            def vsMaxTurspalt = (querschnitt + 2500 * anzTurenOhneDichtung) / 100 / 3.1 * java.lang.Math.sqrt(1.5d)
            // 5
            if (DEBUG) println "berechneUberstromelemente: map.raumUberstromVolumenstrom=${map.raumUberstromVolumenstrom?.dump()}"
            // WAC-129: Gebäudedaten - Geplante Belegung -> map.raumUberstromVolumenstrom ist null!
            // try-catch um map.raumUberstromVolumenstrom und als default Wert 0.00 als Double setzen.
            try {
                def usRechenwert = map.raumUberstromVolumenstrom - vsMaxTurspalt
                // 6
                map.raumAnzahlUberstromVentile = java.lang.Math.ceil(usRechenwert / maxVolumenstrom)
            } catch (e) {
                if (DEBUG) println "berechneUberstromelemente: ${e}"
                e.printStackTrace()
                map.raumAnzahlUberstromVentile = 0.0d
            }
        }
        map
	}
	
	/**
	 * Druckverlust - Kanalnetz: berechne eine (gerade eingegebene) Teilstrecke.
	 * @param map One of map.dvb.kanalnetz
	 */
	def berechneTeilstrecke(map) {
		if (DEBUG) println "berechneTeilstrecke: map=${map.dump()}"
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
				if (DEBUG) println "switch clause runs into default. Klasse=${kanal.klasse} not defined ==> lambda not set"
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
		if (DEBUG) println "berechneTeilstrecke: map=${map.dump()}"
		map
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
		if (DEBUG) println "berechneVentileinstellung"
		def teilstrecken = { s ->
			s?.split(";").toList()
		}
		// Hole den Luftvolumenstrom der letzten Teilstrecke
		def luftVsLetzteTeilstrecke = { ve ->
			// Hole Luftvolumenstrom der letzten Teilstrecke
			def letzteTeilstrecke = teilstrecken(ve.teilstrecken).last().toInteger()
			def teilstrecke = map.dvb.kanalnetz.find {
                    it.teilstrecke == letzteTeilstrecke
            }
			if (teilstrecke.luftart == ve.luftart) {
				teilstrecke.luftVs
			} else {
				0.0d
			}
		}
		// Alle Einträge in der Tabelle Ventileinstellung durchlaufen
		map.dvb.ventileinstellung.each { ve ->
			//println "ve: ${ve}"
			// Prüfe, ob die letzte Teilstrecke existiert und ob die Luftart übereinstimmt
			def luftVsLts = luftVsLetzteTeilstrecke(ve)
			//println "luftVsLts=${luftVsLts}"
			if (luftVsLts > 0.0d) {
				// Berechne dP offen
				ve.dpOffen =
					wacModelService.getMinimalerDruckverlustFurVentil(ve.ventilbezeichnung, ve.luftart, luftVsLts)
				// Berechne Gesamtwiderstand aller Teilstrecken
				def x = teilstrecken(ve.teilstrecken).collect { t ->
						map.dvb.kanalnetz.find {
							it.teilstrecke.toString2() == t
						}.widerstandTeilstrecke
					}
				def z = x.inject(0.0d, { o, n ->
						o + n
					})
				ve.gesamtWiderstand = ve.dpOffen + z
			} else {
				println "berechneVentileinstellung: Letzte Teilstrecke ${letzteTeilstrecke} existiert nicht oder" +
					" Luftart stimmt nicht überein: ${map.luftart} == ${teilstrecke?.luftart}?"
			}
		}
		// Ermittle maximale Widerstandswerte
		// maximaler Wert aus Spalte "Gesamt [Pa]" minus den eigenen Wert "Gesamt [Pa]"
		def sortedZu = map.dvb.ventileinstellung.findAll { it.luftart == "ZU" }?.sort { it.gesamtWiderstand }?.toList()
		def maxZu = sortedZu?.size() > 0 ? sortedZu.last().gesamtWiderstand : 0.0d
		def sortedAb = map.dvb.ventileinstellung.findAll { it.luftart == "AB" }?.sort { it.gesamtWiderstand }?.toList()
		def maxAb = sortedAb?.size() > 0 ? sortedAb.last().gesamtWiderstand : 0.0d
		// Differenzen und Abgleich
		// Alle Einträge in der Tabelle Ventileinstellung durchlaufen
		map.dvb.ventileinstellung.each { ve ->
			if (ve.luftart == "ZU") {
				ve.differenz = maxZu - ve.gesamtWiderstand
			} else if (ve.luftart == "AB") {
				ve.differenz = maxAb - ve.gesamtWiderstand
			}
			ve.abgleich = ve.differenz + ve.dpOffen
			ve.einstellung =
				wacModelService.getEinstellung(ve.ventilbezeichnung, ve.luftart, luftVsLetzteTeilstrecke(ve), ve.abgleich)
		}
	}
	
	/**
	 * Raum bearbeiten - Türen berechnen, wenn Raumvolumenströme/Überströmelemente eingegeben wurden.
	 * @param map One of model.map.raum.raume
	 */
	def berechneTurspalt(map) {
		// Gilt nicht für Überström-Räume
		if (DEBUG) println "berechneTurspalt: map=${map.dump()}"
		if (map.raumLuftart.contains("ÜB")) {
            if (DEBUG) println "berechneTurspalt: Keine Berechnung von ÜB-Räumen"
		} else {
            // WAC-173: Existiert ein Durchgang? Ja, dann gesamte Berechnung nicht stattfinden, ÜB-Menge = 0 setzen
            def durchgang = map.turen.findAll { it.turBezeichnung ==~ /.*Durchgang.*/ }?.size() ?: 0
            if (durchgang) {
                // TODO Nicht setzen, wenn Werte manuell geändert wurden (WAC-151)?
                map.raumUberstromVolumenstrom = 0
                return
            }
			def anzTurenOhneDichtung = map.turen.findAll { it.turDichtung == false }?.size() ?: 0
            def abziehenTurenOhneDichtung = 2500 * anzTurenOhneDichtung
			def summeTurBreiten = map.turen.sum { it.turBreite.toDouble2() }
            if (DEBUG) println "berechneTurspalt: anzTurenOhneDichtung=${anzTurenOhneDichtung} summeTurBreiten=${summeTurBreiten}"
			map.turen.findAll { it.turBreite > 0 }?.each {
                try {
                    // Zuerst Überströmventile berechnen!
                    if (DEBUG) println "berechneTurspalt: map.raumUberstromVolumenstrom=${map.raumUberstromVolumenstrom}"
                    def tsqf = (100 * 3.1d * map.raumUberstromVolumenstrom / java.lang.Math.sqrt(1.5d)) - abziehenTurenOhneDichtung
                    it.turSpalthohe = tsqf / summeTurBreiten
                    it.turQuerschnitt = tsqf * it.turBreite / summeTurBreiten
                    if (DEBUG) println "berechneTurspalt: abziehen=${abziehen} tsqf=${tsqf} turSpalthohe=${it.turSpalthohe} turQuerschnitt=${it.turQuerschnitt}"
                } catch (e) {
                    println "berechneTurspalt: EXCEPTION=${e}"
                }
			}
            // WAC-165: Hinweis: Türspalt > max. Türspalthöhe?
            // TODO Zugriff auf model.meta notwendig! Alternativ Property pro Raum.
		}
        map
	}
	
	/**
	 * Aktustikberechnung.
	 */
	def berechneAkustik(typ, input, map) {
		def zero = [slp125: 0.0d, slp250: 0.0d, slp500: 0.0d, slp1000: 0.0d, slp2000: 0.0d, slp4000: 0.0d]
		// Convert all values in a map; multiply with -1
		def minus1 = { m ->
			m?.inject [:], { o, n ->
				o[n.key.toLowerCase()] = n.value * -1
				o
			}
		}
		// What list to work on?
		def m = map.akustik."${typ.toLowerCase()}"
		def t = m.tabelle
		// Row 1
		t[0] = wacModelService.getOktavmittenfrequenz(input.zentralgerat, input.volumenstrom, typ)
		// Row 2
		def pegelerhohungExternerDruck = wacModelService.getPegelerhohungExternerDruck(input.zentralgerat)
		if (DEBUG) println "pegelerhohungExternerDruck=$pegelerhohungExternerDruck"
		//t[1] = [:]
		pegelerhohungExternerDruck.each { k, v -> t[1][k.toLowerCase()] = (v * input.slpErhohungKanalnetz) / 100 }
		// Row 3
		//t[2] = [:]
		pegelerhohungExternerDruck.each { k, v -> t[2][k.toLowerCase()] = (v * input.slpErhohungFilter) / 100 }
		// Row 4
		t[3] = minus1(wacModelService.getSchallleistungspegel(input.hauptschalldampfer1)) ?: zero
		// Row 5
		t[4] = minus1(wacModelService.getSchallleistungspegel(input.hauptschalldampfer2)) ?: zero
		// Row 6
		def umlenkungenWert = input.umlenkungen * 0.9d
		t[5] = minus1(zero.inject([:], { o, n -> o[n.key] = umlenkungenWert; o }))
		// Row 7
		switch (input.luftverteilerkasten) {
			case 0:
				t[6] = zero
				break
			default:
				t[6] = [slp125: -3d, slp250: -3d, slp500: -3d, slp1000: -3d, slp2000: -3d, slp4000: -3d]
		}
		// Row 8
		def slpLangsdampfung = wacModelService.getSchallleistungspegel(input.langsdampfungKanal)
		slpLangsdampfung ? slpLangsdampfung.each { k, v -> t[7][k.toLowerCase()] = (v * input.langsdampfungKanalLfdmMeter) } : zero
		t[7] = minus1(t[7])
		// Row 9
		t[8] = minus1(wacModelService.getSchallleistungspegel(input.schalldampferVentil)) ?: zero
		// Row 10
		t[9] = minus1(wacModelService.getSchallleistungspegel(input.einfugungsdammwert)) ?: zero
		// Row 11
		switch (input.raumabsorption) {
			case "BAD":
				t[10] = zero
				break
			case "WOHNEN":
				t[10] = [slp125: -4d, slp250: -4d, slp500: -4d, slp1000: -4d, slp2000: -4d, slp4000: -4d]
				break
		}
		// Row 12
		switch (typ) {
			case "Zuluft":
				t[11] = [slp125: -16.1d, slp250: -8.6d, slp500: -3.2d, slp1000: 0.0d, slp2000: 1.2d, slp4000: 1.0d]
				break
			case "Abluft":
				t[11] = [slp125: -16.1d, slp250: -8.6d, slp500: -3.2d, slp1000: 0.0d, slp2000: 1.2d, slp4000: 0.0d]
				break
		}
		// Row 13
		def sumColumn = { k ->
			def s = 0.0d
			0.upto 11, {
				s += t[it][k]
			}
			s
		}
		t[12] = [slp125: sumColumn("slp125"), slp250: sumColumn("slp250"), slp500: sumColumn("slp500"), slp1000: sumColumn("slp1000"), slp2000: sumColumn("slp2000"), slp4000: sumColumn("slp4000")]
		//
		// Mittleren Schalldruckpegel berechnen
		//
		// Werte absteigend sortieren
		def msdpWerte = t[12].collect { it.value }.sort { a, b -> b <=> a }
		/* Original code
		def b = a[0]
		def i = 0
		def x
		while (i <= 4) {
			x = b - a[i + 1]
			if (x < 0 || x > 13) { i = 99 }
			else { b += diffToDelta(Math.round(x)) }
			i++
		}
		*/
		def diffToDelta = { Float abstand ->
			final Float[] ft = [
					3.0f, 2.8f, 2.5f, 2.3f,  2.1f, 1.9f,  1.8f, 1.6f,  1.5f, 1.3f,   1.2f,  1.1f, 1f,
					0.9f, 0.8f, 0.7f, 0.6f, 0.55f, 0.5f, 0.45f, 0.4f, 0.35f, 0.3f, 0.275f, 0.25f, 0.225f,
					0.2f
				]
			if (abstand > 0 && abstand <= 13f && abstand % 0.5f == 0) {
				ft[(int) abstand * 2]
			} else {
				0f
			}
		}
		def msdp = msdpWerte[0]
		def tmp
		msdpWerte.eachWithIndex { it, idx ->
		    tmp = msdpWerte[idx + 1] ? msdp - msdpWerte[idx + 1] : 0
		    if (idx > 4 || tmp < 0 || tmp > 13) return
		    msdp += diffToDelta(Math.round(tmp))
		}
		// MSDP darf nie unter 20 sein
		m.mittlererSchalldruckpegel = java.lang.Math.max(20, msdp)
		//
		if (DEBUG) t.eachWithIndex { it, p -> println "$p: $it" }
	}
	
}

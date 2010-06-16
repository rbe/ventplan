package com.westaflex.wac

/**
 * 
 */
//@Singleton
class WacCalculationService {
	
	static scope = "singleton"
	
	/**
	 * Dezimalzahl auf 5 runden.
	 */
	private Integer round5(Double factor) {
		5 * (Math.round(factor / 5))
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
	 * Projekt, Gebäudedaten
	 */
	Double mindestaussenluftRate(Integer personenAnzahl, Double aussenluftVolumenstrom) {
		personenAnzahl * aussenluftVolumenstrom
	}
	
	/**
	 * Gebäudedaten - Geometrie anhand eingegebener Räume berechnen.
	 */
	void geometrieAusRaumdaten(map) {
		map.with {
			// Gesamtfläche berechnen
			gebaude.geometrie.wohnflache =
				(raum.raume.inject(0.0f) { o, n ->
					o + n.raumFlache
				})
			// Mittlere Raumhöhe berechnen
			gebaude.geometrie.raumhohe =
				(raum.raume.inject(0.0f) { o, n ->
					o + n.raumHohe
				} / raum.raume.size())
		}
		// Geometrie berechnen...
		geometrie(map)
	}
	
	/**
	 * Gebäudedate - Geometrie berechnen.
	 */
	void geometrie(map) {
		def g = map.gebaude.geometrie
		try {
			// Luftvolumen der Nutzungseinheit = Wohnfläche * mittlere Raumhöhe
			if (g.wohnflache && g.raumhohe) {
				g.luftvolumen = g.geluftetesVolumen = volumen(g.wohnflache, g.raumhohe)
			}
			// Gelüftetes Volumen = gelüftete Fläche * mittlere Raumhöhe
			if (g.gelufteteFlache && g.raumhohe) {
				g.geluftetesVolumen = volumen(g.gelufteteFlache, g.raumhohe)
			}
			// Gelüftetes Volumen = Luftvolumen, wenn kein gelüftetes Volumen berechnet
			if (g.luftvolumen && !g.geluftetesVolumen) {
				g.geluftetesVolumen = g.luftvolumen
			}
		} catch (e) {
			e.printStackTrace()
			g.luftvolumen = g.geluftetesVolumen = "E"
		}
		// Set calculated values in model
		map.gebaude.geometrie.geluftetesVolumen = g.geluftetesVolumen
		map.gebaude.geometrie.luftvolumen = g.luftvolumen
		println "geometrie: ${map.gebaude.geometrie?.dump()}"
	}
	
	/**
	 * Summe der Fläche aller Räume.
	 */
	Double summeRaumFlache(map) {
		def flache = map.raum.raume.inject(0.0f) { o, n -> o + n.raumFlache }
		println "summeRaumFlache: ${flache}"
		flache
	}
	
	/**
	 * Summe der Volumen aller Räume.
	 */
	Double summeRaumVolumen(map) {
		def volumen = map.raum.raume.inject(0.0f) { o, n -> o + n.raumVolumen }
		def mittlereRaumhohe = map.gebaude.geometrie.raumhohe
		if (volumen < 30f * mittlereRaumhohe) volumen = 30.0f * mittlereRaumhohe
		println "summeRaumVolumen: ${volumen?.dump()}"
		volumen
	}
	
	/**
	 * Prüfe den Zuluftfaktor, Rückgabe: [übergebener wert, neuer wert]
	 */
	def prufeZuluftfaktor = { float zf ->
		def minMax = { v, min, max ->
			if (v < min) { min }
			else if (v > max) { max } else { v }
		}
		def nzf
		switch (raumWerte.raumBezeichnung) {
			case "Wohnzimmer":									nzf = minMax(zf, 2.5f, 3.5f); break
			case ["Kinderzimmer", "Schlafzimmer"]:				nzf = minMax(zf, 1.0f, 3.0f); break
			case ["Esszimmer", "Arbeitszimmer", "Gästezimmer"]: nzf = minMax(zf, 1.0f, 2.0f); break
			default: nzf = zf
		}
		[zf, nzf]
	}
	
	/**
	 * Gesamt-Außenluft-Volumenstrom berechnen.
	 */
	Double gesamtAussenluftVs(map) {
		// Fläche = Geometrie, gelüftetes Volumen / mittlere Raumhöhe
		def flache = 0.0f
		def g = map.gebaude.geometrie
		if (g.geluftetesVolumen && g.raumhohe) {
			flache = g.geluftetesVolumen / g.raumhohe
		}
		// Fläche = addiere alle Flächen aus der Tabelle Raumdaten
		else {
			flache = summeRaumFlache(map)
			// Wenn Summe < 30, dann 30
			if (flache < 30.0f) flache = 30.0f
		}
		//
		if (flache == Double.NaN) {
			println "gesamtAussenluftVs: flache=${flache?.dump()}"
			flache = 30.0f
		}
		//
		def r = 0.0f
		if (flache) {
			r = -0.001 * flache * flache + 1.15 * flache + 20
		} else {
			println "gesamtAussenluftVs: Konnte keine Fläche ermitteln"
		}
		println "gesamtAussenluftVs: ${r?.dump()}"
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
				println "warmeschutzFaktor: unbekannt, 0.0"
				0.0f
			}
		}
		println "warmeschutzFaktor: ${r?.dump()}"
		r
	}
	
	/**
	 * DiffDruck ist abhängig von der Gebäudelage.
	 */
	Double diffDruck(map) {
		def r = map.gebaude.lage.with {
			if (windschwach) 2.0f
			else if (windstark) 4.0f
			else {
				println "diffDruck: Gebäudelage unbekannt, 0.0"
				0.0f
			}
		}
		println "diffDruck: ${r?.dump()}"
		r
	}
	
	/**
	 * Wirksamen Infiltrationsanteil berechnen.
	 */
	Double infiltration(map, Boolean ventilator) {
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
			m.n50 = map.gebaude.luftdichtheit.luftwechsel
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
			m.flache = map.gebaude.geometrie.geluftetesVolumen / map.gebaude.geometrie.raumhohe
			m.volumen = map.gebaude.geometrie.geluftetesVolumen
		}
		// wirk wird nach Tabelle 11 Seite 29 ermittelt, Norm 1946-6 endgültige Fassung
		//m.wirk = sys * inf * (-flache / 1600 + 1.025)
		/*if (ventilator) m.wirk = 0.45f
		else m.wirk = 0.5f*/
		m.wirk = ventilator ? 0.45f : 0.5f
		//
		def r = m.with {
			if (wirk && volumen && n50 && diffDruck && druckExpo) {
				wirk * volumen * n50 * Math.pow(diffDruck / 50, druckExpo)
			} else {
				0.0f
			}
		}
		println "infiltration: ${m?.dump()} -> ${r?.dump()}"
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
		println "ltmErforderlich: ${r?.dump()}"
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
			// TODO Fehlermeldung, dialog
			println "autoLuftmenge: Es sind keine lüftungstechnischen Maßnahmen notwendig!"
		}
		// LTM: erste Berechnung für Raumvolumenströme
		// Summiere Daten aus Raumdaten
		Double gesamtZu = zuluftRaume(map).inject(0.0f) { o, n ->
			o + n.raumZuluftfaktor
		}
		Double gesamtAb = abluftRaume(map).inject(0.0f) { o, n ->
			o + n.raumAbluftVs
		}
		// Gesamt-Außenluftvolumenstrom bestimmen
		Double gesamtAussenluft =
			Math.max(
					Math.max(gesamtAb, gesamtAussenluftVs(map)),
					map.gebaude.geplanteBelegung.mindestaussenluftrate
				) * (map.gebaude.faktorBesondereAnforderungen ?: 1.0f)
		// Gesamt-Außenluftvolumenstrom für lüftungstechnische Maßnahmen
		Double gesamtAvsLTM = 0.0f
		if (map.aussenluftVs.infiltrationBerechnen && b) {
			gesamtAvsLTM = gesamtAussenluft - infiltration(true)
		} else {
			gesamtAvsLTM = gesamtAussenluft
		}
		//
		Double ltmAbluftSumme = 0.0f
		Double ltmZuluftSumme = 0.0f
		// Alle Räume, die einen Abluftvolumenstrom > 0 haben...
		map.raum.raume.grep { it.raumAbluftVs > 0.0f }.each {
			Double ltmAbluftRaum = Math.round(gesamtAvsLTM / gesamtAb * it.raumAbluftVs)
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
					Double ltmZuluftRaum = Math.round(gesamtAvsLTM * it.raumZuluftfaktor / gesamtZu)
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
		map.raum.raume.grep { it.raumZuluftfaktor > 0.0f && it.raumLuftart != "ZU/AB" }.each {
			Double ltmZuluftRaum = Math.round(gesamtAvsLTM * it.raumZuluftfaktor / gesamtZu)
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
		map.aussenluftVs.gesamt = round5(
				gesamtAvs * wsFaktor * map.gebaude.faktorBesondereAnforderungen
			)
		// Infiltration
		map.aussenluftVs.infiltration = round5(infiltration(map, false))
		// Lüftungstechnische Maßnahmen erforderlich?
		if (ltmErforderlich(map)) {
			map.aussenluftVs.massnahme = "Lüftungstechnische Maßnahmen erforderlich!"
		}
		//
		autoLuftmenge(map, false)
		Double grundluftung = gesamtAvs //gesamtAussenluftVs(map)
		Double geluftetesVolumen = map.gebaude.geometrie.geluftetesVolumen ?: 0.0f
		Double mindestluftung
		Double intensivluftung
		Double feuchteluftung
		// Ausgabe der Gesamt-Außenluftvolumenströme
		map.aussenluftVs.gesamtAvsNeLvsNl = round5(grundluftung)
		map.aussenluftVs.gesamtAvsNeLwNl = (grundluftung / geluftetesVolumen)
		mindestluftung = 0.7f * grundluftung
		map.aussenluftVs.gesamtAvsNeLvsRl = round5(mindestluftung)
		map.aussenluftVs.gesamtAvsNeLwRl = (mindestluftung / geluftetesVolumen)
		intensivluftung = 1.3f * grundluftung
		map.aussenluftVs.gesamtAvsNeLvsIl = round5(intensivluftung)
		map.aussenluftVs.gesamtAvsNeLwIl = (intensivluftung / geluftetesVolumen)
		feuchteluftung = wsFaktor * grundluftung
		map.aussenluftVs.gesamtAvsNeLvsFs = round5(feuchteluftung)
		map.aussenluftVs.gesamtAvsNeLwFs = (feuchteluftung / geluftetesVolumen)
		// Ausgabe der Gesamt-Raumabluft-Volumenströme
		grundluftung = abluftRaume(map).inject(0.0f) { o, n ->
			o + n.raumAbluftVs
		}
		map.aussenluftVs.gesamtAvsRaumLvsNl = round5(grundluftung)
		map.aussenluftVs.gesamtAvsRaumLwNl = (grundluftung / geluftetesVolumen)
		mindestluftung = 0.7f * grundluftung
		map.aussenluftVs.gesamtAvsRaumLvsRl = round5(mindestluftung)
		map.aussenluftVs.gesamtAvsRaumLwRl = (mindestluftung / geluftetesVolumen)
		intensivluftung = 1.3f * grundluftung
		map.aussenluftVs.gesamtAvsRaumLvsIl = round5(intensivluftung)
		map.aussenluftVs.gesamtAvsRaumLwIl = (intensivluftung / geluftetesVolumen)
		feuchteluftung = wsFaktor * grundluftung
		map.aussenluftVs.gesamtAvsRaumLvsFs = round5(feuchteluftung)
		map.aussenluftVs.gesamtAvsRaumLwFs = (feuchteluftung / geluftetesVolumen)
		// Ausgabe der personenbezogenen Gesamtaußenluftvolumenströme
		grundluftung = map.gebaude.geplanteBelegung.mindestaussenluftrate
		map.aussenluftVs.gesamtAvsPersonLvsNl = round5(grundluftung)
		map.aussenluftVs.gesamtAvsPersonLwNl = (grundluftung / geluftetesVolumen)
		mindestluftung = 0.7f * grundluftung
		map.aussenluftVs.gesamtAvsPersonLvsRl = round5(mindestluftung)
		map.aussenluftVs.gesamtAvsPersonLwRl = (mindestluftung / geluftetesVolumen)
		intensivluftung = 1.3f * grundluftung
		map.aussenluftVs.gesamtAvsPersonLvsIl = round5(intensivluftung)
		map.aussenluftVs.gesamtAvsPersonLwIl = (intensivluftung / geluftetesVolumen)
		feuchteluftung = wsFaktor * grundluftung
		map.aussenluftVs.gesamtAvsPersonLvsFs = round5(feuchteluftung)
		map.aussenluftVs.gesamtAvsPersonLwFs = (feuchteluftung / geluftetesVolumen)
		// Ausgabe der Volumenströme für LTM
		grundluftung = Math.max(map.raum.ltmAbluftSumme, map.raum.ltmZuluftSumme)
		grundluftung = Math.max(gesamtAvs/*gesamtAussenluftVs(map)*/, grundluftung)
		grundluftung = Math.max(map.gebaude.geplanteBelegung.mindestaussenluftrate, grundluftung)
		if (map.aussenluftVs.infiltrationBerechnen) {
			grundluftung -= infiltration(map, true)
		}
		map.aussenluftVs.gesamtLvsLtmLvsNl = round5(grundluftung)
		map.aussenluftVs.gesamtLvsLtmLwNl = (grundluftung / geluftetesVolumen)
		mindestluftung = 0.7f * grundluftung
		map.aussenluftVs.gesamtLvsLtmLvsRl = round5(mindestluftung)
		map.aussenluftVs.gesamtLvsLtmLwRl = (mindestluftung / geluftetesVolumen)
		intensivluftung = 1.3f * grundluftung
		map.aussenluftVs.gesamtLvsLtmLvsIl = round5(intensivluftung)
		map.aussenluftVs.gesamtLvsLtmLwIl = (intensivluftung / geluftetesVolumen)
	}
	
	/**
	 * 
	 */
	void luftmengeBerechnen() {
		float fZuluftMenge = 0f;
		float fAbluftMenge = 0f;
		String typ = null;
		int divisor = 1;
		float volumen = 0f;
		float luftwechsel = 0f;
		int anzahlVentile = 0;
		float luftmengeJeVentil = 0f;
		HashMap<String, Integer> hmVol = new HashMap();
		Integer row = lmeTabelle.getSelectedRow();
		if (row > -1) {
			volumen = getLuftmengeVolumen(row);
			fZuluftMenge = getLuftmengeIstLuftmenge(row);
			fAbluftMenge = getLuftmengeSollLuftmenge(row);
			typ = getLuftmengeTypenbezeichnung(row);
			if (fAbluftMenge > 0) {
				if (volumen != 0) {
					luftwechsel = fAbluftMenge / volumen;
				}
				setLuftmengeLuftwechsel(row, luftwechsel);
			} else {
				if (fZuluftMenge > 0) {
					if (volumen != 0) {
						luftwechsel = fZuluftMenge / volumen;
					}
					setLuftmengeLuftwechsel(row, luftwechsel);
				}
			}
			if (fZuluftMenge > 0 && typ != null) {
				if (hmVol.containsKey(typ)) {
					divisor = hmVol.get(typ);
				} else {
					divisor = getMaxVolumenstrom(typ);
					if (divisor == 0) {
						divisor = Math.round(fZuluftMenge);
					}
					hmVol.put(typ, divisor);
				}
				// Anzahl Ventile
				anzahlVentile = (int) Math.ceil(fZuluftMenge / divisor);
				JTableUtil.setFormattedFloatInTableCell(lmeTabelle, row, ANZABLUFTVENTILE, anzahlVentile);
				// Luftmenge je Ventile
				luftmengeJeVentil = fZuluftMenge / anzahlVentile;
				JTableUtil.setFormattedFloatInTableCell(lmeTabelle, row, ZULUFTMENGEJEVENTIL, luftmengeJeVentil);
			} else {
				if (fAbluftMenge > 0 && typ != null) {
					if (hmVol.containsKey(typ)) {
						divisor = hmVol.get(typ);
					} else {
						divisor = getMaxVolumenstrom(typ);
						if (divisor == 0) {
							divisor = Math.round(fAbluftMenge);
						}
						hmVol.put(typ, divisor);
					}
					// Anzahl Ventile
					anzahlVentile = (int) Math.ceil(fAbluftMenge / divisor);
					JTableUtil.setFormattedFloatInTableCell(lmeTabelle, row, ANZABLUFTVENTILE, anzahlVentile);
					// Luftmenge je Ventile
					luftmengeJeVentil = fAbluftMenge / anzahlVentile;
					JTableUtil.setFormattedFloatInTableCell(lmeTabelle, row, ABLUFTMENGEJEVENTIL, luftmengeJeVentil);
				} else {
					lmeTableModel.setValueAt(null, row, ANZABLUFTVENTILE);
					lmeTableModel.setValueAt(null, row, ABLUFTMENGEJEVENTIL);
				}
			}
		}
		//
		Float vol = 0
		if (project.getGelueftetVolumen() != 0) {
			vol = project.getGelueftetVolumen()
		} else {
			vol = sumVolumen()
		}
		//
		luftmengeAutoBerechnen(false);
		//
		Float fGesAU_Luft = 0.0f
		if (project.getWirkInfiltration()) {
			fGesAU_Luft = project.round5(project.getVsGrundlueftungWertLabel() + berechneInfiltration(true));
		} else {
			fGesAU_Luft = project.getVsGrundlueftungWertLabel();
		}
		// Setze Ergebnis "Gesamtvolumen"
		project.setLmeGesamtvolumenWertLabel(vol);
		project.setLmeGesAussenluftVolumenWertLabel(fGesAU_Luft);
		project.setLmeGebaeudeluftwechselWertLabel(fGesAU_Luft / vol);
	}
	
	/**
	 * Zeigt alle (meist errechneten) Komponenten mit ihren akutellen Werten
	 * an: Luftmenge
	 */
	void updateLuftmengeTab(Boolean b) {
		berechnungen.luftmengeBerechnen();
		float fZuluftmenge = ConversionUtil.parseFloatFromComponent(lmeSummeZuluftmengeWertLabel);
		float fAbluftmenge = ConversionUtil.parseFloatFromComponent(lmeSummeAbluftmengeWertLabel);
		float fGrundlueftung1 = Math.max(fAbluftmenge, fZuluftmenge);
		float fInfiltration = 0f;
		if (getWirkInfiltration()) {
			fInfiltration = berechnungen.berechneInfiltration(true);
		}
		float fGrundlueftung = fGrundlueftung1 - fInfiltration;
		float fMindestlueftung = 0.7f * fGrundlueftung1 - fInfiltration;
		float fIntensivlueftung = 1.3f * fGrundlueftung1 - fInfiltration;
		float fFeuchtelueftung = getWaermeschutz() * fGrundlueftung1 - fInfiltration;
		float fMinimum = Float.parseFloat(lmeVolumenstromCombobox.getItemAt(0).toString());
		if (lmeZentralgeraetCombobox.getSelectedItem().toString().contains("400WAC")) {
			fMinimum = 75;
		}
		if (fMindestlueftung < fMinimum) {
			fMindestlueftung = fMinimum;
		}
		if (fFeuchtelueftung < fMinimum) {
			fFeuchtelueftung = fMinimum;
		}
		ConversionUtil.setFormattedFloatInComponent(lmeGrundlueftungWertLabel, round5(fGrundlueftung), Locale.GERMAN);
		ConversionUtil.setFormattedFloatInComponent(lmeMindestlueftungWertLabel, round5(fMindestlueftung), Locale.GERMAN);
		ConversionUtil.setFormattedFloatInComponent(lmeFeuchteschutzWertLabel, round5(fFeuchtelueftung), Locale.GERMAN);
		ConversionUtil.setFormattedFloatInComponent(lmeIntensivlueftungWertLabel, round5(fIntensivlueftung), Locale.GERMAN);
		setGeraeteauswahl(round5(fGrundlueftung));
		// Versteckte *SummeWertLabel setzen
		ConversionUtil.setFormattedFloatInComponent(lmeAbSummeWertLabel, berechnungen.sumVolumen("AB"), Locale.GERMAN);
		ConversionUtil.setFormattedFloatInComponent(lmeZuSummeWertLabel, berechnungen.sumVolumen("ZU"), Locale.GERMAN);
		ConversionUtil.setFormattedFloatInComponent(lmeUebSummeWertLabel, berechnungen.sumVolumen("ÜB"), Locale.GERMAN);
		berechnungen.aktualisiereVentile();
		setSumLTMZuluftmengeWertLabel();
		setSumLTMAbluftmengeWertLabel();
		berechnungen.aktualisiereUeberstroemelemente();
	}
	
}

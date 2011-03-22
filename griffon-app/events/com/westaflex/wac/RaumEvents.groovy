/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/events/com/westaflex/wac/RaumEvents.groovy
 * Last modified at 22.03.2011 13:07:54 by rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

/**
 * 
 */
@griffon.util.EventPublisher
class RaumEvents {
	
	public static boolean DEBUG = false
	
	def model
	def wacCalculationService
	def wacModelService
	
	/**
	 * Execute code 'later'.
	 */
	def doLater = { closure ->
		javax.swing.SwingUtilities.invokeLater closure
	}
	
	/**
	 * Einen neuen Raum hinzufügen.
	 */
	def onRaumHinzufugen = { raum, view ->
		if (DEBUG) println "processing event 'RaumHinzufugen': raum=${raum.dump()}"
        if (DEBUG) println "processing event 'RaumHinzufugen': raum.turen=${raum.turen}"
		// Standard-Werte setzen
		raum.with {
			// Übernehme Wert für Bezeichnung vom Typ?
			raumBezeichnung = raumBezeichnung ?: raumTyp
			// Länge + Breite
			raumLange = 0.0d
			raumBreite = 0.0d
			// Fläche, Höhe, Volumen
			raumFlache = raumFlache.toDouble2()
			raumHohe = raumHohe.toDouble2()
			raumVolumen = raumFlache * raumHohe
			// Zuluftfaktor
			raumZuluftfaktor = raumZuluftfaktor?.toDouble2() ?: 0.0d
			// Abluftvolumenstrom
			raumAbluftVolumenstrom = raumAbluftVolumenstrom?.toDouble2() ?: 0.0d
			// Standard Türspalthöhe ist 10 mm
			raumTurspaltHohe = 10.0d
		}
		doLater {
			// Raum im Model hinzufügen
            if (DEBUG) println "onRaumHinzufugen: raum -> ${raum}"
			model.addRaum(raum, view)
			onRaumHinzugefugt(raum.position, view)
		}
	}
	
	/**
	 * Ein Raum wurde hinzugefügt - berechnen und letzen Zeile in Tabellen wählen.
	 */
	def onRaumHinzugefugt = { raumIndex, view ->
		doLater {
			if (DEBUG) println "processing event 'RaumHinzugefugt': raumIndex=${raumIndex}"
			// Neu berechnen
			onRaumGeandert(raumIndex)
		}
	}
	
	/**
	 * Ein Raum wurde geändert - berechne alles, was von Räumen abhängt.
	 */
	def onRaumGeandert = { raumIndex ->
		if (DEBUG) println "onRaumGeandert: raum -> ${model.map.raum.raume}"
		doLater {
			if (DEBUG) println "processing event 'RaumGeandert': raumIndex=${raumIndex}"
            // WAC-65: Errechnete Werte zurücksetzen
            model.map.raum.raume[raumIndex].with {
                raumVolumen = raumFlache * raumHohe
                raumLuftwechsel = 0.0d
                raumAbluftVolumenstromInfiltration = 0.0d // Abluftvs abzgl. Infiltration
                raumAnzahlAbluftventile = 0
                raumAbluftmengeJeVentil = 0.0d
                raumAnzahlZuluftventile = 0
                raumZuluftmengeJeVentil = 0.0d
                raumAnzahlUberstromVentile = 0
                raumUberstromVolumenstrom = 0.0d
            }
			// Nummern der Räume berechnen
			wacCalculationService.berechneRaumnummer(model.map)
			// Gebäude-Geometrie berechnen
			wacCalculationService.geometrieAusRaumdaten(model.map)
			// Aussenluftvolumenströme berechnen
			wacCalculationService.aussenluftVs(model.map)
            // Zu-/Abluftventile
            model.map.raum.raume[raumIndex] =
                wacCalculationService.berechneZuAbluftventile(model.map.raum.raume[raumIndex])
            // Türspalt
            model.map.raum.raume[raumIndex] =
                wacCalculationService.berechneTurspalt(model.map.raum.raume[raumIndex])
            // Überströmelement berechnen
            model.map.raum.raume[raumIndex] =
                wacCalculationService.berechneUberstromelemente(model.map.raum.raume[raumIndex])
			// Zentralgerät bestimmen
			publishEvent "ZentralgeratAktualisieren"
			// Diesen Raum in allen Tabellen anwählen
			publishEvent "RaumInTabelleWahlen", [raumIndex]
		}
	}
	
	/**
	 * Einen Raum entfernen.
	 */
	def onRaumEntfernen = { raumIndex, view ->
		doLater {
			if (DEBUG) println "onRaumEntfernen: raumIndex=${raumIndex}"
			// Raum aus Model entfernen
			model.removeRaum(raumIndex, view)
			// Es hat sich was geändert...
			onRaumGeandert(raumIndex)
		}
	}
	
}

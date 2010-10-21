/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/events/com/westaflex/wac/RaumEvents.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

/**
 * 
 */
@griffon.util.EventPublisher
class RaumEvents {
	
	public static boolean DEBUG = true
	
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
			raumAbluftVs = raumAbluftVs?.toDouble2() ?: 0.0d
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
			// Gebäude-Geometrie berechnen
			wacCalculationService.geometrieAusRaumdaten(model.map)
			// Aussenluftvolumenströme berechnen
			wacCalculationService.aussenluftVs(model.map)
			// Nummern der Räume berechnen
			wacCalculationService.berechneRaumnummer(model.map)
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

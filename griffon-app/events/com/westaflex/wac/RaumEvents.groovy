/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/events/com/westaflex/wac/RaumEvents.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
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
	 * TODO rbe What code should be executed to update the UI after actions?
	 */
	def syncUI = { closure = {} ->
		doLater {
			model.resyncRaumTableModels()
			closure()
		}
	}
	
	/**
	 * Einen neuen Raum hinzufügen.
	 */
	def onRaumHinzufugen = { raum ->
		println "processing event 'RaumHinzufugen': raum=${raum.dump()}"
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
			model.addRaum(raum)
			onRaumHinzugefugt(raum.position)
		}
	}
	
	/**
	 * Ein Raum wurde hinzugefügt - berechnen und letzen Zeile in Tabellen wählen.
	 */
	def onRaumHinzugefugt = { raumIndex ->
		doLater {
			println "processing event 'RaumHinzugefugt': raumIndex=${raumIndex}"
			// Add PropertyChangeListener to our model.map
			GH.addMapPropertyChangeListener("map.raum.raume", model.map.raum.raume[raumIndex])
			// Neu berechnen
			onRaumGeandert(raumIndex)
			// RaumVsView - Zu-/Abluftventile TabelModel aktualisieren
			println "publish event AddTableModelRow"
			publishEvent "AddTableModelRow", [raumIndex]
		}
	}
	
	/**
	 * Ein Raum wurde geändert - berechne alles, was von Räumen abhängt.
	 */
	def onRaumGeandert = { raumIndex ->
		doLater {
			println "processing event 'RaumGeandert': raumIndex=${raumIndex}: ${model.map.raum.raume[raumIndex]?.dump()}"
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
	def onRaumEntfernen = { raumIndex ->
		doLater {
			println "onRaumEntfernen: raumIndex=${raumIndex}"
			def zuLoschenderRaum = model.map.raum.raume[raumIndex]
			// Raum aus Model entfernen
			model.removeRaum(raumIndex)
			// RaumVsView - Zu-/Abluftventile TabelModel aktualisieren
			println "onRaumEntfernen: publish event RemoveTableModelRow"
			publishEvent "RemoveTableModelRow", [raumIndex]
		}
	}
	
}

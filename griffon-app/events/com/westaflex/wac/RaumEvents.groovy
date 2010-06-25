/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/events/com/westaflex/wac/RaumEvents.groovy
 * 
 * Copyright (C) 1996-2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

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
	 * 
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
	def onRaumHinzufugen = { raumWerte ->
		println "processing event 'RaumHinzufugen'"
		// Standard-Werte setzen
		raumWerte.with {
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
		// Überstrom-Raum
		if (raumWerte.raumLuftart == "ÜB") {
			def (zuluftfaktor, neuerZuluftfaktor) = wacCalculationService.prufeZuluftfaktor(raumWerte.raumZuluftfaktor)
			if (zuluftfaktor != neuerZuluftfaktor) {
				// TODO mmu Dialog with Oxbow
				println "Der Zuluftfaktor wird von ${zuluftfaktor} auf ${neuerZuluftfaktor} (laut Norm-Tolerenz) geändert!"
			}
			raumWerte.raumZuluftfaktor = neuerZuluftfaktor
		}
		// Raum im Model unten (= position: ...size()) hinzufügen
		def raum = raumWerte + [position: model.map.raum.raume.size() ?: 0]
		doLater {
			model.addRaum(raum)
			onRaumHinzugefugt(model.map.raum.raume.size() - 1 ?: 0)
		}
	}
	
	/**
	 * Ein Raum wurde hinzugefügt - berechnen und letzen Zeile in Tabellen wählen.
	 */
	def onRaumHinzugefugt = { raumIndex ->
		doLater {
			println "onRaumHinzugefugt: raumIndex=${raumIndex}"
			// Neu berechnen
			onRaumGeandert()
			// Diesen Raum in allen Tabellen anwählen
			publishEvent "RaumInTabelleWahlen", [raumIndex]
		}
	}
	
	/**
	 * Ein Raum wurde geändert - berechne alles, was von Räumen abhängt.
	 */
	def onRaumGeandert = {
		doLater {
			println "processing event 'RaumGeandert'"
			wacCalculationService.geometrieAusRaumdaten(model.map)
			wacCalculationService.aussenluftVs(model.map)
		}
	}
	
	/**
	 * 
	 */
	def onRaumEntfernen = { raumIndex ->
		doLater {
			println "onRaumEntfernen: raumIndex=${raumIndex}"
			// Raum aus Model entfernen
			model.removeRaum(raumIndex)
			// Neu berechnen
			onRaumGeandert()
			// Einen anderen Raum in allen Tabellen anwählen
			publishEvent "RaumInTabelleWahlen", [raumIndex]
		}
	}
	
	/**
	 * 
	 */
	def onRaumZuAbluftventileLuftmengeBerechnen = { raumIndex ->
		doLater {
			println "onRaumZuAbluftventileLuftmengeBerechnen: Berechne Luftmenge für Raum Nr. ${raumIndex}"
			wacCalculationService.raumLuftmengeBerechnen(map, raumIndex)
			model.resyncRaumTableModels()
		}
	}
	
	/**
	 * 
	 */
	def onRaumUberstromelementeLuftmengeBerechnen = { raumIndex ->
		doLater {
			println "onRaumUberstromelementeLuftmengeBerechnen: Berechne Luftmenge für Raum Nr. ${raumIndex}"
			wacCalculationService.raumLuftmengeBerechnen(map, raumIndex)
			model.resyncRaumTableModels()
		}
	}
	
}

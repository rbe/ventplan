/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/services/com/westaflex/wac/WacModelService.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.westaflex.wac

import groovy.sql.*
import org.javanicus.gsql.*

/**
 * Communicate with WestaWAC database.
 */
class WacModelService {
	
	/**
	 * 
	 */
	public static boolean DEBUG = false
	
	/**
	 * Hole Liste mit Zentralgeräten (Raumvolumenströme).
	 */
	List getZentralgerat() {
		def r = withSql { sql ->
				sql.rows("SELECT artikelnummer FROM artikelstamm"
					+ " WHERE kategorie = ? AND gesperrt = ? AND maxvolumenstrom <> ?"
					+ " ORDER BY artikelnummer",
					[1, false, 0])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "getZentralgerat: ${r?.dump()}"
		r
	}
	
	/**
	 * Hole Volumenströme für ein bestimmtes Zentralgerät (Raumvolumenströme).
	 */
	List getVolumenstromFurZentralgerat(String artikel) {
		def r = withSql { sql ->
				sql.rows("SELECT DISTINCT volumenstrom FROM schalleistungspegel"
					+ " WHERE artikelnummer = ? ORDER BY volumenstrom",
					[artikel])
			}?.collect {
				it.volumenstrom
			}
		if (DEBUG) println "getVolumenstromFurZentralgerat: ${r?.dump()}"
		r
	}
	
	/**
	 * 
	 */
	String getZentralgeratFurVolumenstrom(Integer luftung) {
		def r = withSql { sql ->
				sql.firstRow("SELECT artikelnummer FROM artikelstamm"
					+ " WHERE kategorie = 1 AND maxvolumenstrom >= ?"
					+ " ORDER BY artikelnummer",
					[luftung])
			}
		if (DEBUG) println "getZentralgeratForVolumenstrom: ${luftung} -> ${r}"
		r ? r.ARTIKELNUMMER : ""
	}
	
	/**
	 * Hole alle Zu/Abluftventile.
	 */
	List getZuAbluftventile() {
		def r = withSql { sql ->
				sql.rows("SELECT DISTINCT(artikelnummer) FROM druckverlust WHERE ausblaswinkel <> ? ORDER BY artikelnummer", [180])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "getZuluftventile: ${r?.dump()}"
		r
	}
	
	/**
	 * Hole alle Überströmelemente.
	 */
	List getUberstromelemente() {
		def r = withSql { sql ->
				sql.rows("SELECT artikelnummer FROM artikelstamm WHERE klasse = ? ORDER BY artikelnummer", [14])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "getUberstromElemente: ${r?.dump()}"
		r
	}
	
	/**
	 * 
	 */
	Integer getMaxVolumenstrom(String artikel) {
		def r = withSql { sql ->
				sql.firstRow("SELECT maxvolumenstrom FROM artikelstamm"
					+ " WHERE artikelnummer = ?"
					+ " ORDER BY maxvolumenstrom",
					[artikel])
			}
		if (DEBUG) println "getMaxVolumenstrom(${artikel}): ${r?.dump()}"
		r ? r as Integer : 0
	}
	
	/**
	 * 
	 */
	List getDvbKanalbezeichnung() {
		def r = withSql { sql ->
				sql.rows("SELECT artikelnummer FROM artikelstamm WHERE klasse BETWEEN 4 AND 8 ORDER BY artikelnummer")
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "getDvbKanalbezeichnung(): ${r?.dump()}"
		r
	}
	
	/**
	 * 
	 */
	def getKanal(String kanalbezeichnung) {
		def r = withSql { sql ->
				sql.firstRow("SELECT klasse, durchmesser, flaeche, seitea, seiteb FROM rohrwerte"
					+ " WHERE artikelnummer = ?",
					[kanalbezeichnung])
			}
		if (DEBUG) println "getKanal(): ${kanalbezeichnung}: ${r?.dump()}"
		r
	}
	
	/**
	 * 
	 */
	List getDvbVentileinstellung() {
		def r = withSql { sql ->
				sql.rows("SELECT DISTINCT(artikelnummer) FROM druckverlust WHERE ausblaswinkel <> 180 ORDER BY artikelnummer")
			}?.collect {
				it.artikelnummer
			}
		// Add empty item
		r << ""
		if (DEBUG) println "getDvbKanalbezeichnung(): ${r?.dump()}"
		r
	}
	
	/**
	 * 
	 */
	List getWbw() {
		def r = withSql { sql ->
				sql.rows("SELECT id, bezeichnung, wert, CONCAT(id, '.png') bild FROM widerstandsbeiwerte ORDER BY bezeichnung")
			}
		if (DEBUG) println "getWbw: r=${r?.dump()}"
		r
	}
	
	/**
	 * 
	 */
	def getMinimalerDruckverlustFurVentil(String ventilbezeichnung, String luftart, Double luftmenge) {
		def r = withSql { sql ->
				sql.firstRow(
					"SELECT MIN(druckverlust) druckverlust FROM druckverlust"
					+ " WHERE artikelnummer = ? AND luftart = ? AND luftmenge = ?",
					[ventilbezeichnung, luftart, luftmenge])
			}
		if (DEBUG) println "getDruckverlustFurVentil(): ${r?.dump()}"
		r ? r.druckverlust : 0.0d
	}
	
	/**
	 * 
	 */
	def getEinstellung(String ventilbezeichnung, String luftart, Double luftmenge, Double abgleich) {
		def r = withSql { sql ->
				sql.rows("SELECT DISTINCT einstellung, druckverlust, luftmenge"
					+ " FROM druckverlust"
					+ " WHERE artikelnummer = ? AND luftart = ? AND luftmenge >= ?"
					+ " AND (ausblaswinkel = 360 OR ausblaswinkel = 0)"
					+ " ORDER BY luftmenge ASC, einstellung ASC",
					[ventilbezeichnung, luftart, luftmenge])
			}
		// Suche die nächst höhere zum Parameter 'luftmenge' passende Luftmenge aus den Datenbankergebnissen
		// Dies funktioniert nur mit einem in aufsteigender Reihenfolge sortierten Luftmengen!
		def nahe = r.find { if (it >= luftmenge) luftmenge }
		// Nehme nur diese Einträge und errechne min(|(abgleich - r.druckverlust)|)
		def m = r.findAll {
					it.luftmenge == nahe
				}.inject([druckverlust: Double.MAX_VALUE], { o, n ->
					def v1 = Math.abs(abgleich - o.druckverlust)
					def v2 = Math.abs(abgleich - n.druckverlust)
					if (v1 < v2) v1
					else v2
				})
		if (DEBUG) println "getEinstellung: einstellung=${m.einstellung}"
		m.einstellung
	}
	
	/**
	 * 
	 */
	List getSchalldampfer() {
		def r = withSql { sql ->
			sql.rows("SELECT artikelnummer FROM artikelstamm WHERE klasse = 2 AND gesperrt = false")
		}?.collect {
			it.artikelnummer
		}
		if (DEBUG) println "getSchalldampfer: ${r}"
		[""] + r
	}
	
}

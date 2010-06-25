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
//@Singleton(lazy = true)
class WacModelService {
	
	/**
	 * Hole Liste mit Zentralgeräten (Raumvolumenströme).
	 */
	List getZentralgerat() {
		def r = withSql { sql ->
				sql.rows("SELECT artikelnummer FROM artikelstamm WHERE kategorie = ? AND gesperrt = ? AND maxvolumenstrom <> ? ORDER BY artikelnummer", [1, false, 0])
			}?.collect {
				it.artikelnummer
			}
		//println "getZentralgerat: ${r?.dump()}"
		r
	}
	
	/**
	 * Hole Volumenströme für ein bestimmtes Zentralgerät (Raumvolumenströme).
	 */
	List getVolumenstromFurZentralgerat(String artikel) {
		def r = withSql { sql ->
				sql.rows("SELECT DISTINCT volumenstrom FROM schalleistungspegel WHERE artikelnummer = ? ORDER BY volumenstrom", [artikel])
			}?.collect {
				it.volumenstrom
			}
		//println "getVolumenstromFurZentralgerat: ${r?.dump()}"
		r
	}
	
	/**
	 * Hole alle Zu/Abluftventile.
	 */
	List getZuAbluftventile() {
		def r = withSql { sql ->
				sql.rows("SELECT DISTINCT(artikelnummer) FROM druckverlust WHERE ausblaswinkel <> ?", [180])
			}?.collect {
				it.artikelnummer
			}
		//println "getZuluftventile: ${r?.dump()}"
		r
	}
	
	/**
	 * Hole alle Überströmelemente.
	 */
	List getUberstromelemente() {
		def r = withSql { sql ->
				sql.rows("SELECT artikelnummer FROM artikelstamm WHERE klasse = ?", [14])
			}?.collect {
				it.artikelnummer
			}
		//println "getUberstromElemente: ${r?.dump()}"
		r
	}
	
	/**
	 * 
	 */
	Integer getMaxVolumenstrom(String artikel) {
		def r = withSql { sql ->
				sql.firstRow("SELECT maxvolumenstrom FROM artikelstamm WHERE artikelnummer = ? ORDER BY maxvolumenstrom", [artikel])
			}
		//println "getMaxVolumenstrom(${artikel}): ${r?.dump()}"
		r ? r as Integer : 0
	}
	
}

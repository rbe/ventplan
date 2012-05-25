/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

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
        def r = withSql { dataSourceName, sql ->
            sql.rows("SELECT artikelnummer FROM artikelstamm"
                    + " WHERE kategorie = ? AND gesperrt = ? AND maxvolumenstrom <> ?"
                    + " ORDER BY artikelnummer",
                    [1, false, 0])
        }?.collect {
            it.artikelnummer
        }
        if (DEBUG)
            println "getZentralgerat: ${r?.dump()}"
        r
    }

    /**
     * Hole Volumenströme für ein bestimmtes Zentralgerät (Raumvolumenströme).
     */
    List getVolumenstromFurZentralgerat(String artikel) {
        def r = withSql { dataSourceName, sql ->
            sql.rows("SELECT DISTINCT volumenstrom FROM schalleistungspegel"
                    + " WHERE artikelnummer = ? ORDER BY volumenstrom",
                    [artikel])
        }?.collect {
            it.volumenstrom
        }
        if (DEBUG)
            println "getVolumenstromFurZentralgerat: ${r?.dump()}"
        r
    }

    /**
     *
     */
    String getZentralgeratFurVolumenstrom(Integer luftung) {
        def r = withSql { dataSourceName, sql ->
            sql.firstRow("SELECT artikelnummer FROM artikelstamm"
                    + " WHERE kategorie = 1 AND gesperrt = ? AND maxvolumenstrom >= ?"
                    + " ORDER BY artikelnummer",
                    [false, luftung])
        }
        if (DEBUG)
            println "getZentralgeratForVolumenstrom: ${luftung} -> ${r}"
        r ? r.ARTIKELNUMMER : ""
    }

    /**
     * Hole alle Zu/Abluftventile.
     */
    List getZuAbluftventile() {
        def r = withSql { dataSourceName, sql ->
            sql.rows("SELECT DISTINCT(artikelnummer) FROM druckverlust WHERE ausblaswinkel <> ? ORDER BY artikelnummer", [180])
        }?.collect {
            it.artikelnummer
        }
        if (DEBUG)
            println "getZuluftventile: ${r?.dump()}"
        r
    }

    /**
     * Hole alle Zuluftventile.
     */
    List getZuluftventile() {
        def r = withSql { dataSourceName, sql ->
            sql.rows("SELECT DISTINCT(artikelnummer) FROM druckverlust WHERE luftart = 'ZU' AND ausblaswinkel <> ? ORDER BY artikelnummer", [180])
        }?.collect {
            it.artikelnummer
        }
        if (DEBUG)
            println "getZuluftventile: ${r?.dump()}"
        r
    }

    /**
     * Hole alle Abluftventile.
     */
    List getAbluftventile() {
        def r = withSql { dataSourceName, sql ->
            sql.rows("SELECT DISTINCT(artikelnummer) FROM druckverlust WHERE luftart = 'AB' AND ausblaswinkel <> ? ORDER BY artikelnummer", [180])
        }?.collect {
            it.artikelnummer
        }
        if (DEBUG)
            println "getZuluftventile: ${r?.dump()}"
        r
    }

    /**
     * Hole alle Überströmelemente.
     */
    List getUberstromelemente() {
        def r = withSql { dataSourceName, sql ->
            sql.rows("SELECT artikelnummer FROM artikelstamm WHERE klasse = ? ORDER BY artikelnummer", [14])
        }?.collect {
            it.artikelnummer
        }
        if (DEBUG)
            println "getUberstromElemente: ${r?.dump()}"
        r
    }

    /**
     *
     */
    Integer getMaxVolumenstrom(String artikel) {
        def r = withSql { dataSourceName, sql ->
            sql.firstRow("SELECT maxvolumenstrom FROM artikelstamm"
                    + " WHERE artikelnummer = ?"
                    + " ORDER BY maxvolumenstrom",
                    [artikel])
        }
        if (DEBUG)
            println "getMaxVolumenstrom(${artikel}): ${r?.dump()}"
        r ? r.maxvolumenstrom as Integer : 0
    }

    /**
     *
     */
    List getDvbKanalbezeichnung() {
        def r = withSql { dataSourceName, sql ->
            sql.rows("SELECT artikelnummer FROM artikelstamm WHERE klasse BETWEEN 4 AND 8 ORDER BY artikelnummer")
        }?.collect {
            it.artikelnummer
        }
        if (DEBUG)
            println "getDvbKanalbezeichnung(): ${r?.dump()}"
        r
    }

    /**
     *
     */
    def getKanal(String kanalbezeichnung) {
        def r = withSql { dataSourceName, sql ->
            sql.firstRow("SELECT klasse, durchmesser, flaeche, seitea, seiteb FROM rohrwerte"
                    + " WHERE artikelnummer = ?",
                    [kanalbezeichnung])
        }
        if (DEBUG)
            println "getKanal(): ${kanalbezeichnung}: ${r?.dump()}"
        r
    }

    /**
     *
     */
    List getDvbVentileinstellung() {
        def r = withSql { dataSourceName, sql ->
            sql.rows("SELECT DISTINCT(artikelnummer) FROM druckverlust WHERE ausblaswinkel <> 180 ORDER BY artikelnummer")
        }?.collect {
            it.artikelnummer
        }
        // Add empty item
        r = [""] + r
        if (DEBUG)
            println "getDvbKanalbezeichnung(): ${r?.dump()}"
        r
    }

    /**
     *
     */
    List getWbw() {
        def r = withSql { dataSourceName, sql ->
            // H2 sql.rows("SELECT id, bezeichnung, wert, CONCAT(id, '.png') bild FROM widerstandsbeiwerte ORDER BY bezeichnung")
            sql.rows("SELECT id, bezeichnung, wert, id || '.png' bild FROM widerstandsbeiwerte ORDER BY bezeichnung")
        }
        if (DEBUG)
            println "getWbw: r=${r?.dump()}"
        r
    }

    /**
     *
     */
    def getMinimalerDruckverlustFurVentil(String ventilbezeichnung, String luftart, Double luftmenge) {
        def r = withSql { dataSourceName, sql ->
            sql.firstRow("SELECT MIN(druckverlust) druckverlust FROM druckverlust"
                    + " WHERE artikelnummer = ? AND luftart = ? AND luftmenge >= ?",
                    [ventilbezeichnung, luftart, luftmenge])
        }
        if (DEBUG)
            println "getDruckverlustFurVentil(${[ventilbezeichnung, luftart, luftmenge]}): ${r?.dump()}"
        r?.druckverlust ?: 0.0d
    }

    /**
     *
     */
    def getEinstellung(String ventilbezeichnung, String luftart, Double luftmenge, Double abgleich) {
        def r = withSql { dataSourceName, sql ->
            sql.rows("SELECT DISTINCT einstellung, druckverlust, luftmenge"
                    + " FROM druckverlust"
                    + " WHERE artikelnummer = ? AND luftart = ? AND luftmenge >= ?"
                    + " AND (ausblaswinkel = 360 OR ausblaswinkel = 0)"
                    + " ORDER BY luftmenge ASC, einstellung ASC",
                    [ventilbezeichnung, luftart, luftmenge])
        }
        //println "getEinstellung: r=${r}"
        if (r.size() == 0)
            return
        // Suche die nächst höhere zum Parameter 'luftmenge' passende Luftmenge aus den Datenbankergebnissen
        // Dies funktioniert nur mit einem in aufsteigender Reihenfolge sortierten Luftmengen!
        def nahe = r.find {
            //println "${it.luftmenge} >= ${luftmenge}?"
            it.luftmenge >= luftmenge
        }.luftmenge
        // Nehme nur diese Einträge und errechne min(|(abgleich - r.druckverlust)|)
        def m = r.findAll {
            //println "${it.luftmenge} == ${nahe}?"
            it.luftmenge == nahe
        }.inject([druckverlust: Double.MAX_VALUE], { o, n ->
            int v1 = Math.abs(abgleich - o.druckverlust)
            int v2 = Math.abs(abgleich - n.druckverlust)
            v1 < v2 ? o : n
        })
        if (DEBUG)
            println "getEinstellung(${[ventilbezeichnung, luftart, luftmenge, abgleich]}): einstellung=${m.einstellung}"
        m.einstellung
    }

    /**
     *
     */
    List getSchalldampfer() {
        def r = withSql { dataSourceName, sql ->
            sql.rows("SELECT artikelnummer FROM artikelstamm WHERE klasse = ? AND gesperrt = ?", [2, false])
        }?.collect {
            it.artikelnummer
        }
        // Add empty item
        r = [""] + r
        if (DEBUG)
            println "getSchalldampfer: ${r?.dump()}"
        r
    }

    /**
     * Akustikberechnung, db(A) des Zentralgeräts.
     */
    def getDezibelZentralgerat(artnr, volumenstrom, luftart) {
        def r = withSql { dataSourceName, sql ->
            sql.rows("SELECT s.dba"
                    + " FROM schalleistungspegel s"
                    + " WHERE artikelnummer = ? AND volumenstrom >= ? AND ZuAbEx = ?",
                    [artnr, volumenstrom, luftart == "Zuluft" ? 0 : 1])
        }
        r = r[0].dba
        if (DEBUG)
            println "getDezibelZentralgerat($artnr,$volumenstrom,$luftart): ${r?.dump()}"
        r
    }

    /**
     * Akustikberechnung, Oktavmittenfrequenz.
     */
    Map getOktavmittenfrequenz(artnr, volumenstrom, luftart) {
        def r = withSql { dataSourceName, sql ->
            sql.rows("SELECT s.slp125, s.slp250, s.slp500, s.slp1000, s.slp2000, s.slp4000, s.dba"
                    + " FROM schalleistungspegel s"
                    + " WHERE artikelnummer = ? AND volumenstrom >= ? AND ZuAbEx = ?",
                    [artnr, volumenstrom, luftart == "Zuluft" ? 0 : 1])
        }
        r = r[0]
        if (DEBUG)
            println "getOktavmittenfrequenz($artnr,$volumenstrom,$luftart): ${r?.dump()}"
        r
    }

    /**
     * Akustikberechnung, Schallleistungspegel.
     */
    def getSchallleistungspegel(artnr) {
        def r = withSql { dataSourceName, sql ->
            sql.rows("SELECT s.slp125, s.slp250, s.slp500, s.slp1000, s.slp2000, s.slp4000"
                    + " FROM schalleistungspegel s"
                    + " WHERE artikelnummer = ?",
                    [artnr])
        }
        r = r[0]
        if (DEBUG)
            println "getSchallleistungspegel($artnr): ${r?.dump()}"
        r
    }

    /**
     * Akustikberechnung, Pegelerhöhung externer Druck.
     */
    Map getPegelerhohungExternerDruck(artnr) {
        def r = withSql { dataSourceName, sql ->
            sql.rows("SELECT s.slp125, s.slp250, s.slp500, s.slp1000, s.slp2000, s.slp4000"
                    + " FROM schalleistungspegel s"
                    + " WHERE artikelnummer = ? AND ZuAbEx = 2",
                    [artnr])
        }
        r = r[0]
        if (DEBUG)
            println "getPegelerhohungExternerDruck($artnr): ${r?.dump()}"
        r
    }

}

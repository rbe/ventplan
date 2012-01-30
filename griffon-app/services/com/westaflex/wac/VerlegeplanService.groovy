/**
 * WAC
 * 
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. All rights reserved.
 * Created by: mmu
 */
package com.westaflex.wac

import groovy.sql.*
import org.javanicus.gsql.*

/**
 * Erstellen der Stückliste aus den vorhandenen Werten.
 */
class VerlegeplanService {

    /**
	 *
	 */
	public static boolean DEBUG = false

    // Geschosse:
    //      {KG; EG; OG; DG; SB}
    // Pro Geschoss:
    //      * Geschoss a[i,0]
    //      * Anzahl Zuluftventile pro Geschoss a[i,1]
    //      * Zuluftvolumenstrom pro Geschoss a[i,2]
    //      * Gesamt Zuluftvolumenstrom = Volumenstrom des Zentralgeräts a[i,3]
    //      * Anzahl Abluftventile pro Geschoss a[i,4]
    //      * Abluftvolumenstrom pro Geschoss a[i,5]
    //      * Gesamt Abluftvolumenstrom = Volumenstrom des Zentralgeräts a[i,6]
	def stucklistArray = [ [ "KG", "EG", "OG", "DG", "SB" ],
                           [ ] ]
	
	/**
	 * 
	 */
	def VerlegeplanService() {
		
	}


    // SQL für Gerätepaket: (Zuluft + Abluft)
    // MaxVolumenstrom >= 'Volumenstrom des Zentralgeräts'
    Integer selectMaxVolumenstrom(String zentralgerat, Integer volumenstrom)
    {
        def statement = "SELECT MaxVolumenstrom FROM pakete " +
                        " WHERE Geraet = ? " +
                        " AND MaxVolumenstrom >= ? " +
                        " AND Kategorie = 72 " +
                        " ORDER BY MaxVolumenstrom asc;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, volumenstrom])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectMaxVolumenstrom: ${r?.dump()}"
		r
    }

    def selectZentralgeratZuluft(String zentralgerat, Integer maxvolumenstrom) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 72 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.MaxVolumenstrom >= ? " +
                        " AND pakete.Bedingung = 'ZU' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, maxvolumenstrom])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectZentralgeratZuluft: ${r?.dump()}"
		r
    }

    def selectZentralgeratAbluft(String zentralgerat, Integer maxvolumenstrom) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 72 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.MaxVolumenstrom >= ? " +
                        " AND pakete.Bedingung = 'AB' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, maxvolumenstrom])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectZentralgeratAbluft: ${r?.dump()}"
		r
    }



    //SQL für AU-FO-Paket: (Außenluft und Fortluft)
    def selectAussenluftPaket(String zentralgerat, String paketeBedingung) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 70 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.Bedingung = ? " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, paketeBedingung])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectAussenluftPaket: ${r?.dump()}"
		r
    }

    def selectFortluftPaket(String zentralgerat, String paketeBedingung) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 71 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.Bedingung = ? " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, paketeBedingung])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectFortluftPaket: ${r?.dump()}"
		r
    }


    //SQL für Grundpaket: (Zuluft + Abluft)
    def selectGrundpaketZuluft(String zentralgerat, Integer maxvolumenstrom) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 73 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.MaxVolumenstrom = ? " +
                        " AND pakete.Bedingung = 'ZU' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, maxvolumenstrom])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectGrundpaketZuluft: ${r?.dump()}"
		r
    }

    def selectGrundpaketAbluft(String zentralgerat, Integer maxvolumenstrom) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 73 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.MaxVolumenstrom = ? " +
                        " AND pakete.Bedingung = 'AB' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, maxvolumenstrom])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectGrundpaketAbluft: ${r?.dump()}"
		r
    }


    //SQL und Logik für Erweiterungspaket
    List selectErweiterungspaketZuluft() {
        def statement = "SELECT MaxVolumenstrom " +
                        " FROM pakete " +
                        " WHERE Kategorie = 75 " +
                        " AND MaxVolumenstrom >= 'Gesamt Zuluftvolumenstrom' " +
                        " ORDER BY MaxVolumenstrom asc;"
    }
    
    List selectErweiterungspaketAbluft() {
        def statement = "SELECT MaxVolumenstrom " +
                        " FROM pakete " +
                        " WHERE Kategorie = 75 " +
                        " AND MaxVolumenstrom >= 'Gesamt Abluftvolumenstrom' " +
                        " ORDER BY MaxVolumenstrom asc;"
    }


    //Für Zuluft:
    // maxvolumentstrom = 'Gesamt Zuluftvolumenstrom'
    List selectErweiterungspaketZuluftProGeschoss(String zentralgerat, Integer maxvolumenstrom) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 74 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.MaxVolumenstrom >= ? " +
                        " AND pakete.Bedingung = 'ZU' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, maxvolumenstrom])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectErweiterungspaketZuluftProGeschoss: ${r?.dump()}"
		r
    }

    //Für Abluft:
    // maxvolumentstrom = 'Gesamt Abluftvolumenstrom'
    List selectErweiterungspaketAbluftProGeschoss(String zentralgerat, Integer maxvolumenstrom) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 74 " +
                        " AND pakete.Geraet = ? " +
                        " AND pakete.MaxVolumenstrom >= ? " +
                        " AND pakete.Bedingung = 'AB' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat, maxvolumenstrom])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectErweiterungspaketAbluftProGeschoss: ${r?.dump()}"
		r
    }

    //SQL für Verteilpaket
    //1. Der Array für das Erweiterungspaket wird auch hier benötigt.
    //2. Pro Geschoss, in dem die Anzahl Zuluftventile (bzw. Abluftventile) größer Null ist, wird folgende SQL-Abfrage gestartet:
    //Für Zuluft:
    List selectVerteilpaketZuluft(Integer anzahlZuluftventile) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 75 " +
                        " AND pakete.Geraet = '' " +
                        " AND pakete.MaxVolumenstrom >= 0 " +
                        " AND pakete.Bedingung >= 'Anzahl Zuluftventile' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[anzahlZuluftventile])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectVerteilpaketZuluft: ${r?.dump()}"
		r
    }

    //Für Abluft:
    List selectVerteilpaketAbluft(Integer anzahlAbluftventile) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 75 " +
                        " AND pakete.Geraet = '' " +
                        " AND pakete.MaxVolumenstrom >= 0 " +
                        " AND pakete.Bedingung >= 'Anzahl Abluftventile' " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[anzahlAbluftventile])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectVerteilpaketAbluft: ${r?.dump()}"
		r
    }

    //SQL für Luftdurchlass
    //1. In einer Map müssen alle Luftventile aus den „Raumvolumenströme / Zu- Abluftventile“ erfasst und die „Gesamtanzahl“ ermittelt werden.
    //   Die Luftventile sind die „Bezeichnung Luftventil“.
    def selectLuftdurchlass(String zentralgerat) {
        def statement = "SELECT stueckliste.Artikel, stueckliste.Anzahl " +
                        " FROM stueckliste, pakete " +
                        " WHERE stueckliste.Paket = pakete.ID " +
                        " AND pakete.Kategorie = 76 " +
                        " AND pakete.Geraet = ? " +
                        " ORDER BY stueckliste.Reihenfolge;"

        def r = withSql { sql ->
				sql.rows(statement,
					[zentralgerat])
			}?.collect {
				it.artikelnummer
			}
		if (DEBUG) println "selectVerteilpaketAbluft: ${r?.dump()}"
		r
    }

}

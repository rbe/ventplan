/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 28.08.12 17:21
 */

package com.ventplan.desktop

import groovy.sql.GroovyRowResult

/**
 *
 */
class StucklisteService {

    /**
     * Database access through our model service.
     */
    VentplanModelService ventplanModelService

    /**
     * Füge einen Artikel zur Stückliste hinzu.
     * @param stuckliste Map
     * @param artikel groovy.sql.GroovyRowResult
     * @param paket
     */
    void artikelAufStuckliste(Map stuckliste, artikel, paket = null) {
        if (null == stuckliste || null == artikel) {
            //throw new IllegalArgumentException('stuckliste or artikel == null?')
            return
        }
        def artikelnummer = ventplanModelService.getArtikelnummer(artikel)
        artikel.ARTIKEL = artikel.ARTIKELNUMMER = artikelnummer
        if (stuckliste.containsKey(artikelnummer)) {
            //def alt = stuckliste[artikelnummer].ANZAHL
            stuckliste[artikelnummer].ANZAHL += artikel.ANZAHL ?: 1.0
            //println String.format('+      Artikel hinzu: Paket=%5s Artikel=%17s Anzahl=%4.1f (%4.1f + %4.1f)', paket ?: '', artikelnummer, stuckliste[artikelnummer].ANZAHL, artikel.ANZAHL, alt)
        } else {
            //println String.format('* Füge Artikel hinzu: Paket=%5s Artikel=%17s Anzahl=%4.1f', paket ?: '', artikelnummer, artikel.ANZAHL)
            stuckliste[artikelnummer] = artikel
            // Prüfe Artikel auf Gültigkeit
            if (!ventplanModelService.isArticleValidToday(artikelnummer)) {
                stuckliste[artikelnummer].ARTIKELBEZEICHNUNG = '*** ' + stuckliste[artikelnummer].ARTIKELBEZEICHNUNG
            }
        }
    }

    /**
     * Erstelle Ergebnis (sortiert etc.) aus einer Stückliste (siehe paketeZuStuckliste, artikelAufStuckliste).
     * @param stuckliste
     */
    Map makeResult(Map stuckliste) {
        stuckliste.sort { Map.Entry map ->
            map.value.REIHENFOLGE
        }
        /* WAC-223
        stuckliste.each { Map.Entry map ->
            // Prüfe Artikel auf Gültigkeit
            if (!ventplanModelService.isArticleValidToday(map.ARTIKELNUMMER)) {
                map.ARTIKELBEZEICHNUNG = '*** ' + map.ARTIKELBEZEICHNUNG
            }
        }
        */
    }

    /**
     * Zeige Inhalt der Stückliste formatiert an.
     * @param stuckliste
     */
    void dumpStuckliste(Map stuckliste) {
        println ''
        println 'GESAMTLISTE DER ARTIKEL'
        println '======================='
        println String.format('%2s %12s %7s %6s %17s - %s', 'NR', 'REIHENFOLGE', 'ANZAHL', 'ME', 'ARTIKELNUMMER', 'ARTIKELBEZEICHNUNG')
        println '----------------------------------------------------------------------'
        makeResult(stuckliste).eachWithIndex { it, i ->
            def artikel = it.value
            int reihenfolge = (int) artikel.REIHENFOLGE
            double anzahl = (double) artikel.ANZAHL
            String artikelnummer = ventplanModelService.getArtikelnummer(artikel)
            println String.format('%2d % 12d % 7.1f %6s %17s - %s', i + 1, reihenfolge, anzahl, artikel.MENGENEINHEIT, artikelnummer, artikel.ARTIKELBEZEICHNUNG)
        }
    }

    /**
     *
     * @param model Our model.
     * @return Map Die Stückliste.
     */
    Map processData(Map map) {
        def pakete = []
        Map stuckliste = [:]
        String zentralgerat = map.anlage.zentralgerat
        Integer volumenstrom = map.anlage.volumenstromZentralgerat
        // Grundpaket
        try {
            List grundpaket = ventplanModelService.getGrundpaket(zentralgerat)
            //println String.format("%17s für %8s (Vs=%d) ist %s", 'Grundpaket', zentralgerat, volumenstrom, grundpaket)
            pakete += grundpaket
        } catch (e) {
        }
        // Gerätepaket
        try {
            List geratepaket = ventplanModelService.getGeratepaket(zentralgerat, volumenstrom)
            //println String.format("%17s für %8s (Vs=%d) ist %s", 'Geraetepaket', zentralgerat, volumenstrom, geratepaket)
            pakete += geratepaket
        } catch (e) {
        }
        // Erweiterungspaket für alle Ebenen außer die Erste
        try {
            List erwei = ventplanModelService.getErweiterungspaket(zentralgerat, volumenstrom)
            // Ebenen
            List verteilebenen = ventplanModelService.getVerteilebenen(map)
            int anzahlVerteilebenen = verteilebenen.size() - 1
            if (anzahlVerteilebenen > 0) {
                //println String.format("%17s für %8s (Vs=%d) sind %s", 'Verteilbenen', zentralgerat, volumenstrom, verteilebenen.join(', '))
                1.upto anzahlVerteilebenen, {
                    //println String.format("%17s für %8s (Vs=%d), %s für Ebene(n) %s", 'Erweiterungspaket', zentralgerat, volumenstrom, erwei, verteilebenen[it])
                    pakete += erwei
                }
            }
        } catch (e) {
        }
        // Außenluftpaket
        try {
            String aussenluft = map.anlage.aussenluft.grep { it.value == true }?.key[0]
            aussenluft = aussenluft[0].toUpperCase() + aussenluft[1..-1]
            if (aussenluft == 'Erdwarme') {
                aussenluft = 'EWT'
            }
            List aussenluftpaket = ventplanModelService.getAussenluftpaket(zentralgerat, volumenstrom, aussenluft)
            //println String.format("%17s für %8s (Vs=%d), %s ist %s", 'Aussenluftpaket', zentralgerat, volumenstrom, 'Wand', aussenluftpaket)
            pakete += aussenluftpaket
        } catch (e) {
        }
        // Fortluftpaket
        try {
            String fortluft = map.anlage.fortluft.grep { it.value == true }?.key[0]
            fortluft = fortluft[0].toUpperCase() + fortluft[1..-1]
            List fortluftpaket = ventplanModelService.getFortluftpaket(zentralgerat, volumenstrom, fortluft)
            //println String.format("%17s für %8s (Vs=%d), %s ist %s", 'Fortluftpaket', zentralgerat, volumenstrom, 'Dach', fortluftpaket)
            pakete += fortluftpaket
        } catch (e) {
        }
        // Verteilpakete
        try {
            def _verteilpakete = ventplanModelService.getVerteilpakete(map)
            def verteilpakete = _verteilpakete*.value['AB']['paket'] + _verteilpakete*.value['ZU']['paket']
            //println String.format("%17s für %8s (Vs=%d), sind %s", 'Verteilpakete', zentralgerat, volumenstrom, verteilpakete)
            pakete += verteilpakete
        } catch (e) {
        }
        // Luftauslässe
        try {
            List abluftventile = ventplanModelService.countAbluftventile(map).collect {
                // WAC-235
                if (!it.key.startsWith('0.0') && !it.key.startsWith('0,0')) {
                    ventplanModelService.getLuftauslasspaket(it.key, 'AB') * it.value
                }
            }.flatten()
            //println String.format("%17s für %8s (Vs=%d), %s", 'Abluftventile', zentralgerat, volumenstrom, abluftventile)
            pakete += abluftventile
        } catch (e) {
        }
        // Lufteinlässe
        try {
            List zuluftventile = ventplanModelService.countZuluftventile(map).collect {
                // WAC-235
                if (!it.key.startsWith('0.0') && !it.key.startsWith('0,0')) {
                    ventplanModelService.getLuftauslasspaket(it.key, 'ZU') * it.value
                }
            }.flatten()
            //println String.format("%17s für %8s (Vs=%d), %s", 'Zuluftventile', zentralgerat, volumenstrom, zuluftventile)
            pakete += zuluftventile
        } catch (e) {
        }
        // Raumvolumenströme, Überströmelemente, m=[Überströmelement:Anzahl]
        List uberstromventile = null
        try {
            // WAC-244
            Map<String, Integer> ub = ventplanModelService.countUberstromelemente(map)
            uberstromventile = ub.collect() {
                Map a = ventplanModelService.getArtikel(it.key)
                a.ANZAHL = (double) it.value
                a
            }.flatten()
        } catch (e) {
        }
        //
        /*
        println String.format("%17s für %8s (Vs=%d) sind %s", 'Gesamte Pakete', zentralgerat, volumenstrom, pakete)
        println "${this}"
        println "HOLE ARTIKEL FÜR JEDES PAKET"
        println "============================"
        */
        // ArrayList can contain a hole, like element 9 is set, 10 is null, 11 is set
        pakete?.sort { p -> p?.REIHENFOLGE }?.each { p ->
            if (p) {
                ventplanModelService.paketeZuStuckliste([p.ID]).each { st ->
                    artikelAufStuckliste(stuckliste, st, p)
                }
            }
        }
        uberstromventile?.each { st ->
            artikelAufStuckliste(stuckliste, st)
        }
        // WAC-231 Sprungmengen
        stuckliste.each { Map.Entry st ->
            String artikel = st.key
            GroovyRowResult r = (GroovyRowResult) st.value
            if (r.MENGENEINHEIT && r.LIEFERMENGE) {
                if (r.LIEFERMENGE > 1.0d) {
                    double richtig = Math.ceil(r.ANZAHL / r.LIEFERMENGE)
//                    println "${artikel} ==> ${r.LIEFERMENGE} x ${r.MENGENEINHEIT}: ${r.ANZAHL} -> ${richtig}"
                    r.ANZAHL = richtig
                }
            }
        }
        return stuckliste
    }

}

/*
 * WAC
 *
 * Copyright (C) 2005      Informationssysteme Ralf Bensmann.
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

/**
 *
 */
class StucklisteService {

    private static boolean DEBUG = false
    /*
    def sql
    def withSql(closure) {
        closure('default', sql)
    }
    */

    /**
     * Get value from JDBC result, e.g. take care of CLOB.
     * @param value
     */
    def getVal(value) {
        switch (value) {
            case { it instanceof java.sql.Clob }:
                value.asciiStream.getText('UTF-8')
                break
            default:
                value
        }
    }

    /**
     * Finde passenden Volumenstrom (in Datenbank) zu tatsächlichem, errechnetem Volumenstrom.
     * @param zentralgerat
     * @param volumenstrom
     * @param bedingung
     * @return
     */
    Integer getVolumenstrom(String zentralgerat, Integer volumenstrom) {
        // Check arguments
        if (null == zentralgerat || null == volumenstrom) {
            throw new IllegalStateException('Zentralgerät, Volumenstrom fehlt!')
        }
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT maxvolumenstrom' <<
                ' FROM pakete' <<
                ' WHERE geraet = ?.gerat AND maxvolumenstrom >= ?.maxvolumenstrom' <<
                ' ORDER BY maxvolumenstrom ASC'
        if (DEBUG)
            println "${this}.getVolumenstrom: statement=${statement.toString()}"
        def r_maxvs = withSql { dataSourceName, sql ->
            sql.rows(statement.toString(), [gerat: zentralgerat, maxvolumenstrom: volumenstrom])
        }
        if (DEBUG)
            println "${this}.getVolumenstrom: " + r_maxvs[0].maxvolumenstrom
        r_maxvs[0].MAXVOLUMENSTROM
    }

    /**
     * Finde das Grundpaket für ein Zentralgerät (Kategorie 73).
     * @param zentralgerat
     * @param luftart Optional: ZU, AB
     * @return Liste mit IDs aus PAKETE.ID
     */
    List getGrundpaket(String zentralgerat, String luftart = null) {
        // Check arguments
        if (null == zentralgerat) {
            throw new IllegalStateException('Zentralgerät fehlt!')
        }
        // 1 x Grundpaket, unabhängig vom Volumenstrom
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT id' <<
                '  FROM pakete' <<
                ' WHERE geraet = ?.gerat AND kategorie = ?.kategorie'
        if (DEBUG)
            println "${this}.getGrundpaket: statement=${statement.toString()}"
        List r73 = withSql { dataSourceName, sql -> sql.rows(statement.toString(), [gerat: zentralgerat, kategorie: 73]) }
        if (DEBUG)
            println "${this}.getGrundpaket(${zentralgerat}): grundpakete=${r73*.id}"
        r73*.id
    }

    /**
     * Findet ein Erweiterungspaket für ein Zentralgerät.
     * @param zentralgerat
     * @param volumenstrom Tatsächlicher, berechneter Volumenstrom (nicht Wert in Datenbank).
     * @param luftart Optional: ZU, AB
     * @return Liste mit IDs aus PAKETE.KATEGORIE
     */
    List getErweiterungspaket(String zentralgerat, Integer volumenstrom, String luftart = null) {
        // Check arguments
        if (null == zentralgerat || null == volumenstrom) {
            throw new IllegalStateException('Zentralgerät, Volumenstrom fehlt!')
        }
        // Bestimme passenden Volumenstrom in Datenbank
        Integer maxvs = getVolumenstrom(zentralgerat, volumenstrom)
        //
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT id, kategorie, name' <<
                ' FROM pakete' <<
                ' WHERE geraet = ?.gerat AND maxvolumenstrom = ?.maxvolumenstrom AND kategorie = ?.kategorie'
        if (DEBUG)
            println "${this}.getErweiterungspaket: statement=${statement.toString()}"
        def r = withSql { dataSourceName, sql ->
            sql.rows(statement.toString(), [gerat: zentralgerat, maxvolumenstrom: maxvs, kategorie: 74])
        }
        if (DEBUG)
            println "${this}.getErweiterungspaket: " + r*.id
        return r*.id
    }

    /**
     * Findet ein Gerätepaket für ein bestimmtes Zentralgerät/Volumenstrom.
     * @param zentralgerat
     * @param volumenstrom Tatsächlicher, berechneter Volumenstrom (nicht Wert in Datenbank).
     * @return
     */
    List getGeratepaket(String zentralgerat, Integer volumenstrom) {
        // Check arguments
        if (null == zentralgerat || null == volumenstrom) {
            throw new IllegalStateException('Zentralgerät, Volumenstrom fehlt!')
        }
        // Bestimme passenden Volumenstrom in Datenbank
        Integer maxvs = getVolumenstrom(zentralgerat, volumenstrom)
        //
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT id, name' <<
                ' FROM pakete' <<
                ' WHERE geraet = ?.gerat AND maxvolumenstrom = ?.maxvolumenstrom AND kategorie = ?.kategorie'
        if (DEBUG)
            println "${this}.getGeratePaket: statement=${statement.toString()}"
        def r = withSql { dataSourceName, sql ->
            sql.rows(statement.toString(), [gerat: zentralgerat, maxvolumenstrom: maxvs, kategorie: 72])
        }
        if (DEBUG)
            println "${this}.getGeratePaket: " + r*.id
        return r*.id
    }

    /**
     * Findet ein Aussenluftpaket für ein bestimmtes Zentralgerät.
     * @param zentralgerat
     * @param volumenstrom Tatsächlicher, berechneter Volumenstrom (nicht Wert in Datenbank).
     * @param bedingung Wand, Dach, EWT
     * @return
     */
    List getAussenluftpaket(String zentralgerat, Integer volumenstrom, String bedingung) {
        // Check arguments
        if (null == zentralgerat || null == volumenstrom || null == bedingung) {
            throw new IllegalStateException('Zentralgerät, Volumenstrom, Bedingung fehlt!')
        }
        /* Bestimme passenden Volumenstrom in Datenbank
        Integer maxvs = getVolumenstrom(zentralgerat, volumenstrom)
        */
        //
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT id, name' <<
                ' FROM pakete' <<
                ' WHERE geraet = ?.gerat AND bedingung = ?.bedingung AND kategorie = ?.kategorie'
        if (DEBUG)
            println "${this}.getAussenluftpaket: statement=${statement.toString()}"
        def r = withSql { dataSourceName, sql ->
            sql.rows(statement.toString(), [gerat: zentralgerat, bedingung: bedingung, kategorie: 70])
        }
        if (DEBUG)
            println "${this}.getAussenluftpaket: " + r*.id
        return r*.id
    }

    /**
     * Findet ein Fortluftpaket für ein bestimmtes Zentralgerät.
     * @param zentralgerat
     * @param volumenstrom Tatsächlicher, berechneter Volumenstrom (nicht Wert in Datenbank).
     * @param bedingung Wand, Dach
     * @return
     */
    List getFortluftpaket(String zentralgerat, Integer volumenstrom, String bedingung) {
        // Check arguments
        if (null == zentralgerat || null == volumenstrom || null == bedingung) {
            throw new IllegalStateException('Zentralgerät, Volumenstrom, Bedingung fehlt!')
        }
        /* Bestimme passenden Volumenstrom in Datenbank
        Integer maxvs = getVolumenstrom(zentralgerat, volumenstrom)
        */
        //
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT id, name' <<
                ' FROM pakete' <<
                ' WHERE geraet = ?.gerat AND bedingung = ?.bedingung AND kategorie = ?.kategorie'
        if (DEBUG)
            println "${this}.getAussenluftpaket: statement=${statement.toString()}"
        def r = withSql { dataSourceName, sql ->
            sql.rows(statement.toString(), [gerat: zentralgerat, bedingung: bedingung, kategorie: 71])
        }
        if (DEBUG)
            println "${this}.getAussenluftpaket: " + r*.id
        return r*.id
    }

    /**
     * Findet ein Luftauslasspaket (für einen bestimmtes Ventil).
     * @param luftauslass Artikelnummer (z.B. 100ULC)
     * @param bedingung AB, ZU
     * @return
     */
    List getLuftauslasspaket(String luftauslass, String bedingung) {
        // Check arguments
        if (null == luftauslass || null == bedingung) {
            throw new IllegalStateException('Luftauslass oder Bedingung fehlt!')
        }
        //
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT id, name' <<
                ' FROM pakete' << // Feld NAME und GERAET hat in diesem Fall den gleichen Inhalt, den Luftauslass
                ' WHERE name = ?.name AND bedingung = ?.bedingung AND kategorie = ?.kategorie'
        if (DEBUG)
            println "${this}.getLuftauslasspaket: statement=${statement.toString()}"
        def r = withSql { dataSourceName, sql ->
            sql.rows(statement.toString(), [name: luftauslass, bedingung: bedingung, kategorie: 76])
        }
        if (DEBUG) {
            println "${this}.getVerteilpaket: " + r*.id
        }
        return r*.id
    }

    /**
     * Bestimme alle Ebenen.
     * @param map
     * @return List < String >
     */
    List<String> getEbenen(Map map) {
        // Check argument
        //if (!)
        map.raum.raume.grep { r -> r.raumGeschoss }.groupBy { r -> r.raumGeschoss }*.key
    }

    /**
     * Bestimme die Anzahl für jedes Abluftventil in allen Räumen.
     * @param map
     * @return Map [Abluftventil:Anzahl]
     */
    Map<String, Integer> getAbluftventile(Map map) {
        map.raum.raume.grep { r -> r.raumBezeichnungAbluftventile }.groupBy { r ->
            r.raumLuftart + '-' + r.raumBezeichnungAbluftventile
        }/*.grep { it.key }*/.inject [:], { o, n ->
            o[n.key] = n.value.size()
            return o
        }
    }

    /**
     * Bestimme die Anzahl für jedes Zuluftventil in allen Räumen.
     * @param map
     * @return Map [Zuluftventil:Anzahl]
     */
    Map<String, Integer> getZuluftventile(Map map) {
        map.raum.raume.grep { r -> r.raumBezeichnungZuluftventile }.groupBy { r ->
            r.raumLuftart + '-' + r.raumBezeichnungZuluftventile
        }/*.grep { it.key }*/.inject [:], { o, n ->
            o[n.key] = n.value.size()
            return o
        }
    }

    /**
     * Bestimme die Anzahl für jedes Überströmelement in allen Räumen.
     * @param map
     * @return Map [Zuluftventil:Anzahl]
     */
    Map<String, Integer> getUberstromelemente(Map map) {
        map.raum.raume.grep { r -> r.raumUberstromElement }.groupBy { r ->
            r.raumUberstromElement
        }/*.grep { it.key }*/.inject [:], { o, n ->
            o[n.key] = n.value.size()
            return o
        }
    }

    /**
     * @param artikelnummer
     */
    def getArtikel(artikelnummer) {
        // Check arguments
        if (null == artikelnummer) {
            throw new IllegalStateException('Keine Artikelnummer angegeben!')
        }
        // JOIN pakete -> stuckliste
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT a.artikelnummer, a.artikelbezeichnung, 1.0 anzahl, 200 reihenfolge, a.mengeneinheit, a.preis' <<
                '  FROM artikelstamm a' <<
                ' WHERE artikelnummer = ?.artikelnummer'
        def r = withSql { dataSourceName, sql ->
            sql.firstRow(statement.toString(), [artikelnummer: artikelnummer])
        }
        r.each { k, v -> r[k] = getVal(v) }
        if (DEBUG)
            println "${this}.artikel(${artikelnummer}): " + r.dump()
        return r
    }

    /**
     * Artikelnummer, Datensatz ist entweder aus Tabelle ARTIKEL oder STUECKLISTE
     * @param artikel
     * @return
     */
    String getArtikelnummer(artikel) {
        try { // STUECKLISTE
            artikel.ARTIKEL
        } catch (groovy.lang.MissingPropertyException e) { // ARTIKEL
            artikel.ARTIKELNUMMER
        }
    }

    /**
     *
     * @param pakete
     * @return
     */
    List paketeZuStuckliste(List<Integer> pakete) {
        // Check arguments
        if (null == pakete) {
            throw new IllegalStateException('Kein(e) Paket(e) angegeben!')
        }
        // JOIN pakete -> stuckliste
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT s.reihenfolge, s.luftart, SUM(s.anzahl) anzahl, a.mengeneinheit, s.artikel, a.artikelbezeichnung, a.preis' <<
                '  FROM stueckliste s' <<
                ' INNER JOIN artikelstamm a ON s.artikel = a.artikelnummer' <<
                ' WHERE paket IN (' << pakete.join(', ') << ')' <<
                ' GROUP BY s.reihenfolge, s.artikel, s.luftart' <<
                ' ORDER BY s.luftart, s.reihenfolge'
        def r = withSql { dataSourceName, sql ->
            sql.rows(statement.toString())
        }
        r.each { row ->
            row.each { k, v -> row[k] = getVal(v) }
        }
        if (DEBUG)
            println "${this}.getStuckliste(${pakete}): " + r.dump()
        return r
    }

    /**
     * Füge einen Artikel zur Stückliste hinzu.
     * @param stuckliste Map
     * @param artikel groovy.sql.GroovyRowResult
     * @param paket
     */
    void artikelAufStuckliste(Map stuckliste, artikel, paket = null) {
        if (null == stuckliste || null == artikel) {
            throw new IllegalArgumentException('stuckliste or artikel == null?')
        }
        def artikelnummer = getArtikelnummer(artikel)
        if (stuckliste.containsKey(artikelnummer)) {
            def alt = stuckliste[artikelnummer].ANZAHL
            stuckliste[artikelnummer].ANZAHL += artikel.ANZAHL ?: 1.0
            println String.format('+      Artikel hinzu: Paket=%5s Artikel=%17s Anzahl=%4.1f (%4.1f + %4.1f)',
                    paket ?: '', artikelnummer, stuckliste[artikelnummer].ANZAHL, artikel.ANZAHL, alt)
        } else {
            stuckliste[artikelnummer] = artikel
            println String.format('* Füge Artikel hinzu: Paket=%5s Artikel=%17s Anzahl=%4.1f', paket ?: '', artikelnummer, artikel.ANZAHL)
        }
    }

    /**
     * Erstelle Ergebnis (sortiert etc.)
     * @param stuckliste
     */
    Map makeResult(Map stuckliste) {
        stuckliste.sort { it.value.REIHENFOLGE }
    }

    /**
     * Zeige Inhalt der Stückliste formatiert an.
     * @param stuckliste
     */
    void dumpStuckliste(Map stuckliste) {
        println ''
        println 'GESAMTLISTE DER ARTIKEL'
        println '======================='
        println String.format('%2s %12s %7s %7s %17s - %s', 'NR', 'REIHENFOLGE', 'ANZAHL', 'ME', 'ARTIKELNUMMER', 'ARTIKELBEZEICHNUNG')
        println '----------------------------------------------------------------------'
        makeResult(stuckliste).eachWithIndex { it, i ->
            def artikel = it.value
            int reihenfolge = (int) artikel.REIHENFOLGE
            double anzahl = (double) artikel.ANZAHL
            String artikelnummer = getArtikelnummer(artikel)
            println String.format('%2d % 12d % 7.1f %6s %17s - %s', i + 1, reihenfolge, anzahl, '!' + artikel.MENGENEINHEIT + '!', artikelnummer, artikel.ARTIKELBEZEICHNUNG)
        }
    }

    /**
     *
     * @param model Our model.
     * @return Map Die Stückliste.
     */
    Map processData(Map map) {
        println "========"
        println "BEISPIEL"
        println "Mömbris"
        println "========"
        def pakete = []
        Map stuckliste = [:]
        String zentralgerat = '300WAC'
        Integer volumenstrom = 170
        // Grundpaket
        List grundpaket = getGrundpaket(zentralgerat)
        println String.format("%17s für %8s (Vs=%d) ist %s", 'Grundpaket', zentralgerat, volumenstrom, grundpaket)
        pakete += grundpaket
        // Gerätepaket
        List geratepaket = getGeratepaket(zentralgerat, volumenstrom)
        pakete += geratepaket
        println String.format("%17s für %8s (Vs=%d) ist %s", 'Geraetepaket', zentralgerat, volumenstrom, geratepaket)
        // Ebenen
        List ebenen = getEbenen(map)
        // Erweiterungspaket für alle Ebenen außer die Erste
        List erwei = getErweiterungspaket(zentralgerat, volumenstrom)
        println String.format("%17s für %8s (Vs=%d) sind %s", 'Ebenen', zentralgerat, volumenstrom, ebenen.join(', '))
        1.upto ebenen.size() - 1, {
            println String.format("%17s für %8s (Vs=%d), %s für Ebene(n) %s", 'Erweiterungspaket', zentralgerat, volumenstrom, erwei, ebenen[it])
            pakete += erwei
        }
        // Außenluftpaket
        List aussenluftpaket = getAussenluftpaket('300WAC', volumenstrom, 'Wand')
        println String.format("%17s für %8s (Vs=%d), %s ist %s", 'Aussenluftpaket', zentralgerat, volumenstrom, 'Wand', aussenluftpaket)
        pakete += aussenluftpaket
        // Fortluftpaket
        List fortluftpaket = getFortluftpaket('300WAC', volumenstrom, 'Dach')
        println String.format("%17s für %8s (Vs=%d), %s ist %s", 'Fortluftpaket', zentralgerat, volumenstrom, 'Dach', fortluftpaket)
        pakete += fortluftpaket
        // Luftauslasspakete
        // Raumvolumenströme, Abluftventile, m=[Luftauslass:Anzahl]
        Map abluftventile = getAbluftventile(map)
        // Raumvolumenströme, Zuluftventile, m=[Luftauslass:Anzahl]
        Map zuluftventile = getZuluftventile(map)
        // Raumvolumenströme, Überströmelemente, m=[Übertrömelement:Anzahl]
        Map uberstromventile = getUberstromelemente(map)
        println String.format("%17s für %8s (Vs=%d), %s", 'Abluftventile', zentralgerat, volumenstrom, abluftventile.collect { "${it.value} x ${it.key - 'AB-'}" }.join(', '))
        println String.format("%17s für %8s (Vs=%d), %s", 'Zuluftventile', zentralgerat, volumenstrom, zuluftventile.collect { "${it.value} x ${it.key - 'ZU-'}" }.join(', '))
        println String.format("%17s für %8s (Vs=%d), %s", 'Uberstromventile', zentralgerat, volumenstrom, uberstromventile.collect { "${it.value} x ${it.key}" }.join(', '))
        println String.format("%17s für %8s (Vs=%d) sind %s", 'Gesamte Pakete', zentralgerat, volumenstrom, pakete)
        println ""
        println "HOLE ARTIKEL FÜR JEDES PAKET"
        println "============================"
        pakete.flatten().each { p ->
            paketeZuStuckliste([p]).each { st ->
                artikelAufStuckliste(stuckliste, st, p)
            }
        }
        println ""
        println "WEITERE ARTIKEL"
        println "==============="
        def ventile = [zuluftventile, abluftventile, uberstromventile].inject [:], { o, n ->
            n.each {
                def k = it.key - ~/.*-/
                o.containsKey(k) ? (o[k] += it.value) : (o[k] = it.value)
            }
            o
        }
        ventile.each { ventil ->
            def artikel = getArtikel(ventil.key)
            if (artikel) {
                artikel.ANZAHL = (double) ventil.value
                println "${ventil} -> ${artikel}"
                artikelAufStuckliste(stuckliste, artikel)
            } else {
                println "Artikel ${artikelnummer} nicht gefunden!"
            }
        }
        return stuckliste
    }

}

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

import groovy.sql.Sql

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
     * Bestimme alle Ebenen.
     * @param map
     * @return List < String >
     */
    List<String> getVerteilebenen(Map map) {
        // Check argument
        if (null == map) {
            throw new IllegalArgumentException('Map fehlt!')
        }
        map.raum.raume.grep { r -> r.raumVerteilebene }.groupBy { r -> r.raumVerteilebene }*.key
    }

    /**
     * Zähle die Anzahl von Ventilen pro Verteilebene und Luftart.
     * @param map Eine Map wie im Model: map.raum
     * @param luftart Eins von: 'Ab', 'Zu'
     * @return Map<String,Integer> [Ab/Zuluftventil:Anzahl]
     */
    Map<String, Integer> countVentileProVerteilebene(List<Map> map, String luftart) {
        // Check argument
        if (null == map) {
            throw new IllegalArgumentException('Map fehlt!')
        }
        /*
        raumBezeichnungAbluftventile:'',
        raumAnzahlAbluftventile:0,
        raumBezeichnungZuluftventile:'100ULC',
        raumAnzahlZuluftventile:2.0,
        raumVerteilebene:'DG',
        raumAnzahlUberstromVentile:0,
        raumUberstromElement:'',
        */
        map.grep { r -> r."raumBezeichnung${luftart}luftventile" }.inject [:], { Map o, Map n ->
            //println n."raumAnzahl${luftart}luftventile" + ' x ' + n."raumBezeichnung${luftart}luftventile"
            if (o.containsKey(n."raumBezeichnung${luftart}luftventile")) {
                o[n."raumBezeichnung${luftart}luftventile"] += (int) n."raumAnzahl${luftart}luftventile"
            } else {
                o[n."raumBezeichnung${luftart}luftventile"] = (int) n."raumAnzahl${luftart}luftventile"
            }
            o
        }
    }

    /**
     * Bestimme die Anzahl für jedes Abluftventil in allen Räumen.
     * @param map
     * @return Map < String , Integer >  [Abluftventil:Anzahl]
     */
    Map<String, Integer> countAbluftventile(Map map) {
        map.raum.raume.grep { r ->
            r.raumBezeichnungAbluftventile
        }.inject [:], { Map o, Map n ->
            String k = n.raumBezeichnungAbluftventile
            int v = n.raumAnzahlAbluftventile
            o.containsKey(k) ? (o[k] += v) : (o[k] = v)
            return o
        }
    }

    /**
     * Bestimme die Anzahl für jedes Zuluftventil in allen Räumen.
     * @param map
     * @return Map < String , Integer >  [Zuluftventil:Anzahl]
     */
    Map<String, Integer> countZuluftventile(Map map) {
        map.raum.raume.grep { r ->
            r.raumBezeichnungZuluftventile
        }.inject [:], { Map o, Map n ->
            String k = n.raumBezeichnungZuluftventile
            int v = n.raumAnzahlZuluftventile
            o.containsKey(k) ? (o[k] += v) : (o[k] = v)
            return o
        }
    }

    /**
     * Bestimme die Anzahl für jedes Überströmelement in allen Räumen.
     * @param map
     * @return Map < String , Integer >  [Zuluftventil:Anzahl]
     */
    Map<String, Integer> countUberstromelemente(Map map) {
        map.raum.raume.grep { r ->
            r.raumUberstromElement
        }.inject [:], { Map o, Map n ->
            String k = n.raumUberstromElement
            int v = n.raumAnzahlUberstromVentile
            o.containsKey(k) ? (o[k] += v) : (o[k] = v)
            return o
        }
    }

    /**
     * @param artikelnummer
     * @return Map
     */
    Map getArtikel(artikelnummer) {
        // Check arguments
        if (null == artikelnummer) {
            throw new IllegalStateException('Keine Artikelnummer angegeben!')
        }
        // JOIN pakete -> stuckliste
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT a.artikelnummer, a.artikelbezeichnung, 1.0 ANZAHL, 900 REIHENFOLGE, a.mengeneinheit, a.liefermenge, a.preis' <<
                '  FROM artikelstamm a' <<
                ' WHERE a.artikelnummer = ?.artikelnummer'
        def r = withSql { dataSourceName, sql ->
            sql.firstRow(statement.toString(), [artikelnummer: artikelnummer])
        }
        r.each { k, v -> r[k] = getVal(v) }
        /*if (DEBUG)
            println "${this}.getArtikel(${artikelnummer}): " + r.dump()*/
        return r
    }

    /**
     * @param artikelnummer
     * @return Map
     */
    List findArtikel(text) {
        // Check arguments
        if (null == text) {
            throw new IllegalStateException('Kein Text angegeben!')
        }
        text = '%' + text.value + '%'
        // JOIN pakete -> stuckliste
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT a.artikelnummer, a.artikelbezeichnung, 1.0 ANZAHL, 900 REIHENFOLGE, a.mengeneinheit, a.liefermenge, a.preis' <<
                '  FROM artikelstamm a' <<
                ' WHERE a.artikelnummer like ?.text' <<
                '    OR a.artikelbezeichnung like ?.text'
        def r = withSql { dataSourceName, sql ->
            sql.rows(statement.toString(), [text: text])
        }

        r.each { row ->
            row.each { k, v -> row[k] = getVal(v) }
        }
        /*if (DEBUG)
            println "${this}.findArtikel(${text}): " + r.dump()*/
        return r
    }

    /**
     * Artikelnummer, Datensatz ist entweder aus Tabelle ARTIKEL oder STUECKLISTE
     * @param artikel Map
     * @return String Die Artikelnummer
     */
    String getArtikelnummer(Map artikel) {
        try { // STUECKLISTE
            artikel.ARTIKEL
        } catch (groovy.lang.MissingPropertyException e) { // ARTIKEL
            artikel.ARTIKELNUMMER
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
        /*if (DEBUG)
            println "${this}.getVolumenstrom: statement=${statement.toString()}"*/
        def r_maxvs = withSql { dataSourceName, sql ->
            sql.rows(statement.toString(), [gerat: zentralgerat, maxvolumenstrom: volumenstrom])
        }
        /*if (DEBUG)
            println "${this}.getVolumenstrom: " + r_maxvs[0].maxvolumenstrom*/
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
        /*if (DEBUG)
            println "${this}.getGrundpaket(${zentralgerat}): grundpakete=${r73*.id}"*/
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
        /*if (DEBUG)
            println "${this}.getErweiterungspaket: " + r*.id*/
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
        /*if (DEBUG)
            println "${this}.getGeratePaket: " + r*.id*/
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
        /*if (DEBUG)
            println "${this}.getAussenluftpaket: " + r*.id*/
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
        /*if (DEBUG)
            println "${this}.getAussenluftpaket: " + r*.id*/
        return r*.id
    }

    /**
     * Errechnet benötigte Verteilpakete anhand der Anzahl der Ab/Zuluftventile pro Verteilebene.
     * @param map Eine Map wie im Model: map.raum
     * @return
     */
    Map<String, Map<String, Integer>> getVerteilpakete(Map map) {
        Map<String, Map<String, Integer>> verteilpakete = [:]
        // SQL statement
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT id, name' <<
                ' FROM pakete' <<
                ' WHERE kategorie = ?.kategorie AND bedingung >= ?.bedingung' <<
                ' ORDER BY bedingung ASC'
        if (DEBUG) {
            println "${this}.getVerteilpakete: statement=${statement.toString()}"
        }
        //
        getVerteilebenen(map).each { e ->
            if (!verteilpakete.containsKey(e)) {
                verteilpakete[e] = ['AB': ['anzahl': 0, 'paket': 0], 'ZU': ['anzahl': 0, 'paket': 0]]
            }
            def raumeAufEbene = map.raum.raume.grep { r -> r.raumVerteilebene.equals(e) }
            // Errechne Anzahl aller Abluftventile und hole Paket
            verteilpakete[e]['AB']['anzahl'] = countVentileProVerteilebene(raumeAufEbene, 'Ab').inject 0, { int o, n -> o + (int) n.value }
            verteilpakete[e]['AB']['paket'] = withSql { dataSourceName, Sql sql ->
                sql.firstRow(statement.toString(), [bedingung: verteilpakete[e]['AB']['anzahl'], kategorie: 75])
            }.ID
            // Errechne Anzahl aller Zuluftventile und hole Paket
            verteilpakete[e]['ZU']['anzahl'] = countVentileProVerteilebene(raumeAufEbene, 'Zu').inject 0, { int o, n -> o + (int) n.value }
            verteilpakete[e]['ZU']['paket'] = withSql { dataSourceName, Sql sql ->
                sql.firstRow(statement.toString(), [bedingung: verteilpakete[e]['ZU']['anzahl'], kategorie: 75])
            }.ID
        }
        //
        return verteilpakete
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
        /*if (DEBUG) {
            println "${this}.getLuftauslasspaket: " + r*.id
        }*/
        return r*.id
    }

    /**
     * Errechnet benötigte Luftauslasspakete anhand der Ab/Zuluftventile, Überströmelemente.
     * @param map Eine Map wie im Model: map.raum
     * @return
     */
    Map<String, Integer> getLuftauslasspakete(Map map) {
        // SQL statement
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT id, name' <<
                ' FROM pakete' <<
                ' WHERE kategorie = ?.kategorie AND bedingung >= ?.bedingung' <<
                ' ORDER BY bedingung ASC'
        if (DEBUG) {
            println "${this}.getLuftauslasspakete: statement=${statement.toString()}"
        }
        withSql { dataSourceName, Sql sql ->
            sql.firstRow(statement.toString(), [bedingung: luftauslass, kategorie: 76])
        }.ID
    }

    /**
     * Hole alle Artikel zu einer Menge an Paketen.
     * @param pakete
     * @return List Alle Artikel zu den Paketen.
     */
    List paketeZuStuckliste(List<Integer> pakete) {
        // Check arguments
        if (null == pakete) {
            throw new IllegalStateException('Kein(e) Paket(e) angegeben!')
        }
        // JOIN pakete -> stuckliste
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT s.reihenfolge, s.luftart, SUM(s.anzahl) ANZAHL, a.mengeneinheit, a.liefermenge, s.artikel, a.artikelbezeichnung, a.preis' <<
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
            println "${this}.paketeZuStuckliste(${pakete}): " + r.dump()
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
            if (DEBUG) {
                println String.format('+      Artikel hinzu: Paket=%5s Artikel=%17s Anzahl=%4.1f (%4.1f + %4.1f)',
                        paket ?: '', artikelnummer, stuckliste[artikelnummer].ANZAHL, artikel.ANZAHL, alt)
            }
        } else {
            stuckliste[artikelnummer] = artikel
            if (DEBUG) {
                println String.format('* Füge Artikel hinzu: Paket=%5s Artikel=%17s Anzahl=%4.1f', paket ?: '', artikelnummer, artikel.ANZAHL)
            }
        }
    }

    /**
     * Erstelle Ergebnis (sortiert etc.) aus einer Stückliste (siehe paketeZuStuckliste, artikelAufStuckliste).
     * @param stuckliste
     */
    Map makeResult(Map stuckliste) {
        stuckliste.sort { k, v ->
            println "makeResult -> ${v.dump()}"
            /*it.value*/v.REIHENFOLGE
        }
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
            String artikelnummer = getArtikelnummer(artikel)
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
        String zentralgerat = '300WAC'
        Integer volumenstrom = 170
        // Grundpaket
        List grundpaket = getGrundpaket(zentralgerat)
        if (DEBUG) println String.format("%17s für %8s (Vs=%d) ist %s", 'Grundpaket', zentralgerat, volumenstrom, grundpaket)
        pakete += grundpaket
        // Gerätepaket
        List geratepaket = getGeratepaket(zentralgerat, volumenstrom)
        pakete += geratepaket
        if (DEBUG) println String.format("%17s für %8s (Vs=%d) ist %s", 'Geraetepaket', zentralgerat, volumenstrom, geratepaket)
        // Erweiterungspaket für alle Ebenen außer die Erste
        List erwei = getErweiterungspaket(zentralgerat, volumenstrom)
        // Ebenen
        List verteilebenen = getVerteilebenen(map)
        int anzahlVerteilebenen = verteilebenen.size() - 1
        if (anzahlVerteilebenen > 0) {
            if (DEBUG) println String.format("%17s für %8s (Vs=%d) sind %s", 'Verteilbenen', zentralgerat, volumenstrom, verteilebenen.join(', '))
            1.upto anzahlVerteilebenen, {
                if (DEBUG) {
                    println String.format("%17s für %8s (Vs=%d), %s für Ebene(n) %s", 'Erweiterungspaket', zentralgerat, volumenstrom, erwei, verteilebenen[it])
                }
                pakete += erwei
            }
        }
        // Außenluftpaket
        List aussenluftpaket = getAussenluftpaket('300WAC', volumenstrom, 'Wand')
        if (DEBUG) println String.format("%17s für %8s (Vs=%d), %s ist %s", 'Aussenluftpaket', zentralgerat, volumenstrom, 'Wand', aussenluftpaket)
        pakete += aussenluftpaket
        // Fortluftpaket
        List fortluftpaket = getFortluftpaket('300WAC', volumenstrom, 'Dach')
        if (DEBUG) println String.format("%17s für %8s (Vs=%d), %s ist %s", 'Fortluftpaket', zentralgerat, volumenstrom, 'Dach', fortluftpaket)
        pakete += fortluftpaket
        // Verteilpakete
        def _verteilpakete = getVerteilpakete(map)
        def verteilpakete = _verteilpakete*.value['AB']['paket'] + _verteilpakete*.value['ZU']['paket']
        if (DEBUG) println String.format("%17s für %8s (Vs=%d), sind %s", 'Verteilpakete', zentralgerat, volumenstrom, verteilpakete)
        pakete += verteilpakete
        // Luftauslässe
        List abluftventile = countAbluftventile(map).collect {
            getLuftauslasspaket(it.key, 'AB') * it.value
        }.flatten()
        pakete += abluftventile
        if (DEBUG) println String.format("%17s für %8s (Vs=%d), %s", 'Abluftventile', zentralgerat, volumenstrom, abluftventile)
        List zuluftventile = countZuluftventile(map).collect {
            getLuftauslasspaket(it.key, 'ZU') * it.value // TODO 'AB' nur, da 100ULC in DB mit AB steht
        }.flatten()
        pakete += zuluftventile
        if (DEBUG) println String.format("%17s für %8s (Vs=%d), %s", 'Zuluftventile', zentralgerat, volumenstrom, zuluftventile)
        // Raumvolumenströme, Überströmelemente, m=[Übertrömelement:Anzahl]
        Map<String, Integer> uberstromventile = countUberstromelemente(map)
        //
        if (DEBUG) {
            println String.format("%17s für %8s (Vs=%d) sind %s", 'Gesamte Pakete', zentralgerat, volumenstrom, pakete)
            println "${this}"
            println "HOLE ARTIKEL FÜR JEDES PAKET"
            println "============================"
        }
        pakete.each { p ->
            paketeZuStuckliste([p]).each { st ->
                artikelAufStuckliste(stuckliste, st, p)
            }
        }
        return stuckliste
    }

}

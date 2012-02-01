/*
 * WAC
 *
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

/**
 *
 */
class AngebotService {

    private static boolean DEBUG = false
    def sql

    def withSql(closure) {
        closure(sql)
    }

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
        if (DEBUG) println "${this}.getVolumenstrom: statement=${statement.toString()}"
        def r_maxvs = withSql { sql ->
            sql.rows(statement.toString(), [gerat: zentralgerat, maxvolumenstrom: volumenstrom])
        }
        if (DEBUG) println "${this}.getVolumenstrom: " + r_maxvs[0].maxvolumenstrom
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
        if (DEBUG) println "${this}.getGrundpaket: statement=${statement.toString()}"
        List r73 = withSql { sql -> sql.rows(statement.toString(), [gerat: zentralgerat, kategorie: 73]) }
        if (DEBUG) println "${this}.getGrundpaket(${zentralgerat}): grundpakete=${r73*.id}"
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
        if (DEBUG) println "${this}.getErweiterungspaket: statement=${statement.toString()}"
        def r = withSql { sql ->
            sql.rows(statement.toString(), [gerat: zentralgerat, maxvolumenstrom: maxvs, kategorie: 74])
        }
        if (DEBUG) println "${this}.getErweiterungspaket: " + r*.id
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
        if (DEBUG) println "${this}.getGeratePaket: statement=${statement.toString()}"
        def r = withSql { sql ->
            sql.rows(statement.toString(), [gerat: zentralgerat, maxvolumenstrom: maxvs, kategorie: 72])
        }
        if (DEBUG) println "${this}.getGeratePaket: " + r*.id
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
        if (DEBUG) println "${this}.getAussenluftpaket: statement=${statement.toString()}"
        def r = withSql { sql ->
            sql.rows(statement.toString(), [gerat: zentralgerat, bedingung: bedingung, kategorie: 70])
        }
        if (DEBUG) println "${this}.getAussenluftpaket: " + r*.id
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
        if (DEBUG) println "${this}.getAussenluftpaket: statement=${statement.toString()}"
        def r = withSql { sql ->
            sql.rows(statement.toString(), [gerat: zentralgerat, bedingung: bedingung, kategorie: 71])
        }
        if (DEBUG) println "${this}.getAussenluftpaket: " + r*.id
        return r*.id
    }

    /**
     * @param artikelnummer
     */
    def artikel(artikelnummer) {
        // Check arguments
        if (null == artikelnummer) {
            throw new IllegalStateException('Keine Artikelnummer angegeben!')
        }
        // JOIN pakete -> stuckliste
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT a.artikelnummer, a.artikelbezeichnung, 1.0 anzahl, 200 reihenfolge, a.mengeneinheit' <<
                     '  FROM artikelstamm a' <<
                     ' WHERE artikelnummer = ?.artikelnummer'
        def r = withSql { sql ->
            sql.firstRow(statement.toString(), [artikelnummer: artikelnummer])
        }
        r.each { k, v -> r[k] = getVal(v) }
        if (DEBUG) println "${this}.artikel(${artikelnummer}): " + r.dump()
        return r
    }

    /**
     *
     * @param pakete
     * @return
     */
    List getStuckliste(List<Integer> pakete) {
        // Check arguments
        if (null == pakete) {
            throw new IllegalStateException('Kein(e) Paket(e) angegeben!')
        }
        // JOIN pakete -> stuckliste
        StringBuilder statement = new StringBuilder()
        statement << 'SELECT s.reihenfolge, s.luftart, SUM(s.anzahl) anzahl, a.mengeneinheit, s.artikel, a.artikelbezeichnung' <<
                     '  FROM stueckliste s' <<
                     ' INNER JOIN artikelstamm a ON s.artikel = a.artikelnummer' <<
                     ' WHERE paket IN (' << pakete.join(', ') << ')' <<
                     ' GROUP BY s.reihenfolge, s.artikel, s.luftart' <<
                     ' ORDER BY s.luftart, s.reihenfolge'
        def r = withSql { sql ->
            sql.rows(statement.toString())
        }
        r.each { row ->
            row.each { k, v -> row[k] = getVal(v) }
        }
        if (DEBUG) println "${this}.getStuckliste(${pakete}): " + r.dump()
        return r
    }

}

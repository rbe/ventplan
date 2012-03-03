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

menuBar = menuBar {
    menu(id: 'menuDatei', text: 'Datei', mnemonic: 'D') {
        menuItem(neuesProjektAction)
        menuItem(projektOeffnenAction)
        // WAC-161 Zuletzt geöffnete Projekte
        menu(id: 'recentlyOpenedMenu', text: 'Zuletzt geöffnete Projekte', enabled: bind { model.aktivesProjekt == null })
        // WAC-192 Suchfunktion für WPX-Dateien
        menuItem(nachProjektSuchenAction)
        menuItem(projektSpeichernAction)
        menuItem(projektSpeichernAlsAction)
        // WAC-155 menuItem(alleProjekteSpeichernAction)
        menuItem(projektSchliessenAction)
        // Separator
        separator()
        menuItem(exitAction)
    }
    //
    menu(id: 'menuAuslegung', text: 'Auslegung', mnemonic: 'A') {
        // WAC-151 Automatische und manuelle Berechnung
        menuItem(automatischeBerechnungAction)
    }
    //
    menu(id: 'menuDokumente', text: 'Dokumente', mnemonic: 'O') {
        // WAC-108 Auslegung
        menuItem(projektAuslegungErstellenAction)
        /*
        // WAC-202 Verlegeplan
        menuItem(verlegeplanAction)
        */
        // WAC-108 Stückliste
        menuItem(projektStuecklisteErstellenAction)
        // WAC-108 Angebot
        menuItem(projektAngebotErstellenAction)
        /*
        // Separator
        separator()
        // WAC-177 Angebotsverfolgung
        menuItem(angebotsverfolgungAction)
        */
    }
    // WAC-167 Info-Menü mit Über-Dialog
    menu(id: 'menuInfo', text: '?', mnemonic: 'I') {
        menuItem(aboutAction)
    }
}
return menuBar

/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/16/12 10:35 AM
 */
package com.ventplan.desktop

import static griffon.util.GriffonApplicationUtils.isMacOSX

menuBar = menuBar {
    // Datei
    menu(id: 'menuDatei', text: 'Datei', mnemonic: 'D') {
        /* TODO target 7.0.9
        // Neu...
        menu(id: 'menuProjektNeu', text: 'Neu...', icon: imageIcon(resource: '/menu/project_new.png')) {
            menuItem(neuesProjektAction_EFH4ZKBWC)
            menuItem(neuesProjektAction_EFH5ZKBHWWC)
            menuItem(neuesProjektAction_EFH5ZKBWC2KRHW)
            menuItem(neuesProjektAction_EFH5ZKBWCDG)
        }
        */
        menuItem(neuesProjektAction)
        // TODO target 7.0.9
        // WAC-234 Wizard Dialog
        menuItem(neuesProjektWizardAction)
        //
        menuItem(projektOeffnenAction)
        // WAC-161 Zuletzt geöffnete Projekte
        menu(id: 'recentlyOpenedMenu', text: 'Zuletzt geöffnete Projekte', icon: imageIcon(resource: '/menu/project_recently_opened.png'), enabled: bind { model.aktivesProjekt == null })
        // WAC-192 Suchfunktion für WPX-Dateien
        menuItem(nachProjektSuchenAction)
        menuItem(projektSpeichernAction)
        menuItem(projektSpeichernAlsAction)
        // WAC-155 menuItem(alleProjekteSpeichernAction)
        menuItem(projektSchliessenAction)
        if (!isMacOSX) {
            // Separator
            separator()
            menuItem(exitAction)
        }
    }
    // Auslegung
    menu(id: 'menuAuslegung', text: 'Auslegung', mnemonic: 'A') {
        // WAC-151 Automatische und manuelle Berechnung
        menuItem(automatischeBerechnungAction)
        // WAC-108 Auslegung
        menuItem(projektAuslegungErstellenAction)
        // WAC-202 Prinzipskizze
        menuItem(projektPrinzipskizzeErstellenAction)
    }
    // Dokumente
    menu(id: 'menuDokumente', text: 'Dokumente', mnemonic: 'O') {
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
    // Info
    menu(id: 'menuInfo', text: 'Hilfe') {
        // WAC-167 Info-Menü mit Über-Dialog
        menuItem(aboutAction)
    }
}
return menuBar

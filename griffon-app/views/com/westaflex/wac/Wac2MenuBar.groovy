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

menuBar = menuBar {
    menu(id: 'hauptmenu', text: 'Auslegung', mnemonic: 'A') {
        menuItem(neuesProjektAction)
        menuItem(projektOeffnenAction)
        // WAC-161 Zuletzt geöffnete Projekte
        menu(id: 'recentlyOpenedMenu', text: 'Zuletzt geöffnete Projekte', enabled: bind { model.aktivesProjekt == null })
        menuItem(projektSpeichernAction)
        menuItem(projektSpeichernAlsAction)
        // WAC-155 menuItem(alleProjekteSpeichernAction)
        menuItem(projektSchliessenAction)
        // Separator
        separator()
        // WAC-151 Automatische und manuelle Berechnung
        menuItem(automatischeBerechnungAction)
        // Separator
        separator()
        // WAC-202 Verlegeplan
        menuItem(verlegeplanAction)
        // WAC-108 Auslegung und Angebot mit Stückliste erstellen
        menuItem(projektAuslegungErstellenAction)
        menuItem(projektAngebotErstellenAction)
        // WAC-177 Angebotsverfolgung
        menuItem(angebotsverfolgungAction)
//      if (!isMacOSX) {
        // Separator
        separator()
        menuItem(exitAction)
//      }
    }
    // WAC-167 Info-Menü mit Über-Dialog
    menu(id: 'infomenu', text: '?', mnemonic: 'I') {
        menuItem(aboutAction)
    }
}
return menuBar

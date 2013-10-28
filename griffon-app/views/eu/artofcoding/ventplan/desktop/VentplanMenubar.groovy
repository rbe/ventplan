/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 17:23
 */

package eu.artofcoding.ventplan.desktop

menuBar = menuBar {
/*
    // Ablage
    menu(id: 'menuDatei', text: 'Datei', mnemonic: 'D') {
        menuItem(ventidModusAction)
        if (!isMacOSX) {
            separator()
            menuItem(exitAction)
        }
    }
*/
    // Projekt
    menu(id: 'menuProjekt', text: 'Projekt', mnemonic: 'P') {
        /* WAC-274
        // Neu...
        menu(id: 'menuProjektNeu', text: 'Neu...', icon: imageIcon(resource: '/menu/project_new.png')) {
            menuItem(neuesProjektAction_EFH4ZKBWC)
            menuItem(neuesProjektAction_EFH5ZKBHWWC)
            menuItem(neuesProjektAction_EFH5ZKBWC2KRHW)
            menuItem(neuesProjektAction_EFH5ZKBWCDG)
        }
        */
        // WAC-274 menuItem(neuesProjektAction)
        // WAC-234 Wizard Dialog
        menuItem(neuesProjektWizardAction)
        //
        separator()
        menuItem(projektOeffnenAction)
        // WAC-161 Zuletzt geöffnete Projekte
        menu(id: 'recentlyOpenedMenu', text: 'Zuletzt geöffnete Projekte', icon: imageIcon(resource: '/menu/project_recently_opened.png'), enabled: bind { model.aktivesProjekt == null })
        // WAC-192 Suchfunktion für WPX-Dateien
        menuItem(nachProjektSuchenAction)
        separator()
        menuItem(aktivesProjektSpeichernAction)
        menuItem(aktivesProjektSpeichernAlsAction)
        // WAC-155 menuItem(alleProjekteSpeichernAction)
        menuItem(projektSchliessenAction)
    }
    // Berechnung
    menu(id: 'menuBerechnung', text: 'Berechnung', mnemonic: 'A') {
        // WAC-151 Automatische und manuelle Berechnung
        menuItem(automatischeBerechnungAction)
    }
    // Dokumente
    menu(id: 'menuDokumente', text: 'Dokumente', mnemonic: 'O') {
        // WAC-108 Auslegung
        menuItem(auslegungErstellenAction)
        // WAC-108 Stückliste
        menuItem(stuecklisteErstellenAction)
        // WAC-108 Angebot
        menuItem(angebotErstellenAction)
        // WAC-202 Prinzipskizze
        menuItem(prinzipskizzeErstellenAction)
    }
    // Info
    menu(id: 'menuInfo', text: 'Hilfe') {
        // WAC-167 Info-Menü mit Über-Dialog
        menuItem(aboutAction)
    }
}

return menuBar

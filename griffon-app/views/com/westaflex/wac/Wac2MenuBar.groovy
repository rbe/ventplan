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
		separator()
		menuItem(projektSpeichernAction)
		menuItem(projektSpeichernAlsAction)
        // WAC-155 menuItem(alleProjekteSpeichernAction)
		menuItem(projektSchliessenAction)
		separator()
        // WAC-161: Zuletzt geöffnete Projekte
        // Position i=10
        menu(id: 'recentlyOpenedMenu', text: 'Zuletzt geöffnete Projekte', enabled: bind { model.aktivesProjekt == null })
        separator()
        menuItem(seitenansichtAction)
        // WAC-151: Automatische und manuelle Berechnung
//        separator()
//        menuItem(automatischeBerechnungAction)
		/* Später aktivieren, Angebote sind noch nicht verfügbar! menuItem(druckenAction)*/
        separator()
        // WAC-108: Stückliste generieren
        menuItem(stucklisteAction)
        // WAC-177: Angebotsverfolgung
        menuItem(angebotsverfolgungAction)
//		if (!isMacOSX) {
			separator()
			menuItem(exitAction)
//		}
	}

    // WAC-167: Info-Menü mit Über-Dialog
    menu(id: 'infomenu', text: '?', mnemonic: 'I') {
        menuItem(aboutAction)
    }

	/*
	menu(text: 'Edit', mnemonic: 'E') {
		menuItem(undoAction)
		menuItem(redoAction)
		separator()
		menuItem(cutAction)
		menuItem(copyAction)
		menuItem(pasteAction)
		separator()
		menuItem(selectAllAction)
		separator()
		menuItem(findAction)
		menuItem(findNextAction)
		menuItem(findPreviousAction)
		menuItem(replaceAction)
	}
	*/
}

return menuBar

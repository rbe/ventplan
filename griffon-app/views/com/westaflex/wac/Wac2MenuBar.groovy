/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/Wac2MenuBar.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 */
package com.westaflex.wac

menuBar = menuBar {
	
    menu(id: 'hauptmenu', text: 'Auslegung', mnemonic: 'A') {
		menuItem(neuesProjektAction)
		menuItem(projektOeffnenAction)
		separator()
		menuItem(projektSpeichernAction)
		menuItem(projektSpeichernAlsAction)
		menuItem(alleProjekteSpeichernAction)
		menuItem(projektSchliessenAction)
		separator()
        // WAC-161: Zuletzt geöffnete Projekte
        // Position i=10
        menu(id: 'recentlyOpenedMenu', text: 'Zuletzt geöffnete Projekte')
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

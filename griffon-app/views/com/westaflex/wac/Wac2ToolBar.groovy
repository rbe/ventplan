/**
 * WAC
 * 
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. All rights reserved.
 */
package com.westaflex.wac

def vertSep = { -> separator(orientation: javax.swing.SwingConstants.VERTICAL) }

toolBar(id: 'toolbar', rollover: true) {
	button(neuesProjektAction, text: null)
	button(projektOeffnenAction, text: null)
	vertSep()
	button(projektSpeichernAction, text: null)
    //button(projektSpeichernAlsAction, text: null)
    button(alleProjekteSpeichernAction, text: null)
    vertSep()
	button(projektSeitenansichtAction, text: null)
    // WAC-151: Automatische und manuelle Berechnung
    vertSep()
	button(automatischeBerechnungAction, text: null)
	/* Später aktivieren, Angebote sind noch nicht verfügbar! button(projektDruckenAction, text: null)*/
}

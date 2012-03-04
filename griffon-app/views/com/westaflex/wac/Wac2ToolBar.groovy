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

def vertSep = {-> separator(orientation: javax.swing.SwingConstants.VERTICAL) }

toolBar(id: 'toolbar', rollover: true) {
    button(neuesProjektAction, text: null)
    button(projektOeffnenAction, text: null)
    button(projektSpeichernAction, text: null)
    //button(projektSpeichernAlsAction, text: null)
    // WAC-155 button(alleProjekteSpeichernAction, text: null)
    vertSep()
    // WAC-108 Auslegung
    button(projektAuslegungErstellenAction, text: null)
    // WAC-108 Stückliste
    button(projektStuecklisteErstellenAction, text: null)
    // WAC-108 Angebot
    button(projektAngebotErstellenAction, text: null)
    // WAC-151: Automatische und manuelle Berechnung
    vertSep()
    button(automatischeBerechnungAction, text: null)
}

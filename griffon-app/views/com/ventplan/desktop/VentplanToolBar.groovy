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

import javax.swing.SwingConstants

def vertSep = {-> separator(orientation: SwingConstants.VERTICAL) }

toolBar(id: 'toolbar', rollover: true) {
    button(neuesProjektAction, text: null, toolTipText: 'Ein neues Projekt starten')
    button(projektOeffnenAction, text: null, toolTipText: 'Ein Projekt öffnen')
    // WAC-192 Suchfunktion für WPX-Dateien
    button(nachProjektSuchenAction, text: null, toolTipText: 'Nach Projekten suchen')
    button(projektSpeichernAction, text: null, toolTipText: 'Projekt speichern')
    // WAC-155 button(alleProjekteSpeichernAction, text: null)
    //
    vertSep()
    // WAC-151: Automatische und manuelle Berechnung
    button(automatischeBerechnungAction, text: null, toolTipText: 'Automatische Berechnung erneut durchführen')
    //
    vertSep()
    // WAC-108 Auslegung
    button(projektAuslegungErstellenAction, text: null, toolTipText: 'Dokument "Auslegung" erstellen')
    // WAC-108 Stückliste
    button(projektStuecklisteErstellenAction, text: null, toolTipText: 'Dokument "Stückliste" erstellen')
    // WAC-108 Angebot
    button(projektAngebotErstellenAction, text: null, toolTipText: 'Dokument "Angebot" erstellen')
    // WAC-202 Prinzipskizze
    button(projektPrinzipskizzeErstellenAction, text: null, toolTipText: 'Prinzipskizze erstellen')
}

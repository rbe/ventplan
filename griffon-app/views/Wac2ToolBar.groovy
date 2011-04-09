/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/Wac2ToolBar.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 */

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

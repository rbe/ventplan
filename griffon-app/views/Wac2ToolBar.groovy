/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/Wac2ToolBar.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */

def vertSep = { -> separator(orientation: javax.swing.SwingConstants.VERTICAL) }

toolBar(id: 'toolbar', rollover: true) {
	button(neuesProjektAction, text: null)
	button(projektOeffnenAction, text: null)
	vertSep()
	button(projektSpeichernAction, text: null)
	button(projektSpeichernAlsAction, text: null)
	vertSep()
	button(projektSeitenansichtAction, text: null)
	// TODO button(projektDruckenAction, text: null)
}

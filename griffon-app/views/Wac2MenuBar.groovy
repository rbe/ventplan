/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/Wac2MenuBar.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 */
import static griffon.util.GriffonApplicationUtils.*

menuBar = menuBar {
	
	menu(text: 'Auslegung', mnemonic: 'A') {
		menuItem(neuesProjektAction)
		menuItem(projektOeffnenAction)
		separator()
		menuItem(projektSpeichernAction)
		menuItem(projektSpeichernAlsAction)
		menuItem(projektSchliessenAction)
		separator()
		menuItem(seitenansichtAction)
		// TODO menuItem(druckenAction)
		if (!isMacOSX) {
			separator()
			menuItem(exitAction)
		}
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

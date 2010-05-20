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
		menuItem(druckenAction)
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

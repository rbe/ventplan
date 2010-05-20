import javax.swing.SwingConstants

def vertSep = { -> separator(orientation: SwingConstants.VERTICAL) }

toolBar(id: 'toolbar', rollover: true) {
	button(neuesProjektAction, text: null)
	button(projektOeffnenAction, text: null)
	vertSep()
	button(projektSpeichernAction, text: null)
	button(projektSpeichernAlsAction, text: null)
	vertSep()
	button(seitenansichtAction, text: null)
	button(druckenAction, text: null)
	/*
	button(undoAction, text: null)
	button(redoAction, text: null)
	separator(orientation: SwingConstants.VERTICAL)
	button(cutAction, text: null)
	button(copyAction, text: null)
	button(pasteAction, text: null)
	separator(orientation: SwingConstants.VERTICAL)
	button(findAction, text: null)
	button(replaceAction, text: null)
	separator(orientation: SwingConstants.VERTICAL)
	button(toggleLayoutAction, text: null)
	button(snapshotAction, text: null)
	*/
}

//return toolbar

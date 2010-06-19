/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/actions/Wac2Actions.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
import static griffon.util.GriffonApplicationUtils.*
import groovy.ui.Console

neuesProjektAction = action(
		id: "neuesProjektAction",
		enabled: true,
		name: "Neues Projekt",
		mnemonic: "N",
		accelerator: shortcut("N"),
		smallIcon: imageIcon(resource: "/menu/neuesprojekt.png"),
		closure: controller.neuesProjekt
	)

projektOeffnenAction = action(
		id: "projektOeffnenAction",
		enabled: true,
		name: "Projekt öffnen",
		mnemonic: "O",
		accelerator: shortcut("O"),
		smallIcon: imageIcon(resource: "/menu/projektoeffnen.png", class: Console),
		closure: {}
	)

projektSpeichernAction = action(
		id: "projektSpeichernAction",
		enabled: true,
		name: "Projekt speichern",
		mnemonic: "S",
		accelerator: shortcut("S"),
		smallIcon: imageIcon("/menu/projektspeichern.png"),
		closure: {}
	)

projektSpeichernAlsAction = action(
		id: "projektSpeichernAlsAction",
		enabled: true,
		name: "Projekt speichern als...",
		mnemonic: "A",
		accelerator: shortcut("shift S"),
		smallIcon: imageIcon("/menu/projektspeichern.png"),
		closure: {}
	)

projektSchliessenAction = action(
		id: "projektSchliessenAction",
		enabled: true,
		name: "Projekt schliessen",
		mnemonic: "W",
		accelerator: shortcut("W"),
		closure: {}
	)

seitenansichtAction = action(
		id: "seitenansichtAction",
		enabled: true,
		name: "Seitenansicht",
		smallIcon: imageIcon("/menu/seitenansicht.png"),
		closure: {}
	)

druckenAction = action(
		id: "druckenAction",
		enabled: true,
		name: "Drucken",
		mnemonic: "P",
		accelerator: shortcut("P"),
		smallIcon: imageIcon("/menu/drucken.png"),
		closure: {}
	)

exitAction = action(
		id: "exitAction",
		enabled: true,
		name: "WestaWAC beenden",
		mnemonic: "Q",
		accelerator: shortcut("Q"),
		closure: {}
	)

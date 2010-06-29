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
		name: "Neues Projekt",
		mnemonic: "N",
		accelerator: shortcut("N"),
		smallIcon: imageIcon(resource: "/menu/neuesprojekt.png"),
		enabled: true,
		closure: controller.neuesProjekt
	)

projektOeffnenAction = action(
		id: "projektOeffnenAction",
		name: "Projekt öffnen",
		mnemonic: "O",
		accelerator: shortcut("O"),
		smallIcon: imageIcon(resource: "/menu/projektoeffnen.png", class: Console),
		enabled: true,
		closure: {}
	)

projektSpeichernAction = action(
		id: "projektSpeichernAction",
		name: "Projekt speichern",
		mnemonic: "S",
		accelerator: shortcut("S"),
		smallIcon: imageIcon("/menu/projektspeichern.png"),
		enabled: bind { model.aktivesProjekt.model != null }, // TODO Bind to model.map.dirty
		closure: {}
	)

projektSpeichernAlsAction = action(
		id: "projektSpeichernAlsAction",
		name: "Projekt speichern als...",
		mnemonic: "A",
		accelerator: shortcut("shift S"),
		smallIcon: imageIcon("/menu/projektspeichern.png"),
		enabled: bind { model.aktivesProjekt.model != null }, // TODO Bind to model.map.dirty
		closure: {}
	)

projektSchliessenAction = action(
		id: "projektSchliessenAction",
		name: "Projekt schliessen",
		mnemonic: "W",
		accelerator: shortcut("W"),
		enabled: bind { model.aktivesProjekt.model != null },
		closure: {}
	)

projektSeitenansichtAction = action(
		id: "seitenansichtAction",
		name: "Seitenansicht",
		smallIcon: imageIcon("/menu/seitenansicht.png"),
		enabled: bind { model.aktivesProjekt.model != null },
		closure: {}
	)

projektDruckenAction = action(
		id: "druckenAction",
		name: "Drucken",
		mnemonic: "P",
		accelerator: shortcut("P"),
		smallIcon: imageIcon("/menu/drucken.png"),
		enabled: bind { model.aktivesProjekt.model != null },
		closure: {}
	)

exitAction = action(
		id: "exitAction",
		name: "WestaWAC beenden",
		mnemonic: "Q",
		accelerator: shortcut("Q"),
		enabled: true,
		closure: {}
	)

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
		accelerator: shortcut("A"),
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

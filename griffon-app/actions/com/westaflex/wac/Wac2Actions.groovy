/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/controllers/Wac2Controller.groovy
 * Last modified at 13.03.2011 18:34:15 by rbe
 */
package com.westaflex.wac

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
        closure: controller.projektOffnen
        )

projektSpeichernAction = action(
        id: "aktivesProjektSpeichernAction",
        name: "Projekt speichern",
        mnemonic: "S",
        accelerator: shortcut("S"),
        smallIcon: imageIcon("/menu/projektspeichern.png"),
        enabled: bind { model.aktivesProjektGeandert },
        closure: controller.aktivesProjektSpeichern
        )

projektSpeichernAlsAction = action(
        id: "aktivesProjektSpeichernAlsAction",
        name: "Projekt speichern als...",
        mnemonic: "L",
        accelerator: shortcut("shift S"),
        smallIcon: imageIcon("/menu/projektspeichern.png"),
        enabled: bind { model.aktivesProjektGeandert },
        closure: controller.aktivesProjektSpeichernAls
        )

alleProjekteSpeichernAction = action(
        id: "alleProjekteSpeichernAction",
        name: "Alle Projekte speichern",
        mnemonic: "E",
        accelerator: shortcut("shift A"),
        smallIcon: imageIcon("/menu/alleprojektespeichern.png"),
        enabled: bind { model.alleProjekteGeandert },
        closure: controller.alleProjekteSpeichern
        )

projektSchliessenAction = action(
        id: "projektSchliessenAction",
        name: "Projekt schliessen",
        mnemonic: "W",
        accelerator: shortcut("W"),
        enabled: bind { model.aktivesProjekt != null },
        closure: controller.projektSchliessen
        )

projektSeitenansichtAction = action(
        id: "seitenansichtAction",
        name: "Seitenansicht",
        mnemonic: "A",
        accelerator: shortcut("A"),
        smallIcon: imageIcon("/menu/seitenansicht.png"),
        enabled: bind { model.aktivesProjekt != null },
        closure: controller.projektSeitenansicht
        )

projektDruckenAction = action(
        id: "druckenAction",
        name: "Drucken",
        mnemonic: "P",
        accelerator: shortcut("P"),
        smallIcon: imageIcon("/menu/drucken.png"),
        enabled: bind { model.aktivesProjekt != null },
        closure: controller.projektDrucken
        )

exitAction = action(
        id: "exitAction",
        name: "WestaWAC beenden",
        mnemonic: "Q",
        accelerator: shortcut("Q"),
        enabled: true,
        closure: controller.exitApplication
        )

// WAC-151: Automatische und manuelle Berechnung
automatischeBerechnungAction = action(
        id: "automatischeBerechnungAction",
        name: "Automatische Berechnung",
        mnemonic: "B",
        accelerator: shortcut("B"),
        smallIcon: imageIcon(resource: "/menu/automatischeBerechnung.png"),
        enabled: bind { model.aktivesProjekt != null },
        closure: controller.automatischeBerechnung
        )

// WAC-167: Info-Menü mit Über-Dialog
aboutAction = action(
        id: "aboutAction",
        name: "Über",
        mnemonic: "I",
        accelerator: shortcut("I"),
        enabled: true,
        closure: controller.aboutDialogOeffnen
        )

// WAC-177: Angebotsverfolgung
angebotsverfolgungAction = action(
    id: "angebotsverfolgungAction",
    name: "Angebotsverfolgung",
    mnemonic: "U",
    accelerator: shortcut("U"),
    enabled: true,
    closure: controller.angebotsverfolgung
)

// WAC-108: Stückliste generieren
stucklisteAction = action(
    id: "stucklisteAction",
    name: "Stückliste generieren",
    mnemonic: "G",
    accelerator: shortcut("G"),
    enabled: bind { model.aktivesProjekt != null },
    closure: controller.stuckliste
)

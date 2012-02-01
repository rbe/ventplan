/*
 * WAC
 * 
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. All rights reserved.
 *
 */
package com.westaflex.wac

import groovy.ui.Console

neuesProjektAction = action(
        id: "neuesProjektAction",
        name: "Neues Projekt",
        mnemonic: "N",
        accelerator: shortcut("N"),
        smallIcon: imageIcon(resource: "/menu/neuesprojekt.png"),
        enabled: bind { model.aktivesProjekt == null },
        closure: controller.neuesProjekt
        )

projektOeffnenAction = action(
        id: "projektOeffnenAction",
        name: "Projekt öffnen",
        mnemonic: "O",
        accelerator: shortcut("O"),
        smallIcon: imageIcon(resource: "/menu/projektoeffnen.png", class: Console),
        enabled: bind { model.aktivesProjekt == null },
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

// WAC-155
/*
alleProjekteSpeichernAction = action(
        id: "alleProjekteSpeichernAction",
        name: "Alle Projekte speichern",
        mnemonic: "E",
        accelerator: shortcut("shift A"),
        smallIcon: imageIcon("/menu/alleprojektespeichern.png"),
        enabled: bind { model.alleProjekteGeandert },
        closure: controller.alleProjekteSpeichernAction
        )
*/

projektSchliessenAction = action(
        id: "projektSchliessenAction",
        name: "Projekt schliessen",
        mnemonic: "W",
        accelerator: shortcut("W"),
        enabled: bind { model.aktivesProjekt != null },
        closure: controller.projektSchliessen
        )

// WAC-108 Angebot mit Stückliste generieren
projektAuslegungErstellenAction = action(
        id: "auslegungErstellenAction",
        name: "Auslegung erstellen",
        mnemonic: "L",
        accelerator: shortcut("L"),
        smallIcon: imageIcon("/menu/seitenansicht.png"),
        enabled: bind { model.aktivesProjekt != null },
        closure: controller.projektAuslegungErstellen
        )

// WAC-108 Angebot mit Stückliste generieren.
projektAngebotErstellenAction = action(
        id: "angebotErstellenAction",
        name: "Angebot erstellen",
        mnemonic: "P",
        accelerator: shortcut("P"),
        smallIcon: imageIcon("/menu/drucken.png"),
        enabled: bind { model.aktivesProjekt != null },
        closure: controller.projektAngebotErstellen
        )

exitAction = action(
        id: "exitAction",
        name: "WestaWAC beenden",
        mnemonic: "Q",
        accelerator: shortcut("Q"),
        enabled: true,
        closure: controller.exitApplication
        )

// WAC-151 Automatische und manuelle Berechnung
automatischeBerechnungAction = action(
        id: "automatischeBerechnungAction",
        name: "Automatische Berechnung",
        mnemonic: "B",
        accelerator: shortcut("B"),
        smallIcon: imageIcon(resource: "/menu/automatischeBerechnung.png"),
        enabled: bind { model.aktivesProjekt != null },
        closure: controller.automatischeBerechnung
        )

// WAC-177 Angebotsverfolgung
angebotsverfolgungAction = action(
    id: "angebotsverfolgungAction",
    name: "Angebotsverfolgung",
    mnemonic: "U",
    accelerator: shortcut("U"),
    enabled: bind { model.aktivesProjekt != null },
    closure: controller.angebotsverfolgung
)

// WAC-202 Verlegeplan
verlegeplanAction = action(
    id: "verlegeplanAction",
    name: "Verlegeplan erstellen",
    mnemonic: "G",
    accelerator: shortcut("G"),
    enabled: bind { model.aktivesProjekt != null },
    closure: controller.stuckliste
)

// WAC-167 Info-Menü mit Über-Dialog
aboutAction = action(
        id: "aboutAction",
        name: "Über",
        mnemonic: "I",
        accelerator: shortcut("I"),
        enabled: true,
        closure: controller.aboutDialogOeffnen
)

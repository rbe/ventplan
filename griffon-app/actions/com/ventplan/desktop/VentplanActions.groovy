/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/16/12 10:35 AM
 */
package com.ventplan.desktop

exitAction = action(
        id: 'exitAction',
        name: 'Ventplan beenden',
        mnemonic: 'Q',
        accelerator: shortcut('Q'),
        smallIcon: imageIcon(resource: '/menu/app_exit.png'),
        enabled: true,
        closure: controller.exitApplication
)

neuesProjektAction = action(
        id: 'neuesProjektAction',
        name: 'Neues Projekt',
        mnemonic: 'N',
        accelerator: shortcut('N'),
        smallIcon: imageIcon(resource: '/menu/project_new.png'),
        enabled: bind { model.aktivesProjekt == null },
        closure: controller.neuesProjekt
)

projektOeffnenAction = action(
        id: 'projektOeffnenAction',
        name: 'Projekt öffnen',
        mnemonic: 'O',
        accelerator: shortcut('O'),
        //smallIcon: imageIcon(resource: '/menu/project_open.png', class: Console),
        smallIcon: imageIcon('/menu/project_open.png'),
        enabled: bind { model.aktivesProjekt == null },
        closure: controller.projektOffnen
)

projektSpeichernAction = action(
        id: 'aktivesProjektSpeichernAction',
        name: 'Projekt speichern',
        mnemonic: 'S',
        accelerator: shortcut('S'),
        smallIcon: imageIcon('/menu/project_save.png'),
        enabled: bind { model.aktivesProjektGeandert },
        closure: controller.aktivesProjektSpeichern
)

projektSpeichernAlsAction = action(
        id: 'aktivesProjektSpeichernAlsAction',
        name: 'Projekt speichern als...',
        mnemonic: 'L',
        accelerator: shortcut('shift S'),
        smallIcon: imageIcon('/menu/project_save_as.png'),
        enabled: bind { model.aktivesProjektGeandert },
        closure: controller.aktivesProjektSpeichernAls
)

projektSchliessenAction = action(
        id: 'projektSchliessenAction',
        name: 'Projekt schliessen',
        mnemonic: 'W',
        accelerator: shortcut('W'),
        smallIcon: imageIcon(resource: '/menu/project_close.png'),
        enabled: bind { model.aktivesProjekt != null },
        closure: controller.projektSchliessen
)

// WAC-108 Auslegung
projektAuslegungErstellenAction = action(
        id: 'auslegungErstellenAction',
        name: 'Auslegung erstellen',
        mnemonic: 'L',
        accelerator: shortcut('L'),
        smallIcon: imageIcon('/menu/project_auslegung.png'),
        enabled: bind { model.aktivesProjekt != null },
        closure: controller.projektAuslegungErstellen
)

// WAC-108 Angebot
projektAngebotErstellenAction = action(
        id: 'angebotErstellenAction',
        name: 'Angebot erstellen',
        mnemonic: 'P',
        accelerator: shortcut('P'),
        smallIcon: imageIcon('/menu/project_offer.png'),
        enabled: bind { model.aktivesProjekt != null },
        closure: controller.projektAngebotErstellen
)

// WAC-108 Stückliste
projektStuecklisteErstellenAction = action(
        id: 'stuecklisteErstellenAction',
        name: 'Stückliste erstellen',
        mnemonic: 'K',
        accelerator: shortcut('K'),
        smallIcon: imageIcon('/menu/project_partlist.png'),
        enabled: bind { model.aktivesProjekt != null },
        closure: controller.projektStuecklisteErstellen
)

// WAC-151 Automatische und manuelle Berechnung
automatischeBerechnungAction = action(
        id: 'automatischeBerechnungAction',
        name: 'Automatische Berechnung',
        mnemonic: 'B',
        accelerator: shortcut('B'),
        smallIcon: imageIcon(resource: '/menu/project_auto_calc.png'),
        enabled: bind { model.aktivesProjekt != null },
        closure: controller.automatischeBerechnung
)

// WAC-155
/*
alleProjekteSpeichernAction = action(
        id: "alleProjekteSpeichernAction",
        name: "Alle Projekte speichern",
        mnemonic: "E",
        accelerator: shortcut("shift A"),
        smallIcon: imageIcon("/menu/project_save_all.png"),
        enabled: bind { model.alleProjekteGeandert },
        closure: controller.alleProjekteSpeichernAction
        )
*/

// WAC-167 Info-Menü mit Über-Dialog
aboutAction = action(
        id: 'aboutAction',
        name: 'Über',
        smallIcon: imageIcon(resource: '/menu/app_info.png'),
        enabled: true,
        closure: controller.aboutDialogOeffnen
)

/*
// WAC-177 Angebotsverfolgung
angebotsverfolgungAction = action(
    id: 'angebotsverfolgungAction',
    name: 'Angebotsverfolgung',
    mnemonic: 'U',
    accelerator: shortcut('U'),
    enabled: bind { model.aktivesProjekt != null },
    closure: controller.angebotsverfolgung
)
*/

// WAC-192 Suchfunktion für WPX-Dateien
nachProjektSuchenAction = action(
        id: 'nachProjektSuchenAction',
        name: 'Projekt suchen',
        mnemonic: 'F',
        accelerator: shortcut('F'),
        smallIcon: imageIcon(resource: '/menu/app_filefind.png'),
        enabled: bind { model.aktivesProjekt == null },
        closure: controller.nachProjektSuchenDialogOeffnen
)

// WAC-202 Verlegeplan
projektVerlegeplanErstellenAction = action(
        id: 'verlegeplanAction',
        name: 'Verlegeplan erstellen',
        mnemonic: 'G',
        accelerator: shortcut('G'),
        smallIcon: imageIcon(resource: '/menu/project_package_utilities.png'),
        enabled: bind { model.aktivesProjekt != null },
        closure: controller.projektVerlegeplanErstellen
)

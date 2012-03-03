/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout

import com.bensmann.griffon.GriffonHelper as GH

import java.awt.Color
import javax.swing.ListSelectionModel

// Dieser Dialog wird für die Suche von WPX-Dateien (Projekte) genutzt
panel(id: "projektSuchenPanel", layout: new MigLayout("fillx, wrap", "[]para[fill]para[fill]", ""), constraints: "grow") {
    // Informationen über den Ersteller
    label("Volltextsuche im Ordner", foreground: Color.BLUE, constraints: "grow, span 3")

    label("Suche in Ordner")
    button(id: "projektSuchenOrdnerOeffnen", text: "Ordner wählen")
    label()

    label("Gewählter Ordner")
    label(id: "projektSuchenOrdnerPfad", constraints: "grow, span 2")

    label("Bauvorhaben")
    textField(id: "projektSuchenBauvorhaben", constraints: "grow, span 2")

    label("Angebotsnummer")
    textField(id: "projektSuchenAngebotsnummer", constraints: "grow, span 2")

    label("Installateur")
    textField(id: "projektSuchenInstallateur", constraints: "grow, span 2")

    label("Handel")
    textField(id: "projektSuchenHandel", constraints: "grow, span 2")

    label("", constraints: "span 2")
    button(id: "projektSuchenStarteSuche", constraints: "grow")

    list(id: "projektSuchen",
         listData: null,
         visibleRowCount: 5,
         selectionMode:ListSelectionModel.SINGLE_SELECTION
    )

    button(id: "projektSuchenAbbruch", text: "Abbrechen")
    // Kompletter Text, damit Dimension stimmt, wenn Text nachträglich geändert wird (durch Controller/Action)
    button(id: "projektSuchenDateiOeffnen", text: "Gewählte Datei öffnen")
}

build(ProjektSuchenBindings)

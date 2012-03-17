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

import java.text.SimpleDateFormat
import javax.swing.table.JTableHeader

// Stuckliste view
panel(id: "stucklisteSuchePanel", layout: new MigLayout("fillx, wrap", "[fill]", "[]para[]"), constraints: "grow") {
    panel(id: "stucklisteSuchePanel", layout: new MigLayout("fillx, wrap", "[fill]para[fill]para[fill]para[]", ""), constraints: "grow") {
        label("Artikelnr.")
        label("Text/Beschreibung")
        label("Anzahl")
        label()

        textField(id: "stucklisteSucheArtikelnummer")
        label(id: "stucklisteSucheArtikeltext")
        textField(id: "stucklisteSucheArtikelanzahl")
        button(id: "stucklisteSucheHinzufugen", text: "+")
    }
//    panel(id: "stucklisteErgebnisPanel", layout: new MigLayout("fillx, wrap", "[fill]", ""), constraints: "grow") {
//        scrollPane() {
//            table(id: "stucklisteErgebnisTabelle", model: model.createStucklisteUbersichtTableModel()) {
//                //current.setRowHeight(16)
//                current.setSortable(false)
//                current.getTableHeader().setDefaultRenderer(new JTableHeader().getDefaultRenderer())
//                current.setAutoCreateRowSorter(false)
//                current.setRowSorter(null)
//                current.setFillsViewportHeight(true)
//            }
//        }
//    }
    panel(id: "stucklisteUbersichtPanel", layout: new MigLayout("fillx, wrap", "[fill]", ""), constraints: "grow") {
        scrollPane() {
            table(id: "stucklisteUbersichtTabelle") {
                //current.setRowHeight(16)
                current.setSortable(false)
                current.getTableHeader().setDefaultRenderer(new JTableHeader().getDefaultRenderer())
                current.setAutoCreateRowSorter(false)
                current.setRowSorter(null)
                current.setFillsViewportHeight(true)
            }
        }
    }
//   button('Schliessen', actionPerformed: { e ->
//        controller.aboutDialog.dispose()
//    })
}

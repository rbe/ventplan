/**
 * WAC
 * 
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. All rights reserved.
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout


import ca.odell.glazedlists.EventList
import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.SortedList
import ca.odell.glazedlists.BasicEventList
import ca.odell.glazedlists.*
import javax.swing.JComboBox

panel(id: "raumPanel", layout: new MigLayout("fill", "[fill,grow]", "")) {
    borderLayout()
    // Raum anlegen
    panel(id: "raumEingabePanel", constraints: NORTH, layout: new MigLayout("fillx", "[left,fill]para[left]")) {
        label("Raumtyp")
        label("abw. Raumbezeichnung")
        label("Geschoss")
        label("Luftart")
        label("Raumfläche (m²)")
        label("Raumhöhe (m)")
        label("Zuluftfaktor")
        label("Abluftvolumenstrom (m³)", constraints: "wrap")
        comboBox(id: "raumTyp", items: model.meta.raum.typ)
        textField(id: "raumBezeichnung", constraints: "width 150px")
        comboBox(id: "raumGeschoss", items: model.meta.raum.geschoss)
        comboBox(id: "raumLuftart", items: model.meta.raum.luftart)
        textField(id: "raumFlache", constraints: "width 100px")
        textField(id: "raumHohe", text: "2,50", constraints: "width 100px")
        textField(id: "raumZuluftfaktor", text: "3,00", editable: bind { raumLuftart.selectedItem != "ÜB" }, constraints: "width 80px")
        textField(id: "raumAbluftVolumenstrom",         editable: bind { raumLuftart.selectedItem != "ÜB" }, constraints: "width 80px")
        // Hinzufügen-Button aktivieren, wenn Fläche eingegeben wurde
        button(id: "raumHinzufugen"/*, enabled: bind { !raumFlache.text.isEmpty() }*/, text: "Hinzufügen")
    }
    // Tabelle aller Räume
    panel(id: "raumTabellePanel", constraints: CENTER, layout: new MigLayout("fill", "[fill]")) {
        jideScrollPane(constraints: "grow") {
            table(id: "raumTabelle", model: model.createRaumTableModel())
        }
    }
    // Buttons
    hbox(constraints: SOUTH) {
        button(id: "raumEntfernen",            enabled: bind {model.raumButtonsEnabled}, text: "Raum entfernen")
        button(id: "raumBearbeiten",           enabled: bind {model.raumButtonsEnabled}, text: "Raum bearbeiten")
        button(id: "raumKopieren",             enabled: bind {model.raumButtonsEnabled}, text: "Raum kopieren")

        button(id: "raumNachObenVerschieben",  enabled: bind {model.raumVerschiebenButtonsEnabled}, text: "^")
        button(id: "raumNachUntenVerschieben", enabled: bind {model.raumVerschiebenButtonsEnabled}, text: "v")
    }
}
// Format fields
GH.yellowTextField(raumBezeichnung)
[raumFlache, raumHohe, raumZuluftfaktor, raumAbluftVolumenstrom].each {
    GH.autoformatDoubleTextField(it)
}
// Bindings
build(RaumdatenBindings)

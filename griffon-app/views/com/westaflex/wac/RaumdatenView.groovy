/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/com/westaflex/wac/RaumdatenView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout

jideScrollPane(constraints: "grow") {
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
            textField(id: "raumAbluftVs",                   editable: bind { raumLuftart.selectedItem != "ÜB" }, constraints: "width 80px")
            // Hinzufügen-Button aktivieren, wenn Fläche eingegeben wurde
            button(id: "raumHinzufugen", enabled: bind { !raumFlache.text.isEmpty() }, text: "Hinzufügen")
        }
        // Tabelle aller Räume
        panel(id: "raumTabellePanel", constraints: CENTER, layout: new MigLayout("fill", "[fill]")) {
            jideScrollPane(constraints: "grow") {
                //table(id: "raumTabelle", model: model.createRaumTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                //}
                table(id: 'raumTabelle', selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION, constraints: "grow") {
                    tableModel() {
                        current.addTableModelListener(GH.getRaumdatenTableModelListener())
                        propertyColumn(header: 'Raum', 
                            propertyName: 'raumBezeichnungCombo',
                            cellEditor: GH.getRaumdatenBezeichnungCellEditor(builder,model),
                            cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                        )
                        propertyColumn(header: 'Geschoss', 
                            propertyName: 'raumGeschossCombo',
                            cellEditor: GH.getRaumdatenGeschossCellEditor(builder,model),
                            cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                        )
                        propertyColumn(header: 'Luftart', 
                            propertyName: 'raumLuftartCombo',
                            cellEditor: GH.getRaumdatenLuftartCellEditor(builder,model),
                            cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                        )
                        propertyColumn(header: GH.ws("Raumfläche<br/>(m²)"), propertyName: 'raumFlache')
                        propertyColumn(header: GH.ws("Raumhöhe<br/>(m)"), propertyName: 'raumHohe')
                        propertyColumn(header: 'Zuluftfaktor', propertyName: 'raumZuluftfaktor')
                        propertyColumn(header: 'Abluftvolumenstrom', propertyName: 'raumAbluftVs')
                    }
                }
            }
        }
        // Buttons
        hbox(constraints: SOUTH) {
            // TODO mmu Enable buttons when table isn't empty and a row is selected
            button(id: "raumEntfernen",            enabled: bind {1==1}, text: "Raum entfernen")
            button(id: "raumBearbeiten",           enabled: bind {1==1}, text: "Raum bearbeiten")
            button(id: "raumKopieren",             enabled: bind {1==1}, text: "Raum kopieren")

            /*
            button(id: "raumEntfernen",            enabled: bind { model.map.raum.raume?.size() > 1 ? true : false }, text: "Raum entfernen")
            button(id: "raumBearbeiten",           enabled: bind { model.map.raum.raume?.size() > 0 ? true : false }, text: "Raum bearbeiten")
            button(id: "raumKopieren",             enabled: bind { model.map.raum.raume?.size() > 0 ? true : false }, text: "Raum kopieren")
            */
        }
        vbox(constraints: EAST) {
            // TODO mmu Buttons "nach oben" und "nach unten" müssen rechts neben die Tabelle
            button(id: "raumNachObenVerschieben",  enabled: bind {1==1}, text: "^")
            button(id: "raumNachUntenVerschieben", enabled: bind {1==1}, text: "v")
            /*
            // TODO mmu Buttons "nach oben" und "nach unten" müssen rechts neben die Tabelle
            button(id: "raumNachObenVerschieben",  enabled: bind { model.map.raum.raume?.size() > 0 ? true : false }, text: "^")
            button(id: "raumNachUntenVerschieben", enabled: bind { model.map.raum.raume?.size() > 0 ? true : false }, text: "v")
            */
        }
    }
}
// Format fields
GH.recurse(raumEingabePanel, GH.yellowTextField)
[raumFlache, raumHohe, raumZuluftfaktor, raumAbluftVs].each {
    GH.autoformatDoubleTextField(it)
}
// Bindings
build(RaumdatenBindings)

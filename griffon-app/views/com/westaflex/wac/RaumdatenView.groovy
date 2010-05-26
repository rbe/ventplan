package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout

panel(id: "raumPanel") {
	borderLayout()
	// Raum anlegen
	panel(id: "raumEingabePanel", constraints: NORTH, layout: new MigLayout("fillx", "[fill]")) {
		label("Raumtyp")
		label("abw. Raumbezeichnung")
		label("Geschoss")
		label("Luftart")
		label("Raumfläche (m²)")
		label("Raumhöhe (m)")
		label("Zuluftfaktor")
		label("Abluftvolumenstrom (m³)", constraints: "wrap")
		comboBox(id: "raumTyp", items: model.map.raum.typ)
		textField(id: "raumBezeichnung")
		comboBox(id: "raumGeschoss", items: model.map.raum.geschoss)
		comboBox(id: "raumLuftart", items: model.map.raum.luftart)
		textField(id: "raumFlache")
		textField(id: "raumHohe", text: "2,50")
		textField(id: "raumZuluftfaktor", text: "3,00", editable: bind { raumLuftart.selectedItem != "ÜB" })
		textField(id: "raumAbluftVs",                   editable: bind { raumLuftart.selectedItem != "ÜB" })
		// Hinzufügen-Button aktivieren, wenn Fläche eingegeben wurde
		button(id: "raumHinzufugen", enabled: bind { !raumFlache.text.isEmpty() }, text: "Hinzufügen")
	}
	// Tabelle aller Räume
	panel(id: "raumTabellePanel", constraints: CENTER, layout: new MigLayout("fill", "[fill]")) {
		jideScrollPane() {
			table(id: "raumTabelle", model: model.createRaumTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
			}
		}
	}
	// Buttons
	hbox(constraints: SOUTH) {
		button(id: "raumEntfernen",            enabled: bind { /*raumTabelle.selectedRow > -1*/1 == 1 }, text: "Raum entfernen")
		button(id: "raumBearbeiten",           enabled: bind { /*raumTabelle.selectedRow > -1*/1 == 1 }, text: "Raum bearbeiten")
		button(id: "raumKopieren",             enabled: bind { /*raumTabelle.selectedRow > -1*/1 == 1 }, text: "Raum kopieren")
		button(id: "raumNachObenVerschieben",  enabled: bind { /*raumTabelle.selectedRow > -1*/1 == 1 }, text: "^")
		button(id: "raumNachUntenVerschieben", enabled: bind { /*raumTabelle.selectedRow > -1*/1 == 1 }, text: "v")
	}
	
}
GH.recurse(raumEingabePanel, GH.yellowTextField)
[raumFlache, raumHohe, raumZuluftfaktor, raumAbluftVs].each {
	GH.floatTextField(it)
	GH.rightAlignTextField(it)
}
// Bindings
build(RaumdatenBindings)

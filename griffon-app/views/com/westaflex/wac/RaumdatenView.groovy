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
		textField(id: "raumHohe")
		textField(id: "raumZuluftfaktor")
		textField(id: "raumAbluftVs")
		// Hinzufügen-Button aktivieren, wenn Fläche eingegeben wurde
		button(id: "raumHinzufugen", text: "Hinzufügen", enabled: bind { !raumFlache.text.isEmpty() })
	}
	// Tabelle aller Räume
	panel(id: "raumTabelle", constraints: CENTER, layout: new MigLayout("fill", "[fill]")) {
		jideScrollPane() {
			table(id: "raumTable", model: model.createRaumTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
			}
		}
	}
	// Buttons
	hbox(constraints: SOUTH) {
		button("Raum entfernen")
		button("Raum bearbeiten")
		button("Raum kopieren")
	}
}
GH.recurse(raumEingabePanel, GH.yellowTextField)
[raumFlache, raumHohe, raumZuluftfaktor, raumAbluftVs].each {
	GH.floatTextField(it)
	GH.rightAlignTextField(it)
}
// Bindings
build(RaumdatenBindings)

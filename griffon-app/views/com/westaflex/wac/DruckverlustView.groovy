package com.westaflex.wac

import net.miginfocom.swing.MigLayout

// Druckverlustberechnung
panel(id: "druckverlustTabPanel") {
	// Tabellen für 
	jideTabbedPane(id: "druckverlustTabGroup", constraints: "span") {
		// Druckverlustberechnung - Kanalnetz
		panel(id: "druckverlustKanalnetzTab", title: "Kanalnetz", layout: new MigLayout("fillx, wrap 7", "[left,fill]para[left,fill]para[left,fill]para[left,fill]para[left,fill]para[left]para[left]", "[fill]")) {
			
			label("Luftart", constraints: "width 100::150")
			label("Nr. Teilstrecke", constraints: "width 100::150")
			label("Luftmenge (m³/h)", constraints: "width 100::150")
			label("Kanalbezeichnung", constraints: "width 100::150")
			label("Länge (m)", constraints: "width 100::150")
			label("", constraints: "width 100::150")
			label("", constraints: "width 100::150")
			
			comboBox(id: "druckverlustKanalnetzLuftart")
			textField(id: "druckverlustKanalnetzNrTeilstrecke")
			textField(id: "druckverlustKanalnetzLuftmenge")
			comboBox(id: "druckverlustKanalnetzKanalbezeichnung")
			textField(id: "druckverlustKanalnetzLange")
			button(id: "druckverlustKanalnetzHinzufugen", text: "Hinzufügen")
			button(id: "druckverlustKanalnetzWiderstandswerte", text: "Widerstandsbeiwerte...")
			
			/*
			panel(id: "druckverlustKanalnetzTabellePanel", constraints: "cell 0 0 7 1", layout: new MigLayout("fillx", "[fill]")) {
				jideScrollPane() {
					table(id: "druckverlustKanalnetzTabelle", model: model.createDruckverlustKanalnetzTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
					}
				}
			}
			*/
			
			button(id: "", text: "Entfernen")
		}
		// Druckverlustberechnung - Ventileinstellung
		panel(id: "druckverlustVentileinstellungTab", title: "Ventileinstellung", layout: new MigLayout("fillx, wrap 6", "[left,fill]para[left,fill]para[left,fill]para[left,fill]para[left,fill]para[left,fill]", "[fill]")) {
		
			label("Luftart", constraints: "width 100::150")
			label("Raum", constraints: "width 100::150")
			label("Teilstrecken", constraints: "width 100::150")
			label("", constraints: "width 100::150")
			label("Ventilbezeichnung", constraints: "width 100::150")
			label("", constraints: "width 100::150")
			
			comboBox(id: "druckverlustVentileinstellungLuftart")
			textField(id: "druckverlustVentileinstellungRaum")
			textField(id: "druckverlustVentileinstellungTeilstrecken")
			button(id: "druckverlustVentileinstellungAuswahlen", text: "Auswählen")
			comboBox(id: "druckverlustVentileinstellungVentilbezeichnung")
			button(id: "druckverlustVentileinstellungHinzufugen", text: "Hinzufügen")
		
			/*
			panel(id: "druckverlustVentileinstellungTabellePanel", constraints: "cell 0 0 6 1", layout: new MigLayout("fillx", "[fill]")) {
				jideScrollPane() {
					table(id: "druckverlustVentileinstellungTabelle", model: model.createDruckverlustVentileinstellungTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
					}
				}
			}
			*/
			
			button(id: "", text: "Entfernen")
		}
	}
}
// druckverlustTabGroup
druckverlustTabGroup.with {
	setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
}

package com.westaflex.wac

import net.miginfocom.swing.MigLayout

// Raumvolumenströme
panel(id: "raumVsVentileTabPanel", layout: new MigLayout("fillx", "[left]para[right]para[left]para[fill]para[left]", "[fill]")) {
	// Tabellen für Zu-/Abluftventile, Überströmventile
	jideTabbedPane(id: "raumVsVentileTabGroup", constraints: "span") {
		// Raumvolumenströme - Zu-/Abluftventile
		panel(id: "raumVsZuAbluftventileTab", title: "Zu-/Abluftventile") {
			panel(id: "raumVsZuAbluftventileTabellePanel", constraints: "cell 0 0 5 1", layout: new MigLayout("fillx", "[fill]")) {
				jideScrollPane() {
					table(id: "raumVsZuAbluftventileTabelle", model: model.createRaumVsZuAbluftventileTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
					}
				}
			}
		}
		// Raumvolumenströme - Überströmventile
		panel(id: "raumVsUberstromventileTab", title: "Überströmventile") {
			panel(id: "raumVsUberstromventileTabellePanel", constraints: "cell 0 0 5 1", layout: new MigLayout("fillx", "[fill]")) {
				jideScrollPane() {
					table(id: "raumVsUberstromventileTabelle", model: model.createRaumVsUberstromventileTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
					}
				}
			}
		}
	}
	// Informationen
	label("Gesamtvolumen der Nutzungseinheit", constraints: "cell 0 1")
	label("62,50", constraints: "cell 1 1")
	label("m³", constraints: "cell 2 1")
	panel(id: "raumVsZuAbluftventileAussenVsDerLuftungstechnischenMassnahme", border: titledBorder("Außenluftvolumenstrom der lüftungstechnischen Massnahme"), layout: new MigLayout("fillx, wrap 4", "[left]30[right]10[left]30[left]", "[fill]"), constraints: "cell 3 1 1 5") {
		label("Feuchteschutz")
		label("50,00")
		label("m³/h")
		label("Zentralgerät")
		
		label("Reduzierte Lüftung")
		label("50,00")
		label("m³/h")
		comboBox(id: "raumVsZuAbluftventileZentralgerat")
		
		label("Nennlüftung")
		label("45,00")
		label("m³/h")
		label("Volumenstrom")
		
		label("Intensivlüftung")
		label("60,00")
		label("m³/h")
		comboBox(id: "raumVsZuAbluftventileVolumenstrom")
	}
	button(id: "raumVsZuAbluftventileRaumBearbeiten", text: "Raum bearbeiten", constraints: "cell 4 1")
	
	label("Luftwechsel der Nutzungseinheit", constraints: "cell 0 2")
	label("0,80", constraints: "cell 1 2")
	label("l/h", constraints: "cell 2 2")
	button(id: "raumVsZuAbluftventileSpeichern", text: "Speichern", constraints: "cell 4 2")
	
	label("Gesamtaußenluft-Volumentstrom mit Infiltration", constraints: "cell 0 3")
	label("50,00", constraints: "cell 1 3")
	label("m³/h", constraints: "cell 2 3")	
	button(id: "raumVsZuAbluftventileAngebotErstellen", text: "Angebot erstellen", constraints: "cell 4 3")
}
// raumVsVentileTabGroup
raumVsVentileTabGroup.with {
	setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
}

package com.westaflex.wac

import net.miginfocom.swing.MigLayout

panel(id: "raumdatenVsZuAbluftventile") {
	table(id: "raumdatenVsUberstromventileTable", constraints: "cell 0 0 5 1", model: model.createRaumVsUberstromventileModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
	}
	
	label("Gesamtvolumen der Nutzungseinheit", constraints: "cell 0 1")
	label("62,50", constraints: "cell 1 1")
	label("m³", constraints: "cell 2 1")
	//panel(id: "raumVsZuAbluftventileAussenVsDerLuftungstechnischenMassnahmeSub", title: "Außenluftvolumenstrom der lüftungstechnischen Maßnahme", layout: new MigLayout("fillx, wrap 4", "[left]30[right]10[left]30[left]", "[fill]"), constraints: "cell 3 0 1 5") {
	panel(id: "raumVsZuAbluftventileAussenVsDerLuftungstechnischenMassnahme", layout: new MigLayout("fillx, wrap 4", "[left]30[right]10[left]30[left]", "[fill]"), constraints: "cell 3 1 1 5") {
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

// Bindings
build(RaumVsZuAbluftventileBindings)

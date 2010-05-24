package com.westaflex.wac

import net.miginfocom.swing.MigLayout

// Notwendigkeit der lüftungstechnischen Maßnahme
panel(id: "aussenluftVsNotwendigkeitLuftungstechnischeMassnahme", border: titledBorder(title: "Notwendigkeit der lüftungstechnischen Maßnahme"), layout: new MigLayout("wrap", "[left]30[right]15[left]30[left]", "rel[]rel")) {
	label("Feuchteschutz: Gesamt-Außenluftvolumenstrom")
	label("15,00")
	label("m³/h")
	label("Lüftungstechnische Maßnahmen erforderlich!")
	
	label("Luftvolumenstrom durch Infiltration")
	label("15,00")
	label("m³/h")
	label("")
}

// Gesamt-Außenluftvolumenströme für Nutzungseinheit
panel(id: "aussenluftVsGesamtAussenluftVsNutzungseinheit", border: titledBorder(title: "Gesamt-Außenluftvolumenströme für Nutzungseinheit"), layout: new MigLayout("wrap", "[left]30[right]15[left]30[right]15[left]30[right]15[left]30[right]15[left]", "rel[]rel")) {
	label("")
	label("Lüftung zum Feuchteschutz")
	label("")
	label("Reduzierte Lüftung")
	label("")
	label("Nennlüftung")
	label("")
	label("Intensivlüftung")
	label("")
	
	label("Luftvolumenstrom")
	label("0,00")
	label("m³/h")
	label("0,00")
	label("m³/h")
	label("0,00")
	label("m³/h")
	label("0,00")
	label("m³/h")
	
	label("Luftwechsel")
	label("0,00")
	label("l/h")
	label("0,00")
	label("l/h")
	label("0,00")
	label("l/h")
	label("0,00")
	label("l/h")
}

// Gesamtabluftvolumenströme der Räume
panel(id: "aussenluftVsGesamtabluftVsRaume", border: titledBorder(title: "Gesamtabluftvolumenströme der Räume"), layout: new MigLayout("wrap", "[left]30[right]15[left]30[right]15[left]30[right]15[left]30[right]15[left]", "rel[]rel")) {
	label("")
	label("Lüftung zum Feuchteschutz")
	label("")
	label("Reduzierte Lüftung")
	label("")
	label("Nennlüftung")
	label("")
	label("Intensivlüftung")
	label("")
	
	label("Luftvolumenstrom")
	label("0,00")
	label("m³/h")
	label("0,00")
	label("m³/h")
	label("0,00")
	label("m³/h")
	label("0,00")
	label("m³/h")
	
	label("Luftwechsel")
	label("0,00")
	label("l/h")
	label("0,00")
	label("l/h")
	label("0,00")
	label("l/h")
	label("0,00")
	label("l/h")
}

// personenbezogene Gesamt-Abluftvolumenströme
panel(id: "aussenluftVsPersonenbezogeneGesamtabluftVs", border: titledBorder(title: "personenbezogene Gesamt-Abluftvolumenströme"), layout: new MigLayout("wrap", "[left]30[right]15[left]30[right]15[left]30[right]15[left]30[right]15[left]", "rel[]rel")) {
	label("")
	label("Lüftung zum Feuchteschutz")
	label("")
	label("Reduzierte Lüftung")
	label("")
	label("Nennlüftung")
	label("")
	label("Intensivlüftung")
	label("")
	
	label("Luftvolumenstrom")
	label("0,00")
	label("m³/h")
	label("0,00")
	label("m³/h")
	label("0,00")
	label("m³/h")
	label("0,00")
	label("m³/h")
	
	label("Luftwechsel")
	label("0,00")
	label("l/h")
	label("0,00")
	label("l/h")
	label("0,00")
	label("l/h")
	label("0,00")
	label("l/h")
}

// Gesamt-Luftvolumenstrom für lüftungstechnische Maßnahmen
panel(id: "aussenluftVsGesamtLuftVsLuftungstechnischeMassnahmen", border: titledBorder(title: "Gesamt-Luftvolumenstrom für lüftungstechnische Maßnahmen"), layout: new MigLayout("wrap", "[left]30[right]15[left]30[right]15[left]30[right]15[left]30[left]", "rel[]rel")) {
	label("")
	label("Reduzierte Lüftung")
	label("")
	label("Nennlüftung")
	label("")
	label("Intensivlüftung")
	label("", constraints: "span")
	
	label("Luftvolumenstrom")
	label("0,00")
	label("m³/h")
	label("0,00")
	label("m³/h")
	label("0,00")
	label("m³/h")
	checkBox(id: "aussenluftVsGesamtLuftVsLuftungstechnischeMassnahmenInfiltrationsanteilBerechnen", selected: false, text: "mit Infiltrationsanteil berechnen")
	
	label("Luftwechsel")
	label("0,00")
	label("l/h")
	label("0,00")
	label("l/h")
	label("0,00")
	label("l/h", constraints: "span")
}
package com.westaflex.wac

import net.miginfocom.swing.MigLayout

// Notwendigkeit der lüftungstechnischen Maßnahme
panel(id: "aussenluftVsNotwendigkeitLuftungstechnischeMassnahme", border: titledBorder(title: "Notwendigkeit der lüftungstechnischen Maßnahme"), layout: new MigLayout("wrap", "[left]30[right]15[left]30[left]", "rel[]rel")) {
	label("Feuchteschutz: Gesamt-Außenluftvolumenstrom")
	label(id: "aussenluftVsGesamt", text: "  0,00")
	label("m³/h")
	label(id: "aussenluftVsMassnahme", " ", foreground: java.awt.Color.RED)
	
	label("Luftvolumenstrom durch Infiltration")
	label(id: "aussenluftVsInfiltration", text: "  0,00")
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
	label(id: "gesamtAvsNE11", text: "  0,00")
	label("m³/h")
	label(id: "gesamtAvsNE12", text: "  0,00")
	label("m³/h")
	label(id: "gesamtAvsNE13", text: "  0,00")
	label("m³/h")
	label(id: "gesamtAvsNE14", text: "  0,00")
	label("m³/h")
	
	label("Luftwechsel")
	label(id: "gesamtAvsNE21", text: "  0,00")
	label("l/h")
	label(id: "gesamtAvsNE22", text: "  0,00")
	label("l/h")
	label(id: "gesamtAvsNE23", text: "  0,00")
	label("l/h")
	label(id: "gesamtAvsNE24", text: "  0,00")
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
	label(id: "gesamtAvsR11", text: "  0,00")
	label("m³/h")
	label(id: "gesamtAvsR12", text: "  0,00")
	label("m³/h")
	label(id: "gesamtAvsR13", text: "  0,00")
	label("m³/h")
	label(id: "gesamtAvsR14", text: "  0,00")
	label("m³/h")
	
	label("Luftwechsel")
	label(id: "gesamtAvsR21", text: "  0,00")
	label("l/h")
	label(id: "gesamtAvsR22", text: "  0,00")
	label("l/h")
	label(id: "gesamtAvsR23", text: "  0,00")
	label("l/h")
	label(id: "gesamtAvsR24", text: "  0,00")
	label("l/h")
}

// personenbezogene Gesamt-Abluftvolumenströme
panel(id: "aussenluftVsPersonenbezogeneGesamtabluftVs", border: titledBorder(title: "Personenbezogene Gesamt-Abluftvolumenströme"), layout: new MigLayout("wrap", "[left]30[right]15[left]30[right]15[left]30[right]15[left]30[right]15[left]", "rel[]rel")) {
	label("")
	label(id: "aussenluftVsLuftungZumFeuchteschutzLabel", "Lüftung zum Feuchteschutz")
	label("")
	label("Reduzierte Lüftung")
	label("")
	label("Nennlüftung")
	label("")
	label("Intensivlüftung")
	label("")
	
	label("Luftvolumenstrom")
	label(id: "personenGesamtAvs11", text: "  0,00")
	label("m³/h")
	label(id: "personenGesamtAvs12", text: "  0,00")
	label("m³/h")
	label(id: "personenGesamtAvs13", text: "  0,00")
	label("m³/h")
	label(id: "personenGesamtAvs14", text: "  0,00")
	label("m³/h")
	
	label("Luftwechsel")
	label(id: "personenGesamtAvs21", text: "  0,00")
	label("l/h")
	label(id: "personenGesamtAvs22", text: "  0,00")
	label("l/h")
	label(id: "personenGesamtAvs23", text: "  0,00")
	label("l/h")
	label(id: "personenGesamtAvs24", text: "  0,00")
	label("l/h")
}

// Gesamt-Luftvolumenstrom für lüftungstechnische Maßnahmen
panel(id: "aussenluftVsGesamtLuftVsLuftungstechnischeMassnahmen", border: titledBorder(title: "Gesamt-Luftvolumenstrom für lüftungstechnische Maßnahmen"), layout: new MigLayout("wrap", "[left]30[right]15[left]30[right]15[left]30[right]15[left]30[left]", "rel[]rel")) {
	label("")
	label(id: "aussenluftVsGesamtLuftVsLuftungstechnischeMassnahmenLabel", "Reduzierte Lüftung", size: [aussenluftVsLuftungZumFeuchteschutzLabel.width, aussenluftVsLuftungZumFeuchteschutzLabel.height])
	label("")
	label("Nennlüftung")
	label("")
	label("Intensivlüftung")
	label("", constraints: "span")
	
	label("Luftvolumenstrom")
	label(id: "gesamtAvsLM11", text: "  0,00")
	label("m³/h")
	label(id: "gesamtAvsLM12", text: "  0,00")
	label("m³/h")
	label(id: "gesamtAvsLM13", text: "  0,00")
	label("m³/h")
	checkBox(id: "aussenluftVsGesamtLMMitInfiltrationsanteilBerechnen", selected: false, text: "mit Infiltrationsanteil berechnen")
	
	label("Luftwechsel")
	label(id: "gesamtAvsLM21", text: "  0,00")
	label("l/h")
	label(id: "gesamtAvsLM22", text: "  0,00")
	label("l/h")
	label(id: "gesamtAvsLM23", text: "  0,00")
	label("l/h", constraints: "span")
}

button(text: "Berechnen", actionPerformed: controller.berechneAussenluftVs)
// Bindings
build(AussenluftVsBindings)

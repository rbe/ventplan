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
	label(id: "gesamtAvsNeLvsFs", text: "  0,00"); label("m³/h")
	label(id: "gesamtAvsNeLvsRl", text: "  0,00"); label("m³/h")
	label(id: "gesamtAvsNeLvsNl", text: "  0,00"); label("m³/h")
	label(id: "gesamtAvsNeLvsIl", text: "  0,00"); label("m³/h")
	
	label("Luftwechsel")
	label(id: "gesamtAvsNeLwFs", text: "  0,00"); label("l/h")
	label(id: "gesamtAvsNeLwRl", text: "  0,00"); label("l/h")
	label(id: "gesamtAvsNeLwNl", text: "  0,00"); label("l/h")
	label(id: "gesamtAvsNeLwIl", text: "  0,00"); label("l/h")
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
	label(id: "gesamtAvsRaumLvsFs", text: "  0,00"); label("m³/h")
	label(id: "gesamtAvsRaumLvsRl", text: "  0,00"); label("m³/h")
	label(id: "gesamtAvsRaumLvsNl", text: "  0,00"); label("m³/h")
	label(id: "gesamtAvsRaumLvsIl", text: "  0,00"); label("m³/h")
	
	label("Luftwechsel")
	label(id: "gesamtAvsRaumLwFs", text: "  0,00"); label("l/h")
	label(id: "gesamtAvsRaumLwRl", text: "  0,00"); label("l/h")
	label(id: "gesamtAvsRaumLwNl", text: "  0,00"); label("l/h")
	label(id: "gesamtAvsRaumLwIl", text: "  0,00"); label("l/h")
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
	label(id: "gesamtAvsPersonLvsFs", text: "  0,00"); label("m³/h")
	label(id: "gesamtAvsPersonLvsRl", text: "  0,00"); label("m³/h")
	label(id: "gesamtAvsPersonLvsNl", text: "  0,00"); label("m³/h")
	label(id: "gesamtAvsPersonLvsIl", text: "  0,00"); label("m³/h")
	
	label("Luftwechsel")
	label(id: "gesamtAvsPersonLwFs", text: "  0,00"); label("l/h")
	label(id: "gesamtAvsPersonLwRl", text: "  0,00"); label("l/h")
	label(id: "gesamtAvsPersonLwNl", text: "  0,00"); label("l/h")
	label(id: "gesamtAvsPersonLwIl", text: "  0,00"); label("l/h")
}

// Gesamt-Luftvolumenstrom für lüftungstechnische Maßnahmen
panel(id: "aussenluftVsGesamtLTM", border: titledBorder(title: "Gesamt-Luftvolumenstrom für lüftungstechnische Maßnahmen"), layout: new MigLayout("wrap", "[left]30[right]15[left]30[right]15[left]30[right]15[left]30[left]", "rel[]rel")) {
	label("")
	label(id: "aussenluftVsGesamtLTMLabel", "Reduzierte Lüftung", size: [aussenluftVsLuftungZumFeuchteschutzLabel.width, aussenluftVsLuftungZumFeuchteschutzLabel.height])
	label("")
	label("Nennlüftung")
	label("")
	label("Intensivlüftung")
	label("", constraints: "span")
	
	label("Luftvolumenstrom")
	label(id: "gesamtLvsLtmLvsRl", text: "  0,00"); label("m³/h")
	label(id: "gesamtLvsLtmLvsNl", text: "  0,00"); label("m³/h")
	label(id: "gesamtLvsLtmLvsIl", text: "  0,00"); label("m³/h")
	checkBox(id: "aussenluftVsGesamtLtmInfiltrationsanteil", selected: false, text: "mit Infiltrationsanteil berechnen")
	
	label("Luftwechsel")
	label(id: "gesamtLvsLtmLwRl", text: "  0,00"); label("l/h")
	label(id: "gesamtLvsLtmLwNl", text: "  0,00"); label("l/h")
	label(id: "gesamtLvsLtmLwIl", text: "  0,00"); label("l/h", constraints: "span")
}

button(text: "Berechnen", actionPerformed: controller.berechneAussenluftVs)
// Bindings
build(AussenluftVsBindings)

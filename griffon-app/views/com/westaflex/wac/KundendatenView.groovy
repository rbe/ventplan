package com.westaflex.wac

import net.miginfocom.swing.MigLayout

// Großhandel
panel(border: titledBorder(title: "Kunde 1 (Großhandel)"), layout: new MigLayout("fill, insets 5, gap 5, wrap 2", "[][grow]", "[][]")) {
	// Row 1
	label("Firma 1")
	textField(id: "grosshandelFirma1", constraints: "growx")
	// Row 2
	label("Firma 2")
	textField(id: "grosshandelFirma2", constraints: "growx")
	// Row 3
	label("Strasse")
	textField(id: "grosshandelStrasse", constraints: "growx")
	// Row 4
	label("PLZ / Ort")
	panel(constraints: "grow", layout: new MigLayout("fill, insets 2, gap 2", "[grow][grow]", "[grow]")) {
		textField(id: "grosshandelPlz", constraints: "growx")
		textField(id: "grosshandelOrt", constraints: "growx")
	}
	// Row 5
	label("Telefon")
	textField(id: "grosshandelTelefon", constraints: "growx")
	// Row 6
	label("Telefax")
	textField(id: "grosshandelTelefax", constraints: "growx")
	// Row 7
	label("Ansprechpartner")
	textField(id: "grosshandelAnsprechpartner", constraints: "growx")
}
// Ausführende Firma
panel(border: titledBorder(title: "Kunde 2 (Ausführende Firma)"), layout: new MigLayout("fill, insets 5, gap 5, wrap 2", "[][grow]", "[][]")) {
	// Row 1
	label("Firma 1")
	textField(id: "ausfuhrendeFirmaFirma1", constraints: "growx")
	// Row 2
	label("Firma 2")
	textField(id: "ausfuhrendeFirmaFirma2", constraints: "growx")
	// Row 3
	label("Strasse")
	textField(id: "ausfuhrendeFirmaStrasse", constraints: "growx")
	// Row 4
	label("PLZ / Ort")
	panel(constraints: "grow", layout: new MigLayout("fill, insets 2, gap 2", "[grow][grow]", "[grow]")) {
		textField(id: "ausfuhrendeFirmaPlz", constraints: "growx")
		textField(id: "ausfuhrendeFirmaOrt", constraints: "growx")
	}
	// Row 5
	label("Telefon")
	textField(id: "ausfuhrendeFirmaTelefon", constraints: "growx")
	// Row 6
	label("Telefax")
	textField(id: "ausfuhrendeFirmaTelefax", constraints: "growx")
	// Row 7
	label("Ansprechpartner")
	textField(id: "ausfuhrendeFirmaAnsprechpartner", constraints: "growx")
}
// Notizen
panel(border: titledBorder(title: "Notizen"), constraints: "span", layout: new MigLayout("fill, wrap 2", "[][grow]", "[][grow]")) {
	// Bauvorhaben
	label("Bauvorhaben")
	textField(id: "bauvorhaben", constraints: "growx")
	// Notizen
	label("Notizen")
	scrollPane(constraints: "grow") {
		textArea(id: "notizen", constraints: "grow")
	}
}
// Bindings
build(KundendatenBindings)
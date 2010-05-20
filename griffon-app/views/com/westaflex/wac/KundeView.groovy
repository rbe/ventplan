package com.westaflex.wac

panel(id: "kundeTab", border: titledBorder(title: "Kundendaten")) {
	gridLayout(columns: 2, rows: 5)
	// Row 1
	label("Firma 1")
	textField(id: "kunde1")
	// Row 2
	label("Firma 2")
	textField(id: "kunde2")
}

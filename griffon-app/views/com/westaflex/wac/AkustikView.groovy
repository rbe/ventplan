package com.westaflex.wac

import net.miginfocom.swing.MigLayout

// Akustikberechnung
panel(id: "akustikTabPanel") {
	// Tabellen für 
	jideTabbedPane(id: "akustikTabGroup", constraints: "span") {
		buildLayout("Zuluft")
		
		buildLayout("Abluft")
	}
}
// akustikTabGroup
akustikTabGroup.with {
	setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
}

/**
 * Synchronize all Swing table models depending on map.raum.raume.
 */
def buildLayout(tabname) {
	
	// Akustikberechnung - Zuluft
	panel(id: "akustik" + tabname + "Tab", title: tabname, layout: new MigLayout("fillx, wrap 4", "[left,fill]para[right,fill]para[center,fill]para[left,fill]", "[fill]")) {
		label("Raumbezeichnung", constraints: "cell 0 0")
		label("", constraints: "cell 1 0, width 150::200")
		// TODO: Zentralgerät aus RaumVsView -> raumVsZuAbluftventileZentralgerat
		label("Zentrales Lüftungsgerät " + raumVsZuAbluftventileZentralgerat.selectedItem, constraints: "cell 2 0")
		label("", constraints: "cell 3 0")
		
		comboBox(id: "akustik" + tabname + "Raumbezeichnung", constraints: "cell 0 1")
		label("", constraints: "cell 1 1")
		label(tabname, constraints: "cell 2 1")
		label("", constraints: "cell 3 1")
		
		label("", constraints: "cell 0 2")
		label("", constraints: "cell 1 2")
		label("Oktavmittenfrequenz in Hz", constraints: "cell 2 2")
		label("", constraints: "cell 3 2")

		label("", constraints: "cell 0 3")
		label("", constraints: "cell 1 3")
		// TODO table here!!! constraints: "cell 2 3 13 1"
		label("dB(A)", constraints: "cell 3 3")
		
		label("Schallleistungspegel Zuluftstutzen", constraints: "cell 0 4")
		// TODO: Zentralgerät aus RaumVsView -> raumVsZuAbluftventileZentralgerat
		// TODO: split -> comboBox...???
		label(raumVsZuAbluftventileZentralgerat.selectedItem, constraints: "cell 1 4")
		label("", constraints: "cell 2 4")
		// TODO: mittelwert der 1. Reihe von der Tabelle?
		label("", constraints: "cell 3 4")
		
		label("Schallleistungspegelerhöhung Kanalnetz", constraints: "cell 0 5")
		comboBox(id: "akustik" + tabname + "Kanalnetz", constraints: "cell 1 5")
		label("", constraints: "cell 3 5")
		
		label("Schallleistungspegelerhöhung Filter", constraints: "cell 0 6")
		comboBox(id: "akustik" + tabname + "Filter", constraints: "cell 1 6")
		label("", constraints: "cell 3 6")
		
		label("1. Hauptschalldämpfer", constraints: "cell 0 7")
		comboBox(id: "akustik" + tabname + "1Hauptschalldampfer", constraints: "cell 1 7")
		label("", constraints: "cell 3 7")
		
		label("2. Hauptschalldämpfer", constraints: "cell 0 8")
		comboBox(id: "akustik" + tabname + "2Hauptschalldampfer", constraints: "cell 1 8")
		label("", constraints: "cell 3 8")
		
		label("Anzahl der Umlenkungen 90° Stck.", constraints: "cell 0 9")
		textField(id: "akustik" + tabname + "AnzahlUmlenkungen90GradStck", constraints: "cell 1 9")
		label("", constraints: "cell 3 9")
		
		label("Luftverteilerkasten Stck.", constraints: "cell 0 10")
		textField(id: "akustik" + tabname + "LuftverteilerkastenStck", constraints: "cell 1 10")
		label("", constraints: "cell 3 10")
		
		label("Längsdämpfung Kanal lfdm.", constraints: "cell 0 11")
		// TODO: split ???
		textField(id: "akustik" + tabname + "LangsdampfungKanal", constraints: "cell 1 11")
		label("", constraints: "cell 3 11")
		
		label("Schalldämpfer Ventil", constraints: "cell 0 12")
		comboBox(id: "akustik" + tabname + "SchalldampferVentil", constraints: "cell 1 12")
		label("", constraints: "cell 3 12")
		
		label("Einfügungsdämmwert Luftdurchlass", constraints: "cell 0 13")
		comboBox(id: "akustik" + tabname + "EinfugungsdammwertLuftdurchlass", constraints: "cell 1 13")
		label("", constraints: "cell 3 13")
		
		label("Raumabsorption (Ahnnahme) BAD=0 WOHNEN=1", constraints: "cell 0 14")
		textField(id: "akustik" + tabname + "Raumabsorption", constraints: "cell 1 14")
		label("", constraints: "cell 3 14")
		
		label("Korrektur der A-Bewertung", constraints: "cell 0 15")
		label("", constraints: "cell 1 15")
		label("", constraints: "cell 3 15")
		
		label("Bewerteter Schallpegel", constraints: "cell 0 16")
		label("", constraints: "cell 1 16")
		label("Mittlerer Schalldruckpegel* dB(A) =", constraints: "cell 2 16")
		label("", constraints: "cell 3 16") // Wert aus Berechnung
		
		label("* Bei dieser Berechnung handelt es dich um eine theoretische Auslegung, deren Werte in der Praxis abweichen können", constraints: "span")
	}
	
	return "akustik" + tabname + "Tab"
}

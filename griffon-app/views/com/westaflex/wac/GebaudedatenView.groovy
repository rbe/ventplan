package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout

// Gebäudetyp
panel(id: "gebaudeTyp", border: titledBorder(title: "Gebäudetyp"), layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
	buttonGroup().with {
		add radioButton(id: "gebaudeTypMFH", text: "Mehrfamilienhaus MFH")
		add radioButton(id: "gebaudeTypEFH", text: "Einfamilienhaus EFH")
		add radioButton(id: "gebaudeTypMaisonette", text: "Maisonette")
	}
}
// Gebäudelage
panel(id: "gebaudeLage", border: titledBorder(title: "Gebäudelage"), layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
	buttonGroup().with {
		add radioButton(id: "gebaudeLageWindschwach", text: "windschwach")
		add radioButton(id: "gebaudeLageWindstark", text: "windstark")
	}
}
// Wärmeschutz
panel(id: "gebaudewarmeschutz", border: titledBorder(title: "Wärmeschutz"), layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
	buttonGroup().with {
		add radioButton(id: "gebaudeWarmeschutzHoch", text: "hoch (Neubau / Sanierung mind. WSchV 1995)")
		add radioButton(id: "gebaudeWarmeschutzNiedrig", text: "niedrig (Gebäude bestand vor 1995)")
	}
}
// Geometrie
panel(id: "gebaudeGeometrie", border: titledBorder(title: "Geometrie"), layout: new MigLayout("fillx, wrap 3", "[fill][fill][fill]", "")) {
	//
	textField(id: "gebaudeGeometrieWohnflache")
	label("m²")
	label("Wohnfläche der Nutzungseinheit")
	//
	textField(id: "gebaudeGeometrieMittlereRaumhohe")
	label("m")
	label("mittlere Raumhöhe")
	//
	textField(id: "gebaudeGeometrieLuftvolumen", editable: false)
	label("m³")
	label("Luftvolumen der Nutzungseinheit")
	//
	textField(id: "gebaudeGeometrieGelufteteFlache")
	label("m²")
	label("gelüftete Fläche")
	//
	textField(id: "gebaudeGeometrieGeluftetesVolumen", editable: false)
	label("m³")
	label("gelüftetes Volumen")
}
GH.recurse(gebaudeGeometrie, GH.doubleTextField)
// Luftdichtheit der Gebäudehülle
panel(id: "gebaudeLuftdichtheit", border: titledBorder(title: "Luftdichtheit der Gebäudehülle"), constraints: "span 2", layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
	buttonGroup().with {
		hbox {
			vbox {
				add radioButton(id: "gebaudeLuftdichtheitKategorieA", text: "Kategorie A (ventilatorgestützt)")
				add radioButton(id: "gebaudeLuftdichtheitKategorieB", text: "Kategorie B (frei, Neubau)")
				add radioButton(id: "gebaudeLuftdichtheitKategorieC", text: "Kategorie C (frei, Bestand)")
			}
			vbox {
				add radioButton(id: "gebaudeLuftdichtheitMesswerte", text: "Messwerte")
				hbox {
					textField(id: "gebaudeLuftdichtheitDruckdifferenz")
					label("Druckdifferenz in Pa")
				}
				hbox {
					textField(id: "gebaudeLuftdichtheitLuftwechsel")
					label("Luftwechsel in 1/h")
				}
				hbox {
					textField(id: "gebaudeLuftdichtheitDruckexponent")
					label("Druckexponent")
				}
			}
		}
	}
}
GH.recurse(gebaudeLuftdichtheit, GH.doubleTextField)
// Besondere Anforderungen
panel(id: "gebaudeBesondereAnforderungen", border: titledBorder(title: "Besondere Anforderungen", constraints: "span"), layout: new MigLayout("fillx, wrap 2", "[fill][fill]", "[fill]")) {
	textField(id: "faktorBesondereAnforderungen", constraints: "growx")
	label("Faktor für besondere bauphysikalische oder hygienische Anforderungen")
}
GH.doubleTextField(faktorBesondereAnforderungen)
// Geplante Belegung
panel(id: "gebaudeGeplanteBelegung", border: titledBorder(title: "Geplante Belegung", constraints: "span"), layout: new MigLayout("fillx, wrap 4", "[fill][fill]", "[fill]")) {
	label("Personenanzahl")
	spinner(id: "gebaudeGeplantePersonenanzahl")
	label("Außenluftvolumenstrom pro Person")
	spinner(id: "gebaudeGeplanteAussenluftVsProPerson")
	//
	label("Mindestaußenluftrate:", foreground: java.awt.Color.RED)
	label(id: "gebaudeGeplanteMindestaussenluftrate", foreground: java.awt.Color.RED, text: "0")
	label("m³/h", foreground: java.awt.Color.RED)
}
GH.doubleTextField(gebaudeGeplanteAussenluftVsProPerson)
// Bindings
build(GebaudedatenBindings)

package com.westaflex.wac

import com.jidesoft.plaf.LookAndFeelFactory
import com.jidesoft.swing.JideTabbedPane
import net.miginfocom.swing.MigLayout

jideTabbedPane(projektTabGroup, selectedIndex: projektTabGroup.tabCount) {
	panel(id: "projektTab", title: tabName) {
		borderLayout()
		jideTabbedPane(id: "datenTabGroup") {
			// Kundendaten
			panel(id: "kundenTab", title: "Kundendaten", layout: new MigLayout("fill, wrap 2", "[fill][fill]", "[fill][fill]")) {
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
					textField(id: "ausfuehrendeFirmaFirma1", constraints: "growx")
					// Row 2
					label("Firma 2")
					textField(id: "ausfuehrendeFirmaFirma2", constraints: "growx")
					// Row 3
					label("Strasse")
					textField(id: "ausfuehrendeFirmaStrasse", constraints: "growx")
					// Row 4
					label("PLZ / Ort")
					panel(constraints: "grow", layout: new MigLayout("fill, insets 2, gap 2", "[grow][grow]", "[grow]")) {
						textField(id: "ausfuehrendeFirmaPlz", constraints: "growx")
						textField(id: "ausfuehrendeFirmaOrt", constraints: "growx")
					}
					// Row 5
					label("Telefon")
					textField(id: "ausfuehrendeFirmaTelefon", constraints: "growx")
					// Row 6
					label("Telefax")
					textField(id: "ausfuehrendeFirmaTelefax", constraints: "growx")
					// Row 7
					label("Ansprechpartner")
					textField(id: "ausfuehrendeFirmaAnsprechpartner", constraints: "growx")
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
			}
			// Gebäudedaten
			panel(id: "gebaeudeTab", title: "Gebäudedaten", layout: new MigLayout("fill, wrap 3", "[fill][fill][fill]", "[fill][fill]")) {
				// Gebäudetyp
				panel(id: "gebaeudeTyp", border: titledBorder(title: "Gebäudetyp"), layout: new MigLayout("fill, wrap 1", "[fill]", "[fill]")) {
					buttonGroup().with {  
						add radioButton(id: "gebaeudeTypMFH", text: "Mehrfamilienhaus MFH"/*, selected: bind(source: model.map.gebaeude, sourceProperty: "gebaeudeTypMFH", mutual: true)*/)
						add radioButton(id: "gebaeudeTypEFH", text: "Einfamilienhaus EFH"/*, selected: bind(source: model.map.gebaeude, sourceProperty: "gebaeudeTypEFH", mutual: true)*/)
						add radioButton(id: "gebaeudeTypMaisonette", text: "Maisonette"/*, selected: bind(source: model.map.gebaeude, sourceProperty: "gebaeudeTypMaisonette", mutual: true)*/)
					}
				}
				// Gebäudelage
				panel(id: "gebaeudeLage", border: titledBorder(title: "Gebäudelage"), layout: new MigLayout("fill, wrap 1", "[fill]", "[fill]")) {
					buttonGroup().with {  
						add radioButton(id: "gebaeudeLageWindschwach", text: "windschwach"/*, selected: bind(source: model.map.gebaeude, sourceProperty: "gebaeudeLageWindschwach", mutual: true)*/)
						add radioButton(id: "gebaeudeLageWindstark", text: "windstark"/*, selected: bind(source: model.map.gebaeude, sourceProperty: "gebaeudeLageWindstark", mutual: true)*/)
					}
				}
				// Wärmeschutz
				panel(id: "gebaeudeWaermeschutz", border: titledBorder(title: "Wärmeschutz"), layout: new MigLayout("fill, wrap 1", "[fill]", "[fill]")) {
					buttonGroup().with {  
						add radioButton(id: "gebaeudeWaermeschutzHoch", text: "hoch (Neubau / Sanierung mind. WSchV 1995)"/*, selected: bind(source: model.map.gebaeude, sourceProperty: "gebaeudeWaermeschutzHoch", mutual: true)*/)
						add radioButton(id: "gebaeudeWaermeschutzNiedrig", text: "niedrig (Gebäude bestand vor 1995)"/*, selected: bind(source: model.map.gebaeude, sourceProperty: "gebaeudeWaermeschutzNiedrig", mutual: true)*/)
					}
				}
				// Geometrie
				panel(id: "gebaeudeGeometrie", border: titledBorder(title: "Geometrie")) {
					
				}
				// Luftdichtheit der Gebäudehülle
				panel(id: "gebaeudeLuftdichtheit", border: titledBorder(title: "Luftdichtheit der Gebäudehülle"), constraints: "span 2", layout: new MigLayout("fill, wrap 1", "[fill]", "[fill]")) {
					buttonGroup().with {  
						add radioButton(text: "Kategorie A (ventilatorgestützt)")
						add radioButton(text: "Kategorie B (frei, Neubau)")
						add radioButton(text: "Kategorie C (frei, Bestand)")
					}
				}
				// Besondere Anforderungen
				panel(id: "gebaeudeBesondereAnforderungen", border: titledBorder(title: "Besondere Anforderungen", constraints: "span"), layout: new MigLayout("fill, wrap 2", "[fill][fill]", "[fill]")) {
					textField(id: "faktorBesondereAnforderungen", constraints: "growx")
					label("Faktor für besondere bauphysikalische oder hygienische Anforderungen")
				}
				// Geplante Belegung
				panel(id: "gebaeudeGeplanteBelegung", border: titledBorder(title: "Geplante Belegung", constraints: "span")) {
					label("Personenanzahl")
					textField()
				}
			}
			// Analgendaten
			panel(id: "anlageTab", title: "Anlagedaten") {
				borderLayout()
			}
			// Raumdaten
			panel(id: "raumTab", title: "Raumdaten") {
				borderLayout()
			}
			// Außenluftvolumenströme
			panel(id: "aussenluftVsTab", title: "Außenluftvolumenströme") {
				borderLayout()
			}
			// Raumvolumenströme
			panel(id: "raumVsTab", title: "Raumvolumenströme") {
				borderLayout()
			}
			// Druckverlustberechnung
			panel(id: "druckverlustTab", title: "Druckverlustberechnung") {
				borderLayout()
			}
			// Akkustikberechnung
			panel(id: "akkustikTab", title: "Akkustikberechnung") {
				borderLayout()
			}
		}
		hbox(constraints: SOUTH) {
			button(halloAction)
		}
	}
	
}
//
// Bindings
//
// Kundendaten - Großhandel
bind(source: model.map.grosshandel,       sourceProperty: "firma1",           target: grosshandelFirma1,                targetProperty: "text", mutual: true)
bind(source: model.map.grosshandel,       sourceProperty: "firma2",           target: grosshandelFirma2,                targetProperty: "text", mutual: true)
bind(source: model.map.grosshandel,       sourceProperty: "strasse",          target: grosshandelStrasse,               targetProperty: "text", mutual: true)
bind(source: model.map.grosshandel,       sourceProperty: "plz",              target: grosshandelPlz,                   targetProperty: "text", mutual: true)
bind(source: model.map.grosshandel,       sourceProperty: "ort",              target: grosshandelOrt,                   targetProperty: "text", mutual: true)
bind(source: model.map.grosshandel,       sourceProperty: "telefon",          target: grosshandelTelefon,               targetProperty: "text", mutual: true)
bind(source: model.map.grosshandel,       sourceProperty: "telefax",          target: grosshandelTelefax,               targetProperty: "text", mutual: true)
bind(source: model.map.grosshandel,       sourceProperty: "ansprechpartner",  target: grosshandelAnsprechpartner,       targetProperty: "text", mutual: true)
// Kundendaten - Ausführende Firma
bind(source: model.map.ausfuehrendeFirma, sourceProperty: "firma1",         , target: ausfuehrendeFirmaFirma1,          targetProperty: "text", mutual: true)
bind(source: model.map.ausfuehrendeFirma, sourceProperty: "firma2",         , target: ausfuehrendeFirmaFirma2,          targetProperty: "text", mutual: true)
bind(source: model.map.ausfuehrendeFirma, sourceProperty: "strasse",        , target: ausfuehrendeFirmaStrasse,         targetProperty: "text", mutual: true)
bind(source: model.map.ausfuehrendeFirma, sourceProperty: "plz",            , target: ausfuehrendeFirmaPlz,             targetProperty: "text", mutual: true)
bind(source: model.map.ausfuehrendeFirma, sourceProperty: "ort",            , target: ausfuehrendeFirmaOrt,             targetProperty: "text", mutual: true)
bind(source: model.map.ausfuehrendeFirma, sourceProperty: "telefon",        , target: ausfuehrendeFirmaTelefon,         targetProperty: "text", mutual: true)
bind(source: model.map.ausfuehrendeFirma, sourceProperty: "telefax",        , target: ausfuehrendeFirmaTelefax,         targetProperty: "text", mutual: true)
bind(source: model.map.ausfuehrendeFirma, sourceProperty: "ansprechpartner" , target: ausfuehrendeFirmaAnsprechpartner, targetProperty: "text", mutual: true)
// Kundendaten - Notizen
bind(source: model.map, sourceProperty: "bauvorhaben", target: bauvorhaben, targetProperty: "text", mutual: true)
bind(source: model.map, sourceProperty: "notizen",     target: notizen,     targetProperty: "text", mutual: true)
// Kundendaten - Bauvorhaben: Update tab title
bauvorhaben.addCaretListener({ evt ->
	if (bauvorhaben.text) {
		projektTabGroup.setTitleAt(projektTabGroup.selectedIndex, "Projekt - ${bauvorhaben.text}")
	}
} as javax.swing.event.CaretListener)
// Gebäudedaten - Gebäudetyp
bind(source: model.map.gebaeude, sourceProperty: "gebaeudeTypMFH",        target: gebaeudeTypMFH,        targetProperty: "selected", mutual: true)
bind(source: model.map.gebaeude, sourceProperty: "gebaeudeTypEFH",        target: gebaeudeTypEFH,        targetProperty: "selected", mutual: true)
bind(source: model.map.gebaeude, sourceProperty: "gebaeudeTypMaisonette", target: gebaeudeTypMaisonette, targetProperty: "selected", mutual: true)
// Gebäudedaten - Gebäudelage
bind(source: model.map.gebaeude, sourceProperty: "gebaeudeLageWindstark",   target: gebaeudeLageWindstark,   targetProperty: "selected", mutual: true)
bind(source: model.map.gebaeude, sourceProperty: "gebaeudeLageWindschwach", target: gebaeudeLageWindschwach, targetProperty: "selected", mutual: true)
//
// JIDE
//
//LookAndFeelFactory.installJideExtension(LookAndFeelFactory.ECLIPSE_STYLE);
// projektTabGroup
projektTabGroup.with {
	setTabColorProvider(JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	//ColorTheme(JideTabbedPane.COLOR_THEME_WIN2K)
	//setTabShape(JideTabbedPane.SHAPE_ECLIPSE)
	setBoldActiveTab(true)
	setShowCloseButton(true)
	setShowCloseButtonOnSelectedTab(true)
}
// datenTabGroup
datenTabGroup.with {
	setTabColorProvider(JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	//setColorTheme(JideTabbedPane.COLOR_THEME_WIN2K)
	//setTabShape(JideTabbedPane.SHAPE_ECLIPSE)
	setBoldActiveTab(true)
}

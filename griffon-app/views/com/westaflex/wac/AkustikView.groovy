/**
 * /Users/rbe/project/wac2/griffon-app/views/com/westaflex/wac/AkustikView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout

// Akustikberechnung
panel(constraints: "grow", layout: new MigLayout("fillx, wrap", "[fill]", "[fill]")) {
    panel(id: "akustikTabPanel", layout: new MigLayout("fill", "[]", "")) {
        // Tabellen für
        jideTabbedPane(id: "akustikTabGroup", constraints: "grow, span") {
            panel(id: "akustikZuluftTab", title: "Zuluft", layout: new MigLayout("fill", "[]", "")) {
                buildLayout("Zuluft")
            }
            panel(id: "akustikAbluftTab", title: "Abluft", layout: new MigLayout("fill", "[]", "")) {
                buildLayout("Abluft")
            }
        }
    }
}
// akustikTabGroup
akustikTabGroup.with {
	setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
}
// Bindings
build(AkustikBindings)

/**
 * Synchronize all Swing table models depending on map.raum.raume.
 */
def buildLayout(tabname) {
	def tabTitleForeground = tabname == "Zuluft" ? GH.MY_RED : java.awt.Color.BLUE
	// Akustikberechnung - Zuluft
	panel(layout: new MigLayout("fill, wrap 3", "[left]para[right]para[left]", "[fill]")) {
        panel(layout: new MigLayout("fillx", "[fill]para[right,fill]para[right]", "[fill]")) {
            label("Raumbezeichnung", constraints: "cell 0 0, wrap")
            
            comboBox(id: "akustik${tabname}Raumbezeichnung", items: model.meta.raum.typ)
            label(" ", constraints: "span 2, wrap")

            label(" ", constraints: "span 3, wrap")

            label(" ", constraints: "span 3, wrap")

            label("Schallleistungspegel ${tabname}stutzen", foreground: GH.MY_RED)
            // TODO: split -> comboBox...???
            label(id: "akustik${tabname}${tabname}stutzenZentralgerat", raumVsZentralgerat.selectedItem)
            comboBox(id: "akustik${tabname}Pegel", constraints: "width 60px!, wrap", items: model.meta.volumenstromZentralgerat)

            label("Schallleistungspegelerhöhung Kanalnetz", foreground: GH.MY_RED)
            comboBox(id: "akustik${tabname}Kanalnetz", constraints: "span 2, wrap", items: (10..200).step(10))


            label("Schallleistungspegelerhöhung Filter", foreground: GH.MY_RED)
            comboBox(id: "akustik${tabname}Filter", constraints: "span 2, wrap", items: (10..200).step(10))

            label("1. Hauptschalldämpfer", foreground: GH.MY_GREEN)
            //comboBox(id: "akustik${tabname}1Hauptschalldampfer", constraints: "span 2, wrap")
            switch (tabname) {
				case "Zuluft":
					comboBox(id: "akustik${tabname}1Hauptschalldampfer", constraints: "span 2, wrap", items: model.meta.akustikSchalldampfer, selectedItem: "100150TYP4A")
					break
				case "Abluft":
					comboBox(id: "akustik${tabname}1Hauptschalldampfer", constraints: "span 2, wrap", items: model.meta.akustikSchalldampfer, selectedItem: "")
					break
            }

            label("2. Hauptschalldämpfer", foreground: GH.MY_GREEN)
            comboBox(id: "akustik${tabname}2Hauptschalldampfer", constraints: "span 2, wrap", items: model.meta.akustikSchalldampfer)

            label("Anzahl der Umlenkungen 90° Stck.", foreground: GH.MY_GREEN)
            label("")
            textField(id: "akustik${tabname}AnzahlUmlenkungen90GradStck", constraints: "width 60px!, wrap")

            label("Luftverteilerkasten Stck.", foreground: GH.MY_GREEN)
            label("")
            textField(id: "akustik${tabname}LuftverteilerkastenStck", constraints: "width 60px!, wrap")

            label("Längsdämpfung Kanal lfdm.", foreground: GH.MY_GREEN)
            comboBox(id: "akustik${tabname}LangsdampfungKanal", items: model.meta.dvbKanalbezeichnung)
            textField(id: "akustik${tabname}LangsdampfungKanalWert", constraints: "width 60px!, wrap")

            label("Schalldämpfer Ventil", foreground: GH.MY_GREEN)
            comboBox(id: "akustik${tabname}SchalldampferVentil", constraints: "span 2, wrap", items: model.meta.akustikSchalldampfer)

            label("Einfügungsdämmwert Luftdurchlass", foreground: GH.MY_GREEN)
            comboBox(id: "akustik${tabname}EinfugungsdammwertLuftdurchlass", constraints: "span 2, wrap", items: model.meta.dvbVentileinstellung)

            label("Raumabsorption (Annahme) BAD=0 WOHNEN=1", foreground: GH.MY_GREEN)
            label("")
            textField(id: "akustik${tabname}Raumabsorption", constraints: "width 60px!, wrap")

            label("Korrektur der A-Bewertung", constraints: "wrap")

            label("Bewerteter Schallpegel", constraints: "wrap")
        }

        panel(layout: new MigLayout("fillx, wrap", "[center]", "[fill]")) {
            label("Zentrales Lüftungsgerät " + raumVsZentralgerat.selectedItem, foreground: tabTitleForeground)

            label(tabname, foreground: tabTitleForeground)

            label("Oktavmittenfrequenz in Hz")

            jideScrollPane(constraints: "grow") {
                table(id: 'akustik${tabName}Tabelle', selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                    tableModel() {
                        propertyColumn(header: GH.ws("125"),  propertyName: "125")
                        propertyColumn(header: GH.ws("250"),  propertyName: "250")
                        propertyColumn(header: GH.ws("500"),  propertyName: "500")
                        propertyColumn(header: GH.ws("1000"), propertyName: "1000")
                        propertyColumn(header: GH.ws("2000"), propertyName: "2000")
                        propertyColumn(header: GH.ws("4000"), propertyName: "4000")
                    }
                }
            }

            label("Mittlerer Schalldruckpegel* dB(A) =", constraints: "right")
        }

        panel(layout: new MigLayout("fillx, wrap", "[fill]", "[fill]")) {
            label(" ", constraints: "wrap")
            label(" ", constraints: "wrap")
            label(" ", constraints: "wrap")
            label("dB(A)")
        }

        label(constraints: "wrap")
        label("* Bei dieser Berechnung handelt es dich um eine <br>theoretische Auslegung, deren Werte in der Praxis abweichen können", constraints: "right, span 3")
	}
	return "akustik${tabname}Tab"
}

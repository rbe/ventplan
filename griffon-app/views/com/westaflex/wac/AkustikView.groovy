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

/**
 * Synchronize all Swing table models depending on map.raum.raume.
 */
def buildLayout(tabname) {
	// Akustikberechnung - Zuluft
	panel(layout: new MigLayout("fillx, wrap 4", "[left,fill]para[right,fill]para[center,fill]para[left,fill]", "[fill]")) {
        panel(layout: new MigLayout("fillx", "[fill]para[right,fill]", "[fill]")) {
            label("Raumbezeichnung", constraints: "cell 0 0, wrap")
            
            comboBox(id: "akustik${tabname}Raumbezeichnung", constraints: "span 2")
            label("", constraints: "wrap")

            label("", constraints: "span 2, wrap")

            label("", constraints: "span 2, wrap")

            label("Schallleistungspegel Zuluftstutzen")
            // TODO: Zentralgerät aus RaumVsView -> raumVsZuAbluftventileZentralgerat
            // TODO: split -> comboBox...???
            label(raumVsZentralgerat.selectedItem, constraints: "wrap")

            label("Schallleistungspegelerhöhung Kanalnetz")
            comboBox(id: "akustik${tabname}Kanalnetz", constraints: "wrap", model: [(0..200).step 10])


            label("Schallleistungspegelerhöhung Filter")
            comboBox(id: "akustik${tabname}Filter", constraints: "wrap")

            label("1. Hauptschalldämpfer")
            comboBox(id: "akustik${tabname}1Hauptschalldampfer", constraints: "wrap")

            label("2. Hauptschalldämpfer")
            comboBox(id: "akustik${tabname}2Hauptschalldampfer", constraints: "wrap")

            label("Anzahl der Umlenkungen 90° Stck.")
            textField(id: "akustik${tabname}AnzahlUmlenkungen90GradStck", constraints: "wrap")

            label("Luftverteilerkasten Stck.")
            textField(id: "akustik${tabname}LuftverteilerkastenStck", constraints: "wrap")

            label("Längsdämpfung Kanal lfdm.")
            // TODO: split ???
            textField(id: "akustik${tabname}LangsdampfungKanal", constraints: "wrap")

            label("Schalldämpfer Ventil")
            comboBox(id: "akustik${tabname}SchalldampferVentil", constraints: "wrap")

            label("Einfügungsdämmwert Luftdurchlass")
            comboBox(id: "akustik${tabname}EinfugungsdammwertLuftdurchlass", constraints: "wrap")

            label("Raumabsorption (Annahme) BAD=0 WOHNEN=1")
            textField(id: "akustik${tabname}Raumabsorption", constraints: "wrap")

            label("Korrektur der A-Bewertung", constraints: "wrap")

            label("Bewerteter Schallpegel", constraints: "wrap")

            label(GH.ws("* Bei dieser Berechnung handelt es dich um eine <br>theoretische Auslegung, deren Werte in der Praxis abweichen können"), constraints: "wrap")
        }

        panel(layout: new MigLayout("fillx, wrap", "[fill]", "[fill]")) {
            label("Zentrales Lüftungsgerät " + raumVsZentralgerat.selectedItem)

            label(tabname)

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

            label("Mittlerer Schalldruckpegel* dB(A) =")
        }

        panel(layout: new MigLayout("fillx, wrap", "[fill]", "[fill]")) {
            label("dB(A)")
        }
	}
	return "akustik${tabname}Tab"
}

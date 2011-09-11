/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/com/westaflex/wac/RaumVsView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout

// Raumvolumenströme
panel(layout: new MigLayout("fill, wrap", "[fill, grow]", "[fill,grow]"), constraints: "grow") {
    // Tabellen für Zu-/Abluftventile, Überströmventile
    panel(layout: new MigLayout("fill, wrap 1", "[fill, grow]", "[fill,grow]"), constraints: "grow") {
        // WAC-171
        label(id: "raumVsTurenHinweis", foreground: java.awt.Color.RED)

        // WAC-171
        label(id: "raumVsUbElementeHinweis", foreground: java.awt.Color.RED)

        jideTabbedPane(id: "raumVsVentileTabGroup", constraints: "height ::280, grow, span") {
            // Raumvolumenströme - Zu-/Abluftventile
            panel(id: "raumVsZuAbluftventileTab", title: "Zu-/Abluftventile", layout: new MigLayout("fill", "[fill,grow]", "[fill,grow]"), constraints: "grow") {
                panel(id: "raumVsZuAbluftventileTabellePanel", layout: new MigLayout("", "[fill, grow]"), constraints: "grow") {
                    jideScrollPane(constraints: "grow") {
                        table(id: 'raumVsZuAbluftventileTabelle', model: model.createRaumVsZuAbluftventileTableModel())
                    }
                }
            }
            // Raumvolumenströme - Überströmventile
            panel(id: "raumVsUberstromventileTab", title: "Überströmventile", layout: new MigLayout("fill", "[fill,grow]", "[fill,grow]"), constraints: "grow") {
                panel(id: "raumVsUberstromelementeTabellePanel", layout: new MigLayout("fillx", "[fill]"), constraints: "grow") {
                    jideScrollPane(constraints: "grow") {
                        table(id: "raumVsUberstromelementeTabelle", model: model.createRaumVsUberstromelementeTableModel())
                    }
                }
            }
        }
    }
    panel(layout: new MigLayout("", "[] [] [grow]")) {
        panel(layout: new MigLayout("", "[] [right] []", "[] 16 []")) {
            // Informationen
            label("Gesamtvolumen der Nutzungseinheit")
            label(id: "raumVsGesamtVolumenNE")
            label("m³", constraints: "wrap")

            label("Luftwechsel der Nutzungseinheit")
            label(id: "raumVsLuftwechselNE", text: "0,00")
            label("l/h", constraints: "wrap")

            label("Gesamtaußenluft-Volumentstrom mit Infiltration")
            label(id: "raumVsGesamtaussenluftVsMitInfiltration", text: "0,00")
            label("m³/h", constraints: "wrap")
        }
        panel(border: titledBorder("Außenluftvolumenstrom der LTM"), layout: new MigLayout("fill", "[grow]")) {
            panel(layout: new MigLayout("fill, wrap 4", "[left] 20 [right] [left] 20 [left]"), constraints: "grow") {
                label("Feuchteschutz")
                label(id: "raumVsAussenluftVsDerLtmFs", text: "0,00")
                label("m³/h")
                label("Zentralgerät")

                label("Reduzierte Lüftung")
                label(id: "raumVsAussenluftVsDerLtmRl", text: "0,00")
                label("m³/h")
                comboBox(id: "raumVsZentralgerat", items: model.meta.zentralgerat)

                label("Nennlüftung")
                label(id: "raumVsAussenluftVsDerLtmNl", text: "0,00")
                label("m³/h")
                label("Volumenstrom")

                label("Intensivlüftung")
                label(id: "raumVsAussenluftVsDerLtmIl", text: "0,00")
                label("m³/h")
                comboBox(id: "raumVsVolumenstrom", items: model.meta.volumenstromZentralgerat)
            }
        }
        panel(layout: new MigLayout("", "[fill, grow]", "[] 10 []"), constraints: "wrap") {
            button(id: "raumVsRaumBearbeiten", text: "Raum bearbeiten", constraints: "wrap")
            //button(id: "raumVsZuAbluftventileSpeichern", text: "Speichern", constraints: "wrap")
            button(id: "raumVsZuAbluftventileAngebotErstellen", text: "Angebot erstellen", constraints: "wrap")
        }
    }
}

// raumVsVentileTabGroup
raumVsVentileTabGroup.with {
	setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
}

/*
raumVsZuAbluftventileTabelle.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF)
//raumVsZuAbluftventileTabelle.packTable(0)
raumVsZuAbluftventileTabelle.packColumn(0, 10, 60)
raumVsZuAbluftventileTabelle.packColumn(1, 10, 60)
raumVsZuAbluftventileTabelle.packColumn(2, 10, 80)
raumVsZuAbluftventileTabelle.packColumn(3, 10, 80)
raumVsZuAbluftventileTabelle.packColumn(4, 10, 70)
raumVsZuAbluftventileTabelle.packColumn(5, 10, 90)
raumVsZuAbluftventileTabelle.packColumn(6, 10, 90)
raumVsZuAbluftventileTabelle.packColumn(7, 10, 90)
raumVsZuAbluftventileTabelle.packColumn(8, 10, 70)
raumVsZuAbluftventileTabelle.packColumn(9, 10, 90)
raumVsZuAbluftventileTabelle.packColumn(10, 10, 90)
raumVsZuAbluftventileTabelle.packColumn(11, 10, 90)
raumVsZuAbluftventileTabelle.packColumn(12, 10, 80)
*/

// Bindings
build(RaumVsBindings)

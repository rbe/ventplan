/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/com/westaflex/wac/RaumVsView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout

// Raumvolumenströme
panel(constraints: "grow", layout: new MigLayout("fill, wrap", "[fill, grow]", "[fill,grow]")) {
    // Tabellen für Zu-/Abluftventile, Überströmventile
    panel(constraints: "grow", layout: new MigLayout("fill", "[fill, grow]", "[fill,grow]")) {
        jideTabbedPane(id: "raumVsVentileTabGroup", constraints: "grow, span") {
            // Raumvolumenströme - Zu-/Abluftventile
            panel(id: "raumVsZuAbluftventileTab", title: "Zu-/Abluftventile", constraints: "grow", layout: new MigLayout("fill", "[fill,grow]", "[fill,grow]")) {
                panel(id: "raumVsZuAbluftventileTabellePanel", constraints: "grow", layout: new MigLayout("", "[fill, grow]")) {
                    jideScrollPane(constraints: "grow") {
                        table(id: 'raumVsZuAbluftventileTabelle', model: model.createRaumVsZuAbluftventileTableModel())
                    }
                }
            }
            // Raumvolumenströme - Überströmventile
            panel(id: "raumVsUberstromventileTab", title: "Überströmventile", constraints: "grow", layout: new MigLayout("fill", "[fill,grow]", "[fill,grow]")) {
                panel(id: "raumVsUberstromventileTabellePanel", constraints: "grow", layout: new MigLayout("fillx", "[fill]")) {
                    jideScrollPane(constraints: "grow") {
                        table(id: "raumVsUberstromventileTabelle", model: model.createRaumVsUberstromventileTableModel())
                    }
                }
            }
        }
    }
    panel(constraints: "grow", layout: new MigLayout("fill", "[fill, grow] 16 [fill, grow] 16 [fill, grow]", "")) {
        panel(layout: new MigLayout("", "[] 16 [] 16 []", "[] 16 []")) {
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
        panel(border: titledBorder("Außenluftvolumenstrom der lüftungstechnischen Massnahme"),
                constraints: "grow, width 500", layout: new MigLayout("fill", "[]", "")) {
            panel(constraints: "grow", layout: new MigLayout("fill, wrap 4", "[left] 16 [right] 16 [left] 16 [left]", "")) {
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
        panel(constraints: "wrap", layout: new MigLayout("", "[fill, grow]", "[] 10 []")) {
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
// Bindings
build(RaumVsBindings)

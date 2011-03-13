/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/com/westaflex/wac/DruckverlustView.groovy
 *
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 *
 * Created by: rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout

// Druckverlustberechnung
panel(id: "dvbTabPanel", layout: new MigLayout("fill", "[fill]", "[fill]"), constraints: "grow") {

    // Tabellen für Druckverlustberechnung
    jideTabbedPane(id: "dvbTabGroup", constraints: "grow, span") {

        // Druckverlustberechnung - Kanalnetz
        panel(id: "dvbKanalnetzTab", title: "Kanalnetz", layout: new MigLayout("fill", "[fill,grow]", "[fill]"), constraints: "grow") {
            jideScrollPane(constraints: "grow") {
                panel(id: "dvbKanalnetzPanel", layout: new MigLayout("", "[grow]", ""), constraints: "grow") {
                    panel(id: "dvbKanalnetzInput", layout: new MigLayout("", "[fill] 16 [fill] 16 [fill] 16 [fill] 16 [fill] 16 [fill] 16 [fill]", ""), constraints: "grow, wrap") {

                        label("Luftart")
                        label("Nr. Teilstrecke")
                        label("Luftmenge (m³/h)")
                        label("Kanalbezeichnung")
                        label("Länge (m)")
                        label("")
                        label("", constraints: "wrap")

                        comboBox(id: "dvbKanalnetzLuftart", items: ["ZU", "AB"])
                        textField(id: "dvbKanalnetzNrTeilstrecke", constraints: "width 80px")
                        textField(id: "dvbKanalnetzLuftmenge", constraints: "width 100px")
                        comboBox(id: "dvbKanalnetzKanalbezeichnung", items: model.meta.dvbKanalbezeichnung)
                        textField(id: "dvbKanalnetzLange", constraints: "width 80px")
                        button(id: "dvbKanalnetzHinzufugen", text: "Hinzufügen", constraints: "wrap")
                    }

                    panel(id: "dvbKanalnetzTabellePanel", layout: new MigLayout("fill", "[grow]", ""), constraints: "span, grow, wrap") {
                        jideScrollPane(constraints: "grow") {
                            table(id: 'dvbKanalnetzTabelle', model: model.createDvbKanalnetzTableModel())
                        }
                    }

                    panel(layout: new MigLayout("fillx", "[left] 16 []", ""), constraints: "span, wrap") {
                        // TODO mmu Enable only when table isn't empty and row is selected
                        button(id: "dvbKanalnetzEntfernen",        text: "Entfernen",               constraints: "split 2")
                        button(id: "dvbKanalnetzWiderstandswerte", text: "Widerstandsbeiwerte...")
                    }

                }
            }
        }

        // Druckverlustberechnung - Ventileinstellung
        panel(id: "dvbVentileinstellungTab", title: "Ventileinstellung", layout: new MigLayout("fill", "[fill,grow]", "[fill]"), constraints: "grow") {
            // Druckverlustberechnung - Ventileinstellung
            jideScrollPane(constraints: "grow") {
                panel(id: "dvbVentileinstellungPanel", layout: new MigLayout("", "[grow]"), constraints: "grow") {
                    panel(id: "dvbVentileinstellungInput", layout: new MigLayout("", "[] 16 [] 16 [] 16 [] 16 [] 16 [] 16 []", ""), constraints: "grow, wrap") {

                        label("Luftart")
                        label("Raum")
                        label("Teilstrecken")
                        label("")
                        label("Ventilbezeichnung")
                        label("")
                        label("Hinweis:", foreground: java.awt.Color.RED, constraints: "wrap")

                        comboBox(id: "dvbVentileinstellungLuftart", items: ["ZU", "AB", "AU", "FO"])
                        comboBox(id: "dvbVentileinstellungRaum", items: model.meta.raum.typ + [/* items werden nach RaumHinzufugen aktualisiert, siehe Ticket#10 */])
                        textField(id: "dvbVentileinstellungTeilstrecken", constraints: "width 150px")
                        button(id: "dvbVentileinstellungAuswahlen", text: "Auswählen")
                        comboBox(id: "dvbVentileinstellungVentilbezeichnung", items: model.meta.dvbVentileinstellung)
                        button(id: "dvbVentileinstellungHinzufugen", text: "Hinzufügen")
                        label("Mindesteingabe 5 Teilstrecken", foreground: java.awt.Color.RED, constraints: "wrap")
                    }

                    panel(id: "dvbVentileinstellungTabellePanel", layout: new MigLayout("fill", "[fill,grow]", ""), constraints: "span, grow, wrap") {
                        jideScrollPane(constraints: "grow") {
                            table(id: 'dvbVentileinstellungTabelle', model: model.createDvbVentileinstellungTableModel())
                        }
                    }

                    panel(layout: new MigLayout("fillx", "[left] 16 []", ""), constraints: "span, wrap") {
                        // TODO mmu Enable only when table isn't empty and row is selected
                        button(id: "dvbVentileinstellungEntfernen", text: "Entfernen", enabled: bind { dvbVentileinstellungTabelle?.selectedRow >= 0 ? true : false })
                    }

                }
            }
        }
    }
}
// Textfields
GH.doubleTextField(dvbKanalnetzNrTeilstrecke)
GH.autoformatDoubleTextField(dvbKanalnetzLuftmenge)
GH.autoformatDoubleTextField(dvbKanalnetzLange)
// dvbTabGroup
dvbTabGroup.with {
	setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
}
// Bindings
build(DruckverlustBindings)

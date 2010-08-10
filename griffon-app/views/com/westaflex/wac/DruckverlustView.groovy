/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/com/westaflex/wac/DruckverlustView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout

jideScrollPane(constraints: "grow") {
    // Druckverlustberechnung
    panel(id: "dvbTabPanel", layout: new MigLayout("fill", "[fill]", "[fill]")) {

        // Tabellen für Druckverlustberechnung
        jideTabbedPane(id: "dvbTabGroup", constraints: "grow, span") {

            // Druckverlustberechnung - Kanalnetz
            panel(id: "dvbKanalnetzTab", title: "Kanalnetz", layout: new MigLayout("fillx", "[fill]", "[fill]")) {
                jideScrollPane(constraints: "grow") {
                    panel(id: "dvbKanalnetzPanel", layout: new MigLayout("", "[]para[]para[]para[]para[]para[]para[]", "")) {

                        label("Luftart")
                        label("Nr. Teilstrecke")
                        label("Luftmenge (m³/h)")
                        label("Kanalbezeichnung")
                        label("Länge (m)")
                        label("")
                        label("", constraints: "wrap")

                        comboBox(id: "dvbKanalnetzLuftart", items: ["ZU", "AB"])
                        textField(id: "dvbKanalnetzNrTeilstrecke")
                        textField(id: "dvbKanalnetzLuftmenge")
                        comboBox(id: "dvbKanalnetzKanalbezeichnung", items: model.meta.dvbKanalbezeichnung)
                        textField(id: "dvbKanalnetzLange")
                        button(id: "dvbKanalnetzHinzufugen", text: "Hinzufügen", constraints: "wrap")

                        panel(id: "dvbKanalnetzTabellePanel", constraints: "span", layout: new MigLayout("fillx", "[fill]", "")) {
                            jideScrollPane(constraints: "grow") {
                                table(id: "dvbKanalnetzTabelle", model: model.createDvbKanalnetzTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                                }
                            }
                        }

                        panel(layout: new MigLayout("fillx", "[left]para[right]", "[fill]")) {
                            // TODO mmu Enable only when table isn't empty and row is selected
                            button(id: "dvbKanalnetzEntfernen",        text: "Entfernen",               enabled: bind { 1 == 1 })
                            button(id: "dvbKanalnetzWiderstandswerte", text: "Widerstandsbeiwerte..." , enabled: bind { 1 == 1 })
                        }

                    }
                }
            }

            // Druckverlustberechnung - Ventileinstellung
            panel(id: "dvbVentileinstellungTab", title: "Ventileinstellung", layout: new MigLayout("fillx", "[fill]", "[fill]")) {
                // Druckverlustberechnung - Ventileinstellung
                jideScrollPane(constraints: "grow") {
                    panel(id: "dvbVentileinstellungPanel", layout: new MigLayout("", "[]para[]para[]para[]para[]para[]", "")) {

                        label("Luftart")
                        label("Raum")
                        label("Teilstrecken")
                        label("")
                        label("Ventilbezeichnung")
                        label("", constraints: "wrap")

                        comboBox(id: "dvbVentileinstellungLuftart", items: ["ZU", "AB", "AU", "FO"])
                        comboBox(id: "dvbVentileinstellungRaum", items: model.meta.raum.typ + [/* items werden nach RaumHinzufugen aktualisiert, siehe Ticket#10 */])
                        textField(id: "dvbVentileinstellungTeilstrecken")
                        button(id: "dvbVentileinstellungAuswahlen", text: "Auswählen")
                        comboBox(id: "dvbVentileinstellungVentilbezeichnung", items: model.meta.dvbVentileinstellung)
                        button(id: "dvbVentileinstellungHinzufugen", text: "Hinzufügen", constraints: "wrap")

                        panel(id: "dvbVentileinstellungTabellePanel", constraints: "span", layout: new MigLayout("fillx", "[fill]", "")) {
                            jideScrollPane(constraints: "grow") {
                                table(id: "dvbVentileinstellungTabelle", model: model.createDvbVentileinstellungTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                                }
                            }
                        }

                        button(id: "dvbVentileinstellungEntfernen", text: "Entfernen")

                    }
                }
            }
        }
    }
}
// dvbTabGroup
dvbTabGroup.with {
	setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
}
// Bindings
build(DruckverlustBindings)

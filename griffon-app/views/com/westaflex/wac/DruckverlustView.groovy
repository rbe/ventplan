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

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout

// Druckverlustberechnung
panel(id: "dvbTabPanel", layout: new MigLayout("fill", "[fill]", "[fill]")) {

    // Tabellen für Druckverlustberechnung
    jideTabbedPane(id: "dvbTabGroup", constraints: "grow, span") {

        // Druckverlustberechnung - Kanalnetz
        panel(id: "dvbKanalnetzTab", title: "Kanalnetz", layout: new MigLayout("fill", "[fill,grow]", "[fill]")) {
            jideScrollPane(constraints: "grow") {
                panel(id: "dvbKanalnetzPanel", layout: new MigLayout("", "[fill] 16 [fill] 16 [fill] 16 [fill] 16 [fill] 16 [fill] 16 [fill]", "")) {

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

                    panel(id: "dvbKanalnetzTabellePanel", constraints: "span, grow", layout: new MigLayout("fill", "[fill,grow]", "")) {
                        jideScrollPane(constraints: "grow, width 600:1000:") {
                            table(id: 'dvbKanalnetzTabelle', model: model.createDvbKanalnetzTableModel())
                        }
                    }

                    panel(constraints: "span", layout: new MigLayout("fillx", "[left] 16 []", "")) {
                        // TODO mmu Enable only when table isn't empty and row is selected
                        button(id: "dvbKanalnetzEntfernen",        text: "Entfernen",               enabled: bind { 1 == 1 }, constraints: "split 2")
                        button(id: "dvbKanalnetzWiderstandswerte", text: "Widerstandsbeiwerte..." , enabled: bind { 1 == 1 })
                    }

                }
            }
        }

        // Druckverlustberechnung - Ventileinstellung
        panel(id: "dvbVentileinstellungTab", title: "Ventileinstellung", layout: new MigLayout("fill", "[fill,grow]", "[fill]")) {
            // Druckverlustberechnung - Ventileinstellung
            jideScrollPane(constraints: "grow") {
                panel(id: "dvbVentileinstellungPanel", layout: new MigLayout("", "[] 16 [] 16 [] 16 [] 16 [] 16 []", "")) {

                    label("Luftart")
                    label("Raum")
                    label("Teilstrecken")
                    label("")
                    label("Ventilbezeichnung")
                    label("", constraints: "wrap")

                    comboBox(id: "dvbVentileinstellungLuftart", items: ["ZU", "AB", "AU", "FO"])
                    comboBox(id: "dvbVentileinstellungRaum", items: model.meta.raum.typ + [/* items werden nach RaumHinzufugen aktualisiert, siehe Ticket#10 */])
                    textField(id: "dvbVentileinstellungTeilstrecken", constraints: "width 150px")
                    button(id: "dvbVentileinstellungAuswahlen", text: "Auswählen")
                    comboBox(id: "dvbVentileinstellungVentilbezeichnung", items: model.meta.dvbVentileinstellung)
                    button(id: "dvbVentileinstellungHinzufugen", text: "Hinzufügen", constraints: "wrap")

                    panel(id: "dvbVentileinstellungTabellePanel", constraints: "span, grow", layout: new MigLayout("fill", "[fill,grow]", "")) {
                        jideScrollPane(constraints: "grow, width 600:1000:") {
                            table(id: 'dvbVentileinstellungTabelle', model: model.createDvbVentileinstellungTableModel())
                        }
                    }

                    panel(constraints: "span", layout: new MigLayout("fillx", "[left] 16 []", "")) {
                        // TODO mmu Enable only when table isn't empty and row is selected
                        button(id: "dvbVentileinstellungEntfernen", text: "Entfernen", enabled: bind { 1 == 1 })
                        // TODO -> es gibt gar keine Teilstrecken !!!
                        //button(id: "dvbVentileinstellungTeilstrecke", text: "Teilstrecke auswählen" , enabled: bind { 1 == 1 })
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

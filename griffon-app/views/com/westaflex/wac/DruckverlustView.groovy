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
import com.bensmann.griffon.WacTableHelper as WTH
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
                            //table(id: "dvbKanalnetzTabelle", model: model.createDvbKanalnetzTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                            //}
                            table(id: 'dvbKanalnetzTabelle', selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION, constraints: "grow") {
                                tableModel() {
                                    current.addTableModelListener(WTH.getDvbKanalnetzTableModelListener())
                                    propertyColumn(header: 'Luftart',
                                        propertyName: 'dvbkLuftart',
                                        cellEditor: WTH.getDruckverlustLuftartEditor(builder,model),
                                        cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                    )
                                    propertyColumn(header: 'Teilstrecke', propertyName: 'teilstrecke')
                                    propertyColumn(header: GH.ws("Luftvolumen<br/>strom<br/>(m³/h)"), propertyName: 'luftVs')
                                    propertyColumn(header: 'Kanalbezeichnung',
                                        propertyName: 'kanalbezeichnung',
                                        cellEditor: WTH.getDruckverlustKanalbezeichnungEditor(builder,model),
                                        cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                    )
                                    propertyColumn(header: GH.ws("Kanallänge<br/>(m)"), propertyName: 'lange')
                                    propertyColumn(header: GH.ws("Geschwindigkeit<br/>(m/s)"), propertyName: 'geschwindigkeit', editable: false)
                                    propertyColumn(header: GH.ws("Reibungswiderstand<br/>gerader Kanal<br/>(Pa)"), propertyName: 'reibungswiderstand', editable: false)
                                    propertyColumn(header: GH.ws("Gesamtwider-<br/>standszahl"), propertyName: 'gesamtwiderstandszahl', editable: false)
                                    propertyColumn(header: GH.ws("Einzelwider-<br/>stand<br/>(Pa)"), propertyName: 'einzelwiderstand', editable: false)
                                    propertyColumn(header: GH.ws("Widerstand<br/>Teilstrecke<br/><(Pa)"), propertyName: 'widerstandTeilstrecke', editable: false)
                                }
                            }
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
                            //table(id: "dvbVentileinstellungTabelle", model: model.createDvbVentileinstellungTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                            //}
                            table(id: 'dvbVentileinstellungTabelle', selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION, constraints: "grow") {
                                tableModel() {
                                    current.addTableModelListener(WTH.getDvbKanalnetzTableModelListener())
                                    propertyColumn(header: 'Luftart',
                                        propertyName: 'dvbvLuftart',
                                        cellEditor: WTH.getDruckverlustEinstellungLuftartEditor(builder,model),
                                        cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                    )
                                    propertyColumn(header: 'Raum',
                                        propertyName: 'dvbvRaum',
                                        cellEditor: WTH.getDruckverlustEinstellungRaumEditor(builder,model),
                                        cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                    )
                                    propertyColumn(header: 'Teilstrecken', propertyName: 'teilstrecken')
                                    propertyColumn(header: 'Ventiltyp', 
                                        propertyName: 'ventilbezeichnung',
                                        cellEditor: WTH.getDruckverlustEinstellungVentilbezeichnungEditor(builder,model),
                                        cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                    )
                                    propertyColumn(header: GH.ws("dP offen<br/>(Pa)"), propertyName: 'dpOffen')
                                    propertyColumn(header: GH.ws("Gesamt<br/>(Pa)"), propertyName: 'gesamtWiderstand')
                                    propertyColumn(header: 'Differenz', propertyName: 'differenz')
                                    propertyColumn(header: GH.ws("Abgleich<br/>(Pa)"), propertyName: 'abgleich')
                                    propertyColumn(header: 'Einstellung', propertyName: 'einstellung')
                                }
                            }
                        }
                    }

                    panel(constraints: "span", layout: new MigLayout("fillx", "[left] 16 []", "")) {
                        // TODO mmu Enable only when table isn't empty and row is selected
                        button(id: "dvbVentileinstellungEntfernen", text: "Entfernen", enabled: bind { 1 == 1 })
                        button(id: "dvbVentileinstellungTeilstrecke", text: "Teilstrecke auswählen" , enabled: bind { 1 == 1 })
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

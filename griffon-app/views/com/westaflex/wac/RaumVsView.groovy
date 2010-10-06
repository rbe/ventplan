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
import com.bensmann.griffon.WacTableHelper as WTH
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
                        table(id: 'raumVsZuAbluftventileTabelle', selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION, constraints: "grow") {
                            tableModel() {
                                //current.addTableModelListener(GH.getRaumVsZuAbluftventileTableModelListener())
                                current.addTableModelListener({ javax.swing.event.TableModelEvent e ->
                                    if (0 == e.type) {
                                        println "raumVsZuAbluftventileTabelle,TableModelListener,updateRaumVentile: ${e.firstRow} ${e.lastRow} ${e.column} ${e.type}"
                                        controller.updateRaumVentile(e.firstRow)
                                    }
                                } as javax.swing.event.TableModelListener)
                                propertyColumn(header: 'Raum',
                                    propertyName: 'raumBezeichnungCombo',
                                    cellEditor: WTH.getRaumdatenBezeichnungCellEditor(builder,model),
                                    cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                )
                                propertyColumn(header: 'Luftart',
                                    propertyName: 'raumLuftartCombo',
                                    cellEditor: WTH.getRaumdatenLuftartCellEditor(builder,model),
                                    cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                )
                                propertyColumn(header: GH.ws("Raumvolumen<br/>(m³)"), propertyName: 'raumVolumen')
                                propertyColumn(header: GH.ws("Luftwechsel<br/>(1/h)"), propertyName: 'raumLuftwechsel')
                                propertyColumn(header: GH.ws("Anzahl<br/>Abluftventile"), propertyName: 'raumAnzalAbluftventile')
                                propertyColumn(header: GH.ws("Abluftmenge<br/>je Ventil"), propertyName: 'raumAblufmengeAbluftventile')
                                propertyColumn(header: GH.ws("Volumenstrom<br/>(m³/h)"), propertyName: 'raumVolumenstrom')
                                propertyColumn(header: GH.ws("Anzahl<br/>Zuluftventile"), propertyName: 'raumAnzahlZuluftventile')
                                propertyColumn(header: GH.ws("Bezeichnung<br/>Zuluftventile"),
                                    propertyName: 'raumVsBezeichnungZuluftventileCombo',
                                    cellEditor: WTH.getRaumVsBezeichnungZuluftventileCellEditor(builder,model),
                                    cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                ) // combo
                                propertyColumn(header: GH.ws("Bezeichnung<br/>Abluftventile"),
                                    propertyName: 'raumVsBezeichnungAbluftventileCombo',
                                    cellEditor: WTH.getRaumVsBezeichnungAbluftventileCellEditor(builder,model),
                                    cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                ) // combo
                                propertyColumn(header: "Verteilebene",
                                    //propertyName: 'raumVsVerteilebeneCombo',
                                    //cellEditor: GH.getRaumVsVerteilebeneCellEditor(builder),
                                    propertyName: 'raumGeschossCombo',
                                    cellEditor: WTH.getRaumdatenGeschossCellEditor(builder,model),
                                    cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                ) // combo
                                propertyColumn(header: GH.ws("Zuluftmenge<br/>je Ventil"), propertyName: 'raumZuluftmengeJeVentil', editable: false)
                            }
                        }
                    }
                }
            }
            // Raumvolumenströme - Überströmventile
            panel(id: "raumVsUberstromventileTab", title: "Überströmventile", constraints: "grow", layout: new MigLayout("fill", "[fill,grow]", "[fill,grow]")) {
                panel(id: "raumVsUberstromventileTabellePanel", constraints: "grow", layout: new MigLayout("fillx", "[fill]")) {
                    jideScrollPane(constraints: "grow") {
                        //table(id: "raumVsUberstromventileTabelle", model: model.createRaumVsUberstromventileTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                        //}
                        table(id: 'raumVsUberstromventileTabelle', selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION, constraints: "grow") {
                            tableModel() {
                                current.addTableModelListener(WTH.getRaumVsUberstromTableModelListener())
                                propertyColumn(header: 'Raum',
                                    //propertyName: 'raumBezeichnungUberstromCombo',
                                    //cellEditor: GH.getRaumVsUberstromRaumBezeichnungCellEditor(builder,model),
                                    propertyName: 'raumBezeichnungCombo',
                                    cellEditor: WTH.getRaumdatenBezeichnungCellEditor(builder,model),
                                    cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                )
                                propertyColumn(header: 'Luftart',
                                    propertyName: 'raumLuftartCombo',
                                    cellEditor: WTH.getRaumdatenLuftartCellEditor(builder,model),
                                    cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                )
                                propertyColumn(header: GH.ws("Anzahl<br/>Ventile"), propertyName: 'raumAnzahlUberstromVentile')
                                propertyColumn(header: GH.ws("Volumenstrom<br/>(m³/h)"), propertyName: 'raumVolumenstrom')
                                propertyColumn(header: GH.ws("Überström<br/>Elemente"),
                                    propertyName: 'raumVsUberstromElementCombo',
                                    cellEditor: WTH.getRaumVsUberstromElementCellEditor(builder,model),
                                    cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                )
                            }
                        }
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

// Event abfangen. model.map.raum.raume aktualisieren...
WTH.getRaumdatenBezeichnungCellEditor(builder,model).editingStopped = { e ->
    println "stop editing row: ${-> raumVsUberstromventileTabelle.selectedColumn} ${-> raumVsUberstromventileTabelle.selectedRow} -> ${e}"
    // raumbezeichnung aktualisieren
    if (e.getSource() instanceof javax.swing.DefaultCellEditor) {
        def cellEditor = e.getSource() as javax.swing.DefaultCellEditor
        controller.updateRaumBezeichnungCombo(raumVsUberstromventileTabelle.selectedRow, cellEditor.getCellEditorValue())
    }
}
WTH.getRaumdatenLuftartCellEditor(builder,model).editingStopped = { e ->
    println "stop editing row: ${-> raumVsUberstromventileTabelle.selectedColumn} ${-> raumVsUberstromventileTabelle.selectedRow} -> ${e}"
    // raum Luftart aktualisieren
    if (e.getSource() instanceof javax.swing.DefaultCellEditor) {
        def cellEditor = e.getSource() as javax.swing.DefaultCellEditor
        controller.updateRaumLuftartCombo(raumVsUberstromventileTabelle.selectedRow, cellEditor.getCellEditorValue())
    }
}

// raumVsVentileTabGroup
raumVsVentileTabGroup.with {
	setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
}
// Bindings
build(RaumVsBindings)

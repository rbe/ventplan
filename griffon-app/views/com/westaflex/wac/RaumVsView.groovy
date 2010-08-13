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
jideScrollPane(constraints: "grow") {
    panel(constraints: "grow", layout: new MigLayout("fillx, wrap", "[fill]", "[fill]")) {
        // Tabellen für Zu-/Abluftventile, Überströmventile
        panel(constraints: "grow", layout: new MigLayout("fill", "[]", "")) {
            jideTabbedPane(id: "raumVsVentileTabGroup", constraints: "grow, span") {
                // Raumvolumenströme - Zu-/Abluftventile
                panel(id: "raumVsZuAbluftventileTab", title: "Zu-/Abluftventile", constraints: "grow") {
                    panel(id: "raumVsZuAbluftventileTabellePanel", constraints: "grow", layout: new MigLayout("", "[]")) {
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
                                        cellEditor: GH.getRaumdatenBezeichnungCellEditor(builder,model),
                                        cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                    )
                                    propertyColumn(header: 'Luftart',
                                        propertyName: 'raumLuftartCombo',
                                        cellEditor: GH.getRaumdatenLuftartCellEditor(builder,model),
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
                                        cellEditor: GH.getRaumVsBezeichnungZuluftventileCellEditor(builder,model),
                                        cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                    ) // combo
                                    propertyColumn(header: GH.ws("Bezeichnung<br/>Abluftventile"),
                                        propertyName: 'raumVsBezeichnungAbluftventileCombo',
                                        cellEditor: GH.getRaumVsBezeichnungAbluftventileCellEditor(builder,model),
                                        cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                    ) // combo
                                    propertyColumn(header: "Verteilebene",
                                        //propertyName: 'raumVsVerteilebeneCombo',
                                        //cellEditor: GH.getRaumVsVerteilebeneCellEditor(builder),
                                        propertyName: 'raumGeschossCombo',
                                        cellEditor: GH.getRaumdatenGeschossCellEditor(builder,model),
                                        cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                    ) // combo
                                    propertyColumn(header: GH.ws("Zuluftmenge<br/>je Ventil"), propertyName: 'raumZuluftmengeJeVentil', editable: false)
                                }
                            }
                        }
                    }
                }
                // Raumvolumenströme - Überströmventile
                panel(id: "raumVsUberstromventileTab", title: "Überströmventile", constraints: "grow") {
                    panel(id: "raumVsUberstromventileTabellePanel", constraints: "grow", layout: new MigLayout("fillx", "[fill]")) {
                        jideScrollPane(constraints: "grow") {
                            //table(id: "raumVsUberstromventileTabelle", model: model.createRaumVsUberstromventileTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                            //}
                            table(id: 'raumVsUberstromventileTabelle', selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION, constraints: "grow") {
                                tableModel() {
                                    current.addTableModelListener(GH.getRaumVsUberstromTableModelListener())
                                    propertyColumn(header: 'Raum',
                                        //propertyName: 'raumBezeichnungUberstromCombo',
                                        //cellEditor: GH.getRaumVsUberstromRaumBezeichnungCellEditor(builder,model),
                                        propertyName: 'raumBezeichnungCombo',
                                        cellEditor: GH.getRaumdatenBezeichnungCellEditor(builder,model),
                                        cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                    )
                                    propertyColumn(header: 'Luftart',
                                        propertyName: 'raumLuftartCombo',
                                        cellEditor: GH.getRaumdatenLuftartCellEditor(builder,model),
                                        cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                    )
                                    propertyColumn(header: GH.ws("Anzahl<br/>Ventile"), propertyName: 'raumAnzahlUberstromVentile')
                                    propertyColumn(header: GH.ws("Volumenstrom<br/>(m³/h)"), propertyName: 'raumVolumenstrom')
                                    propertyColumn(header: GH.ws("Überström<br/>Elemente"),
                                        propertyName: 'raumVsUberstromElementCombo',
                                        cellEditor: GH.getRaumVsUberstromElementCellEditor(builder,model),
                                        cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        panel(constraints: "grow", layout: new MigLayout("wrap", "[]", "")) {
            panel(id: "raumVsVentileTabPanel", layout: new MigLayout("fillx", "[left]para[right]para[left]para[fill]para[left]", "[fill]")) {
                // Informationen
                label("Gesamtvolumen der Nutzungseinheit", constraints: "cell 0 1")
                label(id: "raumVsGesamtVolumenNE", constraints: "cell 1 1")
                label("m³", constraints: "cell 2 1")
                panel(id: "raumVsAussenluftVsDerLtm", border: titledBorder("Außenluftvolumenstrom der lüftungstechnischen Massnahme"), layout: new MigLayout("fillx, wrap 4", "[left]30[right]10[left]30[left]", "[fill]"), constraints: "cell 3 1 1 5") {
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
                button(id: "raumVsRaumBearbeiten", text: "Raum bearbeiten", constraints: "cell 4 1")

                label("Luftwechsel der Nutzungseinheit", constraints: "cell 0 2")
                label(id: "raumVsLuftwechselNE", text: "0,00", constraints: "cell 1 2")
                label("l/h", constraints: "cell 2 2")
                button(id: "raumVsZuAbluftventileSpeichern", text: "Speichern", constraints: "cell 4 2")

                label("Gesamtaußenluft-Volumentstrom mit Infiltration", constraints: "cell 0 3")
                label(id: "raumVsGesamtaussenluftVsMitInfiltration", text: "0,00", constraints: "cell 1 3")
                label("m³/h", constraints: "cell 2 3")
                button(id: "raumVsZuAbluftventileAngebotErstellen", text: "Angebot erstellen", constraints: "cell 4 3")
            }
        }
    }
}

// Event abfangen. model.map.raum.raume aktualisieren...
GH.getRaumdatenBezeichnungCellEditor(builder,model).editingStopped = { e ->
    println "stop editing row: ${-> raumVsUberstromventileTabelle.selectedColumn} ${-> raumVsUberstromventileTabelle.selectedRow} -> ${e}"
    // raumbezeichnung aktualisieren
    if (e.getSource() instanceof javax.swing.DefaultCellEditor) {
        def cellEditor = e.getSource() as javax.swing.DefaultCellEditor
        controller.updateRaumBezeichnungCombo(raumVsUberstromventileTabelle.selectedRow, cellEditor.getCellEditorValue())
    }
}
GH.getRaumdatenLuftartCellEditor(builder,model).editingStopped = { e ->
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

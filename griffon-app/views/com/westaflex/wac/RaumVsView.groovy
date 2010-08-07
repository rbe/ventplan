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
    panel(id: "raumVsVentileTabPanel", layout: new MigLayout("fillx", "[left]para[right]para[left]para[fill]para[left]", "[fill]")) {
        // Tabellen für Zu-/Abluftventile, Überströmventile
        jideTabbedPane(id: "raumVsVentileTabGroup", constraints: "span") {
            // Raumvolumenströme - Zu-/Abluftventile
            panel(id: "raumVsZuAbluftventileTab", title: "Zu-/Abluftventile", constraints: "grow") {
                panel(id: "raumVsZuAbluftventileTabellePanel", constraints: "grow, cell 0 0 5 1", layout: new MigLayout("fillx", "[fill]")) {
                    jideScrollPane(constraints: "grow") {
                        table(id: 'raumVsZuAbluftventileTabelle', selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                            tableModel() {
                                current.addTableModelListener(GH.getRaumVsZuAbluftventileTableModelListener())
                                propertyColumn(header: 'Raum', propertyName: 'raumBezeichnung')
                                propertyColumn(header: 'Luftart', propertyName: 'raumLuftart')
                                propertyColumn(header: GH.ws("Raumvolumen<br/>(m³)"), propertyName: 'raumVolumen')
                                propertyColumn(header: GH.ws("Luftwechsel<br/>(1/h)"), propertyName: 'raumLuftwechsel')
                                propertyColumn(header: GH.ws("Anzahl<br/>Abluftventile"), propertyName: 'raumBezeichnungAbluftventile')
                                propertyColumn(header: GH.ws("Abluftmenge<br/>je Ventil"), propertyName: 'raumAnzahlAbluftventile')
                                propertyColumn(header: GH.ws("Volumenstrom<br/>(m³/h)"), propertyName: 'raumVolumenstrom')
                                propertyColumn(header: GH.ws("Anzahl<br/>Zuluftventile"), propertyName: 'raumAnzahlZuluftventile')
                                propertyColumn(header: GH.ws("Bezeichnung<br/>Zuluftventile"),
                                    propertyName: 'raumBezeichnungZuluftventileCombo',
                                    cellEditor: GH.getRaumBezeichnungZuluftventileCellEditor(builder,model),
                                    cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                ) // combo
                                propertyColumn(header: GH.ws("Bezeichnung<br/>Abluftventile"),
                                    propertyName: 'raumBezeichnungAbluftventileCombo',
                                    cellEditor: GH.getRaumBezeichnungAbluftventileCellEditor(builder,model),
                                    cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                ) // combo
                                propertyColumn(header: "Ventilebene",
                                    propertyName: 'raumVentilebeneCombo',
                                    cellEditor: GH.getRaumVentilebeneCellEditor(builder),
                                    cellRenderer: new javax.swing.table.DefaultTableCellRenderer()
                                ) // combo
                                propertyColumn(header: GH.ws("Zuluftmenge<br/>je Ventil"), propertyName: 'raumZuluftmengeJeVentil')
                            }
                        }
                    }
                }
            }
            // Raumvolumenströme - Überströmventile
            panel(id: "raumVsUberstromventileTab", title: "Überströmventile", constraints: "grow") {
                panel(id: "raumVsUberstromventileTabellePanel", constraints: "grow, cell 0 0 5 1", layout: new MigLayout("fillx", "[fill]")) {
                    jideScrollPane(constraints: "grow") {
                        table(id: "raumVsUberstromventileTabelle", model: model.createRaumVsUberstromventileTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                        }
                    }
                }
            }
        }
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
// raumVsVentileTabGroup
raumVsVentileTabGroup.with {
	setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
}
// Bindings
build(RaumVsBindings)

/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 17:50
 */
package eu.artofcoding.ventplan.desktop

import net.miginfocom.swing.MigLayout

import java.awt.*

// Raumvolumenströme
panel(layout: new MigLayout('fill, wrap', '[fill, grow]', '[fill,grow]'), constraints: 'grow') {
    // Tabellen für Zu-/Abluftventile, Überströmventile
    panel(layout: new MigLayout('ins 0 n 0 n, fill, wrap 1', '[fill, grow]', '[fill,grow]'), constraints: 'grow') {
        // WAC-171
        label(id: 'raumVsTurenHinweis', foreground: Color.RED)
        // WAC-171
        label(id: 'raumVsUbElementeHinweis', foreground: Color.RED)
        // WAC-223
        label(id: 'kaufmannischeArtikelHinweis', foreground: Color.RED, constraints: 'width ::800')
        // WAC-254
        label(id: 'zuluftmengeVerteilebeneHinweis', foreground: Color.RED, constraints: 'width ::800')
        label(id: 'abluftmengeVerteilebeneHinweis', foreground: Color.RED, constraints: 'width ::800')
        //
        jideTabbedPane(id: 'raumVsVentileTabGroup', constraints: 'height ::280, grow, span') {
            // Raumvolumenströme - Zu-/Abluftventile
            panel(id: 'raumVsZuAbluftventileTab', title: 'Zu-/Abluftventile', layout: new MigLayout('ins 0 n 0 n, fill', '[fill,grow]', '[fill,grow]'), constraints: 'grow') {
                panel(id: 'raumVsZuAbluftventileTabellePanel', layout: new MigLayout('ins 0 n 0 n', '[fill, grow]'), constraints: 'grow') {
                    jideScrollPane(constraints: 'grow') {
                        table(id: 'raumVsZuAbluftventileTabelle', model: model.createRaumVsZuAbluftventileTableModel())
                    }
                }
            }
            // Raumvolumenströme - Überströmventile
            panel(id: 'raumVsUberstromventileTab', title: 'Überströmventile', layout: new MigLayout('ins 0 n 0 n, fill', '[fill,grow]', '[fill,grow]'), constraints: 'grow') {
                panel(id: 'raumVsUberstromelementeTabellePanel', layout: new MigLayout('ins 0 n 0 n, fillx', '[fill]'), constraints: 'grow') {
                    jideScrollPane(constraints: 'grow') {
                        table(id: 'raumVsUberstromelementeTabelle', model: model.createRaumVsUberstromelementeTableModel())
                    }
                }
            }
        }
    }
    panel(layout: new MigLayout('', '[] [] [grow]')) {
        panel(layout: new MigLayout('ins 0 n 0 n', '[] [right] []', '[] 16 []')) {
            // Informationen
            label('Gesamtvolumen der Nutzungseinheit')
            label(id: 'raumVsGesamtVolumenNE')
            label('m³', constraints: 'wrap')

            label('Luftwechsel der Nutzungseinheit')
            label(id: 'raumVsLuftwechselNE', text: '0,00')
            label('l/h', constraints: 'wrap')

            label('Gesamtaußenluft-Volumentstrom mit Infiltration')
            label(id: 'raumVsGesamtaussenluftVsMitInfiltration', text: '0,00')
            label('m³/h', constraints: 'wrap')
        }
        panel(border: titledBorder('Außenluftvolumenstrom der LTM'), layout: new MigLayout('ins 0 n 0 n, fill', '[grow]')) {
            panel(layout: new MigLayout('ins 0 n 0 n, fill, wrap 4', '[left] 20 [right] [left] 20 [left]'), constraints: 'grow') {
                label('Feuchteschutz')
                label(id: 'raumVsAussenluftVsDerLtmFs', text: '0,00')
                label('m³/h')
                label('Zentralgerät')

                label('Reduzierte Lüftung')
                label(id: 'raumVsAussenluftVsDerLtmRl', text: '0,00')
                label('m³/h')
                comboBox(id: 'raumVsZentralgerat', items: model.meta.zentralgerat)

                label('Nennlüftung')
                label(id: 'raumVsAussenluftVsDerLtmNl', text: '0,00')
                label('m³/h')
                label('Volumenstrom')

                label('Intensivlüftung')
                label(id: 'raumVsAussenluftVsDerLtmIl', text: '0,00')
                label('m³/h')
                comboBox(id: 'raumVsVolumenstrom', items: model.meta.volumenstromZentralgerat)
            }
            //
            button(id: 'standardAuslassButton', text: 'Standard-Auslässe setzen')
        }
        /* WAC-233
        panel(layout: new MigLayout("ins 0 n 0 n", "[fill, grow]", "[] 10 []"), constraints: "wrap") {
            button(id: "raumVsRaumBearbeiten", text: "Raum bearbeiten", constraints: "wrap")
            //button(id: "raumVsZuAbluftventileSpeichern", text: "Speichern", constraints: "wrap")
            button(id: "raumVsZuAbluftventileAngebotErstellen", text: "Angebot erstellen", constraints: "wrap")
        }
        */
    }
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

//
// JIDE
//
// raumVsVentileTabGroup
raumVsVentileTabGroup.with {
    setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
    setBoldActiveTab(true)
}

// WAC-222 Improvement for showing grid lines.
raumVsZuAbluftventileTabelle.showGrid = true
raumVsZuAbluftventileTabelle.gridColor = Color.GRAY
raumVsUberstromelementeTabelle.showGrid = true
raumVsUberstromelementeTabelle.gridColor = Color.GRAY

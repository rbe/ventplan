/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 17:23
 */

package eu.artofcoding.ventplan.desktop

import net.miginfocom.swing.MigLayout

import javax.swing.table.JTableHeader
import java.awt.*

// Stuckliste view
panel(id: 'stucklisteSuchePanel', layout: new MigLayout('fillx, wrap', '[fill]', '[]0[]'), constraints: 'grow') {

    panel(id: 'stucklisteSuchePanel', layout: new MigLayout('fillx, wrap', '[150:150:,fill]para[150:150:,fill]para[fill]', '[]0[]'), constraints: 'grow') {
        label('Bitte prüfen: Möglicherweise wurde die Stückliste nach der automatischen Berechnung', constraints: 'span 3')
        label('manuell verändert und gespeichert.', constraints: 'span 3')
        label('  ', constraints: 'span 3')

        label('Artikelnr.', constraints: 'span 3')
        //label('Text/Beschreibung')
        //label('Anzahl')

        textField(id: 'stucklisteSucheArtikelnummer', constraints: 'span 2')
        //label(id: 'stucklisteSucheArtikeltext')
        //textField(id: 'stucklisteSucheArtikelanzahl')
        button(id: 'stucklisteSucheStarten', text: 'Suchen')
    }
    panel(id: 'stucklisteErgebnisPanel', layout: new MigLayout('fillx, wrap', '[fill]', '[]0[]'), constraints: 'grow') {
        scrollPane() {
            table(id: 'stucklisteErgebnisTabelle', model: model.createStucklisteErgebnisTableModel()) {
                current.getTableHeader().setDefaultRenderer(new JTableHeader().defaultRenderer)
                current.setAutoCreateRowSorter(false)
                current.setRowSorter(null)
                current.setFillsViewportHeight(false)
                current.setPreferredScrollableViewportSize(new Dimension(400,120));
            }
        }
        button(id: 'stucklisteSucheHinzufugen', text: 'Ausgewählten Artikel zur Stückliste hinzufügen')
    }

    panel(id: 'stucklisteUbersichtPanel', layout: new MigLayout('fillx, wrap', '[fill]', '[]0[]'), constraints: 'grow') {
        zoneLayout {
            zoneRow('y+*y')
        }
        panel(constraints: "y", border: compoundBorder(outer: emptyBorder(1), inner: emptyBorder(1))) {
            zl = zoneLayout {
                zoneRow('a+*......1b^<.b')
                zoneRow('.........1c^<.c')
                zoneRow('.........1d^<.d')
                zoneRow('........a1e^<.e')
                zoneRow('........1......')
                zoneRow('f-............f')
            }

            scrollPane(constraints: 'a') {
                table(id: 'stucklisteUbersichtTabelle', model: model.createStucklisteUbersichtTableModel()) {
                    current.getTableHeader().setDefaultRenderer(new JTableHeader().getDefaultRenderer())
                    current.setAutoCreateRowSorter(false)
                    current.setRowSorter(null)
                    current.setFillsViewportHeight(false)
                    current.setPreferredScrollableViewportSize(new Dimension(380,120));
                }
            }

            button(id: 'stucklisteUbersichtSortierNachObenVerschieben', text: '^', constraints: 'b')
            button(id: 'stucklisteUbersichtSortierNachUntenVerschieben', text: 'v', constraints: 'c')

            button(id: 'stucklisteUbersichtArtikelMengePlusEins', text: '+', constraints: 'd')
            button(id: 'stucklisteUbersichtArtikelMengeMinusEins', text: '-', constraints: 'e')

            button(id: 'stucklisteUbersichtLoescheArtikel', text: 'Ausgewählten Artikel aus Liste löschen', constraints: 'f')
        }
    }
    panel(id: 'stucklisteBottomButtonPanel', layout: new MigLayout('fill, wrap', '[]para[]', '[]0[]'), constraints: 'grow') {
        button(id: 'stucklisteAbbrechen', text: 'Vorgang abbrechen')
        button(id: 'stucklisteWeiter', text: 'Weiter')
    }

}

// Bindings
build(StucklisteBindings)

// WAC-222 Improvement for showing grid lines.
stucklisteErgebnisTabelle.showGrid = true
stucklisteErgebnisTabelle.gridColor = Color.GRAY
stucklisteUbersichtTabelle.showGrid = true
stucklisteUbersichtTabelle.gridColor = Color.GRAY

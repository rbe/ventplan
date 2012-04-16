/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout

import com.bensmann.griffon.GriffonHelper as GH

import java.text.SimpleDateFormat
import javax.swing.table.JTableHeader

// Stuckliste view
panel(id: 'stucklisteSuchePanel', layout: new MigLayout('fillx, wrap', '[fill]', '[]para[]'), constraints: 'grow') {
    panel(id: 'stucklisteSuchePanel', layout: new MigLayout('fillx, wrap', '[fill]para[fill]para[fill]', ''), constraints: 'grow') {
        label('Artikelnr.', constraints: 'span 3')
        //label('Text/Beschreibung')
        //label('Anzahl')

        textField(id: 'stucklisteSucheArtikelnummer', constraints: "span 2")
        //label(id: 'stucklisteSucheArtikeltext')
        //textField(id: 'stucklisteSucheArtikelanzahl')
        button(id: 'stucklisteSucheStarten', text: 'Suchen')

    }
    panel(id: 'stucklisteErgebnisPanel', layout: new MigLayout('fillx, wrap', '[fill]', ''), constraints: 'grow') {
        scrollPane() {
            table(id: 'stucklisteErgebnisTabelle', model: model.createStucklisteErgebnisTableModel()) {
                current.setSortable(false)
                current.getTableHeader().setDefaultRenderer(new JTableHeader().getDefaultRenderer())
                current.setAutoCreateRowSorter(false)
                current.setRowSorter(null)
                current.setFillsViewportHeight(true)
            }
        }
        button(id: 'stucklisteSucheHinzufugen', text: 'Ausgewählten Artikel zur Stückliste hinzufügen')
    }


    panel(id: 'stucklisteUbersichtPanel', layout: new MigLayout('fillx, wrap', '[fill]', ''), constraints: 'grow') {
        zoneLayout {
            zoneRow('y+*y')
        }
        panel(constraints: "y", border: compoundBorder(outer: emptyBorder(5), inner: emptyBorder(5))) {
            zl = zoneLayout {
                zoneRow('a+*......1b^<.b')
                zoneRow('.........1c^<.c')
                zoneRow('.........1d^<.d')
                zoneRow('........a1e^<.e')
                zoneRow('........2......')
                zoneRow('f-............f')
            }

            scrollPane(constraints: 'a') {
                table(id: 'stucklisteUbersichtTabelle', model: model.createStucklisteUbersichtTableModel()) {
                    current.setSortable(false)
                    current.getTableHeader().setDefaultRenderer(new JTableHeader().getDefaultRenderer())
                    current.setAutoCreateRowSorter(false)
                    current.setRowSorter(null)
                    current.setFillsViewportHeight(true)
                }
            }

            button(id: 'stucklisteUbersichtSortierNachObenVerschieben', text: '^', constraints: 'b')
            button(id: 'stucklisteUbersichtSortierNachUntenVerschieben', text: 'v', constraints: 'c')

            button(id: 'stucklisteUbersichtArtikelMengePlusEins', text: '+', constraints: 'd')
            button(id: 'stucklisteUbersichtArtikelMengeMinusEins', text: '-', constraints: 'e')

            button(id: 'stucklisteUbersichtLoescheArtikel', text: 'Ausgewählten Artikel aus Liste löschen', constraints: 'f')
        }
    }
    panel(id: 'stucklisteUbersichtPanel', layout: new MigLayout('fill, wrap', '[]para[]', ''), constraints: 'grow') {
        button(id: 'stucklisteWeiter', text: 'Weiter zur Stücklisten-Generierung')
        button(id: 'stucklisteAbbrechen', text: 'Vorgang abbrechen')
    }
}

build(StucklisteBindings)

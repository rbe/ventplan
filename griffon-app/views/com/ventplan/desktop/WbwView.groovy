/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/16/12 10:35 AM
 */
package com.ventplan.desktop

import com.bensmann.griffon.GriffonHelper as GH

import net.miginfocom.swing.MigLayout

panel(id: 'wbwPanel', layout: new MigLayout('fillx, wrap 2', '[fill][fill]', '[fill][fill]')) {

    // Links oben: Tabelle: Anzahl (Textfeld), Bezeichnung des Widerstands, Widerstandswert
    panel(id: 'wbwTabellePanel', layout: new MigLayout('fill', '[fill]', '[fill]')) {
        jideScrollPane() {
            table(id: 'wbwTabelle', model: model.createWbwTableModel(), selectionMode: ListSelectionModel.SINGLE_SELECTION) {
                // WAC-160: Feste Spaltenbreiten vergeben.
                current.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                //current.removeColumn(current.columnModel.getColumn(0)) // REMOVE
                current.columnModel.getColumn(0).setPreferredWidth(60)
                current.columnModel.getColumn(1).setPreferredWidth(200)
                current.columnModel.getColumn(2).setPreferredWidth(120)
                current.columnModel.getColumn(3).setMaxWidth(0)
            }
        }
    }
    // Rechts oben: Bezeichnung (Textfeld), Widerstandsbeiwert (Textfeld), Anzahl (Textfeld)
    panel(id: 'wbwPflege', layout: new MigLayout('fillx, wrap 1', '[fill]', '[fill][fill]')) {

        panel(layout: new MigLayout('fillx, wrap 1', '[fill]', '[fill][fill]')) {
            label('Bezeichnung')
            textField(id: 'wbwBezeichnung')

            label('Widerstandsbeiwert')
            textField(id: 'wbwWert')

            label('Anzahl')
            textField(id: 'wbwAnzahl')

            button(id: 'wbwSaveButton', text: 'Übernehmen')
            label()
        }

        panel(background: Color.WHITE, layout: new MigLayout('fill', '[center]', ''), constraints: 'span, grow, height 300px!, width 300px!') {
            label(id: 'wbwBild', text: '-- kein Bild --', background: Color.WHITE, constraints: 'height 220px!, width 220px!')
        }

    }
    // Links unten: Summe aller Einzelwiderstände
    panel(id: 'wbwSummePanel', layout: new MigLayout('fillx', '[left][right]', '[fill]')) {
        label('<html><b>Summe aller Einzelwiderstände</b></html>')
        label(id: 'wbwSumme', text: bind(source: model.meta, sourceProperty: 'summeAktuelleWBW', converter: { v -> "<html><b>${v.toString2()}</b></html>" }))
    }
    // Rechts unten: Buttons
    panel(id: 'wbwButton', layout: new MigLayout('fillx', '[left][right]', '[fill]')) {
        button(id: 'wbwOk', text: 'OK')
        button(id: 'wbwCancel', text: 'Abbrechen')
    }

}

// Format fields
GH.autoformatDoubleTextField(wbwWert)
// Bindings
build(WbwBindings)
// WAC-222 Improvement for showing grid lines.
wbwTabelle.showGrid = true
wbwTabelle.gridColor = Color.GRAY

/* Numeric document model */
class NumericDocument extends javax.swing.text.PlainDocument {

    public NumericDocument() {
        super();
    }

    //Insert string method
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str != null) {
            if (isNumeric(str) == true) {
                super.insertString(offset, str, attr);
            }
        }
        return;
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}

wbwAnzahl.setDocument(new NumericDocument())
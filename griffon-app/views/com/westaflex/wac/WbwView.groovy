/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/views/com/westaflex/wac/WbwView.groovy
 * Last modified at 27.03.2011 19:22:24 by rbe
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout
import javax.swing.text.*

panel(id: "wbwPanel", layout: new MigLayout("fillx, wrap 2", "[fill][fill]", "[fill][fill]")) {
	
	// Links oben: Tabelle: Anzahl (Textfeld), Bezeichnung des Widerstands, Widerstandswert
	panel(id: "wbwTabellePanel", layout: new MigLayout("fill", "[fill]", "[fill]")) {
		jideScrollPane() {
			table(id: "wbwTabelle", model: model.createWbwTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
				current.columnModel.getColumn(0).setWidth(15)
				current.columnModel.getColumn(2).setWidth(30)
			}
		}
	}
	// Rechts oben: Bezeichnung (Textfeld), Widerstandsbeiwert (Textfeld), Anzahl (Textfeld)
	panel(id: "wbwPflege", layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill][fill]")) {
		
		panel(layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill][fill]")) {
			label("Bezeichnung")
			textField(id: "wbwBezeichnung")
			
			label("Widerstandsbeiwert")
			textField(id: "wbwWert")
			
			label("Anzahl")
			textField(id: "wbwAnzahl")
			
			button(id: "wbwSaveButton", text: "Übernehmen")
			label()
		}
		
		panel(background: java.awt.Color.WHITE, layout: new MigLayout("fill", "[center]", ""), constraints: "span, grow, height 300px!, width 300px!") {
			label(id: "wbwBild", text: "-- kein Bild --", background: java.awt.Color.WHITE, constraints: "height 220px!, width 220px!")
		}
	}
	
	// Links unten: Summe aller Einzelwiderstände
	panel(id: "wbwSummePanel", layout: new MigLayout("fillx", "[left][right]", "[fill]")) {
		label("<html><b>Summe aller Einzelwiderstände</b></html>")
		label(id: "wbwSumme", text: bind(source: model.meta, sourceProperty: "summeAktuelleWBW", converter: { v -> "<html><b>${v.toString2()}</b></html>" }))
	}
	// Rechts unten: Buttons
	panel(id: "wbwButton", layout: new MigLayout("fillx", "[left][right]", "[fill]")) {
		button(id: "wbwOk", text: "OK")
		button(id: "wbwCancel", text: "Abbrechen")
	}
	
}
// Format fields
GH.autoformatDoubleTextField(wbwWert)
// Bindings
build(WbwBindings)

/*Numeric document model*/
class NumericDocument extends PlainDocument {

     public NumericDocument() {
          super();
     }

     //Insert string method
     public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
          if (str != null){
               if (isNumeric(str) == true) {
                    super.insertString(offset, str, attr);
               }
          }
          return;
     }

    private boolean isNumeric(String str) {
        try{
            Integer.parseInt(str);
        } catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}

wbwAnzahl.setDocument(new NumericDocument())

/**
 * /Users/rbe/project/wac2/griffon-app/views/com/westaflex/wac/WbwView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout

panel(id: "wbwPanel", layout: new MigLayout("debug, fillx, wrap 2", "[fill][fill]", "[fill][fill]")) {
	
	// Links oben: Tabelle: Anzahl (Textfeld), Bezeichnung des Widerstands, Widerstandswert
	panel(id: "wbwTabellePanel", layout: new MigLayout("fill", "[fill]", "[fill]")) {
		jideScrollPane() {
			table(id: "wbwTabelle", model: model.createWbwTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
			}
		}
	}
	// Rechts oben: Bezeichnung (Textfeld), Widerstandsbeiwert (Textfeld), Anzahl (Textfeld)
	panel(id: "wbwPflege", layout: new MigLayout("debug, fillx, wrap 2", "[fill][fill]", "[fill][fill]")) {
		
		panel(layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill][fill]")) {
			button("<<")
			button(">>")
		}
		
		panel(layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill][fill]")) {
			label("Bezeichnung")
			textField()
			
			label("Widerstandsbeiwert")
			textField()
			
			label("Anzahl")
			textField()
		}
		
		// TODO mmu Set initial size so label won't resize when an image is displayed
		label(id: "wbwBild", text: "-- kein Bild --", constraints: "span, grow")
	}
	
	// Links unten: Summe aller Einzelwiderstände
	panel(id: "wbwSumme", layout: new MigLayout("fillx", "[left][right]", "[fill]")) {
		label("<html><b>Summe aller Einzelwiderstände</b></html>")
		label("<html><b>0,00</b></html>")
	}
	// Rechts unten: Buttons
	panel(id: "wbwButton", layout: new MigLayout("fillx", "[left][right]", "[fill]")) {
		button(id: "wbwOk", text: "OK")
		button(id: "wbwCancel", text: "Abbrechen")
	}
	
}
// Bindings
build(WbwBindings)

/**
 * /Users/rbe/project/wac2/griffon-app/views/com/westaflex/wac/WbwView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout

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
// Bindings
build(WbwBindings)

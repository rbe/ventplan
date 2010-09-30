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

panel(id: "teilstreckenPanel", layout: new MigLayout("debug, fillx, wrap 2", "[fill][fill]", "[fill][fill]")) {
	
	// Links oben: Tabelle: Anzahl (Textfeld), Bezeichnung des Widerstands, Widerstandswert
	panel(id: "teilstreckenTabellePanel", layout: new MigLayout("fill", "[fill]", "[fill]")) {
	}
	// Rechts oben: Bezeichnung (Textfeld), Widerstandsbeiwert (Textfeld), Anzahl (Textfeld)
	panel(id: "teilstreckenPflege", layout: new MigLayout("debug, fillx, wrap 2", "[fill][fill]", "[fill][fill]")) {
		
		panel(layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill][fill]")) {
			button("<<")
			button(">>")
		}
		
		panel(layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill][fill]")) {
			label("Bezeichnung")
			textField()
			
			label("Teilstrecke")
			textField()
			
			label("Anzahl")
			textField()
		}
		
		// TODO mmu Set initial size so label won't resize when an image is displayed
		label(id: "teilstreckenBild", text: "-- kein Bild --", constraints: "span, grow")
	}
	
}
// Bindings
build(TeilstreckenBindings)

/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/bindings/com/westaflex/wac/RaumdatenBindings.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.westaflex.wac

// Raumdaten - Raum-Eingabe
// Add list selection listener to synchronize every table's selection and model.meta.gewahlterRaum
[raumTabelle].each {
	it.selectionModel.addListSelectionListener([
		valueChanged: { evt ->
				controller.raumInTabelleGewahlt(evt, it)
			}
		] as javax.swing.event.ListSelectionListener)
}
// Binding for items of comboboxes is done in RaumdatenView!
// Combobox Raumtyp
raumTyp.actionPerformed = controller.raumTypGeandert
// Raum-Buttons unten
raumHinzufugen.actionPerformed = controller.raumHinzufugen
//raumHinzufugen.enabled = model.map.raum.raume?.size() > 0 ? true : false
raumEntfernen.actionPerformed = controller.raumEntfernen
//raumEntfernen.enabled = model.map.raum.raume?.size() > 0 ? true : false
raumKopieren.actionPerformed = controller.raumKopieren
//raumKopieren.enabled = model.map.raum.raume?.size() > 0 ? true : false
raumBearbeiten.actionPerformed = controller.raumBearbeiten
//raumBearbeiten.enabled = model.map.raum.raume?.size() > 0 ? true : false
raumNachObenVerschieben.actionPerformed = controller.raumNachObenVerschieben
//raumNachObenVerschieben.enabled = model.map.raum.raume?.size() > 0 ? true : false
raumNachUntenVerschieben.actionPerformed = controller.raumNachUntenVerschieben
//raumNachUntenVerschieben.enabled = model.map.raum.raume?.size() > 0 ? true : false

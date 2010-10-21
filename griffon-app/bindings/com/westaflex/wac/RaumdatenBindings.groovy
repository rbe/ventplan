/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/bindings/com/westaflex/wac/RaumdatenBindings.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
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
raumEntfernen.actionPerformed = controller.raumEntfernen
raumKopieren.actionPerformed = controller.raumKopieren
raumBearbeiten.actionPerformed = controller.raumBearbeiten
raumNachObenVerschieben.actionPerformed = controller.raumNachObenVerschieben
raumNachUntenVerschieben.actionPerformed = controller.raumNachUntenVerschieben

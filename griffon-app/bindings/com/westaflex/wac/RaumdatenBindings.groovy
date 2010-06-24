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
// Add list selection listener and synchronize every table's selection and model.meta.gewahlterRaum
raumTabelle.selectionModel.addListSelectionListener([
	valueChanged: { evt ->
			controller.raumInTabelleGewahlt(evt)
		}
	] as javax.swing.event.ListSelectionListener)
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

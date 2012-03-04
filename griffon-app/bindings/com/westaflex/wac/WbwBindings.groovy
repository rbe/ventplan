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

// Add list selection listener to select a Wbw and show its picture
[wbwTabelle].each {
	it.selectionModel.addListSelectionListener([
		valueChanged: { evt ->
				controller.wbwInTabelleGewahlt(evt)
			}
		] as javax.swing.event.ListSelectionListener)
}
// Buttons
wbwOk.actionPerformed = controller.wbwOkButton
wbwCancel.actionPerformed = controller.wbwCancelButton
wbwSaveButton.actionPerformed = controller.wbwSaveButton

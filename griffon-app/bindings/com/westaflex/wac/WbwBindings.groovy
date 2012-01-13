/**
 * WAC
 * 
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. All rights reserved.
 * Created by: rbe
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

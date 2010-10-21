/**
 * /Users/rbe/project/wac2/griffon-app/bindings/com/westaflex/wac/WbwBindings.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
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

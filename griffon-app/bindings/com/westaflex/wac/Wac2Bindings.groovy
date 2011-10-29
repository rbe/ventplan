/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/bindings/Wac2Bindings.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

// ChangeListener for active tab; tell model about its MVC ID
projektTabGroup.addChangeListener({ evt ->
		controller.projektIndexAktivieren(evt.source.selectedIndex)
	} as javax.swing.event.ChangeListener)

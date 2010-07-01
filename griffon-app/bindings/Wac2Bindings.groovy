/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/bindings/Wac2Bindings.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */

// ChangeListener for active tab; tell model about its MVC ID
projektTabGroup.addChangeListener({ evt ->
		controller.projektIndexAktivieren(evt.source.selectedIndex)
	} as javax.swing.event.ChangeListener)

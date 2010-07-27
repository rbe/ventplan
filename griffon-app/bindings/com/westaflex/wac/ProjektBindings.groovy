/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/bindings/com/westaflex/wac/ProjektBindings.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.westaflex.wac

[projektTabGroup].each {
	it.addChangeListener([
		stateChanged: { evt ->
				controller.closeTab(evt)
			}
		] as javax.swing.event.ChangeListener)
}

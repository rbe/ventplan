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

// ChangeListener for active tab; tell model about its MVC ID
projektTabGroup.addChangeListener({ evt ->
		controller.projektIndexAktivieren(evt.source.selectedIndex)
	} as javax.swing.event.ChangeListener)

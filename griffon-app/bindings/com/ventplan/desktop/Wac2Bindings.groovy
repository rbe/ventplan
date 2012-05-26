/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.ventplan.desktop

// ChangeListener for active tab; tell model about its MVC ID
projektTabGroup.addChangeListener({ evt ->
		controller.projektIndexAktivieren(evt.source.selectedIndex)
	} as javax.swing.event.ChangeListener)

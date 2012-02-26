/*
 * WAC
 *
 * Copyright (C) 2005      Informationssysteme Ralf Bensmann.
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

application {
	title='Wac2'
	startupGroups=['wac2', 'Dialog']
	autoShutdown=false
}
mvcGroups {
	'Projekt' {
		model="com.westaflex.wac.ProjektModel"
		controller="com.westaflex.wac.ProjektController"
		actions="com.westaflex.wac.ProjektActions"
		view="com.westaflex.wac.ProjektView"
	}
	'wac2' {
		model="com.westaflex.wac.Wac2Model"
		controller="com.westaflex.wac.Wac2Controller"
		actions="com.westaflex.wac.Wac2Actions"
		view="com.westaflex.wac.Wac2View"
	}
	Dialog {
		controller="com.westaflex.wac.DialogController"
	}
}

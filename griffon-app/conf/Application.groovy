/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/8/12 3:17 PM
 */

application {
    title = 'Ventplan'
    startupGroups = ['MainFrame', 'Dialog']
    autoShutdown = false
}
mvcGroups {
    'Projekt' {
        model = 'com.ventplan.desktop.ProjektModel'
        controller = 'com.ventplan.desktop.ProjektController'
        actions = 'com.ventplan.desktop.ProjektActions'
        view = 'com.ventplan.desktop.ProjektView'
    }
    'MainFrame' {
        model = 'com.ventplan.desktop.VentplanModel'
        controller = 'com.ventplan.desktop.VentplanController'
        actions = 'com.ventplan.desktop.VentplanActions'
        view = 'com.ventplan.desktop.VentplanView'
    }
    Dialog {
        controller = 'com.ventplan.desktop.DialogController'
    }
}

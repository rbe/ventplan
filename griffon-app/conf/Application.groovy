/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

application {
    title = 'VentPlan'
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

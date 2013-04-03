/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 18:32
 */

application {
    title = 'Ventplan'
    startupGroups = ['MainFrame', 'Dialog']
    autoShutdown = true
}
mvcGroups {
    'Dialog' {
        controller = 'eu.artofcoding.ventplan.desktop.DialogController'
    }
    'Projekt' {
        model      = 'eu.artofcoding.ventplan.desktop.ProjektModel'
        controller = 'eu.artofcoding.ventplan.desktop.ProjektController'
        view       = 'eu.artofcoding.ventplan.desktop.ProjektView'
    }
    'MainFrame' {
        model      = 'eu.artofcoding.ventplan.desktop.VentplanModel'
        view       = 'eu.artofcoding.ventplan.desktop.VentplanView'
        controller = 'eu.artofcoding.ventplan.desktop.VentplanController'
    }

}

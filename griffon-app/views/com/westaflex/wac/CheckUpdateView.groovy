/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */


package com.westaflex.wac

import net.miginfocom.swing.MigLayout
import com.bensmann.griffon.GriffonHelper as GH

// About view
panel(layout: new MigLayout("wrap", "[center]", "[fill]"), constraints: "grow") {
    label(icon: imageIcon("/image/ventplan_splash.png"), constraints: "wrap")

    label(' ')
    label("Es liegt ein Update bereit!")

    label(' ')
    label(id: '', 'http://www.ventplan.com/latest/', mouseReleased: { e ->
        java.awt.Desktop.desktop.browse(java.net.URI.create('http://www.ventplan.com/latest/'))
    })
    /*
    label(' ')
    button('Schliessen', actionPerformed: { e ->
        println "ACTION ${this}"
    })
    */
}

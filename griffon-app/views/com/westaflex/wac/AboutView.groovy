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
package com.westaflex.wac

import net.miginfocom.swing.MigLayout
import com.bensmann.griffon.GriffonHelper as GH
import java.text.SimpleDateFormat

SimpleDateFormat sdf = new SimpleDateFormat('yyyy')

// About view
panel(layout: new MigLayout("wrap", "[center]", "[fill]"), constraints: "grow") {
    label(icon: imageIcon("/image/ventplan_splash.png"), constraints: "wrap")

    label(' ')
    label("Version: ${GH.localVersion()}")
    label('Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann')
    label("Copyright (C) 2011-${sdf.format(new Date())} art of coding UG (haftungsbeschränkt)")

    label(' ')
    label('Diese Software wurde gemeinsam mit einem Hersteller von')
    label('Lüftungsanlagen unter Orientierung an der DIN 1946-6 entwickelt.')
    label(' ')
    label('Trotz grösster Sorgfalt bei Entwicklung von Software kann')
    label('keine Garantie für die Berechnungen und deren Richtigkeit')
    label('im Hinblick auf das konkrete Bauvorhaben gegeben werden.')
    /*
    label(' ')
    button('Schliessen', actionPerformed: { e ->
        println "ACTION ${this}"
    })
    */
}

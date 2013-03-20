/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 17:23
 */
package eu.artofcoding.ventplan.desktop

import net.miginfocom.swing.MigLayout

import java.text.SimpleDateFormat

// About view
SimpleDateFormat sdf = new SimpleDateFormat('yyyy')
panel(layout: new MigLayout('wrap', '[center]', '[fill]'), constraints: 'grow') {
    label(icon: imageIcon('/image/ventplan_splash.png'), constraints: 'wrap')
    label(' ')
    label("Version: ${VentplanResource.ventplanVersion}")
    panel(layout: new MigLayout('wrap', '[left]', '[fill]')) {
        label('Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann')
        label("Copyright (C) 2011-${sdf.format(new Date())} art of coding UG (haftungsbeschränkt)")
        label("Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.")
    }
    panel(layout: new MigLayout('wrap', '[left]', '[fill]')) {
        label('Haftungsausschluss:')
        label('Diese Software wurde gemeinsam mit einem Hersteller von')
        label('Lüftungsanlagen unter Orientierung an der DIN 1946-6 entwickelt.')
        label('Trotz grösster Sorgfalt bei Entwicklung von Software kann')
        label('keine Garantie für die Berechnungen und deren Richtigkeit gegeben werden.')
    }
    label(' ')
    button('Schliessen', actionPerformed: { e ->
        controller.aboutDialog.dispose()
    })
}

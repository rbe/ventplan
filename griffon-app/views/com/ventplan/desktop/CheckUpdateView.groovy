/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/16/12 10:35 AM
 */
package com.ventplan.desktop

import net.miginfocom.swing.MigLayout
import com.bensmann.griffon.GriffonHelper as GH

def mr = { e ->
    String url = GH.getVentplanProperties().get('ventplan.update.info.url')
    java.awt.Desktop.desktop.browse(java.net.URI.create(url))
}

// About view
panel(layout: new MigLayout("wrap", "[center]", "[fill]"), constraints: "grow") {
    label(icon: imageIcon('/image/Ventplan_splash.png'), constraints: 'wrap', mouseReleased: mr)

    label(' ')
    label('Es liegt ein Update für Sie bereit!')
    label('Bitte klicken Sie auf den nachstehenden Link.')

    label(' ')
    label('http://www.ventplan.com/latest/', foreground: Color.BLUE, mouseReleased: mr)

    label(' ')
    button('Ja, ich habe das gelesen!', actionPerformed: { e ->
        controller.checkUpdateDialog.dispose()
    })
}

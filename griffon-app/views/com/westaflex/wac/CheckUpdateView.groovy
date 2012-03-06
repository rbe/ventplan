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
import java.awt.Color

def mr = { e ->
    String url = GH.getVentplanProperties().get('ventplan.update.info.url')
    java.awt.Desktop.desktop.browse(java.net.URI.create(url))
}

// About view
panel(layout: new MigLayout("wrap", "[center]", "[fill]"), constraints: "grow") {
    label(icon: imageIcon('/image/VentPlan_splash.png'), constraints: 'wrap', mouseReleased: mr)

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

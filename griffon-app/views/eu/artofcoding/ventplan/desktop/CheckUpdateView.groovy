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

def mr = { e ->
    String url = VentplanResource.getVentplanProperties().get('ventplan.update.info.url')
    java.awt.Desktop.desktop.browse(java.net.URI.create(url))
}

// About view
panel(layout: new MigLayout("wrap", "[center]", "[fill]"), constraints: "grow") {
    label(icon: imageIcon('/image/ventplan_splash.png'), constraints: 'wrap', mouseReleased: mr)

    label(' ')
    label('Es liegt ein Update für Sie bereit!')
    label('Bitte klicken Sie auf den nachstehenden Link.')

    label(' ')
    label('http://www.ventplan.com/download.html', foreground: Color.BLUE, mouseReleased: mr)

    label(' ')
    button('Ja, ich habe das gelesen!', actionPerformed: { e ->
        controller.checkUpdateDialog.dispose()
    })
}

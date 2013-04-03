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

import java.awt.*

vbox {
    separator()
    panel {
        gridBagLayout()
        progressBar(id: 'mainStatusProgressBar', minimum: 0, maximum: 100, indeterminate: bind { model.statusProgressBarIndeterminate })
        label(id: 'mainStatusBarText', text: bind { model.statusBarText },
                constraints: gbc(weightx: 1.0,
                        anchor: GridBagConstraints.WEST,
                        fill: GridBagConstraints.HORIZONTAL,
                        insets: [1, 3, 1, 3])
        )
    }
}
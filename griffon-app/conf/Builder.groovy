/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 18:21
 */

root {
    'groovy.swing.SwingBuilder' {
        controller = ['Threading']
        view = '*'
    }
    'griffon.builder.jide.JideBuilder' {
        view = '*'
    }
    'OxbowGriffonAddon' {
        addon = true
        controller = ['ask', 'confirm', 'choice', 'error', 'inform', 'input', 'showException', 'radioChoice', 'warn']
    }
}

jx {
    'groovy.swing.SwingXBuilder' {
        controller = ['withWorker']
        view = '*'
    }
}

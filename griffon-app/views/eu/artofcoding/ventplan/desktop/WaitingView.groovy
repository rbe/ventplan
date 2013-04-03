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

// Auslegung input dialog view
panel(id: "waitingPanel", layout: new MigLayout("fill, wrap", "[fill]"), constraints: "width 400px!") {
    label("Bitte warten Sie während das Dokument erstellt wird...")
    progressBar(id: "waitingProgressBar", minimum: 0, maximum: 100, indeterminate: true )
}

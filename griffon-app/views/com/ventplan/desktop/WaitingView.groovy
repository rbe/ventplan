/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.ventplan.desktop

import net.miginfocom.swing.MigLayout

// Auslegung input dialog view
panel(id: "waitingPanel", layout: new MigLayout("fill, wrap", "[fill]"), constraints: "width 400px!") {
    label("Bitte warten Sie während das Dokument erstellt wird...")
    progressBar(id: "waitingProgressBar", minimum: 0, maximum: 100, indeterminate: true )
}

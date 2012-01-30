/**
 * WAC
 * 
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. All rights reserved.
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout

// Auslegung input dialog view
panel(id: "waitingPanel", layout: new MigLayout("fill, wrap", "[fill]"), constraints: "width 400px!") {
    label("Bitte warten Sie während die Auslegung erstellt wird...")
    progressBar(id: "waitingProgressBar", minimum: 0, maximum: 100, indeterminate: true )
}
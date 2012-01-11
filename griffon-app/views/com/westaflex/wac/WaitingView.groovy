/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/com/westaflex/wac/RaumVsView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011 art of coding UG (haftungsbeschränkt)
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout

// Auslegung input dialog view
panel(id: "waitingPanel", layout: new MigLayout("fill, wrap", "[fill]"), constraints: "width 400px!") {
    label("Bitte warten Sie während die Auslegung erstellt wird...")
    progressBar(id: "waitingProgressBar", minimum: 0, maximum: 100, indeterminate: true )
}

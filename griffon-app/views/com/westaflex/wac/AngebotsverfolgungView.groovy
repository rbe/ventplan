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

// Angebotsverfolgung view
panel(layout: new MigLayout("wrap", "[100] [300,grow] [100]", "[fill]")) {
    
    label("Geben Sie erst einen Namen ein, damit Sie eine Datei hochladen können.", constraints: "span 3")

    label("Name:")
    textField(id: "angebotsverfolgungName", text: "")
    label("")
    
    label("Datei:")
    textField(id: "angebotsverfolgungFileDialog", text: "")    
    button(id: "angebotsverfolgungButton", text: "Datei wählen und hochladen")
    
}

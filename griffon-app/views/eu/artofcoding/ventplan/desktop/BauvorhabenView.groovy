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

// Bauvorhaben input dialog view
panel(id: "bauvorhabenDialogPanel", layout: new MigLayout("fillx, wrap", "[]para[fill]para[fill]", ''), constraints: "grow") {
    
    label("Bauvorhaben")
    textField(id: "bauvorhabenDialogBauvorhaben", constraints: "grow, span 2")

    label("Plz/Ort")
    textField(id: "bauvorhabenDialogPlz", constraints: "width 80px!")
    textField(id: "bauvorhabenDialogOrt", constraints: "width 150px!")

    label("Angebotsnummer")
    textField(id: "bauvorhabenDialogAngebotsnummer", constraints: "grow, span 2")

    checkBox(id: "bauvorhabenDialogAGB", text: "Akzeptieren Sie unsere AGB?", constraints: "grow, span 2")
    button(id: "bauvorhabenDialogAGBOeffnen", text: "AGBs öffnen")

    label('')
    label('')
    button(id: "bauvorhabenDialogAbsenden", text: "Eingaben speichern und Dokument erstellen")
}

build(BauvorhabenBindings)

/*
 * WAC
 *
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout

// Auslegung input dialog view
panel(id: "auslegungErstellerPanel", layout: new MigLayout("fillx, wrap", "[]para[fill]para[fill]", ""), constraints: "grow") {
    
    label("Firma")
    textField(id: "auslegungErstellerFirma", constraints: "grow, span 2")

    label("Name")
    textField(id: "auslegungErstellerName", constraints: "grow, span 2")

    label("Anschrift")
    textField(id: "auslegungErstellerAnschrift", constraints: "grow, span 2")

    label("Plz/Ort")
    textField(id: "auslegungErstellerPlz", constraints: "width 80px!")
    textField(id: "auslegungErstellerOrt", constraints: "width 150px!")

    label("Telefon")
    textField(id: "auslegungErstellerTelefon", constraints: "grow, span 2")

    label("Fax")
    textField(id: "auslegungErstellerFax", constraints: "grow, span 2")

    label("Email")
    textField(id: "auslegungErstellerEmail", constraints: "grow, span 2")

    label("Angebotsnummer")
    textField(id: "auslegungErstellerAngebotsnummer", constraints: "grow, span 2")

    button(id: "auslegungErstellerAbbruch", text: "Abbrechen")
    label("")
    // Kompletter Text, damit Dimension stimmt, wenn Text nachträglich geändert wird (durch Controller/Action)
    button(id: "auslegungErstellerSpeichern", text: "Eingaben speichern und Auslegung erstellen")
}

build(NutzerdatenBindings)
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
panel(id: "auslegungErstellerPanel", layout: new MigLayout("fillx, wrap", "[]para[fill]para[fill]", ''), constraints: "grow") {
    
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

    label('')
    label('')
    button(id: "nutzerdatenSpeichernButton", text: "Eingaben speichern und Auslegung erstellen")
}

build(AuslegungBindings)

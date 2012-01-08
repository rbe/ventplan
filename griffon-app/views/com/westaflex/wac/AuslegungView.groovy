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
import com.bensmann.griffon.GriffonHelper as GH

// Auslegung input dialog view
panel(id: 'auslegungErstellerPanel') {
    zoneLayout {
        zoneRow('a+*a')
    }
    panel(constraints: 'a', border: compoundBorder(outer: emptyBorder(5), inner: emptyBorder(5))) {

        zone = zoneLayout {
            zoneRow('a+*a2b...+*..b', template: 'inputRow')
            zoneRow('c+*c2d+*d2e+*e', template: 'inputRow2')
            zoneRow('f.....+*.....f', template: 'buttonRow')
        }

        zone.insertTemplate('inputRow');

        label("Firma", constraints: 'a')
        textField(id: "auslegungErstellerFirma", constraints: 'b')

        label("Name", constraints: 'a')
        textField(id: "auslegungErstellerName", constraints: 'b')

        label("Anschrift", constraints: 'a')
        textField(id: "auslegungErstellerAnschrift", constraints: 'b')

        zone.insertTemplate('inputRow2');
        label("Plz/Ort", constraints: 'c')
        textField(id: "auslegungErstellerPlz", constraints: 'd')
        textField(id: "auslegungErstellerOrt", constraints: 'e')

        zone.insertTemplate('inputRow');
        label("Telefon", constraints: 'a')
        textField(id: "auslegungErstellerTelefon", constraints: 'b')

        label("Fax", constraints: 'a')
        textField(id: "auslegungErstellerFax", constraints: 'b')

        label("Email", constraints: 'a')
        textField(id: "auslegungErstellerEmail", constraints: 'b')

        zone.insertTemplate('buttonRow');
        button(id: "auslegungErstellerSpeichern", text: "Eingaben speichern", constraints: 'f')

    }
}
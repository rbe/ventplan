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

import eu.artofcoding.griffon.helper.GriffonHelper as GH

// Dierser Dialog wird nun für die Erstellung aller möglichen Dokumente genutzt
// Auslegung input dialog view
panel(id: 'auslegungErstellerPanel', layout: new MigLayout('fillx, wrap', '[]para[fill]para[fill]', ''), constraints: 'grow') {
    // Informationen über den Ersteller
    label('Informationen über den Ersteller des Dokuments', foreground: Color.BLUE, constraints: 'grow, span 3')

    label('Firma')
    textField(id: 'auslegungErstellerFirma', constraints: 'grow, span 2')

    label('Name')
    textField(id: 'auslegungErstellerName', constraints: 'grow, span 2')

    label('Anschrift')
    textField(id: 'auslegungErstellerAnschrift', constraints: 'grow, span 2')

    label('Plz/Ort')
    textField(id: 'auslegungErstellerPlz', constraints: 'width 80px!')
    textField(id: 'auslegungErstellerOrt', constraints: 'width 150px!, grow')

    label('Telefon')
    textField(id: 'auslegungErstellerTelefon', constraints: 'grow, span 2')

    label('Fax')
    textField(id: 'auslegungErstellerFax', constraints: 'grow, span 2')

    label('Email')
    textField(id: 'auslegungErstellerEmail', constraints: 'grow, span 2')
    
    // Informationen über das Angebot
    label(' ', constraints: 'grow, span 3')
    label('Informationen über das Angebot', foreground: Color.BLUE, constraints: 'grow, span 3')

    label('Angebotsnummer')
    textField(id: 'auslegungErstellerAngebotsnummer', constraints: 'grow, span 2')

    label('Empfänger')
    comboBox(id: 'dokumentEmpfanger', items: model.meta.empfanger, constraints: 'grow, span 2')

    // Dokument
    label(' ', constraints: 'grow, span 3')
    label('Einstellungen zum Dokument', foreground: Color.BLUE, constraints: 'grow, span 3')

    label('Typ des Dokuments')
    def a = [
            'PDF - Portable Document Format (unveränderbar)',
            'ODF - OpenDocument Format (veränderbar)'
    ]
    comboBox(id: 'auslegungErstellerDokumenttyp', items: a, constraints: 'grow, span 2')

    // Abbrechen
    label(' ', constraints: 'grow, span 3')
    label('Und nun?', foreground: Color.BLUE, constraints: 'grow, span 3')
    label('')
    button(id: 'nutzerdatenAbbrechenButton', text: 'Abbrechen')
    // Kompletter Text, damit Dimension stimmt, wenn Text nachträglich geändert wird (durch Controller/Action)
    button(id: 'nutzerdatenSpeichernButton', text: 'Eingaben speichern und Dokument erstellen')
}

[
        auslegungErstellerFirma, auslegungErstellerName,
        auslegungErstellerAnschrift, auslegungErstellerPlz, auslegungErstellerOrt,
        auslegungErstellerTelefon, auslegungErstellerFax, auslegungErstellerEmail,
        auslegungErstellerAngebotsnummer
].each {
    GH.yellowTextField(it)
}

// Bindings
nutzerdatenAbbrechenButton.actionPerformed = controller.nutzerdatenAbbrechen
nutzerdatenSpeichernButton.actionPerformed = controller.nutzerdatenSpeichern

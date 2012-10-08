/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 28.07.12 18:04
 */
package com.ventplan.desktop

import com.bensmann.griffon.GriffonHelper as GH

import net.miginfocom.swing.MigLayout

// Dierser Dialog wird nun für die Erstellung aller möglichen Dokumente genutzt
// Auslegung input dialog view
panel(id: 'erstellerPanel', layout: new MigLayout('fillx, wrap', '[]para[fill]para[fill]', ''), constraints: 'grow') {
    // Informationen über den Ersteller
    label('Informationen über den Ersteller des Dokuments (optional)', foreground: Color.BLUE, constraints: 'grow, span 3')

    label('Firma')
    textField(id: 'erstellerFirma', constraints: 'grow, span 2')

    label('Name')
    textField(id: 'erstellerName', constraints: 'grow, span 2')

    label('Anschrift')
    textField(id: 'erstellerAnschrift', constraints: 'grow, span 2')

    label('PLZ Ort')
    textField(id: 'erstellerPlz', constraints: 'width 80px!')
    textField(id: 'erstellerOrt', constraints: 'width 150px!, grow')

    label('Telefon')
    textField(id: 'erstellerTelefon', constraints: 'grow, span 2')

    label('Fax')
    textField(id: 'erstellerFax', constraints: 'grow, span 2')

    label('Email')
    textField(id: 'erstellerEmail', constraints: 'grow, span 2')
    
    // Informationen über das Angebot
    label(' ', constraints: 'grow, span 3')
    label('Inhalte der Auslegung', foreground: Color.BLUE, constraints: 'grow, span 3')

    label(' ')
    checkBox(id: 'auslegungAllgemeineDaten', text: 'Allgemeine Daten (Adressen, allg. Informationen, Räume)', constraints: 'grow, span 2')

    label(' ')
    checkBox(id: 'auslegungLufmengen', text: 'Luftmengen (Raumvolumenströme, Überströmelemente)', constraints: 'grow, span 2')

    label(' ')
    checkBox(id: 'auslegungAkustikberechnung', text: 'Akustikberechnung', constraints: 'grow, span 2')

    label(' ')
    checkBox(id: 'auslegungDruckverlustberechnung', text: 'Druckverlustberechnung', constraints: 'grow, span 2')

    // Dokument
    label(' ', constraints: 'grow, span 3')
    label('Einstellungen zum Dokument', foreground: Color.BLUE, constraints: 'grow, span 3')

    label('Typ des Dokuments')
    def a = [
            'PDF - Portable Document Format (unveränderbar)',
            'ODF - OpenDocument Format (veränderbar)'
    ]
    comboBox(id: 'erstellerDokumenttyp', items: a, constraints: 'grow, span 2')

    // Abbrechen
    label(' ', constraints: 'grow, span 3')
    label('Und nun?', foreground: Color.BLUE, constraints: 'grow, span 3')
    label('')
    button(id: 'nutzerdatenAbbrechenButton', text: 'Abbrechen')
    // Kompletter Text, damit Dimension stimmt, wenn Text nachträglich geändert wird (durch Controller/Action)
    button(id: 'nutzerdatenSpeichernButton', text: 'Eingaben speichern und Dokument erstellen')
}

[
        erstellerFirma, erstellerName,
        erstellerAnschrift, erstellerPlz, erstellerOrt,
        erstellerTelefon, erstellerFax, erstellerEmail
].each {
    GH.yellowTextField(it)
}
build(AuslegungNutzerdatenBindings)

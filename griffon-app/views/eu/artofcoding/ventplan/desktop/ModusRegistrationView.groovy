/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * mmu, 03.04.13 21:26
 */

package eu.artofcoding.ventplan.desktop

import eu.artofcoding.griffon.helper.GriffonHelper as GH

import net.miginfocom.swing.MigLayout
import java.awt.Color

// WAC-272
// Dierser Dialog wird nun für die Registrierung einer Vent-ID genutzt
panel(id: 'ventidRegistrationPanel', layout: new MigLayout('fillx, wrap', '[]para[fill]para[fill]', ''), constraints: 'grow') {
    // Informationen über den Ersteller
    label('Informationen über den Ersteller des Dokuments (optional)', foreground: Color.BLUE, constraints: 'grow, span 3')

    label('')
    comboBox(id: 'ventidRegistrationAnrede', items: ['Frau', 'Herr'], constraints: 'grow, span 2')

    label('Firma')
    textField(id: 'ventidRegistrationFirma', constraints: 'grow, span 2')

    label('Vor- und Zuname')
    textField(id: 'ventidRegistrationName', constraints: 'grow, span 2')

    label('Straße ')
    textField(id: 'ventidRegistrationAnschrift', constraints: 'grow, span 2')

    label('PLZ und Ort')
    textField(id: 'ventidRegistrationPlz', constraints: 'width 80px!')
    textField(id: 'ventidRegistrationOrt', constraints: 'width 150px!, grow')

    label('Telefon')
    textField(id: 'ventidRegistrationTelefon', constraints: 'grow, span 2')

    //label('Fax')
    //textField(id: 'erstellerFax', constraints: 'grow, span 2')

    label('E-Mail')
    textField(id: 'ventidRegistrationEmail', constraints: 'grow, span 2')

    label('Passwort')
    textField(id: 'ventidRegistrationPasswort', constraints: 'grow, span 2')

    label('Passwort')
    textField(id: 'ventidRegistrationPasswort2', constraints: 'grow, span 2')

    checkBox(id: 'ventidRegistrationDialogAGB', text: 'Ja, ich habe die AGBs und allg. Geschäftsbedingungen gelesen!', constraints: 'grow, span 3')
    button(id: 'ventidRegistrationDialogAGBOeffnen', text: 'AGBs öffnen')

    // Abbrechen
    label(' ', constraints: 'grow, span 3')
    label('Und nun?', foreground: Color.BLUE, constraints: 'grow, span 3')
    label(' ')
    button(id: 'ventidRegistrationAbbrechenButton', text: 'Abbrechen')
    // Kompletter Text, damit Dimension stimmt, wenn Text nachträglich geändert wird (durch Controller/Action)
    button(id: 'ventidRegistrationErstellenButton', text: 'Vent-ID erstellen')
}

[
    ventidRegistrationFirma, ventidRegistrationName,
    ventidRegistrationAnschrift, ventidRegistrationPlz, ventidRegistrationOrt,
    ventidRegistrationTelefon, ventidRegistrationEmail, ventidRegistrationPasswort,
    ventidRegistrationPasswort2
].each {
    GH.yellowTextField(it)
}

// Bindings
ventidRegistrationAbbrechenButton.actionPerformed = controller.ventIdRegistrierungAbbrechen
ventidRegistrationErstellenButton.actionPerformed = controller.ventIdRegistrierungSpeichern

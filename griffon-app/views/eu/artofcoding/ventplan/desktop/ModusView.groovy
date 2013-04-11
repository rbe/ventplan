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
panel(id: 'ventidPanel', layout: new MigLayout('fillx, wrap', '[]para[fill]', ''), constraints: 'grow') {
    // Informationen über den Ersteller
    label('Bitte wählen Sie den gewünschten Arbeitsmodus', constraints: 'grow, span 2')

    label(' ', constraints: 'span 2')

    buttonGroup().with {
        add radioButton(id: 'ventidBasic', text: 'Basic / Express', constraints: 'wrap')
        add radioButton(id: 'ventidProfessional', text: 'Professional', constraints: 'wrap')
        add radioButton(id: 'ventidExpert', text: 'Expert (Vent-ID notwendig)', selected: true, constraints: 'wrap')
    }

    label(' ', constraints: 'span 2')

    label(id: 'ventidLogin', text: 'Login', constraints: 'grow, span 2')

    textField(id: 'ventidEmail', constraints: 'width 140px!')
    label('Noch keine Vent-ID?')

    textField(id: 'ventidPasswort', constraints: 'width 140px!')
    button(id: 'ventidRegDialogOeffnenButton', text: 'Jetzt Konto erstellen')

    button(id: 'ventidLoginButton', text: 'Einloggen')
    button(id: 'ventidAbbrechenButton', text: 'Abbrechen')
}

[
    ventidEmail, ventidPasswort
].each {
    GH.yellowTextField(it)
}

// Bindings
ventidRegDialogOeffnenButton.actionPerformed = controller.ventIdRegistrierungDialogOeffnen
ventidAbbrechenButton.actionPerformed = controller.ventIdDialogAbbrechen
ventidLoginButton.actionPerformed = controller.ventIdLogin

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

// WAC-272 Registrierung einer Ventplan ID
panel(id: 'ventidPanel', layout: new MigLayout('fillx, wrap', '[]para[fill]', ''), constraints: 'grow') {
    label('Bitte wählen Sie den gewünschten Arbeitsmodus:', constraints: 'grow, span 2')

    label(' ', constraints: 'span 2')

    buttonGroup().with {
        add radioButton(id: 'ventidBasic', text: 'Community (kostenfrei)', constraints: 'wrap', selected: true)
        add radioButton(id: 'ventidProfessional', text: 'Professional', constraints: 'wrap')
        add radioButton(id: 'ventidExpert', text: 'Enterprise', constraints: 'wrap')
    }

    label(' ', constraints: 'span 2')

    label(id: 'ventidLogin', text: 'Login (Benutzername und Passwort)', constraints: 'grow, span 2')

    textField(id: 'ventidEmail', text: 'E-Mail-Adresse', constraints: 'width 200px!')
    label('Noch keine Ventplan ID?')

    textField(id: 'ventidPasswort', text: 'Passwort', constraints: 'width 200px!')
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

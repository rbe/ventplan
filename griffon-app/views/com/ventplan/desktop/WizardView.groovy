/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/16/12 10:35 AM
 */
package com.ventplan.desktop

import net.miginfocom.swing.MigLayout

// Wizard view
zoneLayout {
    zoneRow('x+*x')
}
//panel(id: 'wizardMainPanel', layout: new MigLayout('fillx, wrap', '[fill]', '[]para[]'), constraints: 'grow') {
panel(constraints: 'x', border: compoundBorder(outer: emptyBorder(5), inner: emptyBorder(5))) {

    zl = zoneLayout {
        //zoneRow('a+*a2b+*b2c+*c2d+*d', template: 'valueRow')
        //zoneRow('e..+*...e2f..+*...f', template: 'valueRow2')
        zoneRow('a+*a2b+*b', template: 'valueRow1')
        zoneRow('c+*c2d+*d', template: 'valueRow2')
        zoneRow('e...-*..e', template: 'valueRow3')
    }

    zl.insertTemplate('valueRow1');
    // Gebäudetyp
    panel(id: 'wizardGebaudeTyp', border: titledBorder(title: 'Gebäudetyp'), constraints: 'a', layout: new MigLayout('ins 0 n 0 n, wrap', '[]', '')) {
        buttonGroup().with {
            add radioButton(id: 'wizardGebaudeTypMFH', text: 'Mehrfamilienhaus MFH')
            add radioButton(id: 'wizardGebaudeTypEFH', text: 'Einfamilienhaus EFH')
            add radioButton(id: 'wizardGebaudeTypMaisonette', text: 'Maisonette')
        }
        //label("<html><p style='font-size: 9px;'>* Nur eine Auswahlmöglichkeit</p></html>", foreground: java.awt.Color.BLUE)
    }
    // Gebäudelage
    panel(id: 'wizardGebaudeLage', border: titledBorder(title: 'Gebäudelage'), constraints: 'b', layout: new MigLayout('ins 0 n 0 n, wrap', '[]', '')) {
        buttonGroup().with {
            add radioButton(id: 'wizardGebaudeLageWindschwach', text: 'windschwach')
            add radioButton(id: 'wizardGebaudeLageWindstark', text: 'windstark')
        }
        //label("<html><p style='font-size: 9px;'>* Nur eine Auswahlmöglichkeit</p></html>", foreground: java.awt.Color.BLUE)
    }
    zl.insertTemplate('valueRow2');
    // Wärmeschutz
    panel(id: 'wizardGbaudewarmeschutz', border: titledBorder(title: 'Wärmeschutz'), constraints: 'c', layout: new MigLayout('ins 0 n 0 n, wrap', '[]', '')) {
        buttonGroup().with {
            add radioButton(id: 'wizardGebaudeWarmeschutzHoch', text: 'hoch (Neubau / Sanierung mind. WSchV 1995)')
            add radioButton(id: 'wizardGebaudeWarmeschutzNiedrig', text: 'niedrig (Gebäude bestand vor 1995)')
        }
        //label("<html><p style='font-size: 9px;'>* Nur eine Auswahlmöglichkeit</p></html>", foreground: java.awt.Color.BLUE)
    }
    // GebäudeGeplanteBelegung
    panel(id: 'wizardGebaudeGeplanteBelegung', border: titledBorder(title: 'Gebäudetyp'), constraints: 'd', layout: new MigLayout('ins 0 n 0 n, fill', '[fill]', '')) {
        label('Personenanzahl')
        textField(id: 'wizardHausPersonenanzahl', constraints: 'grow, wrap')

        label('Außenluftvolumenstrom pro Person')
        textField(id: 'wizardHausAussenluftVsProPerson', constraints: 'grow')
    }

    zl.insertTemplate('valueRow3')
    panel(id: 'wizardRaumTypen', border: titledBorder(title: 'Raumtypen'), constraints: 'e', layout: new MigLayout('ins 0 n 0 n, wrap', '[]', '')) {
        zl = zoneLayout {
            zoneRow('a-*a1b-*b3c-*c1d-*d', template: 'r')
        }
        zl.insertTemplate('r')
        label('Wohnzimmer', constraints: 'a')
        textField(id: 'wizardRaumTypWohnzimmer', constraints: 'b')
        label('WC', constraints: 'c')
        textField(id: 'wizardRaumTypWC', constraints: 'd')

        zl.insertTemplate('r')
        label('Kinderzimmer', constraints: 'a')
        textField(id: 'wizardRaumTypKinderzimmer', constraints: 'b')
        label('Küche', constraints: 'c')
        textField(id: 'wizardRaumTypKuche', constraints: 'd')

        zl.insertTemplate('r')
        label('Schlafzimmer', constraints: 'a')
        textField(id: 'wizardRaumTypSchlafzimmer', constraints: 'b')
        label('Kochnische', constraints: 'c')
        textField(id: 'wizardRaumTypKochnische', constraints: 'd')

        zl.insertTemplate('r')
        label('Esszimmer', constraints: 'a')
        textField(id: 'wizardRaumTypEsszimmer', constraints: 'b')
        label('Bad mit/ohne WC', constraints: 'c')
        textField(id: 'wizardRaumTypBad', constraints: 'd')

        zl.insertTemplate('r')
        label('Arbeitszimmer', constraints: 'a')
        textField(id: 'wizardRaumTypArbeitszimmer', constraints: 'b')
        label('Duschraum', constraints: 'c')
        textField(id: 'wizardRaumTypDuschraum', constraints: 'd')

        zl.insertTemplate('r')
        label('Gästezimmer', constraints: 'a')
        textField(id: 'wizardRaumTypGastezimmer', constraints: 'b')
        label('Sauna', constraints: 'c')
        textField(id: 'wizardRaumTypSauna', constraints: 'd')

        zl.insertTemplate('r')
        label('Hausarbeitsraum', constraints: 'a')
        textField(id: 'wizardRaumTypHausarbeitsraum', constraints: 'b')
        label('Flur', constraints: 'c')
        textField(id: 'wizardRaumTypFlur', constraints: 'd')

        zl.insertTemplate('r')
        label('Kellerraum', constraints: 'a')
        textField(id: 'wizardRaumTypKellerraum', constraints: 'b')
        label('Diele', constraints: 'c')
        textField(id: 'wizardRaumTypDiele', constraints: 'd')
    }

//    panel(id: 'stucklisteBottomButtonPanel', layout: new MigLayout('fill, wrap', '[]para[]', ''), constraints: 'grow') {
//        button(id: 'stucklisteAbbrechen', text: 'Vorgang abbrechen')
//        button(id: 'stucklisteWeiter', text: 'Weiter')
//    }

}

build(WizardBindings)

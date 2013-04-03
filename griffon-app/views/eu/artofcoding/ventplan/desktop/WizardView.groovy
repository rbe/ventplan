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

// WAC-234 Wizard Dialog view
zoneLayout {
    zoneRow('x+*x')
}
panel(constraints: 'x', border: compoundBorder(outer: emptyBorder(5), inner: emptyBorder(5))) {

    zl = zoneLayout {
        zoneRow('a+*a2b+*b', template: 'valueRow1')
        zoneRow('c+*c2d+*d', template: 'valueRow2')
        zoneRow('e...-*..e', template: 'valueRow3')
        zoneRow('f>......f', template: 'valueRow4')
    }

    zl.insertTemplate('valueRow3')
    panel(id: 'wizardExpressProjekt', border: titledBorder(title: 'Express-Projekt erstellen'), constraints: 'e', layout: new MigLayout('ins 0 n 0 n', '[]', '')) {
        label('Projektname')
        textField(id: 'wizardProjektName', constraints: 'width 200px!')
    }
    zl.insertTemplate('valueRow1')
    // Gebäudetyp
    panel(id: 'wizardGebaudeTyp', border: titledBorder(title: 'Gebäudetyp'), constraints: 'a', layout: new MigLayout('ins 0 n 0 n, wrap', '[]', '')) {
        buttonGroup().with {
            add radioButton(id: 'wizardGebaudeTypMFH', text: 'Mehrfamilienhaus MFH')
            add radioButton(id: 'wizardGebaudeTypEFH', text: 'Einfamilienhaus EFH', selected: true)
            add radioButton(id: 'wizardGebaudeTypMaisonette', text: 'Maisonette')
        }
        //label("<html><p style='font-size: 9px;'>* Nur eine Auswahlmöglichkeit</p></html>", foreground: java.awt.Color.BLUE)
    }
    // Gebäudelage
    panel(id: 'wizardGebaudeLage', border: titledBorder(title: 'Gebäudelage'), constraints: 'b', layout: new MigLayout('ins 0 n 0 n, wrap', '[]', '')) {
        buttonGroup().with {
            add radioButton(id: 'wizardGebaudeLageWindschwach', text: 'windschwach', selected: true)
            add radioButton(id: 'wizardGebaudeLageWindstark', text: 'windstark')
        }
        //label("<html><p style='font-size: 9px;'>* Nur eine Auswahlmöglichkeit</p></html>", foreground: java.awt.Color.BLUE)
    }
    zl.insertTemplate('valueRow2')
    // Wärmeschutz
    panel(id: 'wizardGbaudewarmeschutz', border: titledBorder(title: 'Wärmeschutz'), constraints: 'c', layout: new MigLayout('ins 0 n 0 n, wrap', '[]', '')) {
        buttonGroup().with {
            add radioButton(id: 'wizardGebaudeWarmeschutzHoch', text: 'hoch (Neubau / Sanierung mind. WSchV 1995)', selected: true)
            add radioButton(id: 'wizardGebaudeWarmeschutzNiedrig', text: 'niedrig (Gebäude bestand vor 1995)')
        }
        //label("<html><p style='font-size: 9px;'>* Nur eine Auswahlmöglichkeit</p></html>", foreground: java.awt.Color.BLUE)
    }
    // GebäudeGeplanteBelegung
    panel(id: 'wizardGebaudeGeplanteBelegung', border: titledBorder(title: 'Geplante Belegung'), constraints: 'd', layout: new MigLayout('ins 0 n 0 n, fill', '[fill]', '')) {
        label('Personenanzahl')
        textField(id: 'wizardHausPersonenanzahl', constraints: 'width 60px!, wrap', text: '2')

        label('Außenluftvolumenstrom pro Person (m³/h)')
        textField(id: 'wizardHausAussenluftVsProPerson', constraints: 'width 60px!', text: '30')
    }

    zl.insertTemplate('valueRow3')
    panel(id: 'wizardRaumTypen', border: titledBorder(title: 'Anzahl der Raumtypen festlegen'), constraints: 'e', layout: new MigLayout('ins 0 n 0 n, wrap', '[]', '')) {
        zl2 = zoneLayout {
            zoneRow('a-*a1b-*b3c-*c1d-*d', template: 'r')
        }
        zl2.insertTemplate('r')
        label('Wohnzimmer', constraints: 'a')
        textField(id: 'wizardRaumTypWohnzimmer', size: [60,15], constraints: 'b', text: '1')
        label('WC', constraints: 'c')
        textField(id: 'wizardRaumTypWC', size: [60,15], constraints: 'd', text: '1')

        zl2.insertTemplate('r')
        label('Kinderzimmer', constraints: 'a')
        textField(id: 'wizardRaumTypKinderzimmer', size: [60,15], constraints: 'b', text: '2')
        label('Küche', constraints: 'c')
        textField(id: 'wizardRaumTypKuche', size: [60,15], constraints: 'd', text: '1')

        zl2.insertTemplate('r')
        label('Schlafzimmer', constraints: 'a')
        textField(id: 'wizardRaumTypSchlafzimmer', size: [60,15], constraints: 'b', text: '1')
        label('Kochnische', constraints: 'c')
        textField(id: 'wizardRaumTypKochnische', size: [60,15], constraints: 'd')

        zl2.insertTemplate('r')
        label('Esszimmer', constraints: 'a')
        textField(id: 'wizardRaumTypEsszimmer', size: [60,15], constraints: 'b')
        label('Bad mit/ohne WC', constraints: 'c')
        textField(id: 'wizardRaumTypBad', size: [60,15], constraints: 'd', text: '1')

        zl2.insertTemplate('r')
        label('Arbeitszimmer', constraints: 'a')
        textField(id: 'wizardRaumTypArbeitszimmer', size: [60,15], constraints: 'b')
        label('Duschraum', constraints: 'c')
        textField(id: 'wizardRaumTypDuschraum', size: [60,15], constraints: 'd')

        zl2.insertTemplate('r')
        label('Gästezimmer', constraints: 'a')
        textField(id: 'wizardRaumTypGastezimmer', size: [60,15], constraints: 'b')
        label('Sauna', constraints: 'c')
        textField(id: 'wizardRaumTypSauna', size: [60,15], constraints: 'd')

        zl2.insertTemplate('r')
        label('Hausarbeitsraum', constraints: 'a')
        textField(id: 'wizardRaumTypHausarbeitsraum', size: [60,15], constraints: 'b')
        label('Flur', constraints: 'c')
        textField(id: 'wizardRaumTypFlur', size: [60,15], constraints: 'd', text: '1')

        zl2.insertTemplate('r')
        label('Kellerraum', constraints: 'a')
        textField(id: 'wizardRaumTypKellerraum', size: [60,15], constraints: 'b')
        label('Diele', constraints: 'c')
        textField(id: 'wizardRaumTypDiele', size: [60,15], constraints: 'd')
    }

    zl.insertTemplate('valueRow4')
    panel(id: 'wizardBottomButtonPanel', constraints: 'f', layout: new MigLayout('ins 0 n 0 n, wrap', '[]para[]', '')) {
        button(id: 'wizardAbbrechen', text: 'Vorgang abbrechen')
        button(id: 'wizardProjektErstellen', text: 'Neues Projekt erstellen')
    }

}

build(WizardBindings)

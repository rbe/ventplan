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

import com.bensmann.griffon.GriffonHelper as GH

// WAC-234 Wizard Dialog


wizardAbbrechen.actionPerformed = controller.wizardAbbrechen
wizardProjektErstellen.actionPerformed = controller.wizardProjektErstellen

// Gebäudetyp
bind(source: model.wizardmap.gebaude.typ, sourceProperty: 'mfh',        target: wizardGebaudeTypMFH,        targetProperty: 'selected', mutual: true)
bind(source: model.wizardmap.gebaude.typ, sourceProperty: 'efh',        target: wizardGebaudeTypEFH,        targetProperty: 'selected', mutual: true)
bind(source: model.wizardmap.gebaude.typ, sourceProperty: 'maisonette', target: wizardGebaudeTypMaisonette, targetProperty: 'selected', mutual: true)
//[gebaudeTypMFH, gebaudeTypEFH, gebaudeTypMaisonette].each {
//    GH.onChange(it, null, controller.gebaudedatenGeandert)
//}
// Wärmeschutz
bind(source: model.wizardmap.gebaude.warmeschutz, sourceProperty: 'hoch',    target: wizardGebaudeWarmeschutzHoch,    targetProperty: 'selected', mutual: true)
bind(source: model.wizardmap.gebaude.warmeschutz, sourceProperty: 'niedrig', target: wizardGebaudeWarmeschutzNiedrig, targetProperty: 'selected', mutual: true)
// Gebäudelage
bind(source: model.wizardmap.gebaude.lage, sourceProperty: 'windschwach', target: wizardGebaudeLageWindschwach, targetProperty: 'selected', mutual: true)
bind(source: model.wizardmap.gebaude.lage, sourceProperty: 'windstark',   target: wizardGebaudeLageWindstark,   targetProperty: 'selected', mutual: true)
//[gebaudeLageWindschwach, gebaudeLageWindstark].each {
//    GH.onChange(it, null, controller.gebaudedatenGeandert)
//}
// Geplante Belegung
bind(source: model.wizardmap.gebaude.geplanteBelegung, sourceProperty: 'personenanzahl',        target: wizardHausPersonenanzahl,        targetProperty: 'value')
bind(source: model.wizardmap.gebaude.geplanteBelegung, sourceProperty: 'aussenluftVsProPerson', target: wizardHausAussenluftVsProPerson, targetProperty: 'value')
//bind(source: model.wizardmap.gebaude.geplanteBelegung, sourceProperty: 'mindestaussenluftrate', target: gebaudeGeplanteMindestaussenluftrate, targetProperty: 'text',  converter: GH.toString2Converter)
//[gebaudeGeplantePersonenanzahl, gebaudeGeplanteAussenluftVsProPerson].each {
//	it.stateChanged = controller.berechneMindestaussenluftrate
//    GH.installKeyAdapter(it.editor.textField, GH.NUMBER_KEY_CODES, { evt ->
//            controller.berechneMindestaussenluftrate()
//        })
//}

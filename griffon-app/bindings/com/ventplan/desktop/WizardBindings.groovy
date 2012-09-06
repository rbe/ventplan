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

//// Gebäudedaten - Gebäudetyp
//bind(source: model.map.gebaude.typ, sourceProperty: "mfh",        target: gebaudeTypMFH,        targetProperty: "selected", mutual: true)
//bind(source: model.map.gebaude.typ, sourceProperty: "efh",        target: gebaudeTypEFH,        targetProperty: "selected", mutual: true)
//bind(source: model.map.gebaude.typ, sourceProperty: "maisonette", target: gebaudeTypMaisonette, targetProperty: "selected", mutual: true)
//[gebaudeTypMFH, gebaudeTypEFH, gebaudeTypMaisonette].each {
//    GH.onChange(it, null, controller.gebaudedatenGeandert)
//}
//// Gebäudedaten - Gebäudelage
//bind(source: model.map.gebaude.lage, sourceProperty: "windschwach", target: gebaudeLageWindschwach, targetProperty: "selected", mutual: true)
//bind(source: model.map.gebaude.lage, sourceProperty: "windstark",   target: gebaudeLageWindstark,   targetProperty: "selected", mutual: true)
//[gebaudeLageWindschwach, gebaudeLageWindstark].each {
//    GH.onChange(it, null, controller.gebaudedatenGeandert)
//}
//bind(source: model.map.gebaude.geplanteBelegung, sourceProperty: "personenanzahl",        target: gebaudeGeplantePersonenanzahl,        targetProperty: "value")
//bind(source: model.map.gebaude.geplanteBelegung, sourceProperty: "aussenluftVsProPerson", target: gebaudeGeplanteAussenluftVsProPerson, targetProperty: "value")
//bind(source: model.map.gebaude.geplanteBelegung, sourceProperty: "mindestaussenluftrate", target: gebaudeGeplanteMindestaussenluftrate, targetProperty: "text",  converter: GH.toString2Converter)
//[gebaudeGeplantePersonenanzahl, gebaudeGeplanteAussenluftVsProPerson].each {
//	it.stateChanged = controller.berechneMindestaussenluftrate
//    GH.installKeyAdapter(it.editor.textField, GH.NUMBER_KEY_CODES, { evt ->
//            controller.berechneMindestaussenluftrate()
//        })
//}

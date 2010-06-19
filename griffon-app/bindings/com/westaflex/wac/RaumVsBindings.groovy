/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/bindings/com/westaflex/wac/RaumVsBindings.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

// Raumvolumenströme
// Binding for items of comboboxes is done in RaumVsiew!
bind(source: model.map.raum.raumVs, sourceProperty: "gesamtVolumenNE",                     target: "raumVsGesamtVolumenNE",                   targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.raum.raumVs, sourceProperty: "luftwechselNE",                       target: "raumVsLuftwechselNE",                     targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.raum.raumVs, sourceProperty: "gesamtaussenluftVsMitInfiltration",   target: "raumVsGesamtaussenluftVsMitInfiltration", targetProperty: "text", converter: GH.toString2Converter)
// Aussenluftvolumenstrom der lüftungstechnsichen Maßnahme
//bind(source: model.map.raum.raumVs, sourceProperty: "", target: "raumVsAussenluftVsDerLtmFs", targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.raum.aussenluftVs, sourceProperty: "gesamtLvsLtmLvsRl", target: "raumVsAussenluftVsDerLtmRl", targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.raum.aussenluftVs, sourceProperty: "gesamtLvsLtmLvsNl", target: "raumVsAussenluftVsDerLtmNl", targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.raum.aussenluftVs, sourceProperty: "gesamtLvsLtmLvsIl", target: "raumVsAussenluftVsDerLtmIl", targetProperty: "text", converter: GH.toString2Converter)
//
raumVsZentralgerat.actionPerformed = controller.zentralgeratGewahlt
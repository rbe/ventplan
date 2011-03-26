/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/bindings/com/westaflex/wac/RaumBearbeitenBindings.groovy
 * Last modified at 22.03.2011 13:07:54 by rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

// Raum bearbeiten
// Raum
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumTyp",         target: raumBearbeitenRaumtyp,      targetProperty: "selectedItem")
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumBezeichnung", target: raumBearbeitenBezeichnung,  targetProperty: "text")
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumGeschoss",    target: raumBearbeitenRaumGeschoss, targetProperty: "selectedItem")
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumNummer",      target: raumBearbeitenRaumnummer,   targetProperty: "text")
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumLuftart",     target: raumBearbeitenLuftart,      targetProperty: "text")
// Luftart
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumZuluftfaktor",       target: raumBearbeitenLuftartFaktorZuluftverteilung, targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumAbluftVolumenstrom", target: raumBearbeitenLuftartAbluftVs,               targetProperty: "text", converter: GH.toString2Converter)
/*
[raumBearbeitenRaumtyp, raumBearbeitenBezeichnung, raumBearbeitenRaumGeschoss, raumBearbeitenLuftartFaktorZuluftverteilung, raumBearbeitenLuftartAbluftVs].each {
    GH.onChange(component: it, closure: controller.raumBearbeitenGeandert)
}
*/
// Türen
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumMaxTurspaltHohe", target: raumBearbeitenDetailsTurspalthohe, targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumTurspaltHinweis", target: raumBearbeitenTurspaltHinweis,     targetProperty: "text")
// Optional
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumLange",   target: raumBearbeitenOptionalRaumlange,   targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumBreite",  target: raumBearbeitenOptionalRaumbreite,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumHohe",    target: raumBearbeitenOptionalRaumhohe,    targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumFlache",  target: raumBearbeitenOptionalRaumflache,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumVolumen", target: raumBearbeitenOptionalRaumvolumen, targetProperty: "text", converter: GH.toString2Converter)
/*
[raumBearbeitenOptionalRaumlange, raumBearbeitenOptionalRaumbreite, raumBearbeitenOptionalRaumhohe].each {
    GH.onChange(component: it, closure: controller.raumBearbeitenGeandert)
}
*/
// onChange
GH.recurse(raumBearbeitenTabPanel, { component ->
    GH.onChange(component, null, controller.raumBearbeitenGeandert)
})
// Schliessen
raumBearbeitenSchliessen.actionPerformed = controller.raumBearbeitenSchliessen
// Tur entfernen / Werte zuruecksetzen
raumBearbeitenDetailsTurentfernen.actionPerformed = controller.raumBearbeitenTurEntfernen

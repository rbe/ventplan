/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/bindings/com/westaflex/wac/RaumBearbeitenBindings.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

// Raum bearbeiten
// Raum
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumTyp",    target: raumBearbeitenRaumtyp, targetProperty: "selectedItem", mutual: true)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumBezeichnung", target: raumBearbeitenBezeichnung,  targetProperty: "text",         mutual: true)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumGeschoss",    target: raumBearbeitenRaumGeschoss, targetProperty: "selectedItem", mutual: true)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumNummer",      target: raumBearbeitenRaumnummer,   targetProperty: "text",         mutual: true)
// Luftart
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumZuluftfaktor", target: raumBearbeitenLuftartFaktorZuluftverteilung, targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumAbluftVs",     target: raumBearbeitenLuftartAbluftVs,               targetProperty: "text", converter: GH.toString2Converter)
[raumBearbeitenLuftartFaktorZuluftverteilung, raumBearbeitenLuftartAbluftVs].each {
	it.focusLost = controller.raumBearbeitenLuftartGeandert
}
// Durchlassposition
// Kanalanschluß
// Türen
// Optional
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumLange",   target: raumBearbeitenOptionalRaumlange,   targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumBreite",  target: raumBearbeitenOptionalRaumbreite,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumHohe",    target: raumBearbeitenOptionalRaumhohe,    targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumFlache",  target: raumBearbeitenOptionalRaumflache,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumVolumen", target: raumBearbeitenOptionalRaumvolumen, targetProperty: "text", converter: GH.toString2Converter)
[raumBearbeitenOptionalRaumlange, raumBearbeitenOptionalRaumbreite].each {
	it.focusLost = controller.raumBearbeitenGeometrieGeandert
}

raumBearbeitenSchliessen.actionPerformed = controller.raumBearbeitenSchliessen


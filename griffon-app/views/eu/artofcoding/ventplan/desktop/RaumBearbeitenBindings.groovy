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

import eu.artofcoding.griffon.helper.GriffonHelper as GH

// Raum bearbeiten
// Raum
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumTyp",         target: raumBearbeitenRaumtyp,      targetProperty: "selectedItem")
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumBezeichnung", target: raumBearbeitenBezeichnung,  targetProperty: "text")
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumGeschoss",    target: raumBearbeitenRaumGeschoss, targetProperty: "selectedItem")
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumNummer",      target: raumBearbeitenRaumnummer,   targetProperty: "text")
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumLuftart",     target: raumBearbeitenLuftart,      targetProperty: "selectedItem")
[raumBearbeitenRaumtyp, raumBearbeitenBezeichnung, raumBearbeitenRaumGeschoss, raumBearbeitenRaumnummer, raumBearbeitenLuftart].each {
    GH.onChange(it, null, controller.raumBearbeitenGeandert)
}
// Luftart
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumZuluftfaktor",       target: raumBearbeitenLuftartFaktorZuluftverteilung, targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumAbluftVolumenstrom", target: raumBearbeitenLuftartAbluftVs,               targetProperty: "text", converter: GH.toString2Converter)
// Beim Verlassen des Feldes neu berechnen
[raumBearbeitenLuftartFaktorZuluftverteilung, raumBearbeitenLuftartAbluftVs].each {
    GH.onFocusLost(it, null, controller.raumBearbeitenGeandert)
}
// Türen
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumMaxTurspaltHohe", target: raumBearbeitenDetailsTurspalthohe, targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumTurspaltHinweis", target: raumBearbeitenTurspaltHinweis,     targetProperty: "text")
[raumBearbeitenDetailsTurspalthohe].each {
    GH.onChange(it, null, controller.raumBearbeitenGeandert)
}
// Optional
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumLange",   target: raumBearbeitenOptionalRaumlange,   targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumBreite",  target: raumBearbeitenOptionalRaumbreite,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumHohe",    target: raumBearbeitenOptionalRaumhohe,    targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumFlache",  target: raumBearbeitenOptionalRaumflache,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.meta.gewahlterRaum, sourceProperty: "raumVolumen", target: raumBearbeitenOptionalRaumvolumen, targetProperty: "text", converter: GH.toString2Converter)
[raumBearbeitenOptionalRaumlange, raumBearbeitenOptionalRaumbreite, raumBearbeitenOptionalRaumhohe].each {
    GH.onChange(it, null, controller.raumBearbeitenGeandert)
}
// Schliessen
raumBearbeitenSchliessen.actionPerformed = controller.raumBearbeitenSchliessen
// Tur entfernen / Werte zuruecksetzen
raumBearbeitenDetailsTurentfernen.actionPerformed = controller.raumBearbeitenTurEntfernen

package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

// Gebäudedaten - Gebäudetyp
bind(source: model.map.gebaude.typ, sourceProperty: "MFH",        target: gebaudeTypMFH,        targetProperty: "selected", mutual: true)
bind(source: model.map.gebaude.typ, sourceProperty: "EFH",        target: gebaudeTypEFH,        targetProperty: "selected", mutual: true)
bind(source: model.map.gebaude.typ, sourceProperty: "Maisonette", target: gebaudeTypMaisonette, targetProperty: "selected", mutual: true)
// Gebäudedaten - Gebäudelage
bind(source: model.map.gebaude.lage, sourceProperty: "windschwach", target: gebaudeLageWindschwach, targetProperty: "selected", mutual: true)
bind(source: model.map.gebaude.lage, sourceProperty: "windstark",   target: gebaudeLageWindstark,   targetProperty: "selected", mutual: true)
// Gebäudedaten - Wärmeschutz
bind(source: model.map.gebaude.warmeschutz, sourceProperty: "hoch",    target: gebaudeWarmeschutzHoch,    targetProperty: "selected", mutual: true)
bind(source: model.map.gebaude.warmeschutz, sourceProperty: "niedrig", target: gebaudeWarmeschutzNiedrig, targetProperty: "selected", mutual: true)
// Gebäudedaten - Geometrie
bind(source: model.map.gebaude.geometrie, sourceProperty: "wohnflache",        target: gebaudeGeometrieWohnflache,        targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.gebaude.geometrie, sourceProperty: "raumhohe",          target: gebaudeGeometrieMittlereRaumhohe,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.gebaude.geometrie, sourceProperty: "luftvolumen",       target: gebaudeGeometrieLuftvolumen,       targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.gebaude.geometrie, sourceProperty: "gelufteteFlache",   target: gebaudeGeometrieGelufteteFlache,   targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.gebaude.geometrie, sourceProperty: "geluftetesVolumen", target: gebaudeGeometrieGeluftetesVolumen, targetProperty: "text", converter: GH.toString2Converter)
[gebaudeGeometrieWohnflache, gebaudeGeometrieMittlereRaumhohe, gebaudeGeometrieGelufteteFlache].each {
	it.focusLost = controller.berechneGeometrie
}
// Luftdichtheit der Gebäudehülle
bind(source: model.map.gebaude.luftdichtheit, sourceProperty: "kategorieA", target: gebaudeLuftdichtheitKategorieA, targetProperty: "selected", mutual: true)
bind(source: model.map.gebaude.luftdichtheit, sourceProperty: "kategorieB", target: gebaudeLuftdichtheitKategorieB, targetProperty: "selected", mutual: true)
bind(source: model.map.gebaude.luftdichtheit, sourceProperty: "kategorieC", target: gebaudeLuftdichtheitKategorieC, targetProperty: "selected", mutual: true)
bind(source: model.map.gebaude.luftdichtheit, sourceProperty: "messwerte",  target: gebaudeLuftdichtheitMesswerte,  targetProperty: "selected", mutual: true)
// Luftdichtheit der Gebäudehülle - Messwerte
bind(source: gebaudeLuftdichtheitMesswerte, sourceProperty: "selected", target: gebaudeLuftdichtheitDruckdifferenz, targetProperty: "enabled")
bind(source: gebaudeLuftdichtheitMesswerte, sourceProperty: "selected", target: gebaudeLuftdichtheitLuftwechsel,    targetProperty: "enabled")
bind(source: gebaudeLuftdichtheitMesswerte, sourceProperty: "selected", target: gebaudeLuftdichtheitDruckexponent,  targetProperty: "enabled")
bind(source: model.map.gebaude.luftdichtheit, sourceProperty: "druckdifferenz", target: gebaudeLuftdichtheitDruckdifferenz, targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.gebaude.luftdichtheit, sourceProperty: "luftwechsel",    target: gebaudeLuftdichtheitLuftwechsel,    targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.gebaude.luftdichtheit, sourceProperty: "druckexponent",  target: gebaudeLuftdichtheitDruckexponent,  targetProperty: "text", converter: GH.toString3Converter)
[gebaudeLuftdichtheitDruckdifferenz, gebaudeLuftdichtheitLuftwechsel, gebaudeLuftdichtheitDruckexponent].each {
	it.focusLost = controller.speichereLuftdichtheit
}
// Luftdichtheit der Gebäudehülle - Kategorien
gebaudeLuftdichtheitKategorieA.actionPerformed = controller.luftdichtheitKategorieA
gebaudeLuftdichtheitKategorieB.actionPerformed = controller.luftdichtheitKategorieB
gebaudeLuftdichtheitKategorieC.actionPerformed = controller.luftdichtheitKategorieC
// Besondere Anforderungen
bind(source: model.map.gebaude, sourceProperty: "faktorBesondereAnforderungen", target: faktorBesondereAnforderungen, targetProperty: "text", converter: GH.toString2Converter)
faktorBesondereAnforderungen.focusLost = controller.speichereFaktorBesondereAnforderungen
// Geplante Belegung
bind(source: model.map.gebaude.geplanteBelegung, sourceProperty: "personenanzahl",        target: gebaudeGeplantePersonenanzahl,        targetProperty: "value", mutual: true)
bind(source: model.map.gebaude.geplanteBelegung, sourceProperty: "aussenluftVsProPerson", target: gebaudeGeplanteAussenluftVsProPerson, targetProperty: "value", mutual: true)
bind(source: model.map.gebaude.geplanteBelegung, sourceProperty: "mindestaussenluftrate", target: gebaudeGeplanteMindestaussenluftrate, targetProperty: "text",  converter: GH.toString2Converter)
[gebaudeGeplantePersonenanzahl, gebaudeGeplanteAussenluftVsProPerson].each {
	it.stateChanged = controller.berechneMindestaussenluftrate
}

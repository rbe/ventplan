package com.westaflex.wac

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
bind(source: model.map.gebaude.geometrie, sourceProperty: "wohnflaeche",       target: gebaudeGeometrieWohnflache,        targetProperty: "text", mutual: true)
bind(source: model.map.gebaude.geometrie, sourceProperty: "raumhoehe",         target: gebaudeGeometrieMittlereRaumhohe,  targetProperty: "text", mutual: true)
bind(source: model.map.gebaude.geometrie, sourceProperty: "luftvolumen",       target: gebaudeGeometrieLuftvolumen,       targetProperty: "text", mutual: true)
bind(source: model.map.gebaude.geometrie, sourceProperty: "gelufteteFlaeche",  target: gebaudeGeometrieGelufteteFlache,   targetProperty: "text", mutual: true)
bind(source: model.map.gebaude.geometrie, sourceProperty: "geluftetesVolumen", target: gebaudeGeometrieGeluftetesVolumen, targetProperty: "text", mutual: true)
// Luftdichtheit der Gebäudehülle
bind(source: model.map.gebaude.luftdichtheit, sourceProperty: "kategorieA", target: gebaudeLuftdichtheitKategorieA, targetProperty: "selected", mutual: true)
bind(source: model.map.gebaude.luftdichtheit, sourceProperty: "kategorieB", target: gebaudeLuftdichtheitKategorieB, targetProperty: "selected", mutual: true)
bind(source: model.map.gebaude.luftdichtheit, sourceProperty: "kategorieC", target: gebaudeLuftdichtheitKategorieC, targetProperty: "selected", mutual: true)
bind(source: model.map.gebaude.luftdichtheit, sourceProperty: "messwerte",  target: gebaudeLuftdichtheitMesswerte,  targetProperty: "selected", mutual: true)
//
bind(source: gebaudeLuftdichtheitMesswerte, sourceProperty: "selected", target: gebaudeLuftdichtheitDruckdifferenz, targetProperty: "enabled")
bind(source: gebaudeLuftdichtheitMesswerte, sourceProperty: "selected", target: gebaudeLuftdichtheitLuftwechsel,    targetProperty: "enabled")
bind(source: gebaudeLuftdichtheitMesswerte, sourceProperty: "selected", target: gebaudeLuftdichtheitDruckexponent,  targetProperty: "enabled")
//
bind(source: model.map.gebaude.luftdichtheit, sourceProperty: "druckdifferenz", target: gebaudeLuftdichtheitDruckdifferenz, targetProperty: "text", mutual: true)
bind(source: model.map.gebaude.luftdichtheit, sourceProperty: "luftwechsel",    target: gebaudeLuftdichtheitLuftwechsel,    targetProperty: "text", mutual: true)
bind(source: model.map.gebaude.luftdichtheit, sourceProperty: "druckexponent",  target: gebaudeLuftdichtheitDruckexponent,  targetProperty: "text", mutual: true)
// Kategorie A
gebaudeLuftdichtheitKategorieA.addActionListener({ evt ->
	view.gebaudeLuftdichtheitDruckdifferenz.text = "2,00"
	view.gebaudeLuftdichtheitLuftwechsel.text = "1,00"
	view.gebaudeLuftdichtheitDruckexponent.text = "0,666"
} as java.awt.event.ActionListener)
// Kategorie B
gebaudeLuftdichtheitKategorieB.addActionListener({ evt ->
	gebaudeLuftdichtheitDruckdifferenz.text = "2,00"
	gebaudeLuftdichtheitLuftwechsel.text = "1,50"
	gebaudeLuftdichtheitDruckexponent.text = "0,666"
} as java.awt.event.ActionListener)
// Kategorie C
gebaudeLuftdichtheitKategorieC.addActionListener({ evt ->
	gebaudeLuftdichtheitDruckdifferenz.text = "2,00"
	gebaudeLuftdichtheitLuftwechsel.text = "2,00"
	gebaudeLuftdichtheitDruckexponent.text = "0,666"
} as java.awt.event.ActionListener)

// Besondere Anforderungen
bind(source: model.map.gebaude, sourceProperty: "faktorBesondereAnforderungen", target: faktorBesondereAnforderungen, targetProperty: "text", mutual: true)
// Geplante Belegung

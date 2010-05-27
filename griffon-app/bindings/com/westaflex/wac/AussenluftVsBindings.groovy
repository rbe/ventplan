package com.westaflex.wac

// Notwendigkeit der lüftungstechnischen Maßnahme
bind(source: model.map.aussenluftVs, sourceProperty: "gesamt",       target: aussenluftVsGesamt,       targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "infiltration", target: aussenluftVsInfiltration, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "massnahme",    target: aussenluftVsMassnahme,    targetProperty: "text")
// Zeilen: Lvs = Luftvolumenstrom, Lw = Luftwechsel
// Spalten: Fs = Feutcheschutz, Rl = reduzierte Lüftung, Nl = Nennlüftung, Il = Intensivlüftung
// Gesamt-Außenluftvolumenströme für Nutzungseinheit
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLvsFs", target: gesamtAvsNeLvsFs, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLvsRl", target: gesamtAvsNeLvsRl, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLvsNl", target: gesamtAvsNeLvsNl, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLvsIl", target: gesamtAvsNeLvsIl, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLwFs",  target: gesamtAvsNeLwFs,  targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLwRl",  target: gesamtAvsNeLwRl,  targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLwNl",  target: gesamtAvsNeLwNl,  targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLwIl",  target: gesamtAvsNeLwIl,  targetProperty: "text")
// Gesamt-Abluftvolumenströme der Räume
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLvsFs", target: gesamtAvsRaumLvsFs, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLvsRl", target: gesamtAvsRaumLvsRl, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLvsNl", target: gesamtAvsRaumLvsNl, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLvsIl", target: gesamtAvsRaumLvsIl, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLwFs",  target: gesamtAvsRaumLwFs,  targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLwRl",  target: gesamtAvsRaumLwRl,  targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLwNl",  target: gesamtAvsRaumLwNl,  targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLwIl",  target: gesamtAvsRaumLwIl,  targetProperty: "text")
// Personenbezogene Gesamt-Außenluftvolumenströme
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLvsFs", target: gesamtAvsPersonLvsFs, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLvsRl", target: gesamtAvsPersonLvsRl, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLvsNl", target: gesamtAvsPersonLvsNl, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLvsIl", target: gesamtAvsPersonLvsIl, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLwFs",  target: gesamtAvsPersonLwFs,  targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLwRl",  target: gesamtAvsPersonLwRl,  targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLwNl",  target: gesamtAvsPersonLwNl,  targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLwIl",  target: gesamtAvsPersonLwIl,  targetProperty: "text")
// Gesamt-Luftvolumenstrom für lüftungstechnische Maßnahmen
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLvsRl", target: gesamtLvsLtmLvsRl, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLvsNl", target: gesamtLvsLtmLvsNl, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLvsIl", target: gesamtLvsLtmLvsIl, targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLwRl",  target: gesamtLvsLtmLwRl,  targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLwNl",  target: gesamtLvsLtmLwNl,  targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLwIl",  target: gesamtLvsLtmLwIl,  targetProperty: "text")

// Mit Infiltrationsanteil berechnen
bind(source: model.map.aussenluftVs, sourceProperty: "infiltrationBerechnen", target: aussenluftVsGesamtLtmInfiltrationsanteil, targetProperty: "selected", mutual: true)
aussenluftVsGesamtLtmInfiltrationsanteil.actionPerformed = controller.berechneAussenluftVs

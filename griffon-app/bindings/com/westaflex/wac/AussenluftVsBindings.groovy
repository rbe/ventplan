package com.westaflex.wac

//
bind(source: model.map.aussenluftVs, sourceProperty: "gesamt",       target: aussenluftVsGesamt,       targetProperty: "text")
bind(source: model.map.aussenluftVs, sourceProperty: "infiltration", target: aussenluftVsInfiltration, targetProperty: "text")
// Label "Lüftungstechnische Maßnahmen erforderlich"
bind(source: model.map.aussenluftVs, sourceProperty: "massnahme", target: aussenluftVsMassnahme, targetProperty: "text")
//
aussenluftVsGesamtLMMitInfiltrationsanteilBerechnen.actionPerformed = controller.berechneAussenluftVs

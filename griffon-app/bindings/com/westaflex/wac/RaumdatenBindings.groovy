package com.westaflex.wac

// Raumdaten - Raum-Eingabe
// Binding of comboboxes is done in RaumdatenView!
bind(source: model.map.raum, sourceProperty: "hohe",         target: raumHohe,         targetProperty: "text")
bind(source: model.map.raum, sourceProperty: "zuluftfaktor", target: raumZuluftfaktor, targetProperty: "text")
raumHinzufugen.actionPerformed = controller.raumHinzufugen

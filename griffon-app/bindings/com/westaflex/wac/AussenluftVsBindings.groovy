/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/bindings/com/westaflex/wac/AussenluftVsBindings.groovy
 * Last modified at 23.03.2011 13:16:37 by rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

// Notwendigkeit der lüftungstechnischen Maßnahme
bind(source: model.map.aussenluftVs, sourceProperty: "gesamt",       target: aussenluftVsGesamt,       targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "infiltration", target: aussenluftVsInfiltration, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "massnahme",    target: aussenluftVsMassnahme,    targetProperty: "text")
// Zeilen: Lvs = Luftvolumenstrom, Lw = Luftwechsel
// Spalten: Fs = Feutcheschutz, Rl = reduzierte Lüftung, Nl = Nennlüftung, Il = Intensivlüftung
// Gesamt-Außenluftvolumenströme für Nutzungseinheit
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLvsFs", target: gesamtAvsNeLvsFs, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLvsRl", target: gesamtAvsNeLvsRl, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLvsNl", target: gesamtAvsNeLvsNl, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLvsIl", target: gesamtAvsNeLvsIl, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLwFs",  target: gesamtAvsNeLwFs,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLwRl",  target: gesamtAvsNeLwRl,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLwNl",  target: gesamtAvsNeLwNl,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsNeLwIl",  target: gesamtAvsNeLwIl,  targetProperty: "text", converter: GH.toString2Converter)
// Gesamt-Abluftvolumenströme der Räume
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLvsFs", target: gesamtAvsRaumLvsFs, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLvsRl", target: gesamtAvsRaumLvsRl, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLvsNl", target: gesamtAvsRaumLvsNl, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLvsIl", target: gesamtAvsRaumLvsIl, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLwFs",  target: gesamtAvsRaumLwFs,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLwRl",  target: gesamtAvsRaumLwRl,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLwNl",  target: gesamtAvsRaumLwNl,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsRaumLwIl",  target: gesamtAvsRaumLwIl,  targetProperty: "text", converter: GH.toString2Converter)
// Personenbezogene Gesamt-Außenluftvolumenströme
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLvsFs", target: gesamtAvsPersonLvsFs, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLvsRl", target: gesamtAvsPersonLvsRl, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLvsNl", target: gesamtAvsPersonLvsNl, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLvsIl", target: gesamtAvsPersonLvsIl, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLwFs",  target: gesamtAvsPersonLwFs,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLwRl",  target: gesamtAvsPersonLwRl,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLwNl",  target: gesamtAvsPersonLwNl,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtAvsPersonLwIl",  target: gesamtAvsPersonLwIl,  targetProperty: "text", converter: GH.toString2Converter)
// Gesamt-Luftvolumenstrom für lüftungstechnische Maßnahmen
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLvsFs", target: gesamtLvsLtmLvsFs, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLvsRl", target: gesamtLvsLtmLvsRl, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLvsNl", target: gesamtLvsLtmLvsNl, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLvsIl", target: gesamtLvsLtmLvsIl, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLwFs",  target: gesamtLvsLtmLwFs,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLwRl",  target: gesamtLvsLtmLwRl,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLwNl",  target: gesamtLvsLtmLwNl,  targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLwIl",  target: gesamtLvsLtmLwIl,  targetProperty: "text", converter: GH.toString2Converter)
// Mit Infiltrationsanteil berechnen
bind(source: model.map.aussenluftVs, sourceProperty: "infiltrationBerechnen", target: aussenluftVsGesamtLtmInfiltrationsanteil, targetProperty: "selected", mutual: true)
aussenluftVsGesamtLtmInfiltrationsanteil.actionPerformed = controller.berechneAussenluftVs

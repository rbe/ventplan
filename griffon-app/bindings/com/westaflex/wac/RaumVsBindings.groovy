/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

// Raumvolumenströme
// WAC-171: Hinweis für Türen und ÜB-Elemente
bind(source: model.map.raum.raumVs, sourceProperty: "turenHinweis",      target: raumVsTurenHinweis,      targetProperty: "text")
bind(source: model.map.raum.raumVs, sourceProperty: "ubElementeHinweis", target: raumVsUbElementeHinweis, targetProperty: "text")
// Add list selection listener to synchronize every table's selection and model.meta.gewahlterRaum
[raumVsZuAbluftventileTabelle, raumVsUberstromelementeTabelle].each {
	it.selectionModel.addListSelectionListener([
		valueChanged: { evt ->
				controller.raumInTabelleGewahlt(evt, it)
			}
		] as javax.swing.event.ListSelectionListener)
}
// Comboboxes
// Binding for items of comboboxes is done in RaumVsiew!
raumVsZentralgerat.actionPerformed = controller.zentralgeratManuellGewahlt
raumVsVolumenstrom.actionPerformed = controller.volumenstromZentralgeratManuellGewahlt
//
bind(source: model.map.raum.raumVs, sourceProperty: "gesamtVolumenNE",                   target: raumVsGesamtVolumenNE,                   targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.raum.raumVs, sourceProperty: "luftwechselNE",                     target: raumVsLuftwechselNE,                     targetProperty: "text", converter: GH.toString2Converter)
bind(source: model.map.raum.raumVs, sourceProperty: "gesamtaussenluftVsMitInfiltration", target: raumVsGesamtaussenluftVsMitInfiltration, targetProperty: "text", converter: GH.toString2Round5Converter)
// Aussenluftvolumenstrom der lüftungstechnsichen Maßnahme
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLvsFs", target: raumVsAussenluftVsDerLtmFs, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLvsRl", target: raumVsAussenluftVsDerLtmRl, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLvsNl", target: raumVsAussenluftVsDerLtmNl, targetProperty: "text", converter: GH.toString2Round5Converter)
bind(source: model.map.aussenluftVs, sourceProperty: "gesamtLvsLtmLvsIl", target: raumVsAussenluftVsDerLtmIl, targetProperty: "text", converter: GH.toString2Round5Converter)
//
raumVsRaumBearbeiten.actionPerformed = controller.raumBearbeiten

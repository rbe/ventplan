/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/16/12 10:35 AM
 */
package com.ventplan.desktop

// Anlagendaten - Gerätestandort
bind(source: model.map.anlage.standort, sourceProperty: 'KG', target: anlageGeratestandortKG, targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.standort, sourceProperty: 'EG', target: anlageGeratestandortEG, targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.standort, sourceProperty: 'OG', target: anlageGeratestandortOG, targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.standort, sourceProperty: 'DG', target: anlageGeratestandortDG, targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.standort, sourceProperty: 'SB', target: anlageGeratestandortSB, targetProperty: 'selected', mutual: true)
// Anlagendaten - Luftkanalverlegung
bind(source: model.map.anlage.luftkanalverlegung, sourceProperty: 'aufputz',     target: anlageLuftkanalverlegungAufputz,     targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.luftkanalverlegung, sourceProperty: 'dammschicht', target: anlageLuftkanalverlegungDammschicht, targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.luftkanalverlegung, sourceProperty: 'decke',       target: anlageLuftkanalverlegungDecke,       targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.luftkanalverlegung, sourceProperty: 'spitzboden',  target: anlageLuftkanalverlegungSpitzboden,  targetProperty: 'selected', mutual: true)
// Anlagendaten - Außenluft
bind(source: model.map.anlage.aussenluft, sourceProperty: 'dach',        target: anlageAussenluftDach,        targetProperty: 'selected',     mutual: true)
bind(source: model.map.anlage.aussenluft, sourceProperty: 'wand',        target: anlageAussenluftWand,        targetProperty: 'selected',     mutual: true)
bind(source: model.map.anlage.aussenluft, sourceProperty: 'erdwarme',    target: anlageAussenluftErdwarme,    targetProperty: 'selected',     mutual: true)
// Anlagendaten - Zuluft
bind(source: model.map.anlage.zuluft, sourceProperty: 'tellerventile' , target: anlageZuluftTellerventile,  targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.zuluft, sourceProperty: 'schlitzauslass', target: anlageZuluftSchlitzauslass, targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.zuluft, sourceProperty: 'fussboden'     , target: anlageZuluftFussboden,      targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.zuluft, sourceProperty: 'sockel'        , target: anlageZuluftSockel,         targetProperty: 'selected', mutual: true)
// Anlagendaten - Abluft
bind(source: model.map.anlage.abluft, sourceProperty: 'tellerventile' , target: anlageAbluftTellerventile,  targetProperty: 'selected',  mutual: true)
// Anlagendaten - Fortluft
bind(source: model.map.anlage.fortluft, sourceProperty: 'dach',         target: anlageFortluftDach,         targetProperty: 'selected',     mutual: true)
bind(source: model.map.anlage.fortluft, sourceProperty: 'wand',         target: anlageFortluftWand,         targetProperty: 'selected',     mutual: true)
bind(source: model.map.anlage.fortluft, sourceProperty: 'bogen135',     target: anlageFortluftBogen135,     targetProperty: 'selected',     mutual: true)
// Anlagendaten - Energie-Kennzeichen
bind(source: model.map.anlage.energie, sourceProperty: 'zuAbluftWarme', target: anlageEnergieZuAbluftWarme, targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.energie, sourceProperty: 'bemessung',     target: anlageEnergieBemessung,     targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.energie, sourceProperty: 'ruckgewinnung', target: anlageEnergieRuckgewinnung, targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.energie, sourceProperty: 'regelung',      target: anlageEnergieRegelung,      targetProperty: 'selected', mutual: true)
//
bind(source: anlageEnergieZuAbluftWarme, sourceProperty: 'selected', target: anlageEnergieBemessung,     targetProperty: 'enabled')
bind(source: anlageEnergieZuAbluftWarme, sourceProperty: 'selected', target: anlageEnergieRuckgewinnung, targetProperty: 'enabled')
bind(source: anlageEnergieZuAbluftWarme, sourceProperty: 'selected', target: anlageEnergieRegelung,      targetProperty: 'enabled')
//
bind(source: model.map.anlage.energie, sourceProperty: 'nachricht',     target: anlageEnergieNachricht,     targetProperty: 'text')
[anlageEnergieZuAbluftWarme, anlageEnergieBemessung, anlageEnergieRuckgewinnung, anlageEnergieRegelung].each {
	it.actionPerformed = controller.berechneEnergieKennzeichen
}
// Anlagendaten - Hygiene-Kennzeichen
bind(source: model.map.anlage.hygiene, sourceProperty: 'ausfuhrung',         target: anlageHygieneAusfuhrung,         targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.hygiene, sourceProperty: 'filterung',          target: anlageHygieneFilterung,          targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.hygiene, sourceProperty: 'keineVerschmutzung', target: anlageHygieneKeineVerschmutzung, targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage.hygiene, sourceProperty: 'dichtheitsklasseB',  target: anlageHygieneDichtheitsklasseB,  targetProperty: 'selected', mutual: true)
//
bind(source: model.map.anlage.hygiene, sourceProperty: 'nachricht',     target: anlageHygieneNachricht,     targetProperty: 'text')
[anlageHygieneAusfuhrung, anlageHygieneFilterung, anlageHygieneKeineVerschmutzung, anlageHygieneDichtheitsklasseB].each {
	it.actionPerformed = controller.berechneHygieneKennzeichen
}
// Anlagendaten - Rückschlagkappe, Schallschutz-Kennzeichnung, Feuerstätten-Kennzeichnung, Kennzeichnung der Lüftungsanlage
bind(source: model.map.anlage, sourceProperty: 'ruckschlagklappe',            target: anlageruckschlagklappe,            targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage, sourceProperty: 'schallschutz',                target: anlageSchallschutz,                targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage, sourceProperty: 'feuerstatte',                 target: anlageFeuerstatte,                 targetProperty: 'selected', mutual: true)
bind(source: model.map.anlage, sourceProperty: 'kennzeichnungLuftungsanlage', target: anlageKennzeichnungLuftungsanlage, targetProperty: 'text')
[anlageruckschlagklappe, anlageSchallschutz, anlageFeuerstatte].each {
	it.actionPerformed = controller.berechneKennzeichenLuftungsanlage
}

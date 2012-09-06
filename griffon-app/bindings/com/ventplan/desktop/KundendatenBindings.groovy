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

// Kundendaten - Großhandel
bind(source: model.map.kundendaten.grosshandel,       sourceProperty: "firma1",           target: grosshandelFirma1,                targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten.grosshandel,       sourceProperty: "firma2",           target: grosshandelFirma2,                targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten.grosshandel,       sourceProperty: "strasse",          target: grosshandelStrasse,               targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten.grosshandel,       sourceProperty: "plz",              target: grosshandelPlz,                   targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten.grosshandel,       sourceProperty: "ort",              target: grosshandelOrt,                   targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten.grosshandel,       sourceProperty: "telefon",          target: grosshandelTelefon,               targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten.grosshandel,       sourceProperty: "telefax",          target: grosshandelTelefax,               targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten.grosshandel,       sourceProperty: "ansprechpartner",  target: grosshandelAnsprechpartner,       targetProperty: "text", mutual: true)
// Kundendaten - Ausführende Firma
bind(source: model.map.kundendaten.ausfuhrendeFirma, sourceProperty: "firma1",          target: ausfuhrendeFirmaFirma1,          targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten.ausfuhrendeFirma, sourceProperty: "firma2",          target: ausfuhrendeFirmaFirma2,          targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten.ausfuhrendeFirma, sourceProperty: "strasse",         target: ausfuhrendeFirmaStrasse,         targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten.ausfuhrendeFirma, sourceProperty: "plz",             target: ausfuhrendeFirmaPlz,             targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten.ausfuhrendeFirma, sourceProperty: "ort",             target: ausfuhrendeFirmaOrt,             targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten.ausfuhrendeFirma, sourceProperty: "telefon",         target: ausfuhrendeFirmaTelefon,         targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten.ausfuhrendeFirma, sourceProperty: "telefax",         target: ausfuhrendeFirmaTelefax,         targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten.ausfuhrendeFirma, sourceProperty: "ansprechpartner", target: ausfuhrendeFirmaAnsprechpartner, targetProperty: "text", mutual: true)
// Kundendaten - Notizen
bind(source: model.map.kundendaten, sourceProperty: "bauvorhaben",          target: bauvorhaben,          targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten, sourceProperty: "bauvorhabenAnschrift", target: bauvorhabenAnschrift, targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten, sourceProperty: "bauvorhabenPlz",       target: bauvorhabenPlz,       targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten, sourceProperty: "bauvorhabenOrt",       target: bauvorhabenOrt,       targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten, sourceProperty: "notizen",              target: notizen,              targetProperty: "text", mutual: true)
// Kundendaten - Bauvorhaben: Update tab title
bauvorhaben.addCaretListener({ evt -> controller.setTabTitle() } as javax.swing.event.CaretListener)
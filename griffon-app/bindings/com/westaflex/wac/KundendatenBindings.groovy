/**
 * WAC
 * 
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. All rights reserved.
 * Created by: rbe
 */
package com.westaflex.wac

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
bind(source: model.map.kundendaten, sourceProperty: "bauvorhaben",    target: bauvorhaben,    targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten, sourceProperty: "bauvorhabenPlz", target: bauvorhabenPlz, targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten, sourceProperty: "bauvorhabenOrt", target: bauvorhabenOrt, targetProperty: "text", mutual: true)
bind(source: model.map.kundendaten, sourceProperty: "notizen",        target: notizen,        targetProperty: "text", mutual: true)
// Kundendaten - Bauvorhaben: Update tab title
bauvorhaben.addCaretListener({ evt -> controller.setTabTitle() } as javax.swing.event.CaretListener)

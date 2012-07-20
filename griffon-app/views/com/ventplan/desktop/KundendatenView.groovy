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

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout

// Großhandel
panel(id: "kundendatenHauptPanel", layout: new MigLayout("ins 0 n 0 n, wrap 2","[grow,fill] [grow,fill]"), constraints: "grow") {
    panel(id: "kundendatenGrosshandel", border: titledBorder(title: "Großhandel"), layout: new MigLayout("ins 0 n 0 n, wrap 2, fill", "[right] 16 [grow,fill] []"), constraints: "grow") {
        // Row 1
        label("Firma 1")
        textField(id: "grosshandelFirma1")
        // Row 2
        label("Firma 2")
        textField(id: "grosshandelFirma2")
        // Row 3
        label("Strasse")
        textField(id: "grosshandelStrasse")
        // Row 4
        label("PLZ / Ort")
        textField(id: "grosshandelPlz", constraints: "split 2, width 80px!")
        textField(id: "grosshandelOrt", constraints: "growx")
        // Row 5
        label("Telefon")
        textField(id: "grosshandelTelefon")
        // Row 6
        label("Telefax")
        textField(id: "grosshandelTelefax")
        // Row 7
        label("Ansprechpartner")
        textField(id: "grosshandelAnsprechpartner")
    }
    // Ausführende Firma
    panel(id: "kundendatenAusfuhrendeFirma", border: titledBorder(title: "Ausführende Firma"), layout: new MigLayout("ins 0 n 0 n, wrap 2, fill", "[right] 16 [grow,fill] []"), constraints: "grow") {
        // Row 1
        label("Firma 1")
        textField(id: "ausfuhrendeFirmaFirma1")
        // Row 2
        label("Firma 2")
        textField(id: "ausfuhrendeFirmaFirma2")
        // Row 3
        label("Strasse")
        textField(id: "ausfuhrendeFirmaStrasse")
        // Row 4
        label("PLZ / Ort")
        textField(id: "ausfuhrendeFirmaPlz", constraints: "split 2, width 80px!")
        textField(id: "ausfuhrendeFirmaOrt", constraints: "growx")
        //}
        // Row 5
        label("Telefon")
        textField(id: "ausfuhrendeFirmaTelefon")
        // Row 6
        label("Telefax")
        textField(id: "ausfuhrendeFirmaTelefax")
        // Row 7
        label("Ansprechpartner")
        textField(id: "ausfuhrendeFirmaAnsprechpartner")
    }
    // Notizen
    panel(id: "kundendatenNotizen", border: titledBorder(title: "Bauvorhaben, Investor"), layout: new MigLayout("ins 0 n 0 n, wrap 2","[] 16 [grow,fill]"), constraints: "grow, span 2") {
        // Bauvorhaben
        label("Bauvorhaben")
        textField(id: "bauvorhaben", constraints: "growx")
        label("Anschrift")
        textField(id: "bauvorhabenAnschrift", constraints: "growx")
        label("PLZ/Ort")
        panel(layout: new MigLayout("ins 0 0 0 0, wrap 2","[fill] 16 [grow,fill]"), constraints: "growx") {
            textField(id: "bauvorhabenPlz", constraints: "width 80px!")
            textField(id: "bauvorhabenOrt", constraints: "growx")
        }
        // Notizen
        label("Notizen")
        jideScrollPane(constraints: "grow") {
            textArea(id: "notizen", rows: 13, constraints: "grow")
        }
    }
}
GH.yellowTextField(grosshandelFirma1)
GH.yellowTextField(grosshandelFirma2)
GH.yellowTextField(grosshandelStrasse)
GH.yellowTextField(grosshandelPlz)
GH.yellowTextField(grosshandelOrt)
GH.yellowTextField(grosshandelTelefon)
GH.yellowTextField(grosshandelTelefax)
GH.yellowTextField(grosshandelAnsprechpartner)
GH.yellowTextField(ausfuhrendeFirmaFirma1)
GH.yellowTextField(ausfuhrendeFirmaFirma2)
GH.yellowTextField(ausfuhrendeFirmaStrasse)
GH.yellowTextField(ausfuhrendeFirmaPlz)
GH.yellowTextField(ausfuhrendeFirmaOrt)
GH.yellowTextField(ausfuhrendeFirmaTelefon)
GH.yellowTextField(ausfuhrendeFirmaTelefax)
GH.yellowTextField(ausfuhrendeFirmaAnsprechpartner)
GH.yellowTextField(bauvorhaben)
GH.yellowTextField(bauvorhabenAnschrift)
GH.yellowTextField(bauvorhabenPlz)
GH.yellowTextField(bauvorhabenOrt)
// Bindings
build(KundendatenBindings)

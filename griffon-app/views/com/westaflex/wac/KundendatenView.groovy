/**
 * /Users/rbe/project/wac2/griffon-app/views/com/westaflex/wac/KundendatenView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout


// Großhandel
// new MigLayout("wrap 2","[] [grow]","[] [grow] [grow] []")
//panel(id: "kundendatenHauptPanel", constraints: "grow", layout: new MigLayout("wrap 2", "[300::, grow 50, fill][300::, grow 50, fill]", "[]15[]")) {
panel(id: "kundendatenHauptPanel", layout: new MigLayout("wrap 2","[grow,fill] [grow,fill]")) {
    panel(id: "kundendatenGrosshandel", border: titledBorder(title: "Kunde 1 (Großhandel)"), constraints: "grow", layout: new MigLayout("wrap 2, fill", "[right] 16 [grow,fill] []")) {
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
        //panel(constraints: "grow", layout: new MigLayout("fill, insets 2, gap 2", "[grow][grow]", "[grow]")) {
        textField(id: "grosshandelPlz", constraints: "split 2, width 80px!")
        textField(id: "grosshandelOrt", constraints: "width :400:")
        //}
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
    GH.recurse(kundendatenGrosshandel, GH.yellowTextField)
    // Ausführende Firma
    //panel(id: "kundendatenAusfuhrendeFirma", border: titledBorder(title: "Kunde 2 (Ausführende Firma)"), layout: new MigLayout("fill, insets 5, gap 5, wrap 2", "[][grow]", "[][]")) {
    panel(id: "kundendatenAusfuhrendeFirma", border: titledBorder(title: "Kunde 2 (Ausführende Firma)"), constraints: "grow", layout: new MigLayout("wrap 2, fill", "[right] 16 [grow,fill] []")) {
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
        //panel(constraints: "grow", layout: new MigLayout("fill, insets 2, gap 2", "[grow][grow]", "[grow]")) {
        textField(id: "ausfuhrendeFirmaPlz", constraints: "split 2, width 80px!")
        textField(id: "ausfuhrendeFirmaOrt", constraints: "width :400:")
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
    GH.recurse(kundendatenAusfuhrendeFirma, GH.yellowTextField)
    // Notizen
    panel(id: "kundendatenNotizen", border: titledBorder(title: "Notizen"), constraints: "grow, span 2", layout: new MigLayout("wrap 2","[] 16 [grow,fill]")) {
        // Bauvorhaben
        label("Bauvorhaben")
        textField(id: "bauvorhaben", constraints: "growx")
        // Notizen
        label("Notizen")
        jideScrollPane(constraints: "grow") {
            textArea(id: "notizen", rows: 13)
        }
    }
    GH.recurse(kundendatenNotizen, GH.yellowTextField)
}

// Bindings
build(KundendatenBindings)

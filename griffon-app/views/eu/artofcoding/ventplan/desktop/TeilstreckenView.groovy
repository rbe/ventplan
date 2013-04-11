/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 17:23
 */

package eu.artofcoding.ventplan.desktop

import eu.artofcoding.griffon.helper.GriffonHelper as GH
import net.miginfocom.swing.MigLayout

panel(id: "teilstreckenPanel", layout: new MigLayout("fillx, wrap", "[fill]", "[fill]")) {

    // Links oben: Tabelle: Anzahl (Textfeld), Bezeichnung des Widerstands, Widerstandswert
    panel(id: "teilstreckenTabellePanel", layout: new MigLayout("fill, wrap 3", "[][][]", "[fill]")) {
        label(GH.ws("Verfügbare<br />Teilstrecken"))
        label(" ")
        label(GH.ws("Ausgewählte<br/>Teilstrecken"))

        jideScrollPane(constraints: "width 70px!") {
            list(id: "teilstreckenVerfugbareListe", model: new javax.swing.DefaultListModel())
        }
        panel(layout: new MigLayout("fill, wrap 1", "[]", "50[][]30")) {
            button(id: "teilstreckenNachVerfugbare", "<<")
            button(id: "teilstreckenNachAusgewahlte", ">>")
        }
        jideScrollPane(constraints: "width 70px!") {
            list(id: "teilstreckenAusgewahlteListe", model: new javax.swing.DefaultListModel())
        }

        label("Übernommen wird", constraints: "span 2, wrap")
        textField(id: "teilstreckenAuswahl", constraints: "grow, span 2")
    }
    // Rechts unten: Buttons
    panel(id: "teilstreckenButton", layout: new MigLayout("fillx", "[left][right]", "[fill]")) {
        button(id: "teilstreckenOk", text: "OK")
        button(id: "teilstreckenCancel", text: "Abbrechen")
    }

}

// Bindings
// Buttons
teilstreckenNachVerfugbare.actionPerformed = controller.teilstreckenNachVerfugbarVerschieben
teilstreckenNachAusgewahlte.actionPerformed = controller.teilstreckenNachAusgewahlteVerschieben
teilstreckenOk.actionPerformed = controller.teilstreckenOkButton
teilstreckenCancel.actionPerformed = controller.teilstreckenCancelButton

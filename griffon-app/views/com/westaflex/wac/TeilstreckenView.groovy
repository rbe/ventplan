/**
 * /Users/rbe/project/wac2/griffon-app/views/com/westaflex/wac/WbwView.groovy
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
build(TeilstreckenBindings)

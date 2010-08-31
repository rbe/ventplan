/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/com/westaflex/wac/ProjektView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout

jideTabbedPane(projektTabGroup, selectedIndex: projektTabGroup.tabCount) {
    panel(id: "projektTab", title: controller.makeTabTitle().toString()) {
        borderLayout()
        // Kundendaten
        jideTabbedPane(id: "datenTabGroup") {
            // Kundendaten , constraints: java.awt.BorderLayout.CENTER
            panel(id: "kundenTab", title: "Kundendaten", layout: new MigLayout("","[] [fill,grow] []")) {
                build(KundendatenView)
            }
            // Gebäudedaten
            panel(id: "gebaudeTab", title: "Gebäudedaten", layout: new MigLayout("","[] [fill,grow] []")) {
                build(GebaudedatenView)
            }
            // Anlagendaten
            panel(id: "anlageTab", title: "Anlagedaten", layout: new MigLayout("","[] [fill,grow] []")) {
                build(AnlagendatenView)
            }
            // Raumdaten
            panel(id: "raumTab", title: "Raumdaten", layout: new MigLayout("","[] [fill,grow] []")) {
                build(RaumdatenView)
            }
            // Außenluftvolumenströme
            panel(id: "aussenluftVsTab", title: "Außenluftvolumenströme", layout: new MigLayout("","[] [fill,grow] []")) {
                build(AussenluftVsView)
            }
            // Raumvolumenströme
            panel(id: "raumVsTab", title: "Raumvolumenströme", layout: new MigLayout("","[] [fill,grow] []")) {
                build(RaumVsView)
            }
            // Druckverlustberechnung
            panel(id: "dvbTab", title: "Druckverlustberechnung", layout: new MigLayout("","[] [fill,grow] []")) {
                build(DruckverlustView)
            }
            // Akkustikberechnung
            panel(id: "akustikTab", title: "Akustikberechnung", layout: new MigLayout("","[] [fill,grow] []")) {
                build(AkustikView)
            }
        }
        /*
        hbox(constraints: SOUTH) {
            //button("Hier könnte Ihre Werbung stehen!")
        }
        */
    }

}
// Bindings
build(ProjektBindings)
//
// JIDE
//
// projektTabGroup
projektTabGroup.with {
	setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
	setShowCloseButton(false)
	setShowCloseButtonOnSelectedTab(false)
}
// datenTabGroup
datenTabGroup.with {
	setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
	setShowCloseButton(false)
}

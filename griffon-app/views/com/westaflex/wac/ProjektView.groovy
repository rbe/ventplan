/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/com/westaflex/wac/ProjektView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout

jideTabbedPane(projektTabGroup, selectedIndex: projektTabGroup.tabCount, constraints: "growx") {
    panel(id: "projektTab", title: controller.makeTabTitle().toString(), layout: new MigLayout("fill, wrap"), constraints: "grow") {
        // Kundendaten
        jideTabbedPane(id: "datenTabGroup", constraints: "grow") {
            // Kundendaten , constraints: java.awt.BorderLayout.CENTER
            panel(id: "kundenTab", title: "Kundendaten", layout: new MigLayout("fillx","[grow]"), constraints: "grow") {
                build(KundendatenView)
            }
            // Gebäudedaten
            panel(id: "gebaudeTab", title: "Gebäudedaten", layout: new MigLayout("fillx","[grow]"), constraints: "grow") {
                build(GebaudedatenView)
            }
            // Anlagendaten
            panel(id: "anlageTab", title: "Anlagedaten", layout: new MigLayout("fillx","[grow]"), constraints: "grow") {
                build(AnlagendatenView)
            }
            // Raumdaten
            panel(id: "raumTab", title: "Raumdaten", layout: new MigLayout("fillx","[grow]"), constraints: "grow") {
                build(RaumdatenView)
            }
            // Außenluftvolumenströme
            panel(id: "aussenluftVsTab", title: "Außenluftvolumenströme", layout: new MigLayout("fillx","[grow]"), constraints: "grow") {
                build(AussenluftVsView)
            }
            // Raumvolumenströme
            panel(id: "raumVsTab", title: "Raumvolumenströme", layout: new MigLayout("fillx","[grow]"), constraints: "grow") {
                build(RaumVsView)
            }
            // TODO rbe Vorerst ausgeschaltet; Berechnungen mit Stukemeier prüfen
            // Druckverlustberechnung
            panel(id: "dvbTab", title: "Druckverlustberechnung", layout: new MigLayout("fillx","[grow]"), constraints: "grow") {
                build(DruckverlustView)
            }
            // Akustikberechnung
            panel(id: "akustikTab", title: "Akustikberechnung", layout: new MigLayout("fillx","[grow]"), constraints: "grow") {
                build(AkustikView)
            }
        }
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

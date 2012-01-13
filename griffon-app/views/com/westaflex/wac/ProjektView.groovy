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

import net.miginfocom.swing.MigLayout

jideTabbedPane(projektTabGroup, selectedIndex: projektTabGroup.tabCount, constraints: "growx") {
    panel(id: "projektTab", title: controller.makeTabTitle().toString(), layout: new MigLayout("ins 5 n 0 n, fill, wrap"), constraints: "grow") {
        // Kundendaten
        jideTabbedPane(id: "datenTabGroup", constraints: "grow") {
            // Kundendaten , constraints: java.awt.BorderLayout.CENTER
            panel(id: "kundenTab", title: "Kundendaten", layout: new MigLayout("ins 5 n 0 n, fillx","[grow]"), constraints: "grow") {
                build(KundendatenView)
            }
            // Gebäudedaten
            panel(id: "gebaudeTab", title: "Gebäudedaten", layout: new MigLayout("ins 5 n 0 n, fillx","[grow]"), constraints: "grow") {
                build(GebaudedatenView)
            }
            // Anlagendaten
            //panel(id: "anlageTab", title: "Anlagedaten", layout: new MigLayout("fillx","[grow]"), constraints: "grow") {
            panel(id: "anlageTab", title: "Anlagedaten", layout: new MigLayout("ins 5 n 0 n, ")) {
                build(AnlagendatenView)
            }
            // Raumdaten
            panel(id: "raumTab", title: "Raumdaten", layout: new MigLayout("ins 5 n 0 n, fillx","[grow]"), constraints: "grow") {
                build(RaumdatenView)
            }
            // Außenluftvolumenströme
            panel(id: "aussenluftVsTab", title: "Außenluftvolumenströme", layout: new MigLayout("ins 5 n 0 n, fillx","[grow]"), constraints: "grow") {
                build(AussenluftVsView)
            }
            // Raumvolumenströme
            panel(id: "raumVsTab", title: "Raumvolumenströme", layout: new MigLayout("ins 5 n 0 n, fillx","[grow]"), constraints: "grow") {
                build(RaumVsView)
            }
            // TODO rbe Vorerst ausgeschaltet; Berechnungen mit Stukemeier prüfen
            // Druckverlustberechnung
            panel(id: "dvbTab", title: "Druckverlustberechnung", layout: new MigLayout("ins 5 n 0 n, fillx","[grow]"), constraints: "grow") {
                build(DruckverlustView)
            }
            // Akustikberechnung
            panel(id: "akustikTab", title: "Akustikberechnung", layout: new MigLayout("ins 5 n 0 n, fillx","[grow]"), constraints: "grow") {
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
	//setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
	setShowCloseButton(false)
	setShowCloseButtonOnSelectedTab(false)
}
// datenTabGroup
datenTabGroup.with {
	//setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
	setShowCloseButton(false)
}

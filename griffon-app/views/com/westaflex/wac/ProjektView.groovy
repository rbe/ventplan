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
	panel(id: "projektTab", title: controller.makeTabTitle()) {
		borderLayout()
		// Scrollpane
		//jideScrollPane() {
			jideTabbedPane(id: "datenTabGroup") {
				// Kundendaten
				//panel(id: "kundenTab", title: "Kundendaten", layout: new MigLayout("fillx, wrap 2", "[fill][fill]", "[fill][fill]")) {
                panel(id: "kundenTab", title: "Kundendaten", layout: new MigLayout("fill", "[fill, grow]", "[fill]")) {
					build(KundendatenView)
				}
				// Gebäudedaten
				panel(id: "gebaudeTab", title: "Gebäudedaten", layout: new MigLayout("fillx, wrap 3", "[fill][fill][fill]", "[fill][fill]")) {
					build(GebaudedatenView)
				}
				// Anlagendaten
				panel(id: "anlageTab", title: "Anlagedaten", layout: new MigLayout("fillx, wrap 3", "[fill][fill][fill]", "[fill][fill]")) {
					build(AnlagendatenView)
				}
				// Raumdaten
				panel(id: "raumTab", title: "Raumdaten", layout: new MigLayout("fillx")) {
					build(RaumdatenView)
				}
				// Außenluftvolumenströme
				panel(id: "aussenluftVsTab", title: "Außenluftvolumenströme", layout: new MigLayout("fillx, wrap", "[fill]", "[fill]")) {
					build(AussenluftVsView)
				}
				// Raumvolumenströme
				panel(id: "raumVsTab", title: "Raumvolumenströme", layout: new MigLayout("fillx")) {
					build(RaumVsView)
				}
				// Druckverlustberechnung
				panel(id: "dvbTab", title: "Druckverlustberechnung") {
					build(DruckverlustView)
				}
				// Akkustikberechnung
				panel(id: "akustikTab", title: "Akustikberechnung") {
					build(AkustikView)
				}
				
				// RaumdatenDialogView - Testweise hier...
				//panel(id: "raumdatenDialogTab", title: "RaumdatenDialog Einstellungen") {
				//	build(RaumdatenDialogView)
				//}
			}
		//}
		hbox(constraints: SOUTH) {
			//button("Hier könnte Ihre Werbung stehen!")
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

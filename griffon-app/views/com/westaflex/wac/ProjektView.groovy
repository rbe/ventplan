package com.westaflex.wac

import net.miginfocom.swing.MigLayout

jideTabbedPane(projektTabGroup, selectedIndex: projektTabGroup.tabCount) {
	panel(id: "projektTab", title: tabName) {
		borderLayout()
		// Scrollpane
		jideScrollPane() {
			jideTabbedPane(id: "datenTabGroup") {
				// Kundendaten
				panel(id: "kundenTab", title: "Kundendaten", layout: new MigLayout("fillx, wrap 2", "[fill][fill]", "[fill][fill]")) {
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
				panel(id: "druckverlustTab", title: "Druckverlustberechnung") {
					build(DruckverlustView)
				}
				// Akkustikberechnung
				panel(id: "akustikTab", title: "Akustikberechnung") {
					build(AkustikView)
				}
				
				// RaumdatenDialogView - Testweise hier...
				panel(id: "raumdatenDialogTab", title: "RaumdatenDialog Einstellungen") {
					build(RaumdatenDialogView)
				}
			}
		}
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
	setShowCloseButton(true)
	setShowCloseButtonOnSelectedTab(true)
}
// datenTabGroup
datenTabGroup.with {
	setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
	setShowCloseButton(false)
}

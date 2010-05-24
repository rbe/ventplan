package com.westaflex.wac

import com.jidesoft.plaf.LookAndFeelFactory
import com.jidesoft.swing.JideTabbedPane
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
				panel(id: "raumVsTab", title: "Raumvolumenströme") {
					build(RaumVsView)
				}
				// Druckverlustberechnung
				panel(id: "druckverlustTab", title: "Druckverlustberechnung") {
					build(DruckverlustView)
				}
				// Akkustikberechnung
				panel(id: "akkustikTab", title: "Akkustikberechnung") {
					build(AkkustikView)
				}
			}
		}
		hbox(constraints: SOUTH) {
			button(halloAction)
		}
	}
}
// Bindings
build(ProjektBindings)
//
// JIDE
//
//LookAndFeelFactory.installJideExtension(LookAndFeelFactory.ECLIPSE_STYLE)
// projektTabGroup
projektTabGroup.with {
	setTabColorProvider(JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	//ColorTheme(JideTabbedPane.COLOR_THEME_WIN2K)
	//setTabShape(JideTabbedPane.SHAPE_ECLIPSE)
	setBoldActiveTab(true)
	setShowCloseButton(true)
	setShowCloseButtonOnSelectedTab(true)
}
// datenTabGroup
datenTabGroup.with {
	setTabColorProvider(JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	//setColorTheme(JideTabbedPane.COLOR_THEME_WIN2K)
	//setTabShape(JideTabbedPane.SHAPE_ECLIPSE)
	setBoldActiveTab(true)
}

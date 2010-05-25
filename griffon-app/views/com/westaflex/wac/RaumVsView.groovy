package com.westaflex.wac

import net.miginfocom.swing.MigLayout

panel(id: "raumVsVentileTabPanel") {
	jideTabbedPane() {
		panel(id: "raumVsZuAbluftventileTab", title: "Zu-/Abluftventile") {
			build(RaumVsZuAbluftventileView)
		}
		
		panel(id: "raumVsUberstromventileTab", title: "Überströmventile") {
			// build(RaumVsUberstromventileView)
		}
	}
}


// raumVsVentileTabGroup
/*
raumVsVentileTabGroup.with {
	setTabColorProvider(JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	//setColorTheme(JideTabbedPane.COLOR_THEME_WIN2K)
	//setTabShape(JideTabbedPane.SHAPE_ECLIPSE)
	setBoldActiveTab(true)
}
*/

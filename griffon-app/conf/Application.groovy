application {
	title = 'Wac2'
	startupGroups = ['wac2']
	// Should Griffon exit when no Griffon created frames are showing?
	// rbe: see Wac2View.application.windowClosing
	autoShutdown = false
	// If you want some non-standard application class, apply it here
	//frameClass = 'javax.swing.JFrame'
}
mvcGroups {
	// MVC Group for "com.westaflex.wac.Projekt"
	'Projekt' {
		model = 'com.westaflex.wac.ProjektModel'
		controller = 'com.westaflex.wac.ProjektController'
		actions = 'ProjektActions'
		view = 'com.westaflex.wac.ProjektView'
	}
	// MVC Group for "com.westaflex.wac.Kunde"
	'Kunde' {
		model = 'com.westaflex.wac.KundeModel'
		controller = 'com.westaflex.wac.KundeController'
		view = 'com.westaflex.wac.KundeView'
	}
	// MVC Group for "wac2"
	'wac2' {
		model = 'Wac2Model'
		controller = 'Wac2Controller'
		actions = 'Wac2Actions'
		view = 'Wac2View'
	}
}
griffon.basic_injection.disable = true

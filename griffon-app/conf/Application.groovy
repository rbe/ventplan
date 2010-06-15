application {
	title = "Wac2"
	startupGroups = ["wac2"]
	autoShutdown = false
}
mvcGroups {
	Projekt {
		model = "com.westaflex.wac.ProjektModel"
		controller = "com.westaflex.wac.ProjektController"
		actions = "ProjektActions"
		view = "com.westaflex.wac.ProjektView"
	}
	Kunde {
		model = "com.westaflex.wac.KundeModel"
		controller = "com.westaflex.wac.KundeController"
		view = "com.westaflex.wac.KundeView"
	}
	wac2 {
		model = "Wac2Model"
		controller = "Wac2Controller"
		actions = "Wac2Actions"
		view = "Wac2View"
	}
}
griffon.basic_injection.disable = true

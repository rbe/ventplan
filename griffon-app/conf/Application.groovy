application {
	title="Wac2"
	startupGroups=["wac2", "Dialog"]
	autoShutdown=false
}
mvcGroups {
	Projekt {
		model="com.westaflex.wac.ProjektModel"
		controller="com.westaflex.wac.ProjektController"
		actions="com.westaflex.wac.ProjektActions"
		view="com.westaflex.wac.ProjektView"
	}
	wac2 {
		model="Wac2Model"
		controller="Wac2Controller"
		actions="Wac2Actions"
		view="Wac2View"
	}
	Dialog {
		controller="com.westaflex.wac.DialogController"
	}
}

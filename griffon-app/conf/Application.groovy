application {
	title='Wac2'
	startupGroups=['wac2', 'Dialog']
	autoShutdown=false
}
mvcGroups {
	'Projekt' {
		model="com.westaflex.wac.ProjektModel"
		controller="com.westaflex.wac.ProjektController"
		actions="com.westaflex.wac.ProjektActions"
		view="com.westaflex.wac.ProjektView"
	}
	'wac2' {
		model="com.westaflex.wac.Wac2Model"
		controller="com.westaflex.wac.Wac2Controller"
		actions="com.westaflex.wac.Wac2Actions"
		view="com.westaflex.wac.Wac2View"
	}
	Dialog {
		controller="com.westaflex.wac.DialogController"
	}
}

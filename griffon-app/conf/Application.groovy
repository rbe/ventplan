application {
	title="Wac2"
	startupGroups=["wac2"]
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
}
griffon {
	basic_injection.disable=true
	gsql.injectInto=[/*"controller", */"service"]
}

griffon.basic_injection.disable = true

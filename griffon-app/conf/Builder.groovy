jx {
	'groovy.swing.SwingXBuilder' {
        controller = ['withWorker']
		view = '*'
	}
}

root {
	'groovy.swing.SwingBuilder' {
		controller = ['Threading']
		view = '*'
	}
	'griffon.app.ApplicationBuilder' {
		view = '*'
	}
}

root.'TrayBuilderGriffonAddon'.addon = true

root.'griffon.builder.swingxtras.SwingxtrasBuilder'.view = '*'
root.'griffon.builder.jide.JideBuilder'.view = '*'

root.'GlazedlistsGriffonAddon'.addon = true

root.'OxbowGriffonAddon'.addon=true
root.'OxbowGriffonAddon'.controller=['ask','choice','error','inform','showException','radioChoice','warn']

root.'MiglayoutGriffonAddon'.addon=true


root.'GsqlGriffonAddon'.addon=true


root.'SpringGriffonAddon'.addon=true

root.'WsclientGriffonAddon'.addon=true

root.'DatasourceGriffonAddon'.addon=true

root.'ZonelayoutGriffonAddon'.addon=true

root.'SplashGriffonAddon'.addon=true

root.'GlazedlistsGriffonAddon'.addon=true



root.'RestGriffonAddon'.addon=true

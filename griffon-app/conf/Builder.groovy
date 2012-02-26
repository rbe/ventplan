/*
 * WAC
 *
 * Copyright (C) 2005      Informationssysteme Ralf Bensmann.
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

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

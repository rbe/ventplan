/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/Wac2View.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */

def screen = java.awt.Toolkit.defaultToolkit.screenSize

application(title: 'WestaWAC 2',
	size: [screen.width as int, screen.height as int],
	pack: false,
	locationByPlatform: true,
	iconImage: imageIcon('/griffon-icon-48x48.png').image,
	iconImages: [
		imageIcon('/griffon-icon-48x48.png').image,
		imageIcon('/griffon-icon-32x32.png').image,
		imageIcon('/griffon-icon-16x16.png').image
	],
	// Our window close listener
	defaultCloseOperation: javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE,
	windowClosing: { evt ->
		// Ask if we can close
		if (controller.canClose()) {
			app.shutdown()
		} else {
			println "windowClosing(${evt.dump()}): there are unsaved changes"
			// TODO mmu Show dialog: ask user for save all, cancel, quit
		}
	}
) {
	// Build menu bar
	menuBar(build(Wac2MenuBar))
	// Build toolbar
	toolBar(build(Wac2ToolBar))
	// Content
	widget(build(Wac2MainPane))
	// Bindings
	build(Wac2Bindings)
	// The status bar
	jxstatusBar(id: "mainStatusBar") {
		label(id: "mainStatusBarText", text: bind { model.statusBarText })
	}
}

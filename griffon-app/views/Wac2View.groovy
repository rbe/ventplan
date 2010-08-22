/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/Wac2View.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
import net.miginfocom.swing.MigLayout

def screen = java.awt.Toolkit.defaultToolkit.screenSize

wpxFileChooserWindow = fileChooser(
		dialogTitle: "Bitte wählen Sie eine WPX-Datei",
		multiSelectionEnabled: false,
		fileFilter: [
				getDescription: { -> "WestaWAC Projekt XML" },
				accept: { file ->
					//println "wpxFileChooser: filtering ${file.dump()} isDirectory=${file.isDirectory()} endsWith(wpx)=${file.name.endsWith(".wpx")}"
					return file.isDirectory() || file.name.toLowerCase().endsWith(".wpx")
				} ] as javax.swing.filechooser.FileFilter
	)

wac2Frame = application(title: 'WestaWAC 2',
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
	windowClosing: controller.exitApplication
) {
	// Build menu bar
	menuBar(build(Wac2MenuBar))
	// Build toolbar
	toolBar(build(Wac2ToolBar))
	// Content
    widget(
        // set scrollpane for all projects
        jideScrollPane(id: "mainScrollPane") {
            build(Wac2MainPane)
        }
    )
    // set visibility of scrollbars
    mainScrollPane.setHorizontalScrollBarPolicy(com.jidesoft.swing.JideScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    mainScrollPane.setVerticalScrollBarPolicy(com.jidesoft.swing.JideScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	// Bindings
	build(Wac2Bindings)
	// The status bar
	jxstatusBar(id: "mainStatusBar") {
		label(id: "mainStatusBarText", text: bind { model.statusBarText })
	}
}

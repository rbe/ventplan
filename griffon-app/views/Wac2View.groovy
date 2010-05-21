import java.awt.Toolkit
import javax.swing.WindowConstants

// Build actions
build(Wac2Actions)
def screen = Toolkit.getDefaultToolkit().getScreenSize()

application(title: 'WestaWAC 2',
	size: [screen.width as int, screen.height as int],
	pack: false,
	//location:[50,50],
	locationByPlatform: true,
	iconImage: imageIcon('/griffon-icon-48x48.png').image,
	iconImages: [
		imageIcon('/griffon-icon-48x48.png').image,
		imageIcon('/griffon-icon-32x32.png').image,
		imageIcon('/griffon-icon-16x16.png').image
	],
	// Our window close listener
	defaultCloseOperation: WindowConstants.DO_NOTHING_ON_CLOSE,
	windowClosing: { evt ->
		// TODO Ask service if we can close
		if (true) {
			println "windowClosing(${evt})"
			app.shutdown()
		} else {
			println "windowClosing(${evt}): else"
		}
	}
) {
	// Build menu bar
	menuBar(build(Wac2MenuBar))
	// Build toolbar
	toolBar(build(Wac2ToolBar))
	// Content
	widget(build(Wac2MainPane))
	// The status bar
	jxstatusBar(id: "statusBar") {
		label("statusbar")
	}
}

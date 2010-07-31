/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/controllers/com/westaflex/wac/ProjektController.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.westaflex.wac

import com.ezware.dialog.task.CommandLink

/**
 * 
 */
class DialogController {
	
	/**
     * Show this dialog, when closing the application.
     */
	def showApplicationCloseDialog() {
        /*
        def choice
        def lastPane = builder.optionPane()
        choice = lastPane.showOptionDialog( null,
            'Es befinden sich nicht gespeicherte Projekte in der Anwendung. Was möchten Sie machen?',
            'Achtung: Nicht gespeicherte Projekte vorhanden',
            javax.swing.JOptionPane.YES_NO_CANCEL_OPTION,
            javax.swing.JOptionPane.WARNING_MESSAGE,
            null,
            options as Object[],
            options[2])
        */
        def choice = choice("Anwendung schliessen?", "Die Anwendung enthält nicht gespeicherte Projekte. Bitte wählen Sie.", 1, [new CommandLink("Alle Speichern",""),new CommandLink("Abbrechen",""),new CommandLink("Schliessen","")])
        choice
    }

    /**
     * Show this dialog, when closing a project.
     */
	def showCloseProjectDialog() {
        def choice = choice("Projekt schliessen?", "Das Projekt enthält nicht gespeicherte Werte. Bitte wählen Sie.", 1, [new CommandLink("Speichern",""),new CommandLink("Abbrechen",""),new CommandLink("Schliessen","")])
        choice
    }

}

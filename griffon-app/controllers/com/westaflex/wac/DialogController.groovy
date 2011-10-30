/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/controllers/com/westaflex/wac/ProjektController.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 */
package com.westaflex.wac

import com.ezware.dialog.task.CommandLink
import java.awt.Window

/**
 * Manage and create dialogs.
 */
class DialogController {

    /**
     * Dialog anzeigen, wenn die Applikation geschlossen werden soll, obwohl
     * noch nicht gespeicherte Projekte vorhanden sind.
     */
    def showApplicationCloseDialog(window = null) {
        window ?: Window.windows.find{it.focused}
        def choice = choice(
            window,
            "Anwendung schliessen?",
            "Die Anwendung enthält nicht gespeicherte Projekte. Bitte wählen Sie.",
            1,
            [
                new CommandLink("Alle Speichern", ""),
                new CommandLink("Abbrechen", ""),
                new CommandLink("Ohne Speichern schliessen", "")
            ]
            )
        choice
    }

    /**
     * Dialog anzeigen, wenn ein nicht gespeichertes Projekt geschlossen werden soll.
     * WAC-185: Schliessen in Ok ändern.
     */
    def showCloseProjectDialog(window = null) {
        window ?: Window.windows.find{it.focused}
        def choice = choice(
            window,
            "Projekt schliessen?",
            "Das Projekt enthält nicht gespeicherte Werte. Bitte wählen Sie.",
            1,
            [
                new CommandLink("Speichern", ""),
                new CommandLink("Abbrechen", ""),
                new CommandLink("Ok", "")
            ]
            )
        choice
    }

    /**
     * Zeige Informationsdialog mit mitgegebener Nachricht an.
     */
    def showInformDialog = { window = null, infoMsg ->
        window ?: Window.windows.find{it.focused}
        inform(window, "Information", infoMsg)
    }

    /**
     * Zeige Fehler-Informationsdialog mit mitgegebener Nachricht an.
     */
    def showErrorDialog = { window = null, errorMsg ->
        window ?: Window.windows.find{it.focused}
        inform(window, "Fehler", errorMsg)
    }

    /**
     * Ticket #97
     * Dialog zum Drucken anzeigen.
     * Ja = Daten an OOo senden
     * Nein = es wird ein Blanko Angebot geöffnet
     */
    def showPrintProjectDialog(window = null) {
        window ?: Window.windows.find{it.focused}
        def choice = choice(
            window,
            "Angebot aus der aktuellen Auslegung erstellen?",
            "Bitte wählen Sie.",
            1,
            [
                new CommandLink("Ja", ""),
                new CommandLink("Nein", ""),
            ]
            )
        choice
    }
    
    /**
     * Zeige Informationsdialog für Angebotsverfolgung mit mitgegebener Nachricht an.
     */
    def showCustomInformDialog = { titel, infoMsg ->
        println "DialogController.showCustomInformDialog titel -> ${titel}, ${infoMsg}"
        TaskDialogs.inform(titel as String, infoMsg)
    }
    
}

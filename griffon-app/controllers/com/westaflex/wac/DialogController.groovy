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
import com.ezware.dialog.task.TaskDialogs
import java.awt.Window

/**
 * Manage and create dialogs.
 */
class DialogController {

    /**
     * Dialog anzeigen, wenn die Applikation geschlossen werden soll, obwohl
     * noch nicht gespeicherte Projekte vorhanden sind.
     */
    int showApplicationSaveAndCloseDialog() {
        def choice = choice(
            app.windowManager.windows.find{it.focused},
            "Anwendung schliessen?",
            "Die Anwendung enthält nicht gespeicherte Projekte. Bitte wählen Sie.",
            1,
            [
                new CommandLink("Alle Speichern", ""),
                new CommandLink("Abbrechen", ""),
                new CommandLink("Ohne Speichern schliessen", "")
            ]
            )
        return choice
    }
    
    /**
     * Dialog anzeigen, wenn die Applikation geschlossen werden soll.
     * Dialog nur für Schliessen bzw. Abbrechen nutzen.
     */
    int showApplicationOnlyCloseDialog() {
        def choice = choice(
            app.windowManager.windows.find{it.focused},
            "Anwendung schliessen?",
            "Möchten Sie die Anwendung wirklich schliessen?",
            1,
            [
                new CommandLink("Ja", ""),
                new CommandLink("Nein", "")
            ]
            )
        return choice
    }

    /**
     * Dialog anzeigen, wenn ein nicht gespeichertes Projekt geschlossen werden soll.
     * WAC-185: Schliessen in Ok ändern.
     */
    int showCloseProjectDialog() {
        def choice = choice(
            app.windowManager.windows.find{it.focused},
            "Projekt schliessen?",
            "Das Projekt enthält nicht gespeicherte Werte. Bitte wählen Sie.",
            1,
            [
                new CommandLink("Speichern", ""),
                new CommandLink("Abbrechen", ""),
                new CommandLink("Ok", "")
            ]
            )
        return choice
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
    int showPrintProjectDialog() {
        def choice = choice(
            app.windowManager.windows.find{it.focused},
            "Angebot aus der aktuellen Auslegung erstellen?",
            "Bitte wählen Sie.",
            1,
            [
                new CommandLink("Ja", ""),
                new CommandLink("Nein", ""),
            ]
            )
        return choice
    }
    
    /**
     * Zeige Informationsdialog für Angebotsverfolgung mit mitgegebener Nachricht an.
     */
    def showCustomInformDialog = { titel, infoMsg ->
        println "DialogController.showCustomInformDialog titel -> ${titel}, ${infoMsg}"
        TaskDialogs.inform(app.windowManager.windows.find{it.focused}, titel as String, infoMsg)
    }
    
}

/**
 * WAC
 * 
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. All rights reserved.
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
                new CommandLink("Alle Speichern und schliessen", ""),
                new CommandLink("Abbrechen, nicht speichern und nicht schliessen", ""),
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
                new CommandLink("Ja, klar!", ""),
                new CommandLink("Nein, doch nicht.", "")
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
                new CommandLink("Speichern und das Projekt schliessen", ""),
                new CommandLink("Abbrechen, nicht speichern und Projekt geöffnet lassen", ""),
                new CommandLink("Ok, nicht speichern und Projekt schliessen", "")
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
        TaskDialogs.inform(app.windowManager.windows.find{it.focused}, titel as String, infoMsg)
    }
    
}

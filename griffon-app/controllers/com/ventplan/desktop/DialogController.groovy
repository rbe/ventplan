/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.ventplan.desktop

import com.ezware.dialog.task.CommandLink
import com.ezware.dialog.task.TaskDialogs

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
                app.windowManager.windows.find { it.focused },
                'Anwendung schliessen?',
                'Die Anwendung enthält nicht gespeicherte Projekte.\n\nBitte wählen Sie.',
                1,
                [
                        new CommandLink('Alles speichern und schliessen', ''),
                        new CommandLink('Abbrechen und nicht schliessen', ''),
                        new CommandLink('Ohne Speichern schliessen', '')
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
                app.windowManager.windows.find { it.focused },
                'Anwendung schliessen?',
                'Möchten Sie die Anwendung wirklich schliessen?',
                1,
                [
                        new CommandLink('Ja, klar!', ''),
                        new CommandLink('Nein, doch nicht.', '')
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
                app.windowManager.windows.find { it.focused },
                'Projekt schliessen?',
                'Das Projekt enthält nicht gespeicherte Werte.\n\nBitte wählen Sie.',
                1,
                [
                        new CommandLink('Speichern und das Projekt schliessen', ''),
                        new CommandLink('Abbrechen und Projekt geöffnet lassen', ''),
                        new CommandLink('OK, nicht speichern und Projekt schliessen', '')
                ]
        )
        return choice
    }

    /**
     * Zeige Informationsdialog mit mitgegebener Nachricht an.
     */
    def showInformDialog = { window = null, infoMsg ->
        //window = window ?: Window.windows.find { it.focused }
        inform(window, 'Information', infoMsg)
    }

    /**
     * Zeige Fehler-Informationsdialog mit mitgegebener Nachricht an.
     */
    def showErrorDialog = { window = null, errorMsg ->
        //window = window ?: Window.windows.find { it.focused }
        inform(window, 'Fehler', errorMsg)
    }

    /**
     * Zeige Informationsdialog für Angebotsverfolgung mit mitgegebener Nachricht an.
     */
    def showCustomInformDialog = { titel, infoMsg ->
        TaskDialogs.inform(app.windowManager.windows.find { it.focused }, titel as String, infoMsg)
    }

}

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

/**
 * Manage and create dialogs.
 */
class DialogController {
	
	/**
	 * Dialog anzeigen, wenn die Applikation geschlossen werden soll, obwohl
	 * noch nicht gespeicherte Projekte vorhanden sind.
	 */
	def showApplicationCloseDialog() {
		def choice = choice(
						"Anwendung schliessen?",
						"Die Anwendung enthält nicht gespeicherte Projekte. Bitte wählen Sie.",
						1,
						[
							new CommandLink("Alle Speichern", ""),
							//new CommandLink("Abbrechen", ""),
							new CommandLink("Ohne Speichern schliessen", "")
						]
					)
		choice
	}
	
	/**
	 * Dialog anzeigen, wenn ein nicht gespeichertes Projekt geschlossen werden soll.
	 */
	def showCloseProjectDialog() {
		def choice = choice(
						"Projekt schliessen?",
						"Das Projekt enthält nicht gespeicherte Werte. Bitte wählen Sie.",
						1,
						[
							new CommandLink("Speichern", ""),
							new CommandLink("Abbrechen", ""),
							new CommandLink("Schliessen", "")
						]
					)
		choice
	}
	
	/**
	 * Zeige Informationsdialog mit mitgegebener Nachricht an.
	 */
	def showInformDialog = { infoMsg ->
		inform("Information", infoMsg)
	}
	
	/**
	 * Zeige Fehler-Informationsdialog mit mitgegebener Nachricht an.
	 */
	def showErrorDialog = { errorMsg ->
		inform("Fehler", errorMsg)
	}

    /**
     * Ticket #97
     * Dialog zum Drucken anzeigen.
     * Ja = Daten an OOo senden
     * Nein = es wird ein Blanko Angebot geöffnet
     */
    def showPrintProjectDialog() {
		def choice = choice(
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
}

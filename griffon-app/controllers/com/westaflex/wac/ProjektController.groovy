/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/controllers/com/westaflex/wac/ProjektController.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import javax.swing.DefaultCellEditor
import javax.swing.JComboBox
import javax.swing.JTable
import javax.swing.table.AbstractTableModel
import javax.swing.event.TableModelListener
import java.awt.Component

/**
 * 
 */
@griffon.util.EventPublisher
class ProjektController {
	
	public static boolean DEBUG = false
	
	def builder
	
	def model
	def view
	
	def wacCalculationService
	def wacModelService
	def projektModelService
	
	def raumBearbeitenDialog
	def wbwDialog
	def teilstreckenDialog
	
	/**
	 * Initialize MVC group.
	 */
	void mvcGroupInit(Map args) {
		// Splash screen
		Wac2Splash.instance.initializingProject()
		// Save MVC id
		model.mvcId = args.mvcId
		// Set defaults
		setDefaultValues()
		// Add PropertyChangeListener to our model.meta
		//GH.addMapPropertyChangeListener("meta", model.meta)
		// Add PropertyChangeListener to our model.map
		GH.addMapPropertyChangeListener("map", model.map, { evt ->
				// Only set dirty flag, when modified property is not the dirty flag
				// Used for loading and saving
				if (evt.propertyName != "dirty" && !model.map.dirty) {
					// Dirty-flag im eigenen und Wac2Model setzen
					model.map.dirty = true
					app.models["wac2"].aktivesProjektGeandert = true
					// Change tab title (show a star)
					setTabTitle()
				}
			})
		// Setup private event listener
		// This properties are used with constructor of the event listener
		def props = [
				model: model,
				wacCalculationService: wacCalculationService,
				wacModelService: wacModelService
			]
		GH.tieEventListener(this, ProjektEvents, props)
		GH.tieEventListener(this, GebaudeEvents, props)
		GH.tieEventListener(this, RaumEvents, props)
		GH.tieEventListener(this, AussenluftVsEvents, props)
		GH.tieEventListener(this, RaumVsEvents, props)
		GH.tieEventListener(this, DvbKanalnetzEvents, props)
		GH.tieEventListener(this, DvbVentileinstellungEvents, props)
	}
	
	/**
	 * Setze Standardwerte (meist in Comboboxen).
	 */
	def setDefaultValues() {
		// Reference meta values
		model.meta = app.models["wac2"].meta
		// Raumvolumenströme, Zentralgerät
		model.map.anlage.zentralgerat = model.meta.zentralgerat[0]
		// Raumvolumenströme, Volumenstrom des Zentralgeräts; default ist erster Wert der Liste
		model.map.anlage.volumenstromZentralgerat = model.meta.volumenstromZentralgerat[0]
		// Druckverlustberechnung - Kanalnetz - Kanalbezeichnung
		model.map.dvb.kanalbezeichnung = model.meta.dvbKanalbezeichnung
		// Druckverlustberechnung - Ventileinstellung - Ventilbezeichnung
		// TODO Why this? model.map.dvb.ventileinstellung = model.meta.dvbVentileinstellung[]
	}
	
	/**
	 * Titel für dieses Projekt erstellen: Bauvorhaben, ansonsten MVC ID.
	 */
	def getProjektTitel = {
		def title = new StringBuilder()
		// Bauvorhaben
		def bauvorhaben = model.map.kundendaten.bauvorhaben
		if (bauvorhaben) {
			title << "Projekt - ${bauvorhaben}"
		} else {
			title << model.mvcId
		}
		title
	}
	
	/**
	 * Titel der Tab für dieses Projekt erstellen, und Sternchen für ungesicherte Änderungen anhängen.
	 */
	def makeTabTitle = {
		def tabTitle = getProjektTitel()
		// Dateiname des Projekts oder MVC ID
		tabTitle << " (${model.wpxFilename ?: view.mvcId})"
		// Ungespeicherte Daten?
		if (model.map.dirty) tabTitle << "*"
		//
		tabTitle
	}
	
	/**
	 * Titel des Projekts für Tab setzen.
	 */
	def setTabTitle() {
		view.projektTabGroup.setTitleAt(view.projektTabGroup.selectedIndex, makeTabTitle().toString())
	}
	
	/**
	 * Can we close? Is there unsaved data -- is our model dirty?
	 */
	boolean canClose() {
		model.map.dirty == false
	}
	
	/**
	 * TODO mmu UNUSED?
	 * Dialog anzeigen, wenn ein nicht gespeichertes Projekt geschlossen wird.
	def closeProjectTab = { evt = null ->
		if (DEBUG) println "closeProjectTab: closeTab=${app.controllers}"
		def choice = app.controllers["Dialog"].showCloseProjectDialog()
		if (DEBUG) println "closeProjectTab: choice=${choice}"
		// TODO rbe
		switch (choice) {
			case 0: // Save: save the closing project
				if (DEBUG) println "closeProjectTab: choice -> saving project"
				// Return boolean value from save()
				save()
				break
			case 1: // Close: just close the tab...
				if (DEBUG) println "closeProjectTab: choice -> do nothing"
				break
			case 2: // Cancel: do nothing...
				if (DEBUG) println "closeProjectTab: choice -> closing project"
				break
		}
	}
	 */
	
	/**
	 * 
	 */
	def afterLoading = {
		if (DEBUG) println "afterLoading: fire RaumHinzufugen"
		// Räume
		model.map.raum.raume.each { raum ->
			publishEvent "RaumHinzugefugt", [raum.position]
		}
		// HACK
		try { Thread.sleep(500) } catch (e) {}
		model.resyncRaumTableModels()
		if (DEBUG) println "afterLoading: setting model.map.dirty to false"
		// Set dirty-flag in project's model to false
		model.map.dirty = false
		// Update tab title to ensure that no "unsaved-data-star" is displayed
		setTabTitle()
		// Splash screen
		if (DEBUG) println "projektOffnen: disposing splashscreen"
		Wac2Splash.instance.dispose()
	}
	
	/**
	 * Save this project.
	 * @return Boolean Was project successfully saved to a file?
	 */
	def save = {
		if (DEBUG) println "save: saving project '${getProjektTitel()}' in file ${model.wpxFilename?.dump()}"
		try {
			if (model.wpxFilename) {
				// Save data
				projektModelService.save(model.map, model.wpxFilename)
				// Set dirty-flag in project's model to false
				model.map.dirty = false
				// Update tab title to ensure that no "unsaved-data-star" is displayed
				setTabTitle()
				// Project was saved
				true
			} else {
				// Project was not saved
				false
			}
		} catch (e) {
            def errorMsg = e.printStackTrace()
            app.controllers["Dialog"].showErrorDialog(errorMsg as String)
			// Project was not saved
			false
		}
	}
	
	/**
	 * Gebäudedaten - Geometrie wurde manuell eingegeben.
	 */
	def berechneGeometrie = {
		// Read values from view and transfer them into our model
		def g = [
			wohnflache: view.gebaudeGeometrieWohnflache.text.toDouble2(),
			raumhohe: view.gebaudeGeometrieMittlereRaumhohe.text.toDouble2(),
			gelufteteFlache: view.gebaudeGeometrieGelufteteFlache.text.toDouble2()
		]
		// Write values into model
		model.map.gebaude.geometrie.wohnflache = g.wohnflache
		model.map.gebaude.geometrie.raumhohe = g.raumhohe
		model.map.gebaude.geometrie.gelufteteFlache = g.gelufteteFlache
		//
		publishEvent "GeometrieEingegeben"
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def luftdichtheitKategorieA = {
		doLater {
			model.map.gebaude.luftdichtheit.with {
				druckdifferenz = 2.0d
				luftwechsel = 1.0d
				druckexponent = 0.666f
			}
		}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def luftdichtheitKategorieB = {
		doLater {
			model.map.gebaude.luftdichtheit.with {
				druckdifferenz = 2.0d
				luftwechsel = 1.5f
				druckexponent = 0.666f
			}
		}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def luftdichtheitKategorieC = {
		doLater {
			model.map.gebaude.luftdichtheit.with {
				druckdifferenz = 2.0d
				luftwechsel = 2.0d
				druckexponent = 0.666f
			}
		}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
	def speichereLuftdichtheit = {
		doLater {
			model.map.gebaude.luftdichtheit.druckdifferenz = view.gebaudeLuftdichtheitDruckdifferenz.text.toDouble2()
			model.map.gebaude.luftdichtheit.luftwechsel = view.gebaudeLuftdichtheitLuftwechsel.text.toDouble2()
			model.map.gebaude.luftdichtheit.druckexponent = view.gebaudeLuftdichtheitDruckexponent.text.toDouble2()
		}
	}
	
	/**
	 * Gebäudedaten - Faktor für besondere Anforderungen
	 */
	def speichereFaktorBesondereAnforderungen = {
		doLater {
			model.map.gebaude.faktorBesondereAnforderungen = view.faktorBesondereAnforderungen.text.toDouble2()
		}
	}
	
	/**
	 * Gebäudedaten - Geplante Belegung
	 */
	def berechneMindestaussenluftrate = {
		doLater {
			model.map.gebaude.geplanteBelegung.with {
				try {
					mindestaussenluftrate = personenanzahl * aussenluftVsProPerson
				} catch (e) {
					def errorMsg = e.printStackTrace()
					app.controllers["Dialog"].showErrorDialog(errorMsg as String)
					mindestaussenluftrate = 0.0d
				}
			}
			publishEvent "AussenluftVsBerechnen"
		}
	}
	
	/**
	 * Anlagendaten - Energie-Kennzeichen
	 */
	def berechneEnergieKennzeichen = {
		doLater {
			model.map.anlage.energie.with {
				if (zuAbluftWarme && bemessung && ruckgewinnung && regelung) {
					nachricht = "Energiekennzeichen gesetzt!"
				} else {
					nachricht = " "
				}
			}
			berechneKennzeichenLuftungsanlage()
		}
	}
	
	/**
	 * Anlagendaten - Hygiene-Kennzeichen
	 */
	def berechneHygieneKennzeichen = {
		doLater {
			model.map.anlage.hygiene.with {
				if (ausfuhrung && filterung && keineVerschmutzung && dichtheitsklasseB) {
					nachricht = "Hygienekennzeichen gesetzt!"
				} else {
					nachricht = " "
				}
			}
			berechneKennzeichenLuftungsanlage()
		}
	}
	
	/**
	 * Anlagendaten - Kennzeichen
	 */
	def berechneKennzeichenLuftungsanlage = {
		doLater {
			def gebaudeTyp = model.map.gebaude.efh ? "EFH" : "WE"
			def energieKz = model.map.anlage.energie.nachricht != " " ? "E" : "0"
			def hygieneKz = model.map.anlage.hygiene.nachricht != " " ? "H" : "0"
			def ruckschlag = model.map.anlage.ruckschlagkappe ? "RK" : "0"
			def schallschutz = model.map.anlage.schallschutz ? "S" : "0"
			def feuerstatte = model.map.anlage.feuerstatte ? "F" : "0"
			model.map.anlage.kennzeichnungLuftungsanlage =
				"ZuAbLS-Z-${gebaudeTyp}-WÜT-${energieKz}-${hygieneKz}-${ruckschlag}-${schallschutz}-${feuerstatte}"
		}
	}
	
	/**
	 * Aussenluftvolumenströme - Mit/ohne Infiltrationsanteil berechnen.
	 */
	def berechneAussenluftVs = {
		publishEvent "AussenluftVsBerechnen"
	}
	
	/**
	 * Raumdaten - Raumtyp in Combobox ausgewählt.
	 */
	def raumTypGeandert = {
		doLater {
			switch (view.raumTyp.selectedIndex) {
				// Zulufträume
				case 0..5:
					view.raumLuftart.selectedItem = "ZU"
					switch (view.raumTyp.selectedIndex) {
						case 0:
							view.raumAbluftVs.text = ""
							view.raumZuluftfaktor.text = "3,00"
							break
						case 1..2:
							view.raumAbluftVs.text = ""
							view.raumZuluftfaktor.text = "2,00"
							break
						case 3..5:
							view.raumAbluftVs.text = ""
							view.raumZuluftfaktor.text = "1,50"
							break
					}
					break
				// Ablufträume
				case 6..13:
					view.raumLuftart.selectedItem = "AB"
					switch (view.raumTyp.selectedIndex) {
						case 6..8:
							view.raumZuluftfaktor.text = ""
							view.raumAbluftVs.text = "25"
							break
						case 9..12:
							view.raumZuluftfaktor.text = ""
							view.raumAbluftVs.text = "45"
							break
						case 13:
							view.raumZuluftfaktor.text = ""
							view.raumAbluftVs.text = "100"
							break
					}
					break
				// Überströmräume
				case { it > 13 }:
					view.raumLuftart.selectedItem = "ÜB"
					view.raumZuluftfaktor.text = ""
					view.raumAbluftVs.text = ""
			}
		}
	}
	
	/**
	 * Raumdaten - Raum anlegen.
	 */
	def raumHinzufugen = {
		// Erstelle Model für Raum: Standardwerte überschreiben mit eingegebenen Werten
		// Berechne Position: Raum wird unten angefügt
		def raum = model.raumMapTemplate +
					GH.getValuesFromView(view, "raum") +
					[position: model.map.raum.raume.size()]
		// Prüfe Toleranzwerte für Zuluftfaktor
		if (raum.raumLuftart ==~ /ZU.*/) {
			def eingegebenerZuluftfaktor = raum.raumZuluftfaktor.toDouble2()
			def (zuluftfaktor, neuerZuluftfaktor) =
				wacCalculationService.prufeZuluftfaktor(raum.raumTyp, eingegebenerZuluftfaktor)
			if (zuluftfaktor != neuerZuluftfaktor) {
				def infoMsg = "Der Zuluftfaktor wird von ${zuluftfaktor} auf ${neuerZuluftfaktor} (laut Norm-Tolerenz) geändert!"
				app.controllers["Dialog"].showInformDialog(infoMsg as String)
				if (DEBUG) println infoMsg
			}
			raum.raumZuluftfaktor = neuerZuluftfaktor
		}
		// Hole Werte für neuen Raum aus der View und füge Raum hinzu
		if (DEBUG) println "raumHinzufugen: publishing event for raum.position=${raum.position}"
		publishEvent "RaumHinzufugen", [raum, view]
	}
	
	/**
	 * Raumdarten - ein Raum wurde geändert.
	 */
	def raumGeandert = {
		publishEvent "RaumGeandert", [view.raumTabelle.selectedRow]
	}
	
	/**
	 * Raumdaten - einen Raum entfernen.
	 */
	def raumEntfernen = {
		publishEvent "RaumEntfernen", [view.raumTabelle.selectedRow, view]
	}
	
	/**
	 * Raumdaten - einen Raum kopieren.
	 */
	def raumKopieren = {
		doLater {
			// Get selected row
			def row = view.raumTabelle.selectedRow
			// Raum anhand seiner Position finden und eine Kopie erzeugen
			def x = model.map.raum.raume.find { it.position == row }.clone()
			// Neuen Namen und neue Position (Ende) setzen
			x.raumBezeichnung = "Kopie von ${x.raumBezeichnung}"
			x.position = model.map.raum.raume.size()
			// Raum zum Model hinzufügen
			model.map.raum.raume.add(x) // TODO Call ProjektModel.addRaum()??
			// Raum hinzugefügt
			publishEvent "RaumHinzugefugt", [view.raumTabelle.rowCount - 1]
		}
	}
	
	/**
	 * Raumdaten - einen Raum in der Tabelle nach oben verschieben.
	 */
	def raumNachObenVerschieben = {
		doLater {
			// Get selected row
			def row = view.raumTabelle.selectedRow
			if (row > 0) {
				// Recalculate positions
				model.map.raum.raume.each {
					if (it.position == row - 1) it.position += 1
					else if (it.position == row) it.position -= 1
				}
				model.resyncRaumTableModels()
				// Raum geändert
				publishEvent "RaumGeandert", [row - 1]
			}
		}
	}
	
	/**
	 * Raumdaten - einen Raum in der Tabelle nach oben verschieben.
	 */
	def raumNachUntenVerschieben = {
		doLater {
			// Get selected row
			def row = view.raumTabelle.selectedRow
			if (row < view.raumTabelle.rowCount - 1) {
				// Recalculate positions
				model.map.raum.raume.each {
					if (it.position == row + 1) it.position -= 1
					else if (it.position == row) it.position += 1
				}
				model.resyncRaumTableModels()
				// Raum geändert
				publishEvent "RaumGeandert", [row - 1]
			}
		}
	}
	
	/**
	 * Raumdaten - einen Raum bearbeiten.
	 */
	def raumBearbeiten = {
		// Get selected row
		def row = view.raumTabelle.selectedRow
		if (row > -1) {
			// Show dialog
			raumBearbeitenDialog = GH.createDialog(builder, RaumBearbeitenView, [title: "Raum bearbeiten", pack: true])
			raumBearbeitenDialog.show()
			if (DEBUG) println "raumBearbeiten: dialog '${raumBearbeitenDialog.title}' closed: dialog=${raumBearbeitenDialog.dump()}"
			// Berechne alles, was von Räumen abhängt
			publishEvent "RaumGeandert", [row]
		}
	}
	
	/**
	 * RaumBearbeiten - RaumBearbeitenView schliessen.
	 */
	def raumBearbeitenSchliessen = {
		if (DEBUG) println "raumBearbeitenSchliessen -> closing dialog: raumBearbeitenDialog=$raumBearbeitenDialog"
		raumBearbeitenDialog.dispose()
	}
	
	/**
	 * Raumvolumenströme - Zu/Abluftventile geändert.
	 */
	def raumZuAbluftventileGeandert = {
		def raumIndex = view.raumVsZuAbluftventileTabelle.selectedRow
		if (DEBUG) println "raumZuAbluftventileGeandert: raumIndex=${raumIndex}"
		if (raumIndex > -1) {
			wacCalculationService.berechneZuAbluftventile(model.map.raum.raume[raumIndex])
			//publishEvent "RaumZuAbluftventileLuftmengeBerechnen", [raumIndex]
		} else {
			if (DEBUG) println "raumZuAbluftventileGeandert: Kein Raum ausgewählt, es wird nichts berechnet"
		}
	}
	
	/**
	 * Raumvolumenströme - Überströmelemente geändert.
	 */
	def raumUberstromelementeGeandert = {
		def raumIndex = view.raumVsUberstromelementeTabelle.selectedRow
		if (raumIndex > -1) {
			wacCalculationService.berechneUberstromelemente(model.map.raum.raume[raumIndex])
			//publishEvent "RaumUberstromelementeLuftmengeBerechnen", [raumIndex]
		} else {
			if (DEBUG) println "raumUberstromelementeLuftmengeBerechnen: Kein Raum ausgewählt, es wird nichts berechnet"
		}
	}
	
	/**
	 * TODO Raum bearbeiten - Daten eingegeben.
	 */
	def raumBearbeitenGeandert = {
		if (DEBUG) println "TODO raumBearbeitenGeandert"
	}
	
	/**
	 * In einer der Raum-Tabellen wurde die Auswahl durch den Benutzer geändert:
	 * die Auswahl aller anderen Tabellen entsprechend anpassen.
	 * @param evt javax.swing.event.ListSelectionEvent
	 */
	def raumInTabelleGewahlt = { evt, table ->
		if (!evt.isAdjusting && evt.firstIndex > -1 && evt.lastIndex > -1) {
			// source = javax.swing.ListSelectionModel
			def selectedRow = evt.source.leadSelectionIndex
			//if (DEBUG) println "raumInTabelleGewahlt: ${evt.dump()}, selectedRow=${selectedRow}"
			onRaumInTabelleWahlen(selectedRow, table)
		}
	}
	
	/**
	 * Execute code with all "Raum"-tables...
	 */
	def withAllRaumTables = { closure ->
		view.with {
			[raumTabelle, raumVsZuAbluftventileTabelle, raumVsUberstromelementeTabelle].each { t ->
				closure(t)
			}
		}
	}
	
	/**
	 * Einen bestimmten Raum in allen Raum-Tabellen markieren.
	 */
	def onRaumInTabelleWahlen = { row, table = null ->
		doLater {
			//if (DEBUG) println "onRaumInTabelleWahlen: row=${row}"
			row = GH.checkRow(row, view.raumTabelle)
			if (row > -1) {
				// Raum in Raumdaten-Tabelle, Raumvolumenströme-Zu/Abluftventile-Tabelle,
				// Raumvolumenströme-Überströmelemente-Tabelle markieren
				withAllRaumTables { t ->
					GH.withDisabledListSelectionListeners t, { -> changeSelection(row, 0, false, false) }
				}
				// Aktuellen Raum in Metadaten setzen
				model.meta.gewahlterRaum.putAll(model.map.raum.raume[row])
				model.meta.gewahlterRaum.raumNummer = row + 1
			} else {
				// Remove selection in all tables
				withAllRaumTables { t ->
					t.clearSelection()
				}
				// Aktuell gewählten Raum in Metadaten leeren
				model.meta.gewahlterRaum.clear()
			}
		}
	}
	
	/**
	 * Raumvolumenströme - Zentralgerät: manuelle Auswahl des Zentralgeräts.
	 */
	def zentralgeratGewahlt = {
		publishEvent "ZentralgeratGewahlt", [view.raumVsZentralgerat.selectedItem]
	}
	
	/**
	 * Raumvolumenströme - Zentralgerät: das Zentralgerat wurde geändert.
	 */
	def onZentralgeratGeandert = {
		// Füge Volumenströme des aktuellen Zentralgeräts in Combobox hinzu
		view.raumVsVolumenstrom.removeAllItems()
		model.meta.volumenstromZentralgerat.each { view.raumVsVolumenstrom.addItem(it) }
	}
	
	/**
	 * Raumvolumenströme - Zentralgerät: automatische Aktualisierung das Zentralgeräts.
	 * Darf nur dann durchgeführt werden, wenn der Benutzer das Zentralgerät noch nicht selbst
	 * verändert hat!
	 */
	def onZentralgeratAktualisieren = {
		doLater {
			if (DEBUG) println "onZentralgeratAktualisieren: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
			if (!model.map.anlage.zentralgeratManuell) {
				def (zentralgerat, nl) = wacCalculationService.berechneZentralgerat(model.map)
				if (DEBUG) println "onZentralgeratAktualisieren: zentralgerat=${zentralgerat}," +
					"nl=${nl}/${wacCalculationService.round5(nl)}"
				// Aktualisiere Zentralgerät
				GH.withDisabledActionListeners view.raumVsZentralgerat, {
					model.map.anlage.zentralgerat =
						view.raumVsZentralgerat.selectedItem =
						zentralgerat
				}
				// Aktualisiere Volumenstrom
				GH.withDisabledActionListeners view.raumVsVolumenstrom, {
					view.raumVsVolumenstrom.removeAllItems()
					// Hole Volumenströme des Zentralgeräts
					model.meta.volumenstromZentralgerat =
						wacModelService.getVolumenstromFurZentralgerat(view.raumVsZentralgerat.selectedItem)
					// Füge Volumenströme in Combobox hinzu
					model.meta.volumenstromZentralgerat.each { view.raumVsVolumenstrom.addItem(it) }
					// Selektiere errechneten Volumenstrom
					def i = wacCalculationService.round5(nl)
					model.map.anlage.volumenstromZentralgerat =
						view.raumVsVolumenstrom.selectedItem =
						model.meta.volumenstromZentralgerat.find { it == i }
				}
			}
		}
	}
	
	/**
	 * Raumvolumenströme - Volumenstrom des Zentralgeräts.
	 */
	def volumenstromZentralgeratGewahlt = {
		// Im Projekt-Model speichern
		model.map.anlage.volumenstromZentralgerat = view.raumVsVolumenstrom.selectedItem?.toInteger()
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz - Hinzufügen.
	 */
	def dvbKanalnetzHinzufugen = {
		def kanalnetz = GH.getValuesFromView(view, "dvbKanalnetz")
		publishEvent "DvbKanalnetzHinzufugen", [kanalnetz, view]
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz wurde hinzugefügt: Eintrag in der Tabelle anwählen.
	 */
	def onDvbKanalnetzInTabelleWahlen = { kanalnetzIndex ->
		doLater {
			view.dvbKanalnetzTabelle.changeSelection(kanalnetzIndex, 0, false, false)
		}
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz - Geändert.
	 */
	def dvbKanalnetzGeandert = { kanalnetzIndex ->
		publishEvent "DvbKanalnetzGeandert", [kanalnetzIndex]
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz - Entfernen.
	 */
	def dvbKanalnetzEntfernen = {
		publishEvent "DvbKanalnetzEntfernen", [view.dvbKanalnetzTabelle.selectedRow]
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz - Widerstandsbeiwerte.
	 */
	def widerstandsbeiwerteBearbeiten = {
		// Welche Teilstrecke ist ausgewählt? Index bestimmen
		def index = view.dvbKanalnetzTabelle.selectedRow
		println "widerstandsbeiwerteBearbeiten: index=${index}"
		// TableModel für WBW hinzufügen, wenn noch nicht vorhanden
		model.addWbwTableModel(index)
		model.meta.dvbKanalnetzGewahlt = index
		// WBW summieren, damit das Label im Dialog (bind model.meta.summeAktuelleWBW) den richtigen Wert anzeigt
		wbwSummieren()
		// Show dialog
		wbwDialog = GH.createDialog(builder, WbwView, [title: "Widerstandsbeiwerte", size: [750, 650]])
		wbwDialog.show()
		if (DEBUG) println "widerstandsbeiwerteBearbeiten: dialog '${dialog.title}'"
	}
	
	/**
	 * Ein Widerstandsbeiwert wurde in der Tabelle gewählt:
	 * Bild anzeigen und Daten in die Eingabemaske kopieren.
	 */
	def wbwInTabelleGewahlt = { evt = null ->
		// Welche Teilstrecke ist ausgewählt? Index bestimmen
		def index = model.meta.dvbKanalnetzGewahlt //view.dvbKanalnetzTabelle.selectedRow
		// Welche Zeile ist gewählt --> welcher Widerstand?
		def wbwIndex = view.wbwTabelle.selectedRow
		println "wbwInTabelleGewahlt: index=${index} wbwIndex=${wbwIndex}"
		def wbw = model.tableModels.wbw[index][wbwIndex]
		javax.swing.ImageIcon image = new javax.swing.ImageIcon(Wac2Resource.getWiderstandUrl(wbw.id))
		// Image und Text setzen
		if (image) {
			view.wbwBild.text = ""
			view.wbwBild.icon = image
		} else {
			view.wbwBild.text = "-- kein Bild --"
			view.wbwBild.icon = null
		}
		// Daten in Eingabemaske setzen
		view.wbwBezeichnung.text = wbw.name
		view.wbwWert.text = wbw.widerstandsbeiwert.toString2(0)
		view.wbwAnzahl.text = wbw.anzahl
	}
	
	/**
	 * Widerstandsbeiwerte, Übernehmen-Button.
	 */
	def wbwSaveButton = {
		// Welche Teilstrecke ist ausgewählt? Index bestimmen
		def index = model.meta.dvbKanalnetzGewahlt //view.dvbKanalnetzTabelle.selectedRow
		println "wbwSaveButton: index=${index}"
		def wbw = model.tableModels.wbw[index]
		// Daten aus der Eingabemaske holen
		def dialogWbw = [
			name: view.wbwBezeichnung.text,
			widerstandsbeiwert: view.wbwWert.text?.toDouble2() ?: 0.0d,
			anzahl: view.wbwAnzahl.text?.toInteger() ?: 0
		]
		// Wenn WBW noch nicht vorhanden, dann hinzufügen
		if (!wbw.find { it.name == dialogWbw.name }) {
			println "wbwSaveButton: adding ${dialogWbw.dump()} to model"
			wbw << dialogWbw
			model.resyncWbwTableModels()
		}
	}
	
	/**
	 * Widerstandsbeiwerte: eingegebene Werte aufsummieren.
	 */
	def wbwSummieren = {
		// Welche Teilstrecke ist ausgewählt? Index bestimmen
		def index = model.meta.dvbKanalnetzGewahlt //view.dvbKanalnetzTabelle.selectedRow
		println "wbwSummieren: index=${index}"
		def wbw = model.tableModels.wbw[index]
		// Summiere WBW
		def map = model.map.dvb.kanalnetz[index]
		model.meta.summeAktuelleWBW = map.gesamtwiderstandszahl =
			wbw.sum {
				it.anzahl.toDouble2() * it.widerstandsbeiwert.toDouble2()
			}
		println "wbwSummieren: map.gesamtwiderstandszahl=${map.gesamtwiderstandszahl}"
	}
	
	/**
	 * Widerstandsbeiwerte, Dialog mit OK geschlossen.
	 */
	def wbwOkButton = {
		// Welche Teilstrecke ist ausgewählt? Index bestimmen
		def index = model.meta.dvbKanalnetzGewahlt //view.dvbKanalnetzTabelle.selectedRow
		println "wbwOkButton: index=${index}"
		def wbw = model.tableModels.wbw[index]
		def map = model.map.dvb.kanalnetz[index]
		// Berechne Teilstrecke
		wbwSummieren()
		wacCalculationService.berechneTeilstrecke(map)
		// Resync model
		model.resyncDvbKanalnetzTableModels()
		// Close dialog
		wbwDialog.dispose()
	}
	
	/**
	 * 
	 */
	def wbwCancelButton = {
		// Close dialog
		wbwDialog.dispose()
	}
	
	/**
	 * Druckverlustberechnung - Ventileinstellung - Hinzufügen.
	 */
	def dvbVentileinstellungHinzufugen = {
		def ventileinstellung = GH.getValuesFromView(view, "dvbVentileinstellung")
		publishEvent "DvbVentileinstellungHinzufugen", [ventileinstellung, view]
	}
	
	/**
	 * Druckverlustberechnung - Ventileinstellung wurde hinzugefügt: Eintrag in der Tabelle anwählen.
	 */
	def onDvbVentileinstellungInTabelleWahlen = { ventileinstellungIndex ->
		doLater {
			view.dvbVentileinstellungTabelle.changeSelection(ventileinstellungIndex, 0, false, false)
		}
	}
	
	/**
	 * Druckverlustberechnung - Ventileinstellung - Geändert.
	 */
	def dvbVentileinstellungGeandert = { ventileinstellungIndex ->
		publishEvent "DvbVentileinstellungGeandert", [ventileinstellungIndex]
	}
	
	/**
	 * Druckverlustberechnung - Ventileinstellung - Entfernen.
	 */
	def dvbVentileinstellungEntfernen = {
		publishEvent "DvbVentileinstellungEntfernen", [view.dvbVentileinstellungTabelle.selectedRow]
	}
	
	/**
	 * Druckverlustberechnung - Ventileinstellung - Teilstrecke wählen.
	 */
	def dvbVentileinstellungTeilstreckeDialog = {
		teilstreckenDialog = GH.createDialog(builder, TeilstreckenView)
		teilstreckenDialog.show()
		if (DEBUG) println "TeilstreckenAuswahlen: dialog '${dialog.title}' closed: dialog=${dialog.dump()}"
	}
	
	/**
	 * Teilstrecken, Dialog mit OK geschlossen.
	 */
	def teilstreckenOkButton = {
	}
	
	/**
	 *
	 */
	def teilstreckenCancelButton = {
	}
	
	/**
	 * TODO UNUSED!!!?
	 * Raumvolumenströme - Zu-/Abluftventile:
	 * Raumbezeichnung Comboboxen in der RaumdatenView und RaumVsView aktualisieren
	def updateRaumBezeichnungCombo = { rowIndex, newValue ->
		// Neues TableModel setzen !
		if (DEBUG) println "updateRaumBezeichnungCombo: ${rowIndex}"
		doLater {
			model.resyncRaumTableModels()
		}
	}
	 */
	
	/**
	 * TODO UNUSED!!!?
	 * Raumvolumenströme - Zu-/Abluftventile:
	 * Luftart-Comboboxen in der RaumdatenView und RaumVsView aktualisieren
	def updateRaumLuftartCombo = { rowIndex, newValue ->
		// Neues TableModel setzen !
		if (DEBUG) println "updateRaumLuftartCombo: add row to table model ${rowIndex}"
		doLater {
			model.resyncRaumTableModels()
		}
	}
	 */
	
	/**
	 * TODO UNUSED!!!?
	 * Raumvolumenströme - Zu-/Abluftventile:
	 * Combobox für eines der Ventile geändert.
	def updateRaumVentile = { rowIndex ->
		if (DEBUG) println "-" * 80
		if (DEBUG) println "updateRaumVentile: model.map.raum.raume=${model.map.raum.raume}"
		try {
			// Raum holen
			def r = model.map.raum.raume[rowIndex]
			if (DEBUG) println "updateRaumVentile: row#${rowIndex} changed, raum=${r.dump()}"
			// Werte aus Tabelle übertragen
			def tableModel = view.raumVsZuAbluftventileTabelle.model
			def row = tableModel.rows.get(rowIndex)
			if (DEBUG) println row
			// Berechnen und neue Werte im Model speichern
			model.map.raum.raume[rowIndex] = wacCalculationService.berechneZuAbluftventile(r)
			// Werte in Tabelle übertragen
			////REMOVE tableModel.setValueAt("100ULC", rowIndex, 8)
			//
			//tableModel.fireTableDataChanged()
		} catch (NullPointerException e) {}
		if (DEBUG) println "-" * 80
	}
	 */
	
	/**
	 * Akustikberechnung - Zentralgerät geändert.
	 */
	def aktualisiereAkustikVolumenstrom = { tabname ->
		def zg = view."akustik${tabname}${tabname}stutzenZentralgerat"
		def p = view."akustik${tabname}Pegel"
		// Aktualisiere Volumenstrom
		GH.withDisabledActionListeners p, {
			p.removeAllItems()
			// Hole Volumenströme des Zentralgeräts und füge diese in Combobox hinzu
			wacModelService.getVolumenstromFurZentralgerat(zg.selectedItem).each { p.addItem(it) }
		}
	}
	
	/**
	 * Akustikberechnung.
	 */
	def berechneAkustik = { tabname ->
		def m = model.map.akustik."${tabname.toLowerCase()}"
		// Konvertiere Wert TextField, ComboBox in Integer, default ist 0
		// Eingabe einer 0 im TextField gibt ""???
		def getInt = { comp ->
			def x
			if (comp instanceof javax.swing.JTextField) {
				x = comp.text
			} else if (comp instanceof javax.swing.JComboBox) {
				x = comp.selectedItem
			}
			if (x == "") x = null
			x?.toInteger() ?: 0
		}
		// Input parameter map
		def input = [
				zentralgerat: view."akustik${tabname}${tabname}stutzenZentralgerat".selectedItem,
				volumenstrom: getInt(view."akustik${tabname}Pegel"),
				slpErhohungKanalnetz: getInt(view."akustik${tabname}Kanalnetz"),
				slpErhohungFilter: getInt(view."akustik${tabname}Filter"),
				hauptschalldampfer1: view."akustik${tabname}1Hauptschalldampfer".selectedItem,
				hauptschalldampfer2: view."akustik${tabname}2Hauptschalldampfer".selectedItem,
				umlenkungen: getInt(view."akustik${tabname}AnzahlUmlenkungen90GradStck"),
				luftverteilerkasten: getInt(view."akustik${tabname}LuftverteilerkastenStck"),
				langsdampfungKanal: view."akustik${tabname}LangsdampfungKanal".selectedItem,
				langsdampfungKanalLfdmMeter: getInt(view."akustik${tabname}LangsdampfungKanalLfdmMeter"),
				schalldampferVentil: view."akustik${tabname}SchalldampferVentil".selectedItem,
				einfugungsdammwert: view."akustik${tabname}EinfugungsdammwertLuftdurchlass".selectedItem,
				raumabsorption: getInt(view."akustik${tabname}Raumabsorption")
			]
		if (input.zentralgerat) {
			// Volumenstrom gesetzt?
			if (input.volumenstrom == 0) {
				input.volumenstrom = 50
				view."akustik${tabname}Pegel".selectedItem = model.meta.volumenstromZentralgerat[0]
			}
			// Berechne Akustik
			wacCalculationService.berechneAkustik(tabname, input, model.map)
			// Zentralgerät, Überschrift
			view."akustik${tabname}${tabname}Zentralgerat".text =
				input.zentralgerat
			// db(A)
			view."akustik${tabname}dbA".text =
				wacModelService.getDezibelZentralgerat(input.zentralgerat, input.volumenstrom, tabname).toString2()
			// Mittlerer Schalldruckpegel
			view."akustik${tabname}MittlererSchalldruckpegel".text =
				m.mittlererSchalldruckpegel?.toString2() ?: 0d.toString2()
			// Resync table
			model.resyncAkustikTableModels()
		}
	}
	
}

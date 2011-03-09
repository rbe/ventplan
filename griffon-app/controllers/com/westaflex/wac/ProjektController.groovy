/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/controllers/com/westaflex/wac/ProjektController.groovy
 * Last modified at 09.03.2011 15:51:21 by rbe
 */

package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import javax.swing.DefaultCellEditor
import javax.swing.JComboBox
import javax.swing.JTable
import javax.swing.table.AbstractTableModel
import javax.swing.event.TableModelListener
import java.awt.Component
import javax.swing.DefaultListModel

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
	def oooService
	
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
				// Show dialog only when property changes
				if (evt.propertyName == "ltm") {
					ltmErforderlichDialog()
				}
				// Only set dirty flag, when modified property is not the dirty flag
				// Used for loading and saving
				else if (evt.propertyName != "dirty" && !model.map.dirty) {
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
		/* Druckverlustberechnung - Kanalnetz - Kanalbezeichnung
		model.map.dvb.kanalbezeichnung = model.meta.dvbKanalbezeichnung
		// Druckverlustberechnung - Ventileinstellung - Ventilbezeichnung
		// TODO Why this? model.map.dvb.ventileinstellung = model.meta.dvbVentileinstellung[]*/
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
	 * 
	 */
	def afterLoading = {
		if (DEBUG) println "afterLoading: fire RaumHinzufugen"
		// Räume
		// ConcurrentModificationException!
		def raume = model.map.raum.raume.clone()
		// Jeden Raum einzeln (ohne Events!) berechnen
		raume.each { raum ->
			model.addRaumTurenModel()
			// Aussenluftvolumenströme berechnen
			//wacCalculationService.aussenluftVs(model.map)
			// Nummern der Räume berechnen
			wacCalculationService.berechneRaumnummer(model.map)
		}
		// Räume: set cell editors
		model.setRaumEditors(view)
		// Gebäude-Geometrie berechnen
		wacCalculationService.geometrieAusRaumdaten(model.map)
		// Zentralgerät bestimmen
		onZentralgeratAktualisieren()
		// Anlagendaten - Kennzeichen
		berechneEnergieKennzeichen()
		berechneHygieneKennzeichen()
		berechneKennzeichenLuftungsanlage()
		// Zu-/Abluftventile
		wacCalculationService.berechneZuAbluftventile(model.map)
		// Jeden Türspalt berechnen
		raume.each { raum ->
			// Überströmelemente
			wacCalculationService.berechneUberstromelemente(raum)
			// Türspalt berechnen
			wacCalculationService.berechneTurspalt(raum)
		}
		println model.map.anlage.fortluft
		//
		model.resyncRaumTableModels()
		// Update tab title to ensure that no "unsaved-data-star" is displayed
		model.map.dirty = false
		setTabTitle()
		// Close splash screen
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
	 * Button "Seitenansicht".
	 */
	def seitenansicht = {
		doOutside {
			// TODO Show informational dialog
			////println model.map
			// TODO mmu Dialog: Daten aus Auslegung, Blanko? Ticket #97.
			def doc = oooService.performAuslegung(false, getProjektTitel(), model.map)
			if (DEBUG) println "projektSeitenansicht: doc=${doc?.dump()}"
			// Open document
			switch (System.getProperty("os.name")) {
				case { it ==~ /Windows.*/ }:
					def program = new java.io.File(System.getenv("OOO_HOME"), "program")
					def cmd = [
						"${program.absolutePath.replace('\\', '/')}/soffice.exe",
						"-nologo", "-nofirststartwizard", "-nodefault",
						"-nocrashreport", "-norestart", "-norestore",
						"-nolockcheck",
						"-writer", "-o \"${doc.absolutePath}\""
					]
					def p = cmd.execute(null, program)
					p.waitFor()
					if (DEBUG) println "${cmd} = ${p.exitValue()}"
					break
				default:
					java.awt.Desktop.desktop.open(doc)
			}
			// TODO Close informational dialog
		}
	}
	
	/**
	 * Aktuelles Projekt drucken
	 */
	def drucken = {
		def choice = app.controllers["Dialog"].showPrintProjectDialog()
        if (DEBUG) println "exitApplication: choice=${choice}"
        switch (choice) {
            case 0:
                if (DEBUG) println "Ja = Daten an OOo senden"
                // TODO rbe: Daten an OOo senden
                break
            case 1: // Cancel: do nothing...
                if (DEBUG) println "Nein = es wird ein Blanko Angebot geöffnet"
                // TODO rbe: Blanko Angebot öffnen
                break
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
			//gelufteteFlache: view.gebaudeGeometrieGelufteteFlache.text.toDouble2()
			gelufteteVolumen: view.gebaudeGeometrieGeluftetesVolumen.text.toDouble2()
		]
		// Write values into model
		model.map.gebaude.geometrie.wohnflache = g.wohnflache
		model.map.gebaude.geometrie.raumhohe = g.raumhohe
		//model.map.gebaude.geometrie.gelufteteFlache = g.gelufteteFlache
		model.map.gebaude.geometrie.gelufteteaVolumen = g.geluftetesVolumen
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
			// Save actual caret position
			def personenanzahlCaretPos = view.gebaudeGeplantePersonenanzahl.editor.textField.caretPosition
			def aussenluftVsProPersonCaretPos = view.gebaudeGeplanteAussenluftVsProPerson.editor.textField.caretPosition
			println "view.gebaudeGeplantePersonenanzahl.caretPos 1=${view.gebaudeGeplantePersonenanzahl.editor.textField.caretPosition}"
			// Hack: model is not updated when KeyListener is used on spinner.editor.textfield
			model.map.gebaude.geplanteBelegung.personenanzahl = view.gebaudeGeplantePersonenanzahl.editor.textField.text?.toInteger() ?: 0
			println "view.gebaudeGeplantePersonenanzahl.caretPos 2=${view.gebaudeGeplantePersonenanzahl.editor.textField.caretPosition}"
			model.map.gebaude.geplanteBelegung.aussenluftVsProPerson = view.gebaudeGeplanteAussenluftVsProPerson.editor.textField.text?.toInteger() ?: 0
			model.map.gebaude.geplanteBelegung.with {
				try {
					mindestaussenluftrate = personenanzahl * aussenluftVsProPerson
				} catch (e) {
					def errorMsg = e.printStackTrace()
					app.controllers["Dialog"].showErrorDialog(errorMsg as String)
					mindestaussenluftrate = 0.0d
				}
			}
			// Set caret to old position; is moved through model update?
			println "view.gebaudeGeplantePersonenanzahl.editor.textField.selectedText=${view.gebaudeGeplantePersonenanzahl.editor.textField.selectedText}"
			view.gebaudeGeplantePersonenanzahl.editor.textField.caretPosition = personenanzahlCaretPos
			view.gebaudeGeplanteAussenluftVsProPerson.editor.textField.caretPosition = aussenluftVsProPersonCaretPos
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
			def ruckschlag = model.map.anlage.ruckschlagklappe ? "RK" : "0"
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
							view.raumAbluftVolumenstrom.text = ""
							view.raumZuluftfaktor.text = "3,00"
							break
						case 1..2:
							view.raumAbluftVolumenstrom.text = ""
							view.raumZuluftfaktor.text = "2,00"
							break
						case 3..5:
							view.raumAbluftVolumenstrom.text = ""
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
							view.raumAbluftVolumenstrom.text = "25"
							break
						case 9..12:
							view.raumZuluftfaktor.text = ""
							view.raumAbluftVolumenstrom.text = "45"
							break
						case 13:
							view.raumZuluftfaktor.text = ""
							view.raumAbluftVolumenstrom.text = "100"
							break
					}
					break
				// Überströmräume
				case { it > 13 }:
					view.raumLuftart.selectedItem = "ÜB"
					view.raumZuluftfaktor.text = ""
					view.raumAbluftVolumenstrom.text = ""
			}
		}
	}
	
	/**
	 * Zeige Dialog "lüftungstechnische Maßnahmen erforderlich."
	 */
	def ltmErforderlichDialog = {
		def infoMsg = model.map.messages.ltm
		app.controllers["Dialog"].showInformDialog(infoMsg as String)
		if (DEBUG) println infoMsg
	}
	
	/**
	 * Raumdaten - Raum anlegen.
	 */
	def raumHinzufugen = {
		// Erstelle Model für Raum: Standardwerte überschreiben mit eingegebenen Werten
		// Berechne Position: Raum wird unten angefügt
		def raum = model.raumMapTemplate.clone() +
					GH.getValuesFromView(view, "raum") +
					[position: model.map.raum.raume.size()]
		// TODO rbe Improve cloning of list with maps: use deep, not shallow copy.
		// raum.turen = model.raumTurenTemplate.clone() as ObservableList
		raum.turen = [
						[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
						[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
						[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
						[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true],
						[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true]
					] as ObservableList
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
			def x = model.map.raum.raume.find { it.position == row }



            def newMap = new ObservableMap()
            x.collect{
                //[turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true]
                // turen separat kopieren, da sonst Abhaengigkeiten zum Originalraum bestehen
                if (it.key == 'turen') {
                    if (DEBUG) println "raumKopieren turen -> key=${it.key} ::: value=${it.value}"
                    def y = it.value
                    if (DEBUG) println "turen dump ---> ${y.dump()}"
                    def turenList = [] as ObservableList
                    y.each() { i ->
                        if (DEBUG) println "list=${i}"
                        def tur = i
                        def turenMap = new ObservableMap()
                        tur.collect{
                            turenMap.put(it.key, it.value)
                        }
                        turenList.add(turenMap)
                    }
                    if (DEBUG) println "turenList -> ${turenList}"
                    newMap.put(it.key, turenList)
                }
                else {
                    newMap.put(it.key, it.value)
                }
                if (DEBUG) println "raumKopieren -> key=${it.key} ::: value=${it.value}"

            }
            if (DEBUG) println "newMap -> ${newMap}"

			// Neuen Namen und neue Position (Ende) setzen
            // raumBezeichnung als String speichern (vorher GString).
			newMap.raumBezeichnung = "Kopie von ${x.raumBezeichnung}".toString()
			newMap.position = model.map.raum.raume.size()
			// Raum zum Model hinzufügen
            
			model.addRaum(newMap, view, true)
			// Raum hinzugefügt
			publishEvent "RaumHinzugefugt", [newMap.position, view]
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
				publishEvent "RaumGeandert", [row + 1]
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
			// Modify TableModel for Turen
			def columnModel = view.raumBearbeitenTurenTabelle.columnModel
			// TODO Move values into model.meta
			GH.makeComboboxCellEditor columnModel.getColumn(0), ["Tür", "Durchgang"]
			GH.makeComboboxCellEditor columnModel.getColumn(1), [610, 735, 860, 985, 1110, 1235, 1485, 1735, 1985]
			raumBearbeitenDialog.show()
		}
	}
	
	/**
	 * RaumBearbeiten - RaumBearbeitenView schliessen.
	 */
	def raumBearbeitenSchliessen = {
		if (DEBUG) println "raumBearbeitenSchliessen: closing dialog '${raumBearbeitenDialog.title}'"
		raumBearbeitenDialog.dispose()
		// Berechne alles, was von Räumen abhängt
		publishEvent "RaumGeandert", [view.raumTabelle.selectedRow]
	}
	
	/**
	 * TODO Raum bearbeiten - Daten eingegeben.
	 */
	def raumBearbeitenGeandert = {
		if (DEBUG) println "TODO raumBearbeitenGeandert"
	}
	
	/**
	 * 
	 */
	def berechneTuren = { raumIndex = null ->
		// Hole gewählten Raum
		def raum = model.map.raum.raume[raumIndex ?: view.raumTabelle.selectedRow]
		// Türen berechnen?
		if (raum.turen.findAll { it.turBreite > 0 }?.size() > 0 && raum.raumUberstromVolumenstrom) {
			wacCalculationService.berechneTurspalt(raum)
		}
	}

    /**
     * Tur Werte entfernen in Raum bearbeiten Dialog
     */
    def raumBearbeitenTurEntfernen = { raumIndex = null ->
        if (DEBUG) println "raumBearbeitenTurEntfernen: view.raumBearbeitenTurenTabelle.selectedRow -> ${view.raumBearbeitenTurenTabelle.selectedRow}"
        def turenIndex = view.raumBearbeitenTurenTabelle.selectedRow
        try
        {
            def rowIndex = model.meta.gewahlterRaum.position
            def raum = model.map.raum.raume[rowIndex]
            raum.turen[turenIndex] = [turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true]
            if (DEBUG) println "raumBearbeitenTurEntfernen: ${raum}"
        }
        catch (e) {}
        model.resyncRaumTableModels()
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
	def zentralgeratManuellGewahlt = {
		////publishEvent "ZentralgeratGewahlt", [view.raumVsZentralgerat.selectedItem]
		doLater {
			// Merken, dass das Zentralgerät manuell ausgewählt wurde
			// -> keine automatische Auswahl des Zentralgeräts mehr durchführen
			model.map.anlage.zentralgeratManuell = true
			// Zentralgerät aus View in Model übernehmen
			model.map.anlage.zentralgerat = view.raumVsZentralgerat.selectedItem
			// Hole Volumenströme des Zentralgeräts
			model.meta.volumenstromZentralgerat =
				wacModelService.getVolumenstromFurZentralgerat(model.map.anlage.zentralgerat)
			// Aussenluftvolumenströme neu berechnen
			wacCalculationService.aussenluftVs(model.map)
			// Neue Auswahl setzen
			zentralgeratAktualisieren()
		}
	}
	
	/**
	 * Raumvolumenströme - Zentralgerät: das Zentralgerat wurde geändert.
	def onZentralgeratGeandert = {
	}
	 */
	
	/**
	 * Aktualisiere Zentralgerät und Volumenstrom in allen Comboboxen
	 */
	def zentralgeratAktualisieren = {
		doLater {
			println "zentralgeratAktualisieren"
			/*
			// Füge Volumenströme des aktuellen Zentralgeräts in Combobox hinzu
			// Raumvolumenströme
			view.raumVsVolumenstrom.removeAllItems()
			// Akustikberechnung Zu-/Abluft
			view.akustikZuluftPegel.removeAllItems()
			view.akustikAbluftPegel.removeAllItems()
			model.meta.volumenstromZentralgerat.each {
				// Raumvolumenströme
				view.raumVsVolumenstrom.addItem(it)
				// Akustikberechnung Zu-/Abluft
				view.akustikZuluftPegel.addItem(it)
				view.akustikAbluftPegel.addItem(it)
			}
			// Akustikberechnung Zu-/Abluft
			view.akustikZuluftZuluftstutzenZentralgerat.selectedItem =
				view.akustikAbluftAbluftstutzenZentralgerat.selectedItem =
				model.meta.volumenstromZentralgerat
			*/
			// Aktualisiere Zentralgerät
			GH.withDisabledActionListeners view.raumVsZentralgerat, {
				// Raumvolumenströme
				model.map.anlage.zentralgerat =
					view.raumVsZentralgerat.selectedItem =
					model.map.anlage.zentralgerat
				// Akustik Zu-/Abluft
				view.akustikZuluftZuluftstutzenZentralgerat.selectedItem =
					view.akustikAbluftAbluftstutzenZentralgerat.selectedItem =
					model.map.anlage.zentralgerat
			}
			// Aktualisiere Volumenstrom
			GH.withDisabledActionListeners view.raumVsVolumenstrom, {
				// Hole Volumenströme des Zentralgeräts
				def volumenstromZentralgerat =
					wacModelService.getVolumenstromFurZentralgerat(view.raumVsZentralgerat.selectedItem)
				// 5er-Schritte
				model.meta.volumenstromZentralgerat = []
				def minVsZentralgerat = volumenstromZentralgerat[0] as Integer
				def maxVsZentralgerat = volumenstromZentralgerat.toList().last() as Integer
				(minVsZentralgerat..maxVsZentralgerat).step 5, { model.meta.volumenstromZentralgerat << it }
				// Füge Volumenströme in Comboboxen hinzu
				view.raumVsVolumenstrom.removeAllItems()
				// Akustik
				view.akustikZuluftPegel.removeAllItems()
				view.akustikAbluftPegel.removeAllItems()
				model.meta.volumenstromZentralgerat.each {
					// Raumvolumenströme
					view.raumVsVolumenstrom.addItem(it)
					// Akustikberechnung
					view.akustikZuluftPegel.addItem(it)
					view.akustikAbluftPegel.addItem(it)
				}
				// Selektiere errechneten Volumenstrom
				def roundedVs = wacCalculationService.round5(model.map.anlage.volumenstromZentralgerat)
				println "model.map.anlage.volumenstromZentralgerat=${model.map.anlage.volumenstromZentralgerat} roundedVs=${roundedVs}"
				def foundVs = model.meta.volumenstromZentralgerat.find { it.toInteger() == roundedVs }
				// Wenn gerundeter Volumenstrom nicht gefunden wurde, setze Minimum des Zentralgeräts
				if (!foundVs) {
					foundVs = model.meta.volumenstromZentralgerat[0]
				}
				model.map.anlage.volumenstromZentralgerat =
					view.raumVsVolumenstrom.selectedItem =
					foundVs
				// Akustik
				view.akustikZuluftPegel.selectedItem = foundVs
				view.akustikAbluftPegel.selectedItem = foundVs
			}
		}
	}
	
	/**
	 * Raumvolumenströme - Zentralgerät: automatische Aktualisierung das Zentralgeräts.
	 * Darf nur dann durchgeführt werden, wenn der Benutzer das Zentralgerät noch nicht selbst
	 * verändert hat!
	 */
	def onZentralgeratAktualisieren = {
		doLater {
			/*if (DEBUG)*/ println "onZentralgeratAktualisieren: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
			if (!model.map.anlage.zentralgeratManuell) {
				// Berechne Zentralgerät und Volumenstrom
				def (zentralgerat, nl) = wacCalculationService.berechneZentralgerat(model.map)
				model.map.anlage.zentralgerat = zentralgerat
				model.map.anlage.volumenstromZentralgerat = wacCalculationService.round5(nl)
				/*if (DEBUG)*/ println "zentralgeratAktualisieren: zentralgerat=${model.map.anlage.zentralgerat}, nl=${nl}/${model.map.anlage.volumenstromZentralgerat}"
				zentralgeratAktualisieren()
			}
		}
	}
	
	/**
	 * Raumvolumenströme - Volumenstrom des Zentralgeräts.
	 */
	def volumenstromZentralgeratManuellGewahlt = {
		// Aus der View im Projekt-Model speichern
		model.map.anlage.volumenstromZentralgerat = view.raumVsVolumenstrom.selectedItem?.toInteger()
		zentralgeratAktualisieren()
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
		if (DEBUG) println "widerstandsbeiwerteBearbeiten: index=${index}"
		// Setze Index des gewählten Kanalnetzes in Metadaten
		model.meta.dvbKanalnetzGewahlt = index
		// TableModel für WBW hinzufügen, wenn noch nicht vorhanden
		model.addWbwTableModel(index)
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
		if (DEBUG) println "wbwInTabelleGewahlt: index=${index} wbwIndex=${wbwIndex}"
        
        def wbw = model.tableModels.wbw[index][wbwIndex]
        javax.swing.ImageIcon image = null
        // Neu generierte WBWs haben kein Image. Exception abfangen.
        try {
            def url = Wac2Resource.getWiderstandURL(wbw.id)
            image = new javax.swing.ImageIcon(url)
        } catch (NullPointerException e) { }
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
		if (DEBUG) println "wbwSaveButton: index=${index}"
		def wbw = model.tableModels.wbw[index]
		// Daten aus der Eingabemaske holen
		def dialogWbw = [
			name: view.wbwBezeichnung.text,
			widerstandsbeiwert: view.wbwWert.text?.toDouble2() ?: 0.0d,
			anzahl: view.wbwAnzahl.text?.toInteger() ?: 0
		]
		// Wenn WBW noch nicht vorhanden, dann hinzufügen
		if (!wbw.find { it.name == dialogWbw.name }) {
			if (DEBUG) println "wbwSaveButton: adding ${dialogWbw.dump()} to model"
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
		if (DEBUG) println "wbwSummieren: index=${index}"
		def wbw = model.tableModels.wbw[index]
		// Summiere WBW
		def map = model.map.dvb.kanalnetz[index]
		model.meta.summeAktuelleWBW =
			map.gesamtwiderstandszahl =
			wbw.sum {
				it.anzahl.toDouble2() * it.widerstandsbeiwert.toDouble2()
			}
		if (DEBUG) println "wbwSummieren: map.gesamtwiderstandszahl=${map.gesamtwiderstandszahl}"
	}
	
	/**
	 * Widerstandsbeiwerte, Dialog mit OK geschlossen.
	 */
	def wbwOkButton = {
		// Welche Teilstrecke ist ausgewählt? Index bestimmen
		def index = model.meta.dvbKanalnetzGewahlt //view.dvbKanalnetzTabelle.selectedRow
		if (DEBUG) println "wbwOkButton: index=${index}"
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
			// Wurde keine Einstellung gefunden, Benutzer informieren
			model.map.dvb.ventileinstellung.each { ve ->
				if (ve.einstellung == 0) {
					def infoMsg = "Keine Einstellung für Ventil ${ve.ventilbezeichnung} gefunden! Bitte prüfen Sie die Zeile#${ve.position}."
					app.controllers["Dialog"].showInformDialog(infoMsg as String)
					println infoMsg
				}
			}
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
        teilstreckenDialog = GH.createDialog(builder, TeilstreckenView, [title: "Teilstrecken", size: [250, 400]])

        def listModel = view.teilstreckenVerfugbareListe.model
        model.map.dvb.kanalnetz.each { listModel.addElement(it.teilstrecke) }

        if (DEBUG) println "dvbVentileinstellungTeilstreckeDialog: listModel -> ${listModel}"

        view.teilstreckenVerfugbareListe.setModel(listModel)

        teilstreckenDialog.show()

		if (DEBUG) println "TeilstreckenAuswahlen: dialog '${dialog.title}' closed: dialog=${dialog.dump()}"
	}

    /**
     * Teilstrecke von ausgewählte Teilstrecke nach verfügbare Teilstrecke verschieben
     * TODO mmu remove old value!
     */
    def teilstreckenNachVerfugbarVerschieben = {
        // get selected items
        def selectedValues = view.teilstreckenAusgewahlteListe.selectedValues as String[]
        if (DEBUG) println "teilstreckenNachVerfugbarVerschieben: selectedIndices -> ${selectedValues}"
        // add to verfugbare list and remove from ausgewahlte list
        def listModel = view.teilstreckenVerfugbareListe.model
        selectedValues.each { listModel.addElement(it) }
        view.teilstreckenVerfugbareListe.setModel(listModel)

        // remove from ausgewahlte list
        def aListModel = view.teilstreckenAusgewahlteListe.model
        selectedValues.each { aListModel.remove(it) }
        
        view.teilstreckenAusgewahlteListe.setModel(aListModel)

        def listArray = aListModel.toArray()
        def newText = listArray.collect { it }.join(';')
        view.teilstreckenAuswahl.setText(newText)
    }

    /**
     * Teilstrecke von verfügbare Teilstrecke nach ausgewählte Teilstrecke verschieben
     * TODO mmu remove old value!
     */
    def teilstreckenNachAusgewahlteVerschieben = {
        // get selected items
        def selectedValues = view.teilstreckenVerfugbareListe.selectedValues as String[]
        if (DEBUG) println "teilstreckenNachAusgewahlteVerschieben: selectedValues -> ${selectedValues}"
        // add to ausgewahlte list and remove from verfugbare list
        def aListModel = view.teilstreckenAusgewahlteListe.model
        selectedValues.each { aListModel.addElement(it) }
        view.teilstreckenAusgewahlteListe.setModel(aListModel)

        // remove from verfugbare list
        def vListModel = view.teilstreckenVerfugbareListe.model
        selectedValues.each { vListModel.removeElement(it) }
        view.teilstreckenVerfugbareListe.setModel(vListModel)

        if (DEBUG) println "view -> ${view}"

        // set text
        def listArray = aListModel.toArray()
        def newText = listArray.collect { it }.join(';')
        view.teilstreckenAuswahl.setText(newText)
    }
	
	/**
	 * Teilstrecken, Dialog mit OK geschlossen.
	 */
	def teilstreckenOkButton = {
        // save values...
        view.dvbVentileinstellungTeilstrecken.setText(view.teilstreckenAuswahl.text)
        teilstreckenDialog.dispose()
	}
	
	/**
	 * Teilstrecken Dialog Abbrechen - nichts speichern!
	 */
	def teilstreckenCancelButton = {
        // Close dialog
        teilstreckenDialog.dispose()
	}
	
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
		// Nur berechnen, wenn Zentralgerät gesetzt
		if (input.zentralgerat) {
			// Volumenstrom gesetzt?
			if (input.volumenstrom == 0) {
				input.volumenstrom = 50
				view."akustik${tabname}Pegel".selectedItem = model.meta.volumenstromZentralgerat[0]
			}
			// Berechne Akustik
			wacCalculationService.berechneAkustik(tabname, input, model.map)
			// Zentralgerät, Überschrift
			view."akustik${tabname}${tabname}Zentralgerat".text = input.zentralgerat
			// db(A)
			m.dbA =
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

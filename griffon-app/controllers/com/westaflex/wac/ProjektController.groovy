/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/controllers/com/westaflex/wac/ProjektController.groovy
 * Last modified at 28.03.2011 13:34:00 by rbe
 */

package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import com.bensmann.griffon.PdfCreator
import javax.swing.DefaultCellEditor
import javax.swing.JComboBox
import javax.swing.JTable
import javax.swing.table.AbstractTableModel
import javax.swing.event.TableModelListener
import java.awt.Component
import javax.swing.DefaultListModel
import javax.swing.event.TableModelEvent
import griffon.transform.Threading
import static groovyx.net.http.ContentType.XML
import static groovyx.net.http.ContentType.BINARY

/**
 * 
 */
class ProjektController {
	
	public static boolean DEBUG = false
    Boolean loadMode = false
	
	def builder
	
	def model
	def view
	
	def wacCalculationService
	def wacModelService
	def projektModelService
	def oooService
	
	javax.swing.JDialog raumBearbeitenDialog
	def wbwDialog
	def teilstreckenDialog

	/**
	 * Initialize MVC group.
	 */
	void mvcGroupInit(Map args) {
        // Save MVC id
            model.mvcId = args.mvcId
            // Set defaults
            setDefaultValues()
            // Add PropertyChangeListener to our model.meta
            GH.addMapPropertyChangeListener("meta", model.meta)
            // Add PropertyChangeListener to our model.map
            GH.addMapPropertyChangeListener("map", model.map, { evt ->
                // Nur ausführen, wenn Projekt nicht gerade geladen wird
                if (!loadMode) {
                    // Show dialog only when property changes
                    if (evt.propertyName == "ltm") {
                        ltmErforderlichDialog()
                    }
                    // Only set dirty flag, when modified property is not the dirty flag
                    // Used for loading and saving
                    else if (evt.propertyName != "dirty" && !model.map.dirty) {
                        // Dirty-flag im eigenen und Wac2Model setzen
                        model.map.dirty = true
                        // Wac2Model über Änderung informieren
                        app.models["wac2"].aktivesProjektGeandert =
                            app.models["wac2"].alleProjekteGeandert =
                            true
                        // Change tab title (show a star)
                            ////println "popertyChangeListener: calling setTabTitle: ${evt.propertyName}"
                        setTabTitle(view.projektTabGroup.tabCount - 1)
                    }
                }
            })
        //}
	}
	
	/**
	 * Setze Standardwerte (meist in Comboboxen).
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def setDefaultValues() {
		// Lookup values from database and put them into our model
//        doOutside {
            // Raumdaten - Türen
            model.meta.raumTurTyp = ["Tür", "Durchgang"]
            model.meta.raumTurbreiten = [610, 735, 860, 985, 1110, 1235, 1485, 1735, 1985]
			// Raumvolumenströme - Bezeichnungen der Zu-/Abluftventile
			model.meta.raum.raumVsBezeichnungZuluftventile = wacModelService.getZuluftventile()
			model.meta.raum.raumVsBezeichnungAbluftventile = wacModelService.getAbluftventile()
			// Raumvolumenströme - Überströmelemente
			model.meta.raum.raumVsUberstromelemente = wacModelService.getUberstromelemente()
            // Fix: raum typ setzen, sonst wird bei den AkustikBindings eine Exception geworfen.
            //model.meta.raum.typ = ["Wohnzimmer", "Kinderzimmer", "Schlafzimmer", "Esszimmer", "Arbeitszimmer", "Gästezimmer",
            //          "Hausarbeitsraum", "Kellerraum", "WC", "Küche", "Kochnische", "Bad mit/ohne WC", "Duschraum",
            //          "Sauna", "Flur", "Diele"]
			// Raumvolumenströme - Zentralgerät + Volumenstrom
			model.meta.zentralgerat = wacModelService.getZentralgerat()
			// Liste aller möglichen Volumenströme des 1. Zentralgeräts
			def volumenstromZentralgerat =
				wacModelService.getVolumenstromFurZentralgerat(model.meta.zentralgerat[0])
			// 5er-Schritte
			model.meta.volumenstromZentralgerat = []
			def minVsZentralgerat = volumenstromZentralgerat[0] as Integer
			def maxVsZentralgerat = volumenstromZentralgerat.toList().last() as Integer
			(minVsZentralgerat..maxVsZentralgerat).step 5, { model.meta.volumenstromZentralgerat << it }
			// Druckverlustberechnung - Kanalnetz - Kanalbezeichnung
			model.meta.druckverlust.kanalnetz.kanalbezeichnung = wacModelService.getDvbKanalbezeichnung()
			// Druckverlustberechnung - Kanalnetz - Widerstandsbeiwerte
			model.meta.wbw = wacModelService.getWbw()
			// Druckverlustberechnung - Ventileinstellung - Ventilbezeichnung
			model.meta.druckverlust.ventileinstellung.ventilbezeichnung = wacModelService.getDvbVentileinstellung()
			// Akustikberechnung - 1. Hauptschalldämpfer
			model.meta.akustik.schalldampfer = wacModelService.getSchalldampfer()
            //
            ////doLater {
                // Raumvolumenströme, Zentralgerät
                model.map.anlage.zentralgerat = model.meta.zentralgerat[0]
                // Raumvolumenströme, Volumenstrom des Zentralgeräts; default ist erster Wert der Liste
                model.map.anlage.volumenstromZentralgerat = model.meta.volumenstromZentralgerat[0]
            ////}
//        }
	}
	
	/**
	 * Titel für dieses Projekt erstellen: Bauvorhaben, ansonsten MVC ID.
	 */
	StringBuilder getProjektTitel() {
        def title = new StringBuilder()
        // Bauvorhaben
        def bauvorhaben = model.map.kundendaten.bauvorhaben
        if (bauvorhaben) {
            title << "Projekt - ${bauvorhaben}"
        } else {
            title << model.mvcId
        }
        if (DEBUG) println "ProjektController.getProjektTitel stop -> ${title}"
        title
	}
	
	/**
	 * Titel der Tab für dieses Projekt erstellen, und Sternchen für ungesicherte Änderungen anhängen.
	 */
	StringBuilder makeTabTitle() {
        def tabTitle = getProjektTitel()
        if (DEBUG) println "ProjektController.makeTabTitle start -> ${tabTitle}"
        // Dateiname des Projekts oder MVC ID
        tabTitle << " (${model.wpxFilename ?: view.mvcId})"
        // Ungespeicherte Daten?
        if (model.map.dirty) {
            tabTitle << "*"
        }
        //
        tabTitle
	}
	
	/**
	 * Titel des Projekts für Tab setzen.
	 */
	def setTabTitle = { tabIndex -> 
        if (DEBUG) println "ProjektController.setTabTitle tabIndex=${tabIndex}"
        if (!tabIndex) {
            tabIndex = view.projektTabGroup.selectedIndex
            if (DEBUG) println "ProjektController.setTabTitle new tabIndex=${tabIndex}"
        }
        def tabTitle = makeTabTitle()?.toString()
        view.projektTabGroup.setTitleAt(tabIndex, tabTitle)
	}
	
	/**
	 * Can we close? Is there unsaved data -- is our model dirty?
	 */
	boolean canClose() {
		model.map.dirty == false
	}

	/**
     * WAC-151: Perform automatic calculations of 'everything'.
     */
    def automatischeBerechnung = {
        // Flags setzen
        model.map.anlage.zentralgeratManuell = false
        // Neu berechnen
        berechneAlles()
    }
    
    /**
	 * Save this project.
	 * @return Boolean Was project successfully saved to a file?
	 */
	boolean save() {
		if (DEBUG) println "save: saving project '${getProjektTitel()}' in file ${model.wpxFilename?.dump()}"
		try {
			if (model.wpxFilename) {
				// Save data
				projektModelService.save(model.map, model.wpxFilename)
				// Set dirty-flag in project's model to false
				model.map.dirty = false
				// Update tab title to ensure that no "unsaved-data-star" is displayed
				setTabTitle(view.projektTabGroup.selectedIndex)
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
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def seitenansicht = {
        // TODO mmu Dialog: Daten aus Auslegung, Blanko? Ticket #97.
        def doc = oooService.performAuslegung(false, getProjektTitel(), model.map)

        def restUrl = GH.getOdiseeRestUrl() as String
        def restPath = GH.getOdiseeRestPath() as String

        def pdfFile
        edt {
            pdfFile = restXML(restUrl, restPath, doc) as java.io.File
        }
        
        if (DEBUG) println "pdfFile: ${pdfFile?.dump()}"

        if (pdfFile?.exists()) {

            if (DEBUG) println "projektSeitenansicht: doc=${pdfFile?.dump()}"
            // Open document
            switch (System.getProperty("os.name")) {
                case { it ==~ /Windows.*/ }:
                    def program = new java.io.File(System.getenv("OOO_HOME"), "program")
                    def cmd = [
                        "${program.absolutePath.replace('\\', '/')}/soffice.exe",
                        "-nologo", "-nofirststartwizard", "-nodefault",
                        "-nocrashreport", "-norestart", "-norestore",
                        "-nolockcheck",
                        "-writer", "-o \"${pdfFile.absolutePath}\""
                    ]
                    def p = cmd.execute(null, program)
                    p.waitFor()
                    if (DEBUG) println "${cmd} = ${p.exitValue()}"
                    break
                default:
                    java.awt.Desktop.desktop.open(pdfFile)
            }
        }
	}
    
    /**
     * Post xml document via REST and receive a PDF file.
     */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    java.io.File restXML(restUrl, restPath, xmlDoc) {
        java.io.File responseFile
        try {
            withRest(id: "odisee", uri: restUrl) {
                
                def resp = post(path: restPath, body: xmlDoc, requestContentType: XML, responseContentType: BINARY)

                if (DEBUG) println "model.wpxFilename.absolutePath -> ${model.wpxFilename}"
                responseFile = new java.io.File(model.wpxFilename + ".pdf")
                
                responseFile << resp.responseData
                if (DEBUG) println "response end..."
            }
        } catch (e) {
            println "post withRest exception -> ${e.dump()}"
        }
        if (DEBUG) println "returning responsefile ${responseFile.absolutePath}"
        return responseFile
    }
	
	/**
	 * Aktuelles Projekt drucken.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def drucken = {
		def choice = app.controllers["Dialog"].showPrintProjectDialog()
        if (DEBUG) println "drucken: choice=${choice}"
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
     * Alles neu berechnen.
     */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    def berechneAlles = { loadMode = false ->
        this.loadMode = loadMode
        if (DEBUG) println "berechneAlles: loadMode=${loadMode}"
        // Anlagendaten - Kennzeichen
        berechneEnergieKennzeichen()
        berechneHygieneKennzeichen()
        berechneKennzeichenLuftungsanlage()
        // Projekt wird geladen, Räume: Türen-TableModels hinzufügen
        if (loadMode) {
            // Räume; ConcurrentModificationException!
            def raume = model.map.raum.raume.clone()
            // Jedem Raum ein TableModel für Türen hinzufügen
            raume.each { raum ->
                model.addRaumTurenModel()
            }
        }
        // Räume: set cell editors
        if (loadMode) model.setRaumEditors(view)
        try {
            model.resyncRaumTableModels()
        } catch (e) {
            println "berechneAlles: resyncRaumTableModels: ${e}"
        }
        //
        model.map.raum.raume.each { raum ->
            if (DEBUG) println "berechneAlles: BERECHNE RAUM ${raum.position}"
            try {
                raumGeandert(raum.position)
            } catch (e) {
                println "berechneAlles: ${raum.raumBezeichnung} ${e}"
            }
        }
        model.resyncRaumTableModels()
        // Druckverlustberechnung
        // Kanalnetze berechnen
        model.map.dvb.kanalnetz.each {
            if (DEBUG) println "berechneAlles: BERECHNE KANALNETZ ${it}"
            dvbKanalnetzGeandert(it.position)
        }
        // Ventile berechnen
        wacCalculationService.berechneVentileinstellung(model.map)
        // CellEditors, TableModels aktualisieren
        model.resyncDvbKanalnetzTableModels()
        model.resyncDvbVentileinstellungTableModels()
        if (loadMode) model.setDvbKanalnetzEditors(view)
        if (loadMode) model.setDvbVentileinstellungEditors(view)
        // Akustik
        berechneAkustik("Zuluft")
        berechneAkustik("Abluft")
        // Dirty flag
        model.map.dirty = false
        setTabTitle(view.projektTabGroup.selectedIndex)
        //
        this.loadMode = false
    }

    /**
     * Gebäudedaten wurden geändert - Aussenluftvolumenströme berechnen.
     */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    def gebaudedatenGeandert = { evt = null ->
        if (model.map.gebaude.lage.windstark) {
            // WAC-169: Änderung der Druckdifferenz durch Gebäudelage windstark
            //doLater { 
                model.map.gebaude.luftdichtheit.druckdifferenz = 4.0d 
            //}
        }
        berechneAussenluftVs()
    }

	/**
	 * Gebäudedaten - Geometrie wurde manuell eingegeben.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def berechneGeometrie = {
		// Read values from view and transfer them into our model
		def g = [
			wohnflache: view.gebaudeGeometrieWohnflache.text.toDouble2(),
			raumhohe: view.gebaudeGeometrieMittlereRaumhohe.text.toDouble2(),
			//gelufteteFlache: view.gebaudeGeometrieGelufteteFlache.text.toDouble2()
			gelufteteVolumen: view.gebaudeGeometrieGeluftetesVolumen.text.toDouble2()
		]
        //doLater {
            // Write values into model
            model.map.gebaude.geometrie.wohnflache = g.wohnflache
            model.map.gebaude.geometrie.raumhohe = g.raumhohe
            //model.map.gebaude.geometrie.gelufteteFlache = g.gelufteteFlache
            model.map.gebaude.geometrie.gelufteteaVolumen = g.geluftetesVolumen
            //
            wacCalculationService.geometrie(model.map)
            wacCalculationService.aussenluftVs(model.map)
            // Zentralgerät bestimmen
            onZentralgeratAktualisieren()
        //}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def luftdichtheitKategorieA = {
		//doLater {
			model.map.gebaude.luftdichtheit.with {
				druckdifferenz = 2.0d
				luftwechsel = 1.0d
				druckexponent = 0.666f
			}
            berechneAussenluftVs()
		//}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def luftdichtheitKategorieB = {
		//doLater {
			model.map.gebaude.luftdichtheit.with {
				druckdifferenz = 2.0d
				luftwechsel = 1.5f
				druckexponent = 0.666f
			}
            berechneAussenluftVs()
		//}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def luftdichtheitKategorieC = {
		//doLater {
			model.map.gebaude.luftdichtheit.with {
				druckdifferenz = 2.0d
				luftwechsel = 2.0d
				druckexponent = 0.666f
			}
            berechneAussenluftVs()
		//}
	}
	
	/**
	 * Gebäudedaten - Luftdichtheit der Gebäudehülle
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def speichereLuftdichtheit = {
		//doLater {
			model.map.gebaude.luftdichtheit.druckdifferenz = view.gebaudeLuftdichtheitDruckdifferenz.text.toDouble2()
			model.map.gebaude.luftdichtheit.luftwechsel = view.gebaudeLuftdichtheitLuftwechsel.text.toDouble2()
			model.map.gebaude.luftdichtheit.druckexponent = view.gebaudeLuftdichtheitDruckexponent.text.toDouble2()
		//}
        berechneAussenluftVs()
	}
	
	/**
	 * Gebäudedaten - Faktor für besondere Anforderungen
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def speichereFaktorBesondereAnforderungen = {
		//doLater {
			model.map.gebaude.faktorBesondereAnforderungen = view.faktorBesondereAnforderungen.text.toDouble2()
		//}
	}
	
	/**
	 * Gebäudedaten - Geplante Belegung
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def berechneMindestaussenluftrate = {
		//doLater {
			// Save actual caret position
			def personenanzahlCaretPos = view.gebaudeGeplantePersonenanzahl.editor.textField.caretPosition
			def aussenluftVsProPersonCaretPos = view.gebaudeGeplanteAussenluftVsProPerson.editor.textField.caretPosition
			// Hack: model is not updated when KeyListener is used on spinner.editor.textfield
			model.map.gebaude.geplanteBelegung.personenanzahl = view.gebaudeGeplantePersonenanzahl.editor.textField.text?.toDouble2(0) ?: 0
            model.map.gebaude.geplanteBelegung.aussenluftVsProPerson = view.gebaudeGeplanteAussenluftVsProPerson.editor.textField.text?.toDouble2() ?: 0.0d
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
			view.gebaudeGeplantePersonenanzahl.editor.textField.caretPosition = personenanzahlCaretPos
			view.gebaudeGeplanteAussenluftVsProPerson.editor.textField.caretPosition = aussenluftVsProPersonCaretPos
            // Berechnen
            berechneAussenluftVs()
		//}
	}
	
	/**
	 * Anlagendaten - Energie-Kennzeichen
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def berechneEnergieKennzeichen = {
		//doLater {
			model.map.anlage.energie.with {
				if (zuAbluftWarme && bemessung && ruckgewinnung && regelung) {
					nachricht = "Energiekennzeichen gesetzt!"
				} else {
					nachricht = " "
				}
			}
			berechneKennzeichenLuftungsanlage()
		//}
	}
	
	/**
	 * Anlagendaten - Hygiene-Kennzeichen
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def berechneHygieneKennzeichen = {
		//doLater {
			model.map.anlage.hygiene.with {
				if (ausfuhrung && filterung && keineVerschmutzung && dichtheitsklasseB) {
					nachricht = "Hygienekennzeichen gesetzt!"
				} else {
					nachricht = " "
				}
			}
			berechneKennzeichenLuftungsanlage()
		//}
	}
	
	/**
	 * Anlagendaten - Kennzeichen
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def berechneKennzeichenLuftungsanlage = {
		//doLater {
			def gebaudeTyp = model.map.gebaude.efh ? "EFH" : "WE"
			def energieKz = model.map.anlage.energie.nachricht != " " ? "E" : "0"
			def hygieneKz = model.map.anlage.hygiene.nachricht != " " ? "H" : "0"
			def ruckschlag = model.map.anlage.ruckschlagklappe ? "RK" : "0"
			def schallschutz = model.map.anlage.schallschutz ? "S" : "0"
			def feuerstatte = model.map.anlage.feuerstatte ? "F" : "0"
			model.map.anlage.kennzeichnungLuftungsanlage =
				"ZuAbLS-Z-${gebaudeTyp}-WÜT-${energieKz}-${hygieneKz}-${ruckschlag}-${schallschutz}-${feuerstatte}"
		//}
	}
	
	/**
	 * Aussenluftvolumenströme berechnen.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def berechneAussenluftVs = {
        // Mit/ohne Infiltrationsanteil berechnen
        wacCalculationService.aussenluftVs(model.map)
        // Zentralgerät bestimmen
        onZentralgeratAktualisieren()
	}
	
	/**
	 * Raumdaten - Eingabe - Raumtyp in Combobox ausgewählt.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def raumTypGeandert = {
		//doLater {
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
		//}
	}
	
	/**
	 * Zeige Dialog "lüftungstechnische Maßnahmen erforderlich."
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def ltmErforderlichDialog = {
		def infoMsg = model.map.messages.ltm
		app.controllers["Dialog"].showInformDialog(infoMsg as String)
		if (DEBUG) println infoMsg
	}
	
	/**
	 * Raumdaten - Raum anlegen.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
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
		raum.with {
			// Übernehme Wert für Bezeichnung vom Typ?
			raumBezeichnung = raumBezeichnung ?: raumTyp
			// Länge + Breite
			raumLange = 0.0d
			raumBreite = 0.0d
			// Fläche, Höhe, Volumen
			raumFlache = raumFlache.toDouble2()
			raumHohe = raumHohe.toDouble2()
			raumVolumen = raumFlache * raumHohe
			// Zuluftfaktor
			raumZuluftfaktor = raumZuluftfaktor?.toDouble2() ?: 0.0d
			// Abluftvolumenstrom
			raumAbluftVolumenstrom = raumAbluftVolumenstrom?.toDouble2() ?: 0.0d
			// Standard Türspalthöhe ist 10 mm
			raumTurspaltHohe = 10.0d
		}
		//doLater {
			// Raum im Model hinzufügen
            if (DEBUG) println "onRaumHinzufugen: raum -> ${raum}"
			model.addRaum(raum, view)
            raumGeandert(model.map.raum.raume.size() - 1)
            // WAC-170: abw. Raumbezeichnung leeren
            view.raumBezeichnung.text = ""
            // WAC-179: Abluftmenge je Ventil / Anzahl AB-Ventile ändert sich nicht, wenn ein Abluftraum gelöscht wird
            berechneAlles()
		//}
	}
	
	/**
	 * Raumdarten - ein Raum wurde geändert.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def raumGeandert = { Integer raumIndex ->
        // WAC-174 (raumIndex kann == 0 sein!)
        if (raumIndex < 0) raumIndex = view.raumTabelle.selectedRow
        if (DEBUG) println "raumGeandert: raum[${raumIndex}]"
        if (raumIndex > -1) {
            if (DEBUG) println "raumGeandert: raum[${raumIndex}] ${model.map.raum.raume[raumIndex]}"
            // Diesen Raum in allen Tabellen anwählen
            onRaumInTabelleWahlen(raumIndex)
            // Raum holen
            ////def raum = model.map.raum.raume[raumIndex]
            // Raumdaten prüfen
            model.prufeRaumdaten(model.map.raum.raume[raumIndex])
            // WAC-65: Errechnete Werte zurücksetzen
            model.map.raum.raume[raumIndex].with {
                if (raumBreite && raumLange) raumFlache = raumBreite * raumLange
                raumVolumen = raumFlache * raumHohe
                raumLuftwechsel = 0.0d
                // Abluft
                raumAbluftVolumenstromInfiltration = 0.0d // Abluftvs abzgl. Infiltration
                raumAnzahlAbluftventile = 0
                raumAbluftmengeJeVentil = 0.0d
                // Zuluft
                raumZuluftVolumenstromInfiltration = 0.0d // Zuluftvs abzgl. Infiltration
                raumAnzahlZuluftventile = 0
                raumZuluftmengeJeVentil = 0.0d
                raumAnzahlUberstromVentile = 0
            }
            // Überströmvolumenstrom
            // WAC-151
            if (!model.map.anlage.zentralgeratManuell) {
                model.map.raum.raume[raumIndex].raumUberstromVolumenstrom = 0.0d
            }
            // Raumvolumenströme, (Werte abzgl. Infiltration werden zur Berechnung der Ventile benötigt) berechnen
            wacCalculationService.autoLuftmenge(model.map)
            // Zu-/Abluftventile
            model.map.raum.raume[raumIndex] =
                wacCalculationService.berechneZuAbluftventile(model.map.raum.raume[raumIndex])
            // Türspalt und Türen
            model.map.raum.raume[raumIndex] =
                wacCalculationService.berechneTurspalt(model.map.raum.raume[raumIndex])
            berechneTuren(null, raumIndex)
            // Überströmelement berechnen
            model.map.raum.raume[raumIndex] =
                wacCalculationService.berechneUberstromelemente(model.map.raum.raume[raumIndex])
        }
        // Nummern der Räume berechnen
        wacCalculationService.berechneRaumnummer(model.map)
        // Gebäude-Geometrie berechnen
        wacCalculationService.geometrieAusRaumdaten(model.map)
        // Aussenluftvolumenströme berechnen
        wacCalculationService.aussenluftVs(model.map)
        // Zentralgerät bestimmen
        onZentralgeratAktualisieren()
        // WAC-171: Finde Räume ohne Türen oder ÜB-Elemente
        def raumeOhneTuren = model.map.raum.raume.findAll { raum ->
            raum.raumUberstromVolumenstrom > 0 && !raum.turen.any { it.turBreite > 0 }
        }
        def raumeOhneUbElemente = []
        raumeOhneUbElemente = model.map.raum.raume.findAll { raum ->
            def turSpalthoheUberschritten = raum.turen.findAll {
                it.turSpalthohe > raum.raumMaxTurspaltHohe.toDouble2()
            }?.size() ?: 0
            if (DEBUG) println "WAC-171: Raum=${raum.raumBezeichnung}, turspalthoheUberschritten=${turSpalthoheUberschritten}"
            if (DEBUG) println "WAC-171: ${raum.raumUberstromVolumenstrom > 0} && ${turSpalthoheUberschritten > 0} && ${!raum.raumUberstromElement}"
            raum.raumUberstromVolumenstrom > 0 && turSpalthoheUberschritten > 0 /*&& !raum.raumUberstromElement*/
        }
        model.map.raum.raumVs.turenHinweis = raumeOhneTuren.size() > 0 ?
            "Hinweis: bitte Türen prüfen: ${raumeOhneTuren.collect { it.raumBezeichnung }.join(", ")}" : ""
        model.map.raum.raumVs.ubElementeHinweis = raumeOhneUbElemente.size() > 0 ?
            "Hinweis: bitte ÜB-Elemente prüfen: ${raumeOhneUbElemente.collect { it.raumBezeichnung }.join(", ")}" : ""
	}
	
	/**
	 * Raumdaten - einen Raum entfernen.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def raumEntfernen = {
        // Raum aus Model entfernen
        model.removeRaum(view.raumTabelle.selectedRow, view)
        // Es hat sich was geändert...
        raumGeandert(view.raumTabelle.selectedRow)
        // WAC-179: Abluftmenge je Ventil / Anzahl AB-Ventile ändert sich nicht, wenn ein Abluftraum gelöscht wird
        berechneAlles()
	}
	
	/**
	 * Raumdaten - einen Raum kopieren.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def raumKopieren = {
		//doLater {
			// Get selected row
			def row = view.raumTabelle.selectedRow
			// Raum anhand seiner Position finden
			def x = model.map.raum.raume.find { it.position == row }
            // Kopie erzeugen
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
            raumGeandert(newMap.position)
            // WAC-179: Abluftmenge je Ventil / Anzahl AB-Ventile ändert sich nicht, wenn ein Abluftraum gelöscht wird
            berechneAlles()
		//}
	}
	
	/**
	 * Raumdaten - einen Raum in der Tabelle nach oben verschieben.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def raumNachObenVerschieben = {
		//doLater {
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
                raumGeandert(row - 1)
			}
		//}
	}
	
	/**
	 * Raumdaten - einen Raum in der Tabelle nach oben verschieben.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def raumNachUntenVerschieben = {
		//doLater {
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
                raumGeandert(row + 1)
			}
		//}
	}
	
	/**
	 * Raumdaten - einen Raum bearbeiten.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def raumBearbeiten = {
		// Get selected row
        def row = view.raumTabelle.selectedRow
        if (row > -1) {
            // Show dialog
            /*raumBearbeitenDialog = builder.dialog([title: "Raum bearbeiten", pack: true]) {
                jideScrollPane() {
                    build(RaumBearbeitenView)
                }
            }*/
            raumBearbeitenDialog = GH.createDialog(builder, RaumBearbeitenView, [title: "Raum bearbeiten", pack: true])
            // Modify TableModel for Turen
            def columnModel = view.raumBearbeitenTurenTabelle.columnModel
            GH.makeComboboxCellEditor(columnModel.getColumn(0), model.meta.raumTurTyp)
            GH.makeComboboxCellEditor(columnModel.getColumn(1), model.meta.raumTurbreiten)
            /*
            view.raumBearbeitenTurenTabelle.getModel().addTableModelListener({ TableModelEvent e ->
                println "test"
                println "test"
                println "test e -> ${e.dump()}"
                println "test"
                println "test"
            } as TableModelListener);
            */
            berechneTuren(null, row)
            raumBearbeitenDialog.show()
        }
	}
	
	/**
	 * RaumBearbeiten - RaumBearbeitenView schliessen.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def raumBearbeitenSchliessen = {
        if (DEBUG) println "raumBearbeitenSchliessen: closing dialog '${raumBearbeitenDialog.title}'"
        raumBearbeitenGeandert()
        raumBearbeitenDialog.dispose()
	}
	
	/**
	 * Raum bearbeiten - Daten eingegeben. Mit raumBearbeitenSchliessen zusammenlegen?
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def raumBearbeitenGeandert = { evt = null ->
        // WAC-174: Immer Raum Index/Position aus Metadaten nehmen
        //def raumIndex = view.raumTabelle.selectedRow
        def raumIndex = model.meta.gewahlterRaum.position
        // Daten aus Dialog übertragen und neu berechnen
        def m = model.map.raum.raume[raumIndex]
        // Raumnummer
        model.meta.gewahlterRaum.raumNummer =
            m.raumNummer =
            view.raumBearbeitenRaumnummer.text
        // Raumbezeichnung
        model.meta.gewahlterRaum.raumBezeichnung =
            m.raumBezeichnung =
            view.raumBearbeitenBezeichnung.text
        // Raumtyp
        // Geschoss
        model.meta.gewahlterRaum.raumGeschoss =
            m.raumGeschoss =
            view.raumBearbeitenRaumGeschoss.selectedItem
        // Luftart
        try {
            //println "!!!: ${view.raumBearbeitenLuftart.selectedItem?.dump()}"
            model.meta.gewahlterRaum.raumLuftart =
                m.raumLuftart =
                view.raumBearbeitenLuftart.selectedItem
        } catch (e) {
            // TODO Warum, funktioniert trotzdem? groovy.lang.MissingPropertyException: No such property: text for class: javax.swing.JComboBox
            //println "raumBearbeitenGeandert: EXCEPTION: ${e}"
        }
        // Zuluftfaktor
        model.meta.gewahlterRaum.raumZuluftfaktor =
            m.raumZuluftfaktor =
            view.raumBearbeitenLuftartFaktorZuluftverteilung.text?.toDouble2()
        // Abluftvolumenstrom
        model.meta.gewahlterRaum.raumAbluftVolumenstrom =
            m.raumAbluftVolumenstrom =
            view.raumBearbeitenLuftartAbluftVs.text?.toDouble2()
        // Max. Türspalthöhe
        model.meta.gewahlterRaum.raumMaxTurspaltHohe =
            m.raumMaxTurspaltHohe =
            view.raumBearbeitenDetailsTurspalthohe.text?.toDouble2()
        // Geometrie
        model.meta.gewahlterRaum.raumLange =
            m.raumLange =
            view.raumBearbeitenOptionalRaumlange.text?.toDouble2()
        model.meta.gewahlterRaum.raumBreite =
            m.raumBreite =
            view.raumBearbeitenOptionalRaumbreite.text?.toDouble2()
        model.meta.gewahlterRaum.raumHohe =
            m.raumHohe =
            view.raumBearbeitenOptionalRaumhohe.text?.toDouble2()
        // Raum neu berechnen
        raumGeandert(raumIndex)
        // Daten aus Model in den Dialog übertragen
        // Zuluft/Abluft
        model.meta.gewahlterRaum.raumZuluftfaktor = m.raumZuluftfaktor
        model.meta.gewahlterRaum.raumAbluftvolumenstrom = m.raumAbluftvolumenstrom
        ////if (model.meta.gewahlterRaum.)
        // Geometrie
        model.meta.gewahlterRaum.raumFlache = m.raumFlache
        model.meta.gewahlterRaum.raumVolumen = m.raumVolumen
        model.meta.gewahlterRaum.raumNummer = m.raumNummer
	}
	
	/**
	 * Berechne Türen eines bestimmten Raumes.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def berechneTuren = { evt = null, raumIndex = null ->
        if (DEBUG) println "WAC-174: berechneTuren: evt=${evt?.dump()} raumIndex=${raumIndex?.dump()}"
		// Hole gewählten Raum
        if (!raumIndex) raumIndex = view.raumTabelle.selectedRow
        if (DEBUG) println "WAC-174: berechneTuren: raumIndex=${raumIndex?.dump()}"
		def raum = model.map.raum.raume[raumIndex]
        if (DEBUG) println "WAC-174: berechneTuren: raum=${raum?.dump()}"
		// Türen berechnen?
        if (raum.turen.findAll { it.turBreite > 0 }?.size() > 0 && raum.raumUberstromVolumenstrom) {
            wacCalculationService.berechneTurspalt(raum)
            // WAC-165: Hinweis: Türspalt > max. Türspalthöhe?
            def turSpalthoheUberschritten = raum.turen.findAll {
                it.turSpalthohe > raum.raumMaxTurspaltHohe.toDouble2()
            }?.size() ?: 0
            if (turSpalthoheUberschritten > 0) {
                model.meta.gewahlterRaum.raumTurspaltHinweis = "Hinweis: Maximale Türspalthöhe überschritten!"
            } else {
                model.meta.gewahlterRaum.raumTurspaltHinweis = ""
            }
        }
        // WAC-165: Bugfix: Werte in der Türen-Tabelle werden erst dann aktualisiert, wenn die Maus über einzelne Zeilen bewegt wird
        try {
            view.raumBearbeitenTurenTabelle.repaint()
        } catch (e) {}
	}

    /**
     * Tur Werte entfernen in Raum bearbeiten Dialog
     */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
    def raumBearbeitenTurEntfernen = { evt = null ->
        if (DEBUG) println "raumBearbeitenTurEntfernen: view.raumBearbeitenTurenTabelle.selectedRow -> ${view.raumBearbeitenTurenTabelle.selectedRow}"
        def turenIndex = view.raumBearbeitenTurenTabelle.selectedRow
        def raumIndex = model.meta.gewahlterRaum.position
        try {
            def raum = model.map.raum.raume[raumIndex]
            raum.turen[turenIndex] = [turBezeichnung: "", turBreite: 0, turQuerschnitt: 0, turSpalthohe: 0, turDichtung: true]
            if (DEBUG) println "raumBearbeitenTurEntfernen: ${raum}"
        } catch (e) {}
        // WAC-174: resyncTableModels ist notwendig, selectedRow wird auf 0 gesetzt, daher selectedRow setzen
        model.resyncRaumTableModels()
        view.raumTabelle.changeSelection(model.meta.gewahlterRaum.position, 0, false, false) 
        // WAC-174: Parameter fehlten!
        berechneTuren(null, raumIndex)
    }
	
	/**
	 * Raumvolumenströme - Zu/Abluftventile geändert.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def raumZuAbluftventileGeandert = {
		def raumIndex = view.raumVsZuAbluftventileTabelle.selectedRow
		if (DEBUG) println "raumZuAbluftventileGeandert: raumIndex=${raumIndex}"
		if (raumIndex > -1) {
			wacCalculationService.berechneZuAbluftventile(model.map.raum.raume[raumIndex])
		} else {
			if (DEBUG) println "raumZuAbluftventileGeandert: Kein Raum ausgewählt, es wird nichts berechnet"
		}
	}
	
	/**
	 * Raumvolumenströme - Überströmelemente geändert.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def raumUberstromelementeGeandert = {
		def raumIndex = view.raumVsUberstromelementeTabelle.selectedRow
		if (raumIndex > -1) {
			wacCalculationService.berechneUberstromelemente(model.map.raum.raume[raumIndex])
		} else {
			if (DEBUG) println "raumUberstromelementeLuftmengeBerechnen: Kein Raum ausgewählt, es wird nichts berechnet"
		}
	}
	
	/**
	 * In einer der Raum-Tabellen wurde die Auswahl durch den Benutzer geändert:
	 * die Auswahl aller anderen Tabellen entsprechend anpassen.
	 * @param evt javax.swing.event.ListSelectionEvent
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
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
		//doLater {
			//if (DEBUG) println "onRaumInTabelleWahlen: row=${row}"
			row = GH.checkRow(row, view.raumTabelle)
			if (row > -1) {
				// Raum in Raumdaten-Tabelle, Raumvolumenströme-Zu/Abluftventile-Tabelle,
				// Raumvolumenströme-Überströmelemente-Tabelle markieren
				withAllRaumTables { t ->
                    try {
                        GH.withDisabledListSelectionListeners t, { -> changeSelection(row, 0, false, false) }
                    } catch (e) {
                        // java.lang.IndexOutOfBoundsException: bitIndex < 0: -1
                    }
				}
				// Aktuellen Raum in Metadaten setzen
                model.meta.gewahlterRaum.putAll(model.map.raum.raume[row])
				// TODO Warum wird das hier gemacht??? WacCalculationService.berechneRaumnummer ist zuständig!
                ////model.meta.gewahlterRaum.raumNummer = row + 1
			} else {
				// Remove selection in all tables
				withAllRaumTables { t ->
					t.clearSelection()
				}
				// Aktuell gewählten Raum in Metadaten leeren
				model.meta.gewahlterRaum.clear()
			}
		//}
	}
	
	/**
	 * Raumvolumenströme - Zentralgerät: manuelle Auswahl des Zentralgeräts.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def zentralgeratManuellGewahlt = {
		////publishEvent "ZentralgeratGewahlt", [view.raumVsZentralgerat.selectedItem]
//		doLater {
			// Merken, dass das Zentralgerät manuell ausgewählt wurde
			// -> keine automatische Auswahl des Zentralgeräts mehr durchführen
            if (DEBUG) println "zentralgeratManuellGewahlt: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
			model.map.anlage.zentralgeratManuell = true
            if (DEBUG) println "zentralgeratManuellGewahlt: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
			// Zentralgerät aus View in Model übernehmen
			model.map.anlage.zentralgerat = view.raumVsZentralgerat.selectedItem
			// Hole Volumenströme des Zentralgeräts
			model.meta.volumenstromZentralgerat =
				wacModelService.getVolumenstromFurZentralgerat(model.map.anlage.zentralgerat)
			// Aussenluftvolumenströme neu berechnen
			wacCalculationService.aussenluftVs(model.map)
			// Neue Auswahl setzen
			zentralgeratAktualisieren()
//		}
	}

	/**
	 * Aktualisiere Zentralgerät und Volumenstrom in allen Comboboxen
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	void zentralgeratAktualisieren() {
        if (DEBUG) println "zentralgeratAktualisieren: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
		//doLater {
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
				if (DEBUG) println "zentralgeratAktualisieren: model.map.anlage.volumenstromZentralgerat=${model.map.anlage.volumenstromZentralgerat} roundedVs=${roundedVs}"
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
		//}
	}
	
	/**
	 * Raumvolumenströme - Zentralgerät: automatische Aktualisierung das Zentralgeräts.
	 * Darf nur dann durchgeführt werden, wenn der Benutzer das Zentralgerät noch nicht selbst
	 * verändert hat!
	 */
	def onZentralgeratAktualisieren = {
//		doLater {
			if (DEBUG) println "onZentralgeratAktualisieren: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
			if (!model.map.anlage.zentralgeratManuell) {
				// Berechne Zentralgerät und Volumenstrom
				def (zentralgerat, nl) = wacCalculationService.berechneZentralgerat(model.map)
				model.map.anlage.zentralgerat = zentralgerat
				model.map.anlage.volumenstromZentralgerat = wacCalculationService.round5(nl)
				if (DEBUG) println "onZentralgeratAktualisieren: zentralgerat=${model.map.anlage.zentralgerat}, nl=${nl}/${model.map.anlage.volumenstromZentralgerat}"
				zentralgeratAktualisieren()
			}
//		}
	}
	
	/**
	 * Raumvolumenströme - Volumenstrom des Zentralgeräts.
	 */
	def volumenstromZentralgeratManuellGewahlt = {
        // Merken, dass das Zentralgerät manuell ausgewählt wurde
        // -> keine automatische Auswahl des Zentralgeräts mehr durchführen
        if (DEBUG) println "zentralgeratManuellGewahlt: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
        model.map.anlage.zentralgeratManuell = true
        if (DEBUG) println "zentralgeratManuellGewahlt: zentralgeratManuell=${model.map.anlage.zentralgeratManuell}"
		// Aus der View im Projekt-Model speichern
		model.map.anlage.volumenstromZentralgerat = view.raumVsVolumenstrom.selectedItem?.toInteger()
		zentralgeratAktualisieren()
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz - Hinzufügen.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def dvbKanalnetzHinzufugen = {
		def kanalnetz = GH.getValuesFromView(view, "dvbKanalnetz")
		////publishEvent "DvbKanalnetzHinzufugen", [kanalnetz, view]
        //doLater {
            // Map values from GUI
            def k = [
                    luftart: kanalnetz.dvbKanalnetzLuftart,
                    teilstrecke: kanalnetz.dvbKanalnetzNrTeilstrecke?.toInteger(),
                    luftVs: kanalnetz.dvbKanalnetzLuftmenge?.toDouble2(),
                    kanalbezeichnung: kanalnetz.dvbKanalnetzKanalbezeichnung,
                    lange: kanalnetz.dvbKanalnetzLange?.toDouble2(),
                    position: model.map.dvb.kanalnetz.size(),
                    gesamtwiderstandszahl: 0.0d
                ] as ObservableMap
            // Berechne die Teilstrecke
            k = wacCalculationService.berechneTeilstrecke(k)
            // Add values to model
            model.addDvbKanalnetz(k, view)
            // Add PropertyChangeListener to our model.map
            GH.addMapPropertyChangeListener("map.dvb.kanalnetz", k)
            //
            dvbKanalnetzGeandert(k.position)
        //}
        // Reset values in view
        view.dvbKanalnetzNrTeilstrecke.text = ""
        view.dvbKanalnetzLuftmenge.text = ""
        view.dvbKanalnetzLange.text = ""
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz wurde hinzugefügt: Eintrag in der Tabelle anwählen.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_SYNC)
	def onDvbKanalnetzInTabelleWahlen = { kanalnetzIndex ->
		//doLater {
			view.dvbKanalnetzTabelle.changeSelection(kanalnetzIndex, 0, false, false)
		//}
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz - Geändert.
	 */
	def dvbKanalnetzGeandert = { kanalnetzIndex ->
		///publishEvent "DvbKanalnetzGeandert", [kanalnetzIndex]
//        doLater {
            // Berechne die Teilstrecke
            model.map.dvb.kanalnetz[kanalnetzIndex] =
                wacCalculationService.berechneTeilstrecke(model.map.dvb.kanalnetz[kanalnetzIndex])
            //
            ////publishEvent "DvbKanalnetzInTabelleWahlen", [kanalnetzIndex]
            !loadMode && onDvbKanalnetzInTabelleWahlen(kanalnetzIndex)
//        }
	}
	
	/**
	 * Druckverlustberechnung - Kanalnetz - Entfernen.
	 */
	def dvbKanalnetzEntfernen = {
		///publishEvent "DvbKanalnetzEntfernen", [view.dvbKanalnetzTabelle.selectedRow]
        def kanalnetzIndex = view.dvbKanalnetzTabelle.selectedRow
        doLater {
            //println "onDvbKanalnetzEntfernen: kanalnetzIndex=${kanalnetzIndex}"
            // Zeile aus Model entfernen
            model.removeDvbKanalnetz(kanalnetzIndex)
        }
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
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
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
        view.wbwWert.text = wbw.widerstandsbeiwert.toString2()
        view.wbwAnzahl.text = wbw.anzahl
	}
	
	/**
	 * Widerstandsbeiwerte, Übernehmen-Button.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
	def wbwSaveButton = {
		// Welche Teilstrecke ist ausgewählt? Index bestimmen
		def index = model.meta.dvbKanalnetzGewahlt //view.dvbKanalnetzTabelle.selectedRow
		if (DEBUG) println "wbwSaveButton: index=${index}"
		def wbw = model.tableModels.wbw[index]
		// Daten aus der Eingabemaske holen
		def dialogWbw = [
			name: view.wbwBezeichnung.text,
			widerstandsbeiwert: view.wbwWert.text?.toDouble2() ?: 0.0d,
			anzahl: view.wbwAnzahl.text?.toString() ?: "0"
		]
		// Wenn WBW noch nicht vorhanden, dann hinzufügen
        def editedWbw = wbw.find { it.name == dialogWbw.name }
		if (!editedWbw) {
			if (DEBUG) println "wbwSaveButton: adding ${dialogWbw.dump()} to model"
			wbw << dialogWbw
        } else {
            editedWbw = wbw[view.wbwTabelle.selectedRow]
            editedWbw.widerstandsbeiwert = dialogWbw.widerstandsbeiwert
            editedWbw.anzahl = dialogWbw.anzahl
        }
        wbwSummieren()
    }
	
	/**
	 * Widerstandsbeiwerte: eingegebene Werte aufsummieren.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
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
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
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
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
	def wbwCancelButton = {
		// Close dialog
		wbwDialog.dispose()
	}
	
	/**
	 * Druckverlustberechnung - Ventileinstellung - Hinzufügen.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
	def dvbVentileinstellungHinzufugen = {
		def ventileinstellung = GH.getValuesFromView(view, "dvbVentileinstellung")
		////publishEvent "DvbVentileinstellungHinzufugen", [ventileinstellung, view]
        doLater {
            // Map values from GUI
            def v = [
                    luftart: ventileinstellung.dvbVentileinstellungLuftart,
                    raum: ventileinstellung.dvbVentileinstellungRaum,
                    teilstrecken: ventileinstellung.dvbVentileinstellungTeilstrecken,
                    ventilbezeichnung: ventileinstellung.dvbVentileinstellungVentilbezeichnung,
                    position: model.map.dvb.ventileinstellung.size() ?: 0
                ] as ObservableMap
            model.addDvbVentileinstellung(v, view)
            def index = v.position
            // Add PropertyChangeListener to our model.map
            GH.addMapPropertyChangeListener("map.dvb.ventileinstellung",
                model.map.dvb.ventileinstellung[index])
            //
            dvbVentileinstellungGeandert(index)
        }
	}
	
	/**
	 * Druckverlustberechnung - Ventileinstellung wurde hinzugefügt: Eintrag in der Tabelle anwählen.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
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
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
	def dvbVentileinstellungGeandert = { ventileinstellungIndex ->
		////publishEvent "DvbVentileinstellungGeandert", [ventileinstellungIndex]
        doLater {
            wacCalculationService.berechneVentileinstellung(model.map)
            //
            ////publishEvent "DvbVentileinstellungInTabelleWahlen", [ventileinstellungIndex]
            onDvbVentileinstellungInTabelleWahlen(ventileinstellungIndex)
        }
	}
	
	/**
	 * Druckverlustberechnung - Ventileinstellung - Entfernen.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
	def dvbVentileinstellungEntfernen = {
		////publishEvent "DvbVentileinstellungEntfernen", [view.dvbVentileinstellungTabelle.selectedRow]
        doLater {
            //println "onDvbVentileinstellungEntfernen: ventileinstellungIndex=${ventileinstellungIndex}"
            // Zeile aus Model entfernen
            model.removeDvbVentileinstellung(ventileinstellungIndex)
        }
	}
	
	/**
	 * Druckverlustberechnung - Ventileinstellung - Teilstrecke wählen.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
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
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
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
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
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
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
	def teilstreckenOkButton = {
        // save values...
        view.dvbVentileinstellungTeilstrecken.setText(view.teilstreckenAuswahl.text)
        teilstreckenDialog.dispose()
	}
	
	/**
	 * Teilstrecken Dialog Abbrechen - nichts speichern!
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
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
            // TODO 5er-Schritte
			wacModelService.getVolumenstromFurZentralgerat(zg.selectedItem).each { p.addItem(it) }
		}
	}
	
	/**
	 * Akustikberechnung.
	 */
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
	void berechneAkustik(tabname) {
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
				raumabsorption: view."akustik${tabname}Raumabsorption".selectedItem
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
            m.dbA = view."akustik${tabname}dbA".text =
				wacModelService.getDezibelZentralgerat(input.zentralgerat, input.volumenstrom, tabname).toString2()
			// Mittlerer Schalldruckpegel
			view."akustik${tabname}MittlererSchalldruckpegel".text =
				m.mittlererSchalldruckpegel?.toString2() ?: 0d.toString2()
			// Resync table
			model.resyncAkustikTableModels(view)
		}
	}
    
    
    /**
     *
     */
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def generiereStuckliste = {
        
        try {
            
            def title = getProjektTitel() as String
            
            // Save document in user home dir
            //def userDir = System.getProperty("user.home")
            //userDir = userDir + "/" + title + "_" + System.currentTimeMillis() + ".pdf"
            def stucklisteFilename = model.wpxFilename + "_" + System.currentTimeMillis() + ".pdf"
            
            PdfCreator pdfCreator = new PdfCreator()
            // Create a new pdf document
            pdfCreator.createDocument(stucklisteFilename)

            def logourl = Wac2Resource.getPdfLogo()
            if (DEBUG) println "logourl -> ${logourl.dump()}"
            pdfCreator.addLogo(logourl)
            // Add title to document
            pdfCreator.addTitle(title)
            // create table with relative column width
            pdfCreator.createTable([2f, 1f, 3f, 1f] as float[])
            
            // Zentralgerät
            def zentralgerat = "Zentralgerät: ${model.map.anlage.zentralgerat}" as String
            pdfCreator.addArtikelToDocument(zentralgerat)
            
            model.map.raum.raume.each { r -> erzeugeRaumStuckliste(r, pdfCreator) }
            
            erzeugeDruckverlustStuckliste(model.map.dvb, pdfCreator)
            
            erzeugeSchalldampferStuckliste(model.map.akustik.zuluft, "zuluft", pdfCreator)
            erzeugeSchalldampferStuckliste(model.map.akustik.abluft, "abluft", pdfCreator)
            
            // Add table to the document
            pdfCreator.addTable()
            // Close the pdf document
            pdfCreator.closeDocument()
            
            def successMsg = "Stückliste '${stucklisteFilename}' erfolgreich generiert"
            app.controllers["Dialog"].showInformDialog(successMsg as String)
        } catch (e) {
            println "Error generating document: ${e}"
            
            def errorMsg = "Beim generieren der Stückliste ist ein Fehler aufgetreten"
            app.controllers["Dialog"].showErrorDialog(errorMsg as String)
        }
        
    }
    
    
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def erzeugeRaumStuckliste = { map, pdfCreator -> 
        
        if (DEBUG) println "map -> ${map.dump()}"
        
        def luftart = "Abluft"
        def ventil = map.raumBezeichnungAbluftventile
        def anzahl = map.raumAnzahlAbluftventile
        if (!ventil) {
            luftart = "Zuluft"
            ventil = map.raumBezeichnungZuluftventile
            anzahl = map.raumAnzahlZuluftventile
        }
        
        if (ventil && anzahl > 0) {
            //pdfCreator.addRaum(map.raumBezeichnung)
            pdfCreator.addArtikel(map.raumBezeichnung, luftart, ventil, anzahl)
        }
        
        // Überströmventile hinzufügen
        anzahl = map.raumAnzahlUberstromVentile
        ventil = map.raumUberstromElement
        luftart = "Überström"
        
        if (ventil && anzahl > 0) {
            //pdfCreator.addRaum(map.raumBezeichnung)
            pdfCreator.addArtikel("", luftart, ventil, anzahl)
        }
        
    }
    
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def erzeugeDruckverlustStuckliste = { dvb, pdfCreator ->
        dvb.kanalnetz.each { 
            pdfCreator.addArtikel("", "", it.kanalbezeichnung, it.lange)
        }
    }   
    
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    def erzeugeSchalldampferStuckliste = { akustik, luftart, pdfCreator ->
        pdfCreator.addArtikel("", "", akustik.hauptschalldampfer1, 1)
        pdfCreator.addArtikel("", "", akustik.hauptschalldampfer2, 1)
    }
    
}

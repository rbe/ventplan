/**
 * /Users/rbe/project/wac2/griffon-app/views/com/westaflex/wac/AnlagendatenView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout

jideScrollPane(constraints: "grow") {
	
	panel(constraints: "grow", layout: new MigLayout("fillx, filly", "[fill][fill][fill]", "[fill]")) {
		
		// Gerätestandort
		panel(id: "anlageGeratestandortPanel", border: titledBorder(title: "Gerätestandort"), layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
			buttonGroup().with {
				add radioButton(id: "anlageGeratestandortKG", text: "Kellergeschoss")
				add radioButton(id: "anlageGeratestandortEG", text: "Erdgeschoss")
				add radioButton(id: "anlageGeratestandortOG", text: "Obergeschoss")
				add radioButton(id: "anlageGeratestandortDG", text: "Dachgeschoss")
				add radioButton(id: "anlageGeratestandortSB", text: "Spitzboden")
			}
		}
		
		// Luftkanalverlegung
		panel(id: "anlageLuftkanalverlegungPanel", border: titledBorder(title: "Luftkanalverlegung"), layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
			label("Quadroflexsysteme 100 mit nur 60 mm Aufbauhöhe")
			checkBox(id: "anlageLuftkanalverlegungAufputz",     text: "Aufputz (Abkastung)")
			checkBox(id: "anlageLuftkanalverlegungDammschicht", text: "Dämmschicht unter Estrich")
			checkBox(id: "anlageLuftkanalverlegungDecke",       text: "Decke (abgehängt)")
			checkBox(id: "anlageLuftkanalverlegungSpitzboden",  text: "Spitzboden")
		}
	
		// Außenluft
		panel(id: "anlageAussenluftPanel", border: titledBorder(title: "Außenluft"), constraints: "wrap", layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
			buttonGroup().with {
				add radioButton(id: "anlageAussenluftDach",     text: "Dachdurchführung")
				add radioButton(id: "anlageAussenluftWand",     text: "Wand (Luftgitter)")
				add radioButton(id: "anlageAussenluftErdwarme", text: "Erdwärmetauscher")
			}
		}
	
		// Zuluftdurchlässe
		panel(id: "anlageZuluftPanel", border: titledBorder(title: "Zuluftdurchlässe"), layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
			checkBox(id: "anlageZuluftTellerventile",  text: "Tellerventile")
			checkBox(id: "anlageZuluftSchlitzauslass", text: "Schlitzauslass (Weitwurfdüse)")
			checkBox(id: "anlageZuluftFussboden",      text: "Fußbodenauslass")
			checkBox(id: "anlageZuluftSockel",         text: "Sockelquellauslass")
		}
	
		// Abluftdurchlässe
		panel(id: "anlageAbluft", border: titledBorder(title: "Abluftdurchlässe"), layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
			checkBox(id: "anlageAbluftTellerventile",  text: "Tellerventile (Standard)")
		}
	
		// Fortluft
		panel(id: "anlageFortluftPanel", border: titledBorder(title: "Fortluft"), constraints: "wrap", layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
			buttonGroup().with {
				add radioButton(id: "anlageFortluftDach",         text: "Dachdurchführung")
				add radioButton(id: "anlageFortluftWand",         text: "Wand (Luftgitter)")
				add radioButton(id: "anlageFortluftLichtschacht", text: "Lichtschacht (Kellergeschoss)")
			}
		}

        panel(id: "test", layout: new MigLayout("fillx", "[fill][fill]", "[fill]"), constraints: "span") {
            // Energie-Kennzeichen
            panel(id: "anlageEnergiePanel", border: titledBorder(title: "Energie-Kennzeichen"), layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
                checkBox(id: "anlageEnergieZuAbluftWarme", text: "Zu-/Abluftgeräte mit Wärmerückgewinnung")
                checkBox(id: "anlageEnergieBemessung",     text: "Bemessung und Ausführung des Lüftungssystems")
                checkBox(id: "anlageEnergieRuckgewinnung", text: "Rückgewinnung von Abluftwärme")
                checkBox(id: "anlageEnergieRegelung",      text: "Zweckmäßige Relegung")
                label(id: "anlageEnergieNachricht", foreground: java.awt.Color.RED, text: " ")
            }

            // Hygiene-Kennzeichen
            panel(id: "anlageHygienePanel", border: titledBorder(title: "Hygiene-Kennzeichen"), constraints: "span 2, wrap", layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
                checkBox(id: "anlageHygieneAusfuhrung",         text: "Ausführung und Lage der Außenluftansaugung")
                checkBox(id: "anlageHygieneFilterung",          text: "Filterung der Außenluft und der Abluft")
                checkBox(id: "anlageHygieneKeineVerschmutzung", text: "möglichst keine Verschmutzung des Luftleitungsnetzes")
                checkBox(id: "anlageHygieneDichtheitsklasseB",  text: "Dichtheitsklasse B der Luftleitungen")
                label(id: "anlageHygieneNachricht", foreground: java.awt.Color.RED, text: " ")
            }
        }
	
		// Rückschlagkappe
		panel(id: "anlageRuckschlagPanel", border: titledBorder(title: "Rückschlagkappe"), layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
			checkBox(id: "anlageRuckschlagkappe", text: "Lüftungsanlage mit Rückschlagkappe")
		}
	
		// Schallschutz-Kennzeichnung
		panel(id: "anlageSchallschutzPanel", border: titledBorder(title: "Schallschutz-Kennzeichnung"), layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
			checkBox(id: "anlageSchallschutz", text: "Lüftungsanlage mit Schallschutz")
		}
	
		// Feuerstätten-Kennzeichnung
		panel(id: "anlageFeuerstattePanel", border: titledBorder(title: "Feuerstätten-Kennzeichnung"), constraints: "wrap", layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
			checkBox(id: "anlageFeuerstatte", text: "Lüftungsanlage mit Sicherheitseinrichtung")
		}
	
		// Kennzeichnung der Lüftungsanlage
		panel(id: "anlageKennzeichnungPanel", border: titledBorder(title: "Kennzeichnung der Lüftungsanlage"), constraints: "span", layout: new MigLayout("fillx, wrap 1", "[fill]", "[fill]")) {
			label(id: "anlageKennzeichnungLuftungsanlage", foreground: java.awt.Color.RED)
		}
	
	}
	
}

// Bindings
build(AnlagendatenBindings)

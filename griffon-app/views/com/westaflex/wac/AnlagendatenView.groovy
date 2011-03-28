/**
 * /Users/rbe/project/wac2/griffon-app/views/com/westaflex/wac/AnlagendatenView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout

panel(layout: new MigLayout("fillx, wrap", "[grow]", "[fill]"), constraints: "grow") {
    panel(layout: new MigLayout("fillx", "[fill,grow] [fill,grow] [fill,grow]", "[fill] -8 []"), constraints: "grow") {
        // Gerätestandort
        panel(id: "anlageGeratestandortPanel", border: titledBorder(title: "Gerätestandort"), layout: new MigLayout("wrap 1", "[]")) {
            buttonGroup().with {
                add radioButton(id: "anlageGeratestandortKG", text: "Kellergeschoss")
                add radioButton(id: "anlageGeratestandortEG", text: "Erdgeschoss")
                add radioButton(id: "anlageGeratestandortOG", text: "Obergeschoss")
                add radioButton(id: "anlageGeratestandortDG", text: "Dachgeschoss")
                add radioButton(id: "anlageGeratestandortSB", text: "Spitzboden")
            }
            label("<html><p style='font-size: 9px;'>* Nur eine Auswahlmöglichkeit</p></html>", foreground: java.awt.Color.BLUE)
        }

        // Luftkanalverlegung
        panel(id: "anlageLuftkanalverlegungPanel", border: titledBorder(title: "Luftkanalverlegung"), layout: new MigLayout("wrap 1", "[]")) {
            label("Quadroflexsysteme 100 mit nur 60 mm Aufbauhöhe")
            checkBox(id: "anlageLuftkanalverlegungAufputz",     text: "Aufputz (Abkastung)")
            checkBox(id: "anlageLuftkanalverlegungDammschicht", text: "Dämmschicht unter Estrich")
            checkBox(id: "anlageLuftkanalverlegungDecke",       text: "Decke (abgehängt)")
            checkBox(id: "anlageLuftkanalverlegungSpitzboden",  text: "Spitzboden")
            label("<html><p style='font-size: 9px;'>* Mehrfachauswahl möglich</p></html>", foreground: java.awt.Color.BLUE)
        }

        // Außenluft
        panel(id: "anlageAussenluftPanel", border: titledBorder(title: "Außenluft"), constraints: "wrap", layout: new MigLayout("wrap 1", "[]")) {
            buttonGroup().with {
                add radioButton(id: "anlageAussenluftDach",     text: "Dachdurchführung")
                add radioButton(id: "anlageAussenluftWand",     text: "Wand (Luftgitter)")
                add radioButton(id: "anlageAussenluftErdwarme", text: "Erdwärmetauscher")
            }
            label("<html><p style='font-size: 9px;'>* Nur eine Auswahlmöglichkeit</p></html>", foreground: java.awt.Color.BLUE)
        }
    }
    panel(layout: new MigLayout("fillx", "[fill] [fill] [fill]", "[fill] -8 []"), constraints: "grow") {
        // Zuluftdurchlässe
        panel(id: "anlageZuluftPanel", border: titledBorder(title: "Zuluftdurchlässe"), layout: new MigLayout("wrap 1", "[]")) {
            checkBox(id: "anlageZuluftTellerventile",  text: "Tellerventile")
            checkBox(id: "anlageZuluftSchlitzauslass", text: "Schlitzauslass (Weitwurfdüse)")
            checkBox(id: "anlageZuluftFussboden",      text: "Fußbodenauslass")
            checkBox(id: "anlageZuluftSockel",         text: "Sockelquellauslass")
            label("<html><p style='font-size: 9px;'>* Mehrfachauswahl möglich</p></html>", foreground: java.awt.Color.BLUE)
        }

        // Abluftdurchlässe
        panel(id: "anlageAbluft", border: titledBorder(title: "Abluftdurchlässe"), layout: new MigLayout("wrap 1", "[]")) {
            checkBox(id: "anlageAbluftTellerventile",  text: "Tellerventile (Standard)")
        }

        // Fortluft
        panel(id: "anlageFortluftPanel", border: titledBorder(title: "Fortluft"), constraints: "wrap", layout: new MigLayout("wrap 1", "[]")) {
            buttonGroup().with {
                add radioButton(id: "anlageFortluftDach",         text: "Dachdurchführung")
                add radioButton(id: "anlageFortluftWand",         text: "Wand (Luftgitter)")
                add radioButton(id: "anlageFortluftBogen135",     text: "Bogen 135°")
            }
            label("<html><p style='font-size: 9px;'>* Nur eine Auswahlmöglichkeit</p></html>", foreground: java.awt.Color.BLUE)
        }
    }
    panel(layout: new MigLayout("fillx", "[fill] [fill]", "[fill] -8 []"), constraints: "grow, span") {
        // Energie-Kennzeichen
        panel(id: "anlageEnergiePanel", border: titledBorder(title: "Energie-Kennzeichen"), layout: new MigLayout("wrap 1", "[]")) {
            checkBox(id: "anlageEnergieZuAbluftWarme", text: "Zu-/Abluftgeräte mit Wärmerückgewinnung")
            checkBox(id: "anlageEnergieBemessung",     text: "Bemessung und Ausführung des Lüftungssystems")
            checkBox(id: "anlageEnergieRuckgewinnung", text: "Rückgewinnung von Abluftwärme")
            checkBox(id: "anlageEnergieRegelung",      text: "Zweckmäßige Relegung")
            label(id: "anlageEnergieNachricht", foreground: java.awt.Color.RED, text: " ")
            label("<html><p style='font-size: 9px;'>* Mehrfachauswahl möglich</p></html>", foreground: java.awt.Color.BLUE)
        }

        // Hygiene-Kennzeichen
        panel(id: "anlageHygienePanel", border: titledBorder(title: "Hygiene-Kennzeichen"), constraints: "span 2, wrap", layout: new MigLayout("wrap 1", "[]")) {
            checkBox(id: "anlageHygieneAusfuhrung",         text: "Ausführung und Lage der Außenluftansaugung")
            checkBox(id: "anlageHygieneFilterung",          text: "Filterung der Außenluft und der Abluft")
            checkBox(id: "anlageHygieneKeineVerschmutzung", text: "möglichst keine Verschmutzung des Luftleitungsnetzes")
            checkBox(id: "anlageHygieneDichtheitsklasseB",  text: "Dichtheitsklasse B der Luftleitungen")
            label(id: "anlageHygieneNachricht", foreground: java.awt.Color.RED, text: " ")
            label("<html><p style='font-size: 9px;'>* Mehrfachauswahl möglich</p></html>", foreground: java.awt.Color.BLUE)
        }
    }
    panel(layout: new MigLayout("fillx", "[fill] [fill] [fill]", "[fill] -8 []"), constraints: "grow") {
        // Rückschlagkappe
        panel(id: "anlageRuckschlagPanel", border: titledBorder(title: "Rückschlagkappe"), layout: new MigLayout("wrap 1", "[]")) {
            checkBox(id: "anlageruckschlagklappe", text: "Lüftungsanlage mit Rückschlagkappe")
        }

        // Schallschutz-Kennzeichnung
        panel(id: "anlageSchallschutzPanel", border: titledBorder(title: "Schallschutz-Kennzeichnung"), layout: new MigLayout("wrap 1", "[]")) {
            checkBox(id: "anlageSchallschutz", text: "Lüftungsanlage mit Schallschutz")
        }

        // Feuerstätten-Kennzeichnung
        panel(id: "anlageFeuerstattePanel", border: titledBorder(title: "Feuerstätten-Kennzeichnung"), constraints: "wrap", layout: new MigLayout("wrap 1", "[]")) {
            checkBox(id: "anlageFeuerstatte", text: "Lüftungsanlage mit Sicherheitseinrichtung")
        }
    }
    panel(layout: new MigLayout("fillx", "[fill] [fill] [fill]", "[fill] -8 []"), constraints: "grow") {
        // Kennzeichnung der Lüftungsanlage
        panel(id: "anlageKennzeichnungPanel", border: titledBorder(title: "Kennzeichnung der Lüftungsanlage"), constraints: "span", layout: new MigLayout("wrap 1", "[]")) {
            label(id: "anlageKennzeichnungLuftungsanlage", foreground: java.awt.Color.RED)
        }
    }
}

// Bindings
build(AnlagendatenBindings)

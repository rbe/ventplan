/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/com/westaflex/wac/GebaudedatenView.groovy
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

panel(constraints: "grow", layout: new MigLayout("fillx, wrap", "[fill]", "[fill]")) {
    panel(layout: new MigLayout("fillx, wrap", "[fill]para[fill]para[fill]", "[fill] -5 []"), constraints: "span, wrap") {
        // Gebäudetyp
        panel(id: "gebaudeTyp", border: titledBorder(title: "Gebäudetyp"), layout: new MigLayout("wrap", "[]", "")) {
            buttonGroup().with {
                add radioButton(id: "gebaudeTypMFH", text: "Mehrfamilienhaus MFH")
                add radioButton(id: "gebaudeTypEFH", text: "Einfamilienhaus EFH")
                add radioButton(id: "gebaudeTypMaisonette", text: "Maisonette")
            }
            label("<html><p style='font-size: 9px;'>* Nur eine Auswahlmöglichkeit</p></html>", foreground: java.awt.Color.BLUE)
        }
        // Gebäudelage
        panel(id: "gebaudeLage", border: titledBorder(title: "Gebäudelage"), layout: new MigLayout("wrap", "[]", "")) {
            buttonGroup().with {
                add radioButton(id: "gebaudeLageWindschwach", text: "windschwach")
                add radioButton(id: "gebaudeLageWindstark", text: "windstark")
            }
            label("<html><p style='font-size: 9px;'>* Nur eine Auswahlmöglichkeit</p></html>", foreground: java.awt.Color.BLUE)
        }
        // Wärmeschutz
        panel(id: "gebaudewarmeschutz", border: titledBorder(title: "Wärmeschutz"), layout: new MigLayout("wrap 1", "[]", "")) {
            buttonGroup().with {
                add radioButton(id: "gebaudeWarmeschutzHoch", text: "hoch (Neubau / Sanierung mind. WSchV 1995)")
                add radioButton(id: "gebaudeWarmeschutzNiedrig", text: "niedrig (Gebäude bestand vor 1995)")
            }
            label("<html><p style='font-size: 9px;'>* Nur eine Auswahlmöglichkeit</p></html>", foreground: java.awt.Color.BLUE)
        }
    }
    // Geometrie
    panel(layout: new MigLayout("fillx, wrap", "[fill]para[fill]", "[fill] -5 []"), constraints: "span, wrap") {
        panel(id: "gebaudeGeometrie", border: titledBorder(title: "Geometrie"), layout: new MigLayout("wrap 3", "[]para[]para[]", "")) {
            //
            textField(id: "gebaudeGeometrieWohnflache", constraints: "width 60px!")
            label("m²")
            label("Wohnfläche der Nutzungseinheit")
            //
            textField(id: "gebaudeGeometrieMittlereRaumhohe", constraints: "width 60px!")
            label("m")
            label("mittlere Raumhöhe")
            //
            textField(id: "gebaudeGeometrieLuftvolumen", editable: false, constraints: "width 60px!")
            label("m³")
            label("Luftvolumen der Nutzungseinheit")
            /* Auf Wunsch des Kunden entfernt, ist == gelüftetes Volumen
            textField(id: "gebaudeGeometrieGelufteteFlache", constraints: "width 60px!")
            label("m²")
            label("gelüftete Fläche")
            */
            //
            textField(id: "gebaudeGeometrieGeluftetesVolumen", editable: false, constraints: "width 60px!")
            label("m³")
            label("gelüftetes Volumen")
        }
        GH.recurse(gebaudeGeometrie, GH.doubleTextField)
        // Luftdichtheit der Gebäudehülle
        panel(id: "gebaudeLuftdichtheit", border: titledBorder(title: "Luftdichtheit der Gebäudehülle"), layout: new MigLayout("wrap 1", "[]para[]", "")) {
            buttonGroup().with {
                add radioButton(id: "gebaudeLuftdichtheitKategorieA", text: "Kategorie A (ventilatorgestützt)", constraints: "cell 0 1")
                add radioButton(id: "gebaudeLuftdichtheitKategorieB", text: "Kategorie B (frei, Neubau)", constraints: "cell 0 2")
                add radioButton(id: "gebaudeLuftdichtheitKategorieC", text: "Kategorie C (frei, Bestand)", constraints: "cell 0 3")

                add radioButton(id: "gebaudeLuftdichtheitMesswerte", text: "Messwerte", constraints: "cell 1 0")
                textField(id: "gebaudeLuftdichtheitDruckdifferenz", constraints: "width 80px!, cell 1 1")
                label("Druckdifferenz in Pa", constraints: "cell 2 1")
                textField(id: "gebaudeLuftdichtheitLuftwechsel", constraints: "width 80px!, cell 1 2")
                label("Luftwechsel in 1/h", constraints: "cell 2 2")
                textField(id: "gebaudeLuftdichtheitDruckexponent", constraints: "width 80px!, cell 1 3")
                label("Druckexponent", constraints: "cell 2 3")
            }
            label("<html><p style='font-size: 9px;'>* Nur eine Auswahlmöglichkeit</p></html>", foreground: java.awt.Color.BLUE)
        }
        GH.recurse(gebaudeLuftdichtheit, GH.doubleTextField)
    }
    // Besondere Anforderungen
    panel(layout: new MigLayout("fillx, wrap", "[fill]", "[fill] -5 []"), constraints: "span, wrap") {
        panel(id: "gebaudeBesondereAnforderungen", border: titledBorder(title: "Besondere Anforderungen"), constraints: "span", layout: new MigLayout("wrap 2", "[]para[]", "")) {
            textField(id: "faktorBesondereAnforderungen", constraints: "width 80px!")
            label("Faktor für besondere bauphysikalische oder hygienische Anforderungen")
        }
    }
    GH.doubleTextField(faktorBesondereAnforderungen)
    // Geplante Belegung
    panel(layout: new MigLayout("fillx, wrap", "[fill]", "[fill] -5 []"), constraints: "wrap") {
        panel(id: "gebaudeGeplanteBelegung", border: titledBorder(title: "Geplante Belegung"), constraints: "span", layout: new MigLayout("", "[]para[right]para[]", "")) {
            label("Personenanzahl")
            spinner(id: "gebaudeGeplantePersonenanzahl", constraints: "wrap, width 100px!")
            label("Außenluftvolumenstrom pro Person")
            spinner(id: "gebaudeGeplanteAussenluftVsProPerson", constraints: "wrap, width 100px!")
            //
            label("Mindestaußenluftrate:", foreground: java.awt.Color.RED)
            label(id: "gebaudeGeplanteMindestaussenluftrate", foreground: java.awt.Color.RED, text: "0", constraints: "right")
            label("m³/h", foreground: java.awt.Color.RED)
        }
    }
    GH.doubleTextField(gebaudeGeplanteAussenluftVsProPerson)
}
GH.selectAllTextField(gebaudeGeplantePersonenanzahl.editor.textField)
GH.yellowTextField(gebaudeGeplantePersonenanzahl.editor.textField)
GH.selectAllTextField(gebaudeGeplanteAussenluftVsProPerson.editor.textField)
GH.yellowTextField(gebaudeGeplanteAussenluftVsProPerson.editor.textField)
// Bindings
build(GebaudedatenBindings)

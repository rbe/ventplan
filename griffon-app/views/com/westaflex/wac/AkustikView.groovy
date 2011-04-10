/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/views/com/westaflex/wac/AkustikView.groovy
 * Last modified at 27.03.2011 19:30:22 by rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout

// Akustikberechnung
panel(layout: new MigLayout("fillx, wrap", "[fill]", "[fill]"), constraints: "grow") {

    panel(id: "akustikTabPanel", layout: new MigLayout("fill", "[grow]", ""), constraints: "grow") {
        // Tabellen für
        jideTabbedPane(id: "akustikTabGroup", constraints: "grow, span") {
            panel(id: "akustikZuluftTab", title: "Zuluft", layout: new MigLayout("fill", "[fill]", ""), constraints: "grow") {
                buildLayout("Zuluft")
            }
            panel(id: "akustikAbluftTab", title: "Abluft", layout: new MigLayout("fill", "[fill]", ""), constraints: "grow") {
                buildLayout("Abluft")
            }
        }
    }
}
// akustikTabGroup
akustikTabGroup.with {
	setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
}
// Format fields
GH.yellowTextField(akustikZuluftAnzahlUmlenkungen90GradStck)
GH.yellowTextField(akustikZuluftLuftverteilerkastenStck)
GH.yellowTextField(akustikZuluftLangsdampfungKanalLfdmMeter)
GH.yellowTextField(akustikAbluftAnzahlUmlenkungen90GradStck)
GH.yellowTextField(akustikAbluftLuftverteilerkastenStck)
GH.yellowTextField(akustikAbluftLangsdampfungKanalLfdmMeter)
// Bindings
build(AkustikBindings)

akustikZuluftTabelle.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF)
akustikZuluftTabelle.packColumn(0, 10, 100)
akustikZuluftTabelle.packColumn(1, 10, 100)
akustikZuluftTabelle.packColumn(2, 10, 100)
akustikZuluftTabelle.packColumn(3, 10, 100)
akustikZuluftTabelle.packColumn(4, 10, 100)
akustikZuluftTabelle.packColumn(5, 10, 100)

akustikAbluftTabelle.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF)
akustikAbluftTabelle.packColumn(0, 10, 80)
akustikAbluftTabelle.packColumn(1, 10, 80)
akustikAbluftTabelle.packColumn(2, 10, 80)
akustikAbluftTabelle.packColumn(3, 10, 80)
akustikAbluftTabelle.packColumn(4, 10, 80)
akustikAbluftTabelle.packColumn(5, 10, 80)

/**
 * Synchronize all Swing table models depending on map.raum.raume.
 */
def buildLayout(tabname) {
	def tabTitleForeground = tabname == "Zuluft" ? GH.MY_RED : java.awt.Color.BLUE
	// Akustikberechnung - Zuluft
    panel(layout: new MigLayout("fill, wrap", "[]", "[fill]"), constraints: "grow") {
        panel(layout: new MigLayout("wrap 3", "[left]5[center]5[left]", "[fill]"), constraints: "grow") {
            panel(layout: new MigLayout("fillx, wrap", "[fill]", "[fill]")) {
                label("Raumbezeichnung")

                comboBox(id: "akustik${tabname}Raumbezeichnung", items: model.meta.raum.typ)
            }
            panel(layout: new MigLayout("wrap", "[center]", "[]")) {

                panel() {
                    label("Zentrales Lüftungsgerät", foreground: tabTitleForeground)
                    label(id: "akustik${tabname}${tabname}Zentralgerat", foreground: tabTitleForeground)
                }

                label(tabname, foreground: tabTitleForeground)

                label("Oktavmittenfrequenz in Hz")
            }
            panel(layout: new MigLayout("fillx, wrap", "[fill]", "[fill]")) {
                label(" ")
            }

            panel(layout: new MigLayout("fillx", "[fill]para[fill]para[right]", "3[fill]-1")) {

                label(" ", constraints: "height 26px!, span 3, wrap")

                label("Schallleistungspegel ${tabname}stutzen", foreground: GH.MY_RED, constraints: "height 30px!")

                comboBox(id: "akustik${tabname}${tabname}stutzenZentralgerat", items: [""] + model.meta.zentralgerat, selectedItem: "")
                comboBox(id: "akustik${tabname}Pegel", constraints: "width 80px!, wrap", items: [""] + model.meta.volumenstromZentralgerat)

                label("Schallleistungspegelerhöhung Kanalnetz", foreground: GH.MY_RED, constraints: "height 30px!")
                comboBox(id: "akustik${tabname}Kanalnetz", constraints: "span 2, wrap", items: (10..200).step(10))


                label("Schallleistungspegelerhöhung Filter", foreground: GH.MY_RED, constraints: "height 30px!")
                comboBox(id: "akustik${tabname}Filter", constraints: "span 2, wrap", items: (10..200).step(10))

                label("1. Hauptschalldämpfer", foreground: GH.MY_GREEN, constraints: "height 30px!")
                switch (tabname) {
                    case "Zuluft":
                        comboBox(id: "akustik${tabname}1Hauptschalldampfer", constraints: "span 2, wrap", items: model.meta.akustik.schalldampfer, selectedItem: "100150TYP4A")
                        break
                    case "Abluft":
                        comboBox(id: "akustik${tabname}1Hauptschalldampfer", constraints: "span 2, wrap", items: model.meta.akustik.schalldampfer, selectedItem: "")
                        break
                }

                label("2. Hauptschalldämpfer", foreground: GH.MY_GREEN, constraints: "height 30px!")
                comboBox(id: "akustik${tabname}2Hauptschalldampfer", constraints: "span 2, wrap", items: model.meta.akustik.schalldampfer)

                label("Anzahl der Umlenkungen 90° Stck.", foreground: GH.MY_GREEN, constraints: "height 30px!")
                label("")
                textField(id: "akustik${tabname}AnzahlUmlenkungen90GradStck", constraints: "width 80px!, wrap")

                label("Luftverteilerkasten Stck.", foreground: GH.MY_GREEN, constraints: "height 30px!")
                label("")
                textField(id: "akustik${tabname}LuftverteilerkastenStck", constraints: "width 80px!, wrap")

                label("Längsdämpfung Kanal lfdm.", foreground: GH.MY_GREEN, constraints: "height 30px!")
                comboBox(id: "akustik${tabname}LangsdampfungKanal", items: model.meta.druckverlust.kanalnetz.kanalbezeichnung)
                textField(id: "akustik${tabname}LangsdampfungKanalLfdmMeter", constraints: "width 80px!, wrap")

                label("Schalldämpfer Ventil", foreground: GH.MY_GREEN, constraints: "height 30px!")
                comboBox(id: "akustik${tabname}SchalldampferVentil", constraints: "span 2, wrap", items: model.meta.akustik.schalldampfer)

                label("Einfügungsdämmwert Luftdurchlass", foreground: GH.MY_GREEN, constraints: "height 30px!")
                comboBox(id: "akustik${tabname}EinfugungsdammwertLuftdurchlass", constraints: "span 2, wrap", items: model.meta.druckverlust.ventileinstellung.ventilbezeichnung, selectedItem: "100ALSQ3W002")

                label("Raumabsorption (Annahme)", foreground: GH.MY_GREEN, constraints: "span 2, height 30px!")
                switch (tabname) {
                    case "Zuluft":
                        comboBox(id: "akustik${tabname}Raumabsorption", constraints: "wrap", items: ["BAD", "WOHNEN"], selectedItem: "WOHNEN")
                        break
                    case "Abluft":
                        comboBox(id: "akustik${tabname}Raumabsorption", constraints: "wrap", items: ["BAD", "WOHNEN"], selectedItem: "BAD")
                        break
                }

                label("Korrektur der A-Bewertung", constraints: "height 30px!, wrap")

                label("Bewerteter Schallpegel", constraints: "height 30px!, wrap")
            }
            panel(layout: new MigLayout("fill, wrap", "[right]", "[fill]")) {
                if (System.properties.'os.name'.contains("Windows")) {
                    jideScrollPane(constraints: "grow, height 500px!") {
                        def tm
                        switch (tabname) {
                            case "Zuluft":
                                tm = model.createAkustikZuluftTableModel()
                                break
                            case "Abluft":
                                tm = model.createAkustikAbluftTableModel()
                                break
                        }
                        table(id: "akustik${tabname}Tabelle", model: tm, selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                            current.setRowHeight(33)
                        }
                    }
                }
                else {
                    jideScrollPane(constraints: "grow, height 460px!") {
                        def tm
                        switch (tabname) {
                            case "Zuluft":
                                tm = model.createAkustikZuluftTableModel()
                                break
                            case "Abluft":
                                tm = model.createAkustikAbluftTableModel()
                                break
                        }
                        table(id: "akustik${tabname}Tabelle", model: tm, selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                            current.setRowHeight(33)
                        }
                    }
                }
            }
            panel(layout: new MigLayout("fillx, wrap", "[]", "[fill]")) {
                label("dB(A)")
                label(id: "akustik${tabname}dbA", text: "0,00", constraints: "height 30px!")
            }
            //panel() {
            //    label("")
            //}
//            panel(layout: new MigLayout("wrap", "[450:480:650, right]", "[]")) {
            panel(layout: new MigLayout("fillx, wrap", "[fill, right]", "[]"), constraints: "right, span 2") {
                label("Mittlerer Schalldruckpegel* dB(A) =", constraints: "right, wrap")
            }
            panel() {
                label(id: "akustik${tabname}MittlererSchalldruckpegel", text: "0,00", constraints: "left")
            }
            panel(layout: new MigLayout("wrap", "[right]", "[fill]"), constraints: "right, span 2") {

                label("<html>* Bei dieser Berechnung handelt es dich um eine theoretische Auslegung, deren Werte in der Praxis abweichen können</html>", constraints: "right, span 2")
            }
            panel() {
                label("")
            }
        }
    }
    return "akustik${tabname}Tab"
}

/**
 * /Users/rbe/project/wac2/griffon-app/views/com/westaflex/wac/AkustikView.groovy
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

// Akustikberechnung
panel(constraints: "grow", layout: new MigLayout("fillx, wrap", "[fill]", "[fill]")) {
    panel(id: "akustikTabPanel", layout: new MigLayout("fill", "[]", "")) {
        // Tabellen für
        jideTabbedPane(id: "akustikTabGroup", constraints: "grow, span") {
            panel(id: "akustikZuluftTab", title: "Zuluft", layout: new MigLayout("fill", "[]", "")) {
                buildLayout("Zuluft")
            }
            panel(id: "akustikAbluftTab", title: "Abluft", layout: new MigLayout("fill", "[]", "")) {
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
GH.recurse(akustikTabPanel, GH.yellowTextField)
// Bindings
build(AkustikBindings)

/**
 * Synchronize all Swing table models depending on map.raum.raume.
 */
def buildLayout(tabname) {
	def tabTitleForeground = tabname == "Zuluft" ? GH.MY_RED : java.awt.Color.BLUE
	// Akustikberechnung - Zuluft
    panel(layout: new MigLayout("fill, wrap", "[fill]", "[fill]")) {
        panel(layout: new MigLayout("wrap 3", "[left]10[right]10[left]", "[fill]")) {

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
        }
        panel(layout: new MigLayout("wrap 3", "[left][right]5[left]", "[fill]")) {
        //panel(layout: new MigLayout("debug, fill, wrap 3", "[left][right]5[left]", "[fill]")) {
            panel(layout: new MigLayout("fillx", "[fill]para[fill]para[right]", "3[fill]-1")) {

                label(" ", constraints: "height 26px!, span 3, wrap")

                label("Schallleistungspegel ${tabname}stutzen", foreground: GH.MY_RED, constraints: "height 30px!")

                comboBox(id: "akustik${tabname}${tabname}stutzenZentralgerat", items: [""] + model.meta.zentralgerat, selectedItem: "")
                comboBox(id: "akustik${tabname}Pegel", constraints: "width 70px!, wrap", items: [""] + model.meta.volumenstromZentralgerat)

                label("Schallleistungspegelerhöhung Kanalnetz", foreground: GH.MY_RED, constraints: "height 30px!")
                comboBox(id: "akustik${tabname}Kanalnetz", constraints: "span 2, wrap", items: (10..200).step(10))


                label("Schallleistungspegelerhöhung Filter", foreground: GH.MY_RED, constraints: "height 30px!")
                comboBox(id: "akustik${tabname}Filter", constraints: "span 2, wrap", items: (10..200).step(10))

                label("1. Hauptschalldämpfer", foreground: GH.MY_GREEN, constraints: "height 30px!")
                switch (tabname) {
                    case "Zuluft":
                        comboBox(id: "akustik${tabname}1Hauptschalldampfer", constraints: "span 2, wrap", items: model.meta.akustikSchalldampfer, selectedItem: "100150TYP4A")
                        break
                    case "Abluft":
                        comboBox(id: "akustik${tabname}1Hauptschalldampfer", constraints: "span 2, wrap", items: model.meta.akustikSchalldampfer, selectedItem: "")
                        break
                }

                label("2. Hauptschalldämpfer", foreground: GH.MY_GREEN, constraints: "height 30px!")
                comboBox(id: "akustik${tabname}2Hauptschalldampfer", constraints: "span 2, wrap", items: model.meta.akustikSchalldampfer)

                label("Anzahl der Umlenkungen 90° Stck.", foreground: GH.MY_GREEN, constraints: "height 30px!")
                label("")
                textField(id: "akustik${tabname}AnzahlUmlenkungen90GradStck", constraints: "width 70px!, wrap")

                label("Luftverteilerkasten Stck.", foreground: GH.MY_GREEN, constraints: "height 30px!")
                label("")
                textField(id: "akustik${tabname}LuftverteilerkastenStck", constraints: "width 70px!, wrap")

                label("Längsdämpfung Kanal lfdm.", foreground: GH.MY_GREEN, constraints: "height 30px!")
                comboBox(id: "akustik${tabname}LangsdampfungKanal", items: model.meta.dvbKanalbezeichnung)
                textField(id: "akustik${tabname}LangsdampfungKanalLfdmMeter", constraints: "width 70px!, wrap")

                label("Schalldämpfer Ventil", foreground: GH.MY_GREEN, constraints: "height 30px!")
                comboBox(id: "akustik${tabname}SchalldampferVentil", constraints: "span 2, wrap", items: model.meta.akustikSchalldampfer)

                label("Einfügungsdämmwert Luftdurchlass", foreground: GH.MY_GREEN, constraints: "height 30px!")
                comboBox(id: "akustik${tabname}EinfugungsdammwertLuftdurchlass", constraints: "span 2, wrap", items: model.meta.dvbVentileinstellung, selectedItem: "100ALSQ3W002")

                label("Raumabsorption (Annahme) BAD=0 WOHNEN=1", foreground: GH.MY_GREEN, constraints: "height 30px!")
                label("")
                switch (tabname) {
                    case "Zuluft":
                        textField(id: "akustik${tabname}Raumabsorption", constraints: "width 70px!, wrap", text: "1")
                        break
                    case "Abluft":
                        textField(id: "akustik${tabname}Raumabsorption", constraints: "width 70px!, wrap", text: "0")
                        break
                }

                label("Korrektur der A-Bewertung", constraints: "height 30px!, wrap")

                label("Bewerteter Schallpegel", constraints: "height 30px!, wrap")
            }
            panel(layout: new MigLayout("fill, wrap", "[fill,right]", "[fill]")) {
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
            panel() {
                label("")
            }
            panel(layout: new MigLayout("wrap", "[right]", "[fill]")) {
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


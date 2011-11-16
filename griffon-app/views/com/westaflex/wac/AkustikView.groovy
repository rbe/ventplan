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
import javax.swing.table.JTableHeader;

// Akustikberechnung
//panel(layout: new MigLayout("ins 0 n 0 n, fillx, wrap", "[fill]", "[fill]"), constraints: "grow") {
//zoneLayout {
//    zoneRow('z+*z')
//}
//panel(constraints: 'z', border: compoundBorder(outer: emptyBorder(5), inner: emptyBorder(5))) {
    jideTabbedPane(id: "akustikTabGroup", constraints: "grow, span") {
        panel(id: "akustikZuluftTab", constraints: "grow", title: "Zuluft", layout: new MigLayout("ins 0 n 0 n, fill", "[fill]", "")) {
            zoneLayout {
                zoneRow('y+*y')
            }
            buildLayout("Zuluft")
        }
        //}
        panel(id: "akustikAbluftTab", title: "Abluft", layout: new MigLayout("ins 0 n 0 n, fill", "[fill]", ""), constraints: "grow") {
            zoneLayout {
                zoneRow('y+*y')
            }
            buildLayout("Abluft")
        }
    }
//}

    /*panel(id: "akustikTabPanel", layout: new MigLayout("ins 0 n 0 n, fill", "[grow]", ""), constraints: "grow") {
        // Tabellen für
        jideTabbedPane(id: "akustikTabGroup", constraints: "grow, span") {
            panel(id: "akustikZuluftTab", title: "Zuluft", layout: new MigLayout("ins 0 n 0 n, fill", "[fill]", ""), constraints: "grow") {
                buildLayout("Zuluft")
            }
            panel(id: "akustikAbluftTab", title: "Abluft", layout: new MigLayout("ins 0 n 0 n, fill", "[fill]", ""), constraints: "grow") {
                buildLayout("Abluft")
            }
        }
    }
}*/
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
akustikZuluftTabelle.packColumn(0, 14, 100)
akustikZuluftTabelle.packColumn(1, 14, 100)
akustikZuluftTabelle.packColumn(2, 14, 100)
akustikZuluftTabelle.packColumn(3, 14, 100)
akustikZuluftTabelle.packColumn(4, 14, 100)
akustikZuluftTabelle.packColumn(5, 14, 100)

akustikAbluftTabelle.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF)
akustikAbluftTabelle.packColumn(0, 12, 80)
akustikAbluftTabelle.packColumn(1, 12, 80)
akustikAbluftTabelle.packColumn(2, 12, 80)
akustikAbluftTabelle.packColumn(3, 12, 80)
akustikAbluftTabelle.packColumn(4, 12, 80)
akustikAbluftTabelle.packColumn(5, 12, 80)


/**
 * Synchronize all Swing table models depending on map.raum.raume.
 */
def buildLayout(tabname) {
	def tabTitleForeground = tabname == "Zuluft" ? GH.MY_RED : java.awt.Color.BLUE

    panel(constraints: "y", border: compoundBorder(outer: emptyBorder(5), inner: emptyBorder(5))) {

        zl = zoneLayout {
            zoneRow('a<-.a2b-.bc>..c2d.......d1.....', template: 'headerrow')
            zoneRow('...............6...............')
            zoneRow('.....2.........2o........1p^<.p')
            zoneRow('e<-.e2f^-fg^>.g2.........1q^<.q')
            //zoneRow('h<-.hi^.-...i..............')
            zoneRow('j<-.j2k^.-....k2...............', template: 'row1')
            zoneRow('l<-.l2m^-mn^>-n2...............', template: 'row2')
            zoneRow('r<-.r2.........2........o......')
            zoneRow('...............6...............')
            zoneRow('s<-...........s2t>......t1u^<.u')
            zoneRow('...............6...............')
            zoneRow('v>......................v1.....')
        }

        zl.insertTemplate('headerrow')
        label("Raumbezeichnung", constraints: "a")
        panel(constraints: "d", border: compoundBorder(outer: emptyBorder(0), inner: emptyBorder(0))) {
            label("Zentrales Lüftungsgerät",    foreground: tabTitleForeground)
            label(id: "akustik${tabname}${tabname}Zentralgerat", foreground: tabTitleForeground)
        }

        zl.insertTemplate('headerrow')
        comboBox(id: "akustik${tabname}Raumbezeichnung", constraints: "a", items: model.meta.raum.typ)
        label(tabname, constraints: "d", foreground: tabTitleForeground)

        zl.insertTemplate('headerrow')
        label("Oktavmittenfrequenz in Hz", constraints: "d")


        label("Schallleistungspegel ${tabname}stutzen", foreground: GH.MY_RED, constraints: "e")
        comboBox(id: "akustik${tabname}${tabname}stutzenZentralgerat", constraints: "f", items: [""] + model.meta.zentralgerat, selectedItem: "")
        comboBox(id: "akustik${tabname}Pegel", constraints: "g", items: [""] + model.meta.volumenstromZentralgerat)

        scrollPane(constraints: "o") {
            def tm
            switch (tabname) {
                case "Zuluft":
                    tm = model.createAkustikZuluftTableModel()
                    break
                case "Abluft":
                    tm = model.createAkustikAbluftTableModel()
                    break
            }
            //table(id: "akustik${tabname}Tabelle", model: tm, selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
            table(id: "akustik${tabname}Tabelle", model: tm) {
                //current.setRowHeight(33)
                //current.setSortable(false)
                //current.getTableHeader().setDefaultRenderer(new JTableHeader().getDefaultRenderer())
                //current.setAutoCreateRowSorter(false)
                //current.setRowSorter(null)
            }
        }

        label("dB(A)", constraints: "p")
        label(id: "akustik${tabname}dbA", text: "0,00", constraints: "q")


        zl.insertTemplate('row1')
        label("Schallleistungspegelerhöhung Kanalnetz", foreground: GH.MY_RED, constraints: "j")
        comboBox(id: "akustik${tabname}Kanalnetz", constraints: "k", items: (10..200).step(10))

        zl.insertTemplate('row1')
        label("Schallleistungspegelerhöhung Filter", foreground: GH.MY_RED, constraints: "j")
        comboBox(id: "akustik${tabname}Filter", constraints: "k", items: (10..200).step(10))


        zl.insertTemplate('row1')
        label("1. Hauptschalldämpfer", foreground: GH.MY_GREEN, constraints: "j")
        switch (tabname) {
            case "Zuluft":
                comboBox(id: "akustik${tabname}1Hauptschalldampfer", constraints: "k", items: model.meta.akustik.schalldampfer, selectedItem: "100150TYP4A")
                break
            case "Abluft":
                comboBox(id: "akustik${tabname}1Hauptschalldampfer", constraints: "k", items: model.meta.akustik.schalldampfer, selectedItem: "")
                break
        }



        zl.insertTemplate('row1')
        label("2. Hauptschalldämpfer", foreground: GH.MY_GREEN, constraints: "j")
        comboBox(id: "akustik${tabname}2Hauptschalldampfer", constraints: "k", items: model.meta.akustik.schalldampfer)


        zl.insertTemplate('row2')
        label("Anzahl der Umlenkungen 90° Stck.", foreground: GH.MY_GREEN, constraints: "l")
        textField(id: "akustik${tabname}AnzahlUmlenkungen90GradStck", constraints: "n")

        zl.insertTemplate('row2')
        label("Luftverteilerkasten Stck.", foreground: GH.MY_GREEN, constraints: "l")
        textField(id: "akustik${tabname}LuftverteilerkastenStck", constraints: "n")


        zl.insertTemplate('row2')
        label("Längsdämpfung Kanal lfdm.", foreground: GH.MY_GREEN, constraints: "l")
        comboBox(id: "akustik${tabname}LangsdampfungKanal", constraints: "m", items: model.meta.druckverlust.kanalnetz.kanalbezeichnung)
        textField(id: "akustik${tabname}LangsdampfungKanalLfdmMeter", constraints: "n")


        zl.insertTemplate('row1')
        label("Schalldämpfer Ventil", foreground: GH.MY_GREEN, constraints: "j")
        comboBox(id: "akustik${tabname}SchalldampferVentil", constraints: "k", items: model.meta.akustik.schalldampfer)

        zl.insertTemplate('row1')
        label("Einfügungsdämmwert Luftdurchlass", foreground: GH.MY_GREEN, constraints: "j")
        comboBox(id: "akustik${tabname}EinfugungsdammwertLuftdurchlass", constraints: "k", items: model.meta.druckverlust.ventileinstellung.ventilbezeichnung, selectedItem: "100ALSQ3W002")


        zl.insertTemplate('row2')
        label("Raumabsorption (Annahme)", foreground: GH.MY_GREEN, constraints: "l")
        switch (tabname) {
            case "Zuluft":
                comboBox(id: "akustik${tabname}Raumabsorption", constraints: "n", items: ["BAD", "WOHNEN"], selectedItem: "WOHNEN")
                break
            case "Abluft":
                comboBox(id: "akustik${tabname}Raumabsorption", constraints: "n", items: ["BAD", "WOHNEN"], selectedItem: "BAD")
                break
        }

        zl.insertTemplate('row1')
        label("Korrektur der A-Bewertung", constraints: "j")


        label("Bewerteter Schallpegel", constraints: "s")
        label("Mittlerer Schalldruckpegel* dB(A) =", constraints: "t")
        label(id: "akustik${tabname}MittlererSchalldruckpegel", text: "0,00", constraints: "u")

        label("<html>* Bei dieser Berechnung handelt es dich um eine theoretische Auslegung, deren Werte in der Praxis abweichen können</html>", constraints: "v")
    }

    // Akustikberechnung - Zuluft
/*    panel(layout: new MigLayout("ins 0 n 0 n, fill, wrap", "[]", "[fill]"), constraints: "grow") {
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
                        //table(id: "akustik${tabname}Tabelle", model: tm, selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                        table(id: "akustik${tabname}Tabelle", model: tm) {
                            //current.setRowHeight(33)
                            //current.setSortable(false)
                            //current.getTableHeader().setDefaultRenderer(new JTableHeader().getDefaultRenderer())
                            //current.setAutoCreateRowSorter(false)
                            //current.setRowSorter(null)
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
                        //table(id: "akustik${tabname}Tabelle", model: tm, selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                        table(id: "akustik${tabname}Tabelle", model: tm) {
                            //current.setRowHeight(33)
                            //current.setSortable(false)
                            //current.getTableHeader().setDefaultRenderer(new JTableHeader().getDefaultRenderer())
                            //current.setAutoCreateRowSorter(false)
                            //current.setRowSorter(null)
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
    }*/
    return "akustik${tabname}Tab"
}

/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout
import javax.swing.table.JTableHeader;
import griffon.transform.Threading

jideTabbedPane(id: "akustikTabGroup", constraints: "grow, span") {
    panel(id: "akustikZuluftTab", constraints: "grow", title: "Zuluft", layout: new MigLayout("ins 0 n 0 n, fill", "[fill]", "")) {
        zoneLayout {
            zoneRow('y+*y')
        }
        //buildLayout("Zuluft")
        panel(constraints: "y", border: compoundBorder(outer: emptyBorder(5), inner: emptyBorder(5))) {

            zl = zoneLayout {
                // Die ersten 3 Zeilen
                zoneRow('a<-.a2b-.bc>..c2d.......d1.....', template: 'headerrow')
                // Folgezeile mit vertikalem Abstand von 6px
                zoneRow('...............5...............')
                // 1. Zeile. o = Tabelle
                zoneRow('...............2o|!......1p^<.p')
                zoneRow('e<-.e2f^-fg^>.g2.........1q^<.q')
                zoneRow('j<-.j2k^.-....k2...............', template: 'row1')
                zoneRow('l<-.l2m^-mn^>-n2...............', template: 'row2')
                zoneRow('7x<-..........x2...............', template: 'row3')
                // Tabelle o wird hier geschlossen
                zoneRow('r<-.r..........2........o......')
                zoneRow('...............6...............')
                zoneRow('s<-...........s2t>......t1u^<.u')
                zoneRow('...............6...............')
                zoneRow('v>......................v1.....')
            }

            zl.insertTemplate('headerrow')
            label("Raumbezeichnung", constraints: "a")
            panel(constraints: "d", border: compoundBorder(outer: emptyBorder(0), inner: emptyBorder(0))) {
                label("Zentrales Lüftungsgerät",    foreground: GH.MY_RED)
                label(id: "akustikZuluftZuluftZentralgerat", foreground: GH.MY_RED)
            }

            zl.insertTemplate('headerrow')
            comboBox(id: "akustikZuluftRaumbezeichnung", constraints: "a", items: model.meta.raum.typ)
            label("Zuluft", constraints: "d", foreground: GH.MY_RED)

            zl.insertTemplate('headerrow')
            label("Oktavmittenfrequenz in Hz", constraints: "d")


            label("Schallleistungspegel Zuluftstutzen", foreground: GH.MY_RED, constraints: "e")
            comboBox(id: "akustikZuluftZuluftstutzenZentralgerat", constraints: "f", items: [""] + model.meta.zentralgerat, selectedItem: "")
            comboBox(id: "akustikZuluftPegel", constraints: "g", items: [""] + model.meta.volumenstromZentralgerat, selectedItem: "")

            scrollPane(constraints: "o") {
                table(id: "akustikZuluftTabelle", model: model.createAkustikZuluftTableModel()) {
                    current.setSortable(false)
                    current.getTableHeader().setDefaultRenderer(new JTableHeader().getDefaultRenderer())
                    current.setAutoCreateRowSorter(false)
                    current.setRowSorter(null)
                    current.setFillsViewportHeight(true)
                }
            }

            label("dB(A)", constraints: "p")
            label(id: "akustikZuluftdbA", text: "0,00", constraints: "q")


            zl.insertTemplate('row1')
            label("Schallleistungspegelerhöhung Kanalnetz", foreground: GH.MY_RED, constraints: "j")
            comboBox(id: "akustikZuluftKanalnetz", constraints: "k", items: (10..200).step(10))

            zl.insertTemplate('row1')
            label("Schallleistungspegelerhöhung Filter", foreground: GH.MY_RED, constraints: "j")
            comboBox(id: "akustikZuluftFilter", constraints: "k", items: (10..200).step(10))


            zl.insertTemplate('row1')
            label("1. Hauptschalldämpfer", foreground: GH.MY_GREEN, constraints: "j")
            comboBox(id: "akustikZuluft1Hauptschalldampfer", constraints: "k", items: model.meta.akustik.schalldampfer, selectedItem: "100150TYP4A")


            zl.insertTemplate('row1')
            label("2. Hauptschalldämpfer", foreground: GH.MY_GREEN, constraints: "j")
            comboBox(id: "akustikZuluft2Hauptschalldampfer", constraints: "k", items: model.meta.akustik.schalldampfer)


            zl.insertTemplate('row2')
            label("Anzahl der Umlenkungen 90° Stck.", foreground: GH.MY_GREEN, constraints: "l")
            textField(id: "akustikZuluftAnzahlUmlenkungen90GradStck", constraints: "n")

            zl.insertTemplate('row2')
            label("Luftverteilerkasten Stck.", foreground: GH.MY_GREEN, constraints: "l")
            textField(id: "akustikZuluftLuftverteilerkastenStck", constraints: "n")


            zl.insertTemplate('row2')
            label("Längsdämpfung Kanal lfdm.", foreground: GH.MY_GREEN, constraints: "l")
            comboBox(id: "akustikZuluftLangsdampfungKanal", constraints: "m", items: model.meta.druckverlust.kanalnetz.kanalbezeichnung)
            textField(id: "akustikZuluftLangsdampfungKanalLfdmMeter", constraints: "n")


            zl.insertTemplate('row1')
            label("Schalldämpfer Ventil", foreground: GH.MY_GREEN, constraints: "j")
            comboBox(id: "akustikZuluftSchalldampferVentil", constraints: "k", items: model.meta.akustik.schalldampfer)

            zl.insertTemplate('row1')
            label("Einfügungsdämmwert Luftdurchlass", foreground: GH.MY_GREEN, constraints: "j")
            comboBox(id: "akustikZuluftEinfugungsdammwertLuftdurchlass", constraints: "k", items: model.meta.druckverlust.ventileinstellung.ventilbezeichnung, selectedItem: "100ALSQ3W002")


            zl.insertTemplate('row2')
            label("Raumabsorption (Annahme)", foreground: GH.MY_GREEN, constraints: "l")
            comboBox(id: "akustikZuluftRaumabsorption", constraints: "n", items: ["BAD", "WOHNEN"], selectedItem: "WOHNEN")

            zl.insertTemplate('row1')
            label("Korrektur der A-Bewertung", constraints: "j")

            zl.insertTemplate('row3')
            label(' ', constraints: 'x') // TODO: rbe -> kommt hier vielleicht 'Bewerteter Schallpegel' hin?

            zl.insertTemplate('row3')
            label(' ', constraints: 'x')

            label('Bewerteter Schallpegel', constraints: 's')
            label("Mittlerer Schalldruckpegel* dB(A) =", constraints: "t")
            label(id: "akustikZuluftMittlererSchalldruckpegel", text: "0,00", constraints: "u")

            label("<html>* Bei dieser Berechnung handelt es dich um eine theoretische Auslegung, deren Werte in der Praxis abweichen können</html>", constraints: "v")
        }
    }
    //}
    panel(id: "akustikAbluftTab", title: "Abluft", layout: new MigLayout("ins 0 n 0 n, fill", "[fill]", ""), constraints: "grow") {
        zoneLayout {
            zoneRow('y+*y')
        }
        //buildLayout("Abluft")
        panel(constraints: "y", border: compoundBorder(outer: emptyBorder(5), inner: emptyBorder(5))) {

            zl = zoneLayout {
                // Die ersten 3 Zeilen
                zoneRow('a<-.a2b-.bc>..c2d.......d1.....', template: 'headerrow')
                // Folgezeile mit vertikalem Abstand von 6px
                zoneRow('...............5...............')
                // 1. Zeile. o = Tabelle
                zoneRow('...............2o|!......1p^<.p')
                zoneRow('e<-.e2f^-fg^>.g2.........1q^<.q')
                zoneRow('j<-.j2k^.-....k2...............', template: 'row1')
                zoneRow('l<-.l2m^-mn^>-n2...............', template: 'row2')
                zoneRow('7x<-..........x2...............', template: 'row3')
                // Tabelle o wird hier geschlossen
                zoneRow('r<-.r..........2........o......')
                zoneRow('...............6...............')
                zoneRow('s<-...........s2t>......t1u^<.u')
                zoneRow('...............6...............')
                zoneRow('v>......................v1.....')
            }

            zl.insertTemplate('headerrow')
            label("Raumbezeichnung", constraints: "a")
            panel(constraints: "d", border: compoundBorder(outer: emptyBorder(0), inner: emptyBorder(0))) {
                label("Zentrales Lüftungsgerät",    foreground: java.awt.Color.BLUE)
                label(id: "akustikAbluftAbluftZentralgerat", foreground: java.awt.Color.BLUE)
            }

            zl.insertTemplate('headerrow')
            comboBox(id: "akustikAbluftRaumbezeichnung", constraints: "a", items: model.meta.raum.typ)
            label("Abluft", constraints: "d", foreground: java.awt.Color.BLUE)

            zl.insertTemplate('headerrow')
            label("Oktavmittenfrequenz in Hz", constraints: "d")


            label("Schallleistungspegel Abluftstutzen", foreground: GH.MY_RED, constraints: "e")
            comboBox(id: "akustikAbluftAbluftstutzenZentralgerat", constraints: "f", items: [""] + model.meta.zentralgerat, selectedItem: "")
            comboBox(id: "akustikAbluftPegel", constraints: "g", items: [""] + model.meta.volumenstromZentralgerat, selectedItem: "")

            scrollPane(constraints: "o") {
                def tm = model.createAkustikAbluftTableModel()
                table(id: "akustikAbluftTabelle", model: tm) {
                    current.setSortable(false)
                    current.getTableHeader().setDefaultRenderer(new JTableHeader().getDefaultRenderer())
                    current.setAutoCreateRowSorter(false)
                    current.setRowSorter(null)
                    current.setFillsViewportHeight(true)
                }
            }

            label("dB(A)", constraints: "p")
            label(id: "akustikAbluftdbA", text: "0,00", constraints: "q")


            zl.insertTemplate('row1')
            label("Schallleistungspegelerhöhung Kanalnetz", foreground: GH.MY_RED, constraints: "j")
            comboBox(id: "akustikAbluftKanalnetz", constraints: "k", items: (10..200).step(10))

            zl.insertTemplate('row1')
            label("Schallleistungspegelerhöhung Filter", foreground: GH.MY_RED, constraints: "j")
            comboBox(id: "akustikAbluftFilter", constraints: "k", items: (10..200).step(10))


            zl.insertTemplate('row1')
            label("1. Hauptschalldämpfer", foreground: GH.MY_GREEN, constraints: "j")
            comboBox(id: "akustikAbluft1Hauptschalldampfer", constraints: "k", items: model.meta.akustik.schalldampfer, selectedItem: "100150TYP4A")


            zl.insertTemplate('row1')
            label("2. Hauptschalldämpfer", foreground: GH.MY_GREEN, constraints: "j")
            comboBox(id: "akustikAbluft2Hauptschalldampfer", constraints: "k", items: model.meta.akustik.schalldampfer)


            zl.insertTemplate('row2')
            label("Anzahl der Umlenkungen 90° Stck.", foreground: GH.MY_GREEN, constraints: "l")
            textField(id: "akustikAbluftAnzahlUmlenkungen90GradStck", constraints: "n")

            zl.insertTemplate('row2')
            label("Luftverteilerkasten Stck.", foreground: GH.MY_GREEN, constraints: "l")
            textField(id: "akustikAbluftLuftverteilerkastenStck", constraints: "n")


            zl.insertTemplate('row2')
            label("Längsdämpfung Kanal lfdm.", foreground: GH.MY_GREEN, constraints: "l")
            comboBox(id: "akustikAbluftLangsdampfungKanal", constraints: "m", items: model.meta.druckverlust.kanalnetz.kanalbezeichnung)
            textField(id: "akustikAbluftLangsdampfungKanalLfdmMeter", constraints: "n")


            zl.insertTemplate('row1')
            label("Schalldämpfer Ventil", foreground: GH.MY_GREEN, constraints: "j")
            comboBox(id: "akustikAbluftSchalldampferVentil", constraints: "k", items: model.meta.akustik.schalldampfer)

            zl.insertTemplate('row1')
            label("Einfügungsdämmwert Luftdurchlass", foreground: GH.MY_GREEN, constraints: "j")
            comboBox(id: "akustikAbluftEinfugungsdammwertLuftdurchlass", constraints: "k", items: model.meta.druckverlust.ventileinstellung.ventilbezeichnung, selectedItem: "100ALSQ3W002")


            zl.insertTemplate('row2')
            label("Raumabsorption (Annahme)", foreground: GH.MY_GREEN, constraints: "l")
            comboBox(id: "akustikAbluftRaumabsorption", constraints: "n", items: ["BAD", "WOHNEN"], selectedItem: "BAD")

            zl.insertTemplate('row1')
            label("Korrektur der A-Bewertung", constraints: "j")

            zl.insertTemplate('row3')
            label(' ', constraints: 'x') // TODO: rbe -> kommt hier vielleicht 'Bewerteter Schallpegel' hin?

            zl.insertTemplate('row3')
            label(' ', constraints: 'x')


            label("Bewerteter Schallpegel", constraints: "s")
            label("Mittlerer Schalldruckpegel* dB(A) =", constraints: "t")
            label(id: "akustikAbluftMittlererSchalldruckpegel", text: "0,00", constraints: "u")

            label("<html>* Bei dieser Berechnung handelt es dich um eine theoretische Auslegung, deren Werte in der Praxis abweichen können</html>", constraints: "v")
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

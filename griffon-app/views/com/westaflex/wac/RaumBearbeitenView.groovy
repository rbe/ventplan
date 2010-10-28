/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/com/westaflex/wac/RaumBearbeitenDialog.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout

// RaumdatenDialogView
jideScrollPane(id: "raumBearbeitenScrollPane") {
    panel(id: "raumBearbeitenTabPanel", layout: new MigLayout("", "[]", "[]")) {
        panel(layout: new MigLayout("wrap", "[center]", "[]")) {
            //jideTabbedPane(id: "raumBearbeitenTabGroup") {
            panel(id: "raumBearbeitenDetailsTab", border: titledBorder("Details")) {
                    // RaumdatenDialog - Details ...
                    //panel(id: "raumBearbeitenDetailsTab", border: titledBorder("Details")) {
                        //borderLayout()
                panel(id: "raumVsZuAbluftventileTabellePanel", layout: new MigLayout("", "[fill]", "[]")) {
                    panel(id: "raumBearbeiten", border: titledBorder("Raum"), layout: new MigLayout("", "[left]para[right]para[left]para[left]para[left,fill]para[left,fill]para[left]"), constraints: "span") {
                        label(id: "", text: "Geschoss")
                        label("")
                        label(id: "", text: "Raumnummer", constraints: "span 2")
                        label(id: "", text: "Raumname")
                        label(id: "", text: "Raumtyp")
                        label("", constraints: "wrap")

                        comboBox(id: "raumBearbeitenRaumGeschoss", items: model.meta.raum.geschoss)
                        button(id: "raumBearbeitenRaumLinks", text: " < ")
                        textField(id: "raumBearbeitenRaumnummer", constraints: "width 50px")
                        button(id: "raumBearbeitenRaumRechts", text: " > ")
                        textField(id: "raumBearbeitenBezeichnung", text: "", constraints: "width 100px")
                        comboBox(id: "raumBearbeitenRaumtyp", items: model.meta.raum.typ)
                        button(id: "raumdatenDialogRaumButton", text: "...")
                    }

                    panel(id: "raumBearbeitenLuftartPanel", border: titledBorder("Luftart"), constraints: "span", layout: new MigLayout("", "[]para[]para[]", "")) {
                        comboBox(id: "raumBearbeitenLuftart", constraints: "width 100px", items: model.meta.raum.luftart, enabled: false)
                        textField(id: "raumBearbeitenLuftartFaktorZuluftverteilung", text: "", constraints: "width 100px")
                        label(id: "raumBearbeitenFaktorZuluftverteilungLabel", text: "Faktor Zuluftverteilung", constraints: "wrap")

                        label("")
                        textField(id: "raumBearbeitenLuftartAbluftVs", text: "", constraints: "width 100px")
                        label(id: "raumBearbeitenLuftartAbluftVsLabel", text: "Abluftvolumentstrom in m³/h", constraints: "cell 2 1")
                    }

                    panel(id: "raumBearbeitenTabelle", layout: new MigLayout("", "[left]para[left]para[left]", "[]"), constraints: "span") {
                        label(text: "Maximale Türspalthöhe [mm]")
                        textField(id: "raumBearbeitenDetailsTurspalthohe", text: "10,00", constraints: "width 100px")
                        button(id: "raumBearbeitenDetailsTurentfernen", text: "Tür entfernen", constraints: "wrap")

                        jideScrollPane(constraints: "height 150px, span") {
                            table(id: "raumBearbeitenTurenTabelle", model: model.createRaumTurenTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
                            }
                        }
                    }

                    panel(id: "raumBearbeitenOptionalMain", border: titledBorder("Optional"), constraints: "span", layout: new MigLayout("", "[fill]para[fill]para[fill]", "")) {
                        panel(id: "raumBearbeitenOptional", layout: new MigLayout("", "[left]para[right]para[left]para[left]para[right]para[left]para[left]para[right]para[left]"), constraints: "span") {
                            label(text: "Raumlänge")
                            textField(id: "raumBearbeitenOptionalRaumlange", constraints: "width 100px")
                            label(text: "m")
                            label(text: "Raumbreite")
                            textField(id: "raumBearbeitenOptionalRaumbreite", constraints: "width 100px")
                            label(text: "m")
                            label(text: "Raumhöhe")
                            textField(id: "raumBearbeitenOptionalRaumhohe", constraints: "width 100px")
                            label(text: "m", constraints: "wrap")

                            label(text: "Raumfläche")
                            textField(id: "raumBearbeitenOptionalRaumflache", constraints: "width 100px")
                            label(text: "m²")
                            label(text: "Raumvolumen")
                            textField(id: "raumBearbeitenOptionalRaumvolumen", constraints: "width 100px")
                            label(text: "m³")
                            label("")
                            label("")
                            label("")
                        }

                        panel(id: "raumBearbeitenOptionalSub", constraints: "span", layout: new MigLayout("fill", "[left]para[right]", "")) {
                            panel(id: "raumBearbeitenDurchlassposition", border: titledBorder("Durchlassposition"), layout: new MigLayout("", "[left,fill]para[left,fill]para[left,fill]", "[]")) {
                                button(id: "raumBearbeitenDurchlasspositionInfo", text: "Info...", constraints: "cell 0 0")
                                label(text: "Zuluft", constraints: "cell 1 0")
                                buttonGroup().with {
                                    add radioButton(id: "raumBearbeitenDurchlasspositionZuluftDecke", text: "Decke", constraints: "cell 1 1")
                                    add radioButton(id: "raumBearbeitenDurchlasspositionZuluftWandOben", text: "Wand oben", constraints: "cell 1 2")
                                    add radioButton(id: "raumBearbeitenDurchlasspositionZuluftWandUnten", text: "Wand unten", constraints: "cell 1 3")
                                    add radioButton(id: "raumBearbeitenDurchlasspositionZuluftBoden", text: "Boden", constraints: "cell 1 4")
                                }
                                label(text: "Abluft", constraints: "cell 2 0")
                                buttonGroup().with {
                                    add radioButton(id: "raumBearbeitenDurchlasspositionAbluftDecke", text: "Decke", constraints: "cell 2 1")
                                    add radioButton(id: "raumBearbeitenDurchlasspositionAbluftWandOben", text: "Wand oben", constraints: "cell 2 2")
                                    add radioButton(id: "raumBearbeitenDurchlasspositionAbluftWandUnten", text: "Wand unten", constraints: "cell 2 3")
                                    add radioButton(id: "raumBearbeitenDurchlasspositionAbluftBoden", text: "Boden", constraints: "cell 2 4")
                                }
                            }
                            panel(id: "raumBearbeitenKanalanschluss", border: titledBorder("Kanalanschluss"), constraints: "wrap", layout: new MigLayout("", "[left,fill]para[left,fill]para[left,fill]", "[]")) {
                                button(id: "raumBearbeitenKanalanschlussInfo", text: "Info...", constraints: "cell 0 0")
                                label(text: "Zuluft", constraints: "cell 1 0")
                                buttonGroup().with {
                                    add radioButton(id: "raumBearbeitenKanalanschlussZuluftDecke", text: "Decke", constraints: "cell 1 1")
                                    add radioButton(id: "raumBearbeitenKanalanschlussZuluftWandOben", text: "Wand oben", constraints: "cell 1 2")
                                    add radioButton(id: "raumBearbeitenKanalanschlussZuluftWandUnten", text: "Wand unten", constraints: "cell 1 3")
                                    add radioButton(id: "raumBearbeitenKanalanschlussZuluftBoden", text: "Boden", constraints: "cell 1 4")
                                }
                                label(text: "Abluft", constraints: "cell 2 0")
                                buttonGroup().with {
                                    add radioButton(id: "raumBearbeitenKanalanschlussAbluftDecke", text: "Decke", constraints: "cell 2 1")
                                    add radioButton(id: "raumBearbeitenKanalanschlussAbluftWandOben", text: "Wand oben", constraints: "cell 2 2")
                                    add radioButton(id: "raumBearbeitenKanalanschlussAbluftWandUnten", text: "Wand unten", constraints: "cell 2 3")
                                    add radioButton(id: "raumBearbeitenKanalanschlussAbluftBoden", text: "Boden", constraints: "cell 2 4")
                                }
                            }
                        }
                    }
                        //}
                }
            }
            panel(id: "raumBearbeitenSubPanel2") {
                button(id: "raumBearbeitenSchliessen", text: "Schliessen")
            }
        }
    }

}
//}

// Format fields
GH.recurse(raumBearbeitenTabPanel, GH.yellowTextField)
// Bindings
build(RaumBearbeitenBindings)

raumBearbeitenScrollPane.setHorizontalScrollBarPolicy(com.jidesoft.swing.JideScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
raumBearbeitenScrollPane.setVerticalScrollBarPolicy(com.jidesoft.swing.JideScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

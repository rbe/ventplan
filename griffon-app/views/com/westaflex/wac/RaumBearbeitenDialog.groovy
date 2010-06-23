/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/com/westaflex/wac/RaumBearbeitenDialog.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout
//import groovy.swing.SwingBuilder
//import javax.swing.JFrame

dialog(id: "raumBearbeitenDialog", title: "Raum bearbeiten",
	visible: false, modal: true, pack: true, locationByPlatform: true) {
	// RaumdatenDialogView
	panel(id: "raumBearbeitenTabPanel") {
		jideTabbedPane(id: "raumBearbeitenTabGroup") {

			// RaumdatenDialog - Details ... , layout: new MigLayout("fillx, wrap 2", "[fill],[fill]")
			panel(id: "raumBearbeitenDetailsTab", title: "Details", layout: new MigLayout("fillx, wrap 2", "[fill]", "[fill]")) {

				panel(id: "raumBearbeiten", border: titledBorder("Raum"), layout: new MigLayout("fillx", "[left]para[right]para[left]para[left]para[left,fill]para[left,fill]para[left]"), constraints: "span") {
					label(id: "", text: "Geschoss")
					label("")
					label(id: "", text: "Raumnummer", constraints: "span 2")
					label(id: "", text: "Raumname")
					label(id: "", text: "Raumtyp")
					label("", constraints: "wrap")

					comboBox(id: "raumBearbeitenRaumGeschoss", items: model.meta.raum.geschoss, selectedItem: bind { model.meta.gewahlterRaum?.geschoss })
					button(id: "links", text: " < ")
					textField(id: "raumBearbeitenRaumnummer", text: "01")
					button(id: "rechts", text: " > ")
					textField(id: "raumBearbeitenBezeichnung", text: "raumbez")
					comboBox(id: "raumBearbeitenRaumtyp")
					button(id: "raumdatenDialogRaumButton", text: "...")
				}

				panel(id: "raumBearbeitenLuftart", border: titledBorder("Luftart"), constraints: "span") {
					comboBox(id: "raumBearbeitenLuftartCombo", constraints: "cell 0 0 2 1")
					textField(id: "raumBearbeitenLuftartFaktorZuluftverteilung", text: "", constraints: "cell 1 0")
					label(id: "raumBearbeitenLuftartCombo", text: "Faktor Zuluftverteilung", constraints: "cell 2 0")

					textField(id: "raumBearbeitenLuftartAbluftVs", text: "", constraints: "cell 1 1")
					label(id: "raumBearbeitenLuftartCombo", text: "Abluftvolumentstrom in m³/h", constraints: "cell 2 1")
				}

				panel(id: "raumBearbeitenDurchlassposition", border: titledBorder("Durchlassposition"), layout: new MigLayout("fillx", "[left,fill]para[left,fill]para[left,fill]", "[fill]")) {
					button(id: "raumBearbeitenDurchlasspositionInfo", text: "Info...")
					label(text: "Zuluft")
					label(text: "Abluft")

					buttonGroup().with {
						add radioButton(id: "raumBearbeitenDurchlasspositionZuluftDecke", text: "Decke", constraints: "wrap")
						add radioButton(id: "raumBearbeitenDurchlasspositionZuluftWandOben", text: "Wand oben", constraints: "wrap")
						add radioButton(id: "raumBearbeitenDurchlasspositionZuluftWandUnten", text: "Wand unten", constraints: "wrap")
						add radioButton(id: "raumBearbeitenDurchlasspositionZuluftBoden", text: "Boden", constraints: "wrap")
					}

					buttonGroup().with {
						add radioButton(id: "raumBearbeitenDurchlasspositionAbluftDecke", text: "Decke", constraints: "wrap")
						add radioButton(id: "raumBearbeitenDurchlasspositionAbluftWandOben", text: "Wand oben", constraints: "wrap")
						add radioButton(id: "raumBearbeitenDurchlasspositionAbluftWandUnten", text: "Wand unten", constraints: "wrap")
						add radioButton(id: "raumBearbeitenDurchlasspositionAbluftBoden", text: "Boden", constraints: "wrap")
					}
				}

				panel(id: "raumBearbeitenKanalanschluss", border: titledBorder("Kanalanschluss"), layout: new MigLayout("fillx", "[left,fill]para[left,fill]para[left,fill]", "[fill]")) {
					button(id: "raumBearbeitenKanalanschlussInfo", text: "Info...")
					label(text: "Zuluft")
					label(text: "Abluft")

					buttonGroup().with {
						add radioButton(id: "raumBearbeitenKanalanschlussZuluftDecke", text: "Decke", constraints: "wrap")
						add radioButton(id: "raumBearbeitenKanalanschlussZuluftWandOben", text: "Wand oben", constraints: "wrap")
						add radioButton(id: "raumBearbeitenKanalanschlussZuluftWandUnten", text: "Wand unten", constraints: "wrap")
						add radioButton(id: "raumBearbeitenKanalanschlussZuluftBoden", text: "Boden", constraints: "wrap")
					}

					buttonGroup().with {
						add radioButton(id: "raumBearbeitenKanalanschlussAbluftDecke", text: "Decke", constraints: "wrap")
						add radioButton(id: "raumBearbeitenKanalanschlussAbluftWandOben", text: "Wand oben", constraints: "wrap")
						add radioButton(id: "raumBearbeitenKanalanschlussAbluftWandUnten", text: "Wand unten", constraints: "wrap")
						add radioButton(id: "raumBearbeitenKanalanschlussAbluftBoden", text: "Boden", constraints: "wrap")
					}
				}

				panel(id: "raumBearbeitenTabelle", layout: new MigLayout("fillx", "[left]para[left]para[left]", "[fill]"), constraints: "span") {

					label(text: "Maximale Türspalthöhe [mm]")
					textField(id: "raumBearbeitenDetailsTurspalthohe", text: "10.0")
					button(id: "raumBearbeitenDetailsTurentfernen", text: "Tür entfernen", constraints: "wrap")

					jideScrollPane() {
						//table(id: "raumBearbeitenDetailsTabelle", constraints: "span", model: model.createRaumEinstellungenTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
						//}
					}
				}

				panel(id: "raumBearbeitenOptional", border: titledBorder("Optional"), layout: new MigLayout("fillx", "[left]para[right]para[left]para[left]para[right]para[left]para[left]para[right]para[left]"), constraints: "span") {
					label(text: "Raumlänge")
					textField(id: "raumBearbeitenOptionalRaumlange")
					label(text: "m")
					label(text: "Raumbreite")
					textField(id: "raumBearbeitenOptionalRaumbreite")
					label(text: "m")
					label(text: "Raumhöhe")
					textField(id: "raumBearbeitenOptionalRaumhohe")
					label(text: "m", constraints: "wrap")

					label(text: "Raumfläche")
					textField(id: "raumBearbeitenOptionalRaumflache")
					label(text: "m²")
					label(text: "Raumvolumen")
					textField(id: "raumBearbeitenOptionalRaumvolumen")
					label(text: "m³")
					label("")
					label("")
					label("")
				}
			}

			// RaumdatenDialog - Zusammenfassung
			panel(id: "raumBearbeitenZusammenfassungTab", title: "Zusammenfassung") {
				jideScrollPane() {
					// TODO...
					//table(id: "raumEinstellungenTabelle", model: model.createRaumEinstellungenTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
					//}
				}
			}

		}
	}
}
// Bindings
bean(raumBearbeitenBezeichnung, text: bind(source: model.map.gewahlterRaum, sourceProperty: "raumBezeichnung"))
//bind(source: model.meta.gewahlterRaum, sourceProperty: "raumBezeichnung", target: "raumBearbeitenBezeichnung", targetProperty: "text")

return raumBearbeitenDialog

/*
class RaumdatenDialogView {
	public RaumdatenDialogView(def row) { 
		SwingBuilder swing = new SwingBuilder()
		swing.frame( title: "GroovyFileViewer", 
					 defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE, 
					 pack: false, visible: true, id: "raumdatendialogframe" ) { 
			// TODO: hier einfügen...
			//button("Click to select a file", constraints: context.SOUTH, actionPerformed: this.&selectFile)           
		}
		swing.frame.size = [800,600]
	}
}
*/

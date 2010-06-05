package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH
import net.miginfocom.swing.MigLayout
//import groovy.swing.SwingBuilder
//import javax.swing.JFrame


// RaumdatenDialogView
panel(id: "raumDialogTabPanel") {
	jideTabbedPane(id: "raumDialogTabGroup") {
		
		// RaumdatenDialog - Details ... , layout: new MigLayout("fillx, wrap 2", "[fill],[fill]")
		panel(id: "raumDialogDetailsTab", title: "Details", layout: new MigLayout("fillx, wrap 2", "[fill]", "[fill]")) {
			
			panel(id: "raumDialog", border: titledBorder("Raum"), layout: new MigLayout("fillx", "[left]para[right]para[left]para[left]para[left,fill]para[left,fill]para[left]"), constraints: "span") {
				label(id: "", text: "Geschoss")
				label("")
				label(id: "", text: "Raumnummer", constraints: "span 2")
				label(id: "", text: "Raumname")
				label(id: "", text: "Raumtyp")
				label("", constraints: "wrap")
				
				comboBox(id: "raumDialogRaumGeschoss")
				button(id: "links", text: " < ")
				textField(id: "raumDialogRaumnummer", text: "01")
				button(id: "rechts", text: " > ")
				textField(id: "raumDialogWohnzimmer", text: "Wohnzimmer")
				comboBox(id: "raumDialogRaumtyp")
				button(id: "raumdatenDialogRaumButton", text: "...")
			}
			
			panel(id: "raumDialogLuftart", border: titledBorder("Luftart"), constraints: "span") {
				comboBox(id: "raumDialogLuftartCombo", constraints: "cell 0 0 2 1")
				textField(id: "raumDialogLuftartFaktorZuluftverteilung", text: "", constraints: "cell 1 0")
				label(id: "raumDialogLuftartCombo", text: "Faktor Zuluftverteilung", constraints: "cell 2 0")
				
				textField(id: "raumDialogLuftartAbluftVs", text: "", constraints: "cell 1 1")
				label(id: "raumDialogLuftartCombo", text: "Abluftvolumentstrom in m³/h", constraints: "cell 2 1")
			}
			
			panel(id: "raumDialogDurchlassposition", border: titledBorder("Durchlassposition"), layout: new MigLayout("fillx", "[left,fill]para[left,fill]para[left,fill]", "[fill]")) {
				button(id: "raumDialogDurchlasspositionInfo", text: "Info...")
				label(text: "Zuluft")
				label(text: "Abluft")
				
				buttonGroup().with {
					add radioButton(id: "raumDialogDurchlasspositionZuluftDecke", text: "Decke", constraints: "wrap")
					add radioButton(id: "raumDialogDurchlasspositionZuluftWandOben", text: "Wand oben", constraints: "wrap")
					add radioButton(id: "raumDialogDurchlasspositionZuluftWandUnten", text: "Wand unten", constraints: "wrap")
					add radioButton(id: "raumDialogDurchlasspositionZuluftBoden", text: "Boden", constraints: "wrap")
				}
				
				buttonGroup().with {
					add radioButton(id: "raumDialogDurchlasspositionAbluftDecke", text: "Decke", constraints: "wrap")
					add radioButton(id: "raumDialogDurchlasspositionAbluftWandOben", text: "Wand oben", constraints: "wrap")
					add radioButton(id: "raumDialogDurchlasspositionAbluftWandUnten", text: "Wand unten", constraints: "wrap")
					add radioButton(id: "raumDialogDurchlasspositionAbluftBoden", text: "Boden", constraints: "wrap")
				}
			}
			
			panel(id: "raumDialogKanalanschluss", border: titledBorder("Kanalanschluss"), layout: new MigLayout("fillx", "[left,fill]para[left,fill]para[left,fill]", "[fill]")) {
				button(id: "raumDialogKanalanschlussInfo", text: "Info...")
				label(text: "Zuluft")
				label(text: "Abluft")
				
				buttonGroup().with {
					add radioButton(id: "raumDialogKanalanschlussZuluftDecke", text: "Decke", constraints: "wrap")
					add radioButton(id: "raumDialogKanalanschlussZuluftWandOben", text: "Wand oben", constraints: "wrap")
					add radioButton(id: "raumDialogKanalanschlussZuluftWandUnten", text: "Wand unten", constraints: "wrap")
					add radioButton(id: "raumDialogKanalanschlussZuluftBoden", text: "Boden", constraints: "wrap")
				}
				
				buttonGroup().with {
					add radioButton(id: "raumDialogKanalanschlussAbluftDecke", text: "Decke", constraints: "wrap")
					add radioButton(id: "raumDialogKanalanschlussAbluftWandOben", text: "Wand oben", constraints: "wrap")
					add radioButton(id: "raumDialogKanalanschlussAbluftWandUnten", text: "Wand unten", constraints: "wrap")
					add radioButton(id: "raumDialogKanalanschlussAbluftBoden", text: "Boden", constraints: "wrap")
				}
			}
			
			panel(id: "raumDialogTabelle", layout: new MigLayout("fillx", "[left]para[left]para[left]", "[fill]"), constraints: "span") {
				
				label(text: "Maximale Türspalthöhe [mm]")
				textField(id: "raumDialogDetailsTurspalthohe", text: "10.0")
				button(id: "raumDialogDetailsTurentfernen", text: "Tür entfernen", constraints: "wrap")
			
				jideScrollPane() {
					//table(id: "raumDialogDetailsTabelle", constraints: "span", model: model.createRaumEinstellungenTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
					//}
				}
			}
			
			panel(id: "raumDialogOptional", border: titledBorder("Optional"), layout: new MigLayout("fillx", "[left]para[right]para[left]para[left]para[right]para[left]para[left]para[right]para[left]"), constraints: "span") {
				label(text: "Raumlänge")
				textField(id: "raumDialogOptionalRaumlange")
				label(text: "m")
				label(text: "Raumbreite")
				textField(id: "raumDialogOptionalRaumbreite")
				label(text: "m")
				label(text: "Raumhöhe")
				textField(id: "raumDialogOptionalRaumhohe")
				label(text: "m", constraints: "wrap")
				
				label(text: "Raumfläche")
				textField(id: "raumDialogOptionalRaumflache")
				label(text: "m²")
				label(text: "Raumvolumen")
				textField(id: "raumDialogOptionalRaumvolumen")
				label(text: "m³")
				label("")
				label("")
				label("")
			}
		}
		
		// RaumdatenDialog - Zusammenfassung
		panel(id: "raumDialogZusammenfassungTab", title: "Zusammenfassung") {
			jideScrollPane() {
				// TODO...
				//table(id: "raumEinstellungenTabelle", model: model.createRaumEinstellungenTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
				//}
			}
		}
		
	}
}



/*
class RaumdatenDialogView {

	SwingBuilder swing 

	public RaumdatenDialogView(def row) { 
		swing = new SwingBuilder()
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
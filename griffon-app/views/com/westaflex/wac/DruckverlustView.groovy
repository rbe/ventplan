/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/views/com/westaflex/wac/DruckverlustView.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import net.miginfocom.swing.MigLayout

// Druckverlustberechnung
panel(id: "dvbTabPanel", layout: new MigLayout("fillx", "[fill]", "[fill]")) {
	// Tabellen für Druckverlustberechnung
	jideTabbedPane(id: "dvbTabGroup", constraints: "span") {
		
		// Druckverlustberechnung - Kanalnetz
		panel(id: "dvbKanalnetzTab", title: "Kanalnetz", constraints: "grow") {
			jideScrollPane() {
				panel(id: "dvbKanalnetzPanel", layout: new MigLayout("fillx", "[left,fill]para[left,fill]para[left,fill]para[left,fill]para[left,fill]para[left]para[left]", "[fill]")) {
					
					label("Luftart", constraints: "width 100::150")
					label("Nr. Teilstrecke", constraints: "width 100::150")
					label("Luftmenge (m³/h)", constraints: "width 100::150")
					label("Kanalbezeichnung", constraints: "width 100::150")
					label("Länge (m)", constraints: "width 100::150")
					label("", constraints: "width 100::150")
					label("", constraints: "width 100::150, wrap")
					
					comboBox(id: "dvbKanalnetzLuftart", items: ["ZU", "AB"])
					textField(id: "dvbKanalnetzNrTeilstrecke")
					textField(id: "dvbKanalnetzLuftmenge")
					comboBox(id: "dvbKanalnetzKanalbezeichnung", items: model.meta.dvbKanalbezeichnung)
					textField(id: "dvbKanalnetzLange")
					button(id: "dvbKanalnetzHinzufugen", text: "Hinzufügen")
					button(id: "dvbKanalnetzWiderstandswerte", text: "Widerstandsbeiwerte...", constraints: "wrap")
					
					panel(id: "dvbKanalnetzTabellePanel", constraints: "span", layout: new MigLayout("fillx", "[fill]")) {
						jideScrollPane() {
							table(id: "dvbKanalnetzTabelle", model: model.createDvbKanalnetzTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
							}
						}
					}
					
					button(id: "dvbKanalnetzEntfernen", text: "Entfernen")
					
				}
			}
		}
		
		// Druckverlustberechnung - Ventileinstellung
		panel(id: "dvbVentileinstellungTab", title: "Ventileinstellung", constraints: "grow") {
			// Druckverlustberechnung - Ventileinstellung
			jideScrollPane() {
				panel(id: "dvbVentileinstellungPanel", layout: new MigLayout("fillx", "[left,fill]para[left,fill]para[left,fill]para[left,fill]para[left,fill]para[left,fill]", "[fill]")) {
					
					label("Luftart", constraints: "width 100::150")
					label("Raum", constraints: "width 100::150")
					label("Teilstrecken", constraints: "width 100::150")
					label("", constraints: "width 100::150")
					label("Ventilbezeichnung", constraints: "width 100::150")
					label("", constraints: "width 100::150, wrap")
					
					comboBox(id: "dvbVentileinstellungLuftart", items: ["ZU", "AB", "AU", "FO"])
					comboBox(id: "dvbVentileinstellungRaum", items: model.meta.raum.typ + [/* items werden nach RaumHinzufugen aktualisiert, siehe Ticket#10 */])
					textField(id: "dvbVentileinstellungTeilstrecken")
					button(id: "dvbVentileinstellungAuswahlen", text: "Auswählen")
					comboBox(id: "dvbVentileinstellungVentilbezeichnung", items: model.meta.dvbVentileinstellung)
					button(id: "dvbVentileinstellungHinzufugen", text: "Hinzufügen", constraints: "wrap")
					
					panel(id: "dvbVentileinstellungTabellePanel", constraints: "span", layout: new MigLayout("fillx, filly", "[fill]")) {
						jideScrollPane() {
							table(id: "dvbVentileinstellungTabelle", model: model.createDvbVentileinstellungTableModel(), selectionMode: javax.swing.ListSelectionModel.SINGLE_SELECTION) {
							}
						}
					}
					
					button(id: "dvbVentileinstellungEntfernen", text: "Entfernen")
					
				}
			}
		}
		
	}
}
// dvbTabGroup
dvbTabGroup.with {
	setTabColorProvider(com.jidesoft.swing.JideTabbedPane.ONENOTE_COLOR_PROVIDER)
	setBoldActiveTab(true)
}

/**
 * /Users/rbe/project/wac2/griffon-app/bindings/com/westaflex/wac/AkustikBindings.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

// Zuluft
bind(source: model.map.akustik.zuluft, sourceProperty: "anzahlUmlenkungen",       target: akustikZuluftAnzahlUmlenkungen90GradStck, targetProperty: "text", mutual: true)
bind(source: model.map.akustik.zuluft, sourceProperty: "luftverteilerkastenStck", target: akustikZuluftLuftverteilerkastenStck,     targetProperty: "text", mutual: true)
bind(source: model.map.akustik.zuluft, sourceProperty: "langsdampfung",           target: akustikZuluftLangsdampfungKanalLfdmMeter, targetProperty: "text", mutual: true)
// Abluft
bind(source: model.map.akustik.abluft, sourceProperty: "anzahlUmlenkungen",       target: akustikAbluftAnzahlUmlenkungen90GradStck, targetProperty: "text", mutual: true)
bind(source: model.map.akustik.abluft, sourceProperty: "luftverteilerkastenStck", target: akustikAbluftLuftverteilerkastenStck,     targetProperty: "text", mutual: true)
bind(source: model.map.akustik.abluft, sourceProperty: "langsdampfung",           target: akustikAbluftLangsdampfungKanalLfdmMeter, targetProperty: "text", mutual: true)
/* Zentralgerät
TODO Nicht als Binding lösen, sondern nach Berechnung aktualisieren!?
bind(source: raumVsZentralgerat, sourceProperty: "selectedItem", target: akustikZuluftZuluftstutzenZentralgerat, targetProperty: "selectedItem")
bind(source: raumVsZentralgerat, sourceProperty: "selectedItem", target: akustikAbluftAbluftstutzenZentralgerat, targetProperty: "selectedItem")
*/
// Mittlerer Schalldruckpegel
bind(source: model.map.akustik.zuluft, sourceProperty: "mittlererSchalldruckpegel", target: akustikZuluftMittlererSchalldruckpegel, targetProperty: "text")
bind(source: model.map.akustik.abluft, sourceProperty: "mittlererSchalldruckpegel", target: akustikAbluftMittlererSchalldruckpegel, targetProperty: "text")
// ActionListener
def addActionListener = { comp, tabname ->
	comp.addActionListener({ evt ->
		controller.berechneAkustik(tabname)
	} as java.awt.event.ActionListener)
}
def addKeyListener = { comp, tabname ->
	comp.addKeyListener(
		[
			keyReleased: { evt ->
				controller.berechneAkustik(tabname)
			}
		] as java.awt.event.KeyAdapter
	)
}
def addListenerToComboBox = { tabname ->
	[
		"akustik${tabname}Raumbezeichnung",
		"akustik${tabname}${tabname}stutzenZentralgerat", "akustik${tabname}Pegel",
		"akustik${tabname}Kanalnetz", "akustik${tabname}Filter",
		"akustik${tabname}1Hauptschalldampfer",
		"akustik${tabname}2Hauptschalldampfer",
		"akustik${tabname}LangsdampfungKanal",
		"akustik${tabname}SchalldampferVentil",
		"akustik${tabname}EinfugungsdammwertLuftdurchlass"
	].each {
		addActionListener(view."${it}", tabname)
	}
}
def addListenerToTextField = { tabname ->
	[
		"akustik${tabname}AnzahlUmlenkungen90GradStck",
		"akustik${tabname}LuftverteilerkastenStck",
		"akustik${tabname}LangsdampfungKanalLfdmMeter",
		"akustik${tabname}Raumabsorption"
	].each {
		addKeyListener(view."${it}", tabname)
	}
}
addListenerToComboBox("Zuluft")
addListenerToTextField("Zuluft")
addListenerToComboBox("Abluft")
addListenerToTextField("Abluft")

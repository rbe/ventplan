/**
 * /Users/rbe/project/wac2/griffon-app/bindings/com/westaflex/wac/AkustikBindings.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

// Zuluft
bind(source: model.map.akustik.zuluft, sourceProperty: "anzahlUmlenkungen",           target: akustikZuluftAnzahlUmlenkungen90GradStck,     targetProperty: "text",         mutual: true)
bind(source: model.map.akustik.zuluft, sourceProperty: "luftverteilerkastenStck",     target: akustikZuluftLuftverteilerkastenStck,         targetProperty: "text",         mutual: true)
bind(source: model.map.akustil.zuluft, sourceProperty: "raumBezeichnung",             target: akustikZuluftRaumbezeichnung,                 targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.zuluft, sourceProperty: "zentralgerat",                target: akustikZuluftZuluftstutzenZentralgerat,       targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.zuluft, sourceProperty: "volumenstromZentralgerat",    target: akustikZuluftPegel,                           targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.zuluft, sourceProperty: "slpErhohungKanalnetz",        target: akustikZuluftKanalnetz,                       targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.zuluft, sourceProperty: "slpErhohungFilter",           target: akustikZuluftFilter,                          targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.zuluft, sourceProperty: "hauptschalldampfer1",         target: akustikZuluft1Hauptschalldampfer,             targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.zuluft, sourceProperty: "hauptschalldampfer2",         target: akustikZuluft2Hauptschalldampfer,             targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.zuluft, sourceProperty: "langsdampfungKanal",          target: akustikZuluftLangsdampfungKanal,              targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.zuluft, sourceProperty: "langsdampfungKanalLfdmMeter", target: akustikZu	luftLangsdampfungKanalLfdmMeter,    targetProperty: "text",         mutual: true)
bind(source: model.map.akustil.zuluft, sourceProperty: "schalldampferVentil",         target: akustikZuluftSchalldampferVentil,             targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.zuluft, sourceProperty: "einfugungsdammwert",          target: akustikZuluftEinfugungsdammwertLuftdurchlass, targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.zuluft, sourceProperty: "raumabsorption",              target: akustikZuluftRaumabsorption,                  targetProperty: "text",         mutual: true)
// Abluft
bind(source: model.map.akustik.abluft, sourceProperty: "anzahlUmlenkungen",           target: akustikAbluftAnzahlUmlenkungen90GradStck,     targetProperty: "text",         mutual: true)
bind(source: model.map.akustik.abluft, sourceProperty: "luftverteilerkastenStck",     target: akustikAbluftLuftverteilerkastenStck,         targetProperty: "text",         mutual: true)
bind(source: model.map.akustil.abluft, sourceProperty: "raumBezeichnung",             target: akustikAbluftRaumbezeichnung,                 targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.abluft, sourceProperty: "zentralgerat",                target: akustikAbluftAbluftstutzenZentralgerat,       targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.abluft, sourceProperty: "volumenstromZentralgerat",    target: akustikAbluftPegel,                           targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.abluft, sourceProperty: "slpErhohungKanalnetz",        target: akustikAbluftKanalnetz,                       targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.abluft, sourceProperty: "slpErhohungFilter",           target: akustikAbluftFilter,                          targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.abluft, sourceProperty: "hauptschalldampfer1",         target: akustikAbluft1Hauptschalldampfer,             targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.abluft, sourceProperty: "hauptschalldampfer2",         target: akustikAbluft2Hauptschalldampfer,             targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.abluft, sourceProperty: "langsdampfungKanal",          target: akustikAbluftLangsdampfungKanal,              targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.abluft, sourceProperty: "langsdampfungKanalLfdmMeter", target: akustikAbluftLangsdampfungKanalLfdmMeter,     targetProperty: "text",         mutual: true)
bind(source: model.map.akustil.abluft, sourceProperty: "schalldampferVentil",         target: akustikAbluftSchalldampferVentil,             targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.abluft, sourceProperty: "einfugungsdammwert",          target: akustikAbluftEinfugungsdammwertLuftdurchlass, targetProperty: "selectedItem", mutual: true)
bind(source: model.map.akustil.abluft, sourceProperty: "raumabsorption",              target: akustikAbluftRaumabsorption,                  targetProperty: "text",         mutual: true)
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
// Add ActionListener
def addAL = { tabname ->
	view."akustik${tabname}${tabname}stutzenZentralgerat".addActionListener({ evt ->
		controller.aktualisiereAkustikVolumenstrom(tabname)
	} as java.awt.event.ActionListener)
}
addAL("Zuluft")
addAL("Abluft")

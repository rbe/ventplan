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
// Abluf
bind(source: model.map.akustik.abluft, sourceProperty: "anzahlUmlenkungen",       target: akustikAbluftAnzahlUmlenkungen90GradStck, targetProperty: "text", mutual: true)
bind(source: model.map.akustik.abluft, sourceProperty: "luftverteilerkastenStck", target: akustikAbluftLuftverteilerkastenStck,     targetProperty: "text", mutual: true)
bind(source: model.map.akustik.abluft, sourceProperty: "langsdampfung",           target: akustikAbluftLangsdampfungKanalLfdmMeter, targetProperty: "text", mutual: true)
/* Zentralgerät
TODO Nicht als Binding lösen, sondern nach Berechnung aktualisieren!?
bind(source: raumVsZentralgerat, sourceProperty: "selectedItem", target: akustikZuluftZuluftstutzenZentralgerat, targetProperty: "selectedItem")
bind(source: raumVsZentralgerat, sourceProperty: "selectedItem", target: akustikAbluftAbluftstutzenZentralgerat, targetProperty: "selectedItem")
*/
// ActionListener
[akustikZuluftZuluftstutzenZentralgerat, akustikZuluftPegel].each { comp ->
	comp.addActionListener({ evt ->
		controller.berechneAkustik("Zuluft")
	} as java.awt.event.ActionListener)
}

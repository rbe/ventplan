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

//
bind(source: model.map.akustik.zuluft, sourceProperty: "anzahlUmlenkungen",       target: akustikZuluftAnzahlUmlenkungen90GradStck, targetProperty: "text", mutual: true)
bind(source: model.map.akustik.zuluft, sourceProperty: "luftverteilerkastenStck", target: akustikZuluftLuftverteilerkastenStck,     targetProperty: "text", mutual: true)
bind(source: model.map.akustik.zuluft, sourceProperty: "langsdampfung",           target: akustikZuluftLangsdampfungKanalWert,      targetProperty: "text", mutual: true)
bind(source: model.map.akustik.abluft, sourceProperty: "anzahlUmlenkungen",       target: akustikAbluftAnzahlUmlenkungen90GradStck, targetProperty: "text", mutual: true)
bind(source: model.map.akustik.abluft, sourceProperty: "luftverteilerkastenStck", target: akustikAbluftLuftverteilerkastenStck,     targetProperty: "text", mutual: true)
bind(source: model.map.akustik.abluft, sourceProperty: "langsdampfung",           target: akustikAbluftLangsdampfungKanalWert,      targetProperty: "text", mutual: true)
//
bind(source: raumVsZentralgerat, sourceProperty: "selectedItem", target: akustikZuluftZuluftstutzenZentralgerat, targetProperty: "text")
bind(source: raumVsZentralgerat, sourceProperty: "selectedItem", target: akustikAbluftAbluftstutzenZentralgerat, targetProperty: "text")

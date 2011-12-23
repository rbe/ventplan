/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/services/com/westaflex/wac/ProjektModelService.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.XmlHelper as X
import com.westaflex.wac.WpxConstants as WX

/**
 * Speichern und Laden von WAC-Projekten im WPX-Format.
 */
class ProjektModelService {
    
    def validator
    
    def xmlns
    def xsd
    
    def xmlSlurper
    def domBuilder
    
    /**
     *
     */
    def ProjektModelService() {
        // Load XSD
        def xsdStream = Wac2Resource.getWPXXSDAsStream()
        //println "ProjektModelService: found XSD for WPX: ${xsdStream.getClass()}"
        validator = javax.xml.validation.SchemaFactory
                    .newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI)
                    .newSchema(new javax.xml.transform.stream.StreamSource(xsdStream))
                    .newValidator()
        // XmlSlurper for reading XML
        xmlSlurper = new XmlSlurper()
        // Read XML using locally cached DTDs
        xmlSlurper.setEntityResolver(com.bensmann.griffon.CachedDTD.entityResolver)
        // Turn off loading of external DTDs
        // xmlSlurper.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        // Create DOMBuilder and set it in XmlHelper too
        X.domBuilder = domBuilder = groovy.xml.DOMBuilder.newInstance()
    }
    
    /**
     * Validate WPX XML file against XSD.
     * @param xml java.lang.String
     */
    def validateWpx = { String xml ->
        try {
            validator.validate(
                new javax.xml.transform.stream.StreamSource(new java.io.StringReader(xml)))
        } catch (e) {
            println "Kann Projekt nicht öffnen: ${e}"
        }
    }
    
    /**
     * @param file java.io.File or java.lang.String
     */
    def load = { file ->
        try {
            // Load WPX XML file
            def fh = file instanceof java.io.File ? file : new java.io.File(file)
            def xml = fh.getText("UTF-8")
            // Validate
            validateWpx(xml)
            // Return document
            xmlSlurper.parseText(xml)
        } catch (e) {
            e.printStackTrace()
        }
    }
    
    /**
     * Transform loaded XML into our Griffon model.
     * @param xml XmlSlurped XML
     */
    def toMap = { xml ->
        use (groovy.xml.dom.DOMCategory) {
            def p = xml."projekt"
            def ausfuhrende = p."firma".find { it."rolle".text() == "Ausfuhrende" }
            def grosshandel = p."firma".find { it."rolle".text() == "Grosshandel" }
            def gebaude = p."gebaude"
            def zentralgerat = p."zentralgerat"
            // Räume
            def raume = []
            gebaude."raum".each { room ->
                def r = [
                            position:                     X.vi { room."position".text() },
                            raumNummer:                   X.vs { room."raumnummer".text() },
                            raumBezeichnung:              X.vs { room."bezeichnung".text() },
                            raumTyp:                      X.vs { WX[room."raumtyp".text()] },
                            raumLuftart:                  X.vs { WX[room."luftart".text()] },
                            raumGeschoss:                 X.vs { room."geschoss".text() },
                            raumFlache:                   X.vd { room."raumflache".text() },
                            raumHohe:                     X.vd { room."raumhohe".text() },
                            raumLange:                    X.vd { room."raumlange".text() },
                            raumBreite:                   X.vd { room."raumbreite".text() },
                            raumVolumen:                  X.vd { room."raumvolumen".text() },
                            raumZuluftfaktor:             X.vd { room."zuluftfaktor".text() },
                            raumAbluftVolumenstrom:       X.vd { room."abluftvolumenstrom".text() },
                            raumLuftwechsel:              X.vd { room."luftwechsel".text() },
                            raumVolumenstrom:             X.vd { room."volumenstrom".text() },
                            raumBezeichnungAbluftventile: X.vs { room."bezeichnungAbluftventile".text() },
                            raumAnzahlAbluftventile:      X.vi { room."anzahlAbluftventile".text() },
                            raumAbluftmengeJeVentil:      X.vd { room."abluftmengeJeVentile".text() },
                            raumBezeichnungZuluftventile: X.vs { room."bezeichnungZuluftventile".text() },
                            raumAnzahlZuluftventile:      X.vi { room."anzahlZuluftventile".text() },
                            raumZuluftmengeJeVentil:      X.vd { room."zuluftmengeJeVentile".text() },
                            raumVerteilebene:             X.vs { room."ventilebene".text() },
                            raumAnzahlUberstromVentile:   X.vi { room."anzahlUberstromventile".text() },
                            raumUberstromElement:         X.vs { room."uberstromelement".text() },
                            raumMaxTurspaltHohe:          X.vd { room."maxTurspaltHohe".text() },
                            turen: [] /*as ObservableList*/
                        ] as ObservableMap
                // Türen
                room."tur".each { tur ->
                    r.turen << [
                                turBezeichnung: X.vs { tur."name".text() },
                                turBreite:      X.vi { tur."breite".text() },
                                turQuerschnitt: X.vd { tur."querschnitt".text() },
                                turSpalthohe:   X.vd { tur."spalthohe".text() },
                                turDichtung:    X.vb { tur."dichtung".text() == "true" }
                            ]
                }
                raume << r
            }
            // Druckverlustberechnung - Kanalnetz
            def kanalnetze = []
            p."druckverlust"?."kanalnetz"?.eachWithIndex { kanalnetz, idx ->
                def k = [
                            position:              idx,
                            luftart:               X.vs { kanalnetz."luftart".text() },
                            teilstrecke:           X.vi { kanalnetz."nrTeilstrecke".text() },
                            luftVs:                X.vd { kanalnetz."luftmenge".text() },
                            kanalbezeichnung:      X.vs { kanalnetz."kanalbezeichnung".text() },
                            lange:                 X.vd { kanalnetz."kanallange".text() },
                            gesamtwiderstandszahl: 0.0d
                        ] as ObservableMap
                kanalnetze << k
            }
            // Druckverlustberechnung - Ventileinstellung
            def ventileinstellungen = []
            p."druckverlust"?."ventileinstellung"?.eachWithIndex { ventileinstellung, idx ->
                def v = [
                            position:          idx,
                            luftart:           X.vs { ventileinstellung."luftart".text() },
                            raum:              X.vs { ventileinstellung."raum".text() },
                            teilstrecken:      X.vs { ventileinstellung."teilstrecken".text() },
                            ventilbezeichnung: X.vs { ventileinstellung."ventilbezeichnung".text() },
                        ] as ObservableMap
                ventileinstellungen << v
            }
            // Akustikberechnung
            def makeAkustik = { node ->
                [
                    raumBezeichnung:             X.vs { node."raum".text() },
                    slpErhohungKanalnetz:        X.vi { node."slpErhohungKanalnetz".text() },
                    slpErhohungFilter:           X.vi { node."slpErhohungFilter".text() },
                    hauptschalldampfer1:         X.vs { node."hauptschalldampfer1".text() },
                    hauptschalldampfer2:         X.vs { node."hauptschalldampfer2".text() },
                    anzahlUmlenkungen:           X.vi { node."anzahlUmlenkungen".text() },
                    luftverteilerkastenStck:     X.vi { node."luftverteilerkastenStck".text() },
                    langsdampfungKanal:          X.vs { node."langsdampfungKanal".text() },
                    langsdampfungKanalLfdmMeter: X.vi { node."langsdampfungKanalLfdm".text() },
                    schalldampferVentil:         X.vs { node."schalldampferVentil".text() },
                    einfugungsdammwert:          X.vs { node."einfugungsdammwert".text() },
                    raumabsorption:              X.vs { node."raumabsorption".text() },
                ]
            }
            //
            def anlage = p."anlage"
            // Build map; return value
            [
                        kundendaten: [
                            bauvorhaben: X.vs { p."bauvorhaben".text() },
                            notizen:     X.vs { p."notizen".text() },
                            grosshandel: [
                                firma1:          X.vs { grosshandel."firma1".text() },
                                firma2:          X.vs { grosshandel."firma2".text() },
                                strasse:         X.vs { grosshandel."adresse"."strasse".text() },
                                plz:             X.vs { grosshandel."adresse"."postleitzahl".text() },
                                ort:             X.vs { grosshandel."adresse"."ort".text() },
                                telefon:         X.vs { grosshandel."tel".text() },
                                telefax:         X.vs { grosshandel."fax".text() },
                                ansprechpartner: X.vs { grosshandel."kontakt"."person"."name".text() },
                            ],
                            ausfuhrendeFirma: [
                                firma1:          X.vs { ausfuhrende."firma1".text() },
                                firma2:          X.vs { ausfuhrende."firma2".text() },
                                strasse:         X.vs { ausfuhrende."adresse"."strasse".text() },
                                plz:             X.vs { ausfuhrende."adresse"."postleitzahl".text() },
                                ort:             X.vs { ausfuhrende."adresse"."ort".text() },
                                telefon:         X.vs { ausfuhrende."tel".text() },
                                telefax:         X.vs { ausfuhrende."fax".text() },
                                ansprechpartner: X.vs { ausfuhrende."kontakt"."person"."name".text() },
                            ],
                        ],
                        gebaude: [
                            typ: [
                                efh:         X.vb { gebaude."gebaudeTyp".text() == "EFH" },
                                mfh:         X.vb { gebaude."gebaudeTyp".text() == "MFH" },
                                maisonette:  X.vb { gebaude."gebaudeTyp".text() == "MAI" }
                            ],
                            lage: [
                                windschwach: X.vb { gebaude."gebaudeLage".text() == "SCH" },
                                windstark:   X.vb { gebaude."gebaudeLage".text() == "STA" }
                            ],
                            warmeschutz: [
                                hoch:    X.vb { gebaude."warmeschutz".text() == "HOC" },
                                niedrig: X.vb { gebaude."warmeschutz".text() == "NIE" },
                            ],
                            geometrie: [:], // Will be calculated
                            luftdichtheit: [
                                kategorieA:     X.vb { gebaude."luftdichtheit".text() == "A" },
                                kategorieB:     X.vb { gebaude."luftdichtheit".text() == "B" },
                                kategorieC:     X.vb { gebaude."luftdichtheit".text() == "C" },
                                kategorieM:     X.vb { gebaude."luftdichtheit".text() == "M" },
                                druckdifferenz: X.vd { gebaude."luftdichtheitDruckdifferenz".text() },
                                luftwechsel:    X.vd { gebaude."luftdichtheitLuftwechsel".text() },
                                druckexponent:  X.vd { gebaude."luftdichtheitDruckexponent".text() }
                            ],
                            faktorBesondereAnforderungen: X.vd { gebaude."besAnfFaktor".text() },
                            geplanteBelegung: [
                                personenanzahl:         X.vi { gebaude."personenAnzahl".text() },
                                aussenluftVsProPerson:  X.vd { gebaude."personenVolumen".text() },
                                // Will be calculated
                                //mindestaussenluftrate: 0.0d
                            ],
                        ],
                        anlage: [
                            standort: [
                                KG: X.vb { zentralgerat."geratestandort".text() == "KG" },
                                EG: X.vb { zentralgerat."geratestandort".text() == "EG" },
                                OG: X.vb { zentralgerat."geratestandort".text() == "OG" },
                                DG: X.vb { zentralgerat."geratestandort".text() == "DG" },
                                SG: X.vb { zentralgerat."geratestandort".text() == "SG" },
                            ],
                            luftkanalverlegung: [
                                aufputz:     X.vb { gebaude."luftkanalverlegung".find { it.text() == "AUF" } == "AUF" },
                                dammschicht: X.vb { gebaude."luftkanalverlegung".find { it.text() == "DAM" } == "DAM" },
                                decke:       X.vb { gebaude."luftkanalverlegung".find { it.text() == "DEC" } == "DEC" },
                                spitzboden:  X.vb { gebaude."luftkanalverlegung".find { it.text() == "SPI" } == "SPI" },
                            ],
                            // TODO move to complex type zentralgerat
                            aussenluft: [
                                dach:     X.vb { gebaude."aussenluft".find { it.text() == "DAC" } == "DAC" },
                                wand:     X.vb { gebaude."aussenluft".find { it.text() == "WAN" } == "WAN" },
                                erdwarme: X.vb { gebaude."aussenluft".find { it.text() == "ERD" } == "ERD" },
                            ],
                            zuluft: [
                                tellerventile:  X.vb { gebaude."zuluftdurchlasse".find { it.text() == "TEL" } == "TEL" },
                                schlitzauslass: X.vb { gebaude."zuluftdurchlasse".find { it.text() == "SCH" } == "SCH" },
                                fussboden:      X.vb { gebaude."zuluftdurchlasse".find { it.text() == "FUS" } == "FUS" },
                                sockel:         X.vb {
                                    gebaude."zuluftdurchlasse".find { it.text() == "SOC" } == "SOC"
                                }
                            ],
                            abluft: [
                                tellerventile: X.vb {
                                    gebaude."abluftdurchlasse".find { it.text() == "TEL" } == "TEL"
                                }
                            ],
                            fortluft: [
                                dach:         X.vb { gebaude."fortluft".find { it.text() == "DAC" } == "DAC" },
                                wand:         X.vb { gebaude."fortluft".find { it.text() == "WAN" } == "WAN" },
                                lichtschacht: X.vb { gebaude."fortluft".find { it.text() == "LIC" } == "LIC" || gebaude."fortluft".find { it.text() == "BOG135" } == "BOG135" },
                            ],
                            energie: [
                                zuAbluftWarme: X.vb { zentralgerat."energie"."zuAbluftWarme".text() == "true" },
                                bemessung:     X.vb { zentralgerat."energie"."bemessung".text() == "true" },
                                ruckgewinnung: X.vb { zentralgerat."energie"."ruckgewinnung".text() == "true" },
                                regelung:      X.vb {
                                    zentralgerat."energie"."regelung".text() == "true"
                                }
                            ],
                            hygiene: [
                                ausfuhrung:         X.vb { zentralgerat."hygiene"."ausfuhrung".text() == "true" },
                                filterung:          X.vb { zentralgerat."hygiene"."filterung".text() == "true" },
                                keineVerschmutzung: X.vb { zentralgerat."hygiene"."keineVerschmutzung".text() == "true" },
                                dichtheitsklasseB:  X.vb {
                                    zentralgerat."hygiene"."dichtheitsklasseB".text() == "true"
                                }
                            ],
                            ruckschlagklappe: X.vb { zentralgerat."ruckschlagklappe".text() == "true" },
                            schallschutz:     X.vb { zentralgerat."schallschutz".text() == "true" },
                            feuerstatte:      X.vb { zentralgerat."feuerstatte".text() == "true" },
                            //kennzeichnungLuftungsanlage: "ZuAbLS-Z-WE-WÜT-0-0-0-0-0", // Will be calculated
                            zentralgerat:             X.vs { anlage."zentralgerat"."name".text() },
                            zentralgeratManuell:      X.vb { anlage."zentralgerat"."manuell".text() == "true" },
                            volumenstromZentralgerat: X.vi { anlage."zentralgerat"."volumenstrom".text() },
                        ],
                        raum: [
                            raume: raume,
                            raumVs: [:] // Will be calculated
                        ],
                        aussenluftVs: [:], // Will be calculated
                        dvb: [
                            kanalnetz: kanalnetze,
                            ventileinstellung: ventileinstellungen
                        ],
                        akustik: [
                            zuluft: makeAkustik(p."akustik"?."zuluft"),
                            abluft: makeAkustik(p."akustik"?."abluft"),
                        ]
                    ]
        }
    }

    /**
     *
     */
    def makeAdresse = { map ->
        domBuilder.adresse() {
            X.m(["strasse", "ort", "postleitzahl", "land"], map)
        }
    }

    /**
     *
     */
    def makePerson = { map ->
        domBuilder.person() {
            X.m(["benutzername", "name", "vorname", "nachname", "email", "tel", "fax"], map)
            map.adresse && makeAdresse(map.adresse)
        }
    }

    /**
     *
     */
    def makeKontakt = { map ->
        domBuilder.kontakt() {
            X.m(["rolle"], map)
            map.person && makePerson(map.person)
        }
    }

    /**
     *
     */
    def makeFirma = { firmaRolle, map ->
        domBuilder.firma() {
            X.tc { firma1(map.firma1) }
            X.tc { firma2(map.firma2) }
            X.tc { rolle(firmaRolle) }
            X.tc { email(map.email) }
            X.tc { tel(map.telefon) }
            X.tc { fax(map.telefax) }
            makeAdresse([strasse: map.strasse, postleitzahl: map.plz, ort: map.ort])
            makeKontakt(rolle: "Ansprechpartner", person: [name: map.ansprechpartner])
        }
    }

    /**
     *
     */
    def makeRaum = { map ->
        domBuilder.raum() {
            X.tc { position(map.position) }
            X.tc { raumnummer(map.raumNummer) }
            X.tc { bezeichnung(map.raumBezeichnung) }
            X.tc { raumtyp(WX[map.raumTyp]) }
            X.tc { geschoss(map.raumGeschoss) }
            X.tc { luftart(WX[map.raumLuftart]) }
            X.tc { raumflache(map.raumFlache) }
            X.tc { raumhohe(map.raumHohe) }
            X.tc { raumlange(map.raumLange) }
            X.tc { raumbreite(map.raumbreite) }
            X.tc { raumvolumen(map.raumVolumen) }
            X.tc { zuluftfaktor(map.raumZuluftfaktor) }
            X.tc { abluftvolumenstrom(map.raumAbluftVolumenstrom) }
            X.tc { luftwechsel(map.raumLuftwechsel) }
            X.tc { volumenstrom(map.raumVolumenstrom) }
            X.tc { bezeichnungAbluftventile(map.raumBezeichnungAbluftventile) }
            X.tc { anzahlAbluftventile(map.raumAnzahlAbluftventile as Integer) }
            X.tc { abluftmengeJeVentil(map.raumAbluftmengeJeVentil) }
            X.tc { bezeichnungZuluftventile(map.raumBezeichnungZuluftventile) }
            X.tc { anzahlZuluftventile(map.raumAnzahlZuluftventile as Integer) }
            X.tc { zuluftmengeJeVentil(map.raumZuluftmengeJeVentil) }
            X.tc { ventilebene(map.raumVerteilebene) }
            X.tc { anzahlUberstromventile(map.raumAnzahlUberstromVentile as Integer) }
            X.tc { uberstromelement(map.raumUberstromElement) }
            X.tc { maxTurspaltHohe(map.raumMaxTurspaltHohe) }
            // Türen
            map.turen?.eachWithIndex { t, i ->
                ////				if (t.turBezeichnung && t.turBreite) {
                tur() {
                    X.tc { name(t.turBezeichnung) } { name("Tür ${i}") }
                    X.tc { breite(t.turBreite as Integer) } { breite(0) }
                    X.tc { querschnitt(t.turQuerschnitt) } { querschnitt(0.0) }
                    X.tc { spalthohe(t.turSpalthohe) } { spalthohe(0.0) }
                    X.tc { dichtung(t.turDichtung) } { dichtung(true) }
                }
                ////				}
            }
        }
    }

    /**
     *
     */
    def makeGebaude = { map ->
        def g = map.gebaude
        def geo = g.geometrie
        def a = map.anlage
        domBuilder.gebaude() {
            X.tc { gebaudeTyp(WX[g.typ.grep { it.value == true }?.key[0]]) }
            X.tc { gebaudeLage(WX[g.lage.grep { it.value == true }?.key[0]]) }
            X.tc { warmeschutz(WX[g.warmeschutz.grep { it.value == true }?.key[0]]) }
            X.tc {
                luftdichtheit((g.luftdichtheit.grep { it.key ==~ /kategorie[\w]/ && it.value == true }?.key[0]) - "kategorie")
            }
            X.tc { luftdichtheitDruckdifferenz(g.luftdichtheit.druckdifferenz) }
            X.tc { luftdichtheitLuftwechsel(g.luftdichtheit.luftwechsel) }
            X.tc { luftdichtheitDruckexponent(g.luftdichtheit.druckexponent) }
            X.tc { besAnfFaktor(g.faktorBesondereAnforderungen) }
            X.tc { personenAnzahl(g.geplanteBelegung.personenanzahl as Integer) }
            X.tc { personenVolumen(g.geplanteBelegung.aussenluftVsProPerson) }
            // mindestaussenluftrate
            // TODO move into zentralgerat
            X.tc { aussenluft(WX[a.aussenluft.grep { it.value == true }?.key[0]]) }
            X.tc { fortluft(WX[a.fortluft.grep { it.value == true }?.key[0]]) }
            X.tc {
                a.luftkanalverlegung.grep { it.value }.collect { it.key }.each {
                    luftkanalverlegung(WX[it])
                }
            }
            X.tc {
                a.zuluft.grep { it.value }.collect { it.key }.each {
                    zuluftdurchlasse(WX[it])
                }
            }
            X.tc {
                a.abluft.grep { it.value }.collect { it.key }.each {
                    abluftdurchlasse(WX[it])
                }
            }
            // TODO end
            // Geometrie
            geometrie() {
                X.tc { wohnflache(geo.wohnflache) }
                X.tc { mittlereRaumhohe(geo.raumhohe) }
                X.tc { luftvolumen(geo.luftvolumen) }
                X.tc { geluftetesVolumen(geo.geluftetesVolumen) }
                X.tc { gelufteteFlache(geo.gelufteteFlache) }
            }
            // Räume
            map.raum.raume.each { r -> makeRaum(r) }
        }
        // Anlagendaten, Zentralgerät
        domBuilder.zentralgerat() {
            X.tc { name(a.zentralgerat) }
            X.tc { manuell(a.zentralgeratManuell) }
            // TODO selektieren volumenstrom, nicht liste
            X.tc { volumenstrom(a.volumenstromZentralgerat) }
            X.tc { geratestandort(a.standort.grep { it.value == true }?.key[0]) }
            // Energie
            energie() {
                def e = a.energie
                X.tc { zuAbluftWarme(e.zuAbluftWarme) }
                X.tc { bemessung(e.bemessung) }
                X.tc { ruckgewinnung(e.ruckgewinnung) }
                X.tc { regelung(e.regelung) }
            }
            // Hygiene
            hygiene() {
                def h = a.hygiene
                X.tc { ausfuhrung(h.ausfuhrung) }
                X.tc { filterung(h.filterung) }
                X.tc { keineVerschmutzung(h.keineVerschmutzung) }
                X.tc { dichtheitsklasseB(h.dichtheitsklasseB) }
            }
            X.tc { ruckschlagkappe(a.ruckschlagkappe) }
            X.tc { schallschutz(a.schallschutz) }
            X.tc { feuerstatte(a.feuerstatte) }
        }
    }

    /**
     *
     */
    def makeDruckverlust = { dvb ->
        def makeKanalnetz = { k ->
            domBuilder.kanalnetz() {
                X.tc { luftart(k.luftart) }
                X.tc { nrTeilstrecke(k.teilstrecke) }
                X.tc { luftmenge(k.luftVs) }
                X.tc { kanalbezeichnung(k.kanalbezeichnung) }
                X.tc { kanallange(k.lange) }
            }
        }
        def makeVentileinstellung = { v ->
            domBuilder.ventileinstellung() {
                X.tc { luftart(v.luftart) }
                X.tc { raum(v.raum) }
                X.tc { teilstrecken(v.teilstrecken) }
                X.tc { ventilbezeichnung(v.ventilbezeichnung) }
            }
        }
        domBuilder.druckverlust() {
            dvb.kanalnetz.each { makeKanalnetz(it) }
            dvb.ventileinstellung.each { makeVentileinstellung(it) }
        }
    }

    /**
     *
     */
    def makeAkustik = { akustik, typ ->
        domBuilder."${typ}"() {
            X.tc { raum(akustik.raumBezeichnung) }
            X.tc { slpErhohungKanalnetz(akustik.slpErhohungKanalnetz as Integer ?: 0) }
            X.tc { slpErhohungFilter(akustik.slpErhohungFilter as Integer ?: 0) }
            X.tc { hauptschalldampfer1(akustik.hauptschalldampfer1) }
            X.tc { hauptschalldampfer2(akustik.hauptschalldampfer2) }
            X.tc { anzahlUmlenkungen(akustik.anzahlUmlenkungen as Integer ?: 0) }
            X.tc { luftverteilerkastenStck(akustik.luftverteilerkastenStck as Integer ?: 0) }
            X.tc { langsdampfungKanal(akustik.langsdampfungKanal) }
            X.tc { langsdampfungKanalLfdm(akustik.langsdampfungKanalLfdmMeter as Integer ?: 0) }
            X.tc { schalldampferVentil(akustik.schalldampferVentil) }
            X.tc { einfugungsdammwert(akustik.einfugungsdammwert) }
            X.tc { raumabsorption(akustik.raumabsorption) }
        }
    }

    /**
     *
     */
    def save = { map, file ->
        def wpx = domBuilder."westaflex-wpx" {
            projekt() {
                ersteller() { person() }
                X.tc { bauvorhaben(map.kundendaten.bauvorhaben) } { bauvorhaben() }
                X.tc { notizen(map.kundendaten.notizen) }
                makeGebaude(map)
                makeDruckverlust(map.dvb)
                X.tc {
                    akustik() {
                        makeAkustik(map.akustik.zuluft, "zuluft")
                        makeAkustik(map.akustik.abluft, "abluft")
                    }
                }
                makeFirma("Grosshandel", map.kundendaten.grosshandel)
                makeFirma("Ausfuhrende", map.kundendaten.ausfuhrendeFirma)
            }
        }
        if (file) {
            def fh = file instanceof java.io.File ? file : new File(file)
            fh.withWriter("UTF-8") { writer ->
                writer.write(groovy.xml.XmlUtil.serialize(wpx))
            }
        }
    }
}

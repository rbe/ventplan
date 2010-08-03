/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/services/com/westaflex/wac/ProjektModelService.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.XmlHelper as X
import com.westaflex.wac.WpxConstants as WX

/**
 * Speichern und Laden von WAC 2-Projekten im WPX 2-Format.
 */
class ProjektModelService {
	
	def xsdFile
	def validator
	
	def xmlns
	def xsd
	
	def domBuilder
	
	/**
	 * 
	 */
	def ProjektModelService() {
		// Load XSD
		xsdFile = new File(Wac2Resource.getWpxXsdUri())
		println "ProjektModelService: found XSD for WPX: ${xsdFile}"
		validator = javax.xml.validation.SchemaFactory
					.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI)
					.newSchema(new javax.xml.transform.stream.StreamSource(new FileReader(xsdFile)))
					.newValidator()
		/*
		xmlns = new groovy.xml.NamespaceBuilder(new NodeBuilder())
		xsd = xmlns.namespace("http://www.w3.org/2001/XMLSchema", "xsd")
		*/
		// Create DOMBuilder and set it in XmlHelper too
		X.domBuilder = domBuilder = groovy.xml.DOMBuilder.newInstance()
	}
	
	/**
	 * Validate WPX XML file against XSD.
	 * @param xml java.lang.String
	 */
	def validateWpx = { String xml ->
		validator.validate(new javax.xml.transform.stream.StreamSource(new StringReader(xml)))
	}
	
	/**
	 * @param file java.io.File or java.lang.String
	 */
	def load = { file ->
		try {
			// Load WPX XML file
			def fh = file instanceof java.io.File ? file : new File(file)
			def xml = fh.text
			// Validate
			validateWpx(xml)
			// Return document
			new XmlSlurper().parseText(xml)
			// domBuilder.parseText(xml).documentElement //.parse(new ByteArrayInputStream(xml.bytes))
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
			def anlage = p."anlage"
			[
				kundendaten: [
					bauvorhaben: X.vs { p."bauvorhaben".text() },
					notizen:     X.vs { p."notizen".text() },
					grosshandel: [
							firma1:          X.vs { grosshandel."firma1".text() },
							firma2:          X.vs { grosshandel."firma2".text() },
							strasse:         X.vs { grosshandel."adresse"."strasse".text() },
							plz:             X.vs { grosshandel."adresse"."plz".text() },
							ort:             X.vs { grosshandel."adresse"."ort".text() },
							telefon:         X.vs { grosshandel."tel".text() },
							telefax:         X.vs { grosshandel."fax".text() },
							ansprechpartner: X.vs { grosshandel."kontakt"."person"."name".text() },
						],
					ausfuhrendeFirma: [
							firma1:          X.vs { ausfuhrende."firma1".text() },
							firma2:          X.vs { ausfuhrende."firma2".text() },
							strasse:         X.vs { ausfuhrende."adresse"."strasse".text() },
							plz:             X.vs { ausfuhrende."adresse"."plz".text() },
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
						// Will be calculated after loading
						geometrie: [:],
						luftdichtheit: [
								kategorieA: X.vb { gebaude."luftdichtheit".text() == "A" },
								kategorieB: X.vb { gebaude."luftdichtheit".text() == "B" },
								kategorieC: X.vb { gebaude."luftdichtheit".text() == "C" },
								kategorieM: X.vb { gebaude."luftdichtheit".text() == "M" },
								druckdifferenz: X.vd { gebaude."luftdichtheitDruckdifferenz".text() },
								luftwechsel: X.vd { gebaude."luftdichtheitLuftwechsel".text() },
								druckexponent: X.vd { gebaude."luftdichtheitDruckexponent".text() }
							],
						faktorBesondereAnforderungen: X.vd { gebaude."besAnfFaktor".text() },
						geplanteBelegung: [
								personenanzahl:  X.vd { gebaude."personenAnzahl".text() },
								aussenluftVsProPerson:  X.vd { gebaude."personenVolumen".text() },
								// Will be calculated
								//mindestaussenluftrate: 0.0d
							],
					],
				anlage: [
						standort: [
								KG: X.vb { gebaude."geratestandort".text() == "KG" },
								EG: X.vb { gebaude."geratestandort".text() == "EG" },
								OG: X.vb { gebaude."geratestandort".text() == "OG" },
								DG: X.vb { gebaude."geratestandort".text() == "DG" },
								SG: X.vb { gebaude."geratestandort".text() == "SG" }
							],
						luftkanalverlegung: [
								:
							],
						aussenluft: [
								:
							],
						zuluft: [
								:
							],
						abluft: [
								:
							],
						fortluft: [
								dach: true
							],
						energie: [
								zuAbluftWarme: true,
								nachricht: " "
							],
						hygiene: [
								nachricht: " "
							],
						// Will be calculated
						//kennzeichnungLuftungsanlage: "ZuAbLS-Z-WE-WÜT-0-0-0-0-0",
						zentralgerat: X.vs { anlage."zentralgerat"."name".text() },
						zentralgeratManuell: X.vb { anlage."zentralgerat"."manuell".text() },
						volumenstromZentralgerat: X.vi { anlage."zentralgerat"."volumenstrom".text() },
					],
				raum: [
						raume: [
								/* ProjektModel.raumMapTemplate wird durch Event RaumHinzufugen pro Raum erstellt */
							],
						// Will be calculated
						//ltmZuluftSumme: 0.0d,
						//ltmAbluftSumme: 0.0d,
						// Will be calculated
						raumVs: [:]
					],
				// Will be calculated
				aussenluftVs: [:],
				dvb: [
						kanalnetz: [],
						ventileinstellung: []
					],
				akkustik: [:]
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
			X.m(["benutzername", "vorname", "nachname", "name", "email", "tel", "fax"], map)
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
		domBuilder.room() {
			X.tc { raumnummer(map.raumNummer) }
			X.tc { bezeichnung(map.raumBezeichnung) }
			X.tc { raumtyp(map.raumTyp) }
			X.tc { geschoss(map.raumGeschoss) }
			X.tc { luftart(map.raumLuftart) }
			X.tc { raumflache(map.raumFlache) }
			X.tc { raumhohe(map.raumHohe) }
			X.tc { raumlange(map.raumLange) }
			X.tc { raumbreite(map.raumbreite) }
			X.tc { zuluftfaktor(map.raumZuluftfaktor) }
			X.tc { abluftvs(map.raumAbluftVs) }
			// Türen
			map.turen.eachWithIndex { t, i ->
				tur() {
					X.tc { name(t.turname) } { name("Tür ${i}") }
					X.tc { breite(t.turbreite) } { breite(610.0) }
				}
			}
		}
	}
	
	/**
	 * 
	 */
	def makeGebaude = { map ->
		def g = map.gebaude
		def a = map.anlage
		domBuilder.gebaude() {
			X.tc { gebaudeTyp(WX[g.typ.grep { it.value == true }?.key[0]]) }
			X.tc { gebaudeLage(WX[g.lage.grep { it.value == true }?.key[0]]) }
			X.tc { warmeschutz(WX[g.warmeschutz.grep { it.value == true }?.key[0]]) }
			geometrie() {
				def gg = g.geometrie
				X.tc { wohnflache(gg.wohnflache) }
				X.tc { mittlereRaumhohe(gg.raumhohe) }
				X.tc { luftvolumen(gg.luftvolumen) }
				X.tc { geluftetesVolumen(gg.geluftetesVolumen) }
				X.tc { gelufteteFlache(gg.gelufteteFlache) }
			}
			X.tc {
				luftdichtheit((g.luftdichtheit.grep { it.key ==~ /kategorie[\w]/ && it.value == true }?.key[0]) - "kategorie")
			}
			X.tc { luftdichtheitDruckdifferenz(g.luftdichtheit.druckdifferenz) }
			X.tc { luftdichtheitLuftwechsel(g.luftdichtheit.luftwechsel) }
			X.tc { luftdichtheitDruckexponent(g.luftdichtheit.druckexponent) }
			X.tc { besAnfFaktor(g.faktorBesondereAnforderungen) }
			X.tc { personenAnzahl(g.geplanteBelegung.personenanzahl) }
			X.tc { personenVolumen(g.geplanteBelegung.mindestaussenluftrate) }
			X.tc { aussenluft(WX[a.aussenluft.grep { it.value == true }?.key[0]]) }
			X.tc { fortluft(WX[a.fortluft.grep { it.value == true }?.key[0]]) }
			[a.luftkanalverlegung].each {
				X.tc { luftkanalverlegung(WX[it]) }
			}
			[a.zuluft].each {
				X.tc { zuluftdurchlasse(WX[it]) }
			}
			[a.abluft].each {
				X.tc { abluftdurchlasse(WX[it]) }
			}
			// Räume
			map.raum.raume.each { r -> makeRaum(r) }
		}
		// Anlage
		domBuilder.anlage() {
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
		// Zentralgerät
		domBuilder.zentralgerat() {
			X.tc { name(a.zentralgerat) }
			X.tc { manuell(a.zentralgeratManuell) }
			// TODO selektieren volumenstrom, nicht liste
			X.tc { volumenstrom(a.volumenstromZentralgerat) }
			X.tc { geratestandort(a.standort.grep { it.value == true }?.key[0]) }
		}
	}
	
	/**
	 * 
	 */
	def save = { map, file ->
		def wpx = domBuilder."westaflex-wpx" {
			project() {
				ersteller()
				X.tc { bauvorhaben(map.kundendaten.bauvorhaben) }
				X.tc { notizen(map.kundendaten.notizen) }
				makeGebaude(map)
				makeFirma("Grosshandel", map.kundendaten.grosshandel)
				makeFirma("Ausfuhrende", map.kundendaten.ausfuhrendeFirma)
			}
		}
		if (file) {
			def fh = file instanceof java.io.File ? file : new File(file)
			fh.withWriter { writer ->
				writer.write(groovy.xml.XmlUtil.serialize(wpx))
			}
		}
	}
	
}

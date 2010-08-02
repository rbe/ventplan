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
		//javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
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
							ansprechpartner: X.vs { grosshandel."person"."nachname".text() },
						] as ObservableMap,
					ausfuhrendeFirma: [
							firma1:          X.vs { ausfuhrende."firma1".text() },
							firma2:          X.vs { ausfuhrende."firma2".text() },
							strasse:         X.vs { ausfuhrende."adresse"."strasse".text() },
							plz:             X.vs { ausfuhrende."adresse"."plz".text() },
							ort:             X.vs { ausfuhrende."adresse"."ort".text() },
							telefon:         X.vs { ausfuhrende."tel".text() },
							telefax:         X.vs { ausfuhrende."fax".text() },
							ansprechpartner: X.vs { ausfuhrende."person"."nachname".text() },
						] as ObservableMap,
				] as ObservableMap,
				gebaude: [
						typ: [
								efh:         X.vb { gebaude."gebaudeTyp".text() == "EFH" },
								mfh:         X.vb { gebaude."gebaudeTyp".text() == "MFH" },
								maisonette:  X.vb { gebaude."gebaudeTyp".text() == "MAI" }
							] as ObservableMap,
						lage: [
								windschwach: X.vb { gebaude."gebaudeLage".text() == "SCH" },
								windstark:   X.vb { gebaude."gebaudeLage".text() == "STA" }
							] as ObservableMap,
						warmeschutz: [
								hoch:    X.vb { gebaude."warmeschutz".text() == "HOC" },
								niedrig: X.vb { gebaude."warmeschutz".text() == "NIE" },
							] as ObservableMap,
						geometrie: [:
								//raumhohe: "0,00",
								//geluftetesVolumen: "0,00"
							] as ObservableMap,
						luftdichtheit: [
								kategorieA: true,
								kategorieB: true,
								kategorieC: true,
								kategorieM: true,
								druckdifferenz: 2.0d,
								luftwechsel: 1.0d,
								druckexponent: 0.666f
							] as ObservableMap,
						faktorBesondereAnforderungen: 1.0d,
						geplanteBelegung: [
								personenanzahl: 0.0d,
								aussenluftVsProPerson: 30,
								mindestaussenluftrate: 0.0d
							] as ObservableMap,
					] as ObservableMap,
				anlage: [
						standort: [EG: true] as ObservableMap,
						luftkanalverlegung: [:] as ObservableMap,
						aussenluft: [:] as ObservableMap,
						zuluft: [:] as ObservableMap,
						abluft: [:] as ObservableMap,
						fortluft: [dach: true] as ObservableMap,
						energie: [zuAbluftWarme: true, nachricht: " "] as ObservableMap,
						hygiene: [nachricht: " "] as ObservableMap,
						kennzeichnungLuftungsanlage: "ZuAbLS-Z-WE-WÜT-0-0-0-0-0",
						zentralgerat: "",
						zentralgeratManuell: false,
						volumenstromZentralgerat: 0,
					] as ObservableMap,
				raum: [
						raume: [
								/* ProjektModel.raumMapTemplate wird durch Event RaumHinzufugen pro Raum erstellt */
							] as ObservableList,
						ltmZuluftSumme: 0.0d,
						ltmAbluftSumme: 0.0d,
						raumVs: [
							gesamtVolumenNE: 0.0d,
							luftwechselNE: 0.0d,
							gesamtaussenluftVsMitInfiltration: 0.0d
						] as ObservableMap
					] as ObservableMap,
				aussenluftVs: [
						infiltrationBerechnen: true,
						massnahme: " ",
						gesamtLvsLtmLvsFs: 0.0d,
						gesamtLvsLtmLvsRl: 0.0d,
						gesamtLvsLtmLvsNl: 0.0d,
						gesamtLvsLtmLvsIl: 0.0d,
					] as ObservableMap,
				dvb: [
						kanalnetz: [] as ObservableList,
						ventileinstellung: [] as ObservableList
					] as ObservableMap,
				akkustik: [:] as ObservableMap
			] as ObservableMap
		}
	}
	
	/**
	 * 
	 */
	def makeAdresse = { map ->
		domBuilder.adresse() {
			X.m(["strasse", "postleitzahl", "ort", "land"], map)
		}
	}
	
	/**
	 * 
	 */
	def makePerson = { map ->
		domBuilder.person() {
			X.m(["benutzername", "vorname", "nachname", "email", "tel", "fax"], map)
			makeAdresse(map.adresse)
		}
	}
	
	/**
	 * 
	 */
	def makeFirma = { firmaRolle, map ->
		domBuilder.firma() {
			X.tc { firma1(map.firma1) } { firma1() }
			X.tc { firma2(map.firma2) } { firma2() }
			X.tc { rolle(firmaRolle) } { rolle() }
			X.tc { email(map.email) } { email() }
			X.tc { tel(map.telefon) } { tel() }
			X.tc { fax(map.telefax) } { fax() }
			makeAdresse([strasse: map.strasse, postleitzahl: map.plz, stadt: map.ort])
			// Not much information available in WAC2!
			makePerson(nachname: map.ansprechpartner)
		}
	}
	
	/**
	 * 
	 */
	def makeRaum = { map ->
		domBuilder.raum() {
			X.tc { raumnummer(map.raumNummer) } { raumnummer() }
			X.tc { bezeichnung(map.raumBezeichnung) } { bezeichnung() }
			X.tc { raumtyp(map.raumTyp) } { raumtyp() }
			X.tc { geschoss(map.raumGeschoss) } { geschoss() }
			X.tc { luftart(map.raumLuftart) } { luftart() }
			X.tc { raumflache(map.raumFlache) } { raumflache() }
			X.tc { raumhohe(map.raumHohe) } { raumhohe() }
			X.tc { raumlange(map.raumLange) } { raumlange() }
			X.tc { raumbreite(map.raumbreite) } { raumbreite() }
			X.tc { zuluftfaktor(map.raumZuluftfaktor) } { zuluftfaktor() }
			X.tc { abluftvs(map.raumAbluftVs) } { abluftVs() }
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
			X.tc { gebaudeTyp(g.typ.grep { it.value == true }?.key[0]) } { gebaudeTyp() }
			X.tc { gebaudeLage(g.lage.grep { it.value == true }?.key[0]) } { gebaudeLage() }
			X.tc { warmeschutz(g.warmeschutz.grep { it.value == true }?.key[0]) } { warmeschutz() }
			domBuilder.geometrie() {
				def gg = g.geometrie
				X.tc { wohnflache(gg.wohnflache) }
				X.tc { mittlereRaumhohe(gg.raumhohe) }
				X.tc { luftvolumen(gg.luftvolumen) }
				X.tc { geluftetesVolumen(gg.geluftetesVolumen) }
				X.tc { gelufteteFlache(gg.gelufteteFlache) }
			}
			X.tc {
				luftdichtheit((g.warmeschutz.grep { it.key ==~ /kategorie[\w]/ && it.value == true }?.key[0]) - "kategorie")
			} { luftdichtheit() }
			X.tc { besAnfFaktor(g.faktorBesondereAnforderungen) } { besAnfFaktor() }
			X.tc { personenAnzahl(g.geplanteBelegung.personenanzahl) } { personenAnzahl() }
			X.tc { personenVolumen(g.geplanteBelegung.mindestaussenluftrate) } { personenVolumen() }
			X.tc { aussenluft(a.aussenluft.grep { it.value == true }?.key[0]) } { aussenluft() }
			X.tc { fortluft(a.fortluft.grep { it.value == true }?.key[0]) } { fortluft() }
			X.tc { luftkanalverlegung() } { luftkanalverlegung() }
			X.tc { zuluftdurchlasse() } { zuluftdurchlasse() }
			X.tc { abluftdurchlasse() } { abluftdurchlasse() }
			// Räume
			map.raum.raume.each { r -> makeRaum(r) }
			// Zentralgerät
			zentralgerat() {
				X.tc { name(a.zentralgerat) } { name() }
				// TODO selektieren volumenstrom, nicht liste
				X.tc { volumenstrom(a.volumenstromZentralgerat) } { volumenstrom() }
				X.tc { geratestandort(a.standort.grep { it.value == true }?.key[0]) } { geratestandort() }
			}
		}
	}
	
	/**
	 * Mapping old WPX constants into new ones
	 * Ticket #20
	 */
	private static final wpxConstants = [
			
		]
	
	/**
	 * Mapping old WPX constants into new ones
	 * Ticket #20
	 */
	def mapConstant = { c ->
		wpxConstants[c]
	}
	
	/**
	 * 
	 */
	def save = { map, file ->
		def wpx = domBuilder."westaflex-wpx" {
			project() {
				// This information is not available in WAC2!
				/*
				ersteller() {
					rolle(map.person?.rolle)
					makePerson(map.person)
				}
				erstellt()
				bearbeitet()
				*/
				makeFirma("Grosshandel", map.kundendaten.grosshandel)
				makeFirma("Ausfuhrende", map.kundendaten.ausfuhrendeFirma)
				X.tc { bauvorhaben(map.kundendaten.bauvorhaben) } { bauvorhaben() }
				X.tc { notizen(map.kundendaten.notizen) } { notizen() }
				makeGebaude(map)
			}
		}
	}
	
}

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
		xsdFile = new File(Wac2Resource.class.getResource("../resources/westaflex-project-1.0.1.xsd").toURI())
		println xsdFile
		validator = javax.xml.validation.SchemaFactory
					.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI)
					.newSchema(new javax.xml.transform.stream.StreamSource(new FileReader(xsdFile)))
					.newValidator()
		/*
		xmlns = new groovy.xml.NamespaceBuilder(new NodeBuilder())
		xsd = xmlns.namespace("http://www.w3.org/2001/XMLSchema", "xsd")
		*/
		//javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
		domBuilder = groovy.xml.DOMBuilder.newInstance()
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
			domBuilder.parse(new ByteArrayInputStream(xml.bytes))
		} catch (e) {
			e.printStackTrace()
		}
	}
	
	/**
	 * Transform loaded XML into our Griffon model.
	 */
	def toMap = { org.w3c.dom.Document xml ->
		[kundendaten: [bauvorhaben: "hallo spencer"] as ObservableMap] as ObservableMap
	}
	
	/**
	 * Try to create a node.
	 */
	def tc = { valueClosure, defaultClosure = null ->
		try {
			valueClosure()
		} catch (e) {
			//
			println "tc: CATCHED: ${e}"
			// Default?
			if (defaultClosure) {
				defaultClosure()
			}
		}
	}
	
	/**
	 * 
	 */
	def m = { keys, map ->
		if (map) {
			keys.each { k ->
				tc { domBuilder."${k}"(map[k] ?: "") } { domBuilder."${k}"() }
			}
		}
	}
	
	/**
	 * 
	 */
	def makeAdresse = { map ->
		domBuilder.adresse() {
			m(["name", "strasse", "stadt", "ort", "postleitzahl", "land"], map)
		}
	}
	
	/**
	 * 
	 */
	def makePerson = { map ->
		domBuilder.person() {
			m(["benutzername", "vorname", "nachname", "email", "tel", "fax"], map)
			makeAdresse(map.adresse)
		}
	}
	
	/**
	 * 
	 */
	def makeRaum = { map ->
		domBuilder.raum() {
			tc { raumnummer(map.raumNummer) } { raumnummer() }
			tc { bezeichnung(map.raumBezeichnung) } { bezeichnung() }
			tc { raumtyp(map.raumTyp) } { raumtyp() }
			tc { geschoss(map.raumGeschoss) } { geschoss() }
			tc { luftart(map.raumLuftart) } { luftart() }
			tc { raumflache(map.raumFlache) } { raumflache() }
			tc { raumhohe(map.raumHohe) } { raumhohe() }
			tc { raumlange(map.raumLange) } { raumlange() }
			tc { raumbreite(map.raumbreite) } { raumbreite() }
			tc { zuluftfaktor(map.raumZuluftfaktor) } { zuluftfaktor() }
			tc { abluftvs(map.raumAbluftVs) } { abluftVs() }
			// Türen
			map.turen.eachWithIndex { t, i ->
				tur() {
					tc { name(t.turname) } { name("Tür ${i}") }
					tc { breite(t.turbreite) } { breite(610.0) }
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
			tc { gebaudeTyp(g.typ.grep { it.value == true }?.key[0]) } { gebaudeTyp() }
			tc { gebaudeLage(g.lage.grep { it.value == true }?.key[0]) } { gebaudeLage() }
			tc { warmeschutz(g.warmeschutz.grep { it.value == true }?.key[0]) } { warmeschutz() }
			tc {
				luftdichtheit((g.warmeschutz.grep { it.key ==~ /kategorie[\w]/ && it.value == true }?.key[0]) - "kategorie")
			} { luftdichtheit() }
			tc { besAnfFaktor(g.faktorBesondereAnforderungen) } { besAnfFaktor() }
			tc { personenAnzahl(g.geplanteBelegung.personenanzahl) } { personenAnzahl() }
			tc { personenVolumen(g.geplanteBelegung.mindestaussenluftrate) } { personenVolumen() }
			tc { aussenluft(a.aussenluft.grep { it.value == true }?.key[0]) } { aussenluft() }
			tc { fortluft(a.fortluft.grep { it.value == true }?.key[0]) } { fortluft() }
			tc { luftkanalverlegung() } { luftkanalverlegung() }
			tc { zuluftdurchlasse() } { zuluftdurchlasse() }
			tc { abluftdurchlasse() } { abluftdurchlasse() }
			// Räume
			map.raum.raume.each { r -> tc { makeRaum(r) } }
			// Zentralgerät
			zentralgerat() {
				tc { name(a.zentralgerat) } { name() }
				tc { volumenstrom(a.volumenstromZentralgerat) } { volumenstrom() }
				tc { geratestandort(a.standort.grep { it.value == true }?.key[0]) } { geratestandort() }
			}
		}
	}
	
	/**
	 * 
	 */
	def makeFirma = { firmaRolle, map ->
		domBuilder.firma() {
			tc { firma1(map.firma1) } { firma1() }
			tc { firma1(map.firma2) } { firma2() }
			tc { rolle(firmaRolle) } { rolle() }
			tc { email(map.email) } { email() }
			tc { tel(map.telefon) } { tel() }
			tc { fax(map.telefax) } { fax() }
			makeAdresse([strasse: map.strasse, postleitzahl: map.plz, stadt: map.ort])
			// Not much information available in WAC2!
			makePerson(nachname: map.ansprechpartner)
		}
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
				makeFirma("grosshandel", map.kundendaten.grosshandel)
				makeFirma("ausfuhrende", map.kundendaten.ausfuhrendeFirma)
				tc { bauvorhaben(map.kundendaten.bauvorhaben) } { bauvorhaben() }
				tc { notizen(map.kundendaten.notizen) } { notizen() }
				makeGebaude(map)
			}
		}
	}
	
}

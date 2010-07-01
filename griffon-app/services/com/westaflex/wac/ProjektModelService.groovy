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
		xsdFile = new File(Wac2Resource.class.getResource("../resources/westaflex-project-1.0.xsd").toURI())
		println xsdFile
		validator = javax.xml.validation.SchemaFactory
					.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI)
					.newSchema(new javax.xml.transform.stream.StreamSource(new FileReader(xsdFile)))
					.newValidator()
		/*
		xmlns = new groovy.xml.NamespaceBuilder(new NodeBuilder())
		xsd = xmlns.namespace("http://www.w3.org/2001/XMLSchema", "xsd")
		*/
		//
		domBuilder = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
	}
	
	/**
	 * 
	 */
	def load = { file ->
		try {
			// Load project XML
			def fh = file instanceof java.io.File ? file : new File(file)
			def xml = fh.text
			// Validate XML
			validator.validate(new javax.xml.transform.stream.StreamSource(new StringReader(xml)))
			// Return document
			domBuilder.parse(new ByteArrayInputStream(xml.bytes))
		} catch (e) {
			e.printStackTrace()
		}
	}
	
	/**
	 * 
	 */
	def toMap = {
		[kundendaten: [bauvorhaben: "hallo spencer"] as ObservableMap] as ObservableMap
	}
	
	/**
	 * 
	 */
	def save = { map, file ->
		/*
		<westaflex-wpx>
			<projekt>
				<ersteller>
					<rolle></rolle>
					<person>
						<benutzername/>
						<vorname/>
						<nachname/>
						<email/>
						<tel/>
						<fax/>
						<adresse>
							<name/>
							<strasse/>
							<stadt/>
							<ort/>
							<postleitzahl/>
							<land/>
						</adresse>
					</person>
				</ersteller>
				<erstellt>2010-06-28T08:23:44</erstellt>
				<bearbeitet>2010-07-01T10:50:21</bearbeitet>
				<bauvorhaben></bauvorhaben>
				<gebaude>
					<!-- MFH EFH MAI -->
					<gebaudeTyp>MFH</gebaudeTyp>
					<!-- SCH STA -->
					<gebaudeLage>SCH</gebaudeLage>
					<!-- HOC NIE -->
					<warmeschutz>NIE</warmeschutz>
					<!-- A B C M-->
					<luftdichtheit>A</luftdichtheit>
					<!-- KG EG OG DG SB -->
					<geratestandort>EG</geratestandort>
					<besAnfFaktor>1.0</besAnfFaktor>
					<personenAnzahl>3</personenAnzahl>
					<personenVolumen>90</personenVolumen>
					<!-- DAC WAN ERD -->
					<aussenluft>DAC</aussenluft>
					<!-- DAC WAN LIC -->
					<fortluft>DAC</fortluft>
					<luftkanalverlegung/>
					<zuluftdurchlasse/>
					<abluftdurchlasse/>
					<room>
						<raumnummer>001</raumnummer>
						<bezeichnung>Wohnzimmer</bezeichnung>
						<!-- WOH KIN SLF ESS ARB GAS HAU KEL WC" KUC BAD DUS SAU FLU DIE -->
						<raumtyp>WOH</raumtyp>
						<!-- KG EG OG DG SB -->
						<geschoss>EG</geschoss>
						<!-- ZU AB ZUA UB -->
						<luftart>ZU</luftart>
						<raumflache>25.0</raumflache>
						<raumhohe>2.50</raumhohe>
						<tuer>
							<name>T&#xFC;r</name>
							<breite>610</breite>
						</tuer>
					</room>
				</gebaude>
				<firma>
					<name>Firma name</name>
					<rolle>Ausfuhrende</rolle>
					<email></email>
					<tel></tel>
					<fax></fax>
					<adresse>
						<name/>
						<strasse/>
						<stadt/>
						<ort/>
						<postleitzahl/>
						<land/>
					</adresse>
					<kontakt>
						<rolle></rolle>
						<person>
							<benutzername/>
							<vorname/>
							<nachname/>
							<email/>
							<tel/>
							<fax/>
							<adresse>
								<name/>
								<strasse/>
								<stadt/>
								<ort/>
								<postleitzahl/>
								<land/>
							</adresse>
						</person>
					</kontakt>
				</firma>
			</projekt>
		</westaflex-wpx>
		*/
	}
	
}

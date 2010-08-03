/**
 * /Users/rbe/project/wac2/griffon-app/util/com/bensmann/griffon/WpxConstants.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.bensmann.griffon

/**
 * Konstanten für das Mapping von Schlüsseln aus dem "ProjektModel" nach XML.
 * Wird vor allem wegen den Abkürzungen aus der Webversion genutzt.
 */
class WpxConstants {
	
	/**
	 * Mapping old WPX constants into new ones
	 * Ticket #20
	 */
	private static final wpxConstants = [
			// Gebäudetyp
			efh: "EFH",
			mfh: "MFH",
			maisonette: "MAI",
			// Gebäudelage
			windschwach: "SCH",
			windstark: "STA",
			// Wärmeschutz
			hoch: "HOC",
			niedrig: "NIE",
			// Luftkanalverlegung
			// Außenluft, Fortluft
			dach: "DAC",
			wand: "WAN",
			erdwarme: "ERD",
			lichtschacht: "LIC",
			// Raumtypen
			"Wohnzimmer": "WOH",
			"Kinderzimmer": "KIN",
			"Schlafzimmer": "SLF",
			"Esszimmer": "ESS",
			"Arbeitszimmer": "ARB",
			"Gästezimmer": "GAS",
			"Hausarbeitsraum": "HAU",
			"Kellerraum": "KEL",
			"WC": "WC",
			"Küche": "KUC",
			"Bad mit/ohne WC": "BAD",
			"Duschraum": "DUS",
			"Sauna": "SAU",
			"Flur": "FLU",
			"Diele": "DIE",
		]
	
	/**
	 * Mapping old WPX constants into new ones
	 * Ticket #20
	 */
	String static mapConstant = { c ->
		wpxConstants[c]
	}
	
}

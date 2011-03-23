/*
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
 *
 * Project wac
 * /Users/rbe/project/wac/griffon-app/util/com/westaflex/wac/WpxConstants.groovy
 * Last modified at 23.03.2011 12:55:41 by rbe
 */
package com.westaflex.wac

import com.bensmann.griffon.GriffonHelper as GH

/**
 * Konstanten für das Mapping von Schlüsseln aus dem "ProjektModel" nach XML.
 * Wird vor allem wegen den Abkürzungen aus der Webversion genutzt.
 * Siehe Ticket #20.
 */
class WpxConstants {

    private static boolean DEBUG = false
	
	private static final m = [
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
			aufputz: "AUF",
			dammschicht: "DAM",
			decke: "DEC",
			spitzboden: "SPI",
			// Zuluftdurchlässe, Abluftdurchlässe
			tellerventile: "TEL",
			fussboden: "FUS",
			schlitzauslass: "SCH",
			sockel: "SOC",
			// Außenluft, Fortluft
			dach: "DAC",
			wand: "WAN",
			erdwarme: "ERD",
			lichtschacht: "LIC",
			// Luftarten
			"ÜB": "UB",
			"ZU/AB": "ZUA",
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
            "Kochnische": "KUC",
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
	def static get(String p) {
		if (DEBUG) print "WpxConstants: mapping ${p?.dump()}"
		def r = WpxConstants.m[p]
        // Search reverse (value -> key)
		if (!r) r = GH.invertMap(WpxConstants.m)[p]
        // No result? Return input.
        if (!r) r = p
		if (DEBUG) println " -> ${r?.dump()}"
		r
	}
	
}

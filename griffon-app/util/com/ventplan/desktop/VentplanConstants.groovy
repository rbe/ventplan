/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/16/12 10:35 AM
 */
package com.ventplan.desktop

import com.bensmann.griffon.GriffonHelper as GH

/**
 * WAC-17 WAC-103 WAC-164
 * Konstanten für das Mapping von Schlüsseln aus dem "ProjektModel" nach XML.
 * Wird vor allem wegen den Abkürzungen aus der Webversion genutzt.
 */
class VentplanConstants {

    private static final m = [
            // Gebäudetyp
            efh: 'EFH',
            mfh: 'MFH',
            maisonette: 'MAI',
            // Gebäudelage
            windschwach: 'SCH',
            windstark: 'STA',
            // Wärmeschutz
            hoch: 'HOC',
            niedrig: 'NIE',
            // Luftkanalverlegung
            aufputz: 'AUF',
            dammschicht: 'DAM',
            decke: 'DEC',
            spitzboden: 'SPI',
            // Zuluftdurchlässe, Abluftdurchlässe
            tellerventile: 'TEL',
            fussboden: 'FUS',
            schlitzauslass: 'SCH',
            sockel: 'SOC',
            // Außenluft, Fortluft
            dach: 'DAC',
            wand: 'WAN',
            erdwarme: 'ERD',
            lichtschacht: 'LIC',
            bogen135: 'BOG135',
            // Luftarten
            'ÜB': 'UB',
            'ZU/AB': 'ZUA',
            // Raumtypen
            'Wohnzimmer': 'WOH',
            'Kinderzimmer': 'KIN',
            'Schlafzimmer': 'SLF',
            'Esszimmer': 'ESS',
            'Arbeitszimmer': 'ARB',
            'Gästezimmer': 'GAS',
            'Hausarbeitsraum': 'HAU',
            'Kellerraum': 'KEL',
            'WC': 'WC',
            'Küche': 'KUC',
            'Kochnische': 'KUC',
            'Bad mit/ohne WC': 'BAD',
            'Duschraum': 'DUS',
            'Sauna': 'SAU',
            'Flur': 'FLU',
            'Diele': 'DIE',
    ]

    /**
     * Mapping old VPX constants into new ones.
     */
    def static get(String p) {
        def r = VentplanConstants.m[p]
        // Search reverse (value -> key)
        if (!r) {
            r = GH.invertMap(VentplanConstants.m)[p]
        }
        // No result? Return input.
        if (!r) {
            r = p
        }
        r
    }

}

/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 12.02.13 19:01
 */

package com.ventplan.desktop

import com.ventplan.prinzipskizze.PrinzipskizzeClient

import static DocumentPrefHelper.PREFS_USER_KEY_NAME
import static DocumentPrefHelper.PREFS_USER_KEY_PRINZIPSKIZZE_PLAN

/**
 * WAC-202 Prinzipskizze
 */
@SuppressWarnings("GrMethodMayBeStatic")
class PrinzipskizzeService {

    //<editor-fold desc="WAC-245 Artikel für Aussenluft- und Fortluftauslässe">

    private String artikelFurAussenluftauslass(Map map) {
        String aussenluft = 'z.B. 200LG002/004' //model.map.anlage.aussenluft.lufteinlass
        try {
            Integer volumenstromZentralgerat = map.anlage.volumenstromZentralgerat
            String x = map.anlage.aussenluft.grep { it.value == true }?.key[0] // dach, wand, erdwarme
            switch (x) {
                case 'dach':
                    aussenluft = '200DDF003'
                    break
                case 'wand':
                    if (volumenstromZentralgerat <= 210) {
                        aussenluft = '200LG002'
                    } else {
                        aussenluft = '200LG004'
                    }
                    break
                case 'erdwarme':
                    if (volumenstromZentralgerat <= 210) {
                        aussenluft = '200LE008'
                    } else {
                        aussenluft = '250LE'
                    }
                    break
            }
        } catch (e) {
            // ignore
        }
        aussenluft
    }

    private String artikelFurFortluftauslass(Map map) {
        String fortluft = 'z.B. 200LG002/4'
        try {
            Integer volumenstromZentralgerat = map.anlage.volumenstromZentralgerat
            String x = map.anlage.fortluft.grep { it.value == true }?.key[0]  // dach, wand, bogen135
            switch (x) {
                case 'dach':
                    fortluft = '200DDF003'
                    break
                case 'wand':
                    if (volumenstromZentralgerat <= 210) {
                        fortluft = '200LG002'
                    } else {
                        fortluft = '200LG004'
                    }
                    break
                case 'bogen135':
                    fortluft = '200LD001'
                    break
            }
        } catch (e) {
            // ignore
        }
        fortluft
    }

    //</editor-fold>

    public File makePrinzipskizze(Map map, String vpxFilename) {
        // WAC-245
        String aussenluft = artikelFurAussenluftauslass(map)
        String fortluft = artikelFurFortluftauslass(map)
        // Zentralgerät
        String zentralgerat = "${map.anlage.zentralgerat} (${map.anlage.standort.grep { it.value == true }?.key[0]})"
        def findRaum = { String luftart, String geschoss ->
            StringBuilder builder = new StringBuilder(20)
            List raume = map.raum.raume.findAll { r ->
                r.raumVerteilebene?.equals(geschoss)
            }
            List raume2 = []
            raume.each { Map raum ->
                builder.delete(0, builder.length())
                if (raum.raumLuftart.contains(luftart)) {
                    if (raum.raumAnzahlZuluftventile > 0) {
                        double d = (double) raum.raumAnzahlZuluftventile
                        builder << raum.raumVerteilebene << ', ' << raum.raumBezeichnung << ': ' << raum.raumBezeichnungZuluftventile
                        1.upto d, {
                            raume2 << builder.toString()
                        }
                    } else if (raum.raumAnzahlAbluftventile > 0) {
                        double d = (double) raum.raumAnzahlAbluftventile
                        builder << raum.raumVerteilebene << ', ' << raum.raumBezeichnung << ': ' << raum.raumBezeichnungAbluftventile
                        1.upto d, {
                            raume2 << builder.toString()
                        }
                    }
                }
            }
            raume2.size() > 0 ? raume2.findAll { null != it } : null
        }
        // Abluft
        List<String> abluftKG = findRaum('AB', 'KG')
        List<String> abluftEG = findRaum('AB', 'EG')
        List<String> abluftDG = findRaum('AB', 'DG')
        List<String> abluftOG = findRaum('AB', 'OG')
        List<String> abluftSB = findRaum('AB', 'SB')
        def (ab1, ab2, ab3) = [abluftKG, abluftEG, abluftDG, abluftOG, abluftSB].grep { it }
        // Zuluft
        List<String> zuluftKG = findRaum('ZU', 'KG')
        List<String> zuluftEG = findRaum('ZU', 'EG')
        List<String> zuluftDG = findRaum('ZU', 'DG')
        List<String> zuluftOG = findRaum('ZU', 'OG')
        List<String> zuluftSB = findRaum('ZU', 'SB')
        def (zu1, zu2, zu3) = [zuluftKG, zuluftEG, zuluftDG, zuluftOG, zuluftSB].grep { it }
        File prinzipskizzeGrafik = null
        //
        DocumentPrefHelper prefHelper = DocumentPrefHelper.instance
        // SOAP service URL
        URL prinzipskizzeServiceURL = new URL(VentplanResource.prinzipskizzeSoapUrl)
        /*
        PrinzipskizzeClient prinzipskizzeClient = new PrinzipskizzeClient()
        byte[] b = prinzipskizzeClient.create(prinzipskizzeServiceURL, aussenluft, fortluft, zentralgerat, ab1, ab2, ab3, zu1, zu2, zu3)
        */
        String empty = ''
        String bauvorhaben = (String) map.kundendaten.bauvorhaben
        String ersteller = (String) prefHelper.getPrefValue(PREFS_USER_KEY_NAME) ?: ''
        String datum = new Date().format('dd.MM.yyyy')
        byte[] b = PrinzipskizzeClient.createWAC237(prinzipskizzeServiceURL, bauvorhaben, empty, ersteller, datum, aussenluft, fortluft, zentralgerat, ab1, ab2, ab3, zu1, zu2, zu3);
        if (b != null && b.size() > 0) {
            prinzipskizzeGrafik = new File(FilenameHelper.getVentplanDir(), FilenameHelper.cleanFilename("${vpxFilename - '.vpx'}_Prinzipskizze.png"))
            FileOutputStream fos = new FileOutputStream(prinzipskizzeGrafik)
            fos.write(b)
            fos.close()
        }
        return prinzipskizzeGrafik;
    }

}

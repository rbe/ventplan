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

import com.ventplan.verlegeplan.PrinzipskizzeClient

@SuppressWarnings("GrMethodMayBeStatic")
class PrinzipskizzeService {

    public File makePrinzipskizze(Map map, String vpxFilename, String aussenluft, String fortluft) {
        /*
        // WAC-245
        String aussenluft = artikelfurAussenluftauslass()
        String fortluft = artikelFurFortluftauslass()
        */
        // Zentralgerät
        String zentralgerat = "${map.anlage.zentralgerat} (${map.anlage.standort.grep { it.value == true }?.key[0]})"
        def findRaum = { String luftart, String geschoss ->
            StringBuilder builder = new StringBuilder()
            List raume = map.raum.raume.findAll { r ->
                r.raumGeschoss == geschoss
            }
            List raume2 = raume.collect { raum ->
                if (builder.length() > 0)
                    builder.delete(0, builder.length())
                if (raum.raumLuftart.contains(luftart)) {
                    if (raum.raumAnzahlZuluftventile > 0) {
                        builder << raum.raumAnzahlZuluftventile.toString2(0) << ' x ' << raum.raumBezeichnungZuluftventile
                    }
                    if (builder.length() > 0) {
                        builder << ', '
                    }
                    if (raum.raumAnzahlAbluftventile > 0) {
                        builder << raum.raumAnzahlAbluftventile.toString2(0) << ' x ' << raum.raumBezeichnungAbluftventile
                    }
                    raum.raumGeschoss + ', ' + raum.raumBezeichnung + ': ' + builder.toString()
                } else {
                    null
                }
            }
            raume2?.size() > 0 ? raume2.findAll { null != it } : null
        }
        // Abluft
        List<String> abluft0 = findRaum('AB', 'KG')
        List<String> abluft1 = findRaum('AB', 'EG')
        List<String> abluft2 = findRaum('AB', 'DG')
        List<String> abluft3 = findRaum('AB', 'OG')
        List<String> abluft4 = findRaum('AB', 'SB')
        def (ab1, ab2, ab3) = [abluft0, abluft1, abluft2, abluft3, abluft4].grep { it }
        // Zuluft
        List<String> zuluft0 = findRaum('ZU', 'KG')
        List<String> zuluft1 = findRaum('ZU', 'EG')
        List<String> zuluft2 = findRaum('ZU', 'DG')
        List<String> zuluft3 = findRaum('ZU', 'OG')
        List<String> zuluft4 = findRaum('ZU', 'SB')
        def (zu1, zu2, zu3) = [zuluft0, zuluft1, zuluft2, zuluft3, zuluft4].grep { it }
        File prinzipskizzeGrafik = null
        // SOAP service URL
        URL prinzipskizzeServiceURL = new URL(VentplanResource.prinzipskizzeSoapUrl)
        PrinzipskizzeClient prinzipskizzeClient = new PrinzipskizzeClient()
        byte[] b = prinzipskizzeClient.create(prinzipskizzeServiceURL, aussenluft, fortluft, zentralgerat, ab1, ab2, ab3, zu1, zu2, zu3)
        if (b != null && b.size() > 0) {
            prinzipskizzeGrafik = new File(FilenameHelper.getVentplanDir(), FilenameHelper.cleanFilename("${vpxFilename}_Prinzipskizze.png"))
            FileOutputStream fos = new FileOutputStream(prinzipskizzeGrafik)
            fos.write(b)
            fos.close()
        }
        return prinzipskizzeGrafik;
    }

}

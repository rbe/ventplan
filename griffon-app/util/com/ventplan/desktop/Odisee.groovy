/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 23.01.13 12:12
 */

package com.ventplan.desktop

public class Odisee {

    private static String filenameWoExtension(String filename) {
        filename - '.wpx' - '.vpx'
    }

    private static String filenameWoExtension(File file) {
        filenameWoExtension(file.name)
    }

    /**
     * Cleanup filename for Odisee, no special characters.
     * @param vpxFile File object.
     * @return String with filename w/o extension and special characters.
     */
    public static String odiseeRequestName(File vpxFile) {
        String vpxFilenameWoExt = filenameWoExtension(vpxFile)
        /*
        // HACK Ventplan -> Odisee + Umlaute
        return vpxFilenameWoExt
                .replace('ä', 'ae').replace('ö', 'oe').replace('ü', 'ue').replace('Ä', 'Ae').replace('Ö', 'Oe').replace('Ü', 'Ue')
                .replace(':', '').replace('/', '').replace('\\', '')
        */
        StringBuilder builder = new StringBuilder()
        for (char c : vpxFilenameWoExt.chars) {
            int i = (int) c
            if ((i >= 48 && i <= 57) || (i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
                builder.append(c)
            }
        }
        return builder.toString()
    }

}

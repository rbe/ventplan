/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 17:23
 */

package eu.artofcoding.ventplan.desktop

@Singleton
class ProjektSuchenPrefHelper {

    public static final String PREFS_USER_KEY_SUCH_ORDNER = "suchordner"

    private ProjektSuchenPrefHelper() {
    }

    /**
     * Saves the file path search folder into the Preferences.
     */
    public static void save(String filepath) {
        try {
            // Remove node - should not exist - and save user information...
            PrefHelper.setPrefValue(PREFS_USER_KEY_SUCH_ORDNER, filepath)
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Get a value from the preferences by its preferences key.
     */
    public static String getSearchFolder() {
        String value = null;
        try {
            value = PrefHelper.getPrefValue(PREFS_USER_KEY_SUCH_ORDNER)
        } catch (Exception e) {
            // ignore
        }
        return value
    }

}

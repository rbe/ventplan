/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.ventplan.desktop

/**
 * WAC-161: Zuletzt geöffnete Projekte
 * Save and load preferences for a Most Recently Used (MRU) list.
 */
@Singleton
class ProjektSuchenPrefHelper {

    public static boolean DEBUG = false
    private static java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(this)
    private static final String PREFS_USER_NODE = "/ventplanprojektsuchen"
    public static final String PREFS_USER_KEY_SUCH_ORDNER = "suchordner"

    /**
     *
     */
    private ProjektSuchenPrefHelper() {
        // WAC-108 Angebotsnummer soll jedesmal eingegeben werden und wird nur temporär gespeichert
    }

    /**
     *
     * @return
     */
    public boolean hasSavedValues() {
        try {
            def value = getPrefValue(PREFS_USER_KEY_NAME)
            if (value) {
                return true
            } else {
                return false
            }
        } catch (e) {
            println "${this}.hasSavedValues: EXCEPTION=${e}"
        }
        return false
    }

    /**
     * Saves the file path search folder into the Preferences.
     */
    public void save(filepath) {
        try {
            // Remove node - should not exist - and save user information...
            prefs.put(PREFS_USER_KEY_SUCH_ORDNER, filepath)
            prefs.flush();
        } catch (Exception e) {
            // do nothing
            println "${this}.save: EXCEPTION=${e}"
        }
    }

    /**
     * Get a value from the preferences by its preferences key.
     */
    public getSearchFolder = {
        String value = null;
        try {
            value = prefs.get(PREFS_USER_KEY_SUCH_ORDNER, '')
        } catch (Exception e) {
            println "${this}.getPrefValue: EXCEPTION=${e}"
        }
        return value
    }

    /**
     * Get all pref values as a formatted string.
     */
    public String getAllPrefValuesAsString() {
        String value = null
        try {
            value = prefs.get(PREFS_USER_KEY_SUCH_ORDNER)
        } catch (Exception e) {
            println "${this}.getAllPrefValuesAsString: EXCEPTION=${e}"
        }
        return value
    }

}

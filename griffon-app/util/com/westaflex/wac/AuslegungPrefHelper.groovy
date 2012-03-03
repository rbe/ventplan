/*
 * WAC
 *
 * Copyright (C) 2005      Informationssysteme Ralf Bensmann.
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

/**
 * WAC-161: Zuletzt geöffnete Projekte
 * Save and load preferences for a Most Recently Used (MRU) list.
 */
@Singleton
class AuslegungPrefHelper {

    public static boolean DEBUG = false
    private static java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(this)
    private static final String PREFS_USER_NODE = "/wacauslegung"
    public static final String PREFS_USER_KEY_FIRMA = "erstellerFirma"
    public static final String PREFS_USER_KEY_NAME = "erstellerName"
    public static final String PREFS_USER_KEY_STRASSE = "erstellerStrasse"
    public static final String PREFS_USER_KEY_PLZ = "erstellerPlz"
    public static final String PREFS_USER_KEY_ORT = "erstellerOrt"
    public static final String PREFS_USER_KEY_TEL = "erstellerTel"
    public static final String PREFS_USER_KEY_FAX = "erstellerFax"
    public static final String PREFS_USER_KEY_EMAIL = "erstellerEmail"
    public static final String PREFS_USER_KEY_ANGEBOTSNUMMER = "erstellerAngebotsnummer"
    public static final String PREFS_USER_KEY_EMPFANGER = "erstellerEmpfanger"
    public static final String PREFS_USER_KEY_DOKUMENTTYP = "erstellerDokumenttyp"

    /**
     *
     */
    private AuslegungPrefHelper() {
        // WAC-108 Angebotsnummer soll jedesmal eingegeben werden und wird nur temporär gespeichert
        prefs.put(PREFS_USER_KEY_ANGEBOTSNUMMER, '')
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
     * Saves a map of user information into the Preferences.
     */
    public void save(map) {
        try {
            [
                    PREFS_USER_KEY_FIRMA, PREFS_USER_KEY_NAME, PREFS_USER_KEY_STRASSE, PREFS_USER_KEY_PLZ, PREFS_USER_KEY_ORT,
                    PREFS_USER_KEY_TEL, PREFS_USER_KEY_FAX, PREFS_USER_KEY_EMAIL,
                    PREFS_USER_KEY_ANGEBOTSNUMMER, PREFS_USER_KEY_EMPFANGER, PREFS_USER_KEY_DOKUMENTTYP
            ].each {
                // Remove node - should not exist - and save user information...
                prefs.put(it, map[it])
            }
            prefs.flush();
        } catch (Exception e) {
            // do nothing
            println "${this}.save: EXCEPTION=${e}"
        }
    }

    /**
     * Get a value from the preferences by its preferences key.
     */
    public getPrefValue = {String prefKey ->
        String value = null;
        try {
            value = prefs.get(prefKey, '')
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
            value = prefs.get(PREFS_USER_KEY_FIRMA) + '\n' +
                    prefs.get(PREFS_USER_KEY_NAME) + '\n' +
                    prefs.get(PREFS_USER_KEY_STRASSE) + '\n' +
                    prefs.get(PREFS_USER_KEY_PLZ) + ' ' + prefs.get(PREFS_USER_KEY_ORT) + '\n' +
                    'Tel: ' + prefs.get(PREFS_USER_KEY_TEL) + '\n' +
                    'Fax: ' + prefs.get(PREFS_USER_KEY_FAX) + '\n' +
                    'Email: ' + prefs.get(PREFS_USER_KEY_EMAIL) + '\n'
        } catch (Exception e) {
            println "${this}.getAllPrefValuesAsString: EXCEPTION=${e}"
        }
        return value
    }

}

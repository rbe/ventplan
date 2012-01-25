/*
 * WAC
 *
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

import java.util.prefs.BackingStoreException

/**
 * WAC-161: Zuletzt geöffnete Projekte
 * Save and load preferences for a Most Recently Used (MRU) list.
 */
@Singleton
class AuslegungPrefHelper {

    public static boolean DEBUG = false    
    private static java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(this);
    private static final String PREFS_USER_NODE = "/wacauslegung";
    public static final String PREFS_USER_KEY_FIRMA = "erstellerFirma";
    public static final String PREFS_USER_KEY_NAME = "erstellerName";
    public static final String PREFS_USER_KEY_STRASSE = "erstellerStrasse";
    public static final String PREFS_USER_KEY_PLZ = "erstellerPlz";
    public static final String PREFS_USER_KEY_ORT = "erstellerOrt";
    public static final String PREFS_USER_KEY_TEL = "erstellerTel";
    public static final String PREFS_USER_KEY_FAX = "erstellerFax";
    public static final String PREFS_USER_KEY_EMAIL = "erstellerEmail";

    private AuslegungPrefHelper() {
    }
    
    public boolean hasSavedValues() {
        try {
            def value = getPrefValue(PREFS_USER_KEY_NAME)
            if (value) {
                return true;
            } else {
                return false;
            }
        } catch (e) {
            println "AuslegungPrefHelper hasSavedValues error -> ${e.dump()}"
        }
        return false;
    }

    /**
     * Saves a map of user information into the Preferences.
     */
    public void save(map) {
        try {
            // Remove node - should not exist - and save user information...
            prefs.put(PREFS_USER_KEY_FIRMA, map[PREFS_USER_KEY_FIRMA]);
            prefs.put(PREFS_USER_KEY_NAME, map[PREFS_USER_KEY_NAME]);
            prefs.put(PREFS_USER_KEY_STRASSE, map[PREFS_USER_KEY_STRASSE]);
            prefs.put(PREFS_USER_KEY_PLZ, map[PREFS_USER_KEY_PLZ]);
            prefs.put(PREFS_USER_KEY_ORT, map[PREFS_USER_KEY_ORT]);
            prefs.put(PREFS_USER_KEY_TEL, map[PREFS_USER_KEY_TEL]);
            prefs.put(PREFS_USER_KEY_FAX, map[PREFS_USER_KEY_FAX]);
            prefs.put(PREFS_USER_KEY_EMAIL, map[PREFS_USER_KEY_EMAIL]);
            
            prefs.flush();
        } catch (Exception e) {
            // do nothing
            println "AuslegungPrefHelper Error saving prefs ${e.dump()}";
        }
    }

    /**
     * Get a value from the preferences by its preferences key.
     */
    public String getPrefValue(String prefKey) {
        String value = null;
        try{
            value = prefs.get(prefKey, "");
            if (DEBUG) println "AuslegungPrefHelper getPrefValue value -> ${value}"
        }
        catch (Exception e) {
            if (DEBUG) println "AuslegungPrefHelper getPrefValue -> ${e.dump()}"
        }
        return value;
    }
    
    /**
     * Get all pref values as a formatted string.
     */
    public String getAllPrefValuesAsString() {
        String value = null;
        try{
            value = prefs.get(PREFS_USER_KEY_FIRMA) + "\n" + 
                    prefs.get(PREFS_USER_KEY_NAME) + "\n" + 
                    prefs.get(PREFS_USER_KEY_STRASSE) + "\n" + 
                    prefs.get(PREFS_USER_KEY_PLZ) + " " + prefs.get(PREFS_USER_KEY_ORT) + "\n" + 
                    "Tel: " + prefs.get(PREFS_USER_KEY_TEL) + "\n" + 
                    "Fax: " + prefs.get(PREFS_USER_KEY_FAX) + "\n" + 
                    "Email: " + prefs.get(PREFS_USER_KEY_EMAIL) + "\n"
            if (DEBUG) println "AuslegungPrefHelper getPrefValue value -> ${value}"
        }
        catch (Exception e) {
            if (DEBUG) println "AuslegungPrefHelper getPrefValue -> ${e.dump()}"
        }
        return value;
    }

}

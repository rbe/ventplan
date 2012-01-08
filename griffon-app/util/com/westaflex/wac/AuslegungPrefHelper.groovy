/**
 * /Users/rbe/project/wac2/griffon-app/util/com/westaflex/wac/CheckUpdate.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

import java.util.prefs.BackingStoreException

/**
 * WAC-161: Zuletzt geöffnete Projekte
 * Save and load preferences for a Most Recently Used (MRU) list.
 */
class AuslegungPrefHelper {

    public static boolean DEBUG = false    
    
    private static final String PREFS_USER_NODE = "/wacauslegung";
    public static final String PREFS_USER_KEY_FIRMA = "firma";
    public static final String PREFS_USER_KEY_NAME = "name";
    public static final String PREFS_USER_KEY_STRASSE = "strasse";
    public static final String PREFS_USER_KEY_PLZ = "plz";
    public static final String PREFS_USER_KEY_ORT = "ort";

    
    private static AuslegungPrefHelper INSTANCE = null;
    
    public static AuslegungPrefHelper getInstance() {
        if (null == INSTANCE) {
            return new AuslegungPrefHelper();
        }
        else {
            return INSTANCE;
        }
    }
    
    private AuslegungPrefHelper() {
        // just instantiate...
    }
    
    public boolean hasSavedValues() {
        try {
            def value = getPrefValue(PREFS_USER_KEY_FIRMA)
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
            getPrefs().removeNode();
            
            getPrefs().put(PREFS_USER_KEY_FIRMA, map[PREFS_USER_KEY_FIRMA]);
            getPrefs().put(PREFS_USER_KEY_NAME, map[PREFS_USER_KEY_NAME]);
            getPrefs().put(PREFS_USER_KEY_STRASSE, map[PREFS_USER_KEY_STRASSE]);
            getPrefs().put(PREFS_USER_KEY_PLZ, map[PREFS_USER_KEY_PLZ]);
            getPrefs().put(PREFS_USER_KEY_ORT, map[PREFS_USER_KEY_ORT]);
            getPrefs().put(PREFS_USER_KEY_TEL, map[PREFS_USER_KEY_TEL]);
            getPrefs().put(PREFS_USER_KEY_FAX, map[PREFS_USER_KEY_FAX]);
            getPrefs().put(PREFS_USER_KEY_EMAIL, map[PREFS_USER_KEY_EMAIL]);
            
            getPrefs().flush();
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
            value = getPrefs().get(prefKey);
            if (DEBUG) println "AuslegungPrefHelper getPrefValue value -> ${value}, index=${i}"
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
            value = getPrefs().get(PREFS_USER_KEY_FIRMA) + "\n" + 
                    getPrefs().get(PREFS_USER_KEY_NAME) + "\n" + 
                    getPrefs().get(PREFS_USER_KEY_STRASSE) + "\n" + 
                    getPrefs().get(PREFS_USER_KEY_PLZ) + " " + getPrefs().get(PREFS_USER_KEY_ORT) + "\n" + 
                    "Tel: " + getPrefs().get(PREFS_USER_KEY_TEL) + "\n" + 
                    "Fax: " + getPrefs().get(PREFS_USER_KEY_FAX) + "\n" + 
                    "Email: " + getPrefs().get(PREFS_USER_KEY_EMAIL) + "\n"
            if (DEBUG) println "AuslegungPrefHelper getPrefValue value -> ${value}, index=${i}"
        }
        catch (Exception e) {
            if (DEBUG) println "AuslegungPrefHelper getPrefValue -> ${e.dump()}"
        }
        return value;
    }

    protected java.util.prefs.Preferences getPrefs() {
        println "AuslegungPrefHelper getPrefs path=${getPrefs().absolutePath()}"
        return java.util.prefs.Preferences.userRoot().node(PREFS_USER_NODE);
    }

}

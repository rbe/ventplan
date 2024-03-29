/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 02.04.13 10:01
 */

package eu.artofcoding.ventplan.desktop

import java.util.prefs.Preferences

public class PrefHelper {

    public static final String DEFAULT_NODE = "/com/ventplan/desktop"

    public static Preferences getPrefs() {
        Preferences node = Preferences.userRoot().node(DEFAULT_NODE)
        return node;
    }

    public static Preferences getPrefs(String node) {
        Preferences _node = Preferences.userRoot().node(node)
        return _node;
    }

    public static Preferences getPrefs(Class<?> node) {
        Preferences _node = Preferences.userNodeForPackage(node)
        return _node;
    }

    public static String getPrefValue(String key) {
        String value = getPrefValue(DEFAULT_NODE, key)
        return value
    }

    public static String getPrefValue(String node, String key, String defaultValue = '') {
        String value = null
        try {
            value = getPrefs(node).get(key, defaultValue)
        } catch (Exception e) {
            // ignore
        }
        return value
    }

    public static String getPrefValue(Class<?> node, String key, String defaultValue = '') {
        String value = null
        try {
            value = getPrefs(node).get(key, defaultValue)
        } catch (Exception e) {
            // ignore
        }
        return value
    }

    public static void setPrefValue(String key, String value) {
        Preferences prefs = getPrefs(DEFAULT_NODE)
        prefs.put(key, value)
        prefs.flush()
    }

    public static void setPrefValue(String node, String key, String value) {
        Preferences prefs = getPrefs(node)
        prefs.put(key, value)
        prefs.flush()
    }

    public static void setPrefValue(Class<?> node, String key, String value) {
        Preferences prefs = getPrefs(node)
        prefs.put(key, value)
        prefs.flush()
    }

    public static boolean hasSavedValues(String key) {
        hasSavedValues(DEFAULT_NODE, key)
    }

    public static boolean hasSavedValues(String node, String key) {
        try {
            def value = getPrefValue(node, key)
            if (value) {
                return true
            } else {
                return false
            }
        } catch (e) {
            // ignore
        }
        return false
    }

    public static boolean hasSavedValues(Class<?> node, String key) {
        try {
            def value = getPrefValue(node, key)
            if (value) {
                return true
            } else {
                return false
            }
        } catch (e) {
            // ignore
        }
        return false
    }

}

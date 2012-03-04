/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

/**
 * Provide access to resources.
 */
class Wac2Resource {

    /**
     * Get URL for splash screen.
     */
    def static getSplashScreenURL = {
        // dev
        def r = Wac2Resource.class.getResource("../resources/image/ventplan_splash.png")
        // prod
        if (!r)
            r = Wac2Resource.class.getResource("/image/ventplan_splash.png")
        r
    }

    /**
     * Get URI for XSD of WPX files.
     */
    def static getWPXXSDAsURL = {
        // dev
        def r = Wac2Resource.class.getResource("../resources/xml/westaflex-project.xsd")
        // prod
        if (!r)
            r = Wac2Resource.class.getResource("/xml/westaflex-project.xsd")
        r.toURI()
    }

    /**
     * Get stream for XSD of WPX files.
     */
    def static getWPXXSDAsStream = {
        // dev
        def r = Wac2Resource.class.getResourceAsStream("../resources/xml/westaflex-project.xsd")
        // prod
        if (!r)
            r = Wac2Resource.class.getResourceAsStream("/xml/westaflex-project.xsd")
        r
    }

    /**
     * Get image for 'Widerstand'.
     */
    def static getWiderstandURL = { n ->
        def r
        try {
            // dev
            r = Wac2Resource.class.getResource("../resources/widerstand/${n}.jpg")
            // prod
            if (!r)
                r = Wac2Resource.class.getResource("/widerstand/${n}.jpg")
        } catch (NullPointerException e) {
            r = ""
        }
        r
    }

    /**
     * Get template for OpenOffice.
     */
    def static getOOoURL = { t ->
        // dev
        def r = Wac2Resource.class.getResource("../resources/ooo/${t}.ott")
        // prod
        if (!r)
            r = Wac2Resource.class.getResource("/ooo/${t}.ott")
        r
    }

    /**
     * Get template for OpenOffice.
     */
    def static getOOoAsStream = { t ->
        // dev
        def r = Wac2Resource.class.getResourceAsStream("../resources/ooo/${t}.ott")
        // prod
        if (!r)
            r = Wac2Resource.class.getResourceAsStream("/ooo/${t}.ott")
        r
    }

    /**
     * Get Pdf Logo.
     */
    def static getPdfLogo = {
        // dev
        def r = Wac2Resource.class.getResource("../resources/image/westaflex_logo.png")
        // prod
        if (!r)
            r = Wac2Resource.class.getResource("/image/westaflex_logo.png")
        r
    }

    /**
     * Get wac web service properties.
     */
    def static getWacwsProperties() {
        // dev
        def r = Wac2Resource.class.getResourceAsStream("/ventplan.properties")
        // prod
        if (!r)
            r = Wac2Resource.class.getResourceAsStream("/wacws/ventplan.properties")
        r
    }

    /**
     * Get version.
     * Caution: 
     * Had to change this because "version" file could not be loaded in griffon 0.9.4.
     */
    def static getConfVersion() {
        // dev
        def r = Wac2Resource.class.getResourceAsStream("/version.properties")
        // prod
        if (!r)
            r = Wac2Resource.class.getResourceAsStream("/version.properties")
        r
    }

}

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
        def r = Wac2Resource.class.getResource("../resources/image/VentPlan_splash.png")
        // prod
        if (!r)
            r = Wac2Resource.class.getResource("/image/VentPlan_splash.png")
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

}

/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/16/12 10:35 AM
 */
package com.ventplan.desktop

/**
 * Provide access to resources.
 */
class VentplanResource {

    /**
     * Get URL for splash screen.
     */
    def static getSplashScreenURL = {
        // dev
        def r = VentplanResource.class.getResource('../resources/image/ventplan_splash.png')
        // prod
        if (!r) {
            r = VentplanResource.class.getResource('/image/ventplan_splash.png')
        }
        r
    }

    /**
     * TODO Rename to VPX
     * Get URI for XSD of WPX files.
     */
    def static getWPXXSDAsURL = {
        // dev
        def r = VentplanResource.class.getResource('../resources/xml/ventplan-project.xsd')
        // prod
        if (!r) {
            r = VentplanResource.class.getResource('/xml/ventplan-project.xsd')
        }
        r.toURI()
    }

    /**
     * TODO Rename to VPX
     * Get stream for XSD of WPX files.
     */
    def static getWPXXSDAsStream = {
        // dev
        def r = VentplanResource.class.getResourceAsStream('../resources/xml/ventplan-project.xsd')
        // prod
        if (!r) {
            r = VentplanResource.class.getResourceAsStream('/xml/ventplan-project.xsd')
        }
        r
    }

    /**
     * Get image for 'Widerstand'.
     * @param n ID of image below resources/widerstand/xxx.jpg
     * @return URL to image.
     */
    def static getWiderstandURL = { n ->
        def r
        try {
            // dev
            r = VentplanResource.class.getResource("../resources/widerstand/${n}.jpg")
            // prod
            if (!r) {
                r = VentplanResource.class.getResource("/widerstand/${n}.jpg")
            }
        } catch (NullPointerException e) {
            r = null
        }
        r
    }

    /**
     * TODO Rename method
     * Get VentPlan web service properties.
     */
    def static getWacwsProperties() {
        // dev
        def r = VentplanResource.class.getResourceAsStream("/ventplan.properties")
        // prod
        if (!r)
            r = VentplanResource.class.getResourceAsStream("/wacws/ventplan.properties")
        r
    }

}

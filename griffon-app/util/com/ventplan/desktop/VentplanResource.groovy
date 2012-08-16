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
    static URL getSplashScreenURL() {
        // dev
        def r = VentplanResource.class.getResource('../resources/image/ventplan_splash.png')
        // prod
        if (!r) {
            r = VentplanResource.class.getResource('/image/ventplan_splash.png')
        }
        r
    }

    /**
     * Get URI for XSD of WPX files.
     */
    static URI getVPXXSDAsURI() {
        // dev
        def r = VentplanResource.class.getResource('../resources/xml/ventplan-project.xsd')
        // prod
        if (!r) {
            r = VentplanResource.class.getResource('/xml/ventplan-project.xsd')
        }
        r.toURI()
    }

    /**
     * Get stream for XSD of WPX files.
     */
    static InputStream getVPXXSDAsStream() {
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
    static URL getWiderstandURL(String n) {
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
     * URL for Ventplan updates.
     */
    static String getUpdateUrl() {
        return getVentplanProperties().get('ventplan.update.check.url') as String
    }

    static String getPrinzipskizzeSoapUrl() {
        return getVentplanProperties().get('service.prinzipskizze.soap.url') as String
    }

    static String getOdiseeServiceRestUrl() {
        return getVentplanProperties().get('service.odisee.rest.url') as String
    }

    static String getOdiseeServiceRestPath() {
        return getVentplanProperties().get('service.odisee.rest.path') as String
    }

    static String getVentplanVersion() {
        return getVentplanProperties().get('ventplan.version') as String
    }

    /**
     * Get Ventplan properties as a stream.
     * @return InputStream reference.
     */
    static InputStream getVentplanPropertiesAsStream() {
        // dev
        def r = VentplanResource.class.getResourceAsStream('/ventplan.properties')
        // prod
        if (!r) {
            r = VentplanResource.class.getResourceAsStream('/wacws/ventplan.properties')
        }
        r
    }

    /**
     * Get Ventplan properties.
     * @return Properties reference.
     */
    static Properties getVentplanProperties() {
        Properties properties = new Properties()
        def p = VentplanResource.getVentplanPropertiesAsStream()
        properties.load(p)
        return properties
    }

}

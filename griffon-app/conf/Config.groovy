/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 16:56
 */

log4j = {
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c - %m%n')
    }
    error 'org.codehaus.griffon'
    info 'griffon.util',
            'griffon.core',
            'griffon.swing',
            'griffon.app',
            'spring'
    info 'griffon.plugins.datasource'
    info 'org.apache.http', 'org.apache.http.headers', 'org.apache.http.wire', 'groovyx.net.http'
}

griffon.datasource.injectInto = ['service']

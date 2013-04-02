/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 02.04.13 09:20
 */

package eu.artofcoding.griffon.helper

public class HttpCategory {

    def static leftShift(File file, URL url, String user = null, String passwd = null) {
        InputStream input = null
        OutputStream output = null
        try {
            input = url.openStream()
            if (user && passwd) {
                String remoteAuth = "Basic " + "${user}:${passwd}".getBytes().encodeBase64().toString()
                url.setRequestProperty("Authorization", remoteAuth);
            }
            output = new BufferedOutputStream(new FileOutputStream(file))
            output << input
        } finally {
            input?.close()
            output?.close()
        }
    }

    def static leftShift(OutputStream stream, URL url, String user = null, String passwd = null) {
        InputStream input = null
        OutputStream output = null
        try {
            input = url.openStream()
            if (user && passwd) {
                String remoteAuth = "Basic " + "${user}:${passwd}".getBytes().encodeBase64().toString()
                url.setRequestProperty("Authorization", remoteAuth);
            }
            output = new BufferedOutputStream(stream)
            output << input
        } finally {
            input?.close()
            output?.close()
        }
    }

}

/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 17:23
 */
package eu.artofcoding.griffon.helper

import org.xml.sax.EntityResolver
import org.xml.sax.InputSource

class CachedDTD {

    def static entityResolver = [
            resolveEntity: { String publicId, String systemId ->
                try {
                    new InputSource(CachedDTD.class.getResourceAsStream("dtd/${systemId.split('/').last()}"))
                } catch (e) {
                    e.printStackTrace()
                    null
                }
            }
    ] as EntityResolver

}

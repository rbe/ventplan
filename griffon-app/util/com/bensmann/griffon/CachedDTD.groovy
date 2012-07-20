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
package com.bensmann.griffon

/**
 *
 */
class CachedDTD {

    /**
     *
     */
    def static entityResolver = [
            resolveEntity: { publicId, systemId ->
                try {
                    new org.xml.sax.InputSource(CachedDTD.class.getResourceAsStream("dtd/" + systemId.split("/").last()))
                } catch (e) {
                    e.printStackTrace()
                    null
                }
            }
    ] as org.xml.sax.EntityResolver

}

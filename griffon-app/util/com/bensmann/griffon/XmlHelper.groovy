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

class XmlHelper {

    /**
     * Groovy DOM builder.
     */
    def static domBuilder

    /**
     * Try to create a node.
     */
    def static tc = { valueClosure, defaultClosure = null ->
        try {
            //println "valueClosure -> ${valueClosure?.dump()}"
            valueClosure()
        } catch (e) {
            // Default?
            if (defaultClosure) {
                defaultClosure()
            }
        }
    }

    /**
     *
     */
    def static m = { keys, map ->
        if (map) {
            keys.each { k ->
                XmlHelper.tc { domBuilder."${k}"(map[k] ?: '') } { XmlHelper.domBuilder."${k}"() }
            }
        }
    }

    /**
     * XML value to String
     */
    def static vs = { closure ->
        def std = ''
        try {
            return closure() ?: std
        } catch (e) { /*println e*/ }
        std
    }

    /**
     * XML value as Integer
     */
    def static vi = { closure ->
        def std = 0
        try {
            return (closure() as Integer) ?: std
        } catch (e) { /*println e*/ }
        std
    }

    /**
     * XML value as Double
     */
    def static vd = { closure ->
        def std = 0.0d
        try {
            return (closure() as Double) ?: std
        } catch (e) { /*println e*/ }
        std
    }

    /**
     * XML value as Boolean
     */
    def static vb = { closure ->
        def std = false
        try {
            return (closure() as Boolean) ?: std
        } catch (e) { /*println e*/ }
        std
    }

}

/**
 * /Users/rbe/project/wac2/griffon-app/util/com/bensmann/griffon/XmlHelper.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
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
			valueClosure()
		} catch (e) {
			//println "tc: CATCHED: ${e}"
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
				XmlHelper.tc { domBuilder."${k}"(map[k] ?: "") } { XmlHelper.domBuilder."${k}"() }
			}
		}
	}
	
	/**
	 * XML value to String
	 */
	def static vs = { closure ->
		def std = ""
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
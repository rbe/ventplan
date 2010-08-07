/**
 * /Users/rbe/project/wac2/griffon-app/util/com/bensmann/griffon/CachedDTD.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
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

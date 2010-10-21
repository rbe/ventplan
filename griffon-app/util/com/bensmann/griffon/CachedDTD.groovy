/**
 * /Users/rbe/project/wac2/griffon-app/util/com/bensmann/griffon/CachedDTD.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
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

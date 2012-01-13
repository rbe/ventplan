/**
 * WAC
 * 
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. All rights reserved.
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

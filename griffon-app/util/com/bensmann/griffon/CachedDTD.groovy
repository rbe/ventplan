/**
 * /Users/rbe/project/wac2/griffon-app/util/com/bensmann/griffon/CachedDTD.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
public class CachedDTD implements org.xml.sax.EntityResolver {
	
	/**
	 * 
	 */
	public org.xml.sax.InputSource resolveEntity(String publicId, String systemId) throws org.xml.sax.SAXException, java.io.IOException {
		String resource = systemId.substring(systemId.lastIndexOf("/") + 1)
		println "resolveEntity: publicId=${publicId} systemId=${systemId} resource=${resource}"
		try {
			InputStream uri = CachedDTD.class.getResourceAsStream("dtd/" + resource)
			println "resolveEntity: uri=${CachedDTD.class.getResource("dtd/" + resource)}"
			new org.xml.sax.InputSource(uri)
		} catch (e) {
			e.printStackTrace()
			null
		}
	}
	
}

/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/conf/Wac2Resource.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */

/**
 * Provide access to resources.
 */
class Wac2Resource {
	
	/**
	 * Get URL for splash screen.
	 */
	def static getSplashScreenUrl = {
		// dev
		def r = Wac2Resource.class.getResource("../resources/splash.png")
		// prod
		if (!r) r = Wac2Resource.class.getResource("/splash.png")
		r
	}
	
	/**
	 * Get URI for XSD of WPX files.
	 */
	def static getWPXXSDAsUri = {
		// dev
		def r = Wac2Resource.class.getResource("../resources/westaflex-project.xsd")
		// prod
		if (!r) r = Wac2Resource.class.getResource("/westaflex-project.xsd")
		r.toURI()
	}
	
	/**
	 * Get stream for XSD of WPX files.
	 */
	def static getWPXXSDAsStream = {
		// dev
		def r = Wac2Resource.class.getResourceAsStream("../resources/westaflex-project.xsd")
		// prod
		if (!r) r = Wac2Resource.class.getResourceAsStream("/westaflex-project.xsd")
		r
	}
	
	/**
	 * Get image for 'Widerstand'.
	 */
	def static getWiderstandUrl = { n ->
		// dev
		def r = Wac2Resource.class.getResource("../resources/widerstand/${n}.jpg")
		// prod
		if (!r) r = Wac2Resource.class.getResource("/widerstand/${n}.jpg")
	}
	
}

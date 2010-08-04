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
		Wac2Resource.class.getResource("../resources/splash.png")
	}
	
	/**
	 * Get URI for XSD of WPX files.
	 */
	def static getWpxXsdUri = {
		Wac2Resource.class.getResource("../resources/westaflex-project-1.0.2.xsd").toURI()
	}
	
	/**
	 * Get image for 'Widerstand'.
	 */
	def static getWiderstandUrl = { n ->
		Wac2Resource.class.getResource("../resources/widerstand/${n}.jpg")
	}
	
}

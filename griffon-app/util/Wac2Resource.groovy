/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/conf/Wac2Resource.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
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
	def static getSplashScreenURL = {
		// dev
		def r = Wac2Resource.class.getResource("../resources/image/splash.png")
		// prod
		if (!r) r = Wac2Resource.class.getResource("/image/splash.png")
		r
	}
	
	/**
	 * Get URI for XSD of WPX files.
	 */
	def static getWPXXSDAsURL = {
		// dev
		def r = Wac2Resource.class.getResource("../resources/xml/westaflex-project.xsd")
		// prod
		if (!r) r = Wac2Resource.class.getResource("/xml/westaflex-project.xsd")
		r.toURI()
	}
	
	/**
	 * Get stream for XSD of WPX files.
	 */
	def static getWPXXSDAsStream = {
		// dev
		def r = Wac2Resource.class.getResourceAsStream("../resources/xml/westaflex-project.xsd")
		// prod
		if (!r) r = Wac2Resource.class.getResourceAsStream("/xml/westaflex-project.xsd")
		r
	}
	
	/**
	 * Get image for 'Widerstand'.
	 */
	def static getWiderstandURL = { n ->
        try
        {
            // dev
            def r = Wac2Resource.class.getResource("../resources/widerstand/${n}.jpg")
            // prod
            if (!r) r = Wac2Resource.class.getResource("/widerstand/${n}.jpg")
            r
        }
        catch (NullPointerException e)
        { }
        ""
	}
	
	/**
	 * Get template for OpenOffice.
	 */
	def static getOOoURL = { t ->
		// dev
		def r = Wac2Resource.class.getResource("../resources/ooo/${t}.ott")
		// prod
		if (!r) r = Wac2Resource.class.getResource("/ooo/${t}.ott")
		r
	}
	
	/**
	 * Get template for OpenOffice.
	 */
	def static getOOoAsStream = { t ->
		// dev
		def r = Wac2Resource.class.getResourceAsStream("../resources/ooo/${t}.ott")
		// prod
		if (!r) r = Wac2Resource.class.getResourceAsStream("/ooo/${t}.ott")
		r
	}
	
}
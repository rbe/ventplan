/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/conf/Wac2Resource.groovy
 * 
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011 art of coding UG (haftungsbeschränkt).
 *
 * Nutzungslizenz siehe http://files.art-of-coding.eu/aoc/AOCPL_v10_de.html
 * Use is subject to license terms, see http://files.art-of-coding.eu/aoc/AOCPL_v10_en.html
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
        def r
        try {
            // dev
            r = Wac2Resource.class.getResource("../resources/widerstand/${n}.jpg")
            // prod
            if (!r) r = Wac2Resource.class.getResource("/widerstand/${n}.jpg")
        } catch (NullPointerException e) { 
            r = ""
        }
        r
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
    
    /**
	 * Get template for OpenOffice.
	 */
	def static getPdfLogo = {
		// dev
		def r = Wac2Resource.class.getResource("../resources/image/westaflex_logo.png")
		// prod
		if (!r) r = Wac2Resource.class.getResource("/image/westaflex_logo.png")
		r
	}
    
    /**
	 * Get template for OpenOffice.
	 */
	def static getWacwsProperties = {
		// dev
		def r = Wac2Resource.class.getResourceAsStream("../resources/wacws/wacws.properties")
		// prod
		if (!r) r = Wac2Resource.class.getResourceAsStream("/wacws/wacws.properties")
		r
	}
	
}

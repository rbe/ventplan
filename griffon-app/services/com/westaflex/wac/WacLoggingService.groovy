/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/services/com/westaflex/wac/WacLoggingService.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
package com.westaflex.wac

/**
 * The all-mighty WAC logging service.
 */
class WacLoggingService {
	
	/**
	 * Instance of a Java Logging API implementation.
	 */
	def logger
	
	/**
	 * Name of class we are logging for.
	 */
	def klass
	
	/**
	 * The maximum level we should log.
	 */
	def level
	
	/**
	 * 
	 */
	def info(text) {
		logger.info(text)
	}
	
}

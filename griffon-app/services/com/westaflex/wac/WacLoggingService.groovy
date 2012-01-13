/**
 * WAC
 * 
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. All rights reserved.
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

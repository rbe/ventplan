/*
 * WAC
 *
 * Copyright (C) 2005      Informationssysteme Ralf Bensmann.
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
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

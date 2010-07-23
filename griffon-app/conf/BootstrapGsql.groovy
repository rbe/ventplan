/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/conf/BootstrapGsql.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */
import groovy.sql.Sql

/**
 * 
 */
class BootstrapGsql {
	
	def init = { Sql sql ->
		// Set splash screen status text
		Wac2Splash.instance.connectingDatabase()
		println "BootstrapGsql.init: ${sql}"
	}
	
	def destroy = { Sql sql ->
		println "BootstrapGsql.destroy: ${sql}"
	}
	
}

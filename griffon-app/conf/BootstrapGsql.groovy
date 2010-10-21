/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/conf/BootstrapGsql.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
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
		//println "BootstrapGsql.init: ${sql}"
	}
	
	def destroy = { Sql sql ->
		//println "BootstrapGsql.destroy: ${sql}"
	}
	
}

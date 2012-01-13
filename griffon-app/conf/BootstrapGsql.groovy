/**
 * WAC
 * 
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2010-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. All rights reserved.
 * Created by: rbe
 */
import groovy.sql.Sql

/**
 * 
 */
class BootstrapGsql {
    
    def init = { String dataSourceName = 'default', Sql sql ->
        // Set splash screen status text
        Wac2Splash.instance.connectingDatabase()
        //println "BootstrapGsql.init: ${sql}"
    }
    
    def destroy = { String dataSourceName = 'default', Sql sql ->
        //println "BootstrapGsql.destroy: ${sql}"
    }
    
}

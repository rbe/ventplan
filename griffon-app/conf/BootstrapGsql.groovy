/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
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

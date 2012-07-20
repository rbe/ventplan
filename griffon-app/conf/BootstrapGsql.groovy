/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/7/12 3:42 PM
 */

import groovy.sql.Sql
import com.ventplan.desktop.VentplanSplash

/**
 *
 */
class BootstrapGsql {

    /**
     *
     */
    def init = { String dataSourceName = 'default', Sql sql ->
        // Set splash screen status text
        VentplanSplash.instance.connectingDatabase()
        //println "BootstrapGsql.init: ${sql}"
    }

    /**
     *
     */
    def destroy = { String dataSourceName = 'default', Sql sql ->
        //println "BootstrapGsql.destroy: ${sql}"
    }

}

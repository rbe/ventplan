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

import eu.artofcoding.griffon.helper.HttpHelper
import eu.artofcoding.ventplan.desktop.VentplanResource
import eu.artofcoding.ventplan.desktop.VentplanSplash
import groovy.sql.Sql

class BootstrapGsql {

    def init = { String dataSourceName = 'default', Sql sql ->
        // Set splash screen status text: connecting database
        VentplanSplash.instance.connectingDatabase()
        // Set splash screen status text: updating database
        VentplanSplash.instance.updatingDatabase()
        try {
            String baseurl = VentplanResource.getDatabaseUpdateUrl()
            int me = sql.firstRow('SELECT dbrev FROM ventplan WHERE id = 1')[0] as int
            int head = HttpHelper.download("${baseurl}/head").toInteger()
            if (me + 1 < head) {
                sql.withTransaction {
                    (me + 1).upto head, {
                        String content = HttpHelper.download("${baseurl}/${it}.sql")
                        content.eachLine {
                            //print "rev#${me} -> rev#${it}: ${it}"
                            sql.executeUpdate(it)
                        }
                    }
                }
            } else {
                //println "rev#${me} == rev#${head}"
            }
            sql.executeUpdate("UPDATE ventplan SET DBREV = ${head} WHERE ID = 1")
        } catch (e) {
            // ignore
        } finally {
            // ignore
        }
    }

    def destroy = { String dataSourceName = 'default', Sql sql ->
    }

}

/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 16:51
 */

dataSource {
    driverClassName = 'org.sqlite.JDBC'
}

environments {
    development {
        dataSource {
            url = 'jdbc:sqlite:../sql/ventplan.db'
        }
    }
    test {
        dataSource {
            url = 'jdbc:sqlite:../sql/ventplan.db'
        }
    }
    production {
        dataSource {
            // install4j
            url = 'jdbc:sqlite:lib/ventplan.db'
            //
            // Windows
            //
            // griffon prod prepare-windows
            // griffon prod create-windows
            //url = 'jdbc:sqlite:../lib/ventplan.db'
            //
            // OS X
            //
            // griffon prod prepare-mac
            // griffon prod create-mac
            // url = 'jdbc:sqlite:Ventplan.app/Contents/Resources/Java/ventplan.db'
            //
            // Package
            //
            // griffon prod package zip
            // url = 'jdbc:sqlite:lib/ventplan.db'
            // griffon prod package jar
            // url = 'jdbc:sqlite:ventplan.db'
        }
    }
}

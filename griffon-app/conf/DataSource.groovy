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
            // install dir of izpack.
            url = 'jdbc:sqlite:../lib/ventplan.db'
        }
    }
}

/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

dataSource {
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
environments {
    development {
        dataSource {
            url = "jdbc:h2:zip:../lib/dtmp.zip!/westawac"
        }
    }
    test {
        dataSource {
            url = "jdbc:h2:zip:../lib/dtmp.zip!/westawac"
        }  
    }
    production {
        dataSource {
            // install dir of izpack.
            url = "jdbc:h2:zip:../sql/dtmp.zip!/westawac"
        }
    }
}

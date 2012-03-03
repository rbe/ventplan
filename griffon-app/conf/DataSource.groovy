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
  //driverClassName = "org.h2.Driver"
    driverClassName = "org.sqlite.JDBC"
  //username = "sa"
    username = ''
    password = ''
}
environments {
    development {
        dataSource {
          //url = "jdbc:h2:zip:../lib/dtmp.zip!/westawac"
            url = "jdbc:sqlite:../sql/westawac.sqlite.db"
        }
    }
    test {
        dataSource {
          //url = "jdbc:h2:zip:../lib/dtmp.zip!/westawac"
            url = "jdbc:sqlite:../sql/westawac.sqlite.db"
        }
    }
    production {
        dataSource {
          //url = "jdbc:h2:zip:lib/dtmp.zip!/westawac"
            url = "jdbc:sqlite:lib/westawac.sqlite.db"
            // install dir of izpack.
            //url = "jdbc:sqlite:../sql/westawac.sqlite.db"
        }
    }
}

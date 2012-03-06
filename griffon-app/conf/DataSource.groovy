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
    driverClassName = 'org.sqlite.JDBC'
}
environments {
    development {
        dataSource {
            url = 'jdbc:sqlite:../sql/VentPlan.db'
        }
    }
    test {
        dataSource {
            url = 'jdbc:sqlite:../sql/VentPlan.db'
        }
    }
    production {
        dataSource {
            // install dir of izpack.
            url = 'jdbc:sqlite:lib/VentPlan.db'
        }
    }
}

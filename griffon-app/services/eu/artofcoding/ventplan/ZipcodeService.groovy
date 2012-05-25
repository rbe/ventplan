/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package eu.artofcoding.ventplan

class ZipcodeService {

    /**
     *
     * @param zipcode
     * @return
     */
    Map findVertreter(String zipcode) {
        def r = withSql { dataSourceName, sql ->
            sql.rows('SELECT * FROM handelsvertretung'
                     + ' WHERE plzvon <= ?.zipcode AND plzbis >= ?.zipcode',
                    [zipcode: zipcode])
        }
        r ? r[0] : null
    }

}

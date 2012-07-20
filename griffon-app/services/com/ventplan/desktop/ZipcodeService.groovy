/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/16/12 10:35 AM
 */
package com.ventplan.desktop

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

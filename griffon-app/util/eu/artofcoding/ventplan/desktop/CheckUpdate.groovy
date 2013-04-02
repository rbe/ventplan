/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 17:23
 */

package eu.artofcoding.ventplan.desktop

import griffon.swing.SwingApplication

/**
 * Check for an update.
 */
class CheckUpdate implements Runnable {

    /**
     * Has user acknowledged? 
     */
    private static boolean userAcknowledged = false

    /**
     * Griffon app.
     */
    SwingApplication app

    /**
     * Just check if URL exists.
     * @return True, if URL exists.
     */
    boolean update() {
        boolean b = false
        try {
            String url = String.format(VentplanResource.updateUrl, VentplanResource.ventplanVersion)
            URI.create(url).toURL().withInputStream {}
            b = true
        } catch (FileNotFoundException e) {
            // ignore
        } catch (Exception e) {
            // ignore
        }
        return b
    }

    void run() {
        // Check for updates as long as: no updates were found or user acknowledged a new update
        while (!userAcknowledged) {
            if (update() && !userAcknowledged) {
                // Bitte Dialog mit Link einbauen zu http://www.ventplan.com/latest/
                app.controllers['MainFrame'].checkUpdateDialogOeffnen()
                userAcknowledged = true
            }
            // Check every 10 minutes
            try {
                Thread.sleep(10 * 60 * 1000)
            } catch (Exception e) {
                // ignore
            }
        }
    }

}

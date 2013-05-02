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

import griffon.plugins.splash.SplashScreen

/**
 * Manage a splash screen.
 */
@Singleton
class VentplanSplash {

    def setup = {
        // Set a splash image
        URL url = VentplanResource.getSplashScreenURL()
        SplashScreen.instance.setImage(url)
        SplashScreen.instance.showStatus("...")
        // Show splash screen
        SplashScreen.instance.splash()
        SplashScreen.instance.waitForSplash()
    }

    def dispose = {
        try {
            SplashScreen.instance?.dispose()
        } catch (e) {}
    }

    def initializing = {
        SplashScreen.instance.showStatus("Phase 1/5: Initialisiere...")
    }

    def connectingDatabase = {
        SplashScreen.instance.showStatus("Phase 2/5: Verbinde zur Datenbank...")
    }

    def updatingDatabase(String detail = null) {
        SplashScreen.instance.showStatus("Phase 3/5: Aktualisiere Datenbank...${detail ?: ''}")
    }

    def creatingUI = {
        SplashScreen.instance.showStatus("Phase 4/5: Erstelle die Benutzeroberfläche...")
    }

    def startingUp = {
        SplashScreen.instance.showStatus("Phase 5/5: Starte die Applikation...")
    }

}

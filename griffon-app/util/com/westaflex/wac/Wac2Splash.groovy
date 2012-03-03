/*
 * WAC
 *
 * Copyright (C) 2005      Informationssysteme Ralf Bensmann.
 * Copyright (C) 2009-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

/**
 * Manage a splash screen.
 */
@Singleton
class Wac2Splash {

    /**
     * Instance of Griffon splash screen plugin
     */
    //def splashScreen = griffon.plugins.splash.SplashScreen.instance

    def setup = {
        //println "Wac2Splash setup"
        // Set a splash image
        URL url = Wac2Resource.getSplashScreenURL()
        griffon.plugins.splash.SplashScreen.instance.setImage(url)
        griffon.plugins.splash.SplashScreen.instance.showStatus("...")
        // Show splash screen
        griffon.plugins.splash.SplashScreen.instance.splash()
        griffon.plugins.splash.SplashScreen.instance.waitForSplash()
    }

    def dispose = {
        //println "Wac2Splash dispose"
        try {
            griffon.plugins.splash.SplashScreen.instance?.dispose()
        } catch (e) {}
    }

    def initializing = {
        griffon.plugins.splash.SplashScreen.instance.showStatus("Phase 1/4: Initialisiere...")
    }

    def connectingDatabase = {
        griffon.plugins.splash.SplashScreen.instance.showStatus("Phase 2/4: Verbinde zur Datenbank...")
    }

    def creatingUI = {
        griffon.plugins.splash.SplashScreen.instance.showStatus("Phase 3/4: Erstelle die Benutzeroberfläche...")
    }

    def startingUp = {
        griffon.plugins.splash.SplashScreen.instance.showStatus("Phase 4/4: Starte die Applikation...")
    }

    def creatingProject = {
        griffon.plugins.splash.SplashScreen.instance.showStatus("Phase 1/3: Erstelle ein neues Projekt...")
    }

    def initializingProject = {
        griffon.plugins.splash.SplashScreen.instance.showStatus("Phase 2/3: Initialisiere das Projekt...")
    }

    def creatingUiForProject = {
        griffon.plugins.splash.SplashScreen.instance.showStatus("Phase 3/3: Erstelle Benutzeroberfläche für das Projekt...")
    }

    def loadingProject = {
        griffon.plugins.splash.SplashScreen.instance.showStatus("Phase 2/3: Lade Daten aus dem Projekt...")
    }

}

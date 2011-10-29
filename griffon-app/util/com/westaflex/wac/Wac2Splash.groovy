/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/conf/Wac2Splash.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Nutzungslizenz siehe http://www.bensmann.com/BPL_v10_de.html
 * Use is subject to license terms, see http://www.bensmann.com/BPL_v10_en.html
 * 
 * Created by: rbe
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
        println "Wac2Splash setup"
		// Set a splash image
		URL url = Wac2Resource.getSplashScreenURL()
		griffon.plugins.splash.SplashScreen.instance.setImage(url)
		griffon.plugins.splash.SplashScreen.instance.showStatus("...")
		// Show splash screen
		griffon.plugins.splash.SplashScreen.instance.splash()
		griffon.plugins.splash.SplashScreen.instance.waitForSplash()
	}
	
	def dispose = {
        println "Wac2Splash dispose"
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

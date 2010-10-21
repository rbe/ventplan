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
	def splashScreen = SplashScreen.instance
	
	def setup = {
		// Set a splash image
		URL url = Wac2Resource.getSplashScreenURL()
		splashScreen.setImage(url)
		splashScreen.showStatus("...")
		// Show splash screen
		splashScreen.splash()
		splashScreen.waitForSplash()
	}
	
	def dispose = {
		splashScreen.dispose()
	}
	
	def initializing = {
		splashScreen.showStatus("Phase 1/4: Initialisiere...")
	}
	
	def connectingDatabase = {
		splashScreen.showStatus("Phase 2/4: Verbinde zur Datenbank...")
	}
	
	def creatingUI = {
		splashScreen.showStatus("Phase 3/4: Erstelle die Benutzeroberfläche...")
	}
	
	def startingUp = {
		splashScreen.showStatus("Phase 4/4: Starte die Applikation...")
	}
	
	def creatingProject = {
		splashScreen.showStatus("Phase 1/3: Erstelle ein neues Projekt...")
	}
	
	def initializingProject = {
		splashScreen.showStatus("Phase 2/3: Initialisiere das Projekt...")
	}

	def creatingUiForProject = {
		splashScreen.showStatus("Phase 3/3: Erstelle Benutzeroberfläche für das Projekt...")
	}
	
	def loadingProject = {
		splashScreen.showStatus("Phase 2/3: Lade Daten aus dem Projekt...")
	}
	
}

/**
 * /Users/rbe/project/westaflex/WestaWAC2/griffon-app/conf/Wac2Splash.groovy
 * 
 * Copyright (C) 2010 Informationssysteme Ralf Bensmann.
 * Alle Rechte vorbehalten. Nutzungslizenz siehe http://www.bensmann.com/license_de.html
 * All Rights Reserved. Use is subject to license terms, see http://www.bensmann.com/license_en.html
 * 
 * Created by: rbe
 */

@Singleton
class Wac2Splash {
	
	/**
	 * Instance of Griffon splash screen plugin
	 */
	def splashScreen = SplashScreen.instance
	
	def init = {
		// Set a splash image
		URL url = this.class.getResource("../resources/splash.png")
		splashScreen.setImage(url)
		// Set splash screen status text
		splashScreen.showStatus("Phase 1/4: Initializing...")
		splashScreen.splash()
		splashScreen.waitForSplash()
	}
	
	def initialized = {
		splashScreen.showStatus("Phase 1/4: Initializing... done")
	}
	
	def connectingDatabase = {
		splashScreen.showStatus("Phase 2/4: Connecting database...")
	}
	
	def creatingUI = {
		splashScreen.showStatus("Phase 3/4: Creating UI...")
	}
	
	def startingUp = {
		splashScreen.showStatus("Phase 4/4: Starting up...")
	}
	
}

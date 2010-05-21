package com.westaflex.wac

/**
 * 
 */
//@Singleton
class WacCalculationService {
	
	static scope = "singleton"
	
	/**
	 * Ermittelt das Volumen aus den vorhandenen Werten:
	 * Gesamtvolumen = Fläche * Höhe
	 */
	float volumen(float flaeche, float hoehe) {
		flaeche * hoehe
	}
	
	/**
	 * Projekt, Gebäudedaten
	 */
	float mindestaussenluftRate(int personenAnzahl, float aussenluftVolumenstrom) {
		personenAnzahl * aussenluftVolumenstrom
	}
	
}

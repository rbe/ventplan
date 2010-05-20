package com.westaflex.wac

/**
 * 
 */
@Singleton
class WacCalculationService {
	
	/**
	 * Ermittelt das Volumen aus den vorhandenen Werten:
	 * Gesamtvolumen = Fläche * Höhe
	 */
	def berechneVolumen(flaeche, hoehe) {
		wohnflaeche * raumHoehe
	}
	
	/**
	 * Projekt, Gebäudedaten
	 */
	def berechneMindestaussenluftRate(personenAnzahl, aussenluftVolumenstrom) {
		personenAnzahl * aussenluftVolumenstrom
	}
	
}

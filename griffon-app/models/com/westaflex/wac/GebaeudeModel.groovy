package com.westaflex.wac

import groovy.beans.Bindable

/**
 * 
 */
class GebaeudeModel {
	
	// MFH, EFH, Maisonette
	@Bindable boolean typMFH
	@Bindable boolean typEFH
	@Bindable boolean typMaisonette
	// Gebäudelage
	@Bindable boolean windschwach
	@Bindable boolean windstark
	// Wärmeschutz
	@Bindable boolean typWaermeschutzHoch
	@Bindable boolean typWaermeschutzNiedrig
	// Geometrie
	@Bindable double wohnflaeche
	@Bindable double mittlereRaumhöhe
	@Bindable double luftvolumen
	@Bindable double geluefteteFlaeche
	@Bindable double gelueftetesVolumen
	// Luftdichtheit der Gebäudehülle
	@Bindable boolean kategorieA
	@Bindable boolean kategorieB
	@Bindable boolean kategorieC
	@Bindable double druckdifferenzInPa
	@Bindable double luftwechselProStd
	@Bindable double druckexponent
	// Besondere Anforderungen
	@Bindable int faktor
	// Geplante Belegung
	@Bindable int personenzahl
	@Bindable aussenlustVsProPerson
	int mindestAussenluftRate
	
}

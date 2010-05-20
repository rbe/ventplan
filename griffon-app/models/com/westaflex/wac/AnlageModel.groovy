package com.westaflex.wac

import groovy.beans.Bindable

/**
 * 
 */
class AnlageModel {
	
	// Gerätestandort
	@Bindable boolean geraeteStandOrtKG
	@Bindable boolean geraeteStandOrtEG = true
	@Bindable boolean geraeteStandOrtOG
	@Bindable boolean geraeteStandOrtDG
	@Bindable boolean geraeteStandOrtSB
	//Luftkanalverlegung
	@Bindable boolean luftkanalverlegungAufputz
	@Bindable boolean luftkanalverlegungDaemmschicht
	@Bindable boolean luftkanalverlegungDecke
	@Bindable boolean luftkanalverlegungSpitzboden
	// Außenluft
	@Bindable boolean aussenluftDach
	@Bindable boolean aussenluftWand = true
	@Bindable boolean aussenluftErdwaermetauscher
	// Zuluftdurchlässe
	@Bindable boolean zuluftdurchlaesseTellerventile
	@Bindable boolean zuluftdurchlaesseSchlitzauslass
	@Bindable boolean zuluftdurchlaesseFussboden
	@Bindable boolean zuluftdurchlaesseSockel
	// Abluftdurchlässe
	@Bindable boolean abluftdurchlaesseTellerventile
	// Fortluft
	@Bindable boolean fortluftDach = true
	@Bindable boolean fortluftWand
	@Bindable boolean fortluftLichtschacht
	// Energie-Kennzeichen
	@Bindable boolean energieKzWaermerueckgewinnung = true
	@Bindable boolean energieKzBemessung
	@Bindable boolean energieKzRueckgewinnungAbluft
	@Bindable boolean energieKzZweckmaessigeRegelung
	// Hygiene-Kennzeichen
	// Rückschlagklappe
	// Schallschutz-Kennzeichnung
	// Feuerstätten-Kennzeichnung
	
}

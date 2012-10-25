/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 7/16/12 10:35 AM
 */
package com.ventplan.desktop

import ca.odell.glazedlists.*

/**
 *
 */
class VentplanModel {

    /**
     * Status bar.
     */
    @Bindable def statusBarText = 'Bereit.'

    /**
     * Progress bar in status bar.
     * Wert auf true setzen bewirkt, dass die Progress bar "unendlich" durchläuft.
     * Wert auf false setzen beendet das Ganze wieder.
     */
    @Bindable def statusProgressBarIndeterminate = false

    /**
     * Liste aller offenen Projekte - MVC IDs.
     */
    def projekte = []

    /**
     * Die MVC ID des derzeit aktiven Projekts/der aktive Tab.
     */
    @Bindable def aktivesProjekt

    /**
     * Wurde das Model des aktuellen Projekts geändert?
     */
    @Bindable Boolean aktivesProjektGeandert = false

    /**
     * Wurde irgendein Model eines Projekts geändert?
     */
    @Bindable Boolean alleProjekteGeandert = false

    /**
     *
     */
    @Bindable EventList projektSuchenEventList = new BasicEventList()

    /**
     * WAC-234 Wizard Dialog view
     */
    @Bindable
    def wizardmap = [
        gebaude: [
            typ: [mfh: true] as ObservableMap,
            lage: [windschwach: true] as ObservableMap,
            warmeschutz: [hoch: true] as ObservableMap,
            geometrie: [:
                //raumhohe: "0,00",
                //geluftetesVolumen: "0,00"
            ] as ObservableMap,
            luftdichtheit: [
                kategorieA: true,
                druckdifferenz: 2.0d,
                luftwechsel: 1.0d,
                druckexponent: 0.666f
            ] as ObservableMap,
            faktorBesondereAnforderungen: 1.0d,
            geplanteBelegung: [
                personenanzahl: 0,
                aussenluftVsProPerson: 30.0d,
                mindestaussenluftrate: 0.0d
            ] as ObservableMap,
        ] as ObservableMap,
        raum: [
            raume: [
                /* ProjektModel.raumMapTemplate wird durch Event RaumHinzufugen pro Raum erstellt */
            ] as ObservableList,
            ltmZuluftSumme: 0.0d,
            ltmAbluftSumme: 0.0d,
            raumVs: [
                gesamtVolumenNE: 0.0d,
                luftwechselNE: 0.0d,
                gesamtaussenluftVsMitInfiltration: 0.0d
            ] as ObservableMap
        ] as ObservableMap
    ] as ObservableMap

    /**
     * Template für Wizard-Dialog
     */
    def raumMapTemplate = [
            position: 0,
            raumBezeichnung: '',
            raumLuftart: 'ZU',
            raumGeschoss: 'EG',
            raumLange: 0.0d,
            raumBreite: 0.0d,
            raumFlache: 0.0d,
            raumHohe: 0.0d,
            raumZuluftfaktor: 0.0d,
            raumVolumen: 0.0d,
            raumLuftwechsel: 0.0d,
            raumZuluftVolumenstrom: 0.0d,
            raumZuluftVolumenstromInfiltration: 0.0d, // Zuluftfaktor abzgl. Infiltration
            raumAbluftVolumenstrom: 0.0d,
            raumAbluftVolumenstromInfiltration: 0.0d, // Abluftvs abzgl. Infiltration
            raumBezeichnungAbluftventile: '',
            raumAnzahlAbluftventile: 0,
            raumAbluftmengeJeVentil: 0.0d,
            raumBezeichnungZuluftventile: '',
            raumAnzahlZuluftventile: 0,
            raumZuluftmengeJeVentil: 0.0d,
            raumVerteilebene: '',
            raumAnzahlUberstromVentile: 0,
            raumUberstromElement: '',
            raumUberstromVolumenstrom: 0.0d,
            raumNummer: '',
            raumMaxTurspaltHohe: 10.0d,
            turen: []
    ]
}

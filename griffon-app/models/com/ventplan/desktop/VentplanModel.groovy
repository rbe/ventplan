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

}

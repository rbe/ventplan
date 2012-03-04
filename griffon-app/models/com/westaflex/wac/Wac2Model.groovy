/*
 * VentPlan
 *
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann.
 * Copyright (C) 2011-2012 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */
package com.westaflex.wac

import ca.odell.glazedlists.*
import groovy.beans.Bindable

/**
 *
 */
class Wac2Model {

    /**
     * Status bar.
     */
    @Bindable def statusBarText = "Bereit."

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

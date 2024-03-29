/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2013 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * rbe, 19.03.13 17:23
 */
package eu.artofcoding.ventplan.desktop

import javax.swing.event.ListSelectionListener

// Raumdaten - Raum-Eingabe
// Add list selection listener to synchronize every table's selection and model.meta.gewahlterRaum
[raumTabelle].each {
    it.selectionModel.addListSelectionListener([
            valueChanged: { evt ->
                controller.raumInTabelleGewahlt(evt, it)
            }
    ] as ListSelectionListener)
}
// Binding for items of comboboxes is done in RaumdatenView!
//raumEingabePanel.inputMap.put(KeyStroke.getKeyStroke("ENTER"), { ActionEvent e -> println "here we go: ${e}" } as AbstractAction)
// Combobox Raumtyp
raumTyp.actionPerformed = controller.raumTypGeandert
// Raum-Buttons unten
raumHinzufugen.actionPerformed = controller.raumHinzufugen
raumEntfernen.actionPerformed = controller.raumEntfernen
raumKopieren.actionPerformed = controller.raumKopieren
raumBearbeiten.actionPerformed = controller.raumBearbeiten
raumNachObenVerschieben.actionPerformed = controller.raumNachObenVerschieben
raumNachUntenVerschieben.actionPerformed = controller.raumNachUntenVerschieben

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

import javax.swing.plaf.basic.BasicComboBoxUI
import javax.swing.plaf.basic.BasicComboPopup
import javax.swing.plaf.basic.ComboPopup
import java.awt.*

public class CustomComboBoxUI extends BasicComboBoxUI {

    protected ComboPopup createPopup() {
        BasicComboPopup popup = new BasicComboPopup(this.comboBox) {
            @Override
            protected Rectangle computePopupBounds(int px, int py, int pw, int ph) {
                int cwidth = 220;
                try {
                    cwidth = this.comboBox.getPreferredSize().getWidth()
                } catch (Exception e) {
                    // ignore
                }
                //println "cwidth: ${cwidth}, pw: ${pw}, ph: ${ph}"
                return super.computePopupBounds(px, py, Math.max(cwidth, pw), ph);
            }
        };
        popup.getAccessibleContext().setAccessibleParent(this.comboBox);
        return popup;
    }

}

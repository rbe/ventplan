/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * mmu, 13.02.13 20:59
 */
package com.ventplan.desktop

import javax.swing.plaf.basic.BasicComboBoxUI
import javax.swing.plaf.basic.BasicComboPopup
import javax.swing.plaf.basic.ComboPopup
import java.awt.Insets
import java.awt.Rectangle

class CustomComboBoxUI extends BasicComboBoxUI {

    protected ComboPopup createPopup() {
        BasicComboPopup popup = new BasicComboPopup(this.comboBox) {
            @Override
            protected Rectangle computePopupBounds(int px, int py, int pw, int ph) {
                return super.computePopupBounds(px, py, Math.max(200, pw), ph);
            }
        };
        popup.getAccessibleContext().setAccessibleParent(this.comboBox);
        return popup;
    }


}

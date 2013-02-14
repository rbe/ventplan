/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * mmu, 13.02.13 19:09
 */
package com.ventplan.desktop

import javax.swing.ComboBoxModel
import javax.swing.JComboBox

class ImageComboBox extends JComboBox {
    public ImageComboBox() {
        super();
        setUI(new com.ventplan.desktop.CustomComboBoxUI());
    }

    public ImageComboBox(Vector items) {
        super(items);
    }

    public ImageComboBox(ComboBoxModel aModel) {
        super(aModel);
    }

    private boolean layingOut = false;

    public void doLayout(){
        try{
            layingOut = true;
            super.doLayout();
        }finally{
            layingOut = false;
        }
    }

}

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

import javax.swing.*

public class ImageComboBox extends JComboBox {

    private boolean layingOut = false;

    public ImageComboBox() {
        super();
        setUI(new CustomComboBoxUI());
    }

    public ImageComboBox(Vector items) {
        super(items);
    }

    public ImageComboBox(ComboBoxModel aModel) {
        super(aModel);
    }

    public void doLayout() {
        try {
            layingOut = true;
            super.doLayout();
        } catch (e) {
            // ignore
        } finally {
            layingOut = false;
        }
    }

}

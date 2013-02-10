/*
 * Ventplan
 * ventplan, ventplan
 * Copyright (C) 2005-2010 Informationssysteme Ralf Bensmann, http://www.bensmann.com/
 * Copyright (C) 2011-2012 art of coding UG, http://www.art-of-coding.eu/
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 *
 * mmu, 02.02.13 16:21
 */
package com.ventplan.desktop

import javax.swing.DefaultListCellRenderer
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JList
import java.awt.Component
import com.ventplan.desktop.VentplanResource

import java.awt.Image

public class ComboBoxImageRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Get the renderer component from parent class
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        // Get icon to use for the list item value
        Icon icon
        def text = value
        try {
            def url = VentplanResource.getWiderstandURL(value)
            if (url) {
                icon = new ImageIcon(url)
                icon.setImage(icon.getImage().getScaledInstance(30, 30, Image.SCALE_FAST));
            } else {
                url = VentplanResource.getWiderstandURL('no_pic')
                icon = new ImageIcon(url)
                icon.setImage(icon.getImage().getScaledInstance(30, 30, Image.SCALE_FAST));
            }
        } catch (e) {
            println "icon for value ${value} not ${e.cause} found"
        }
        // Set icon to display for value
        if (!icon) {
            def url = VentplanResource.getWiderstandURL('1')
            icon = new ImageIcon(url)
            text = 'Keine Bild vorhanden'
        }
        label.setIcon(icon);
        label.setText(text)
        // set height and width
        //label.setPreferredSize(new Dimension(50, 20))
        return label;
    }

}

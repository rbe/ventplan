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

import javax.swing.*
import java.awt.*
import javax.swing.DefaultListCellRenderer
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JList
import java.awt.Component
import java.awt.Image
import com.ventplan.desktop.VentplanResource

public class ComboBoxImageRenderer extends DefaultListCellRenderer {

    /**
     * Max image height to scale.
     */
    private static final int MAX_IMAGE_HEIGHT = 30

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Get the renderer component from parent class
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        // Get icon to use for the list item value
        Icon icon = null;
        try {
            URL url = VentplanResource.getVentileURL(text);
            if (url) {
                icon = new ImageIcon(url);
                Image scaledIcon = icon.getImage(); //getScaledImage(icon);
                if (scaledIcon) {
                    icon.setImage(scaledIcon);
                }
            } else {
                url = VentplanResource.getVentileURL('no_pic');
                icon = new ImageIcon(url);
                //icon.setImage(icon.getImage().getScaledInstance(30, 30, Image.SCALE_FAST));
                icon.setImage(icon.getImage());
            }
        } catch (e) {
            println "icon for value [${value}] not found. Cause: [${e}]"
        }
        // Set icon to display for value
        if (!icon) {
            URL url = VentplanResource.getVentileURL('no_pic');
            icon = new ImageIcon(url);
        }
        label.setIcon(icon);
        return label;
    }

    /**
     * Get scaled image dimensions. If original image height is greater than <code>MAX_IMAGE_HEIGHT</code> then return
     * new scaled image dimension.
     * @param originalWidth
     * @param originalHeight
     * @return
     */
    public static Image getScaledImage(ImageIcon icon) {
        Image scaled;
        Image image = icon.getImage();
        int originalHeight = icon.getIconHeight();
        // then check if we need to scale even with the new height
        if (originalHeight > MAX_IMAGE_HEIGHT) {
            //scale width to maintain aspect ratio
            int newWidth = (MAX_IMAGE_HEIGHT * icon.getIconWidth()) / originalHeight;
            //scaled = image.getScaledInstance(newWidth, MAX_IMAGE_HEIGHT, Image.SCALE_FAST)
            scaled = image.getScaledInstance(-1, MAX_IMAGE_HEIGHT, Image.SCALE_FAST)
        } else {
            scaled = image.getScaledInstance(-1, originalHeight, Image.SCALE_FAST)
        }
        return scaled;
    }

}

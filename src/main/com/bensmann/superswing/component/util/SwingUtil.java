/*
 * SwingUtil.java
 *
 * Created on 26. Juli 2006, 16:28
 *
 */

package com.bensmann.superswing.component.util;

import javax.swing.JOptionPane;

/**
 *
 * $Header$
 * @author rb
 * @version $Id$
 * @date $Date$
 * @log $Log$
 */
public class SwingUtil {
    
    /**
     * Do not create a new instance of SwingUtil
     */
    private SwingUtil() {
    }
    
    /**
     *
     */
    public static int showCloseConfirmationDialog() {
        
        return JOptionPane.showConfirmDialog(
                null,
                "Sollen Ihre Änderungen gespeichert werden?",
                "Speichern?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
    }
    
}

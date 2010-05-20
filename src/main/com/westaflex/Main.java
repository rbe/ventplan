/*
 * Main.java
 *
 * Created on 26. Juli 2006, 15:09
 *
 */

package com.westaflex;

import com.westaflex.component.WacMainFrame;
import com.westaflex.util.WestaWacApplHelper;

/**
 *
 * $Header$
 * @author rb
 * @version $Id$
 * @date $Date$
 * @log $Log$
 */
public class Main {
    
    /**
     * Do not create a new instance of Main
     */
    private Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        final WacMainFrame wacMainFrame = new WacMainFrame();
        
        WestaWacApplHelper.getInstance().setWacMainFrame(wacMainFrame);
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                wacMainFrame.setVisible(true);
            }
        });
        
    }
    
}

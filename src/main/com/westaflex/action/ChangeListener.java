/*
 * ChangeListener.java
 *
 * Created on 21. September 2007, 00:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.action;

import java.awt.Container;
import javax.swing.JComponent;

/**
 *
 * @author os
 */
public class ChangeListener implements javax.swing.event.ChangeListener{
    
    JComponent comp;
    /** Creates a new instance of ChangeListener */
    public ChangeListener(JComponent comp) {
        this.comp = comp;
    }
    
    public void stateChanged(javax.swing.event.ChangeEvent evt) {
        
        Container c = comp.getTopLevelAncestor();
        if (c != null){
            c.firePropertyChange("Changed", 0, 1);
        }
    }
}

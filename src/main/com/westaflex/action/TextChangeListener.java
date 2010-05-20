/*
 * TextChangeListener.java
 *
 * Created on 21. September 2007, 01:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.action;

import java.awt.Container;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import javax.swing.JComponent;

/**
 *
 * @author os
 */
public class TextChangeListener implements InputMethodListener {
    
    private JComponent comp;
    
    /** Creates a new instance of TextChangeListener */
    public TextChangeListener(JComponent comp) {
        
        this.comp = comp;
    }
    
    public void inputMethodTextChanged(InputMethodEvent event) {
        fireChanged();
    }
    
    public void caretPositionChanged(InputMethodEvent event) {
    }
    private void fireChanged(){
        Container c = comp.getTopLevelAncestor();
        if (c != null){
            c.firePropertyChange("Changed", 0, 1);
        }
    }
    
}

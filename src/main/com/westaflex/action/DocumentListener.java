/*
 * DocumentListener.java
 *
 * Created on 21. September 2007, 01:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.action;

import java.awt.Container;
import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;

/**
 *
 * @author os
 */
public class DocumentListener implements javax.swing.event.DocumentListener {
    JComponent comp = null;
    /** Creates a new instance of DocumentListener */
    public DocumentListener(JComponent comp) {
        this.comp = comp;
    }
    public void insertUpdate(DocumentEvent e)  { fireChanged(); }
    public void removeUpdate(DocumentEvent e)  { fireChanged(); }
    public void changedUpdate(DocumentEvent e) { fireChanged(); }
    
    private void fireChanged(){
        Container c = comp.getTopLevelAncestor();
        if (c != null){
            c.firePropertyChange("Changed", 0, 1);
        }
    }
}

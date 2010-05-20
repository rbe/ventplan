/*
 * FocusAction.java
 *
 * Created on 29. Mai 2007, 19:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.action;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Oliver
 */
public class FocusAction implements FocusListener {
    
    static final Color myYellow =  new Color(255,255,180);
    
    /** Creates a new instance of FocusAction */
    public FocusAction() {
    }
    
    public void focusGained(FocusEvent e) {
        ((Component)e.getSource()).setBackground(myYellow);
        if (e.getSource() instanceof JTextComponent) {
            ((JTextComponent)e.getSource()).selectAll();
        }
    }
    
    public void focusLost(FocusEvent e) {
        ((Component)e.getSource()).setBackground(Color.WHITE);
    }
}

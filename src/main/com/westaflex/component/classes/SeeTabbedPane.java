/*
 * SeeTabbedPane.java
 *
 * Created on 29. Mai 2007, 22:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.component.classes;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import javax.swing.JTabbedPane;

/**
 *
 * @author Oliver
 */
public class SeeTabbedPane extends JTabbedPane {
    
    private static final HashMap<String, Object> myPanels = new HashMap<String, Object>(5);
    
    /** Creates a new instance of SeeTabbedPane */
    public SeeTabbedPane() {
    }
    public Component add(String title, Component comp){
        int i = getTabCount();
        myPanels.put(title, comp);
        super.add((i+1) + " " +  title, comp);
        setMnemonicAt(i, KeyEvent.VK_1+i);
        return comp;
    }

}

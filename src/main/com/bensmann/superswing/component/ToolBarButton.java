/*
 * ToolBarButton.java
 *
 * Created on 6. August 2006, 13:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.bensmann.superswing.component;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 *
 * @author rb
 */
public class ToolBarButton extends JButton {
    
    /** Creates a new instance of ToolBarButton */
    public ToolBarButton() {
        super();
        initToolBarButton();
    }
    
    public ToolBarButton(String text) {
        super(text);
        initToolBarButton();
    }
    
    public ToolBarButton(String text, Icon icon) {
        super(text, icon);
        initToolBarButton();
    }
    
    public void initToolBarButton() {
        setBorderPainted(false);
        setFocusPainted(false);
    }
    
}

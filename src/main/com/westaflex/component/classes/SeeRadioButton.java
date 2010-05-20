/*
 * SeeRadioButton.java
 *
 * Created on 29. August 2006, 23:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.component.classes;

import com.sun.star.comp.beans.OfficeDocument;
import javax.swing.JRadioButton;

/**
 *
 * @author Oliver
 */
public class SeeRadioButton extends JRadioButton implements DocumentAware {

    private RaumItem.PROP prop = null;
    
    /** Creates a new instance of SeeRadioButton */
    public SeeRadioButton() {
        addChangeListener(new com.westaflex.action.ChangeListener(this));
    }
    
    @Override
    public String getString() {
        if (isSelected() == true) {
            return getText().trim();
        }

        return "";
    }
    
    @Override
    public boolean toDoc(OfficeDocument od) {
        
        return new SeeDocBridge().toDoc(od, myPrefix + getName(), getString());
    }

    @Override
    public Object getValue() {
        return getString();
    }

    @Override
    public void setValue(Object value) {
        //nothing to do yet
    }

    @Override
    public void setRaumItemProp( RaumItem.PROP prop ) {
        this.prop = prop;
    }

    @Override
    public RaumItem.PROP getRaumItemProp() {
        return this.prop;
    }
    
}

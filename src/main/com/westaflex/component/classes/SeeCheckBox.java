/*
 * SeeCheckBox.java
 *
 * Created on 29. August 2006, 23:13
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.component.classes;

import com.sun.star.comp.beans.OfficeDocument;
import com.westaflex.component.classes.RaumItem.PROP;
import javax.swing.JCheckBox;

/**
 *
 * @author Oliver
 */
public class SeeCheckBox extends JCheckBox implements DocumentAware {

    private RaumItem.PROP prop = null;
    
    /** Creates a new instance of SeeCheckBox */
    public SeeCheckBox() {
        super();
        getModel().addChangeListener(new com.westaflex.action.ChangeListener(this));
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
    public void setRaumItemProp( PROP prop ) {
        this.prop = prop;
    }

    @Override
    public PROP getRaumItemProp() {
        return this.prop;
    }
}

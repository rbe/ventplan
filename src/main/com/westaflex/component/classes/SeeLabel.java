/*
 * SeeLabel.java
 *
 * Created on 30. August 2006, 18:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.component.classes;

import com.sun.star.comp.beans.OfficeDocument;
import javax.swing.JLabel;

/**
 *
 * @author Oliver
 */
public class SeeLabel extends JLabel
        implements DocumentAware {
    private RaumItem.PROP prop = null;

    /** Creates a new instance of SeeLabel */
    public SeeLabel() {
    }
    
    @Override
    public String getString() {
        return getText().trim();
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
        setText(value.toString());
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

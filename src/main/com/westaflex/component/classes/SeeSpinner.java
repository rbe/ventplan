/*
 * SeeSpinner.java
 *
 * Created on 30. August 2006, 17:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.component.classes;

import com.sun.star.comp.beans.OfficeDocument;
import com.westaflex.swing.SPIN;

/**
 *
 * @author Oliver
 */
public class SeeSpinner extends SPIN
        implements DocumentAware, DataAware {

    private RaumItem.PROP prop = null;
    
    /** Creates a new instance of SeeSpinner */
    public SeeSpinner() {
        addChangeListener(new com.westaflex.action.ChangeListener(this));
    }
        
    public boolean toDoc(OfficeDocument od) {
        
        // Delegieren von toDoc an die Klasse SeeDocBridge
        return new SeeDocBridge().toDoc(od, myPrefix + getName(), getString());
    }
    
    public boolean toDatabase() {
        return true;
    }
    
    public boolean fromDatabase() {
        return true;
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

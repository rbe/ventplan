/*
 * SeeTextField.java
 *
 * Created on 11. August 2006, 18:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.component.classes;

import com.sun.star.comp.beans.OfficeDocument;
import com.westaflex.action.FocusAction;
import com.westaflex.swing.EDTX;

/**
 *
 * @author Oliver
 */
public class SeeTextField extends EDTX
        implements DocumentAware {

    private RaumItem.PROP prop = null;
    
    /** Creates a new instance of SeeTextField */
    public SeeTextField() {
        super();
        addFocusListener(new FocusAction());
        getDocument().addDocumentListener(new com.westaflex.action.DocumentListener(this));
    }
        
    @Override
    public boolean toDoc(OfficeDocument od) {
        
        return new SeeDocBridge().toDoc(od, myPrefix + getName(), getString());
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

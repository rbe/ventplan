/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.westaflex.swing;

import com.westaflex.component.classes.ContentAware;
import com.westaflex.component.classes.RaumItem;
import javax.swing.JTextField;

/**
 *
 * @author seebass
 */
public class EDTX extends JTextField implements ContentAware {

    private RaumItem.PROP prop = null;

    @Override
    public Object getValue() {
        return getText();
    }

    @Override
    public String getString() {
        return getText().trim();
    }

    @Override
    public void setValue(Object value) {
        if (value != null) {
            setText(value.toString());
        }
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

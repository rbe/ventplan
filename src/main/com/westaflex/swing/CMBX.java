/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.westaflex.swing;

import com.westaflex.component.classes.ContentAware;
import com.westaflex.component.classes.RaumItem;
import com.westaflex.component.classes.RaumItem.PROP;
import javax.swing.JComboBox;

/**
 *
 * @author seebass
 */
public class CMBX extends JComboBox implements ContentAware {

    private RaumItem.PROP prop = null;

    @Override
    public Object getValue() {
        return getString();
    }

      @Override
    public String getString() {
        if (getSelectedItem() != null){
            return ((String) getSelectedItem()).trim();
        }

        return "";
    }

    @Override
    public void setValue(Object value) {
        setSelectedItem(value);
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

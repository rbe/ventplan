/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.westaflex.swing;

import com.westaflex.component.classes.ContentAware;
import com.westaflex.component.classes.RaumItem;
import javax.swing.JSpinner;

/**
 *
 * @author seebass
 */
public class SPIN extends JSpinner implements ContentAware {

    private RaumItem.PROP prop = null;

    @Override
    public String getString() {
        return ("" + getValue()).trim();
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

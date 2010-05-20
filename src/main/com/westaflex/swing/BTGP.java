/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.westaflex.swing;

import com.westaflex.component.classes.ContentAware;
import com.westaflex.component.classes.RaumItem;
import com.westaflex.component.classes.RaumItem.PROP;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

/**
 *
 * @author seebass
 */
public class BTGP extends ButtonGroup implements ContentAware {

    private String tooltiptext = "";
    private RaumItem.PROP prop = null;

    public String getToolTipText() {
        return tooltiptext;
    }

    public void setToolTipText(String tooltiptext) {
        this.tooltiptext = tooltiptext;
    }

    @Override
    public Object getValue() {
        return getString();
    }

    @Override
    public String getString() {

        String ret = "";

        for (AbstractButton elem : buttons) {

            if (elem.isSelected()) {
                ret = elem.getText();
                break;
            }
        }
        return ret;
    }

    @Override
    public void setValue(Object value) {
        for (AbstractButton ab : buttons) {
            if (ab.getText().equals(value)) {
                ab.setSelected(true);
                break;
            }
        }
    }

    @Override
    public void setRaumItemProp( PROP prop ) {
        this.prop = prop;
    }

    @Override
    public PROP getRaumItemProp() {
        return prop;
    }

}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.westaflex.component.classes;

/**
 *
 * @author seebass
 */
public interface ContentAware {

    public Object getValue();
    public String getString();
    public void setValue(Object value);
    public void setRaumItemProp( RaumItem.PROP prop );
    public RaumItem.PROP getRaumItemProp();
}

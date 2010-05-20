/*
 * SeeButtonGroup.java
 *
 * Created on 29. August 2006, 21:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.component.classes;

import com.sun.star.comp.beans.OfficeDocument;
import com.westaflex.swing.BTGP;
import java.util.Enumeration;
import javax.swing.AbstractButton;

/**
 *
 * @author Oliver
 */
public class SeeButtonGroup extends    BTGP
                            implements DocumentAware {

    private String name = null;
    private String[] buttonValues = null;
    /** Creates a new instance of SeeButtonGroup */

    public SeeButtonGroup() {
        super();
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }
    
    public void setButtonValues(String[] buttonValues){
        this.buttonValues = buttonValues;
    }
    
    @Override
    public String getString() {
       String ret = "";
       int i = 0;
       
       Enumeration<AbstractButton> buttons = getElements();
       AbstractButton elem = null;
       while (buttons.hasMoreElements()){
           elem = buttons.nextElement();
           if (elem.isSelected()){
            ret = buttonValues[i];
            break;
           }
           i++;
       }
        return ret;
    }
    
    @Override
    public boolean toDoc(OfficeDocument od) {
        return true;
    }

    @Override
    public Object getValue() {
        return getString();
    }
}

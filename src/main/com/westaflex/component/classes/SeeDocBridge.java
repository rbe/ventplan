/*
 * SeeDocBridge.java
 *
 * Created on 30. August 2006, 13:52
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.component.classes;

import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.comp.beans.OfficeDocument;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameAccess;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.text.XTextFieldsSupplier;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.XPrintable;

/**
 *
 * @author Oliver
 */
public class SeeDocBridge {
    
    /** Creates a new instance of SeeDocBridge */
    public SeeDocBridge() {
    }
    
    public boolean toDoc(OfficeDocument od, String myRef, String s) {
        //System.out.format("Mein Name ist %s%n", myRef);
        XTextFieldsSupplier xTFS =
                (XTextFieldsSupplier) UnoRuntime.queryInterface(
                XTextFieldsSupplier.class, od);
        XNameAccess tfm = xTFS.getTextFieldMasters();
        if (tfm.hasByName(myRef)) {
            XPropertySet xPropSet = null;
            try {
                xPropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, tfm.getByName(myRef));
                try {
                    xPropSet.setPropertyValue("Content", s);
                } catch (PropertyVetoException ex) {
                    ex.printStackTrace();
                } catch (UnknownPropertyException ex) {
                    ex.printStackTrace();
                } catch (com.sun.star.lang.IllegalArgumentException ex) {
                    ex.printStackTrace();
                } catch (WrappedTargetException ex) {
                    ex.printStackTrace();
                }
            } catch (WrappedTargetException ex) {
                ex.printStackTrace();
            } catch (NoSuchElementException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }
    
    public void print(OfficeDocument od) {
        
        XPrintable xPrintable = (XPrintable) UnoRuntime.queryInterface(XPrintable.class, od);
        try {
            xPrintable.print(null);
        } catch (com.sun.star.lang.IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        
    }
}
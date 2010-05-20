/*
 * DocumentAware.java
 *
 * Created on 11. August 2006, 18:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.component.classes;

import com.sun.star.comp.beans.OfficeDocument;

/**
 *
 * @author Oliver
 */
public interface DocumentAware extends ContentAware {
   public final String myPrefix = "com.sun.star.text.FieldMaster.User.";

    public boolean toDoc(OfficeDocument od);
}

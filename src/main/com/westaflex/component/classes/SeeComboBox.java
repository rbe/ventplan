/*
 * SeeComboBox.java
 *
 * Created on 10. August 2006, 23:40
 *
 */

package com.westaflex.component.classes;

import com.sun.star.comp.beans.OfficeDocument;
import com.westaflex.action.FocusAction;
import com.westaflex.database.WestaDB;
import com.westaflex.swing.CMBX;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComboBox.KeySelectionManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 *
 * @author Oliver
 */
public class SeeComboBox extends CMBX
        implements DocumentAware, DataAware, KeySelectionManager {
    
    private String searchFor;
    private long lap;
    
    public String mySearch = null;
    public String myVolatile = null;
    /**
     * Creates a new instance of SeeComboBox
     */
    public SeeComboBox() {
        super();
        lap = new java.util.Date().getTime();
        setKeySelectionManager(this);
        JTextComponent cbEditor = (JTextComponent) getEditor().getEditorComponent();
        cbEditor.setDocument(new AutoCompletion(this));
        cbEditor.getDocument().addDocumentListener( new com.westaflex.action.DocumentListener(this));
        addFocusListener(new FocusAction());
    }

    public SeeComboBox(DefaultComboBoxModel cbm) {
        
        this();
        setModel(cbm);
    }
        
    @Override
    public boolean toDoc(OfficeDocument od) {
        return new SeeDocBridge().toDoc(od, myPrefix + getName(), getString());
    }
    
    @Override
    public boolean toDatabase() {
        return true;
    }
    
    @Override
    public boolean fromDatabase() {
        setModel(makeModelFromMySearch());
        return (getModel() == null);
    }
    public DefaultComboBoxModel makeModelFromMySearch(boolean bEmpty) {
        DefaultComboBoxModel dcm = makeModelFromMySearch();
        
        if(dcm != null){
            dcm.insertElementAt("",0);
        }
        return dcm;
        
    }
    
    public DefaultComboBoxModel makeModelFromMySearch() {
        String[] res = null;
        String s = null;
        
        if (myVolatile != null) {
            s = mySearch.replace("~myVolatile~", myVolatile);
        } else {
            s = mySearch;
        }
        return new DefaultComboBoxModel(WestaDB.getInstance().queryDBResultList(s));
    }
    public DefaultComboBoxModel makeModelFromComboBox(){
        
        DefaultComboBoxModel cbm = new DefaultComboBoxModel();
        
        for (int i = 0;i<getItemCount();i++){
            cbm.addElement(getItemAt(i));
        }
        return cbm;
    }
    
    public int selectionForKey(char aKey, ComboBoxModel aModel) {
        long now = new java.util.Date().getTime();
        
        if(lap + 500 < now) {
            searchFor = "" + aKey;
        }
        lap = now;
        String current;
        for(int i = 0; i < aModel.getSize(); i++) {
            current = aModel.getElementAt(i).toString().toLowerCase();
            if(current.startsWith(searchFor)){
                return i;
            }
        }
        return -1;
    }
    public void setAutoCompletion(boolean yesno){
        JTextComponent cbEditor = (JTextComponent) getEditor().getEditorComponent();
        if (yesno == true){
            cbEditor.setDocument(new AutoCompletion(this));
        } else {
            cbEditor.setDocument(new PlainDocument());
        }
    }

    private class AutoCompletion extends PlainDocument {
        
        //  Special thanks to Thomas Bierhance from orbital-computer.de
        // for this code
        JComboBox comboBox;
        ComboBoxModel model;
        // flag to indicate if setSelectedItem has been called
        // subsequent calls to remove/insertString should be ignored
        boolean selecting=false;
        
        public AutoCompletion(final JComboBox comboBox) {
            this.comboBox = comboBox;
            model = comboBox.getModel();
        }
        
        @Override
        public void remove(int offs, int len) throws BadLocationException {
            // return immediately when selecting an item
            if (selecting){
                return;
            }
            
            super.remove(offs, len);
        }
        
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            // return immediately when selecting an item
            if (selecting){
                return;
            }
            // insert the string into the document
            super.insertString(offs, str, a);
            // lookup and select a matching item
            Object item = lookupItem(getText(0, getLength()));
            if (item != null){
                setSelectedItem(item);
                // remove all text and insert the completed string
                super.remove(0, getLength());
                super.insertString(0, item.toString(), a);
                // select the completed part
                JTextComponent editor = (JTextComponent) comboBox.getEditor().getEditorComponent();
                editor.setSelectionStart(offs+str.length());
                editor.setSelectionEnd(getLength());
            }
        }
        
        private void setSelectedItem(Object item) {
            selecting = true;
            model.setSelectedItem(item);
            selecting = false;
        }
        
        private Object lookupItem(String pattern) {
            // iterate over all items
            for (int i=0, n=model.getSize(); i < n; i++) {
                Object currentItem = model.getElementAt(i);
                // current item starts with the pattern?
                if (currentItem.toString().toUpperCase().startsWith(pattern.toUpperCase())) {
                    return currentItem;
                }
            }
            // no item starts with the pattern => return null
            return null;
        }
        
    }
}
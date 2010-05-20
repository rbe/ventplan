/*
 * ActionYellow.java
 *
 * Created on 29. Mai 2007, 18:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

/**
 *
 * @author Oliver
 */
public class GUIAction {
    private GUIAction(){
    }
    private static class LazyHolder {
        private static GUIAction instance = new GUIAction();
    }
    public static GUIAction getInstance() {
        return LazyHolder.instance;
    }
    
    /** Creates a new instance of ActionYellow */
    public Action makeAction(String name) {
        String qName = null;
        Action action = null;
        try {
            qName = "com.westaflex.GUIAction$" + name;
            Class klasse = Class.forName(qName);
            action = (Action) klasse.newInstance();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return action;
    }
    
    public class AuslegungNeuAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, e.getActionCommand());
        }
    }
}
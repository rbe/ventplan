/*
 * SeePanel.java
 *
 * Created on 26. März 2007, 14:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.component.classes;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Oliver
 */
public class SeePanel extends JPanel {
    
    /** Creates a new instance of SeePanel */
    public SeePanel() {
        super();
        init();
    }
    
    public void init(){
        Component tcomp = null;
        
        for (Component comp:getComponents()){
            if(comp instanceof JLabel){
                tcomp = comp;
            } else if(comp.getName().substring(0, comp.getName().lastIndexOf("Label",0)).equals(tcomp.getName())){
                ((JLabel)tcomp).setLabelFor(comp);
            }
        }
    }
}

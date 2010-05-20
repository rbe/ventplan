/*
 * JMenuUtil.java
 *
 * Created on 5. August 2006, 18:09
 *
 */

package com.bensmann.superswing.component.util;

import com.bensmann.superswing.ApplHelper;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author rb
 */
public class JMenuUtil {
    
    private static ApplHelper applHelper;
    
    // Static initializer
    static {
        applHelper = ApplHelper.getInstance();
    }
    
    /**
     * Do not create a new instance of JMenuUtil
     */
    private JMenuUtil() {
    }
    
}

/*
 * LookAndFeelUtil.java
 *
 * Created on 5. August 2006, 12:53
 *
 */

package com.bensmann.superswing.component.util;

import com.bensmann.superswing.ApplHelper;
import com.bensmann.superswing.ApplHelperConstants;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author rb
 */
public class LookAndFeelUtil {
    
    /**
     * Do not create a new instance of LookAndFeelUtil
     */
    private LookAndFeelUtil() {
    }
    
    /**
     *
     * @param component
     */
    public static void setJavaLookAndFeel(Component component) {
        setLookAndFeel(component, "javax.swing.plaf.metal.MetalLookAndFeel");
    }
    
    /**
     *
     * @param compnent
     */
    public static void setMotifLookAndFeel(Component component) {
        setLookAndFeel(component, "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    }
    
    /**
     *
     * @param component
     */
    public static void setWindowsLookAndFeel(Component component) {
        setLookAndFeel(component, "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    }
    
    /**
     *
     * @param component
     * @param lookAndFeelClass
     */
    public static void setLookAndFeel(Component component, String lookAndFeelClass) {
        
        try {
            // Update component with look and feel
            UIManager.setLookAndFeel(lookAndFeelClass);
            SwingUtilities.updateComponentTreeUI(component);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        
        // Save look and feel in ApplHelper
        ApplHelper.getInstance().setPreference(
                ApplHelperConstants.LOOK_AND_FEEL_CLASS,
                lookAndFeelClass);
        
    }
    
    /**
     *
     * @return
     */
    public static JMenu generateMinimalLookAndFeelMenu(final Component component) {
        
        JMenu menu = new JMenu("Look & Feel");
        JMenuItem javaLnFMenuItem = new JMenuItem("Java");
        JMenuItem motifLnFMenuItem = new JMenuItem("Motif");
        JMenuItem windowsLnFMenuItem = new JMenuItem("Windows");
        
        javaLnFMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LookAndFeelUtil.setJavaLookAndFeel(component);
            }
        });
        
        motifLnFMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LookAndFeelUtil.setMotifLookAndFeel(component);
            }
        });
        
        windowsLnFMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                LookAndFeelUtil.setWindowsLookAndFeel(component);
            }
        });
        
        menu.add(javaLnFMenuItem);
        menu.add(motifLnFMenuItem);
        menu.add(windowsLnFMenuItem);
        
        return menu;
        
    }
    
//    /**
//     * Generate a menu for changing look and feel
//     *
//     * @param component Component which look and feel shall be updated
//     * @return
//     */
//    public static JMenu generateLookAndFeelMenu(Component component) {
//
//        String lookAndFeelList = null;
//        String lookAndFeelName = null;
//        String lookAndFeelClass = null;
//        String temp = null;
//        StringTokenizer st = null;
//        JMenu changeLookAndFeel = new JMenu();
//        changeLookAndFeel.setText("Look & Feel");
//
//        // Look up property lookandfeel for names of further properties
//        lookAndFeelList = getProperty("lookandfeel");
//        st = new StringTokenizer(lookAndFeelList, ",");
//        while(st.hasMoreTokens()) {
//
//            temp = st.nextToken();
//            lookAndFeelName = getProperty("lookandfeel." + temp + ".name");
//            lookAndFeelClass = getProperty("lookandfeel." + temp + ".class");
//
//            if (lookAndFeelName != null && lookAndFeelClass != null) {
//
//                // Generate menu item for look and feel
//                changeLookAndFeel.add(
//                        getLookAndFeelMenuItem(lookAndFeelName,
//                        lookAndFeelClass, component));
//
//            }
//
//        }
//
//        return changeLookAndFeel;
//
//    }
//
//    /**
//     * Create menu item for changing look and feel of a certain Swing component
//     * (usually a JFrame).
//     *
//     * @param name
//     * @param lookAndFeelClassName
//     * @param component Component which look and feel shall be updated
//     * @return
//     */
//    public static JMenuItem getLookAndFeelMenuItem(String name,
//            final String lookAndFeelClassName, final Component component) {
//
//        Class clazz = null;
//        JMenuItem menuItem = new JMenuItem();
//
//        menuItem.setText(name);
//
//        // Try to load the requested look and feel class. If it cannot
//        // be loaded or a wrong Java version is used, print out an error
//        // message
//        try {
//
//            clazz = Class.forName(lookAndFeelClassName);
//
//            menuItem.addActionListener(new ActionListener() {
//
//                public void actionPerformed(ActionEvent e) {
//                    LookAndFeelUtil.setLookAndFeel(component, lookAndFeelClassName);
//                }
//
//            });
//
//        } catch (ClassNotFoundException e) {
//
//            // Could not find class
//            System.out.println("Could not create menu item for " +
//                    "look and feel class '" +
//                    lookAndFeelClassName +
//                    "': " +
//                    e);
//
//        } catch (UnsupportedClassVersionError e) {
//
//            // Look and feel not for this version of Java
//            System.out.println("Could not create menu item for " +
//                    "look and feel class '" +
//                    lookAndFeelClassName +
//                    "': Wrong Java version: " +
//                    e);
//
//        } catch (NullPointerException e) {
//            // Null was given as class name
//        }
//
//        return menuItem;
//
//    }
    
}

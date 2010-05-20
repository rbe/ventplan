/*
 * JDesktopUtil.java
 *
 * Created on 5. August 2006, 12:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.bensmann.superswing.component.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 *
 * @author rb
 */
public class JDesktopUtil {
    
    private static JDesktopUtil instance;
    
    private JDesktopPane desktopPane;
    
    private int frameDistance;
    
    private int nextFrameX;
    
    private int nextFrameY;
    
    /** Creates a new instance of JDesktopUtil */
    private JDesktopUtil(JDesktopPane desktopPane) {
        this.desktopPane = desktopPane;
        frameDistance = 20;
    }
    
    public static JDesktopUtil getInstance(JDesktopPane desktopPane) {
        
        if (instance == null) {
            instance = new JDesktopUtil(desktopPane);
        }
        
        return instance;
        
    }
    
    public void addInternalFrame(JInternalFrame iframe) {
        
//        iframe.getContentPane().add(c);
        desktopPane.add(iframe);
//        // add listener to confirm frame closing
//        iframe.addVetoableChangeListener(this);
        
        // position frame
        int width = desktopPane.getWidth() / 2;
        int height = desktopPane.getHeight() / 2;
        iframe.reshape(nextFrameX, nextFrameY, width, height);
        
        iframe.show();
        
        // select the frame--might be vetoed
        try {
            iframe.setSelected(true);
        } catch (PropertyVetoException e) {
        }
        
        // if this is the first time, compute distance between cascaded frames
        if (frameDistance == 0) {
            
            frameDistance =
                    iframe.getHeight() -
                    iframe.getContentPane().getHeight();
            
        }
        
        // compute placement for next frame
        
        nextFrameX += frameDistance;
        nextFrameY += frameDistance;
        if (nextFrameX + width > desktopPane.getWidth()) {
            nextFrameX = 0;
        }
        if (nextFrameY + height > desktopPane.getHeight()) {
            nextFrameY = 0;
        }
        
    }
    
    public void cascadeWindows(JDesktopPane desktopPane) {
        
        JInternalFrame[] frames = desktopPane.getAllFrames();
        int x = 0;
        int y = 0;
        int width = desktopPane.getWidth() / 2;
        int height = desktopPane.getHeight() / 2;
        
        for (int i = 0; i < frames.length; i++) {
            
            if (!frames[i].isIcon()) {
                
                try {
                    
                    // try to make maximized frames resizable this might be
                    // vetoed
                    
                    frames[i].setMaximum(false);
                    frames[i].reshape(x, y, width, height);
                    
                    x += frameDistance;
                    y += frameDistance;
                    // wrap around at the desktop edge
                    if (x + width > desktopPane.getWidth()) {
                        x = 0;
                    }
                    if (y + height > desktopPane.getHeight()) {
                        y = 0;
                    }
                    
                } catch (PropertyVetoException e) {
                }
                
            }
            
        }
        
    }
    
    public void tileWindows(JDesktopPane desktopPane) {
        
        JInternalFrame[] frames = desktopPane.getAllFrames();
        
        // count frames that aren't iconized
        int frameCount = 0;
        for (int i = 0; i < frames.length; i++) {
            
            if (!frames[i].isIcon()) {
                frameCount++;
            }
            
        }
        
        int rows = (int) Math.sqrt(frameCount);
        int cols = frameCount / rows;
        int extra = frameCount % rows;
        // number of columns with an extra row
        
        int width = desktopPane.getWidth() / cols;
        int height = desktopPane.getHeight() / rows;
        int r = 0;
        int c = 0;
        
        for (int i = 0; i < frames.length; i++) {
            
            if (!frames[i].isIcon()) {
                
                try {
                    
                    frames[i].setMaximum(false);
                    frames[i].reshape(c * width, r * height, width, height);
                    
                    r++;
                    
                    if (r == rows) {
                        
                        r = 0;
                        c++;
                        
                        if (c == cols - extra) { // start adding an extra row
                            rows++;
                            height = desktopPane.getHeight() / rows;
                        }
                        
                    }
                    
                } catch (PropertyVetoException e) {
                }
                
            }
            
        }
        
    }
    
    /**
     * 
     * 
     * @param menuBar 
     * @param windowMenu 
     * @param position 
     * @return 
     */
    public JMenu generateInternalFrameMenu(JMenuBar menuBar, JMenu windowMenu,
            int position) {
        
        JMenu menu = null;
        JMenuItem menuItem = null;
        
        menu = new JMenu();
        menu.setText("Fenster");
        
        for (final JInternalFrame frame : desktopPane.getAllFrames()) {
            
            menuItem = new JMenuItem();
            menuItem.setText(frame.getTitle());
            menuItem.addActionListener(new ActionListener() {
                
                public void actionPerformed(ActionEvent actionEvent) {
                    desktopPane.getDesktopManager().activateFrame(frame);
                }
                
            });
            
            menu.add(menuItem);
            
        }
        
        menuBar.remove(windowMenu);
        if (position > -1) {
            menuBar.add(menu, position);
        } else {
            menuBar.add(menu);
        }
        
        return menu;
        
    }
    
    /**
     * 
     * 
     * @param menuBar 
     * @param windowMenu 
     * @return 
     */
    public JMenu generateInternalFrameMenu(JMenuBar menuBar, JMenu windowMenu) {
        return generateInternalFrameMenu(menuBar, windowMenu, menuBar.getComponentCount() - 2);
    }
    
    /**
     *
     * @param frame
     * @param menu
     */
    public void removeInternalFrameFromMenu(JInternalFrame frame, JMenu menu) {
        
        for (int i = 0; i < menu.getItemCount(); i++) {
            
            if (menu.getItem(i).getText().equals(frame.getTitle())) {
                menu.remove(i);
            }
            
        }
        
    }
    
}

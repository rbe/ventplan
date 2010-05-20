/*
 * SplashScreen.java
 *
 * Created on 21. Juli 2006, 13:44
 *
 */

package com.bensmann.superswing.component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.border.BevelBorder;

/**
 *
 * @author rb
 */
public final class SplashScreen implements Runnable {
    
    /**
     *
     */
    private JWindow window;
    
    /**
     *
     */
    private JPanel panel;
    
    /**
     *
     */
    private boolean stop;
    
    /**
     * Show splash screen for x ms
     */
    private int delay;
    
    /**
     * Image used in splash screen
     */
    protected ImageIcon image;
    
    /**
     * The subtitle is displayed below the image
     */
    private String subtitle;
    
    /**
     *
     * @param subtitle
     */
    public SplashScreen(String subtitle) {
        this.subtitle = subtitle;
        delay = 500;
        image = new ImageIcon(this.getClass().getResource(
                "/com/bensmann/superswing/resource/image/javaduke.gif"));
    }
    
    /**
     * 
     * @param subtitle 
     * @param image 
     */
    public SplashScreen(String subtitle, ImageIcon image) {
        this(subtitle);
        this.image = image;
    }
    
    /**
     *
     * @param subtitle
     * @param delay
     */
    public SplashScreen(String subtitle, int delay) {
        this(subtitle);
        this.delay = delay;
    }
    
    /**
     *
     */
    public void initializeUI() {
        
        window = new JWindow();
        
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createBevelBorder(BevelBorder.RAISED), BorderFactory
                .createEtchedBorder()));
        panel.add(new JLabel(image, JLabel.CENTER), BorderLayout.CENTER);
        panel.add(new JLabel(subtitle, JLabel.CENTER), BorderLayout.SOUTH);
        
        window.getContentPane().add(panel);
        
    }
    
    /**
     *
     */
    private void showSplashScreen() {
        
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        
        window.setLocation(dimension.width / 3, dimension.height / 3);
        window.setSize((int) (image.getIconWidth() * 1.2),
                (int) (image.getIconHeight() * 1.2));
        
        window.setVisible(true);
        
        while (!stop) {
            
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
            }
            
        }
        
        window.dispose();
        
    }
    
    /**
     *
     */
    public void run() {
        
        try {
            initializeUI();
            showSplashScreen();
        } catch (Throwable t) {
        }
        
    }
    
    public void stop() {
        stop = true;
    }
    
    public static void main(String[] args) throws Exception {
        SplashScreen s = new SplashScreen("Test");
        new Thread(s).start();
        Thread.sleep(3000);
        s.stop();
    }
    
}

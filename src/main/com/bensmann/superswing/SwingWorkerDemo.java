/*
 * SwingWorkerDemo.java
 *
 * Created on 26. März 2007, 13:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.bensmann.superswing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingWorker;

/**
 *
 * @author rb
 */
public class SwingWorkerDemo extends JFrame {
    
    private JButton button = new JButton("Change my mind!");
    
    class ClockPrecision extends SwingWorker<Long, Object> {
        
        @Override
        public Long doInBackground() {
            
            long startNano = System.nanoTime();
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            
            return (System.nanoTime() - startNano) / (1000 * 1000);
            
        }
        
        @Override
        protected void done() {
            
            try {
                button.setText("" + get());
            }
            /* InterruptedException, ExecutionException */
            catch (Exception e) {
            }
            
        }
        
    }
    
    public SwingWorkerDemo() {
        
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        add(button);
        
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ClockPrecision().execute();
            }
        };
        
        button.addActionListener( al );
        
        pack();
        
    }
    
    public static void main(String[] args) {
        new SwingWorkerDemo().setVisible(true);
    }
    
}

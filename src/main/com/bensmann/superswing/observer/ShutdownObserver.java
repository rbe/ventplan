/*
 * ShutdownObserver.java
 *
 * Created on 11. August 2006, 20:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.bensmann.superswing.observer;

/**
 *
 * @author rb
 */
public interface ShutdownObserver {
    
    void processShutdown(ShutdownObservable shutdownObservable);
    
}

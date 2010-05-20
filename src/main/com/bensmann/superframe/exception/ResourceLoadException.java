/*
 * ResourceLoadException.java
 *
 * Created on 19. Dezember 2005, 11:45
 *
 */

package com.bensmann.superframe.exception;

/**
 *
 * $Header$
 * @author rb
 * @version $Id$
 * @date $Date$
 * @log $Log$
 */
public class ResourceLoadException extends Exception {
    
    /**
     * Creates a new instance of ResourceLoadException
     */
    public ResourceLoadException() {
        super();
    }
    
    public ResourceLoadException(String message) {
        super(message);
    }
    
    public ResourceLoadException(String message, Throwable throwable) {
        super(message, throwable);
    }
    
}

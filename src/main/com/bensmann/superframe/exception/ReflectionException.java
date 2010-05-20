/*
 * com/bensmann/superframe/exception/ReflectionException.java
 *
 * ReflectionException.java created on 20. Januar 2007, 15:24 by rb
 *
 * Copyright (C) 2006 Ralf Bensmann, java@bensmann.com
 *
 */

package com.bensmann.superframe.exception;

/**
 *
 * @author rb
 * @version 1.0
 */
public class ReflectionException extends Exception {
    
    /**
     * Creates a new instance of ReflectionException
     *
     * @param message 
     */
    public ReflectionException(String message) {
        super(message);
    }
    
    /**
     * Creates a new instance of ReflectionException
     *
     * @param message
     * @param throwable
     */
    public ReflectionException(String message, Throwable throwable) {
        super(message, throwable);
    }
    
}

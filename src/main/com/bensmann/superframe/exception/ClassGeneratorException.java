/*
 * com/bensmann/superframe/exception/ClassGeneratorException.java
 *
 * ClassGeneratorException.java created on 19. Januar 2007, 10:20 by rb
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
public class ClassGeneratorException extends Exception {
    
    /**
     * Creates a new instance of ClassGeneratorException
     *
     * @param message
     */
    public ClassGeneratorException(String message) {
        super(message);
    }
    
    /**
     * Creates a new instance of ClassGeneratorException
     *
     * @param message
     * @param throwable
     */
    public ClassGeneratorException(String message, Throwable throwable) {
        super(message, throwable);
    }
    
}

/*
 * Created on 28.07.2003
 *
 */
package com.bensmann.superframe.exception;

/**
 * @author rb
 * @version $Id: SuperFrameException.java,v 1.1 2005/07/19 15:51:38 rb Exp $
 */
public class SuperFrameException extends Exception {
    
    public SuperFrameException() {
    }
    
    public SuperFrameException(String message) {
        super(message);
    }
    
    public SuperFrameException(Exception e) {
        super(e.getMessage());
        this.setStackTrace(e.getStackTrace());
    }
    
}
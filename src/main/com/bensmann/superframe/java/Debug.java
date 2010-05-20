/*
 * Created on 05.04.2005
 *
 */

package com.bensmann.superframe.java;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * @author rb
 * @version $Id: Debug.java,v 1.1 2005/07/19 15:51:38 rb Exp $
 */
public final class Debug {
    
    public static boolean DEBUG_WITH_TIMESTAMP = true;
    
    public static boolean DEBUG = true;
    
    /**
     * Print string for debugging purposes
     *
     * @param clazz
     * @param method
     * @param text
     */
    public final static void log(String text) {
        
        StringBuffer sb = new StringBuffer();
        
        // Hack(?) to get the stack trace.
        Throwable dummyException = new Throwable();
        StackTraceElement locations[] = dummyException.getStackTrace();
        // Caller will be the second element
        String className = "unknown";
        String methodName = "unknown";
        String fileName = "unknown";
        int lineNumber = -1;
        if (locations != null && locations.length > 1) {
            StackTraceElement caller = locations[1];
            className = caller.getClassName();
            methodName = caller.getMethodName();
            fileName = caller.getFileName();
            lineNumber = caller.getLineNumber();
        }
        
        if (DEBUG_WITH_TIMESTAMP) {
            
            sb.append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSSSSS").
                    format(new Date())).append(" ");
            
        }
        
        if (DEBUG) {
            
            // Add log message
            sb.append(
                    "#" +
                    Thread.currentThread().getId() +
                    " " +
                    className +
                    "." +
                    methodName +
                    "(" +
                    fileName +
                    ":" +
                    lineNumber+ "): " +
                    text);
            
            System.out.println(sb.toString());
            
        }
        
    }
    
}

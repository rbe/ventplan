/*
 * com/bensmann/superframe/java/lang/JavaNameScanner.java
 *
 * JavaNameScanner.java created on 18. Januar 2007, 15:42 by rb
 *
 * Copyright (C) 2006 Ralf Bensmann, java@bensmann.com
 *
 */

package com.bensmann.superframe.java.lang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Scan for "package " and "public class " statements in a file to identify
 * package and class name from a Java source code file
 *
 * @author rb
 * @version 1.0
 */
public class JavaNameScanner implements Runnable {
    
    /**
     *
     */
    private File javaFile;
    
    /**
     *
     */
    private String packageName;
    
    /**
     *
     */
    private String className;
    
    /**
     * Creates a new instance of JavaNameScanner
     * 
     * @param javaFile 
     */
    public JavaNameScanner(File javaFile) {
        this.javaFile = javaFile;
    }
    
    /**
     * 
     * @return 
     */
    public String getPackageName() {
        return packageName;
    }
    
    /**
     * 
     * @return 
     */
    public String getClassName() {
        return className;
    }
    
    /**
     *
     */
    public void run() {
        
        BufferedReader br = null;
        String s = null;
        String t = null;
        
        boolean foundPackage = false;
        boolean foundClass = false;
        
        synchronized (this) {
            
            try {
                
                br = new BufferedReader(new FileReader(javaFile));
                while((s = br.readLine()) != null) {
                    
                    if (s.startsWith("package ")) {
                        
                        packageName = s.substring(
                                s.indexOf(" ") + 1, s.length() - 1).trim();
                        
                        foundPackage = true;
                        
                    }
                    
                    if (s.startsWith("public")) {
                        
                        t = s.substring(s.indexOf("class ") + "class ".length());
                        className = t.substring(0, t.indexOf(" ")).trim();
                        
                        foundClass = true;
                        
                    }
                    
                    if (foundPackage && foundClass) {
                        break;
                    }
                    
                }
                
            } catch(IOException e) {
                // ignore
            }
            
            this.notify();
            
        }
        
    }
    
}

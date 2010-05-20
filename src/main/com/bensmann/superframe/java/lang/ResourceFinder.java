/*
 * ResourceFinder.java
 *
 * Created on 20. Mai 2005, 16:30
 *
 */

package com.bensmann.superframe.java.lang;

import java.io.InputStream;
import java.net.URL;

/**
 * Try to find a class in different class loaders
 *
 * @author Ralf Bensmann
 * @version $Id: ResourceFinder.java,v 1.1 2005/07/19 15:51:39 rb Exp $
 */
public class ResourceFinder {
    
    /**
     *
     */
    private static ResourceFinder singleton;
    
    /**
     * Create instance of MultiClassLoader
     */
    private ResourceFinder() {
    }
    
    /**
     * 
     * @return 
     */
    public static synchronized ResourceFinder getInstance() {
        
        if (singleton == null) {
            singleton = new ResourceFinder();
        }
        
        return singleton;
        
    }
    
    /**
     * Try to load a class using different class loaders
     *
     * @param name
     * @return
     */
    public Class findClass(String name) {
        
        Class clazz = null;
        
        try {
            
            clazz = Thread.currentThread().getContextClassLoader().
                    loadClass(name);
            
        } catch (ClassNotFoundException e) {
        }
        
        if (clazz == null) {
            
            try {
                clazz = getClass().getClassLoader().loadClass(name);
            } catch (ClassNotFoundException e) {
            }
            
        }
        
        return clazz;
        
    }
    
    /**
     * Try to find a resource as stream using different classes as a starting
     * point
     *
     * @param name 
     * @return 
     */
    public URL findResource(String name) {
        
        URL resource = null;
        
        resource = Thread.currentThread().getContextClassLoader().
                getResource(name);
        
        if (resource == null) {
            resource = getClass().getResource(name);
        }
        
        return resource;
        
    }
    
    /**
     * Try to find a resource as stream using different classes as a starting
     * point
     *
     * @param name 
     * @return 
     */
    public InputStream findResourceAsStream(String name) {
        
        InputStream resource = null;
        
        resource = Thread.currentThread().getContextClassLoader().
                getResourceAsStream(name);
        
        if (resource == null) {
            resource = getClass().getResourceAsStream(name);
        }
        
        return resource;
        
    }
    
}

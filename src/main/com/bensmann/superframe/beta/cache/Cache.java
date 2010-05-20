/*
 * Cache.java
 *
 * Created on 25.09.2007, 16:13:04
 *
 */

package com.bensmann.superframe.beta.cache;

/**
 *
 * @param T
 * @author Ralf_Bensmann
 */
public interface Cache<T> {

    /**
     * Put object into cache
     * @param ident
     * @param o
     * @return 
     */
    T put(String ident, T o);

    /**
     * Retrieve object from cache
     * @param ident
     * @return
     */
    T get(String ident);
    
    /**
     * Remove object from cache
     * @param ident 
     * @return 
     */
    T remove(String ident);
}
/*
 * MRUCache.java
 *
 * Created on 26.09.2007, 10:52:30
 *
 */

package com.bensmann.superframe.beta.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 *
 * @param T
 * @author Ralf_Bensmann
 */
public class MRUCache<T> implements Cache<T>, Runnable {

    /**
     *
     */
    private static Logger logger;

    /**
     * Map for cache
     */
    private Map<String, T> map;

    /**
     * Cache constant number of objects
     */
    private int size;

    /**
     * Indicates that map is filled up
     */
    private boolean mapFull;
    static {
        logger =Logger.getLogger(MRUCache.class.getName());
    }

    private class CheckSizeThread implements Runnable {

        private int timeout;

        private CheckSizeThread() {
            this(5);
        }

        private CheckSizeThread(int timeout) {
            this.timeout = timeout;
        }

        public void run() {
            synchronized (map) {
                // Check number of objects
                if (map.size() == size) {
                    logger.warning("Maximum number of objects reached!");
                    mapFull = true;
                } else if (mapFull && map.size() < size) {
                    mapFull = false;
                }
            }
            // Wait
            try {
                Thread.sleep(timeout * 1000);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     *
     */
    public MRUCache() {
        // Maximum number of objects in cache is 100
        size = 100;
        //
        initialize();
    }

    /**
     *
     * @param size
     */
    public MRUCache(int size) {
        // Maximum number of objects in cache
        this.size = size;
        //
        initialize();
    }

    private void initialize() {
        map =   new ConcurrentHashMap<String, T>(size);
        // Start check size thread
        new Thread(new CheckSizeThread()).start();
    }

    /**
     *
     * @param ident
     * @param o
     * @return
     */
    public T put(String ident, T o) {
        // Put object in cache
        if (!mapFull) {
            map.put(ident, o);
        }
        return o;
    }

    public T get(String ident) {
        return map.get(ident);
    }

    public T remove(String ident) {
        return map.remove(ident);
    }

    /**
     *
     */
    public void run() {
    }
}
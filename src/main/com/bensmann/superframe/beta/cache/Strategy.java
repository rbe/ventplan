/*
 * Strategy.java
 *
 * Created on 25.09.2007, 16:15:21
 *
 */

package com.bensmann.superframe.beta.cache;

/**
 *
 * @author Ralf_Bensmann
 */
public enum Strategy {

    /**
     * LRU
     */
    LEAST("LRU"),
    /**
     * MRU
     */
    MOST("MRU");

    /**
     * Name of cache strategy
     */
    private String strategyName;

    Strategy(String strategyName) {
        this.strategyName = strategyName;
    }
}
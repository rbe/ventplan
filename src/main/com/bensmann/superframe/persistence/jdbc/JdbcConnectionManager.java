/*
 * Created on 12.10.2004
 *
 */
package com.bensmann.superframe.persistence.jdbc;

import com.bensmann.superframe.java.Debug;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Singleton object for a JDBC connection manager.
 *
 * @author rb
 * @version $Id: JdbcConnectionManager.java,v 1.1 2005/07/19 15:51:40 rb Exp $
 */
public class JdbcConnectionManager {
    
    /**
     * Singleton pattern
     */
    private static JdbcConnectionManager singleton = null;
    
    /**
     *
     */
    private Map<String, SingleJdbcConnection> connections =
            new HashMap<String, SingleJdbcConnection>();
    
    /**
     * Synchronize access to Map connections
     */
    private Semaphore sem = new Semaphore(3, true);
    
    /**
     * Singleton pattern: private constructor
     */
    private JdbcConnectionManager() {
    }
    
    /**
     * Singleton pattern: create new instance if neccessary and return instance
     */
    public static JdbcConnectionManager getInstance() {
        
        if (singleton == null) {
            singleton = new JdbcConnectionManager();
        }
        
        return singleton;
        
    }
    
    /**
     *
     */
    private void printSemaphore() {
        System.out.println("Available semaphores: " + sem.availablePermits());
    }
    
    /**
     *
     * @param sjc
     * @param ident
     */
    public void addConnection(String ident, SingleJdbcConnection sjc) {
        
        printSemaphore();
        if (sem.tryAcquire()) {
            
            // Add connection with key 'ident_name of database' to hashmap
            String s = ident + "_" + sjc.getJdbcUrl();
            connections.put(s, sjc);
            
            // Debug
            Debug.log("Added connection: " + s);
            
            // Release semaphore
            sem.release();
            
        } else {
            Debug.log("Could not acquire semaphore!");
        }
        printSemaphore();
        
    }
    
    /**
     *
     * @param ident
     */
    public void removeConnection(String ident) {
        
        printSemaphore();
        // Try to acquire semaphore
        if (sem.tryAcquire()) {
            // Remove connection from Map
            connections.remove(ident);
            // Release semaphore
            sem.release();
            
        }
        printSemaphore();
        
    }
    
    /**
     * Return JDBC connection identified by 'ident'
     *
     * @param ident
     * @return
     */
    public SingleJdbcConnection getConnection(String ident)
    throws SQLException {
        
        SingleJdbcConnection sjc = null;
        
        System.out.println("Getting connection for session " + ident);
        
        printSemaphore();
        if (sem.tryAcquire()) {
            
            // Is there a connection for this session already?
            sjc = connections.get(ident);
            try {
                sjc.checkConnection();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            
            sem.release();
            
        } else {
            Debug.log("Could not acquire semaphore!");
        }
        printSemaphore();
        
        return sjc;
        
    }
    
}
/*
 * PropertyUtil.java
 *
 * Created on 18. Juni 2005, 13:25
 *
 */

package com.bensmann.superframe.java.util;

import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author Ralf Bensmann
 * @version $Id: PropertyUtil.java,v 1.1 2005/07/19 15:51:39 rb Exp $
 */
public class PropertyUtil {
    
    /** You cannot create an instance of PropertyUtil */
    private PropertyUtil() {
    }
    
    /** Erzeugt ein neues Properties-Objekt wobei nur die Einträge,
     * die per Schlüssel/Array übergeben werden, in das neue Properties-
     * Objekt übernommen werden
     *
     * @param filterKeys String-Array mit zu suchenden Schlüsseln
     * @param props Properties-Objekt, das durchsucht werden soll
     * @return Ein neues Properties-Objekt
     */
    public static Properties filterPropertiesForKeys(
            String[] filterKeys,
            Properties props) {
        
        Properties p = new Properties();
        
        if (props != null) {
            Enumeration e = props.keys();
            
            while (e.hasMoreElements()) {
                
                String key = (String) e.nextElement();
                
                for (int i = 0; i < filterKeys.length; i++) {
                    if (key.indexOf(filterKeys[i]) == 0)
                        p.put(key, props.getProperty(key));
                    
                }
                
            }
            
        }
        
        return p;
        
    }
    
    /** Erzeugt ein neues Properties-Objekt wobei nur die Einträge,
     * die per Werte/Array übergeben werden, in das neue Properties-
     * Objekt übernommen werden
     *
     * @param filterValues String-Array mit zu suchenden Werten
     * @param props Properties-Objekt, das durchsucht werden soll
     * @return Ein neues Properties-Objekt
     */
    public static Properties filterPropertiesForValues(
            String[] filterValues,
            Properties props) {
        
        Properties p = new Properties();
        
        if (props != null) {
            
            Enumeration e = props.keys();
            
            while (e.hasMoreElements()) {
                
                String key = (String) e.nextElement();
                String value = props.getProperty(key);
                
                for (int i = 0; i < filterValues.length; i++) {
                    if (value.indexOf(filterValues[i]) != -1)
                        p.put(key, props.getProperty(key));
                }
                
            }
            
        }
        
        return p;
        
    }
    
    /** Zeigt ein Properties-Objekt auf stdout an
     *
     * @param props Ein Properties-Objekt
     */
    public static void dumpProperties(Properties props) {
        
        if (props != null) {
            
            Enumeration e = props.keys();
            
            while (e.hasMoreElements()) {
                
                String key = (String) e.nextElement();
                System.out.println(key + "=" + props.getProperty(key));
                
            }
            
        }
        
    }
    
}

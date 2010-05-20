/**
 * Created on Jan 20, 2003
 *
 */
package com.bensmann.superframe.java.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author rb
 * @version $Id: MapUtil.java,v 1.1 2005/07/19 15:51:39 rb Exp $
 */
public class MapUtil {
    
    /** You cannot create an instance */
    private MapUtil() {
    }
    
    public static String setKeysToCommaString(Set s) {
        
        StringBuffer sb = new StringBuffer();
        
        Iterator i = s.iterator();
        
        while (i.hasNext()) {
            
            Map.Entry o = (Map.Entry) i.next();
            
            sb.append((String) o.getKey());
            
            if (i.hasNext()) {
                sb.append(", ");
            }
            
        }
        
        return sb.toString();
        
    }
    
    public static String searchMap(Map hm, String searchFor) {
        
        Iterator i = hm.entrySet().iterator();
        String newName = null;
        
        while (i.hasNext()) {
            
            Map.Entry entry = (Map.Entry) i.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            
            if (key.equals(searchFor)) {
                newName = value;
            }
            
            if (value.equals(searchFor)) {
                newName = key;
            }
            
        }
        
        return newName;
        
    }
    
    public static String searchMapIgnoreCase(Map hm, String searchFor) {
        
        Iterator i = hm.entrySet().iterator();
        String newName = null;
        
        while (i.hasNext()) {
            
            Map.Entry entry = (Map.Entry) i.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            
            if (key.equalsIgnoreCase(searchFor))
                newName = value;
            
            if (value.equalsIgnoreCase(searchFor))
                newName = key;
            
        }
        
        return newName;
    }
    
}
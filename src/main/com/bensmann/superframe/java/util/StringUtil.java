/*
 * Datei angelegt am 21.10.2003
 */
package com.bensmann.superframe.java.util;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Masanori Fujita
 * @version $Id: StringUtil.java,v 1.1 2005/07/19 15:51:39 rb Exp $
 */
public final class StringUtil {
    
    // TODO: Use Enums
    public static final int LEFT = 1;
    
    public static final int RIGHT = 2;
    
    /**
     * You cannot create an instance from StringUtil
     */
    private StringUtil() {
    }
    
    public static String toString(Collection c) {
        
        StringBuffer result = new StringBuffer();
        Iterator it = c.iterator();
        
        while (it.hasNext()) {
            Object o = (Object) it.next();
            result.append(o.toString());
        }
        
        return result.toString();
        
    }
    
    /**
     * @param input
     * @param orientation
     * @param length
     * @param fillWith
     * @return
     */
    public static String justify(String input, int orientation, int length,
            String fillWith) {
        
        String output = input;
        int lengthOfInput = input.length();
        int lengthDelta = length - lengthOfInput;
        
        String fill = "";
        if (lengthDelta > 0) {
            
            for (int i = 0; i < lengthDelta; i++) {
                fill += fillWith;
            }
            
            if (orientation == LEFT) {
                output += fill;
            } else if (orientation == RIGHT) {
                output = fill + input;
            }
            
        } else {
            output = input.substring(0, length);
        }
        
        return output;
        
    }
    
    /**
     * Fill up a string (append at end) 's' up to a length of 'length' with
     * placeholder 'p'
     * @param s
     * @param length
     * @param p
     * @return With 'p' up to 'length' filled up string 's'
     */
    public static String fillUp(String s, int length, String p) {
        
        StringBuffer sb = new StringBuffer(s);
        
        // We assume that argument length is greater than s.length() :-)
        for (int i = 0; i < length - s.length(); i++) {
            sb.append(p);
        }
        
        return sb.toString();
        
    }
    
    /**
     * Remove any digits from string, characters will survive
     * @param s
     * @return
     */
    public static String removeDigits(String s) {
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            
            char c = s.charAt(i);
            if (!Character.isDigit(c)) {
                sb.append(c);
            }
            
        }
        
        return sb.toString();
        
    }
    
    /**
     * Remove any letters from string, digits will survive
     * @param s
     * @return
     */
    public static String removeLetters(String s) {
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            
            char c = s.charAt(i);
            if (!Character.isLetter(c)) {
                sb.append(c);
            }
            
        }
        
        return sb.toString();
        
    }
    
    /**
     * Get only digits from string
     * @param s
     * @return
     */
    public static String getDigits(String s) {
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                sb.append(c);
            }
            
        }
        
        return sb.toString();
        
    }
    
    /**
     * Convert first letter to upper case
     */
    public static String toUpperCaseFirst(String s) {
        
        char firstCharacter = '\0';
        StringBuffer sb = null;
        
        if (s != null) {
            
            firstCharacter = Character.toUpperCase(s.charAt(0));
            sb = new StringBuffer(s);
            sb.setCharAt(0, firstCharacter);
            
        }
        
        return sb != null ? sb.toString() : null;
        
    }
    
}

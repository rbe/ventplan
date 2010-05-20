/*
 * ConversionUtil.java
 *
 * Created on 5. August 2006, 12:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.bensmann.superswing.component.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

/**
 *
 * @author rb
 */
public class ConversionUtil {
    
    private static NumberFormat nfGerman;
    
    private static NumberFormat nfUs;
    
    // Static initializer
    static {
        
        nfGerman = NumberFormat.getInstance(Locale.GERMAN);
        nfGerman.setGroupingUsed(true);
        nfGerman.setMinimumFractionDigits(2);
        
        nfUs = NumberFormat.getInstance(Locale.US);
        nfUs.setGroupingUsed(true);
        nfUs.setMinimumFractionDigits(2);
        
    }
    
    /**
     * Creates a new instance of ConversionUtil
     */
    public ConversionUtil() {
    }
    
    /**
     *
     * @param f
     * @return
     */
    public static Locale getLocaleFromFloat(String s) {
        
        Locale locale = null;
        
        // Test for American style (with dot)
        if (s.indexOf(".") >= 0) {
            locale = Locale.US;
        }
        // Test for German style (with comma)
        else if (s.indexOf(",") >= 0) {
            locale = Locale.GERMAN;
        }
        
        return locale;
        
    }
    
    /**
     *
     * @param f
     * @return
     */
    public static Locale getLocaleFromFloat(float f) {
        return getLocaleFromFloat("" + f);
    }
    
    /**
     *
     * @return
     * @param component
     */
    public static float parseFloatFromComponent(JComponent component) {
        
        float f = 0f;
        String s = null;
        
        if (component instanceof JTextComponent) {
            s = ((JTextComponent) component).getText();
        } else if (component instanceof JLabel) {
            s = ((JLabel) component).getText();
        }
        
        // Test for American style (with dot)
        if (getLocaleFromFloat(s) == Locale.US) {
            
            try {
                f = nfUs.parse(s).floatValue();
            } catch (ParseException e) {}
            
        }
        // Test for German style (with comma)
        else if (getLocaleFromFloat(s) == Locale.GERMAN) {
            
            try {
                f = nfGerman.parse(s).floatValue();
            } catch (ParseException e) {}
            
        }
        // No punctuation found
        else {
            
            try {
                f = nfGerman.parse(s).floatValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
        }
        
        return f;
        
    }
    
    /**
     *
     * @param textComponent
     * @return
     */
    public static double parseDoubleFromComponent(JComponent component) {
        
        double d = 0f;
        String s = null;
        
        if (component instanceof JTextComponent) {
            s = ((JTextComponent) component).getText();
        } else if (component instanceof JLabel) {
            s = ((JLabel) component).getText();
        }
        
        // Test for American style (with dot)
        if (getLocaleFromFloat(s) == Locale.US) {
            
            try {
                d = nfUs.parse(s).doubleValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
        }
        // Test for German style (with comma)
        else if (getLocaleFromFloat(s) == Locale.GERMAN) {
            
            try {
                d = nfGerman.parse(s).doubleValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
        }
        // No punctuation found
        else {
            
            try {
                d = nfGerman.parse(s).doubleValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
        }
        
        return d;
        
    }
    
    /**
     *
     * @param textComponent
     * @return
     */
    public static int parseIntFromComponent(JComponent component) {
        
        int i = 0;
        String s = null;
        
        if (component instanceof JTextComponent) {
            s = ((JTextComponent) component).getText();
        } else if (component instanceof JLabel) {
            s = ((JLabel) component).getText();
        }
        
        // Test for American style (with dot)
        if (getLocaleFromFloat(s) == Locale.US) {
            
            try {
                i = nfUs.parse(s).intValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
        }
        // Test for German style (with comma)
        else if (getLocaleFromFloat(s) == Locale.GERMAN) {
            
            try {
                i = nfGerman.parse(s).intValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
        }
        // No punctuation found
        else {
            
            try {
                i = nfGerman.parse(s).intValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
        }
        
        return i;
        
    }
    
    /**
     * Set a float as a formatted value in a JComponent
     *
     * @param component
     * @param f
     */
    public static void setFormattedFloatInComponent(JComponent component, float f, Locale locale) {
        
        String s = null;
        
        // HACK: f may be NaN
        if (("" + f).equals("NaN")) {
            f = 0.0f;
        }
        
        if (locale == Locale.US) {
            s = nfUs.format(f);
        } else if (locale == Locale.GERMAN) {
            s = nfGerman.format(f);
        }
        
        if (component instanceof JTextComponent) {
            ((JTextComponent) component).setText(s);
        } else if (component instanceof JLabel) {
            ((JLabel) component).setText(s);
        }
        
    }
    
    /**
     *
     * @param component
     * @param f
     */
    public static void setFormattedFloatInComponent(JComponent component, float f) {
        Locale locale = getLocaleFromFloat(f);
        setFormattedFloatInComponent(component, f, locale);
    }
    
}

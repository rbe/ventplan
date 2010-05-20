/*
 * ApplHelper.java
 *
 * Created on 23. Juli 2006, 13:32
 *
 */

package com.bensmann.superswing;

import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;
import javax.swing.DesktopManager;
import javax.swing.JDesktopPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * $Header$
 * @author rb
 * @version $Id$
 * @date $Date$
 * @log $Log$
 */
public class ApplHelper {
    
    /**
     */
    private static ApplHelper instance;
    
    /**
     * User preferences (stored in Windows Registry or dot-file)
     */
    protected Preferences userPreferences;
    
    /**
     */
    protected JDesktopPane desktopPane;
    
    /**
     * XML document for configuration
     */
    protected Document document;
    
    /**
     *
     */
    protected XPath xpath;
    
    /**
     * Singleton pattern: do not create a new instance of ApplHelper
     * ... use getInstance()
     */
    protected ApplHelper() {
        
        xpath = XPathFactory.newInstance().newXPath();
        
        // Load user preferences (from Windows Registry or UNIX dot-files)
        loadPreferences();
        
    }
    
    /**
     *
     * @return
     */
    public static ApplHelper getInstance() {
        
        if (instance == null) {
            instance = new ApplHelper();
        }
        
        return instance;
        
    }
    
    /**
     * 
     * 
     * @param configResource 
     * @throws javax.xml.parsers.ParserConfigurationException 
     * @throws org.xml.sax.SAXException 
     * @throws javax.xml.xpath.XPathExpressionException 
     * @throws java.io.IOException 
     */
    public void loadXmlConfiguration(String configResource)
    throws ParserConfigurationException, SAXException, IOException,
            XPathExpressionException {
        
        InputStream inputStream = null;
        
        // Get input stream for resource
        inputStream = ApplHelper.class.getResourceAsStream(configResource);
        
        // Parse XML document if it wasn't parsed before
        if (document == null && inputStream != null) {
            
            document = DocumentBuilderFactory.
                    newInstance().
                    newDocumentBuilder().
                    parse(inputStream);
            
        }
        
    }
    
    /**
     * 
     * @param configResource 
     * @throws javax.xml.parsers.ParserConfigurationException 
     * @throws org.xml.sax.SAXException 
     * @throws java.io.IOException 
     * @throws javax.xml.xpath.XPathExpressionException 
     */
    public void loadXmlConfiguration()
    throws ParserConfigurationException, SAXException, IOException,
            XPathExpressionException {
        
        loadXmlConfiguration("config.xml");
        
    }
    
    /**
     * Load preferences for this package
     */
    public void loadPreferences() {
        userPreferences = Preferences.userNodeForPackage(this.getClass());
    }
    
    /**
     * Get a certain preference from the user preferences object. Default value
     * is null.
     *
     * @param key
     * @return
     */
    public String getPreference(String key) {
        return userPreferences.get(key, null);
    }
    
    /**
     * Store a value under a certain key in the user preferences.
     *
     * @param key
     * @param value
     */
    public void setPreference(String key, String value) {
        userPreferences.put(key, value);
    }
    
    /**
     * Remove a certain preference.
     *
     * @param key
     */
    public void removePreference(String key) {
        userPreferences.remove(key);
    }
    
    /**
     * 
     * @param desktopPane 
     */
    public void setDesktopPane(JDesktopPane desktopPane) {
        this.desktopPane = desktopPane;
    }
    
    /**
     * 
     * @return 
     */
    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }
    
    /**
     * 
     * @return 
     */
    public DesktopManager getDesktopManager() {
        return desktopPane.getDesktopManager();
    }
    
    /**
     * 
     * @return 
     */
    public Document getDocument() {
        return document;
    }
    
}

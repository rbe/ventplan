/*
 * XmlToComboBox.java
 *
 * Created on 21. Juli 2006, 12:18
 *
 */

package com.bensmann.superswing.model;

import java.io.File;
import java.io.IOException;
import javax.swing.JComboBox;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * $Header$
 * @author rb
 * @version $Id$
 * @date $Date$
 * @log $Log$
 */
public class XmlToComboBox {
    
    /**
     * Creates a new instance of XmlToComboBox
     */
    public XmlToComboBox() {
    }
    
    /**
     * This method takes a XML element and builds up a JComboBox.
     *
     *
     * @param element
     * @param childName
     * @param attributes
     * @return
     */
    public static JComboBox generateComboBox(NodeList nodes,
            String attribute, String defaultAttribute) {
        
        JComboBox comboBox = null;
        String[] s = null;
        String defaultAttributeValue = null;
        String selectedItem = null; // Attribute default="yes"
        int i = 0;
        
        if (nodes != null) {
            
            s = new String[nodes.getLength()];
            
            for (int z = 0; z < nodes.getLength(); z++) {
                
                if (attribute == null) {
                    s[i++] = nodes.item(z).getTextContent();
                } else {
                    s[i++] = nodes.item(z).
                            getAttributes().
                            getNamedItem(attribute).
                            getNodeValue();
                }
                
                if (defaultAttribute != null) {
                    
                    Node n = nodes.item(z).
                            getAttributes().
                            getNamedItem(defaultAttribute);
                    
                    if (n != null) {
                        
                        defaultAttributeValue = n.getNodeValue();
                        
                        if (defaultAttributeValue != null &&
                                (defaultAttributeValue.equalsIgnoreCase("yes") ||
                                defaultAttributeValue.equalsIgnoreCase("true"))) {
                            
                            selectedItem = s[i - 1];
                            
                        }
                        
                    }
                    
                }
                
            }
            
            comboBox = new JComboBox(s);
            if (selectedItem != null) {
                comboBox.setSelectedItem(selectedItem);
            }
            
        }
        
        return comboBox;
        
    }
    
    public static JComboBox generateComboBox(NodeList nodes, String attribute) {
        return generateComboBox(nodes, attribute, null);
    }
    
    public static JComboBox generateComboBox(NodeList nodes) {
        return generateComboBox(nodes, null, null);
    }
    
    public static void main(String[] args)
    throws ParserConfigurationException, SAXException, IOException,
            XPathExpressionException {
        
        File f = new File("c:/netbeans-workspace/WestaWac/src/com/westaflex/resource/config/westawac.xml");
        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
        
        XPathExpression x = XPathFactory.newInstance().newXPath().compile("//westawac/luftmengentyp/modell");
        NodeList nodes = (NodeList) x.evaluate(d, XPathConstants.NODESET);
        System.out.println(XmlToComboBox.generateComboBox(nodes, "name", "100FILTAB").getModel().getSize());
        
    }
    
}

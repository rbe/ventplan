/*
 * com/bensmann/superframe/persistence/jdbc/JAXPTest.java
 *
 * JAXPTest.java created on 6. Januar 2007, 16:15 by rb
 *
 * Copyright (C) 2006 Ralf Bensmann, java@bensmann.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA
 *
 */

package com.bensmann.superframe.persistence.jdbc;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author rb
 * @version 1.0
 */
public class JAXPTest {
    
    /**
     * Creates a new instance of JAXPTest
     */
    public JAXPTest() {
    }
    
    public static void main(String[] args) {
        
        // Create document
        Document document = null;
        Node rootNode = null;
        Element testNode = null;
        try {
            document = DocumentBuilderFactory.
                    newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        // Root node
        rootNode = document.createElement("root");
        document.appendChild(rootNode);
        // Node with attribute
        testNode = document.createElement("test");
        testNode.setAttribute("attr", "value");
        testNode.setTextContent("test content");
        rootNode.appendChild(testNode);
        rootNode.appendChild(testNode.cloneNode(true));
        
        // Output XML document
        Transformer transformer = null;
        try {
            
            transformer =
                    TransformerFactory.newInstance().
                    newTransformer();
            
            transformer.setOutputProperty("encoding", "ISO-8859-15");
            transformer.setOutputProperty("indent", "yes");
            
            transformer.transform(
                    new DOMSource(document), new StreamResult(System.out));
            
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // XPath
        String search = null;
        XPath xpath = null;
        XPathExpression xpe = null;
        NodeList nodes = null;
        Node node = null;
        NamedNodeMap attributes = null;
        Node attr = null;
        xpath = XPathFactory.newInstance().newXPath();
//        search = "//test[@attr='value']";
        search = "test/@attr";
        try {
            
            // Use XPath to look for certain nodes
            xpe = xpath.compile(search);
            nodes = (NodeList) xpe.evaluate(rootNode, XPathConstants.NODESET);
            
            // Show every node
            for (int i = 0; i < nodes.getLength(); i++) {
                
                node = nodes.item(i);
                System.out.println(i + " " + node.getNodeName());
                System.out.println("   --> " + node.getTextContent());
                
                // Show attributes of node
                attributes = node.getAttributes();
                if (attributes != null) {
                    
                    for (int j = 0; j < attributes.getLength(); j++) {
                        attr = attributes.item(j);
                        System.out.println("   --> " + attr.getNodeName() + " = " + attr.getNodeValue());
                    }
                    
                }
                
            }
            
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        
    }
    
}

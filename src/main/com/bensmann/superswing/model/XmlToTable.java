/*
 * XmlToTable.java
 *
 * Created on 24. Juli 2006, 13:27
 *
 */

package com.bensmann.superswing.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * $Header$
 * @author rb
 * @version $Id$
 * @date $Date$
 * @log $Log$
 */
public class XmlToTable {
    
    /**
     * Creates a new instance of XmlToComboBox
     */
    public XmlToTable() {
    }
    
    /**
     * This method takes a XML element and builds up a JTable.
     *
     * @param tableModelClass
     * @param nodes
     * @param elementsToColumn
     * @throws javax.xml.xpath.XPathExpressionException
     * @return
     */
    public static JTable generateTable(Class<?> tableModelClass, NodeList nodes,
            String[] elementsToColumn)
            throws XPathExpressionException {
        
        JTable table = null;
        String[][] rowData = null;
        String[] columnNames = null;
        String defaultAttributeValue = null;
        int i = 0;
        int j = 0;
        Node child = null;
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression x = null;
        Constructor constructor = null;
        
        if (nodes != null) {
            
            columnNames = elementsToColumn;
            rowData = new String[nodes.getLength()][elementsToColumn.length];
            
            for (int z = 0; z < nodes.getLength(); z++) {
                
                for (String col : elementsToColumn) {
                    
                    // TODO Xpath compile or getElementsByTagName()?
                    x = xpath.compile(col.toLowerCase() + "/text()");
                    rowData[i][j++] = x.evaluate(nodes.item(z));
                    
                }
                
                i++;
                j = 0;
                
            }
            
            try {
                
                constructor = tableModelClass.getConstructor(
                        new Class[] {Object[][].class, Object[].class});
                
                table = new JTable((TableModel) constructor.newInstance(rowData, columnNames));
                
            } catch (SecurityException ex) {
                ex.printStackTrace();
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
            
        }
        
        return table;
        
    }
    
    /**
     *
     * @param nodes
     * @param elementsToColumn
     * @throws javax.xml.xpath.XPathExpressionException
     * @return
     */
    public static JTable generateTable(NodeList nodes, String[] elementsToColumn)
    throws XPathExpressionException {
        
        return generateTable(DefaultTableModel.class, nodes, elementsToColumn);
        
    }
    
}

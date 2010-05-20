/*
 * XmlToSwing.java
 *
 * Created on 26. Juli 2006, 17:00
 *
 */

package com.bensmann.superswing.model;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 *
 * $Header$
 * @author rb
 * @version $Id$
 * @date $Date$
 * @log $Log$
 */
public class XmlToComponent {
    
    private Document document;
    
    /**
     * Creates a new instance of XmlToSwing
     */
    public XmlToComponent(Document document) {
        this.document = document;
    }
    
    /**
     *
     * @param comboBoxModelElement
     * @param comboBox
     */
    private void injectComboBoxModel(Element comboBoxModelElement, JComboBox comboBox) {
        
        // <component name="wfGeschossCombobox" type="javax.swing.JComboBox">
        //     <comboboxmodel selected-item="EG">
        //         <item index="0">KG</item>
        //         <item index="1">EG</item>
        //         <item index="2">OG</item>
        //         <item index="3">SB</item>
        //     </comboboxmodel>
        // </component>
        
        // JComboBox
        JComboBox comboBox2 = null;
        NodeList itemElements = null;
        String[] comboBoxItems = null;
        
        // item-Elemente holen
        itemElements = comboBoxModelElement.getElementsByTagName("item");
        // String-Array mit Werten aus <item> aufbauen
        comboBoxItems = new String[itemElements.getLength()];
        for (int i = 0; i < itemElements.getLength(); i++) {
            comboBoxItems[i] = itemElements.item(i).getTextContent();
        }
        // Neue ComboBox erzeugen
        comboBox2 = new JComboBox(comboBoxItems);
        // Selected item setzen
        comboBox2.setSelectedItem("" + comboBoxModelElement.getAttribute("selected-item"));
        // Model auf die bestehende übertragen
        comboBox.setModel(comboBox2.getModel());
        
    }
    
    /**
     *
     * @param tableModelElement
     * @param table
     */
    private void injectTableModel(Element tableModelElement, JTable table)
    throws ClassNotFoundException {
        
        // <component name="widerstandsbeiwerteTabelleTable" type="javax.swing.JTable">
        //     <tablemodel columns="3" rows="3" selected-row="1">
        //         <column-header>
        //             <column index="0" name="Anzahl"/>
        //             <column index="1" name="Bezeichnung"/>
        //             <column index="2" name="Wert"/>
        //         </column-header>
        //         <rowdata column="0" row="0">0</rowdata>
        //         <rowdata column="1" row="0">Kr&#252;mmer flach</rowdata>
        //         <rowdata column="2" row="0">0.45</rowdata>
        //         <rowdata column="0" row="1">0</rowdata>
        //         <rowdata column="1" row="1">Kr&#252;mmer hoch</rowdata>
        //         <rowdata column="2" row="1">0.41</rowdata>
        //         <rowdata column="0" row="2">0</rowdata>
        //         <rowdata column="1" row="2">AZ Trennung w3/w1 = 0,4</rowdata>
        //         <rowdata column="2" row="2">7</rowdata>
        //     </tablemodel>
        // </component>
        
        // JTable
        JTable table2 = null;
        int selectedRow = -1;
        NodeList tableColumnHeaderElements = null;
        Element tableColumnHeaderElement = null;
        NodeList tableRowDataElements = null;
        Element tableRowDataElement = null;
        int tableRowCount = 0;
        int tableColumnCount = 0;
        String[] tableColumnHeader = null;
        String[][] tableRowData = null;
        int row = 0;
        int col = 0;
        Constructor<?> constructor = null;
        Class<?> tableModelClass = null;
        Class<?>[] cellEditorClass = null;
        
        // Class of table model
        tableModelClass = Class.forName(tableModelElement.getAttribute("type"));
        // column-header- und rowdata-Elemente holen
        tableColumnHeaderElements = tableModelElement.getElementsByTagName("column");
        //
        tableRowDataElements = tableModelElement.getElementsByTagName("rowdata");
        // Anzahl der Zeilen und Spalten
        tableRowCount = Integer.valueOf(tableModelElement.getAttribute("rows"));
        tableColumnCount = Integer.valueOf(tableModelElement.getAttribute("columns"));
        
        // String-Array für Spaltenüberschriften initialisieren
        tableColumnHeader = new String[tableColumnCount];
        cellEditorClass = new Class[tableColumnCount];
        for (int i = 0; i < tableColumnHeaderElements.getLength(); i++) {
            
            tableColumnHeaderElement = (Element) tableColumnHeaderElements.item(i);
            tableColumnHeader[Integer.valueOf(tableColumnHeaderElement.getAttribute("index"))] =
                    tableColumnHeaderElement.getAttribute("name");
            
            cellEditorClass[i] = Class.forName(tableColumnHeaderElement.getAttribute("cell-editor"));
            
        }
        // String-Array für Daten initialisieren
        tableRowData = new String[tableRowCount][tableColumnCount];
        for (int i = 0; i < tableRowCount * tableColumnCount; i++) {
            
            tableRowDataElement = (Element) tableRowDataElements.item(i);
            
            row = Integer.valueOf(tableRowDataElement.getAttribute("row"));
            col = Integer.valueOf(tableRowDataElement.getAttribute("column"));
            
            tableRowData[row][col] = tableRowDataElement.getTextContent();
            
        }
        
        // Neue Table anlegen und Model kopieren
        try {
            constructor = DefaultTableModel.class.getConstructor(Object[][].class, Object[].class);
//            constructor = tableModelClass.getConstructor(
//                    Object[][].class, Object[].class);
            table2 = new JTable((TableModel) constructor.newInstance(tableRowData, tableColumnHeader));
            table.setModel(table2.getModel());
            
            // TODO: Set cell editor
//            for (int i = 0; i < tableColumnCount; i++) {
//                constructor = cellEditorClass[i].getConstructor(new Class[] {});
//                table2.getColumn(i).setCellEditor((TableCellEditor) constructor.newInstance(new Object[] {}));
//            }
            
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
        
        // selected-row holen
        try {
            
            selectedRow = Integer.valueOf(tableModelElement.getAttribute("selected-row"));
            
            if (row > -1) {
                table.changeSelection(selectedRow, 0, false, false);
            }
            
        } catch (NumberFormatException e) {
            // ignore
        }
        
    }
    
    /**
     *
     * @param component
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public void injectValues(JComponent component)
    throws XPathExpressionException {
        
        String componentName = null;
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression searchForNameXpathExpression = null;
        Element result = null;
        String value = null;
        String tableName = null;
        
//        System.out.println("1. Looking for component name=" + component.getName());
        // Go through every component...
        for (Component comp : component.getComponents()) {
            
//            System.out.println("c=" + comp.getClass().getName());
//            if (comp.getClass().getName().equalsIgnoreCase("com.westaflex.component.classes.SeeTable")){
//                System.out.println(">>>>>>>>>Tabelle");
//            }
            
            // Recursion needed?
            // JPanel
            if (comp instanceof JPanel) {
                injectValues((JPanel) comp);
            }
            // JScrollPane
            else if (comp instanceof JScrollPane) {
                injectValues((JScrollPane) comp);
            }
            // JScrollPane.JViewport
            else if (comp instanceof JViewport) {
                injectValues((JViewport) comp);
            }
            // JTabbedPane
            else if (comp instanceof JTabbedPane) {
                injectValues((JTabbedPane) comp);
            }
            // JInternalFrame
            else if (comp instanceof JInternalFrame) {
                injectValues((JInternalFrame) comp);
            }
            // JRootPane
            else if (comp instanceof JRootPane) {
                injectValues((JRootPane) comp);
            }
            // JLayeredPane
            else if (comp instanceof JLayeredPane) {
                injectValues((JLayeredPane) comp);
            }
            // JLayeredPane
            else if (comp instanceof BasicInternalFrameTitlePane) {
                injectValues((BasicInternalFrameTitlePane) comp);
            }
            // Anything else like TextField, TextArea, ComboBox, CheckBox,
            // RadioButton
            else {
                
                // Process component if 'name' of component is set
                componentName = comp.getName();
                if (componentName != null) {
                    
//                    System.out.println("2. Looking for component name=" + componentName);
                    
                    searchForNameXpathExpression =
                            xpath.compile("//*[@name='" + comp.getName() + "']");
                    result = (Element) searchForNameXpathExpression.
                            evaluate(document, XPathConstants.NODE);
                    if (result != null){
                        value = result.getAttribute("value");
                        
                        // JLabel
                        // <component name="mindestaussenluftrateWertLabel" type="javax.swing.JLabel" value="0"/>
                        if (comp instanceof JLabel) {
                            ((JLabel) comp).setText(value);
                        }
                        // JTextField, JTextArea
                        // <component name="adBauvorhabenTextField" type="javax.swing.JTextField" value="EFH tuts das?"/>
                        else if (comp instanceof JTextComponent) {
                            ((JTextComponent) comp).setText(value);
                        }
                        // JCheckBox, JRadioButton
                        // <component name="lkAufputzCheckbox" type="javax.swing.JCheckBox" value="false"/>
                        else if (comp instanceof JToggleButton) {
                            ((JToggleButton) comp).setSelected(value.equals("true"));
                        }
                        // JSpinner
                        // <component name="personenanzahlSpinner" type="javax.swing.JSpinner" value="0"/>
                        else if (comp instanceof JSpinner) {
                            ((JSpinner) comp).getModel().setValue(Integer.valueOf("" + value));
                        }
                        // JComboBox
                        else if (comp instanceof JComboBox) {
                            // comboboxmodel-Elemente holen
                            injectComboBoxModel(
                                    (Element) result.getElementsByTagName("comboboxmodel").item(0),
                                    (JComboBox) comp);
                        }
                        // JTable
                        else if (comp instanceof JTable) {
                            
                            tableName = ((Element) result.getElementsByTagName("tablemodel").item(0)).getAttribute("name");
                            
                            try {
                                // tablemodel-Element holen
                                injectTableModel(
                                        (Element) result.getElementsByTagName("tablemodel").item(0),
                                        (JTable) comp);
                            } catch (ClassNotFoundException ex) {
                                System.out.println("Cannot inject table model for table '" + tableName + "':");
                                ex.printStackTrace();
                            }
                            
                        }
                    }
                }
                
            }
            
        }
        
    }
    
//    /**
//     *
//     * @param name
//     * @return
//     */
//    public Component createComponent(String name) {
//
//        Component component = null;
//        XPath xpath = XPathFactory.newInstance().newXPath();
//        XPathExpression searchForNameXpathExpression = null;
//        Element result = null;
//
//        try {
//
//            searchForNameXpathExpression =
//                    xpath.compile("//*[@name='" + name + "']");
//            result = (Element) searchForNameXpathExpression.
//                    evaluate(document, XPathConstants.NODE);
//
//        } catch (XPathExpressionException ex) {
//            ex.printStackTrace();
//        }
//
//        return component;
//
//    }
    
}

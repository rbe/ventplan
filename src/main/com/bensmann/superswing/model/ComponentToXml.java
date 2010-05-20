/*
 * ComponentToXml.java
 *
 * Created on 24. Juli 2006, 15:37
 *
 */
package com.bensmann.superswing.model;

import com.bensmann.superswing.model.filter.XmlDataModelFilter;
import java.awt.Component;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ComboBoxModel;
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
import javax.swing.SpinnerModel;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * $Header$
 * @author rb
 * @version $Id$
 * @date $Date$
 * @log $Log$
 */
public final class ComponentToXml {

    /**
     *
     */
    private Document document;
    /**
     * Chain of XmlDataModelFilters
     */
    private List<XmlDataModelFilter> filterChain;

    /**
     * Creates a new instance of SwingToXml
     */
    public ComponentToXml() {
        filterChain = new LinkedList<XmlDataModelFilter>();
    }

    /**
     *
     * @param filter
     */
    public void addXmlDataModelFilter(XmlDataModelFilter filter) {
        filterChain.add(filter);
    }

    /**
     *
     * @param filter
     */
    public void removeFilter(XmlDataModelFilter filter) {
        filterChain.remove(filter);
    }

    /**
     *
     * @param s
     * @return
     */
    private String stringNotNull(String s) {
        return s != null ? s : "";
    }

    /**
     *
     * @param label
     * @return
     */
    private Node labelToElement(JLabel label) {

        Element tmpElement = null;
        //
        String componentName = null;
        String componentType = null;

        componentType = label.getClass().getName();
        componentName = label.getName();

        tmpElement = document.createElement("component");
        tmpElement.setAttribute("type", stringNotNull(componentType));
        tmpElement.setAttribute("name", stringNotNull(componentName));

        tmpElement.setAttribute("value", stringNotNull(label.getText()));

        return tmpElement;

    }

    /**
     *
     * @param textComponent
     * @return
     */
    private Node textcomponentToElement(JTextComponent textComponent) {

        Element tmpElement = null;
        //
        String componentName = null;
        String componentType = null;

        componentType = textComponent.getClass().getName();
        componentName = textComponent.getName();

        tmpElement = document.createElement("component");
        tmpElement.setAttribute("type", stringNotNull(componentType));
        tmpElement.setAttribute("name", stringNotNull(componentName));

        tmpElement.setAttribute("value", stringNotNull(textComponent.getText()));

        return tmpElement;

    }

    /**
     *
     * @param toogleButton
     * @return
     */
    private Node toggleButtonToElement(JToggleButton toogleButton) {

        Element tmpElement = null;
        //
        String componentName = null;
        String componentType = null;

        componentType = toogleButton.getClass().getName();
        componentName = toogleButton.getName();

        tmpElement = document.createElement("component");
        tmpElement.setAttribute("type", stringNotNull(componentType));
        tmpElement.setAttribute("name", stringNotNull(componentName));

        tmpElement.setAttribute("value", toogleButton.isSelected() ? "true" : "false");

        return tmpElement;

    }

    /**
     *
     * @param spinner
     * @return
     */
    private Node spinnerToElement(JSpinner spinner) {

        Element tmpElement = null;
        //
        String componentName = null;
        String componentType = null;
        //
        SpinnerModel spinnerModel = null;
        Element spinnerModelElement = null;
        Element spinnerModelTmpElement = null;
        Object value = null;

        componentType = spinner.getClass().getName();
        componentName = spinner.getName();

        tmpElement = document.createElement("component");
        tmpElement.setAttribute("type", stringNotNull(componentType));
        tmpElement.setAttribute("name", stringNotNull(componentName));

        tmpElement.setAttribute("value", stringNotNull("" + spinner.getValue()));

        // Add SpinnerModel
        spinnerModel = spinner.getModel();
        spinnerModelElement = document.createElement("spinnermodel");
        // Add current value from SpinnerModel
        spinnerModelTmpElement = document.createElement("item");
        spinnerModelTmpElement.setAttribute("value", stringNotNull("" + spinnerModel.getValue()));
        spinnerModelElement.appendChild(spinnerModelTmpElement);

        tmpElement.appendChild(spinnerModelElement);

        return tmpElement;

    }

    /**
     *
     * @param table
     * @return
     */
    private Node tableToElement(JTable table) {

        Element tmpElement = null;
        //
        String componentName = null;
        String componentType = null;
        // TableModel of JTable
        TableModel tableModel = null;
        int columnCount = 0;
        int rowCount = 0;
        Object rowData = null;
        Element tablemodelElement = null;
        Element columnHeaderElement = null;
        Element columnNameElement = null;
        Element rowDataElement = null;

        componentType = table.getClass().getName();
        componentName = table.getName();

        tmpElement = document.createElement("component");
        tmpElement.setAttribute("type", stringNotNull(componentType));
        tmpElement.setAttribute("name", stringNotNull(componentName));

        tableModel = table.getModel();
        columnCount = tableModel.getColumnCount();
        rowCount = tableModel.getRowCount();
        rowDataElement = null;

        tablemodelElement = document.createElement("tablemodel");
        tablemodelElement.setAttribute("type", "" + tableModel.getClass().getName());
        tablemodelElement.setAttribute("rows", "" + rowCount);
        tablemodelElement.setAttribute("columns", "" + columnCount);
        tablemodelElement.setAttribute("selected-row", "" + table.getSelectedRow());

        // Header (name of columns)
        columnHeaderElement = document.createElement("column-header");
        for (int i = 0; i < columnCount; i++) {

            columnNameElement = document.createElement("column");
            columnNameElement.setAttribute("index", "" + i);
            columnNameElement.setAttribute("name", table.getColumnName(i));
            columnNameElement.setAttribute("cell-editor", table.getCellEditor(0, i).getClass().getName());

            columnHeaderElement.appendChild(columnNameElement);

        }
        tablemodelElement.appendChild(columnHeaderElement);

        // Rowdata
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {

            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {

                rowDataElement = document.createElement("rowdata");
                rowDataElement.setAttribute("row", "" + rowIndex);
                rowDataElement.setAttribute("column", "" + columnIndex);
                rowData = tableModel.getValueAt(rowIndex, columnIndex);
                rowDataElement.setTextContent(
                        rowData != null ? rowData.toString() : "");

                tablemodelElement.appendChild(rowDataElement);

            }

        }

        tmpElement.appendChild(tablemodelElement);

        return tmpElement;

    }

    /**
     *
     * @param comboBox
     * @return
     */
    private Node comboboxToElement(JComboBox comboBox) {

        Element tmpElement = null;
        //
        String componentName = null;
        String componentType = null;
        // ComboBoxModel of JComboBox
        ComboBoxModel comboboxModel = null;
        Object item = null;
        Element comboboxmodelElement = null;
        Element itemElement = null;
        //
        Object tmpObject = null;
        String tmpString = null;

        componentType = comboBox.getClass().getName();
        componentName = comboBox.getName();

        tmpElement = document.createElement("component");
        tmpElement.setAttribute("type", stringNotNull(componentType));
        tmpElement.setAttribute("name", stringNotNull(componentName));

        comboboxModel = comboBox.getModel();
        comboboxmodelElement = document.createElement("comboboxmodel");
        for (int i = 0; i < comboboxModel.getSize(); i++) {

            item = comboboxModel.getElementAt(i);

            itemElement = document.createElement("item");
            itemElement.setAttribute("index", "" + i);

            itemElement.setTextContent(item != null ? item.toString() : "");

            comboboxmodelElement.appendChild(itemElement);

        }

        tmpObject = comboBox.getSelectedItem();
        if (tmpObject != null) {
            tmpString = tmpObject.toString();
        } else {
            tmpString = null;
        }
        comboboxmodelElement.setAttribute("selected-item",
                stringNotNull(tmpString));

        tmpElement.appendChild(comboboxmodelElement);

        return tmpElement;

    }

    /**
     *
     *
     * @param component
     * @return
     */
    public Document generateXml(JComponent component)
            throws ParserConfigurationException {

        Node tmpElement = null;
        //
        String componentName = null;
        String componentType = null;
        //
        boolean add = false;
        Component[] components = null;
        Element componentElement = null;

        if (document == null) {

            document = DocumentBuilderFactory.newInstance().
                    newDocumentBuilder().
                    newDocument();

        }

        componentType = component.getClass().getName();
        componentName = component.getName();

        componentElement = document.createElement("component");
        // Set type and name of JComponent
        componentElement.setAttribute("type", stringNotNull(componentType));
        componentElement.setAttribute("name", stringNotNull(componentName));

        // Go through every Component in component
        components = component.getComponents();
        for (Component c : components) {

//            System.out.println("c=" + c.getClass().getName());

            // Recursion needed?
            // JPanel
            if (c instanceof JPanel) {
                componentElement.appendChild(
                        generateXml((JPanel) c).getDocumentElement());
            } // JScrollPane
            else if (c instanceof JScrollPane) {
                componentElement.appendChild(
                        generateXml((JScrollPane) c).getDocumentElement());
            } // JScrollPane.JViewport
            else if (c instanceof JViewport) {
                componentElement.appendChild(
                        generateXml((JViewport) c).getDocumentElement());
            } // JTabbedPane
            else if (c instanceof JTabbedPane) {
                componentElement.appendChild(
                        generateXml((JTabbedPane) c).getDocumentElement());
            } // JInternalFrame
            else if (c instanceof JInternalFrame) {
                componentElement.appendChild(
                        generateXml((JInternalFrame) c).getDocumentElement());
            } // JRootPane
            else if (c instanceof JRootPane) {
                componentElement.appendChild(
                        generateXml((JRootPane) c).getDocumentElement());
            } // JLayeredPane
            else if (c instanceof JLayeredPane) {
                componentElement.appendChild(
                        generateXml((JLayeredPane) c).getDocumentElement());
            } // JLayeredPane
            else if (c instanceof BasicInternalFrameTitlePane) {
                componentElement.appendChild(
                        generateXml((BasicInternalFrameTitlePane) c).
                        getDocumentElement());
            } // Anything else like TextField, TextArea,
            // ComboBox, CheckBox, RadioButton
            else {

                // Process component if 'name' of component is set
                componentName = c.getName();
                if (componentName != null) {

                    // JLabel
                    if (c instanceof JLabel) {
                        tmpElement = labelToElement((JLabel) c);
                        add = true;
                    } // JTextField, JTextArea
                    else if (c instanceof JTextComponent) {
                        tmpElement = textcomponentToElement((JTextComponent) c);
                        add = true;
                    } // JCheckBox, JRadioButton
                    else if (c instanceof JToggleButton) {
                        tmpElement = toggleButtonToElement((JToggleButton) c);
                        add = true;
                    } // JSpinner
                    else if (c instanceof JSpinner) {
                        tmpElement = spinnerToElement((JSpinner) c);
                        add = true;
                    } // JComboBox
                    else if (c instanceof JComboBox) {
                        tmpElement = comboboxToElement((JComboBox) c);
                        add = true;
                    } // JTable
                    else if (c instanceof JTable) {
                        tmpElement = tableToElement((JTable) c);
                        add = true;
                    }

                    // Add element to document
                    if (add) {

                        // Apply filter - if present - to generated node
                        if (filterChain != null) {
                            // Process filter chain
                            for (XmlDataModelFilter f : filterChain) {
                                tmpElement = f.filter(tmpElement);
                            }
                        }

                        if (tmpElement != null) {
                            componentElement.appendChild(tmpElement);
                        }

                        add = false;

                    }

                }

            }

        }

        if (componentElement != null) {
            document.appendChild(componentElement);
        }

        return document;

    }
}

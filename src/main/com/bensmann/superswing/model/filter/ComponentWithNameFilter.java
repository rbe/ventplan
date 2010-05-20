/*
 * ComponentWithNameFilter.java
 *
 * Created on 13. August 2006, 22:09
 *
 */

package com.bensmann.superswing.model.filter;

import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This filter only allows 'component'-elements with a valued 'name'-attribute
 *
 * @author rb
 */
public class ComponentWithNameFilter implements XmlDataModelFilter {
    
    /**
     * Creates a new instance of ComponentWithNameFilter
     */
    public ComponentWithNameFilter() {
    }
    
    /**
     *
     * @param element
     * @return
     */
    private boolean isElementWithName(Element element) {
        
        boolean b = false;
        
        if (element.getAttribute("name").length() > 0) {
            b = true;
        }
        
        return b;
        
    }
    
    /**
     * 
     * @param node 
     * @return 
     */
    private List<String> checkForName(Node node) {
        
        List<String> list = new LinkedList<String>();
        Element element = (Element) node;
        NodeList children = element.getChildNodes();
        Element tmpElement = null;
        
        // Recursively check children for component elements
        // that have a name attribute containing a value
        for (int i = 0; i < children.getLength(); i++) {
            
            tmpElement = (Element) children.item(i);
            if (isElementWithName(tmpElement)) {
                
                list.add(tmpElement.getAttribute("name"));
                System.out.println("name="+tmpElement.getAttribute("name"));
                
                if (tmpElement.hasChildNodes()) {
                    list.addAll(checkForName(tmpElement));
                }
                
            }
            
        }
        
        return list;
        
    }
    
    /**
     *
     * @param node
     * @return
     */
    public Node filter(Node node) {
        
        List<String> list = checkForName(node);
        Element element = (Element) node;
        
        // Build up new element that consists of elements from List 'list'
        
        
        return element;
        
    }
    
}

/*
 * XmlDataModelFilter.java
 *
 * Created on 13. August 2006, 15:26
 *
 */

package com.bensmann.superswing.model.filter;

import org.w3c.dom.Node;

/**
 *
 * @author rb
 */
public interface XmlDataModelFilter {
    
    /**
     * Filter a node (mostly an Element) and returned filtered node.
     * Used to control XML output of ComponentToXml (e.g. customizing
     * for a certain customer's needs).
     */
    Node filter(Node node);
    
}

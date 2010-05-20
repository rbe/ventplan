/*
 * com/bensmann/superframe/xml/JAXPOutput.java
 *
 * JAXPOutput.java created on 16. Januar 2007, 11:16 by rb
 *
 * Copyright (C) 2006 Ralf Bensmann, java@bensmann.com
 *
 */

package com.bensmann.superframe.xml;

import java.io.OutputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 *
 * @author rb
 * @version 1.0
 */
public class JAXPOutput {
    
    /**
     * Do not create a new instance of JAXPOutput
     */
    private JAXPOutput() {
    }
    
    public static final void output(Document document, OutputStream outputStream) {
        
        // Output XML document
        Transformer transformer = null;
        try {
            
            transformer =
                    TransformerFactory.newInstance().
                    newTransformer();
            
            transformer.setOutputProperty("encoding", "ISO-8859-15");
            transformer.setOutputProperty("indent", "yes");
            
            transformer.transform(
                    new DOMSource(document), new StreamResult(outputStream));
            
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
}

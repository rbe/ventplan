/*
 * XmlDataModel.java
 *
 * Created on 26. Juli 2006, 14:53
 *
 */

package com.bensmann.superswing.model;

import com.bensmann.superswing.ApplHelper;
import com.bensmann.superswing.component.ShowErrorInternalFrame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * XmlDataModel bietet Laden und Speichern der Projekte an
 *
 * $Header$
 * @author rb
 * @version $Id$
 * @date $Date$
 * @log $Log$
 */
public class XmlDataModel {
    
    private static ApplHelper applHelper;
    
    private static Document document;
    
    private static Element projectElement;
    
    private static Transformer transformer;
    
    static {
        
        applHelper = ApplHelper.getInstance();
        
        try {
            
            transformer =
                    TransformerFactory.newInstance().
                    newTransformer();
            
            transformer.setOutputProperty("encoding", "ISO-8859-15");
            transformer.setOutputProperty("indent", "yes");
            
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Do not create a new instance of XmlDataModel
     */
    private XmlDataModel() {
    }
    
    /**
     * 
     * @param component 
     * @param file 
     * @param gzip 
     * @throws javax.xml.parsers.ParserConfigurationException 
     * @throws javax.xml.transform.TransformerException 
     */
    public static void save(JComponent component, File file, boolean gzip)
    throws ParserConfigurationException, TransformerException {
        
        Exception exception = null;
        ShowErrorInternalFrame showErrorInternalFrame = null;
        FileOutputStream fos = null;
        GZIPOutputStream gos = null;
        OutputStream os = null;
        
        document = new ComponentToXml().generateXml(component);
        
        // Create 'project'-element
        projectElement = document.createElement("project");
        
        // Check for Components that have a title
        if (component instanceof JInternalFrame) {
            
            // Titel des Projekts setzen <project>
            projectElement.setAttribute("title",
                    ((JInternalFrame) component).getTitle());
            
        }
        
        // Bisherigen Inhalt des Dokuments unter <project> setzen
        projectElement.appendChild(document.getDocumentElement());
        
        // Projekt-Element dem Document hinzufügen
        document.appendChild(projectElement);
        
//        OutputFormat o = new OutputFormat(document);
//        o.setIndent(1);
//        o.setLineSeparator("\n");
//        XMLSerializer s = new XMLSerializer(System.out, o);
//        try {
//            s.serialize(document);
//        } catch (IOException ex) {
//        }
        
        
        if (transformer != null) {
            
            try {
                
                fos = new FileOutputStream(file);
                
                if (gzip) {
                    gos = new GZIPOutputStream(fos);
                    os = gos;
                } else {
                    os = fos;
                }
                
                transformer.transform(
                        new DOMSource(document), new StreamResult(os));
                
                os.close();
                fos.close();
                
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (TransformerException ex) {
                ex.printStackTrace();
            }
            
        } else {
            // TODO Fehlermeldung: lege StackTrace in Liste ab
            // und gebe diese zurück
        }
        
    }
    
    /**
     * 
     * @param component 
     * @param file 
     * @throws javax.xml.parsers.ParserConfigurationException 
     * @throws javax.xml.transform.TransformerException 
     */
    public static void save(JComponent component, File file)
    throws ParserConfigurationException, TransformerException {
        save(component, file, true);
    }
    
    /**
     * 
     * 
     * @param wpxFile 
     * @param gzip 
     * @param component 
     * @throws javax.xml.parsers.ParserConfigurationException 
     * @throws java.net.URISyntaxException 
     * @throws org.xml.sax.SAXException 
     * @throws java.io.IOException 
     * @throws javax.xml.xpath.XPathExpressionException 
     */
    public static void load(JComponent component, File wpxFile, boolean gzip)
    throws ParserConfigurationException, URISyntaxException, SAXException,
            IOException, XPathExpressionException {
        
        Document document = null;
        FileInputStream fis = new FileInputStream(wpxFile);
        GZIPInputStream gis = null;
        InputStream is = null;
        
        if (gzip) {
            gis = new GZIPInputStream(fis);
            is = gis;
        } else {
            is = fis;
        }
        
        document =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().
                parse(is);
        
        try {
            fis.close();
            gis.close();
        } catch (Exception e) {
            // ignore
        }
        
        new XmlToComponent(document).injectValues(component);
        
    }
    
    /**
     * 
     * @param wpxFile 
     * @param component 
     * @throws javax.xml.parsers.ParserConfigurationException 
     * @throws java.net.URISyntaxException 
     * @throws org.xml.sax.SAXException 
     * @throws java.io.IOException 
     * @throws javax.xml.xpath.XPathExpressionException 
     */
    public static void load(JComponent component, File wpxFile)
    throws ParserConfigurationException, URISyntaxException, SAXException,
            IOException, XPathExpressionException {
        load(component, wpxFile, true);
    }
    
}

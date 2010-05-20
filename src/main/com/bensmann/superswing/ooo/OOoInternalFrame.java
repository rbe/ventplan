
/*
 * OOoInternalFrame.java
 *
 * Created on 8. August 2006, 22:50
 *
 */
package com.bensmann.superswing.ooo;

//~--- non-JDK imports --------------------------------------------------------

import com.bensmann.superswing.observer.ShutdownObservable;
import com.bensmann.superswing.observer.ShutdownObserver;
import com.sun.star.beans.Property;

//~--- JDK imports ------------------------------------------------------------

import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.comp.beans.NoConnectionException;
import com.sun.star.comp.beans.OOoBean;
import com.sun.star.comp.beans.OfficeDocument;
import com.sun.star.comp.beans.SystemWindowException;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XLayoutManager;
import com.sun.star.lang.DisposedException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.CloseVetoException;
import com.sun.star.util.XCloseable;

import java.awt.BorderLayout;

import java.io.IOException;
import java.io.InputStream;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

//~--- classes ----------------------------------------------------------------

/**
 *
 * @author rb
 */
public class OOoInternalFrame extends JInternalFrame
        implements ShutdownObserver {
    
    /**
     * List of resources that shall be (un)visible
     */
    private static String[] resourcesToDisableForReadOnlyView = {
        "private:resource/toolbar/alignmentbar",
        "private:resource/toolbar/arrowshapes",
        "private:resource/toolbar/basicshapes",
        "private:resource/toolbar/calloutshapes",
        "private:resource/toolbar/colorbar",
        "private:resource/toolbar/drawbar",
        "private:resource/toolbar/drawobjectbar",
        "private:resource/toolbar/extrusionobjectbar",
        "private:resource/toolbar/fontworkobjectbar",
        "private:resource/toolbar/fontworkshapetypes",
        "private:resource/toolbar/formatobjectbar",
        "private:resource/toolbar/formcontrols",
        "private:resource/toolbar/formdesign",
        "private:resource/toolbar/formsfilterbar",
        "private:resource/toolbar/formsnavigationbar",
        "private:resource/toolbar/formsobjectbar",
        "private:resource/toolbar/formtextobjectbar",
        "private:resource/toolbar/fullscreenbar",
        "private:resource/toolbar/graphicobjectbar",
        "private:resource/toolbar/insertbar",
        "private:resource/toolbar/insertcellsbar",
        "private:resource/toolbar/insertobjectbar",
        "private:resource/toolbar/mediaobjectbar",
        "private:resource/toolbar/moreformcontrols",
        "private:resource/toolbar/previewbar",
        "private:resource/toolbar/standardbar",
        "private:resource/toolbar/starshapes",
        "private:resource/toolbar/symbolshapes",
        "private:resource/toolbar/textobjectbar",
        "private:resource/toolbar/toolbar",
        
//      "private:resource/toolbar/viewerbar",
        "private:resource/menubar/menubar",
        
//      "private:resource/statusbar/statusbar"
    };
    private static String[] viewSettings = {
        "ShowAnnotations",
        "ShowBreaks",
        "ShowDrawings",
        "ShowFieldCommands",
        "ShowFootnoteBackground",
        "ShowHiddenParagraphs",
        "ShowHiddenText",
        "ShowRulers",
        "ShowIndexMarkBackground",
        "ShowParaBreaks",
        "ShowProtectedSpaces",
        "ShowSoftHyphens",
        "ShowSpaces",
        "ShowTableBoundaries",
        "ShowTabstops",
        "ShowTextBoundaries",
        "ShowTextFieldBackground"
    };
    
    //~--- fields -------------------------------------------------------------
    
    /**
     *
     */
    private OOoBean bean;
    
    /**
     * Remember if a resource was visible; used in hideResources()
     */
    private Map<String, Boolean> resourceVisibleState;
    private Map<String, Boolean> viewSettingsState;
    
    //~--- constructors -------------------------------------------------------
    
    /**
     * Creates a new instance of OOoInternalFrame
     */
    public OOoInternalFrame() {
        resourceVisibleState = new Hashtable<String, Boolean>();
        viewSettingsState    = new Hashtable<String, Boolean>();
        
        try {
            connectOOo();
            initComponents();
            setBorder(null);
        } catch (com.sun.star.uno.Exception ex) {
            ex.printStackTrace();
        }
    }
    
    //~--- methods ------------------------------------------------------------
    
    /**
     * Closes the bean viewer, leaves OOo running.
     */
    public void close() {
        setVisible(false);
//        restoreResourcesVisibleState();
        try {
            OfficeDocument oDoc = null;
            
            oDoc = bean.getDocument();
            if (oDoc != null){
                oDoc.setModified(false);
                XCloseable xCloseable = (XCloseable) UnoRuntime.queryInterface(XCloseable.class, oDoc);
                if (xCloseable != null){
                    try {
                        xCloseable.close(true);
                    } catch (CloseVetoException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        } catch (NoConnectionException ex) {
            ex.printStackTrace();
        }
        //bean.stopOOoConnection();
    }
    
    /**
     * Hide all resources to display a read-only document. Visible state of
     * all resources is saved to restore it later if appreciated.
     */
    public void hideResourcesForReadOnlyView() {
        XPropertySet   xPropSet       = null;
        XLayoutManager xLayoutManager = null;
        
        try {
            xPropSet =
                    (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    bean.getFrame());
            xLayoutManager = (XLayoutManager) UnoRuntime.queryInterface(
                    XLayoutManager.class,
                    xPropSet.getPropertyValue("LayoutManager"));
            
            for (String res : resourcesToDisableForReadOnlyView) {
                saveResourceVisibleState(res,
                        xLayoutManager.isElementVisible(res));
                xLayoutManager.hideElement(res);
            }
            
            // The statusbar should be shown
            xLayoutManager.showElement("private:resource/toolbar/viewerbar");
        } catch (NoConnectionException ex) {
            ex.printStackTrace();
        } catch (WrappedTargetException ex) {
            ex.printStackTrace();
        } catch (UnknownPropertyException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     *
     */
    private void initComponents() {
        addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                close();
            }
        });
        setLayout(new BorderLayout());
        add(bean, BorderLayout.CENTER);
        setResizable(true);
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setSize(640, 480);
        setOpaque(true);
//        try {
//            setMaximum(true);
//        } catch (java.beans.PropertyVetoException ex) {
//            ex.printStackTrace();
//        }
    }
    
    public void setVisible() {
        setVisible(true);
    }
    
    /**
     *
     * @param inputStream
     */
    public void loadDocumentFromStream(InputStream inputStream) {
        loadDocumentFromStream(inputStream, null);
    }
    
    /**
     *
     * @param inputStream
     * @param prop
     */
    public void loadDocumentFromStream(InputStream inputStream,
            PropertyValue[] prop) {
        try {
            bean.loadFromStream(inputStream, prop);
            bean.aquireSystemWindow();
            hideResourcesForReadOnlyView();
            setViewSettings(false);
        } catch (CloseVetoException ex) {
            ex.printStackTrace();
        } catch (com.sun.star.lang.IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NoConnectionException ex) {
            ex.printStackTrace();
        } catch (SystemWindowException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     *
     * @param url
     */
    public void loadDocumentFromURL(String url) {
        loadDocumentFromURL(url, null);
    }
    
    /**
     *
     * @param url
     * @param prop
     */
    public void loadDocumentFromURL(String url, PropertyValue[] prop) {
        try {
            bean.loadFromURL(url, prop);
            bean.aquireSystemWindow();
            hideResourcesForReadOnlyView();
            setViewSettings(false);
        } catch (CloseVetoException ex) {
            ex.printStackTrace();
        } catch (com.sun.star.lang.IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NoConnectionException ex) {
            ex.printStackTrace();
        } catch (SystemWindowException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     *
     * @param shutdownObservable
     */
    public void processShutdown(ShutdownObservable shutdownObservable) {
        close();
    }
    
    /**
     * Restore the visible state of all resources (to the state before we
     * modified it)
     */
    public void restoreResourcesVisibleState() {
        XPropertySet   xPropSet       = null;
        XLayoutManager xLayoutManager = null;
        
        try {
            xPropSet =
                    (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    bean.getFrame());
            xLayoutManager = (XLayoutManager) UnoRuntime.queryInterface(
                    XLayoutManager.class,
                    xPropSet.getPropertyValue("LayoutManager"));
            
            for (String res : resourcesToDisableForReadOnlyView) {
                if (wasResourceVisible(res)) {
                    xLayoutManager.showElement(res);
                } else {
                    xLayoutManager.hideElement(res);
                }
            }
        } catch (NoConnectionException ex) {
            ex.printStackTrace();
        } catch (WrappedTargetException ex) {
            ex.printStackTrace();
        } catch (UnknownPropertyException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Put boolean into a map that describes the visible state of a resource
     *
     * @param resourceName
     * @param visible
     */
    private void saveResourceVisibleState(String resourceName,
            boolean visible) {
        resourceVisibleState.put(resourceName, visible);
    }
    
    /**
     * Closes the bean viewer and tries to terminate OOo.
     */
    private void terminate() {
        XDesktop xDesktop = null;
        
        close();
        
        try {
            xDesktop = bean.getOOoDesktop();
            xDesktop.terminate();
        } catch (NoConnectionException e) {
            
            // ignore
        } catch (DisposedException e) {
            
            // ignore
        }
    }
    
    /**
     *
     * @param resourceName
     */
    private boolean wasResourceVisible(String resourceName) {
        return resourceVisibleState.get(resourceName);
    }
    
    //~--- get methods --------------------------------------------------------
    
    /**
     *
     * @return
     */
    public OfficeDocument getDocument() {
        OfficeDocument doc = null;
        
        try {
            doc = bean.getDocument();
        } catch (NoConnectionException ex) {
            ex.printStackTrace();
        }
        
        return doc;
    }
    
    //~--- set methods --------------------------------------------------------
    
    private void setViewSettings(Boolean set) {
        XPropertySet xViewSettings;
        int i = 0;
        
        try {
            xViewSettings =
                    (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, bean.getController());
            String viewSetting = null;
            
            XPropertySetInfo xPropSetInfo = xViewSettings.getPropertySetInfo();
            Property[] prop = xPropSetInfo.getProperties();
            System.out.format("proplen %d%n",prop.length);
            for (int j = 0; j < prop.length; j++) {
                System.out.format("propnam = %s%n",prop[j].Name);
            }
            for (i=0;i<viewSettings.length;i++) {
                if (set == true) {
                    xViewSettings.setPropertyValue(viewSettings[i], viewSettingsState.get(viewSettings[i]));
                } else {
                    
                }
            }
        } catch (NoConnectionException ex) {
            ex.printStackTrace();
        } catch (UnknownPropertyException ex) {
            System.err.println("Property = " + viewSettings[i]);
            ex.printStackTrace();
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        } catch (WrappedTargetException ex) {
            ex.printStackTrace();
        } catch (com.sun.star.lang.IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
    private void connectOOo() throws com.sun.star.uno.Exception{
        final String url = "uno:socket,host=0,port=2002;urp;StarOffice.ServiceManager";
        XComponentContext xRemoteContext;
        try {
            bean = new OOoBean();
            bean.startOOoConnection(url);
        } catch (NoConnectionException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
}

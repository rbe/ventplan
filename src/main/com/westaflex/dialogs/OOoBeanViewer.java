package com.westaflex.dialogs;

import com.seebass.tools.Tools;
import com.sun.star.beans.PropertyValue;
import com.sun.star.comp.beans.*;
import com.sun.star.util.CloseVetoException;
import com.westaflex.resource.Strings.Strings;
import com.westaflex.util.WestaWacApplHelper;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

public class OOoBeanViewer extends java.applet.Applet {
    private boolean standalone = false;
    private JDialog parent = null;
    private java.awt.Panel topPanel;
    private java.awt.Panel bottomPanel;
    private javax.swing.JButton closeButton;
    private JButton pdfButton;
    private JButton printButton;
    private javax.swing.JButton terminateButton;
    private JTextField documentURLTextField;
    
    private OOoBean aBean;
    
    
    
    public OOoBeanViewer(){
        super();
    }
    
    @Override
    public void init() {
        
        aBean = new OOoBean();
        aBean.setMenuBarVisible(false);
        topPanel = new java.awt.Panel();
        bottomPanel = new java.awt.Panel();
        pdfButton = new javax.swing.JButton("PDF-Dokument erzeugen");
        printButton = new javax.swing.JButton("Drucken");
        closeButton = new javax.swing.JButton("Schließen");
        terminateButton = new javax.swing.JButton("Terminieren");
        
        documentURLTextField = new javax.swing.JTextField();
        
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close();
            }
        });
        pdfButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OfficeDocument od = getDocument();
                File f = Tools.saveSingleFile(null, WestaWacApplHelper.getInstance().getPreference(WestaWacApplHelper.PROJECT_SAVE_DIRECTORY), "Portable Document Format (*.pdf)");
                
                if ((f != null) && (od != null)) {
                    com.sun.star.beans.PropertyValue args[] = new com.sun.star.beans.PropertyValue[1];
                    args[0] = new com.sun.star.beans.PropertyValue();
                    args[0].Name = "FilterName";
                    args[0].Value = "writer_pdf_Export";
                    try {
                        String filename = f.toURI() + (f.getAbsolutePath().endsWith(".pdf")?"":".pdf");
                        od.storeToURL(filename, args);
                    } catch (com.sun.star.io.IOException ex) {
                        Tools.msgbox(Strings.CANNOT_SAVE_FILE);
                        ex.printStackTrace();
                    }
                }
            }
        });
        
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OfficeDocument od = getDocument();
                
                if (od != null) {
                    try {
                        od.print(null);
                    } catch (com.sun.star.lang.IllegalArgumentException ex) {
                        ex.printStackTrace();
                        Tools.msgbox(Strings.CANNOT_PRINT);
                    }
                }
            }
        });
        
        terminateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                terminate();
            }
        });
        
        documentURLTextField.setEditable(false);
        documentURLTextField.setPreferredSize(new java.awt.Dimension(200, 30));
        
        topPanel.setLayout( new java.awt.GridLayout(1,10, 10, 0));
        topPanel.add(pdfButton, 0);
        topPanel.add(printButton, 1);
        topPanel.add(closeButton, 2);
        //topPanel.add(terminateButton, 3);
        
        //bottomPanel.setLayout( new java.awt.GridLayout(1,1) );
        bottomPanel.setLayout( new java.awt.BorderLayout() );
        bottomPanel.add(documentURLTextField);
        
        setLayout(new java.awt.BorderLayout());
        
        add(aBean, java.awt.BorderLayout.CENTER);
        add(topPanel, java.awt.BorderLayout.NORTH);
        add(bottomPanel, java.awt.BorderLayout.SOUTH);
    }
    
    public void setParent(JDialog parent) {
        this.parent = parent;
    }
    
    /**
     * Create a blank document of type <code>desc</code>
     *
     * @param url The private internal URL of the OpenOffice.org
     *            document describing the document
     * @param desc A description of the document to be created
     */
    public void createBlankDoc(String url, String desc) {
        //Create a blank document
        try {
            documentURLTextField.setText(desc);
            //Get the office process to load the URL
            aBean.loadFromURL( url, null );
            
            aBean.aquireSystemWindow();
        } catch ( com.sun.star.comp.beans.SystemWindowException aExc ) {
            System.err.println( "OOoBeanViewer.1:" );
            aExc.printStackTrace();
        } catch ( com.sun.star.comp.beans.NoConnectionException aExc ) {
            System.err.println( "OOoBeanViewer.2:" );
            aExc.printStackTrace();
        } catch ( Exception aExc ) {
            System.err.println( "OOoBeanViewer.3:" );
            aExc.printStackTrace();
            //return;
        }
    }
    public void loadDocumentFromStream(InputStream stream, PropertyValue[] prop, String desc) {
        
        documentURLTextField.setText(desc);
        try {
            aBean.loadFromStream(stream, prop);
            aBean.aquireSystemWindow();
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
    
    /** closes the bean viewer, leaves OOo running.
     */
    private void close() {
        setVisible(false);
        aBean.stopOOoConnection();
        stop();
        if (parent == null){
            System.exit(0);
        } else {
            parent.dispose();
        }
    }
    
    /** closes the bean viewer and tries to terminate OOo.
     */
    private void terminate() {
        setVisible(false);
        com.sun.star.frame.XDesktop xDesktop = null;
        try {
            xDesktop = aBean.getOOoDesktop();
        } catch ( com.sun.star.comp.beans.NoConnectionException aExc ) {} // ignore
        aBean.stopOOoConnection();
        stop();
        if ( xDesktop != null ){
            xDesktop.terminate();
        }
        if (parent == null){
            System.exit(0);
        } else {
            parent.dispose();
        }
    }
    
    /**
     * An ExitListener listening for windowClosing events
     */
    private class ExitListener extends java.awt.event.WindowAdapter {
        /**
         * windowClosed
         *
         * @param e A WindowEvent for a closed Window event
         */
        public void windowClosed( java.awt.event.WindowEvent e) {
            close();
        }
        
        /**
         * windowClosing for a closing window event
         *
         * @param e A WindowEvent for a closing window event
         */
        public void windowClosing( java.awt.event.WindowEvent e) {
            ((java.awt.Window)e.getSource()).dispose();
        }
    }
    
    public OfficeDocument getDocument() {
        
        OfficeDocument od = null;
        try {
            od = aBean.getDocument();
        } catch (NoConnectionException ex) {
            ex.printStackTrace();
        }
        return od;
    }
    public static void main(String args[]) {
        JDialog frame = new JDialog(new java.awt.Frame("OpenOffice.org Demo"), false);
        OOoBeanViewer aViewer = new OOoBeanViewer();
        
        frame.setLayout(new java.awt.BorderLayout());
        frame.addWindowListener( aViewer.new ExitListener() );
        aViewer.init();
        aViewer.start();
        frame.add(aViewer);
        frame.setLocation( 200, 200 );
        frame.setSize( 800, 480 );
        frame.setVisible(true);
        frame.setModal(true);
        aViewer.createBlankDoc("private:factory/swriter",
                "New text document");
        aViewer.standalone = true;
        
        
    }
    
}
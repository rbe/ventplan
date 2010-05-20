/*
 * BridgeConnection.java
 *
 * Created on 21. August 2007, 23:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.util;

/**
 *
 * @author os
 */
/*
 * BridgeConnection.java
 *
 * Created on 21. August 2007, 23:35
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import com.sun.star.connection.NoConnectException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.XComponentContext;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.beans.XPropertySet;
import com.sun.star.bridge.XBridgeFactory;
import com.sun.star.bridge.XBridge;
import com.sun.star.connection.XConnector;
import com.sun.star.connection.XConnection;

public class BridgeConnection {
    private XBridge bridge = null ;
    private XComponentContext xRemoteContext = null;
    private com.sun.star.frame.XComponentLoader officeComponentLoader;
    private  XMultiComponentFactory xRemoteServiceManager = null;
    private String con ;
    /**
     * main
     */
    public static void main(String[] args)
    throws Exception {
        BridgeConnection s = new BridgeConnection(args) ;
        for (int i = 0; i < 200; i++) {
            
            s.useConnection() ;
            // put your stuff here
            Thread.sleep(20);
            s.release();
        }
        
    }
    /* constructor */
    public BridgeConnection(String[] args) {
        if (args.length != 2) {
            System.out.println("Give me java sample host port");
            con = "socket,host=0,port=2022";
//                           System.exit(-1);
        } else
            con = "socket,host="+args[0]+",port="+args[1];
        
    }
    public BridgeConnection(){
        this(new String[] { "0", "2002" });
    }
    /**
     * open connection to openoffice
     */
    public  XComponentContext useConnection() throws NoConnectException, com.sun.star.uno.Exception {
        XComponentContext xOfficeComponentContext = null;
        try {
            XComponentContext _ctx =
                    com.sun.star.comp.helper.Bootstrap.createInitialComponentContext( null );
            
            xRemoteContext = _ctx ;
            
            Object x = xRemoteContext.getServiceManager().createInstanceWithContext(
                    "com.sun.star.connection.Connector", xRemoteContext );
            
            XConnector xConnector = (XConnector )
            UnoRuntime.queryInterface(XConnector.class, x);
            XConnection connection = xConnector.connect( con);
            if (connection == null){
                System.out.println("Connection is null");
            }
            x = xRemoteContext.getServiceManager().createInstanceWithContext(
                    "com.sun.star.bridge.BridgeFactory", xRemoteContext);
            XBridgeFactory xBridgeFactory = (XBridgeFactory) UnoRuntime.queryInterface(
                    XBridgeFactory.class , x );
            if (xBridgeFactory== null){
                System.out.println("bridge factoriy is null");
            }
            // this is the bridge that you will dispose
            bridge = xBridgeFactory.createBridge( "" , "urp", connection , null );
            
            System.out.println(String.format("bridgename \t= %s,\ndesc \t\t= %s", bridge.getDescription(), bridge.getName()));
            // get the remote instance
            x = bridge.getInstance( "StarOffice.ServiceManager");
            // Query the initial object for its main factory interface
            xRemoteServiceManager = ( XMultiComponentFactory )
            UnoRuntime.queryInterface( XMultiComponentFactory.class, x );
            // retrieve the component context (it's not yet exported from the office)
            // Query for the XPropertySet interface.
            XPropertySet xProperySet = ( XPropertySet )
            UnoRuntime.queryInterface( XPropertySet.class, xRemoteServiceManager );
            
            // Get the default context from the office server.
            Object oDefaultContext =
                    xProperySet.getPropertyValue( "DefaultContext" );
            
            // Query for the interface XComponentContext.
            xOfficeComponentContext =
                    ( XComponentContext ) UnoRuntime.queryInterface(
                    XComponentContext.class, oDefaultContext );
            // now create the desktop service
            // NOTE: use the office component context here !
            Object oDesktop = xRemoteServiceManager.createInstanceWithContext(
                    "com.sun.star.frame.Desktop", xOfficeComponentContext );
            
            officeComponentLoader = ( XComponentLoader )
            UnoRuntime.queryInterface( XComponentLoader.class, oDesktop );
            
            
            
            String available = (null !=officeComponentLoader ? "available" : "not available");
            System.out.println( "remote ServiceManager is " + available );
        } catch( com.sun.star.lang.DisposedException e ) { //works from Patch 1
            xRemoteContext = null;
            throw e;
        } catch( com.sun.star.uno.Exception e ) { //works from Patch 1
            xRemoteContext = null;
            throw e;
        } catch( java.lang.Exception e ){
            xRemoteContext = null;
        }
        return xOfficeComponentContext;
    }
    
    /**
     * when all the work is done, call release to dispose of the bridge
     * and your program can exit normaly, without forcing a System.exit
     */
    public void release()
    throws Exception {
        XComponent xcomponent =
                ( XComponent ) UnoRuntime.queryInterface( XComponent.class,
                bridge );
        // Closing the bridge
        xcomponent.dispose();
        
    }
}
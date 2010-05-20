/*
 * CallOOBasicMacro.java
 *
 * Created on 30. Mai 2007, 21:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.util;

import com.sun.star.awt.XDialogProvider;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.connection.NoConnectException;
import com.sun.star.frame.XDesktop;
import com.sun.star.script.provider.XScript;
import com.sun.star.script.provider.XScriptProvider;
import com.sun.star.script.provider.XScriptProviderFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Oliver
 */
public class OOoBridge implements com.sun.star.lang.XEventListener {

    public static final String LOCATION_APPLICATION = "application";
    public static final String LOCATION_DOCUMENT = "document";
    private final String scriptPrefix = "vnd.sun.star.script:";
    private XComponentContext xContext = null;
    private static OOoBridge oooBridge = null;
    private BridgeConnection bridgeConnection = null;
    private boolean isConnected;

    /** Creates a new instance of CallOOBasicMacro */
    private OOoBridge() {
    }

    public static OOoBridge getInstance() {
        if (oooBridge == null) {
            oooBridge = new OOoBridge();
        }
        return oooBridge;
    }

    public boolean connect() {
        xContext = getRemoteContext();
        return xContext != null;
    }

    public void close() throws Exception {

        if (bridgeConnection != null) {
            try {
                bridgeConnection.release();
            } catch (java.lang.Exception ex) {
                Logger.getLogger(OOoBridge.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (xContext != null) {
            try {
                System.out.println(new Date() + ": xContext!=null");
                XDesktop xDesktop = (XDesktop) UnoRuntime.queryInterface(XDesktop.class, getRemoteContext());
                if (xDesktop != null) {
                    xDesktop.terminate();
                }
                System.out.println(new Date() + ": terminated");
            } catch (com.sun.star.lang.DisposedException e) {
                e.printStackTrace();
            }
        }
    }

    public Object callBasicMacro(String macroPath, String location, Object[] params) {


        XScript myScript = null;
        Object oMSPFac = getRemoteServiceManager();
        Object result = null;
        try {
            oMSPFac = xContext.getServiceManager().createInstanceWithContext("com.sun.star.script.provider.MasterScriptProviderFactory", xContext);
            XScriptProviderFactory xScriptProviderFactory = (XScriptProviderFactory) UnoRuntime.queryInterface(XScriptProviderFactory.class, oMSPFac);
            Object oMSP = xScriptProviderFactory.createScriptProvider("");
            XScriptProvider xScriptProvider = (XScriptProvider) UnoRuntime.queryInterface(XScriptProvider.class, oMSP);
            myScript = xScriptProvider.getScript(scriptPrefix + macroPath + "?language=Basic&location=" + location);
            short[][] s = {{0, 0}, {0, 0}};
            Object b;
            b = null;
            Object[][] object0 = {{b, b}, {b, b}};
            result = myScript.invoke(params, s, object0);
        } catch (com.sun.star.uno.Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public Object createDialogFromLibrary(String url) {
        Object dlg = null;
        try {
            Object ob = xContext.getServiceManager().createInstanceWithContext("com.sun.star.awt.DialogProvider", xContext);
            XDialogProvider xDialogProvider = (XDialogProvider) UnoRuntime.queryInterface(XDialogProvider.class, ob);
            dlg = xDialogProvider.createDialog(url);
        } catch (Exception ex) {
            Logger.getLogger(OOoBridge.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dlg;
    }

    private Object getRemoteServiceManager() {
        return getRemoteContext().getServiceManager();
    }

    private XComponentContext getRemoteContext() {
//        if (xContext == null){
        if (bridgeConnection == null) {
//                xLocalContext = com.sun.star.comp.helper.Bootstrap.createInitialComponentContext(null);
//                XMultiComponentFactory xLocalServiceManager = xLocalContext.getServiceManager();
//                Object urlResolver  = xLocalServiceManager.createInstanceWithContext(
//                        "com.sun.star.bridge.UnoUrlResolver", xLocalContext );
//                XUnoUrlResolver xUnoUrlResolver = (XUnoUrlResolver) UnoRuntime.queryInterface(
//                        XUnoUrlResolver.class, urlResolver );
//                Object initialObject = xUnoUrlResolver.resolve(ooourl);
//                System.out.println("remote-context resolved!");
//                XPropertySet xPropertySet = (XPropertySet)UnoRuntime.queryInterface(
//                        XPropertySet.class, initialObject);
//                Object context = xPropertySet.getPropertyValue("DefaultContext");
//                xContext = (XComponentContext)UnoRuntime.queryInterface(
//                        XComponentContext.class, context);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
            bridgeConnection = new BridgeConnection(new String[]{"0", "2022"});
        }
        try {
            xContext = bridgeConnection.useConnection();
        } catch (NoConnectException ex) {
            System.out.println("No Connection to OOo-Server!\nMessage:\n\n" + ex.getMessage());
        } catch (com.sun.star.uno.Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                bridgeConnection = null;
                xContext = Bootstrap.bootstrap();
            } catch (BootstrapException bex) {
                bex.printStackTrace();
            }
        }
//        }
        isConnected = (xContext != null);
        return xContext;
    }

    @Override
    public void disposing(com.sun.star.lang.EventObject event) {
        // remote bridge has gone down, because the office crashed or was terminated.
        System.out.println("\n\nOO HAS BEEN DISPOSED!!!");
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
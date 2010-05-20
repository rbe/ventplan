/*
 * OOoBridgeSwingWorker.java
 *
 * Created on 24. August 2007, 19:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.westaflex.util;

import com.seebass.tools.Tools;
import com.westaflex.resource.Strings.Strings;
import javax.swing.SwingWorker;

/**
 *
 * @author os
 */
public class OOoBridgeSwingWorker extends SwingWorker<Object, Void> {

    private OOoBridge oooBridge;
    private String macroPath;
    private String location;
    private Object[] params;
    private Object myObj = null;

    public OOoBridgeSwingWorker() {

        oooBridge = OOoBridge.getInstance();
    }

    public void executeMacro(String macroPath, String location, Object[] params) {
        /** Creates a new instance of OOoBridgeSwingWorker */

        this.macroPath = macroPath;
        this.location = location;
        this.params = params;
        execute();
    }

    @Override
    public Object doInBackground() {

        WestaWacApplHelper.getInstance().setStatusText(Strings.CONNECTING_OOO, true);
        boolean connect = oooBridge.connect();
        if (connect == true) {
            WestaWacApplHelper.getInstance().setStatusText();
            oooBridge.callBasicMacro(macroPath, location, params);
        } else {
            Tools.errbox(Strings.CANNOT_CONNECT_TO_TEXTSERVER);
        }
        return myObj;
    }
}
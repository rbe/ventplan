/*
 * com/bensmann/superswing/component/SystemTrayUtil.java
 *
 * SystemTrayUtil.java created on 5. Januar 2007, 09:46 by rb
 *
 * Copyright (C) 2007 Ralf Bensmann, java@bensmann.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA
 *
 */

package com.bensmann.superswing.component.util;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
//import java.awt.SystemTray;
import java.awt.Toolkit;
//import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 *
 * @author rb
 * @version 1.0
 */
public class SystemTrayUtil {
    
    /**
     * Do not create a new instance of SystemTrayUtil
     */
    private SystemTrayUtil() {
    }
    
    /**
     * 
     * @param name 
     * @param imageURL 
     * @param popupMenu 
     * @throws java.awt.AWTException 
     */
    public static void setSystemTrayIcon(String name, URL imageURL, PopupMenu popupMenu)
    throws AWTException {
        
//        Image image = null;
//        TrayIcon trayIcon = null;
//        SystemTray tray = null;
//        
//        image = Toolkit.getDefaultToolkit().getImage(imageURL);
//        trayIcon = new TrayIcon(image, name, popupMenu);
//        trayIcon.setImageAutoSize(true);
//        
//        tray = SystemTray.getSystemTray();
//        tray.add(trayIcon);
        
    }
    
}

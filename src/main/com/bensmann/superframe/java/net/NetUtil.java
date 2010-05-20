/**
 * Created on Jan 20, 2003
 *
 */
package com.bensmann.superframe.java.net;

import com.bensmann.superframe.java.*;
import com.bensmann.superframe.java.lang.LangUtil;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author rb
 * @version $Id: NetUtil.java,v 1.1 2005/07/19 15:51:39 rb Exp $
 */
public class NetUtil {
    
    /**
     * Prueft einen uebergebenen String auf Name oder IP-Adresse und gibt
     * ein InetAddress-Objekt zurueck
     */
    public static InetAddress getInetAddress(String hostnameOrIpAdress)
    throws UnknownHostException {
        
        InetAddress host = InetAddress.getLocalHost();
        
        if (LangUtil.isCharAtLetter(hostnameOrIpAdress, 0)) {
            host = InetAddress.getByName(hostnameOrIpAdress);
        }
        else if (LangUtil.isCharAtNumber(hostnameOrIpAdress,
                hostnameOrIpAdress.length())) {
            host = InetAddress.getByAddress(hostnameOrIpAdress.getBytes());
        }
        
        return host;
        
    }
    
}

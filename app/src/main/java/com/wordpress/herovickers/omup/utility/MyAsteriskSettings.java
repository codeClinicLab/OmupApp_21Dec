package com.wordpress.herovickers.omup.utility;

import android.text.format.Formatter;
import android.util.Log;

//import org.asteriskjava.pbx.internal.asterisk.AsteriskSettings;
import org.asteriskjava.pbx.DefaultAsteriskSettings;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MyAsteriskSettings extends DefaultAsteriskSettings
{
String TAG="MyAsteriskSettings ";
    @Override
    public String getManagerPassword() {
        // this password MUST match the password (secret=) in manager.conf
        return "omupdeveloper";
    }

    @Override
    public String getManagerUsername() {
        // this MUST match the section header '[myconnection]' in manager.conf
        return "admin";
    }

    @Override
    public String getAsteriskIP() {
        // The IP address or FQDN of your Asterisk server.
       return "80.211.134.130";
   //  return "68.183.219.216";
    }

    @Override
    public String getAgiHost() {
        // The IP Address or FQDN of you asterisk-java application.
        return getLocalIpAddress();
//        return "1.1.1.1";
    }
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                   Log.i(TAG, "***** IP="+ ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
        Log.e(TAG, ex.toString());
        }
        return null;
    }

}
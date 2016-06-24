
package com.android.test;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
/**
 * Get W-LAN information
 * @author senthil
 *
 */
public class NetInfo {
    private ConnectivityManager connManager = null;

    private WifiManager wifiManager = null;

    private WifiInfo wifiInfo = null;

    private Context context = null;
    
    
    public NetInfo(Context context) {
        connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.context = context;
        initWIFIManager();

    }
    /**
     * Initialize the wifi manager
     */
    public void initWIFIManager() {
        
            wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            wifiInfo = wifiManager.getConnectionInfo();
            
            
            
        
    }
    /**
     * Return the status of the device internet connection
     * @return true device have the internet connection
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public int getCurrentNetworkType() {
    	int iConnType=0;
        if (null == connManager)
            return iConnType;

        NetworkInfo netinfo = connManager.getActiveNetworkInfo();
        if(netinfo != null){
        	iConnType= netinfo.getType();
        }
        return iConnType;
    }

    public boolean isNetworkOn() {
        // You can use this to determine whether you are connected:

        final ConnectivityManager cm = (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // Return true if connected, either in 3G or wi-fi
        final boolean connected = (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        return connected;
    }

    public String getWifiIpAddress() {
        if (null == wifiManager || null == wifiInfo)
            return "";

        int ipAddress = wifiInfo.getIpAddress();

        return String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    public String getWiFiMACAddress() {
        if (null == wifiManager )
            return "";

        return wifiInfo.getMacAddress();
    }
    /**
     * Returns the service set identifier (SSID) of the current 802.11 network. 
     * @return
     */
    public String getWiFiSSID() {
        if (null == wifiManager || null == wifiInfo)
            return "";

        return wifiInfo.getSSID();
    }
    /**
     * Returns the received signal strength indicator of the current 802.11 network.
     * @return
     */
    public int getWiFiRssi() {
        if (null == wifiManager || null == wifiInfo)
            return 0;

        return wifiInfo.getRssi();
    }
    /**
     * Return the basic service set identifier (BSSID) of the current access point. 
     * @return
     */
    public String getWiFiBSSID() {
        if (null == wifiManager || null == wifiInfo)
            return "";

        return wifiInfo.getBSSID();
    }
    /**
     * Returns the current link speed in Mbs 
     * @return
     */
    public String getLinkSpeed() {
        if (null == wifiManager || null == wifiInfo)
            return 0+"Mbs";

        return wifiInfo.getLinkSpeed()+"Mbs";
    }
    /**
     * 
     * @return
     */
    public boolean isWiFiEnabled(){
        boolean isEnabled=false;
        if (null != wifiManager ){
            isEnabled= wifiManager.isWifiEnabled();
            
        }
        return isEnabled;
    }
    public String getSupplicantDeatils(){
        SupplicantState ss= wifiInfo.getSupplicantState();
        DetailedState  detailedState=WifiInfo.getDetailedStateOf(ss);
        return detailedState.name();
    }
  
    public String getIPAddress() {
        String ipaddress = "";

        try {
            Enumeration<NetworkInterface> enumnet = NetworkInterface.getNetworkInterfaces();
            NetworkInterface netinterface = null;
            if(enumnet != null){
            while (enumnet.hasMoreElements()) {
                netinterface = enumnet.nextElement();

                for (Enumeration<InetAddress> enumip = netinterface.getInetAddresses(); enumip
                        .hasMoreElements();) {
                    InetAddress inetAddress = enumip.nextElement();

                    if (!inetAddress.isLoopbackAddress()) {
                        ipaddress = inetAddress.getHostAddress();

                        break;
                    }
                }
            }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return ipaddress;
    }
}

package com.example.test;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SuppressLint("NewApi")
@TargetApi(8)
public class MainActivity extends Activity {
    private static final int HOST_ADDRESS = 0x7f000001;// represent ip 127.0.0.1
    
    private Context mContext = null;
    
    private ConnectivityManager mCm;
    private WifiManager mWifiManager;
    private PackageManager mPackageManager;

    
    @SuppressLint("NewApi")
    @TargetApi(8)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("LIFE", " A oncreate");
        mContext= this;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mPackageManager = mContext.getPackageManager();
        mCm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);

                final android.net.NetworkInfo wifi =
                        mCm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                final android.net.NetworkInfo mobile =
                        mCm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if( wifi.isAvailable() && wifi.getDetailedState() == DetailedState.CONNECTED){
                    Toast.makeText(this, "Wifi" , Toast.LENGTH_LONG).show();
                }
               
                if( mobile.isAvailable() && mobile.getDetailedState() == DetailedState.CONNECTED ){
                    Toast.makeText(this, "Mobile 3G " , Toast.LENGTH_LONG).show();
                }
                else
                {   
                    Toast.makeText(this, "No Network " , Toast.LENGTH_LONG).show();
                }
                if(wifi != null){
                boolean isWifiConn = wifi.isConnected();
                Log.d(TAG_LOG, "Wifi connected: " + isWifiConn);
                }
                
                if(mobile != null){
                boolean isMobileConn = mobile.isConnected();
                Log.d(TAG_LOG, "Mobile connected: " + isMobileConn);
                }
                ContentResolver cr =mContext.getContentResolver();
                try {
                    int roaming=Settings.Secure.getInt(cr, Settings.Secure.DATA_ROAMING);
                    Log.d(TAG_LOG, "roaming: " + roaming);
                } catch (SettingNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if( isMobileDataEnabled(this)){
                    try {
                        testStartUsingNetworkFeature_enableHipri();
                    } catch (Exception e) {
                        
                        e.printStackTrace();
                    }

                    long txPackets = TrafficStats.getMobileTxPackets();
                    long rxPackets = TrafficStats.getMobileRxPackets();
                    long txBytes   = TrafficStats.getMobileTxBytes();
                    long rxBytes   = TrafficStats.getMobileRxBytes();
                    Log.d(TAG_LOG,"txPackets:"+txPackets);
                    Log.d(TAG_LOG,"rxPackets:"+rxPackets);
                    Log.d(TAG_LOG,"txBytes:"+txBytes);
                    Log.d(TAG_LOG,"rxBytes:"+rxBytes);
              //  forceMobileConnectionForAddress(this, "http://www.google.com");
                }
    }
    
    private boolean isMobileDataEnabled(Context context){
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class<?> cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        return mobileDataEnabled;
    }
    public static final String TAG_LOG="Test";
    /**
     * Enable mobile connection for a specific address
     * @param context a Context (application or activity)
     * @param address the address to enable
     * @return true for success, else false
     */
    private boolean forceMobileConnectionForAddress(Context context, String address) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connectivityManager) {
            Log.d(TAG_LOG, "ConnectivityManager is null, cannot try to force a mobile connection");
            return false;
        }
        connectivityManager.setNetworkPreference(ConnectivityManager.TYPE_MOBILE);
        //check if mobile connection is available and connected
        State state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE_HIPRI).getState();
        Log.d(TAG_LOG, "TYPE_MOBILE_HIPRI network state: " + state);
        if (0 == state.compareTo(State.CONNECTED) || 0 == state.compareTo(State.CONNECTING)) {
            return true;
        }

        //activate mobile connection in addition to other connection already activated
        int resultInt = connectivityManager.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE,"enableHIPRI");
        Log.d(TAG_LOG, "startUsingNetworkFeature for enableHIPRI result: " + resultInt);

        //-1 means errors
        // 0 means already enabled
        // 1 means enabled
        // other values can be returned, because this method is vendor specific
        if (-1 == resultInt) {
            Log.e(TAG_LOG, "Wrong result of startUsingNetworkFeature, maybe problems");
            return false;
        }
        if (0 == resultInt) {
            Log.d(TAG_LOG, "No need to perform additional network settings");
            return true;
        }

        //find the host name to route
        String hostName = extractAddressFromUrl(address);
        Log.d(TAG_LOG, "Source address: " + address);
        Log.d(TAG_LOG, "Destination host address to route: " + hostName);
        if (TextUtils.isEmpty(hostName)) hostName = address;

        //create a route for the specified address
        int hostAddress = lookupHost(hostName);
        if (-1 == hostAddress) {
            Log.e(TAG_LOG, "Wrong host address transformation, result was -1");
            return false;
        }
        //wait some time needed to connection manager for waking up
        try {
            for (int counter=0; counter<30; counter++) {
                State checkState = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE_HIPRI).getState();
                if (0 == checkState.compareTo(State.CONNECTED))
                    break;
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            //nothing to do
        }
        boolean resultBool = connectivityManager.requestRouteToHost(ConnectivityManager.TYPE_MOBILE_HIPRI, hostAddress);
        Log.d(TAG_LOG, "requestRouteToHost result: " + resultBool);
        if (!resultBool)
            Log.e(TAG_LOG, "Wrong requestRouteToHost result: expected true, but was false");

        NetworkInfo info[]= connectivityManager.getAllNetworkInfo();
        for (NetworkInfo networkInfo : info) {
            Log.d(TAG_LOG, "networkInfo result: " + networkInfo);
        }
        
        return resultBool;
    }
    @Override
    protected void onRestart() {
        
        super.onRestart();
        Log.i("LIFE", " A onRestart");
    }
    @Override
    protected void onStart() {
        
        super.onStart();
        Log.i("LIFE", " A onStart");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    protected void onPause() {
        
        super.onPause();
        Log.i("LIFE", " A onPause");
        
        
    }
    /**
     * This method extracts from address the hostname
     * @param url eg. http://some.where.com:8080/sync
     * @return some.where.com
     */
    public static String extractAddressFromUrl(String url) {
        String urlToProcess = null;

        //find protocol
        int protocolEndIndex = url.indexOf("://");
        if(protocolEndIndex>0) {
            urlToProcess = url.substring(protocolEndIndex + 3);
        } else {
            urlToProcess = url;
        }

        // If we have port number in the address we strip everything
        // after the port number
        int pos = urlToProcess.indexOf(':');
        if (pos >= 0) {
            urlToProcess = urlToProcess.substring(0, pos);
        }

        // If we have resource location in the address then we strip
        // everything after the '/'
        pos = urlToProcess.indexOf('/');
        if (pos >= 0) {
            urlToProcess = urlToProcess.substring(0, pos);
        }

        // If we have ? in the address then we strip
        // everything after the '?'
        pos = urlToProcess.indexOf('?');
        if (pos >= 0) {
            urlToProcess = urlToProcess.substring(0, pos);
        }
        return urlToProcess;
    }

    /**
     * Transform host name in int value used by {@link ConnectivityManager.requestRouteToHost}
     * method
     *
     * @param hostname
     * @return -1 if the host doesn't exists, elsewhere its translation
     * to an integer
     */
    private static int lookupHost(String hostname) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            return -1;
        }
        byte[] addrBytes;
        int addr;
        addrBytes = inetAddress.getAddress();
        addr = ((addrBytes[3] & 0xff) << 24)
                | ((addrBytes[2] & 0xff) << 16)
                | ((addrBytes[1] & 0xff) << 8 )
                |  (addrBytes[0] & 0xff);
        return addr;
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        Log.i("LIFE", " A onSaveInstanceState");
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("LIFE", " A onRestoreInstanceState");
    }
   MyReceiver mMyReceiver = new MyReceiver();
@Override
protected void onResume() {
    
    super.onResume();
    Log.i("LIFE", " A onResume");
    IntentFilter filter = new  IntentFilter();
    filter.addAction("com.trimble.Test");
    registerReceiver(mMyReceiver, filter);
} 
   
    @Override
    protected void onStop() {
        
        super.onStop();
        Log.i("LIFE", " A onStop");
    }
    boolean bSongA=true;
   public void onClicks(View view){
      final Intent intent = new Intent(this,B.class);
      String stSongA="http://agnicreation.com/A/Azhagiya%20Tamil%20Magan/Tamilmp3world.Com%20-%20Nee%20Marilyn%20Monroe.mp3";
      String stSongB="http://agnicreation.com/A/Azhagiya%20Tamil%20Magan/Tamilmp3world.Com%20-%20Nee%20Marilyn%20Monroe.mp3";
       
      
       final String stSong=bSongA?stSongA:stSongB;
      if(bSongA){
          bSongA=false;
      }else{
          bSongA=true;
      }
      
            intent.putExtra("song",stSong);
            startService(intent);
           
      
      
   }
   public void onClickr(View view){
       
       
       Intent intent = new Intent(this,B.class);
       stopService(intent);
       
    }
    @Override
    protected void onDestroy() {
        
        super.onDestroy();
        Log.i("LIFE", " A onDestroy");
        unregisterReceiver(mMyReceiver);
    }
    /** Test that hipri can be brought up when Wifi is enabled. */
    public void testStartUsingNetworkFeature_enableHipri() throws Exception {
        if (!mPackageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
                || !mPackageManager.hasSystemFeature(PackageManager.FEATURE_WIFI)) {
            // This test requires a mobile data connection and WiFi.
            return;
        }

        boolean isWifiConnected = mWifiManager.isWifiEnabled()
                && mWifiManager.getConnectionInfo().getSSID() != null;

        try {
            // Make sure WiFi is connected to an access point.
            if (!isWifiConnected) {
                connectToWifi();
            }

            // Register a receiver that will capture the connectivity change for hipri.
            ConnectivityActionReceiver receiver =
                    new ConnectivityActionReceiver(ConnectivityManager.TYPE_MOBILE_HIPRI);
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            mContext.registerReceiver(receiver, filter);

            // Try to start using the hipri feature...
            int result = mCm.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE,
                    "enableHIPRI");
            //"Couldn't start using the HIPRI feature.",
           boolean isSetTrue=  result != -1;

           NetworkInfo info[]= mCm.getAllNetworkInfo();
           for (NetworkInfo networkInfo : info) {
               Log.d(TAG_LOG, "networkInfo result: " + networkInfo);
           }
            // Check that the ConnectivityManager reported that it connected using hipri...
          //  assertTrue("Couldn't connect using hipri...",);
           isSetTrue = receiver.waitForConnection();
           // assertTrue("Couldn't requestRouteToHost using HIPRI.",                 );
           isSetTrue= mCm.requestRouteToHost(ConnectivityManager.TYPE_MOBILE_HIPRI, HOST_ADDRESS);
           onRoutedRequest(HTTP);
        } catch (InterruptedException e) {
            Log.e(TAG_LOG,"Broadcast receiver waiting for ConnectivityManager interrupted.");
        } finally {
            mCm.stopUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE,
                    "enableHIPRI");
            if (!isWifiConnected) {
                mWifiManager.setWifiEnabled(false);
            }
        }
    }
    private void connectToWifi() throws InterruptedException {
        ConnectivityActionReceiver receiver =
                new ConnectivityActionReceiver(ConnectivityManager.TYPE_WIFI);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(receiver, filter);

        boolean isSetTrue=(mWifiManager.setWifiEnabled(true));
        //"Wifi must be configured to connect to an access point for this test.";
        isSetTrue= receiver.waitForConnection();

        mContext.unregisterReceiver(receiver);
    }
    /** Receiver that captures the last connectivity change's network type and state. */
    private class ConnectivityActionReceiver extends BroadcastReceiver {

        private final CountDownLatch mReceiveLatch = new CountDownLatch(1);

        private final int mNetworkType;

        ConnectivityActionReceiver(int networkType) {
            mNetworkType = networkType;
        }

        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = intent.getExtras()
                    .getParcelable(ConnectivityManager.EXTRA_NETWORK_INFO);
            int networkType = networkInfo.getType();
            State networkState = networkInfo.getState();
            Log.i(TAG_LOG, "Network type: " + networkType + " state: " + networkState);
            if (networkType == mNetworkType && networkInfo.getState() == State.CONNECTED) {
                mReceiveLatch.countDown();
            }
        }

        public boolean waitForConnection() throws InterruptedException {
            return mReceiveLatch.await(30, TimeUnit.SECONDS);
        }
    }
    
   
    private final static int HTTP   = 2;

    private void onRoutedRequest(int type) {
        String url = "www.google.com";

        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(url);
        } catch (Exception e) {
            Log.e(TAG_LOG, "error fetching address for " + url);
            return;
        }

        mCm.requestRouteToHost(ConnectivityManager.TYPE_MOBILE_HIPRI, HOST_ADDRESS);

        switch (type) {
           
            case HTTP:
                HttpGet get = new HttpGet("http://agnicreation.com/A/Azhagiya%20Tamil%20Magan/Tamilmp3world.Com%20-%20Nee%20Marilyn%20Monroe.mp3");
                HttpClient client = new DefaultHttpClient();
                try {
                    HttpResponse httpResponse = client.execute(get);
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    if(inputStream != null)
                    {
                      /* if( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                           String stPath=Environment.getExternalStorageDirectory().getAbsolutePath();
                           StringBuffer buffer = new StringBuffer(stPath);
                           buffer.append(File.separator);
                           buffer.append("song.mp3");
                           FileOutputStream fileOutputStream = new FileOutputStream(buffer.toString());
                           int iData=-1;
                           try{
                           while((iData =inputStream.read()) != -1){
                               fileOutputStream.write(iData);
                           }
                           fileOutputStream.flush();
                           }finally{
                               if(fileOutputStream != null){
                                   fileOutputStream.close();
                               }
                           }
                       }*/
                        
                    }
                    Log.d(TAG_LOG, "routed http request gives " + httpResponse.getStatusLine());
                } catch (Exception e) {
                    Log.e(TAG_LOG, "routed http request exception = " + e);
                }
        }

    }
}

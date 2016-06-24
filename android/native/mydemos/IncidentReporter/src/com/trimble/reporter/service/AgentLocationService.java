/**
 * Copyright Trimble Inc., 2011 - 2012 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.reporter.service
 *
 * File name:
 *		AgentLocationService.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Dec 15, 2012 10:49:03 AM
 *
 * Abstract:
 *
 *
 * Environment:
 *	Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */



package com.trimble.reporter.service;


import com.trimble.reporter.R;
import com.trimble.reporter.TrackActivity;
import com.trimble.reporter.app.TCCApplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author sprabhu
 *
 */

public class AgentLocationService extends Service implements LocationListener {
    
    private LocationManager mLocationManager =null;
    
    private Context mContext =null;
    
    private TimerTask timerTask=null;
    
    private Timer timer=null;
    
    private static final int SCHDULER_TIME=1000*60; //1 min
    
    private double dLat=0.0;
    private double dLon=0.0;
    
    @Override
    public void onCreate() {
        super.onCreate();
        mLocationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,60 *1000, 200, this);
        createTask();
    }
    
    @Override
    public void onLocationChanged(Location location) {
      dLat=location.getLatitude();
      dLon=location.getLongitude();
      ((TCCApplication)getApplication()).setdLat(dLat, dLon);
    }

   
    @Override
    public void onProviderDisabled(String provider) {
      

    }

   
    @Override
    public void onProviderEnabled(String provider) {
      

    }

    
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      

    }

    
    @Override
    public IBinder onBind(Intent arg0) {
      
        return null;
    }
    
   /**
 * @param context the context to set
 */
public void setContext(Context context) {
    mContext = context;
}
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       
        return START_STICKY;
    }
    
    public void fireNotification() {
        String message;

        message = mContext.getString(R.string.notify_message);

        // Grab the notification manager to show the notification
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = message;

        // Grab the context to show the event for
        CharSequence contentTitle = mContext.getString(R.string.notify_title);
        CharSequence contentText = message;

        // Grab the intent to start the App
        PendingIntent contentIntent = getActivityPendingIntent(mContext);

        // This is the notification we will present
        Notification notification = new Notification(icon, tickerText,
                System.currentTimeMillis());

        // Set info for this event
        notification.setLatestEventInfo(mContext, contentTitle, contentText,
                contentIntent);

        // Setoff a sound...
        // notification.defaults |= Notification.FLAG_AUTO_CANCEL|
        // Notification.DEFAULT_LIGHTS;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        // notification.defaults |= Notification.DEFAULT_SOUND;

        long[] vibrate = { 0, 100, 200, 300 };
        notification.vibrate = vibrate;

        // Present the notification
        mNotificationManager.cancelAll();
        mNotificationManager.notify(Integer.MAX_VALUE, notification);
    }
    public PendingIntent getActivityPendingIntent(Context context) {
        //Seeting intent to fire when notifation tapped from the notification tray.
        Intent notificationIntent = new Intent(context, TrackActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        return contentIntent;
    }

    /* (non-Javadoc)
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        mLocationManager.removeUpdates(this);
        super.onDestroy();
        cancelTask();
    }

    private void createTask(){
        timer = new Timer();
        timerTask = new TimerTask() {
            
            @Override
            public void run() {
                
                ((TCCApplication)getApplication()).setdLat(dLat, dLon);
            }
        };
        timer.schedule(timerTask, SCHDULER_TIME);
    }
    
    private void cancelTask(){
        timer.cancel();
        timerTask.cancel();
    }
    
}

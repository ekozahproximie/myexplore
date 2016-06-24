/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 * 
 *
 * Module Name: com.trimble.scout
 *
 * File name: ScoutActivity.java
 *
 * Author: sprabhu
 *
 * Created On: Jun 14, 20146:39:10 PM
 *
 * Abstract:
 *
 *
 * Environment: Mobile Profile : Mobile Configuration :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.scout;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.glass.sample.compass.OrientationManager;
import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.login.NetWorkListenerImpl;
import com.trimble.agmantra.scout.acdc.ScoutACDCApi;

/**
 * @author sprabhu
 *
 */
public abstract class ScoutActivity extends Activity {

//  private static final String                USER_NAME       = "ag.cf.trimble@gmail.com";
//	private static final String                PASSWORD        = "agcf#2014";
  private static final String                USER_NAME       = "karthiga_murugan@trimble.com";
  private static final String                PASSWORD        = "c0nnectedf@rm";

   private static final String                LOG             = "Test";

   private transient FarmWorksContentProvider contentProvider =null;
   
   private transient ScoutACDCApi             mAcdc           = null;



   private NetWorkListenerImpl                networkLis      = null;

   private transient OrientationManager       mOrientationManager;
   
   private static final String DEV_MODE="dev_mode";

   @Override
   protected void onCreate(Bundle savedInstanceState) {

      contentProvider = FarmWorksContentProvider.getInstance(this);
      mAcdc = ScoutACDCApi.getInstance(this);
      networkLis = new NetWorkListenerImpl(this);
      SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
      LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

      mOrientationManager = new OrientationManager(sensorManager,
            locationManager);
      startTrackUserOrientation(true);
      if (mAcdc.islogOut()) {
         networkLis.doLogin(USER_NAME, PASSWORD, true);
      }

      if (!contentProvider.isTemeplateStored(ScoutActivity.this)) {
         Log.i(LOG, "FarmWorksContentProvider template is not stored");
         contentProvider.initAll();
      } else {
         Log.i(LOG, "FarmWorksContentProvider template is  stored");
      }
      super.onCreate(savedInstanceState);

      
   }

   protected Location getLocation(){
      Location userLocation = null;
      if (mOrientationManager != null && mOrientationManager.hasLocation()) {
         Log.d(LOG, "Fetching with user's location");
         userLocation = mOrientationManager.getLocation();
      } else

      {
         // TODO change the test location
         userLocation = new Location(LocationManager.GPS_PROVIDER);
         userLocation.setLongitude(-105.047040);
         userLocation.setLatitude(39.844854);
         userLocation.setTime(System.currentTimeMillis());
         userLocation.setElapsedRealtimeNanos(SystemClock
               .elapsedRealtimeNanos());

      }
      return userLocation;
   }
 
   @Override
   protected void onDestroy() {
      super.onDestroy();
      startTrackUserOrientation(false);
      mOrientationManager = null;
   }

   private void startTrackUserOrientation(final boolean isStart) {

      if (isStart) {
         mOrientationManager.addOnChangedListener(mCompassListener);
         mOrientationManager.start();

         if (mOrientationManager.hasLocation()) {
            Location location = mOrientationManager.getLocation();
         }
      } else {
         mOrientationManager.removeOnChangedListener(mCompassListener);
         mOrientationManager.stop();

      }
   }

   private final OrientationManager.OnChangedListener mCompassListener = 
         new OrientationManager.OnChangedListener() {

                 @Override
                 public void onOrientationChanged(
                       OrientationManager orientationManager) {
                    final float iUserHeading = orientationManager
                          .getHeading();

                 }

                 @Override
                 public void onLocationChanged(
                       OrientationManager orientationManager) {
                    final Location userLocation = orientationManager
                          .getLocation();

                 }

                 @Override
                 public void onAccuracyChanged(
                       OrientationManager orientationManager) {
                    final boolean mInterference = orientationManager
                          .hasInterference();

                 }
                                                                       };
                                                                       
                    public static void updateDevModePreference(final Context context){
                       final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                       final boolean isDevMode=prefs.getBoolean(DEV_MODE,Constants.IS_DEV_BUILD);
                       Log.i("DevMode", "get:"+isDevMode);
                       Constants.IS_DEV_BUILD=isDevMode;
                       
                   }
                   public static void modifyDevModePreference(final Context context){
                       final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                       Editor editor=prefs.edit();
                       Log.i("DevMode", "put:"+Constants.IS_DEV_BUILD);
                       editor.putBoolean(DEV_MODE, Constants.IS_DEV_BUILD);
                       editor.commit();
                       
                   }
                   public static boolean isAPP_DevMode(final Context context){
                      updateDevModePreference(context);
                      return Constants.IS_DEV_BUILD;
                   }

}

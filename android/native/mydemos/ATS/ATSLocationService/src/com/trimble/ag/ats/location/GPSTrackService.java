/**
 * Copyright Trimble Inc., 2015 - 2016 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Spime Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name: atsLocationService
 *
 * Module Name: com.trimble.ag.ats.location
 *
 * File name: GPSTrackService.java
 *
 * Author: sprabhu
 *
 * Created On: 28-Oct-2015 12:17:26 am
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
package com.trimble.ag.ats.location;

import android.app.Service;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.trimble.ag.ats.acdc.ACDCApi;
import com.trimble.ag.ats.db.LocationContent;
import com.trimble.ag.ats.db.ATSContentProvdier;
import com.trimble.ag.ats.R;

import java.util.ArrayList;

/**
 * @author sprabhu
 * 
 */
public class GPSTrackService extends Service implements LocationListener {

	private static final String TAG = GPSTrackService.class.getSimpleName();

	// flag for GPS status
	private transient boolean isGPSEnabled = false;

	// flag for network status
	private transient boolean isNetworkEnabled = false;

	// flag for GPS status
	private transient boolean isLocationRecordStarted = false;

	private final MyGPSBinder binder = new MyGPSBinder();

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10
	// meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1
	// minute

	// Declaring a Location Manager
	private static LocationManager locationManager;

	private LocationListener locationListener = null;

	private Location location = null;

	private transient ATSContentProvdier contentProvdier = null;

	private ACDCApi acdcApi = null;

	/**
	 * Content resolver, for performing database operations.
	 */
	private ContentResolver mContentResolver;

	/**
	 * @param name
	 */
	public GPSTrackService() {

	}

	public class MyGPSBinder extends Binder {

		/**
       * 
       */
		public MyGPSBinder() {

		}

		public GPSTrackService getParentServiceObject() {
			return GPSTrackService.this;
		}

	}

	public boolean isLocationRecordStarted() {
		return isLocationRecordStarted;

	}

	/**
	 * @param locationListener
	 *            the locationListener to set
	 */
	public void setLocationListener(LocationListener locationListener) {
		this.locationListener = locationListener;
	}

	@Override
   public void onLocationChanged(Location location) {
      if(location == null){
         return;
      }
      if(locationListener != null){
         locationListener.onLocationChanged(location);
      }
            
      ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
      // Add new items
      
          
          batch.add(ContentProviderOperation.newInsert(LocationContent.LocationEntry.CONTENT_URI)
                  .withValue(LocationContent.LocationEntry.COLUMN_NAME_LOCATION_ID, location.getTime())
                  .withValue(LocationContent.LocationEntry.COLUMN_NAME_LONGTITUDE, location.getLongitude())
                  .withValue(LocationContent.LocationEntry.COLUMN_NAME_LATITUDE, location.getLatitude())
                  .withValue(LocationContent.LocationEntry.COLUMN_NAME_HEADING, location.getBearing())
                  .withValue(LocationContent.LocationEntry.COLUMN_NAME_SPEED, location.getSpeed())
                  .withValue(LocationContent.LocationEntry.COLUMN_NAME_TIME, location.getTime())
                  .withValue(LocationContent.LocationEntry.COLUMN_NAME_IS_SYNCED, false)
                  .withValue(LocationContent.LocationEntry.COLUMN_NAME_ORGANIZATION_ID, acdcApi.getOrganizationID())
                  .withValue(LocationContent.LocationEntry.COLUMN_NAME_ALTITUDE, location.getAltitude())
                  .build());
          
      
      Log.i(TAG, "Merge solution ready. Applying batch update");
     
      try {
         mContentResolver.applyBatch(LocationContent.CONTENT_AUTHORITY, batch);
      } catch (RemoteException e) {
      
         e.printStackTrace();
      } catch (OperationApplicationException e) {
      
         e.printStackTrace();
      }
           // contentProvdier.insertLocation(new com.trimble.ag.ats.entity.Location(location));
            
      
   }

	@Override
	public void onCreate() {
		contentProvdier = ATSContentProvdier
				.getInstance(getApplicationContext());
		mContentResolver = getContentResolver();
		acdcApi = ACDCApi.getInstance(getApplicationContext());
		Toast.makeText(getApplicationContext(), R.string.start,
				Toast.LENGTH_SHORT).show();
		super.onCreate();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return START_STICKY;
	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.isLocationRecordStarted = true;
				// First get location from Network Provider
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d(TAG, "Network provider Enabled");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d(TAG, "GPS provider Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);

						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopUsingGPS();
		Toast.makeText(getApplicationContext(), R.string.stop,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {

		return binder;
	}

}

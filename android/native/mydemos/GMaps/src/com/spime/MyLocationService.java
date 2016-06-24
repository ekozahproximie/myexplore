package com.spime;



import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyLocationService extends Service {
public String myProxmityLocation=null;
	LocationListener myLocationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			if (location != null) {
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				
				System.out.println("New Location is:" + lat + "," + lng);

			}
		}
	};

	private void setProximityAlert() {

		locationManager = (LocationManager) getSystemService(context);
		double lat = Double.parseDouble(myProxmityLocation.substring(0,myProxmityLocation.indexOf(",")));
		double lng = Double.parseDouble(myProxmityLocation.substring(myProxmityLocation.indexOf(",")+1));;
		float radius = 100f; // meters
		long expiration = -1; // do not expire
		Intent intent = new Intent(ProximityIntentReceiver.TREASURE_PROXIMITY_ALERT);
		 proximityIntent = PendingIntent.getBroadcast(this, -1,
				intent, 0);
		locationManager.addProximityAlert(lat, lng, radius, expiration,
				proximityIntent);
	}
	PendingIntent proximityIntent =null;
	LocationManager locationManager = null;
	String context = null;
	Criteria criteria = null;
	String provider = null;

	public void onCreate() {
		super.onCreate();

		context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getSystemService(context);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);

		criteria.setPowerRequirement(Criteria.POWER_LOW);
		provider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(provider);
		
		locationManager.requestLocationUpdates(provider, 100, 10,
				myLocationListener);
		updateWithNewLocation(location);
		Toast.makeText(this, "Service created ...", Toast.LENGTH_LONG).show();
	}

	private void updateWithNewLocation(Location location) {
		String latLongString;
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			latLongString = "Lat:" + lat + "\nLong:" + lng;
			
		} else {
			latLongString = "No location found";
		}
		Toast.makeText(getApplicationContext(), "Your Current Position is:\n"
				+ latLongString, Toast.LENGTH_SHORT);
	}

	public void onStart(Intent intent, int startid) {
		 Bundle bundle=intent.getExtras();
		 myProxmityLocation=bundle.getString("myAlert");
		Toast.makeText(this, "My Service Started"+myProxmityLocation, Toast.LENGTH_SHORT).show();
		setProximityAlert();
	}
//	@Override    public int onStartCommand(Intent intent, int flags, int startId) {  
//		Log.i("My location Service", "Received start id " + startId + ": " + intent);    
//		// We want this service to continue running until it is explicitly       
//		// stopped, so return sticky.        
//		return START_STICKY;   
//		
//	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onDestroy() {
		super.onDestroy();
		locationManager.removeUpdates(myLocationListener);
		locationManager.removeProximityAlert(proximityIntent);
		Toast.makeText(this, "Service destroyed ...", Toast.LENGTH_LONG).show();
	}

}

package com.spime.friendfinder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts.People;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class FriendFinderMap extends MapActivity {

	// ===========================================================
	// Fields
	// ===========================================================

	private static final String MY_LOCATION_CHANGED_ACTION = new String(
			"android.intent.action.LOCATION_CHANGED");

	/**
	 * Minimum distance in meters for a friend to be recognize as a Friend to be
	 * drawn
	 */
	protected static final int NEARFRIEND_MAX_DISTANCE = 10000000; // 10.000km

	final Handler mHandler = new Handler();

	/*
	 * Stuff we need to save as fields, because we want to use multiple of them
	 * in different methods.
	 */
	protected boolean doUpdates = true;
	protected MapView myMapView = null;
	protected boolean isSatelliteMapView = false;
	protected MapController myMapController = null;
	protected Overlay myOverlayController = null;
	private String stProviderName = "gps";
	private GeoPoint myGeoPoint = null;
	protected LocationManager myLocationManager = null;
	protected Location myLocation = null;
	protected MyIntentReceiver myIntentReceiver = null;
	protected final IntentFilter myIntentFilter = new IntentFilter(
			MY_LOCATION_CHANGED_ACTION);

	/** List of friends in */
	protected ArrayList<Friend> nearFriends = new ArrayList<Friend>();

	// ===========================================================
	// Extra-Classes
	// ===========================================================

	/**
	 * This tiny IntentReceiver updates our stuff as we receive the intents
	 * (LOCATION_CHANGED_ACTION) we told the myLocationManager to send to us.
	 */
	class MyIntentReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (doUpdates)
				updateView(null);

		}
	}

	/**
	 * This method is so huge, because it does a lot of FANCY painting. We could
	 * shorten this method to a few lines. But as users like eye-candy apps ;)
	 * ...
	 */
	protected class MyLocationOverlay extends Overlay {
		@Override
		public boolean draw(Canvas canvas, MapView calculator, boolean shadow,
				long when) {
			super.draw(canvas, calculator, shadow);
			// Setup our "brush"/"pencil"/ whatever...
			Paint paint = new Paint();
			paint.setTextSize(14);

			// Create a Point that represents our GPS-Location
			Double lat = myLocation.getLatitude() * 1E6;
			Double lng = myLocation.getLongitude() * 1E6;
			GeoPoint geoPoint = new GeoPoint(lat.intValue(), lng.intValue());
			Point out = new Point();
			int[] myScreenCoords = new int[2];
			// Converts lat/lng-Point to OUR coordinates on the screen.
			calculator.getProjection().toPixels(geoPoint, out);
			myScreenCoords[0] = out.x;
			myScreenCoords[1] = out.y;

			// Draw a circle for our location
			RectF oval = new RectF(myScreenCoords[0] - 7,
					myScreenCoords[1] + 7, myScreenCoords[0] + 7,
					myScreenCoords[1] - 7);

			// Setup a color for our location
			paint.setStyle(Style.FILL);
			paint.setARGB(255, 80, 150, 30); // Nice strong Android-Green
			// Draw our name
			canvas.drawText(getString(R.string.map_overlay_own_name),
					myScreenCoords[0] + 9, myScreenCoords[1], paint);

			// Change the paint to a 'Lookthrough' Android-Green
			paint.setARGB(80, 156, 192, 36);
			paint.setStrokeWidth(1);
			// draw an oval around our location
			canvas.drawOval(oval, paint);

			// With a black stroke around the oval we drew before.
			paint.setARGB(255, 0, 0, 0);
			paint.setStyle(Style.STROKE);
			canvas.drawCircle(myScreenCoords[0], myScreenCoords[1], 7, paint);
			myGeoPoint = geoPoint;
			myMapController.setCenter(myGeoPoint);
			myMapController.animateTo(myGeoPoint);
			int[] friendScreenCoords = new int[2];
			// Draw each friend with a line pointing to our own location.
			for (Friend aFriend : nearFriends) {
				lat = aFriend.itsLocation.getLatitude() * 1E6;
				lng = aFriend.itsLocation.getLongitude() * 1E6;
				geoPoint = new GeoPoint(lat.intValue(), lng.intValue());
				out = new Point();
				// Converts lat/lng-Point to coordinates on the screen.
				calculator.getProjection().toPixels(geoPoint, out);
				friendScreenCoords[0] = out.x;
				friendScreenCoords[1] = out.y;
				if (Math.abs(friendScreenCoords[0]) < 2000
						&& Math.abs(friendScreenCoords[1]) < 2000) {
					// Draw a circle for this friend and his name
					oval = new RectF(friendScreenCoords[0] - 7,
							friendScreenCoords[1] + 7,
							friendScreenCoords[0] + 7,
							friendScreenCoords[1] - 7);

					// Setup a color for all friends
					paint.setStyle(Style.FILL);
					paint.setARGB(255, 255, 0, 0); // Nice red
					canvas.drawText(aFriend.itsName, friendScreenCoords[0] + 9,
							friendScreenCoords[1], paint);

					// Draw a line connecting us to the current Friend
					paint.setARGB(80, 255, 0, 0); // Nice red, more look
													// through...

					paint.setStrokeWidth(2);
					canvas.drawLine(myScreenCoords[0], myScreenCoords[1],
							friendScreenCoords[0], friendScreenCoords[1], paint);
					paint.setStrokeWidth(1);
					// draw an oval around our friends location
					canvas.drawOval(oval, paint);

					// With a black stroke around the oval we drew before.
					paint.setARGB(255, 0, 0, 0);
					paint.setStyle(Style.STROKE);
					canvas.drawCircle(friendScreenCoords[0],
							friendScreenCoords[1], 7, paint);
				}
			}
			return true;
		}
	}

	// ===========================================================
	// """Constructor""" (or the Entry-Point of this class)
	// ===========================================================
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		/* Create a new MapView and show it */
		myMapView = new MapView(this, "0abATGTiSFFGGWOD2osNnlUQUphe1W8oDHEysjw");

		setContentView(myMapView);
		/*
		 * MapController is capable of zooming and animating and stuff like that
		 */
		myMapController = myMapView.getController();

		/*
		 * With these objects we are capable of drawing graphical stuff on top
		 * of the map
		 */

		MyLocationOverlay myLocationOverlay = new MyLocationOverlay();
		List<Overlay> listOfOverlays = myMapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(myLocationOverlay);
		myMapController.setZoom(2); // Far out

		// Initialize the LocationManager
		myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		updateView(null);

		/*
		 * Prepare the things, that will give us the ability, to receive
		 * Information about our GPS-Position.
		 */
		setupForGPSAutoRefreshing();

		/*
		 * Update the list of our friends once on the start, as they are
		 * not(yet) moving, no updates to them are necessary
		 */
		refreshFriendsList(NEARFRIEND_MAX_DISTANCE);
	}

	/**
	 * Restart the receiving, when we are back on line.
	 */
	@Override
	public void onResume() {
		super.onResume();
		doUpdates = true;

		/*
		 * As we only want to react on the LOCATION_CHANGED intents we made the
		 * OS send out, we have to register it along with a filter, that will
		 * only "pass through" on LOCATION_CHANGED-Intents.
		 */
		registerReceiver(myIntentReceiver, myIntentFilter);
	}

	/**
	 * Make sure to stop the animation when we're no longer on screen, failing
	 * to do so will cause a lot of unnecessary cpu-usage!
	 */
	@Override
	public void onPause() {
		doUpdates = false;
		unregisterReceiver(myIntentReceiver);
		super.onPause();
	}

	// ===========================================================
	// Overridden methods
	// ===========================================================

	// Called only the first time the options menu is displayed.
	// Create the menu entries.
	// Menu adds items in the order shown.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean supRetVal = super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, getString(R.string.map_menu_zoom_in));
		menu.add(0, 1, 0, getString(R.string.map_menu_zoom_out));
		menu.add(0, 2, 0, getString(R.string.map_menu_toggle_street_satellite));
		menu.add(0, 3, 0, getString(R.string.map_menu_back_to_list));
		return supRetVal;
	}

	private void setCenter() {
		myMapController.setCenter(myGeoPoint);
		myMapController.animateTo(myGeoPoint);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0: {
			// Zoom not closer than possible
			int iZoomLevel = myMapView.getZoomLevel() + 1;
			myMapController.setZoom(Math.min(21, iZoomLevel));
			setCenter();
			return true;
		}
		case 1:
			// Zoom not farer than possible
			int iZoomLevel = myMapView.getZoomLevel() - 1;
			myMapController.setZoom(Math.max(1, iZoomLevel));
			setCenter();
			return true;
		case 2:
			// Switch to satellite view
			isSatelliteMapView = !isSatelliteMapView;
			myMapView.setSatellite(isSatelliteMapView);
			return true;
		case 3:
			finish();
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_I) {
			// Zoom not closer than possible
			int iZoomLevel = myMapView.getZoomLevel() + 1;
			myMapController.setZoom(Math.min(21, iZoomLevel));
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_O) {
			// Zoom not farer than possible
			int iZoomLevel = myMapView.getZoomLevel() - 1;
			myMapController.setZoom(Math.max(1, iZoomLevel));
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_T) {
			// Switch to satellite view
			isSatelliteMapView = !isSatelliteMapView;
			myMapView.setSatellite(isSatelliteMapView);
			return true;
		}
		return false;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void setupForGPSAutoRefreshing() {
		/*
		 * Register with out LocationManager to send us an intent (whos
		 * Action-String we defined above) when an intent to the location
		 * manager, that we want to get informed on changes to our own position.
		 * This is one of the hottest features in Android.
		 */
		final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 25; // in Meters
		final long MINIMUM_TIME_BETWEEN_UPDATE = 5000; // in Milliseconds
		// Get the first provider available
		List<String> providers = myLocationManager.getAllProviders();
		stProviderName = providers.get(0);
		LocationProvider provider = myLocationManager
				.getProvider(stProviderName);

		myLocationManager.requestLocationUpdates(provider.getName(),
				MINIMUM_TIME_BETWEEN_UPDATE, MINIMUM_DISTANCECHANGE_FOR_UPDATE,
				new LocationListener() {

					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
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

						updateView(location);
					}
				});

		/*
		 * Create an IntentReceiver, that will react on the Intents we made our
		 * LocationManager to send to us.
		 */
		myIntentReceiver = new MyIntentReceiver();

		/*
		 * In onResume() the following method will be called automatically!
		 * registerReceiver(myIntentReceiver, myIntentFilter);
		 */
	}

	private void updateView(Location location) {
		// Refresh our gps-location
		myLocation = myLocationManager.getLastKnownLocation(stProviderName);
		if (location != null) {
			myLocation = location;
		}

		/*
		 * Redraws the mapViee, which also makes our OverlayController redraw
		 * our Circles and Lines
		 */
		myMapView.invalidate();

		/*
		 * As the location of our Friends is static and for performance-reasons,
		 * we do not call this
		 */
		// refreshFriendsList(NEARFRIEND_MAX_DISTANCE);
	}

	private void refreshFriendsList(long maxDistanceInMeter) {
		Cursor c = getContentResolver().query(People.CONTENT_URI, null, null,
				null, People.NAME + " ASC");
		startManagingCursor(c);

		int notesColumn = c.getColumnIndex(People.NOTES);
		int nameColumn = c.getColumnIndex(People.NAME);
		c.moveToFirst();
		// Moves the cursor to the first row
		// and returns true if there is sth. to get
		if (c.isFirst()) {
			do {
				String notesString = c.getString(notesColumn);

				Location friendLocation = null;
				if (notesString != null) {
					// Pattern for extracting geo-ContentURIs from the notes.
					final String geoPattern = "geo:[\\-]?[0-9]{1,3}\\.[0-9]{1,6}\\,[\\-]?[0-9]{1,3}\\.[0-9]{1,6}\\#";
					// Compile and use regular expression
					Pattern pattern = Pattern.compile(geoPattern);

					CharSequence inputStr = notesString;
					Matcher matcher = pattern.matcher(inputStr);

					boolean matchFound = matcher.find();
					if (matchFound) {
						// We take the first match available
						String groupStr = matcher.group(0);
						// And parse the Lat/Long-GeoPos-Values from it
						friendLocation = new Location("My location");
						String latid = groupStr.substring(
								groupStr.indexOf(":") + 1,
								groupStr.indexOf(","));
						String longit = groupStr.substring(
								groupStr.indexOf(",") + 1,
								groupStr.indexOf("#"));
						friendLocation.setLongitude(Float.parseFloat(longit));
						friendLocation.setLatitude(Float.parseFloat(latid));
						if (friendLocation != null
								&& myLocation.distanceTo(friendLocation) < maxDistanceInMeter) {
							String friendName = c.getString(nameColumn);
							nearFriends.add(new Friend(friendLocation,
									friendName));
						}
					}
				}

			} while (c.moveToNext());
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}

package com.spime;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class EarthquakeMap extends MapActivity {
	Cursor earthquakeCursor;
	NotificationManager notificationManager;
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		String svcName = Context.NOTIFICATION_SERVICE;
		notificationManager = (NotificationManager)getSystemService(svcName);
		setContentView(R.layout.earthquake_map);
		Uri earthquakeURI = EarthquakeProvider.CONTENT_URI;
		earthquakeCursor = getContentResolver().query(earthquakeURI, null,
				null, null, null);
		MapView earthquakeMap = (MapView) findViewById(R.id.map_view);
		EarthquakeOverlay eo = new EarthquakeOverlay(earthquakeCursor);
		earthquakeMap.getOverlays().add(eo);
		LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.zoom);
		View zoomView = earthquakeMap.getZoomControls();

		zoomLayout.addView(zoomView, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		earthquakeMap.displayZoomControls(true);
		MapController mc = earthquakeMap.getController();
		String coordinates[] = { "1.352566007", "103.78921587" };
		double lat = Double.parseDouble(coordinates[0]);
		double lng = Double.parseDouble(coordinates[1]);

		GeoPoint p = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));

		mc.animateTo(p);
		mc.setZoom(17);
		earthquakeMap.invalidate();

	}

	@Override
	public void onDestroy() {
		earthquakeCursor.close();
		super.onDestroy();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public class EarthquakeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			notificationManager.cancel(EarthquakeService.NOTIFICATION_ID);
			earthquakeCursor.requery();
			MapView earthquakeMap = (MapView) findViewById(R.id.map_view);
			earthquakeMap.invalidate();
		}
	}

	EarthquakeReceiver receiver;

	@Override
	public void onResume() {
		notificationManager.cancel(EarthquakeService.NOTIFICATION_ID);
		earthquakeCursor.requery();
		IntentFilter filter;
		filter = new IntentFilter(EarthquakeService.NEW_EARTHQUAKE_FOUND);
		receiver = new EarthquakeReceiver();
		registerReceiver(receiver, filter);
		super.onResume();
	}

	@Override
	public void onPause() {
		unregisterReceiver(receiver);
		earthquakeCursor.deactivate();
		super.onPause();
	}

}

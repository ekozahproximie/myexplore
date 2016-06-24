package com.spime;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.Overlay;

public class GMaps extends MapActivity implements LocationListener  {
	MapView mapView = null;
	MapController mc;
	GeoPoint p;
	public boolean isLocationFound=false;
	public boolean isNewAlertNeed=false; 
	public double lat=0;
	public double lon=0;
	
	/* This method is called when use position will get changed */
	public void onLocationChanged(Location location) {
		if (location != null) {
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		isLocationFound=true;
		p = new GeoPoint((int)(long) (lat * 1E6), (int)(long)( lng * 1E6));
		System.out.println(p.getLongitudeE6()/1E6+","+p.getLatitudeE6()/1E6);
		mc.animateTo(p);
		}
		
		

	}

	public void onProviderDisabled(String provider) {
	}
	public void onProviderEnabled(String provider) {
	}
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	class MapOverlay extends com.google.android.maps.Overlay {
		int cX=0;
		int cY=0;
		int r=15;
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

			// ---translate the GeoPoint to screen pixels---
			Point screenPts = new Point();
			mapView.getProjection().toPixels(p, screenPts);
			
			if(isNewAlertNeed){
			Paint linePaint = new Paint(); 
		    linePaint.setARGB(255, 255, 0, 0); 
		    linePaint.setStrokeWidth(3); 
		    linePaint.setDither(true); 
		    linePaint.setStyle(Style.STROKE); 
		    linePaint.setAntiAlias(true); 
		    linePaint.setStrokeJoin(Paint.Join.ROUND); 
		    linePaint.setStrokeCap(Paint.Cap.ROUND);
			canvas.drawCircle(cX, cY, 30, linePaint);
			GeoPoint p = mapView.getProjection().fromPixels(
                     cX,
                     cY);
			lon=p.getLongitudeE6()/1E6;
			lat=p.getLatitudeE6()/1E6;
			}
			
		if(isLocationFound){
			// ---add the marker---
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.pin);
			canvas.drawBitmap(bmp, screenPts.x, screenPts.y - 48, null);
			
		}
			return true;
		}
		 @Override
		public boolean onTrackballEvent(MotionEvent event, MapView mapView) {
			// TODO Auto-generated method stub
			 Log.v("Info",event.toString());
			return super.onTrackballEvent(event, mapView);
			
		}

		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			// ---when user lifts his finger---
						
	if (event.getAction() == 1 ) {
				try{  
				GeoPoint p = mapView.getProjection().fromPixels(
		                    (int) event.getX(),
		                    (int) event.getY());
				
		                    Toast.makeText(getBaseContext(), 
		                        p.getLatitudeE6() / 1E6 + "," + 
		                        p.getLongitudeE6() /1E6 , 
		                        Toast.LENGTH_SHORT).show();
		                    cX=(int) event.getX();
		                    cY=(int) event.getY();
		                    mapView.refreshDrawableState();
				} catch (Exception e) {
					e.printStackTrace();
					Log.v("title", ""+e.getLocalizedMessage());
				}
				return true;
			} else
				return false;
		}
	}

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		LocationManager locationManager;
		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager)getSystemService(context);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);	
		
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(provider, 2000, 10,
				this);
//		
//		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, this);


		mapView = (MapView) findViewById(R.id.mapView);
		LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.zoom);
		View zoomView = mapView.getZoomControls();

		zoomLayout. addView(zoomView, new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	final Button btnclickme=(Button)findViewById(R.id.map);
		btnclickme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String stStaus=(String) btnclickme.getText();
            	if(stStaus.equals("Street")){
            	btnclickme.setText("Satellite");
            	mapView.setSatellite(true);
            	mapView.setStreetView(false);
            	mapView.refreshDrawableState();
            	}else if(stStaus.equals("Satellite")){
            		btnclickme.setText("Street");
            		mapView.setSatellite(false);
            		mapView.setStreetView(true);
            		mapView.refreshDrawableState();
            	}
            }
        });               
		mapView.displayZoomControls(true);
		mc = mapView.getController();
		String coordinates[] = { "12.996541", "80.2520051" };
		double lat = Double.parseDouble(coordinates[0]);
		double lng = Double.parseDouble(coordinates[1]);

		p = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));

		mc.animateTo(p);
		mc.setZoom(17);
		
		MapOverlay mapOverlay = new MapOverlay();
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(mapOverlay);
        
		//mapView.invalidate();

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_3:
			mc.zoomIn();
			break;
		case KeyEvent.KEYCODE_1:
			mc.zoomOut();
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	IntentFilter filter=null;
	ProximityIntentReceiver  receiver=null;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		 filter = new IntentFilter(ProximityIntentReceiver.TREASURE_PROXIMITY_ALERT);
		 receiver=new ProximityIntentReceiver();
		registerReceiver(new ProximityIntentReceiver(), filter);
		
		super.onResume();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onPause();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		menu.add(0, 3, Menu.NONE, "Mark Area ");
		menu.add(0, 1, Menu.NONE, "Add Alert ");
		menu.add(0, 2, Menu.NONE, "Remove Alert");
		
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case (1): {
			if(isNewAlertNeed){
			Intent service=new Intent(GMaps.this, MyLocationService.class);
			Bundle bundle= new Bundle();
			bundle.putString("myAlert", lat+","+lon);
			service.putExtras(bundle);
			startService(service);
			}else{
				Toast.makeText(getApplicationContext(), "Select area for new Alert", Toast.LENGTH_SHORT);
			}
			return true;
		}
		case (2): {
			stopService(new Intent(GMaps.this, MyLocationService.class));
			return true;
		}
		case (3): {
			isNewAlertNeed=true;
			return true;
		}
		}

		return true;
	}
	
}
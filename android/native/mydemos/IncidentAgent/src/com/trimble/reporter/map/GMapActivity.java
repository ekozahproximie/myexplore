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
 *      com.trimble.reporter
 *
 * File name:
 *		GMapActivity.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Dec 15, 2012 8:35:34 AM
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



package com.trimble.reporter.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import com.trimble.agent.R;
import com.trimble.reporter.ReporterActivity;
import com.trimble.reporter.utils.Utils;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import java.util.List;



/**
 * @author sprabhu
 *
 */

public class GMapActivity extends MapActivity {

    public static final String GEO_LOCATION = "geo_location";
    private AddItemizedOverlay itemizedOverlay=null;
    private  List<Overlay> mapOverlays=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_map);
        
        

    }
    
    @Override
    protected void onStart() {
        super.onStart();
        if (!Utils.isInternetAvailable(getApplication())
                || Utils.isAirplaneModeOn(getApplication())) {
            
            Utils.showError(getApplication(),
                    getResources().getString(R.string.connection_error));
        }
        // Displaying Zooming controls
        
        MapView mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);

        /**
         * Changing Map Type
         * */
        // mapView.setSatellite(true); // Satellite View
        // mapView.setStreetView(true); // Street View
        // mapView.setTraffic(true); // Traffic view

        /**
         * showing location by Latitude and Longitude
         * */
        MapController mc = mapView.getController();
        GeoPoint geoPoint = getGeoLocFromBundle(getIntent().getExtras());
        if(geoPoint == null)
        {
            Utils.showError(getApplicationContext(), getString(R.string.geo_loc_error));
            return;
        }
        mc.animateTo(geoPoint);
        mc.setZoom(15);
        mapView.invalidate();

        /**
         * Placing Marker
         * */
        mapOverlays= mapView.getOverlays();
        Drawable drawable = this.getResources()
                .getDrawable(R.drawable.mark_red);
        itemizedOverlay = new AddItemizedOverlay(drawable,
                this,lat,lon,this);
        addOverlay(geoPoint);
        
    }
    double lat=0.0;
    double lon=0.0;
    private GeoPoint getGeoLocFromBundle(Bundle lBundle) {
        if(lBundle == null) { return null; }
        String geoLocString = lBundle.getString(GEO_LOCATION);
        
        if(TextUtils.isEmpty(geoLocString)) { return null; }
        
        String[] geoLocSubStrings = geoLocString.split(" ");
        
        if(geoLocSubStrings.length <= 1) { return null; }
        
        
         lat = Double.parseDouble(geoLocSubStrings[0]);
         lon = Double.parseDouble(geoLocSubStrings[1]);

        return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
    }

    protected boolean isRouteDisplayed() {
        return false;
    }
    public void onClick(View onclick){
        switch (onclick.getId()) {
            case R.id.sendloc:
                sendResult();
                break;

            default:
                break;
        }
    }
    public void sendResult( ){
        Intent intent = new Intent();
        intent.putExtra(ReporterActivity.LATITUDE, String.valueOf(itemizedOverlay.dLat));
        intent.putExtra(ReporterActivity.LONGITUDE, String.valueOf(itemizedOverlay.dLon));
        setResult(RESULT_OK,intent);
        finish();
    }
    public void addOverlay(GeoPoint geoPoint){
        OverlayItem overlayitem = new OverlayItem(geoPoint, null, null);
        mapOverlays.clear();
        itemizedOverlay.clear();
        itemizedOverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedOverlay);
    }

}
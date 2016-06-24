
package com.trimble.reporter.map;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class AddItemizedOverlay extends ItemizedOverlay<OverlayItem> {

    private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();

    private Context context;

    private GMapActivity mActivity = null;

    public double dLat = 0.0;

    public double dLon = 0.0;

    public AddItemizedOverlay(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
    }

    public AddItemizedOverlay(Drawable defaultMarker, Context context, double dLat, double dLon,
            GMapActivity mActivity) {
        this(defaultMarker);
        this.context = context;

        this.dLat = dLat;
        this.dLon = dLon;
        this.mActivity = mActivity;
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mapOverlays.get(i);
    }

    @Override
    public int size() {
        return mapOverlays.size();
    }

    public void clear() {
        mapOverlays.clear();

    }

    @Override
    protected boolean onTap(int index) {
        Log.e("Tap", "Tap Performed");
        return true;
    }

    public void addOverlay(OverlayItem overlay) {
        mapOverlays.add(overlay);
        this.populate();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.google.android.maps.ItemizedOverlay#draw(android.graphics.Canvas,
     * com.google.android.maps.MapView, boolean)
     */
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {

        super.draw(canvas, mapView, shadow);
    }

    /**
     * Getting Latitude and Longitude on Touch event
     **/
    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {

        if (event.getAction() == 1) {
            GeoPoint geopoint = mapView.getProjection().fromPixels((int)event.getX(),
                    (int)event.getY());
            // latitude
            dLat = geopoint.getLatitudeE6() / 1E6;
            // longitude
            dLon = geopoint.getLongitudeE6() / 1E6;
            showToast("Lat: " + dLat + ", Lon: " + dLon);
            mActivity.addOverlay(geopoint);
            return true;
        }
        return false;
    }

    private Toast mToast = null;

    public void showToast(String stMsg) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, stMsg, Toast.LENGTH_SHORT);
        mToast.show();
    }

}

package com.navteq.lpa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Contains static method to perform common Android Acivities. This call should never
 * include any application specific functionality so that it may be reused on any
 * android project. 
 * 
 * @author JThorn
 *
 */
public class NavteqLPALocationUtils {
	
	private static final String LOGCAT = "AndroidUtils";
	private static Location location;
	private static LocationListener listener = null;
	
    public static boolean collectLocationData(final Activity ctx, final long duration) {
    	
    	// only need to call this once
    	if(listener != null) { 
    		
    		return true;
    	}
    	
    	// location updates need to be registered on a Looper thread, so call it from the main UI thread
    	ctx.runOnUiThread(new Runnable() {    	
    		public void run() {
    	    	final LocationManager m = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
    	    	if(m != null) {
    	    		location = m.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	    		if(location == null){
    	    			location = m.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	    		}
    	    		
    	    		listener = new LocationListener() {
    	    			public void onLocationChanged(Location l) {
    	    				Log.d(LOGCAT, "GPS: Received location " +l.toString());
    	    				location = l;
    	    			}

    					public void onProviderDisabled(String provider) {
    						Log.d(LOGCAT, "GPS: provider disabled");
    					}

    					public void onProviderEnabled(String provider) {
    						Log.d(LOGCAT, "GPS: provider endabled");
    						location = m.getLastKnownLocation(provider);
    					}

    					public void onStatusChanged(String provider, int status, Bundle extras) {
    						Log.d(LOGCAT, "GPS: Status changed: " +provider +" status=" +status);
    					}
    	    		};
    	    		m.requestLocationUpdates(LocationManager.GPS_PROVIDER, duration, 0, listener);
    	    	}    			
    		}
    	});
    	
    	return false;
    }
    
    public static boolean stopCollectingLocation(Context ctx) {
    	if(listener != null) {
    		LocationManager m = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
        	if(m != null) {
        		m.removeUpdates(listener);
        		listener = null;
        		return true;
        	}
    	}
    	
    	return false;
    }
    
    public static Location getLocation() {
    	return location;
    }
    
    public static String getLocationString() {
		if(location == null) {
			return "";
		}
		
		return Double.toString(location.getLatitude()) +" " +Double.toString(location.getLongitude());
    }		
	
	/**
     * Displays an error dialog box. 
     * @param ctx the parent Activity
     * @param errorMsg the message to display
     * @param buttonText the text for the dismiss button
     * @param quit if true, calls finish on the parent Activity
     */
	public static void displayError(final Activity ctx, String errorMsg, String buttonText, boolean quit) {
    	if(quit) {
    		displayError(ctx, errorMsg, "An Error Occurred", buttonText, new OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	ctx.finish();
                }
    		});
    	}
    	else {
    		displayError(ctx, errorMsg, "An Error Occurred", buttonText, null);
    	}
    }
    
	/**
	 * Displays an error dialog box.
	 * @param ctx the parent activity
	 * @param errorMsg the error message to display
	 * @param title the title of the dialog window
	 * @param buttonText the text of the dismiss button
	 * @param listener handler for the dismiss button
	 */
    public static void displayError(Context ctx, String errorMsg, String title, String buttonText, OnClickListener listener) {
    	AlertDialog.Builder okDlg = new AlertDialog.Builder(ctx);
    	okDlg.setTitle(title);
    	okDlg.setMessage(errorMsg);
    	okDlg.setPositiveButton(buttonText, listener);    
    	okDlg.show();
    }	
    


}

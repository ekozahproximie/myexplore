package com.spime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class ProximityIntentReceiver extends BroadcastReceiver {
	public static String TREASURE_PROXIMITY_ALERT = "com.spime.MYLOCATIONALERT";
	public static final String CUSTOM_INTENT = "com.spime.GMaps.Proxy";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String key = LocationManager.KEY_PROXIMITY_ENTERING;
		Boolean entering = intent.getBooleanExtra(key, false);
		System.out.println("Am received");
		//80.252016 12.996412
		

		Intent i = new Intent();  
    	   i.setAction(CUSTOM_INTENT);  
	       context.sendBroadcast(i);  
	}

}

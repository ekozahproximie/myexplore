package com.spime.gps;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GPSEnableReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("GPSEnableReceiver", "GPSEnableReceiver invoked");
		if (intent.getAction().equals("android.location.GPS_ENABLED_CHANGE")) {
			Bundle bundle = intent.getExtras();
			boolean isGPSEnabled = bundle.getBoolean("enabled");
			if (isGPSEnabled) {
				Toast.makeText(context, "GPS enable", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(context, "GPS disable", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}

}

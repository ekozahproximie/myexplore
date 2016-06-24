package com.spime;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class FeatureSMSService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	public void onCreate() {
		super.onCreate();
	}
	public void onStart(Intent intent, int startid) {
		 Bundle bundle=intent.getExtras();
		 String stPhone=bundle.getString("myData");
		 stPhone=stPhone.substring(0, stPhone.indexOf(",") );
		 String stMsg=bundle.getString("myData");
		 stMsg=stMsg.substring( stMsg.indexOf(",")+1 );
		Toast.makeText(this, "My Service Started"+stPhone+","+stMsg, Toast.LENGTH_SHORT).show();
		
	}
	public void onDestroy() {
		super.onDestroy();
	}
}

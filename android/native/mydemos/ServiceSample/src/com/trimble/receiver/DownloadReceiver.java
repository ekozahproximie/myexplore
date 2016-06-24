package com.trimble.receiver;

import com.trimple.services.DownloadService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DownloadReceiver extends BroadcastReceiver {

	public static final String DOWNLOAD_COMPLETE="com.trimble.Downloadcomplete";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent != null && intent.getExtras() != null){
			String stFileLocation=intent.getStringExtra(DownloadService.STORE_F_NAME);
			Toast.makeText(context, "File path:"+stFileLocation, Toast.LENGTH_SHORT).show();
		}

	}

}

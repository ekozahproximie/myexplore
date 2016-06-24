package com.trimple.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.trimple.servicesample.R;

public class MyService extends Service {
	
	private static final String TAG = "ServicesDemo";

	private MediaPlayer player;
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
     
    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created...", Toast.LENGTH_LONG).show();
         
        player = MediaPlayer.create(this, R.raw.bong);
        player.setLooping(false); // Set looping
    }
    
    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service Started...", Toast.LENGTH_LONG).show();
        player.start();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i( TAG, "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped..", Toast.LENGTH_LONG).show();
         player.stop();
    }
     
   

}

package com.trimble.myglassfleet;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;

import com.trimble.assetservice.AssetService;

public class FleetMenuActivity extends Activity {
   
   protected static final int LIST_ALL_ASSET = 0;
   private final Handler mHandler = new Handler();
   private boolean mAttachedToWindow;
   private boolean mOptionsMenuOpen;
   private AssetService.AssetBinder mCompassService =null;
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      
      super.onCreate(savedInstanceState);
      final Intent intent =new Intent(this, AssetService.class);
      final boolean isServiceBound= bindService( intent, mConnection,Service.MODE_PRIVATE );
//      if(isServiceBound){
//         startService(intent);
//      }
      
   }


   private ServiceConnection mConnection = new ServiceConnection() {
       @Override
       public void onServiceConnected(ComponentName name, IBinder service) {
           if (service instanceof AssetService.AssetBinder) {
               mCompassService = (AssetService.AssetBinder) service;
               openOptionsMenu();
           }
       }

       @Override
       public void onServiceDisconnected(ComponentName name) {
           // Do nothing.
       }
   };

   @Override
   public void onAttachedToWindow() {
       super.onAttachedToWindow();
       mAttachedToWindow = true;
       openOptionsMenu();
   }

   @Override
   public void onDetachedFromWindow() {
       super.onDetachedFromWindow();
       mAttachedToWindow = false;
   }

   @Override
   public void openOptionsMenu() {
       if (!mOptionsMenuOpen && mAttachedToWindow && mCompassService != null) {
           super.openOptionsMenu();
       }
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.fleet, menu);
       return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
          
          case R.id.show_my_asset:
             
                   
                // Start the new Activity at the end of the message queue for proper options menu
                // animation. This is only needed when starting a new Activity or stopping a Service
                // that published a LiveCard.
                post(new Runnable() {

                    @Override
                    public void run() {
                        Intent setTimerIntent =
                                new Intent(FleetMenuActivity.this, FleetCardScrollActivity.class);
                        startActivityForResult(setTimerIntent, LIST_ALL_ASSET);
                    }
                });
              return true;
           case R.id.read_aloud:
              if(mCompassService != null){
                 mCompassService.readAssetNameAloud();
              }
               return true;
           case R.id.stop:
               // Stop the service at the end of the message queue for proper options menu
               // animation. This is only needed when starting an Activity or stopping a Service
               // that published a LiveCard.
               mHandler.post(new Runnable() {

                   @Override
                   public void run() {
                       stopService(new Intent(FleetMenuActivity.this, AssetService.class));
                   }
               });
               return true;
           default:
               return super.onOptionsItemSelected(item);
       }
   }

   @Override
   public void onOptionsMenuClosed(Menu menu) {
       super.onOptionsMenuClosed(menu);
       mOptionsMenuOpen = false;

       unbindService(mConnection);

       // We must call finish() from this method to ensure that the activity ends either when an
       // item is selected from the menu or when the menu is dismissed by swiping down.
       finish();
   }
   /**
    * Posts a {@link Runnable} at the end of the message loop, overridable for testing.
    */
   protected void post(Runnable runnable) {
       mHandler.post(runnable);
   }
}

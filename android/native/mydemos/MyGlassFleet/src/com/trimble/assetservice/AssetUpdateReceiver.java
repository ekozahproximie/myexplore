package com.trimble.assetservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.trimble.myglassfleet.FleetCardScrollActivity;


public class AssetUpdateReceiver extends BroadcastReceiver {
   
   private transient FleetCardScrollActivity fleetCardScrollActivity =null;

   
   public AssetUpdateReceiver(final FleetCardScrollActivity fleetMenuActivity) {
      this.fleetCardScrollActivity=fleetMenuActivity;
   }
   @Override
   public void onReceive(Context context, Intent intent) {
      if(fleetCardScrollActivity != null){
         fleetCardScrollActivity.updateNewAsset();
      }

   }

}

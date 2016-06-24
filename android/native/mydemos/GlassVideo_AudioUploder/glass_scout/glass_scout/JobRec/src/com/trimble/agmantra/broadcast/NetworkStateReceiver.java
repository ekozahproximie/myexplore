package com.trimble.agmantra.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.trimble.agmantra.dbutil.Log;

public class NetworkStateReceiver extends BroadcastReceiver {

   private static boolean isNetworkAvailable = false;

   public void onReceive(Context context, Intent intent) {

      String action = intent.getAction();
      if (action != null) {

         if (intent.getExtras() != null) {
            NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get(
                  ConnectivityManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null
                  && networkInfo.getState() == NetworkInfo.State.CONNECTED) {

               isNetworkAvailable = true;
               Log.i("Ag-NetworkStateReceiver", "Network " + networkInfo.getTypeName()
                     + " connected");
            }
         }
         if (intent.getExtras().getBoolean(

         ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {

            isNetworkAvailable = false;
         }
      }
   }

   // Check & get Network available info present or not

   public static boolean isSDcardPresent() {
      return isNetworkAvailable;
   }

}

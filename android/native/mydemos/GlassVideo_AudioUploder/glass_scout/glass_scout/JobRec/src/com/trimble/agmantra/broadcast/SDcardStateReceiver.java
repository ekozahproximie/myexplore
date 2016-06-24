package com.trimble.agmantra.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.trimble.agmantra.dbutil.Log;

public class SDcardStateReceiver extends BroadcastReceiver {

   private static boolean isSdCardMount      = false;

   public void onReceive(Context context, Intent intent) {
      // Bundle bundle = intent.getExtras();
      String action = intent.getAction();

      if (action != null) {

         if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            // SD card available
            Log.i("SDCARD_RECEIVER", "Sdcard receiver"
                  + Intent.ACTION_MEDIA_MOUNTED);
         } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)
               || action.equals(Intent.ACTION_MEDIA_CHECKING)) {
            Log.i("SDCARD_RECEIVER", "Sdcard receiver"
                  + Intent.ACTION_MEDIA_UNMOUNTED);

         }
      }

      isSdCardMount = true;
   }

   // Check & get SDcard info present or not
   public static boolean isSDcardPresent() {
      return isSdCardMount;
   }

}

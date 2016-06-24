/**
 * Copyright Trimble Inc., 2013 - 2014 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.agmantra.log
 *
 * File name:
 *	    SdcardReceiver.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Dec 1, 20134:12:58 PM
 *
 * Abstract:
 *
 *
 * Environment:
 *  Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.ag.filemonitor.log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


/**
 * @author sprabhu
 *
 */
public class SdcardReceiver extends BroadcastReceiver {
 
   private static final String TAG=LogReaderTask.TAG;
   
   private LogReaderTask logReaderTask =null;
   
   public SdcardReceiver(final LogReaderTask logReaderTask) {
    this.logReaderTask=logReaderTask;
   }

  
   @Override
   public void onReceive(Context context, Intent intent) {
  //Environment.MEDIA_MOUNTED_READ_ONLY
      if(intent != null && intent.getAction() != null){
         final String stAction=intent.getAction();
         Log.i(TAG, " Action :"+stAction);
         checkMediaAction(stAction);
         
      }
   }
   
   private void checkMediaAction(String stAction){
      if(stAction.equals(Intent.ACTION_MEDIA_MOUNTED) 
            || stAction.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)){
         logReaderTask.startLogProcess();
         
      }else{
         logReaderTask.stopLogProcess();
      }
   }
   
   public static IntentFilter getSdcardIntentFilter(){
      final IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
      intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
      intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
      intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
      intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
      intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
      intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
      intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
      intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
      intentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
      intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
      intentFilter.addAction(Intent.ACTION_MEDIA_NOFS);
      intentFilter.addDataScheme("file");
      
      return intentFilter;
      
   }

}

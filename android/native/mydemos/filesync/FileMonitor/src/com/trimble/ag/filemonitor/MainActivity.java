/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 * 
 *
 * Module Name: com.trimble.ag.filemonitor
 *
 * File name: MainActivity.java
 *
 * Author: sprabhu
 *
 * Created On: Oct 9, 20144:18:00 PM
 *
 * Abstract:
 *
 *
 * Environment: Mobile Profile : Mobile Configuration :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.ag.filemonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.trimble.ag.acdc.ACDCApi;
import com.trimble.ag.acdc.s3api.AmazonS3ClientAPI;
import com.trimble.ag.filemonitor.log.LogReaderTask;
import com.trimble.ag.filemonitor.service.MonitorService;

import java.io.File;

/**
 * @author sprabhu
 *
 */
public class MainActivity extends Activity {

   public static final String LOG = MainActivity.class.getSimpleName();

   /**
    * 
    */
   public MainActivity() {

   }

   /*
    * (non-Javadoc)
    * 
    * @see android.app.Activity#onCreate(android.os.Bundle)
    */
   @Override
   protected void onCreate(Bundle savedInstanceState) {

      super.onCreate(savedInstanceState);

      final Intent intent = new Intent(this, MonitorService.class);
      final File aFolder = new File(Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator + "A");
      if (!aFolder.exists()) {
         final boolean isCreated = aFolder.mkdirs();
         Log.i(LOG, aFolder + " isCreated :" + isCreated);
      } else {
         Log.i(LOG, aFolder + " exists");
      }
      LogReaderTask logReaderTask = LogReaderTask
            .getInstance(getApplicationContext());
      Log.i(LOG, "log file name" + logReaderTask.getLogFilePath().getParent());
      intent.putExtra(MonitorService.MONITOR_PATH, logReaderTask
            .getLogFilePath().getParent());
      intent.setAction("com.trimble.ag.filemonitor.service.MonitorService");
     
    
      startService(intent);
// ZipUtils.doZip(Environment.getExternalStorageDirectory()+File.separator+"A/",
// Environment.getExternalStorageDirectory()+
// File.separator+"ZipFiles/test.zip", true);

   }

   /*
    * (non-Javadoc)
    * 
    * @see android.app.Activity#onDestroy()
    */
   @Override
   protected void onDestroy() {

      super.onDestroy();
   }

}

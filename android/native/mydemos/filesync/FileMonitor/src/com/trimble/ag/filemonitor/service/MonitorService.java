/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 * 
 *
 * Module Name: com.trimble.ag.filemonitor.service
 *
 * File name: MonitorService.java
 *
 * Author: sprabhu
 *
 * Created On: Sep 30, 201412:28:04 PM
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
package com.trimble.ag.filemonitor.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.trimble.ag.filemonitor.RecursiveFileObserver;
import com.trimble.ag.filemonitor.ZipJob;
import com.trimble.ag.filemonitor.entity.FileInfo;
import com.trimble.ag.filemonitor.utils.Utils;
import com.trimble.ag.nabu.db.FileSyncContentProvider;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author sprabhu
 *
 */
public class MonitorService extends Service implements Runnable {

   private static final String                              LOG                      = MonitorService.class
                                                                                           .getSimpleName();

   public static final String                               MONITOR_PATH             = "monitor_path";

   private static final String                              MONITOR_THREAD           = "monitor_thread";

   private transient Queue<RecursiveFileObserver>           queueMonitorJobs         = null;

   private transient HashMap<String, RecursiveFileObserver> mapRecursiveFileObserver = null;

   private transient boolean                                isMonitorStop            = false;

   private transient FileSyncContentProvider                contentProvider          = null;

   private transient ZipJob                                 zipJob                   = null;

   private static final String                              APP_NAME                 = "test";
   
  private transient static final String ZIP_FOLDER_NAME     = "ZipFiles";

   @Override
   public void onCreate() {
      contentProvider = FileSyncContentProvider
            .getInstance(getApplicationContext());
      zipJob =ZipJob.getInstance(getApplicationContext());
      super.onCreate();
   }

   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      if (intent != null) {
         addFileMonitor(getFilePath(intent));
      } else {
         getAllFileInfoFromDb();
      }
      Log.i(LOG, "flags:" + flags + "," + startId);
      return START_STICKY;
   }

   private void getAllFileInfoFromDb() {
      final Thread thread = new Thread() {

         @Override
         public void run() {
            final List<FileInfo> listOfFileInfo = contentProvider
                  .getAllFileInfo();
            for (final FileInfo fileInfo : listOfFileInfo) {
               addFileMonitor(fileInfo.getFilePath());
            }

         }
      };
      thread.start();
   }

   private String getFilePath(Intent intent) {
      if (intent == null) {
         Log.e(LOG, "addFileMonitor intent is null");
         return null;
      }
      final Bundle bundle = intent.getExtras();
      final String stFilePath = bundle.getString(MONITOR_PATH);
      return stFilePath;
   }

   @Override
   public void run() {
      while (!isMonitorStop) {
         if (queueMonitorJobs.isEmpty()) {
            continue;
         }
         final RecursiveFileObserver fileObserver = queueMonitorJobs.poll();
         if (fileObserver == null) {
            continue;
         }
         final String stFilePath=fileObserver.getPath();
         Log.i(LOG, "File Observer started:" + stFilePath);
         
         contentProvider.addFile(stFilePath, APP_NAME,getOutputDirectory(stFilePath));
         zipJob.addFileToZip(stFilePath);
         
         fileObserver.startWatching();

      }

   }
   private String getOutputDirectory(final  String stFilePath){
      final StringBuilder builder = new StringBuilder();
      builder.append(Environment.getExternalStorageDirectory().getAbsolutePath());
      builder.append(File.separator);
      builder.append(ZIP_FOLDER_NAME);
      builder.append(File.separator);
      final File fileInfo = new File(stFilePath);
      final String stSourceFileLoc = fileInfo.getName();

      final File file = new File(stSourceFileLoc);
      builder.append(file.getName());
      builder.append(".zip");
      return builder.toString();
   }

   private void addFileMonitor(final String stFilePath) {

      File file = null;
      if (stFilePath == null || stFilePath.trim().length() == 0
            || !(file = new File(stFilePath)).exists()) {
         Log.e(LOG, "File not found in the path");
         return;
      }
      if (!file.isDirectory()) {
         Log.e(LOG, "File not a directory");
         return;
      }

      if (queueMonitorJobs == null) {
         mapRecursiveFileObserver = new HashMap<String, RecursiveFileObserver>(
               1);
         queueMonitorJobs = new LinkedList<RecursiveFileObserver>();
      }

      if (mapRecursiveFileObserver.containsKey(stFilePath)) {
         Log.e(LOG, "File already has monitor");
         return;
      }
      RecursiveFileObserver fileObserver = new RecursiveFileObserver(stFilePath,zipJob);

      queueMonitorJobs.add(fileObserver);
      mapRecursiveFileObserver.put(stFilePath, fileObserver);

      if (!Utils.isThreadRunning(MONITOR_THREAD)) {
         final Thread thread = new Thread(this, MONITOR_THREAD);
         thread.start();
      }
   }

   @Override
   public void onDestroy() {
      Log.i(LOG, "onDestroy");
      super.onDestroy();
      stopMonitor();
   }

   public void stopMonitor() {
      isMonitorStop = true;
      if (queueMonitorJobs != null) {
         queueMonitorJobs.clear();
      }

   }

   @Override
   public IBinder onBind(Intent intent) {

      return null;
   }

}

/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 * 
 *
 * Module Name: com.trimble.scout.net
 *
 * File name: JobUploader.java
 *
 * Author: sprabhu
 *
 * Created On: Jun 14, 20147:00:25 PM
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
package com.trimble.scout.net;

import android.content.Context;
import android.util.Log;

import com.trimble.agmantra.utils.Utils;
import com.trimble.scout.encode.VideoJob;

import java.util.Vector;

/**
 * @author sprabhu
 *
 */
public final class JobUploader {

   /**
    * 
    */

   private static final String LOG                = "test";
   private transient boolean   isUploadJobStop    = false;
   private Vector<VideoJob>    vecVideoJob        = new Vector<VideoJob>(1);
   private UploadWorkerThread  uploadWorkerThread = null;
   
   private Context appContext=null;

   private JobUploader(final Context appContext) {
      if (jobUploader != null) {
         throw new IllegalAccessError("please use getInstance :(");

      }
      this.appContext=appContext;
   }

   private static JobUploader jobUploader = null;

   public static synchronized JobUploader getInstance(final Context appContext) {
      if (jobUploader == null) {
         jobUploader = new JobUploader(appContext);
      }

      return jobUploader;
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {

      throw new CloneNotSupportedException();
   }

   public boolean updateVideoToServer(final VideoJob videoJob) {
      boolean isAdded = false;
      if (videoJob == null) {
         return isAdded;
      }
      synchronized (vecVideoJob) {
         isAdded = vecVideoJob.add(videoJob);
      }
      if (!Utils.isThreadRunning(THREAD_NAME)) {
         uploadWorkerThread = new UploadWorkerThread();
         uploadWorkerThread.start();
      }
      return isAdded;
   }

   public void stopUpdateJob() {
      synchronized (vecVideoJob) {
         vecVideoJob.clear();
         Log.i(LOG, "UploadWorkerThread remove all video job");
      }
      isUploadJobStop = true;
      Log.i(LOG, "stopUpdateJob");
   }

   private static final String THREAD_NAME = "UploadWorkerThread";

   private class UploadWorkerThread extends Thread {

      
      public UploadWorkerThread() {
         super(THREAD_NAME);
      }
      private boolean deleteFile(VideoJob videoJob){
         return videoJob.getFileToUpload().delete();
      }
      @Override
      public void run() {
         Log.i(LOG, "UploadWorkerThread start running");
         try {
            while ( ! isUploadJobStop) {

               if (vecVideoJob.isEmpty() ) {
                  continue;
               }
               if(! Utils.isInternetConnection(appContext)){
                  Log.i(LOG, "UploadWorkerThread  no networkConnnection");
                  continue;
               }
               VideoJob job = null;
               synchronized (vecVideoJob) {
                  job = vecVideoJob.get(0);
               }
               
              // boolean isUploaded= MultipartUtility.main(job);
               boolean isUploaded= SFTPUtility.main(job);
               if(isUploaded){
               
                 synchronized (vecVideoJob) {
                    job.createJobSendFile(job.getLocation());
                    vecVideoJob.remove(0);
                    Log.i(LOG, "UploadWorkerThread remove the video job");
                 }
                 
               }else{
                  synchronized (vecVideoJob) {
                     vecVideoJob.remove(0);
                     Log.i(LOG, "UploadWorkerThread remove the video job");
                  }
                   Log.e(LOG, "UploadWorkerThread uploaded job fail"+job);
               }
               Log.i(LOG, "UploadWorkerThread delete the video job"+deleteFile(job));
            }
         } finally {
            Log.i(LOG, "UploadWorkerThread stop running...");
         }
      }
   }

}

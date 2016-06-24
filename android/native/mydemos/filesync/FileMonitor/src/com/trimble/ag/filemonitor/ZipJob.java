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
 * File name: ZipJob.java
 *
 * Author: sprabhu
 *
 * Created On: Oct 15, 20143:27:06 PM
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

import android.content.Context;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.trimble.ag.acdc.ACDCApi;
import com.trimble.ag.acdc.s3api.AmazonS3ClientAPI;
import com.trimble.ag.filemonitor.entity.FileInfo;
import com.trimble.ag.nabu.db.FileSyncContentProvider;
import com.trimble.agmantra.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author sprabhu
 *
 */
public class ZipJob {

   private transient HashSet<FileInfo>   fileInfoSet     = null;

   private transient Context             appContext      = null;

   private static ZipJob                 zipJob          = null;

   private TimerTask                     timerTask       = null;

   private Timer                         timer           = null;

   private FileSyncContentProvider       contentProvider = null;

   private transient boolean isTimerStarted =false;

   

   private final static int              ZIP_INTERVEL    = 2 * 60 * 1000;
   
   private final static long              LAST_MODIFIED_TIME_CAP    = 1000L * 60L * 30L;

   protected static final String         LOG             = ZipJob.class
                                                               .getSimpleName();
   
   private AmazonS3ClientAPI amazonS3ClientAPI = null;
   
   private static final String SOURCE="TMX";
   
   private static final String ENVIRONMENT="DEV";

   /**
    * 
    */
   private ZipJob(final Context appContext) {
      if (zipJob != null) {
         throw new IllegalAccessError("use getInstance");
      }
      this.appContext = appContext;
      contentProvider = FileSyncContentProvider.getInstance(appContext);
      amazonS3ClientAPI= AmazonS3ClientAPI.getInstance(appContext);
      timer = new Timer("File Zipper Thread");
      fileInfoSet= new HashSet<FileInfo>();
   }

   public static synchronized ZipJob getInstance(final Context appContext) {
      if (zipJob == null) {
         zipJob = new ZipJob(appContext);
      }
      return zipJob;
   }

   public void addFileToZip(final String stFileName) {
      if (stFileName != null) {
         final FileInfo fileInfo = contentProvider
               .getFileInfoByName(stFileName);
        if(fileInfo == null){
           return;
        }
         fileInfoSet.add(fileInfo);
         if(! isTimerStarted ){
            startZippingJob();
         }
      }
   }

   public void addFileToZip(final FileInfo fileInfo) {
      fileInfoSet.add(fileInfo);
   }

   public void startZippingJob() {
      
      
      
      timerTask = new TimerTask() {

         @Override
         public void run() {

            if (fileInfoSet.isEmpty()) {
               return;
            }
            for (FileInfo fileInfo : fileInfoSet) {
              
               fileInfo = contentProvider.getFileInfoById(fileInfo.getId());
               
               final String stSourceFileLoc = fileInfo.getFilePath();

               final File file = new File(stSourceFileLoc);
               

               Log.i(LOG, "Zip job taking file:" + stSourceFileLoc);
               final long lastModifiedTime = file.lastModified();
               final long currentFileSize = file.length();
               final Date lastZipDate = fileInfo.getLastZipTime();
               
               final long dbLastFileSize = fileInfo.getLastFileSize();
               final long lStatus=fileInfo.getStatus();
               final String stDestFileLoc = fileInfo.getDescFilePath();
               if( lStatus == FileSyncContentProvider.CREATE ||
                     ( lastZipDate != null &&  
                       lastModifiedTime - lastZipDate.getTime() > LAST_MODIFIED_TIME_CAP ) ){
                  doZipJob(stSourceFileLoc, stDestFileLoc, fileInfo, lastModifiedTime, currentFileSize);
               }else if(  currentFileSize !=  dbLastFileSize){
                  doZipJob(stSourceFileLoc, stDestFileLoc, fileInfo, lastModifiedTime, currentFileSize);
               }else{
                  fileInfo.setStatus(FileSyncContentProvider.NO_CHANGE);
                  contentProvider.updateFileInfo(fileInfo);
               }
            }
         }
      };
      
      timer.schedule(timerTask, 0, ZIP_INTERVEL);
      isTimerStarted=true;
   }

   private void doZipJob(final String stSourceFileLoc,
         final String stDestFileLoc, final FileInfo fileInfo,
         final long lastModifiedTime, final long currentFileSize) {
      final boolean bZipViaJNI = true;
      final Date zipDate=new Date();
      boolean isSuccess = ZipUtils.doZip(stSourceFileLoc, stDestFileLoc,
            bZipViaJNI);
      Log.i(LOG, "Zip job complete status:" + isSuccess);
      if (isSuccess) {
         fileInfo.setLastZipTime(zipDate);
         fileInfo.setLastFileSize(currentFileSize);
         try {
            amazonS3ClientAPI.doUpload(appContext, stDestFileLoc, SOURCE);
         } catch (AmazonServiceException e) {
            
            e.printStackTrace();
         } catch (UnknownHostException e) {
            
            e.printStackTrace();
         } catch (AmazonClientException e) {
            
            e.printStackTrace();
         } catch (IOException e) {
           
            e.printStackTrace();
         } catch (InterruptedException e) {
           
            e.printStackTrace();
         }
         Log.i(LOG, "Zip job destination file path:" + stDestFileLoc);
         contentProvider.updateFileInfo(fileInfo);
      }
   }

   public void cancelJob() {
      isTimerStarted=false;
      timer.cancel();
      if (timerTask != null) {
         timerTask.cancel();
      }
   }
}

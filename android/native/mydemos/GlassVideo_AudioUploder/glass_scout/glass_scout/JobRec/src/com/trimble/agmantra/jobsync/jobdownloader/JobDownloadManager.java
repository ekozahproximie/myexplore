package com.trimble.agmantra.jobsync.jobdownloader;

import com.trimble.agmantra.acdc.ACDCApi;
import com.trimble.agmantra.acdc.FileInfo;
import com.trimble.agmantra.acdc.FileList;
import com.trimble.agmantra.acdc.exception.InvalidResponseException;
import com.trimble.agmantra.acdc.exception.RegsitrationException;
import com.trimble.agmantra.acdc.exception.TicketException;
import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.utils.Utils;

import android.content.Context;
import android.os.Environment;
import com.trimble.agmantra.dbutil.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

public class JobDownloadManager {

   private static JobDownloadManager jobDownloadMngr = null;

   private Context                   context         = null;

  
   private JobDownloadQueue jobDownloadQueue = null;

   private JobDownloadManager(Context context) {
      this.context = context;
      
      jobDownloadQueue = JobDownloadQueue.getInstance(
            context, true);
   }

   /**
    * 
    * @param context
    * @return
    */
   public static JobDownloadManager getInstance(Context context) {

      if (jobDownloadMngr == null) {
         jobDownloadMngr = new JobDownloadManager(context);
      }
      return jobDownloadMngr;
   }

   public boolean startDownlaod() {

      boolean isReturn = false;
      
      FileList toDeviceFileList = null;

      String stToDevicePath;
      try {
         stToDevicePath = Utils.getToDeviceFileLoc(android.os.Build.MODEL);
      } catch (FileNotFoundException e2) {
         
         e2.printStackTrace();
         return false;
      }

      ACDCApi acdc = ACDCApi.getInstance(context);

      long lCurrTime = System.currentTimeMillis();

      try {
         
         toDeviceFileList = acdc.requestPendingList(String.valueOf(lCurrTime), stToDevicePath);
         
         addPendingFilesInDownloaderQueue(toDeviceFileList);
         
         isReturn = true;
         
      } catch (UnknownHostException e) {
         e.printStackTrace();
         Log.i(Constants.TAG_JOB_ENCODER,
               "SDcard Unmounted - unknown host in list of acdc files failed");
      } 
      
      catch (InvalidResponseException e) {
          e.printStackTrace();
          Log.i(Constants.TAG_JOB_ENCODER,
                "InvalidResponse"+e.getMessage());
       }
      catch (TicketException e) {
         try {            
            // refresh the tciket
            acdc.registration();
            
            // TODO - Servicde again call downloader files in next 2 mins 
            /*try {
               toDeviceFileList = acdc.requestPendingList(lCurrTime, stToDevicePath);
            } catch (TicketException e1) {
               e1.printStackTrace();
            }*/
            
         } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
         } catch (UnknownHostException e1) {
            e1.printStackTrace();
         } catch (IOException e1) {
            if (!Environment.getExternalStorageState().equals(
                  Environment.MEDIA_MOUNTED)) {
               Log.i(Constants.TAG_JOB_ENCODER,
                     "SDcard Unmounted - list  of acdc files failed");
            } else {
               e.printStackTrace();
            }
            return false;
         } catch (RegsitrationException e1) {
            e1.printStackTrace();
         }
         e.printStackTrace();
      } catch (IOException e) {
         if (!Environment.getExternalStorageState().equals(
               Environment.MEDIA_MOUNTED)) {
            Log.i(Constants.TAG_JOB_ENCODER,
                  "SDcard Unmounted - list  of acdc files failed");
         } else {
            e.printStackTrace();
         }
         return false;
      }
     return  isReturn;
   }
   
   private void addPendingFilesInDownloaderQueue(FileList fileList) {

      if (fileList != null && fileList.mVecFileList != null
            && fileList.mVecFileList.size() > 0) {

         for (FileInfo fileInfo : fileList.mVecFileList) {
  
            jobDownloadQueue.addToDownloaderQueue(fileInfo.stFileId,
                  fileInfo.stFileName);            
         }         
         jobDownloadQueue.startWorkerThread();
         
      }
   }
   
   public void stopDownload() {
      if (jobDownloadQueue != null) {
         jobDownloadQueue.stopDownload();
      }
   }

}

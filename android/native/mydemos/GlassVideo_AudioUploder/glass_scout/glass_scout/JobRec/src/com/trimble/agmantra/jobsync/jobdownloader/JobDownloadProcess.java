package com.trimble.agmantra.jobsync.jobdownloader;

import com.trimble.agmantra.acdc.ACDCApi;
import com.trimble.agmantra.acdc.exception.FileNameException;
import com.trimble.agmantra.acdc.exception.RegsitrationException;
import com.trimble.agmantra.acdc.exception.TicketException;
import com.trimble.agmantra.constant.Constants;

import android.content.Context;
import android.os.Environment;
import com.trimble.agmantra.dbutil.Log;

import java.io.IOException;
import java.net.UnknownHostException;

public class JobDownloadProcess implements Runnable {

   private Thread              donwloaderThread                = null;
   
   private boolean             isAsynMode            = false;
   
   private String stFileId = null;
   
   private JobDownloadListener   downloadProcessListener = null;

   private static final String THREAD_NAME           = "JobSync_download ";

   private Context             context               = null;

   public JobDownloadProcess(String stFileId, boolean isAsyn,
         JobDownloadListener processListener, Context context) {
      this.isAsynMode = isAsyn;
      this.downloadProcessListener = processListener;
      this.context = context;
      this.stFileId =stFileId;
   }

   public void startDownloader() {
      if (isAsynMode) {
         donwloaderThread = new Thread(this);
         donwloaderThread.setName(THREAD_NAME+stFileId);
         donwloaderThread.start();
      } else {
         startDownloadProcess();
      }
   }

   @Override
   public void run() {
      if (!Thread.interrupted()) {
         startDownloadProcess();
      }
   }

   private void startDownloadProcess() {

      String stFileName = null;

      ACDCApi acdcApi = ACDCApi.getInstance(context);

      try {

         stFileName = acdcApi.downloadFile(stFileId);

         if (stFileName != null) {
            
            if(downloadProcessListener!=null){
               downloadProcessListener.onJobDownloadSuccess(stFileName);
            }            
            Log.i(Constants.TAG_JOB_DOWNLOADER,
                  "SDcard Unmounted - file downloaded succes fully");
         }

      } catch (UnknownHostException e) {
         e.printStackTrace();
      } catch (TicketException e) {
         try {
            // refresh the ticket info
            acdcApi.registration();
            if(downloadProcessListener!=null){
               downloadProcessListener.onJobDownloadFailure(stFileName,JobDownloadListener.TICKET_EXCEPTION);
            }

         } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
         } catch (UnknownHostException e1) {
            e1.printStackTrace();
         } catch (IOException e1) {
            if (!Environment.getExternalStorageState().equals(
                  Environment.MEDIA_MOUNTED)) {
               Log.i(Constants.TAG_JOB_DOWNLOADER,
                     "SDcard Unmounted - list  of acdc files failed");
            } else {
               e.printStackTrace();
            }
            return;
         } catch (RegsitrationException e1) {
            e1.printStackTrace();
         }
         e.printStackTrace();
      } catch (IOException e) {
         if (!Environment.getExternalStorageState().equals(
               Environment.MEDIA_MOUNTED)) {
            Log.i(Constants.TAG_JOB_DOWNLOADER,
                  "SDcard Unmounted - list  of acdc files failed");
         } else {
            e.printStackTrace();
         }
         return;
      } catch (FileNameException e) {

         Log.i(Constants.TAG_JOB_DOWNLOADER,
               "SDcard Unmounted - file download invalid file name");
         e.printStackTrace();
      }

   }

   public void stopDownload() {
      if (donwloaderThread != null) {
         donwloaderThread.interrupt();
      }
   }

}

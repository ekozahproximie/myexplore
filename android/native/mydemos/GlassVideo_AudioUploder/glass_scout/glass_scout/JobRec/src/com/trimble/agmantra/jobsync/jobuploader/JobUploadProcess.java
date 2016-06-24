package com.trimble.agmantra.jobsync.jobuploader;

import com.trimble.agmantra.acdc.ACDCApi;
import com.trimble.agmantra.acdc.exception.FileNameException;
import com.trimble.agmantra.acdc.exception.InvalidResponseException;
import com.trimble.agmantra.acdc.exception.RegsitrationException;
import com.trimble.agmantra.acdc.exception.TicketException;
import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.dbutil.Log;
import com.trimble.agmantra.entity.Job;
import com.trimble.agmantra.utils.Utils;
import com.trimble.agmantra.utils.ZipUtils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

public class JobUploadProcess implements Runnable {

   private Thread              thread                = null;

   private String              stJobFilePath         = null;

   private boolean             isAsynMode            = false;

   private JobUploadListener   uploadProcessListener = null;

   private static final String THREAD_NAME           = "JobSync_upload ";

   private Context             context               = null;
   
   private long lJobId=-1;
   
   public static final int WAIT_FOR_NETWORK = 1;
   
   public static final int UPLAOD_START= 2;
   
   public static final int UPLAOD_FINISH= 3;
   
   public static final int UPLAOD_FAILER= 4;
   
   private JobUploaderQueue jobUploaderQueue=null;

   public JobUploadProcess(String stFilePath, long lJobId,boolean isAsyn,
         JobUploadListener processListener, Context context,JobUploaderQueue jobUploaderQueue) {
      this.lJobId=lJobId;
      this.stJobFilePath = stFilePath;
      this.isAsynMode = isAsyn;
      this.uploadProcessListener = processListener;
      this.jobUploaderQueue=jobUploaderQueue;
      
      this.context = context;
   }

   public void startUploader() {
      if (isAsynMode) {
         thread = new Thread(this);
         thread.setName(THREAD_NAME + stJobFilePath);
         thread.start();
      } else {
         startUploadprocess();
      }
   }

   public String getJobFilePath() {
      return stJobFilePath;
   }

   @Override
   public void run() {
      if (!Thread.interrupted()) {
         startUploadprocess();
      }
   }

   private void startUploadprocess() {

      ACDCApi acdcApi = ACDCApi.getInstance(context);
      uploadProcessListener.onJobUploadStatus(UPLAOD_START, lJobId);
      jobUploaderQueue.setCurrentJobUploadStatus(UPLAOD_START, lJobId);
      String stFileName = null;
      try {
          try {
              if(acdcApi.isKeyExpire()){
                  acdcApi.registration();
              }
        } catch (RegsitrationException e) {
            Log.e(Constants.TAG_JOB_UPLOADER,
                    "printStackTrace - RegsitrationException");
            e.printStackTrace();
           
            if (uploadProcessListener != null) {
                uploadProcessListener.onJobUploadFailure(stJobFilePath,lJobId,
                      JobUploadListener.TICKET_EXCEPTION);
             }
            return;
        }
          if (!ZipUtils.isValidZip(stJobFilePath)) {
              Log.e(Constants.TAG_JOB_UPLOADER, "zip file unzip error for " + stJobFilePath);
              FarmWorksContentProvider contentProvider = FarmWorksContentProvider.getInstance(context);
              Job mJob= contentProvider.getJobInfoByJobId(lJobId);
              mJob.setStatus(AgDataStoreResources.JOB_STATUS_ENCODING);
              return;

          }
          Log.d(Constants.TAG_JOB_UPLOADER, "Starting upload of job to ACDC");
          stFileName = acdcApi.uploadFileData(stJobFilePath);
         
         if (stFileName != null) {
            if (uploadProcessListener != null) {
                Utils.deleteDirectory(Constants.getFlagStoreDir_Job(lJobId));
               uploadProcessListener.onJobUploadStatus(UPLAOD_FINISH, lJobId);
               jobUploaderQueue.clearJobUpdateStatus();
               uploadProcessListener.onJobUploadSuccess(stJobFilePath,lJobId);
            }
         }

      } catch (UnknownHostException e) {
         e.printStackTrace();
      } 
      catch (InvalidResponseException e) {
          e.printStackTrace();
          Log.i(Constants.TAG_JOB_UPLOADER,
                "InvalidResponse"+e.getMessage());
       }
      catch (TicketException e) {
         try {
             Log.e(Constants.TAG_JOB_UPLOADER,
                     "printStackTrace - TicketException");

             if (uploadProcessListener != null) {
                 uploadProcessListener.onJobUploadFailure(stJobFilePath,lJobId,
                       JobUploadListener.TICKET_EXCEPTION);
              }
            // refresh the ticket info
            acdcApi.registration();


         } catch (IllegalArgumentException e1) {
            Log.e(Constants.TAG_JOB_UPLOADER,
                  "printStackTrace - IllegalArgumentException");
            e1.printStackTrace();
         } catch (UnknownHostException e1) {
            Log.e(Constants.TAG_JOB_UPLOADER,
                  "printStackTrace - UnknownHostException");
            e1.printStackTrace();
           
         } catch (IOException e1) {
            if (!Environment.getExternalStorageState().equals(
                  Environment.MEDIA_MOUNTED)) {
               Log.i(Constants.TAG_JOB_UPLOADER,
                     "SDcard Unmounted - IOException upload failure - IOException1");
            } else {
                
               Log.e(Constants.TAG_JOB_UPLOADER,
                     "printStackTrace - IOException upload failure - IOException1");
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
            
            Log.i(Constants.TAG_JOB_UPLOADER,
                  "SDcard Unmounted - upload failure - IOException2");
         } else {
             File file=new File(stJobFilePath);
             
            Log.e(Constants.TAG_JOB_UPLOADER,
                  "IOException upload failure - "+file+" len:"+file.length()/(1024*1024)+" mb");
            e.printStackTrace();
         }
         if (uploadProcessListener != null) {
             uploadProcessListener.onJobUploadStatus( JobUploadProcess.UPLAOD_FAILER,lJobId);
          }
         return;
      } catch (FileNameException e) {

         Log.i(Constants.TAG_JOB_UPLOADER,
               "SDcard Unmounted - upload failure - FileNameException");
         e.printStackTrace();
         //!!!! patch !!!!!!
         if (uploadProcessListener != null) {
             uploadProcessListener.onJobUploadSuccess(stJobFilePath,lJobId);
          }
      }
   }
   
   public void stopUpload() {
      if (thread != null) {
         thread.interrupt();         
      }
   }

}

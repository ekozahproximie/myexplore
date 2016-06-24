package com.trimble.agmantra.jobsync.jobdownloader;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.jobsync.JobSyncManager;
import com.trimble.agmantra.jobsync.importer.JobImportQueue;
import com.trimble.agmantra.utils.ZipUtils;

import android.content.Context;
import com.trimble.agmantra.dbutil.Log;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class JobDownloadQueue implements JobDownloadListener {

   private static JobDownloadQueue                 jobDownloadQueue           = null;

   private Context                                 context                    = null;

   private LinkedBlockingQueue<JobDownloadProcess> jobDownloaderBlockingQueue = null;

   private boolean                                 isAsynMode                 = false;

   private boolean                                 isStart                    = false;
   private boolean                                 isStop                     = false;

   private HashMap<String, JobDownloadProcess>     jobDownloadMap             = null;

   private WorkerThread                            workerThread               = null;

   private static final String                     THREAD_NAME                = "jobDownloadQueue";

   private boolean                                 isFinishAtLast             = false;

   // private String                                  stFileId                   = null;

   // private String stJobFileName = null;

   private JobDownloadQueue(Context context, boolean isAsync) {
      this.context = context;
      this.isAsynMode = isAsync;

      jobDownloaderBlockingQueue = new LinkedBlockingQueue<JobDownloadProcess>(
            1);
      jobDownloadMap = new HashMap<String, JobDownloadProcess>(1);

      this.context = context;
   }

   public static synchronized JobDownloadQueue getInstance(Context context,
         boolean isAsync) {
      if (jobDownloadQueue == null) {
         jobDownloadQueue = new JobDownloadQueue(context, isAsync);
      }
      return jobDownloadQueue;
   }
   
  

   public void startWorkerThread() {
      if (!isStart) {
         workerThread = new WorkerThread();
         workerThread.setName(THREAD_NAME);

         workerThread.start();
      }

   }

   public void addToDownloaderQueue(String stFileID, String stJobFileName) {
      JobDownloadProcess downloadProcess = null;
      // this.stFileId = stFileID;
      if (stJobFileName != null && !stJobFileName.equals(Constants.ST_EMPTY)) {
         downloadProcess = new JobDownloadProcess(stJobFileName, isAsynMode,
               this, context);
         try {
            jobDownloaderBlockingQueue.put(downloadProcess);
            jobDownloadMap.put(stFileID, downloadProcess);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      startWorkerThread();
   }

   private class WorkerThread extends Thread {

      public WorkerThread() {
         isStart = true;
      }

      @Override
      public void run() {

         out: while (!isStop) {

            if (Thread.interrupted()) {
               break out;
            }
            JobDownloadProcess downloadProcess = null;

            try {
               downloadProcess = jobDownloaderBlockingQueue.take();

            } catch (InterruptedException e) {

               e.printStackTrace();
               break out;
            }

            if (downloadProcess != null) {
               downloadProcess.startDownloader();
            }
            if (isFinishAtLast && jobDownloaderBlockingQueue.isEmpty()) {
               break out;
            }
         }
         isStart = false;
      }
   }

   public void stopDownload() {
      isStop = true;
      Set<String> stJobFile = jobDownloadMap.keySet();
      for (String stFileId : stJobFile) {
         JobDownloadProcess jobRunning = jobDownloadMap.get(stFileId);
         if (jobRunning != null) {
            jobRunning.stopDownload();
         }
      }
      if (workerThread != null) {
         workerThread.interrupt();
         isStart = false;
      }
   }

   public void setFinishAtLast(boolean isFinishAtLast) {
      this.isFinishAtLast = isFinishAtLast;
   }

   public boolean isDownloadingInProcess() {

      boolean isEncoding = true;

      if (jobDownloaderBlockingQueue != null
            && !jobDownloaderBlockingQueue.isEmpty()) {
         isEncoding = true;
      }
      return isEncoding;
   }

   @Override
   public void onJobDownloadSuccess(String stFilePath) {
      jobDownloadMap.remove(stFilePath);
      // startImportProcess(stFilePath);
   }

   @Override
   public void onJobDownloadFailure(String stFileId, int iStatus) {
      Log.i("Upload Failure", "File upload failure");
      if (iStatus == JobDownloadListener.TICKET_EXCEPTION) {
         addToDownloaderQueue(stFileId, "");
      }
   }

   private void startImportProcess(String stFilePath ){
      
         
      String stImportFileLoc =ZipUtils.doUnZip(stFilePath);
     
      JobImportQueue jobImportQueue = JobImportQueue.getInstance(context, true);
      
      JobSyncManager.getInstance(context).addToImportList(stFilePath);
      
      jobImportQueue.addToImporterQueue(stImportFileLoc);
      
      jobImportQueue.startWorkerThread();
      
   }
}

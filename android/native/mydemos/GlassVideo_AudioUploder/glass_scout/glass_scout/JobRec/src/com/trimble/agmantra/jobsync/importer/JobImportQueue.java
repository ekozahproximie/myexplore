package com.trimble.agmantra.jobsync.importer;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.jobencoder.JobEncodeQueue;
import com.trimble.agmantra.jobsync.JobSyncManager;
import com.trimble.agmantra.utils.Utils;

import android.content.Context;
import android.os.Environment;
import com.trimble.agmantra.dbutil.Log;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class JobImportQueue implements JobImportListener {

   private static JobImportQueue                 jobImportQueue         = null;

   private Context                               context                = null;

   private LinkedBlockingQueue<JobImportProcess> jobImportBlockingQueue = null;

   private boolean                               isAsynMode             = false;

   private boolean                               isStart                = false;
   private boolean                               isStop                 = false;

   private HashMap<String, JobImportProcess>     jobImportMap           = null;

   private WorkerThread                          workerThread           = null;

   private static final String                   THREAD_NAME            = "jobImportQueue";

   private boolean                               isFinishAtLast         = false;

   private JobSyncManager                        syncManager            = null;

   private JobEncodeQueue                        encodeQueue            = null;

   private int                                   iJobImportCount        = 0;

   private JobImportQueue(Context context, boolean isAsync) {
      this.context = context;
      this.isAsynMode = isAsync;

      jobImportBlockingQueue = new LinkedBlockingQueue<JobImportProcess>(1);
      jobImportMap = new HashMap<String, JobImportProcess>(1);
     
      this.context = context;
   }

   public static synchronized JobImportQueue getInstance(Context context,
         boolean isAsync) {
      if (jobImportQueue == null) {
         jobImportQueue = new JobImportQueue(context, isAsync);
      }
      return jobImportQueue;
   }

   
   private boolean isSDCardMount() {
      return Environment.MEDIA_MOUNTED.equals(Environment
            .getExternalStorageState());
   }
   
   public void startWorkerThread() {
      if (isSDCardMount() && !isStart) {
         workerThread = new WorkerThread();
         workerThread.setName(THREAD_NAME);
         workerThread.start();
      }
   }

   /**
    * 
    * Add the process
    * 
    * @param stImportLoc
    */
   public void addToImporterQueue(String stImportLoc) {
      JobImportProcess importProcess = null;
      if (stImportLoc != null && !stImportLoc.equals(Constants.ST_EMPTY)) {
         importProcess = new JobImportProcess(stImportLoc, isAsynMode, this,
               context);
         try {
            iJobImportCount++;
            jobImportBlockingQueue.put(importProcess);
            jobImportMap.put(stImportLoc, importProcess);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }

      Log.i(Constants.TAG_JOB_IMPORTER, "File import - addToImporterQueue");
      startWorkerThread();
   }

   public boolean hasImportInProgress() {
      boolean isProgress = false;

      isProgress = (jobImportBlockingQueue != null)
            && (!jobImportBlockingQueue.isEmpty()) || iJobImportCount > 0;
      return isProgress;
   }

   private class WorkerThread extends Thread {

      public WorkerThread() {
         isStart = true;
      }

      @Override
      public void run() {

         Log.i(Constants.TAG_JOB_IMPORTER, "File import worker thread started");

         out: while (!isStop) {

            if (Thread.interrupted()) {
               
               break out;
            }
            JobImportProcess importProcess = null;
            
            synchronized (syncManager.syncMonitor) {

               try {
                  if (encodeQueue.hasEncodeInProgress())
                     syncManager.syncMonitor.wait();
               } catch (InterruptedException e) {
                  Log.i(Constants.TAG_JOB_IMPORTER, "InterruptedException - ");
                  e.printStackTrace();
               }

               try {
                  importProcess = jobImportBlockingQueue.take();

               } catch (InterruptedException e) {
                  e.printStackTrace();
                  break out;
               }

               if (importProcess != null) {
                  
                     importProcess.startImporter();
                  
                  if (!isAsynMode && !hasImportInProgress()) {
                     syncManager.syncMonitor.notify();
                     if (isFinishAtLast) 
                        stopImport();
                     break out;
                  }

               }

            }

         }
         isStart = false;
      }
   }

   /**
    * Stop All the Import threads
    */
   public void stopImport() {
      isStop = true;
      Set<String> stJobFile = jobImportMap.keySet();
      for (String stFileId : stJobFile) {
         JobImportProcess jobRunning = jobImportMap.get(stFileId);
         if (jobRunning != null) {
            jobRunning.stopImport();
         }
      }
      if (workerThread != null) {
         workerThread.interrupt();
         isStart = false;
      }
      synchronized (syncManager.syncMonitor) {
            syncManager.syncMonitor.notifyAll();
       }
      Log.i(Constants.TAG_JOB_IMPORTER, "File import stoped");
   }

   public void setFinishAtLast(boolean isFinishAtLast) {
      this.isFinishAtLast = isFinishAtLast;
   }

   /**
    * return the import process status
    * 
    * @return
    */

   @Override
   public void onJobImportSuccess(String stImpLoc) {

      jobImportMap.remove(stImpLoc);
      iJobImportCount--;
      JobSyncManager.getInstance(context).removeFromImportList(stImpLoc);

      Utils.deleteDirectory(stImpLoc);
      Log.i(Constants.TAG_JOB_IMPORTER, "File import completed");
      stop_notify();
   
   }
   private void stop_notify(){
      if (!hasImportInProgress()) {
         JobSyncManager.getInstance(context).startEncodeProcess();
         synchronized (syncManager.syncMonitor) {
            if(isAsynMode ){
               syncManager.syncMonitor.notifyAll();
              
            }
         }
         if(isFinishAtLast){
            stopImport();
         }
      }
   }

   @Override
   public void onJobImportFailure(String stFileId, int iStatus) {
      Log.i(Constants.TAG_JOB_IMPORTER, "File upload failure");
      if (iStatus == JobImportListener.IO_EXCEPTION) {
         iJobImportCount--;
         // When failure happend we have to notify for encoder thread - Testings
         // if (!isImportInProcess()) {

         // }
         // addToImporterQueue(stFileId);
         stop_notify();
      }
   }

   public void setJobEncodeQueue(JobEncodeQueue jobEncodeQueue) {
      encodeQueue =jobEncodeQueue;
      
   }

   public void setSyncManager(JobSyncManager jobSyncManager) {
      syncManager=jobSyncManager;
      
   }

}

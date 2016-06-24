package com.trimble.agmantra.jobencoder;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.dbutil.Log;
import com.trimble.agmantra.jobsync.JobSyncManager;
import com.trimble.agmantra.jobsync.JobSyncService;
import com.trimble.agmantra.jobsync.importer.JobImportQueue;
import com.trimble.agmantra.utils.Utils;
import com.trimble.agmantra.utils.ZipUtils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class JobEncodeQueue implements JobEncoderProcessListener {

   private LinkedBlockingQueue<JobEncoderProcess> jobEncoderBlockingQueue = null;

   private boolean                                isJobInAsynMode         = false;

   private boolean                                isStop                  = false;

   private HashMap<Long, JobEncoderProcess>       jobMap                  = null;

   private WorkerThread                           workerThread            = null;

   private Context                                context                 = null;

   private static final String                    THREAD_NAME             = "jobenQueue";

   private boolean                                isStart                 = false;

   private boolean                                isFinishAtLast          = false;

   private static JobEncodeQueue                  jobEncoderQueue         = null;
   
   private JobSyncManager syncManager = null;
   
   private int iJobEncodeCount=0; 

   private JobImportQueue importQueue =null;
   
   private Toast toast = null;
   
   public static synchronized JobEncodeQueue getInstance(Context context,
         boolean isJobInAsynMode ) {
      if (jobEncoderQueue == null) {
         jobEncoderQueue = new JobEncodeQueue(isJobInAsynMode, context);
      }
      return jobEncoderQueue;
   }

   private JobEncodeQueue(boolean isJobInAsynMode, Context context) {
      this.isJobInAsynMode = isJobInAsynMode;
      jobEncoderBlockingQueue = new LinkedBlockingQueue<JobEncoderProcess>(1);
      jobMap = new HashMap<Long, JobEncoderProcess>(1);
      this.context = context;
          
   }

   private boolean isSDCardMount() {
      return Environment.MEDIA_MOUNTED.equals(Environment
            .getExternalStorageState());
   }

   public boolean hasEncodeInProgress(){
      boolean isProgress=false;
      
      
      isProgress=(jobEncoderBlockingQueue != null )&& (!jobEncoderBlockingQueue.isEmpty() )||  iJobEncodeCount > 0;
      return isProgress;
   }
   public void startWorkerThread() {
      if (isSDCardMount() && !isStart) {

         workerThread = new WorkerThread();
         workerThread.setName(THREAD_NAME);
         workerThread.start();

      }
   }

   public void addJobToEncode(long lJobID) {
      JobEncoderProcess encoderProcess = null;
      if (lJobID != -1) {

         encoderProcess = new JobEncoderProcess(lJobID, isJobInAsynMode, this,
               context);
         if (!jobMap.containsKey(lJobID)) {
            try {
               iJobEncodeCount++;
               jobEncoderBlockingQueue.put(encoderProcess);
               jobMap.put(lJobID, encoderProcess);
               Log.i(Constants.TAG_JOB_ENCODER, "addJobToEncode - Jobid-->"
                     + lJobID);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      }
      startWorkerThread();
   }

   private class WorkerThread extends Thread {

      public WorkerThread() {
         isStart = true;
         isStop=false;
      }

      @Override
      public void run() {

         out: while (!isStop) {

            if (Thread.interrupted()) {
               break out;
            }
            JobEncoderProcess encoderProcess = null;
            
            try {
               
              //synchronized (/*syncManager.syncMonitor*/) {                
                  /*if (importQueue.hasImportInProgress()) {
                     try {
                        syncManager.syncMonitor.wait();
                     } catch (InterruptedException e) {
                        Log.i(Constants.TAG_JOB_ENCODER,
                              "InterruptedException - ");
                        e.printStackTrace();
                     }
                  }*/
                  encoderProcess = jobEncoderBlockingQueue.take();

                  if (encoderProcess != null) {
                     encoderProcess.startEncode();
                  }
               //}

               if (!isJobInAsynMode && !hasEncodeInProgress()) {
                  //syncManager.syncMonitor.notifyAll();
                  if (isFinishAtLast) stopEncode();
                  break out;
               }
            } catch (InterruptedException e) {

               e.printStackTrace();
               break out;
            }
         }
         isStart = false;

      }
   }

   public void stopEncode() {
      isStop = true;
      Set<Long> setJobID = jobMap.keySet();
      for (Long lJobID : setJobID) {
         JobEncoderProcess jobRunning = jobMap.get(lJobID);
         if (jobRunning != null) { 
            jobRunning.stopEncode();
         }
      }
      if (workerThread != null) {
         workerThread.interrupt();
      }
      
      clearQueue();
      if(JobSyncService.isApplicationClosed){
         stopSerivce();
      }
           
   }

   public void setFinishAtLast(boolean isFinishAtLast) {
      this.isFinishAtLast = isFinishAtLast;
   }
   /**
    * return the import process status
    * 
    * @return
    */
  

   private void updateJobInfo(long lJobID, boolean isFinished) {

      FarmWorksContentProvider dbMangr = FarmWorksContentProvider
            .getInstance(context);
      com.trimble.agmantra.entity.Job mJob = dbMangr.getJobInfoByJobId(lJobID);
      if (isFinished) {

         Map<Integer, String> mapPath = JobEncoder.getInstance(context)
               .getTemplatePath();
        
         Log.i(Constants.TAG_JOB_ENCODER, "Job Zip Files Count"+mapPath.size());
         
       
         StringBuilder stAllZipFiles = new StringBuilder();

         Set<Integer>  TemplateIDSet= mapPath.keySet();
         for (Integer iTemplate : TemplateIDSet) {
             
             String stSrcPath =mapPath.get(iTemplate);
       
            String stZipFileName = null;
            StringBuilder stZipFile = new StringBuilder();

            if ((iTemplate <= AgDataStoreResources.ATT_TYPE_NDVI_REF && iTemplate >= AgDataStoreResources.ATT_TYPE_INSECTS)
                  || iTemplate == AgDataStoreResources.ATT_TYPE_IMAGE) {
               stZipFileName = Utils.getScoutJobFilename(lJobID, iTemplate,
                     JobEncoder.getInstance(null).getJobTime(), true);

            } else {
               stZipFileName = Utils.getBoundaryJobFilename(lJobID, JobEncoder
                     .getInstance(null).getJobTime(),dbMangr);

            }
            if(stZipFileName==null){
               return;
            }

            try {
               stZipFile.append(Utils.getZipFileLoc(android.os.Build.MODEL));
            } catch (FileNotFoundException e) {
               e.printStackTrace();
               if(e.getMessage().equals(Utils.UNABLE_TO_CREATE)){
                  Log.i(Constants.TAG_JOB_ENCODER,"No memory space in sdcard - updateJobInfo" );
                  return;
               }
            }
            stZipFile.append(File.separator);

            stZipFile.append(stZipFileName);
            
            stZipFile.append(Constants.ZIP_FILE_EXTENS);
            
            boolean isValiZipFile = ZipUtils.doZip(stSrcPath, stZipFile.toString(), true);
            
            if (isValiZipFile) {
               Log.i(Constants.TAG_JOB_ENCODER,"Valid zip file-->" +stZipFile.toString());
               stAllZipFiles.append(stZipFile.toString() + Constants.ST_COMMA);
            } else {               
               Log.i(Constants.TAG_JOB_ENCODER,"Not a Valid zip file-->" +stZipFile.toString()+"-- addJobToEncode again ");
               addJobToEncode(lJobID);
               return;
            }
         }
         mJob.setJobfilepath(stAllZipFiles.toString());
         Log.i(Constants.TAG_JOB_ENCODER, stAllZipFiles.toString());
         JobEncoder.getInstance(null).setClearTemplatePath();
         mJob.setStatus(AgDataStoreResources.JOB_STATUS_UNUPLOADED);
         
         startJobUpload(lJobID);

      } 

      dbMangr.updateJob(mJob);
    
   }

   @Override
   public void jobEncodeComplete(long lJobID) {

      jobMap.remove(lJobID);
      updateJobInfo(lJobID, true);
      iJobEncodeCount--;
      
      if (toast != null) {
         synchronized (toast) {
            toast=null;
         }
      }
      
     //stop_notify();
      stopSerivce();
   }
   
   private void stopSerivce(){
      if(!hasEncodeInProgress() && JobSyncService.isApplicationClosed
            && context != null && ! Utils.isInternetConnection(context)  ){
         JobSyncService.stopService(context);
      } else if(!hasEncodeInProgress() && JobSyncService.isApplicationClosed
            && context != null && !  isSDCardMount() ){
         JobSyncService.stopService(context);
      }
   }
   
   private void stop_notify(){
      if(!hasEncodeInProgress() ){              
         
         synchronized (syncManager.syncMonitor) {
            if(isJobInAsynMode ){
               syncManager.syncMonitor.notifyAll();              
            }
         }
         // JobSyncManager.getInstance(context).startImportProcess();
         if(isFinishAtLast){
            stopEncode();
         }
      }
   }

   @Override
   public void jobEncodeFailer(long lJobid, int iStatus,String stStatus) {
      try {
         if (iStatus == JobEncoderProcessListener.SD_CARD_REMOVED) {
            iJobEncodeCount--;

            Log.i(Constants.TAG_JOB_ENCODER, "JobEncode failure for the job -"
                  + lJobid);
            stopSerivce();
            // addJobToEncode(lJobid);
            // updateJobInfo(lJobid, false);
            // stop_notify();
         }
         if(stStatus!=null && stStatus.equals(Constants.SDCARD_NO_SPACE)){
            
            if(toast != null){
               toast.cancel();
               return;
            } 
            
            if (toast != null) {
               synchronized (toast) {
                  toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
                  toast.show();
               }
            }
         }
      } catch (Exception e) {
         iJobEncodeCount = 0;
         jobEncoderBlockingQueue.clear();
         workerThread.interrupt();
         e.printStackTrace();
         // stop_notify();
      }

   }
   
   private void startJobUpload(long lJobId){
      
	   Log.d (Constants.TAG_JOB_SYNC_SERVICE, "startJobUpload ...");
      
      if (Utils.isInternetConnection(context)) {
          Log.i(Constants.TAG_JOB_SYNC_SERVICE,
                "Network Connection available");

          syncManager.addEncodedJobInUploaderQueue(lJobId,true);

       } else {
          Log.i(Constants.TAG_JOB_SYNC_SERVICE,
                "Network Connection is not available");
       }
      
   }

   public void setJobImportQueue(JobImportQueue jobImportQueue) {
     
      importQueue=jobImportQueue;
   }

   public void setSyncManager(JobSyncManager jobSyncManager) {
     syncManager=jobSyncManager;

    
   }
   
  public void clearQueue(){
     if(jobEncoderBlockingQueue!=null){
        jobEncoderBlockingQueue.clear();
        if(jobMap!=null){
           jobMap.clear();
        }
     }
        
  }

}

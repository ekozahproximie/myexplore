package com.trimble.agmantra.jobsync.jobuploader;

import com.trimble.agmantra.acdc.ACDCApi;
import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.dbutil.Log;
import com.trimble.agmantra.entity.Job;
import com.trimble.agmantra.jobencoder.JobEncodeQueue;
import com.trimble.agmantra.jobsync.JobSyncService;
import com.trimble.agmantra.utils.Utils;

import android.content.Context;
import android.os.Environment;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

public class JobUploaderQueue implements JobUploadListener {

   private static JobUploaderQueue               jobUploadQueue           = null;

   private Context                               context                  = null;

   private LinkedBlockingQueue<JobUploadProcess> jobUploaderBlockingQueue = null;

   private boolean                               isAsynMode               = false;

   private boolean                               isStart                  = false;
   private boolean                               isStop                   = false;

   private HashMap<String, JobUploadProcess>     jobUploadMap             = null;
   
   

   private WorkerThread                          workerThread             = null;

   private static final String                   THREAD_NAME              = "jobUploadQueue";

   private boolean                               isFinishAtLast           = false;
   
   private JobEncodeQueue encodeQueue = null;
   
   private int iJobUploadCount=0;
   
   private int iJobStatus=-1;
   
   private long lCurrJobID=-1;

   // private long                                  lJobId                   = 0;
   // private String stJobFileName = null;
   
   private JobUploaderQueue(Context context, boolean isAsync) {
      this.context = context;
      this.isAsynMode = isAsync;

      jobUploaderBlockingQueue = new LinkedBlockingQueue<JobUploadProcess>(20);
      jobUploadMap = new HashMap<String, JobUploadProcess>(1);
      
      this.context = context;
   }

   public static synchronized JobUploaderQueue getInstance(Context context,
         boolean isAsync) {
      if (jobUploadQueue == null) {
         jobUploadQueue = new JobUploaderQueue(context, isAsync);
      }
      return jobUploadQueue;
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

   public void addToUploderQueue(long lJobId, String stJobFileName) {
      JobUploadProcess uploadProcess = null;
      // this.lJobId = lJobId;
      if (stJobFileName != null && !stJobFileName.equals(Constants.ST_EMPTY)) {
         uploadProcess = new JobUploadProcess(stJobFileName,lJobId, isAsynMode, this,
               context,this);
       
             
            if(! jobUploadMap.containsKey(stJobFileName))
            {
                
                jobUploadMap.put(stJobFileName, uploadProcess);
                jobUploaderBlockingQueue.offer(uploadProcess);
                iJobUploadCount++;
            }
            
        
         startWorkerThread();
      }
    
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
            if(! Utils.isInternetConnection(context)){
               Log.i(Constants.TAG_JOB_UPLOADER,
                     "Job upload WorkerThread no network ");    
               stopUpload();
               break out;
            }
            JobUploadProcess uploadProcess = null;


               try {
                  uploadProcess = jobUploaderBlockingQueue.take();
               } catch (InterruptedException e) {
                
                  e.printStackTrace();
                  stopUpload();
                  break out;
               }

            
            
            if (uploadProcess != null) {
               uploadProcess.startUploader();
            }
            if (isFinishAtLast && jobUploaderBlockingQueue.isEmpty()) {
               break out;
            }
         }
         isStart = false;
      }
   }
   
   public void stopUpload() {
      isStop = true;
      Set<String> stJobFile = jobUploadMap.keySet();
      for (String stJobFileName : stJobFile) {
         JobUploadProcess jobRunning = jobUploadMap.get(stJobFileName);
         if (jobRunning != null) {
            jobRunning.stopUpload();
         }
      }
      if (workerThread != null) {
         workerThread.interrupt();
      }
     
      clearQueue();
      if(JobSyncService.isApplicationClosed){
         stopService();
      }
   }

   public void setFinishAtLast(boolean isFinishAtLast) {
      this.isFinishAtLast = isFinishAtLast;
   }

   @Override
   public void onJobUploadSuccess(String stJobFilePath,long lJobId) {
       Log.i(Constants.TAG_JOB_UPLOADER,
               "onJobUploadSuccess for"+stJobFilePath);    
      updateJobInDB(stJobFilePath,lJobId);
   
      iJobUploadCount--;
      jobUploadMap.remove(stJobFilePath);
      if(iJobUploadCount == 0 && ! hasUploadInProgress()){
          onAllJobUploaded(); 
      }
          if(mJobUploadListener != null){
              for (JobUploadListener uploadListener : mJobUploadListener) {
                  if(uploadListener != null){
                      uploadListener.onJobUploadSuccess(stJobFilePath,lJobId);
                  }
              }
              
          }
      
      try {
        
         Utils.deleteFile(stJobFilePath,Constants.IS_MOVE_UPLOADED_FILE);
    
      } catch (FileNotFoundException e) {
        
         e.printStackTrace();
      }
   }

   @Override
   public void onJobUploadFailure(String stJobFilePath,long lJobId, int iStatus) {
      
      Log.i(Constants.TAG_JOB_UPLOADER,
            "SDcard Unmounted - IOException upload failure - onJobUploadFailure");
      //onJobUploadStatus(JobUploadProcess.UPLAOD_FAILER, lJobId);
      iJobUploadCount--;
      addToUploderQueue(lJobId, stJobFilePath);
      
   }
   
   private void stopService(){
      
      if((context != null && JobSyncService.isApplicationClosed && encodeQueue != null &&
            !encodeQueue.hasEncodeInProgress() && iJobUploadCount <= 0 )){
          JobSyncService.stopService(context); 
      }else if((context != null && JobSyncService.isApplicationClosed && encodeQueue != null &&
            !encodeQueue.hasEncodeInProgress() && iJobUploadCount >= 0 ) && !Utils.isInternetConnection(context)){
         JobSyncService.stopService(context);
      }
      
   }
   
   private void updateJobInDB(String stJobFilePath, long lJobId) {

      FarmWorksContentProvider fwDbManager = FarmWorksContentProvider
            .getInstance(context);
      Job job = fwDbManager.getJobInfoByJobId(lJobId);
      if(job != null){
      String stJobFileList = job.getJobfilepath();

      if (stJobFileList != null && !stJobFileList.equals(Constants.ST_EMPTY)
            && !stJobFileList.equals(Constants.ST_COMMA)) {
         stJobFileList = stJobFileList.replace(stJobFilePath+Constants.ST_COMMA,Constants.ST_EMPTY);
      } 
      
      if(stJobFileList != null && stJobFileList.equals(Constants.ST_EMPTY)) {
         job.setStatus(AgDataStoreResources.JOB_STATUS_UPLOADED);
      }

      job.setJobfilepath(stJobFileList);

      fwDbManager.updateJob(job);
      }
   }
   
  /* private void testOldMethod(){
      String stData[]=stJobFileList.split(",");
      StringBuffer buffer =null;
   if(stData != null){
      buffer = new StringBuffer();
      for (int i = 0; i < stData.length; i++) {
         String stFileName = stData[i];
         if(stFileName != null){
            stFileName=stFileName.trim();
         if(!stFileName.equals(Constants.ST_EMPTY) && !stFileName.equals(stJobFilePath) ){
            buffer.append(stFileName+",");
         }
         }
      }
      stJobFileList=buffer.toString();
      }else{
         
        stJobFileList=Constants.ST_EMPTY;
    }
    
      
      if (stJobFileList == null || stJobFileList.equals(Constants.ST_EMPTY)) {
         job.setStatus(AgDataStoreResources.JOB_STATUS_UPLOADED);
      } 
   }*/
   
   public boolean hasUploadInProgress(){
      boolean isProgress=false;            
      isProgress = (jobUploaderBlockingQueue != null )&& (!jobUploaderBlockingQueue.isEmpty() );
      return isProgress;
   }
   
   public void clearQueue(){
       Log.i(ACDCApi.TAG, "job Uploader clearQueue");
      if(jobUploaderBlockingQueue!=null){
         jobUploaderBlockingQueue.clear();
         if(jobUploadMap!=null){
            jobUploadMap.clear();
         }
         clearJobUpdateStatus();
         if(mJobUploadListener != null ){
           for (JobUploadListener iterable_element : mJobUploadListener) {
               if(iterable_element != null)
               iterable_element.onJobUploadStatus(JobUploadProcess.WAIT_FOR_NETWORK, -1);
        }
             //mJobUploadListener.clear();
         //    mJobUploadListener=null;
         }
      }
   }
   
  
public void setEncodeQueue(JobEncodeQueue encodeQueue) {
   this.encodeQueue = encodeQueue;
}
private Vector<JobUploadListener> mJobUploadListener=null;
/**
 * @param jobUploadListener the jobUploadListener to set
 */
public boolean addJobUploadListener(JobUploadListener jobUploadListener) {
    boolean isAdded=false;
    if(jobUploadListener == null){
        return isAdded;
    }
    if(mJobUploadListener == null ){
        mJobUploadListener =new Vector<JobUploadListener>(1); 
    }
    
    isAdded=mJobUploadListener.add(jobUploadListener);
    for (JobUploadListener uploadListener : mJobUploadListener) {
        if(lCurrJobID != -1 && iJobStatus != -1){
        uploadListener.onJobUploadStatus(iJobStatus, lCurrJobID);
        }
    }
    
    if(iJobUploadCount == 0 && ! hasUploadInProgress()){
       onAllJobUploaded(); 
   }
    return isAdded;
    
}

public boolean removeJobUploadListener(JobUploadListener jobUploadListener) {
    boolean isRemoved=false;
    if(jobUploadListener == null){
        return isRemoved;
    }
    if(mJobUploadListener != null){
        isRemoved=mJobUploadListener.remove(jobUploadListener);

        }
    if(mJobUploadListener != null && mJobUploadListener.size() == 0){
        mJobUploadListener.clear();
        mJobUploadListener=null;
    }
    return isRemoved;
}
 
/* (non-Javadoc)
 * @see com.trimble.agmantra.jobsync.jobuploader.JobUploadListener#onAllJobUploaded()
 */
@Override
public void onAllJobUploaded() {
    
    if(mJobUploadListener != null){
        for (JobUploadListener uploadListener : mJobUploadListener) {
            if(uploadListener != null){
                uploadListener.onAllJobUploaded();
            }
        }
        
    }
}

/* (non-Javadoc)
 * @see com.trimble.agmantra.jobsync.jobuploader.JobUploadListener#onJobUploadStatus(int, long)
 */
@Override
public void onJobUploadStatus(int iStatus, long lJobID) {
    
    if(mJobUploadListener != null){
        for (JobUploadListener uploadListener : mJobUploadListener) {
            if(uploadListener != null){
                uploadListener.onJobUploadStatus(iStatus,lJobID);
            }
        }
        
    }
}

public void setCurrentJobUploadStatus(int iJobStatus,long lJobID){
    this.iJobStatus=iJobStatus;
    this.lCurrJobID=lJobID;
}

public void clearJobUpdateStatus(){
    iJobStatus=-1;
    lCurrJobID=-1;
}
   
}

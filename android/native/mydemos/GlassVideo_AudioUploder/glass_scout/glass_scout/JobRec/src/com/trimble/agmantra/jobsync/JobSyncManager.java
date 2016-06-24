
package com.trimble.agmantra.jobsync;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.dbutil.Log;
import com.trimble.agmantra.entity.Job;
import com.trimble.agmantra.jobencoder.JobEncodeQueue;
import com.trimble.agmantra.jobsync.importer.JobImportQueue;
import com.trimble.agmantra.jobsync.jobdownloader.JobDownloadManager;
import com.trimble.agmantra.jobsync.jobuploader.JobUploaderQueue;
import com.trimble.agmantra.utils.Utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class JobSyncManager {

    private static JobSyncManager jobSyncMngr = null;

    private Context context = null;

    public JobUploaderQueue jobUploadQueue = null;

    public JobEncodeQueue jobEncodeQueue = null;

    public JobImportQueue jobImportQueue = null;

    private JobDownloadManager jobDownloadManager = null;

    private FarmWorksContentProvider fwDbManager = null;
    
    public  static final int ALL_JOBS = -1; 

    // REQUIRED PARAMETER
    private static final int TIME_INTERVAL = 4 * 30000; // 2 * 60000;

    // Timer intervals in millisec
    private Vector<String> vecImportFilePath = null;

    public Object syncMonitor = null;
    
    private JobSyncManager(Context context) {
        this.context = context;
        fwDbManager = FarmWorksContentProvider.getInstance(context);
        syncMonitor = new Object();
        initSynchProcess();
    }

    /**
     * @param context
     * @return
     */
    public static JobSyncManager getInstance(Context context) {

        if (jobSyncMngr == null) {
            jobSyncMngr = new JobSyncManager(context);
        }
        return jobSyncMngr;
    }
    
    private void initSynchProcess(){
       jobEncodeQueue = JobEncodeQueue.getInstance(context, false);
       jobUploadQueue = JobUploaderQueue.getInstance(context, false);
       jobImportQueue = JobImportQueue.getInstance(context, true);

       jobEncodeQueue.setSyncManager(this);
       jobImportQueue.setSyncManager(this);
       
       jobUploadQueue.setEncodeQueue(jobEncodeQueue);
       
       jobImportQueue.setJobEncodeQueue(jobEncodeQueue);
       jobEncodeQueue.setJobImportQueue(jobImportQueue);

       //jobImportQueue.startWorkerThread();
       //jobEncodeQueue.startWorkerThread();

       //jobUploadQueue.startWorkerThread();
       jobDownloadManager = JobDownloadManager.getInstance(context);
    }

    public void startSyncProcess() {
        
        Log.i(Constants.TAG_JOB_ENCODER, "startSyncProcess");
                
        String stSDCardState = Environment.getExternalStorageState();

        if (stSDCardState.equals(Environment.MEDIA_MOUNTED)) {

            addAllFinishedJob();
            // updateJobImportQueue();

            if (Utils.isInternetConnection(context)) {
                Log.i(Constants.TAG_JOB_SYNC_SERVICE, "Network Connection available");

                addEncodedJobInUploaderQueue(ALL_JOBS, false);
                // TODO start download process
                // startDowloaderProcess();

            } else {
                Log.i(Constants.TAG_JOB_SYNC_SERVICE, "Network Connection is not available");
            }

        } else {
            Log.i(Constants.TAG_JOB_SYNC_SERVICE,
                    "Sdcard Unmounted Job Sync service Canbot started!");
        }
        Log.i(Constants.TAG_JOB_ENCODER, "end startSyncProcess");
    }

    /**
     * Start Encode process worker thread
     */
    public void startEncodeProcess() {
        if (jobEncodeQueue != null) {
            jobEncodeQueue.startWorkerThread();
        }
    }

    // Upload process if network is available
    public void updateEncoderQueue(final long lJobId) {
       
       Thread thread=new Thread(){
        
        @Override
        public void run() {
            Log.i(Constants.TAG_JOB_ENCODER, "updateEncoderQueue JobId-->"+lJobId);

            if (isJobValid(lJobId, AgDataStoreResources.JOB_STATUS_ENCODING)) {
                jobEncodeQueue.addJobToEncode(lJobId);
            }
        } 
       };
       thread.setName("updateEncoderQueue by jobid");
       thread.start();

    }
    
    public void addAllFinishedJob(){
        Thread thread = new Thread(){
            
            @Override
            public void run() {
                addFinishedJobInEncodeQueue();
            }
        };
        thread.setName("addFinishedJobInEncodeQueue thread");
        thread.start();
    }
    
    /**
     * Add finished job in jobencoder queue
     */
    private synchronized void addFinishedJobInEncodeQueue() {
              
       Log.i(Constants.TAG_JOB_ENCODER, "addFinishedJobInEncodeQueue");
       
      if (jobEncodeQueue != null) {

         List<Long> jobIdList = getJobListTobeEncode();
         if (jobIdList != null && jobIdList.size() > 0) {
            for (Long jobID : jobIdList) {
               jobEncodeQueue.addJobToEncode(jobID);
            }
         }
      }
    }

    // Stop Encode process if network is not available
    public void stopEncodeProcess() {
        if (jobEncodeQueue != null) {
            jobEncodeQueue.stopEncode();
        }

    }

    /**
     * Start upload process worker thread
     */
    public void startImportProcess() {

        if (jobImportQueue != null) {
            jobImportQueue.startWorkerThread();
        }
        updateJobImportQueue();
    }

    private void updateJobImportQueue() {
        if (vecImportFilePath != null && vecImportFilePath.size() > 0) {
            for (String stPath : vecImportFilePath) {
                if (jobImportQueue != null) {
                    jobImportQueue.addToImporterQueue(stPath);
                }
            }
        }
    }

    public void stopImportProcess() {
        if (jobImportQueue != null) {
            jobImportQueue.stopImport();
        }
    }

    /**
     * Start upload process worker thread
     */
    public void startUploadProcess() {
        if (jobUploadQueue != null) {
            jobUploadQueue.startWorkerThread();
        }
    }


    /**
     * Add finished job in jobencoder queue
     */
    public void addEncodedJobInUploaderQueue(final long lJobId,final boolean isUpdate) {
        Log.d(Constants.TAG_JOB_UPLOADER, "addEncodedJobInUploaderQueue");

        Thread thread= new Thread(){
         
        @Override
        public void run() {
            Log.d(Constants.TAG_JOB_UPLOADER, "running Thread");

            addToUploaderQueue( lJobId,  isUpdate);
            }  
        };
        thread.setName("Encode Job add to upload queue");
        thread.start();
       
    }
   
    private void addToUploaderQueue(long lJobId, boolean isUpdate){
        Log.d(Constants.TAG_JOB_UPLOADER, "To upload items");

        if (jobUploadQueue != null && Utils.isInternetConnection(context)) {

            LinkedHashMap<String, Long> jobUploadMap = null;

            if (!isUpdate) {
                jobUploadQueue.clearQueue();
               jobUploadMap = getAllJobFileListTobeUpload();
            } else {
                jobUploadMap = getJobFileListTobeUpload(lJobId);
            }
            
            Log.d(Constants.TAG_JOB_UPLOADER, "To upload " + jobUploadMap.size() + " items, lJobId - " + Long.toString(lJobId));

            if (jobUploadMap != null && jobUploadMap.size() > 0) {
                
                Set<String> fileSet=jobUploadMap.keySet();
                for (String stUploadedFile : fileSet) {
                   Long lJobiD= jobUploadMap.get(stUploadedFile);
                   if(lJobiD != null){
                	   jobUploadQueue.addToUploderQueue( lJobiD,stUploadedFile);
                   }
                }
              
            }
        }
        else {
        	Log.e(Constants.TAG_JOB_UPLOADER, "jobUploadQueue is null");
        }
    }
    // Stop Upload process if network is not available
    public void stopUploadProcess() {
        if (jobUploadQueue != null) {            
            jobUploadQueue.stopUpload();
        }

    }

    /**
     * List of jobs to be Encode
     * 
     * @return
     */
    private List<Long> getJobListTobeEncode() {
        List<Long> jobIdList = null;
        if (fwDbManager != null) {
            List<Job> jobList = fwDbManager.getFinishedJobs();
            if (jobList != null && jobList.size() > 0) {
                jobIdList = new ArrayList<Long>();
                for (Job job : jobList) {
                    jobIdList.add(job.getId());
                }
            }
        }
        return jobIdList;
    }

    /**
     * List of jobs to be upload
     * 
     * @return
     */
    private LinkedHashMap<String, Long> getAllJobFileListTobeUpload() {

        LinkedHashMap<String, Long> jobUploadMap = null;

        if (fwDbManager != null) {

            List<Job> jobList = fwDbManager.getJobsToBeUploaded();
            

            if (jobList != null && jobList.size() > 0) {
            	jobUploadMap = new LinkedHashMap<String, Long>(jobList.size());
                Comparator<Job> timeComparator = new Comparator<Job>() {

                    @Override
                    public int compare(Job lhs, Job rhs) {
                       return lhs.getStarttime().compareTo(rhs.getStarttime());
                    }
                };
                Collections.sort(jobList, timeComparator);
                for (Job job : jobList) {

                    String stJobFileList = job.getJobfilepath();

                    if (stJobFileList != null && !stJobFileList.equals(Constants.ST_EMPTY)) {

                        String[] stFileArr = stJobFileList.split(Constants.ST_COMMA);

                        for (int i = 0; i < stFileArr.length; i++) {
                            jobUploadMap.put(stFileArr[i], job.getId());
                        }
                    }
                }
            } 
            
            else {
               
               //Log.w(Constants.TAG_JOB_UPLOADER, "No file in DB to be upload but available from_device folder");
               
               /* String stUploadFilePath=null;
                try {
                    stUploadFilePath = Utils.getZipFileLoc(android.os.Build.MODEL);
                    File file = new File(stUploadFilePath);
                    File[] fileList = null;
                    if (file.isDirectory()) {
                        fileList = file.listFiles(new FileFilter() {
                            
                            @Override
                            public boolean accept(File pathname) {
                                return (pathname.getName().endsWith(".zip"));
                                 
                            }
                        });
                    }
                    if (fileList != null && fileList.length > 0) {
                        for (File fileDet : fileList) {
                            
                        
                            jobUploadMap.put(fileDet.getAbsolutePath(), fileDet.length()); 
                        }
                    }
                } catch (FileNotFoundException e) {
                   
                    e.printStackTrace();
                }

             */ 
            }
        }
        return jobUploadMap;
    }

    /**
     * List of jobsfile to be upload
     * 
     * @return
     */
    private LinkedHashMap<String, Long> getJobFileListTobeUpload(long lJobId) {

        LinkedHashMap<String, Long> jobUploadMap = null;

        if (isJobValid(lJobId, AgDataStoreResources.JOB_STATUS_UNUPLOADED)) {

            String stJobFileList = null;

            String[] stFileArr = null;

            if (fwDbManager != null) {

                Job job = fwDbManager.getJobInfoByJobId(lJobId);

                jobUploadMap = new LinkedHashMap<String, Long>();

                stJobFileList = job.getJobfilepath();

                if (stJobFileList != null && !stJobFileList.equals(Constants.ST_EMPTY)) {

                    stFileArr = stJobFileList.split(Constants.ST_COMMA);

                    for (int i = 0; i < stFileArr.length; i++) {
                        jobUploadMap.put(stFileArr[i], job.getId());
                    }
                }
            }
        }
        return jobUploadMap;
    }

    /**
     * Check the Job is Finished or not to encode
     * 
     * @param lJobId
     * @return
     */
    private boolean isJobValid(long lJobId, int iJobStatus) {

        boolean isTobeEncode = false;

        if (fwDbManager != null && lJobId != -1) {

            Job encodeJob = fwDbManager.getJobInfoByJobId(lJobId);

            if (encodeJob != null) {
                Integer status=encodeJob.getStatus();
                if (status != null && status == iJobStatus) {
                    isTobeEncode = true;
                }
            }
        }
        return isTobeEncode;
    }

    private Handler jobDownloadhandler = new Handler();

    public void startDowloaderProcess() {
        if (jobDownloadhandler != null) {
            jobDownloadhandler.post(jobDownload);
        }
    }

    /**
     * Job downloader thread
     */
    private Runnable jobDownload = new Runnable() {

        public void run() {
            boolean isReturn = false;
            if (jobDownloadManager != null) {
                // isReturn = jobDownloadManager.startDownlaod();
                if (!isReturn) {
                    Log.i(Constants.TAG_JOB_DOWNLOADER, "Donwloader failure");

                }
            }

            jobDownloadhandler.postDelayed(jobDownload, TIME_INTERVAL);
        }

    };

    // Stop Location Tracking data
    public void stopDownloadProcess() {

        if (jobDownloadhandler != null) {
            jobDownloadhandler.removeCallbacks(jobDownload);
        }

        if (jobDownloadManager != null) {
            jobDownloadManager.stopDownload();
        }

    }

    public void addToImportList(String stImportFilePath) {
        if (vecImportFilePath == null) {
            vecImportFilePath = new Vector<String>();
        }
        vecImportFilePath.add(stImportFilePath);
    }

    public void removeFromImportList(String stImportFilePath) {
        if (vecImportFilePath != null && vecImportFilePath.size() > 0) {
            vecImportFilePath.remove(stImportFilePath);
        }
    }
    
    public void clear(){
       jobSyncMngr = null;
    }

}

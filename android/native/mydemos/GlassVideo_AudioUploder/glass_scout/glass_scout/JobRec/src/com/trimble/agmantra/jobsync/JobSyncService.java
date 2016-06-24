package com.trimble.agmantra.jobsync;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.dbutil.Log;
import com.trimble.agmantra.utils.Utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;

import java.io.FileNotFoundException;

public class JobSyncService extends Service {

    private boolean isSdCardMount = false;

    private boolean isNetworkAvailable = false;

    private JobSyncManager jobSyncManager = null;
    
    public static boolean isApplicationClosed=false;
    
    public static String ACTION_SERVICE = "com.trimble.agmantra.jobsync.JobSyncService";
    
    private BroadcastReceiver mMountReceiver = null;
    
    private BroadcastReceiver mUnmountReceiver = null;
    
    private BroadcastReceiver mNetworkConnectionReceiver = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public class LocalBinder extends Binder {

        JobSyncService getService() {
            return JobSyncService.this;
        }
    }

    @Override
   public void onCreate() {

      jobSyncManager = JobSyncManager.getInstance(this);
      Log.i(Constants.TAG_JOB_SYNC_SERVICE, "Started Oncreate");
      init();

   }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

      Log.i(Constants.TAG_JOB_SYNC_SERVICE, "Received start id " + startId
            + ": " + intent);

      Bundle bundle = intent.getExtras();

      long lJobId = bundle.getLong(Constants.JOB_ID);

      // String stJobFilePath = bundle.getString(Constants.JOB_FILE_PATH);

      String stSDCardState = Environment.getExternalStorageState();

      if (stSDCardState.equals(Environment.MEDIA_MOUNTED)) {
         if (lJobId == JobSyncManager.ALL_JOBS) {
            jobSyncManager.startSyncProcess();
         } else {
            jobSyncManager.updateEncoderQueue(lJobId);
         }

      } else {
         Log.i(Constants.TAG_JOB_SYNC_SERVICE,
               "Sdcard Unmounted Job Sync service Canbot started!");
      }
      return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        //jobSyncManager.stopEncodeProcess();
       // jobSyncManager.stopUploadProcess();

        // jobSyncManager.stopDownloadProcess();
        // jobSyncManager.stopImportProcess();
       
        // jobSyncManager.clear();
        //        .show();

        unRegisterSDAndNetworkListener();
    }

    /**
     * Initiate the receiver
     * 
     * @return
     */
   public boolean init() {

      boolean isinit = true;
      Log.i(Constants.TAG_JOB_SYNC_SERVICE, "Init start");

      this.mMountReceiver = new BroadcastReceiver() {

         @Override
         public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
               Log.i("JobEncodeQueue", "mMountReceiver onReceive called!");

               isSdCardMount = true;

               // updateUploaderQueue();
               jobSyncManager.addAllFinishedJob();
               jobSyncManager.startEncodeProcess();

               if (Utils.isInternetConnection(context)) {
                  jobSyncManager.addEncodedJobInUploaderQueue(
                        JobSyncManager.ALL_JOBS, false);
                  jobSyncManager.startUploadProcess();
               }

               // jobSyncManager.startDowloaderProcess();
               // jobSyncManager.startImportProcess();
            }
         }
      };

      this.mUnmountReceiver = new BroadcastReceiver() {

         @Override
         public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();

            if (intentAction.equals(Intent.ACTION_MEDIA_UNMOUNTED)
                  || intentAction.equalsIgnoreCase(Intent.ACTION_MEDIA_REMOVED)
                  || intentAction
                        .equalsIgnoreCase(Intent.ACTION_MEDIA_BAD_REMOVAL)
                  || intentAction.equalsIgnoreCase(Intent.ACTION_MEDIA_EJECT)) {
               
               Log.i("JobEncodeQueue", "unMountReceiver onReceive called!");

               isSdCardMount = false;

               jobSyncManager.stopEncodeProcess();

               jobSyncManager.stopUploadProcess();

               // jobSyncManager.stopDownloadProcess();
               // jobSyncManager.stopImportProcess();
            }
         }
      };

      this.mNetworkConnectionReceiver = new BroadcastReceiver() {

         @Override
         public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (intent != null && action != null){

            if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
               return;
            }

            if (intent.getExtras().getBoolean(
                  ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {

               isNetworkAvailable = false;
               Log.i(Constants.TAG_JOB_SYNC_SERVICE,
                     " net connection got disconneted ");
               // jobSyncManager.stopDownloadProcess();
               jobSyncManager.stopUploadProcess();

            } else {
               String stSDCardState = Environment.getExternalStorageState();

               if (stSDCardState.equals(Environment.MEDIA_MOUNTED)
                     && !isNetworkAvailable) {
                  Log.i(Constants.TAG_JOB_SYNC_SERVICE,
                        " net connection enabled ");
                  jobSyncManager.addEncodedJobInUploaderQueue(
                        JobSyncManager.ALL_JOBS, false);
                  jobSyncManager.startUploadProcess();
               }
               isNetworkAvailable = true;
               // jobSyncManager.startDowloaderProcess();
            }
		}
         }
      };

      IntentFilter sdcardMountFilter = new IntentFilter(
            Intent.ACTION_MEDIA_MOUNTED);
      sdcardMountFilter.addDataScheme("file");
      this.registerReceiver(mMountReceiver, sdcardMountFilter);

      IntentFilter sdcardUnMountFilter = new IntentFilter();
      sdcardUnMountFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
      sdcardUnMountFilter.addAction(Intent.ACTION_MEDIA_REMOVED);    
      sdcardUnMountFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
      sdcardUnMountFilter.addAction(Intent.ACTION_MEDIA_EJECT);
      sdcardUnMountFilter.addDataScheme("file");
      this.registerReceiver(mUnmountReceiver, sdcardUnMountFilter);

    
      return isinit;
   }

    /**
     * Unregister all the receiver
     */
    public void unRegisterSDAndNetworkListener() {
        this.unregisterReceiver(mMountReceiver);
        this.unregisterReceiver(mUnmountReceiver);
        this.unregisterReceiver(mNetworkConnectionReceiver);
    }

    @SuppressWarnings("unused")
    private final IBinder mBinder = new LocalBinder();

    public boolean isNetWorkAvailable() {
        return isNetworkAvailable;
    }

    public boolean isSdcardMount() {
        return isSdCardMount;
    }
    
  /* public void stopService() {

      if (jobSyncManager != null) {

         jobSyncManager.stopEncodeProcess();
         jobSyncManager.stopUploadProcess();
      }
      // jobSyncManager.stopDownloadProcess();
      // jobSyncManager.stopImportProcess();
      stopSelf();
   }*/

    
    public static void setAppClose(){
       isApplicationClosed=true;
    }
    public static void setAppLaunch(){
       isApplicationClosed=false;
    }
    public static void stopService(Context context){
       if(context != null){
       Intent intent = new Intent(context, JobSyncService.class);      
       intent.setAction(ACTION_SERVICE);
       context.stopService(intent);
       }
    }

   // Start Sync service
   public static boolean startJobSyncService(long lJobId, Context context) {
      
      boolean isReturn = false;
      
      if (context == null) {
         return isReturn;
      }
      Intent intent = new Intent(context, JobSyncService.class);
      intent.setAction(ACTION_SERVICE);
      Bundle bundle = new Bundle();
      bundle.putLong(com.trimble.agmantra.constant.Constants.JOB_ID, lJobId);

      String stZipFilePath;
      try {
         stZipFilePath = Utils.getZipFileLoc(android.os.Build.MODEL);
         bundle.putString(
               com.trimble.agmantra.constant.Constants.JOB_FILE_PATH,
               stZipFilePath);

         intent.putExtras(bundle);
         context.startService(intent);
         isReturn= true;
      } catch (FileNotFoundException e) {
         Log.i(Constants.TAG_JOB_SYNC_SERVICE, "Cannot start service - No space in SDCARD - startJobSyncService --> " + lJobId);       
         e.printStackTrace();
      }      
      return isReturn;      
   }
      
}

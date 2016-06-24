package com.trimble.ag.nabu.db;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.trimble.ag.filemonitor.dao.DaoMaster;
import com.trimble.ag.filemonitor.dao.DaoSession;
import com.trimble.ag.filemonitor.dao.FileInfoDao;
import com.trimble.ag.filemonitor.dao.FileSyncDaoMaster;
import com.trimble.ag.filemonitor.entity.FileInfo;

import java.io.File;
import java.util.List;

public final class FileSyncContentProvider {

   private static FileSyncContentProvider fileSyncContentProvider      = null;
   private static final String            TAG                      = FileSyncContentProvider.class
                                                                         .getSimpleName();
   private transient Context              context                  = null;

   private transient CustomDevOpenHelper  devOpenHelper            = null;
   private transient DaoSession           m_DAOSession;
   private transient FileSyncDaoMaster    m_DAOMaster;
   private transient DaoWrapper           daoWrapper               = null;
   
   private transient FileInfoDao mFileInfoDao =null;

   private transient SQLiteDatabase       db;
   private transient boolean              isDbUpdatedOnFirstLaunch = true;

   private static final String            FIRST_TIME_LAUNCH        = "first_time";
   
   public static final long                CREATE                   = 1;
   public static final long                NO_CHANGE                = 2;
   public static final long                ZIP_IN_PROGRESS          = 3;
   public static final long                ZIPPED                   = 4;

 
   private FileSyncContentProvider(Context context) {
      this.context = context;
      if (CustomDevOpenHelper.IS_STORE_ON_SD) {
         IntentFilter iFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
         iFilter.addDataScheme("file");
         context.registerReceiver(mMountReceiver, iFilter);

         IntentFilter iFilter2 = new IntentFilter(Intent.ACTION_MEDIA_EJECT);
         iFilter.addDataScheme("file");
         context.registerReceiver(mUnmountReceiver, iFilter2);
         if (!Environment.MEDIA_MOUNTED.equals(Environment
               .getExternalStorageState())) {
            Log.i(TAG, "initAll NOT DONE!");
            return;
         }
      }
      initAll();

   }

   private BroadcastReceiver mMountReceiver   = new BroadcastReceiver() {

                                                 @Override
                                                 public void onReceive(
                                                       Context context,
                                                       Intent intent) {
                                                    if (intent
                                                          .getAction()
                                                          .equals(
                                                                Intent.ACTION_MEDIA_MOUNTED)) {
                                                       Log.i(TAG,
                                                             "mMountReceiver onReceive called!");
                                                       initAll();
                                                    }
                                                 }
                                              };

   private BroadcastReceiver mUnmountReceiver = new BroadcastReceiver() {

                                                 @Override
                                                 public void onReceive(
                                                       Context context,
                                                       Intent intent) {
                                                    if (intent
                                                          .getAction()
                                                          .equals(
                                                                Intent.ACTION_MEDIA_EJECT)) {
                                                       Log.i(TAG,
                                                             "unMountReceiver onReceive called!");
                                                       if (CustomDevOpenHelper.IS_STORE_ON_SD) {

                                                          closeDB();
                                                       }
                                                    }
                                                 }
                                              };

   private void closeDB() {
      if (devOpenHelper != null) {
         devOpenHelper.close();
         Log.i(TAG, "devOpenHelper dbClosed!");
      }

   }

   private boolean isFirstTimeLaunch() {
      final SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(context);
      return preferences.getBoolean(FIRST_TIME_LAUNCH, true);
   }

   public void offFirstTimeLaunch() {
      final SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(context);
      final Editor editor = preferences.edit();
      editor.putBoolean(FIRST_TIME_LAUNCH, false);
      editor.commit();
   }

   private void initAll() {

      if (isFirstTimeLaunch() && CustomDevOpenHelper.IS_STORE_ON_SD) {
         offFirstTimeLaunch();
         deleteOldDB(CustomDevOpenHelper.getDatabasePath(context));
      }
      devOpenHelper = new CustomDevOpenHelper(context);
      db = devOpenHelper.getWritableDatabase();

      m_DAOMaster = new FileSyncDaoMaster(db);
      m_DAOSession = m_DAOMaster.newSession();
      daoWrapper = new DaoWrapper();
      mFileInfoDao = m_DAOSession.getFileInfoDao();

   }

   /**
    * Gets the single instance of FileSyncContentProvider.
    * 
    * @param context
    *           the context
    * @return single instance of FileSyncContentProvider
    */
   public static synchronized FileSyncContentProvider getInstance(Context context) {

      if (fileSyncContentProvider == null) {

         fileSyncContentProvider = new FileSyncContentProvider(context);
      }

      return fileSyncContentProvider;

   }

   public boolean isDBUpdated() {
      return devOpenHelper.isDBUpdated();
   }

   public void setDbUpdate() {

   }

   public int getDBVersion() {
      return DaoMaster.SCHEMA_VERSION;
   }
   
   public long  addFile(final String stFileName,final String stAPPName,final String stOutPutFile){
      final long id=daoWrapper.addFile(stFileName, stAPPName, mFileInfoDao,stOutPutFile);

      return id;
   }
   public boolean  deleteFile(final String stFileName){
      final boolean isDeleted=daoWrapper.deleteFile(stFileName, mFileInfoDao);

      return isDeleted;
   }   
   
   public void updateFileInfo(final long lFileId,final long lStatus,final long 
         lFlag){
      daoWrapper.updateFileInfo(lFileId, lStatus, mFileInfoDao);
   }
   public boolean deleteOldDB(String stPath) {
      File file = new File(stPath);
      boolean isDeleted = false;
      if (file.exists()) {
         isDeleted = file.delete();
         Log.i(TAG, "Old DB deleted");
      }

      file = null;
      return isDeleted;
   }

   /**
    * 
    */
   public List<FileInfo> getAllFileInfo() {
      return mFileInfoDao.queryBuilder().list();
      
   }
   public FileInfo getFileInfoByName(final String stFileName){
      return daoWrapper.getFileByName(stFileName, mFileInfoDao);
   }
   public FileInfo getFileInfoById(final long fileId){
      return mFileInfoDao.load(fileId);
   }
   public void updateFileInfo(final FileInfo fileInfo){
       if(fileInfo != null){
          mFileInfoDao.update(fileInfo);
       }
   }
  }

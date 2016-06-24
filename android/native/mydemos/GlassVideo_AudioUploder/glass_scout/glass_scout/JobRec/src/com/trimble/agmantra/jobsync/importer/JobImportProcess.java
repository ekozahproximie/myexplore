package com.trimble.agmantra.jobsync.importer;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.filecodec.res.ResourceFLSParser;
import com.trimble.agmantra.filecodec.shp.ShapeFileLoader;

import android.content.Context;
import com.trimble.agmantra.dbutil.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public class JobImportProcess implements Runnable {

   private Thread              jobImportThread       = null;

   private boolean             isAsynMode            = false;

   private String              stImportFileLoc       = null;

   private JobImportListener   importProcessListener = null;

   private static final String THREAD_NAME           = "JobSync_download ";

   // private Context context = null;
   
   private FarmWorksContentProvider mContentProvider =null;

   public JobImportProcess(String stFileId, boolean isAsyn,
         JobImportListener processListener, Context context) {
      this.isAsynMode = isAsyn;
      this.importProcessListener = processListener;
      // this.context = context;
      this.stImportFileLoc = stFileId;
      mContentProvider=FarmWorksContentProvider.getInstance(context);
   }

   public void startImporter() {
      if (isAsynMode) {
         jobImportThread = new Thread(this);
         jobImportThread.setName(THREAD_NAME + stImportFileLoc);
         jobImportThread.start();
      } else {
         startImportProcess(stImportFileLoc);
      }
   }

   @Override
   public void run() {
      if (!Thread.interrupted()) {
         startImportProcess(stImportFileLoc);
      }
   }

   /**
    * Starting file import process
    * 
    * @param stDataImportPath
    */
   private void startImportProcess(String stDataImportPath) {

      try {

         File fImporterDir = new File(stDataImportPath);

         String[] filenamesList = fImporterDir.list();

         if (filenamesList == null || filenamesList.length < 1) {
            Log.i(Constants.TAG_JOB_IMPORTER, "filenamesList is empty or null");
            return;
         }
         // Get fls file
         FilenameFilter filter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
               return name.equalsIgnoreCase("resource.fls");
            }
         };

         filenamesList = fImporterDir.list(filter);

         // FLS Importer
         ResourceFLSParser mFlsParser = ResourceFLSParser.getInstance(mContentProvider);
         mFlsParser.flsReader(stDataImportPath);

         // Get FieldBounds Directory
         File[] files = fImporterDir.listFiles();

         if (files == null || files.length<1) {
            Log.i(Constants.TAG_JOB_IMPORTER, "files is empty or null");
            return;
         }

         // This filter only returns directories
         FileFilter fileFilter = new FileFilter() {

            public boolean accept(File file) {
               return file.isDirectory();
            }
         };
         
         files = fImporterDir.listFiles(fileFilter);
         
         if (files == null || files.length<1) {
            Log.i(Constants.TAG_JOB_IMPORTER, "files is empty or null - 1");
            return;
         }
         
         boolean isFieldBoundPresent = false;
         File reqFieldBnd = null;
         for (File file : files) {
            if (file.getName().equalsIgnoreCase("fieldbounds")) {
               reqFieldBnd = file;
               isFieldBoundPresent = true;
               break;
            }
         }
         if (true == isFieldBoundPresent) {
            ShapeFileLoader mShpLoader = ShapeFileLoader.getInstance(mContentProvider);
            mShpLoader.loadShpFile(reqFieldBnd.getAbsolutePath()
                  + "/boundary.shp");
         }

         if (importProcessListener != null) {
            importProcessListener.onJobImportSuccess(stDataImportPath);
         }

         Log.i(Constants.TAG_JOB_IMPORTER, "File import completed");

      } catch (Exception e) {
         e.printStackTrace();
         Log.i(Constants.TAG_JOB_IMPORTER, "File import failed!");
         if (importProcessListener != null) {
            importProcessListener.onJobImportFailure(stDataImportPath,
                  JobImportListener.IO_EXCEPTION);
         }
      }
   }

   public void stopImport() {
      if (jobImportThread != null) {
         jobImportThread.interrupt();
      }
   }

}

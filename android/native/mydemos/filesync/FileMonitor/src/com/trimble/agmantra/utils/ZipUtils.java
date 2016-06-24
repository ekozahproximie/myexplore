package com.trimble.agmantra.utils;

import android.os.Environment;
import android.util.Log;

import com.trimble.ag.filemonitor.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ZipUtils {
   
   private static final String LOG=ZipUtils.class.getSimpleName();
   // Zip utilities

   /**
    * 
    * Make the zip file and placed it in the destination folder
    * 
    * @param stSourceFileLoc
    * @param stDestFileLoc
    */
      
   static {      
      System.loadLibrary("ziputil");      
   }
   
   public static native boolean createJobZipFile(String stInputPath,String stOutputPath);
   
   
   public static boolean doZip(String stSourceFileLoc, String stDestFileLoc,
         boolean bZipViaJNI) {

      boolean isSuccess = false;

      if (bZipViaJNI && Utils.isSDCardMount()) {
         
         try{
            final File descFile = new File(stDestFileLoc);
            final String stParent=descFile.getParent();
            if(stParent != null){
               final File descParentDirectory = new File(stParent);

               if (!descParentDirectory.exists()) {
                  final boolean isSubDirectoryCreated = descParentDirectory
                        .mkdirs();
                  if (!isSubDirectoryCreated) {
                     return isSuccess;
                  }

               }
         }
            if(! descFile.exists()){
               final boolean isCreated= descFile.createNewFile();
               if(isCreated ){
                  Log.i(LOG, "createJobZipFile successfully:"+descFile);
               }
            }
           
            isSuccess = createJobZipFile(stSourceFileLoc,
                  stDestFileLoc);
            
            if (!isSuccess) {
               descFile.delete();
               Log.i(LOG, "Zip file not created");
               return isSuccess;
            }else{
               Log.i(LOG, "JobZipFile successfully created:"+descFile);
            }
         }catch(IOException exception){
            exception.printStackTrace();
         }
         
         
        // isValidZip = ZipUtils.isValidZip(stDestFileLoc);
      }
      return isSuccess;
   }

   /**
    * 
    * Make unzip the file and placed it in the destination folder
    * 
    * @param stZipFileLoc
    * @param stDestLoc
    */
   public static String doUnZip(String stZipFileLoc) {

      FileOutputStream foutStream = null;
      BufferedOutputStream bufOutStream = null;
      BufferedInputStream bufInStream = null;

      ZipFile zipFile = null;

      String stDestPath = null;

      try {

         File fSourceZip = new File(stZipFileLoc);

         stDestPath = stZipFileLoc.substring(0, stZipFileLoc.length() - 4);

         File fFile = new File(stDestPath);
	        boolean isDirNotCreated= fFile.mkdir();
	        if(!isDirNotCreated){
	            Log.i(LOG, "File Extraction - "+stDestPath);
	            return null;
	        }
	         
         Log.i(LOG, "File Extraction - " + stDestPath);

         zipFile = new ZipFile(fSourceZip);

         Enumeration<? extends ZipEntry> enumEntires = zipFile.entries();

         while (enumEntires.hasMoreElements()) {

            ZipEntry zipEntry = (ZipEntry) enumEntires.nextElement();

            File fDest = new File(stDestPath, zipEntry.getName());

            fDest.getParentFile().mkdirs();

            if (zipEntry.isDirectory()) {
               continue;
            } else {
               Log.i(LOG, "File Extracting - " + fDest);

               bufInStream = new BufferedInputStream(
                     zipFile.getInputStream(zipEntry));

               int len;

               byte buffer[] = new byte[1024];

               foutStream = new FileOutputStream(fDest);
               bufOutStream = new BufferedOutputStream(foutStream, 1024);

               while ((len = bufInStream.read(buffer, 0, buffer.length)) != -1) {
                  bufOutStream.write(buffer, 0, len);
               }

               bufOutStream.flush();
               bufOutStream.close();

               bufInStream.close();
            }
         }
      } catch (FileNotFoundException e) {
         String stTemp = Environment.getExternalStorageState();
         if (!stTemp.equals(Environment.MEDIA_MOUNTED)) {
            Log.i(LOG, "SDcard Unmounted");
         } else {
            e.printStackTrace();
         }
         return null;
      } catch (IOException e) {
         if (!Environment.getExternalStorageState().equals(
               Environment.MEDIA_MOUNTED)) {
            Log.i(LOG, "SDcard Unmounted");
         } else {
            e.printStackTrace();
         }
         return null;
      } finally {
         try {
            if (bufOutStream != null) {
               bufOutStream.close();
            }
            if (foutStream != null) {
               foutStream.close();
            }
            if (null != zipFile) {
               zipFile.close();
            }

         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      return stDestPath;
   }


   /**
    * Traverse a directory and get all the files, and add the files into
    * fileList
    * 
    * @param node
    *           file or directory
    */
   private static List<String> generateFileList(File node, String sourcePath) {

      // List<String> fileList = new ArrayList<String>();

      // add directory file only
      if (node.isFile()) {
         fileList.add(generateZipEntry(node.getAbsoluteFile().toString(),
               sourcePath));
      }

      // Add the sub directory
      if (node.isDirectory()) {
         String[] subNote = node.list();
         for (String filename : subNote) {
            generateFileList(new File(node, filename), sourcePath);
         }
      }

      return fileList;
   }

   public static boolean isValidZip(final String stZipFile) {

      if (stZipFile == null) {
         return false;
      }

      File file = new File(stZipFile);

      if (!file.isFile()) {
         return false;
      }

      ZipFile zipFile = null;

      try {
         zipFile = new ZipFile(file);
         return true;
      } catch (ZipException e) {
         file.delete();
         return false;
      } catch (IOException e) {
         file.delete();
         return false;
      } finally {
         try {
            if (zipFile != null) {
               zipFile.close();
               zipFile = null;
            }
            file = null;
         } catch (IOException e) {
         }
      }
   }

   private static List<String> fileList = new ArrayList<String>();

   /**
    * Format the file path for zip
    * 
    * @param file
    *           file path
    * @return Formatted file path
    */
   private static String generateZipEntry(String file, String sourcePath) {
      return file.substring(sourcePath.length() + 1, file.length());
   }

}

package com.trimble.agmantra.utils;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.dbutil.Log;
import com.trimble.agmantra.entity.AgJob;
import com.trimble.agmantra.filecodec.fdt.FDTWrapper;
import com.trimble.agmantra.filecodec.fgp.FGPCodec;
import com.trimble.agmantra.filecodec.fop.Unit;
import com.trimble.agmantra.layers.FGPPoint;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

public class Utils {

   /**
    * Create a new id for that trackmate project
    * 
    * @return
    */

   public static final int    nConversionFactor         = 1609;

   public static final String MAPPING_JOB               = "BoundaryMapping";
   public static final String SCOUTING_JOB              = "Scouting";
   public static final String PHOTO_JOB                 = "PhotoJob";
   public static final String SYNC_FILE_LOC_TO_DEVICE   = "To_Device";
   public static final String SYNC_FILE_LOC_FROM_DEVICE = "From_Device";
   public static final String SYNC_FILE_LOC_IMPORT      = "Import";
   public static final String UPLOADED_FILE_LOC_ARCHIVE = "Archive";

   public static final String FIELD_BOUNDS              = "FieldBounds";
   public static final String SCOUT                     = "Scouting";
   public static final String QUEUED                    = "Queued";
   public static final String PHOTO                     = "Photo";
   public static final String UNABLE_TO_CREATE          = "unable to create";
   public static final int    HEXADECIMAL_LENGTH        = 8;

   public static final long getNewID() {
      Random random = new SecureRandom();

      long lTrackMateId = (((((random.nextInt() << 8) ^ random.nextInt()) << 8) ^ random
            .nextInt()) << 8) ^ random.nextInt();
      if (lTrackMateId <= 1024 || lTrackMateId == Constants.BLANKLONG) {
         lTrackMateId = getNewID();
      }

      return lTrackMateId;
   }

   // Y in meter Lat
   public static double getTimeInMilleSecs(Date date) {

      return date.getTime();
   }

   // Project Location details
   public static String getProjectLoc(String stProjectName)
         throws FileNotFoundException {

      String stExtStorageLoc = Constants.APP_STORAGE_PATH;

      StringBuilder stProjLoc = new StringBuilder();
      stProjLoc.append(stExtStorageLoc);
      stProjLoc.append(File.separator);
      stProjLoc.append(stProjectName);

      File file = new File(stProjLoc.toString());
      if (!file.isDirectory()) {
         boolean isCreated = file.mkdirs();
         if (!isCreated) {
            throw new FileNotFoundException(UNABLE_TO_CREATE);
         }
      }

      return file.getPath();
   }

   // Return the fileLoc directory
   public static String getBoundaryJobLoc(String stProjectId)
         throws FileNotFoundException {

      StringBuilder stBoundaryLoc = new StringBuilder();

      stBoundaryLoc.append(getProjectLoc(stProjectId));
      stBoundaryLoc.append(File.separator);
      stBoundaryLoc.append(MAPPING_JOB);

      File file = new File(stBoundaryLoc.toString());

      if (!file.isDirectory()) {
         boolean isCreated = file.mkdirs();
         if (!isCreated) {
            throw new FileNotFoundException(UNABLE_TO_CREATE);
         }
      }

      return file.toString();
   }
   public static final String TAG="Utils";
// Return the fileLoc directory path with file name
   public static String getBoundaryJobFileLoc(long lJobID, String stJobTime,
         String stProjectId,FarmWorksContentProvider dbMangr) throws FileNotFoundException {

      StringBuilder stJobFileLoc = new StringBuilder();

      String stFileName = getBoundaryJobFilename(lJobID, stJobTime,dbMangr);
      
      if(stFileName==null){
          Log.i(TAG, "getBoundaryJobFileLoc return null");
         return null;
      }

      stJobFileLoc.append(getBoundaryJobLoc(stProjectId));
      stJobFileLoc.append(File.separator);

      stJobFileLoc.append(stFileName);
      stJobFileLoc.append(File.separator);

      stJobFileLoc.append(stProjectId);
      // stJobFileLoc.append(Constants.PROJECT_FILE_EXTENS);
      stJobFileLoc.append(File.separator);

      stJobFileLoc.append(FIELD_BOUNDS);
      stJobFileLoc.append(File.separator);

      stJobFileLoc.append(QUEUED);
      stJobFileLoc.append(File.separator);

      File file = new File(stJobFileLoc.toString());

      if (!file.isDirectory()) {
         boolean isCreated = file.mkdirs();
         if (!isCreated) {
            throw new FileNotFoundException(UNABLE_TO_CREATE);
         }
      }

      stJobFileLoc.append(stFileName);

      return stJobFileLoc.toString();
   }

   // Return the fileLoc directory path with file name
   public static String getFieldBoundsLoc(long lJobID, String stJobTime,
         String stProjName,FarmWorksContentProvider dbMangr) throws FileNotFoundException {

      StringBuilder stJobFileLoc = new StringBuilder();

      stJobFileLoc.append(getBoundaryJobLoc(stProjName));
      stJobFileLoc.append(File.separator);

      String stFileName = getBoundaryJobFilename(lJobID, stJobTime,dbMangr);
      
      if(stFileName==null){
          Log.i(TAG, "getFieldBoundsLoc return null");
         return null;
      }

      stJobFileLoc.append(stFileName);

      return stJobFileLoc.toString();
   }

   // Return the fileLoc directory
   public static String getScoutJobLoc(String stProjId)
         throws FileNotFoundException {

      StringBuilder stBoundaryLoc = new StringBuilder();

      stBoundaryLoc.append(getProjectLoc(stProjId));
      stBoundaryLoc.append(File.separator);
      stBoundaryLoc.append(SCOUTING_JOB);

      File file = new File(stBoundaryLoc.toString());

      if (!file.isDirectory()) {
         boolean isCreated = file.mkdirs();
         if (!isCreated) {
            throw new FileNotFoundException(UNABLE_TO_CREATE);
         }
      }

      return file.toString();
   }

   // Return the fileLoc directory path with file name
   public static String getScoutJobFileLoc(long iJobID, int iTemplateTypeId,
         String stJobTime, String stProjectName) throws FileNotFoundException {

      StringBuilder stJobFileLoc = new StringBuilder();

      String stFileName = getScoutJobFilename(iJobID, iTemplateTypeId,
            stJobTime, false);
      
      if(stFileName==null){
          Log.i(TAG, "getScoutJobFileLoc return null");
         return null;
      }

      stJobFileLoc.append(getScoutJobLoc(stProjectName));
      stJobFileLoc.append(File.separator);

      stJobFileLoc.append(stFileName);
      stJobFileLoc.append(File.separator);

      stJobFileLoc.append(stProjectName);
      // stJobFileLoc.append(Constants.PROJECT_FILE_EXTENS);
      stJobFileLoc.append(File.separator);

      stJobFileLoc.append(SCOUT);
      stJobFileLoc.append(File.separator);

      stJobFileLoc.append(QUEUED);
      stJobFileLoc.append(File.separator);

      File file = new File(stJobFileLoc.toString());

      if (!file.isDirectory()) {
         boolean isCreated = file.mkdirs();
         if (!isCreated) {
            throw new FileNotFoundException(UNABLE_TO_CREATE);
         }
      }

      stJobFileLoc.append(stFileName);

      return stJobFileLoc.toString();
   }

   // Return the fileLoc directory for zip source folder
   public static String getFieldScoutLoc(long lJobID, int iTemplateTypeId,
         String stJobTime, String stProjName) throws FileNotFoundException {

      StringBuilder stJobFileLoc = new StringBuilder();

      stJobFileLoc.append(getScoutJobLoc(stProjName));
      stJobFileLoc.append(File.separator);

      String stFileName = getScoutJobFilename(lJobID, iTemplateTypeId,
            stJobTime, false);
      
      if(stFileName==null){
          Log.i(TAG, "getFieldScoutLoc return null");
         return null;
      }

      stJobFileLoc.append(stFileName);

      return stJobFileLoc.toString();
   }

   // Return Image file loc path with file name - TODO - change the logic here
   public static String getImageFileLoc(long iJobID, int iTemplateTypeId,
         String stJobTime, String stProjName) throws FileNotFoundException {

      StringBuilder stJobFileLoc = new StringBuilder();

      if (!isSDCardMount()) {
         Log.i("Utils", "Sdcard mounted  - getImageFileLoc");
         return null;
      }

      String stFileName = getScoutJobFilename(iJobID, iTemplateTypeId,
            stJobTime, false);
      
      if(stFileName==null){
          Log.i(TAG, "getImageFileLoc return null");
         return null;
      }

      stJobFileLoc.append(getScoutJobLoc(stProjName));
      stJobFileLoc.append(File.separator);

      stJobFileLoc.append(stFileName);
      stJobFileLoc.append(File.separator);

      stJobFileLoc.append(stProjName);
      stJobFileLoc.append(File.separator);

      stJobFileLoc.append(SCOUT);
      stJobFileLoc.append(File.separator);

      stJobFileLoc.append(QUEUED);
      stJobFileLoc.append(File.separator);

      stJobFileLoc.append(stFileName);
      stJobFileLoc.append(File.separator);

      File file = new File(stJobFileLoc.toString());

      if (!file.isDirectory()) {
         boolean isCreated = file.mkdirs();
         if (!isCreated) {
            throw new FileNotFoundException(UNABLE_TO_CREATE);
         }
      }

      return stJobFileLoc.toString();
   }

   // Returns the Boundary related name
   public static String getBoundaryJobFilename(long iJobID, String stJobTime,FarmWorksContentProvider mDatabase) {

      StringBuilder stFileName = new StringBuilder();
      
      if (!isSDCardMount()) {
         Log.i("Utils", "Sdcard mounted  - getBoundaryJobFilename");
         return null;
      }     

      AgJob mAgJob = mDatabase.getAgjobByJobId(iJobID);

      stFileName.append(stJobTime);
      stFileName.append(Constants.ST_UNDERSCORE);

      String strFieldId = getNoPrefixHexaStringFromLong(mAgJob.getFieldId());
      stFileName.append(strFieldId);

      return stFileName.toString();
   }

   // Returns job file name
   public static String getScoutJobFilename(long iJobID, int iTemplateTypeId,
         String stJobTime, boolean isZipFile) {

      StringBuilder stFileName = new StringBuilder();
      
      if(!isSDCardMount()){
         Log.i("Utils","Sdcard mounted  - getScoutJobFilename");           
         return null;
      }
      
      
      FarmWorksContentProvider mDatabase = FarmWorksContentProvider
            .getInstance(null);
      AgJob mAgJob = mDatabase.getAgjobByJobId(iJobID);
      if(mAgJob == null){
      return stFileName.toString();
      }
      stFileName.append(AgDataStoreResources.JOB_TYPE_MAP_SCOUTING_NAME);
      stFileName.append(Constants.ST_UNDERSCORE);

      String stDefaultval = "";

      if (iTemplateTypeId == AgDataStoreResources.ATT_TYPE_IMAGE) {
         stDefaultval =AgDataStoreResources.PHOTO;
      } else {
         stDefaultval = AgDataStoreResources.TEMPLATETYPE_TEMPLATENAME_TFDTTAG[iTemplateTypeId - 1];
      }

      stFileName.append(stDefaultval);
      stFileName.append(Constants.ST_UNDERSCORE);

      stFileName.append(stJobTime);
      stFileName.append(Constants.ST_UNDERSCORE);

      if (true == isZipFile) {
         stFileName.append(getNoPrefixHexaStringFromLong(iJobID));
      } else {
         if (mAgJob != null && mAgJob.getField() != null && mAgJob.getField().getDesc() != null)
            stFileName.append(mAgJob.getField().getDesc());
      }

      return stFileName.toString();

   }

   // Return the fileLoc directory for zip files
   public static String getZipFileLoc(String stDeviceName)
         throws FileNotFoundException {

      StringBuilder stZipFileLoc = new StringBuilder();

      stZipFileLoc.append(getProjectLoc(stDeviceName));
      stZipFileLoc.append(File.separator);
      stZipFileLoc.append(SYNC_FILE_LOC_FROM_DEVICE);

      File file = new File(stZipFileLoc.toString());

      if (!file.isDirectory()) {
         boolean isCreated = file.mkdirs();
         if (!isCreated) {
            throw new FileNotFoundException(UNABLE_TO_CREATE);
         }
      }

      return file.toString();
   }

   // get the TODevice pathj
   public static String getToDeviceFileLoc(String stDeviceName)
         throws FileNotFoundException {

      StringBuilder stZipFileLoc = new StringBuilder();

      stZipFileLoc.append(getProjectLoc(stDeviceName));
      stZipFileLoc.append(File.separator);
      stZipFileLoc.append(SYNC_FILE_LOC_TO_DEVICE);

      File file = new File(stZipFileLoc.toString());

      if (!file.isDirectory()) {
         boolean isCreated = file.mkdirs();
         if (!isCreated) {
            throw new FileNotFoundException(UNABLE_TO_CREATE);
         }
      }

      return file.toString();
   }

   // get the TODevice pathj
   public static String getImportLoc(String stDeviceName)
         throws FileNotFoundException {

      StringBuilder stZipFileLoc = new StringBuilder();

      stZipFileLoc.append(getProjectLoc(stDeviceName));
      stZipFileLoc.append(File.separator);
      stZipFileLoc.append(SYNC_FILE_LOC_TO_DEVICE);
      stZipFileLoc.append(SYNC_FILE_LOC_IMPORT);

      File file = new File(stZipFileLoc.toString());

      if (!file.isDirectory()) {
         boolean isCreated = file.mkdirs();
         if (!isCreated) {
            throw new FileNotFoundException(UNABLE_TO_CREATE);
         }
      }

      return file.toString();
   }

   /**
    * Write the fop file in xml format
    * 
    * @param fopFile
    * @param stXMLData
    * @return
    */
   /*public static boolean getFormatedXMLString(String stXMLData,
         String stReturnData[]) {

      boolean isSuccess = false;
		if(null == stXMLData){
            return isSuccess;
         } 
      DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();

      DocumentBuilder xmlBuilder = null;

      try {
              
            
            
         xmlBuilder = xmlFactory.newDocumentBuilder();

         Document xmlDoc = xmlBuilder.parse(new InputSource(new StringReader(
               stXMLData)));

         TransformerFactory xmlFranFactory = TransformerFactory.newInstance();
         Transformer xmlTransformer = xmlFranFactory.newTransformer();
         xmlTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
               "yes");
         xmlTransformer.setOutputProperty(
               "{http://xml.apache.org/xslt}indent-amount", "6");
         xmlTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

         StreamResult streamResult = new StreamResult(new StringWriter());
         DOMSource domSource = new DOMSource(xmlDoc);
         xmlTransformer.transform(domSource, streamResult);
         String xmlString = streamResult.getWriter().toString();
         if (stReturnData != null && stReturnData.length > 0) {
            stReturnData[0] = xmlString;
         }
         isSuccess = true;

      } catch (ParserConfigurationException e) {
         e.printStackTrace();
      } catch (SAXException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (TransformerConfigurationException e) {
         e.printStackTrace();
      } catch (TransformerException e) {
         e.printStackTrace();
      }
      return isSuccess;
   }*/

// Return the fileLoc directory for Archive Files
   public static String getArchiveFileLoc(String stDeviceName)
         throws FileNotFoundException {

      StringBuilder stZipFileLoc = new StringBuilder();

      stZipFileLoc.append(getProjectLoc(stDeviceName));
      stZipFileLoc.append(File.separator);
      stZipFileLoc.append(UPLOADED_FILE_LOC_ARCHIVE);

      File file = new File(stZipFileLoc.toString());

      if (!file.isDirectory()) {
         boolean isCreated = file.mkdirs();
         if (!isCreated) {
            throw new FileNotFoundException(UNABLE_TO_CREATE);
         }
      }

      return file.toString();
   }

   // File folder delete
   public static void deleteJobFileDir(File dir) {

      File[] fileList = dir.listFiles();
      if (fileList != null) { // some JVMs return null for empty dirs
         for (File file : fileList) {
            if (file.isDirectory()) {
               deleteJobFileDir(file);
            } else {
               file.delete();
            }
         }
      }
      dir.delete();
   }

   // Delete the file in the path

   public static void deleteFile(String stFilePath, boolean isMoveFile)
         throws FileNotFoundException {
      File file = new File(stFilePath);
      if (file.isFile()) {
         if (isMoveFile && Constants.IS_DEV_BUILD) {
            String stArchivePath = getArchiveFileLoc(android.os.Build.MODEL);

            int iLastSlashIndex = stFilePath.lastIndexOf(File.separator);

            String stFileName = stFilePath.substring(iLastSlashIndex);

            File archiveFile = new File(stArchivePath + File.separator
                  + stFileName);

            boolean iMoved=file.renameTo(archiveFile);
            Log.i("Utils"," file moved "+iMoved);
         }
         file.delete();
      } else {
         deleteJobFileDir(file);
      }

   }

   // Delete file file directory
   public static boolean deleteDirectory(String stFilePath) {
      File path = new File(stFilePath);
      if (path.exists()) {
         File[] files = path.listFiles();
         for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
               deleteDirectory(stFilePath + File.separator + files[i].getName());
            } else {
               new File(stFilePath + File.separator + files[i].getName())
                     .delete();
            }
         }
      }
      return (path.delete());
   }

   // Decimal to Hexa String value
   public static String getNoPrefixHexaStringFromLong(long lValue) {

      String stHexValue = Long.toHexString(lValue).toUpperCase();
      if (stHexValue.length() < HEXADECIMAL_LENGTH) {
         int iNoOfZeroesToBeAdded = HEXADECIMAL_LENGTH - stHexValue.length();
         int iValueToBeAdded = 0;
         for (int i = 0; i < iNoOfZeroesToBeAdded; i++) {
            stHexValue = Integer.toString(iValueToBeAdded) + stHexValue;
         }
      }

      if (stHexValue.length() > HEXADECIMAL_LENGTH) {
         stHexValue = stHexValue.substring(stHexValue.length()
               - HEXADECIMAL_LENGTH, stHexValue.length());
      }
      return stHexValue;
   }

   // Decimal to Hexa String value
   public static String getProjIdHexStringFromLong(long lValue) {

      String stValue = Long.toHexString(lValue).toUpperCase();
      if (stValue.length() < HEXADECIMAL_LENGTH) {
         int iNoOfZeroesToBeAdded = HEXADECIMAL_LENGTH - stValue.length();
         int iValueToBeAdded = 0;
         for (int i = 0; i < iNoOfZeroesToBeAdded; i++) {
            stValue = Integer.toString(iValueToBeAdded) + stValue;
         }
      }
      String stHexValue = "0x" + stValue;
      // String stHexValue = "0x" + Long.toHexString(lValue).toUpperCase();
      return stHexValue;
   }

   // Decimal to Hexa String value
   public static String getHexaStringFromInt(int iValue) {

      String stHexValue = "0x" + Integer.toHexString(iValue).toUpperCase();
      return stHexValue;

   }

   // Decimal to Hexa String value
   public static String getHexaStringFromLong(long lValue) {

      String stHexValue = Long.toHexString(lValue).toUpperCase();
      if (stHexValue.length() > HEXADECIMAL_LENGTH) {
         stHexValue = stHexValue.substring(stHexValue.length()
               - HEXADECIMAL_LENGTH, stHexValue.length());
      }
      stHexValue = "0x" + stHexValue;

      return stHexValue;
   }

   // Hexa String to decimal
   public static int getIntegerFromHexaString(String stVal) {

      int fieldid = 0;

      if (stVal != null) {
         fieldid = Integer.valueOf(Integer.decode(stVal));
      }

      return fieldid;

   }

   // Hexa String to decimal
   public static long getLongFromHexaString(String stVal) {

      long fieldid = 0;
      if (stVal != null && stVal.equals(Constants.ST_EMPTY)) {
         fieldid = Long.valueOf(Integer.decode(stVal));
      }
      return fieldid;

   }
   
   public static String getEscapedXMLString(String stVal) {
	
	   if ( (stVal != null) && 
			(false == stVal.equals(Constants.ST_EMPTY)) ) {
		   stVal = stVal.replaceAll("&", "&amp;").replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("'", "&apos;").replaceAll("\"", "&quot;");
		   return stVal;
	   }
	   return null;
   }

   // Square meter to acre
   public static double getAcresFromSquareMeter(double iValue) {

      double dValue = iValue / Unit.UNIT_CONVERSION_SQUARE_METER;

      return dValue;
   }

   // Save Image from URL to Dest Location
   public static boolean saveUriImage(Uri uri, Context context,
         String stDesFile_path) throws FileNotFoundException, IOException {
      final int FILE_SIZE = 1024*300;
      boolean isSuccess = false;
      FileInputStream in = null;
      FileOutputStream out = null;
      if (uri == null) {
         return false;
      }

      try {
         String stFileName = uri.getPath();
         in = new FileInputStream(new File(stFileName));
         byte[] data = new byte[in.available()];
         in.read(data, 0, data.length);
         in.close();
         Bitmap bmp = makeBitmap(data, FILE_SIZE);
         if(bmp != null){
         out = new FileOutputStream(stDesFile_path);
         bmp.compress(Bitmap.CompressFormat.PNG, 75, out);
         out.flush();
         isSuccess = true;
         }
         /*try {
            if (context != null) {
               context.getContentResolver().delete(uri, null, null);
            }
         } catch (IllegalArgumentException e) {
            String stFilePath = uri.getPath();
            File file = new File(stFilePath);
            boolean isDeleted = file.delete();
            if (!isDeleted) {
               e.printStackTrace();
            }

         }*/
         
      } catch (FileNotFoundException e) {
         throw e;
      } finally {
         if (out != null) {
            out.close();
         }
         if (in != null) {
            in.close();
         }
      }
      return isSuccess;
   }
   
   // Save Image from URL to Dest Location
   public static boolean saveCompressedImage(Uri uri, Context context) throws FileNotFoundException, IOException,OutOfMemoryError {
      final int FILE_SIZE = 1024*300;
      boolean isSuccess = false;
      FileInputStream in = null;
      FileOutputStream out = null;
      if (uri == null) {
         return false;
      }

      try {
         String stFileName = uri.getPath();
         final File file=new File(stFileName);
         in = new FileInputStream(file);
         byte[] data = new byte[in.available()];
         in.read(data, 0, data.length);
         in.close();
         Bitmap bmp = makeBitmap(data, FILE_SIZE);
         if(bmp != null){
        	String stDesFile_path=file.getParent()+File.separator+"temp.jpeg";
        	File desFile= new  File(stDesFile_path);
         out = new FileOutputStream(desFile);
         bmp.compress(Bitmap.CompressFormat.PNG, 75, out);
         out.flush();
         isSuccess = true;
         //file.delete();
         isSuccess= desFile.renameTo(file);
         }
         /*try {
            if (context != null) {
               context.getContentResolver().delete(uri, null, null);
            }
         } catch (IllegalArgumentException e) {
            String stFilePath = uri.getPath();
            File file = new File(stFilePath);
            boolean isDeleted = file.delete();
            if (!isDeleted) {
               e.printStackTrace();
            }

         }*/
         
      } catch (FileNotFoundException e) {
         throw e;
      } catch(OutOfMemoryError e){
         throw e;
      } finally {
         if (out != null) {
            out.close();
         }
         if (in != null) {
            in.close();
         }
      }
      return isSuccess;
   }
   
   
// Save Image from URL to Dest Location
	public static long checkJobFileSize(long lJobId) {
		
		long lFileSize=0;
			if (lJobId == -1) {
			return lFileSize;
		}
		final File path = new File(
				com.trimble.agmantra.constant.Constants
						.getFlagStoreDir_Job(lJobId));
		if (!path.exists()) {
			return lFileSize;
		}
		String stPhotoList[] = path.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {

				return filename.endsWith(".jpeg");
			}
		});
		
		if (stPhotoList != null) {
			for (String string : stPhotoList) {

					String stFileName = path.getPath()+File.separator+string;
					lFileSize += new File(stFileName).length();
					
			}
		}
		return lFileSize;
	}

   public static Bitmap makeBitmap(byte[] jpegData, int maxNumOfPixels) {
      try {
         BitmapFactory.Options options = new BitmapFactory.Options();
         options.inJustDecodeBounds = true;
         BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);
         if (options.mCancel || options.outWidth == -1
               || options.outHeight == -1) {
            return null;
         }
         options.inSampleSize = computeSampleSize(options, -1, maxNumOfPixels);
         options.inJustDecodeBounds = false;

         options.inDither = false;
         options.inPreferredConfig = Bitmap.Config.ARGB_8888;
         return BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length,
               options);
      } catch (OutOfMemoryError ex) {
         Log.e("Test", "Got oom exception ", ex);
         return null;
      }
   }
   
   public static Bitmap makeBitmap(InputStream inputStream, int maxNumOfPixels) {
       try {
          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inJustDecodeBounds = true;
          BitmapFactory.decodeStream(inputStream,null, options);
          if (options.mCancel || options.outWidth == -1
                || options.outHeight == -1) {
             return null;
          }
          options.inSampleSize = computeSampleSize(options, -1, maxNumOfPixels);
          options.inJustDecodeBounds = false;

          options.inDither = false;
          options.inPreferredConfig = Bitmap.Config.ARGB_8888;
          return BitmapFactory.decodeStream(inputStream,null, options);
       } catch (OutOfMemoryError ex) {
          Log.e("Test", "Got oom exception ", ex);
          return null;
       }
    }

   public static int computeSampleSize(BitmapFactory.Options options,
         int minSideLength, int maxNumOfPixels) {
      int initialSize = computeInitialSampleSize(options, minSideLength,
            maxNumOfPixels);

      int roundedSize;
      if (initialSize <= 8) {
         roundedSize = 1;
         while (roundedSize < initialSize) {
            roundedSize <<= 1;
         }
      } else {
         roundedSize = (initialSize + 7) / 8 * 8;
      }

      return roundedSize;
   }

   private static int computeInitialSampleSize(BitmapFactory.Options options,
         int minSideLength, int maxNumOfPixels) {
      double w = options.outWidth;
      double h = options.outHeight;

      int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w
            * h / maxNumOfPixels));
      int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
            Math.floor(w / minSideLength), Math.floor(h / minSideLength));

      if (upperBound < lowerBound) {
         // return the larger one when there is no overlapping zone.
         return lowerBound;
      }

      if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
         return 1;
      } else if (minSideLength == -1) {
         return lowerBound;
      } else {
         return upperBound;
      }
   }

   /**
    * 
    * @param fgpPoints
    *           get List of FGP Points
    * @return
    */
   public static byte[] getFGPBlob(ArrayList<FGPPoint> fgpPoints) {

      Vector<FGPPoint> vecFgpPoints = new Vector<FGPPoint>(fgpPoints);

      byte[] blopBuff = FGPCodec.getBlobFromFGPList(vecFgpPoints);

      return blopBuff;

   }

   // Generate Random Image name

   public static String getRandomImageName() {

      Random randam = new SecureRandom();

      int iRand = randam.nextInt(100);

      String stFileName = String.format("%03d", iRand);
      String stImageName = Constants.DEFAULT_IMG_NAME + stFileName
            + Constants.IMAGE_FILE_EXTENS;

      return stImageName;

   }

   // URI validation
   public static boolean isValidUri(String uri) {
      boolean isValid = false;
      if (uri != null) {
         File file = new File(uri);
         Drawable drawble = Drawable.createFromPath(file.getAbsolutePath());
         if (drawble != null) {
            isValid = true;
            uri = null;
         }
      }
      return isValid;
   }

   public static String getTime(String stDateTime, boolean is24HrFormat) {
      String stTime = null;
      if (null != stDateTime) {
         String[] stDateTimeElem = stDateTime.split(" ");

         if (stDateTimeElem.length == 4) {
            String[] stTimeElem = stDateTimeElem[3].split(":");
            stTime = stTimeElem[0] + ":" + stTimeElem[1];
         } else if (stDateTimeElem.length == 5) {
            String[] stTimeElem = stDateTimeElem[3].split(":");
            stTime = stTimeElem[0] + ":" + stTimeElem[1] + " "
                  + stDateTimeElem[4];
         }

      }
      return stTime;
   }

   public static String getDate(Date dtDateTime, boolean is24HrFormat) {
      String stDate = null;

      // the new format as requested - modified by
      if (null != dtDateTime) {
         /*
          * String[] stDateTimeElem = stDateTime.split(" "); if
          * (stDateTimeElem.length == 5 || stDateTimeElem. length == 4) { stDate
          * = stDateTimeElem[0] + " " + stDateTimeElem[1] + " "; }
          */

         stDate = (DateFormat.format("MM/dd/yyyy", dtDateTime)).toString();

      }
      return stDate;
   }

   public static boolean isInternetConnection(Context context) {
      boolean isOnline = false;

      ConnectivityManager connMgnr = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo nwInfo = connMgnr.getActiveNetworkInfo();
      if (nwInfo != null && nwInfo.isConnectedOrConnecting()) {
         isOnline = true;
      }
      return isOnline;
   }

   public static boolean isSDCardMount() {
      return Environment.MEDIA_MOUNTED.equals(Environment
            .getExternalStorageState());
   }

   public static String getDeFaultId(long lID) {

      String stId = getHexaStringFromLong(lID);

      return stId;

   }
   
   public static String getDate_ISO8601(long lTimeMilli){
       String stDate=null;
       Date date = new Date(lTimeMilli);
       java.text.DateFormat m_ISO8601Local = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss");
       stDate=m_ISO8601Local.format(date);
       return stDate;
   }
   public static String getDeviceInfoDetails(Context context) {

       StringBuffer buffer = new StringBuffer();
       final String PACKAGENAME = context.getApplicationContext().getPackageName();

       final String stDeviceType = "Farm Works Mate"; // android.os.Build.PRODUCT;
       final String stProductType = "Android"; // TBD - to retrive phone name
       final String EMPTY = "";
       final String UNKNOWN = "(unknown)";

       final TelephonyManager tm = (TelephonyManager)context
               .getSystemService(Context.TELEPHONY_SERVICE);

       String tmDevice = EMPTY + tm.getDeviceId();
       //tmSerial = EMPTY + tm.getSimSerialNumber();
       String androidId = EMPTY
               + android.provider.Settings.Secure.getString(context.getContentResolver(),
                       android.provider.Settings.Secure.ANDROID_ID);
       UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) |tm.getPhoneType());

       
       String stDeviceId = deviceUuid.toString();
       Log.i(Constants.TAG_JOB_SYNC_SERVICE, "Device ID generated = " + stDeviceId);
       // String stDeviceSerialNo = stDeviceId;
       String stDeviceName = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
       String stAndroidVersion = android.os.Build.VERSION.CODENAME + "_"
               + android.os.Build.VERSION.RELEASE;

       final PackageManager pm = context.getPackageManager();
       if (pm != null) {
           ApplicationInfo ai = null;
           PackageInfo pi = null;

           try {

               ai = pm.getApplicationInfo(PACKAGENAME, 0);
               pi = pm.getPackageInfo(PACKAGENAME, 0);
           } catch (NameNotFoundException e) {
               ai = null;
           }

           String stProductName = (String)(ai != null ? pm.getApplicationLabel(ai) : UNKNOWN);

           String stSoftBuildNo = (String)(pi != null ? pi.versionName : UNKNOWN);
           String stSoftVersion = (String)(pi != null ? pi.versionName : UNKNOWN);

           // construct device details here
           buffer.append("-------------------- Device Details ---------------------").append(
                   Constants.ST_NEWLINE);
           buffer.append("Device ID: ").append(tmDevice).append(Constants.ST_NEWLINE);
           buffer.append("Device UUID: ").append(stDeviceId).append(Constants.ST_NEWLINE);
           buffer.append("Device Name: ").append(stDeviceName).append(Constants.ST_NEWLINE);
           buffer.append("Serial Number: ").append(tm.getSimSerialNumber()).append(Constants.ST_NEWLINE);
           buffer.append("Device Type: ").append(stDeviceType).append(Constants.ST_NEWLINE);
           buffer.append("Product Name: ").append(stProductName).append(Constants.ST_NEWLINE);
           buffer.append("Product Type: ").append(stProductType).append(Constants.ST_NEWLINE);
//           buffer.append("Product Version: ").append(SettingsActivity.getAppVersion()).append(Constants.ST_NEWLINE);
           buffer.append("Android Version: ").append(stAndroidVersion).append(Constants.ST_NEWLINE);
           buffer.append("Software Version: ").append(stSoftVersion).append(Constants.ST_NEWLINE);
           buffer.append("Software Build No: ").append(stSoftBuildNo).append(Constants.ST_NEWLINE);
           if(Constants.IS_DEV_BUILD){
           buffer.append("Build Type: ").append("Developer build").append(Constants.ST_NEWLINE);
           }else{
               buffer.append("Build Type: ").append("Production build").append(Constants.ST_NEWLINE);
           }
           
           buffer.append("CodePage:").append(FDTWrapper.getCodePageHeader(false)).append(Constants.ST_NEWLINE);
           buffer.append("--------------------------------------------------------------").append(
                   Constants.ST_NEWLINE);
       }
       return buffer.toString();
   }
   public static boolean dumpDevInfoTextFile(String stFilePath,Context context){
       boolean isCreated = false;
       final String stFileName = "android_system_info.txt";
       File dir = new File(stFilePath);
       if (!dir.exists() ) {
           return isCreated;
       }
       File file = new File(stFilePath + File.separator + stFileName);
       try {
           if (!file.createNewFile()) {
               return isCreated;
           }
       } catch (IOException e) {
           e.printStackTrace();
           return isCreated;
       }
           
     
       FileOutputStream fileOutputStream = null;
      try {
          fileOutputStream= new FileOutputStream(file);
          String stDeviceInfo=getDeviceInfoDetails(context);
          fileOutputStream.write(stDeviceInfo.getBytes());
          fileOutputStream.flush();
          isCreated=true;
   } catch (FileNotFoundException e) {
       e.printStackTrace();
   }catch (IOException e) {
      e.printStackTrace();
   }finally{
       try {
           if(fileOutputStream != null){
               fileOutputStream.close();
           }
       } catch (IOException e2) {
           e2.printStackTrace();
       }
   }
       
       return isCreated;
   }
   
   // Write DBfile into sdcard
   public static boolean writeDBDataToSdcard(String stDbFilePath, String stDestLoc) {

      File dbFile = new File(stDbFilePath);
      File destFile = new File(stDestLoc);
      
      boolean isSuccess = false;

      FileInputStream fin = null;
      FileOutputStream fout = null;

      BufferedInputStream bufInStream = null;
      BufferedOutputStream bufOutStream = null;

      int len = -1;

      byte[] buff = new byte[1024];

      try {
         fin = new FileInputStream(dbFile);
         fout = new FileOutputStream(destFile);

         bufInStream = new BufferedInputStream(fin);
         
         bufOutStream = new BufferedOutputStream(fout);

         while ((len = bufInStream.read(buff)) != -1) {
            bufOutStream.write(buff);
         }

         bufOutStream.flush();
         bufOutStream.close();

         bufInStream.close();
         
         isSuccess = true;

      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      
      return isSuccess;
   }
   
   // Send email with Multiple attachement
   public static void sendAttachedEmail(Context context, String stEmailTo, String stEmailCC,
         String stSubject, String stEmailText, List<String> listilePaths)
     {
         final Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
         mailIntent.setType("text/plain");
         mailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, 
             new String[]{stEmailTo});
         mailIntent.putExtra(android.content.Intent.EXTRA_CC, 
             new String[]{stEmailCC});

         
         ArrayList<Uri> listUri = new ArrayList<Uri>();
         
         for (String file : listilePaths)
         {
             File fileIn = new File(file);
             Uri fileUri = Uri.fromFile(fileIn);
             listUri.add(fileUri);
         }
         mailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, listUri);
         context.startActivity(Intent.createChooser(mailIntent, "Send mail..."));
     }
   
   // Send email with Single attachement
   public static void sendAttachedEmail_Multiple(Context context, String stEmailTo, String stEmailCC,
         String stSubject, String stEmailText,String stFilePath)
     {

         final Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
         mailIntent.setType("text/plain");
         
         mailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, 
             new String[]{stEmailTo});
         
         mailIntent.putExtra(android.content.Intent.EXTRA_CC, 
             new String[]{stEmailCC});
         
         File file = new File(stFilePath);
         
         Uri fileUri = Uri.fromFile(file);    
         
         mailIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
         
         context.startActivity(Intent.createChooser(mailIntent, "Send mail..."));
     }

   public static String ellipSize(String stinput, int imaxLength) {
      final String ellip = "...";
      if (stinput == null || stinput.length() <= imaxLength
            || stinput.length() < ellip.length()) {
         return stinput;
      }
      return stinput.substring(0, imaxLength - ellip.length()).concat(ellip);
   }

   public static void sendEmail_attchament(Context context,
         String[] staEmailTo, String stSubject, String stEmailText,
         String[] filePaths, String stReportTitle) {

      ArrayList<Uri> listilePaths = new ArrayList<Uri>();
      if (filePaths != null) {
         for (String file : filePaths) {
            File fileIn = new File(Constants.getStoreRoot() + "" + file);
            Uri uri = Uri.fromFile(fileIn);
            listilePaths.add(uri);
         }
      }
      Log.i("SendMail",
            "No of. Attachemnt To be sent :: " + listilePaths.size());
      Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
      intent.putExtra(Intent.EXTRA_EMAIL, staEmailTo);
      intent.putExtra(Intent.EXTRA_SUBJECT, stSubject);
      intent.putExtra(Intent.EXTRA_TEXT, stEmailText);
      intent.setType("application/zip");
      if (listilePaths.size() > 0) {
         intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, listilePaths);

      }

      context.startActivity(Intent.createChooser(intent, stReportTitle));
   }
   
   public static final String COMMA=",";
   
   public static final String DOT=".";
   public  static String getFormatData(String stData){
   	if(stData == null || stData.trim().length() == 0){
   		return stData;
   	}
   	
   		 stData=stData.replace(COMMA, DOT);
   	 
   	  return stData;
   }
   
   private  static String getFormatDataBy(String stData,String delimit,String replace){
	   	if(stData == null || stData.trim().length() == 0){
	   		return stData;
	   	}
	   	
	   		 stData=stData.replace(delimit, replace);
	   	 
	   	  return stData;
	   }
   public  static String getFormatDataBy(String stData){
	   final DecimalFormat df = new DecimalFormat("#.##");
	   
	   String stDummy=df.format(0.45);
  	  	boolean isDotPersent=stDummy.contains(Utils.DOT);
  	  	boolean isCommaPersent=stDummy.contains(Utils.COMMA);
  	  		if(isDotPersent){
  	  		stData=getFormatDataBy(stData,Utils.COMMA,Utils.DOT);
  	  		}else if(isCommaPersent){
  	  		stData=getFormatDataBy(stData,Utils.DOT,Utils.COMMA);
     	   }
	   	
	   	 
	   	  return stData;
	   }

   public static final boolean isThreadRunning(final String stThreadName) {
      boolean isThreadRuning = false;
      if (stThreadName == null || stThreadName.length() == 0) {
         return isThreadRuning;
      }
      if (Thread.getAllStackTraces() != null) {
         Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
         if (threadSet != null) {
            Thread[] runningThreadArray = threadSet
                  .toArray(new Thread[threadSet.size()]);
            for (int i = 0; i < runningThreadArray.length; i++) {
               Thread runningThread = runningThreadArray[i];
               if (runningThread != null) {
                  final String stRunningThreadName = runningThread.getName();
                  isThreadRuning = stThreadName.equals(stRunningThreadName);
                  if (isThreadRuning) {
                     break;
                  }
               }

            }
         }
      }

      return isThreadRuning;

   }
}

package com.trimble.agmantra.filecodec.fgp;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.layers.FGPPoint;
import com.trimble.agmantra.layers.GSObject;
import com.trimble.agmantra.layers.GSObjectType;
import com.trimble.agmantra.utils.IO;

import android.os.Environment;
import android.os.SystemClock;
import com.trimble.agmantra.dbutil.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class FGPCodec {

   private String           stFGPFileName    = "";

   private long             lJobId         = 0;

   private FGPHeader        fgpHeader        = null;

   private DataOutputStream fgpDataOutStream = null;
   private DataInputStream  fgpDataInStream  = null;

   private Vector<FGPPoint> vecFPGPointList  = null;
   private Vector<GSObject> vecGsObject      = null;

   // Integrate DBF / FSN writer when needed. refer SaveSensorData of the C++
   // reference code

   /**
    * 
    * @param stFileName
    * @param lJobId
    */
   public FGPCodec(String stFileName, long lJobId) {
      // TODO:: add required parameters such as Field ID etc to the constructor
      // as a struct.
      this.stFGPFileName = stFileName;
      this.lJobId = lJobId;
   }

   /**
    * 
    * @return
    */
   private DataOutputStream getOutPutStream(boolean isAppend) {
      try {
         if (fgpDataOutStream == null) {
            fgpDataOutStream = new DataOutputStream(new FileOutputStream(
                  stFGPFileName, isAppend));
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
      return fgpDataOutStream;
   }

   /**
    * 
    */
   private void closeDataOutStream() {
      if (fgpDataOutStream != null) {
         try {
            fgpDataOutStream.close();
            fgpDataOutStream = null;
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   /**
    * FGP file write header
    * 
    * @return
    */
   public boolean writeHeader() {

      boolean isSuccess = false;

      if (0 != lJobId) {
         long lCurrentTime = System.currentTimeMillis();
         FGPHeader fgpHeader = new FGPHeader(Constants.FGP_VERSION, lJobId,
                 (int)(lCurrentTime/1000));

         isSuccess = fgpHeader.writeData(getOutPutStream(false));
         closeDataOutStream();
      }
      return isSuccess;
   }

   /**
    * 
    * @param vecFgpPoint
    * @return
    */
   public boolean addVertex(Vector<FGPPoint> vecFgpPoint,String[] stStatus) {
      boolean bRes = false;

      // *** Write out the vertex to .fgp file
      for (int i = 0; i < vecFgpPoint.size(); i++) {
         FGPPoint fgpPoint = vecFgpPoint.get(i);
         bRes = fgpPoint.writeData(getOutPutStream(true),stStatus);
         if(!bRes){
            break;
         }
      }

      closeDataOutStream();
      return bRes;
   }

   /**
    * 
    * @param iGSOObjectType
    * @return
    */
   public boolean startGSO(int iGSOObjectType) {
      return true;
   }

   /**
    * 
    * @param iGSOObjectType
    * @return
    */
   public boolean stopGSO(int iGSOObjectType) {
      return true;
   }

   // Check Whether the file is present or not
   private boolean isFileExist() {
      boolean isExist = false;
      File fLogFile = new File(stFGPFileName);
      if (fLogFile != null) isExist = fLogFile.exists();
      fLogFile = null;
      return isExist;
   }

   /**
    * Close FGP file handler
    */
   public void close() {
      // TODO:: Reset all variables and vectors
      closeDataInStream();
      closeDataOutStream();

      vecFPGPointList = null;
      vecGsObject = null;
   }

   /**
    * 
    * Parser module for byte array from db
    * 
    * @param bufData
    * @return
    */
   public static Vector<FGPPoint> getFGPPointListFromBlob(long lFGPVersion,
         byte[] bufData) {

      Vector<FGPPoint> vecFgp = new Vector<FGPPoint>();
      byte[] mTempByte = new byte[FGPPoint.getSize()];
      int ibufDataIndex = 0;

      while (bufData != null && bufData.length > ibufDataIndex) {

         System.arraycopy(bufData, ibufDataIndex, mTempByte, 0,
               mTempByte.length);

         FGPPoint fgpPoint = new FGPPoint();
         int index = 0;

         fgpPoint.iPointID = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iPassID = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iAttrID = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iRegionID = IO.get4r(mTempByte, index);
         index += 4;

         fgpPoint.iObjectType = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iTime = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iTime_ms = IO.get4r(mTempByte, index);
         index += 4;
         byte[] unused1 = new byte[4];
         if (mTempByte != null) {
            System.arraycopy(mTempByte, index, unused1, 0, unused1.length);
            index += unused1.length;
         }
         fgpPoint.unused1 = unused1;

         fgpPoint.iX = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iY = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iAlt = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iOffset = IO.get4r(mTempByte, index);
         index += 4;

         fgpPoint.iOffsetX = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iOffsetY = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iOffsetAlt = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iDistTraveled = IO.get4r(mTempByte, index);
         index += 4;

         fgpPoint.iSpeed = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iHeading = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iQuality = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iHDOP = IO.get4r(mTempByte, index);
         index += 4;

         fgpPoint.iNo_Sat = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iMarkers = IO.get4r(mTempByte, index);
         index += 4;
         fgpPoint.iBooms = IO.get4r(mTempByte, index);
         index += 4;
         byte[] unused4 = new byte[3];
         if (mTempByte != null) {
            System.arraycopy(mTempByte, index, unused4, 0, unused4.length);
            index += unused4.length;
         }

         byte checkSum = mTempByte[index];
         index += 1;
         fgpPoint.unused4 = unused4;

         byte[] fgpRawData = fgpPoint.getRawData();

         fgpPoint.checksum = checkSum;

         if ((lFGPVersion == 1 && fgpPoint.checksum == 0)
               || fgpPoint.checksum == fgpPoint.calculateCheckSUM(fgpRawData)) {
            System.out.println("Current FGP dataIObject is valid");
            vecFgp.add(fgpPoint);
         } else {
            System.out.println("Current FGP dataIObject Curreupted");
         }
         ibufDataIndex += index;

      }
      return vecFgp;
   }

   /**
    * 
    * Return the FGP points blob
    * 
    * @param vecFGPPoints
    * @return
    */
   public static byte[] getBlobFromFGPList(Vector<FGPPoint> vecFGPPoints) {

      byte[] blob = null;
      int index = 0;
      int numOfRec = 0;
      int iRawFPGPointSize = FGPPoint.getSize();
      if (vecFGPPoints != null && (numOfRec = vecFGPPoints.size()) > 0) {

         blob = new byte[iRawFPGPointSize * numOfRec];

         byte[] tempBlop = new byte[iRawFPGPointSize];

         // TODO: Can the tempblob declaration be moved above the for loop
         for (FGPPoint fgpPoint : vecFGPPoints) {
            tempBlop = fgpPoint.getRawData();
            System.arraycopy(tempBlop, 0, blob, index, tempBlop.length);
            index += tempBlop.length;
         }
      }
      return blob;
   }

   // Functions needed for parsing the FGP. Currently these functions may
   // not be used actively.

   /**
    * preLoaded function for FGP file reading
    * 
    * @return
    */
   public boolean preLoad() {
      boolean isValid = false;
      // Close any currently open file, and open in read mode
      close();
      if (isFileExist()) {
         if (readHeader()) {
            if (fgpHeader.iVersion <= Constants.FGP_VERSION
                  || fgpHeader.iVersion > 1) isValid = true;
         } else {
            isValid = false;
            close();
         }
      }
      return isValid;
   }

   /**
    * Load list of FGPPoints in the fgp file
    * 
    * @return
    */

   // public Vector<GSObject> ParseFGPFile() {
   public Vector<FGPPoint> ParseFGPFile() {

      vecFPGPointList = new Vector<FGPPoint>();
      vecGsObject = new Vector<GSObject>();
      int icurrPassID = -1;
      int icurrAttribID = -1;
      GSObject currentObj = null;

      // Open the file and start reading the points one by one.
      if (isFileExist() && fgpHeader != null) {

         DataInputStream fgpDataInStream = null;
         fgpDataInStream = getInPutStream();
         int iRawFGPPointSize = FGPPoint.getSize();
         int ibuffSize = -1;
         byte[] mTempByte = new byte[iRawFGPPointSize];

         if (null != fgpDataInStream) {

            try {

               while ((ibuffSize = fgpDataInStream.read(mTempByte)) != -1
                     && (ibuffSize == mTempByte.length)) {

                  try {
                     Vector<FGPPoint> lstFGPPoint = getFGPPointListFromBlob(
                           fgpHeader.iVersion, mTempByte);
                     if (lstFGPPoint.size() > 0) {
                        FGPPoint fgpPoint = lstFGPPoint.get(0);
                        // vecFPGPointList.add(fgpPoint);
                        // Add the point to the GSObject

                        // Create a new GSO Object if one of the following
// conditions are true.
                        if ((null == currentObj)
                              || (fgpPoint.iPassID != icurrPassID)
                              || (fgpPoint.iAttrID != icurrAttribID)
                              || (fgpPoint.iObjectType == GSObjectType.GSO_POINT)) {

                           GSObject newObj = null;

                           // If there is a new feature entering then
                           // close the previous GSO object
                           if (null != currentObj) {
                              // currentObj.setHasOffset(false);
                              // currentObj.close();
                              vecGsObject.add(currentObj);
                              currentObj = null;

                           }

                           // If the object is not a point, try finding an
// existing object with the same passid and attribid
                           // define a function that checks the passid and
// attrib id of the GSObjects currently
                           // in the vecGSObject

                           if (null == currentObj) {
                              //newObj = getNewObject(fgpPoint.m_nObjectType,
                              //      fgpPoint.m_nAttrID, false);
                              currentObj = newObj;
                              // Add to hash map based on the passid and
// attribid if req
                           }

                           icurrAttribID = fgpPoint.iAttrID;
                           icurrPassID = fgpPoint.iPassID;
                           // currentObj.setRegionID(fgpPoint.m_nRegionID);
                        }

                        if (null != currentObj) {
                           // add the point here to the currentobj after the
// interface for adding the fgppoint is
                           // implemented.
                           //currentObj.addVertex(fgpPoint.m_nAttrID,
                           //      fgpPoint.m_nX, fgpPoint.m_nY);
                           vecFPGPointList.add(fgpPoint);
                        }
                     }

                  } catch (Exception e) {
                     e.printStackTrace();
                  }

               }
            } catch (FileNotFoundException e) {
               if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                  Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
               }else{
                  e.printStackTrace();
               }          
               return null;
            } catch (IOException e) {
               if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                  Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
               }else{
                  e.printStackTrace();
               }          
               return null;
            }
         }
      }
      // return vecGsObject;
      return vecFPGPointList;
   }

   /**
    * 
    * @return
    */
   private boolean readHeader() {

      boolean isValidFile = false;

      DataInputStream fgpDataInStream = null;
      fgpDataInStream = getInPutStream();

      if (fgpDataInStream != null) {

         byte[] mFiletype = null; // new byte[] { 'F', 'G', 'P', '\0' };
         int iVersion = 0;
         int iId = 0;
         int iDate = 0;

         fgpHeader = new FGPHeader(iVersion, iId, iDate);

         byte[] mTempByte = new byte[FGPHeader.getSize()];

         int ibuffSize = -1;
         try {
            ibuffSize = fgpDataInStream.read(mTempByte);
         } catch (IOException e) {
            e.printStackTrace();
         }
         int iIndex = 0;

         if (ibuffSize != -1) {

            mFiletype = new byte[4];
            if (mTempByte != null) {
               System.arraycopy(mTempByte, 0, mFiletype, iIndex,
                     mFiletype.length);
               iIndex += 4;
            }
            if ((char) mFiletype[0] == 'F' && (char) mFiletype[1] == 'G'
                  && (char) mFiletype[2] == 'P') {
               iVersion = IO.get4r(mTempByte, iIndex);
               iIndex += 4;
               iId = IO.get4r(mTempByte, iIndex);
               iIndex += 4;
               iDate = IO.get4r(mTempByte, iIndex);
               fgpHeader = null;
               fgpHeader = new FGPHeader(iVersion, iId, iDate);
               isValidFile = true;
            }
         }
      }
      return isValidFile;
   }

   /**
    * 
    * @return
    */
   private DataInputStream getInPutStream() {

      try {
         if (fgpDataInStream == null) {
            fgpDataInStream = new DataInputStream(new FileInputStream(
                  stFGPFileName));
         }
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      }
      return fgpDataInStream;
   }

   /**
       * 
       */
   private void closeDataInStream() {
      if (fgpDataInStream != null) {
         try {
            fgpDataInStream.close();
            fgpDataInStream = null;
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

   }
}

package com.trimble.agmantra.filecodec.shp;

import com.hexiong.jdbf.DBFWriter;
import com.hexiong.jdbf.JDBFException;
import com.hexiong.jdbf.JDBField;
import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.entity.AgJob;
import com.trimble.agmantra.entity.Feature;
import com.trimble.agmantra.entity.Field;
import com.trimble.agmantra.entity.JobTransaction;
import com.trimble.agmantra.filecodec.fgp.FGPCodec;
import com.trimble.agmantra.layers.FGPPoint;
import com.trimble.agmantra.utils.IO;
import com.trimble.agmantra.utils.Mercator;

import android.os.Environment;
import com.trimble.agmantra.dbutil.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class ShpFileEncoder {

   Vector<FGPPoint>         fgpPntLst                  = null;
   private static final int SHP_FILE_CODE              = 0x270a;
   private static final int GEOMETRY_NULL              = 0;
   private static final int GEOMETRY_POINT             = 1;
   private static final int GEOMETRY_ARC               = 3;
   private static final int GEOMETRY_POLYGON           = 5;
   private static final int GEOMETRY_MULTIPOINT        = 8;
   private static final int GEOMETRY_POINT_Z           = 11;
   private static final int GEOMETRY_ARC_Z             = 13;
   private static final int GEOMETRY_POLYGON_Z         = 15;
   private static final int GEOMETRY_MULTIPOINT_Z      = 18;
   private static final int GEOMETRY_POINT_M           = 21;
   private static final int GEOMETRY_ARC_M             = 23;
   private static final int GEOMETRY_POLYGON_M         = 25;
   private static final int GEOMETRY_MULTIPOINT_M      = 28;

   // Fixed offsets in the shp file
   private static final int HDR_LEN_OFFSET             = 24;
   private static final int HDR_BB_START_OFFSET        = 36;
   private static final int SHP_HEADER_FINISH_BOUNDARY = 99;

   private static final int SHP_HEADER_LEN             = 100;
   // 4 bytes - record number, 4 byte - record length
   private static final int FEAT_RECORD_HDR_LEN        = 8;
   private static final int SHAPE_TYPE_LEN             = 4;
   // double[4] + int + int = 40 bytes
   private static final int POLYHDR_FIXED_LEN          = 40;
   private static final int SHX_RECORD_LEN             = 8;
   private static final int SHX_HDR_SIZE               = 100;
   private static final int SHX_SINGLE_REC_LEN         = 8;

   // Moved this .shx into constants
   // Parameters pertaining to the header
   int                      iFileCode                  = SHP_FILE_CODE;
   int[]                    iUnused                    = new int[5];
   // Length in 16 bit words. Currently the length of header alone updated.
   // Essentially the entire filelen / 2 has to be done.
   int                      iFileLen                   = 50;
   int                      iVersion                   = 1000;
   // Currently the shp codec is capable of writing only polygons
   int                      iShapeType                 = GEOMETRY_POLYGON;

   int                      iXMin                      = Integer.MAX_VALUE;
   int                      iYMin                      = Integer.MAX_VALUE;
   int                      iXMax                      = Integer.MIN_VALUE;
   int                      iYMax                      = Integer.MIN_VALUE;
   int[]                    iUnUsed2                   = new int[8];

   // Parameters pertaining to each record
   int                      iSHPRecordNumber           = 0;
   int                      iSHPRecordLength           = 0;
   int                      iSHPCurRecHdrOffset        = 0;
   int                      iSHPCurRecOffset           = 0;

   // Parameters for the SHX recording
   // Initialize to the region after the header.
   int                      iSHXCurRecOffset           = 100;

   // Parameters corresponding to the recorded feature.
   int                      iCurPolyHdrOffset          = 0;
   int                      iPolyMinX                  = Integer.MAX_VALUE;
   int                      iPolyMinY                  = Integer.MAX_VALUE;
   int                      iPolyMaxX                  = Integer.MIN_VALUE;
   int                      iPolyMaxY                  = Integer.MIN_VALUE;
   int                      iNumParts                  = 0;
   int                      iNumPoints                 = 0;
   int[]                    iPolyPartsArray            = null;             // Stores
// the offset of each ring (number of

   // points * 8) - since this has to be
   // represented in WORDs

   private boolean initSHPHeader(int iGeometryType) {
      boolean bRet = false;

      if (iGeometryType <= GEOMETRY_NULL
            || iGeometryType > GEOMETRY_MULTIPOINT_M) {
         return bRet;
      }

      iFileCode = SHP_FILE_CODE;
      Arrays.fill(iUnused, 0);
      iFileLen = 50; // Currently updated with the header's size alone.
      iVersion = 1000;
      iShapeType = iGeometryType;
      Arrays.fill(iUnUsed2, 0);

      bRet = true;
      return bRet;
   }

   private int updateShpShxFileHeader(RandomAccessFile shpOutStream,
         RandomAccessFile shxOutStream,String[] stStatus) {
      byte[] byHeader = new byte[100];
      int iOffset = 0;
      int iSHXLengthOffset = 0;

      // Write filecode, unused1, Filelen in BigEndian
      iOffset = IO.put4r(byHeader, iOffset, iFileCode);
      iOffset = IO.put4r(byHeader, iOffset, iUnused[0]);
      iOffset = IO.put4r(byHeader, iOffset, iUnused[1]);
      iOffset = IO.put4r(byHeader, iOffset, iUnused[2]);
      iOffset = IO.put4r(byHeader, iOffset, iUnused[3]);
      iOffset = IO.put4r(byHeader, iOffset, iUnused[4]);
      iSHXLengthOffset = iOffset;
      iOffset = IO.put4r(byHeader, iOffset, iFileLen);
      iOffset = IO.put4(byHeader, iOffset, iVersion);
      iOffset = IO.put4(byHeader, iOffset, iShapeType);
      iOffset = IO.putdouble(byHeader, iOffset, Mercator.xToLon(iXMin), true);
      iOffset = IO.putdouble(byHeader, iOffset, Mercator.yToLat(iYMin), true);
      iOffset = IO.putdouble(byHeader, iOffset, Mercator.xToLon(iXMax), true);
      iOffset = IO.putdouble(byHeader, iOffset, Mercator.yToLat(iYMax), true);
      iOffset = IO.put4(byHeader, iOffset, iUnUsed2[0]);
      iOffset = IO.put4(byHeader, iOffset, iUnUsed2[1]);
      iOffset = IO.put4(byHeader, iOffset, iUnUsed2[2]);
      iOffset = IO.put4(byHeader, iOffset, iUnUsed2[3]);
      iOffset = IO.put4(byHeader, iOffset, iUnUsed2[4]);
      iOffset = IO.put4(byHeader, iOffset, iUnUsed2[5]);
      iOffset = IO.put4(byHeader, iOffset, iUnUsed2[6]);
      iOffset = IO.put4(byHeader, iOffset, iUnUsed2[7]);

      if (null != shpOutStream) {
         try {
            shpOutStream.seek(0);
            shxOutStream.seek(0);
            shpOutStream.write(byHeader);
            // Length of the SHX file is always 100 + 8 bytes since there can be only one polygon that is 
            // transmitted. Hence patching the length to (100+8)/2 = 0x36
            iOffset = IO.put4r(byHeader, iSHXLengthOffset, (SHX_HDR_SIZE + SHX_SINGLE_REC_LEN) >> 1);
            shxOutStream.write(byHeader);
         }catch (FileNotFoundException e) {
            if (!Environment.getExternalStorageState().equals(
                  Environment.MEDIA_MOUNTED)) {
               Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted 1");
            } else {
               stStatus[0] = Constants.SDCARD_NO_SPACE;
               e.printStackTrace();
            }
            return -1;
         } catch (IOException e) {
            if (!Environment.getExternalStorageState().equals(
                  Environment.MEDIA_MOUNTED)) {
               Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted 2");
            } else {
               stStatus[0] = Constants.SDCARD_NO_SPACE;
               e.printStackTrace();
            }
            return -1;
         }
      }
      return iOffset;
   }

   private int updateShpRecordHeader(RandomAccessFile outputStream,String[] stStatus) {
      byte[] byRecordHdr = new byte[12];
      int iOffset = 0;

      iOffset = IO.put4r(byRecordHdr, iOffset, iSHPRecordNumber);
      iOffset = IO.put4r(byRecordHdr, iOffset, iSHPRecordLength);
      iOffset = IO.put4(byRecordHdr, iOffset, GEOMETRY_POLYGON);

      if (null != outputStream) {
         try {
            outputStream.seek(iSHPCurRecHdrOffset);
            outputStream.write(byRecordHdr);
         } catch (FileNotFoundException e) {
            if (!Environment.getExternalStorageState().equals(
                  Environment.MEDIA_MOUNTED)) {
               Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted 3");
            } else {
               stStatus[0] = Constants.SDCARD_NO_SPACE;
               e.printStackTrace();
            }
            return -1;
         } catch (IOException e) {
            if (!Environment.getExternalStorageState().equals(
                  Environment.MEDIA_MOUNTED)) {
               Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted 4");
            } else {

                  stStatus[0] = Constants.SDCARD_NO_SPACE;

               e.printStackTrace();
            }
            return -1;
         }
      }
      return iOffset;
   }

   public boolean createBoundaryShapeFile(long lJobID, String strShpFilename,String[] stStatus,FarmWorksContentProvider agDataStore) {
      boolean bRet = false;
      RandomAccessFile shpOutStream = null;
      RandomAccessFile shxOutStream = null;

      DBFWriter dbfWriter = null;

      JDBField field = null;

      int iOffset = 0;

      
      try{
      try {
    	  
    	// Initialize the header
          bRet = initSHPHeader(GEOMETRY_POLYGON);
         shpOutStream = new RandomAccessFile(strShpFilename, "rw");

         String shxFileName = strShpFilename.replace(Constants.SHP_FILE_EXTENS,
               Constants.SHX_FILE_EXTENS);

         shxOutStream = new RandomAccessFile(shxFileName, "rw");

         String stDbfFileName = strShpFilename.replace(
               Constants.SHP_FILE_EXTENS, Constants.DBF_FILE_EXTENS);

         field = new JDBField(Constants.SHP_DBF_HEADER,
               Constants.FLD_TYPE_NUMBER,
               Constants.MAX_DBF_NUMBER_FIELD_LENGTH,
               Constants.DBF_OTHERS_DECIMAL_COUNT);

         JDBField[] jdbfField = new JDBField[] { field };
         dbfWriter = new DBFWriter(stDbfFileName, jdbfField);

      } catch (FileNotFoundException e) {
         if (!Environment.getExternalStorageState().equals(
               Environment.MEDIA_MOUNTED)) {
            Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted 5");
         } else {

               stStatus[0] = Constants.SDCARD_NO_SPACE;

            e.printStackTrace();
         }
         return false;
      } catch (JDBFException e) {
         if (!Environment.getExternalStorageState().equals(
               Environment.MEDIA_MOUNTED)) {
            Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted 6");
         } else {  
            stStatus[0] = Constants.SDCARD_NO_SPACE;
            e.printStackTrace();
         }
         return false;
      }
      

      if (null == shpOutStream || null == shxOutStream) {
         return bRet;
      }

      // Push the dummy data that is present currently so the offset for
      // recording reset
      // of the features are present
      iOffset = updateShpShxFileHeader(shpOutStream, shxOutStream,stStatus);
         if(iOffset == -1){
            return bRet;
         }
      // Update the current record header to the place where the Shape
      // file's header finished.
      iSHPCurRecHdrOffset = SHP_HEADER_FINISH_BOUNDARY + 1;

      // Following are the actions to be done in this function.
      // 1. Fetch the field ID from the jobid
      // 2. From Feature table fetch the features corresponding to the field and
      // is of type boundary
      // 3. Iterate feature by Feature to collect the points.
      // for each feature
      // a. duplicate first point as the last point to close the loop
      // b. Build the feature using the coordinate vector
      // c. Add it to collection.
      // 4. Save it as a shape file

      try {

        
         AgJob agJob = agDataStore.getAgjobByJobId(lJobID);
         long lFieldID = agJob.getFieldId();

         Field jobFieldInfo = agJob.getField();

         // DB field value not getting correctly - updated this value from
// Jobencoder
         // Already updated in JobEncoder
         // JobEncoder.getInstance().updateFiledBoundingBox(jobFieldInfo);

         List<Feature> featBndryList = agDataStore.getFeaturesByFieldId(
               lFieldID, AgDataStoreResources.FEATURE_TYPE_BOUNDARY);

//         List<JobTransaction> featBndryList = agDataStore.getFeatureInfoByTTId(lJobID,
//               -1);
         if (null == featBndryList || true == featBndryList.isEmpty())
            throw new Exception();

         // The variable below shall refer to the number of parts / rings within
         // the polygon feature right now everything is considered as outer
// rings.
         iNumParts = featBndryList.size();
         iSHPRecordNumber++;
         iPolyPartsArray = new int[iNumParts];
         Arrays.fill(iPolyPartsArray, 0);

         // Update the bounding box for the field to be recorded.
         iPolyMaxX = jobFieldInfo.getBottomRightX();
         iPolyMinX = jobFieldInfo.getTopLeftX();
         iPolyMinY = jobFieldInfo.getBottomRightY();
         iPolyMaxY = jobFieldInfo.getTopLeftY();

         // Update the overall bounding box for the shape file based on this
         // field.
         updateShpFileBBox();

         // Create the dummy header for the record header. Length shall be
         // updated later.
         int offset = updateShpRecordHeader(shpOutStream,stStatus);
         if(offset==-1){
            return false;
         }

         // Update the polygon header offset for changing later
         iCurPolyHdrOffset = iSHPCurRecHdrOffset + FEAT_RECORD_HDR_LEN
               + SHAPE_TYPE_LEN;

         int iPolyHeaderLen = updatePolygonHeader(shpOutStream,stStatus);
         
         if(iPolyHeaderLen==-1){
            return false;
         }

         iSHPCurRecOffset = iCurPolyHdrOffset + iPolyHeaderLen;

         // Reset the number of points for the polygon feature to be recorded.
         iNumPoints = 0;
         int nNoOfPointsInPart = 0;
         int iPolyIndex = 0;

         // Pick the points for each boundary corresponding to the same fieldID
         // and dump
         for (int i = 0; i < iNumParts; i++) {
            Feature bndryFeature = featBndryList.get(i);
            //Feature bndryFeature = featBndryList.get(i).getFeature();

            // Check if the feature is a boundary feature. This is a double
            // check
            if (AgDataStoreResources.FEATURE_TYPE_BOUNDARY == bndryFeature
                  .getFeatureTypeId()) {

               // Update the offset in the parts array
               iPolyPartsArray[iPolyIndex++] = iNumPoints;

               // get the blob of fgp points and then send to the feature
               // constructor.
               byte[] byVertices = bndryFeature.getVertex();
               
               nNoOfPointsInPart = dumpPolyPoints(shpOutStream, byVertices,stStatus);
               
               if(nNoOfPointsInPart==-1){
                  return false;
               }
               
               iNumPoints += nNoOfPointsInPart;
               // Move the offset to record the next item based on the number of
               // points.
               iSHPCurRecOffset += nNoOfPointsInPart * 16;
            }
         }

         // Update the actual number of parts.
         iNumParts = iPolyIndex;

         // called for updating the length / num points.
         int iReturn=  updatePolygonHeader(shpOutStream,stStatus);
         
         if(iReturn  == -1){
            return false;
         }
         // Update the filelen using the currentrecoffset
         iFileLen = iSHPCurRecOffset >> 1;
         // iCurrentRecOffset - indexes after the last point that was inserted.
         // iCurRecHdrOffset - Points to the offset where the record's header is
         // placed. Since the length to be updates is for the number of words
         // after the record, subtract the record header from the length.
         iSHPRecordLength = (iSHPCurRecOffset - iSHPCurRecHdrOffset - FEAT_RECORD_HDR_LEN) >> 1;
        iReturn =updateShpRecordHeader(shpOutStream,stStatus);
        if(iReturn  == -1){
           return false;
        }
        iReturn= updateShpShxFileHeader(shpOutStream, shxOutStream,stStatus);
         
         if(iReturn  == -1){
            return false;
         }
         boolean isReturn=updateshxRecord(shxOutStream, iSHPCurRecHdrOffset, iSHPRecordLength,stStatus);
         
         if(!isReturn){
            return false;
         }

         // Finally set the iCurRecHdrOffset to iCurrentRecOffset since the next
         // record would start there.
         iSHPCurRecHdrOffset = iSHPCurRecOffset;

         // DBF file entry for the field id
         Object[] filedObj = new Object[] { lFieldID };
         dbfWriter.addRecord(filedObj);

      } catch (JDBFException e) {
         if (!Environment.getExternalStorageState().equals(
               Environment.MEDIA_MOUNTED)) {
            Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted 7");
         } else {
            stStatus[0] = Constants.SDCARD_NO_SPACE;
            e.printStackTrace();
         }
         return false;
      }catch (Exception e) {
         if (!Environment.getExternalStorageState().equals(
               Environment.MEDIA_MOUNTED)) {
            Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted 8");
         } else {
            stStatus[0] = Constants.SDCARD_NO_SPACE;
            e.printStackTrace();
         }
         return false;
      }

     
   } finally{
      
      if(shpOutStream!=null){
         try {
            shpOutStream.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      if(shxOutStream!=null){
         try {
            shxOutStream.close();
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      
      try {
         if(dbfWriter != null)
          dbfWriter.close();
       }catch (JDBFException e) {
          e.printStackTrace();
       }
      
   } 
      return bRet;
   }

   private void updateShpFileBBox() {
      // This function takes the dPolyMaxX, dPolyMinY, dPolyMaxY, dPolyMinX as
      // the bbox of the feature added
      // to the shape file and updates the bounding box of the entire shapefile
      // Update Normal Bounding Box

      Log.i("SHPEncoder", "BB before updation:MinX,MinY,MaxX,MaxY = " + iXMin
            + "," + iYMin + "," + iXMax + "," + iYMax);
      Log.i("SHPEncoder", "BB of polygon:MinX,MinY,MaxX,MaxY = " + iPolyMinX
            + "," + iPolyMinY + "," + iPolyMaxX + "," + iPolyMaxY);

      if (iPolyMaxX > iXMax) iXMax = iPolyMaxX;

      if (iPolyMaxY > iYMax) iYMax = iPolyMaxY;

      if (iPolyMinX < iXMin) iXMin = iPolyMinX;

      if (iPolyMinY < iYMin) iYMin = iPolyMinY;

      Log.i("SHPEncoder", "BB after updation:MinX,MinY,MaxX,MaxY = " + iXMin
            + "," + iYMin + "," + iXMax + "," + iYMax);
   }

   private boolean updateshxRecord(RandomAccessFile shxOutStream,
         int iCurRecHdrOffset, int iRecordLength,String[] stStatus) {
      byte[] bySHXRec = new byte[8];
      int iOffset = 0;
      boolean isSuccess = true;

      iOffset = IO.put4r(bySHXRec, iOffset, iCurRecHdrOffset >> 1);
      iOffset = IO.put4r(bySHXRec, iOffset, iRecordLength);

      if (null != shxOutStream) {
         try {
            shxOutStream.seek(iSHXCurRecOffset);
            shxOutStream.write(bySHXRec);
            iSHXCurRecOffset += SHX_RECORD_LEN;
            isSuccess = true;
         } catch (FileNotFoundException e) {
            if (!Environment.getExternalStorageState().equals(
                  Environment.MEDIA_MOUNTED)) {
               Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted 9");
            } else {
               stStatus[0] = Constants.SDCARD_NO_SPACE;
               e.printStackTrace();
            }
            return false;
         } catch (IOException e) {
            if (!Environment.getExternalStorageState().equals(
                  Environment.MEDIA_MOUNTED)) {
               Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted 10");
            } else {
               stStatus[0] = Constants.SDCARD_NO_SPACE;
               e.printStackTrace();
            }
            return false;
         }
      }
      return isSuccess;
   }

   private int dumpPolyPoints(RandomAccessFile shpOutStream, byte[] byVertices,String[] stStatus) {
      int iNoOfPointsInFeature = 0;
      int iOffset = 0;
      if (byVertices == null || byVertices.length < 3) {
         // Not a valid polygon coordinate so skip.
         return iNoOfPointsInFeature;
      }

      Vector<FGPPoint> fgpPointLst = FGPCodec.getFGPPointListFromBlob(
            Constants.FGP_VERSION, byVertices);
      iNoOfPointsInFeature = fgpPointLst.size();

      // Add one to cycle the polygon to the first point
      int iNoOfVertices = iNoOfPointsInFeature + 1;
      // Each double takes 8 bytes, X + Y takes 16 bytes
      int iBufferLen = iNoOfVertices * 16;
      byte[] byPointBuffer = new byte[iBufferLen];

      double dXVal = 0.0;
      double dYVal = 0.0;
      double dXOrgVal = 0.0;
      double dYOrgVal = 0.0;

      for (int j = 0; j < iNoOfPointsInFeature; j++) {

         // TODO:: to see if this fgpPushPoint can be lifted above the loop.
         FGPPoint fgpPushPoint = fgpPointLst.get(j);

         // If offset point is not there , push real points only to shape file.
         int iX = fgpPushPoint.iX;
         int iY = fgpPushPoint.iY;

         if (fgpPushPoint.iOffset != 0) {
            iX = fgpPushPoint.iOffsetX;
            iY = fgpPushPoint.iOffsetY;
         }

         dXVal = Mercator.xToLon(iX);
         dYVal = Mercator.yToLat(iY);

         Log.i("SHPEncoder", "PointID: X/Y = " + fgpPushPoint.iX + "/"
               + fgpPushPoint.iY + "lat/lon = " + dYVal + "/" + dXVal);
         Log.i("SHPEncoder", "Offset PointID: X/Y = " + fgpPushPoint.iOffsetX
               + "/" + fgpPushPoint.iOffsetY);

         if (0 == j) {
            // Store the origin point at dXOrgXVal, dxOrgYVal
            dXOrgVal = dXVal;
            dYOrgVal = dYVal;
         }

         // Dump the point
         iOffset = IO.putdouble(byPointBuffer, iOffset, dXVal, true);
         iOffset = IO.putdouble(byPointBuffer, iOffset, dYVal, true);
      }

      // Cycle the first point to the last to close the polygon
      iOffset = IO.putdouble(byPointBuffer, iOffset, dXOrgVal, true);
      iOffset = IO.putdouble(byPointBuffer, iOffset, dYOrgVal, true);

      if (null != shpOutStream) {
         try {
            shpOutStream.seek(iSHPCurRecOffset);
            shpOutStream.write(byPointBuffer, 0, iBufferLen);
         } catch (FileNotFoundException e) {
            if (!Environment.getExternalStorageState().equals(
                  Environment.MEDIA_MOUNTED)) {
               Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted 11 sdcard status-"+ Environment.getExternalStorageState());
            } else {
               stStatus[0] = Constants.SDCARD_NO_SPACE;
               e.printStackTrace();
            }
            return -1;
         } catch (IOException e) {
            if (!Environment.getExternalStorageState().equals(
                  Environment.MEDIA_MOUNTED)) {
               Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted 12 - sdcard status-"+ Environment.getExternalStorageState());
            } else {
               stStatus[0] = Constants.SDCARD_NO_SPACE;
               e.printStackTrace();
            }
            return -1;
         }
      }
      return iNoOfVertices;
   }

   private int updatePolygonHeader(RandomAccessFile outputStream,String[] stStatus) {
      int iPolygonRecHdrLen = POLYHDR_FIXED_LEN + (4 * iNumParts);
      byte[] byPolyHdr = new byte[iPolygonRecHdrLen];
      int iOffset = 0;

      // Record the polygonheader here
      iOffset = IO.putdouble(byPolyHdr, iOffset, Mercator.xToLon(iPolyMinX),
            true);
      iOffset = IO.putdouble(byPolyHdr, iOffset, Mercator.yToLat(iPolyMinY),
            true);
      iOffset = IO.putdouble(byPolyHdr, iOffset, Mercator.xToLon(iPolyMaxX),
            true);
      iOffset = IO.putdouble(byPolyHdr, iOffset, Mercator.yToLat(iPolyMaxY),
            true);
      iOffset = IO.put4(byPolyHdr, iOffset, iNumParts);
      iOffset = IO.put4(byPolyHdr, iOffset, iNumPoints);

      for (int i = 0; i < iNumParts; i++) {
         iOffset = IO.put4(byPolyHdr, iOffset, iPolyPartsArray[i]);
      }

      if (null != outputStream) {
         try {
            outputStream.seek(iCurPolyHdrOffset);
            outputStream.write(byPolyHdr);
         } catch (FileNotFoundException e) {
            if (!Environment.getExternalStorageState().equals(
                  Environment.MEDIA_MOUNTED)) {
               Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted 13");
            } else {
               stStatus[0] = Constants.SDCARD_NO_SPACE;
               e.printStackTrace();
            }
            return -1;
         } catch (IOException e) {
            if (!Environment.getExternalStorageState().equals(
                  Environment.MEDIA_MOUNTED)) {
               Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted 14");
            } else {
               stStatus[0] = Constants.SDCARD_NO_SPACE;
               e.printStackTrace();
            }
            return -1;
         }
      }
      return iPolygonRecHdrLen;
   }

}

/**
 * Copyright Trimble Inc., 2011 - 2012 All rights reserved.
 * 
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 * 
 * Product Name:
 * 
 * 
 * Module Name: com.trimble.agmantra
 * 
 * File name: ShapeFileReader.java
 * 
 * Author: 
 * 
 * Created On: Jul 18, 2012 3:09:11 PM
 * 
 * Abstract:
 * 
 * 
 * Environment: Mobile Profile : Mobile Configuration :
 * 
 * Notes:
 * 
 * Revision History:
 * 
 * 
 */

package com.trimble.agmantra.filecodec.shp;

import com.hexiong.jdbf.DBFReader;
import com.hexiong.jdbf.JDBFException;
import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.layers.BoundingBox;
import com.trimble.agmantra.layers.GeoPoint;
import com.trimble.agmantra.utils.IO;
import com.trimble.agmantra.utils.Mercator;

import android.os.Environment;
import com.trimble.agmantra.dbutil.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

/**
 * @author 
 */

public class ShapeFileReader implements ShpParseListener {

   public final static int    POINT             = 1;

   public final static int    LINE              = 3;

   public final static int    POLY              = 5;

   public static final String LOG               = "ShapeFile";

   public int                 headerLen;

   public int                 recordLen;

   public int                 nrecords;
   
   public Vector<Integer>     vecFieldids = null;

   public int                 fieldsCount;

   public RandomAccessFile    stream;

   public String              file, stReadFile;
   
   private String             tag = "ShpFileReader";

   int                        iShapeType;

   // for point iShapeType
   Vector<GeoPoint>           vecPoints;

   // for vecVeclines
   Vector<Vector<GeoPoint>>   vecVeclines;

   // for vecVecPolys
   Vector<Vector<GeoPoint>>   vecVecPolys;

   private ShpParseListener   mshpParseListener = null;
   
   private BoundingBox        currentPolyBB = new BoundingBox();

   public ShapeFileReader(String stShapeFilePath) {
      stReadFile = stShapeFilePath;
   }

   public boolean ParseShapeFile(String stShapeFilePath)
         throws FileNotFoundException, IOException {
      
     
      
      byte[] header = new byte[100];
      boolean isDBFParseSuccess = false;
      
      String stDbfFilePath = stShapeFilePath.replace(
            Constants.SHP_FILE_EXTENS, Constants.DBF_FILE_EXTENS);
      
      // Parse the DBF file
      try {
         isDBFParseSuccess = PopulateFieldIDs(stDbfFilePath);
      } finally {
         if (!isDBFParseSuccess) {
            return false; 
         }
      }
      
      stReadFile = stShapeFilePath;
      File shpFile = new File(stShapeFilePath);
      
      if (false == shpFile.exists()) {
         Log.i(tag, "ShapeFile does not exist.");
         return false;
      }
      FileInputStream shapeFile=null;
      try{
      shapeFile = new FileInputStream(shpFile);

      int iReadSize = shapeFile.read(header);

      if (iReadSize != 100) {
         Log.i(LOG, "Read error shape file header " + iReadSize + " "
               + stReadFile);
         return false;
      }

      int iShapeFileCode = IO.get4(header, 0);
      int iFileLength = IO.get4(header, 24) * 2;
      
      // Add checks here to check if the shape file header is present
      if ((0x270A != iShapeFileCode) && (iFileLength != shpFile.length())) {
         Log.i (tag, "Shpheader validation failed. Shapefilecode / length = " + iShapeFileCode + "/" + iFileLength );
         return false;
      }

      // 36–67 double little Minimum bounding rectangle (MBR) of all
      // shapes contained within the shapefile; four doubles in the following
      // order: min X, min Y, max X, max Y

      iShapeType = IO.get4l(header, 32);

      double minX = Double.longBitsToDouble(IO.get8l(header, 36));
      double minY = Double.longBitsToDouble(IO.get8l(header, 44));

      double maxX = Double.longBitsToDouble(IO.get8l(header, 52));
      double maxY = Double.longBitsToDouble(IO.get8l(header, 60));
      
      boolean bReturn = UpdateHeaderData(iShapeType, new BoundingBox((int)Mercator.lonToX(minX), (int)Mercator.latToY(minY), (int)Mercator.lonToX(maxX), (int)Mercator.latToY(maxY)));
      if (false == bReturn) {
         Log.e(tag, "Header validation failed for shape file parsing");
         return bReturn;
      }

      // 61-67
      if (iShapeType == POINT)
         vecPoints = new Vector<GeoPoint>();
      else if (iShapeType == LINE)
         vecVeclines = new Vector<Vector<GeoPoint>>();
      else if (iShapeType == POLY)
         vecVecPolys = new Vector<Vector<GeoPoint>>();
      else {
         msg("unknown ShapeType " + iShapeType);
         return false;
      }

      int iRecordIndex = 0;

      while (true) {
         byte[] hdr = new byte[8];

         iReadSize = shapeFile.read(hdr);

         if (iReadSize < 0) break;

         if (iReadSize != 8) {
            Log.i(LOG, "Read error read vecPoints 2 " + iReadSize + " "
                  + stReadFile);
            return false;
         }

         int iRecordNumber = IO.get4(hdr, 0);
         int iRecordLength = IO.get4(hdr, 4) * 2;

         byte[] contents = new byte[iRecordLength];

         iReadSize = shapeFile.read(contents);

         if (iReadSize != iRecordLength) {
            Log.i(LOG, "Read error read vecPoints 3 " + iReadSize + " "
                  + iRecordLength + " " + stReadFile);
            return false;
         }

         if (iRecordNumber != iRecordIndex + 1) {
            Log.i(LOG, "Unequal record numbers");
            return false;
         }

         if (IO.get4l(contents, 0) != iShapeType) {
            Log.i(LOG,
                  "****************************** " + IO.get4l(contents, 0));
            return false;
         }

         // get contents of shape

         if (iShapeType == POINT)
            readPoint(contents);
         else if (iShapeType == LINE)
            readLine(contents);
         else if (iShapeType == POLY) 
            readPoly(contents);
         
         // Notify the loader using the interface.
         if (POLY == iShapeType) {
            UpdatePolygonFeature(vecFieldids.get(iRecordIndex), currentPolyBB, vecVecPolys);
         }
         iRecordIndex++;


      }
      }finally{
      
         if(shapeFile != null){
         shapeFile.close();
         }
      }
      return true;
   }

   private boolean PopulateFieldIDs(String stDbfFilePath) {
      boolean isSuccess = false;
      DBFReader dbfreader = null;
      try {
         dbfreader = new DBFReader(stDbfFilePath);
         
         int iFldCount = dbfreader.getFieldCount();
         if (0 == iFldCount) return isSuccess;
         
         String strFldName = dbfreader.getField(0).getName();
         if ((null == strFldName) || (0 != strFldName.compareTo("__ID42"))) {
            Log.e(tag, "FieldName found to be different.");
         }
         
         if (null == vecFieldids){
            vecFieldids = new Vector<Integer>();
         } else {
            vecFieldids.clear();
         }
         
         for (int i = 0; dbfreader.hasNextRecord(); i++) {
            Object aobj[] = dbfreader.nextRecord();
            int iFldID = Integer.parseInt( aobj[i].toString());
            vecFieldids.add(iFldID);
         }
         
         isSuccess = (vecFieldids.size() > 0)?true:false;
         
      }  catch (JDBFException e) {
         if (!Environment.getExternalStorageState().equals(
               Environment.MEDIA_MOUNTED)) {
            Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
         } else {
            e.printStackTrace();
         }
         return false;
      }
            
      return isSuccess;
   }

   private GeoPoint readPoint(byte[] contents)

   {
      int type = IO.get4l(contents, 0);

      if (type != 1) {
         Log.i(LOG, "Point iShapeType is " + type);
         return null;
      }

      double x = Double.longBitsToDouble(IO.get8l(contents, 4));
      double y = Double.longBitsToDouble(IO.get8l(contents, 12));

      GeoPoint coor = new GeoPoint(x, y);
      vecPoints.addElement(coor);
      return coor;
   }

   private Vector<Vector<GeoPoint>> readLine(byte[] contents)

   {
      int iShapeType = IO.get4l(contents, 0);

      int iNumberOfParts = 0;
      int iNumberofPoints = 0;

      if (iShapeType != LINE) {
         Log.i(LOG, "Line is not ShapeType " + LINE);
         return null;
      }

      iNumberOfParts = IO.get4l(contents, 36);
      iNumberofPoints = IO.get4l(contents, 40);

      Vector<Vector<GeoPoint>> vecVecLine = new Vector<Vector<GeoPoint>>();

      for (int i = 0; i < iNumberOfParts; ++i) {
         int startIndexForPart = IO.get4l(contents, 44 + i * 4);
         int endIndexForPart;

         if (i == iNumberOfParts - 1)
            endIndexForPart = iNumberofPoints - 1;
         else
            endIndexForPart = IO.get4l(contents, 48 + i * 4) - 1;

         int npoints = endIndexForPart - startIndexForPart + 1;

         int pointStart = 44 + 4 * iNumberOfParts;

         Vector<GeoPoint> vecLine = new Vector<GeoPoint>();

         for (int j = 0; j < npoints; ++j) {
            int n = pointStart + j * 16;

            double x = Double.longBitsToDouble(IO.get8l(contents, n));
            double y = Double.longBitsToDouble(IO.get8l(contents, n + 8));

            GeoPoint coor = new GeoPoint(x, y);
            vecLine.addElement(coor);

         }

         vecVecLine.addElement(vecLine);
      }

      vecVeclines.addAll(vecVecLine);
      return vecVecLine;
   }

   private Vector<Vector<GeoPoint>> readPoly(byte[] contents)

   {
      int iShapeType = IO.get4l(contents, 0);

      int iNumberofParts = 0;
      int iNumberofPoints = 0;

      if (iShapeType != POLY) {
         Log.i(LOG, "Poly is not ShapeType " + POLY);
         return null;
      }

      double minX = Double.longBitsToDouble(IO.get8l(contents, 4));
      double minY = Double.longBitsToDouble(IO.get8l(contents, 12));

      double maxX = Double.longBitsToDouble(IO.get8l(contents, 20));
      double maxY = Double.longBitsToDouble(IO.get8l(contents, 28));
      
      currentPolyBB.left = (int)Mercator.lonToX(minX);
      currentPolyBB.right = (int)Mercator.lonToX(maxX);
      currentPolyBB.bottom = (int)Mercator.latToY(minY);
      currentPolyBB.top = (int)Mercator.latToY(maxY);
      
      iNumberofParts = IO.get4l(contents, 36);
      iNumberofPoints = IO.get4l(contents, 40);

      Vector<Vector<GeoPoint>> vecVecPoly = new Vector<Vector<GeoPoint>>();

      for (int i = 0; i < iNumberofParts; ++i) {
         int startIndexForPart = IO.get4l(contents, 44 + i * 4);
         int endIndexForPart;

         if (i == iNumberofParts - 1)
            endIndexForPart = iNumberofPoints - 1;
         else
            endIndexForPart = IO.get4l(contents, 48 + i * 4) - 1;

         int npoints = endIndexForPart - startIndexForPart + 1;

         int pointStart = 44 + 4 * iNumberofParts;

         Vector<GeoPoint> vecPoly = new Vector<GeoPoint>();

         for (int j = 0; j < npoints; ++j) {
            int n = pointStart + j * 16;

            double x = Double.longBitsToDouble(IO.get8l(contents, n));
            double y = Double.longBitsToDouble(IO.get8l(contents, n + 8));

            GeoPoint coor = new GeoPoint(x, y);
            vecPoly.addElement(coor);
         }

         vecVecPoly.addElement(vecPoly);
      }

      vecVecPolys.addAll(vecVecPoly);
      return vecVecPolys;
   }

   public static void msg(String s) {
      Log.i(LOG, s);
   }

 

   public void setShpListener(ShpParseListener shpParseListener) {
      if (null != shpParseListener) mshpParseListener = shpParseListener;
   }

   public boolean UpdateHeaderData(int iShpType, BoundingBox shpFileBB) {
      boolean isSuccess = false;
      if (null != mshpParseListener) {
         isSuccess = mshpParseListener.UpdateHeaderData(iShpType, shpFileBB);
      }
      return isSuccess;
   }

   public boolean UpdatePolygonFeature(int fieldID, BoundingBox polyBB,
         Vector<Vector<GeoPoint>> vecPolygons) {
      boolean isSuccess = false;
      if (null != mshpParseListener) {
         isSuccess = mshpParseListener.UpdatePolygonFeature(fieldID, polyBB, vecPolygons);
      }
      return isSuccess;
   }

   public boolean UpdatePolyLineFeature(int fieldID,
         Vector<Vector<GeoPoint>> vecLines) {
      boolean isSuccess = false;
      if (null != mshpParseListener) {
         isSuccess = mshpParseListener.UpdatePolyLineFeature(fieldID, vecLines);
      }
      return isSuccess;
   }

   public boolean UpdatePointFeature(int fieldID, Vector<GeoPoint> vecGeoPoints) {
      boolean isSuccess = false;
      if (null != mshpParseListener) {
         isSuccess = mshpParseListener.UpdatePointFeature(fieldID, vecGeoPoints);
      }
      return isSuccess;
   }
}
/**
 * Copyright Trimble Inc., 2013 - 2014 All rights reserved.
 * 
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 * 
 * Product Name:
 * 
 * 
 * Module Name: com.trimble.vilicus.acdc.boundary.res
 * 
 * File name: BoundaryResponse.java
 * 
 * Author: sprabhu
 * 
 * Created On: 3:17:12 PM
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
package com.trimble.agmantra.scout.acdc.cffe.res;

import android.content.Context;
import android.util.Log;

import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.entity.Feature;
import com.trimble.agmantra.entity.Field;
import com.trimble.agmantra.layers.BoundingBox;
import com.trimble.agmantra.layers.FGPPoint;
import com.trimble.agmantra.layers.GSObjectType;
import com.trimble.agmantra.layers.GeoPoint;
import com.trimble.agmantra.login.LoginResponse;
import com.trimble.agmantra.scout.acdc.ACDCResponse;
import com.trimble.agmantra.scout.acdc.MyJSONArray;
import com.trimble.agmantra.scout.acdc.MyJSONObject;
import com.trimble.agmantra.scout.acdc.ScoutACDCApi;
import com.trimble.agmantra.utils.Mercator;
import com.trimble.agmantra.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author sprabhu
 * 
 */
public class BoundaryResponse extends ACDCResponse {

   private static final String C_CONTINUATION = "continuation";

   private static final String C_BOOKMARK     = "bookmark";

   private static final String C_HASMOREDATA  = "hasMoreData";

   private static final String C_BOUNDARYLIST = "boundaryList";

   private static final String C_FIELDID      = "fieldID";

   private static final String C_TYPE         = "type";

   private static final String C_COORDINATES  = "coordinates";

   private static final String C_COLLECTEDUTC = "collectedUTC";

   private static final String C_BOUNDARY     = "boundary";

   private static final String C_ID           = "iD";

   private transient boolean   hasMoreData    = false;

   private transient String    stBookMark     = null;

   private static final String TAG            = "ACDC";

   /**
    * 
    */
   public BoundaryResponse() {

   }

   public boolean readAllBoundaryResponse(final String stJSONResponse,
         final Context context) {
      boolean isSuccess = false;
      if (stJSONResponse == null || stJSONResponse.length() == 0) {
         return isSuccess;
      }
      try {
         final MyJSONObject jsonObject = new MyJSONObject(stJSONResponse);
         isSuccess = jsonObject.getString(ScoutACDCApi.C_RESULT_CODE).equals(
               LoginResponse.SUCCESS);
         if (isSuccess) {
            MyJSONObject continuation = jsonObject
                  .getJSONObject(C_CONTINUATION);
            if (continuation != null) {
               hasMoreData = continuation.getBoolean(C_HASMOREDATA);
               if (hasMoreData) {
                  stBookMark = continuation.getString(C_BOOKMARK);
               }
            }
            MyJSONArray boundaryListJsonArray = jsonObject
                  .getJSONArray(C_BOUNDARYLIST);
            final FarmWorksContentProvider contentProvider = FarmWorksContentProvider
                  .getInstance(context);
            List<Feature> boundary = null;
            List<Feature> allBoundaries = new ArrayList<Feature>(1);
            if (boundaryListJsonArray != null) {
               int iTotalBoundarySize = boundaryListJsonArray.length();

               for (int i = 0; i < iTotalBoundarySize; i++) {
                  boundary = parseBoundary(
                        boundaryListJsonArray.getJSONObject(i), context);
                  if (boundary != null) {
                     allBoundaries.addAll(boundary);
                  }
               }
               contentProvider.insertFeatureList(allBoundaries);
            }
         }
      } catch (JSONException e) {
         Log.e(TAG, e.getMessage(), e);
      }

      return isSuccess;
   }

   private List<Feature> parseBoundary(MyJSONObject boundaryListJSON,
         final Context context) {
      List<Feature> boundary = null;
      if (boundaryListJSON != null) {
         try {
            final String stFieldID = boundaryListJSON.getString(C_FIELDID);
            final String stCollectedUTC = boundaryListJSON
                  .getString(C_COLLECTEDUTC);
            MyJSONObject boundaryJSON = boundaryListJSON
                  .getJSONObject(C_BOUNDARY);
            boundary = getBoundaryData(boundaryJSON, stCollectedUTC,
                  Long.parseLong(stFieldID), context);

         } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
         }
      }

      return boundary;
   }

   private List<Feature> getBoundaryData(final MyJSONObject boundaryJSON,
         String stCollectedUTC, Long lFieldID, Context context) {
      List<Feature> boundaryList = null;
      try {
         if (boundaryJSON != null) {
            // final String stType= boundaryJSON.getString(C_TYPE);
            MyJSONArray coordinateJSONArray = boundaryJSON
                  .getJSONArray(C_COORDINATES);
            FarmWorksContentProvider contentProvider = FarmWorksContentProvider.getInstance(context);
            int iCoordinateSize = coordinateJSONArray.length();
            String stData = null;
            Object object = null;
            byte[] bVertex = null;

            ArrayList<FGPPoint> vecFGPPoints = null;
            String stCoordinateArray[] = null;

            double dLat = 0;
            double dLon = 0;
            int iResultArraySize = 0;
            int iTotalPoint = 0;
            int iOffset = 0;
            double minLat = 0.0;
            double maxLat = 0.0;
            double minLng = 0.0;
            double maxLng = 0.0;

            boundaryList = new ArrayList<Feature>();
            for (int i = 0; i < iCoordinateSize; i++) {
               object = coordinateJSONArray.get(i);
               if (!(object instanceof JSONArray)) {
                  continue;
               }
               JSONArray resultArray = ((JSONArray) object);
               iResultArraySize = resultArray.length();
               iTotalPoint = iResultArraySize;
               if (vecFGPPoints != null) {
                  vecFGPPoints.clear();
               }
               vecFGPPoints = new ArrayList<FGPPoint>(iResultArraySize);
               boolean initialPosition = true;
               bVertex = null;
               minLat = 0.0;
               maxLat = 0.0;
               minLng = 0.0;
               maxLng = 0.0;
               if(iResultArraySize < 2){
                  Log.e(TAG, "Boundary data coordinateSize is : "+iCoordinateSize);
                  return null;
               }
               for (int j = 0; j < iResultArraySize; j++) {
                  stData = resultArray.get(j).toString();
                  stCoordinateArray = null;
                  stCoordinateArray = stData.split(Utils.COMMA);
                  if (stCoordinateArray.length > 2) {
                     dLat = Double.parseDouble(stCoordinateArray[1]);

                     dLon = Double.parseDouble(stCoordinateArray[0]
                           .substring(1));

                     if (initialPosition) {
                        minLat = dLat;
                        maxLat = dLat;
                        minLng = dLon;
                        maxLng = dLon;
                        initialPosition = false;
                     } else {
                        if (dLat < minLat) minLat = dLat;
                        if (dLat > maxLat) maxLat = dLat;
                        if (dLon < minLng) minLng = dLon;
                        if (dLon > maxLng) maxLng = dLon;
                     }

                     FGPPoint fgpPt = new FGPPoint();
                     fgpPt.iX = (int) com.trimble.agmantra.utils.Mercator.lonToX(dLon);
                     fgpPt.iY = (int) com.trimble.agmantra.utils.Mercator.latToY(dLat);
                     fgpPt.iObjectType = GSObjectType.GSO_BOUNDARY;

                     vecFGPPoints.add(fgpPt);
                  }

               }
               bVertex = Utils.getFGPBlob(vecFGPPoints);

               final Feature boundary = new Feature();
               // boundary.setType(stType);

               boundary.setTopLeftX((int) (com.trimble.agmantra.utils.Mercator.lonToX(minLng)));
               boundary.setTopLeftY((int) (com.trimble.agmantra.utils.Mercator.latToY(maxLat)));
               boundary.setBottomRightX((int) (com.trimble.agmantra.utils.Mercator.lonToX(maxLng)));
               boundary.setBottomRightY((int) (com.trimble.agmantra.utils.Mercator.latToY(minLat)));
               boundary
                     .setFeatureTypeId((long) AgDataStoreResources.FEATURE_TYPE_BOUNDARY);

               BoundingBox boundaryBox =new BoundingBox(
            		   (int)(com.trimble.agmantra.utils.Mercator.lonToX(minLng)),
            		   (int)(com.trimble.agmantra.utils.Mercator.latToY(maxLat)), 
                     (int)(com.trimble.agmantra.utils.Mercator.lonToX(maxLng)),
                     (int)(com.trimble.agmantra.utils.Mercator.latToY(minLat)));
               
               // TODO::calculate area code below. Check
               
               double dSignedArea = getObjectArea(vecFGPPoints,
                     boundaryBox);
               boundary.setArea((long)dSignedArea);

               boundary.setFieldId(lFieldID);
               final Field field =contentProvider.getFieldByFieldId(lFieldID);
               if(field != null){
                  String stFieldArea=field.getArea();
                  if(stFieldArea != null){
                     double dArea=Double.parseDouble(stFieldArea);
                     if(dArea > 0){
                        dArea +=(long)dSignedArea;
                     }
                     field.setArea(String.valueOf((long)dArea));
                  }else{
                     field.setArea(String.valueOf((long)dSignedArea));
                  }
                  
                  contentProvider.updateField(field);
               }else{
                  Log.e(TAG,"no field associated with boundary:"+field);
               }
               boundary.setVertex(bVertex);
               boundaryList.add(boundary);
            }
            if (iCoordinateSize == 0) {
               bVertex = null;// new byte[4];
               // iOffset = IO.put4(bVertex, iOffset, iCoordinateSize);
               Feature boundary = new Feature();

               // boundary.setType(stType);

               boundary.setTopLeftX((int) (minLng * 1E6));
               boundary.setTopLeftY((int) (minLat * 1E6));
               boundary.setBottomRightX((int) (maxLng * 1E6));
               boundary.setBottomRightY((int) (maxLat * 1E6));
// double dSignedArea=signedPolygonArea(vecGeopoints);
// dSignedArea=dSignedArea< 0 ? -dSignedArea:dSignedArea;
// dSignedArea= Utils.calculateUnSignedArea(vecGeopoints, new
// GeoPoint(minLat,minLng));
// boundary.setArea((long)dSignedArea);

               boundary.setFieldId(lFieldID);
               boundary.setVertex(bVertex);
               boundaryList.add(boundary);
            }

         }
      } catch (JSONException e) {
         Log.e(TAG, e.getMessage(), e);
      }
      return boundaryList;
   }

   public double getObjectArea(ArrayList<FGPPoint> vecFGPPt,
         BoundingBox mRectBoundary) {
      double dObjectArea = 0;
      FGPPoint mFirstPoint = vecFGPPt.get(0);
      for (FGPPoint iterable_element : vecFGPPt) {

         double dD1 = 0;
         double dD2 = 0;
         double dD3 = 0;
         double dD4 = 0;

         dD1 = Mercator.tvFormulaDistance(mFirstPoint.iX, mFirstPoint.iY,
               mRectBoundary.left, mFirstPoint.iY);
         dD2 = Mercator.tvFormulaDistance(iterable_element.iX,
               iterable_element.iY, iterable_element.iX, mRectBoundary.top);
         dD3 = Mercator.tvFormulaDistance(iterable_element.iX,
               iterable_element.iY, mRectBoundary.left, iterable_element.iY);
         dD4 = Mercator.tvFormulaDistance(mFirstPoint.iX, mFirstPoint.iY,
               mFirstPoint.iX, mRectBoundary.top);

         dObjectArea = dObjectArea + (dD1 * dD2);
         dObjectArea = dObjectArea - (dD3 * dD4);

         mFirstPoint = iterable_element;

      }
      dObjectArea = dObjectArea / 2;
      dObjectArea = Math.abs(dObjectArea);
      return dObjectArea;

   }

 

  /* public boolean readFieldDetailResponse(final String stJSONResponse,
         final Context context) {
      boolean isSuccess = false;
      if (stJSONResponse == null || stJSONResponse.length() == 0) {
         return isSuccess;
      }
      try {
         final MyJSONObject jsonObject = new MyJSONObject(stJSONResponse);
         isSuccess = jsonObject.getString(ScoutACDCApi.C_RESULT_CODE).equals(
               LoginResponse.SUCCESS);
         long id = jsonObject.getLong(C_ID);
         if (isSuccess) {
            MyJSONObject boundaryJSON = jsonObject.getJSONObject(C_BOUNDARY);
            if (boundaryJSON != null) {
               final List<Feature> boundary = getBoundaryData(boundaryJSON,
                     null, id, context);
            }
         }
      } catch (JSONException e) {
         Log.e(TAG, e.getMessage(), e);
      }

      return isSuccess;
   }*/

   /**
    * @return the stBookMark
    */
   public String getBookMark() {
      return stBookMark;
   }

   /**
    * @return the hasMoreData
    */
   public boolean isHasMoreData() {
      return hasMoreData;
   }

}

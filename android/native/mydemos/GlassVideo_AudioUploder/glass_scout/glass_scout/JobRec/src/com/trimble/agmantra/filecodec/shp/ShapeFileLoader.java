package com.trimble.agmantra.filecodec.shp;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.entity.Feature;
import com.trimble.agmantra.entity.Field;
import com.trimble.agmantra.filecodec.fgp.FGPCodec;
import com.trimble.agmantra.layers.BoundingBox;
import com.trimble.agmantra.layers.FGPPoint;
import com.trimble.agmantra.layers.GSObjectType;
import com.trimble.agmantra.layers.GeoPoint;
import com.trimble.agmantra.utils.Mercator;

import android.os.Environment;
import com.trimble.agmantra.dbutil.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

public class ShapeFileLoader {

	private static ShapeFileLoader shpLoader = null;
	
	private FarmWorksContentProvider mContentProvider = null;
	
	private ShapeFileReader shpFileReader = null;

	private String tag = "SHPLOADER";
	
	 private ShpParseListener sphListener =null;

	private ShapeFileLoader(FarmWorksContentProvider mContentProvider ) {
        this.mContentProvider=mContentProvider;
        initListener();
    }
	 private void initListener(){
	     sphListener = new ShpParseListener() {

	         @Override
	         public boolean UpdatePolygonFeature(int fieldid, BoundingBox shpPolyBB,
	                 Vector<Vector<GeoPoint>> vecPolygons) {
	             Boolean isSuccess = false;

	            

	             if (null == vecPolygons) {
	                 Log.e(tag,
	                         "received a polygon feature. But field or vector null");
	                 return isSuccess;
	             }

	             Log.i(tag, "received a polygonfeature for fieldid:" + fieldid);
	             int iNoOfPolygons = vecPolygons.size();
	             if (0 == iNoOfPolygons) {
	                 Log.e(tag, "number of parts in the polygoin is 0");
	                 return isSuccess;
	             }

	             // check if the field id is
	             // present in the field table
	             // if present construct each
	             // polygon into a FGPpoint by ignoring the last point
	             // construct the blob of fgp
	             // points and then push it to DB as a new feature.
	             Field fielddbEntry = mContentProvider.getFieldByFieldId(fieldid);
	             if (null == fielddbEntry) {
	                 Log.e(tag, "Entry corresponding to fieldid :" + fieldid
	                         + "not present");
	                 return isSuccess;
	             }
	             // Update the field's BB with
	             // the BB received from the shape file
	             fielddbEntry.setBottomRightX(shpPolyBB.right);
	             fielddbEntry.setBottomRightY(shpPolyBB.bottom);
	             fielddbEntry.setTopLeftX(shpPolyBB.left);
	             fielddbEntry.setTopLeftY(shpPolyBB.top);

	             mContentProvider.updateField(fielddbEntry);

	             Vector<FGPPoint> vecFGPPt = new Vector<FGPPoint>();

	             for (int i = 0; i < iNoOfPolygons; i++) {
	                 // Construct the FGP Point
	                 // vector and then push to the DB
	                 Vector<GeoPoint> polygon = vecPolygons.get(i);
	                 BoundingBox polyBB = new BoundingBox();

	                 if ((null == polygon) || (0 == polygon.size())) {
	                     Log.e(tag, "NULL entry for the polygon received");
	                     continue;
	                 }

	                 int iNumPoints = polygon.size();
	                 for (int j = 0; j < iNumPoints - 1; j++) {
	                     FGPPoint fgpPt = new FGPPoint();
	                     GeoPoint geoPt = polygon.get(j);

	                     fgpPt.iX = (int) Mercator.lonToX(geoPt.iX);
	                     fgpPt.iY = (int) Mercator.latToY(geoPt.iY);
	                     fgpPt.iObjectType = GSObjectType.GSO_BOUNDARY;

	                     polyBB.stretch(fgpPt.iX, fgpPt.iY);
	                     vecFGPPt.add(fgpPt);
	                 }

	                 // Insert into the DB
	                 updatePolyFeatToDB(vecFGPPt, polyBB, fieldid);

	                 isSuccess = true;
	             }

	             return isSuccess;
	         }

	         @Override
	         public boolean UpdatePolyLineFeature(int field,
	                 Vector<Vector<GeoPoint>> vecvecGeoPoints) {

	             return false;
	         }

	         @Override
	         public boolean UpdatePointFeature(int field,
	                 Vector<GeoPoint> vecGeoPoints) {
	             return false;

	         }

	         @Override
	         public boolean UpdateHeaderData(int iShapeType, BoundingBox shpFileBB) {

	             if (ShapeFileReader.POLY != iShapeType) {
	                 Log.i(tag, "Invalid shapetype received hence returned false");
	                 return false;
	             }
	             return true;

	         }

	     };
	 }

	public static ShapeFileLoader getInstance(FarmWorksContentProvider mContentProvider ) {
		if (null == shpLoader) {
			shpLoader = new ShapeFileLoader(mContentProvider);
		}
		return shpLoader;
	}

	/**
	 * This function parses the shape file using the ShapeFileReader. The
	 * ShapeFileLoader makes use of the interface implemented in this class to
	 * pass the features in the shape file so that it can be added to the DB.
	 * Assumption: prior to calling this API the sync module should have merged
	 * the fls file to the DB.
	 * 
	 * @param strShpFilePath
	 * @return
	 */
	public boolean loadShpFile(String strShpFilePath) {
		boolean isSuccess = false;

		shpFileReader = new ShapeFileReader(strShpFilePath);
		if (null == shpFileReader) {
			return false;
		}

		shpFileReader.setShpListener(sphListener);
		try {
         isSuccess = shpFileReader.ParseShapeFile(strShpFilePath);
      } catch (FileNotFoundException e) {
         if (Environment.getExternalStorageState().equals(
               Environment.MEDIA_MOUNTED)) {
            Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
         } else {
            e.printStackTrace();
         }
         return false;
      } catch (IOException e) {
         if (Environment.getExternalStorageState().equals(
               Environment.MEDIA_MOUNTED)) {
            Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
         } else {
            e.printStackTrace();
         }
         return false;
      }
      return isSuccess;
	}

	// public void dumpPolytoDB(byte[] contents,
	// com.trimble.agmantra.filecodec.shp.FieldInfo mField)
	//
	// {
	// int iShapeType = IO.get4l(contents, 0);
	//
	// int iNumberofParts = 0;
	// int iNumberofPoints = 0;
	//
	// if (iShapeType != ShapeFileReader.POLY) {
	// Log.i(ShapeFileReader.LOG, "Poly is not ShapeType "
	// + ShapeFileReader.POLY);
	// return;
	// }
	//
	// double minX = Double.longBitsToDouble(IO.get8l(contents, 4));
	// double minY = Double.longBitsToDouble(IO.get8l(contents, 12));
	//
	// double maxX = Double.longBitsToDouble(IO.get8l(contents, 20));
	// double maxY = Double.longBitsToDouble(IO.get8l(contents, 28));
	//
	// iNumberofParts = IO.get4l(contents, 36);
	// iNumberofPoints = IO.get4l(contents, 40);
	//
	// int iFieldId = Integer.valueOf(mField.name, 16);
	// for (Field iterable_element : mFieldsList) {
	// if (iFieldId == iterable_element.getId()) {
	// iterable_element.setBottomRightX((int) minX);
	// iterable_element.setBottomRightY((int) maxX);
	// iterable_element.setTopLeftX((int) minY);
	// iterable_element.setTopLeftY((int) maxY);
	// mContentProvider.updateField(iterable_element);
	// break;
	// }
	// }
	//
	// for (int i = 0; i < iNumberofParts; ++i) {
	// int startIndexForPart = IO.get4l(contents, 44 + i * 4);
	// int endIndexForPart;
	//
	// if (i == iNumberofParts - 1)
	// endIndexForPart = iNumberofPoints - 1;
	// else
	// endIndexForPart = IO.get4l(contents, 48 + i * 4) - 1;
	//
	// int npoints = endIndexForPart - startIndexForPart + 1;
	//
	// int pointStart = 44 + 4 * iNumberofParts;
	//
	// Vector<FGPPoint> vecPoly = new Vector<FGPPoint>();
	//
	// for (int j = 0; j < npoints; ++j) {
	// int n = pointStart + j * 16;
	//
	// double dLon = Double.longBitsToDouble(IO.get8l(contents, n));
	// double dLat = Double.longBitsToDouble(IO.get8l(contents, n + 8));
	//
	// FGPPoint mPoint = new FGPPoint();
	// mPoint.iX = mPoint.iOffsetX = (int) Utils.lonToX(dLon);
	// mPoint.iY = mPoint.iOffsetY = (int) Utils.latToY(dLat);
	// mPoint.iObjectType = GSObjectType.GSO_BOUNDARY;
	// vecPoly.addElement(mPoint);
	// }
	// // updateFeatureDeatilsToDB(vecPoly);
	// }
	//
	// }

	private void updatePolyFeatToDB(Vector<FGPPoint> vecFGPPt,
			BoundingBox polyBBox, int iFieldID) {
		// Construct the blob
		byte[] byFGPBlob = FGPCodec.getBlobFromFGPList(vecFGPPt);

		long iFeatureId = mContentProvider.generateFeatureId(0,
				AgDataStoreResources.FEATURE_TYPE_BOUNDARY, false);

		if (iFeatureId > 0) {
			double dArea = getObjectArea(vecFGPPt, polyBBox);
			vecFGPPt.clear();
			Feature bndryFeature = new Feature();
			bndryFeature.setVertex(byFGPBlob);
			bndryFeature.setId(iFeatureId);
			bndryFeature.setBottomRightX(polyBBox.right);
			bndryFeature.setBottomRightY(polyBBox.bottom);
			bndryFeature.setTopLeftX(polyBBox.left);
			bndryFeature.setTopLeftY(polyBBox.top);
			bndryFeature.setFieldId((long) iFieldID);
			bndryFeature.setArea((long) dArea);
			mContentProvider.updateFeature(bndryFeature);
		}
	}
	private double getObjectArea(Vector<FGPPoint> vecFGPPt,
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
			dD2 = Mercator
					.tvFormulaDistance(iterable_element.iX,
							iterable_element.iY, iterable_element.iX,
							mRectBoundary.top);
			dD3 = Mercator.tvFormulaDistance(iterable_element.iX,
					iterable_element.iY, mRectBoundary.left,
					iterable_element.iY);
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
}

/*
 * DBFReader dbfreader = new DBFReader("./book2.dbf"); int i; for (i = 0; i <
 * dbfreader.getFieldCount(); i++) System.out .print((new StringBuilder())
 * .append(dbfreader.getField(i).getName()).append("  ") .toString());
 * 
 * System.out.print("\n"); for (i = 0; dbfreader.hasNextRecord(); i++) { Object
 * aobj[] = dbfreader.nextRecord(Charset.forName("GBK")); for (int j = 0; j <
 * aobj.length; j++) System.out.print((new StringBuilder()).append(aobj[j])
 * .append("  |  ").toString());
 * 
 * System.out.print("\n"); }
 * 
 * System.out.println((new StringBuilder()).append("Total Count: ")
 * .append(i).toString());
 */

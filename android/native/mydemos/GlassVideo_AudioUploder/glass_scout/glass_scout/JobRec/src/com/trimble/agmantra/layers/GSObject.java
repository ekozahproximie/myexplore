package com.trimble.agmantra.layers;

import android.util.MonthDisplayHelper;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.entity.Feature;
import com.trimble.agmantra.job.Job;
import com.trimble.agmantra.jobsync.JobSyncManager;
import com.trimble.agmantra.utils.Mercator;
import com.trimble.agmantra.utils.Utils;

import com.trimble.agmantra.dbutil.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class GSObject. Base class for GSOPath,GSOPolygon,GSOBoundary and
 * GSOPoint. Object refers to Point/Path/Polygon/Boundary shape types.
 */
public abstract class GSObject {

	public static enum VertexOffset {
		OFFSET_NONE, OFFSET_LEFT, OFFSET_RIGHT, OFFSET_ANGLE
	};

	/** Variable storing points list of object FGPPoint */
	public ArrayList<FGPPoint> aPointsList;

	/** Variable storing points list of object FGPPoint */
	protected ArrayList<FGPPoint> aTempPointsList;

	/** Variable storing points list of object FGPPoint */
	// public ArrayList<GeoPoint> aGeopointsList;

	/** Variable storing database manager instance */
	protected FarmWorksContentProvider mDataBase;

	protected Job mJobInstance;

	protected int iObjectType = GSObjectType.GSO_NONE;

	/** Variable storing bounding box for the object */
	protected BoundingBox mRectBoundary;

	/** Variable storing bounding box for the object */
	protected BoundingBox mRectOffsetBoundary;

	/** Variable storing feature id of the object */
	public long iFeatureId;

	/** Boolean variable storing whether the object is loaded or recorded. */
	// TODO: For what it is required.
	protected boolean isObjectLoaded = false;
	
	
	protected boolean isPostProcessingDone = false;

	/**
	 * Boolean Variable storing whether the recording of object is finished or
	 * not.
	 */
	protected boolean isObjectRecFinished = false;

	protected boolean isCheckForAutoClose = false;

	/** Variable storing the length of the object */
	protected long dLength;

	/** Variable storing the offset length of the object */
	protected long dOffsetLength;

	/** Variable storing the area of the object. */
	protected double dArea;

	/** Variable storing the offset area of the object. */
	protected double dOffsetArea;

	/**
	 * Variable storing com.trimble.agmantra.job id of the current recording
	 * com.trimble.agmantra.job.
	 */
	protected long iJobID;

	/**
	 * Variable to store the com.trimble.agmantra.job type of current recording
	 * com.trimble.agmantra.job
	 */
	protected int iPassId;

	/**
	 * Variable to store the com.trimble.agmantra.job type of current recording
	 * com.trimble.agmantra.job
	 */
	protected int iAttributeId;

	protected int iAutoCloseDistance;

	protected FGPPoint[] aFourPoints = null;

	private static final int MIN_POINT = 2;

	private static final int MAX_POINT_FOR_DB = 200;

	protected int iNoOfPointsSentToDB = 0;
	
	private boolean isObjectTobeRefreshed = false;
	
	private boolean bInCorrectOffset = false;

	/**
     * 
     */
	public GSObject(long iJobId, long iFeatureId, int iPassId,
			int iAttributeId, Job mJob) {
		this.mDataBase = mJob.mDataBase;
		this.iJobID = iJobId;
		this.iFeatureId = iFeatureId;
		this.iAttributeId = iAttributeId;
		this.iPassId = iPassId;
		this.mJobInstance = mJob;
		aFourPoints = new FGPPoint[4];
		mRectBoundary = new BoundingBox();
		mRectOffsetBoundary = new BoundingBox();
		dLength = 0;
		dArea = 0;
		dOffsetArea = 0;
		dOffsetLength = 0;
	}

	/**
	 * Adds the received GPS point to object.
	 * 
	 * @param Point
	 *            Object to the class PointInfo storing the details of the
	 *            point.
	 */
	protected void addPointToObject(FGPPoint mPoint, boolean isFixFiltered) {

		// aGeopointsList.add(mGeoPoint);
		// if (false == isObjectLoaded) {
		mPoint.iAttrID = iAttributeId;
		mPoint.iPassID = iPassId;
		if (null == aPointsList) {
			aPointsList = new ArrayList<FGPPoint>();
		}
		aPointsList.add(mPoint);
		updateBoundingBox(mPoint);
		// do it here so that it can be used for autoclose distance check.
		updateLength();
		// TODO Murari: if block to be enabled after implementation of
		// initalllayers
		if (false == isObjectLoaded) {
		if (aPointsList.size() > 1) {
			FGPPoint mPoint1 = aPointsList.get(aPointsList.size() - 2);
			if (mPoint1.iOffset != 0) {

					GeoPoint mGeoPoint = new GeoPoint(
							Mercator.xToLon(mPoint.iX),
							Mercator.yToLat(mPoint.iY),
							Mercator.xToLon(mPoint1.iOffsetX),
							Mercator.yToLat(mPoint1.iOffsetY));
					mJobInstance.sendMapUpdate(iFeatureId, iObjectType,
							mGeoPoint, false,true);
				} else {
					GeoPoint mGeoPoint = new GeoPoint(
							Mercator.xToLon(mPoint.iX),
							Mercator.yToLat(mPoint.iY),
							Mercator.xToLon(mPoint1.iX),
							Mercator.yToLat(mPoint1.iY));
					mJobInstance.sendMapUpdate(iFeatureId, iObjectType,
							mGeoPoint, false,false);
				}
			} else {
				if (iObjectType == GSObjectType.GSO_POINT) {
					GeoPoint mGeoPoint = new GeoPoint(
							Mercator.xToLon(mPoint.iX),
							Mercator.yToLat(mPoint.iY),
							Mercator.xToLon(mPoint.iX),
							Mercator.yToLat(mPoint.iY));
					mJobInstance.sendMapUpdate(iFeatureId, iObjectType,
							mGeoPoint, false,false);

				} else {
					if (mPoint.iOffset != 0) {
						GeoPoint mGeoPoint = new GeoPoint(
								Mercator.xToLon(mPoint.iX),
								Mercator.yToLat(mPoint.iY),
								Mercator.xToLon(mPoint.iOffsetX),
								Mercator.yToLat(mPoint.iOffsetY));
						mJobInstance.sendMapUpdate(iFeatureId, iObjectType,
								mGeoPoint, false,true);

					} else {
						GeoPoint mGeoPoint = new GeoPoint(
								Mercator.xToLon(mPoint.iX),
								Mercator.yToLat(mPoint.iY),
								Mercator.xToLon(0),
								Mercator.yToLat(0));
						mJobInstance.sendMapUpdate(iFeatureId, iObjectType,
								mGeoPoint, false,false);
					}
				}
			}

		} else {
			mRectBoundary.stretch(mPoint.iX, mPoint.iY);
		}
     
		if ((aPointsList.size() >= (iNoOfPointsSentToDB + MAX_POINT_FOR_DB))
				&& (false == isObjectLoaded)) {
			sendObjectToDB(false);
			iNoOfPointsSentToDB = iNoOfPointsSentToDB + MAX_POINT_FOR_DB;
         Log.i(Constants.TAG_JOB_RECORDER,
               "addPointToObject() - number of points count has reached, so updated to db");
		}
	}

	/**
	 * Sends the object's points to the database
	 */
	protected void sendObjectToDB(boolean isLast) {
		if (null == aPointsList) {
			return;
		}
		aTempPointsList = new ArrayList<FGPPoint>();
		int iNoOfPtsToBeSentToDB = 0;
		if (isLast) {
			iNoOfPtsToBeSentToDB = aPointsList.size();
		} else {
			iNoOfPtsToBeSentToDB = iNoOfPointsSentToDB + MAX_POINT_FOR_DB;
		}
		if(false == isObjectRecFinished || false == isPostProcessingDone)
		{
      		   for (int i = iNoOfPointsSentToDB; i < iNoOfPtsToBeSentToDB
      				&& i < aPointsList.size(); i++) {
      			aTempPointsList.add(aPointsList.get(i));
      
      		   }
      		if (aTempPointsList.size() > 0) {
                            Thread thread = new Thread(myRunnable);
                            thread.start();
             Log.i(Constants.TAG_JOB_RECORDER,
                   "sendObjectToDB() - thread created for updating db");
                    }
		}
		else
		{
		   for (int i = 0; i < iNoOfPtsToBeSentToDB
                         && i < aPointsList.size(); i++) {
		      aTempPointsList.add(aPointsList.get(i));     
		      
                  }
		}
	
	
		
	}

	Runnable myRunnable = new Runnable() {

		public void run() {
			byte[] bBufferData = Utils.getFGPBlob(aTempPointsList);			
			mDataBase.updateFeatureVertex(iJobID, iFeatureId, bBufferData);
			aTempPointsList.clear();
			aTempPointsList = null;
                             Log.i(Constants.TAG_JOB_RECORDER,
                                   "Inside thread, updated vertex to db succesfully");
		}

	};

	/**
	 * Flushes the object's points.
	 */
	protected void flushObjectPoints() {
		if (null != aPointsList) {
			aPointsList.clear();
			aPointsList = null;
		}
	}

	/**
	 * Update length of the object.
	 */
	protected void updateLength() {
		if (null == aPointsList) {
			return;
		}
		FGPPoint mPoint = aPointsList.get(0);
		double dLen = 0.0;
		double dOffLen = 0.0;

		for (FGPPoint iterable_element : aPointsList) {
			dLen = dLen
					+ Mercator.tvFormulaDistance(mPoint.iX, mPoint.iY,
							iterable_element.iX, iterable_element.iY);
			CPoint mCurrPoint = mPoint.getoffsetCoordinates();
			CPoint mNextPoint = iterable_element.getoffsetCoordinates();
			dOffLen = dOffLen
					+ Mercator.tvFormulaDistance(mCurrPoint.iX,
							mCurrPoint.iY, mNextPoint.iX, mNextPoint.iY);
			mPoint = iterable_element;
		}
		dLength = (long) dLen;
		dOffsetLength = (long) dOffLen;

	}

	/**
	 * Update area of the object.
	 */
	protected void updateArea() {
		if (null == aPointsList) {
			return;
		}
		if (aPointsList.size() <= MIN_POINT) {
			dArea = 0;
			dOffsetArea = 0;
		} else {
			if (true == isObjectRecFinished) {
				FGPPoint mFirstPoint = aPointsList.get(0);
				for (int i = aPointsList.size()-1; i >= 0 ; i--) {
				   FGPPoint iterable_element = aPointsList.get(i);
				        double dD1 = 0;
					double dD2 = 0;
					double dD3 = 0;
					double dD4 = 0;

					dD1 = Mercator.tvFormulaDistance(mFirstPoint.iX,
							mFirstPoint.iY, mRectBoundary.left, mFirstPoint.iY);
					dD2 = Mercator.tvFormulaDistance(iterable_element.iX,
							iterable_element.iY, iterable_element.iX,
							mRectBoundary.top);
					dD3 = Mercator.tvFormulaDistance(iterable_element.iX,
							iterable_element.iY, mRectBoundary.left,
							iterable_element.iY);
					dD4 = Mercator.tvFormulaDistance(mFirstPoint.iX,
							mFirstPoint.iY, mFirstPoint.iX, mRectBoundary.top);

					dArea = dArea + (dD1 * dD2);
					dArea = dArea - (dD3 * dD4);

					CPoint mFirstOffsetPoint = mFirstPoint
							.getoffsetCoordinates();
					CPoint mNextOffsetPoint = iterable_element
							.getoffsetCoordinates();
					dD1 = Mercator.tvFormulaDistance(mFirstOffsetPoint.iX,
							mFirstOffsetPoint.iY, mRectOffsetBoundary.left,
							mFirstOffsetPoint.iY);
					dD2 = Mercator.tvFormulaDistance(mNextOffsetPoint.iX,
							mNextOffsetPoint.iY, mNextOffsetPoint.iX,
							mRectOffsetBoundary.top);
					dD3 = Mercator.tvFormulaDistance(mNextOffsetPoint.iX,
							mNextOffsetPoint.iY, mRectOffsetBoundary.left,
							mNextOffsetPoint.iY);
					dD4 = Mercator.tvFormulaDistance(mFirstOffsetPoint.iX,
							mFirstOffsetPoint.iY, mFirstOffsetPoint.iX,
							mRectOffsetBoundary.top);

					dOffsetArea = dOffsetArea + (dD1 * dD2);
					dOffsetArea = dOffsetArea - (dD3 * dD4);

					mFirstPoint = iterable_element;

				}
				dArea = dArea / 2;
				dArea = Math.abs(dArea);

				dOffsetArea = dOffsetArea / 2;
				dOffsetArea = Math.abs(dOffsetArea);
            Log.i(Constants.TAG_JOB_RECORDER,
                  "updateArea() - calculated object area = " + dOffsetArea);
			}
		}

	}

	/**
	 * Update the bounding box of the object
	 */
	protected void updateBoundingBox(FGPPoint mFGPPoint) {
		// Update Normal Bounding Box

		mRectBoundary.stretch(mFGPPoint.iX, mFGPPoint.iY);

		aFourPoints[3] = aFourPoints[2];
		aFourPoints[2] = aFourPoints[1];
		aFourPoints[1] = aFourPoints[0];
		aFourPoints[0] = mFGPPoint;
		
		boolean IsPointHasOffset =false;
        if(null!= aFourPoints[1])
        {
           if(0!= aFourPoints[1].iOffset)
           {
              IsPointHasOffset = true;
           }
        }
        
		//boolean IsPointHasOffset = (0 == mFGPPoint.iOffset) ? false : true;

		if (IsPointHasOffset) {

			if (false == isObjectLoaded) {

				calculateOffset(aFourPoints[2], aFourPoints[1], aFourPoints[0]);
				calculateOffset(aFourPoints[1], aFourPoints[0], null);
			}
			else
			{
			   if(false == isbInCorrectOffset())
			   {
			      if((null!= aFourPoints[1]) && (null!= aFourPoints[2]))
			         checkForParallelism(aFourPoints[1],aFourPoints[2]);
			   }
			}

			// *** Update the offset bounding rectangle for the new point
			if (null != aFourPoints[0]) {
				FGPPoint mPoint = aFourPoints[0];
				CPoint mOffsetPoint = mPoint.getoffsetCoordinates();
				mRectOffsetBoundary.stretch(mOffsetPoint.iX, mOffsetPoint.iY);

			}

			// *** Update the offset bounding rectangle for the previous point
			// that may have moved
			if (null != aFourPoints[1]) {

				FGPPoint mPoint = aFourPoints[1];
				CPoint mOffsetPoint = mPoint.getoffsetCoordinates();
				mRectOffsetBoundary.stretch(mOffsetPoint.iX, mOffsetPoint.iY);

			}
		} else {
			mRectOffsetBoundary = mRectBoundary;
		}
	}

	protected void updateObjectDetailsToDb() {
		updateArea();
		// Remove it here so that it is already done.
		// updateLength();
		Feature mObject = new Feature();
		mObject.setId(iFeatureId);
		mObject.setBottomRightX(mRectOffsetBoundary.right);
		mObject.setBottomRightY(mRectOffsetBoundary.bottom);
		mObject.setTopLeftX(mRectOffsetBoundary.left);
		mObject.setTopLeftY(mRectOffsetBoundary.top);
		mObject.setPerimeter(dOffsetLength);
		mObject.setArea((long) dOffsetArea);
		if(isPostProcessingDone)
		{
   		if(null!= aTempPointsList)
   		{	
   		   byte[] bBufferData = Utils.getFGPBlob(aTempPointsList);
   		   mObject.setVertex(bBufferData);
   		   aTempPointsList.clear();
   		}	
   		
                   aTempPointsList = null;
		}
		mDataBase.updateFeature(mObject);
      Log.i(Constants.TAG_JOB_RECORDER,
            "updateObjectDetailsToDb() - after feature recording finished/saved, updated feature details to db");
	}

	protected void checkForAutoClose(FGPPoint mPoint, boolean isFixFiltered) {
		if ((null != aPointsList) && (aPointsList.size() > MIN_POINT)
				&& (dLength > iAutoCloseDistance + 5)) {
			int iLastX = mPoint.iX;
			int iLastY = mPoint.iY;

			FGPPoint mFirstPoint = aPointsList.get(0);
			int iFirstX = mFirstPoint.iX;
			int iFirstY = mFirstPoint.iY;

			// int distance = 0;
			int distance = (int) Mercator.tvFormulaDistance(iFirstX, iFirstY,
					iLastX, iLastY);
			if (distance < iAutoCloseDistance) {
			   if (!isFixFiltered){
				   //Log.i("GSObject", "Condition for autoclose has passed, so adding the unfiltered point to the points list =" + distance);
			      // Add the unfiltered point to the polygon that satisfied the 
			      // autoclose check
			      //addPointToObject(mPoint, isFixFiltered);
			   }
			   isCheckForAutoClose = false;
            Log.i(Constants.TAG_JOB_RECORDER,
                  "checkForAutoClose() - Autoclose for feature callback given");
			   mJobInstance.autoCloseFeature();
			}
		}
	}

	protected void closePath() {
		if (null != aPointsList &&  aPointsList.size() > MIN_POINT) {
			FGPPoint mLastPoint = aPointsList.get(aPointsList.size() - 1);
			FGPPoint mLastToLastPoint = aPointsList.get(aPointsList.size() - 2);
			GeoPoint mGeoPoint = null;
			if (mLastPoint.iOffset != 0) {
				mGeoPoint = new GeoPoint(Mercator.xToLon(mLastPoint.iX),
						Mercator.yToLat(mLastPoint.iY),
						Mercator.xToLon(mLastPoint.iOffsetX),
						Mercator.yToLat(mLastPoint.iOffsetY));
				mJobInstance.sendMapUpdate(iFeatureId, iObjectType, mGeoPoint,true,true);
			} else {
				mGeoPoint = new GeoPoint(Mercator.xToLon(mLastToLastPoint.iX),
				                Mercator.yToLat(mLastToLastPoint.iY),
						Mercator.xToLon(mLastPoint.iX),
						Mercator.yToLat(mLastPoint.iY));
				mJobInstance.sendMapUpdate(iFeatureId, iObjectType, mGeoPoint,false,false);
				
				mGeoPoint = new GeoPoint(Mercator.xToLon(mLastPoint.iX),
                                      Mercator.yToLat(mLastPoint.iY),
                                      Mercator.xToLon(mLastPoint.iX),
                                      Mercator.yToLat(mLastPoint.iY));
                      mJobInstance.sendMapUpdate(iFeatureId, iObjectType, mGeoPoint,true,false);
			}

			
			Log.i(Constants.TAG_JOB_RECORDER,
               "closePath() - path closed and map update given");
		}		
	}

	protected void closeBoundary() {
		if (null != aPointsList) {
			if (aPointsList.size() > MIN_POINT) {
				if (true == isObjectLoaded) {
					FGPPoint mLastPoint = aPointsList
							.get(aPointsList.size() - 1);
					FGPPoint mFirstPoint = aPointsList.get(0);
					GeoPoint mGeoPoint = null;
					if (mLastPoint.iOffset == 0) {
						mGeoPoint = new GeoPoint(
								Mercator.xToLon(mFirstPoint.iX),
								Mercator.yToLat(mFirstPoint.iY),
								Mercator.xToLon(mLastPoint.iX),
								Mercator.yToLat(mLastPoint.iY));

						mJobInstance.sendMapUpdate(iFeatureId, iObjectType,
                                                      mGeoPoint, true,false);
					} else {
						mGeoPoint = new GeoPoint(
								Mercator.xToLon(mFirstPoint.iX),
								Mercator.yToLat(mFirstPoint.iY),
								Mercator.xToLon(mLastPoint.iOffsetX),
								Mercator.yToLat(mLastPoint.iOffsetY));
						mJobInstance.sendMapUpdate(iFeatureId, iObjectType,
                                                      mGeoPoint, true,true);

					}
					

				} else {
					ArrayList<FGPPoint> aFirstThreePoints = new ArrayList<FGPPoint>();
					for (int i = 0; i <= MIN_POINT; i++) {
						FGPPoint array_element = aPointsList.get(i);
						aFirstThreePoints.add(array_element);
					}

					ArrayList<FGPPoint> aLastThreePointsTemp = new ArrayList<FGPPoint>();
					int iSize = aPointsList.size();
					for (int i = iSize; i >= iSize - MIN_POINT; i--) {
						FGPPoint array_element = aPointsList.get(i - 1);
						aLastThreePointsTemp.add(array_element);
					}
					FGPPoint mLastPoint = aLastThreePointsTemp.get(0);
					FGPPoint mFirstPoint = aFirstThreePoints.get(0);
					ArrayList<FGPPoint> aLastThreePoints = new ArrayList<FGPPoint>(
							3);
					if ((mLastPoint.iX != mFirstPoint.iX)
							|| (mLastPoint.iY != mFirstPoint.iY)) {
						aLastThreePoints.add(0, aFirstThreePoints.get(0));
						aLastThreePoints.add(1, aLastThreePointsTemp.get(0));
						aLastThreePoints.add(2, aLastThreePointsTemp.get(1));

					} else {
						aLastThreePoints = aLastThreePointsTemp;
					}

					calculateOffset(aLastThreePoints.get(2),
							aLastThreePoints.get(1), aFirstThreePoints.get(0));
					calculateOffset(aLastThreePoints.get(1),
							aFirstThreePoints.get(0), aFirstThreePoints.get(1));

					// Bounding box/Area/Length may change after calculating
					// offset
					// value with the new point.
					if (null != aLastThreePoints.get(1)) {
						FGPPoint mFGPPoint = aLastThreePoints.get(1);
						CPoint mOffsetPoint = mFGPPoint.getoffsetCoordinates();
						mRectOffsetBoundary.stretch(mOffsetPoint.iX,
								mOffsetPoint.iY);
					}

					if (null != aFirstThreePoints.get(0)) {
						FGPPoint mFGPPoint = aFirstThreePoints.get(0);
						CPoint mOffsetPoint = mFGPPoint.getoffsetCoordinates();
						mRectOffsetBoundary.stretch(mOffsetPoint.iX,
								mOffsetPoint.iY);
					}

					// Sending a map update for offset added first point for
					// closing.
					mFirstPoint = aFirstThreePoints.get(0);
					GeoPoint mGeoPoint = null;
					if (aLastThreePoints.get(1).iOffset != 0) {
						mGeoPoint = new GeoPoint(
								Mercator.xToLon(aLastThreePoints.get(1).iX),
								Mercator.yToLat(aLastThreePoints.get(1).iY),
								Mercator.xToLon(aLastThreePoints.get(1).iOffsetX),
								Mercator.yToLat(aLastThreePoints.get(1).iOffsetY));
						mJobInstance.sendMapUpdate(iFeatureId, iObjectType,
                                                      mGeoPoint, true,true);
					} else {
						mGeoPoint = new GeoPoint(
								Mercator.xToLon(aLastThreePoints.get(1).iX),
								Mercator.yToLat(aLastThreePoints.get(1).iY),
								Mercator.xToLon(aLastThreePoints.get(1).iX),
								Mercator.yToLat(aLastThreePoints.get(1).iY));
						mJobInstance.sendMapUpdate(iFeatureId, iObjectType,
                                                      mGeoPoint, true,false);
					}
					
               

               Log.i(Constants.TAG_JOB_RECORDER,
                     "closeBoundary() - path closed and map updates given");
					// *** Check for backwards polygon and fix it
					if (!calIsClockWise()) {

						// *** If the boundary is not clockwise, we are making
						// it
						// clockwise,so we need to change the offset from left
						// to
						// right,
						// or
						// vice versa.

//						for (FGPPoint mPoint : aPointsList) {
//							mPoint.iOffset = -(mPoint.iOffset);
//						}
					}
				}
			}
		}
	}

	protected boolean calIsClockWise() {
		double X1 = 0;
		double Y1 = 0;
		double X2 = 0;
		double Y2 = 0;
		double dArea = 0.0;

		FGPPoint mPoint = aPointsList.get(0);

		for (FGPPoint iterable_element : aPointsList) {

			X1 = mPoint.iX - mRectBoundary.left;
			Y1 = mPoint.iY - mRectBoundary.top;

			X2 = iterable_element.iX - mRectBoundary.left;
			Y2 = iterable_element.iY - mRectBoundary.top;

			dArea += X1 * Y2 - Y1 * X2;

			mPoint = iterable_element;
		}

		return (dArea > 0);

	}

	protected void calculateOffset(FGPPoint mPrevPoint, FGPPoint mCurrPoint,
			FGPPoint mNextPoint) {
		double Theta = 0.0;
		double dOffset = 0.0;
		double dOrigOffset = 0.0;
		VertexOffset eOffsetDir = VertexOffset.OFFSET_NONE;
		PolarCoord PolarOA = null;
		boolean bCheckOffset = false;

                CPoint O;
                CPoint A;
                CPoint B;

		if (((null == mPrevPoint) && (null == mNextPoint) || (null == mCurrPoint)))
			return;

		CPoint X = new CPoint(mCurrPoint.iX, mCurrPoint.iY);
		dOrigOffset = mCurrPoint.iOffset;

		if (0 != mCurrPoint.iOffset) {
			eOffsetDir = VertexOffset.OFFSET_RIGHT;
			if (mCurrPoint.iOffset < 0) {
				eOffsetDir = VertexOffset.OFFSET_LEFT;
				dOrigOffset = -dOrigOffset;
			}
		}

		if ((dOrigOffset != 0)
				&& (eOffsetDir == VertexOffset.OFFSET_LEFT || eOffsetDir == VertexOffset.OFFSET_RIGHT)) {

			if ((null == mPrevPoint) || (null == mNextPoint)) {
				

				if (null == mPrevPoint) {
					O = new CPoint(mCurrPoint.iX, mCurrPoint.iY);
					A = new CPoint(mNextPoint.iX, mNextPoint.iY);
				} else {
					A = new CPoint(mCurrPoint.iX, mCurrPoint.iY);
					O = new CPoint(mPrevPoint.iX, mPrevPoint.iY);					
				}

				CPoint OA = new CPoint((A.iX - O.iX), (A.iY - O.iY));

				OA.iY = -OA.iY;

				PolarOA = new PolarCoord(OA);

				Theta = PolarOA.dAngle - (Mercator.PI / 2);

				if (Theta < 0)
					Theta += Mercator.PI * 2;

				dOffset = dOrigOffset;
			} else {


			        bCheckOffset = true;
				O = new CPoint(mCurrPoint.iX, mCurrPoint.iY);
				A = new CPoint(mPrevPoint.iX, mPrevPoint.iY);
				B = new CPoint(mNextPoint.iX, mNextPoint.iY);

				CPoint OA = new CPoint((A.iX - O.iX), (A.iY - O.iY));
				CPoint OB = new CPoint((B.iX - O.iX), (B.iY - O.iY));

				OA.iY = -(OA.iY);
				OB.iY = -(OB.iY);

				PolarOA = new PolarCoord(OA);
				PolarCoord PolarOB = new PolarCoord(OB);

				Theta = (PolarOA.dAngle + PolarOB.dAngle) / 2.0;

				if (PolarOA.dAngle > Theta) {
					Theta += Mercator.PI;
				}

				dOffset = dOrigOffset
						/ Math.cos((Mercator.PI - Math.abs(PolarOA.dAngle
								- PolarOB.dAngle)) / 2);
				if (dOffset > dOrigOffset * 3)
					dOffset = dOrigOffset * 3;
				else if (dOffset < dOrigOffset * -3)
					dOffset = dOrigOffset * -3;
			}

			if (eOffsetDir == VertexOffset.OFFSET_LEFT)
				dOffset = -dOffset;

			double deltaX = dOffset * Math.cos(Theta);
			double deltaY = dOffset * Math.sin(Theta);

			int iX = Mercator.MoveEast(X, deltaX);
			int iY = Mercator.MoveNorth(X, deltaY);
			X.iX = iX;
			X.iY = iY;
		}

		mCurrPoint.iOffsetX = X.iX;
		mCurrPoint.iOffsetY = X.iY;
		
		if( bCheckOffset && false == isbInCorrectOffset())
		{		   
   		   checkForParallelism(mCurrPoint,mPrevPoint);
		}
	
	}
	private void checkForParallelism(FGPPoint mCurrPoint, FGPPoint mPrevPoint) {
	   if((mCurrPoint.iOffset < 0 && mPrevPoint.iOffset > 0) || 
	               (mCurrPoint.iOffset > 0 && mPrevPoint.iOffset < 0))
	         {
	            return;
	         }
	   if(mCurrPoint.iOffset == 0 || mPrevPoint.iOffset == 0)
	   {
	      return;
	   }
	   CPoint offsetA = mPrevPoint.getoffsetCoordinates();
           CPoint offsetB = mCurrPoint.getoffsetCoordinates();
  
           CPoint offsetedPoint = new CPoint(
                 (offsetA.iX - offsetB.iX),
                 (offsetA.iY - offsetB.iY));
  
           CPoint OA = new CPoint((mPrevPoint.iX - mCurrPoint.iX),
                 (mPrevPoint.iY - mCurrPoint.iY));
           offsetedPoint.iY = -(offsetedPoint.iY);
           OA.iY = -(OA.iY);
  
           PolarCoord PolarOA = new PolarCoord(OA);
           PolarCoord PolarOB = new PolarCoord(offsetedPoint);
           int realAngle = (int) Math.round(Math.toDegrees(PolarOA.dAngle));
           int offsetAngle = (int) Math.round(Math.toDegrees(PolarOB.dAngle));
           if(Math.abs(realAngle - offsetAngle) >=15)
           {
              setbInCorrectOffset(true);
           }
	}
      
      
   

   /**
     * @return the iObjectType
     */
    public int getObjectType() {
        return iObjectType;
    }
  
   private int checkOffsets(int i) {
      int realAngle = 0;
      int offsetAngle = 0;
      boolean isParralel = true;
      do {
         if (i == aPointsList.size() - 1) {
            if(iObjectType == GSObjectType.GSO_BOUNDARY || iObjectType == GSObjectType.GSO_POLYGON)
            {
                  aFourPoints[0] = aPointsList.get(i);
                  aFourPoints[1] = aPointsList.get(0);
                  i = aPointsList.size();
            }
            else
            {
               i = aPointsList.size() +1;
               break;
            }
         } else {
            aFourPoints[0] = aPointsList.get(i++);
            aFourPoints[1] = aPointsList.get(i);
         }
         boolean isSameDirOffset = false;
         if((aFourPoints[1].iOffset < 0 && aFourPoints[0].iOffset < 0) || 
               (aFourPoints[1].iOffset > 0 && aFourPoints[0].iOffset > 0))
         {
            isSameDirOffset = true;
         }
         if(aFourPoints[1].iOffset!= 0 && aFourPoints[0].iOffset!= 0 && isSameDirOffset)
         {   
            
            CPoint offsetA = aFourPoints[1].getoffsetCoordinates();
            CPoint offsetB = aFourPoints[0].getoffsetCoordinates();
   
            CPoint offsetedPoint = new CPoint(
                  (offsetA.iX - offsetB.iX),
                  (offsetA.iY - offsetB.iY));
   
            CPoint OA = new CPoint((aFourPoints[1].iX - aFourPoints[0].iX),
                  (aFourPoints[1].iY - aFourPoints[0].iY));
            offsetedPoint.iY = -(offsetedPoint.iY);
            OA.iY = -(OA.iY);
   
            PolarCoord PolarOA = new PolarCoord(OA);
            PolarCoord PolarOB = new PolarCoord(offsetedPoint);
            realAngle = (int) Math.round(Math.toDegrees(PolarOA.dAngle));
            offsetAngle = (int) Math.round(Math.toDegrees(PolarOB.dAngle));
            if (i == aPointsList.size()) {
               if (Math.abs(realAngle - offsetAngle) >= 15){
                  isParralel = false;
               }            
               else
               {
                  i = aPointsList.size() +1;
                  break;
               }
            } else {
   //            if(realAngle > 180 && realAngle < 360)
   //            {
   //               realAngle = 360 - real
   //            }
               if (Math.abs(realAngle - offsetAngle) >= 15) {
                  isParralel = false;
               }
   //            if(Math.abs(realAngle-offsetAngle) > 175 && Math.abs(realAngle-offsetAngle) < 185)
   //            {
   //               isParralel = false;
   //            }
   
            }
         }
         else
         {
            if (i == aPointsList.size())
            {
               i = aPointsList.size() +1;
               break;
            }
         }
         
      } while (isParralel);
      return i;

   }

   public void postProcessingPoints() {
      if(null!= aPointsList &&  (aPointsList.size() >= MIN_POINT)) 
      {
       
      aFourPoints = null;
      aFourPoints = new FGPPoint[3];
      int i = 0;
      boolean isPointRemoved = false;
      do {

         int index = checkOffsets(i);
         if (index <= aPointsList.size() - 1) {
               aPointsList.remove(aPointsList.get(index));
               isPointRemoved = true;
               index--;
               if(aPointsList.size() == 2)
               {
                  aPointsList.clear();                  
                  break;
               }
               if (index == 0){                            
                  aFourPoints[2] = aPointsList.get(index);                  
                  aFourPoints[0] = aPointsList.get(index + 1);
                  aFourPoints[1] = aPointsList.get(index + 2);
                  
                  calculateOffset(aFourPoints[2], aFourPoints[0],
                        aFourPoints[1]);                  
                  
                  aFourPoints[2] = aPointsList.get(aPointsList.size() - 1);
                  aFourPoints[0] = aPointsList.get(index);
                  aFourPoints[1] = aPointsList.get(index + 1);
                  
                  
                  calculateOffset(aFourPoints[2], aFourPoints[0],
                        aFourPoints[1]);
               } else {
                  if (index < aPointsList.size() - 2) {                                          
                     aFourPoints[2] = aPointsList.get(index);
                     aFourPoints[0] = aPointsList.get(index + 1);
                     aFourPoints[1] = aPointsList.get(index + 2);
                     

                     calculateOffset(aFourPoints[2], aFourPoints[0],
                           aFourPoints[1]);
                     
                     aFourPoints[2] = aPointsList.get(index - 1);
                     aFourPoints[0] = aPointsList.get(index);
                     aFourPoints[1] = aPointsList.get(index + 1);
                     
                     
                     calculateOffset(aFourPoints[2], aFourPoints[0],
                           aFourPoints[1]);
                  } else {
                     if (index == aPointsList.size() - 1) {
                        
                        aFourPoints[2] = aPointsList.get(index);
                        aFourPoints[0] = aPointsList.get(0);
                        aFourPoints[1] = aPointsList.get(1);
                        
                        
                        calculateOffset(aFourPoints[2], aFourPoints[0], aFourPoints[1]);                       
                        
                        aFourPoints[2] = aPointsList.get(index - 1);
                        aFourPoints[0] = aPointsList.get(index);
                        aFourPoints[1] = aPointsList.get(0);
                        
                        
                        calculateOffset(aFourPoints[2], aFourPoints[0], aFourPoints[1]);
                     }
                     else if (index == aPointsList.size() - 2) {                        
                        aFourPoints[2] = aPointsList.get(index);
                        aFourPoints[0] = aPointsList.get(index+1);
                        aFourPoints[1] = aPointsList.get(0);
                        

                        calculateOffset(aFourPoints[2], aFourPoints[0],
                              aFourPoints[1]);
                                              
                        aFourPoints[2] = aPointsList.get(index - 1);
                        aFourPoints[0] = aPointsList.get(index);
                        aFourPoints[1] = aPointsList.get(index + 1);
                        
                        
                        calculateOffset(aFourPoints[2], aFourPoints[0],
                              aFourPoints[1]);
                     }
                  }
               }            
         } else {
            if (index == aPointsList.size()) {
               index--;
               aPointsList.remove(aPointsList.get(index));     
               index--;  
               isPointRemoved = true;
               aFourPoints[2] = aPointsList.get(index);
               aFourPoints[0] = aPointsList.get(0);
               aFourPoints[1] = aPointsList.get(1);
               
               calculateOffset(aFourPoints[2], aFourPoints[0], aFourPoints[1]);
               
               
               aFourPoints[2] = aPointsList.get(index - 1);
               aFourPoints[0] = aPointsList.get(index);
               aFourPoints[1] = aPointsList.get(0);
               calculateOffset(aFourPoints[2], aFourPoints[0], aFourPoints[1]);
            }
            else
            {
               index++;
            }
         }         
         if(index == 0)
         {
            i = 0;
         }
         else
         {
            i = index-1;
         }
      } while (i != aPointsList.size()+1 );
      if(true == isPointRemoved)
      {         
         if(aPointsList.size() <= 2)
         {
            aPointsList.clear();            
            mDataBase.deleteFeatureById(iJobID, iFeatureId);
         }     

            if(aPointsList.size() > 0)               
            {
               mRectOffsetBoundary = new BoundingBox();
               for (FGPPoint mPoint : aPointsList) {
               
               CPoint mOffsetPoint = mPoint.getoffsetCoordinates();
               mRectOffsetBoundary.stretch(mOffsetPoint.iX, mOffsetPoint.iY);
               }
               updateLength();
            }

         }
      }
   }

   public boolean isObjectTobeRefreshed() {
      return isObjectTobeRefreshed;
   }

   public void setObjectTobeRefreshed(boolean isObjectTobeRefreshed) {
      this.isObjectTobeRefreshed = isObjectTobeRefreshed;
   }

   public boolean isbInCorrectOffset() {
      return bInCorrectOffset;
   }

   public void setbInCorrectOffset(boolean bInCorrectOffset) {
      this.bInCorrectOffset = bInCorrectOffset;
   }
   
   
}


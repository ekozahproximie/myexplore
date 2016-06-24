package com.trimble.agmantra.layers;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.entity.Feature;
import com.trimble.agmantra.entity.JobTransaction;

import com.trimble.agmantra.dbutil.Log;

import java.util.ArrayList;

/**
 * The Class GSObjectLayer.Base class for PolygonLayer,PointLayer,PathLayer and
 * BoundaryLayer classes.
 * 
 */
public abstract class GSObjectLayer {

	/** Array list having the list of GSObjects */
	public ArrayList<GSObject> objectList;

	protected GSObject mcurrGSObject;
	

	protected boolean isAutoCloseEnabled = false;
	protected int iAutoCloseDistance = 0;

	/**
	 * Constructor
	 */
	public GSObjectLayer() {
		objectList = null;
		mcurrGSObject = null;

	}

	/**
	 * Update status of current object.
	 * 
	 * @param isFinished
	 *            Boolean variable stating whether the current recording object
	 *            is finished or not.
	 */
	public void setStatusOfRecordingObject(boolean isFinished,boolean isCorrectionRequired) {
		if (null != mcurrGSObject) {
			if (false == mcurrGSObject.isObjectLoaded) {
			   if(isFinished && isCorrectionRequired)
			   {
			      mcurrGSObject.isPostProcessingDone = true;
			      mcurrGSObject.postProcessingPoints();
			   }
			   if(true == isFinished)
			   {
			      mcurrGSObject.setObjectTobeRefreshed(true);
			   }
			
				JobTransaction mfeatTxn = mcurrGSObject.mDataBase
						.getFeatureInfoFromTxn(mcurrGSObject.iJobID,
								mcurrGSObject.iFeatureId);
				if (null != mfeatTxn) {
					mcurrGSObject.isObjectRecFinished = isFinished;
					if (null != mcurrGSObject.aPointsList) {
						JobTransaction mJobTxn = new JobTransaction();
						mJobTxn.setAttrindexId(mcurrGSObject.iAttributeId);
						mJobTxn.setPassid(mcurrGSObject.iPassId);
						mJobTxn.setJobId(mcurrGSObject.iJobID);
						mJobTxn.setFeatureId(mcurrGSObject.iFeatureId);
						mJobTxn.setStatus((true == isFinished) ? 1 : 0);
						mcurrGSObject.mDataBase.updateJobTxn(mJobTxn, false);
						mcurrGSObject.sendObjectToDB(true);
						mcurrGSObject.updateObjectDetailsToDb();
						
                  Log.i(Constants.TAG_JOB_RECORDER,
                        "setStatusOfRecordingObject() - Object Valid, Object details updated to db");
					}
				}
			} else {
				if (false == isFinished) {
					mcurrGSObject.mJobInstance
							.featureIncompletePreviously(mcurrGSObject.iFeatureId);
					mcurrGSObject.isObjectLoaded = false;
					mcurrGSObject.iNoOfPointsSentToDB = mcurrGSObject.aPointsList
							.size();
               Log.i(Constants.TAG_JOB_RECORDER,
                     "setStatusOfRecordingObject() - Object Valid, succesfully loaded and incomplete obejct");
				} else {
					// mcurrGSObject.mJobInstance.closeFeature(mcurrGSObject.iFeatureId);
               mcurrGSObject.isObjectRecFinished = true;					
               Log.i(Constants.TAG_JOB_RECORDER,
                     "setStatusOfRecordingObject() - Object Valid, succesfully loaded");
				}
			}
		}
	}

	public boolean isCurrObjectValid() {
		boolean isCurrObectValid = false;
		if ((null != mcurrGSObject) && (null != mcurrGSObject.aPointsList)) {
			if ((mcurrGSObject.iObjectType == GSObjectType.GSO_BOUNDARY)
					|| (mcurrGSObject.iObjectType == GSObjectType.GSO_POLYGON)) {
				if (mcurrGSObject.aPointsList.size() > 2) {
					isCurrObectValid = true;
				}
			} else {
				if (mcurrGSObject.aPointsList.size() > 1) {
					isCurrObectValid = true;
				}
			}
		}
		return isCurrObectValid;
	}
	

               
              
           
   

	public void suppressAutoCloseCheck() {
		if (null != mcurrGSObject) {
			mcurrGSObject.isCheckForAutoClose = false;
		}
	}

	public void updateBoundingBox(Feature feature) {
		if (null != mcurrGSObject) {
			mcurrGSObject.mRectOffsetBoundary = new BoundingBox(
					feature.getTopLeftX(), feature.getTopLeftY(),
					feature.getBottomRightX(), feature.getBottomRightY());
		}

	}

   /**
    * Adds the object to list.
    * 
    * @param isLoaded
    *           boolean variable stating whether the object added is a recorded
    *           or loaded one.
    * @param iFeatureId
    *           feature id of the object.
    */
   public void addObjectToList(boolean isLoaded, GSObject mObject) {
      if (null == objectList) {
         objectList = new ArrayList<GSObject>();
      }
      mObject.isObjectLoaded = isLoaded;
      objectList.add(mObject);
      mcurrGSObject = mObject;

   }

   /**
    * Method for flushing the recorded objects.
    */
   public void flushObjects() {
      if (null != objectList) {
         for (GSObject iterable_element : objectList) {
            iterable_element.flushObjectPoints();
         }
         objectList.clear();
         objectList = null;
         mcurrGSObject = null;
      }
   }
   
   public void removeObject(long iFeatureId) {
	      for (GSObject iterable_element : objectList) {
	         if (iterable_element.iFeatureId == iFeatureId) {
	            iterable_element.mJobInstance.removeFeature(iFeatureId,
	                  iterable_element.iObjectType);
	            objectList.remove(iterable_element);
	            break;
	         }
	      }
	   }

   public void addPointToCurrObject(FGPPoint mPoint, boolean isFixFiltered) {
      // TODO Auto-generated method stub

   }
   public void resetCurrGSObject() {
      if (null == mcurrGSObject) {
         int iIncompleteObjectIndex = -1;
         for (int i = 0; i < objectList.size(); i++) {
            if (objectList.get(i).isObjectRecFinished == false) {
               iIncompleteObjectIndex = i;
            }
         }
         if (iIncompleteObjectIndex != -1) {
            mcurrGSObject = objectList.get(iIncompleteObjectIndex);
         }
      }
   }
   /**
 * @return the mcurrGSObject
 */
public GSObject getMcurrGSObject() {
    return mcurrGSObject;
}

public void setCurrGSObjectNull() {
   mcurrGSObject = null;
}
/**
 * @return the objectList
 */
public ArrayList<GSObject> getObjectList() {
    return objectList;
}
}

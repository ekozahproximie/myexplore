package com.trimble.agmantra.layers;

import java.util.ArrayList;

import com.trimble.agmantra.entity.JobTransaction;

// TODO: Auto-generated Javadoc
/**
 * The Class PointLayer.
 */
public class PointLayer extends GSObjectLayer {
	GSOPoint mCurrGSOPoint;

	/**
	 * Construct for the class.
	 * 
	 * @param iJobId
	 *            Parameter stating the Job Id on which the job is getting
	 *            recorded
	 */
	public PointLayer() {
		objectList = null;
		mCurrGSOPoint = null;

   }

   @Override
   public void addPointToCurrObject(FGPPoint mPoint, boolean bisFixFiltered) {
      if (null != mCurrGSOPoint) {
         mCurrGSOPoint.addPointToObject(mPoint, bisFixFiltered);
         setStatusOfRecordingObject(true,false);
      }
   }

   @Override
   public void setStatusOfRecordingObject(boolean isFinished,boolean isCorrectionRequired) {
      if ((null != mCurrGSOPoint) && (false == mCurrGSOPoint.isObjectLoaded)) {
         JobTransaction mJobTxn = new JobTransaction();
         mJobTxn.setAttrindexId(mCurrGSOPoint.iAttributeId);
         mJobTxn.setPassid(mCurrGSOPoint.iPassId);
         mJobTxn.setJobId(mCurrGSOPoint.iJobID);
         mJobTxn.setFeatureId(mCurrGSOPoint.iFeatureId);
         mJobTxn.setStatus((true == isFinished) ? 1 : 0);
         mCurrGSOPoint.mDataBase.updateJobTxn(mJobTxn, false);
         mCurrGSOPoint.isObjectRecFinished = isFinished;
         mCurrGSOPoint.sendObjectToDB(true);
         mCurrGSOPoint.updateObjectDetailsToDb();                 
      }
      mCurrGSOPoint = null;
   }

   @Override
   public void addObjectToList(boolean isLoaded, GSObject mObject) {
      if (null == objectList) {
         objectList = new ArrayList<GSObject>();
      }
      mObject.isObjectLoaded = isLoaded;
      mCurrGSOPoint = (GSOPoint) mObject;
      objectList.add(mObject);
   }  

}

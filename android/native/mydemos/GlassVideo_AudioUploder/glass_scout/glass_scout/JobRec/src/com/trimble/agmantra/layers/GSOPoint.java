package com.trimble.agmantra.layers;

import com.trimble.agmantra.job.Job;
import com.trimble.agmantra.utils.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class GSOPoint.
 */
public class GSOPoint extends GSObject {

   /**
    * Construct for the class.
    * 
    * @param iJobId
    *           Parameter stating the Job Id on which the
    *           com.trimble.agmantra.job is getting recorded
    * @param iFeatureId
    *           Parameter stating the feature id of the object created
    * @param iAttributeId
    * @param iPassId
    */
   public GSOPoint(long iJobId, long iFeatureId, int iPassId, int iAttributeId,
         Job mJobInstance) {
      super(iJobId, iFeatureId, iPassId, iAttributeId, mJobInstance);

   }

   /*
    * (non-Javadoc)
    * 
    * @see
    * com.trimble.agmantra.job.GSObject#addPointToObject(com.trimble.agmantra
    * .job.PointInfo)
    */
   @Override
   public void addPointToObject(FGPPoint mPoint, boolean bisFixFiltered) {
      iObjectType = GSObjectType.GSO_POINT;
      mPoint.iObjectType = iObjectType;
      super.addPointToObject(mPoint, bisFixFiltered);
   }

   /*
    * (non-Javadoc)
    * 
    * @see JOb.GSObject#sendObjectToDB()
    */
   @Override
   public void sendObjectToDB(boolean isLast) {
      // TODO: create raw data (byte file) and update the db with the byte
      // file.
      byte[] mBuffer = Utils.getFGPBlob(aPointsList);
      mDataBase.updateFeatureVertex(iJobID, iFeatureId, mBuffer);
      aPointsList.clear();
   }

}

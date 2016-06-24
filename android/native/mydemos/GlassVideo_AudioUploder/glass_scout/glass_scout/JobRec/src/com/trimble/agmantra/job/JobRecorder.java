package com.trimble.agmantra.job;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.Log;
import com.trimble.agmantra.entity.JobTransaction;
import com.trimble.agmantra.layers.BoundingBox;
import com.trimble.agmantra.layers.FGPPoint;
import com.trimble.agmantra.layers.GSObjectType;
import com.trimble.agmantra.utils.Utils;

/**
 * The Class JobRecorder.
 */
public class JobRecorder {

	/**
	 * Variable to store the Job instance
	 */
	private Job mJobInstance = null;

	/**
	 * Variable to find whether the com.trimble.agmantra.job is in paused or
	 * running state.
	 */
	private boolean isJobPaused = false;

	/**
	 * Construct for the class
	 * 
	 * @param iJobId
	 *            Parameter stating the Job Id on which the
	 *            com.trimble.agmantra.job is getting recorded
	 */

	public JobRecorder(long iJobId,FarmWorksContentProvider mDataBase) {

		mJobInstance = new Job(iJobId,mDataBase);

	}

	public long[] startTask() {
		long[] iFeatureIds = null;
		if (null != mJobInstance) {
			iFeatureIds = mJobInstance.startTask();
		}
		return iFeatureIds;

	}

	public boolean completeTask() {
		// This function is responsible for pushing the remaining points to DB
		// and performing the activities
		// related to
		// creating an encoder and queuing the task to the encoder for upload.
		// Here the temp data structures such as the overlay and others can be
		// purged
		// since DB has all the required data.

		// TODO:: decide who changes the task status to finished.
		boolean isTaskValid = true;
		if (null != mJobInstance) {
			isTaskValid = mJobInstance.completeTask();
		}
		return isTaskValid;
	}

	public void saveTask() {
		// Takes care of dumping the remaining points to the DB.
		mJobInstance.saveTask();
	}

	public void removeTask(boolean isCancel) {
		// Purges the data related to task from local data structures and the DB
		if (null != mJobInstance) {
		    if(false== isCancel && mJobInstance != null){
		        Utils.deleteDirectory(Constants.getFlagStoreDir_Job(mJobInstance.iJobId));
		    }
			mJobInstance.removeTask(isCancel);
		}

	}

	public void pauseTask() {
		// Pauses the current task and ignores the point added to the feature
		// currently recorded that
		// comes through the AddFGPPoint API. Does not block adding of logging
		// points data with Attributes.
	}

	public void resumeTask() {
		// Resumes the task from pause state. Ignore if the state is started or
		// stopped.
	}

	public long addLoggedPoint(FGPPoint mPoint) {
		long iFeatureId = -1;
		if (null != mJobInstance) {
			iFeatureId = mJobInstance.createPointObject();
			mJobInstance.addFGPPointToFeature(mPoint, GSObjectType.GSO_POINT,true);
		}
		return iFeatureId;
		// Responsible for adding a logged point.
		// 1. communicates with the DB manager for creating a feature of type
		// point and retrieve it's ID
		// 2. Accumulate the point in the q
		// 3. Provide the feature ID back to the UI
	}

	public void removeFeature(long iFeatureId) {
		// Responsible for deleting the logged point pertaining to the featureID
		// 1. delete attributes in the attribute table if present
		// 2. delete feature id from the job-transaction table
		// 3. del from feature table if present. (if DB manager takes care of
		// this step ignore)
		if (null != mJobInstance) {
			mJobInstance.removeFeature(iFeatureId);
		}
	}

	public void moveLoggedPoint(long iFeatureId, FGPPoint mPoint) {
		// Responsible for moving the existing logged point to a new provided
		// location.
		// record the new location into DB
	}

	public long startFeatureRecording(int iType) {
		// TODO:: this should not be called when there is an incomplete feature
		// that is previously started.
		long iFeatureId = -1;
		if ((null != mJobInstance)
				&& (mJobInstance.iCurrRecShapeType == GSObjectType.GSO_NONE)) {
			iFeatureId = mJobInstance.startFeatureRecording(iType);
		}
		return iFeatureId;
		// Start a new feature at corresponding layer
		// returns the ID that was retrieved from DB
	}

	public void pauseFeatureRecording(long iFeatureId) {
		// Pauses the current feature recording. Does not block adding of
		// logging points data with Attributes.
		if ((null != mJobInstance)
				&& (mJobInstance.iCurrRecShapeType != GSObjectType.GSO_NONE)) {
			isJobPaused = true;
			mJobInstance.mDataBase.updatePauseTime(mJobInstance.iJobId);
		}
	}

	public void resumeFeatureRecording(long iFeatureId) {
		// Resumes the recording of feature from pause state. Ignore if the
		// state is started or stopped.
		if ((null != mJobInstance)
				&& (mJobInstance.iCurrRecShapeType != GSObjectType.GSO_NONE)) {
			isJobPaused = false;
			mJobInstance.mDataBase.updateResumeTime(mJobInstance.iJobId);
		}
	}

	public boolean stopFeatureRecording(long iFeatureId,boolean isFeatureCorrectionReq) {
		// Completes the feature recording and closes / purges the feature to
		// the DB
		boolean isFeatureValid = true;
		if (null != mJobInstance) {
			isJobPaused = false;
			mJobInstance.stopFeatureRecording(true,isFeatureCorrectionReq);
			JobTransaction jobTxn = mJobInstance.mDataBase
					.getFeatureInfoFromTxn(mJobInstance.iJobId, iFeatureId);
			if (null == jobTxn) {
				isFeatureValid = false;
			}
		}
		return isFeatureValid;
	}

	public void addFGPPointToFeature(long iFeatureId, FGPPoint mPoint, boolean isFixDataFiltered) {
		if (null != mJobInstance) {
			if ((false == isJobPaused)
					&& (mJobInstance.iCurrRecShapeType != GSObjectType.GSO_NONE)) {
				mJobInstance.addFGPPointToFeature(mPoint,
						mJobInstance.iCurrRecShapeType, isFixDataFiltered);
			}
		}

	}

	public void setJobRelatedValues(JobListener mListener, boolean isAutoClose,
			int iAutoCloseDist) {
		if (null != mJobInstance) {
			mJobInstance.setJobRelatedValues(mListener, isAutoClose,
					iAutoCloseDist);
		}
	}

	public void suppressAutoCloseCheck() {
		if (null != mJobInstance) {
			mJobInstance.suppressAutoCloseCheck();
		}
	}
	
	public void updateFieldBoundaries(long iFieldId) {
		if (null != mJobInstance) {
			mJobInstance.updateFieldBoundaries(iFieldId);
		}
	}
	public boolean isTaskValid() {
		boolean isTaskValid = false;
		if (null != mJobInstance) {
			isTaskValid = mJobInstance.isTaskValid();
		}
		return isTaskValid;
	}
	
	public boolean isFeatureValid() {
		boolean isFeatureValid = false;
		if (null != mJobInstance) {
			isFeatureValid = mJobInstance.isFeatureValid();
		}
		return isFeatureValid;
	}
	
	public boolean isFeatureCorrectionReq() {
           boolean isFeatureCorrectionReq = false;
           if (null != mJobInstance) {
              isFeatureCorrectionReq = mJobInstance.isFeatureCorrectionReq();
           }
           return isFeatureCorrectionReq;
   }
	
	public BoundingBox calculateJobBBox(){
		BoundingBox bBox = null;
		if (null != mJobInstance) {
			bBox = mJobInstance.calculateBBoxForJob();
		}
		
		return bBox;
	}

}

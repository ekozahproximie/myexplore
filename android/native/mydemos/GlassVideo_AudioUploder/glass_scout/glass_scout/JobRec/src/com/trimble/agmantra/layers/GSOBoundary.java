package com.trimble.agmantra.layers;

import com.trimble.agmantra.job.Job;

// TODO: Auto-generated Javadoc
/**
 * The Class GSOBoundary.
 */
public class GSOBoundary extends GSObject{
	/**
	 * Construct for the class.
	 * 
	 * @param iJobId
	 *            Parameter stating the Job Id on which the com.trimble.agmantra.job is getting
	 *            recorded
	 * @param iFeatureId
	 *            Parameter stating the feature id of the object created
	 * @param iAttributeId 
	 * @param iPassId 
	 */
	public GSOBoundary(long iJobId, long iFeatureId, int iPassId, int iAttributeId, Job mJobInstance) {
		super(iJobId,iFeatureId,iPassId,iAttributeId,mJobInstance);	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trimble.agmantra.job.GSObject#addPointToObject(com.trimble.agmantra.job.PointInfo)
	 */
	@Override
	public void addPointToObject(FGPPoint mPoint, boolean isFixFiltered) {
		iObjectType = GSObjectType.GSO_BOUNDARY;
		mPoint.iObjectType = iObjectType;
		if (isFixFiltered)
		   super.addPointToObject(mPoint, isFixFiltered);		
		if((false == isObjectLoaded) && (true == isCheckForAutoClose))
		{
			checkForAutoClose(mPoint, isFixFiltered);
		}

	}

}

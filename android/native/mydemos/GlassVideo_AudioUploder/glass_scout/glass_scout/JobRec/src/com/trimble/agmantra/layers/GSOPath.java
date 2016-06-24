package com.trimble.agmantra.layers;


import com.trimble.agmantra.job.Job;
import com.trimble.agmantra.layers.GSObjectType;

/**
 * The Class GSOPath.
 */
public class GSOPath extends GSObject {

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
	public GSOPath(long iJobId, long iFeatureId, int iPassId, int iAttributeId,Job mJobInstance) {
		super(iJobId,iFeatureId,iPassId,iAttributeId,mJobInstance);		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.trimble.agmantra.job.GSObject#addPointToObject(com.trimble.agmantra.job.PointInfo)
	 */
	@Override
	protected void addPointToObject(FGPPoint mPoint, boolean bisFixFiltered) {
		iObjectType = GSObjectType.GSO_POLYLINE;
		mPoint.iObjectType = iObjectType;
		super.addPointToObject(mPoint, bisFixFiltered);	
	}

}

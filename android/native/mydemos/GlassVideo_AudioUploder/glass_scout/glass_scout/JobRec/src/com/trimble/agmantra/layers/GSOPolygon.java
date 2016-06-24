package com.trimble.agmantra.layers;

import com.trimble.agmantra.job.Job;

// TODO: Auto-generated Javadoc
/**
 * The Class GSOPolygon.
 */
public class GSOPolygon extends GSObject {

	/**
	 * Construct for the class.
	 * 
	 * @param iJobId
	 *            Parameter stating the Job Id on which the
	 *            com.trimble.agmantra.job is getting recorded
	 * @param iFeatureId
	 *            Parameter stating the feature id of the object created
	 * @param iAttributeId
	 * @param iPassId
	 */
	public GSOPolygon(long iJobId, long iFeatureId, int iPassId,
			int iAttributeId, Job mJobInstance) {
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
	protected void addPointToObject(FGPPoint mPoint, boolean isFixFiltered) {
		mPoint.iBooms = -1;
		mPoint.iObjectType = GSObjectType.GSO_BOUNDARY;
		iObjectType = GSObjectType.GSO_POLYGON;
		// Add the point only if the point is a filtered point.
		if (isFixFiltered)
		   super.addPointToObject(mPoint, isFixFiltered);
		if ((false == isObjectLoaded) && (true == isCheckForAutoClose)) {
			checkForAutoClose(mPoint, isFixFiltered);
		}

	}
}

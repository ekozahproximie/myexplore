package com.trimble.agmantra.layers;

/**
 * The Class PolygonLayer.
 */
public class PolygonLayer extends GSObjectLayer {

	/**
	 * Construct for the class.
	 * 
	 * @param iAutoCloseDistance
	 * @param isAutoCloseEnabled
	 * 
	 * @param iJobId
	 *            Parameter stating the Job Id on which the
	 *            com.trimble.agmantra.job is getting recorded
	 */
	public PolygonLayer(boolean isAutoCloseEnabled, int iAutoCloseDistance) {
		super();
		this.isAutoCloseEnabled = isAutoCloseEnabled;
		this.iAutoCloseDistance = iAutoCloseDistance;

	}

	@Override
	public void addObjectToList(boolean isLoaded, GSObject mObject) {
		mObject.isCheckForAutoClose = isAutoCloseEnabled;
		mObject.iAutoCloseDistance = iAutoCloseDistance;
		super.addObjectToList(isLoaded, mObject);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.trimble.agmantra.job.GSObjectLayer#updateStatusOfCurrObject(boolean)
	 */
	@Override
	public void setStatusOfRecordingObject(boolean isFinished, boolean isCorrectionRequired) {
		GSOPolygon mCurrGSOPolygon = (GSOPolygon) mcurrGSObject;
      if ((true == isFinished) && (null!=mCurrGSOPolygon) &&(false == mCurrGSOPolygon.isObjectLoaded)) {
		         mCurrGSOPolygon.closeBoundary();
		 }

//		if (true == isFinished) {
//			mCurrGSOPolygon.closeBoundary();
//		}
		super.setStatusOfRecordingObject(isFinished,isCorrectionRequired);
	}

	@Override
   public void addPointToCurrObject(FGPPoint mPoint, boolean isFixFiltered) {
      if (null != mcurrGSObject) {
         GSOPolygon mCurrGSOPolygon = (GSOPolygon) mcurrGSObject;
         mCurrGSOPolygon.addPointToObject(mPoint, isFixFiltered);
            }
         }

   
}
package com.trimble.agmantra.layers;

// TODO: Auto-generated Javadoc
/**
 * The Class BoundaryLayer.
 */
public class BoundaryLayer extends GSObjectLayer {

	/**
	 * Construct for the class.
	 * 
	 * @param iAutoCloseDistance2
	 * @param isAutoCloseEnabled2
	 * 
	 * @param iJobId
	 *            Parameter stating the Job Id on which the
	 *            com.trimble.agmantra.job is getting recorded
	 */
	public BoundaryLayer(boolean isAutoCloseEnabled, int iAutoCloseDistance) {
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
      GSOBoundary mCurrGSOBoundary = (GSOBoundary) mcurrGSObject;
      if ((true == isFinished) && (null != mCurrGSOBoundary)
            && (false == mCurrGSOBoundary.isObjectLoaded)) {
         mCurrGSOBoundary.closeBoundary();
      }
      super.setStatusOfRecordingObject(isFinished,isCorrectionRequired);
	}

   @Override
   public void addPointToCurrObject(FGPPoint mPoint, boolean isFixFiltered) {
      if (null != mcurrGSObject) {
         GSOBoundary mCurrGSOBoundary = (GSOBoundary) mcurrGSObject;
         mCurrGSOBoundary.addPointToObject(mPoint, isFixFiltered);
      }
   }
}
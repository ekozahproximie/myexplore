package com.trimble.agmantra.layers;

// TODO: Auto-generated Javadoc
/**
 * The Class PathLayer.
 */
public class PathLayer extends GSObjectLayer {

	/**
	 * Construct for the class.
	 * 
	 * @param iJobId
	 *            Parameter stating the Job Id on which the
	 *            com.trimble.agmantra.job is getting recorded
	 */
	public PathLayer() {
		super();
	}

	@Override
	public void addPointToCurrObject(FGPPoint mPoint, boolean bisFixFiltered) {
		if (null != mcurrGSObject) {
			GSOPath mCurrGSOPath = (GSOPath) mcurrGSObject;
			mCurrGSOPath.addPointToObject(mPoint, bisFixFiltered);
            }
         }

   /*
    * (non-Javadoc)
    * 
    * @see
    * com.trimble.agmantra.job.GSObjectLayer#updateStatusOfCurrObject(boolean)
    */
   @Override
   public void setStatusOfRecordingObject(boolean isFinished,boolean isCorrectionRequired) {
      GSOPath mCurrGSOPath = (GSOPath) mcurrGSObject;
      if ((true == isFinished) && mCurrGSOPath != null
            && (false == mCurrGSOPath.isObjectLoaded)) {
		 mCurrGSOPath.closePath();
		 }

//		if (true == isFinished) {
//			if (null != mCurrGSOPath) {
//				mCurrGSOPath.closePath();
//			}
//		}
		super.setStatusOfRecordingObject(isFinished,isCorrectionRequired);
	}

}

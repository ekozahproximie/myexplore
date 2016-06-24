package com.trimble.agmantra.job;

import java.util.Vector;

import com.trimble.agmantra.layers.GSObjectLayer;
import com.trimble.agmantra.layers.GeoPoint;


public interface JobListener {
	//TODO:: eObjectType  is of type GSObjectType
	public void sendMapUpdate(long iFeatureId, int eObjectType,GeoPoint mPoint, boolean isFinished,boolean isOffsetPresent);
	public void autoCloseFeature();

	public void featureIncompletePreviously(long id);
	public void initAllLayers(Vector<GSObjectLayer> vecOverlayLayers);
	public void removeFeature(long iFeatureId, int eObjectType);
}

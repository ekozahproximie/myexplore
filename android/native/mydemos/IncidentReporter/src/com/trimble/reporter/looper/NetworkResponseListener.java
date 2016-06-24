package com.trimble.reporter.looper;

public interface NetworkResponseListener {

	public void onConnectionSucess(Object objData, int iDataType);
	public void onConnectionFail(Object objData, int iDataType);

}

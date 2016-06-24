package com.trimble.ag.acdc;

import android.util.Log;

import org.json.JSONException;

abstract public class ACDCResponse {
	
	private static final String TAG = ACDCResponse.class.getSimpleName();
	
	protected String stResultCode = null;
	
	protected boolean isKeyExpired = false;
	
	protected String stMessage=null; 
	
	protected String stModelState=null;
	
	public boolean isKeyExpire(String stLine){
	
		try {
			MyJSONObject serverResponse = new MyJSONObject(stLine);
			
			stResultCode = serverResponse.getString(ACDCApi.C_RESULT_CODE);
			if(stResultCode != null){
			   isKeyExpired=stResultCode.equals(ACDCApi.KEY_EXPIRE);
			}
			stMessage= serverResponse.getString(ACDCApi.C_MESSAGE);
			
			stModelState=serverResponse.getString(ACDCApi.C_MODELSTATE);
			
			if(!isKeyExpired){
				Log.i(TAG, "ACDCResponse failed becauseof Key expire");
			
			}
		}catch(JSONException e){
			Log.e(TAG, "paser error", e);
		}
		return isKeyExpired;
	}
	
	public boolean isKeyExpired() {
		return isKeyExpired;
	}
}

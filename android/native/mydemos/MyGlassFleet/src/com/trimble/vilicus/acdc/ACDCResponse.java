package com.trimble.vilicus.acdc;

import android.util.Log;

import org.json.JSONException;

abstract public class ACDCResponse {
	
	private static final String TAG = ACDCResponse.class.getSimpleName();
	
	protected String stResultCode = null;
	
	protected boolean isKeyExpired = false;
	
	public boolean isKeyExpire(String stLine){
	
		try {
			MyJSONObject serverResponse = new MyJSONObject(stLine);
			
			stResultCode = serverResponse.getString(ACDCApi.C_RESULT_CODE);
			
			isKeyExpired=stResultCode.equals(ACDCApi.KEY_EXPIRE);
			
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

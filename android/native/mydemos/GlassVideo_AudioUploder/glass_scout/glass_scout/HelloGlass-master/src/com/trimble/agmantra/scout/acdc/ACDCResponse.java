package com.trimble.agmantra.scout.acdc;

import android.util.Log;

import org.json.JSONException;

abstract public class ACDCResponse {
	
	private static final String TAG = ACDCResponse.class.getSimpleName();
	
	protected String stResultCode = null;
	
	protected boolean isKeyExpired = false;
	
	public boolean isKeyExpire(String stLine){
	
		try {
			MyJSONObject serverResponse = new MyJSONObject(stLine);
			
			stResultCode = serverResponse.getString(ScoutACDCApi.C_RESULT_CODE);
			
			isKeyExpired=stResultCode.equals(ScoutACDCApi.KEY_EXPIRE);
			
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

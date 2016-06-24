package com.trimble.ag.nabu.acdc.res;

import android.util.Log;

import com.trimble.ag.acdc.ACDCApi;
import com.trimble.ag.acdc.ACDCResponse;
import com.trimble.ag.acdc.MyJSONObject;

import org.json.JSONException;

public class LoginResponse extends ACDCResponse {

	private static final String C_TICKET = "access_token";
	
	public static final String SUCCESS = "Success";

	
	

	// private static final String ERRORCODE = "ErrorCode";
	// private static final String MEANING = "Meaning";

	public boolean isSuccess = false;
	public String ticket = null;
	// public String stErrorCode = null;
	public String stMeaning = "";
	public boolean isAuthenticationFailed = false;
	

	private static final String TAG = LoginResponse.class.getSimpleName();

	public void readResponse(String stLine) {
		try {
			MyJSONObject regisObject = new MyJSONObject(stLine);
			
			final String stResultCode=regisObject.getString(ACDCApi.C_RESULT_CODE);
			isSuccess = stResultCode.equals(
					SUCCESS);

			if (isSuccess) {

				ticket = regisObject.getString(C_TICKET);

				
			}

			// if (!isSuccess) {
			// stErrorCode = regisObject.getString(ERRORCODE);
			// stMeaning = regisObject.getString(MEANING);
			// }

		} catch (JSONException e) {

			Log.e(TAG, e.getMessage(), e);
		}
	}

	public void readOrgChangeTicket(final String stLine) {
		try {
			MyJSONObject regisObject = new MyJSONObject(stLine);
			
			final String stResultCode= regisObject.getString(ACDCApi.C_RESULT_CODE);
			if(stResultCode != null){
			isSuccess =stResultCode.equals(
					SUCCESS);
			}

			if (isSuccess) {

				ticket = regisObject.getString(C_TICKET);

			}

		} catch (JSONException e) {

			Log.e(TAG, e.getMessage(), e);
		}
	}

	private static final String LOGIN_FAILED = "Generating token failed with reason: AuthenticationFailed";

	public boolean isAuthenticationFailed(String stLine) {

		try {
			MyJSONObject regisObject = new MyJSONObject(stLine);

			stMeaning = regisObject.getString(ACDCApi.C_RESULT_CODE);
			if(stMeaning == null || stMeaning.trim().length() == 0){
			   stMeaning  = regisObject.getString(ACDCApi.C_MESSAGE);
			}
			if(stMeaning != null){
			   isAuthenticationFailed = stMeaning.equals(LOGIN_FAILED);
			}

		} catch (JSONException e) {

			Log.e(TAG, e.getMessage(), e);
		}

		return isAuthenticationFailed;
	}

	@Override
	public String toString() {

		return isSuccess + "," + ticket; // + "," + stErrorCode + "," +
											// stMeaning;
	}

	
	
	
}

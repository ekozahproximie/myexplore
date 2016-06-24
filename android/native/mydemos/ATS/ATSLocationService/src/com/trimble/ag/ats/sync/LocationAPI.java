/**
 * Copyright Trimble Inc., 2015 - 2016 All rights reserved.
 * 
 * Licensed Software Confidential and Proprietary Information of Spime Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 * 
 * Product Name: ConnectedFarm
 * 
 * Module Name: com.trimble.agmantra.scout.acdc.feature.req
 * 
 * File name: FeatureAPI.java
 * 
 * Author: kmuruga
 * 
 * Created On: May 18, 20158:52:01 AM
 * 
 * Abstract:
 * 
 * Environment: Android
 * 
 * Mobile Profile :
 *
 * Mobile Configuration :
 * 
 * Notes:
 * 
 * Revision History:
 * 
 * 
 */
package com.trimble.ag.ats.sync;

import java.io.IOException;
import java.net.SocketException;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;

import com.trimble.ag.ats.acdc.ACDCApi;
import com.trimble.ag.ats.acdc.ACDCRequest;
import com.trimble.ag.ats.acdc.JsonClient;
import com.trimble.ag.ats.acdc.res.ACDCResponse;
import com.trimble.ag.ats.acdc.res.LoginResponse;

/**
 * @author DEEPIKA
 * 
 */
public class LocationAPI {

	private static final String TAG = "LocationAPI";

	private Context context = null;

	private static final String POST_LOCATIONS_DATA = "%s://%s/Locations/v1/LocationData";

	/**
    * 
    */

	public LocationAPI(Context context) {
		this.context = context;
	}

	public boolean sendLocationsData(final String stJsonString,
			final ACDCApi acdcApi) throws SocketException, IOException,
			JSONException {

		if (acdcApi.getTicket() == null) {
			Log.i(TAG, " sendLocationsData Return No ticket found");
			return false;
		}
		String stLocationDataURL = String.format(POST_LOCATIONS_DATA,
				acdcApi.getProtoCal(), acdcApi.getDomainName());
		boolean status = true;
		final int RESPONSE_CODE = 200;
		final JsonClient client = new JsonClient(context);

		Log.i(TAG, "sendLocationsData URL:" + stLocationDataURL);

		ACDCResponse acdcResponse = null;
		Log.i(TAG, "sendLocationsData - JsonString = " + stJsonString);
		final ACDCRequest acdcRequest = new ACDCRequest(stLocationDataURL,
				stJsonString, ACDCRequest.POST, ACDCRequest.CONTENT_TYPE_JSON,
				acdcApi.getTicket(), false);
		acdcResponse = client.connectHttp(acdcRequest);

		try {
			String stData = client
					.convertByteArrayToString(acdcResponse.resData);
			Log.i(TAG, "sendLocationsData  Response code:"
					+ acdcResponse.iResponseCode);
			Log.i(TAG, "sendLocationsData  Response:" + stData);

			if (RESPONSE_CODE != acdcResponse.iResponseCode) {
				status = false;
				Log.i(TAG, "addNewFeature Exception-ErrorCode:"
						+ acdcResponse.iResponseCode);
				if (acdcResponse.iResponseCode == ACDCApi.KEY_EXPIRE_ERROR_CODE) {
					final LoginResponse response = acdcApi.login();
					if (response != null) {
						boolean isLoginSucess = response.isSuccess;
						if (isLoginSucess) {
							return sendLocationsData(stJsonString, acdcApi);
						}
					}
				}
			}
		} finally {

		}

		return status;
	}

}

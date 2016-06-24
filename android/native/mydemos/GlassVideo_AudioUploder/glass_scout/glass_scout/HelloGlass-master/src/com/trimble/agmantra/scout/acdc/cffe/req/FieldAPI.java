package com.trimble.agmantra.scout.acdc.cffe.req;

import android.content.Context;
import android.util.Log;

import com.trimble.agmantra.acdc.ACDCRequest;
import com.trimble.agmantra.acdc.ACDCResponse;
import com.trimble.agmantra.acdc.JsonClient;
import com.trimble.agmantra.login.LoginResponse;
import com.trimble.agmantra.scout.acdc.ScoutACDCApi;
import com.trimble.agmantra.scout.acdc.cffe.res.FieldResponse;

import java.io.IOException;
import java.net.UnknownHostException;

public class FieldAPI {

	private static final String TAG = "ACDC";

	private static final String GET_FIELDS_URL = "%s://%s/FarmResources/v1/Fields";

	public synchronized boolean getFields(final ScoutACDCApi acdcApi, final Context context)
			throws UnknownHostException, IOException {
		if (acdcApi.getTicket() == null) {
			Log.i(TAG, "Get Fields Return No ticket found");
			return false;
		}
		String stFieldsURL = String.format(GET_FIELDS_URL,
				acdcApi.getProtoCal(), acdcApi.getDomainName(),
				acdcApi.getTicket());
		boolean status = true;
		final int RESPONSE_CODE = 200;
		JsonClient client = new JsonClient(context);


		Log.i(TAG, "Get Fields URL:" + stFieldsURL);

		

		final ACDCRequest acdcRequest = new ACDCRequest(stFieldsURL, null,
                      ACDCRequest.GET, ACDCRequest.CONTENTTYPE_ENCODE, acdcApi.getTicket());
		      final ACDCResponse acdcResponse = client.connectHttp(acdcRequest);
		
		try {

			String stData = client.convertByteArrayToString(acdcResponse.resData);
			if (stData != null) {
				Log.i(TAG, "Get Fields  Response code:" + acdcResponse.iResponseCode);
				Log.i(TAG, "Get Fields  Response:" + stData);
				FieldResponse fieldsResponse = new FieldResponse();

				if (RESPONSE_CODE != acdcResponse.iResponseCode) {
					status = false;
					Log.i(TAG, "Get Fields Exception-ErrorCode:"
							+ acdcResponse.iResponseCode);
					if (acdcResponse.iResponseCode== ScoutACDCApi.KEY_EXPIRE_ERROR_CODE
							&& fieldsResponse.isKeyExpire(stData)) {
						final LoginResponse response = acdcApi.login();
						boolean isLoginSucess = response.isSuccess;
						if (isLoginSucess) {
							getFields(acdcApi,context);
						}
					}

				} else {
					fieldsResponse.readFieldsResponse(stData,
							acdcApi.getContext());
				}
				String stResp = fieldsResponse.toString();

				Log.i(TAG, "Get Fields -Responsecode:" + acdcResponse.iResponseCode
						+ ";Response:" + stResp);
			}
		} finally {

		}

		return status;
	}
}

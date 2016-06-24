package com.trimble.agmantra.scout.acdc.cffe.req;

import android.content.Context;
import android.util.Log;

import com.trimble.agmantra.acdc.ACDCRequest;
import com.trimble.agmantra.acdc.ACDCResponse;
import com.trimble.agmantra.acdc.JsonClient;
import com.trimble.agmantra.login.LoginResponse;
import com.trimble.agmantra.scout.acdc.ScoutACDCApi;
import com.trimble.agmantra.scout.acdc.cffe.res.FarmResponse;

import java.io.IOException;
import java.net.UnknownHostException;

public class FarmAPI {

	private static final String TAG = "ACDC";
	
	private static final String GET_FARMS_URL    = "%s://%s/FarmResources/v1/Farms";
	
	public synchronized boolean getFarms(final ScoutACDCApi acdcApi, final Context context)
	         throws UnknownHostException, IOException {
		   if(acdcApi.getTicket() == null){
			   Log.i(TAG, "Get Farms Return No ticket found");
			   return false;
		   }
	      String stFarmsURL = String.format(GET_FARMS_URL,acdcApi.getProtoCal(),
	            acdcApi.getDomainName(), acdcApi.getTicket());
	      boolean status = true;
	      final int RESPONSE_CODE = 200;
	      JsonClient client = new JsonClient(context);


	      Log.i(TAG, "Get Farms URL:" + stFarmsURL);

	   
	      final ACDCRequest acdcRequest = new ACDCRequest(stFarmsURL, null,
                    ACDCRequest.GET, ACDCRequest.CONTENTTYPE_ENCODE,acdcApi.getTicket());
	            final ACDCResponse acdcResponse=client.connectHttp(acdcRequest);
	      
	      try {

	         String stData = client.convertByteArrayToString(acdcResponse.resData);
	         if (stData != null) {
	            Log.i(TAG, "Get Farms  Response code:"+acdcResponse.iResponseCode);
	            Log.i(TAG, "Get Farms  Response:"+stData);
	            FarmResponse farmsResponse = new FarmResponse();
	            
	            if (RESPONSE_CODE != acdcResponse.iResponseCode ) {
	            	status = false;
	               Log.i(TAG, "Get Farms Exception-ErrorCode:"+acdcResponse.iResponseCode);
	              if(acdcResponse.iResponseCode== ScoutACDCApi.KEY_EXPIRE_ERROR_CODE
	            		  && farmsResponse.isKeyExpire(stData)){
	            	  final LoginResponse response =acdcApi.login();
	              	  boolean isLoginSucess=response.isSuccess;
	              	  if(isLoginSucess){
	            		  getFarms(acdcApi,context);
	            	  }
	              }

	            } else {
	            	farmsResponse.readFarmsResponse(stData, acdcApi.getContext());
	            }
	            String stResp = farmsResponse.toString();

	            Log.i(TAG, "Get Farms -Responsecode:" + acdcResponse.iResponseCode
	                  + ";Response:" + stResp);
	         }
	      } finally {

	      }

	      return status;
	   }
}

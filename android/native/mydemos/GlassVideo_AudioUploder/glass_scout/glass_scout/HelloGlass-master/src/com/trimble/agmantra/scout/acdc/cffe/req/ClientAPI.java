package com.trimble.agmantra.scout.acdc.cffe.req;

import android.content.Context;
import android.util.Log;

import com.trimble.agmantra.acdc.ACDCRequest;
import com.trimble.agmantra.acdc.ACDCResponse;
import com.trimble.agmantra.acdc.JsonClient;
import com.trimble.agmantra.login.LoginResponse;
import com.trimble.agmantra.scout.acdc.ScoutACDCApi;
import com.trimble.agmantra.scout.acdc.cffe.res.ClientsResponse;

import java.io.IOException;
import java.net.UnknownHostException;

public class ClientAPI {

	private static final String TAG = "ACDC";
	
	private static final String GET_CLIENTS_URL    = "%s://%s/FarmResources/v1/Clients";
	
	public synchronized boolean getClients(final ScoutACDCApi acdcApi, final Context context)
	         throws UnknownHostException, IOException {
		   if(acdcApi.getTicket() == null){
			   Log.i(TAG, "Get Clients Return No ticket found");
			   return false;
		   }
	      String stClientsURL = String.format(GET_CLIENTS_URL,acdcApi.getProtoCal(),
	            acdcApi.getDomainName(), acdcApi.getTicket());
	      boolean status = true;
	      final int RESPONSE_CODE = 200;
	      JsonClient client = new JsonClient(context);

	    
	      Log.i(TAG, "Get Clients URL:" + stClientsURL);

	    
	      
	      final ACDCRequest acdcRequest = new ACDCRequest(stClientsURL, null,
                    ACDCRequest.GET, ACDCRequest.CONTENTTYPE_ENCODE,acdcApi.getTicket());
	            final ACDCResponse acdcResponse=client.connectHttp(acdcRequest);
	      
	      try {

	         String stData = client.convertByteArrayToString(acdcResponse.resData);
	         if (stData != null) {
	            Log.i(TAG, "Get Clients  Response code:"+acdcResponse.iResponseCode);
	            Log.i(TAG, "Get Clients  Response:"+stData);
	            ClientsResponse clientsResponse = new ClientsResponse();
	            
	            if (RESPONSE_CODE != acdcResponse.iResponseCode ) {
	            	status = false;
	               Log.i(TAG, "Get Clients Exception-ErrorCode:"+acdcResponse.iResponseCode);
	              if(acdcResponse.iResponseCode == ScoutACDCApi.KEY_EXPIRE_ERROR_CODE
	            		  && clientsResponse.isKeyExpire(stData)){
	            	  final LoginResponse response =acdcApi.login();
	              	  boolean isLoginSucess=response.isSuccess;
	              	  if(isLoginSucess){
	            		  getClients(acdcApi,context);
	            	  }
	              }

	            } else {
	            	clientsResponse.readClientsResponse(stData, acdcApi.getContext());
	            }
	            String stResp = clientsResponse.toString();

	            Log.i(TAG, "Get Clients -Responsecode:" + acdcResponse.iResponseCode
	                  + ";Response:" + stResp);
	         }
	      } finally {

	      }

	      return status;
	   }
}

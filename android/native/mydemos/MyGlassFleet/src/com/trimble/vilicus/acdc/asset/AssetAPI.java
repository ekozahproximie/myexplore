package com.trimble.vilicus.acdc.asset;

import android.content.Context;
import android.util.Log;

import com.trimble.vilicus.acdc.ACDCApi;
import com.trimble.vilicus.acdc.ACDCRequest;
import com.trimble.vilicus.acdc.JsonClient;
import com.trimble.vilicus.acdc.asset.res.AssetResponse;
import com.trimble.vilicus.acdc.res.ACDCResponse;
import com.trimble.vilicus.acdc.res.LoginResponse;
import com.trimble.vilicus.entity.Asset;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

public class AssetAPI {

   private static final String GET_ASSET_URL    = "%s://%s/ClientApplication/v1/Assets";

   public static final String  ASSET_STATUS_URL = "%s://%s/ClientApplication/v1/AssetStatus";
   
   public static final String ASSET_POSITION_HISTORY="%s://%s/VehicleManagement/v1/AssetLocationHistory?StartUTC=%s" +
   		"&EndUTC=%s&assetID=%s";


   private static final String TAG              = "ACDC";

   private Context context =null;
   
   /**
    * 
    */
   public AssetAPI(Context context) {
      this.context=context;
   }
   
   public synchronized boolean getAssets(final ACDCApi acdcApi)
         throws UnknownHostException, IOException {
	   if(acdcApi.getTicket() == null){
		   Log.i(TAG, "Get Asset Return No ticket found");
		   return false;
	   }
      String stgetAssertURL = String.format(GET_ASSET_URL,acdcApi.getProtoCal(),
            acdcApi.getDomainName(), acdcApi.getTicket());
      boolean status = true;
      final int RESPONSE_CODE = 200;
      JsonClient client = new JsonClient(context);

      Log.i(TAG, "Get Asset URL:" + stgetAssertURL);

      ACDCResponse acdcResponse =null;
      
      
         final ACDCRequest acdcRequest  =new ACDCRequest(stgetAssertURL, null,
               ACDCRequest.GET, ACDCRequest.CONTENT_TYPE_URL_ENCODE,acdcApi.getTicket());
         acdcResponse=client.connectHttp(acdcRequest);
      
      try {
          HashMap<Long,Asset> assetMap=null;
         String stData = client.convertByteArrayToString(acdcResponse.resData);
         if (stData != null) {
            Log.i(TAG, "Get Asset  Response code:"+acdcResponse.iResponseCode);
            Log.i(TAG, "Get Asset  Response:"+stData);
            AssetResponse assertResponse = new AssetResponse();
            
            if (RESPONSE_CODE != acdcResponse.iResponseCode ) {
            	status = false;
               Log.i(TAG, "Get Asset Exception-ErrorCode:"+acdcResponse.iResponseCode);
              if(acdcResponse.iResponseCode == ACDCApi.KEY_EXPIRE_ERROR_CODE
            		  && assertResponse.isKeyExpire(stData)){
            	  final LoginResponse response =acdcApi.login();
            	  if(response != null){
                 	  boolean isLoginSucess=response.isSuccess;
                 	  if(isLoginSucess){
               		  getAssets(acdcApi);
                 	  }
            	  }
              }

            } else {
               assetMap=assertResponse.readAssetResponse(stData, acdcApi.getContext());
                getAssetStatus(acdcApi,assetMap);

            }
            String stResp = assertResponse.toString();

            Log.i(TAG, "Get Asset -Responsecode:" + acdcResponse.iResponseCode
                  + ";Response:" + stResp);
         }
      } finally {

      }

      return status;
   }

   public synchronized boolean getAssetStatus(final ACDCApi acdcApi,final HashMap<Long,Asset> assetMap)
         throws UnknownHostException, IOException {

      boolean status = true;
      final int RESPONSE_CODE = 200;
      JsonClient client = new JsonClient(context);
      String stgetAssertStatusURL = String.format(ASSET_STATUS_URL,acdcApi.getProtoCal(),
            acdcApi.getDomainName(), acdcApi.getTicket());

      Log.i(TAG, "Get Asset status URL:" + stgetAssertStatusURL);
      
      ACDCResponse acdcResponse =null;
   
         final ACDCRequest acdcRequest = new ACDCRequest(stgetAssertStatusURL,
                    null, ACDCRequest.GET, ACDCRequest.CONTENT_TYPE_URL_ENCODE,acdcApi.getTicket());
         acdcResponse = client.connectHttp(acdcRequest);
      
     
      try {

         String stData = client.convertByteArrayToString(acdcResponse.resData);
         if (stData != null) {
            Log.i(TAG, "Get Asset status Response code:"+acdcResponse.iResponseCode);
            Log.i(TAG, "Get Asset status Response:"+stData);
            AssetResponse assertResponse = new AssetResponse();
           
            if (RESPONSE_CODE != acdcResponse.iResponseCode) {
            	status = false;
               Log.i(TAG, "Get Asset status Exception-ErrorCode"+acdcResponse.iResponseCode);
              if(acdcResponse.iResponseCode == ACDCApi.KEY_EXPIRE_ERROR_CODE
            		  && assertResponse.isKeyExpire(stData)){
            	  final LoginResponse response =acdcApi.login();
            	if(response != null){
                 	  boolean isLoginSucess=response.isSuccess;
                 	  if(isLoginSucess){
               		  getAssetStatus(acdcApi,assetMap);
               	  }
            	}
              }

            } else {
            	 assertResponse
                 .readAssetStatusResponse(stData, acdcApi.getContext(),assetMap);
            }
            String stResp = assertResponse.toString();

            Log.i(TAG, "Get Asset status -Responsecode:" + acdcResponse.iResponseCode
                  + ";Response:" + stResp);
         }
      } finally {

      }

      return status;
   }
   
  
}

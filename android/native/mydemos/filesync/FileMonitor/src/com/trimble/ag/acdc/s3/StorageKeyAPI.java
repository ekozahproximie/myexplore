/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 * 
 *
 * Module Name: com.trimble.ag.acdc.s3
 *
 * File name: StorageKeyAPI.java
 *
 * Author: sprabhu
 *
 * Created On: Oct 31, 20144:40:24 PM
 *
 * Abstract:
 *
 *
 * Environment: Mobile Profile : Mobile Configuration :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.ag.acdc.s3;

import android.content.Context;
import android.util.Log;

import com.trimble.ag.acdc.ACDCApi;
import com.trimble.ag.acdc.ACDCRequest;
import com.trimble.ag.acdc.JsonClient;
import com.trimble.ag.nabu.acdc.res.ACDCResponse;
import com.trimble.ag.nabu.acdc.res.LoginResponse;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * @author sprabhu
 *
 */
public class StorageKeyAPI {

   private static final String TAG           = "ACDC";

   private static final String GET_STORAGE_KEY_URL = "%s://%s/storage/v1/storagekey";

   private Context             context       = null;

   /**
  * 
  */
   public StorageKeyAPI(Context context) {
      this.context = context;
   }

   public synchronized StorageKeyResponse getStoragekey(final ACDCApi acdcApi,final StorageKeyRequest keyRequest) throws UnknownHostException, IOException {
      if (acdcApi.getTicket() == null) {
         Log.i(TAG, "Get Storagekey Return No ticket found");
         return null;
      }
      String stStorageKeyURL = String.format(GET_STORAGE_KEY_URL,
            acdcApi.getProtoCal(), acdcApi.getDomainName());
      boolean status = true;
      final int RESPONSE_CODE = 200;
      JsonClient jsonClient = new JsonClient(context);
      stStorageKeyURL=stStorageKeyURL+"?"+keyRequest.getStorageKeyRequest();
      Log.i(TAG, "Get Storagekey URL:" + stStorageKeyURL);

      ACDCResponse acdcResponse = null;

      final ACDCRequest acdcRequest = new ACDCRequest(stStorageKeyURL, null,
            ACDCRequest.GET, ACDCRequest.CONTENT_TYPE_URL_ENCODE,
            acdcApi.getTicket(), false);
      acdcResponse = jsonClient.connectHttp(acdcRequest);
      StorageKeyResponse storageKeyResponse = null;
      try {

         String stData = jsonClient
               .convertByteArrayToString(acdcResponse.resData);
         if (stData != null) {
            Log.i(TAG, "Get Storagekey  Response code:"
                  + acdcResponse.iResponseCode);
            Log.i(TAG, "Get Storagekey  Response:" + stData);
            
            storageKeyResponse=  new StorageKeyResponse(
                  context);
            if (RESPONSE_CODE != acdcResponse.iResponseCode) {
               status = false;
               Log.i(TAG, "Get Storagekey Exception-ErrorCode:"
                     + acdcResponse.iResponseCode);
               
               if (acdcResponse.iResponseCode == ACDCApi.KEY_EXPIRE_ERROR_CODE
                     && storageKeyResponse.isKeyExpire(stData)) {
                  final LoginResponse response = acdcApi.login();
                  if (response != null) {
                     boolean isLoginSucess = response.isSuccess;
                     if (isLoginSucess) {
                        storageKeyResponse=getStoragekey(acdcApi,keyRequest);
                     }
                  }
               }

            } else {
               storageKeyResponse.readStoragekeyResponse(stData);
               String stResp = storageKeyResponse.toString();

               Log.i(TAG, "Get Storagekey -Responsecode:"
                     + acdcResponse.iResponseCode + ";Response:" + stResp);
            }
         }
      } finally {

      }

      return storageKeyResponse;
   }
}

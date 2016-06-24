/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.ag.acdc.file
 *
 * File name:
 *	    FileUploadedNotificationAPI.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Nov 3, 20145:13:28 PM
 *
 * Abstract:
 *
 *
 * Environment:
 *  Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.ag.acdc.file;

import android.content.Context;
import android.util.Log;

import com.trimble.ag.acdc.ACDCApi;
import com.trimble.ag.acdc.ACDCRequest;
import com.trimble.ag.acdc.JsonClient;
import com.trimble.ag.acdc.s3.StorageKeyResponse;
import com.trimble.ag.nabu.acdc.res.ACDCResponse;
import com.trimble.ag.nabu.acdc.res.LoginResponse;

import java.io.IOException;
import java.net.UnknownHostException;


/**
 * @author sprabhu
 *
 */
public class FileUploadedNotificationAPI {

   
   private static final String TAG           = "ACDC";

   private static final String NOTIFICATION_URL = "%s://%s/DataCollection/v1/FileUploaded";

   private Context             context       = null;
   /**
    * 
    */
   public FileUploadedNotificationAPI(final Context context) {
      this.context=context;
   }


   public synchronized FileUploadedNotificationResponse getUploadedNotification(final ACDCApi acdcApi,final FileUploadedNotificationRequest fileUploadedNotificationRequest) throws UnknownHostException, IOException {
      if (acdcApi.getTicket() == null) {
         Log.i(TAG, "Get UploadedNotification Return No ticket found");
         return null;
      }
      String stStorageKeyURL = String.format(NOTIFICATION_URL,
            acdcApi.getProtoCal(), acdcApi.getDomainName());
      boolean status = true;
      final int RESPONSE_CODE = 200;
      JsonClient jsonClient = new JsonClient(context);

      Log.i(TAG, "Get UploadedNotification URL:" + stStorageKeyURL);

      ACDCResponse acdcResponse = null;

      final ACDCRequest acdcRequest = new ACDCRequest(stStorageKeyURL, fileUploadedNotificationRequest.getUploadedNotification(),
            ACDCRequest.POST, ACDCRequest.CONTENT_TYPE_JSON,
            acdcApi.getTicket(), false);
      acdcResponse = jsonClient.connectHttp(acdcRequest);
      FileUploadedNotificationResponse storageKeyResponse = null;
      try {

         String stData = jsonClient
               .convertByteArrayToString(acdcResponse.resData);
         if (stData != null) {
            Log.i(TAG, "Get UploadedNotification  Response code:"
                  + acdcResponse.iResponseCode);
            Log.i(TAG, "Get UploadedNotification  Response:" + stData);
            
            storageKeyResponse=  new FileUploadedNotificationResponse(
                  );
            storageKeyResponse.setResponseCode(acdcResponse.iResponseCode);
            if (RESPONSE_CODE != acdcResponse.iResponseCode) {
               status = false;
               Log.i(TAG, "Get UploadedNotification Exception-ErrorCode:"
                     + acdcResponse.iResponseCode);
               
               if (acdcResponse.iResponseCode == ACDCApi.KEY_EXPIRE_ERROR_CODE
                     && storageKeyResponse.isKeyExpire(stData)) {
                  final LoginResponse response = acdcApi.login();
                  if (response != null) {
                     boolean isLoginSucess = response.isSuccess;
                     if (isLoginSucess) {
                        storageKeyResponse=getUploadedNotification(acdcApi,fileUploadedNotificationRequest);
                     }
                  }
               }

            } else {
             
               String stResp = storageKeyResponse.toString();

               Log.i(TAG, "Get UploadedNotification -Responsecode:"
                     + acdcResponse.iResponseCode + ";Response:" + stResp);
            }
         }
      } finally {

      }

      return storageKeyResponse;
   }
}

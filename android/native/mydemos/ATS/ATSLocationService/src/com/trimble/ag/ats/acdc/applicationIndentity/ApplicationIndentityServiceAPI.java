/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 * 
 *
 * Module Name: com.trimble.ag.nabu.acdc
 *
 * File name: ApplicationIndentityServiceAPI.java
 *
 * Author: sprabhu
 *
 * Created On: 28-Jan-201511:52:30 am
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
package com.trimble.ag.ats.acdc.applicationIndentity;

import android.content.Context;
import android.util.Log;

import com.trimble.ag.ats.acdc.ACDCApi;
import com.trimble.ag.ats.acdc.ACDCRequest;
import com.trimble.ag.ats.acdc.JsonClient;
import com.trimble.ag.ats.acdc.applicationIndentity.req.ApplicationIndentityRequest;
import com.trimble.ag.ats.acdc.applicationIndentity.res.ApplicationIndentityReponse;
import com.trimble.ag.ats.acdc.res.ACDCResponse;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author sprabhu
 *
 */
public class ApplicationIndentityServiceAPI {

  
   private static final String APPLICATION_TOKEN_URL="%s://%s/ClientApplication/v1/ApplicationIdentity";
   
   private Context context = null;
   public ApplicationIndentityServiceAPI(final Context context){
      this.context=context;
   }
   
   public synchronized boolean  getApplicationIndetityToken(final ACDCApi acdcApi
         ,final ApplicationIndentityRequest indentityRequest) throws SocketException, IOException
            {
             
         String stApplicationIndentityURL = String.format(APPLICATION_TOKEN_URL,acdcApi.getProtoCal(),
               acdcApi.getDomainName());
         
         boolean status = true;
         final int RESPONSE_CODE = 201;
         JsonClient client = new JsonClient(context);

         Log.i(ACDCApi.TAG, "Get getApplicationIndetityToken URL:" + stApplicationIndentityURL);

         ACDCResponse  acdcResponse =null;
         
         final ACDCRequest acdcRequest = new ACDCRequest(stApplicationIndentityURL, indentityRequest.getJsonString(),
                  ACDCRequest.POST, ACDCRequest.CONTENT_TYPE_JSON,
                  null,false);
                  
            acdcResponse=client.connectHttp(acdcRequest);
         try {

            String stData = client.convertByteArrayToString(acdcResponse.resData);
            if (stData != null) {
               Log.i(ACDCApi.TAG, "Get getApplicationIndetityToken  Response code:"+acdcResponse.iResponseCode);
               Log.i(ACDCApi.TAG, "Get getApplicationIndetityToken  Response:"+stData);
               final ApplicationIndentityReponse applicationIndentityResponse = new ApplicationIndentityReponse();
               
               if (RESPONSE_CODE != acdcResponse.iResponseCode ) {
                   status = false;
                  Log.i(ACDCApi.TAG, "Get getApplicationIndetityToken Exception-ErrorCode:"+acdcResponse.iResponseCode);

               } else {
                  applicationIndentityResponse.readResponse(stData);
               }
               String stResp = applicationIndentityResponse.toString();
               acdcApi.storeApplicationIndentity(applicationIndentityResponse.getAccessToken());
               Log.i(ACDCApi.TAG, "Get getApplicationIndetityToken -Responsecode:" + acdcResponse.iResponseCode
                     + ";Response:" + stResp);
            }
         } finally {

         }

         return status;
      }
   
}

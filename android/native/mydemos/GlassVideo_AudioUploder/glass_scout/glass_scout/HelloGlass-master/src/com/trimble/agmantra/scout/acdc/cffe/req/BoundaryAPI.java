/**
 * Copyright Trimble Inc., 2013 - 2014 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.vilicus.acdc.boundary
 *
 * File name:
 *	    BoundaryAPI.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     10:45:41 PM
 *
 * Abstract:
 *
 *
 * Environment:
 *	Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.agmantra.scout.acdc.cffe.req;

import android.content.Context;
import android.util.Log;

import com.trimble.agmantra.acdc.ACDCRequest;
import com.trimble.agmantra.acdc.ACDCResponse;
import com.trimble.agmantra.acdc.JsonClient;
import com.trimble.agmantra.login.LoginResponse;
import com.trimble.agmantra.scout.acdc.ScoutACDCApi;
import com.trimble.agmantra.scout.acdc.cffe.res.BoundaryResponse;

import java.io.IOException;
import java.net.UnknownHostException;


/**
 * @author sprabhu
 *
 */
public class BoundaryAPI {

   private static final String TAG = "ACDC";
   
   private Context mContext = null;
   
   private static final String GET_ALL_FIELD_BOUNDARY    = "%s://%s/FarmResources/v1/FieldBoundaries";
   
   private static final String GET_ALL_FIELD_BOUNDARY_BM = "%s://%s/FarmResources/v1/FieldBoundaries?bookmark=%s";
   
   private static final String GET_FIELD_DETAILS = "%s://%s/FarmResources/v1/Fields/%s";
   
   public synchronized boolean getAllFieldBoundary(final ScoutACDCApi acdcApi,final String stBookMark, final Context context)
            throws UnknownHostException, IOException {
              if(acdcApi.getTicket() == null){
                      Log.i(TAG, "Get getAllFieldBoundary Return No ticket found");
                      return false;
              }
              
         String stClientsURL = String.format(GET_ALL_FIELD_BOUNDARY,acdcApi.getProtoCal(),
               acdcApi.getDomainName());
         if(stBookMark != null && stBookMark.length() != 0){
            stClientsURL = String.format(GET_ALL_FIELD_BOUNDARY_BM,acdcApi.getProtoCal(),
                  acdcApi.getDomainName(), stBookMark);
         }
         boolean status = true;
         final int RESPONSE_CODE = 200;
         JsonClient client = new JsonClient(context);


         Log.i(TAG, "Get AllFieldBoundary URL:" + stClientsURL);

         byte[] resData =null;
         
         final ACDCRequest acdcRequest = new ACDCRequest(stClientsURL,null,
                   ACDCRequest.GET, ACDCRequest.CONTENTTYPE_ENCODE,acdcApi.getTicket());
         final ACDCResponse acdcResponse=client.connectHttp(acdcRequest);
         
         try {

            String stData = client.convertByteArrayToString(acdcResponse.resData);
            if (stData != null) {
               Log.i(TAG, "Get AllFieldBoundary  Response code:"+acdcResponse.iResponseCode);
               Log.i(TAG, "Get AllFieldBoundary  Response:"+stData);
               BoundaryResponse boundaryResponse = new BoundaryResponse();
               
               if (RESPONSE_CODE != acdcResponse.iResponseCode) {
                   status = false;
                  Log.i(TAG, "Get AllFieldBoundary Exception-ErrorCode:"+acdcResponse.iResponseCode);
                 if(acdcResponse.iResponseCode== ScoutACDCApi.KEY_EXPIRE_ERROR_CODE
                             && boundaryResponse.isKeyExpire(stData)){
                     final LoginResponse response =acdcApi.login();
                     boolean isLoginSucess=response.isSuccess;
                     if(isLoginSucess){
                        getAllFieldBoundary(acdcApi,stBookMark, context);
                     }
                 }

               } else {
                  boundaryResponse.readAllBoundaryResponse(stData, acdcApi.getContext());
                  final boolean isHasMoreData=boundaryResponse.isHasMoreData();
                  if(isHasMoreData){
                     Log.i(TAG, "Get getAllFieldBoundary is Has More Data ");
                     getAllFieldBoundary(acdcApi,boundaryResponse.getBookMark(),context);
                  }
               }
               String stResp = boundaryResponse.toString();

               Log.i(TAG, "Get AllFieldBoundary -Responsecode:" + acdcResponse.iResponseCode
                     + ";Response:" + stResp);
            }
         } finally {

         }

         return status;
      }
   public synchronized boolean getFieldDetail(final ScoutACDCApi acdcApi,final String stFieldId, final Context context)
         throws UnknownHostException, IOException {
           if(acdcApi.getTicket() == null){
                   Log.i(TAG, "Get getFieldDetail Return No ticket found");
                   return false;
           }
           
      String stClientsURL = String.format(GET_FIELD_DETAILS,acdcApi.getProtoCal(),
            acdcApi.getDomainName(),stFieldId);
     
      boolean status = true;
      final int RESPONSE_CODE = 200;
      JsonClient client = new JsonClient(context);


      Log.i(TAG, "Get FieldDetail URL:" + stClientsURL);

      
      
      final ACDCRequest acdcRequest = new ACDCRequest(stClientsURL, null,
                ACDCRequest.GET, ACDCRequest.CONTENTTYPE_ENCODE,acdcApi.getTicket());
          final ACDCResponse acdcResponse =client.connectHttp(acdcRequest);
      
      try {

         String stData = client.convertByteArrayToString(acdcResponse.resData);
         if (stData != null) {
            Log.i(TAG, "Get FieldDetail  Response code:"+acdcResponse.iResponseCode);
            Log.i(TAG, "Get FieldDetail  Response:"+stData);
            BoundaryResponse boundaryResponse = new BoundaryResponse();
            
            if (RESPONSE_CODE != acdcResponse.iResponseCode) {
                status = false;
               Log.i(TAG, "Get FieldDetail Exception-ErrorCode:"+acdcResponse.iResponseCode);
              if(acdcResponse.iResponseCode== ScoutACDCApi.KEY_EXPIRE_ERROR_CODE
                          && boundaryResponse.isKeyExpire(stData)){
                  final LoginResponse response =acdcApi.login();
                  boolean isLoginSucess=response.isSuccess;
                  if(isLoginSucess){
                     getFieldDetail(acdcApi,stFieldId,context);
                  }
              }

            } else {
               boundaryResponse.readAllBoundaryResponse(stData, acdcApi.getContext());
               
            }
            String stResp = boundaryResponse.toString();

            Log.i(TAG, "Get FieldDetail -Responsecode:" + acdcResponse.iResponseCode
                  + ";Response:" + stResp);
         }
      } finally {

      }

      return status;
   }

}

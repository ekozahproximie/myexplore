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
 *      com.trimble.ag.acdc.s3
 *
 * File name:
 *	    StorageKeyResponse.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Oct 31, 20144:33:53 PM
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
package com.trimble.ag.acdc.s3;



import android.content.Context;
import android.util.Log;

import com.trimble.ag.acdc.ACDCResponse;
import com.trimble.ag.acdc.MyJSONObject;

import org.json.JSONException;


/**
 * @author sprabhu
 *
 */
public class StorageKeyResponse  extends ACDCResponse{

 
   private static final String STORAGEINFO="storageInfo";
   
   private static final String FILEID="fileId";
   
   private static final String STORAGEKEY ="storageKey";
   
   private static final String ENDPOINTNAME="endpointName";
   
   private static final String BUCKETNAME="bucketName";
   
   private static final String TEMPORARYCREDENTIALS="temporaryCredentials";
   
  
   
   
   private static final String LOG="ACDC";
   
   private  String stFileId =null;
   private  String stStorageKey =null;
   private  String stEndpointName =null;
   private  String stBucketName =null;
   private boolean isSuccess=false;
   
   private StorageCredentialsResponse credentialsResponse =null;
   
  
   private Context context =null;
   
   
   /**
    * 
    */
   public StorageKeyResponse(final Context context ) {
      this.context=context;
   }
   
   public boolean readStoragekeyResponse(final String stLine){
       isSuccess=false;
      if(stLine == null || stLine.trim().length() == 0){
         Log.i(LOG, "readStoragekeyResponse input value is null");
         return isSuccess;
      }
      try {
         final MyJSONObject jsonObject = new MyJSONObject(stLine);
         final MyJSONObject storageInfo= jsonObject.getJSONObject(STORAGEINFO);
         if(storageInfo != null){
             stFileId =storageInfo.getString(FILEID);
             stStorageKey=storageInfo.getString(STORAGEKEY);
             stEndpointName=storageInfo.getString(ENDPOINTNAME);
             stBucketName=storageInfo.getString(BUCKETNAME);
             
         }
         final String stTemporaryCredentials = jsonObject.getString(TEMPORARYCREDENTIALS);
         
         credentialsResponse = new StorageCredentialsResponse();
         isSuccess=credentialsResponse.readStorageCredentialsResponse(stTemporaryCredentials);
      } catch (JSONException e) {
         Log.e(LOG, e.getMessage(),e);
         
      }
      return isSuccess;
      
   }
   
   
   /**
    * @return the isSuccess
    */
   public boolean isSuccess() {
      return isSuccess;
   }
   
   /**
    * @return the bucketname
    */
   public  String getBucketname() {
      return stBucketName;
   }
   
   
   /**
    * @return the endpointname
    */
   public  String getEndpointname() {
      return stEndpointName;
   }
   public StorageCredentialsResponse getStorageCredentialsResponse(){
      return credentialsResponse;
   }
   
   
   /**
    * @return the stStorageKey
    */
   public String getStorageKey() {
      return stStorageKey;
   }
   /**
    * @return the stFileId
    */
   public String getFileId() {
      return stFileId;
   }
   public String getAWSAccessKey(){
      
      return credentialsResponse != null ? credentialsResponse.getAWSAccessKey(): null ;
   }
   
   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
    
      return "Bucket:"+stBucketName+",credentialsResponse:"+credentialsResponse;
   }
}

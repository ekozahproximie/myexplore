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
 *	    FileUploadedNotificationRequest.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Nov 3, 201412:20:57 PM
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

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author sprabhu
 *
 */
public class FileUploadedNotificationRequest {

   
   private  String stFileId = null;
   
   private String stFileName = null;
   
   private String stStorageKey =null;
   
   private String stSource = null;
   
   private static final String FILE_ID="fileId";
   
   private static final String FILE_NAME="fileName";
   
   private static final String STORAGE_KEY="storageKey";
   
   private static final String SOURCE="source";
   
   private static final String LOG="ACDC";
   
   /**
    * 
    */
   public FileUploadedNotificationRequest(final String stFileId ,final String stFileName,final String stStorageKey,final String stSource) {
         this.stFileId=stFileId;
         this.stFileName=stFileName;
         this.stStorageKey=stStorageKey;
         this.stSource=stSource;
         
   }
   
   public String getUploadedNotification(){
      final JSONObject jsonObject = new JSONObject();
      try {
         jsonObject.put(FILE_ID, stFileId);
         jsonObject.put(FILE_NAME, stFileName);
         jsonObject.put(STORAGE_KEY, stStorageKey);
         jsonObject.put(SOURCE, stSource);
      } catch (JSONException e) {
        
        Log.e(LOG, e.getMessage(),e);
      }
      
      return jsonObject.toString();
   }

   
   
}

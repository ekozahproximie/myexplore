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
 * File name: StorageCredentialsResponse.java
 *
 * Author: sprabhu
 *
 * Created On: Oct 31, 20145:16:56 PM
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

import android.util.Log;

import com.trimble.ag.acdc.ACDCResponse;
import com.trimble.ag.acdc.MyJSONObject;
import com.trimble.ag.filemonitor.utils.Utils;

import org.json.JSONException;

import java.util.Date;

/**
 * @author sprabhu
 *
 */
public class StorageCredentialsResponse extends ACDCResponse {

   private static final String EXPIRATION        = "expiration";

   private static final String SECRETACCESSKEY   = "secretAccessKey";

   private static final String SESSIONTOKEN      = "sessionToken";

   private static final String ACCESSKEY         = "accessKey";

   private String              stExpiration      = null;
   private String              stSecretAccessKey = null;
   private String              stSessionToken    = null;
   private String              stAccessKey       = null;

   private static final String LOG               = "ACDC";

   /**
    * 
    */
   public StorageCredentialsResponse() {

   }

   public boolean readStorageCredentialsResponse(final String stLine) {
      boolean isSuccess = false;
      if (stLine == null || stLine.trim().length() == 0) {
         Log.i(LOG, "readStorageCredentialsResponse receive empty data");
         return isSuccess;
      }
      try {
         final MyJSONObject jsonObject = new MyJSONObject(stLine);
         stExpiration = jsonObject.getString(EXPIRATION);
         stSecretAccessKey = jsonObject.getString(SECRETACCESSKEY);
         stSessionToken = jsonObject.getString(SESSIONTOKEN);
         stAccessKey = jsonObject.getString(ACCESSKEY);
         final Date expireDate=Utils.readServerDateInUTC(stExpiration);
         isSuccess=true;
      } catch (JSONException e) {
         Log.e(LOG, e.getMessage(), e);
      }
      return isSuccess;
   }
   
   /**
    * @return the stExpiration
    */
   public String getExpiration() {
      return stExpiration;
   }
   
   /**
    * @return the stAccessKey
    */
   public String getAWSAccessKey() {
      return stAccessKey;
   }
   
   
   /**
    * @return the stSecretAccessKey
    */
   public String getAWSSecretAccessKey() {
      return stSecretAccessKey;
   }
   
   
   /**
    * @return the stSessionToken
    */
   public String getAWSSessionToken() {
      return stSessionToken;
   }

   
   @Override
   public String toString() {
      
      return "AccessKey:"+stAccessKey+",SecretAccessKey:"+stSecretAccessKey+",SessionToken:"+stSessionToken ;
   }
}

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
 *      com.trimble.ag.nabu.acdc
 *
 * File name:
 *	    ApplicationIndentityReponse.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     28-Jan-20152:11:45 pm
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
package com.trimble.ag.ats.acdc.applicationIndentity.res;

import android.util.Log;

import com.trimble.ag.ats.acdc.ACDCApi;
import com.trimble.ag.ats.acdc.ACDCResponse;
import com.trimble.ag.ats.acdc.MyJSONObject;

import org.json.JSONException;


/**
 * @author sprabhu
 *
 */
public class ApplicationIndentityReponse extends ACDCResponse {

   private static final String ACCESS_TOKEN="access_token";
   
   //private static final String BRANDNAME ="brandName";
   
   private transient String stAccess_Token= null;
   /**
    * 
    */
   public ApplicationIndentityReponse() {
    
   }
 
   public void readResponse(final String stData){
      try {
         final MyJSONObject jsonObject = new MyJSONObject(stData);
         stAccess_Token= jsonObject.getString(ACCESS_TOKEN);
        // final String stBrandName= jsonObject.getString(BRANDNAME);
      } catch (JSONException e) {
         Log.e(ACDCApi.TAG, e.getMessage(),e);
      }
      
      
   }
   
   public String getAccessToken(){
      return stAccess_Token;
   }

}

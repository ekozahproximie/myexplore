package com.trimble.agmantra.acdc.request;

import com.trimble.agmantra.acdc.ACDCApi;

import org.json.JSONException;
import org.json.JSONObject;

import com.trimble.agmantra.dbutil.Log;


public class DeviceAssnReq {
   
 
   private static final String ORGID="OrgID";
   
   
  
   public String stOrgID=null;
   
   public String getJsonString(){
      StringBuffer buffer = new StringBuffer();
      JSONObject jsonObject = new JSONObject();
      try{
         if(stOrgID == null){
            stOrgID="";
         }
         jsonObject.put(ORGID, stOrgID);
         buffer.append(jsonObject.toString());
         Log.i(ACDCApi.TAG, buffer.toString());
   } catch (JSONException e) {
      
      e.printStackTrace();
   }
   return buffer.toString();
   
}
   }



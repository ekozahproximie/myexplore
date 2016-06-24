package com.trimble.agmantra.acdc.request;

import com.trimble.agmantra.acdc.ACDCApi;

import org.json.JSONException;
import org.json.JSONObject;

import com.trimble.agmantra.dbutil.Log;


public class LogCrashReportReq {
   
   
   private static final String REPORT="Report";
   
   
   public String stReport=null;
   
   public String getJsonString(){
      StringBuffer buffer = new StringBuffer();
      JSONObject jsonObject = new JSONObject();
      try{
         
         if(stReport == null){
            stReport="";
         }
         jsonObject.put(REPORT, stReport);
         buffer.append(jsonObject.toString());
         Log.i(ACDCApi.TAG, buffer.toString());
   } catch (JSONException e) {
      
      e.printStackTrace();
   }
   return buffer.toString();
   
}
   
}

package com.trimble.agmantra.acdc.request;

import com.trimble.agmantra.acdc.ACDCApi;

import org.json.JSONException;
import org.json.JSONObject;

import com.trimble.agmantra.dbutil.Log;


public class EmailDeviceAssnReq {
   
   private static final String TICKET="Ticket";
   private static final String EMAIL="Email";
   
   
   public String stTicket=null;
   public String stEmail=null;
   
   public String getJsonString(){
      StringBuffer buffer = new StringBuffer();
      JSONObject jsonObject = new JSONObject();
      try{
         if(stTicket == null){
            stTicket="";
         }
         jsonObject.put(TICKET, stTicket);
         if(stEmail == null){
            stEmail="";
         }
         jsonObject.put(EMAIL, stEmail);
         buffer.append(jsonObject.toString());
         Log.i(ACDCApi.TAG, buffer.toString());
   } catch (JSONException e) {
      
      e.printStackTrace();
   }
   return buffer.toString();
   
}
}

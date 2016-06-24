package com.trimble.agmantra.acdc.request;

import com.trimble.agmantra.acdc.ACDCApi;

import org.json.JSONException;
import org.json.JSONObject;

import com.trimble.agmantra.dbutil.Log;


public class PendingDeviceFileReq {
   
   private static final String TICKET="Ticket";
   private static final String TIMESTAMPUTC="TimeStampUTC";
   
   
   public String stTicket=null;
   public String stTimestamp_UTC=null; 
   
   
   public String getJsonString(){
      StringBuffer buffer = new StringBuffer();
      JSONObject jsonObject = new JSONObject();
      try{
         if(stTicket == null){
            stTicket="";
         }
         jsonObject.put(TICKET, stTicket);
         if(stTimestamp_UTC == null){
            stTimestamp_UTC="";
         }
         jsonObject.put(TIMESTAMPUTC, stTimestamp_UTC);
         buffer.append(jsonObject.toString());
         Log.i(ACDCApi.TAG, buffer.toString());
      
   } catch (JSONException e) {
      
      e.printStackTrace();
   }
   return buffer.toString();
   
}

}

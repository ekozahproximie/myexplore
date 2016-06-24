package com.trimble.agmantra.acdc.request;

import com.trimble.agmantra.acdc.ACDCApi;

import org.json.JSONException;
import org.json.JSONObject;
import com.trimble.agmantra.dbutil.Log;


public class UploadDeviceDataFileReq {
   private static final  String TICKET="Ticket";
   private static final  String FILENAME="Filename";
   private static final  String RAWBINARYDATA="RawBinaryData";
   private static final  String BYTECOUNT="ByteCount";
   
   
   public String stTicket=null;
   public String stFileName=null;
   public String stRawBinaryData=null;
   public String stByteCount=null;
   
   
   public String getJsonString(){
      StringBuffer buffer = new StringBuffer();
      JSONObject jsonObject = new JSONObject();
      try{
         if(stTicket == null){
            stTicket="";
         }
         jsonObject.put(TICKET, stTicket);
         if(stFileName == null){
            stFileName="";
         }
         jsonObject.put(FILENAME, stFileName);
         if(stRawBinaryData == null){
            stRawBinaryData="";
         }
         jsonObject.put(RAWBINARYDATA, stRawBinaryData);
         if(stByteCount == null){
            stByteCount="";
         }
         jsonObject.put(BYTECOUNT, stByteCount);
         buffer.append(jsonObject.toString());
         Log.i(ACDCApi.TAG, buffer.toString());
   } catch (JSONException e) {
      
      e.printStackTrace();
   }
   return buffer.toString();
   
}

}

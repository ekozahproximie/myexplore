package com.trimble.agmantra.acdc.response;

import org.json.JSONException;
import org.json.JSONObject;


public class GetDeviceFileRes {
   
  
   private static final  String FILENAME="Filename";
   private static final  String RAWBINARYDATA="RawBinaryData";
   private static final  String BYTECOUNT="ByteCount";
   
   
   public String stTicket=null;
   public String stFileName=null;
   public byte[] stRawBinaryData=null;
   public String stByteCount=null;
   
   
   public void readResponse(String stLine){
      try {
       JSONObject regisObject= new JSONObject(stLine);
       stFileName=regisObject.getString(FILENAME);
       
      
       
   } catch (JSONException e) {
       
       e.printStackTrace();
   }
  }
 

   
      
}

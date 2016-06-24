package com.trimble.agmantra.acdc.response;

import org.json.JSONException;
import org.json.JSONObject;


public class DeviceAssnRes {
   
   private static final String SUCCESS="Success";
   private static final String ERRORCODE="ErrorCode";
   private static final String MEANING="Meaning";
   
   
   public boolean isSuccess=false;
   public String stErrorCode=null;
   public String stMeaning=null;
   
   
   public void readResponse(String stLine){
      try {
         if(stLine == null || stLine.length() == 0){
            return;
         }
       JSONObject responseObject= new JSONObject(stLine); 
       
       isSuccess= responseObject.getBoolean(SUCCESS);
    
          
       if(!isSuccess ){
       stErrorCode= responseObject.getString(ERRORCODE);
       stMeaning= responseObject.getString(MEANING);
       }
       
   } catch (JSONException e) {
       
       e.printStackTrace();
   }
  }
  /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      
       return isSuccess+","+stErrorCode+","+stMeaning;
   }
   

}

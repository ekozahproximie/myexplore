package com.trimble.agmantra.acdc.response;

import org.json.JSONException;
import org.json.JSONObject;


public class UploadDeviceDataFileRes {
   
   private static final String SUCCESS="Success";
   private static final String FILEID="FileID";
   private static final String ERRORCODE="ErrorCode";
   private static final String MEANING="Meaning";
   
   
   public boolean isSuccess=false;
   public String stFileId=null;
   public String stErrorCode=null;
   public String stMeaning=null;
   
   
   
   
   public void readResponse(String stLine){
      try {
         if(stLine == null || stLine.length() == 0){
            return;
         }
       JSONObject regisObject= new JSONObject(stLine);
       
       isSuccess= regisObject.getBoolean(SUCCESS);
       if(isSuccess)
           stFileId= regisObject.getString(FILEID);
          
       if( !isSuccess ){
       stErrorCode= regisObject.getString(ERRORCODE);
       stMeaning= regisObject.getString(MEANING);
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
      
       return isSuccess+","+stFileId+","+stErrorCode+","+stMeaning;
   }

}

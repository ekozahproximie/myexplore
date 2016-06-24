package com.trimble.agmantra.acdc.response;

import org.json.JSONException;
import org.json.JSONObject;


public class PendingDeviceFileRes {
   
   private static final String SUCCESS="Success";
   private static final String FILELIST="FileList";
   private static final String FILENAME="FileName";
   private static final String FILEID="FileID";
   private static final String FILESIZE="FileSize";
   private static final String ERRORCODE="ErrorCode";
   private static final String MEANING="Meaning";
   
   
   public boolean isSuccess=false;
   public String stFileList=null;
   public String stFileName=null;
   public String stFileId=null;
   public String stFileSize=null;
   public String stErrorCode=null;
   public String stMeaning=null;
   

   
   
   public void readResponse(String stLine){
      try {
         if(stLine == null || stLine.length() == 0){
            return;
         }
       JSONObject regisObject= new JSONObject(stLine);
       
       isSuccess= regisObject.getBoolean(SUCCESS);
       if(isSuccess){
          stFileList=regisObject.getString(FILELIST);
          stFileName=regisObject.getString(FILENAME);
           stFileId= regisObject.getString(FILEID);
           stFileSize=regisObject.getString(FILESIZE);
       } 
       else{
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
      
       return isSuccess+","+stFileList+","+stFileName+","+stFileId+","+stFileSize+","+stErrorCode+","+stMeaning;
   }

   
}

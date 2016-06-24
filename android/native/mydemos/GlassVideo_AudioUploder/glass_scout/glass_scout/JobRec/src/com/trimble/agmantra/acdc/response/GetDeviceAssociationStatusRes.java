package com.trimble.agmantra.acdc.response;

import org.json.JSONException;
import org.json.JSONObject;


public class GetDeviceAssociationStatusRes {
   
   private static final String SUCCESS="Success";
   private static final String EMAIL="Email";
   private static final String ORGID="OrgID";
   private static final String ORGNAME="OrgName";
   private static final String ERRORCODE="ErrorCode";
   private static final String MEANING="Meaning";
   
   
   public boolean isSuccess=false;
   public String stEmail=null;
   public String stOrgId=null;
   public String stOrgName=null;
   
   
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
                try {
                    stEmail = regisObject.getString(EMAIL);
                } catch (JSONException e) {

                    e.printStackTrace();
                }
                try {
                stOrgName=regisObject.getString(ORGNAME);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
          stOrgId=regisObject.getString(ORGID);
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
      
       return isSuccess+","+stEmail+","+stOrgId+","+stOrgName+","+stErrorCode+","+stMeaning;
   }

}

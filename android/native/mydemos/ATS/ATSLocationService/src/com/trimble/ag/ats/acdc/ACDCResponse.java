package com.trimble.ag.ats.acdc;

import android.util.Log;

import org.json.JSONException;

abstract public class ACDCResponse {
	
	private static final String TAG = ACDCResponse.class.getSimpleName();
	
	protected String stResultCode = null;
	
	protected boolean isKeyExpired = false;
	
	protected String stMessage=null; 
	
	protected String stModelState=null;
	
	private static final String DUPLICATE="DuplicateName";
	
	private static final String DUPLICATE_ID="duplicateID";
	
	protected transient long lDuplicateId=-1;  
	
	protected transient String stDuplicateMsg="";
	
	private transient boolean isDuplicate=false;
	
	private boolean isOrgNotFound=false;
	
	
	public boolean isKeyExpire(String stLine){
	   if(stLine == null || stLine.isEmpty() ){
	      return false;
	   }
	
		try {
			MyJSONObject serverResponse = new MyJSONObject(stLine);
			
			stResultCode = serverResponse.getString(ACDCApi.C_RESULT_CODE);
			if(stResultCode != null){
			   isKeyExpired=stResultCode.equals(ACDCApi.KEY_EXPIRE);
			}
			stMessage= serverResponse.getString(ACDCApi.C_MESSAGE);
			
			stModelState=serverResponse.getString(ACDCApi.C_MODELSTATE);
			
			if(!isKeyExpired){
				Log.i(TAG, "ACDCResponse failed becauseof Key expire");
			
			}
		}catch(JSONException e){
			Log.e(TAG, "paser error", e);
		}
		return isKeyExpired;
	}
	
   public boolean isOrgNotFound(final String stLine) {
            if (stLine == null || stLine.isEmpty()) {
               return false;
            }
            try {
               MyJSONObject serverResponse = new MyJSONObject(stLine);
      
               stMessage = serverResponse.getString(ACDCApi.C_MESSAGE);
               if (stMessage != null) {
                  isOrgNotFound = stResultCode.equals(ACDCApi.ORG_ERROR);
               }
      
            } catch (JSONException e) {
               Log.e(TAG, "paser error", e);
            }
            return isOrgNotFound;
   }
   public boolean isDuplicate(final String stLine) {
      try {
         
         final MyJSONObject serverResponse = new MyJSONObject(stLine);
         stMessage = serverResponse.getString(ACDCApi.C_MESSAGE);
         lDuplicateId=serverResponse.getLong(DUPLICATE_ID);
         isDuplicate = lDuplicateId != -1;
         //isDuplicate=  stMessage.equals(stDuplicateMsg);
      } catch (JSONException e) {
         Log.e(TAG, "paser error", e);
      }
      return lDuplicateId != -1 ;
   }
   
 public boolean isDuplicate(){
    return isDuplicate;
 }
	public boolean isKeyExpired() {
		return isKeyExpired;
	}
}

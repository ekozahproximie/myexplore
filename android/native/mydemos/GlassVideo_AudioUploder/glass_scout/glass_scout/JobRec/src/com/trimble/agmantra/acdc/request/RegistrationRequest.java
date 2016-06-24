package com.trimble.agmantra.acdc.request;

import com.trimble.agmantra.acdc.ACDCApi;

import org.json.JSONException;
import org.json.JSONObject;
import com.trimble.agmantra.dbutil.Log;

public class RegistrationRequest {

   private final static String DEVICE_ID             = "DeviceID";
   private final static String ACCESS_KEY            = "AccessKey";
   private final static String DEVICE_SERIAL_NUMBER  = "DeviceSerialNumber";
   private final static String DEVICE_NAME            = "DeviceName";
   private final static String DEVICE_TYPE            = "DeviceType";
   private final static String PRODUCT_NAME           = "ProductName";
   private final static String PRODUCT_TYPE           = "ProductType";
   private final static String SOFTWARE_VERSION       = "SoftwareVersion";
   private final static String SOFTWARE_BUILDNUMBER   = "SoftwareBuildNumber";
   
   private final static String  USERE_MAIL="UserEmail";

   public String               stUserMailID            = null;
   public String               stDeviceID            = null;
   public String               stAccessKey           = null;
   public String               stDeviceSerialNumber  = null;
   public String               stDeviceName          = null;
   public String               stDeviceType          = null;
   public String               stProductName         = null;
   public String               stProductType         = null;
   public String               stSoftwareVersion     = null;
   public String               stSoftwareBuildNumber = null;

   
   public RegistrationRequest() {
    
   }
   
   public String getJsonString(){
      StringBuffer buffer = new StringBuffer();
      JSONObject jsonObject = new JSONObject();
      
      try {
          if(stUserMailID != null){
              jsonObject.put(USERE_MAIL, stUserMailID);
           }
           
          
          
         if(stDeviceID == null){
            stDeviceID="";
         }
         jsonObject.put(DEVICE_ID, stDeviceID);
         if(stAccessKey == null){
            stAccessKey="";
         }
         jsonObject.put(ACCESS_KEY, stAccessKey);
         
         if(stDeviceSerialNumber == null){
            stDeviceSerialNumber="";
         }
         jsonObject.put(DEVICE_SERIAL_NUMBER, stDeviceSerialNumber);
         
         if(stDeviceName == null){
            stDeviceName="";
         }
         jsonObject.put(DEVICE_NAME, stDeviceName);
         
         if(stDeviceType == null){
            stDeviceType="";
         }
         
         jsonObject.put(DEVICE_TYPE, stDeviceType);
         
         if(stProductName == null){
            stProductName="";
         }
         jsonObject.put(PRODUCT_NAME, stProductName);
         
         if(stProductType == null){
            stProductType="";
         }
         jsonObject.put(PRODUCT_TYPE, stProductType);
         
         if(stSoftwareVersion == null){
            stSoftwareVersion="";
         }
         jsonObject.put(SOFTWARE_VERSION, stSoftwareVersion);
         
         if(stSoftwareBuildNumber == null){
            stSoftwareBuildNumber="";
         }
         jsonObject.put(SOFTWARE_BUILDNUMBER, stSoftwareBuildNumber);
         
         buffer.append(jsonObject.toString());
          
         Log.i(ACDCApi.TAG, buffer.toString());
      } catch (JSONException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return buffer.toString();
      
   }
   
}

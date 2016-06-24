/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 * 
 *
 * Module Name: com.trimble.ag.nabu.acdc.applicationIndentity
 *
 * File name: ApplicationIndentityRequest.java
 *
 * Author: sprabhu
 *
 * Created On: 28-Jan-20152:19:34 pm
 *
 * Abstract:
 *
 *
 * Environment: Mobile Profile : Mobile Configuration :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.ag.ats.acdc.applicationIndentity.req;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.trimble.ag.ats.acdc.ACDCApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author sprabhu
 *
 */
public class ApplicationIndentityRequest {

   private final static String     APPLICATIONID         = "applicationID";

   private final static String     APPLICATION_NAME      = "applicationName";
   private final static String     SOFTWARE_VERSION      = "softwareVersion";
   private final static String     SOFTWARE_BUILDNUMBER  = "softwareBuildNumber";
   private final static String     HARDWARE_SN           = "hardwareSerialNumber";
   private final static String     HARDWARE_NAME         = "hardwareName";
   private final static String     HARDWARE_TYPE         = "hardwareType";
   private final static String     HARDWARE_MODEL_NAME   = "hardwareModelName";
   private final static String     HARDWARE_OS_NAME      = "hardwareOSName";
   private final static String     PROPERTIES            = "properties";
   private final static String     KEY                   = "key";
   private final static String     VALUE                 = "value";

   private String                  stDeviceID            = null;
   private String                  stApplicationName     = null;

   private String                  stSoftwareVersion     = null;
   private String                  stSoftwareBuildNumber = null;

   private HashMap<String, Object> propertiesMap         = null;

   public static final String      MOBILE_FIELD          = "MobileField";

   private final static String     ACDC1                 = "ACDC1";

   private final static String     ENCODING_UTF_8        = "UTF-8";

   /**
    * 
    */
   public ApplicationIndentityRequest(final Context context,
         final String stApplicationName, final String stSoftwareVersion,
         final String stSoftwareBuildNumber) {
      this.stDeviceID = getDeviceId(context);
      this.stApplicationName = stApplicationName;
      this.stSoftwareVersion = stSoftwareVersion;
      this.stSoftwareBuildNumber = stSoftwareBuildNumber;
   }

   public ApplicationIndentityRequest(final Context context,
         final String stApplicationName, final String stSoftwareVersion,
         final String stSoftwareBuildNumber,
         final HashMap<String, Object> propertiesMap) {
      this(context, stApplicationName, stSoftwareVersion, stSoftwareBuildNumber);
      this.propertiesMap = propertiesMap;
   }

   private String getDeviceId(final Context context) {
      final TelephonyManager tm = (TelephonyManager) context
            .getSystemService(Context.TELEPHONY_SERVICE);
     final boolean  hasTelephony = context.getPackageManager().hasSystemFeature(
            PackageManager.FEATURE_TELEPHONY);
      final String tmDevice, androidId;
      String stAccesKey = null;

      long lCode = 0;
      if (hasTelephony && tm != null) {
         tmDevice = tm.getDeviceId();
         if(tmDevice != null){
            lCode = ((long) tmDevice.hashCode() << 32) | tm.getPhoneType();
         }
      } else {
         lCode = Build.SERIAL.hashCode();

      }

      androidId = android.provider.Settings.Secure.getString(
            context.getContentResolver(),
            android.provider.Settings.Secure.ANDROID_ID);
      UUID deviceUuid = new UUID(androidId.hashCode(), lCode);

      final String stDeviceId = deviceUuid.toString();

      try {
         if (null != stDeviceId) {

            stAccesKey = ComputeAccessKey(stDeviceId);
         }
         Log.i(ACDCApi.TAG, " DeviceId: " + stDeviceId + "\n AccesKey: "
               + stAccesKey);

      } catch (UnsupportedEncodingException e) {
         Log.e(ACDCApi.TAG, e.toString());
      }
      return stDeviceId;
   }

   private String ComputeAccessKey(String stDeviceId)
         throws UnsupportedEncodingException {
      byte[] prefixBytes = new String(ACDC1).getBytes(ENCODING_UTF_8);

      UUID uuid = UUID.fromString(stDeviceId);

      long msb = uuid.getMostSignificantBits();
      long lsb = uuid.getLeastSignificantBits();
      byte[] buffer = new byte[16];

      for (int i = 0; i < 8; i++) {
         buffer[i] = (byte) (msb >>> 8 * (7 - i));
      }
      for (int i = 8; i < 16; i++) {
         buffer[i] = (byte) (lsb >>> 8 * (7 - i));
      }

      byte[] bytesOriginal = buffer;
      byte[] bytes = new byte[16];

      // Reverse the first 4 bytes
      bytes[0] = bytesOriginal[3];
      bytes[1] = bytesOriginal[2];
      bytes[2] = bytesOriginal[1];
      bytes[3] = bytesOriginal[0];
      // Reverse 6th and 7th
      bytes[4] = bytesOriginal[5];
      bytes[5] = bytesOriginal[4];
      // Reverse 8th and 9th
      bytes[6] = bytesOriginal[7];
      bytes[7] = bytesOriginal[6];
      // Copy the rest straight up
      for (int i = 8; i < 16; i++) {
         bytes[i] = bytesOriginal[i];
      }
      byte[] deviceIDBytes = bytes;

      byte[] combined = new byte[prefixBytes.length + deviceIDBytes.length];
      System.arraycopy(prefixBytes, 0, combined, 0, prefixBytes.length);
      System.arraycopy(deviceIDBytes, 0, combined, prefixBytes.length,
            deviceIDBytes.length);

      MD5 md5 = new MD5(combined);
      byte[] hashed = md5.fingerprint(combined);
      String accessKey = MD5.toBase64(hashed);

      return accessKey;
   }

   public String getJsonString() {

      JSONObject jsonObject = new JSONObject();

      try {

         if (stDeviceID == null) {
            stDeviceID = "";
         }
         jsonObject.put(APPLICATIONID, stDeviceID);

         if (stApplicationName == null) {
            stApplicationName = "";
         }
         jsonObject.put(APPLICATION_NAME, stApplicationName);

         if (stSoftwareVersion == null) {
            stSoftwareVersion = "";
         }
         jsonObject.put(SOFTWARE_VERSION, stSoftwareVersion);

         if (stSoftwareBuildNumber == null) {
            stSoftwareBuildNumber = "";
         }
         jsonObject.put(SOFTWARE_BUILDNUMBER, stSoftwareBuildNumber);

         jsonObject.put(HARDWARE_NAME, android.os.Build.MANUFACTURER);

         jsonObject.put(HARDWARE_SN, android.os.Build.SERIAL);

         jsonObject.put(HARDWARE_TYPE, android.os.Build.PRODUCT);

         jsonObject.put(HARDWARE_NAME, android.os.Build.HARDWARE);

         jsonObject.put(HARDWARE_MODEL_NAME, android.os.Build.MANUFACTURER
               + " " + android.os.Build.MODEL);

         String androidOS = Build.VERSION.RELEASE;
         jsonObject.put(HARDWARE_OS_NAME, "Android " + androidOS);

         if (propertiesMap != null) {
            final JSONArray jsonArray = new JSONArray();
            for (final String key : propertiesMap.keySet()) {
               final JSONObject propertyJSON = new JSONObject();
               final Object object = propertiesMap.get(key);
               if (object != null) {

                  propertyJSON.put(KEY, key);
                  propertyJSON.put(VALUE, object.toString());
                  jsonArray.put(propertyJSON);
               }

            }

            jsonObject.put(PROPERTIES, jsonArray);
         }

         Log.i(ACDCApi.TAG, jsonObject.toString());
      } catch (JSONException e) {

         Log.e(ACDCApi.TAG, e.getMessage(), e);
      }
      return jsonObject.toString();

   }
   public void addAdditionalPropery(final String stKey,final Object object){
         if(propertiesMap != null && object != null){
            propertiesMap.put(stKey, object.toString());
         }
   }
}

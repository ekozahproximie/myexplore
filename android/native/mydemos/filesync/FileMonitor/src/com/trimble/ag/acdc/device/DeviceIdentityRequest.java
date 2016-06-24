/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 * 
 *
 * Module Name: com.trimble.ag.acdc.device
 *
 * File name: DeviceIdentityRequest.java
 *
 * Author: sprabhu
 *
 * Created On: Oct 31, 20143:55:58 PM
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
package com.trimble.ag.acdc.device;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author sprabhu
 *
 */
public class DeviceIdentityRequest {

   /**
    * 
    */
   private static final String TEST_SERIAL_NUM = "5404500571";
   /**
    * 
    */
   private static final String TMX             = "TmX";
   private static final String SERIAL_NUM      = "serialNumber";
   private static final String DEVICE_TYPE     = "deviceType";

   /**
    * 
    */
   public DeviceIdentityRequest() {

   }

   public String getIdentityRequestJson() {
      JSONObject jsonObject = new JSONObject();
      try {
         final String serialNumber = android.os.Build.SERIAL;
         jsonObject.put(SERIAL_NUM, serialNumber);
         //TODO to comment out below line
         jsonObject.put(SERIAL_NUM, TEST_SERIAL_NUM);
         jsonObject.put(DEVICE_TYPE, TMX);
      } catch (JSONException e) {
         e.printStackTrace();
      }
      return jsonObject.toString();
   }
}

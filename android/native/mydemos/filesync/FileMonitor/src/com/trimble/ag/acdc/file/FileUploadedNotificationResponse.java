/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.ag.acdc.file
 *
 * File name:
 *	    FileUploadedNotificationResponse.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Nov 3, 20145:25:19 PM
 *
 * Abstract:
 *
 *
 * Environment:
 *  Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.ag.acdc.file;

import com.trimble.ag.acdc.ACDCResponse;


/**
 * @author sprabhu
 *
 */
public class FileUploadedNotificationResponse  extends ACDCResponse{

   
   private int iResponseCode= -1;
   
   private boolean isSuccess=false;
   /**
    * 
    */
   public FileUploadedNotificationResponse() {
      
   }
   
   
   /**
    * @param iResponseCode the iResponseCode to set
    */
   public void setResponseCode(int iResponseCode) {
      this.iResponseCode = iResponseCode;
   }

   
   /**
    * @return the iResponseCode
    */
   public int getResponseCode() {
      return iResponseCode;
   }
   /**
    * @return the isSuccess
    */
   public boolean isSuccess() {
      return  iResponseCode == 200;
   }
}

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
 *      com.trimble.ag.acdc.s3
 *
 * File name:
 *	    StorageKeyRequest.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Oct 31, 20144:30:37 PM
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
package com.trimble.ag.acdc.s3;


/**
 * @author sprabhu
 *
 */
public class StorageKeyRequest {

 
   /**
    * 
    */
   private static final String AMP = "&";
   /**
    * 
    */
   private static final String EQ = "=";
   private  String stEnvironment = null;
   private  String stSource = null;
   private  String stFileName= null;
   
   private static final String ENVIRONMENT="environment";
   private static final String SOURCE="source";
   private static final String FILENAME="fileName";
   
   
   /**
    * 
    */
   public StorageKeyRequest(final String stEnvironment,final String stSource,final String stFileName) {
      this.stEnvironment=stEnvironment;
      this.stSource=stSource;
      this.stFileName=stFileName;
   }

   
   public String getStorageKeyRequest(){
      final StringBuilder builder = new StringBuilder();
      builder.append(ENVIRONMENT);builder.append(EQ);builder.append(stEnvironment);
      builder.append(AMP);
      builder.append(SOURCE);builder.append(EQ);builder.append(stSource);
      builder.append(AMP);
      builder.append(FILENAME);builder.append(EQ);builder.append(stFileName);
      
      return builder.toString();
   }
}

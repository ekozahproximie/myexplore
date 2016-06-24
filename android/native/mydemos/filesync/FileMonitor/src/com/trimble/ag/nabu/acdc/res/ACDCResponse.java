/**
 * Copyright Trimble Inc., 2013 - 2014 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.agmantra.acdc
 *
 * File name:
 *	    ACDCResponse.java
 *
 * Author:
 *     Karthiga
 *
 * Created On:
 *     Nov 14, 20132:59:01 PM
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
package com.trimble.ag.nabu.acdc.res;

import org.apache.http.Header;

import java.util.Vector;


/**
 * @author kmuruga
 *
 */
public class ACDCResponse {

   public byte[] resData=null;
   
   public int iResponseCode=0;
   
   
   public Vector<Header[]> vecResponseHeader;
   
   /**
    * 
    */
   public ACDCResponse(final byte[] bResponseData,final int iResponseCode) {
      this.resData=bResponseData;
      this.iResponseCode=iResponseCode;
   }
   
   public ACDCResponse(final byte[] bResponseData,final int iResponseCode,final Vector<Header[]> vecResponseHeader) {
      this.resData=bResponseData;
      this.iResponseCode=iResponseCode;
      this.vecResponseHeader=vecResponseHeader;
   }
}

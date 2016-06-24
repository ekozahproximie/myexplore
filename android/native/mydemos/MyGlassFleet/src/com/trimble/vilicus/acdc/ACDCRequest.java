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
 *	    ACDCRequest.java
 *
 * Author:
 *     Karthiga
 *
 * Created On:
 *     Nov 14, 20132:19:12 PM
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
package com.trimble.vilicus.acdc;



/**
 * @author kmuruga
 *
 */
public class ACDCRequest {
   
  public static final int CONTENT_TYPE_PLAINTEXT = 1;
   
   public static final int CONTENT_TYPE_JSON      = 2;
   
   public static final int CONTENT_TYPE_URL_ENCODE    = 3;
   
   
   public static final int POST                  = 0;
   public static final int GET                   = 1;
   public static final int PUT                   = 2;
   public static final int DELETE                = 3;
   
   public boolean isAuthorizationNeed=false;
   
   public boolean isResponseHeaderNeeed=false;
   
   
   public   String stRequestURL; 
   public String stData;
   public int iHttpRequestType;
   
   public int iContentType;
   public String stAuthorizationHeader;
   
   
   
   /**
    * 
    */
   public ACDCRequest(final String stRequestURL,final  String stData,
         final  int iHttpRequestType,
         final  int iContentType,final  String stAuthorizationHeader) {
      this.stRequestURL=stRequestURL;
      this.stData=stData;
      this.iHttpRequestType=iHttpRequestType;
      this.iContentType=iContentType;
      this.stAuthorizationHeader =stAuthorizationHeader;
      if(stAuthorizationHeader != null){
         isAuthorizationNeed=true;
      }
   }
   
   public ACDCRequest(final String stRequestURL,final  String stData,
         final  int iHttpRequestType,
         final  int iContentType,final  String stAuthorizationHeader,boolean isResponseHeaderNeeed) {
      this.stRequestURL=stRequestURL;
      this.stData=stData;
      this.iHttpRequestType=iHttpRequestType;
      this.iContentType=iContentType;
      this.stAuthorizationHeader =stAuthorizationHeader;
      if(stAuthorizationHeader != null){
         isAuthorizationNeed=true;
      }
      this.isResponseHeaderNeeed=isResponseHeaderNeeed;
   }
}

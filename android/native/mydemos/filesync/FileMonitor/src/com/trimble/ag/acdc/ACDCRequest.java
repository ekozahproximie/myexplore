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
package com.trimble.ag.acdc;



/**
 * @author kmuruga
 *
 */
public class ACDCRequest {
   
  public static final int CONTENT_TYPE_PLAINTEXT = 2;
   
   public static final int CONTENT_TYPE_JSON      = 1;
   
   public static final int CONTENT_TYPE_URL_ENCODE    = 3;
   
   public static final int ACCEPT_ALL    = 0;
   
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
   public boolean isCookieNeed=false;
   public boolean isAcceptZipEncode=false;
   public int iAcceptType=0;
   public boolean isAutoRedirect=false;
   public String stDomainName=null;
   
   public ACDCRequest(final String stRequestURL,final  String stData,
         final  int iHttpRequestType,
         final  int iContentType,
         final boolean isCookieNeed,
         final int iAcceptType,final boolean isAcceptZipEncode,
         final boolean isAutoRedirect) {
      this.stRequestURL=stRequestURL;
      this.stData=stData;
      this.iHttpRequestType=iHttpRequestType;
      this.iContentType=iContentType;
      this.isCookieNeed=isCookieNeed;
      this.iAcceptType=iAcceptType;
      this.isAcceptZipEncode=isAcceptZipEncode;
      this.isAutoRedirect=isAutoRedirect;
     
   }
   public ACDCRequest(final String stRequestURL,final  String stData,
         final  int iHttpRequestType,
         final  int iContentType,
         final boolean isCookieNeed,
         final int iAcceptType,final boolean isAcceptZipEncode,final String stDomainName) {
      this.stRequestURL=stRequestURL;
      this.stData=stData;
      this.iHttpRequestType=iHttpRequestType;
      this.iContentType=iContentType;
      this.isCookieNeed=isCookieNeed;
      this.iAcceptType=iAcceptType;
      this.isAcceptZipEncode=isAcceptZipEncode;
      this.stDomainName=stDomainName;
     
   }
   /**
    * 
    */
   public ACDCRequest(final String stRequestURL,final  String stData,
         final  int iHttpRequestType,
         final  int iContentType,final  String stAuthorizationHeader,final boolean isCookieNeed) {
      this.stRequestURL=stRequestURL;
      this.stData=stData;
      this.iHttpRequestType=iHttpRequestType;
      this.iContentType=iContentType;
      this.stAuthorizationHeader =stAuthorizationHeader;
      this.isCookieNeed=isCookieNeed;
      if(stAuthorizationHeader != null){
         isAuthorizationNeed=true;
      }
   }
   
   public ACDCRequest(final String stRequestURL,final  String stData,
         final  int iHttpRequestType,
         final  int iContentType,final  String stAuthorizationHeader,boolean isResponseHeaderNeeed
         ,final boolean isCookieNeed) {
      this.stRequestURL=stRequestURL;
      this.stData=stData;
      this.iHttpRequestType=iHttpRequestType;
      this.iContentType=iContentType;
      this.stAuthorizationHeader =stAuthorizationHeader;
      this.isCookieNeed=isCookieNeed;
      if(stAuthorizationHeader != null){
         isAuthorizationNeed=true;
      }
      this.isResponseHeaderNeeed=isResponseHeaderNeeed;
   }
}

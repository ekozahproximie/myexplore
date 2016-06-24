/**
 * Copyright Trimble Inc., 2015 - 2016 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Spime Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *		atsLocationService
 *
 * Module Name:
 *		com.trimble.ag.ats.sync.account       		
 *
 * File name:
 *		ACDCAuthenticatorService.java
 *
 * Author:
 *		sprabhu
 *
 * Created On:
 * 		02-Nov-2015 1:38:31 pm
 *
 * Abstract:
 *
 *
 * Environment:
 *	Mobile Profile          :
 *  	Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.ag.ats.sync.account;

import com.trimble.ag.ats.db.LocationContent;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * @author sprabhu
 *
 */
public class ACDCAuthenticatorService extends Service {

 
   private static ACDCAuthenticator acdcAuthenticator = null;
   /**
    * 
    */
   public ACDCAuthenticatorService() {
   
   }
   
   public static final String ACCOUNT_TYPE = LocationContent.CONTENT_AUTHORITY;
   
   public static final String ACCOUNT_NAME = "Connectedfarm";
   
   
   
   /**
    * Obtain a handle to the {@link android.accounts.Account} used for sync in this application.
    *
    * @return Handle to application's account (not guaranteed to resolve unless CreateSyncAccount()
    *         has been called)
    */
   public static Account GetAccount() {
       // Note: Normally the account name is set to the user's identity (username or email
       // address). However, since we aren't actually using any user accounts, it makes more sense
       // to use a generic string in this case.
       //
       // This string should *not* be localized. If the user switches locale, we would not be
       // able to locate the old account, and may erroneously register multiple accounts.
       final String accountName = ACCOUNT_NAME;
       return new Account(accountName, ACCOUNT_TYPE);
   }
   
   @Override
   public void onCreate() {
      super.onCreate();
      if(acdcAuthenticator == null){
         acdcAuthenticator= new ACDCAuthenticator(getApplicationContext());
      }
   }
   
   @Override
   public IBinder onBind(Intent intent) {
      return acdcAuthenticator.getIBinder();
      
   }

}

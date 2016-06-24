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
 *		ACDCAuthenticator.java
 *
 * Author:
 *		sprabhu
 *
 * Created On:
 * 		02-Nov-2015 1:40:00 pm
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

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.trimble.ag.ats.AuthenticatorActivity;
import com.trimble.ag.ats.acdc.ACDCApi;
import com.trimble.ag.ats.acdc.res.LoginResponse;
import com.trimble.ag.ats.entity.User;

import java.io.IOException;
import java.net.UnknownHostException;


/**
 * @author sprabhu
 *
 */
public class ACDCAuthenticator extends AbstractAccountAuthenticator {

   private transient Context mContext = null;
   
   private  static ACDCApi sServerAuthenticate = null;
   /**
    * User data fields
    */
   public static final String USERDATA_USER_OBJ_ID = "userObjectId";   //Parse.com object id
   
   public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
   public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
   
   public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to Connected account";
   public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Connected account";
   

   
   
   private static final String TAG                        = ACDCAuthenticator.class
                                                                .getSimpleName();
      
   /**
    * @param context
    */
   public ACDCAuthenticator(Context context) {
      super(context);
      mContext=context;
      sServerAuthenticate =ACDCApi.getInstance(mContext.getApplicationContext());
   }

  

   // Don't add additional accounts
   @Override
   public Bundle addAccount(AccountAuthenticatorResponse response,
         String accountType, String authTokenType, String[] requiredFeatures,
         Bundle options) throws NetworkErrorException {
      final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
      intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
      intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
      intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
      intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
      final Bundle bundle = new Bundle();
      bundle.putParcelable(AccountManager.KEY_INTENT, intent);
      return bundle;
      
    
   }

  
   @Override
   public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {

       Log.d("Connected", TAG + "> getAuthToken");

       // If the caller requested an authToken type we don't support, then
       // return an error
       if (!authTokenType.equals(ACDCAuthenticator.AUTHTOKEN_TYPE_READ_ONLY) && !authTokenType.equals(ACDCAuthenticator.AUTHTOKEN_TYPE_FULL_ACCESS)) {
           final Bundle result = new Bundle();
           result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
           return result;
       }
       
       // Extract the username and password from the Account Manager, and ask
       // the server for an appropriate AuthToken.
       final AccountManager am = AccountManager.get(mContext);

       String authToken = am.peekAuthToken(account, authTokenType);
       String userId = null; //User identifier, needed for creating ACL on our server-side

       Log.d("Connected", TAG + "> peekAuthToken returned - " + authToken);

       // Lets give another try to authenticate the user
       if (TextUtils.isEmpty(authToken)) {
           final String password = am.getPassword(account);
           if (password != null) {
               try {
                   Log.d("Connected", TAG + "> re-authenticating with the existing password");
                   if (password != null) {
                      LoginResponse loginResponse;
                    try {
                       loginResponse = sServerAuthenticate.login();
                       if(loginResponse != null && loginResponse.isSuccess){
                          authToken = loginResponse.ticket;
                          final User user = sServerAuthenticate.getCurrentUser();
                          if (user != null) {
                           
                             userId = String.valueOf(user.getUserId());
                         }
                       }
                    } catch (UnknownHostException e) {
                     
                       e.printStackTrace();
                    } catch (IOException e) {
                     
                       e.printStackTrace();
                    }
                     
                  }
                   
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
       }

       // If we get an authToken - we return it
       if (!TextUtils.isEmpty(authToken)) {
           final Bundle result = new Bundle();
           result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
           result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
           result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
           return result;
       }

       // If we get here, then we couldn't access the user's password - so we
       // need to re-prompt them for their credentials. We do that by creating
       // an intent to display our AuthenticatorActivity.
       final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
       intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
       intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
       intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
       final Bundle bundle = new Bundle();
       bundle.putParcelable(AccountManager.KEY_INTENT, intent);
       return bundle;
   }


      // Getting a label for the auth token is not supported
   @Override
   public String getAuthTokenLabel(String authTokenType) {
      if (AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
         return AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
     else if (AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
         return AUTHTOKEN_TYPE_READ_ONLY_LABEL;
     else
         return authTokenType + " (Label)";
   }

   // Updating user credentials is not supported
   @Override
   public Bundle updateCredentials(AccountAuthenticatorResponse response,
         Account account, String authTokenType, Bundle options)
         throws NetworkErrorException {
      
     return null;
   }
   // Ignore attempts to confirm credentials
   @Override
   public Bundle confirmCredentials(AccountAuthenticatorResponse response,
         Account account, Bundle options) throws NetworkErrorException {
  
     return null;
   }
   // Editing properties is not supported

   @Override
   public Bundle editProperties(AccountAuthenticatorResponse response,
         String accountType) {
    
      return null;
   }
   // Checking features for the account is not supported

   @Override
   public Bundle hasFeatures(AccountAuthenticatorResponse response,
         Account account, String[] features) throws NetworkErrorException {
      final Bundle result = new Bundle();
      result.putBoolean(KEY_BOOLEAN_RESULT, false);
      return result;
   }

}

package com.trimble.ag.acdc;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.trimble.ag.acdc.device.DeviceIdentityRequest;
import com.trimble.ag.acdc.file.FileUploadedNotificationAPI;
import com.trimble.ag.acdc.file.FileUploadedNotificationRequest;
import com.trimble.ag.acdc.file.FileUploadedNotificationResponse;
import com.trimble.ag.acdc.s3.StorageKeyAPI;
import com.trimble.ag.acdc.s3.StorageKeyRequest;
import com.trimble.ag.acdc.s3.StorageKeyResponse;
import com.trimble.ag.filemonitor.utils.Utils;
import com.trimble.ag.nabu.acdc.res.ACDCResponse;
import com.trimble.ag.nabu.acdc.res.LoginResponse;

import java.io.IOException;
import java.net.UnknownHostException;

public final class ACDCApi {

   public static final String  TAG                      = "ACDC";

   private static final String LOGIN_URL                = "%s://%s/ClientApplication/v1/DeviceIdentity";

   private static final String REFRESH_URL              = "%s://%s/ClientApplication/v1/UserSessions/?orgID=%s";

   private static final String ORGANIZATION_URL         = "%s://%s/ClientApplication/v1/UserSessions/?orgID=%s";

   private static ACDCApi      acdcAPI                  = null;

   private Context             context                  = null;

   private final static String TICKET                   = "ticket";

   private final static String LAST_KEY_GET_TIME        = "lasttime";

   private static final String COMMUICATION_SERVER_MODE = "communcation_server";

   public static final int     DEV_MODE                 = 1;

   public static final int     TEST_MODE                = 2;

   public static final int     REL_MODE                 = 3;

   public static final int     PROD_MODE                = 4;

   public static final int     DEMO_MODE                = 5;

   private static final String DEV_DOMAIN_NAME          = "clientsvcs.wolverine.sandbox.farm";

   private static final String TEST_DOMAIN_NAME         = "team03-clientsvcs.myconnectedfarm.com";

   // private static final String REL_DOMAIN_NAME =
// "rel-integrations.myconnectedfarm.com";
   private static final String REL_DOMAIN_NAME          = "rel-clientsvcs.myconnectedfarm.com";
   // private static final String REL_DOMAIN_NAME = "10.122.141.12";

   private static final String PROD_DOMAIN_NAME         = "clientsvcs.myconnectedfarm.com";

   public static final String  DEMO_DOMAIN_NAME         = "demo-local.com";

   private static final String APP_MODE                 = "devmode";

   public static final String  SERVER_DATE_FORMAT       = "yyyy-MM-dd'T'HH:mm:ss'Z'";
   public static final String  SERVER_DATE_FORMAT_MILI  = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

   public static final String  HTTP                     = "http";

   public static final String  HTTPS                    = "https";

   public String               deviceId                 = null;

   public static final String  PACKAGENAME              = "com.trimble.vilicus";

   private final static String D_TICKET                 = null;

   public String               productName              = null;

   private long                currentTime;

   public static final int     KEY_EXPIRE_ERROR_CODE    = 400;

   private static final String APP_FIRST_LAUNCH         = "app_first_launch";

   public static final String  C_MODELSTATE             = "modelState";
   public static final String  C_MESSAGE                = "message";

   public static final String  C_RESULT_CODE            = "resultCode";

   public static final String  RESULT_CODE              = "ResultCode";

   public static final String  KEY_EXPIRE               = "The incoming token has expired. Get a new access token from the Authorization Server.";
   
   private transient StorageKeyAPI  storageKeyAPI = null;
   
   private transient FileUploadedNotificationAPI fileUploadedNotificationAPI  =null;

   public static synchronized ACDCApi getInstance(Context context) {
      if (acdcAPI == null) {
         acdcAPI = new ACDCApi(context);
         Log.i(TAG, " ACDC-Instance Created");
      }
      return acdcAPI;
   }

   private ACDCApi(Context context) {

      if (acdcAPI != null) {
         throw new IllegalArgumentException("use getInstance method");
      }
      this.context = context;

      currentTime = System.currentTimeMillis();
      checkProductionMode(context);
      storageKeyAPI = new StorageKeyAPI(context);
      fileUploadedNotificationAPI = new FileUploadedNotificationAPI(context);
   }

   public boolean isDevModeManifast() {
      boolean isDevMode = false;
      // get the key from the manifest
      final PackageManager pm = context.getPackageManager();
      try {
         final ApplicationInfo info = pm.getApplicationInfo(
               context.getPackageName(), PackageManager.GET_META_DATA);
         if (info.metaData != null) {
            isDevMode = info.metaData.getBoolean(APP_MODE);

         } else {
            Log.e(TAG, "dev mode meta data does in the manifest.xml");
         }

      } catch (final NameNotFoundException e) {
         Log.e(TAG, e.getMessage(), e);
      }
      return isDevMode;
   }

   private void checkProductionMode(final Context context) {
      if (context == null) {
         return;
      }
      final SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(context);
      final boolean isAPPFirstLaunch = preferences.getBoolean(APP_FIRST_LAUNCH,
            true);
      if (isAPPFirstLaunch) {
         // get the key from the manifest
         final boolean isDevMode = isDevModeManifast();

         final Editor editor = preferences.edit();
         editor.putBoolean(APP_FIRST_LAUNCH, false);
         editor.commit();
      }

   }

   public interface NetWorkListener {

      public static final int SUCCESS     = 0;
      public static final int NO_NETWORK  = 1;
      public static final int NETWORK_CUT = 2;
      public static final int UNKNOWN     = 3;

      public void requestSucesss(boolean isSucess, int iErrorCode);

   }

   public synchronized LoginResponse login() throws UnknownHostException,
         IOException {
      LoginResponse loginResponse = null;
      boolean status = true;
      final int RESPONSE_CODE = 200;
      JsonClient client = new JsonClient(context);
      String stRegistrarionURL = String.format(LOGIN_URL, getProtoCal(),
            getDomainName());

      Log.i(TAG, "Login URL:" + stRegistrarionURL);
      DeviceIdentityRequest deviceidentityRequest = getDeviceIndentityInfo();
      if (deviceidentityRequest == null) {
         Log.i(TAG, "Login request null");
         return loginResponse;
      }
      ACDCResponse acdcResponse = null;

      final ACDCRequest acdcRequest = new ACDCRequest(stRegistrarionURL,
            deviceidentityRequest.getIdentityRequestJson(), ACDCRequest.POST,
            ACDCRequest.CONTENT_TYPE_JSON, null, false);
      acdcResponse = client.connectHttp(acdcRequest);
      try {

         String stData = client.convertByteArrayToString(acdcResponse.resData);
         if (stData != null) {
            Log.i(TAG, "Login Response code:" + acdcResponse.iResponseCode);
            Log.i(TAG, "Login Response:" + stData);
            loginResponse = new LoginResponse();

            if (RESPONSE_CODE != acdcResponse.iResponseCode) {
               status = false;
               Log.i(TAG, "Login Exception-ErrorCode"
                     + acdcResponse.iResponseCode);

               loginResponse.isAuthenticationFailed(stData);
               Log.i(TAG, "Login Error Msg:" + loginResponse.stMeaning);
               status = false;
            } else {

               loginResponse.readResponse(stData);
               if (loginResponse.ticket == null) {
                  status = false;
                  return loginResponse;
               }

               storeTicket(loginResponse.ticket);
             

            }
            final String stResp = loginResponse.toString();

            Log.i(TAG, "Login-Responsecode:" + acdcResponse.iResponseCode
                  + ";Response:" + stResp);
         }
      } finally {

      }

      return loginResponse;
   }

   public void updateData() throws UnknownHostException, IOException {
        final StorageKeyRequest storageKeyRequest   =new StorageKeyRequest("dev", "a541a3de-7dff-4313-8325-89d77f43f196", "myFile.png");
        getStorageKey(storageKeyRequest);
   }

   public synchronized StorageKeyResponse getStorageKey(final StorageKeyRequest storageKeyRequest) throws UnknownHostException, IOException{
      return storageKeyAPI.getStoragekey(this,storageKeyRequest);
   }
   
   public synchronized FileUploadedNotificationResponse doFileUploadedNotification(
         final FileUploadedNotificationRequest fileUploadedNotificationRequest) 
               throws UnknownHostException, IOException{
      return fileUploadedNotificationAPI.getUploadedNotification(this, fileUploadedNotificationRequest);
   }
   public synchronized boolean refersh(final String stOrgID)
         throws UnknownHostException, IOException {

      if (stOrgID == null) {
         Log.i(TAG, "Refersh URL orgID is null");
         return false;
      }

      boolean status = true;
      final int RESPONSE_CODE = 201;
      JsonClient client = new JsonClient(context);
      String stRefershURL = String.format(REFRESH_URL, getProtoCal(),
            getDomainName(), stOrgID);

      Log.i(TAG, "Refersh URL:" + stRefershURL);
      final ACDCRequest acdcRequest = new ACDCRequest(stRefershURL, null,
            ACDCRequest.POST, ACDCRequest.CONTENT_TYPE_URL_ENCODE, getTicket(),
            false);
      final ACDCResponse acdcResponse = client.connectHttp(acdcRequest);
      try {

         String stData = client.convertByteArrayToString(acdcResponse.resData);
         if (stData != null) {
            Log.i(TAG, "Refersh Response code:" + acdcResponse.iResponseCode);
            Log.i(TAG, "Refersh Response:" + stData);
            LoginResponse loginResponse = new LoginResponse();

            if (RESPONSE_CODE != acdcResponse.iResponseCode) {

               status = false;
               Log.i(TAG, "Refersh Exception-ErrorCode"
                     + acdcResponse.iResponseCode);
               if (acdcResponse.iResponseCode == ACDCApi.KEY_EXPIRE_ERROR_CODE
                     && loginResponse.isKeyExpire(stData)) {
                  LoginResponse response = login();
                  if (response != null) {
                     boolean isLoginSucess = response.isSuccess;
                     if (isLoginSucess) {
                        refersh(stOrgID);
                     }
                  }
               }

            } else {
               loginResponse.readOrgChangeTicket(stData);
               storeTicket(loginResponse.ticket);

            }
            String stResp = loginResponse.toString();

            Log.i(TAG, "Refersh URL-Responsecode:" + acdcResponse.iResponseCode
                  + ";Response:" + stResp);
         }
      } finally {

      }

      return status;
   }

   public void storeTicket(String stTicket) {
      SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(context);
      Editor editor = preferences.edit();
      editor.putString(TICKET, stTicket);
      java.util.Date date = new java.util.Date();
      long lCurrentTime = date.getTime();
      editor.putLong(LAST_KEY_GET_TIME, lCurrentTime);
      Log.i(TAG, "Storing Ticket :" + stTicket + " with time:" + date);
      editor.commit();
   }

   public String getTicket() {
      String stTicket = null;
      SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(context);
      stTicket = preferences.getString(TICKET, D_TICKET);
      // Log.i(TAG, "getTicket- ticket = " + stTicket);
      return stTicket;
   }

   public void checkRegistrationKeyExpire(NetWorkListener listener) {

      if (isKeyExpire()) {
         doRegistration(listener);
      } else {
         if (listener != null) {
            listener.requestSucesss(true, NetWorkListener.SUCCESS);
         }
      }

   }

   public boolean isKeyExpire() {

      boolean isKeyExpire = false;
      SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(context);
      String stTicket = getTicket();
      if (stTicket == null) {
         return true;
      }
      long lastKeyTime = preferences.getLong(LAST_KEY_GET_TIME, -1);
      if (currentTime > lastKeyTime) {
         long diff = currentTime - lastKeyTime;

         final long timeInMillis = diff;

         final int days = (int) (timeInMillis / (24L * 60 * 60 * 1000));

         int remdr = (int) (timeInMillis % (24L * 60 * 60 * 1000));

         final int hours = remdr / (60 * 60 * 1000);

         remdr %= 60 * 60 * 1000;

         final int minutes = remdr / (60 * 1000);

         remdr %= 60 * 1000;

         final int seconds = remdr / 1000;

         final int ms = remdr % 1000;
         if (days >= 1 && seconds >= 1) {
            isKeyExpire = true;
         }
      }
      Log.i(TAG, "KeyExpiration Status:" + isKeyExpire);
      return isKeyExpire;
   }

   private static final String REGISTRATION_THREAD = "Registration thread";

   public void doRegistration(NetWorkListener listener) {
      this.mNetWorkListener = listener;
      final boolean isNetworkAvaliable = Utils.isInternetConnection(context);
      if (isNetworkAvaliable || isDemoServer()) {
         if (Utils.isThreadRunning(REGISTRATION_THREAD)) {
            Log.d(TAG, "Registration thread is already running");
            return;
         }
         Thread thread = new Thread(mRunnable, REGISTRATION_THREAD);
         thread.start();
      } else {
         if (mNetWorkListener != null) {
            mNetWorkListener.requestSucesss(false, NetWorkListener.NO_NETWORK);
         }
      }
      // mRegistrationHandler.post(mRunnable);
   }

   public void doRefersh() {

      final boolean isNetworkAvaliable = Utils.isInternetConnection(context);
      if (isNetworkAvaliable && getTicket() != null && isKeyExpire()) {
         Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
               try {
                  Log.i(TAG, "Refersh thread started");

                  refersh(null);
               } catch (UnknownHostException e) {
                  Log.e(TAG, e.getMessage(), e);

               } catch (IOException e) {

                  Log.e(TAG, e.getMessage(), e);
               }

            }
         }, "Refersh thread");
         thread.start();
      }
   }

   private NetWorkListener mNetWorkListener = null;

   public void unRegisterNetworkListener() {
      mNetWorkListener = null;
   }

   private void sendMessage(final boolean isSucess, final int iMessageCode) {
      if (mNetWorkListener != null) {
         mNetWorkListener.requestSucesss(isSucess, iMessageCode);
      }
   }

   private Runnable mRunnable = new Runnable() {

                                 @Override
                                 public void run() {

                                    try {
                                       boolean isSucess = false;

                                       if (!Utils.isInternetConnection(context)
                                             && !isDemoServer()) {

                                          sendMessage(false,
                                                NetWorkListener.NO_NETWORK);
                                          return;
                                       }
                                       LoginResponse response = login();
                                       if (response != null) {
                                          isSucess = response.isSuccess;
                                       }

                                       if (isSucess) {
                                          updateData();
                                       } else {
                                          storeTicket(null);
                                       }

                                       sendMessage(isSucess,
                                             NetWorkListener.SUCCESS);

                                    } catch (UnknownHostException e) {
                                       Log.e(TAG, e.getMessage(), e);
                                       sendMessage(false,
                                             NetWorkListener.NETWORK_CUT);

                                    } catch (IOException e) {
                                       Log.e(TAG, e.getMessage(), e);

                                       sendMessage(false,
                                             NetWorkListener.NETWORK_CUT);

                                    }
                                 }
                              };

   public static void close() {
      acdcAPI = null;
   }

   public void serverMode(int iServerMode) {

      if (iServerMode < DEV_MODE || iServerMode > DEMO_MODE) {
         return;
      }
      final SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(context);
      final Editor editor = preferences.edit();

      editor.putInt(COMMUICATION_SERVER_MODE, iServerMode);

      Log.i(TAG, "Set serverMode:" + iServerMode);
      editor.commit();
   }

   public int getServerMode() {
      boolean isDevMode =true;
      final SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(context);
      int iServerMode = preferences.getInt(COMMUICATION_SERVER_MODE, getAppDefalutServerMode(isDevMode));
      Log.i(TAG, "Get serverMode:" + iServerMode);
      return iServerMode;
   }

   public String getDomainName() {
      final int iServerMode = getServerMode();
      String stServerName = null;
      switch (iServerMode) {
         case DEV_MODE:
            stServerName = DEV_DOMAIN_NAME;
            break;
         case TEST_MODE:
            stServerName = TEST_DOMAIN_NAME;
            break;
         case REL_MODE:
            stServerName = REL_DOMAIN_NAME;
            break;
         case PROD_MODE:
            stServerName = PROD_DOMAIN_NAME;
            break;
         case DEMO_MODE:
            stServerName = DEMO_DOMAIN_NAME;
            break;
         default:
            break;
      }
      return stServerName;
   }

   public boolean isDemoServer() {
      final int iServerMode = getServerMode();
      return iServerMode == DEMO_MODE;
   }

   private DeviceIdentityRequest getDeviceIndentityInfo() {
      DeviceIdentityRequest loginRequest = new DeviceIdentityRequest();

      return loginRequest;
   }

   public void logOut() {
      final SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(context);
      final Editor editor = preferences.edit();
      storeTicket(null);
      editor.commit();
      Log.i(TAG, "Logged out");
   }

   public boolean islogOut() {
      boolean isLogout = true;

      if (getTicket() != null) {
         isLogout = false;
      }

      return isLogout;
   }

   public Context getContext() {
      return context;
   }

   public String getProtoCal() {
      String stProtoCal = HTTP;
      final int iServerMode=getServerMode() ;
      switch (iServerMode) {
         case REL_MODE:
         case PROD_MODE:  
            stProtoCal = HTTPS;
            break;
         default:
            break;
      }
      
      return stProtoCal;

   }

   public int getAppDefalutServerMode(final boolean isDevMode) {
      int iServerMode = ACDCApi.PROD_MODE;
      if (isDevMode) {
         iServerMode = ACDCApi.DEV_MODE;
      }
      return iServerMode;
   }

   public ACDCResponse getDemoServerResponse(final int iResponseCode,
         final String stFileName) {
      ACDCResponse acdcResponse = null;

      final DemoServer demoServer = DemoServer.getInstance(context
            .getApplicationContext());
      if (demoServer.isJSONFileFound(stFileName)) {
         acdcResponse = demoServer.getResponse(iResponseCode, stFileName);
      }
      return acdcResponse;
   }
}

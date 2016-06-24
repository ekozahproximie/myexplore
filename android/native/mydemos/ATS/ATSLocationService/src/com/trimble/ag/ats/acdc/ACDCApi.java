package com.trimble.ag.ats.acdc;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.trimble.ag.ats.acdc.applicationIndentity.ApplicationIndentityServiceAPI;
import com.trimble.ag.ats.acdc.applicationIndentity.req.ApplicationIndentityRequest;
import com.trimble.ag.ats.acdc.req.LoginRequest;
import com.trimble.ag.ats.acdc.res.ACDCResponse;
import com.trimble.ag.ats.acdc.res.LoginResponse;
import com.trimble.ag.ats.db.ATSContentProvdier;
import com.trimble.ag.ats.entity.Organization;
import com.trimble.ag.ats.entity.User;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Vector;

public final class ACDCApi {

	public static final String TAG = "ACDC";

	private static final String LOGIN_URL = "%s://%s/ClientApplication/v1/UserSessions";

	private static final String REFRESH_URL = "%s://%s/ClientApplication/v1/UserSessions/?orgID=%s";
	
	private static final String ORGANIZATION_URL = "%s://%s/ClientApplication/v1/UserSessions/?orgID=%s";

	private static ACDCApi acdcAPI = null;

	private Context context = null;

	private final static String TICKET = "ticket";
	
	private final static String APP_INDENTITY_TICKET = "app_indentity_ticket";

	private final static String LAST_KEY_GET_TIME = "lasttime";

	private static final String COMMUICATION_SERVER_MODE = "communcation_server";

	public static final int DEV_MODE = 1;

	public static final int TEST_MODE = 2;

	public static final int REL_MODE = 3;

	public static final int PROD_MODE = 4;
	
	public static final int DEMO_MODE = 5;
	
	public static final int WOLVERINE_MODE = 6;
	
	public static final int OTHER_MODE = 7;

	private static final String DEV_DOMAIN_NAME = "dev-clientsvcs.myconnectedfarm.com";

	private static final String TEST_DOMAIN_NAME = "tst-clientsvcs.myconnectedfarm.com";

	//private static final String REL_DOMAIN_NAME = "rel-integrations.myconnectedfarm.com";
	private static final String REL_DOMAIN_NAME = "rel-clientsvcs.myconnectedfarm.com";
	//private static final String REL_DOMAIN_NAME = "10.122.141.12";
	
	private static final String PROD_DOMAIN_NAME = "clientsvcs.myconnectedfarm.com";
	
	public static final String DEMO_DOMAIN_NAME = "demo-local.com";
	
	public static final String WOLVERINE_DOMAIN_NAME = "wolverine-clientsvcs.myconnectedfarm.com";
	
	public static final String OTHER_DOMAIN_NAME = "clientsvcs.greenlantern.sandbox.farm";
	
	private static final String APP_MODE = "devmode";
	
	private static final String SERVER_MODE = "server_mode";
	
	private static final String OTHER_SERVER_ADDRESS="other_server_address";
	
	private static final String OTHER_SERVER_DOMAIN="other_server_domain";
	
	private static final String OTHER_SERVER_PROTOCAL="other_server_protocal";
	
	public static final String SERVER_DATE_FORMAT="yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String SERVER_DATE_FORMAT_MILI="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	public static final String HTTP="http";
	
	public static final String HTTPS="https";

	// Development Environment
	// private static String DOMAIN_NAME = null;

	// Development Environment
	// private static String SERVERS_NAME = null;;

	// private long currentTime = -1;

	public String deviceId = null;

	public static final String PACKAGENAME = "com.trimble.vilicus";

	private final static String D_TICKET = null;

	public String productName = null;

	private long currentTime;

	public static final int KEY_EXPIRE_ERROR_CODE=400;
	
	private static final String APP_FIRST_LAUNCH="app_first_launch";
	
         public static final String      C_MODELSTATE             = "modelState";
         public static final String      C_MESSAGE                = "message";
	 
	  public static final String C_RESULT_CODE					="resultCode";
	  
	  public static final String RESULT_CODE					="ResultCode";
	  
	  public static final String KEY_EXPIRE		="The incoming token has expired. Get a new access token from the Authorization Server.";
	  
	  public static final String ORG_ERROR="Generating token failed with reason: OrganizationNotFoundForUserAndAppname";

	
	
	private ApplicationIndentityServiceAPI indentityServiceAPI = null; 
	
	
	
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
		
		indentityServiceAPI = new ApplicationIndentityServiceAPI(context);
		
		currentTime = System.currentTimeMillis();
		checkProductionMode(context);
                
      
	}
	public boolean isDevModeManifast(){
	   boolean isDevMode =false;
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
	private int getManifestServerMode(){
	    int iServerMode=WOLVERINE_MODE;
	           // get the key from the manifest
	           final PackageManager pm = context.getPackageManager();
	           try {
	              final ApplicationInfo info = pm.getApplicationInfo(
	                    context.getPackageName(), PackageManager.GET_META_DATA);
	              if (info.metaData != null) {
	                 
	                 iServerMode =info.metaData.getInt(SERVER_MODE, REL_MODE);
	              } else {
	                 Log.e(TAG, "server mode meta data does in the manifest.xml");
	              }

	           } catch (final NameNotFoundException e) {
	              Log.e(TAG, e.getMessage(), e);
	           }
	           return iServerMode;
	        
	}
	private void checkProductionMode(final Context context){
	   if(context == null){
              return;
	   }
	   final SharedPreferences preferences = PreferenceManager
	            .getDefaultSharedPreferences(context);
	      final boolean isAPPFirstLaunch = preferences.getBoolean(APP_FIRST_LAUNCH,
	            true);
	      if (isAPPFirstLaunch) {
	         // get the key from the manifest
	       
	               final boolean isDevMode = isDevModeManifast();
	               
	               serverMode(getManifestServerMode());
	               
	           
	         final Editor editor =preferences.edit();
	         editor.putBoolean(APP_FIRST_LAUNCH, false);
	         editor.commit();
	      }
	      
	}

	public interface NetWorkListener {

		public void requestSucesss(boolean isSucess, String stErrorMsg);
		public void dataUpdateListener(final int iMode);
	}

	public synchronized LoginResponse login() throws UnknownHostException,
        IOException {
LoginResponse loginResponse =null;
boolean status = true;
final int RESPONSE_CODE = 201;
JsonClient client = new JsonClient(context);
String stRegistrarionURL = String.format(LOGIN_URL,getProtoCal(), getDomainName());
final boolean isJSONString=false;

Log.i(TAG, "Login URL:" + stRegistrarionURL);
LoginRequest loginRequest = getLoginInfo();
if (loginRequest == null) {
        Log.i(TAG, "Login request null");
        return loginResponse;
}
ACDCResponse acdcResponse =null;


   final ACDCRequest acdcRequest = new ACDCRequest(stRegistrarionURL,
                loginRequest.getRequestData(isJSONString), ACDCRequest.POST,
                ACDCRequest.CONTENT_TYPE_URL_ENCODE,null,false);
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
loginResponse.setUserName(loginRequest.username);
loginResponse.setPassword(loginRequest.password);
// Organizations for the user is sorted in alphabetical order and
// the first is assigned to user
String stResponseOrgnizationID = loginResponse.stOrganizationID;
storeTicket(loginResponse.ticket);

storeOrgnaizationStuff(loginResponse);
final String stLastLoginOrgId = getOrganizationID();
if(stLastLoginOrgId != null){
  Log.i(TAG, "Last login org id = " + stLastLoginOrgId);
  stResponseOrgnizationID = stLastLoginOrgId;
} else {
  Log.i(TAG, "No Last org id found");
}

// OrganizationID received from server or random number generated
// will be stored
storeLoginInfo(loginResponse.stUserName,
     loginResponse.stPassword, loginResponse.stOrganizationID);
// stResponseOrgnizationID is the organizationID received from
// server for this user
// For demo user and user who is not associated with any
// organization, directly data will be downloaded
if (stResponseOrgnizationID != null) {
  boolean isSucess = organizationUpdate(stResponseOrgnizationID);
 
    

     Log.i(TAG, "change organization isSucess:"+ isSucess);
} else {
 
  Log.i(TAG, " organization  not found for this server");

}

}
String stResp = loginResponse.toString();

Log.i(TAG, "Login-Responsecode:" + acdcResponse.iResponseCode
  + ";Response:" + stResp);
}
} finally {

}

return loginResponse;
}

private void storeOrgnaizationStuff(final LoginResponse loginResponse){

final int iServerMode = getServerMode();
final ATSContentProvdier nabuContentProvider = ATSContentProvdier
 .getInstance(context);
Vector<Organization> vecOrganization = loginResponse
 .getVecOrganizations();
nabuContentProvider.deleteUserOrgMappingNotInList(vecOrganization, loginResponse.stUserName, iServerMode);
if (vecOrganization != null) {
for (Organization org : vecOrganization) {
 nabuContentProvider.insertUserName(
       loginResponse.stUserName, org.getName(),
       org.getOrgId(), iServerMode,loginResponse.getPrimaryOrgId());
}
} else {
User user = nabuContentProvider.insertUserName(
    loginResponse.stUserName,
    loginResponse.stOrganizationName,
    loginResponse.stOrganizationID, iServerMode,loginResponse.getPrimaryOrgId());
loginResponse.stOrganizationID = nabuContentProvider
    .getUserOrgId(user, iServerMode);
}

}
	
             public synchronized boolean organizationUpdate(final String stOrgID) throws UnknownHostException,
                             IOException {
                     
                     boolean status = true;
                     final int RESPONSE_CODE = 201;
                     JsonClient client = new JsonClient(context);
                     String stRefershURL = String.format(ORGANIZATION_URL, getProtoCal(),getDomainName(),stOrgID);
                     Log.i(TAG, "Organization URL:" + stRefershURL);
                     final ACDCRequest acdcRequest = new ACDCRequest(stRefershURL,null,
                           ACDCRequest.POST, ACDCRequest.CONTENT_TYPE_URL_ENCODE,
                           getTicket(),false);
                     final ACDCResponse acdcResponse = client.connectHttp(acdcRequest);
                     try {
                     
                             String stData = client.convertByteArrayToString(acdcResponse.resData);
                             if (stData != null) {
                                Log.i(TAG, "Organization Response code:"+acdcResponse.iResponseCode);
                                Log.i(TAG, "Organization Response:"+stData);
                                     LoginResponse loginResponse = new LoginResponse();
                                     
                                     if (RESPONSE_CODE != acdcResponse.iResponseCode) {
                     
                                             status = false;
                                             Log.i(TAG, "Organization Exception-ErrorCode"+acdcResponse.iResponseCode);
                                    if(acdcResponse.iResponseCode == ACDCApi.KEY_EXPIRE_ERROR_CODE
                                                       && loginResponse.isKeyExpire(stData)){
                                        LoginResponse response =login();
                                        if(response != null){
                                               boolean isLoginSucess=response.isSuccess;
                                               if(isLoginSucess){
                                                     organizationUpdate(stOrgID);
                                               }
                                        }
                                       }
                     
                                     } else {
                                             loginResponse.readOrgChangeTicket(stData);
                                             storeTicket(loginResponse.ticket);
                                             storeOrganizationID(stOrgID);
                                             
                                     }
                                     String stResp = loginResponse.toString();
                     
                                     Log.i(TAG, "Organization URL-Responsecode:" + acdcResponse.iResponseCode
                                                     + ";Response:" + stResp);
                             }
                     } finally {
                     
                     }
                     
                     return status;
                     }

	public synchronized boolean refersh(final String stOrgID,final boolean isOrgChange) throws UnknownHostException,
			IOException {
	        
	      if(stOrgID == null){
	         Log.i(TAG, "Refersh URL orgID is null");
	         return false;
	      }
	       
		boolean status = true;
		final int RESPONSE_CODE = 201;
		JsonClient client = new JsonClient(context);
		String stRefershURL = String.format(REFRESH_URL, getProtoCal(),getDomainName(),stOrgID);

		Log.i(TAG, "Refersh URL:" + stRefershURL);
		final ACDCRequest acdcRequest = new ACDCRequest(stRefershURL, null,
		      ACDCRequest.POST, ACDCRequest.CONTENT_TYPE_URL_ENCODE,getTicket(),false);
		final ACDCResponse acdcResponse = client.connectHttp(acdcRequest);
		try {

			String stData = client.convertByteArrayToString(acdcResponse.resData);
			if (stData != null) {
			   Log.i(TAG, "Refersh Response code:"+acdcResponse.iResponseCode);
                           Log.i(TAG, "Refersh Response:"+stData);
				LoginResponse loginResponse = new LoginResponse();
				
				if (RESPONSE_CODE != acdcResponse.iResponseCode) {

					status = false;
					Log.i(TAG, "Refersh Exception-ErrorCode"+acdcResponse.iResponseCode);
         		               if(acdcResponse.iResponseCode == ACDCApi.KEY_EXPIRE_ERROR_CODE
         			              		  && loginResponse.isKeyExpire(stData)){
         		            	   LoginResponse response =login();
         		            	if(response != null){
         			              	  boolean isLoginSucess=response.isSuccess;
         			              	  if(isLoginSucess){
         			              		refersh(stOrgID,isOrgChange);
         			              	  }
         			           }
         		               }

				} else {
					loginResponse.readOrgChangeTicket(stData);
					storeOrganizationID(stOrgID);  
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

	public static final String ORG_REFRESH_THREAD = "orgIdRefreshThread";
	private static final int    SUCCESS             = 1;
	private static final int    FAILURE             = 0;
	
	
	 
         
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
		
		stTicket = getPreferenceValue(TICKET, D_TICKET);
		//Log.i(TAG, "getTicket- ticket = " + stTicket);
		return stTicket;
	}

	
	public static void close() {
	
		acdcAPI = null;
	}

	public void serverMode(int iServerMode) {

		if (iServerMode < DEV_MODE || iServerMode > OTHER_MODE) {
			return;
		}
		final SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		final Editor editor = preferences.edit();
		
			editor.putInt(COMMUICATION_SERVER_MODE, iServerMode);	
		
			 Log.i(TAG,"Set serverMode:"+iServerMode);
		editor.commit();
	}

	public int getServerMode() {
		final SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		int iServerMode = preferences.getInt(COMMUICATION_SERVER_MODE,
				PROD_MODE);
		
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
		case WOLVERINE_MODE:
			stServerName = WOLVERINE_DOMAIN_NAME;
			break;
		case OTHER_MODE:
                   stServerName = getOtherServerDomainName();
                   break;
		default:
			break;
		}
		return stServerName;
	}
	public void storeOtherServerDomainName(final String stOtherServerAddress){
	   int colonIndex=-1;
	   if(stOtherServerAddress == null || (colonIndex=stOtherServerAddress.indexOf(":") ) == -1){
	      return;
	   }
	   storeOtherServerAddress(stOtherServerAddress);
	   final String stProtocal=stOtherServerAddress.substring(0,colonIndex);
	   storeOtherServerProtocal(stProtocal);
	   int iLastSlash=stOtherServerAddress.lastIndexOf("/");
	    String stOtherServerDomainName= null;
	    if(iLastSlash == -1 || iLastSlash != stOtherServerAddress.length() ){
		      iLastSlash=stOtherServerAddress.length();
		   }
		   if(colonIndex + 3 < iLastSlash){
			   stOtherServerDomainName=stOtherServerAddress.substring(colonIndex+3,iLastSlash);
		   }
	   
	   storePreferenceValue(OTHER_SERVER_DOMAIN, stOtherServerDomainName);
	   
	}
	public String getOtherServerAddress(){
	 
	   return getPreferenceValue(OTHER_SERVER_ADDRESS, "");
	}
	public  String getOtherServerDomainName(){
           return getPreferenceValue(OTHER_SERVER_DOMAIN, OTHER_DOMAIN_NAME);
        }
        private void storeOtherServerAddress(final String stOtherServerAddress){
           storePreferenceValue(OTHER_SERVER_ADDRESS, stOtherServerAddress);
        }
	private void storeOtherServerProtocal(final String stProtocal){
	   storePreferenceValue(OTHER_SERVER_PROTOCAL, stProtocal);
	         
	}
	private String getOtherServerProtocal(){
	   
	   return getPreferenceValue(OTHER_SERVER_PROTOCAL, HTTP);
	}
	public boolean isDemoServer(){
		final int iServerMode = getServerMode();
		return iServerMode == DEMO_MODE;
	}
			
	
	
	
	private static final String USER_NAME = "user_name";

	private static final String PASS_WORD = "pass_word";
	
	private static final String ORGANIZATION_ID = "organization";

	public void storeLoginInfo(final String stUserName,final  String stPassword,
	      final  String stOrganizationID) {
		final SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		final Editor editor = preferences.edit();
		editor.putString(USER_NAME, stUserName);
		editor.putString(PASS_WORD, stPassword);
		editor.putString(ORGANIZATION_ID, stOrganizationID);
		//Log.i(TAG, "storeLoginInfo-Username=" + stUserName + " password= " + stPassword+ " OrganizationID="+stOrganizationID);
		// Sending side
/*		byte[] data;
		String base64=null;
		try {
			data = stPassword.getBytes("UTF-8");
			 base64 = Base64.encodeToString(data, Base64.DEFAULT);
			// Receiving side
//			 data = Base64.decode(base64, Base64.DEFAULT);
//			 String text = new String(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
*/		
		Log.i(TAG, "storeLoginInfo-Username=" + stUserName +", OrganizationID="+stOrganizationID);
		editor.commit();
	}
	public String getUserName() {
           final String username = getPreferenceValue(USER_NAME, null);
           Log.i(TAG, "getUserName-Username=" + username);
           return username;
	}
	public void storeApplicationIndentity(final String stToken){
	   storePreferenceValue(APP_INDENTITY_TICKET, stToken);
	}
	public String getApplicationIndentityToken(){
	   return getPreferenceValue(APP_INDENTITY_TICKET,  null);
	}
	public boolean registerApplicationIdentity(final ApplicationIndentityRequest indentityRequest) throws UnknownHostException, IOException{
	   return indentityServiceAPI.getApplicationIndetityToken(this, indentityRequest);
	}
	
	private String getPreferenceValue(final String stKey, final String stDefaultValue){
	   final SharedPreferences preferences = PreferenceManager
                 .getDefaultSharedPreferences(context);
	   final String stData = preferences.getString(stKey, stDefaultValue);
	   return stData;
	}
   private void storePreferenceValue(final String stKey, final String stValue) {
      final SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(context);
      final Editor editor = preferences.edit();
      editor.putString(stKey, stValue);
      editor.commit();
   }
	public void storeOrganizationID(
              final  String stOrganizationID) {
	   storePreferenceValue(ORGANIZATION_ID, stOrganizationID);
	      Log.i(TAG, "storeOrgnizationInfo-=" + stOrganizationID);
 
        }
	public String getOrganizationID() {
           final String stOrganizationID = getPreferenceValue(ORGANIZATION_ID, null);
           Log.i(TAG, "getOrganization-Organization=" + stOrganizationID);
           return stOrganizationID;
        }
	private LoginRequest getLoginInfo() {
		LoginRequest loginRequest = null;
		final SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		final String username = preferences.getString(USER_NAME, null);
		final String password = preferences.getString(PASS_WORD, null);
		final String organizationID = preferences.getString(ORGANIZATION_ID, null);
		final String applicationName = "LegacyFeatures";
		if (username != null && password != null) {
			loginRequest = new LoginRequest(username, password, applicationName,organizationID);
		}
		return loginRequest;
	}

	public void logOut() {
		final SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		final Editor editor = preferences.edit();
		editor.putString(USER_NAME, null);
		editor.putString(PASS_WORD, null);
		editor.putString(ORGANIZATION_ID, null);
		storeTicket(null);
		editor.commit();
		Log.i(TAG, "Logged out");
	}
	
	public boolean islogOut() {
		boolean isLogout = true;
		final SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		final String username = preferences.getString(USER_NAME, null);
		final String password = preferences.getString(PASS_WORD, null);

		if (username != null && password != null && getTicket() != null) {
			isLogout = false;
		}

		return isLogout;
	}
	
	
	
      public Context getContext() {
         return context;
      }
      public String getProtoCal(){
         String stProtoCal=HTTP;
         final int iServerMode=getServerMode();
         switch (iServerMode) {
            case REL_MODE:
            case PROD_MODE:
            case WOLVERINE_MODE:
               stProtoCal=HTTPS;
               break;
            case OTHER_MODE:
               stProtoCal=getOtherServerProtocal();
               break;
            default:
               break;
         }
         return stProtoCal;
         
      }
      
      
      
      public int getAppDefalutServerMode(final boolean isDevMode){
         int iServerMode=ACDCApi.PROD_MODE;
         if(isDevMode){
            iServerMode=ACDCApi.REL_MODE;   
         }
         return iServerMode;
      }
    
      public User getCurrentUser() {
         User user = null;
         final SharedPreferences preferences = PreferenceManager
                         .getDefaultSharedPreferences(context);
         final String username = preferences.getString(USER_NAME, null);
         final String organization = preferences
                         .getString(ORGANIZATION_ID, null);
        final  ATSContentProvdier contentProvider = ATSContentProvdier
                         .getInstance(context);
         user = contentProvider.getUserInfo(username, organization);
         return user;
 }      
     
}

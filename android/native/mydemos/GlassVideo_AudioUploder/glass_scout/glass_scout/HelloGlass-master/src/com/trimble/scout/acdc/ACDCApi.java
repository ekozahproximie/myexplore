
package com.trimble.scout.acdc;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.security.MD5;
import com.trimble.agmantra.utils.Utils;
import com.trimble.scout.ScoutActivity;
import com.trimble.scout.acdc.exception.FileNameException;
import com.trimble.scout.acdc.exception.InvalidResponseException;
import com.trimble.scout.acdc.exception.RegsitrationException;
import com.trimble.scout.acdc.exception.TicketException;
import com.trimble.scout.acdc.request.RegistrationRequest;
import com.trimble.scout.acdc.response.RegistrationResponse;
import com.trimble.scout.acdc.response.UploadDeviceDataFileRes;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

/**
 * The Class ACDCApi.
 */
public class ACDCApi {
    
    public static final String TAG="ACDC";
  //Development Environment
    public static final String DEV_DOMAIN_NAME = "dev-integrations.myconnectedfarm.com";
    
   // Production environment
   // private static final String PRODUCTION_DOMAIN_NAME = "myconnectedfarm.com";
   
   //Production environment
    public static final String PRODUCTION_DOMAIN_NAME = "integrations.myconnectedfarm.com";
   
   //test environment
    public static final String TEST_DOMAIN_NAME = "tst-integrations.myconnectedfarm.com";
   
   //Release environment
    public static final String RELEASE_DOMAIN_NAME = "rel-integrations.myconnectedfarm.com";
    
    //Demo environment
    public static final String DEMO_DOMAIN_NAME ="demo-local.myconnectedfarm.com";
    
    private static final String COMMUICATION_SERVER_MODE = "communcation_server";

    public static final int DEV_MODE = 1;

    public static final int TEST_MODE = 2;

    public static final int REL_MODE = 3;

    public static final int PROD_MODE = 4;
    
    public static final int DEMO_MODE = 5;
     
    private static final String APP_MODE = "devmode";

    
   
	 //Production environment
   // private static final String DOMAIN_NAME = "myconnectedfarm.com";

    private static final String REGISTRATION_URL = "http://%s/DataCollection/v1/Devices";

    private static final String LOG_CRASH_REPORT_URL = "http://%s/DataCollection/v1/Reports";

    private static final String DEVICE_FILE_URL = "http://%s/DataCollection/v1/Files/%s";

    private static final String DEVICE_FILE_UPLOAD_URL = "http://%s/DataCollection/v1/Files";

    private static final String ASSOCIATIONS_DEVICE_URL = "http://%s/DataCollection/v1/Association";

    private static final String ASSOCIATIONS_EMAIL_URL = "http://%s/DataCollection/v1/AssociationEmails";

    private static String TO_DEVICE_FILE_PATH = "/sdcard/AgMantra/ToDevice";

    public static final String TICKET_PARAM = "ticket=";

    public static final String FILENAME_PARAM = "filename=";

    public static final String DEVICES = "Devices";

    public static final String FILES = "Files";

    public static final String ASSOCIATIONEMAILS = "AssociationEmails";

    public static final String REPORTS = "Reports";

    public static final String PACKAGENAME = "com.trimble.scout";
    
    public static final int UNKNOWN_ASSCOCIATION_STATUS=-1;
    
    public static final int EMAIL_NOT_SENT=0;
   
    public static final int EMAIL_SENT__DEVICE_NOT_ASSOCIATED=1;
    
    public static final int EMAIL_SENT__DEVICE_ASSOCIATED=2;
    
    public static final int D_ASSOCIATION_STATUS=UNKNOWN_ASSCOCIATION_STATUS;
    public static final String UNKNOWN_ASSCOCIATION_STATUS_DESC="UNKNOWN_ASSCOCIATION_STATUS";
    
    public static final String EMAIL_NOT_SENT_DESC="EMAIL_NOT_SENT";
    
    public static final String EMAIL_SENT__DEVICE_NOT_ASSOCIATED_DESC="EMAIL_SENT__DEVICE_NOT_ASSOCIATED";
    
    public static final String EMAIL_SENT__DEVICE_ASSOCIATED_DESC="EMAIL_SENT__DEVICE_ASSOCIATED";
    
    public static final String SERVER_ADDRESS = "serveraddress";
    
    public static final String SERVER_NAME = "servername";
      
    public String stDeviceId = null;

    public String stAccesKey = null;

    public String stDeviceSerialNo = null;

    public String stDeviceName = null;

    public String stDeviceType = "Farm Works Mate";

    public String stProductName = null;

    public String stProductType = "Android";

    public String stSoftVersion = null;

    public String stSoftBuildNo = null;

    private RegistrationRequest registrationRequest = null;

    public static final String SLASH = "/";

    private Context context = null;

    private final static String TICKET = "ticket";
    
    private final static String ASSOCIATION="association";
    
    private final static String LAST_KEY_GET_TIME="lasttime";
    
    private final static String ACDC1="ACDC1";
    
    private final static String ENCODING_UTF_8="UTF-8"; 
    
    private static final String APP_FIRST_LAUNCH="app_first_launch";
    
    
    private long currentTime=-1;
	private String stEmailID;
    
    private final static String D_TICKET = null;
    
  

    private static ACDCApi acdcAPI = null;

  
    
    public static synchronized ACDCApi getInstance(Context context) {
        if (acdcAPI == null) {
            acdcAPI = new ACDCApi(context);
            Log.i(TAG," ACDC-Instance Created");
        }
        return acdcAPI;
    }

    private void storeTicket(String stTicket) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = preferences.edit();
        editor.putString(TICKET, stTicket);
        java.util.Date date = new  java.util.Date();
        long lCurrentTime=date.getTime();
        editor.putLong(LAST_KEY_GET_TIME, lCurrentTime);
        Log.i(TAG,"Storing Ticket :"+stTicket+" with time:"+date);
        editor.commit();
    }

    public void storeAssociationStatus(int iAssocStatus) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = preferences.edit();
        editor.putInt(ASSOCIATION, iAssocStatus);
        Log.i(TAG,"Storing Association Status :"+iAssocStatus);
        editor.commit();
    }
    
    public int getAssociationStatus() {
        int iAssocStatus =-1;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        iAssocStatus = preferences.getInt(ASSOCIATION, D_ASSOCIATION_STATUS);
        Log.i(TAG,"Retrieving Association Status:"+iAssocStatus+"-"+getAssociationStatusDesc(iAssocStatus));
         return iAssocStatus;
    }
    
    
    public String getTicket() {
        String stTicket = null;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
         stTicket = preferences.getString(TICKET, D_TICKET);
         return stTicket;
    }
    
    private ACDCApi(Context context) {
        this.context = context;
    
       // changeServer();
     
        java.util.Date date = new Date();
        currentTime=date.getTime();
        final String androidId;
        final String EMPTY = "";
        //tmDevice = EMPTY + tm.getDeviceId();
        //tmSerial = EMPTY + tm.getSimSerialNumber();
        androidId = "357965055822988";
      

        stDeviceId = "00000000-4ede-db2f-3e4c-312e00000001";//deviceUuid.toString();
        stDeviceSerialNo = stDeviceId;
        stDeviceName = android.os.Build.MODEL;
        // stDeviceType = android.os.Build.PRODUCT;

        try {
            if (null != stDeviceId) {

                stAccesKey = ComputeAccessKey(stDeviceId);
            }
            Log.i(TAG, " DeviceId: " + stDeviceId + "\n AccesKey: " + stAccesKey);

        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.toString());
        }
        final String UNKNOWN = "(unknown)";
        final PackageManager pm = context.getPackageManager();
        if (pm != null) {
            ApplicationInfo ai = null;
            PackageInfo pi = null;

            try {
                ai = pm.getApplicationInfo(PACKAGENAME, 0);
                pi = pm.getPackageInfo(PACKAGENAME, 0);
            } catch (NameNotFoundException e) {
               Log.e(TAG, e.toString());
                ai = null;
            }

            stProductName = (String)(ai != null ? pm.getApplicationLabel(ai) : UNKNOWN);

            stSoftBuildNo = (String)(pi != null ? pi.versionName : UNKNOWN);
            stSoftVersion = (String)(pi != null ? pi.versionName : UNKNOWN);
            if (registrationRequest == null) {
                registrationRequest = new RegistrationRequest();

                registrationRequest.stDeviceID = stDeviceId;
                registrationRequest.stAccessKey = stAccesKey;
                registrationRequest.stDeviceName = stDeviceName;
                registrationRequest.stDeviceSerialNumber = stDeviceSerialNo;
                registrationRequest.stDeviceType = stDeviceType;
                registrationRequest.stProductName = stProductName;
                registrationRequest.stProductType = stProductType;
                registrationRequest.stSoftwareVersion = stSoftVersion;
                registrationRequest.stSoftwareBuildNumber = stSoftBuildNo;
            }
        }
        checkProductionMode(context);
        ScoutActivity.isAPP_DevMode(context); 
    }
    private boolean isDomainNameEmpty(){
       final String stDomainName=getDomainName();
       return stDomainName == null || (getDomainName().trim().length() == 0 );
    }

    /**
     * Sends Device Registrations details to the ACDC Mainly used to obtain a
     * session ticket when a valid Access Key is provided that is to be used for
     * subsequent method calls. This method can also be used to update certain
     * volatile device parameters, like software version. The device will be
     * registered by ACDC with a POST request with <b>Content-Type:</b>
     * Application/JSON.
     * 
     * 
     * @throws IllegalArgumentException the illegal argument exception
     * @throws UnknownHostException the unknown host exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws RegsitrationException the registration exception
     */
    public synchronized void registration() throws UnknownHostException, IOException,
            RegsitrationException {
        
        
        final int RESPONSE_CODE = 201;
        JsonClient client = new JsonClient(context);
        if(isDomainNameEmpty()){
           Log.i(TAG, "Registration URL Domain Name is empty");
           return ;
        }
        String stRegistrarionURL = String.format(REGISTRATION_URL, getDomainName());
        Log.i(TAG, "Registration URL:"+stRegistrarionURL);
        final ACDCRequest acdcRequest = new ACDCRequest(stRegistrarionURL,
              registrationRequest.getJsonString(), ACDCRequest.POST,ACDCRequest.CONTENTTYPE_JSON, null);
        final ACDCResponse acdcResponse= client.connectHttp(acdcRequest);
        try {
            // "{\"AccessKey\":\"p2dQwIohWQ0OYNA\\/Mt5tlg==\",\"DeviceID\":\"adc283e4-cb46-4ebe-8c8f-954d7fd02b70\",\"DeviceName\":\"sts\",\"DeviceSerialNumber\":\"stResp\",\"DeviceType\":\"HTC Flyer P512\",\"ProductName\":\"ConnectedFarmMobile\",\"ProductType\":\"Android\",\"SoftwareBuildNumber\":\"stst\",\"SoftwareVersion\":\"0.5\"}"
            String stData = client.convertByteArrayToString(acdcResponse.resData);
            if (stData != null) {
                RegistrationResponse registrationResponse = new RegistrationResponse();
                registrationResponse.readResponse(stData);

                Log.i(TAG,"Registration-Responsecode:"+acdcResponse.iResponseCode+",response:"+stData);
                if (RESPONSE_CODE != acdcResponse.iResponseCode) {
                    RegsitrationException exception = new RegsitrationException(
                            registrationResponse.stErrorCode, registrationResponse.stMeaning);
                    Log.i(TAG,"Registration Exception-ErrorCode:"+registrationResponse.stErrorCode+";Meaning:"+registrationResponse.stMeaning);
                    throw exception;
                } else {
                    storeTicket(registrationResponse.stTicket);
                }
               
               
            }
        } finally {
            
        }
    }

    /**
     * sends the collected Device Data files from mobile to ACDC
     * 
     * @param stFileName the stfilename
     * @param data the data
     * @return the string
     * @throws TicketException the ticket exception
     * @throws FileNameException the file name exception
     * @throws UnknownHostException the unknown host exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InvalidResponseException 
     */
    public String uploadFileData(String stFilePath) throws TicketException, FileNameException,
            UnknownHostException, IOException, InvalidResponseException {
        if (stFilePath == null) {
            throw new IllegalArgumentException("Filepath string should not be null");
        }
        if(isDomainNameEmpty()){
           Log.i(TAG, "File Uplaod Domain Name is empty");
           return null;
        }
        byte [] resData = null;
        String stFileId = null;
        JsonClient client = null;
        try {
            File file = new File(stFilePath);
            if (!file.exists()) {
                Log.e(TAG, "file not found on the underlying path" + stFilePath);
                throw new FileNameException( "file not found on the underlying path" + stFilePath);

            }
            if (!file.canRead()) {
                Log.e(TAG, "current context is not allowed to read from this file:" + stFilePath);
                throw new FileNameException( "can't Read file from this path" + stFilePath);
            }
          
            String stfileName = file.getName();
           
           String stTicket = getTicket();
            final int RESPONSE_CODE = 201;

            int iReturnResCode[] = {
                0
            };
            stfileName= URLEncoder.encode(stfileName,"UTF-8"); 
            String stUploadFileURL = String.format(DEVICE_FILE_UPLOAD_URL, getDomainName());
            // need to append in stringbuffer
            stUploadFileURL = stUploadFileURL + "?" + TICKET_PARAM + stTicket + "&"
                    + FILENAME_PARAM + stfileName;
            
            client = new JsonClient(context);
            Log.i(TAG, "File Uplaod request:"+stUploadFileURL);
            
            resData = client.connectHttp(stUploadFileURL, iReturnResCode, stFilePath);
            String stData = client.convertByteArrayToString(resData);
            if (stData != null) {
                UploadDeviceDataFileRes res = new UploadDeviceDataFileRes();
                res.readResponse(stData);
               
                Log.i(TAG,"File upload-Responsecode:"+iReturnResCode[0]+",response:"+stData);
                if (RESPONSE_CODE != iReturnResCode[0]) {
                	if(res.stErrorCode == null){
                		InvalidResponseException exception=new InvalidResponseException(InvalidResponseException.INVALID_RESPONSE,InvalidResponseException.INVALID_RESPONSE_MSG);
                		Log.e(TAG,"InvalidResponse Exception while uploading file-ErrorCode:"+res.stErrorCode);
                		throw exception;
                	}
                    if (res.stErrorCode.equals(TicketException.INVALIDTICKET)) {
                        TicketException exception = new TicketException(res.stErrorCode,
                                res.stMeaning);
                        Log.e(TAG,"TicketException while uploading file-ErrorCode:"+res.stErrorCode+";Meaning:"+res.stMeaning);
                        throw exception;
                    }
                } else {
                	if (res!=null) {
                		 stFileId = res.stFileId;
					}
                   
                }
                Log.i(TAG,"File upload:"+file.getAbsolutePath());
               
                
            }

        }

        finally {
        

          

        }
        return stFileId;
    }

   
    

    private String ComputeAccessKey(String stdeviceId) throws UnsupportedEncodingException {
        byte[] prefixBytes = new String(ACDC1).getBytes(ENCODING_UTF_8);

        UUID uuid = UUID.fromString(stDeviceId);

        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte)(msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte)(lsb >>> 8 * (7 - i));
        }

        byte[] bytesOriginal = buffer;
        byte[] bytes = new byte[16];

        // Reverse the first 4 bytes
        bytes[0] = bytesOriginal[3];
        bytes[1] = bytesOriginal[2];
        bytes[2] = bytesOriginal[1];
        bytes[3] = bytesOriginal[0];
        // Reverse 6th and 7th
        bytes[4] = bytesOriginal[5];
        bytes[5] = bytesOriginal[4];
        // Reverse 8th and 9th
        bytes[6] = bytesOriginal[7];
        bytes[7] = bytesOriginal[6];
        // Copy the rest straight up
        for (int i = 8; i < 16; i++) {
            bytes[i] = bytesOriginal[i];
        }
        byte[] deviceIDBytes = bytes;

        byte[] combined = new byte[prefixBytes.length + deviceIDBytes.length];
        System.arraycopy(prefixBytes, 0, combined, 0, prefixBytes.length);
        System.arraycopy(deviceIDBytes, 0, combined, prefixBytes.length, deviceIDBytes.length);

        MD5 md5 = new MD5(combined);
        byte[] hashed = md5.fingerprint(combined);
        String accessKey = MD5.toBase64(hashed);

        return accessKey;
    }

    public void doRegistration(NetWorkListener listener) {
        this.mNetWorkListener = listener;
        Thread thread =new Thread(mRunnable, "Registration thread");
        thread.start();
        //mRegistrationHandler.post(mRunnable);
    }

    

    private NetWorkListener mNetWorkListener = null;
    
    

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            try {
                if(! Utils.isInternetConnection(context)){
                    return;
                }
                registration();
                if (mNetWorkListener != null) {
                mNetWorkListener.requestSucesss(true, null);
                }
            } catch (UnknownHostException e) {
                if (mNetWorkListener != null) {
                    mNetWorkListener.requestSucesss(false, "UnknownHostException");
                }

            } catch (IOException e) {
                if (mNetWorkListener != null) {
                    mNetWorkListener.requestSucesss(false, "IOException");
                }

            } catch (RegsitrationException e) {
                if (mNetWorkListener != null) {
                    mNetWorkListener.requestSucesss(false, e.getMessage());
                }

            }
        }
    };

    public interface NetWorkListener {

        public void requestSucesss(boolean isSucess, String stErrorMsg);

    }
    
    public void close(){
        acdcAPI=null;
    }
    
    
    public boolean isKeyExpire(){
       
        boolean isKeyExpire=false;
        SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
        String stTicket=getTicket();
        if(stTicket == null){
            return true;
        }
        long lastKeyTime=preferences.getLong(LAST_KEY_GET_TIME, -1);
            if(currentTime > lastKeyTime){
                long diff=currentTime-lastKeyTime;

                final long timeInMillis = diff;

                final int days = (int) (timeInMillis / (24L * 60 * 60 * 1000));

                int remdr = (int) (timeInMillis % (24L * 60 * 60 * 1000));

                final int hours = remdr / (60 * 60 * 1000);

                remdr %= 60 * 60 * 1000;

                final int minutes = remdr / (60 * 1000);

                remdr %= 60 * 1000;

                final int seconds = remdr / 1000;

                final int ms = remdr % 1000;
                if(days >= 1 && seconds >= 1){
                    isKeyExpire=true;
                }
            }
      Log.i(TAG, "KeyExpiration Status:" + isKeyExpire);
            return isKeyExpire;
    }
    
    public void checkRegistrtionKeyExpire(NetWorkListener listener){
       
                if(isKeyExpire()){
                    if(listener != null){
                        doRegistration(listener);
                    }
                }else{
                    if(listener != null)
                    listener.requestSucesss(true, null);
                }
            
    }
	public String getStEmailID() {
		return stEmailID;
	}

	public void setStEmailID(String stEmailID) {
		this.stEmailID = stEmailID;
	}
	
	public String getAppName(){
		return stProductName;
	}
	public  synchronized void changeServer(){
	   boolean isDev=false;
	    if(isDev){
	       serverMode(DEV_MODE);
	    }else{
	       serverMode(PROD_MODE);
	    }
	}
	
	

	
	private String getAssociationStatusDesc(int iStatus){
	   String stDesc=null;
	   switch (iStatus) {
              case ACDCApi.UNKNOWN_ASSCOCIATION_STATUS:
                 stDesc=UNKNOWN_ASSCOCIATION_STATUS_DESC;
                 break;
              case ACDCApi.EMAIL_NOT_SENT:
                 stDesc=EMAIL_NOT_SENT_DESC;
                 break;
              case ACDCApi.EMAIL_SENT__DEVICE_NOT_ASSOCIATED:
                 stDesc=EMAIL_SENT__DEVICE_NOT_ASSOCIATED_DESC;
                 break;
              case ACDCApi.EMAIL_SENT__DEVICE_ASSOCIATED:
                 stDesc=EMAIL_SENT__DEVICE_ASSOCIATED_DESC;
                 break;
                 default:
                    break;
	   }
	   
	   return stDesc;
	}
	
	public void serverMode(int iServerMode) {

           if (iServerMode < DEV_MODE || iServerMode > DEMO_MODE) {
                   return;
           }
           final SharedPreferences preferences = PreferenceManager
                           .getDefaultSharedPreferences(context);
           final Editor editor = preferences.edit();
           
                   editor.putInt(COMMUICATION_SERVER_MODE, iServerMode);   
           
           
           editor.commit();
   }

   public int getServerMode() {
           final SharedPreferences preferences = PreferenceManager
                           .getDefaultSharedPreferences(context);
           int iServerMode = preferences.getInt(COMMUICATION_SERVER_MODE,
                           REL_MODE);
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
              stServerName = RELEASE_DOMAIN_NAME;
              break;
      case PROD_MODE:
              stServerName = PRODUCTION_DOMAIN_NAME;
              break;
      case DEMO_MODE:
              stServerName = DEMO_DOMAIN_NAME;
              break;
      default:
              break;
      }
      return stServerName;
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
            final PackageManager pm = context.getPackageManager();
            try {
               final ApplicationInfo info = pm.getApplicationInfo(
                     context.getPackageName(), PackageManager.GET_META_DATA);
               if (info.metaData != null) {
                  final boolean isDevMode = info.metaData.getBoolean(APP_MODE);
                  setAPPMode(isDevMode);
                  if (Constants.IS_DEV_BUILD) {
                     Log.i(TAG, "Dev mode in manifest");
                  } else {
                     Log.i(TAG, "Production mode in manifest");
                  }
               } else {
                  Log.e(TAG, "dev mode meta data does in the manifest.xml");
               }

            } catch (final NameNotFoundException e) {
               Log.e(TAG, e.getMessage(), e);
            }
            final Editor editor =preferences.edit();
            editor.putBoolean(APP_FIRST_LAUNCH, false);
            editor.commit();
         }
         
   }
   public void setAPPMode(final boolean isDevMode){
      Constants.IS_DEV_BUILD=isDevMode;
      ScoutActivity.modifyDevModePreference(context);
      if(isDevMode){
         serverMode(ACDCApi.DEV_MODE);   
      }else{
         serverMode(ACDCApi.REL_MODE);
      }
   }

}

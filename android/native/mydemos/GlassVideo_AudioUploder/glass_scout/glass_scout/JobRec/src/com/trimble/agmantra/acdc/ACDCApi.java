
package com.trimble.agmantra.acdc;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.trimble.agmantra.acdc.exception.FileNameException;
import com.trimble.agmantra.acdc.exception.InvalidResponseException;
import com.trimble.agmantra.acdc.exception.RegsitrationException;
import com.trimble.agmantra.acdc.exception.TicketException;
import com.trimble.agmantra.acdc.request.DeviceAssnReq;
import com.trimble.agmantra.acdc.request.GetDeviceFileReq;
import com.trimble.agmantra.acdc.request.LogCrashReportReq;
import com.trimble.agmantra.acdc.request.PendingDeviceFileReq;
import com.trimble.agmantra.acdc.request.RegistrationRequest;
import com.trimble.agmantra.acdc.response.DeviceAssnRes;
import com.trimble.agmantra.acdc.response.EmailDeviceAssnRes;
import com.trimble.agmantra.acdc.response.GetDeviceAssociationStatusRes;
import com.trimble.agmantra.acdc.response.LogCrashReportRes;
import com.trimble.agmantra.acdc.response.PendingDeviceFileRes;
import com.trimble.agmantra.acdc.response.RegistrationResponse;
import com.trimble.agmantra.acdc.response.UploadDeviceDataFileRes;
import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.dbutil.Log;
import com.trimble.agmantra.security.MD5;
import com.trimble.agmantra.utils.Utils;

import org.apache.http.Header;

import java.io.File;
import java.io.FileOutputStream;
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

    public static final String PACKAGENAME = "com.trimble.agmantra";
    
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
    
        changeServer();
     
        final TelephonyManager tm = (TelephonyManager)context
                .getSystemService(Context.TELEPHONY_SERVICE);
        java.util.Date date = new Date();
        currentTime=date.getTime();
        final String tmDevice,androidId;
        final String EMPTY = "";
        tmDevice = EMPTY + tm.getDeviceId();
        //tmSerial = EMPTY + tm.getSimSerialNumber();
        androidId = EMPTY
                + android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) |tm.getPhoneType());

        stDeviceId = deviceUuid.toString();
        stDeviceSerialNo = stDeviceId;
        stDeviceName = android.os.Build.MODEL;
        // stDeviceType = android.os.Build.PRODUCT;
        //androidId = "357965055822988";
        
        // sumsung s4 mini device id it is associated to karthiga account
        stDeviceId = "00000000-4ede-db2f-3e4c-312e00000001";//deviceUuid.toString();
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

    /**
     * Request pending list.
     * 
     * @param timestamp the timestamp
     * @return the file list
     * @throws TicketException the ticket exception
     * @throws UnknownHostException the unknown host exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InvalidResponseException 
     */
    public FileList requestPendingList(String timestamp, String stTODevicePath)
            throws TicketException, UnknownHostException, IOException, InvalidResponseException {

        TO_DEVICE_FILE_PATH = stTODevicePath;

        if (timestamp == null) {
            throw new IllegalArgumentException("Invalid TimeStamp");
        }
        if(isDomainNameEmpty()){
           Log.i(TAG, "requestPendingList Domain Name is empty");
           return null;
        }
        final int RESPONSE_CODE = 200; // HttpStatus.SC_OK;
        JsonClient client = new JsonClient(context);
        String stPendingListURL = String.format(DEVICE_FILE_URL, getDomainName());
        if(timestamp != null){
            try{
                long lTimeMilli=Long.parseLong(timestamp);
                timestamp=Utils.getDate_ISO8601(lTimeMilli);
            }catch (NumberFormatException e) {
                // TODO: handle exception
            }
        }
        PendingDeviceFileReq req = new PendingDeviceFileReq();
        req.stTicket = getTicket();
        req.stTimestamp_UTC = timestamp;
        Log.i(TAG, "PendingList Request URL:"+stPendingListURL);
        final ACDCRequest acdcRequest =new ACDCRequest(
              stPendingListURL,  req.getJsonString(),
              ACDCRequest.GET,ACDCRequest.CONTENTTYPE_JSON,null);
        final ACDCResponse acdcResponse = client.connectHttp(acdcRequest);

        try {
            String stData = client.convertByteArrayToString(acdcResponse.resData);
            if (stData != null) {
                PendingDeviceFileRes res = new PendingDeviceFileRes();
                res.readResponse(stData);
                Log.i(TAG, "Pending List-Responsecode:"+acdcResponse.iResponseCode+",response:"+stData);
                
                if (RESPONSE_CODE != acdcResponse.iResponseCode) {
                	if(res.stErrorCode == null){
                		InvalidResponseException exception=new InvalidResponseException(InvalidResponseException.INVALID_RESPONSE,InvalidResponseException.INVALID_RESPONSE_MSG);
                		Log.e(TAG,"InvalidResponse Exception while requesting pending list-ErrorCode:"+res.stErrorCode);
                		throw exception;
                	}
                    if (res.stErrorCode.equals(TicketException.INVALIDTICKET)) {
                        TicketException exception = new TicketException(res.stErrorCode,
                                res.stMeaning);
                        Log.e(TAG,"TicketException while requesting pending list-ErrorCode:"+res.stErrorCode+";Meaning:"+res.stMeaning);
                        throw exception;
                    }
                }
               
            }
        } finally {

        }

        return null;
    }

    /**
     * downloads the files from ACDC.
     * 
     * @param stFileId the st file id
     * @return the file
     * @throws TicketException the ticket exception
     * @throws FileNameException the file name exception
     * @throws UnknownHostException the unknown host exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String downloadFile(String stFileId) throws TicketException, FileNameException,
            UnknownHostException, IOException {
        byte[] bData = null;
        if (stFileId == null) {
            throw new IllegalArgumentException("FileID string should not be null");
        }
        if(isDomainNameEmpty()){
           Log.i(TAG, "downloadFile Domain Name is empty");
           return null;
        }
        GetDeviceFileReq req = new GetDeviceFileReq();
        req.stTicket = getTicket();
        req.stFileId = stFileId;
        final int SUCCESS_RESPONSE_CODE = 200;
        String stGetFileURL = String.format(DEVICE_FILE_URL, getDomainName(), stFileId);
        JsonClient client = new JsonClient(context);

       
        Log.i(TAG, "DownloadFile Request URL:"+stGetFileURL);
        final ACDCRequest  acdcRequest = new ACDCRequest(
              stGetFileURL, req.getQueryString(),
              ACDCRequest.GET,ACDCRequest.CONTENTTYPE_JSON,null,true);
        
        final ACDCResponse acdcResponse= client.connectHttp(acdcRequest);

        if (SUCCESS_RESPONSE_CODE != acdcResponse.iResponseCode) {

            TicketException exception = new TicketException(TicketException.INVALIDTICKET);
            throw exception;
        }
        String stContentType = null;
        String stContentDisposition = null;
        String stContentLength = null;
        /**
         * Content-Type:zip Content-Disposition: attachment; filename=<FileName>
         * Content-Length: <Byte Count>
         */
        File file = new File(TO_DEVICE_FILE_PATH);
        FileOutputStream fileOutputStream = null;
        file.mkdirs();

        try {
            if (acdcResponse.resData != null && acdcResponse.vecResponseHeader != null) {
                // /http://stackoverflow.com/questions/4898527/how-can-i-read-a-binary-file-from-a-socket-input-stream-which-includes-textual-h
                Header responseHeader[] = acdcResponse.vecResponseHeader.firstElement();
                if (responseHeader != null) {
                    for (Header header : responseHeader) {
                        if (header.getName().equals("Content-Type")) {
                            stContentType = header.getValue();
                        } else if (header.getName().equals("Content-Disposition")) {
                            stContentDisposition = header.getValue();
                        } else if (header.getName().equals("Content-Length")) {
                            stContentLength = header.getValue();
                        }

                        if (stContentDisposition != null && stContentType != null
                                && stContentLength != null) {
                            responseHeader = null;
                            acdcResponse.vecResponseHeader.clear();
                            break;
                        }
                    }
                }
                int iContentLen = 0;
                if (stContentLength != null) {
                    iContentLen = Integer.parseInt(stContentLength);
                }
                int iEqualPos = stContentDisposition.indexOf("=");

                if (iEqualPos != -1) {

                    String stFileName = stContentDisposition.substring(iEqualPos);
                    file = new File(file.getAbsolutePath() + File.separator + stFileName);
                    fileOutputStream = new FileOutputStream(file);
                    int size = acdcResponse.resData.length;
                    
                    int iReadlen = acdcResponse.resData.length;
                    
                     fileOutputStream.write(acdcResponse.resData, 0, size);
                       
                    fileOutputStream.flush();
                    if (iReadlen != iContentLen) {
                        // unable to download exception
                       Log.e(TAG,"File Download:unable to download");
                    }
                }

            }

        } finally {
            
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }

        return file.toString();

    }

    public boolean sendLogCrashReport(String stReport) throws TicketException,
            UnknownHostException, IOException {
        if (stReport == null) {
            throw new IllegalArgumentException("Report string should not be null");
        }
        if(isDomainNameEmpty()){
           Log.i(TAG, "sendLogCrashReport Domain Name is empty");
           return false;
        }
        LogCrashReportReq crashReportReq = new LogCrashReportReq();
        crashReportReq.stReport = stReport;

        final int SUCCESS_RESPONSE_CODE = 201;
        JsonClient client = new JsonClient(context);
        String stLogCrashReportURL = String.format(LOG_CRASH_REPORT_URL, getDomainName());
        stLogCrashReportURL = stLogCrashReportURL + "?" + TICKET_PARAM + getTicket();
        Log.i(TAG, "LogCrashReport URL:"+stLogCrashReportURL);
        final ACDCRequest acdcRequest = new ACDCRequest(stLogCrashReportURL,
              crashReportReq.getJsonString(), ACDCRequest.POST,ACDCRequest.CONTENTTYPE_JSON,null);
        final ACDCResponse acdcResponse = client.connectHttp(acdcRequest);
        try {

            String stData = client.convertByteArrayToString(acdcResponse.resData);
            if (stData != null) {
                LogCrashReportRes crashReportRes = new LogCrashReportRes();
                crashReportRes.readResponse(stData);
                Log.i(TAG, "LogCrashReport-Responsecode:"+acdcResponse.iResponseCode+",response:"+stData);
                if (SUCCESS_RESPONSE_CODE != acdcResponse.iResponseCode) {
                    if (crashReportRes.stErrorCode  != null
                            && crashReportRes.stErrorCode.equals(TicketException.INVALIDTICKET)) {
                        TicketException exception = new TicketException(crashReportRes.stErrorCode,
                                crashReportRes.stMeaning);
                        Log.e(TAG,"Ticket Exception  while sending crash report-ErrorCode:"+crashReportRes.stErrorCode+";Meaning:"+crashReportRes.stMeaning);
                        throw exception;
                    }
                }
               
               
                return true;
            }
            return false;
        } finally {
          
        }
    }

    public boolean associateDevice(String stOrgID) throws TicketException, UnknownHostException,
            IOException, InvalidResponseException {

        if (stOrgID == null) {
            throw new IllegalArgumentException("OrgID string should not be null");
        }
        if(isDomainNameEmpty()){
           Log.i(TAG, "associateDevice Domain Name is empty");
           return false;
        }
        DeviceAssnReq req = new DeviceAssnReq();
        String stTicket = getTicket();
        req.stOrgID = stOrgID;

        final int SUCCESS_RESPONSE_CODE = 201;
        JsonClient client = new JsonClient(context);
        String stAssociateDeviceURL = String.format(ASSOCIATIONS_DEVICE_URL, getDomainName());
        stAssociateDeviceURL = stAssociateDeviceURL + "?" + TICKET_PARAM + stTicket;
        Log.i(TAG, "AssociateDeviceURL:"+stAssociateDeviceURL);
        final ACDCRequest acdcRequest = new ACDCRequest(stAssociateDeviceURL,
                req.getJsonString(), ACDCRequest.POST,ACDCRequest.CONTENTTYPE_JSON,null); 
        final ACDCResponse acdcResponse= client.connectHttp(acdcRequest);
        try {

            String stData = client.convertByteArrayToString(acdcResponse.resData);
            if (stData != null) {
                DeviceAssnRes res = new DeviceAssnRes();
                res.readResponse(stData);
                Log.i(TAG, "AssociateDevice-Responsecode:"+acdcResponse.iResponseCode+",response:"+stData);
                if (SUCCESS_RESPONSE_CODE != acdcResponse.iResponseCode) {
                	if(res.stErrorCode == null){
                		InvalidResponseException exception=new InvalidResponseException(InvalidResponseException.INVALID_RESPONSE,InvalidResponseException.INVALID_RESPONSE_MSG);
                        throw exception;
                	}
                    if (res.stErrorCode.equals(TicketException.INVALIDTICKET)) {
                        TicketException exception = new TicketException(res.stErrorCode,
                                res.stMeaning);
                        Log.e(TAG,"TicketException while associating device-ErrorCode:"+res.stErrorCode+";Meaning:"+res.stMeaning);
                        throw exception;
                    }
                }
                
              

                return true;
            }
            return false;
        } finally {
           
        }

    }

    public boolean associateEmail(String stEmailID) throws TicketException, UnknownHostException,
            IOException, InvalidResponseException {
    	boolean isEmailAssociated=false;
        if (stEmailID == null) {
            throw new IllegalArgumentException("EmailID string should not be null");
        }
        if(isDomainNameEmpty()){
           Log.i(TAG, "associateEmail Domain Name is empty");
           return false;
        }
        String stTicket = getTicket();
      

        final int SUCCESS_RESPONSE_CODE = 201;
        JsonClient client = new JsonClient(context);
        String stAssociateEmailURL = String.format(ASSOCIATIONS_EMAIL_URL, getDomainName());
        
        stAssociateEmailURL = stAssociateEmailURL + "?" + TICKET_PARAM + stTicket+"&email="+stEmailID;
        Log.i(TAG, "AssociateEmailURL:"+stAssociateEmailURL);
        
        final ACDCRequest request = new ACDCRequest(stAssociateEmailURL, 
               null, ACDCRequest.POST,ACDCRequest.CONTENTTYPE_PLAINTEXT,null);
        final ACDCResponse acdcResponse = client.connectHttp(request);
        try {

            String stData = client.convertByteArrayToString(acdcResponse.resData);
            if (stData != null) {
                EmailDeviceAssnRes res = new EmailDeviceAssnRes();
                res.readResponse(stData);
                Log.i(TAG, "AssociateEmail -Responsecode:"+acdcResponse.iResponseCode+",response:"+stData);
                if (SUCCESS_RESPONSE_CODE != acdcResponse.iResponseCode) {
                	if(res.stErrorCode == null){
                		InvalidResponseException exception=new InvalidResponseException(InvalidResponseException.INVALID_RESPONSE,InvalidResponseException.INVALID_RESPONSE_MSG);
                        throw exception;
                	}
                    if (res.stErrorCode.equals(TicketException.INVALIDTICKET)) {
                        TicketException exception = new TicketException(res.stErrorCode,
                                res.stMeaning);
                        Log.e(TAG,"TicketException while associating Email-ErrorCode:"+res.stErrorCode+";Meaning:"+res.stMeaning);
                        throw exception;
                    }
                }
                else{
                	if (res!=null) {
                		isEmailAssociated=res.isSuccess;
			}
                }
               

               
            }
            return isEmailAssociated;
        } finally {
            
        }

    }

    public int checkDeviceAssnStatus() throws TicketException, UnknownHostException,
            IOException, InvalidResponseException {
        int iStatus=0;
       String stTicket = getTicket();
       GetDeviceAssociationStatusRes res=null;
        final int SUCCESS_RESPONSE_CODE = 200;
        if(isDomainNameEmpty()){
           Log.i(TAG, "checkDeviceAssnStatus Domain Name is empty");
           return -1;
        }
        JsonClient client = new JsonClient(context);
        String stAssociateDeviceURL = String.format(ASSOCIATIONS_DEVICE_URL, getDomainName());
        stAssociateDeviceURL = stAssociateDeviceURL + "?" + TICKET_PARAM + stTicket;
        Log.i(TAG, "AssociateDeviceURL:"+stAssociateDeviceURL);
        final ACDCRequest acdcRequest = new ACDCRequest(stAssociateDeviceURL,
              null, ACDCRequest.GET,ACDCRequest.CONTENTTYPE_JSON,null);
        final ACDCResponse acdcResponse = client.connectHttp(acdcRequest);
        try {

            String stData = client.convertByteArrayToString(acdcResponse.resData);
            if (stData != null) {
             
                 res = new GetDeviceAssociationStatusRes();
                res.readResponse(stData);
                Log.i(TAG, "DeviceAssociationStatus-Responsecode:"+acdcResponse.iResponseCode+",response:"+stData);
                if (SUCCESS_RESPONSE_CODE != acdcResponse.iResponseCode) {
                	if(res.stErrorCode == null){
                		InvalidResponseException exception=new InvalidResponseException(InvalidResponseException.INVALID_RESPONSE,InvalidResponseException.INVALID_RESPONSE_MSG);
                        throw exception;
                	}
                    if (res.stErrorCode.equals(TicketException.INVALIDTICKET)) {
                        TicketException exception = new TicketException(res.stErrorCode,
                                res.stMeaning);
                        Log.e(TAG,"TicketException while checking DeviceAssnStatus-ErrorCode:"+res.stErrorCode+";Meaning:"+res.stMeaning);
                        throw exception;
                    }
                }else{
                	if (res!=null) {
                		 iStatus=UNKNOWN_ASSCOCIATION_STATUS;
                		if(res.isSuccess){
                			if (res.stOrgName != null) {
                				iStatus=EMAIL_SENT__DEVICE_ASSOCIATED;
                			} else if (res.stEmail != null) {
                		        iStatus=EMAIL_SENT__DEVICE_NOT_ASSOCIATED;
                		        setStEmailID(res.stEmail);
                			} else {
                		        iStatus = EMAIL_NOT_SENT;
                		    }
                		}
                	}
                }
                
              

               
            }
           
        } finally {
            
        }
        storeAssociationStatus(iStatus);
        return iStatus;
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
	    if(Constants.IS_DEV_BUILD){
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

}

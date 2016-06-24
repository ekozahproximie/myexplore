package com.trimble.ag.ats.acdc;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.trimble.ag.ats.acdc.MySSLSocketFactory.TrustEveryoneManager;
import com.trimble.ag.ats.acdc.res.ACDCResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class JsonClient {

	// private static final String USER_AGENT = "Android/";

	// private static final String ZIP_CONTENT = "application/zip";

	public static final int POST = 0;
	public static final int GET = 1;
	public static final int PUT = 2;
	public static final int DELETE = 3;
	
         private static final int    JSON_CONTENT_TYPE       = 1;
      
         private static final int    PLAIN_TEXT_CONTENT_TYPE = 2;
      
         private static final int    URL_ENCODE_CONTENT_TYPE = 3;

	private static final String JSON_CONTENT = "application/json";

	private static final String PLAIN_TEXT_CONTENT = "text/plain";
	
	private static final String URL_ENCODE_CONTENT = "application/x-www-form-urlencoded";
	
	private static final String AUTH="Authorization";

	private final static int CONNECTION_TIMEOUT = 10 * 60 * 1000;

	private final static int CHUNK_SIZE = 4096;
	
	private final static String BEARER="Bearer:";

	private final static int CONNECTION_TIMEOUT_OKHTTP = 2 * 60;
	
	private final static int READ_TIMEOUT_OKHTTP = 5 * 60;
	
	   private static final String COOKIE_NAME        = "cookie_name";
	   private static final String COOKIE_VALUE       = "cookie_value";
	   private static final String COOKIE_VERSION       = "cookie_version";
	   private static final String COOKIE_EXPIRE       = "cookie_expire";
	   private static final String COOKIE_PATH        = "cookie_path";
	   private static final String COOKIE_DOMAIN_NAME = "cookie_domain_name";
	private Context context =null;

	// Create an SSLContext that uses our TrustManager
	private SSLContext sslContext = null;
	
	public JsonClient(Context context) {
	   this.context=context;
	}

   public static final MediaType JSON = MediaType
                                            .parse("application/json; charset=utf-8");
   
   static final OkHttpClient client = new OkHttpClient();
   
   public static final MediaType URL_ENCODE = MediaType
         .parse(URL_ENCODE_CONTENT+"; charset=utf-8");
   
   public static final MediaType PLAIN_TEXT = MediaType
         .parse(PLAIN_TEXT_CONTENT+"; charset=utf-8");
   private static final String CONTENT_TYPE="Content-Type";
   
   private Response getOkHttpClient(final ACDCRequest acdcRequest) throws IOException {
      Request request = new Request.Builder()
          .url(acdcRequest.stRequestURL).addHeader(CONTENT_TYPE, getContentType(acdcRequest))
          .addHeader(AUTH, getAuthToken(acdcRequest))
          .build();
      client.setReadTimeout(READ_TIMEOUT_OKHTTP, TimeUnit.SECONDS);
      client.setConnectTimeout(CONNECTION_TIMEOUT_OKHTTP, TimeUnit.SECONDS);
      setSocketFactory();
      Response response = client.newCall(request).execute();
      return response;
    }
   private Response getPutHttpClient(final ACDCRequest acdcRequest) throws IOException {
      RequestBody body = RequestBody.create(getMediaContentType(acdcRequest),acdcRequest.stData != null ? acdcRequest.stData:"");
      Request request = new Request.Builder()
          .url(acdcRequest.stRequestURL).
            addHeader(CONTENT_TYPE, getContentType(acdcRequest))
          .put(body)
          
          .build();
      client.setReadTimeout(READ_TIMEOUT_OKHTTP, TimeUnit.SECONDS);
      client.setConnectTimeout(CONNECTION_TIMEOUT_OKHTTP, TimeUnit.SECONDS);
      
      setSocketFactory();
      Response response = client.newCall(request).execute();
      return response;
    }
   private String getAuthToken(final ACDCRequest acdcRequest) {
      final StringBuilder stTicket = new StringBuilder();
      stTicket.append(BEARER);
      stTicket.append(acdcRequest.stAuthorizationHeader);

      return stTicket.toString();

   }
   private Response postOkHttpClient(final ACDCRequest acdcRequest) throws IOException {

      

      RequestBody body = RequestBody.create(getMediaContentType(acdcRequest),acdcRequest.stData != null ? acdcRequest.stData:"");
      Request request = null;
      if(acdcRequest.isAuthorizationNeed){
         request =new Request.Builder().url(acdcRequest.stRequestURL).addHeader(AUTH, getAuthToken(acdcRequest)).post(body).build();
      }else{
         request = new Request.Builder().url(acdcRequest.stRequestURL).post(body).build();
      }
      client.setConnectTimeout(30, TimeUnit.SECONDS);
      client.setReadTimeout(30, TimeUnit.SECONDS);
      setSocketFactory();
      Response response = client.newCall(request).execute();
      
      
      return response;

   }
   private void setSocketFactory() {
      
      
     
      if(sslContext == null){
         try {
            final TrustEveryoneManager tm = new TrustEveryoneManager();
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { tm }, null);
         } catch (KeyManagementException e) {
           
            e.printStackTrace();
         } catch (NoSuchAlgorithmException e1) {
           
            e1.printStackTrace();
         }
         client.setSslSocketFactory(sslContext.getSocketFactory());
      }
      
   }
	public ACDCResponse connectHttp(final ACDCRequest acdcRequest) throws 
			IOException, java.net.SocketException {
	         
		
		byte[] data = null;
		com.squareup.okhttp.Headers  headers = null;
		int iResCode =0;
 		
		Response okResponse= null;
		

		try { 
		   
		 
			
			//
			// Log.i("ACDC", "Data:"+stData);
			

			if (acdcRequest.iHttpRequestType == POST) {
			   
				 okResponse=postOkHttpClient( acdcRequest);
				
				

			} else if( acdcRequest.iHttpRequestType == GET ){

				
				 okResponse=getOkHttpClient(acdcRequest);
				
			}
			else if( acdcRequest.iHttpRequestType ==  PUT )
			{
				
				okResponse = getPutHttpClient(acdcRequest);
			}
			else if(acdcRequest.iHttpRequestType == DELETE)
			{
				
			}
			else
			{
				Log.i("JSONClient", "Wrong Request type");
			}
//			if (response != null && response.getStatusLine() != null) {
//				 iResCode = response.getStatusLine().getStatusCode();
//				
//				 
//				if (acdcRequest.isResponseHeaderNeeed ){
//				   vecHeader= new Vector<Header[]>(1);
//				   vecHeader.add(response.getAllHeaders());
//				}
//				entity = response.getEntity();
//
//				if (entity != null) {
//
//					InputStream is = entity.getContent();
//					data = readDataChunked(is);
//					is.close();
//				}
//			}
			if (okResponse != null ) {
                           iResCode = okResponse.code();
                          
                           
                          if (acdcRequest.isResponseHeaderNeeed ){
                             headers= okResponse.headers();
                             //vecHeader.add(okResponse.headers());
                          }
                   
                                  data = okResponse.body().bytes();
                                 
                  }
		} finally {
			

		}

		return new com.trimble.ag.ats.acdc.res.ACDCResponse(data, iResCode, headers);
	}
	
	
	private String getContentType(final ACDCRequest acdcRequest){
	   String stContentType= null;
	   switch (acdcRequest.iContentType) {
            case JSON_CONTENT_TYPE:
               stContentType=JSON_CONTENT;
               break;
            case URL_ENCODE_CONTENT_TYPE:
               stContentType=URL_ENCODE_CONTENT;
               break;
            case PLAIN_TEXT_CONTENT_TYPE:
               stContentType=PLAIN_TEXT_CONTENT;
               break;
            default:
               break;
         }
	   return stContentType;
	}
	private MediaType getMediaContentType(final ACDCRequest acdcRequest){
	  MediaType mediaType = null;
           switch (acdcRequest.iContentType) {
            case JSON_CONTENT_TYPE:
               mediaType=JSON;
               break;
            case URL_ENCODE_CONTENT_TYPE:
               mediaType=URL_ENCODE;
               break;
            case PLAIN_TEXT_CONTENT_TYPE:
               mediaType= PLAIN_TEXT;
               break;
            default:
               break;
         }
           return mediaType;
        }
	public String convertByteArrayToString(byte[] data) {
		if (data == null) {
			return null;
		}
		String sb = new String(data);
		return sb.toString();
	}

	public void closeStream(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	

	private byte[] readDataChunked(InputStream is) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		byte[] buffer = new byte[CHUNK_SIZE];
		int bytesRead = 0;

		while ((bytesRead = is.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}

		byte[] output = out.toByteArray();

		return output;
	}
	 

}

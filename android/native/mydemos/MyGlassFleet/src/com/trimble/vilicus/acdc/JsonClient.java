package com.trimble.vilicus.acdc;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.trimble.vilicus.acdc.res.ACDCResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Vector;

public class JsonClient {

	// private static final String USER_AGENT = "Android/";

	// private static final String ZIP_CONTENT = "application/zip";

	public static final int POST = 0;
	public static final int GET = 1;
	public static final int PUT = 2;
	public static final int DELETE = 3;
	
	private static final int JSON_CONTENT_TYPE = 1;

        private static final int PLAIN_TEXT_CONTENT_TYPE = 2;
        
        private static final int URL_ENCODE_CONTENT_TYPE = 3;
	
	private static final String JSON_CONTENT = "application/json";

	private static final String PLAIN_TEXT_CONTENT = "text/plain";
	
	private static final String URL_ENCODE_CONTENT = "application/x-www-form-urlencoded";
	
	private static final String AUTH="Authorization";

	private final static int CONNECTION_TIMEOUT = 10 * 60 * 1000;

	private final static int CHUNK_SIZE = 4096;
	
	private final static String BEARER="Bearer:";

	   private static final String COOKIE_NAME        = "cookie_name";
	   private static final String COOKIE_VALUE       = "cookie_value";
	   private static final String COOKIE_VERSION       = "cookie_version";
	   private static final String COOKIE_EXPIRE       = "cookie_expire";
	   private static final String COOKIE_PATH        = "cookie_path";
	   private static final String COOKIE_DOMAIN_NAME = "cookie_domain_name";
	private Context context =null;
	public JsonClient(Context context) {
	   this.context=context;
	}

	public ACDCResponse connectHttp(final ACDCRequest acdcRequest) throws ClientProtocolException,
			IOException, java.net.SocketException {
	         
		HttpClient httpURLConnection = null;
		byte[] data = null;
		Vector<Header[]> vecHeader=null;
		int iResCode =0;
 		// HttpConnectionParams.setConnectionTimeout(httpclient.getParams(),
		// 10000); //Timeout Limit
		HttpResponse response = null;

		HttpEntity entity = null;

		try { 
		    HttpClientProvider httpClientProvider = HttpClientProvider.getInstance((Application) context.getApplicationContext());
		    httpURLConnection =  httpClientProvider.getHttpClient();
			
			//
			// Log.i("ACDC", "Data:"+stData);
			

			if (acdcRequest.iHttpRequestType == POST) {
				HttpPost httpPost = new HttpPost(acdcRequest.stRequestURL);
				setAuthorizationHeader(acdcRequest, httpPost);
				
				
				if (acdcRequest.stData != null) {
					final StringEntity se = getStringData(acdcRequest);
					httpPost.setEntity(se);
				}

				 HttpParams myHttpParams = getHttpParams();
				 httpPost.setParams(myHttpParams);
				 httpPost.addHeader("Accept","*/*");
				 httpPost.addHeader("Accept-Encoding","gzip,deflate,sdch");
				 httpPost.addHeader("Accept-Language","en-US,en;q=0.8");
				 httpPost.addHeader("Cache-Control" ,"no-cache");
				 httpPost.addHeader("Connection","keep-alive");
				  
				response = httpURLConnection.execute(httpPost);
				

			} else if( acdcRequest.iHttpRequestType == GET ){

				/*
				 * if (!url.endsWith("?")) url += "?";
				 * 
				 * if (stData!=null) { url = url + stData; }
				 */
				 HttpGet httpget = new HttpGet(acdcRequest.stRequestURL);
				
				 setAuthorizationHeader(acdcRequest,httpget);
				 HttpParams myHttpParams = getHttpParams();
				 httpget.setParams(myHttpParams);

				response = httpURLConnection.execute(httpget);
			}
			else if( acdcRequest.iHttpRequestType ==  PUT )
			{
				HttpPut httpput = new HttpPut(acdcRequest.stRequestURL);
				if (acdcRequest.stData != null) {
					StringEntity se = getStringData(acdcRequest);
					httpput.setEntity(se);
				}
				response = httpURLConnection.execute(httpput);
			}
			else if(acdcRequest.iHttpRequestType == DELETE)
			{
				HttpDelete httpdelete = new HttpDelete(acdcRequest.stRequestURL);
				response = httpURLConnection.execute(httpdelete);
			}
			else
			{
				Log.i("JSONClient", "Wrong Request type");
			}
			if (response != null && response.getStatusLine() != null) {
				 iResCode = response.getStatusLine().getStatusCode();
				

				if (acdcRequest.isResponseHeaderNeeed ){
				   vecHeader= new Vector<Header[]>(1);
				   vecHeader.add(response.getAllHeaders());
				}
				entity = response.getEntity();

				if (entity != null) {

					InputStream is = entity.getContent();
					data = readDataChunked(is);
					is.close();
				}
			}
		} finally {
			if (entity != null) {
				entity.consumeContent();
			}

		}

		return new ACDCResponse(data, iResCode, vecHeader);
	}
	private void setAuthorizationHeader(final ACDCRequest acdcRequest,final HttpRequestBase httpRequestBase ){
	  
	   if(httpRequestBase != null && httpRequestBase != null){
	      if( acdcRequest.isAuthorizationNeed ){
	         final StringBuilder stTicket= new StringBuilder();
	         stTicket.append(BEARER);
	         stTicket.append(acdcRequest.stAuthorizationHeader);
	        
	              httpRequestBase.addHeader(AUTH, stTicket.toString());
	              Log.i(ACDCApi.TAG, "Ticket:"+stTicket.toString());
	            }
	   }
	   
	}
	private StringEntity getStringData(ACDCRequest acdcRequest ) throws UnsupportedEncodingException{
	   StringEntity se = new StringEntity(acdcRequest.stData);

           if (acdcRequest.iContentType == JSON_CONTENT_TYPE ) {
                   se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                                   JSON_CONTENT));
           } else if (acdcRequest.iContentType == URL_ENCODE_CONTENT_TYPE ) {

                   se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                         URL_ENCODE_CONTENT));
           }else if (acdcRequest.iContentType == PLAIN_TEXT_CONTENT_TYPE ) {

              se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                    PLAIN_TEXT_CONTENT));
      }
           return se;
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

	private HttpParams getHttpParams() {
		final HttpParams myHttpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myHttpParams,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(myHttpParams, CONNECTION_TIMEOUT);
		//myHttpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_0);
		 String stUserAgent = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36";
		
		/*// proxy
		 final String PROXY = "10.40.53.21";
		 // proxy host
		  final HttpHost PROXY_HOST = new HttpHost(PROXY, 3128);
		  myHttpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, PROXY_HOST);
		 */
		 myHttpParams.setParameter(CoreProtocolPNames.USER_AGENT, stUserAgent);
		// Set the timeout in milliseconds until a connection is established.
                 // The default value is zero, that means the timeout is not used. 
                 int timeoutConnection = 3000;
                 
                 HttpConnectionParams.setConnectionTimeout(myHttpParams, timeoutConnection);
                 // Set the default socket timeout (SO_TIMEOUT) 
                 // in milliseconds which is the timeout for waiting for data.
                 int timeoutSocket = 5*60*1000;
                 HttpConnectionParams.setSoTimeout(myHttpParams, timeoutSocket);
		return myHttpParams;
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
	 public static 
	   BasicClientCookie getCookie(final Context context) {
	      if(context == null){
	         return null;
	          
	      }
	      final SharedPreferences preferences = PreferenceManager
	            .getDefaultSharedPreferences(context);
	      
	      final String stCookieName=preferences.getString(COOKIE_NAME, null);
	      final String stCookieValue=preferences.getString(COOKIE_VALUE, null);
	      final String stCookiePath=preferences.getString(COOKIE_PATH, null);
	      final String stCookieDomainName=preferences.getString(COOKIE_DOMAIN_NAME, null);
	      final long lEDate=preferences.getLong(COOKIE_EXPIRE, -1);
	      final int iCookieVersion=preferences.getInt(COOKIE_VERSION, -1);
	      BasicClientCookie newCookie =null;
	      if(stCookieName != null && stCookieValue != null){
	       newCookie = new BasicClientCookie(stCookieName,
	            stCookieValue);
	       newCookie.setDomain(stCookieDomainName);
	       if(lEDate != -1){
	         newCookie.setExpiryDate(new Date(lEDate));
	       }
	       if(iCookieVersion !=  -1){
	          newCookie.setVersion(iCookieVersion); 
	       }
	       newCookie.setPath(stCookiePath);
	      }
	    
	      return newCookie;
	   }

}

package com.trimble.scout.acdc;

import android.content.Context;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Vector;

public class JsonClient {

   private static final String USER_AGENT         = "Android/";

   private static final String ZIP_CONTENT        = "application/zip";

   private static final String JSON_CONTENT       = "application/json";

   private static final String PLAIN_TEXT_CONTENT = "text/plain";
   
   private static final String URL_ENCODE_CONTENT = "application/x-www-form-urlencoded";

   private final static int    CONNECTION_TIMEOUT = 10 * 60 * 1000;

   private final static int    CHUNK_SIZE         = 4096;

   private Context             context            = null;

   private static final String AUTH               = "Authorization";

  
   
   
    public JsonClient(Context context){
       this.context = context;
    }
   public ACDCResponse connectHttp(final ACDCRequest request)
         throws ClientProtocolException, IOException, java.net.SocketException {
       byte[] data =null;
       int iResCode =0;
       Vector<Header[]> vecResponseHeader=null;
       HttpClient httpURLConnection = null;
     
      // HttpConnectionParams.setConnectionTimeout(httpclient.getParams(),
      // 10000); //Timeout Limit
      HttpResponse response = null;

      HttpEntity entity = null;

      try {
         httpURLConnection = new DefaultHttpClient();
         //
         // Log.i("ACDC", "Data:"+stData);
         if (request.iHttpRequestType == ACDCRequest.POST) {
            HttpPost httpPost = new HttpPost(request.stRequestURL);
            if (request.isAuthorizationNeed ) {
               httpPost.addHeader(AUTH, "Bearer:" + request.stAuthorizationHeader);
            }
            if (request.stData != null) {
               StringEntity se = new StringEntity(request.stData);

               if (request.iContentType == ACDCRequest.CONTENTTYPE_JSON) {
                  se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                        JSON_CONTENT));
               } else  if (request.iContentType == ACDCRequest.CONTENTTYPE_PLAINTEXT){

                  se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                        PLAIN_TEXT_CONTENT));
               }else{
                  se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                        URL_ENCODE_CONTENT));
               }

               httpPost.setEntity(se);
            }

            HttpParams myHttpParams = getHttpParams();
            httpPost.setParams(myHttpParams);
            httpPost.addHeader("Accept", "*/*");
            httpPost.addHeader("Accept-Encoding", "gzip,deflate,sdch");
            httpPost.addHeader("Accept-Language", "en-US,en;q=0.8");
            httpPost.addHeader("Cache-Control", "no-cache");
            httpPost.addHeader("Connection", "keep-alive");

            response = httpURLConnection.execute(httpPost);
         } else if (request.iHttpRequestType  == ACDCRequest.GET ) {

            /*
             * if (!url.endsWith("?")) url += "?";
             * 
             * if (stData!=null) { url = url + stData; }
             */
            HttpGet httpget = new HttpGet(request.stRequestURL);
            if (request.stAuthorizationHeader != null) {
               httpget.addHeader(AUTH, "Bearer:" + request.stAuthorizationHeader);
            }
            HttpParams myHttpParams = getHttpParams();
            httpget.setParams(myHttpParams);

            response = httpURLConnection.execute(httpget);
         } else if (request.iHttpRequestType == ACDCRequest.PUT ) {
            HttpPut httpput = new HttpPut(request.stRequestURL);
            if (request.stData != null) {
               StringEntity se = new StringEntity(request.stData);

               if (request.iContentType == ACDCRequest.CONTENTTYPE_JSON) {
                  se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                        JSON_CONTENT));
               } else  if (request.iContentType == ACDCRequest.CONTENTTYPE_PLAINTEXT){

                  se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                        PLAIN_TEXT_CONTENT));
               }else{
                  se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
                        URL_ENCODE_CONTENT));
               }

               httpput.setEntity(se);
            }
            response = httpURLConnection.execute(httpput);
         } else if (request.iHttpRequestType == ACDCRequest.DELETE) {
            HttpDelete httpdelete = new HttpDelete(request.stRequestURL);
            response = httpURLConnection.execute(httpdelete);
         } else {
            Log.i("JSONClient", "Wrong Request type");
         }
        if(response != null && response.getStatusLine() != null ){
         iResCode = response.getStatusLine().getStatusCode();
        

        if (request.isResponseHeaderNeeed){
           vecResponseHeader = new Vector<Header[]>(1);
           vecResponseHeader.add(response.getAllHeaders());
        }
          
         entity = response.getEntity();

        if (entity != null) {
           
            InputStream is = entity.getContent();
             data=readDataChunked(is);
            is.close();
        }
        }
        }finally{
            if (entity != null){
                entity.consumeContent();
            }
            
            if (httpURLConnection != null){
                httpURLConnection.getConnectionManager().shutdown();
            }
        }
         final ACDCResponse acdcResponse = new ACDCResponse(data,iResCode, vecResponseHeader);
        return  acdcResponse;
    }

    public byte[] connectHttp(String stURL, int[] iExRespCode, String stFilePath)
            throws UnknownHostException, IOException {
        byte[] data = null;
        FileInputStream fis = null;
        int  fileSize=0;
        HttpResponse response=null;
        HttpEntity entity=null;
        DefaultHttpClient httpURLConnection=null;
        try {
            File file = new File(stFilePath);
            if (!file.exists()) {
                Log.e(ACDCApi.TAG, "file not found on the underlying path" + stFilePath);
                return null;
            }
            if (!file.canRead()) {
                Log.e(ACDCApi.TAG, "current context is not allowed to read from this file:" + stFilePath);
                return null;
            }
            

            httpURLConnection = new DefaultHttpClient();
   
            HttpParams myHttpParams = getHttpParams();
            HttpPost httpPost=null;
            try {
                httpPost = new HttpPost(new URI(stURL));
            } catch (URISyntaxException e) {
                
                e.printStackTrace();
                return null;
            }
            httpPost.setParams(myHttpParams);
            
            
            fis = new FileInputStream(new File(stFilePath));
            fileSize = fis.available();
            Log.i(ACDCApi.TAG,
                    "upload file - "+file+" ,size "+fileSize+" len:"+file.length()/(1024*1024)+" mb");
            InputStreamEntity isEntity= new InputStreamEntity(fis, fis.available());
            isEntity.setContentType(ZIP_CONTENT);
            //isEntity.setChunked(true);
            httpPost.setEntity(isEntity);
            
            response=httpURLConnection.execute(httpPost);
            
            if(response != null && response.getStatusLine() != null){
            int iResCode = response.getStatusLine().getStatusCode();
            iExRespCode[0] = iResCode;
            entity = response.getEntity();
            if(entity != null){
            InputStream is = entity.getContent();
            data=readDataChunked(is);
            is.close();
            
            }
            }
            
        }

        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                // ignore
            }
            
            if (entity != null){
                entity.consumeContent();
            }
            
            if (httpURLConnection != null){
                httpURLConnection.getConnectionManager().shutdown();
            }


        }
        return data;
    }

    

    public String convertByteArrayToString(byte []data) {
        if (data == null) {
            return null;
        }
        String sb= new String(data);
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

    private HttpParams getHttpParams(){
        HttpParams myHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myHttpParams, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(myHttpParams, CONNECTION_TIMEOUT);
        
        String  stUserAgent  = USER_AGENT + ACDCApi.getInstance(context).getAppName();
        
        myHttpParams.setParameter(CoreProtocolPNames.USER_AGENT, stUserAgent);
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
}
/**
 * Copyright Trimble Inc., 2011 - 2012 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.reporter.looper
 *
 * File name:
 *		LooperThread.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Dec 15, 2012 10:05:02 AM
 *
 * Abstract:
 *
 *
 * Environment:
 *	Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */

package com.trimble.reporter.looper;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author sprabhu
 */

public class LooperThread extends Thread {

    public Handler mHandler;

    public LinkedBlockingQueue<DataSend> mBlockingQueue = null;
    
    public static final String LOG="LooperThread";
    
    private static LooperThread mLooperThread =null;
    
    
    public static final String HIT="https://hydrogen.myconnectedsite.com/app/tcc/irtcc/actions/ActOnIncidentReports?";
    
    public static final String INCIDENT_URL="https://hydrogen.myconnectedsite.com/app/tcc/irtcc/actions/ReportIncident";
    
    public static final String LOGIN_URL="https://hydrogen.myconnectedsite.com/tcc/login";
    
    public static final String LOGIN_USER_NAME="tccadmin";
    
    public static final String LOGIN_PASS="apdart%";
    
    public static final String LOGIN_ORG_NAME="tcc";
    
    private static final String LOGIN_PARAMETER="username=%s&password=%s&orgname=%s";
    
    public static final String TRACK_INCIDENTS="https://hydrogen.myconnectedsite.com/app/tcc/irtcc/actions/TrackIncidents?imei=%s&";
    
    private  String LOGIN_REQUEST=String.format(LOGIN_PARAMETER, LOGIN_USER_NAME,LOGIN_PASS,LOGIN_ORG_NAME);
    
    private   String stToken=null; 
    private  boolean isStop=false;
    
    public static final int STRING_TYPE=0;
    
    /**
     * 
     */
    private LooperThread() {
        super("LooperThread");
        mBlockingQueue = new LinkedBlockingQueue<DataSend>(10);
        start();
    }
    
    public static LooperThread getInstance(){
        if(mLooperThread== null){
            mLooperThread= new LooperThread();
        }
        return mLooperThread;
    }
    public void addData(DataSend dataSend) {
        mBlockingQueue.add(dataSend);
    }

    public void removeData(DataSend dataSend) {
        mBlockingQueue.remove(dataSend);
    }

    public void run() {
        while(!isStop){
        try {
            DataSend dataSend = mBlockingQueue.take();
            if (dataSend != null) {
                try {
                    if(stToken == null){
                        LOGIN_REQUEST=String.format(LOGIN_PARAMETER, LOGIN_USER_NAME,URLEncoder.encode(LOGIN_PASS,"UTF-8"),LOGIN_ORG_NAME);
                        
                        byte []bTicketRes= connectHttp(LOGIN_URL, null, null, true, null, true,LOGIN_REQUEST.getBytes());
                        String stTicketResp=new String(bTicketRes);
                        Log.i(LOG, "Response ="+ stTicketResp );
                        try {
                            JSONObject responseObject= new JSONObject(stTicketResp); 
                            
                            stToken = responseObject.getString("ticket");
                            
                        } catch (JSONException e) {
                            
                            e.printStackTrace();
                        }
                    }
                    String stTicket="ticket="+stToken+"&";
                   
                    dataSend.stURL+=stTicket;
                    Log.i(LOG, "URL ="+ dataSend.stURL);
                    byte []bRespose=connectHttp(dataSend.stURL, null, null, dataSend.isPost, null, true,null);
                    Log.i(LOG, "Response ="+ new String(bRespose));
                    if(dataSend.listener != null ){
                    	dataSend.listener.onConnectionSucess(new String(bRespose), STRING_TYPE);
                    }
                } catch (ClientProtocolException e) {
                  
                    e.printStackTrace();
                    if(dataSend.listener != null ){
                    	dataSend.listener.onConnectionFail(e.getMessage(), STRING_TYPE);

                    }
                } catch (IOException e) {
                  
                    e.printStackTrace();
                    if(dataSend.listener != null ){
                    	dataSend.listener.onConnectionFail(e.getMessage(), STRING_TYPE);

                    }
                }
            }
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }
    }

    public byte[] connectHttp(String url, int[] iExRespCode, String stData, boolean isPost,
            Vector<Header[]> vecheader, boolean bIsContentTypePlainText,byte []bData)
            throws ClientProtocolException, IOException {

        HttpClient httpURLConnection = null;
        byte[] data = null;
        // HttpConnectionParams.setConnectionTimeout(httpclient.getParams(),
        // 10000); //Timeout Limit
        HttpResponse response = null;

        HttpEntity entity = null;

        try {
            httpURLConnection = new DefaultHttpClient();
            //
            // Log.i("ACDC", "Data:"+stData);
            if (isPost) {
                HttpPost httpPost = new HttpPost(url);
                if (stData != null) {
                    StringEntity se = new StringEntity(stData);

                    if (!bIsContentTypePlainText) {
                        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, JSON_CONTENT));
                    } else {

                        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, PLAIN_TEXT_CONTENT));
                    }

                    httpPost.setEntity(se);
                }else{
                    ByteArrayEntity isEntity= new ByteArrayEntity(bData);
                    isEntity.setContentType("binary/octet-stream");
                    //isEntity.setChunked(true);
                    httpPost.setEntity(isEntity);
                }

                HttpParams myHttpParams = getHttpParams();
                httpPost.setParams(myHttpParams);

                response = httpURLConnection.execute(httpPost);
            } else {

                /*
                 * if (!url.endsWith("?")) url += "?"; if (stData!=null) { url =
                 * url + stData; }
                 */
                HttpGet httpget = new HttpGet(url);

                HttpParams myHttpParams = getHttpParams();
                httpget.setParams(myHttpParams);

                response = httpURLConnection.execute(httpget);
            }
            if (response != null && response.getStatusLine() != null) {
                if(iExRespCode != null){
                int iResCode = response.getStatusLine().getStatusCode();
                iExRespCode[0] = iResCode;
                }

                if (vecheader != null)
                    vecheader.add(response.getAllHeaders());
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

            if (httpURLConnection != null) {
                httpURLConnection.getConnectionManager().shutdown();
            }
        }

        return data;
    }

    private static final String USER_AGENT = "Android/tcc";

    private static final String JSON_CONTENT = "application/json";

    private static final String PLAIN_TEXT_CONTENT = "text/plain";

    private final static int CHUNK_SIZE = 4096;

    private final static int CONNECTION_TIMEOUT = 80000;

    private HttpParams getHttpParams() {
        HttpParams myHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(myHttpParams, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(myHttpParams, CONNECTION_TIMEOUT);

        String stUserAgent = USER_AGENT;

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
    
    public void stopQueue(){
        interrupt();
        isStop=true;
        mBlockingQueue.clear();
        mBlockingQueue=null;
        mLooperThread= null;
    }

}

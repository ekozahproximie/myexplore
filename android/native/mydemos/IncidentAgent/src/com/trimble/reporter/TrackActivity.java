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
 *      com.trimble.reporter
 *
 * File name:
 *		TrackActivity.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Dec 14, 2012 7:20:59 PM
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

package com.trimble.reporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

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

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trimble.agent.R;
import com.trimble.reporter.incident.TrackDBManager;
import com.trimble.reporter.looper.LooperThread;
import com.trimble.reporter.utils.Utils;

/**
 * @author sprabhu
 */

public class TrackActivity extends BaseActivity {

    private String stIncidentID = null;

    private int iDeviceID = 0;

    private String stTicket = null;;

    private ProgressDialog progress = null;
    
    private boolean isFinished=false;
    
    private String stLat=null;
    
    private String stLon =null;
    
    private String stComments=null;
    
    private String stphoneUid=null;
    
    private String stLink=null;
    
    private String stCategory=null;
    
    private ImageView link=null;
    
    private Bitmap bmp=null;
    
    private byte bImagData[]=null;
    
    TrackDBManager dbManager=null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        stIncidentID = bundle.getString("id");
        dbManager =TrackDBManager.getInstance(this);
        setContentView(R.layout.incident_details);
        iDeviceID = AgentLocActivity.getAgentID(this);
        if(!Utils.isInternetAvailable(this)){
            
            showToast(getString(R.string.connection_error));
            finish();
            return;
        }
        mThread.start();
        
        link=(ImageView)findViewById(R.id.link);
        progress = new ProgressDialog(this, R.style.DefaultProgressDialogTheme);
        progress.setMessage(getString(R.string.update));
        progress.setCancelable(false);
        progress.show();
        isFinished=false;
        
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ((TextView)findViewById(R.id.txt_latitude)).setText(stLon);
            ((TextView)findViewById(R.id.txt_longitude)).setText(stLat);
            ((TextView)findViewById(R.id.commets)).setText(stComments);
          
            link.setImageBitmap(bmp);
                  
              
           
            
        };
    };

    private Thread mThread = new Thread() {
        public void run() {
            String stURL = String.format(LooperThread.GET_INCIDENT, iDeviceID, stIncidentID);
            stURL += LooperThread.getInstance().getTicket();
            try {
            	 Log.i(LooperThread.LOG, "URL ="+ stURL );
                byte bData[]=connectHttp(stURL, null, null, false, null, true, null);
                Log.i(LooperThread.LOG, "Response ="+ new String(bData) );
               
                if(isFinished ){
                    return;
                }
                try {
                    JSONObject responseObject= new JSONObject(new String(bData)); 
                    
                     stLat = responseObject.getString("lat");
                     stLon = responseObject.getString("lon");
                     stComments = responseObject.getString("comments");
                     stphoneUid = responseObject.getString("phoneUid");
                     stLink = responseObject.getString("link");
                     stCategory= responseObject.getString("category");
                    URL url = new URL(stLink+"&ticket="+LooperThread.getInstance().getTicket());
                    bImagData=storeMediaData(url.openConnection().getInputStream());
                    bmp = BitmapFactory.decodeByteArray(bImagData, 0, bImagData.length);
                    
                  
                } catch (JSONException e) {
                    
                    e.printStackTrace();
                }
            } catch (ClientProtocolException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(1);
            progress.cancel();
        }
    };
    
    private byte[] storeMediaData( InputStream is ) {
    	byte bData[]=null;
        try {
        		if(is == null){
        			return null;
        		}
           ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
           
           int read = 0;
           byte[] bytes = new byte[4096];
    
           while ((read = is.read(bytes)) != -1) {
              os.write(bytes, 0, read);
           } 
           bData= os.toByteArray();
           is.close();
           os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bData;
     }
    public void onClick(View onclick){
        switch (onclick.getId()) {
            case R.id.accept:
            	
            	dbManager.insertOrUpdateThumbnail(bImagData, Double.parseDouble(stLat),
                		Double.parseDouble(stLon), stComments, stCategory, stIncidentID);
                Thread thread = new Thread(){
                 
                @Override
                public void run() {
                    String stURL=String.format(LooperThread.ACQUIREINCIDENT,iDeviceID,stIncidentID);
                   
                    stURL+="ticket="+LooperThread.getInstance().getTicket();
                    Log.i(LooperThread.LOG, "URL ="+ stURL );
                    try {
                        byte bData[]=connectHttp(stURL, null, null, false, null,true,null);
                        Log.i("track", new String(bData) );
                    } catch (ClientProtocolException e) {
                       
                        e.printStackTrace();
                    } catch (IOException e) {
                       
                        e.printStackTrace();
                    }
                }  
                };
                
                if(!Utils.isInternetAvailable(this)){
                    
                    showToast(getString(R.string.connection_error));
                    
                }
                thread.start();
                finish();
                break;
            case R.id.cancel:
                finish();
                break;
            default:
                break;
        }
    }
protected void onPause() {
    super.onPause();
    isFinished=true;
    if(progress != null)
    progress.cancel();
}
    public byte[] connectHttp(String url, int[] iExRespCode, String stData, boolean isPost,
            Vector<Header[]> vecheader, boolean bIsContentTypePlainText, byte[] bData)
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
                } else {
                    ByteArrayEntity isEntity = new ByteArrayEntity(bData);
                    isEntity.setContentType("binary/octet-stream");
                    // isEntity.setChunked(true);
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
                if (iExRespCode != null) {
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

}

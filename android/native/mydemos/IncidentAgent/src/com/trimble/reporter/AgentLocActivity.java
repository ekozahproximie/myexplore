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
 *		AgentLocActivity.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Dec 15, 2012 2:52:17 PM
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

import com.trimble.agent.R;
import com.trimble.reporter.app.TCCApplication;
import com.trimble.reporter.looper.DataSend;
import com.trimble.reporter.looper.LooperThread;
import com.trimble.reporter.map.GMapActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

/**
 * @author sprabhu
 */

public class AgentLocActivity extends BaseActivity {

    private TextView edLatitude = null;

    private TextView edLongtitude = null;

    private static final String D_LATITUDE = "12.989985";

    private static final String D_LONGITUDE = "80.249172";

    private static final String LATITUDE = "lat";

    private static final String LONGITUDE = "lon";
    
    public static final String AGENT_ID="agent_id";
    
    public static final int AGENT_ID_1=101;
    
    public static final int AGENT_ID_2=102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.agentinput);
        edLatitude = (TextView)findViewById(R.id.ed_lat);
        edLongtitude = (TextView)findViewById(R.id.ed_lon);
        updateUI();
    }

    private void updateUI() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setLatLon(preferences.getString(LATITUDE, D_LATITUDE),
                preferences.getString(LONGITUDE, D_LONGITUDE));

    }
    public static void storeAgentID(Context context ,int iID){
    	SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
    	Editor editor =preferences.edit();
    	editor.putInt(AGENT_ID, iID);
    	editor.commit();
    }
    public static int getAgentID(Context context ){
    	SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(context);
    	return preferences.getInt(AGENT_ID, AGENT_ID_1);
    }
    
    private void setLatLon(String stLat, String stLon) {
        edLatitude.setText(stLat);
        edLongtitude.setText(stLon);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case LAT_LON_TAKE: {
                    Bundle bundle = data.getExtras();
                    String stLat = bundle.getString(LATITUDE);
                    String stLon = bundle.getString(LONGITUDE);
                    setLatLon(stLat, stLon);
                    break;
                }

                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
    }
    
    @Override
    protected void onPause() {
        
        super.onPause();
        saveUI();
    }
    private void saveUI() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = preferences.edit();
        
        editor.putString(LATITUDE, edLatitude.getText().toString());
        editor.putString(LONGITUDE, edLongtitude.getText().toString());
        editor.commit();
    }
    public void onClick(View onClick) {
        switch (onClick.getId()) {
            case R.id.cancel:{
                
                finish();
            }
            break;
            case R.id.send:{
                sendData();
                finish();
                break;
            }
            case R.id.locmap: {
                Intent intent = new Intent(this, GMapActivity.class);

                double dLat = 0.0;
                double dLon = 0.0;

                if (edLatitude.getText().toString().length() != 0) {
                    dLat = Double.parseDouble(edLatitude.getText().toString());
                }
                if (edLongtitude.getText().toString().length() != 0) {
                    dLon = Double.parseDouble(edLongtitude.getText().toString());
                }

                intent.putExtra(GMapActivity.GEO_LOCATION, dLat + " " + dLon);
                startActivityForResult(intent, LAT_LON_TAKE);
                break;
            }

            default:
                break;
        }
    }
    private void sendData(){
        double dLat=0.0;
        double dLon=0.0;
        
        if(edLatitude.getText().toString().length() != 0){
            dLat=Double.parseDouble(edLatitude.getText().toString());
        }
        if(edLongtitude.getText().toString().length() != 0){
            dLon=Double.parseDouble(edLongtitude.getText().toString());
        }
        
        ((TCCApplication)getApplication()).setdLat(dLat, dLon);
       final DataSend dataSend = new DataSend();
        dataSend.isPost=false;
        dataSend.stURL=String.format(LooperThread.HEART_BEAT_URL,dLat,dLon,getAgentID(this) );
        Thread thread = new Thread(){
          
        @Override
        public void run() {
           
            LooperThread.getInstance().addData(dataSend);
        }  
        };
        thread.start();
    }
}

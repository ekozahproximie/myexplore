package com.trimble.reporter;

import com.trimble.reporter.looper.LooperThread;
import com.trimble.reporter.trackdb.IncidentTrackListActivity;
import com.trimble.reporter.utils.Utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import java.io.File;

public class MainActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpView();
        LooperThread looperThread =LooperThread.getInstance();
        
    }
    private void setUpView() {
        setContentView(R.layout.activity_main);
        //saveLatLon();
        checkDeviceGPS();

    }
  

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.report:{
                openActivity(ReporterActivity.class, 1);
                break;
            }
            case R.id.reportdetails:{
              /*  showAlertDialog(FUNNY, R.string.funding, R.string.kidding,
                        R.string.yes, R.string.no);*/
            	 openActivity(IncidentTrackListActivity.class, 2);
                break;
            }
            case R.id.agent:{
               
               
                break;
            }

            default:
                break;
        }
    }
    
    private void openActivity(Class<?> activityToOpen, int iTitle) {
        Intent intent = new Intent(this, activityToOpen);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            int exit_message_id = R.string.exit_message;

            showAlertDialog(ALERT_APP_EXIT, R.string.exit_title, exit_message_id,
                    R.string.yes, R.string.no);
            return true;
        }
        return false;
    }
    private void closeApp(){
      
         
        Log.i("map","close app");
        Utils.deleteJobFileDir(new File(PhotoActivity.getFlagStoreDir()));
        LooperThread.getInstance().stopQueue();
        // show the splash in every launch
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean(SHOW_SPLASH, true).commit();
          finish();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ALERT_APP_EXIT:
                if (resultCode == ALERT_RESULT_POS) {
                    closeApp();
                }
                break;
            case SPLASH:
                if(resultCode == RESULT_CANCELED){
                    closeApp();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

package com.neural.activity;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.neural.constant.Constants;
import com.neural.demo.DemoDataInjector;
import com.neural.demo.R;
import com.neural.sensor.NtDeviceManagement;
import com.neural.setting.SettingsManager;

public abstract class NeuralBaseActivity extends FragmentActivity{
	
	private static final String SHOW_SPLASH="show_splash";
	
	private static final int SPLASH =0710;
	
	public static final int SETTING_ACTIVITY =0306;
	
	public final SettingsManager settingManger = SettingsManager.getInstance();
	
	// initialize device manager singleton
	public  NtDeviceManagement devManager = null;
	
	public DemoDataInjector dataInjector =null;
	
	private transient Toast toast =null;
			
	
	 protected  void initAppSpecifics() {}
	   @Override
	    protected void onCreate(final Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        // setTheme(R.style.GpsAppTheme);

	        // show the splash in every launch
	        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	        
	        if (prefs.getBoolean(SHOW_SPLASH, false)) {
	            // Start showing the splash screen right away, and then continue
	            // with
	            // initialization
	            startActivityForResult(
	                    new Intent(this,
	                            SplashActivity.class),
	                    SPLASH);
	            overridePendingTransition(0, 0);
	            prefs.edit().putBoolean(SHOW_SPLASH, false).commit();
	        }
	        devManager = NtDeviceManagement.getDefaultDeviceManager(getApplicationContext());
	        devManager.removeStaleDevices();
	        dataInjector= DemoDataInjector.getInstance(this);
	   }
	   
	   public void showAlertDialog(Fragment fragment, int dialogId, int titleId, String message, int posId, int negId,
	         int iconId){
	        Intent intent = new Intent(this, AlertDialogActivity.class);
	        intent.putExtra(Constants.Dialog.DIALOG_TITLE, titleId);
	        intent.putExtra(Constants.Dialog.ALERT_MESSAGE_TEXT, message);
	        intent.putExtra(Constants.Dialog.ALERT_POS, posId);
	        intent.putExtra(Constants.Dialog.ALERT_NEG, negId);
	        intent.putExtra(Constants.Dialog.DIALOG_TITLE_ICON, iconId);
	        
	        if (fragment == null){
	           startActivityForResult(intent, dialogId);
	        } else {
	           startActivityFromFragment(fragment, intent, dialogId);
	        }
	     }
	   public void showAlertDialog(Fragment fragment, int dialogId, int titleId, String message, int posId, int negId
                 ){
                Intent intent = new Intent(this, AlertDialogActivity.class);
                intent.putExtra(Constants.Dialog.DIALOG_TITLE, titleId);
                intent.putExtra(Constants.Dialog.ALERT_MESSAGE_TEXT, message);
                intent.putExtra(Constants.Dialog.ALERT_POS, posId);
                intent.putExtra(Constants.Dialog.ALERT_NEG, negId);
              
                
                if (fragment == null){
                   startActivityForResult(intent, dialogId);
                } else {
                   startActivityFromFragment(fragment, intent, dialogId);
                }
             }
	   public void showAlertDialog(int dialogId, int titleId, int messageId, int posId, int negId){
              showAlertDialog(null, dialogId, titleId, getString(messageId), posId, negId);
           }
	   public void showAlertDialog(int dialogId, int titleId, int messageId, int posId, int negId, int iconId){
	        showAlertDialog(null, dialogId, titleId, getString(messageId), posId, negId,iconId);
	     }
	   
	   public void showAlertDialog(int dialogId, int titleId, String message, int posId, int negId, int iconId){
	        showAlertDialog(null, dialogId, titleId, message, posId, negId,iconId);
	     }
	   
	   @Override
	   protected Dialog onCreateDialog(int id) {
	      ProgressDialog progressDialog = new ProgressDialog(this);
	      progressDialog.setMessage(getString(R.string.please_wait));
	      progressDialog.setCancelable(false);
	      return progressDialog;
	   }
	   public  void showLong(Context context, String message) {
	        if (message == null) {
	            return;
	        }
	        if (toast == null && context != null) {
	            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
	        }
	        if (toast != null) {
	            toast.setText(message);
	            toast.show();
	        }
	    }
}

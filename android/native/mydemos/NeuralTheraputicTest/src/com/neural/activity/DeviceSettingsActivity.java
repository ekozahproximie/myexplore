package com.neural.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.neural.fragment.DeviceFragment;

public class DeviceSettingsActivity extends SettingsActivity{

	private transient DeviceFragment details=null;

	  @Override
      protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        if (getResources().getConfiguration().orientation
	                == Configuration.ORIENTATION_LANDSCAPE) {
	            // If the screen is now in landscape mode, we can show the
	            // dialog in-line with the list so we don't need this activity.
	            finish();
	            return;
	        }

	        if (savedInstanceState == null) {
	            // During initial setup, plug in the details fragment.
	             details = new DeviceFragment();
	            details.setArguments(getIntent().getExtras());
	            FragmentManager fm=getSupportFragmentManager();
	            FragmentTransaction ft=fm.beginTransaction();

	            ft.add(android.R.id.content, details).commit();
	        }
      }
	  @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		  if(details != null){
		  details.onKeyDown(keyCode, event);
		  }
		return super.onKeyDown(keyCode, event);
	}
	
}


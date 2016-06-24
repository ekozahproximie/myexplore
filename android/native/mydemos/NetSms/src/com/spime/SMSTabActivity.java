package com.spime;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class SMSTabActivity extends android.app.TabActivity {
	public void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
		            WindowManager.LayoutParams.FLAG_FULLSCREEN);
 
		setContentView(R.layout.tabsms);
		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost

		Intent intent; // Reusable Intent for each tab

		tabHost.setup();

		// Initialize a TabSpec for each tab and add it to the TabHost
		TabSpec tabSendSms = tabHost.newTabSpec("SendSms");
		tabSendSms.setIndicator("SendSms", res.getDrawable(R.drawable.ic_tab_sendsms));
	  // Create an Intent to launch an Activity for the tab
		intent = getIntent().setClass(this, SendSMS.class);
		tabSendSms.setContent(intent);
		tabHost.addTab(tabSendSms);

		// Do the same for the other tabs
		TabHost.TabSpec inbox;
		inbox = tabHost.newTabSpec("inbox");
		inbox.setIndicator("Inbox", res.getDrawable(R.drawable.ic_tab_inbox));
		intent = new Intent().setClass(this, SmsInbox.class);
//		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		inbox.setContent(intent);
		tabHost.addTab(inbox);
		
		TabHost.TabSpec feature;
		feature = tabHost.newTabSpec("feature");
		feature.setIndicator("Feature SMS", res.getDrawable(R.drawable.ic_tab_feature));
		intent = new Intent().setClass(this, FeatureSms.class);
		feature.setContent(intent);
		tabHost.addTab(feature);

		tabHost.setCurrentTab(0);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.v("Tab", "onPause");
		super.onPause();
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		Log.v("Tab", "onDestroy");
		super.onDestroy();
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		Log.v("Tab", "onStart");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.v("Tab", "onRestart");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.v("Tab", "onResume");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.v("Tab", "onStop");
	}
}

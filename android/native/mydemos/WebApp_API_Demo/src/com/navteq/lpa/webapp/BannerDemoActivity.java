package com.navteq.lpa.webapp;

import com.navteq.lpa.NavteqLPAAdActionListener;
import com.navteq.lpa.NavteqLPAOffer;
import com.navteq.lpa.NavteqLPASettingsManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;

/**
 * BannerDemoActivity.java
 * 
 * @author Jeff Cardillo
 * @copyright 2011 Navteq, Inc. All rights reserved.
 * 
 * This Activity demonstrates how easy it is to add Navteq's LPA services to 
 * your project. Simply set the Affiliate Data in the NavteqLPASettingsManager, 
 * then create a new NavteqLPAOffer object. A banner will be fetched and displayed 
 * over your current Activity. Tap the banner to interact with the ad. 
 *
 */
public class BannerDemoActivity extends Activity implements NavteqLPAAdActionListener {

	// global reference to offer object to handle onResume/onPause events
	NavteqLPAOffer navteqLPA;

	WebView mapBackground;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set up the window to show full screen without a title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// Set content to xml layout for this screen
		setContentView(R.layout.bannerdemo);
		
		ImageButton reloadAd = (ImageButton)findViewById(R.id.roloadAd);
		reloadAd.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				navteqLPA.fetchAd();
			}
		});
		
		mapBackground = (WebView)findViewById(R.id.webview);
		mapBackground.setFocusable(false);
		mapBackground.setVerticalScrollBarEnabled(false);
		
		if(savedInstanceState != null)
		{
			mapBackground.restoreState(savedInstanceState);
		}
		else
		{
			mapBackground.loadUrl(NavteqLPASettingsManager.getInstance(this).getMapBackgroundURL());
		}
		
		// prevent webview scrolling
		mapBackground.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				return (event.getAction() == MotionEvent.ACTION_MOVE);
			}
		  });
		
		// Example of setting affiliate data in the settings manager
		// NavteqLPASettingsManager.getInstance(this).setAffiliateData(partnername, affiliatenametag);
		
		// Example of adding query parameters 
		// NavteqLPAQueryUtils.addQueryParameter("D-INC", "5");
		
		// create the NavteqLPA object
		navteqLPA = new NavteqLPAOffer(this);	
		
		// fetch and display a banner ad
		navteqLPA.fetchAd();
	}		
	
	protected void onSaveInstanceState(Bundle outState) {
		mapBackground.saveState(outState);
	}
	
	/** 
	 * Called when the activity is resumed. 
	 */
	@Override
	public void  onResume(){
		super.onResume();

		navteqLPA.startRefreshTimer();
	}

	/** 
	 * Called when the activity is paused. 
	 */
	@Override
	public void onPause(){
	    super.onPause();
	  
		navteqLPA.stopRefreshTimer();
	}
	
	/** 
	 * Called when the activity is destroyed. 
	 */
	@Override
	public void onDestroy(){
	    super.onDestroy();
	  
		navteqLPA.unregisterAdListener(this);
	}

	public void adBannerFetchStarted() {
		Log.i(NavteqLPAOffer.class.getName(), "Ad Banner Fetch STARTED");
		
	}

	public void adBannerFetchComplete() {
		Log.i(NavteqLPAOffer.class.getName(), "Ad Banner Fetch COMPLETE");
		
	}

	public void adBannerClicked() {
		Log.i(NavteqLPAOffer.class.getName(), "Ad Banner Fetch CLICKED");
		
	}
	
}
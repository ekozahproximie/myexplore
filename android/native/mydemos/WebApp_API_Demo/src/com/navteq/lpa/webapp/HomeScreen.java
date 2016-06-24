/**
 * 	
 */
package com.navteq.lpa.webapp;

import com.navteq.lpa.NavteqLPAQueryUtils;
import com.navteq.lpa.NavteqLPASettingsManager;
import com.navteq.lpa.NavteqLPAWebActivity;
import com.navteq.lpa.webapp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

/**
 * HomeScreen.java
 * 
 * @author Jeff Cardillo
 * @copyright 2011 Navteq, Inc. All rights reserved.
 * 
 * This Activity is the starting point for Navteq's LPA demo. A series of buttons 
 * are presented, each bringing you to a screen to demonstrate specific functionality.
 *
 */
public class HomeScreen extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    // Set up the window to show full screen without a title
	    requestWindowFeature(Window.FEATURE_NO_TITLE);

	    // Set content to xml layout for this screen
	    setContentView(R.layout.homescreen);
	    
	    // initialize the application's settings
	    NavteqLPASettingsManager.getInstance(getApplicationContext());
	    
	    // initialize settings and set affiliate data
	    
	    //Example of initializing settings with affiliate information
		//NavteqLPASettingsManager.getInstance(this).setAffiliateData(partnername, affiliatenametag);
	    
		// Example of adding query parameters 
	    //NavteqLPAWaapiUtils.addQueryParameter("campaignid", "5");
	    
	    // Create home screen buttons and touch functions
	    Button banner = (Button)this.findViewById(R.id.home_screen_button_banner);
	    banner.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent i = new Intent(HomeScreen.this, BannerDemoActivity.class);
		        startActivity(i);
				
			}
		});
	    
	    Button dealfinder = (Button)this.findViewById(R.id.home_screen_dealfinder);
	    dealfinder.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(HomeScreen.this, DealsSlider.class);
		        startActivity(i);
			}
		});
	    
	    Button adwallet = (Button)this.findViewById(R.id.home_screen_ad_wallet);
	    adwallet.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
			    Intent i = new Intent(HomeScreen.this, NavteqLPAWebActivity.class);
				
				String qp = NavteqLPAQueryUtils.buildQueryParamsString();
				i.putExtra("url", NavteqLPASettingsManager.WALLET_PATH + qp);
		        startActivity(i);
			}
		});
	    
	    Button settings = (Button)this.findViewById(R.id.home_screen_button_settings);
	    settings.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent i = new Intent(HomeScreen.this, SettingsActivity.class);
		        startActivity(i);
				
			}
		});
	    
	    Button about = (Button)this.findViewById(R.id.home_screen_button_about);
	    about.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent i = new Intent(HomeScreen.this, AboutActivity.class);
		        startActivity(i);
				
			}
		});
	    
//	    Button faq = (Button)this.findViewById(R.id.home_screen_button_faq);
//	    faq.setOnClickListener(new OnClickListener() {
//			
//			public void onClick(View v) {
//				
//				Intent i = new Intent(HomeScreen.this, FaqActivity.class);
//		        startActivity(i);
//				
//			}
//		});
	}
	
}

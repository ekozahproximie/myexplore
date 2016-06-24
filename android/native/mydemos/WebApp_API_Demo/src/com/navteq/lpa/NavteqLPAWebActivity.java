/**
 * 
 */
package com.navteq.lpa;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * NavteqLPAWebActivity.java
 * 
 * This class is an activity with a WebView that is properly set up
 * to respond to javascript commands offered by the LPA Web App.
 * 
 * @author Jeff Cardillo
 * @copyright 2011 Navteq, Inc. All rights reserved.
 *
 */
public class NavteqLPAWebActivity extends Activity implements NavteqLPAWebClientListener {

	private static final String TAG = "Ad" ;
		
	private WebView mCustomWebView;		
	
	private NavteqLPAWebViewClient webViewClient;
	
	private ProgressDialog spinner;
	
	boolean pageLoadFinished = false;
		
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    
	    // Set up the window to show full screen without a title
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    RelativeLayout layout = new RelativeLayout(this);
	    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
	            RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
	    
	    mCustomWebView = new WebView(this); 

	    layout.addView(mCustomWebView, lp);
	    
	    setContentView(layout);
	    buildCustomWebView();
	    
	    if (savedInstanceState != null
	    		&& savedInstanceState.getBoolean("pageLoadFinished"))
	    {
	    	mCustomWebView.restoreState(savedInstanceState);
	    }
	    else
		{
		    String url = null;
		    
		    Bundle extras = getIntent().getExtras(); 
	
		    if(extras != null)
		    {
		    	String path = extras.getString("url");
		    
		    	if(path != null)    
		    	{
		    		Log.d(TAG, path);
		    		
		    		if (path.startsWith("http"))
		    			url = path;
		    		else
		    			url = NavteqLPASettingsManager.getInstance(getApplicationContext()).getDomainValue() + path;
		    		
			    }
		    	else
			    	Toast.makeText(this, "URL for ad is null", 1000);
		    }
		    else
		    	Toast.makeText(this, "URL for ad is null", 1000);
		    
		    mCustomWebView.loadUrl(url);
		}
	}
	
	/**
	 * This is mandatory code for the Ad Content to work as anticpated
	 */
	private void buildCustomWebView() {
		//Mandatory for Ad
		mCustomWebView.getSettings().setJavaScriptEnabled(true);
		//Mandatory for Ad Wallet
		mCustomWebView.getSettings().setDomStorageEnabled(true);				
		mCustomWebView.getSettings().setLoadWithOverviewMode(true);	
		mCustomWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		
		//Mandatory for closing Ad from Javascript
		mCustomWebView.setWebChromeClient(new WebChromeClient() {      	     	   	      
  	    	    
  	    	@Override   		
  	    	public void onCloseWindow (WebView window){
  	    		
				if(mCustomWebView.canGoBack())
					mCustomWebView.goBack();
				else
				{
	     			Log.d(TAG, "JS Close "+window.getTitle());
	     			if(window.getTitle().equals("Your Ad Wallet")||
	     					window.getTitle().equals("Deal Finder") ){
	     				finish();
	     			}
				}	
  	    	}
  	    });
		
		webViewClient = new NavteqLPAWebViewClient(NavteqLPAWebActivity.this);
		webViewClient.registerNavteqLPAWebClientListener(this);
		
  	    //Mandatory for Ad to intercept different actions
		mCustomWebView.setWebViewClient(webViewClient);
	}     

	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("pageLoadFinished", pageLoadFinished);
		mCustomWebView.saveState(outState);
	}
	
	/** 
	 * Called when the activity is resumed. 
	 */
	@Override
	public void  onResume(){
		super.onResume();
	}

	/** 
	 * Called when the activity is paused. 
	 */
	@Override
	public void onPause(){
	    super.onPause();
	  
	    // work around for funny-business with opening hardware keyboard while
	    // while showing spinner
	    if(spinner != null)
	    	spinner.dismiss();
	    spinner = null;
	}

	/** 
	 * Called when the activity is destroyed. 
	 */
	@Override
	public void onDestroy(){
	   super.onDestroy();
	  
	   webViewClient.unregisterNavteqLPAWebClientListener(this);
	}
	
	@Override
	public void pageLoadStarted() {
		
		pageLoadFinished = false;
		
		if(spinner != null)
			spinner.dismiss();
		spinner = null;
		
		// work around for funny-business with opening hardware keyboard while
	    // while showing spinner
		spinner = new ProgressDialog(this);
		spinner.setMessage("Loading. Please wait...");
		spinner.show();
	}

	@Override
	public void pageLoadFinished() {
		
		pageLoadFinished = true;
		
		// work around for funny-business with opening hardware keyboard while
	    // while showing spinner
		if(spinner != null)
			spinner.dismiss();
		
		spinner = null;
	}

}

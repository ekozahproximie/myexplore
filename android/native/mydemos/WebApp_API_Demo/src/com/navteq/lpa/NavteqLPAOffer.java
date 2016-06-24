package com.navteq.lpa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.navteq.lpa.NavteqLPAWebActivity;

/**
 * NavteqLPAOffer.java
 * 
 * This class creates and displays a teaser ad at the bottom of
 * the specified activity.
 * 
 * @author Jeff Cardillo
 * @copyright 2011 Navteq, Inc. All rights reserved.
 *
 */
public class NavteqLPAOffer
{
	/** WebApp path  */
	private static final String CHECKIN = "checkin/?";		
	
	private Context context;
	private Activity activity;
	private WebView teaserView;
	
	private int refreshInterval = 0;
	
	private static List<NavteqLPAAdActionListener> listeners = new ArrayList<NavteqLPAAdActionListener>(); 
	
	int displayHeight;
	
	/* Handler for refreshing ads */
	private Handler refreshHandler = new Handler();
	
	/**
	 * Creates a standard NavteqLPATeaserAd instance and will obtain a
	 * teaser ad for the passed in WebView.
	 * 
	 * @param parentActivity
	 * @param teaserView
	 */
	public NavteqLPAOffer(Activity activity)
	{
		this.context = activity.getApplicationContext();
		this.activity = activity;
		
		if(activity instanceof NavteqLPAAdActionListener)
			registerAdListener((NavteqLPAAdActionListener)activity);
	
		//Start collection of location data
		NavteqLPALocationUtils.collectLocationData(activity, 60000);
		
		// build teaser view
		buildTeaserView();
	}
	
	/**
	 * Creates a NavteqLPATeaserAd instance and will obtain a new
	 * teaser ad for the passed in WebView every refreshIntervalInSec
	 * seconds.
	 * 
	 * @param parentActivity
	 * @param teaserView
	 * @param refreshIntervalInSec
	 */
	public NavteqLPAOffer(Activity activity, int refreshIntervalInSec)
	{
		this.context = activity.getApplicationContext();
		this.activity = activity;
		
		if(activity instanceof NavteqLPAAdActionListener)
			registerAdListener((NavteqLPAAdActionListener)activity);
		
		//Start collection of location data
		NavteqLPALocationUtils.collectLocationData(activity, 60000);
		
		// build teaser view
		buildTeaserView();
		
		refreshInterval = refreshIntervalInSec;
		
		long mStartTime = 0L;
		if (mStartTime  == 0L) {
            mStartTime = System.currentTimeMillis();
            refreshHandler.removeCallbacks(mUpdateTimeTask);
            refreshHandler.postDelayed(mUpdateTimeTask, 
            		refreshIntervalInSec * 1000);
        }
	}
	
	private Animation slideUpFromBottom() {
		
		Animation anim = new TranslateAnimation(Animation.ABSOLUTE, 0, 
				Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
			
		anim.setDuration(750);
		anim.setRepeatCount(0);
		anim.setFillEnabled(true);
		anim.setFillBefore(true);
		anim.setFillAfter(true);
		anim.setInterpolator(new AccelerateInterpolator());
		anim.setZAdjustment(Animation.ZORDER_TOP);
		return anim;
	}
	
	/**
	 * Starts the refresh timer if the refresh interval is
	 * greater than 0.
	 */
	public void startRefreshTimer()
	{
		if(refreshInterval == 0)
			return;
		
		refreshHandler.removeCallbacks(mUpdateTimeTask);
		refreshHandler.postDelayed(mUpdateTimeTask, refreshInterval);
	}
	
	/**
	 * Stops the refresh timer.
	 */
	public void stopRefreshTimer()
	{
		refreshHandler.removeCallbacks(mUpdateTimeTask);
	}
	
	/**
	 * Sets the refresh interval to the specified number of seconds 
	 * and restarts the refreshTimer.
	 * 
	 * @param intervalInSeconds
	 */
	public void setRefreshTimerInterval(int intervalInSeconds)
	{
		refreshInterval = intervalInSeconds;
		
		refreshHandler.removeCallbacks(mUpdateTimeTask);
		refreshHandler.postDelayed(mUpdateTimeTask, refreshInterval);
	}
	
	/**
	 * Request a new Ad to be obtained.
	 */
	public void fetchAd()
	{
		double[] locationCoords = NavteqLPASettingsManager.getInstance(context).getLocationCoordinates();
		
		fireAdBannerFetchStarted();
		
		reloadAd(teaserView, locationCoords[0], locationCoords[1]);
	}
	
	/**
	 * Sets up the teaser view with all overriding processes
	 * @param a - Original Activity where the teaser view is located
	 * @param i - The intent to move from current activity to the post click content activity
	 * 
	 */
	private void buildTeaserView() {
		
		if(!NavteqLPAQueryUtils.areMandatoryFieldsSet()){
			throw new IllegalStateException("Must set mandatory fields before using this function");
		}
		
		String url = NavteqLPAQueryUtils.buildQueryParamsString();

		this.teaserView = new WebView(activity);
		teaserView.setVisibility(View.GONE);
		teaserView.getSettings().setJavaScriptEnabled(true);
		teaserView.setBackgroundColor(Color.TRANSPARENT);
		teaserView.setVisibility(View.VISIBLE);		
		teaserView.setTag("NMS");
		teaserView.setFocusable(false);
		teaserView.setVerticalScrollBarEnabled(false);

		teaserView.setWebChromeClient(new WebChromeClient() {
			
			@Override
			public void onConsoleMessage(String message, int lineNumber,
					String sourceID) {
				Log.d(getClass().getName(), message + " -- From line " + lineNumber
						+ " of " + sourceID);
			}
		});
		
		RelativeLayout rLayout = new RelativeLayout(activity);
		
		RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 50);
		relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		android.view.ViewGroup group = (android.view.ViewGroup)activity.findViewById(android.R.id.content);
		relativeParams.topMargin = group.getHeight()-50;
		
		rLayout.addView(teaserView, relativeParams);
		group.addView(rLayout);
		
		Log.d(getClass().getName(), "checkin : " + NavteqLPASettingsManager.getInstance(context).getDomainValue()
				+ CHECKIN + url);
		
		teaserView.setWebViewClient(new NavteqOfferWebViewClient());
	}
	
	/*
	 * Timer routine for Handler task for reloading ads
	 */
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
		       Log.d(getClass().getName(), "start update process");
		       
		       long utm = SystemClock.uptimeMillis();      
		       int seconds = refreshInterval;
	    	   
		       double[] coords = NavteqLPASettingsManager.getInstance(context).getLocationCoordinates();

		       reloadAd(teaserView, coords[0], coords[1]);
		      
		       refreshHandler.postAtTime(this,
		               utm + ((seconds + 1) * 1000));
		   }
	};
	
	private class NavteqOfferWebViewClient extends WebViewClient {
		
		@Override
		 public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
			try
			{
				if (url.contains("getPage")) {
					
					fireAdBannerFetchClicked();
					
					// Set up the intent to transfer control from current activity to ad's activity 
			        Intent i = new Intent(context, NavteqLPAWebActivity.class);
					i.putExtra("url", url);
					activity.startActivity(i);
					return true;
				}
			} catch(Exception e) {
				Log.i(NavteqLPAOffer.class.getName(), "Unsupported Action Url: " + url);
			}
			
			return false;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
		
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			
			fireAdBannerFetchCompleted();
			
			teaserView.startAnimation(slideUpFromBottom());
			teaserView.setVisibility(View.VISIBLE);
	    }
		
	}
	
	/**
	 * Reload the ad using a specified lat / lon location 
	 */
	private static void reloadAd(WebView wv, double latitude, double longitude) {		

		if(NavteqLPAQueryUtils.areMandatoryFieldsSet() == false){
			throw new IllegalStateException("Must set mandatory fields before using this function");
		}
		
		if(NavteqLPAQueryUtils.areMandatoryFieldsSet() == false){
			throw new IllegalArgumentException("Latitude and Longitude values must be set");
		}
						
		String sLat = new BigDecimal(latitude).toPlainString();		
		String sLong = new BigDecimal(longitude).toPlainString();
		
		NavteqLPAQueryUtils.addQueryParameter("longitude", sLong);
		NavteqLPAQueryUtils.addQueryParameter("latitude", sLat);
		String url = NavteqLPAQueryUtils.buildQueryParamsString();	
		
		Log.i(NavteqLPAOffer.class.getName(), url);
		
		reloadAd(wv, url);
		
	}

	/**
	 * Example way to reload ad 
	 * 
	 * @param url - String that contains a list of query parameters
	 */
	private static void reloadAd(WebView wv, String queryParams) {						
		
		Log.i(NavteqLPAOffer.class.getName(), NavteqLPASettingsManager.getInstance(null).getDomainValue() + CHECKIN + queryParams);
		
		//To make sure the user does not click an ad while the syste is refreshing it
		wv.setVisibility(View.INVISIBLE);				
		wv.loadUrl(NavteqLPASettingsManager.getInstance(null).getDomainValue() + CHECKIN + queryParams);
		wv.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Add yourself as a listener for Ad fetch events.
	 * 
	 * @param listener
	 */
	public void registerAdListener(NavteqLPAAdActionListener listener)
	{
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	/**
	 * Remove yourself as a listener for Ad fetch events.
	 * 
	 * @param listener
	 */
	public void unregisterAdListener(NavteqLPAAdActionListener listener)
	{
		if(listeners.contains(listener))
			listeners.remove(listener);
	}
	
	private static void fireAdBannerFetchStarted()
	{
		Log.i(NavteqLPAOffer.class.getName(), "Ad Banner Fetch Started");
		
		for(NavteqLPAAdActionListener l : listeners)
			l.adBannerFetchStarted();
	}

	private static void fireAdBannerFetchCompleted()
	{
		Log.i(NavteqLPAOffer.class.getName(), "Ad Banner Fetch Complete");
		
		for(NavteqLPAAdActionListener l : listeners)
			l.adBannerFetchComplete();
	}
	
	private static void fireAdBannerFetchClicked()
	{
		Log.i(NavteqLPAOffer.class.getName(), "Ad Banner Clicked");
		
		for(NavteqLPAAdActionListener l : listeners)
			l.adBannerClicked();
	}
}
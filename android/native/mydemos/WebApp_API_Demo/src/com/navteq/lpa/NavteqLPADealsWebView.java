package com.navteq.lpa;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;

/**
 * NavteqLPADealsWebView.java
 * 
 * This class will configure the provided WebView to behave
 * appropriately as a Deal Finder WebView
 * 
 * @author Jeff Cardillo
 * @copyright 2011 Navteq, Inc. All rights reserved.
 *
 */
public class NavteqLPADealsWebView extends WebView
{
	private Activity activity;
	private WebView webView;
	
	String url;
	
	public NavteqLPADealsWebView(final Activity activity, WebView webview)
	{
		super(activity.getApplicationContext());
		
		this.activity = activity;
		this.webView = webview;
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setGeolocationEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);				
		webView.getSettings().setLoadWithOverviewMode(true);	
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		
		String qp = NavteqLPAQueryUtils.buildQueryParamsString();
		
		url = NavteqLPASettingsManager.getInstance(activity.getApplicationContext()).getDomainValue()
						+ NavteqLPASettingsManager.FINDER_PATH + qp;
	
		load();
		webView.setWebViewClient(new NavteqLPAWebViewClient(activity));
	}
	
	private void load()
	{
		webView.loadUrl(url);
	}

}
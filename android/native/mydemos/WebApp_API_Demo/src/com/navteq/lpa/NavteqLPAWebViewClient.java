package com.navteq.lpa;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * NavteqWebViewClient.java
 * 
 * This class creates a specialized WebViewClient that is
 * configured to behave appropriately to the commands issued
 * by the LPA Web App.
 * 
 * @author Jeff Cardillo
 * @copyright 2011 Navteq, Inc. All rights reserved.
 *
 */
public class NavteqLPAWebViewClient extends WebViewClient
{
	
	private static final String SCHEME_SMS = "sms:";
	private static final String SCHEME_MARKET = "market.android.com";
	private static final String SCHEME_HTTP = "web:";
	private static final String SCHEME_MARKET2 = "market:";
	
	
	Context context;
	ArrayList<NavteqLPAWebClientListener> listeners = new ArrayList<NavteqLPAWebClientListener>(); 
	
	public NavteqLPAWebViewClient(Context context)
	{
		super();
		
		this.context = context;
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) 
	{
		try
		{
			// intercept page load				
			if (url.contains("getPage")) 
			{
				view.loadUrl(url);					
				return true;
			} 
			else 
			{					
				if (url.startsWith(WebView.SCHEME_TEL) ||
						url.startsWith(SCHEME_SMS) ||
						url.contains(SCHEME_MARKET) ||
						url.contains(SCHEME_MARKET2))
				{
					// if contains "web:" then strip
					if (url.startsWith(SCHEME_HTTP))
					{
						url = url.substring(4);
					}
					
					// allow android to open appropriate control
					Uri u = Uri.parse(url);	
					Intent i = new Intent(Intent.ACTION_VIEW, u);
					context.startActivity(i);
					return true; 
				}
				if (url.startsWith(SCHEME_HTTP))
				{
	                Uri u = Uri.parse(url.substring(4));     
	                Intent i = new Intent(Intent.ACTION_VIEW, u);
	                context.startActivity(i);
	                return true;
				}
				else if (url.startsWith("close://")) 
				{
					// if the web browser has pages in history, follow back
			    	if(view.canGoBack())
			    	{
			    		view.goBack();
			    		return true;
			    	}
			    	else
			    	{
			    		// close the view
			    		if(context instanceof Activity)
			    		{
			    			((Activity)context).finish();
			    			return true;
			    		}
					}
				 }
			}
			
		} catch(Exception e)
		{
			Log.i(NavteqLPAWebViewClient.class.getName(), "Unsupported Action Url: " + url);
		}
		
		return false;
	}						
	
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		
		firePageLoadStarted();
    }
	
	@Override
	public void onPageFinished(WebView view, String url) {

		firePageLoadFinished();
    }
	
	/**
	 * Register yourself as a NavteqLPAWebClientListener
	 * @param listener
	 */
	public void registerNavteqLPAWebClientListener(NavteqLPAWebClientListener listener)
	{
		if(!listeners.contains(listener))
			listeners.add(listener);
	}
	
	/**
	 * Unregister yourself as a NavteqLPAWebClientListener
	 * @param listener
	 */
	public void unregisterNavteqLPAWebClientListener(NavteqLPAWebClientListener listener)
	{
		if(listeners.contains(listener))
			listeners.remove(listener);
	}
	
	private void firePageLoadStarted()
	{
		for(NavteqLPAWebClientListener listener : listeners)
			listener.pageLoadStarted();
	}
	
	private void firePageLoadFinished()
	{
		for(NavteqLPAWebClientListener listener : listeners)
			listener.pageLoadFinished();
	}
}
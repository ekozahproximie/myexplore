package com.navteq.lpa;

/**
 * Implement this interface when listening for NavteqLPAWebViewClient events.
 *
 */
public interface NavteqLPAWebClientListener {

	/**
	 * Fired when the web client starts to load a page.
	 */
	public void pageLoadStarted();
	
	/**
	 * Fired when the web client finishes loading a page.
	 */
	public void pageLoadFinished();
	
}

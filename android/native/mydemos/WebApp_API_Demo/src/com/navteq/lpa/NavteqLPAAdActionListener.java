package com.navteq.lpa;

/**
 * Implement this interface when listening for LPAAdActionEvents.
 *
 */
public interface NavteqLPAAdActionListener {

	/**
	 * Fired when a request for an ad banner is issued.
	 */
	public void adBannerFetchStarted();
	
	/**
	 * Fired when a request for an ad banner completes.
	 */
	public void adBannerFetchComplete();
	
	/**
	 * Fired when a user taps an Ad banner
	 */
	public void adBannerClicked();
	
}

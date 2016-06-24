/**
 * 
 */
package com.navteq.lpa.webapp;

import com.navteq.lpa.NavteqLPAQueryUtils;
import com.navteq.lpa.NavteqLPASettingsManager;
import com.navteq.lpa.NavteqLPAWebActivity;
import com.navteq.lpa.NavteqLPAWebViewClient;
import com.navteq.lpa.NavteqLPADealsWebView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ViewFlipper;

/**
 * DealsSlider.java
 * 
 * @author Jeff Cardillo
 * @copyright 2011 Navteq, Inc. All rights reserved.
 * 
 * This Activity demonstrates how easy it is to integrate Navteq's Deal Finder
 * in to your project. This particular class shows how to use the Deal Finder
 * in two different ways. First, using a standard button to launch a new Activity,
 * second, with a "pull out" tab animation on the left of the screen.
 *
 */
public class DealsSlider extends Activity {
	
	ViewFlipper flipper;
	
	WebView sliderWebview;
	
	private boolean dealSliderShown;
	ImageButton slideButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    // Set up the window to show full screen without a title
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    // Set content to xml layout for this screen
	    setContentView(R.layout.dealsslider);
	    
	    flipper = (ViewFlipper) findViewById(R.id.flipper);
	    
	    WebView mapBackground = (WebView)findViewById(R.id.webview);
	    mapBackground.setFocusable(false);
	    mapBackground.setVerticalScrollBarEnabled(false);
		mapBackground.loadUrl(NavteqLPASettingsManager.getInstance(this).getMapBackgroundURL());
		
		// prevent webview scrolling
		mapBackground.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				return (event.getAction() == MotionEvent.ACTION_MOVE);
			}
		  });
	    
	    // Example of opening the deal finder with a simple button.
	    ImageButton dealFinder = (ImageButton)this.findViewById(R.id.dealfinder_button_id);
	    dealFinder.setBackgroundColor(Color.TRANSPARENT);
		dealFinder.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(DealsSlider.this, NavteqLPAWebActivity.class);
				
				String qp = NavteqLPAQueryUtils.buildQueryParamsString();
				i.putExtra("url", NavteqLPASettingsManager.FINDER_PATH + qp);
		        startActivity(i);
			}
		});
	    
	    // Example of opening the deal finder in a custom sliding control.
	    // Must initialize this webview so that this controller can intercept the close message
	    // and slide the page back to previous screen.
		sliderWebview = new NavteqLPADealsWebView(this, (WebView) findViewById(R.id.webcontent));
		sliderWebview.setWebViewClient(new NavteqLPAWebViewClient(DealsSlider.this));
	    
	    slideButton = (ImageButton) findViewById(R.id.dealsslider_button_id);
	    slideButton.setBackgroundColor(Color.TRANSPARENT);
	    slideButton.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View view) {
	        	
	        	dealSliderShown = true;
	        	
	        	slideButton.startAnimation(buttonInFromLeftAnimation());
	        	flipper.setInAnimation(inFromLeftAnimation());
	            flipper.setOutAnimation(stayPut());
	            flipper.showNext();      
	        }
	    });
	    //////////////////////////////////////////////////////////////////////////////////////
	    //

//	    button2.setOnClickListener(new View.OnClickListener() {
//	        public void onClick(View view) {
//	            flipper.setInAnimation(inFromLeftAnimation());
//	            flipper.setOutAnimation(outToRightAnimation());
//	            flipper.showPrevious();      
//	        }
//	    });
	   }
	
	public void shutDealsSlider()
	{
		slideButton.startAnimation(buttonOutToLeftAnimation());
        flipper.setInAnimation(stayPut());
        flipper.setOutAnimation(outToLeftAnimation());
        flipper.showPrevious();  
        dealSliderShown = false;
	
	}
	
	@Override
	public void finish()
	{
		if(dealSliderShown)
		{
			shutDealsSlider();
			return;
		}
		
		super.finish();
	}
	
	@Override
	protected void onResume() {
	
		super.onResume();
	}
	
	private Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
			
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}
	
	private Animation buttonOutToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT,  1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
				Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
			
		outtoLeft.setDuration(500);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}
	
	private Animation buttonInFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  1.2f,
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		inFromLeft.setDuration(540);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}
	
	private Animation inFromLeftAnimation() {
		Animation inFromLeft = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		inFromLeft.setDuration(500);
		inFromLeft.setInterpolator(new AccelerateInterpolator());
		return inFromLeft;
	}
	
	private Animation stayPut() {
		Animation outtoRight = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
			
		outtoRight.setDuration(500);
		outtoRight.setInterpolator(new AccelerateInterpolator());
		return outtoRight;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	
	    	if(dealSliderShown)
	    	{
		    	if(sliderWebview.canGoBack())
		    	{
		    		sliderWebview.goBack();
		    		return true;
		    	}
		    	else
		    	{
			        shutDealsSlider();
			        return true;
		    	}
	    	}
	    	else
	    	{
	    		finish();
	    		return true;
	    	}
	    }
	    return super.onKeyDown(keyCode, event);
	}
		
}

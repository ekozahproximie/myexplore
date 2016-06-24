/**
 * 	
 */
package com.navteq.lpa.webapp;

import com.navteq.lpa.webapp.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

/**
 * About.java
 * 
 * @author Jeff Cardillo
 * @copyright 2011 Navteq, Inc. All rights reserved.
 * 
 * This Activity shows information relating to the current build.
 *
 */
public class FaqActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    // Set up the window to show full screen without a title
	    requestWindowFeature(Window.FEATURE_NO_TITLE);

	    // Set content to xml layout for this screen
	    setContentView(R.layout.faq);
	    
	    TextView appSummary = (TextView)findViewById(R.id.faq);
	    appSummary.setText("FAQ");
	}
	
}

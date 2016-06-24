/**
 * 	
 */
package com.navteq.lpa.webapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;



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
public class AboutActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    // Set up the window to show full screen without a title
	    requestWindowFeature(Window.FEATURE_NO_TITLE);

	    // Set content to xml layout for this screen
	    setContentView(R.layout.about);
	    
	    TextView appSummary = (TextView)findViewById(R.id.versionsummary);
	    appSummary.setText("Version " + getResources().getString(R.string.app_version));
	    
	    TextView copyright = (TextView)findViewById(R.id.copyright);
	    copyright.setText(readCopyrightFile());
	    
	}
	
	private String readCopyrightFile()
	{

	     InputStream inputStream = getResources().openRawResource(R.raw.copyright);
	     
	     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	     
	     int i;
		  
	     try 
	     {
	    	 i = inputStream.read();
	    	 
	    	 while (i != -1)
		     {
	    		 byteArrayOutputStream.write(i);
	    		 i = inputStream.read();
		     }
		     
	    	 inputStream.close();
	    	 
	     } catch (IOException e) {
		   
	    	 e.printStackTrace();
		 }
	  
	     return byteArrayOutputStream.toString();
	}
	
}

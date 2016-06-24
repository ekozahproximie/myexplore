/**
 * Copyright Trimble Inc., 2011 - 2012 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.reporter
 *
 * File name:
 *		SplashActivity.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Dec 14, 2012 2:41:23 PM
 *
 * Abstract:
 *
 *
 * Environment:
 *	Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */



package com.trimble.reporter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * @author sprabhu
 *
 */

public class SplashActivity extends Activity {

    private static final int SPLASH_TIME_MS = 2000;
  
    public SplashActivity() {
    }

    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       setContentView(R.layout.splash);
       
    
    
       Runnable finish = new Runnable() {
          @Override
          public void run() {
              
             setResult(RESULT_OK);
             finish();
             overridePendingTransition(0,0);
              
          }
       };
       new Handler().postDelayed(finish, SPLASH_TIME_MS);
    }
    
    @Override
    protected void onDestroy() {
       View view = findViewById(R.id.splash);
       if (view != null)
          unbindDrawables(view);
       System.gc();
       super.onDestroy();
    }

   
   
    
    private void unbindDrawables(View view) {
       if (view.getBackground() != null) {
          view.getBackground().setCallback(null);
       }
       if (view instanceof ViewGroup) {
          for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
             unbindDrawables(((ViewGroup) view).getChildAt(i));
          }
          ((ViewGroup) view).removeAllViews();
       }
    }
    
}

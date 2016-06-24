package com.neural.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.neural.demo.R;
import com.neural.demo.VideoCapture;
import com.neural.log.ExceptionHandler;

public class SplashActivity extends Activity {
	private static final int SPLASH_TIME_MS = 2000;
	
	private static final int VIDEO_CAPTURE = 2;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_screen);
		/*LogReaderTask logReaderTask =LogReaderTask.getInstance(this);
		logReaderTask.execute();*/
		Runnable finish = new Runnable() {
			@Override
			public void run() {
				startMain();
			
			}

		};
		new Handler().postDelayed(finish, SPLASH_TIME_MS);
		register();
	}
	private void startMain(){
		startActivityForResult(
                new Intent(this,
                        VideoCapture.class),VIDEO_CAPTURE);
		//finish();
		overridePendingTransition(0, 0);
	}
	 @Override
	   protected void onDestroy() {
	      View view = findViewById(R.id.splash);
	      if (view != null){
	         unbindDrawables(view);
	      }
	      //System.gc();
	      super.onDestroy();
	   }
	 
	 private void unbindDrawables(final View view) {
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
	 
	 @Override
	    public boolean onKeyDown(final int keyCode,final KeyEvent event) {
	        boolean isEventHandled=false;
	    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	         
	    		isEventHandled= true;
	        }
	    	isEventHandled=false;
	        return isEventHandled;
	    }
	 
	 @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == VIDEO_CAPTURE){
		    ExceptionHandler.getInstance().clearLog();
		    finish();
		}
		else{
		super.onActivityResult(requestCode, resultCode, data);
		}
	}
	 private void register(){
		  ExceptionHandler.getInstance().setContext(this);
          ExceptionHandler.getInstance().register();
          boolean isDebuggable = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)); 
          ExceptionHandler.getInstance().submitAllLogs(this,false,isDebuggable);
	 }
}

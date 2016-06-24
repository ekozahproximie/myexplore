package com.trimple.servicesample;

import com.trimple.services.MyService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	private static final String TAG = "ServicesDemo";
	 
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	 
	 }
	 
	  public void onClick(View src) {
	    switch (src.getId()) {
	    case R.id.buttonStart:
	       final Intent intent =new Intent(this,MyService.class);
	      // intent.setAction("start.myservice");
	       startService(intent);
	      break;
	    case R.id.buttonStop:
	      stopService(new Intent(this, MyService.class));
	      break;
	    }
	  }
}

package com.spime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.FrameLayout;

public class CameraDemo extends Activity {

	private static final String TAG = "CameraDemo";
	Camera camera;
	Preview preview;
	Button buttonClick;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 // Hide the window title.
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		preview = new Preview(this);
		((FrameLayout) findViewById(R.id.preview)).addView(preview); // <4>
		
	    buttonClick = (Button) findViewById(R.id.buttonClick);
	    buttonClick.setOnClickListener(new OnClickListener() {
	      public void onClick(View v) { // <5>
	        preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
	      }
	    });

		
		
		
		
		
	}

	ShutterCallback shutterCallback = new ShutterCallback() {

		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};
	/** Handles data for raw picture */
	PictureCallback rawCallback = new PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};
	/** Handles data for jpeg picture */
	PictureCallback jpegCallback = new PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {
			
			
				// write to local sandbox file system
				// outStream =
				// CameraDemo.this.openFileOutput(, 0);
				// Or write to sdcard
				
				writeFile(data,String.format("%d.jpg", System.currentTimeMillis()));
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};
	
	public static void writeFile(byte byrs[],String name){
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;

		} else {
			// Something else is wrong. It may be one of many other
			// states, but all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		File root = Environment.getExternalStorageDirectory();
		if (mExternalStorageAvailable &&mExternalStorageWriteable
				&& root.canWrite()) {
			File gpxfile = new File(root, name);
			try {
				gpxfile.createNewFile();
			
			FileOutputStream put = new FileOutputStream(gpxfile);
			
			put.write(byrs);
			put.close();
			}catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(preview.camera != null){
			preview.camera .release();
			preview.camera=null;
		}
	}
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
	  if (keyCode == KeyEvent.KEYCODE_CAMERA) { 
	   // do nothing on camera button \
	   preview.camera.takePicture(shutterCallback, rawCallback,
				jpegCallback);
	   return true;

	  } 
	  return super.onKeyDown(keyCode, event);
	} 

}

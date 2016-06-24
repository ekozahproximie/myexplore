package com.example.test;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Camera myCamera;
	private MyCameraSurfaceView myCameraSurfaceView;
	private MediaRecorder mediaRecorder;

	Button myButton;
	SurfaceHolder surfaceHolder;
	boolean recording;
	private int cameraId = 0;
	private static final String DEBUG_TAG="Main";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		recording = false;

		setContentView(R.layout.activity_main);

		// Get Camera for preview
		myCamera = getCameraInstance();
		if (myCamera == null) {
			Toast.makeText(MainActivity.this, "Fail to get Camera",
					Toast.LENGTH_LONG).show();
		}

		myCameraSurfaceView = new MyCameraSurfaceView(this);
		FrameLayout myCameraPreview = (FrameLayout) findViewById(R.id.videoview);
		myCameraPreview.addView(myCameraSurfaceView);

		myButton = (Button) findViewById(R.id.mybutton);
		myButton.setOnClickListener(myButtonOnClickListener);
	}

	Button.OnClickListener myButtonOnClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (recording) {
				// stop recording and release camera
				mediaRecorder.stop(); // stop the recording
				releaseMediaRecorder(); // release the MediaRecorder object
				recording = false;
				myButton.setText("REC");
				// Exit after saved
				//finish();
			} else {

				// Release Camera before MediaRecorder start
				//releaseCamera();

				if (!prepareMediaRecorder()) {
					Toast.makeText(MainActivity.this,
							"Fail in prepareMediaRecorder()!\n - Ended -",
							Toast.LENGTH_LONG).show();
					finish();
					return;
				}

				mediaRecorder.start();
				recording = true;
				myButton.setText("STOP");
			}
		}
	};
	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}
	

	  private int findFrontFacingCamera() {
	    int cameraId = -1;
	    // Search for the front facing camera
	    int numberOfCameras = Camera.getNumberOfCameras();
	    for (int i = 0; i < numberOfCameras; i++) {
	      CameraInfo info = new CameraInfo();
	      Camera.getCameraInfo(i, info);
	      if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
	        Log.d(DEBUG_TAG, "Camera found");
	        cameraId = i;
	        break;
	      }
	    }
	    return cameraId;
	  }
	private Camera getCameraInstance() {
		// TODO Auto-generated method stub
		Camera c = null;
		try {
			cameraId=findFrontFacingCamera();
			if(cameraId > -1){
				c = Camera.open(cameraId); // attempt to get a Camera instance
			}else{
			c = Camera.open(); // attempt to get a Camera instance
			}
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	private boolean prepareMediaRecorder() {
		if(myCamera == null)
		myCamera = getCameraInstance();
		mediaRecorder = new MediaRecorder();

		myCamera.unlock();
		mediaRecorder.setCamera(myCamera);
		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	/*	mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_TIME_LAPSE_HIGH));*/

		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		/*mediaRecorder.setProfile(CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH));*/

		mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory()+File.separator+"myvideo.mp4");
		mediaRecorder.setMaxDuration(60000); // Set max duration 60 sec.
		mediaRecorder.setMaxFileSize(5000000); // Set max file size 5M

		mediaRecorder.setPreviewDisplay(myCameraSurfaceView.getHolder()
				.getSurface());

		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			releaseMediaRecorder();
			return false;
		}
		return true;

	}
	@Override
	protected void onResume() {
		
		super.onResume();
		prepareMediaRecorder();
	}
	@Override
	protected void onPause() {
		super.onPause();
		releaseMediaRecorder(); // if you are using MediaRecorder, release it
								// first
		releaseCamera(); // release the camera immediately on pause event
	}

	private void releaseMediaRecorder() {
		if (mediaRecorder != null) {
			mediaRecorder.reset(); // clear recorder configuration
			mediaRecorder.release(); // release the recorder object
			mediaRecorder = null;
			myCamera.lock(); // lock camera for later use
		}
	}

	private void releaseCamera() {
		if (myCamera != null) {
			//myCamera.stopPreview();
			//myCamera.setPreviewCallback(null);
			myCamera.release(); // release the camera for other applications
			myCamera = null;
			
		}
	}

	public class MyCameraSurfaceView extends SurfaceView implements
			SurfaceHolder.Callback {

		private SurfaceHolder mHolder;
	
		public MyCameraSurfaceView(Context context) {
			super(context);
			

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			// deprecated setting, but required on Android versions prior to 3.0
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format,
				int weight, int height) {
			// If your preview can change or rotate, take care of those events
			// here.
			// Make sure to stop the preview before resizing or reformatting it.

			if (mHolder.getSurface() == null) {
				// preview surface does not exist
				return;
			}

			// stop preview before making changes
			try {
				myCamera.stopPreview();
			} catch (Exception e) {
				// ignore: tried to stop a non-existent preview
			}

			// make any resize, rotate or reformatting changes here

			// start preview with new settings
			try {
				myCamera.setPreviewDisplay(mHolder);
				setCameraDisplayOrientation(MainActivity.this, cameraId, myCamera);
				myCamera.startPreview();

			} catch (Exception e) {
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO Auto-generated method stub
			// The Surface has been created, now tell the camera where to draw
			// the preview.
			try {
				myCamera.setPreviewDisplay(holder);
				myCamera.startPreview();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub

		}
	}
	
	public static int getDisplayRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        switch (rotation) {
            case Surface.ROTATION_0: return 0;
            case Surface.ROTATION_90: return 90;
            case Surface.ROTATION_180: return 180;
            case Surface.ROTATION_270: return 270;
        }
        return 0;
    }
	
	public static void setCameraDisplayOrientation(Activity activity,
            int cameraId, Camera camera) {
        // See android.hardware.Camera.setCameraDisplayOrientation for
        // documentation.
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int degrees = getDisplayRotation(activity);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}

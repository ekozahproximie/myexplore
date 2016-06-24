package com.neural.demo;

import Camera.MyPreviewCallback;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.neural.activity.NeuralBaseActivity;
import com.neural.activity.SettingsActivity;
import com.neural.camera.CameraHardwareException;
import com.neural.camera.CameraHolder;
import com.neural.constant.Constants;
import com.neural.log.LogReaderTask;
import com.neural.quickblox.VideoChat;
import com.neural.quickblox.activity.SignInActivity;
import com.neural.quickblox.ui.view.OwnSurfaceView;
import com.neural.sensor.NtDeviceManagement;
import com.neural.setting.SettingsManager;
import com.neural.util.Utils;
import com.neural.view.GraphView;
import com.neural.view.MyGestureDetectorCompat;
import com.neural.view.MyGestureListener;
import com.neural.view.PathRecorder;
import com.neural.view.ScaleLayout;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class VideoCapture extends NeuralBaseActivity implements OnClickListener,MediaRecorder.OnErrorListener ,
MediaRecorder.OnInfoListener,OnCheckedChangeListener{
	private transient MediaRecorder mMediaRecorder;

	private transient boolean mMediaRecorderRecording = false;
	private transient Camera mCameraDevice;

	private NtDeviceManagement managedDevices = null;
	private GraphView mGraphView;
	
	private ToggleButton buttonstart = null;
	private ToggleButton callButton = null;
	private transient MyGestureDetectorCompat myGestureDetector;
	private transient MyGestureListener myGestureListener=  null;
	private transient View.OnTouchListener rootViewTouchListener;
	

	private static final String TAG = "VideoCapture";
	private SurfaceHolder mSurfaceHolder = null;
	private SurfaceView mVideoPreview = null;
	private boolean mPreviewing = false;
	private transient int cameraId = 0;
	private transient int iDisplayMode = 0;
	private transient int iOldDisplayMode = 0;
	private int mZoomValue; // The current zoom value.
	private int mZoomMax;
	private MyOrientationEventListener mOrientationListener;
	private CamcorderProfile mProfile = null;
	private static final String FILE_NAME = "neural_videocapture.mp4";
	private boolean mStartPreviewFail = false;
	private int mCameraId;
	private Parameters mParameters;
	boolean mPausing = false;
	private int mNumberOfCameras=0;
	private transient ToggleButton demoButton = null; 
	private transient ScaleLayout scaleLayout =null;
	private transient OwnSurfaceView ownSurfaceView =null;
	private transient MyPreviewCallback myPreViewCallBack =null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LogReaderTask.getInstance(this);
                 
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		/*
		 * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		// cameraView.setClickable(true);
		// cameraView.setOnClickListener(this);

		mNumberOfCameras = CameraHolder.instance().getNumberOfCameras();
		iDisplayMode=settingManger.getDisplayMode(this);
		mOrientationListener = new MyOrientationEventListener(VideoCapture.this);
		myPreViewCallBack = new MyPreviewCallback(this);
		initDisplay();
		
		managedDevices = NtDeviceManagement
				.getDefaultDeviceManager(getApplicationContext());
		//managedDevices.stopStreaming(devAddress);
	}
	private void setGuesterListener(){
	   final View root=findViewById(R.id.root);
	   myGestureListener=  new MyGestureListener(this);
	   myGestureDetector = new MyGestureDetectorCompat(this, myGestureListener);
	   myGestureListener.setDetectorCompat(myGestureDetector);
           rootViewTouchListener = new View.OnTouchListener() {
                   public boolean onTouch(View v, MotionEvent event) {
                           if (myGestureDetector.onTouchEvent(event)) {
                                   return true;
                           }
                           return false;
                   }
           };
           root.setOnTouchListener(rootViewTouchListener);
           if(mGraphView != null){
              mGraphView.setGestureDetector(myGestureDetector);
           }
	}

	private transient boolean isUIinit = false;

	private void initDisplay() {
		
		if (iDisplayMode == SettingsManager.GRAPH_VIEW
				|| iDisplayMode == SettingsManager.VIDEO_GRAPH_VIEW) {
			stopSensorStreaming();
		}
		if (iDisplayMode == SettingsManager.VIDEO_VIEW
				|| iDisplayMode == SettingsManager.VIDEO_GRAPH_VIEW) {
			//releaseMediaRecorder();
		   cameraReleaseEvent();
			
		}
		iDisplayMode=settingManger.getDisplayMode(this);
		iOldDisplayMode =iDisplayMode;
		
		
              
                
                   
		if(iDisplayMode == SettingsManager.VIDEO_VIEW){
			initVideoMode();
		} else if (iDisplayMode == SettingsManager.GRAPH_VIEW) {
			initGraphMode();
		} else if (iDisplayMode == SettingsManager.VIDEO_GRAPH_VIEW) {
			initVideo_Graph();
		}
		isUIinit = true;
	}

	private void initVideoMode() {

		
		setContentView(R.layout.video_display);
		
		//initDisplayModeUI();
		setVideoView();
		setGuesterListener();
		if(isUIinit){
		   cameraRegisterEvent();
		}
               (findViewById(R.id.videoic))
                     .setBackgroundResource(R.drawable.web_camera_fou);
	}

   private void initDemoButton() {
      demoButton = (ToggleButton) findViewById(R.id.demo_mode);
      if (demoButton != null) {

         demoButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                  boolean isChecked) {
              
               if (isChecked) {
                  settingManger.storeDemoMode(isChecked, VideoCapture.this);
                  dataInjector.connectDeviceAsDemo(mGraphView, mGraphView);
                  mGraphView.setResumed(true);
               } else {
                  settingManger.storeDemoMode(isChecked, VideoCapture.this);
                  dataInjector.disConnectDeviceAsDemo();
                  mGraphView.setResumed(false);
               }
            }
         });

      }
      boolean isDemoMode=settingManger.isDemoModeEnabled(VideoCapture.this);
      demoButton.setChecked(isDemoMode);
   }
	private void initGraphMode() {

		managedDevices = NtDeviceManagement
				.getDefaultDeviceManager(getApplicationContext());
		setContentView(R.layout.graph_disaplay);
		//initDisplayModeUI();
		setGraphView();
		setGuesterListener();
		 (findViewById(R.id.graphic))
                 .setBackgroundResource(R.drawable.bar_chart_fou);
		 
	}

	private void initVideo_Graph() {

		managedDevices = NtDeviceManagement
				.getDefaultDeviceManager(getApplicationContext());

		
		setContentView(R.layout.video_graph);
		//initDisplayModeUI();
		setGraphView();
		setVideoView();
		if(isUIinit){
                   cameraRegisterEvent();
                }
		setGuesterListener();
		(findViewById(R.id.monitoric))
                .setBackgroundResource(R.drawable.monitor_fou);
	}

	private void startPreview() throws CameraHardwareException {
		if (iDisplayMode != SettingsManager.VIDEO_VIEW
				&& iDisplayMode != SettingsManager.VIDEO_GRAPH_VIEW) {
			return;
		}
		Log.v(TAG, "startPreview");
		if (mCameraDevice == null) {
			// If the activity is paused and resumed, camera device has been
			// released and we need to open the camera.
			mCameraDevice = CameraHolder.instance().open(mCameraId);
			setPreViewCallBack();
		}
		
		if (mPreviewing == true) {
			mCameraDevice.stopPreview();
			mPreviewing = false;
		}
		   setPreviewDisplay(mSurfaceHolder);
		   
		       final int iOrientation= setCameraDisplayOrientation(this, cameraId, mCameraDevice);
		       ownSurfaceView = new OwnSurfaceView(iOrientation,cameraId);
                       setCameraParameter();
                try {
                        mCameraDevice.startPreview();
                       
                        mPreviewing = true;

		} catch (Throwable ex) {
		        closeCamera();
			throw new RuntimeException("startPreview failed", ex);
		}

	}
	private void setPreViewCallBack(){
	   mCameraDevice.setPreviewCallback(myPreViewCallBack);
	}
	   private void setPreviewDisplay(SurfaceHolder holder) {
	        try {
	          
	           mCameraDevice.setPreviewDisplay(holder);
	           
	        } catch (Throwable ex) {
	            closeCamera();
	            throw new RuntimeException("setPreviewDisplay failed", ex);
	        }
	    }
private void setCameraParameter(){
    mParameters = mCameraDevice.getParameters();
   if (mParameters.isZoomSupported()) {
           mZoomMax = mParameters.getMaxZoom();
           // Currently we use immediate zoom for fast zooming to get
           // better UX and
           // there is no plan to take advantage of the smooth zoom.

           // Set zoom parameters asynchronously
           mParameters.setZoom(mZoomValue);
           mCameraDevice.setParameters(mParameters);

   }
   if (mProfile != null) {
           mParameters.setPreviewSize(
                           mProfile.videoFrameWidth,
                           mProfile.videoFrameHeight);
           mParameters
                           .setPreviewFrameRate(mProfile.videoFrameRate);
           mCameraDevice.setParameters(mParameters);
   }
// Keep preview size up to date.
   mParameters = mCameraDevice.getParameters();
}
	private void setGraphView() {
		mGraphView = new GraphView(this);
		enablePreDrawnClear();
		FrameLayout layout = (FrameLayout) findViewById(R.id.graph);
		layout.addView(mGraphView);
		startSensorStreaming();
		initDemoButton();
		scaleLayout=(ScaleLayout)findViewById(R.id.scalelayout);
		mGraphView.setScaleLayout(scaleLayout);
	}
	private void enablePreDrawnClear(){
	 
	   final ToggleButton predrawnLock = (ToggleButton)findViewById(R.id.predrawnclearlock);
	   if (SettingsManager.getInstance().isPreDrawnEnabled(this)){
   	     
   	      if(predrawnLock !=null){
   	         predrawnLock.setVisibility(View.VISIBLE);
   	      }
   	   
   	   
   	   predrawnLock.setOnCheckedChangeListener( new OnCheckedChangeListener() {
         
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!isChecked){
               mGraphView.clearPreDrawn();
               PathRecorder pathRecorder = PathRecorder.getInstance();
               pathRecorder.clear();
               pathRecorder.stopRecord(false);
            }
            
            mGraphView.lockPredrawn(isChecked);
         }
      });
   	
	   }else{
	      if(predrawnLock !=null){
                 predrawnLock.setVisibility(View.GONE);
              }
	   }
	}
	private void setVideoView() {
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
	   
	   
	   
            mCameraId = findFrontFacingCamera();
            
            if (CamcorderProfile.hasProfile(mCameraId,CamcorderProfile.QUALITY_1080P)){
               mProfile = CamcorderProfile.get(mCameraId,CamcorderProfile.QUALITY_1080P);
            }
             else if (CamcorderProfile.hasProfile(mCameraId,CamcorderProfile.QUALITY_720P)){
                mProfile = CamcorderProfile.get(mCameraId,CamcorderProfile.QUALITY_720P);
             }
             else if (CamcorderProfile.hasProfile(mCameraId,CamcorderProfile.QUALITY_480P)){
                mProfile = CamcorderProfile.get(mCameraId,CamcorderProfile.QUALITY_480P);
             }
             else if (CamcorderProfile.hasProfile(mCameraId,CamcorderProfile.QUALITY_HIGH)){
                mProfile = CamcorderProfile.get(mCameraId,CamcorderProfile.QUALITY_HIGH);
             }
            
             else if (CamcorderProfile.hasProfile(mCameraId,CamcorderProfile.QUALITY_CIF)){
                mProfile = CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_CIF);
               }
             else if (CamcorderProfile.hasProfile(mCameraId,CamcorderProfile.QUALITY_QCIF)){
                  mProfile = CamcorderProfile.get(mCameraId, CamcorderProfile.QUALITY_QCIF);
                 } 
             else if (CamcorderProfile.hasProfile(mCameraId,CamcorderProfile.QUALITY_LOW)){
                    mProfile = CamcorderProfile.get(mCameraId,CamcorderProfile.QUALITY_LOW);
                 }
            
            Thread startPreviewThread=null;
            if (iDisplayMode == SettingsManager.VIDEO_VIEW
                  || iDisplayMode == SettingsManager.VIDEO_GRAPH_VIEW) {
               /*
                * To reduce startup time, we start the preview in another thread. We
                * make sure the preview is started at the end of onCreate.
                */
                startPreviewThread = new Thread(new Runnable() {
      
                  public void run() {
                     try {
                        mStartPreviewFail = false;
                        startPreview();
                     } catch (CameraHardwareException e) {
                        // In eng build, we throw the exception so that test tool
                        // can detect it and report it
                        if ("eng".equals(Build.TYPE)) {
                           throw new RuntimeException(e);
                        }
                        mStartPreviewFail = true;
                     }
                  }
               });
               startPreviewThread.start();
            }
             // Make sure preview is started.
             try {
                if(startPreviewThread != null){
                 startPreviewThread.join();
                }
                 if (mStartPreviewFail) {
                    // showCameraErrorAndFinish();
                     return;
                 }
             } catch (InterruptedException ex) {
                 // ignore
             }
		mVideoPreview = (SurfaceView) findViewById(R.id.surface_camera);
		
		SurfaceHolder localPreviewHolder = mVideoPreview.getHolder();
		if(localPreviewHolder != null){
		localPreviewHolder.addCallback(surfaceCallback);
		localPreviewHolder.setFormat(PixelFormat.TRANSLUCENT);
                
		// deprecated setting, but required on Android versions prior to 3.0
		localPreviewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		buttonstart = (ToggleButton) findViewById(R.id.buttonstart);
	}

   public void showCallOption() {
      if (iDisplayMode == SettingsManager.VIDEO_VIEW
            || iDisplayMode == SettingsManager.VIDEO_GRAPH_VIEW) {
         callButton = (ToggleButton) findViewById(R.id.callbutton);
         callButton.setOnCheckedChangeListener(this);
         final VideoChat chat = VideoChat.getInstance(getApplicationContext());
         if (SignInActivity.getStringPrefrenceValue(this,
               SignInActivity.LOGIN_NAME, null) != null
               && SignInActivity.getStringPrefrenceValue(this,
                     SignInActivity.CALLEE_USER_NAME, null) != null) {
            callButton.setVisibility(View.VISIBLE);
         } else {
            callButton.setVisibility(View.GONE);
         }
      }
   }
	private void restoreCallSetting(){
	   final com.neural.quickblox.VideoChat videochat =
                 com.neural.quickblox.VideoChat .getInstance(getApplicationContext());
           videochat.setActivity(this);
           videochat.onResume();
	}
	public void sendOwnSurfaceData(byte[] data, Camera camera){
	   if(ownSurfaceView != null){
	      ownSurfaceView.onPreviewFrame(data, camera);
	   }
	}
	 private void startVideoRecording() {
	        Log.v(TAG, "startVideoRecording");
	        if (Utils.getStorageStatus(true) != Utils.STORAGE_STATUS_OK) {
	            Log.v(TAG, "Storage issue, ignore the start request");
	            return;
	        }

	        initializeRecorder();
	        if (mMediaRecorder == null) {
	            Log.e(TAG, "Fail to initialize media recorder");
	            return;
	        }

	        pauseAudioPlayback();

	        try {
	            mMediaRecorder.start(); // Recording is now started
	        } catch (RuntimeException e) {
	            Log.e(TAG, "Could not start media recorder. ", e);
	            releaseMediaRecorder();
	            return;
	        }
	       

	        mMediaRecorderRecording = true;
	        keepScreenOn();
	    }
	 //Prepares media recorder.
	    private void initializeRecorder() {
	        Log.v(TAG, "initializeRecorder");
	        // If the mCameraDevice is null, then this activity is going to finish
	        if (mCameraDevice == null) return;

	        if (mSurfaceHolder == null) {
	            Log.v(TAG, "Surface holder is null. Wait for surface changed.");
	            return;
	        }

	        //long requestedSizeLimit = 0;
	        
	        mMediaRecorder = new MediaRecorder();

	        // Unlock the camera object before passing it to media recorder.
	        mCameraDevice.unlock();
	        
	        mMediaRecorder.setCamera(mCameraDevice);
	        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	        mMediaRecorder.setProfile(mProfile);
	        mMediaRecorder.setMaxDuration(50 * 1000); // 50 seconds

                // Set output file.
                if (Utils.getStorageStatus(true) != Utils.STORAGE_STATUS_OK) {
                        mMediaRecorder.setOutputFile("/dev/null");
                } else {
                        mMediaRecorder.setOutputFile(Environment
                                        .getExternalStorageDirectory()
                                        + File.separator
                                        + FILE_NAME);
                        // Set maximum file size.
                        // remaining >= LOW_STORAGE_THRESHOLD at this point, reserve a
                        // quarter
                        // of that to make it more likely that recording can complete
                        // successfully.
                        long maxFileSize = Utils.getAvailableStorage()
                                        - Utils.LOW_STORAGE_THRESHOLD / 4;

                        try {

                                if (maxFileSize > 0) {
                                        mMediaRecorder.setMaxFileSize(maxFileSize);
                                } else {
                                        mMediaRecorder.setMaxFileSize(5 * 1024 * 1024); // Approximately
                                                                                                                                // 5
                                                                                                                                // megabytes
                                }
                        } catch (RuntimeException exception) {
                                // We are going to ignore failure of setMaxFileSize here, as
                                // a) The composer selected may simply not support it, or
                                // b) The underlying media framework may not handle 64-bit
                                // range
                                // on the size restriction.
                                mMediaRecorder.setMaxFileSize(5 * 1024 * 1024); // Approximately 5
                                                                                                                        // megabytes
                        }
                }
                mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
             // See android.hardware.Camera.Parameters.setRotation for
                // documentation.
                int rotation = 0;
                int mOrientation = mOrientationListener.getOrientation();
                if (mOrientation != OrientationEventListener.ORIENTATION_UNKNOWN) {
                        CameraInfo info = CameraHolder.instance().getCameraInfo()[mCameraId];;
                        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                                rotation = (info.orientation - mOrientation + 360) % 360;
                        } else { // back-facing camera
                                rotation = (info.orientation + mOrientation) % 360;
                        }
                }
                mMediaRecorder.setOrientationHint(rotation);
	        try {
	            mMediaRecorder.prepare();
	        } catch (IOException e) {
	            Log.e(TAG, "prepare failed for " , e);
	            releaseMediaRecorder();
	            throw new RuntimeException(e);
	        }

	        mMediaRecorder.setOnErrorListener(this);
	        mMediaRecorder.setOnInfoListener(this);
	    }

	
	 private void stopVideoRecording() {
	        Log.v(TAG, "stopVideoRecording");
	        if (mMediaRecorderRecording) {
	            mMediaRecorder.setOnErrorListener(null);
	            mMediaRecorder.setOnInfoListener(null);
	            try {
	                mMediaRecorder.stop();
	            } catch (RuntimeException e) {
	                Log.e(TAG, "stop fail: " + e.getMessage());
	            }
	            mMediaRecorderRecording = false;
	            keepScreenOnAwhile();
	           
	        }
	        releaseMediaRecorder();  // always release media recorder
	    }
	  private void keepScreenOnAwhile() {
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    }
	  /*
	     * Make sure we're not recording music playing in the background, ask the
	     * MediaPlaybackService to pause playback.
	     */
	    private void pauseAudioPlayback() {
	        // Shamelessly copied from MediaPlaybackService.java, which
	        // should be public, but isn't.
	        Intent i = new Intent("com.android.music.musicservicecommand");
	        i.putExtra("command", "pause");

	        sendBroadcast(i);
	    }
	

	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				Log.d(TAG, "front Camera found");
				cameraId = i;
				break;
			}
		}
		return cameraId;
	}

	private void disConnectDemoDataInject(){
	   if(demoButton != null && demoButton.isChecked()){
             dataInjector.disConnectDeviceAsDemo();
           }
	}


	public void onClick(final View view) {
		
		switch (view.getId()) {
		case R.id.buttonstart:
			if (mMediaRecorderRecording) {
			   stopVideoRecording();
			} else {
			   startVideoRecording() ;
			}
			break;
		case R.id.videoic:
		      if(iOldDisplayMode == SettingsManager.VIDEO_VIEW){
                         break;
                      }
		      disConnectDemoDataInject();
			settingManger.storeDisplayMode(SettingsManager.VIDEO_VIEW,
					VideoCapture.this);
			initDisplay();
			break;
		case R.id.graphic:
		      if(iOldDisplayMode == SettingsManager.GRAPH_VIEW){
                         break;
                       }
		      disConnectDemoDataInject();
			settingManger.storeDisplayMode(SettingsManager.GRAPH_VIEW,
					VideoCapture.this);
			initDisplay();
			break;

		case R.id.monitoric:
		      if(iOldDisplayMode == SettingsManager.VIDEO_GRAPH_VIEW){
                         break;
                      }
		      disConnectDemoDataInject();
			settingManger.storeDisplayMode(SettingsManager.VIDEO_GRAPH_VIEW,
					VideoCapture.this);
			initDisplay();
			break;
		case R.id.setting_page:
		        if(demoButton != null && demoButton.isChecked()){
		           dataInjector.disConnectDeviceAsDemo();
		        }
		       
                        startSettingScreen();
                        break;
		case R.id.callbutton:{
		   final VideoChat videoChat =VideoChat.getInstance(getApplicationContext());
		   boolean isChecked = ((ToggleButton)view).isChecked();
		   
		         if(! isChecked){
		            videoChat.finishCall();
		         }else{
		            videoChat.showCallUserActivity();
		         }
		         break;
		}
		default:
			break;
		}
		
		
	}
	 private void closeCamera() {
	        Log.v(TAG, "closeCamera");
	        if (mCameraDevice == null) {
	            Log.d(TAG, "already stopped.");
	            return;
	        }
	        mCameraDevice.setPreviewCallback(null);
	        // If we don't lock the camera, release() will fail.
	        mCameraDevice.lock();
	        CameraHolder.instance().release();
	        mCameraDevice = null;
	        mPreviewing = false;
	    }
	@Override
	protected void onPause() {
	   if(mGraphView != null){
	      mGraphView.setResumed(false);
             }
		super.onPause();
//		if(mGraphView != null){
//                   mGraphView.onStop();
//		}
		// make sure streaming is stopped
		stopSensorStreaming();
		cameraReleaseEvent();
		
	}
	
	private void cameraReleaseEvent(){
	   mPausing = true;
           // Hide the preview now. Otherwise, the preview may be rotated during
          // onPause and it is annoying to users.
          //mVideoPreview.setVisibility(View.INVISIBLE);
          // This is similar to what mShutterButton.performClick() does,
          // but not quite the same.
          if (mMediaRecorderRecording) {
             stopVideoRecording();
          } else {
              stopVideoRecording();
          }
           closeCamera();
           resetScreenOn();
           mOrientationListener.disable();
	}
	private void resetScreenOn() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void keepScreenOn() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		startSensorStreaming();
		cameraRegisterEvent();
		enablePreDrawnClear();
		if(mGraphView != null){
		   mGraphView.onResume();
		  
	          
		   mGraphView.setUpGraphDisaply();
		   mGraphView.setResumed(true);
		}
		restoreCallSetting();
		
	}
	private void cameraRegisterEvent(){
	   mPausing = false;
           // Start orientation listener as soon as possible because it takes
           // some time to get first orientation.
           mOrientationListener.enable();
           if(mVideoPreview != null){
                 mVideoPreview.setVisibility(View.VISIBLE);
                 if (!mPreviewing && !mStartPreviewFail) {
                    if (!restartPreview()) return;
                }
           }
           
           keepScreenOn();
	}
	 private boolean restartPreview() {
	        try {
	            startPreview();
	        } catch (CameraHardwareException e) {
	           e.printStackTrace();
	            //showCameraErrorAndFinish();
	            return false;
	        }
	        return true;
	    }
	private void startSensorStreaming() {
		
		if (iDisplayMode == SettingsManager.GRAPH_VIEW
				|| iDisplayMode == SettingsManager.VIDEO_GRAPH_VIEW) {
			if (mGraphView != null) {
				managedDevices.startStreamingAll(mGraphView);
//				managedDevices.connect("RN42-B5A4", "00:06:66:46:B5:A4",mGraphView
//				      , 1, null);
			}
		}
	}
	
	private void stopSensorStreaming() {

		if (iDisplayMode == SettingsManager.GRAPH_VIEW
				|| iDisplayMode == SettingsManager.VIDEO_GRAPH_VIEW) {
			if (mGraphView != null) {

				managedDevices.stopStreamingAll();
				managedDevices.setHandler(null);
			}
		}
	}
	
	private void releaseMediaRecorder() {
	        Log.v(TAG, "Releasing media recorder.");
	        if (mMediaRecorder != null) {
	            //cleanupEmptyFile();
	            mMediaRecorder.reset();
	            mMediaRecorder.release();
	            mMediaRecorder = null;
	        }
	        // Take back the camera object control from media recorder. Camera
	        // device may be null if the activity is paused.
	        if (mCameraDevice != null) mCameraDevice.lock();
	    }


	private void initDisplayModeUI() {

		final SettingsManager manager = SettingsManager.getInstance();
		final Spinner s1 = (Spinner) findViewById(R.id.displaymode);
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource(this, R.array.displaymode,
						android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s1.setAdapter(adapter);
		s1.setSelection(iDisplayMode - 1);
		s1.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// showToast("Spinner1: position=" + position + " id=" + id);
				if (isUIinit) {
					isUIinit = false;
					return;
				}
				if (position == 0) {
					manager.storeDisplayMode(SettingsManager.VIDEO_VIEW,
							VideoCapture.this);
					initDisplay();
				} else if (position == 1) {
					manager.storeDisplayMode(SettingsManager.GRAPH_VIEW,
							VideoCapture.this);
					initDisplay();
				} else if (position == 2) {
					manager.storeDisplayMode(SettingsManager.VIDEO_GRAPH_VIEW,
							VideoCapture.this);
					initDisplay();
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// showToast("Spinner1: unselected");
			}
		});

	}
	 @Override
	    public void onBackPressed() {
	        if (mPausing) return;
	        if (mMediaRecorderRecording) {
	            stopVideoRecording();
	        }
	        super.onBackPressed();
	 }
	private transient SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

		private void autoFocus() {
			if (mCameraDevice != null) {
				// get Camera parameters
				Camera.Parameters params = mCameraDevice.getParameters();

				List<String> focusModes = params.getSupportedFocusModes();
				if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
					// Autofocus mode is supported

					// set the focus mode
					params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
					// set Camera parameters
					mCameraDevice.setParameters(params);
				}
			}
		}
		
		public void surfaceCreated(SurfaceHolder holder) {
		// Make sure we have a surface in the holder before proceeding.
		        if (holder.getSurface() == null) {
		            Log.d(TAG, "holder.getSurface() == null");
		            return;
		        }
		       
		        mSurfaceHolder = holder;
		        
		        
		        if (mPausing) {
		            // We're pausing, the screen is off and we already stopped
		            // video recording. We don't want to start the camera again
		            // in this case in order to conserve power.
		            // The fact that surfaceChanged is called _after_ an onPause appears
		            // to be legitimate since in that case the lockscreen always returns
		            // to portrait orientation possibly triggering the notification.
		            return;
		        }

		        // The mCameraDevice will be null if it is fail to connect to the
		        // camera hardware. In this case we will show a dialog and then
		        // finish the activity, so it's OK to ignore it.
		        if (mCameraDevice == null) return;
		        
		        ownSurfaceView.surfaceCreated(holder);
		        
		        // Set preview display if the surface is being created. Preview was
		        // already started.
		        if (holder.isCreating()) {
		            setPreviewDisplay(holder);
		        } else {
		            stopVideoRecording();
		            restartPreview();
		        }
		    }

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if (mCameraDevice != null) {
				// If your preview can change or rotate, take care of those
				// events
				// here.
				// Make sure to stop the preview before resizing or reformatting
				// it.
			   
				if (holder.getSurface() == null) {
					// preview surface does not exist
					return;
				}
				mSurfaceHolder=holder;
				// stop preview before making changes
				try {
					mCameraDevice.stopPreview();
				} catch (Exception e) {
					e.printStackTrace();
					// ignore: tried to stop a non-existent preview
				}

				// make any resize, rotate or reformatting changes here
				// start preview with new settings
				try {
				        setPreViewCallBack();
					mCameraDevice.setPreviewDisplay(mSurfaceHolder);
					setCameraDisplayOrientation(VideoCapture.this, cameraId,
							mCameraDevice);
					mCameraDevice.startPreview();

				} catch (Exception e) {
					e.printStackTrace();
				}
				mPreviewing = true;

			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
		   mSurfaceHolder = null;
		   ownSurfaceView.surfaceDestory();
		}
	};

	private void showToast(String stMsg) {
		Toast.makeText(this, stMsg, Toast.LENGTH_LONG).show();
	}
	
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (myGestureDetector.onTouchEvent(event))
			return true;
		else
			return false;

	}

	public static int getDisplayRotation(Activity activity) {
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		switch (rotation) {
		case Surface.ROTATION_0:
			return 0;
		case Surface.ROTATION_90:
			return 90;
		case Surface.ROTATION_180:
			return 180;
		case Surface.ROTATION_270:
			return 270;
		}
		return 0;
	}

	public static int  setCameraDisplayOrientation(Activity activity,
			int cameraId, Camera camera) {
		// See android.hardware.Camera.setCameraDisplayOrientation for
		// documentation.
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int degrees = getDisplayRotation(activity);
		
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		
		camera.setDisplayOrientation(result);
		return result;
	}

	public void startSettingScreen() {
		Intent intent = new Intent(this, SettingsActivity.class);
		
		startActivityForResult(intent, SETTING_ACTIVITY);
		overridePendingTransition  (R.anim.right_slide_in, R.anim.right_slide_out);
               //this.overridePendingTransition(R.anim.left_right,android.R.anim.anticipate_overshoot_interpolator);
		
	}

	/*
	 * private final AutoFocusCallback mAutoFocusCallback = new
	 * AutoFocusCallback(); private final AutoFocusMoveCallback
	 * mAutoFocusMoveCallback = new AutoFocusMoveCallback();
	 * 
	 * private final class AutoFocusCallback implements
	 * android.hardware.Camera.AutoFocusCallback {
	 * 
	 * @Override public void onAutoFocus( boolean focused,
	 * android.hardware.Camera camera) {
	 * 
	 * 
	 * // mAutoFocusTime = System.currentTimeMillis() - mFocusStartTime; //
	 * Log.v(TAG, "mAutoFocusTime = " + mAutoFocusTime + "ms");
	 * //setCameraState(IDLE); // mFocusManager.onAutoFocus(focused); } }
	 * 
	 * private final class AutoFocusMoveCallback implements
	 * android.hardware.Camera.AutoFocusMoveCallback {
	 * 
	 * @Override public void onAutoFocusMoving( boolean moving,
	 * android.hardware.Camera camera) {
	 * //mFocusManager.onAutoFocusMoving(moving); } }
	 * 
	 * private void setAutoFocus(){
	 * 
	 * myCamera.autoFocus(mAutoFocusCallback);
	 * 
	 * myCamera.setAutoFocusMoveCallback(mAutoFocusMoveCallback);
	 * 
	 * } private void cancelAutoFocus(){
	 * 
	 * myCamera.cancelAutoFocus();
	 * 
	 * }
	 */

         @Override
         public boolean onKeyDown(int keyCode, KeyEvent event) {
      
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
               int exit_message_id = R.string.exit_message;
      
               showAlertDialog(Constants.Dialog.ALERT_APP_EXIT, R.string.exit_title,
                     exit_message_id, R.string.yes, R.string.no);
               return true;
            } else {
      
               return super.onKeyDown(keyCode, event);
            }
         }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constants.Dialog.ALERT_APP_EXIT:
			if (resultCode == Constants.Dialog.ALERT_RESULT_POS) {
				closeApp();
			} else {
				initDisplay();
			}
			break;
		case SETTING_ACTIVITY:
		   if(demoButton != null && demoButton.isChecked()){
		      dataInjector.connectDeviceAsDemo(mGraphView, mGraphView);
		   }
			break;

		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		//closeApp();
	}

	private void closeApp() {
		
		 LogReaderTask logReaderTask =LogReaderTask.getInstance(this);
		 if(logReaderTask != null){ 
		    logReaderTask.clear();
		    }
		 final VideoChat videoChat =VideoChat.getInstance(getApplicationContext());
		 videoChat.appExit();
		 
	   final DemoDataInjector dataInjector = DemoDataInjector.getInstance(this);
	   dataInjector.clear();
	   if(demoButton != null && demoButton.isChecked()){
	      settingManger.storeDemoMode(false, VideoCapture.this);
	   }
	   managedDevices.disconnectAll();
	   final PathRecorder pathRecorder = PathRecorder.getInstance();
	   pathRecorder.clear();
		setResult(RESULT_OK);
		finish();

	}
	
   
  
   private class MyOrientationEventListener extends OrientationEventListener {
      private int mOrientation;
      public MyOrientationEventListener(Context context) {
         super(context);
      }

      @Override
      public void onOrientationChanged(int orientation) {
         if (mMediaRecorderRecording) return;
         // We keep the last known orientation. So if the user first orient
         // the camera then point the camera to floor or sky, we still have
         // the correct orientation.
         if (orientation == ORIENTATION_UNKNOWN) return;
         mOrientation = roundOrientation(orientation);
        
      }
       public int getOrientation(){
          return mOrientation; 
       }
      public  int roundOrientation(int orientation) {
         return ((orientation + 45) / 90 * 90) % 360;
     }
      
   }

// from MediaRecorder.OnErrorListener
   public void onError(MediaRecorder mr, int what, int extra) {
       if (what == MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN) {
           // We may have run out of space on the sdcard.
           stopVideoRecording();
          // updateAndShowStorageHint(true);
       }
   }
   // from MediaRecorder.OnInfoListener
   public void onInfo(MediaRecorder mr, int what, int extra) {
       if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
           if (mMediaRecorderRecording) stopVideoRecording();
       } else if (what
               == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
           if (mMediaRecorderRecording) stopVideoRecording();

           // Show the toast.
           Toast.makeText(VideoCapture.this, R.string.video_reach_size_limit,
                          Toast.LENGTH_LONG).show();
       }
   }
   
   @Override
   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
     
   switch (buttonView.getId()) {
      

      default:
         break;
   }
      
   }
   public void restoreCallButton(final boolean checked){
      if(callButton != null){
         callButton.setChecked(checked);
      } 
     
   }
   /**
    * 
    */
   public OwnSurfaceView getOwnSurfaceView() {
     return ownSurfaceView;
     
      
   }
}

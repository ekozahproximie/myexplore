package com.trimble.camera;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CameraProfile;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GbCameraActivity extends Activity implements SurfaceHolder.Callback {
    
    
    private static final String TAG = "mycamera";
    private Parameters mParameters;
    private Parameters mInitialParams;
    
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder = null;
 
    // multiple cameras support
    private int mNumberOfCameras;
    private int mCameraId;
    
    private boolean mStartPreviewFail = false;
    private boolean mPreviewing;
    private boolean mPausing;
    
    private int mZoomValue=1;  // The current zoom value.
    
    
    // Focus mode. Options are pref_camera_focusmode_entryvalues.
    private String mFocusMode;
    private String mSceneMode;
    
    
    private android.hardware.Camera mCameraDevice;
    
    private SharedPreferences mPreferences;
    public static final String KEY_PICTURE_SIZE = "pref_camera_picturesize_key";
    public static final String KEY_COLOR_EFFECT = "pref_camera_coloreffect_key";
    public static final String KEY_WHITE_BALANCE = "pref_camera_whitebalance_key";
    public static final String KEY_SCENE_MODE = "pref_camera_scenemode_key";
    public static final String KEY_EXPOSURE = "pref_camera_exposure_key";
    public static final String KEY_FOCUS_MODE = "pref_camera_focusmode_key";
    public static final String KEY_FLASH_MODE = "pref_camera_flashmode_key";
    public static final String KEY_JPEG_QUALITY = "pref_camera_jpegquality_key";
    
    
    private static final int NOT_FOUND = -1;
    
    
    // The subset of parameters we need to update in setCameraParameters().
    private static final int UPDATE_PARAM_INITIALIZE = 1;
    private static final int UPDATE_PARAM_ZOOM = 2;
    private static final int UPDATE_PARAM_PREFERENCE = 4;
    private static final int UPDATE_PARAM_ALL = -1;

    private boolean mFirstTimeInitialized;
    private static final int FIRST_TIME_INIT = 2;
    private static final int RESTART_PREVIEW = 3;
    private static final int CLEAR_SCREEN_DELAY = 4;
    private static final int SET_CAMERA_PARAMETERS_WHEN_IDLE = 5;
    
    private static final int SCREEN_DELAY = 2 * 60 * 1000;
    
    
    private static final int FOCUS_NOT_STARTED = 0;
    private static final int FOCUSING = 1;
    private static final int FOCUSING_SNAP_ON_FINISH = 2;
    private static final int FOCUS_SUCCESS = 3;
    private static final int FOCUS_FAIL = 4;
    private int mFocusState = FOCUS_NOT_STARTED;

    
    private final Handler mHandler = new MainHandler();
    
    // When setCameraParametersWhenIdle() is called, we accumulate the subsets
    // needed to be updated in mUpdateSet.
    private int mUpdateSet;
    
    
    /**
     * This Handler is used to post message back onto the main thread of the
     * application
     */
    private class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RESTART_PREVIEW: {
                    restartPreview();
                   
                    break;
                }

                case CLEAR_SCREEN_DELAY: {
                    getWindow().clearFlags(
                            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    break;
                }

                case FIRST_TIME_INIT: {
                    //initializeFirstTime();
                    break;
                }

                case SET_CAMERA_PARAMETERS_WHEN_IDLE: {
                   setCameraParametersWhenIdle(0);
                    break;
                }
            }
        }
    }
   
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_preview);
        
        // Find the total number of cameras available
        mNumberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
            for (int i = 0; i < mNumberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                    mCameraId = i;
                }
            }
            
            /*
             * To reduce startup time, we start the preview in another thread.
             * We make sure the preview is started at the end of onCreate.
             */
            Thread startPreviewThread = new Thread(new Runnable() {
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
            
            // don't set mSurfaceHolder here. We have it set ONLY within
            // surfaceChanged / surfaceDestroyed, other parts of the code
            // assume that when it is set, the surface is also set.
            SurfaceHolder holder = mSurfaceView.getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

           
            // Make sure preview is started.
            try {
                startPreviewThread.join();
                if (mStartPreviewFail) {
                   // showCameraErrorAndFinish();
                    return;
                }
            } catch (InterruptedException ex) {
                // ignore
            }
            
            
    }
    private void startPreview() throws CameraHardwareException {
        if (mPausing || isFinishing()) return;

        ensureCameraDevice();

        // If we're previewing already, stop the preview first (this will blank
        // the screen).
        if (mPreviewing) stopPreview();

        setPreviewDisplay(mSurfaceHolder);
        setCameraDisplayOrientation(this, mCameraId, mCameraDevice);
        setCameraParameters(UPDATE_PARAM_ALL);

        mCameraDevice.setErrorCallback(mErrorCallback);

        try {
            Log.v(TAG, "startPreview");
            mCameraDevice.startPreview();
        } catch (Throwable ex) {
            closeCamera();
            throw new RuntimeException("startPreview failed", ex);
        }
        mPreviewing = true;
        mStatus = IDLE;
    }
    private void ensureCameraDevice() throws CameraHardwareException {
        if (mCameraDevice == null) {
            mCameraDevice = CameraHolder.instance().open(mCameraId);
            mInitialParams = mCameraDevice.getParameters();
        }
    }
    private void stopPreview() {
        if (mCameraDevice != null && mPreviewing) {
            Log.v(TAG, "stopPreview");
            mCameraDevice.stopPreview();
        }
        mPreviewing = false;
        // If auto focus was in progress, it would have been canceled.
     
    }
 // We separate the parameters into several subsets, so we can update only
    // the subsets actually need updating. The PREFERENCE set needs extra
    // locking because the preference can be changed from GLThread as well.
    private void setCameraParameters(int updateSet) {
        mParameters = mCameraDevice.getParameters();

        if ((updateSet & UPDATE_PARAM_INITIALIZE) != 0) {
            updateCameraParametersInitialize();
        }

        if ((updateSet & UPDATE_PARAM_ZOOM) != 0) {
            updateCameraParametersZoom();
        }

        if ((updateSet & UPDATE_PARAM_PREFERENCE) != 0) {
            updateCameraParametersPreference();
        }

        mCameraDevice.setParameters(mParameters);
    }
    
    private void updateCameraParametersPreference() {
        // Set picture size.
        String pictureSize = mPreferences.getString(
                KEY_PICTURE_SIZE, null);
        if (pictureSize == null) {
            initialCameraPictureSize(this, mParameters);
        } else {
            List<Size> supported = mParameters.getSupportedPictureSizes();
            setCameraPictureSize(
                    pictureSize, supported, mParameters);
        }

        // Set the preview frame aspect ratio according to the picture size.
        Size size = mParameters.getPictureSize();
        PreviewFrameLayout frameLayout =
                (PreviewFrameLayout) findViewById(R.id.frame_layout);
        frameLayout.setAspectRatio((double) size.width / size.height);
        
        // Set a preview size that is closest to the viewfinder height and has
        // the right aspect ratio.
        List<Size> sizes = mParameters.getSupportedPreviewSizes();
        Size optimalSize = getOptimalPreviewSize(
                sizes, (double) size.width / size.height);
        if (optimalSize != null) {
            Size original = mParameters.getPreviewSize();
            if (!original.equals(optimalSize)) {
                mParameters.setPreviewSize(optimalSize.width, optimalSize.height);

                // Zoom related settings will be changed for different preview
                // sizes, so set and read the parameters to get lastest values
                mCameraDevice.setParameters(mParameters);
                mParameters = mCameraDevice.getParameters();
            }
        }

        // Since change scene mode may change supported values,
        // Set scene mode first,
        mSceneMode = mPreferences.getString(
                KEY_SCENE_MODE,
                getString(R.string.pref_camera_scenemode_default));
        if (isSupported(mSceneMode, mParameters.getSupportedSceneModes())) {
            if (!mParameters.getSceneMode().equals(mSceneMode)) {
                mParameters.setSceneMode(mSceneMode);
                mCameraDevice.setParameters(mParameters);

                // Setting scene mode will change the settings of flash mode,
                // white balance, and focus mode. Here we read back the
                // parameters, so we can know those settings.
                mParameters = mCameraDevice.getParameters();
            }
        } else {
            mSceneMode = mParameters.getSceneMode();
            if (mSceneMode == null) {
                mSceneMode = Parameters.SCENE_MODE_AUTO;
            }
        }

        // Set JPEG quality.
        String jpegQuality = mPreferences.getString(
                KEY_JPEG_QUALITY,
                getString(R.string.pref_camera_jpegquality_default));
        mParameters.setJpegQuality(getQualityNumber(jpegQuality));

        // For the following settings, we need to check if the settings are
        // still supported by latest driver, if not, ignore the settings.

        // Set color effect parameter.
        String colorEffect = mPreferences.getString(
                KEY_COLOR_EFFECT,
                getString(R.string.pref_camera_coloreffect_default));
        if (isSupported(colorEffect, mParameters.getSupportedColorEffects())) {
            mParameters.setColorEffect(colorEffect);
        }

        // Set exposure compensation
        String exposure = mPreferences.getString(
                KEY_EXPOSURE,
                getString(R.string.pref_exposure_default));
        try {
            int value = Integer.parseInt(exposure);
            int max = mParameters.getMaxExposureCompensation();
            int min = mParameters.getMinExposureCompensation();
            if (value >= min && value <= max) {
                mParameters.setExposureCompensation(value);
            } else {
                Log.w(TAG, "invalid exposure range: " + exposure);
            }
        } catch (NumberFormatException e) {
            Log.w(TAG, "invalid exposure: " + exposure);
        }

        

        if (Parameters.SCENE_MODE_AUTO.equals(mSceneMode)) {
            // Set flash mode.
            String flashMode = mPreferences.getString(
                    KEY_FLASH_MODE,
                    getString(R.string.pref_camera_flashmode_default));
            List<String> supportedFlash = mParameters.getSupportedFlashModes();
            if (isSupported(flashMode, supportedFlash)) {
                mParameters.setFlashMode(flashMode);
            } else {
                flashMode = mParameters.getFlashMode();
                if (flashMode == null) {
                    flashMode = getString(
                            R.string.pref_camera_flashmode_no_flash);
                }
            }

            // Set white balance parameter.
            String whiteBalance = mPreferences.getString(
                    KEY_WHITE_BALANCE,
                    getString(R.string.pref_camera_whitebalance_default));
            if (isSupported(whiteBalance,
                    mParameters.getSupportedWhiteBalance())) {
                mParameters.setWhiteBalance(whiteBalance);
            } else {
                whiteBalance = mParameters.getWhiteBalance();
                if (whiteBalance == null) {
                    whiteBalance = Parameters.WHITE_BALANCE_AUTO;
                }
            }

            // Set focus mode.
            mFocusMode = mPreferences.getString(
                    KEY_FOCUS_MODE,
                    getString(R.string.pref_camera_focusmode_default));
            if (isSupported(mFocusMode, mParameters.getSupportedFocusModes())) {
                mParameters.setFocusMode(mFocusMode);
            } else {
                mFocusMode = mParameters.getFocusMode();
                if (mFocusMode == null) {
                    mFocusMode = Parameters.FOCUS_MODE_AUTO;
                }
            }
        } else {
            mFocusMode = mParameters.getFocusMode();
        }
    }
    private void updateCameraParametersInitialize() {
        // Reset preview frame rate to the maximum because it may be lowered by
        // video camera application.
        List<Integer> frameRates = mParameters.getSupportedPreviewFrameRates();
        if (frameRates != null) {
            Integer max = Collections.max(frameRates);
            mParameters.setPreviewFrameRate(max);
        }

    }
    private void updateCameraParametersZoom() {
        // Set zoom.
        if (mParameters.isZoomSupported()) {
            mParameters.setZoom(mZoomValue);
        }
    }
    public  void initialCameraPictureSize(
            Context context, Parameters parameters) {
        // When launching the camera app first time, we will set the picture
        // size to the first one in the list defined in "arrays.xml" and is also
        // supported by the driver.
        List<Size> supported = parameters.getSupportedPictureSizes();
        if (supported == null) return;
        for (String candidate : context.getResources().getStringArray(
                com.trimble.camera.R.array.pref_camera_picturesize_entryvalues)) {
            if (setCameraPictureSize(candidate, supported, parameters)) {
                SharedPreferences.Editor editor = 
                        mPreferences.edit();
                editor.putString(KEY_PICTURE_SIZE, candidate);
                editor.apply();
                return;
            }
        }
        Log.e(TAG, "No supported picture size found");
    }
    private Size getOptimalPreviewSize(List<Size> sizes, double targetRatio) {
        final double ASPECT_TOLERANCE = 0.05;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Because of bugs of overlay and layout, we sometimes will try to
        // layout the viewfinder in the portrait orientation and thus get the
        // wrong size of mSurfaceView. When we change the preview size, the
        // new overlay will be created before the old one closed, which causes
        // an exception. For now, just get the screen size

        Display display = getWindowManager().getDefaultDisplay();
        int targetHeight = Math.min(display.getHeight(), display.getWidth());

        if (targetHeight <= 0) {
            // We don't know the size of SurefaceView, use screen height
            WindowManager windowManager = (WindowManager)
                    getSystemService(Context.WINDOW_SERVICE);
            targetHeight = windowManager.getDefaultDisplay().getHeight();
        }

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            Log.v(TAG, "No preview size match the aspect ratio");
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
    public  boolean setCameraPictureSize(
            String candidate, List<Size> supported, Parameters parameters) {
        int index = candidate.indexOf('x');
        if (index == NOT_FOUND) return false;
        int width = Integer.parseInt(candidate.substring(0, index));
        int height = Integer.parseInt(candidate.substring(index + 1));
        for (Size size: supported) {
            if (size.width == width && size.height == height) {
                parameters.setPictureSize(width, height);
                return true;
            }
        }
        return false;
    }
    
    private void setPreviewDisplay(SurfaceHolder holder) {
        try {
            mCameraDevice.setPreviewDisplay(holder);
        } catch (Throwable ex) {
            closeCamera();
            throw new RuntimeException("setPreviewDisplay failed", ex);
        }
    }
    public  int getDisplayRotation(Activity activity) {
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
    public  void setCameraDisplayOrientation(Activity activity,
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
    private void closeCamera() {
        if (mCameraDevice != null) {
            CameraHolder.instance().release();
            mCameraDevice.setZoomChangeListener(null);
            mCameraDevice = null;
            mPreviewing = false;
        }
    }
    private  boolean isSupported(String value, List<String> supported) {
        return supported == null ? false : supported.indexOf(value) >= 0;
    }
    // Use the ErrorCallback to capture the crash count
    // on the mediaserver
    private final ErrorCallback mErrorCallback = new ErrorCallback();
    private static final class ErrorCallback
    implements android.hardware.Camera.ErrorCallback {
    public void onError(int error, android.hardware.Camera camera) {
        if (error == android.hardware.Camera.CAMERA_ERROR_SERVER_DIED) {
             
             Log.v(TAG, "media server died");
        }
    }
}
    private boolean restartPreview() {
        try {
            startPreview();
        } catch (CameraHardwareException e) {
            //showCameraErrorAndFinish();
            return false;
        }
        return true;
    }
    
    // Retrieve and return the Jpeg encoding quality number
    // for the given quality level.
    public  int getQualityNumber(String jpegQuality) {
        final int DEFAULT_QUALITY = 85;
        HashMap<String, Integer> mHashMap =
                new HashMap<String, Integer>();
        mHashMap.put("normal",    CameraProfile.QUALITY_LOW);
        mHashMap.put("fine",      CameraProfile.QUALITY_MEDIUM);
        mHashMap.put("superfine", CameraProfile.QUALITY_HIGH);
        Integer quality = mHashMap.get(jpegQuality);
        if (quality == null) {
            Log.w(TAG, "Unknown Jpeg quality: " + jpegQuality);
            return DEFAULT_QUALITY;
        }
        return CameraProfile.getJpegEncodingQualityParameter(quality.intValue());
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2, int arg3) {
        // Make sure we have a surface in the holder before proceeding.
        if (holder.getSurface() == null) {
            Log.d(TAG, "holder.getSurface() == null");
            return;
        }

        // We need to save the holder for later use, even when the mCameraDevice
        // is null. This could happen if onResume() is invoked after this
        // function.
        mSurfaceHolder = holder;

        // The mCameraDevice will be null if it fails to connect to the camera
        // hardware. In this case we will show a dialog and then finish the
        // activity, so it's OK to ignore it.
        if (mCameraDevice == null) return;

        // Sometimes surfaceChanged is called after onPause or before onResume.
        // Ignore it.
        if (mPausing || isFinishing()) return;

        if (mPreviewing && holder.isCreating()) {
            // Set preview display if the surface is being created and preview
            // was already started. That means preview display was set to null
            // and we need to set it now.
            setPreviewDisplay(holder);
        } else {
            // 1. Restart the preview if the size of surface was changed. The
            // framework may not support changing preview display on the fly.
            // 2. Start the preview now if surface was destroyed and preview
            // stopped.
            restartPreview();
        }

        // If first time initialization is not finished, send a message to do
        // it later. We want to finish surfaceChanged as soon as possible to let
        // user see preview first.
        if (!mFirstTimeInitialized) {
            mHandler.sendEmptyMessage(FIRST_TIME_INIT);
        } else {
            //initializeSecondTime();
        }
        
    }
    @Override
    protected void onPause() {
        mPausing = true;
        stopPreview();
        // Close the camera now because other activities may need to use it.
        closeCamera();
        resetScreenOn();
        // Remove the messages in the event queue.
        mHandler.removeMessages(RESTART_PREVIEW);
        mHandler.removeMessages(FIRST_TIME_INIT);
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        mPausing = false;
       
        mZoomValue = 0;
    }
    private void resetScreenOn() {
        mHandler.removeMessages(CLEAR_SCREEN_DELAY);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    private void keepScreenOnAwhile() {
        mHandler.removeMessages(CLEAR_SCREEN_DELAY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mHandler.sendEmptyMessageDelayed(CLEAR_SCREEN_DELAY, SCREEN_DELAY);
    }
    // If the Camera is idle, update the parameters immediately, otherwise
    // accumulate them in mUpdateSet and update later.
    private void setCameraParametersWhenIdle(int additionalUpdateSet) {
        mUpdateSet |= additionalUpdateSet;
        if (mCameraDevice == null) {
            // We will update all the parameters when we open the device, so
            // we don't need to do anything now.
            mUpdateSet = 0;
            return;
        } else if (isCameraIdle()) {
            setCameraParameters(mUpdateSet);
            mUpdateSet = 0;
        } else {
            if (!mHandler.hasMessages(SET_CAMERA_PARAMETERS_WHEN_IDLE)) {
                mHandler.sendEmptyMessageDelayed(
                        SET_CAMERA_PARAMETERS_WHEN_IDLE, 1000);
            }
        }
    }
    private static final int IDLE = 1;
    private int mStatus = IDLE;
    private boolean isCameraIdle() {
        return mStatus == IDLE && mFocusState == FOCUS_NOT_STARTED;
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
       
        
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
        mSurfaceHolder = null;
        
    }
}
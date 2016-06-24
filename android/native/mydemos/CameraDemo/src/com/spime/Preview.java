package com.spime;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class Preview extends SurfaceView implements SurfaceHolder.Callback {

    /**
     * @param context
     * @param attrs
     */
    public Preview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private static final String TAG = "Preview";
    SurfaceHolder mHolder;
    public Camera camera;

    Preview(Context context) {
        super(context);		
        // Install a SurfaceHolder.Callback so we get notified when the		
        // underlying surface is created and destroyed.		
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

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

    public void surfaceCreated(SurfaceHolder holder) {		
        // The Surface has been created, acquire the camera and tell it where		
        // to draw.		
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(new PreviewCallback() { // <10>
                // Called for each frame previewed
                public void onPreviewFrame(byte[] data, Camera camera) {  // <11>
                  Log.d(TAG, "onPreviewFrame called at: " + System.currentTimeMillis());
                  Preview.this.invalidate();  // <12>
                }
              });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {		
        // Surface will be destroyed when we return, so stop the preview.		
        // Because the CameraDevice object is not a shared resource, it's very		
        // important to release it when the activity is paused.		
       if(camera != null){
    	camera.stopPreview();
        
        
       }
        
    }
   
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {		
        // Now that the size is known, set up the camera parameters and begin		
        // the preview.		
        Camera.Parameters parameters = camera.getParameters();
        List<Size> sizes = parameters.getSupportedPreviewSizes();
        Size optimalSize = getOptimalPreviewSize(sizes, w, h);
        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        parameters.setPictureSize(500, 500);
        parameters.setFlashMode("on");
        camera.setParameters(parameters);
        camera.startPreview();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setJpegQuality(50);
        parameters.setJpegThumbnailQuality(50);
        parameters.setPictureSize(1024, 1024);
        // mParameters.setPreviewFormat(ImageFormat.JPEG);
        parameters.setJpegThumbnailSize(256, 256);
        parameters.setPreviewSize(500, 500);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint p = new Paint(Color.RED);
        Log.d(TAG, "draw");
        canvas.drawText("PREVIEW", canvas.getWidth() / 2, canvas.getHeight() / 2, p);
    }
    
    
}


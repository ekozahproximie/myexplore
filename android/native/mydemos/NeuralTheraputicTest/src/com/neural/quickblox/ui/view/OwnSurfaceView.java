/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.neural.quickblox.ui.view
 *
 * File name:
 *	    OwnSurfaceView.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Nov 10, 201412:04:56 AM
 *
 * Abstract:
 *
 *
 * Environment:
 *  Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.neural.quickblox.ui.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @author sprabhu
 *
 */
public class OwnSurfaceView {

   
   private ProcessDataThread processCameraDataThread;
   private ConcurrentLinkedQueue<Runnable> cameraPreviewCallbackQueue;
   
   
   private Matrix rotationMatrixFront;
   private Matrix rotationMatrixBack;
   
   private int currentCameraId=0;
   private final int IMAGE_QUALITY = 25;
   private int FPS = 4; // by default 4 fps
   
   private volatile CameraDataListener cameraDataListener;
   
   /**
    * 
    */
   public OwnSurfaceView( int iOrientation,final int cameraId) {
      cameraPreviewCallbackQueue = new ConcurrentLinkedQueue<Runnable>();
      currentCameraId = (cameraId + 1) % Camera.getNumberOfCameras();
      if(iOrientation == 0){
         iOrientation = 1;
      }
     // Log.i("test", "iOrientation :"+iOrientation);
      
      rotationMatrixFront = new Matrix();
      rotationMatrixFront.postRotate(iOrientation);
      rotationMatrixBack = new Matrix();
      rotationMatrixBack.postRotate(-iOrientation);
     
   }
   public void setCameraDataListener(CameraDataListener cameraDataListener) {
      this.cameraDataListener = cameraDataListener;
  }
   private class ProcessDataThread extends Thread {

      private boolean isRunning;

      public ProcessDataThread() {
          this.isRunning = true;
      }

      @Override
      public void run() {
          while (isRunning) {
             
              if (!cameraPreviewCallbackQueue.isEmpty()) {
             
                  Runnable runnable = cameraPreviewCallbackQueue.poll();
                  if (runnable != null) {
                      runnable.run();
                  }

                  try {
                      Thread.sleep(1000 / FPS);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
              }
          }
      }

      public void stopProcessing() {
          this.isRunning = false;
      }
  }
   
   public void surfaceCreated(SurfaceHolder holder) {
       

       Log.w("MySurfaceView", "surfaceCreated");

       

       processCameraDataThread = new ProcessDataThread();
       processCameraDataThread.setName("ProcessDataThread");
       processCameraDataThread.start();
   }
   public void surfaceDestory(){
      if(processCameraDataThread == null){
         return;
      }
      boolean retry = true;
      // close thread
      processCameraDataThread.stopProcessing();
      while (retry) {
          try {
              processCameraDataThread.join();
              retry = false;
          } catch (InterruptedException e) {
              // try again
          }
      }   
   }
   
   public void onPreviewFrame(byte[] data, Camera camera) {

       Camera.Parameters params = camera.getParameters();
       processCameraData(data, params.getPreviewSize().width, params.getPreviewSize().height);
   }
   
   private void processCameraData(final byte[] cameraData, final int imageWidth, final int imageHeight) {
      
      cameraPreviewCallbackQueue.clear();
      boolean offerSuccess = cameraPreviewCallbackQueue.offer(new Runnable() {
          @Override
          public void run() {
              long start = System.nanoTime();

              // Convert data to JPEG and compress
              YuvImage image = new YuvImage(cameraData, ImageFormat.NV21, imageWidth, imageHeight, null);
              ByteArrayOutputStream out = new ByteArrayOutputStream();
              Rect area = new Rect(0, 0, imageWidth, imageHeight);
              image.compressToJpeg(area, IMAGE_QUALITY, out);
              byte[] jpegVideoFrameData = out.toByteArray();
              
              // rotate image
              byte[] rotatedCameraData = rotateImage(jpegVideoFrameData, imageWidth, imageHeight, currentCameraId);
              if (rotatedCameraData.length == 0) {
                 
                  return;
              }

              // send data to the opponent
              //
              
              if(cameraDataListener != null){
                 cameraDataListener.onCameraDataReceive(rotatedCameraData);
              }
              //
              //

              // close stream
              try {
                  out.close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      });
  }

  private byte[] rotateImage(byte[] cameraData, final int imageWidth, final int imageHeight, int currentCameraId) {
      Bitmap landscapeCameraDataBitmap = BitmapFactory.decodeByteArray(cameraData, 0, cameraData.length);

      Bitmap portraitBitmap = null;
      if(currentCameraId == getCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)) { // front camera
          portraitBitmap = Bitmap.createBitmap(landscapeCameraDataBitmap, 0, 0, imageWidth, imageHeight, rotationMatrixFront, true);
      }else{ // back camera
          portraitBitmap = Bitmap.createBitmap(landscapeCameraDataBitmap, 0, 0, imageWidth, imageHeight, rotationMatrixBack, true);
      }

      landscapeCameraDataBitmap.recycle();
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      if (!portraitBitmap.isRecycled()) {
          portraitBitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, stream);
          byte[] portraitCameraData = stream.toByteArray();
          portraitBitmap.recycle();
          return portraitCameraData;
      } else {
          return new byte[0];
      }
  }

  private int getCameraId(final int facing) {
      int numberOfCameras = Camera.getNumberOfCameras();
      Camera.CameraInfo info = new Camera.CameraInfo();
      for (int id = 0; id < numberOfCameras; id++) {
          Camera.getCameraInfo(id, info);
          if (info.facing == facing) {
              return id;
          }
      }
      return -1;
  }

   public interface CameraDataListener{
      public void onCameraDataReceive(byte[] data);
  }
}

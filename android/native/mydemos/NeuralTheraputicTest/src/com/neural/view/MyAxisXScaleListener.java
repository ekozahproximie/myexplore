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
 *      com.neural.view
 *
 * File name:
 *	    MyAxisXScaleListener.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Mar 30, 20141:08:35 PM
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
package com.neural.view;

import android.graphics.Matrix;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;

import com.neural.setting.SettingsManager;


/**
 * @author sprabhu
 *
 */
public class MyAxisXScaleListener extends SimpleOnScaleGestureListener {
   private transient float scale = 1f;
   
   private static float MIN_ZOOM = SettingsManager.MIN_TIME_SCALE;
  
   private static float MAX_ZOOM = SettingsManager.MAX_TIME_SCALE + SettingsManager.MIN_TIME_SCALE;
   
   private transient Matrix matrix = new Matrix();
   
   private transient  float[] fMatrixMap = new float[9];
   
   private MyGestureDetectorCompat gestureDetectorCompat =null;
   /**
    * 
    */
   public MyAxisXScaleListener(final MyGestureDetectorCompat gestureDetectorCompat) {
      this.gestureDetectorCompat=gestureDetectorCompat;
   }

   @Override
   public boolean onScaleBegin(ScaleGestureDetector detector) {
      return super.onScaleBegin(detector);
   }
 
   @Override
   public void onScaleEnd(ScaleGestureDetector detector) {
      super.onScaleEnd(detector);
   }
   @Override
   public boolean onScale(ScaleGestureDetector detector) {
   
      boolean isXScale=false;
    
      
    
      scale /= detector.getScaleFactor();
      scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));
      matrix.setScale(scale, scale);
      
      matrix.getValues(fMatrixMap);
      float scaleY = fMatrixMap[Matrix.MSCALE_Y];
      float scaleX = fMatrixMap[Matrix.MSCALE_X];
      
      if(gestureDetectorCompat != null){
         if(gestureDetectorCompat.isScaleStarted() && gestureDetectorCompat.isScaleOnHorizontal()){
            gestureDetectorCompat.setTimeBase((int)scale);
          //Log.d("test", " scale on x :"+scaleX );
            isXScale=true;
         }
      }
      return isXScale;
   }
}

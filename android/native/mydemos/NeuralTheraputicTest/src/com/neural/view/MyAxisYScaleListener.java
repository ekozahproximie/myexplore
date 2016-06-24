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
 *	    MyAxisYScaleListener.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Mar 30, 20144:41:07 PM
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

import com.neural.fragment.RehabFragment;
import com.neural.sensor.NtDeviceManagement;
import com.neural.setting.SettingsManager;


/**
 * @author sprabhu
 *
 */
public class MyAxisYScaleListener  extends SimpleOnScaleGestureListener {
   private transient float scale = 2f;
   
   private static float MIN_ZOOM = SettingsManager.MIN_EMG_OFF_SCALE + 1;
  
   private static float MAX_ZOOM = SettingsManager.MAX_EMG_OFF_SCALE ;
   
   private transient Matrix matrix = new Matrix();
   
   private transient  float[] fMatrixMap = new float[9];
   
   private MyGestureDetectorCompat gestureDetectorCompat =null;
   /**
    * 
    */
   public MyAxisYScaleListener(final MyGestureDetectorCompat gestureDetectorCompat) {
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
   
      boolean isYScale=false;
      
      float dscaleFactor= detector.getScaleFactor();
     
      //Log.d("test", " scale:"+detector.getCurrentSpanY() +","+detector.getPreviousSpanY()+"," +detector.getScaleFactor());
      scale = (scale  )/dscaleFactor;
     
      scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));
      matrix.setScale(scale, scale);
     //Log.d("test", " scale on y:"+scale );
      matrix.getValues(fMatrixMap);
      float scaleY = fMatrixMap[Matrix.MSCALE_Y];
      float scaleX = fMatrixMap[Matrix.MSCALE_X];
      
      if(gestureDetectorCompat != null){
         if(gestureDetectorCompat.isScaleStarted() && ! gestureDetectorCompat.isScaleOnHorizontal()){
           // Log.d("test", " scale on y :"+scaleY );
          //  gestureDetectorCompat.setEMGScale((int)scale,"Left Bicep");
            isYScale=true;
         }
      }
      return isYScale;
   }
}

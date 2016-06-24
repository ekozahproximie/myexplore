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
 *	    MyGestureDetectorCompat.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Mar 30, 20142:59:56 PM
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

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.OnGestureListener;

import com.neural.setting.SettingsManager;




/**
 * @author sprabhu
 *
 */
public class MyGestureDetectorCompat extends GestureDetectorCompat {

   
   private boolean isScaleOnHorizontal=false; 
   
   private boolean isScaleStarted=false;
   
   private SettingsManager settingsManager =null;
   
   private Context context=null;
   
   
   /**
    * @param context
    * @param listener
    * @param handler
    */
   public MyGestureDetectorCompat(Context context, OnGestureListener listener,
         Handler handler) {
      super(context, listener, handler);
      
   }

   /**
    * @param context
    * @param listener
    */
   public MyGestureDetectorCompat(Context context, OnGestureListener listener
         ) {
      super(context, listener);
      this.context=context;
      this.settingsManager=SettingsManager.getInstance();;
   }
   
   
   /**
    * @return the isScaleOnHorizontal
    */
   public boolean isScaleOnHorizontal() {
      return isScaleOnHorizontal;
   }
   
   
   /**
    * @param isScaleOnHorizontal the isScaleOnHorizontal to set
    */
   public void setScaleOnHorizontal(boolean isScaleOnHorizontal) {
      this.isScaleOnHorizontal = isScaleOnHorizontal;
   }
   
   /**
    * @param isScaleStarted the isScaleStarted to set
    */
   public void setScaleStarted(boolean isScaleStarted) {
      this.isScaleStarted = isScaleStarted;
   }
   
   /**
    * @return the isScaleStarted
    */
   public boolean isScaleStarted() {
      return isScaleStarted;
   }
   
   public void setTimeBase(final int timebase){
      settingsManager.storeTimeBase(timebase, context);
   }
   public void setEMGScale(final int  emgScale,final String stKey){
      settingsManager.storeEMGScale(emgScale, context,stKey);
   }
}

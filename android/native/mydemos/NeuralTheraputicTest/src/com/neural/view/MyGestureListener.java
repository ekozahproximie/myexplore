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
 *	    MyGestureListener.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Mar 30, 20143:03:16 PM
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

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.neural.demo.VideoCapture;


/**
 * @author sprabhu
 *
 */
public class MyGestureListener extends SimpleOnGestureListener {

   
   private transient float fOldx=0;
   private transient float fOldy=0;
   private transient float fOldx1=0;
   private transient float fOldy1=0;
   
   
   private static final  int DISTANCE_OF_SCALE=60;
   
   private static final int SWIPE_MIN_DISTANCE = 120;
   private static final int SWIPE_MAX_OFF_PATH = 250;
   private static final int SWIPE_THRESHOLD_VELOCITY = 200;
   
   private VideoCapture videoCapture =null;
   
   private MyGestureDetectorCompat myDetectorCompat =null;
   
   /**
    * 
    */
   public MyGestureListener(VideoCapture videoCapture) {
    this.videoCapture=videoCapture;
   }
   @Override
   public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                   float velocityY) {
   //   Log.d("test", "onFling: " + e1.toString()+e2.toString());
         /*  try {
                   if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                           return false;
                   // right to left swipe
                   if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                                   && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                      videoCapture.startSettingScreen();
                   } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                   && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                           // left to right swipe
                           // showToast("left to right swipe");
                   }
           } catch (Exception e) {
                   // nothing
           }*/
           return false;//super.onFling(e1, e2, velocityX, velocityY);
   }
   @Override
   public boolean onDown(MotionEvent event) { 
      clear();
      myDetectorCompat.setScaleStarted(true);
      //Log.d("test","onDown: " ); 
      return true;
       
   }
    private void clear(){
       fOldx=0;
       fOldx1=0;
       fOldy=0;
       fOldy1=0;
    }

      @Override
       public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
               float distanceY) {
      
      if (fOldx == 0 && fOldy == 0){
         fOldx=e1.getX();
         fOldy=e1.getY();
      }
      if (fOldx1 == 0 && fOldy1 == 0){
         fOldx1=e2.getX();
         fOldy1=e2.getY();
      }
      
      final float fdx=Math.abs(fOldx  - e1.getX());
      final float fdx1=Math.abs(fOldx1 - e2.getX());
      
      if(fdx > DISTANCE_OF_SCALE || fdx1 > DISTANCE_OF_SCALE){
       //  Log.d("test", " scroll on x");
         myDetectorCompat.setScaleOnHorizontal(true);
         assignXY(e1, e2);    
      }
      
      final float fdy=Math.abs(fOldy- e1.getY());
      final float fdy1=Math.abs(fOldy1- e2.getY());
     
      if(fdy > DISTANCE_OF_SCALE || fdy1 > DISTANCE_OF_SCALE){
         myDetectorCompat.setScaleOnHorizontal(false);
        // Log.d("test", " scroll on y");
         assignXY(e1, e2);
      }
    
      
          
         // Log.d("test", "onScroll 2: " + fOldy+","+fOldy1);
       
         return true;
       }
      
      private void assignXY(final MotionEvent e1,final MotionEvent e2){
         fOldx=e1.getX();
         fOldy=e1.getY();
         
         fOldx1=e2.getX();
         fOldy1=e2.getY();
      }
      /**
       * @param myDetectorCompat the myDetectorCompat to set
       */
      public void setDetectorCompat(final MyGestureDetectorCompat detectorCompat) {
         this.myDetectorCompat = detectorCompat;
      }
}

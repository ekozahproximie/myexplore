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
 *	    PathRecorder.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     May 25, 201412:01:16 PM
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

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import java.util.Vector;


/**
 * @author sprabhu
 *
 */
public final class PathRecorder {

   
   
   
   private static PathRecorder pathRecorder = null;
   
   private boolean isCloseRecord=false;
   
   private Vector<PointF> vecPointFs = new  Vector<PointF>();
   /**
    * 
    */
   private PathRecorder() {
      if(pathRecorder != null){
         throw new IllegalAccessError("use getInstance method"); 
      }
   }

   public static synchronized PathRecorder getInstance(){
      if(pathRecorder == null){
         pathRecorder = new PathRecorder();
      }
      
      return pathRecorder;
   }
   
   public void addPoint(final PointF pointF){
      if(pointF != null){
         vecPointFs.add(pointF);
      }
   }
   public void stopRecord(final boolean isCloseRecord){
      this.isCloseRecord=isCloseRecord;
      //if(isCloseRecord)
      {
        // Log.i("test", "stopRecord:"+isCloseRecord);
      }
   }
   public void add(final float x,final float y,double lastKnownX ,double maxX){
      
      if(isCloseRecord){
         return;
      }
      if(vecPointFs.size() > 0 && maxX != 0  && lastKnownX >= maxX){
        // Log.i("test", "max point reached");
         isCloseRecord=true;
      }
     // Log.i("test", "addPoint");
      final PointF pointF = new PointF(x, y);
      addPoint(pointF);
   }
   
   public void getPathPoints(final Path mFreezePath){
      if(mFreezePath != null){
         mFreezePath.reset();
         int i=0;
         for (PointF pointF:vecPointFs) {
            if(i++ == 0){
               mFreezePath.moveTo(0,pointF.y);
            }else{
               mFreezePath.lineTo(pointF.x, pointF.y);
            }
         }
      }
   }
   
   public void getBoundaryPathPoints(final Path mFreezePath,final float  fYavlue){
      if(mFreezePath != null){
         mFreezePath.reset();
         int i=0;
         for (PointF pointF:vecPointFs) {
            if(i++ == 0){
               mFreezePath.moveTo(0,pointF.y +fYavlue);
            }else{
               mFreezePath.lineTo(pointF.x, pointF.y+fYavlue);
            }
         }
      }
   }
   
   public void getPreDrawnBoundaryPathPoints(final Path mFreezePath,final float  fYavlue){
      if(mFreezePath != null){
         mFreezePath.reset();
         int i=0;
         for (PointF pointF:vecPointFs) {
            if(i++ == 0){
               mFreezePath.moveTo(pointF.x,pointF.y +fYavlue);
            }else{
               mFreezePath.lineTo(pointF.x, pointF.y+fYavlue);
            }
         }
      }
   }
   public void add(final float x,final float y){
      if(isCloseRecord){
         return;
      }
      final PointF pointF = new PointF(x, y);
      addPoint(pointF);
   }
   
   /**
    * @return the isCloseRecord
    */
   public boolean isCloseRecord() {
      return isCloseRecord;
   }
   public void clear(){
      //Log.i("test", "clear");
      vecPointFs.clear();
      stopRecord(false);
   }
}

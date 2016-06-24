/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 * 
 *
 * Module Name: com.neural.view
 *
 * File name: MultiTouchView.java
 *
 * Author: sprabhu
 *
 * Created On: 17-Jan-201512:58:34 pm
 *
 * Abstract:
 *
 *
 * Environment: Mobile Profile : Mobile Configuration :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.neural.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.neural.demo.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 * @author sprabhu
 *
 */

public class MultiTouchView extends View {

   private Paint            circlePaint               = new Paint(
                                                      Paint.ANTI_ALIAS_FLAG);

   private Paint            linePaint           = new Paint(
                                                      Paint.ANTI_ALIAS_FLAG);
   
   private Paint            scorePaint           = new Paint(
         Paint.ANTI_ALIAS_FLAG);
   
   private String stTopScore=null;
   
   private String stCurrentScore=null;
   
   

   final int                MAX_NUMBER_OF_POINT = 10;
   private PointSort        pointSort           = null;
   private MyPoints         baryCenter          = null;
   private Vector<MyPoints> vecPointFs          = null;
   private Path             mPath               = null;
   private transient int iRadius= 50;
   private Rect bounds = new Rect();
   
   private double dTopArea=0;
   
   private double dCurrentArea=0;

   public MultiTouchView(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
      init(context);
   }

   public MultiTouchView(Context context, AttributeSet attrs) {
      super(context, attrs);
      init(context);
   }

   public MultiTouchView(Context context) {
      super(context);
      init(context);
   }

   void init(final Context context) {
      circlePaint.setStyle(Paint.Style.FILL);
      circlePaint.setStrokeWidth(1);
      circlePaint.setColor(Color.YELLOW);

      linePaint.setColor(0XFF7BC7F2);
      linePaint.setStyle(Paint.Style.FILL);
      linePaint.setStrokeWidth(1);
      
      scorePaint.setColor(Color.BLACK);
      scorePaint.setFakeBoldText(true);
      scorePaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.score_text_size));
      scorePaint.setStyle(Style.STROKE);
      scorePaint.setStrokeCap(Cap.ROUND);
      scorePaint.setStrokeJoin(Join.ROUND);
      
      mPath = new Path();
      pointSort = new PointSort();
      vecPointFs = new Vector<MyPoints>(MAX_NUMBER_OF_POINT);
      for (int i =0;i< MAX_NUMBER_OF_POINT;i++) {
            vecPointFs.add(new MyPoints());
         
      }
      stTopScore=getResources().getString(R.string.Top_score);
      stCurrentScore=getResources().getString(R.string.Current_score);
      iRadius=getResources().getDimensionPixelSize(R.dimen.touch_radius);
   }

   @Override
   protected synchronized void onDraw(Canvas canvas) {
      canvas.drawColor(0xEFDFDFDF);
      boolean isTouch = false;
      for (int i = 0; i < vecPointFs.size(); i++) {
         final MyPoints myPoints = vecPointFs.get(i);
         if (myPoints.isTouching && 
               myPoints.x != 0 && myPoints.y != 0) {
            isTouch = true;
            canvas.drawCircle(myPoints.x, myPoints.y, iRadius, circlePaint);
         }
      }
      if (isTouch) {
         canvas.drawPath(mPath, linePaint);
      } else {
         mPath.reset();
      }
      final String sttopScore=String.format(stTopScore, dTopArea);
      final String stcurrentScore=String.format(stCurrentScore, dCurrentArea);
       int iTextWidth= (int)scorePaint.measureText(sttopScore);
       int iHeight= (int) ( Math.abs(scorePaint.ascent()) + Math.abs(scorePaint.descent()));
      int xPos = (canvas.getWidth() -iTextWidth );
      int yPos = (int)  getTop() +iHeight ; 
      canvas.drawText(sttopScore, xPos, yPos, scorePaint);
      
      
       iTextWidth= (int)scorePaint.measureText(stcurrentScore);
       iHeight= (int) ( Math.abs(scorePaint.ascent()) + Math.abs(scorePaint.descent()));
      xPos = (canvas.getWidth() -iTextWidth );
      yPos = (int) yPos+ +iHeight ; 
      //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.

      canvas.drawText(stcurrentScore, xPos, yPos, scorePaint);
   }

   @Override
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

      setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec));
   }

   private class PointSort implements Comparator<MyPoints> {

      @Override
      public int compare(MyPoints lhs, MyPoints rhs) {
         if (lhs.isTouching == false || rhs.isTouching == false) {
            return 0;
         }
         if (lhs.angle < rhs.angle) {
            return -1;
         } else if (lhs.angle > rhs.angle) {
            return 1;
         } else {
            return 0;

         }

      }

   }

   @Override
   public synchronized boolean onTouchEvent(MotionEvent event) {
      int action = (event.getAction() & MotionEvent.ACTION_MASK);
      int pointCount = event.getPointerCount();
      
      for (int i = 0; i < pointCount; i++) {
         int id = event.getPointerId(i);
       
        
         // Ignore pointer higher than our max.
         if (id < MAX_NUMBER_OF_POINT && id < vecPointFs.size()) {
            final MyPoints myPoints = vecPointFs.get(id);
           

            if ((action == MotionEvent.ACTION_DOWN)
                  || (action == MotionEvent.ACTION_POINTER_DOWN)
                  || (action == MotionEvent.ACTION_MOVE)) {

               myPoints.isTouching = true;
               myPoints.set((int) event.getX(i), (int) event.getY(i));
            } else {
               myPoints.isTouching = false;
            }
         }
      }
      baryCenter = polygonCenterOfMass();
      if (baryCenter != null) {
         for (MyPoints myPoints : vecPointFs) {
            if (myPoints.isTouching == false) {
               continue;
            }
            myPoints.angle = Math.atan2(myPoints.y - baryCenter.y, myPoints.x
                  - baryCenter.x);
         }

         Collections.sort(vecPointFs, pointSort);
         mPath.reset();

         for (MyPoints myPoints : vecPointFs) {
            if (myPoints.isTouching == false) {
               continue;
            }
            if (mPath.isEmpty()) {
               mPath.moveTo(myPoints.x, myPoints.y);
            } else {
               mPath.lineTo(myPoints.x, myPoints.y);
            }
         }
      }
      invalidate();
      return true;

   }

   public MyPoints polygonCenterOfMass() {
      double cx = 0, cy = 0;
      int N = vecPointFs.size() ;
      if (N < 3) {
         return null;
      }
      double dArea =  signedPolygonArea();
      dCurrentArea= (dArea < 0 ?-dArea:dArea )/ 1000;
      if(dCurrentArea > dTopArea ){
         dTopArea =dCurrentArea;
      }
      final MyPoints res = new MyPoints(0, 0);
      int i, j;

      double factor = 0;
      for (i = 0; i < N; i++) {

         j = (i + 1) % N;
         if (!vecPointFs.get(i).isTouching || !vecPointFs.get(j).isTouching) {
            continue;
         }
         double lat1 = vecPointFs.get(i).y;
         double lon1 = vecPointFs.get(i).x;

         double lat2 = vecPointFs.get(j).y;
         double lon2 = vecPointFs.get(j).x;

         factor = (lon1 * lat2 - lon2 * lat1);
         cx += (lon1 + lon2) * factor;
         cy += (lat1 + lat2) * factor;
      }
      dArea *= 6.0f;
      factor = 1 / dArea;
      cx *= factor;
      cy *= factor;

      res.setXY((float) cx, (float) cy);

      return res;
   }

   public double signedPolygonArea() {
      final int N = vecPointFs.size() ;
      int i, j;
      double area = 0;

      for (i = 0; i < N; i++) {
         j = (i + 1) % N;
         if (!vecPointFs.get(i).isTouching || !vecPointFs.get(j).isTouching) {
            continue;
         }
         double lat1 = vecPointFs.get(i).y;
         double lon1 = vecPointFs.get(i).x;

         double lat2 = vecPointFs.get(j).y;
         double lon2 = vecPointFs.get(j).x;

         area += lon1 * lat2;
         area -= lat1 * lon2;
      }
      area /= 2.0;

      return (area);
      // for unsigned
      // return(area < 0 ? -area : area);
   }

   private final static class MyPoints extends PointF {

      private boolean isTouching = false;
      private double  angle      = 0;

      /**
       * 
       */
      public MyPoints(final float x, final float y) {
         super(x, y);
      }

      /**
       * 
       */
      public MyPoints() {

      }

      public void setXY(final float x, final float y) {
         set(x, y);
      }

      /**
       * @param isTouching
       *           the isTouching to set
       */
      public void setTouching(boolean isTouching) {
         this.isTouching = isTouching;
      }

      /**
       * @return the isTouching
       */
      public boolean isTouching() {
         return isTouching;
      }
   }

}

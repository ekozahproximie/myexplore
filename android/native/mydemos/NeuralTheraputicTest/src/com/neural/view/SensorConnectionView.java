/**
 * Copyright Trimble Inc., 2013 - 2014 All rights reserved.
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
 *	    SensorConnectionView.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Nov 24, 20132:03:16 PM
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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.neural.demo.R;


/**
 * @author sprabhu
 *
 */
public class SensorConnectionView extends View {
   static final String TAG = "SensorConnectionView";

   

   private Paint mPaint;
   private Paint mPaintBorder;

   int mRadius;
   
   


   public SensorConnectionView(Context context, AttributeSet attrs) {
       super(context, attrs);

       setFocusable(false);
       setClickable(true);


       mPaint = new Paint();
       mPaint.setAntiAlias(true);
       mPaint.setStrokeWidth(6);
       mPaint.setColor(0xFFB1D46A);
       
       mPaintBorder= new Paint();
       mPaintBorder.setAntiAlias(true);
       mPaintBorder.setStrokeWidth(1);
       mPaintBorder.setStyle(Paint.Style.STROKE);
       mPaintBorder.setColor(Color.BLACK);//0xFF567199);
       

       // look up any layout-defined attributes
       TypedArray a = context.obtainStyledAttributes(attrs,
               R.styleable.DraggableDot);

       final int N = a.getIndexCount();
       for (int i = 0; i < N; i++) {
           int attr = a.getIndex(i);
           switch (attr) {
           case R.styleable.DraggableDot_radius: {
               mRadius = a.getDimensionPixelSize(attr, 0);
           } break;

           }
       }

       Log.i(TAG, "SensorConnectionView @ " + this + " : radius=" + mRadius );

   }

   public void setRadius(int mRadius) {
               this.mRadius = mRadius;
               invalidate();
       }
   public void setCirclePaintColor(int iPaintColor){
         if(mPaint != null){
            mPaint.setColor(iPaintColor);
            invalidate();
         }
   }
   @Override
   protected void onDraw(Canvas canvas) {
       float wf = getWidth();
       float hf = getHeight();
       final float cx = wf/2;
       final float cy = hf/2;
       wf -= getPaddingLeft() + getPaddingRight();
       hf -= getPaddingTop() + getPaddingBottom();
       float rad = (wf < hf) ? wf/2 : hf/2;
       canvas.drawCircle(cx, cy, rad, mPaint);
       canvas.drawCircle(cx, cy, rad+1,mPaintBorder);


   }

   @Override
   protected void onMeasure(int widthSpec, int heightSpec) {
       int totalDiameter = 2*mRadius + getPaddingLeft() + getPaddingRight();
       setMeasuredDimension(totalDiameter, totalDiameter);
   }

}

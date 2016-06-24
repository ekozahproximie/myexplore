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
 *      com.trimble.ag.lono.view
 *
 * File name:
 *	    TestActivity.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Mar 16, 20148:56:18 PM
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
package com.trimble.ag.lono.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;


/**
 * @author sprabhu
 *
 */
public class TestActivity extends Activity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
           super.onCreate(savedInstanceState);
      
           setContentView(new SampleView(this));
   }

   private static class SampleView extends View {

           // CONSTRUCTOR
           public SampleView(Context context) {
                   super(context);
                   setFocusable(true);

           }
           Paint p = new Paint();
           RectF rectF = null;
           /* (non-Javadoc)
         * @see android.view.View#onSizeChanged(int, int, int, int)
         */
         @Override
         protected void onSizeChanged(int w, int h, int oldw, int oldh) {
         // TODO Auto-generated method stub
         super.onSizeChanged(w, h, oldw, oldh);
         rectF = new RectF(w/2, h/2, w/2+100,h/2+ 80);
         p1= new Point(160, 120);
         point= new Point(160, 120);
         }
         Point p1= null;
         Point point =null;
         Path myPath = new Path();
         Paint paint = new Paint();
         final RectF oval = new RectF();
         Path path = new Path();
           @Override
           protected void onDraw(Canvas canvas) {

                   canvas.drawColor(Color.WHITE);
                 
                   // smooths
                   p.setAntiAlias(true);
                   p.setColor(Color.RED);
                   p.setStyle(Paint.Style.STROKE); 
                   p.setStrokeWidth(5);
                   // opacity
                   //p.setAlpha(0x80); //
                    int sweeep_angle=120;  
                   float radius = 120;
                  
                   oval.set(p1.x - radius, p1.y - radius, p1.x + radius, p1.y+ radius);
                   int startAngle = (int) (180 / Math.PI * Math.atan2(point.y - p1.y, point.x - p1.x));
                   myPath.arcTo(oval, startAngle, -(float) sweeep_angle, true);
                   canvas.drawPath(myPath,  p);
                   canvas.drawOval(rectF, p);
                   p.setColor(Color.BLACK);
                   p.setStyle(Style.FILL);
                   int iAngle=-120;
                   canvas.drawArc (rectF, iAngle, 90, true, p);
                   
                   paint.setStyle(Paint.Style.STROKE);
                   paint.setStrokeWidth(2);
                   paint.setColor(Color.RED);
               
                 //  canvas.drawArc(new RectF(10, 10, 200, 150), -90, -90, false, p);

           }

   }
}
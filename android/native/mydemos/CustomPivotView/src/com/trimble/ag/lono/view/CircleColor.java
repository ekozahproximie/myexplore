/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 * 
 *
 * Module Name: com.trimble.ag.lono.view
 *
 * File name: CircleColor.java
 *
 * Author: sprabhu
 *
 * Created On: Mar 16, 201411:53:15 PM
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
package com.trimble.ag.lono.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * @author sprabhu
 *
 */
public class CircleColor extends Activity implements OnClickListener {

   LinearLayout imageView;
   Button       circle, antialias, shadow, blur1, blur2, blur3, blur4, emboss,
         sweep, radial, linear;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.main);
      imageView = (LinearLayout) findViewById(R.id.ImageView01);
      circle = (Button) findViewById(R.id.circle);
      antialias = (Button) findViewById(R.id.antialias);
      shadow = (Button) findViewById(R.id.shadow);
      blur1 = (Button) findViewById(R.id.blur1);
      blur2 = (Button) findViewById(R.id.blur2);
      blur3 = (Button) findViewById(R.id.blur3);
      blur4 = (Button) findViewById(R.id.blur4);

      emboss = (Button) findViewById(R.id.emboss);
      sweep = (Button) findViewById(R.id.sweep);
      linear = (Button) findViewById(R.id.linear);
      radial = (Button) findViewById(R.id.radial);

      circle.setOnClickListener(this);
      antialias.setOnClickListener(this);
      shadow.setOnClickListener(this);
      blur1.setOnClickListener(this);
      blur2.setOnClickListener(this);
      blur3.setOnClickListener(this);
      blur4.setOnClickListener(this);
      emboss.setOnClickListener(this);
      sweep.setOnClickListener(this);
      linear.setOnClickListener(this);
      radial.setOnClickListener(this);
     
   }

   @Override
   public void onClick(View v) {
      imageView.removeAllViews();
      switch (v.getId()) {
         case R.id.circle:
            imageView.addView(new DrawView(CircleColor.this));
            break;
         case R.id.antialias:
            imageView.addView(new DrawView(CircleColor.this).getAntiAlias());
            break;
         case R.id.shadow:
            imageView.addView(new DrawView(CircleColor.this).getShadowEffect());
            break;
         case R.id.blur1:
            imageView.addView(new DrawView(CircleColor.this)
                  .getBlurMaskFitler(1));
            break;
         case R.id.blur2:
            imageView.addView(new DrawView(CircleColor.this)
                  .getBlurMaskFitler(2));
            break;
         case R.id.blur3:
            imageView.addView(new DrawView(CircleColor.this)
                  .getBlurMaskFitler(3));
            break;

         case R.id.blur4:
            imageView.addView(new DrawView(CircleColor.this)
                  .getBlurMaskFitler(4));
            break;
         case R.id.emboss:
            imageView.addView(new DrawView(CircleColor.this)
                  .getEmbossMaskFilter());
            break;
         case R.id.sweep:
            imageView.addView(new DrawView(CircleColor.this)
                  .getSweepShaderEffect());
            break;
         case R.id.linear:
            imageView.addView(new DrawView(CircleColor.this)
                  .getLinearShaderEffect());
            break;
         case R.id.radial:
            imageView.addView(new DrawView(CircleColor.this)
                  .getRadialShaderEffect());
            break;

      }

   }

   public class DrawView extends View {

      Paint   paint = new Paint();
      Context c;
      final float xPos = 80, yPos = 80, radius = 40;

      public DrawView(Context context) {
         super(context);
         c = context;
         paint.setColor(Color.WHITE);
         if(android.os.Build.VERSION.SDK_INT >= 11 ) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
         }
         invalidate();
      }

      private View getAntiAlias() {

         paint.setAntiAlias(true);
         return this;
      }

      private View getShadowEffect() {
         paint.setAntiAlias(true);
         paint.setShadowLayer(10, 10, 5, Color.RED);
         return this;
      }

      private View getBlurMaskFitler(int type) {
         invalidate();
         paint.setAntiAlias(true);
         switch (type) {
            case 1:
               paint.setMaskFilter(new BlurMaskFilter(15, Blur.INNER));
               break;
            case 2:
               paint.setMaskFilter(new BlurMaskFilter(15, Blur.OUTER));
               break;
            case 3:
               paint.setMaskFilter(new BlurMaskFilter(15, Blur.SOLID));
               break;
            case 4:
               paint.setMaskFilter(new BlurMaskFilter(15, Blur.NORMAL));
               break;
         }
         return this;
      }

      private View getEmbossMaskFilter() {
         paint.setAntiAlias(true);
         paint.setMaskFilter(new EmbossMaskFilter(new float[] { 1, 1, 1 },
               0.4f, 10, 8.2f));
         return this;
      }

      private View getSweepShaderEffect() {
         paint.setAntiAlias(true);
         paint.setShader(new SweepGradient(80, 80, Color.RED, Color.WHITE));
         return this;
      }

      private View getRadialShaderEffect() {
         paint.setAntiAlias(true);
         paint.setShader(new RadialGradient(8f, 80f, 90f, Color.RED,
               Color.WHITE, Shader.TileMode.MIRROR));
         return this;
      }

      private View getLinearShaderEffect() {
         paint.setAntiAlias(true);
         paint.setShader(new LinearGradient(8f, 80f, 30f, 20f, Color.RED,
               Color.WHITE, Shader.TileMode.MIRROR));
         return this;
      }

      @Override
      public void onDraw(Canvas canvas) {
         canvas.drawCircle(xPos, yPos, radius, paint);
      }
   }
}

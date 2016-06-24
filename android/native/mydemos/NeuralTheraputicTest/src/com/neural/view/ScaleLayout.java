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
 *	    ScaleLayout.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     May 3, 20147:55:02 PM
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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neural.demo.R;
import com.neural.setting.SettingsManager;


/**
 * @author sprabhu
 *
 */
public class ScaleLayout extends LinearLayout {

   private transient SettingsManager manager = null;
   
   private transient TextView tvTimeScale=null;
   
   private transient TextView tvEmgScale=null;
   
   private transient Context context =null;
   
   /**
    * @param context
    */
   public ScaleLayout(Context context) {
      super(context);
      initUI(context);
   }

   /**
    * @param context
    * @param attrs
    */
   public ScaleLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
      initUI(context);
   }

   /**
    * @param context
    * @param attrs
    * @param defStyle
    */
   public ScaleLayout(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
      initUI(context);
   }
   private void initUI(final Context context) {
      this.context=context;
      manager=SettingsManager.getInstance();
      LayoutInflater layoutInflater = (LayoutInflater) context
                      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      final View view = layoutInflater.inflate(R.layout.scalelayout, this);
      tvTimeScale=(TextView)view.findViewById(R.id.timescale);
      tvEmgScale=(TextView)view.findViewById(R.id.emgscale);

   }
   
   public void updateValues(final String stKey){
      final StringBuilder builder = new StringBuilder();
      builder.append(context.getResources().getString(R.string.time_base_info));
      builder.append(": ");
      builder.append(manager.getTimeBase(context));
      builder.append(" sec");
      tvTimeScale.setText( builder.toString());
      builder.setLength(0);
      builder.append(context.getResources().getString(R.string.emg_scale_info));
      builder.append(": ");
      builder.append(manager.getEMGScale(context,stKey));
      builder.append(" mv/sec");
      tvEmgScale.setText( builder.toString());
   }

}

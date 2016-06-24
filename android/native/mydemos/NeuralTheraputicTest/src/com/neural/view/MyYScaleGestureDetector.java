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
 *	    MyYScaleGestureDetector.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Mar 30, 20145:04:36 PM
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
import android.view.ScaleGestureDetector;


/**
 * @author sprabhu
 *
 */
public class MyYScaleGestureDetector extends ScaleGestureDetector {

   /**
    * @param context
    * @param listener
    */
   public MyYScaleGestureDetector(Context context,
         OnScaleGestureListener listener) {
      super(context, listener);
      
   }


}

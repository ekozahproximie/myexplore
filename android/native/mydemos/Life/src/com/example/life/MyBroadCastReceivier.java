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
 *      com.example.life
 *
 * File name:
 *	    MyBroadCastReceivier.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Feb 27, 201412:24:50 PM
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
package com.example.life;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * @author sprabhu
 *
 */
public class MyBroadCastReceivier extends BroadcastReceiver {

   /**
    * 
    */
   public MyBroadCastReceivier() {
      // TODO Auto-generated constructor stub
   }

   /* (non-Javadoc)
    * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
    */
   @Override
   public void onReceive(Context context, Intent intent) {
      
      Log.i("log", "data:"+intent.getIntExtra("data", 0));
   }

}

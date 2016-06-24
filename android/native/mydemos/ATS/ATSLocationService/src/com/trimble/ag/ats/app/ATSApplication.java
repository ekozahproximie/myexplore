/**
 * Copyright Trimble Inc., 2015 - 2016 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Spime Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name: atsLocationService
 *
 * Module Name: com.trimble.ag.ats.app
 *
 * File name: atsApplication.java
 *
 * Author: sprabhu
 *
 * Created On: 27-Oct-2015 11:41:44 pm
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
package com.trimble.ag.ats.app;

import android.app.Application;

/**
 * @author sprabhu
 *
 */
public class ATSApplication extends Application {

   /**
    * 
    */
   public ATSApplication() {

   }

   @Override
   public void onCreate() {

      super.onCreate();
   }

   @Override
   public void onTerminate() {

      super.onTerminate();
   }

   @Override
   public void onLowMemory() {

   }

}
